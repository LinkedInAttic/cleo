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

package cleo.search;

import java.io.IOException;

/**
 * Indexer
 * 
 * @author jwu
 * @since 01/12, 2011
 * 
 * @param <E> Element
 */
public interface Indexer<E extends Element> {
  
  /**
   * Indexes an element.
   * 
   * @param element - element to index
   * @return <code>true</code> if the element successfully indexed.
   *         Otherwise, <code>false</code>.
   * @throws Exception - NullPointerException is thrown upon a <code>null</code> element. 
   */
  public boolean index(E element) throws Exception;
  
  /**
   * Flushes indexes.
   * 
   * @throws IOException
   */
  public void flush() throws IOException;
}
