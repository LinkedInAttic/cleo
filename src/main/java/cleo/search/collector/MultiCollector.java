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

package cleo.search.collector;

import java.util.Collection;

import cleo.search.Element;

/**
 * MultiCollector
 * 
 * @author jwu
 * @since 02/10, 2011
 * 
 * @param <E> Element
 */
public interface MultiCollector<E extends Element> extends Collector<E> {

  /**
   * @return the Collection view of sources in this MultiCollector.
   */
  public Collection<String> sources();
  
  /**
   * @return the Collection view of collectors in this MulitCollector.
   */
  public Collection<Collector<E>> collectors();
  
  /**
   * Gets the collector for a specific source.
   * 
   * @param source - collector source.
   * @return the collector for a specific source.
   */
  public Collector<E> getCollector(String source);
  
  /**
   * Puts a collector for a specific source.
   *  
   * @param source
   * @param collector
   * @return <code>true</code> if the collector associated with a source is added successfully. Otherwise, <code>false</code>.
   */
  public boolean putCollector(String source, Collector<E> collector);
  
}
