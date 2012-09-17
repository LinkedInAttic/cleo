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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import cleo.search.Element;
import cleo.search.collector.Collector;
import cleo.search.collector.MultiCollector;
import cleo.search.collector.MultiSourceCollector;
import cleo.search.collector.SortedCollector;
import cleo.search.util.Strings;

/**
 * MultiTypeahead - A Typeahead to delegate tasks to multiple individual Typeahead(s). 
 * 
 * @author jwu
 * @since 02/10, 2011
 */
public class MultiTypeahead<E extends Element> implements Typeahead<E> {
  private final String name;
  private final ExecutorService executor;
  private final List<Typeahead<E>> typeaheads;
  private final Map<String, Typeahead<E>> typeaheadMap;
  private final static Logger logger = Logger.getLogger(MultiTypeahead.class);
  
  public MultiTypeahead(String name, List<Typeahead<E>> typeaheads) {
    this(name, typeaheads, Executors.newFixedThreadPool(100, new TypeaheadTaskThreadFactory()));
  }
  
  public MultiTypeahead(String name, List<Typeahead<E>> typeaheads, ExecutorService executorService) {
    this.name = name;
    this.typeaheads = typeaheads;
    this.typeaheadMap = new HashMap<String, Typeahead<E>>();
    for(Typeahead<E> ta : typeaheads) {
      typeaheadMap.put(ta.getName(), ta);
    }
    
    this.executor = (executorService != null) ?
        executorService : Executors.newFixedThreadPool(100, new TypeaheadTaskThreadFactory());
    
    logger.info(name + " started");
  }
  
  public final List<Typeahead<E>> subTypeaheads() {
    return typeaheads;
  }
  
  @Override
  public final String getName() {
    return name;
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
    Collector<E> collector = new SortedCollector<E>(maxNumResults);
    collector = search(uid, terms, collector, timeoutMillis);
    return collector.elements();
  }
  
  @Override
  public Collector<E> search(int uid, String[] terms, Collector<E> collector) {
    return search(uid, terms, collector, Long.MAX_VALUE);
  }
  
  @Override @SuppressWarnings("unchecked")
  public Collector<E> search(int uid, String[] terms, Collector<E> collector, long timeoutMillis) {
    List<TypeaheadTask<E>> taskList = new ArrayList<TypeaheadTask<E>>(typeaheads.size());
    MultiSourceCollector<E> multiCollector = null;
    
    // Prepare tasks
    if(collector instanceof MultiCollector) {
      MultiCollector<E> mc = ((MultiCollector<E>)collector);
      for(String source : mc.sources()) {
        Collector<E> c = mc.getCollector(source);
        Typeahead<E> ta = typeaheadMap.get(source);
        if(c != null && ta != null) {
          taskList.add(new TypeaheadTask<E>(ta, uid, terms, c, timeoutMillis));
        }
      }
    } else {
      multiCollector = new MultiSourceCollector<E>();
      for(Typeahead<E> ta : typeaheads) {
        Collector<E> c = collector.newInstance();
        multiCollector.putCollector(ta.getName(), c);
        taskList.add(new TypeaheadTask<E>(ta, uid, terms, c, timeoutMillis));
      }
    }
    
    int numTasks = taskList.size();
    if(numTasks > 0) {
      // Execute tasks
      List<Future<Collector<E>>> futureList = new ArrayList<Future<Collector<E>>>(taskList.size());
      for(TypeaheadTask<E> t : taskList) {
        futureList.add(executor.submit(t));
      }
      
      for(Future<Collector<E>> f : futureList) {
        try {
          f.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch(TimeoutException e) {
          warnTimeout(uid, terms, timeoutMillis);
        }catch(Exception e) {
          logger.warn(e.getMessage(), e);
        }
      }
    } else {
      multiCollector = null;
    }
    
    return multiCollector == null ? collector : multiCollector;
  }
  
  protected void warnTimeout(int user, String[] terms, long timeout) {
    StringBuilder sb = new StringBuilder();
    
    sb.append(getName())
      .append(" Timeout") 
      .append(" user=").append(user)
      .append(" time=").append(timeout)
      .append(" terms=").append(Strings.toSet(terms));
    
    logger.warn(sb.toString());
  }
  
  /**
   * TypeaheadTaskThreadFactory - Simple daemon thread factory.
   * 
   * @author jwu
   * @since 02/10, 2011
   */
  final static class TypeaheadTaskThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }
  }
}
