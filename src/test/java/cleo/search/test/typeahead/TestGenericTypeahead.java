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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cleo.search.ElementSerializer;
import cleo.search.SimpleElement;
import cleo.search.SimpleElementSerializer;
import cleo.search.store.ConnectionsStore;

/**
 * TestGenericTypeahead
 * 
 * @author jwu
 * @since 02/04, 2011
 */
public class TestGenericTypeahead extends AbstractTestGenericTypeahead<SimpleElement> {

  @Override
  protected ElementSerializer<SimpleElement> createElementSerializer() {
    return new SimpleElementSerializer();
  }
  
  protected boolean dumpEnabled() {
    return true;
  }
  
  public void testApiBasics() throws Exception {
    int uid = 0;
    List<SimpleElement> results;
    
    int elemId = getElementStoreIndexStart() + rand.nextInt(getElementStoreCapacity());
    SimpleElement elem = new SimpleElement(elemId);
    elem.setTimestamp(System.currentTimeMillis());
    elem.setTerms("Bloom", "filter");
    
    typeahead.getElementStore().clear();
    typeahead.index(elem);
    
    results = typeahead.search(uid, new String[] {"Bloo"});
    assertTrue(results.size() == 1);
    assertEquals(elem.getElementId(), results.get(0).getElementId());
    assertEquals(elem.getTimestamp(), results.get(0).getTimestamp());
    
    results = typeahead.search(uid, new String[] {"Bloo", "filter"});
    assertTrue(results.size() == 1);
    assertEquals(elem.getElementId(), results.get(0).getElementId());
    assertEquals(elem.getTimestamp(), results.get(0).getTimestamp());
    
    results = typeahead.search(uid, new String[] {"filter", "Bl"});
    assertTrue(results.size() == 1);
    assertEquals(elem.getElementId(), results.get(0).getElementId());
    assertEquals(elem.getTimestamp(), results.get(0).getTimestamp());
    
    results = typeahead.search(uid, new String[] {"filtering", "Bl"});
    assertTrue(results.size() == 0);
    
    // Refresh
    typeahead.refresh();
    
    results = typeahead.search(uid, new String[] {"filter", "Bl"});
    assertTrue(results.size() == 1);
    assertEquals(elem.getElementId(), results.get(0).getElementId());
    assertEquals(elem.getTimestamp(), results.get(0).getTimestamp());
    
    results = typeahead.search(uid, new String[] {"filtering", "Bl"});
    assertTrue(results.size() == 0);
  }
  
  public void testIndexing() throws Exception {
    ConnectionsStore<String> connectionsStore = typeahead.getConnectionsStore();
    
    int elemId = getElementStoreIndexStart() + rand.nextInt(getElementStoreCapacity());
    SimpleElement elem = new SimpleElement(elemId);
    elem.setTimestamp(System.currentTimeMillis());
    elem.setTerms("Bloom", "filter", "generic", "typeahead");
    
    // Index the first element
    typeahead.getElementStore().clear();
    typeahead.index(elem);
    typeahead.flush();
    
    HashSet<String> termSet = new HashSet<String>();
    HashSet<String> prefixSet = new HashSet<String>(100);
    for(String term : elem.getTerms()) {
      termSet.add(term);
      int len = Math.min(term.length(), typeahead.getMaxKeyLength());
      for(int i = 1; i<= len; i++) {
        prefixSet.add(term.substring(0, i));
      }
    }
    
    HashSet<String> sourceSet = new HashSet<String>(100);
    Iterator<String> sourceIterator = connectionsStore.sourceIterator();
    while(sourceIterator.hasNext()) {
      sourceSet.add(sourceIterator.next());
    }
    
    HashSet<String> unionSet = new HashSet<String>(100);
    unionSet.addAll(sourceSet);
    unionSet.addAll(prefixSet);
    
    assertEquals(prefixSet.size(), sourceSet.size());
    assertEquals(prefixSet.size(), unionSet.size());
    
    if(dumpEnabled()) {
      System.out.println("\tmaxKeyLength " + typeahead.getMaxKeyLength());
      System.out.println("\t" + termSet.size() + " terms " + termSet);
      System.out.println("\t" + unionSet.size() + " prefixes " + unionSet);
    }
    
    sourceIterator = connectionsStore.sourceIterator();
    while(sourceIterator.hasNext()) {
      String source = sourceIterator.next();
      int[] connections = connectionsStore.getConnections(source);
      assertEquals(1, connections.length);
    }
    
    // Index the second element with the same terms as the first element
    SimpleElement elem2 = (SimpleElement)elem.clone();
    if(elem2.getElementId() > getElementStoreIndexStart()) {
      elem2.setElementId(elem2.getElementId() - 1);
    } else {
      elem2.setElementId(elem2.getElementId() + 1);
    }
    typeahead.index(elem2);
    typeahead.flush();
    
    sourceIterator = connectionsStore.sourceIterator();
    while(sourceIterator.hasNext()) {
      String source = sourceIterator.next();
      int[] connections = connectionsStore.getConnections(source);
      assertEquals(2, connections.length);
      assertFalse(connections[0] == connections[1]);
    }
    
    // Do a simple search
    List<SimpleElement> results = typeahead.search(0, new String[] {"filter", "Bl"});
    assertTrue(results != null);
    assertEquals(2, results.size());
    
    // Refresh and do a simple search
    typeahead.refresh();
    
    sourceIterator = connectionsStore.sourceIterator();
    while(sourceIterator.hasNext()) {
      String source = sourceIterator.next();
      int[] connections = connectionsStore.getConnections(source);
      assertEquals(2, connections.length);
      assertFalse(connections[0] == connections[1]);
    }
    
    // Do a simple search
    results = typeahead.search(0, new String[] {"filter", "Bl"});
    assertTrue(results != null);
    assertEquals(2, results.size());
  }
}
