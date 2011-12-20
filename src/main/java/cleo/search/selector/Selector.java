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

package cleo.search.selector;

import java.io.Serializable;

import cleo.search.Element;

/**
 * Selector
 * 
 * @author jwu
 * @since 01/12, 2011
 */
public interface Selector<E extends Element> extends Serializable {
  
  /**
   * Selects an element and calculates its score.
   * 
   * @param element - Element to select.
   * @param ctx     - Selector context which stores the calculated score if element is selected.
   * @return <code>true</code> if the element is selected (i.e. accepted) by this selector. Otherwise, <code>false</code>.
   */
  public boolean select(E element, SelectorContext ctx);
  
}
