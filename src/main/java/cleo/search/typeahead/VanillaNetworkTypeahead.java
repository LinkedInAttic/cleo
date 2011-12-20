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
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.Indexer;
import cleo.search.collector.Collector;
import cleo.search.collector.SimpleCollector;
import cleo.search.connection.Connection;
import cleo.search.connection.ConnectionFilter;
import cleo.search.connection.ConnectionIndexer;
import cleo.search.filter.BloomFilter;
import cleo.search.network.Proximity;
import cleo.search.selector.Selector;
import cleo.search.selector.SelectorContext;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreConnections;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.IntArrayPartition;
import cleo.search.store.StaticIntArrayPartition;
import cleo.search.util.IntIterator;
import cleo.search.util.IntIteratorFromBytes;
import cleo.search.util.Range;
import cleo.search.util.ResourcePool;

/**
 * VanillaNetworkTypeahead
 * 
 * @author jwu
 * @since 02/07, 2011
 * 
 * <p>
 * 07/22, 2011 - Added lock objects to improve update synchronization <br/>
 * 09/18, 2011 - Added support for partially reading network connections <br/>
 */
public class VanillaNetworkTypeahead<E extends Element> implements NetworkTypeahead<E>, Indexer<E>, ConnectionIndexer {
  private final static Logger logger = Logger.getLogger(VanillaNetworkTypeahead.class);
  private final Object elementStoreLock = new Object();
  private final Object connectionsStoreLock = new Object();
  
  protected final String name;
  protected final ArrayStoreElement<E> elementStore;
  protected final ArrayStoreConnections connectionsStore;
  protected final SelectorFactory<E> selectorFactory;
  protected final BloomFilter<Integer> bloomFilter;
  protected final IntArrayPartition filterStore;
  protected final ConnectionFilter connFilter;
  protected final Range range;
  
  protected boolean loggingEnabled = true;
  protected boolean partialReadEnabled = false;
  
  // byte array resource pool
  public final static int BYTES_POOL_SIZE_DEFAULT = 100;
  public final static int BYTE_ARRAY_SIZE_DEFAULT = 1 << 15;  // 32K bytes
  
  protected final ResourcePool<byte[]> bytesPool;
  protected int bytesPoolSize = BYTES_POOL_SIZE_DEFAULT;
  protected int byteArraySize = BYTE_ARRAY_SIZE_DEFAULT;
  
  /**
   * Creates a new TypeaheadSearch instance.
   * 
   * @param elementStore       - Element store
   * @param connectionsStore   - Element connections store
   * @param selectorFactory    - Element selector factory
   * @param bloomFilter        - Bloom filter
   * @param connFilter         - Connection filter for indexing
   */
  public VanillaNetworkTypeahead(String name,
                                 ArrayStoreElement<E> elementStore,
                                 ArrayStoreConnections connectionsStore,
                                 SelectorFactory<E> selectorFactory,
                                 BloomFilter<Integer> bloomFilter,
                                 ConnectionFilter connFilter) {
    this.name = name;
    this.elementStore = elementStore;
    this.connectionsStore = connectionsStore;
    this.selectorFactory = selectorFactory;
    this.bloomFilter = bloomFilter;
    this.connFilter = connFilter;
    
    // Initialize bloom filter store
    this.filterStore = initFilterStore();
    
    // Initialize the element id range
    this.range = new Range(elementStore.getIndexStart(), elementStore.capacity());
    
    // Initialize the resource pool for byte array
    this.bytesPool = new ResourcePool<byte[]>(bytesPoolSize);
    
    // List properties
    this.listProperties();
    logger.info(name + " started");
  }
  
  protected void listProperties() {
    String format = "# %s: %s";
    
    getLogger().info(String.format(format, "name", getName()));
    getLogger().info(String.format(format, "elementStore", elementStore.getClass().getName()));
    getLogger().info(String.format(format, "connectionsStore", connectionsStore.getClass().getName()));
    getLogger().info(String.format(format, "selectorFactory", selectorFactory.getClass().getName()));
    getLogger().info(String.format(format, "bloomFilter", bloomFilter.getClass().getName()));
    getLogger().info(String.format(format, "filterStore", filterStore.getClass().getName()));
    getLogger().info(String.format(format, "connectionFilter", connFilter.toString()));
    getLogger().info(String.format(format, "range", range.toString()));
    getLogger().info("# bytesPoolSize: " + bytesPoolSize);
    getLogger().info("# byteArraySize: " + byteArraySize);
  }
  
  protected IntArrayPartition initFilterStore() {
    long startTime = System.currentTimeMillis();
    
    IntArrayPartition p = new StaticIntArrayPartition(elementStore.getIndexStart(), elementStore.capacity());
    
    try {
      for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
        E element = elementStore.getElement(i);
        if(element != null) {
          p.set(i, bloomFilter.computeIndexFilter(element));
        }
      }
    } catch(Exception e) {
      getLogger().error("failed to initialize filter store");
    }
    
    long totalTime = System.currentTimeMillis() - startTime;
    getLogger().info(getName() + " init filter store: " + totalTime + " ms");
    
    return p;
  }
  
  @Override
  public final String getName() {
    return name;
  }
  
  public final ArrayStoreElement<E> getElementStore() {
    return elementStore;
  }
  
  public final ArrayStoreConnections getConnectionsStore() {
    return connectionsStore;
  }
  
  public final BloomFilter<Integer> getBloomFilter() {
    return bloomFilter;
  }
  
  public final ConnectionFilter getConnectionFilter() {
    return connFilter;
  }
  
  public final SelectorFactory<E> getSelectorFactory() {
    return selectorFactory;
  }
  
  @Override
  public List<E> search(int uid, String[] terms) {
    return search(uid, terms, Integer.MAX_VALUE, Long.MAX_VALUE);
  }
  
  @Override
  public List<E> search(int uid, String[] terms, long timeoutMillis) {
    return search(uid, terms, Integer.MAX_VALUE, timeoutMillis);
  }
  
  @Override
  public List<E> search(int uid, String[] terms, int maxNumResults, long timeoutMillis) {
    if(terms == null || terms.length == 0 || maxNumResults < 1) {
      return new ArrayList<E>();
    }
    
    HitStats hitStats = new HitStats();
    hitStats.start();
    
    Collector<E> collector = new SimpleCollector<E>(maxNumResults);
    Selector<E> selector = getSelectorFactory().createSelector(terms);
    HashSet<Integer> uniqIds = new HashSet<Integer>(199);
    searchInternal(uid, terms, collector, selector, uniqIds, hitStats, timeoutMillis);
    
    hitStats.stop();
    if(loggingEnabled) {
      log(uid, hitStats, terms);
    }
    
    return collector.elements();
  }
  
  protected byte[] getBytesFromPool() {
    byte[] bytes = bytesPool.get();
    return (bytes == null) ? bytes = new byte[byteArraySize] : bytes;
  }
  
  protected void searchInternal(int uid, String[] terms, Collector<E> collector, Selector<E> selector, HitStats hitStats, long timeoutMillis) {
    if(connectionsStore.hasIndex(uid)) {
      // Get a byte array from resource pool
      byte[] bytes = getBytesFromPool();
      
      try {
        IntIteratorFromBytes elemIdIter = getConnectionIterator(uid, bytes);
        if(elemIdIter != null) {
          bytes = elemIdIter.array();
          int filter = bloomFilter.computeQueryFilter(terms);
          applyFilter(filter, elemIdIter, collector, selector, hitStats, timeoutMillis);
        }
      } catch(Exception e) {
        getLogger().warn(e.getMessage(), e);
      } finally {
        // Return the byte array to resource pool
        if(bytes != null && bytes.length == byteArraySize) {
          bytesPool.put(bytes);
        } else {
          if(bytes != null) {
            getLogger().info("bytes on the fly: " + bytes.length);
          }
        }
      }
    }
  }
  
  protected void searchInternal(int uid, String[] terms, Collector<E> collector, Selector<E> selector, HashSet<Integer> uniqIds, HitStats hitStats, long timeoutMillis) {
    if(connectionsStore.hasIndex(uid)) {
      // Get a byte array from resource pool
      byte[] bytes = getBytesFromPool();
      
      try {
        IntIteratorFromBytes elemIdIter = getConnectionIterator(uid, bytes);
        if(elemIdIter != null) {
          bytes = elemIdIter.array();
          int filter = bloomFilter.computeQueryFilter(terms);
          applyFilter(filter, elemIdIter, collector, selector, uniqIds, hitStats, timeoutMillis);
        }
      } catch(Exception e) {
        getLogger().warn(e.getMessage(), e);
      } finally {
        // Return the byte array to resource pool
        if(bytes != null && bytes.length == byteArraySize) {
          bytesPool.put(bytes);
        } else {
          if(bytes != null) {
            getLogger().info("bytes on the fly: " + bytes.length);
          }
        }
      }
    }
  }
  
  protected long applyFilter(int filter, IntIterator elemIdIter, Collector<E> collector, Selector<E> selector, HitStats hitStats, long timeoutMillis) {
    long totalTime = 0;
    long startTime = System.currentTimeMillis();
    
    int numBrowseHits = 0;
    int numFilterHits = 0;
    int numResultHits = 0;
    
    SelectorContext ctx = new SelectorContext();
    
    while(elemIdIter.hasNext()) {
      numBrowseHits++;
      
      int elemId = elemIdIter.next();
      if(elementStore.hasIndex(elemId) && (filterStore.get(elemId) & filter) == filter) {
        numFilterHits++;
        
        E elem = elementStore.getElement(elemId);
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
      
      if(numBrowseHits % 100 == 0) {
        totalTime = System.currentTimeMillis() - startTime;
        if(totalTime > timeoutMillis) break;
      }
    }
    
    hitStats.numBrowseHits += numBrowseHits;
    hitStats.numFilterHits += numFilterHits;
    hitStats.numResultHits += numResultHits;
    
    return System.currentTimeMillis() - startTime;
  }
  
  protected long applyFilter(int filter, IntIterator elemIdIter, Collector<E> collector, Selector<E> selector, HashSet<Integer> uniqIdSet, HitStats hitStats, long timeoutMillis) {
    long totalTime = 0;
    long startTime = System.currentTimeMillis();
    
    int numBrowseHits = 0;
    int numFilterHits = 0;
    int numResultHits = 0;
    
    SelectorContext ctx = new SelectorContext();
    
    while(elemIdIter.hasNext()) {
      numBrowseHits++;
      
      int elemId = elemIdIter.next();
      if(elementStore.hasIndex(elemId) && (filterStore.get(elemId) & filter) == filter) {
        numFilterHits++;
        
        if(!uniqIdSet.contains(elemId)) {
          uniqIdSet.add(elemId);
          
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
      }
      
      if(numBrowseHits % 100 == 0) {
        totalTime = System.currentTimeMillis() - startTime;
        if(totalTime > timeoutMillis) break;
      }
    }
    
    hitStats.numBrowseHits += numBrowseHits;
    hitStats.numFilterHits += numFilterHits;
    hitStats.numResultHits += numResultHits;
    
    return System.currentTimeMillis() - startTime;
  }
  
  protected long applyFilter(int filter, int[] elemIds, Collector<E> collector, Selector<E> selector, HashSet<Integer> uniqIdSet, HitStats hitStats, long timeoutMillis) {
    long totalTime = 0;
    long startTime = System.currentTimeMillis();
    
    int i = 0;
    int numFilterHits = 0;
    int numResultHits = 0;
    
    SelectorContext ctx = new SelectorContext();
    
    for(int cnt = elemIds.length; i < cnt; i++) {
      int elemId = elemIds[i];
      
      if(elementStore.hasIndex(elemId) && (filterStore.get(elemId) & filter) == filter) {
        numFilterHits++;
        
        if(!uniqIdSet.contains(elemId)) {
          uniqIdSet.add(elemId);
          
          E elem = getElementStore().getElement(elemId);
          if(elem != null) {
            if(selector.select(elem, ctx)) {
              numResultHits++;
              collector.add(elem, ctx.getScore(), getName());
              if(collector.canStop()) {
                i++;
                break;
              }
            }
            
            ctx.clear();
          }
        }
      }
      
      if(i % 100 == 0) {
        totalTime = System.currentTimeMillis() - startTime;
        if(totalTime > timeoutMillis) break;
      }
    }
    
    hitStats.numBrowseHits += i;
    hitStats.numFilterHits += numFilterHits;
    hitStats.numResultHits += numResultHits;
    
    return System.currentTimeMillis() - startTime;
  }
  
  protected Logger getLogger() {
    return logger;
  }
  
  protected void log(int user, HitStats hitStats, String[] terms) {
    StringBuilder sb = new StringBuilder();
    
    sb.append(getName())
      .append(" user=").append(user)
      .append(" time=").append(hitStats.totalTime)
      .append(" hits=")
      .append(hitStats.numBrowseHits).append('|')
      .append(hitStats.numFilterHits).append('|')
      .append(hitStats.numResultHits);
    
    sb.append(" terms=").append('{');
    for(String s : terms) {
      sb.append(s).append(',');
    }
    int lastIndex = sb.length() - 1;
    if(sb.charAt(lastIndex) == ',') {
      sb.deleteCharAt(lastIndex);
    }
    sb.append('}');
    
    getLogger().info(sb.toString());
  }
  
  public void setLoggingEnabled(boolean b) {
    this.loggingEnabled = b;
  }
  
  public boolean isLoggingEnabled() {
    return loggingEnabled;
  }
  
  public void setPartialReadEnabled(boolean b) {
    this.partialReadEnabled = b;
  }
  
  public boolean isPartialReadEnabled() {
    return partialReadEnabled;
  }
  
  /**
   * Adds an element to the underlying element store and makes it available for search.
   * 
   * @param element - element to index
   * 
   * @return <code>true</code> if the indexes (element store) changed as a result of this operation.
   *         Otherwise, <code>false</code>.
   */
  @Override
  public boolean index(E element) throws Exception {
    if(element == null) {
      return false;
    }
    
    int elemId = element.getElementId();
    if(!elementStore.hasIndex(elemId)) {
      return false;
    }
    
    synchronized(elementStoreLock) {
      // Update elementStore
      int elemFilter = getBloomFilter().computeIndexFilter(element);
      filterStore.set(elemId, elemFilter);
      elementStore.setElement(elemId, element, element.getTimestamp());
      
      // Logging
      if(getLogger().isTraceEnabled()) {
        getLogger().trace(getName() + " indexed element " + element);
      } else {
        getLogger().info(getName() +  " indexed element " + element.getElementId());
      }
      
      return true;
    }
  }
  
  /**
   * Indexes a connection.
   * 
   * @param  conn - a connection to be indexed.
   * @return <code>true</code> if the underlying indexes changed
   *         as a result of this operation. Otherwise, <code>false</code>.
   * @throws Exception
   */
  @Override
  public boolean index(Connection conn) throws Exception {
    if(!accept(conn)) {
      return false;
    }
    
    synchronized(connectionsStoreLock) {
      // Update connectionsStore
      int source = conn.source();
      int target = conn.target();
      long scn = conn.getTimestamp();
      
      if(conn.isActive()) {
        connectionsStore.addConnection(source, target, scn);
      } else {
        connectionsStore.removeConnection(source, target, scn);
      }
      
      // Logging
      if(getLogger().isTraceEnabled()) {
        getLogger().trace(getName() + " indexed connection " + conn);
      } else {
        getLogger().info(getName() +  " indexed connection " + source + "=>" + target + " " + (conn.isActive() ? 'Y' : 'N'));
      }
      
      return true;
    }
  }
  
  /**
   * Flushes the underlying indexes.
   */
  @Override
  public void flush() throws IOException {
    synchronized(elementStoreLock) {
      elementStore.persist();
    }
    synchronized(connectionsStoreLock) {
      connectionsStore.persist();
    }
  }
  
  @Override
  public Collector<E> search(int uid, String[] terms, Collector<E> collector) {
    return search(uid, terms, collector, Long.MAX_VALUE);
  }
  
  @Override
  public Collector<E> search(int uid, String[] terms, Collector<E> collector, long timeoutMillis) {
    if(terms == null || terms.length == 0) return collector;
    
    HitStats hitStats = new HitStats();
    
    hitStats.start();
    Selector<E> selector = getSelectorFactory().createSelector(terms);
    searchInternal(uid, terms, collector, selector, hitStats, timeoutMillis);
    hitStats.stop();
    
    if(loggingEnabled) {
      log(uid, hitStats, terms);
    }
    
    return collector;
  }
  
  @Override
  public Range getRange() {
    return range;
  }
  
  @Override
  public NetworkTypeaheadContext createContext(int uid) {
    NetworkTypeaheadContext context = new NetworkTypeaheadContextPlain(uid);
    
    if(connectionsStore.hasIndex(uid)) {
      int[] connections = connectionsStore.getConnections(uid);
      context.setConnections(connections);
    }
    
    return context;
  }

  @Override
  public Collector<E> searchNetwork(int uid, String[] terms, Collector<E> collector, NetworkTypeaheadContext context) {
    if(terms == null || terms.length == 0) return collector;
    
    if(context == null) {
      return search(uid, terms, collector, Long.MAX_VALUE);
    } else if(context.getConnections() == null) {
      return search(uid, terms, collector, context.getTimeoutMillis());
    }
    
    // The context has connections and strengths set properly
    HitStats hitStats = new HitStats();
    
    hitStats.start();
    int source = context.getSource();
    Selector<E> selector = getSelectorFactory().createSelector(terms);
    searchNetworkInternal(source, terms, collector, selector, hitStats, context);
    hitStats.stop();
    
    if(loggingEnabled) {
      if(uid != source) {
        getLogger().info(uid + " => " + source);
      }
      log(uid, hitStats, terms);
    }
    
    return collector;
  }
  
  protected void searchNetworkInternal(int uid, String[] terms, Collector<E> collector, Selector<E> selector, HitStats hitStats, NetworkTypeaheadContext context) {
    final long timeoutMillis = context.getTimeoutMillis();
    final long startTime = System.currentTimeMillis();
    long totalTime = 0;
    
    int filter = bloomFilter.computeQueryFilter(terms);
    if(connectionsStore.hasIndex(uid)) {
      int[] connections = context.getConnections();
      if(connections != null) {
        long timeout = timeoutMillis;
        HashSet<Integer> uniqIds = new HashSet<Integer>(199);
        
        // Filter out the network center 
        uniqIds.add(context.getSource());
        
        // Process 1st degree connections
        applyFilter(filter, connections, collector, selector, uniqIds, hitStats, timeout);
        if(collector.canStop()) {
          return;
        }
        
        // Check timeout
        totalTime = System.currentTimeMillis() - startTime;
        timeout = timeoutMillis - totalTime;
        if(timeout <= 0) return;
        
        // Process 2nd degree connections
        
        /*********************************************************************
         * Reuse a byte array for read second-degree connection strength data.
         *********************************************************************/
        
        // Obtain a byte array from resource pool
        byte[] bytes = bytesPool.get();
        if(bytes == null) {
          bytes = new byte[byteArraySize];
        }
        
        try {
          for(int i = 0, cnt = connections.length; i < cnt; i++) {
            int connectionId = connections[i];
            
            IntIteratorFromBytes connIter = getConnectionIterator(connectionId, bytes);
            if(connIter == null) continue;
            bytes = connIter.array();
            
            applyFilter2(filter, connIter, collector, selector, uniqIds, hitStats, timeout);
            if(collector.canStop()) {
              break;
            }
            
            // Check timeout
            totalTime = System.currentTimeMillis() - startTime;
            timeout = timeoutMillis - totalTime;
            if(timeout <= 0) break;
          }
        } catch(Exception e) {
          getLogger().warn(e.getMessage(), e);
        } finally {
          // Return the byte array to resource pool
          if(bytes != null && bytes.length == byteArraySize) {
            bytesPool.put(bytes);
          } else {
            if(bytes != null) {
              getLogger().info("bytes on the fly: " + bytes.length);
            }
          }
        }
      }
    }
  }
  
  /**
   * Applies bloom filter to search the 2nd degree connections.
   * 
   * @param filter                 - Bloom filter value
   * @param connIterator           - Second degree connection iterator
   * @param collector              - Hit collector
   * @param selector               - Element selector
   * @param uniqIdSet              - Unique elementId set
   * @param hitStats               - Hit statistic
   * @param timeoutMillis          - Timeout in milliseconds
   * @return the total of time in milliseconds.
   */
  long applyFilter2(int filter, IntIterator connIterator, Collector<E> collector, Selector<E> selector, HashSet<Integer> uniqIdSet, HitStats hitStats, long timeoutMillis) {
    long totalTime = 0;
    long startTime = System.currentTimeMillis();

    int numBrowseHits = 0;
    int numFilterHits = 0;
    int numResultHits = 0;
    
    SelectorContext ctx = new SelectorContext();
    
    while(connIterator.hasNext()) {
      numBrowseHits++;
      
      int elemId = connIterator.next();
      if(elementStore.hasIndex(elemId) && (filterStore.get(elemId) & filter) == filter) {
        numFilterHits++;
        
        if(!uniqIdSet.contains(elemId)) {
          uniqIdSet.add(elemId);
          
          E elem = getElementStore().getElement(elemId);
          if(elem != null) {
            if(selector.select(elem, ctx)) {
              numResultHits++;
              
              collector.add(elem, ctx.getScore(), getName(), Proximity.DEGREE_2);
              if(collector.canStop()) {
                break;
              }
            }
            
            ctx.clear();
          }
        }
      }
      
      if(numBrowseHits % 100 == 0) {
        totalTime = System.currentTimeMillis() - startTime;
        if(totalTime > timeoutMillis) break;
      }
    }
    
    hitStats.numBrowseHits += numBrowseHits;
    hitStats.numFilterHits += numFilterHits;
    hitStats.numResultHits += numResultHits;
    
    return System.currentTimeMillis() - startTime;
  }
  
  IntIteratorFromBytes getConnectionIterator(int uid, byte[] bytes) {
    if(connectionsStore.hasIndex(uid)) {
      // Read connection strength data into raw byte array
      int lenRead = partialReadEnabled ?
          connectionsStore.readBytes(uid, bytes) : connectionsStore.getBytes(uid, bytes);
      
      // Check whether connection strength data was read successfully
      if(lenRead < 0) {
        if(connectionsStore.getLength(uid) > bytes.length) {
          // Read a new byte array from the connection store
          byte[] bytesNew = connectionsStore.getBytes(uid);
          if(bytesNew != null) {
            lenRead = bytesNew.length;
            
            // Return the byte array to resource pool
            if(lenRead > 0 && bytes.length == byteArraySize) {
              bytesPool.put(bytes);
            }
            bytes = bytesNew;
          }
        }
      }
      
      if(lenRead > 0) {
        return new IntIteratorFromBytes(bytes, 0, lenRead);
      }
    }
    
    return null;
  }
  
  @Override
  public boolean accept(Connection conn) {
    return connFilter.accept(conn);
  }
  
  @Override
  public boolean accept(int source, int target, boolean active) {
    return connFilter.accept(source, target, active);
  }
}
