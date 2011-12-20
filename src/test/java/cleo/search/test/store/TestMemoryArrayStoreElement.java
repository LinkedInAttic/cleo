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

package cleo.search.test.store;

import cleo.search.SimpleElement;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.MemoryArrayStoreElement;

/**
 * TestMemoryArrayStoreElement
 * 
 * @author jwu
 * @since 05/10, 2011
 */
public class TestMemoryArrayStoreElement extends TestArrayStoreElement {
  
  @Override
  protected ArrayStoreElement<SimpleElement> createElementStore() throws Exception {
    ArrayStoreElement<SimpleElement> store = super.createElementStore();
    return new MemoryArrayStoreElement<SimpleElement>(store);
  }
  
  @Override
  protected ArrayStoreElement<SimpleElement> createElementStore(int idStart, int idCount) throws Exception {
    ArrayStoreElement<SimpleElement> store = super.createElementStore(idStart, idCount);
    return new MemoryArrayStoreElement<SimpleElement>(store);
  }
  
  public void testParallelLoading() throws Exception {
    SimpleElement element;
    
    for(int i = 0; i < 1000; i++) {
      int elementId = inRangeId();
      element = new SimpleElement(elementId);
      element.setTimestamp(System.currentTimeMillis());
      element.setTerms(new String[] {"simple", "element." + elementId});
      element.setScore(rand.nextFloat());
      
      long scn = System.currentTimeMillis();
      elementStore.setElement(elementId, element, scn);
    }
    
    elementStore.sync();
    
    ArrayStoreElement<SimpleElement> elementStore2 =
      createElementStore(elementStore.getIndexStart(), elementStore.capacity());
    
    for(int i = 0, cnt = elementStore.capacity(); i < cnt; i++) {
      int elementId = elementStore.getIndexStart() + i;
      element = elementStore.getElement(elementId);
      if(element != null) {
        assertEquals(elementId, elementStore2.getElement(elementId).getElementId());
      }
    }
    
    for(int i = 0, cnt = elementStore2.capacity(); i < cnt; i++) {
      int elementId = elementStore2.getIndexStart() + i;
      element = elementStore2.getElement(elementId);
      if(element != null) {
        assertEquals(elementId, elementStore.getElement(elementId).getElementId());
      }
    }
  }
}
