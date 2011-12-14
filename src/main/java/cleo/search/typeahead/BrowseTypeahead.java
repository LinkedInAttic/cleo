package cleo.search.typeahead;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.collector.Collector;
import cleo.search.filter.BloomFilter;
import cleo.search.selector.Selector;
import cleo.search.selector.SelectorContext;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;

/**
 * BrowseTypeahead
 * 
 * @author jwu
 * @since 02/12, 2011
 */
public class BrowseTypeahead<E extends Element> extends AbstractTypeahead<E> {
  private final static Logger logger = Logger.getLogger(BrowseTypeahead.class);
  
  protected BrowseData browseData = new BrowseData();
  
  public BrowseTypeahead(String name,
                         ArrayStoreElement<E> elementStore,
                         SelectorFactory<E> selectorFactory,
                         BloomFilter<Long> bloomFilter) {
    super(name, elementStore, selectorFactory, bloomFilter);
    
    logger.info(name + " started");
  }
  
  public synchronized void update(int[] elementIds) {
    if(elementIds == null) {
      browseData = new BrowseData();
      return;
    }
    
    int idStart = elementStore.getIndexStart();
    int idEnd = idStart + elementStore.capacity();
    ArrayList<Integer> list = new ArrayList<Integer>(elementIds.length);
    
    for(int i = 0, cnt = elementIds.length; i < cnt; i++) {
      int elemId = elementIds[i];
      if(idStart <= elemId && elemId < idEnd) {
        list.add(elemId);
      }
    }
    
    int size = list.size();
    int[] elemIdArray = new int[size];
    long[] filterArray = new long[size];
    for(int i = 0; i < size; i++) {
      elemIdArray[i] = list.get(i);
      E element = elementStore.getElement(elemIdArray[i]);
      if(element != null) {
        filterArray[i] = bloomFilter.computeIndexFilter(element);
      }
    }
    
    browseData = new BrowseData(elemIdArray, filterArray);
  }
  
  protected static class BrowseData {
    private int[] idArray;
    private long[] filterArray;
    
    public BrowseData() {
      idArray = new int[0];
      filterArray = new long[0];
    }
    
    public BrowseData(int[] idArray, long[] filterArray) {
      this.idArray = idArray;
      this.filterArray = filterArray;
    }
    
    public final int[] getElementIds() {
      return idArray;
    }
    
    public final long[] getElementFilters() {
      return filterArray;
    }
  }
  
  @Override
  public Collector<E> search(int uid, String[] terms, Collector<E> collector, long timeoutMillis) {
    if(terms == null || terms.length == 0) return collector;
    
    HitStats hitStats = new HitStats();
    
    hitStats.start();
    Selector<E> selector = getSelectorFactory().createSelector(terms);
    searchInternal(uid, terms, collector, selector, hitStats, timeoutMillis);
    hitStats.stop();
    
    log(logger, uid, terms, hitStats);
    return collector;
  }
  
  protected void searchInternal(int uid, String[] terms, Collector<E> collector, Selector<E> selector, HitStats hitStats, long timeoutMillis) {
    BrowseData browse = browseData;
    int[] idArray = browse.getElementIds();
    long[] filterArray = browse.getElementFilters();
    if(idArray == null || idArray.length == 0) return;
    
    long filter = bloomFilter.computeQueryFilter(terms);
    
    long totalTime = 0;
    long startTime = System.currentTimeMillis();
    
    int i = 0;
    int numFilterHits = 0;
    int numResultHits = 0;
    
    SelectorContext ctx = new SelectorContext();
    
    for(int cnt = idArray.length; i < cnt; i++) {
      int elemId = idArray[i];
      if((filterArray[i] & filter) == filter) {
        numFilterHits++;
        
        E elem = getElementStore().getElement(elemId);
        if(elem != null) {
          if(selector.select(elem, ctx)) {
            numResultHits++;
            collector.add(elem, ctx.getScore(), getName());
            if(collector.canStop()) {
              break;
            }
          }
          
          ctx.clear();
        }
      }
      
      if(i % 100 == 0) {
        totalTime = System.currentTimeMillis() - startTime;
        if(totalTime > timeoutMillis) break;
      }
    }
    
    hitStats.numBrowseHits += ++i;
    hitStats.numFilterHits += numFilterHits;
    hitStats.numResultHits += numResultHits;
  }
}
