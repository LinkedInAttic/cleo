package cleo.search.typeahead;

import java.io.IOException;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.Indexer;
import cleo.search.collector.Collector;
import cleo.search.filter.BloomFilter;
import cleo.search.selector.Selector;
import cleo.search.selector.SelectorContext;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.util.Range;

/**
 * ScannerTypeahead does a full scan on the entire element store to look for search hits.
 * It is useful for small data sets at the scale of 30,000 elements.
 * 
 * @author jwu
 * @since 03/22, 2011
 */
public class ScannerTypeahead<E extends Element> extends AbstractTypeahead<E> implements RangeTypeahead<E>, Indexer<E> {
  private final static Logger logger = Logger.getLogger(ScannerTypeahead.class);
  private final int rangeStart;
  private final int rangeEnd;
  private final Range range;
  private long[] filterData;
  
  /**
   * Creates a new ScannerTypeahead.
   * 
   * @param name
   *          the name of this ScannerTypeahead.
   * @param elementStore
   *          the element store.
   * @param selectorFactory
   *          the element selector factory.
   * @param bloomFilter
   *          the bloom filter.
   */
  public ScannerTypeahead(String name,
                          ArrayStoreElement<E> elementStore,
                          SelectorFactory<E> selectorFactory,
                          BloomFilter<Long> bloomFilter) {
    super(name, elementStore, selectorFactory, bloomFilter);
    logger.info(name + " start...");
    
    this.rangeStart = elementStore.getIndexStart();
    this.rangeEnd = elementStore.getIndexStart() + elementStore.length();
    this.range = new Range(rangeStart, rangeEnd - rangeStart);
    this.filterData = initFilterData();
    
    logger.info(name + " started.");
  }
  
  protected long[] initFilterData() {
    long startTime = System.currentTimeMillis();
    
    long[] array = new long[rangeEnd - rangeStart];
    for(int i = rangeStart; i < rangeEnd; i++) {
      E elem = elementStore.getElement(i);
      if(elem != null) {
        array[i - rangeStart] = bloomFilter.computeIndexFilter(elem);
      } else {
        array[i - rangeStart] = 0;
      }
    }
    
    long totalTime = System.currentTimeMillis() - startTime;
    logger.info(getName() + " init filter" + "[" + rangeStart + "," + rangeEnd + "): " + totalTime + " ms");
    
    return array;
  }
  
  @Override
  public Collector<E> search(int uid, String[] terms, Collector<E> collector, long timeoutMillis) {
    if(terms == null || terms.length == 0) return collector;
    
    HitStats hitStats = new HitStats();
    
    hitStats.start();
    Selector<E> selector = getSelectorFactory().createSelector(terms);
    searchInternal(uid, getRangeStart(), getRangeEnd(), terms, collector, selector, hitStats, timeoutMillis);
    hitStats.stop();
    
    log(logger, uid, terms, hitStats);
    return collector;
  }
  
  protected void searchInternal(int uid, int start, int end, String[] terms,
                                Collector<E> collector, Selector<E> selector,
                                HitStats hitStats, long timeoutMillis) {
    long filter = bloomFilter.computeQueryFilter(terms);
    
    long totalTime = 0;
    long startTime = System.currentTimeMillis();
    
    int i = start;
    int numFilterHits = 0;
    int numResultHits = 0;
    
    SelectorContext ctx = new SelectorContext();
    
    for(; i < end; i++) {
      hitStats.numBrowseHits++;
      
      if((filterData[i - rangeStart] & filter) == filter) {
        numFilterHits++;
        
        E elem = getElementStore().getElement(i);
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
    
    hitStats.numBrowseHits += (i - start);
    hitStats.numFilterHits += numFilterHits;
    hitStats.numResultHits += numResultHits;
  }
  
  @Override
  public synchronized void flush() throws IOException {
    elementStore.persist();
  }
  
  @Override
  public synchronized boolean index(E element) throws Exception {
    int elemId = element.getElementId();
    if(elementStore.hasIndex(elemId)) {
      long scn = element.getTimestamp();
      long elemFilter = bloomFilter.computeIndexFilter(element);
      filterData[elemId - elementStore.getIndexStart()] = elemFilter;
      elementStore.setElement(elemId, element, scn);
    }
    
    return false;
  }
  
  @Override
  public final int getRangeStart() {
    return rangeStart;
  }
  
  @Override
  public final int getRangeEnd() {
    return rangeEnd;
  }
  
  @Override
  public final Range getRange() {
    return range;
  }
  
  /**
   * Opens a new RangeTypeahead for search.
   * 
   * @param name
   *          the name of RangeTypeahead
   * @param rangeStart
   *          the range start of RangeTypehead 
   * @param rangeEnd
   *          the range end of RangeTypehead
   *          
   * @return a new RangeTypeahead.
   * 
   * @throws RangeException if the specified range has no intersection with the range of this ScannerTypeahead.
   */
  public RangeTypeahead<E> openRangeTypeahead(String name, int rangeStart, int rangeEnd) throws RangeException {
    int start = Math.max(getRangeStart(), rangeStart);
    int end = Math.min(rangeEnd, getRangeEnd());
    
    if(start <= end) {
      return new RangeScannerTypeahead<E>(name, start, end, this);
    }
    
    throw new RangeException(rangeStart, rangeEnd, getRangeStart(), getRangeEnd());
  }
  
  static class RangeScannerTypeahead<E extends Element> extends AbstractTypeahead<E> implements RangeTypeahead<E> {
    private final ScannerTypeahead<E> baseTypeahead;
    private final int rangeStart;
    private final int rangeEnd;
    private final Range range;
    
    RangeScannerTypeahead(String name,
                          int rangeStart, int rangeEnd,
                          ScannerTypeahead<E> baseTypeahead) {
      super(name,
            baseTypeahead.getElementStore(),
            baseTypeahead.getSelectorFactory(),
            baseTypeahead.getBloomFilter());
      this.baseTypeahead = baseTypeahead;
      this.rangeStart = rangeStart;
      this.rangeEnd = rangeEnd;
      this.range = new Range(rangeStart, rangeEnd - rangeStart);
    }
    
    @Override
    public Collector<E> search(int uid, String[] terms, Collector<E> collector, long timeoutMillis) {
      if(terms == null || terms.length == 0) return collector;
      
      HitStats hitStats = new HitStats();
      
      hitStats.start();
      Selector<E> selector = getSelectorFactory().createSelector(terms);
      baseTypeahead.searchInternal(uid, getRangeStart(), getRangeEnd(), terms, collector, selector, hitStats, timeoutMillis);
      hitStats.stop();
      
      log(logger, uid, terms, hitStats);
      return collector;
    }
    
    @Override
    public final int getRangeStart() {
      return rangeStart;
    }
    
    @Override
    public final int getRangeEnd() {
      return rangeEnd;
    }
    
    @Override
    public final Range getRange() {
      return range;
    }
  }
}
