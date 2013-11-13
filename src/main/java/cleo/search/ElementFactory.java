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

/**
 * ElementFactory
 * 
 * @author jwu
 * @since 01/20, 2011
 * 
 * @param <E> Element
 */
public interface ElementFactory<E extends Element> {

  /**
   * Creates a new element based on a user supplied Element.
   * 
   * @param element - User supplied Element.
   * @return a new Element
   */
  public E createElement(Element element);
  
  /**
   * Creates a new element based on user supplied information.
   * @param elementId - Element Id
   * @param timestamp - Element timestamp
   * @param terms     - Element terms
   * @return a new Element
   */
  public E createElement(int elementId, long timestamp, String... terms);
  
}
