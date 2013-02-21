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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.collector.Collector;
import cleo.search.collector.SimpleCollector;
import cleo.search.filter.BloomFilter;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;

/**
 * AbstractTypeahead
 * 
 * @author jwu
 * @since 03/02, 2011
 */
public abstract class AbstractTypeahead<E extends Element> implements Typeahead<E>, Closeable {
  protected final String name;
  protected final BloomFilter<Long> bloomFilter;
  protected final ArrayStoreElement<E> elementStore;
  protected final SelectorFactory<E> selectorFactory;
  protected boolean isClosed=false;
  
  protected AbstractTypeahead(String name,
                              ArrayStoreElement<E> elementStore,
                              SelectorFactory<E> selectorFactory,
                              BloomFilter<Long> bloomFilter) {
    this.name = name;
    this.bloomFilter = bloomFilter;
    this.elementStore = elementStore;
    this.selectorFactory = selectorFactory;
  }

  protected void ensureOpen() throws UnsupportedOperationException {
    if(isClosed)
      throw new UnsupportedOperationException("Cannot modify store, already closed!");
  }

  @Override
  public final String getName() {
    return name;
  }
  
  public final ArrayStoreElement<E> getElementStore() {
    return elementStore;
  }
  
  public final SelectorFactory<E> getSelectorFactory() {
    return selectorFactory;
  }
  
  public final BloomFilter<Long> getBloomFilter() {
    return bloomFilter;
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
    
    List<E> results;
    Collector<E> collector = new SimpleCollector<E>(maxNumResults);
    collector = search(uid, terms, collector, timeoutMillis);
    
    results = collector.elements();
    if(results.size() > maxNumResults) {
      results = results.subList(0, maxNumResults);
    }
    
    return results;
  }
  
  @Override
  public Collector<E> search(int uid, String[] terms, Collector<E> collector) {
    return search(uid, terms, collector, Long.MAX_VALUE);
  }
  
  protected void log(Logger logger, int user, String[] terms, HitStats hitStats) {
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
    
    logger.info(sb.toString());
  }

  public void close() throws IOException {
    try {
      this.elementStore.close();
    }
    finally {
      isClosed=true;
    }
  }
}
