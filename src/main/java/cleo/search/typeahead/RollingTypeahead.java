package cleo.search.typeahead;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.IndexRoller;
import cleo.search.collector.Collector;
import cleo.search.filter.BloomFilter;
import cleo.search.selector.Selector;
import cleo.search.selector.SelectorContext;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.util.DaemonThreadFactory;

/**
 * RollingTypeahead
 * 
 * @author jwu
 * @since 03/02, 2011
 */
class RollingTypeahead<E extends Element> extends AbstractTypeahead<E> {
  public static final int DEFAULT_ROLLING_SIZE = 100;
  
  private final static Logger logger = Logger.getLogger(RollingTypeahead.class);
  private final ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());
  private final Queue<FilterEntry<E>> elementQueue = new ConcurrentLinkedQueue<FilterEntry<E>>();
  private final Map<Integer, E> elementMap = Collections.synchronizedMap(new HashMap<Integer, E>());
  private final RollingDriver rollingDriver;
  private volatile IndexRoller<E> indexRoller;
  private volatile int rollingSize = DEFAULT_ROLLING_SIZE;
  
  public RollingTypeahead(String name,
                          IndexRoller<E> indexRoller,
                          ArrayStoreElement<E> elementStore,
                          SelectorFactory<E> selectorFactory,
                          BloomFilter<Long> bloomFilter) {
    this(name,
         DEFAULT_ROLLING_SIZE,
         indexRoller,
         elementStore,
         selectorFactory,
         bloomFilter);
  }
  
  public RollingTypeahead(String name,
                          int rollingSize,
                          IndexRoller<E> indexRoller,
                          ArrayStoreElement<E> elementStore,
                          SelectorFactory<E> selectorFactory,
                          BloomFilter<Long> bloomFilter) {
    super(name, elementStore, selectorFactory, bloomFilter);
    
    this.indexRoller = indexRoller;
    this.setRollingSize(rollingSize);
    this.rollingDriver = new RollingDriver();
    this.executor.execute(rollingDriver);
  }
  
  class RollingDriver implements Runnable {
    private final long rollingTime = 1000L;
    private final long maxSleepTime = 500L;
    private final long minSleepTime =  10L;
    private volatile boolean active = true;
    private final Object mutex = new Object();
    
    @Override
    public void run() {
      while(active) {
        synchronized(mutex) {
          if(elementQueue.size() >= rollingSize) {
            int cnt = 0;
            List<E> list = new ArrayList<E>(rollingSize);
            Iterator<FilterEntry<E>> iter = elementQueue.iterator();
            while(iter.hasNext()) {
              list.add(iter.next().element);
              if(++cnt == rollingSize) break;
            }
            
            roll(list, false);
          }
        }
        
        // RollingUpdater sleep
        long sleepTime = (rollingSize - elementQueue.size()) * rollingTime / rollingSize;
        sleepTime = Math.max(minSleepTime, sleepTime);
        sleepTime = Math.min(maxSleepTime, sleepTime);
        if(sleepTime > minSleepTime) {
          try {
            Thread.sleep(maxSleepTime);
          } catch (InterruptedException e) {}
        }
      }
      
      drain();
    }
    
    private void roll(List<E> list, boolean clearAll) {
      try {
        if(indexRoller == null) {
          logger.warn("rolling update failure: no rolling handler");
        } else {
          if(indexRoller.roll(list)) {
            logger.info("rolling update success: removed " + list.size()  + " updates");
          } else {
            logger.warn("rolling update failure: removed " + list.size()  + " updates");
          }
        }
      } catch(Exception e) {
        logger.error("rolling update failure: " + e.getMessage(), e);
      } finally {
        if(clearAll) {
          elementMap.clear();
          elementQueue.clear();
        } else {
          for(Element e : list) {
            elementQueue.remove();
            elementMap.remove(e.getElementId());
          }
        }
      }
    }
    
    private void drain() {
      synchronized(mutex) {
        if(elementQueue.size() > 0) {
          List<E> list = new ArrayList<E>();
          Iterator<FilterEntry<E>> iter = elementQueue.iterator();
          while(iter.hasNext()) {
            list.add(iter.next().element);
          }
          
          roll(list, true);
        }
      }
    }
    
    public void setActive(boolean active) {
      this.active = active;
    }
    
    public boolean isActive() {
      return active;
    }
  }
  
  static class FilterEntry<E extends Element> {
    E element;
    long filter;
    
    FilterEntry(E element, long filter) {
      this.element = element;
      this.filter = filter;
    }
  }
  
  synchronized void drain() {
    rollingDriver.drain();
  }
  
  synchronized boolean offer(E element) {
    if(element != null && !elementMap.containsKey(element.getElementId())) {
      long elementFilter = bloomFilter.computeIndexFilter(element);
      elementQueue.offer(new FilterEntry<E>(element, elementFilter));
      elementMap.put(element.getElementId(), element);
      return true;
    }
    
    return false;
  }
  
  public Collector<E> search(int uid, String[] terms, Collector<E> collector, long timeoutMillis, HitStats hitStats) {
    if(terms == null || terms.length == 0) return collector;
    Selector<E> selector = getSelectorFactory().createSelector(terms);
    searchInternal(uid, terms, collector, selector, hitStats, timeoutMillis);
    return collector;
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
    long filter = bloomFilter.computeQueryFilter(terms);
    
    long totalTime = 0;
    long startTime = System.currentTimeMillis();
    
    int i = 0;
    int numFilterHits = 0;
    int numResultHits = 0;
    
    SelectorContext ctx = new SelectorContext();
    
    FilterEntry<E> entry;
    Iterator<FilterEntry<E>> iter = elementQueue.iterator();
    while(iter.hasNext()) {
      entry = iter.next();
      E elem = entry.element;
      
      if(elem != null && (entry.filter & filter) == filter) {
        numFilterHits++;
        
        if(selector.select(elem, ctx)) {
          numResultHits++;
          collector.add(elem, ctx.getScore(), getName());
          if(collector.canStop()) {
            break;
          }
        }
        
        ctx.clear();
      }

      if(i % 100 == 0) {
        totalTime = System.currentTimeMillis() - startTime;
        if(totalTime > timeoutMillis) break;
      }
      
      i++;
    }
    
    hitStats.numBrowseHits += ++i;
    hitStats.numFilterHits += numFilterHits;
    hitStats.numResultHits += numResultHits;
  }
  
  public void setRollingSize(int rollingSize) {
    this.rollingSize = Math.max(1, rollingSize);
  }
  
  public int getRollingSize() {
    return rollingSize;
  }
  
  public void setIndexRoller(IndexRoller<E> indexRoller) {
    this.indexRoller = indexRoller;
  }
  
  public IndexRoller<E> getIndexRoller() {
    return indexRoller;
  }
  
  public synchronized void open() {
    if(!rollingDriver.isActive()) {
      rollingDriver.setActive(true);
      executor.execute(rollingDriver);
    }
  }
  
  public synchronized void close() {
    if(rollingDriver.isActive()) {
      rollingDriver.setActive(false);
      rollingDriver.drain();
      executor.shutdown();
    }
  }
}
