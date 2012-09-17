/*
 * Copyright (c) 2011 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package cleo.search.typeahead;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import krati.Persistable;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.Indexer;
import cleo.search.Score;
import cleo.search.collector.Collector;
import cleo.search.filter.BloomFilter;
import cleo.search.selector.Selector;
import cleo.search.selector.SelectorContext;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ConnectionsStore;
import cleo.search.store.FloatArrayPartition;
import cleo.search.store.LongArrayPartition;
import cleo.search.store.StaticFloatArrayPartition;
import cleo.search.store.StaticLongArrayPartition;
import cleo.search.store.Stores;
import cleo.search.util.ElementScoreHandler;
import cleo.search.util.ScoreScanner;

/**
 * GenericTypeahead
 * 
 * @author jwu
 * @since 02/03, 2011
 * 
 * <p>
 * 05/16, 2011 - Added field maxElementScore <br/>
 * 09/16, 2012 - Used buffering connections store instead of roller to enhance indexing performance <br/> 
 */
public class GenericTypeahead<E extends Element> extends AbstractTypeahead<E> implements Indexer<E>, Persistable {
  private final static Logger logger = Logger.getLogger(GenericTypeahead.class);
  
  protected final ConnectionsStore<String> connectionsStore;
  protected final LongArrayPartition filterStore;
  protected final FloatArrayPartition scoreStore;
  protected final ScoreScanner scoreScanner;
  protected final int maxKeyLength;
  
  protected volatile float maxElementScore;
  
  public GenericTypeahead(String name,
                          ArrayStoreElement<E> elementStore,
                          ConnectionsStore<String> connectionsStore,
                          SelectorFactory<E> selectorFactory,
                          BloomFilter<Long> bloomFilter,
                          ScoreScanner scoreScanner,
                          int maxKeyLength) {
    super(name, elementStore, selectorFactory, bloomFilter);
    logger.info(name + " start...");
    
    this.connectionsStore = connectionsStore;
    this.scoreScanner = scoreScanner;
    this.maxKeyLength = maxKeyLength;
    
    // Initialize scoreStore and maxElementScore
    this.scoreStore = initScoreStore();
    this.maxElementScore = Stores.max(scoreStore);
    
    // Initialize filterStore
    this.filterStore = initFilterStore();
    
    logger.info(name + " started.");
  }
  
  protected FloatArrayPartition initScoreStore() {
    FloatArrayPartition p = new StaticFloatArrayPartition(elementStore.getIndexStart(), elementStore.capacity());
    
    try {
      if(scoreScanner != null) {
        long startTime = System.currentTimeMillis();
        
        ElementScoreHandler handler = new ElementScoreHandler(p);
        scoreScanner.scan(handler);
        
        for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
          float score = p.get(i);
          if(score < Score.MIN_SCORE_FLOAT) {
            score = Score.MIN_SCORE_FLOAT;
            p.set(i, score);
          }
          
          if(elementStore.hasIndex(i)) {
            E element = elementStore.getElement(i);
            if(element != null) {
              element.setScore(score);
            }
          }
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        logger.info(getName() + " load element scores: " + totalTime + " ms");
      }
    } catch(Exception e) {
      logger.warn(getName() + " faileld to load element scores", e);
    }
    
    return p;
  }
  
  protected LongArrayPartition initFilterStore() {
    long startTime = System.currentTimeMillis();
    
    LongArrayPartition p = new StaticLongArrayPartition(elementStore.getIndexStart(), elementStore.capacity());
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      E element = elementStore.getElement(i);
      if(element != null) {
        p.set(i, bloomFilter.computeIndexFilter(element));
      }
    }
    
    long totalTime = System.currentTimeMillis() - startTime;
    logger.info(getName() + " init filter store: " + totalTime + " ms");
    
    return p;
  }
  
  public final int getMaxKeyLength() {
    return maxKeyLength;
  }
  
  public final float getMaxElementScore() {
    return maxElementScore;
  }
  
  public final ConnectionsStore<String> getConnectionsStore() {
    return connectionsStore;
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
    
    if(terms.length == 1) {
      String term = terms[0];
      String prefix = term.substring(0, Math.min(term.length(), maxKeyLength));
      int[] connections = connectionsStore.getConnections(prefix);
      applyFilter(filter, connections, collector, selector, hitStats, timeoutMillis);
    } else {
      int minConnectionCount = Integer.MAX_VALUE;
      int[] minConnections = null;
      
      for(String term : terms) {
        String prefix = term.substring(0, Math.min(term.length(), maxKeyLength));
        int[] connections = connectionsStore.getConnections(prefix);
        if(connections != null) {
          if(minConnectionCount > connections.length) {
            minConnections = connections;
            minConnectionCount = connections.length;
          }
        }
      }
      
      if(minConnections != null) {
        applyFilter(filter, minConnections, collector, selector, hitStats, timeoutMillis);
      }
    }
  }
  
  protected long applyFilter(long filter, int[] elemIds, Collector<E> collector, Selector<E> selector, HitStats hitStats, long timeoutMillis) {
    if(elemIds == null || elemIds.length == 0) return 0;
    
    long totalTime = 0;
    long startTime = System.currentTimeMillis();
    
    int i = 0;
    int numFilterHits = 0;
    int numResultHits = 0;
    
    SelectorContext ctx = new SelectorContext();
    
    for(int cnt = elemIds.length; i < cnt; i++) {
      int elemId = elemIds[i];
      if((filterStore.get(elemId) & filter) == filter) {
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
    
    return System.currentTimeMillis() - startTime;
  }
  
  @Override
  public synchronized boolean index(E element) throws Exception {
    int elemId = element.getElementId();
    if(!elementStore.hasIndex(elemId)) {
      return false;
    }
    
    // Set element score
    if(scoreStore.hasIndex(elemId) && element.getScore() == 0) {
      element.setScore(scoreStore.get(elemId));
    }
    
    // Check if prefixes changed
    boolean prefixChanged = false;
    E oldElement = elementStore.getElement(elemId);
    if(oldElement == null || !Arrays.equals(element.getTerms(), oldElement.getTerms())) {
      prefixChanged = true;
    }
    
    // Update elementStore, filterStore
    long scn = element.getTimestamp();
    long elemFilter = bloomFilter.computeIndexFilter(element);
    filterStore.set(elemId, elemFilter);
    elementStore.setElement(elemId, element, scn);
    
    // Update connectionsStore upon prefix changes
    if(prefixChanged) {
      updateConnectionStore(oldElement, element);
    }
    
    // Logging
    if(logger.isDebugEnabled()) {
      logger.debug(getName() + " indexed element " + element);
    } else if(logger.isInfoEnabled()) {
      logger.info(getName() +  " indexed element " + element.getElementId());
    }
    
    return true;
  }
  
  @Override
  public synchronized void flush() throws IOException {
    persist();
  }
  
  /**
   * Update the underlying connectiosStore.
   *  
   * @param oldElement - old element
   * @param newElement - new element
   * @throws Exception
   */
  protected void updateConnectionStore(E oldElement, E newElement) throws Exception {
    long scn = newElement.getTimestamp();
    int elemId = newElement.getElementId();
    
    if(oldElement == null) {
      // Insert operation
      Set<String> prefixes = new HashSet<String>();
      for(String term : newElement.getTerms()) {
        int len = Math.min(term.length(), maxKeyLength);
        for(int i = 1; i <= len; i++) {
          String source = term.substring(0, i);
          if(prefixes.add(source)) {
            connectionsStore.addConnection(source, elemId, scn);
          }
        }
      }
    } else if(newElement.getTimestamp() >= getHWMark()) {
      // Update operation
      Set<String> oldPrefixes = new HashSet<String>();
      Set<String> newPrefixes = new HashSet<String>();
      
      for(String term : oldElement.getTerms()) {
        int len = Math.min(term.length(), maxKeyLength);
        for(int i = 1; i <= len; i++) {
          String source = term.substring(0, i);
          oldPrefixes.add(source);
        }
      }
      
      for(String term : newElement.getTerms()) {
        int len = Math.min(term.length(), maxKeyLength);
        for(int i = 1; i <= len; i++) {
          String source = term.substring(0, i);
          newPrefixes.add(source);
        }
      }
      
      // Calculate intersection 
      Set<String> commonPrefixes = new HashSet<String>();
      commonPrefixes.addAll(oldPrefixes);
      commonPrefixes.retainAll(newPrefixes);
      
      newPrefixes.removeAll(commonPrefixes);
      for(String source : newPrefixes) {
        connectionsStore.addConnection(source, elemId, scn);
      }
      
      oldPrefixes.removeAll(commonPrefixes);
      for(String source : oldPrefixes) {
        connectionsStore.removeConnection(source, elemId, scn);
      }
    } else {
      logger.info("ignored element: " + newElement);
    }
  }
  
  @Override
  public synchronized void sync() throws IOException {
    elementStore.sync();
    connectionsStore.sync();
  }
  
  @Override
  public synchronized void persist() throws IOException {
    elementStore.persist();
    connectionsStore.persist();
  }
  
  public synchronized void refresh() throws IOException {
    int counter = 0;
    long startTime = System.currentTimeMillis();
    
    // Sync first
    sync();
    
    // Re-score all elements for every connection source
    List<E> list = new ArrayList<E>(10000);
    Comparator<E> scoreCmpDsc = new Comparator<E>() {
      @Override
      public int compare(E e0, E e1) {
        float score0 = e0.getScore();
        float score1 = e1.getScore();
        // Descending order
        return score0 < score1 ? 1 : (score0 == score1 ? (e0.getElementId() - e1.getElementId()) : -1);
      }
    };
    
    Iterator<String> iter = connectionsStore.sourceIterator();
    while(iter.hasNext()) {
      counter++;
      String source = iter.next();
      if(source != null) {
        int[] connections = connectionsStore.getConnections(source);
        if(connections != null) {
          list.clear();
          
          for(int i = 0, cnt = connections.length; i < cnt; i++) {
            if(elementStore.hasIndex(connections[i])) {
              E elem = elementStore.getElement(connections[i]);
              if(elem != null) {
                list.add(elem);
              }
            }
          }
          
          Collections.sort(list, scoreCmpDsc);
          if(list.size() < connections.length) {
            connections = new int[list.size()];
          }
          
          for(int i = 0, cnt = connections.length; i < cnt; i++) {
            connections[i] = list.get(i).getElementId();
          }
          
          // Update source connections
          try {
            connectionsStore.putConnections(source, connections, getHWMark());
          } catch (Exception e) {
            logger.error(getName() + " failed to refresh source: " + source, e);
          }
        }
      }
      
      if(counter%1000 == 0) {
        logger.info(getName() + " refreshed " + counter);
      }
    }
    logger.info(getName() + " refreshed " + counter);
    
    connectionsStore.sync();
    long totalTime = System.currentTimeMillis() - startTime;
    logger.info(getName() + " refreshed in " + (totalTime/1000) + " seconds");
  }
  
  @Override
  public synchronized void saveHWMark(long endOfPeriod) throws Exception {
    connectionsStore.saveHWMark(endOfPeriod);
  }
  
  @Override
  public long getHWMark() {
    return connectionsStore.getHWMark();
  }
  
  @Override
  public long getLWMark() {
    return connectionsStore.getLWMark();
  }
}
