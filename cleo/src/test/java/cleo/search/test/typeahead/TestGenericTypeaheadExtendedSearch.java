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

package cleo.search.test.typeahead;

import cleo.search.ElementSerializer;
import cleo.search.SimpleElement;
import cleo.search.SimpleElementSerializer;
import cleo.search.collector.Collector;
import cleo.search.collector.SimpleCollector;
import cleo.search.collector.SortedCollector;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.MemoryArrayStoreElement;

/**
 * TestGenericTypeaheadExtendedSearch
 * 
 * @author jwu
 * @since 02/04, 2011
 */
public class TestGenericTypeaheadExtendedSearch extends AbstractTestGenericTypeahead<SimpleElement> {

  @Override
  protected ElementSerializer<SimpleElement> createElementSerializer() {
    return new SimpleElementSerializer();
  }
  
  protected ArrayStoreElement<SimpleElement> createElementStore() throws Exception {
    ArrayStoreElement<SimpleElement> elementStore = super.createElementStore();
    // Load into memory
    elementStore = new MemoryArrayStoreElement<SimpleElement>(elementStore);
    return elementStore;
  }
  
  public void testExtendedSearch() throws Exception {
    int uid = 0;
    
    for(int i = 0; i < 1000; i++) {
      SimpleElement elem = new SimpleElement(i);
      elem.setTimestamp(System.currentTimeMillis());
      elem.setTerms(i+"bloom", i+"filter", i+"generic", i+"typeahead");
      elem.setScore(rand.nextFloat());
      typeahead.index(elem);
    }
    
    // SimpleCollector
    Collector<SimpleElement> c;
    c = new SimpleCollector<SimpleElement>(10);
    assertEquals(10, c.capacity());
    
    c.clear();
    typeahead.search(uid, new String[] { "0" }, c);
    assertEquals(1, c.size());
    
    c.clear();
    typeahead.search(uid, new String[] { "1" }, c);
    assertEquals(c.capacity(), c.size());
    
    // SortedCollector
    c = new SortedCollector<SimpleElement>(5);
    assertEquals(5, c.capacity());
    
    c.clear();
    typeahead.search(uid, new String[] { "0" }, c);
    assertEquals(1, c.size());
    
    c.clear();
    typeahead.search(uid, new String[] { "1" }, c);
    assertEquals(c.capacity(), c.size());
    
    c.clear();
    typeahead.search(uid, new String[] { "0", "1", "2" }, c);
    assertEquals(0, c.size());
  }
}
