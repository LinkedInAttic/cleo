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

import java.util.List;

import cleo.search.ElementSerializer;
import cleo.search.SimpleElement;
import cleo.search.SimpleElementSerializer;
import cleo.search.connection.Connection;
import cleo.search.connection.SimpleConnection;

/**
 * TestVanillaNetworkTypeahead
 * 
 * @author jwu
 * @since 01/18, 2011
 */
public class TestVanillaNetworkTypeahead extends AbstractTestVanillaNetworkTypeahead<SimpleElement> {

  @Override
  protected ElementSerializer<SimpleElement> createElementSerializer() {
    return new SimpleElementSerializer();
  }

  public void testApiBasics() throws Exception {
    List<SimpleElement> results;
    
    int uid = getConnectionsStoreIndexStart() + rand.nextInt(this.getConnectionsStoreCapacity());
    int elemId = getElementStoreIndexStart() + rand.nextInt(getElementStoreCapacity());
    
    Connection conn = new SimpleConnection(uid, elemId, true);
    conn.setTimestamp(System.currentTimeMillis());
    
    SimpleElement elem = new SimpleElement(elemId);
    elem.setTimestamp(System.currentTimeMillis());
    elem.setTerms("Bloom", "filter");
    
    assertTrue(typeahead.index(elem));
    assertTrue(typeahead.index(conn));
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
    
    typeahead.flush();
  }
}
