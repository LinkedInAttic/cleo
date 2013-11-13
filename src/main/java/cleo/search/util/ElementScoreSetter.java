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

package cleo.search.util;

import cleo.search.Element;
import cleo.search.store.ArrayStoreElement;

/**
 * ElementScoreSetter
 * 
 * @author jwu
 * @since 02/18, 2011
 */
public class ElementScoreSetter<E extends Element> implements ScoreHandler {
  ArrayStoreElement<E> elementStore;
  
  public ElementScoreSetter(ArrayStoreElement<E> elementStore) {
    this.elementStore = elementStore;
  }
  
  @Override
  public double handle(int elementId, double elementScore) {
    if(elementStore.hasIndex(elementId)) {
      E element = elementStore.getElement(elementId);
      if(element != null) {
        element.setScore((float)elementScore);
      }
    }
    
    return elementScore;
  }
}
