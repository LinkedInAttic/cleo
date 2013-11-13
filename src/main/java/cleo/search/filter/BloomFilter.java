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

package cleo.search.filter;

import cleo.search.Element;

/**
 * BloomFilter
 * 
 * @author jwu
 * @since 01/12, 2011
 */
public interface BloomFilter<T> {
  
  /**
   * @return the total number of bits of this BloomFilter. 
   */
  public int getNumBits();
  
  /**
   * Computes a bloom filter for querying based on a string.
   * 
   * @param value
   * @return query bloom filter
   */
  public T computeQueryFilter(String value);
  
  /**
   * Computes a bloom filter for querying based on a list of strings.
   * 
   * @param values
   * @return query bloom filter
   */
  public T computeQueryFilter(String... values);
  
  /**
   * Computes a bloom filter for indexing based on the terms of an Element.
   * 
   * @param element
   * @return index bloom filter
   */
  public T computeIndexFilter(Element element);
}
