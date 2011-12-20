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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cleo.search.ElementSerializer;
import cleo.search.Hit;
import cleo.search.SimpleElement;
import cleo.search.SimpleElementSerializer;
import cleo.search.collector.Collector;
import cleo.search.collector.SortedCollector;
import cleo.search.connection.Connection;
import cleo.search.connection.SimpleConnection;
import cleo.search.network.Proximity;
import cleo.search.store.ArrayStoreWeights;
import cleo.search.typeahead.NetworkTypeaheadContext;

/**
 * TestWeightedNetworkTypeahead
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public class TestWeightedNetworkTypeahead extends AbstractTestWeightedNetworkTypeahead<SimpleElement> {

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
  
  public void testConnectionIndexer() throws Exception {
    int cnt = rand.nextInt(1000) + 10;
    long scn = System.currentTimeMillis();
    List<Connection> list = new ArrayList<Connection>(cnt);
    Map<String, Connection> map = new HashMap<String, Connection>(cnt);
    
    ArrayStoreWeights store = typeahead.getConnectionsStore();
    
    for(int i = 0; i < cnt; i++) {
      int source = getConnectionsStoreIndexStart() + rand.nextInt(this.getConnectionsStoreCapacity());
      int target = getElementStoreIndexStart() + rand.nextInt(getElementStoreCapacity());
      String key = source + "=>" + target;
      
      if(!map.containsKey(key)) {
        Connection conn = new SimpleConnection(source, target, true);
        conn.setStrength(rand.nextInt(1000000));
        
        map.put(key, conn);
        list.add(conn);
      }
    }
    
    // Index active connections
    for(Connection conn : list) {
      conn.setTimestamp(scn++);
      typeahead.index(conn);
    }
    
    for(Connection conn : list) {
      assertEquals(conn.getStrength(), store.getWeight(conn.source(), conn.target()));
      
      int[][] dat = store.getWeightData(conn.source());
      assertEquals(2, dat.length);
      assertTrue(dat[ArrayStoreWeights.ELEMID_SUBARRAY_INDEX].length > 0);
      assertTrue(dat[ArrayStoreWeights.ELEMID_SUBARRAY_INDEX].length == dat[ArrayStoreWeights.WEIGHT_SUBARRAY_INDEX].length);
    }
    
    // Index inactive connections
    for(Connection conn : list) {
      conn.setActive(false);
      conn.setTimestamp(scn++);
      typeahead.index(conn);
    }
    
    for(Connection conn : list) {
      assertEquals(0, store.getWeight(conn.source(), conn.target()));
      
      int[][] dat = store.getWeightData(conn.source());
      assertEquals(2, dat.length);
      assertEquals(0, dat[ArrayStoreWeights.ELEMID_SUBARRAY_INDEX].length);
      assertTrue(dat[ArrayStoreWeights.ELEMID_SUBARRAY_INDEX].length == dat[ArrayStoreWeights.WEIGHT_SUBARRAY_INDEX].length);
    }
  }
  
  public void testSearchNetworkConnections() throws Exception {
    Set<Integer> uidSet = new HashSet<Integer>();
    Set<Integer> elemIdSet = new HashSet<Integer>();
    
    for(int i = 0; i < 10; i++) {
      int uid = getPartitionStart() + rand.nextInt(getPartitionCount());
      if(!uidSet.contains(uid)) {
        uidSet.add(uid);
        elemIdSet.add(uid);
        createNetworkConnections(uid, elemIdSet);
      }
    }
    
    for(int elemId : elemIdSet) {
      SimpleElement elem = new SimpleElement(elemId);
      elem.setTimestamp(System.currentTimeMillis());
      elem.setScore(rand.nextFloat());
      
      if(elem.getScore() < 0.25) {
        elem.setTerms("bloom", "filter");
      } else if(elem.getScore() < 0.5) {
        elem.setTerms("bloom", "city", "news");
      } else if(elem.getScore() < 0.75) {
        elem.setTerms("financial", "news", "bloom", "corp");
      } else {
        elem.setTerms("ibm", "financial", "bloom", "services");
      }
      
      typeahead.index(elem);
    }
    
    // Flush indexes
    typeahead.flush();
    
    Collector<SimpleElement> collector;
    
    // Collect up to 10 elements
    collector = new SortedCollector<SimpleElement>(10);
    for(int uid : uidSet) {
      collector.clear();
      collector = typeahead.search(uid, new String[]{"blo"}, collector);
      assertTrue(collector.size() > 0);
      for(Hit<SimpleElement> hit : collector.hits()) {
        assertEquals(Proximity.DEGREE_1, hit.getProximity());
      }
    }
    
    // Collect all matched elements
    collector = new SortedCollector<SimpleElement>(elemIdSet.size());
    for(int uid : uidSet) {
      int deg1Cnt = 0;
      int deg2Cnt = 0;
      
      NetworkTypeaheadContext context;
      context = typeahead.createContext(uid);
      context.setTimeoutMillis(Long.MAX_VALUE);
      
      collector.clear();
      collector = typeahead.searchNetwork(uid, new String[]{"blo"}, collector, context);
      
      assertTrue(collector.size() > 0);
      for(Hit<SimpleElement> hit : collector.hits()) {
        if(hit.getProximity() == Proximity.DEGREE_1) deg1Cnt++;
        else if(hit.getProximity() == Proximity.DEGREE_2) deg2Cnt++;
      }
      
      assertTrue(deg1Cnt > 0);
      assertTrue(deg2Cnt > 0);
      assertEquals(collector.size(), deg1Cnt + deg2Cnt);
    }
  }
  
  public void createNetworkConnections(int uid, Set<Integer> elemIdSet) throws Exception {
    int uidMax = getPartitionStart() + getPartitionCount() + rand.nextInt(getPartitionCount());
    List<Connection> connList = new ArrayList<Connection>();
    
    Set<Integer> uidSetDeg1 = new HashSet<Integer>();
    for(int i = 0, cnt = rand.nextInt(10) + 10; i < cnt; i++) {
      int target = rand.nextInt(uidMax);
      if(uid != target && !uidSetDeg1.contains(target)) {
        uidSetDeg1.add(target);
      }
    }
    elemIdSet.addAll(uidSetDeg1);
    
    for(int target : uidSetDeg1) {
      Connection conn = new SimpleConnection(uid, target, true);
      conn.setStrength(rand.nextInt(10000));
      connList.add(conn);
    }
    
    for(int source : uidSetDeg1) {
      Set<Integer> uidSetDeg2 = new HashSet<Integer>();
      
      for(int i = 0, cnt = rand.nextInt(10) + 10; i < cnt; i++) {
        int target = rand.nextInt(uidMax);
        if(source != target && !uidSetDeg2.contains(target)) {
          uidSetDeg2.add(target);
        }
      }
      
      for(int target : uidSetDeg2) {
        Connection conn = new SimpleConnection(source, target, true);
        conn.setStrength(rand.nextInt(10000));
        connList.add(conn);
      }
      
      elemIdSet.addAll(uidSetDeg2);
      uidSetDeg2.clear();
    }
    
    // Index active connections
    for(Connection conn : connList) {
      conn.setTimestamp(System.currentTimeMillis());
      typeahead.index(conn);
    }
  }
  
  public void testSimpleNetwork() throws Exception {
    SimpleElement element;
    
    // Index elements: 1, 11, 12, 13, 111, 112, 121, 131
    element = new SimpleElement(1);
    element.setScore(rand.nextFloat());
    element.setTimestamp(System.currentTimeMillis());
    element.setTerms(new String[]{"001", "professor", "linkedin"});
    typeahead.index(element);
    
    element = new SimpleElement(11);
    element.setScore(rand.nextFloat());
    element.setTimestamp(System.currentTimeMillis());
    element.setTerms(new String[]{"011", "college", "student", "linkedin"});
    typeahead.index(element);
    
    element = new SimpleElement(12);
    element.setScore(rand.nextFloat());
    element.setTimestamp(System.currentTimeMillis());
    element.setTerms(new String[]{"012", "college", "student", "linkedin"});
    typeahead.index(element);
    
    element = new SimpleElement(13);
    element.setScore(rand.nextFloat());
    element.setTimestamp(System.currentTimeMillis());
    element.setTerms(new String[]{"013", "university", "student", "linkedin"});
    typeahead.index(element);
    
    element = new SimpleElement(111);
    element.setScore(rand.nextFloat());
    element.setTimestamp(System.currentTimeMillis());
    element.setTerms(new String[]{"111", "arts", "linkedin"});
    typeahead.index(element);
    
    element = new SimpleElement(112);
    element.setScore(rand.nextFloat());
    element.setTimestamp(System.currentTimeMillis());
    element.setTerms(new String[]{"112", "art", "linkedin"});
    typeahead.index(element);
    
    element = new SimpleElement(121);
    element.setScore(rand.nextFloat());
    element.setTimestamp(System.currentTimeMillis());
    element.setTerms(new String[]{"121", "art", "linkedin"});
    typeahead.index(element);
    
    element = new SimpleElement(131);
    element.setScore(rand.nextFloat());
    element.setTimestamp(System.currentTimeMillis());
    element.setTerms(new String[]{"131", "business", "linkedin"});
    typeahead.index(element);
    
    /**
     * Index connections:
     *   001 -> 011
     *   001 -> 012
     *   001 -> 013
     *   001 -> 111
     *   001 -> 499 (element 499 does not exist)
     *   
     *   011 -> 111
     *   011 -> 112
     *   
     *   012 -> 001
     *   012 -> 121
     *   
     *   013 -> 001
     *   013 -> 131
     */
    Connection conn;
    conn = new SimpleConnection(1, 11, true);
    typeahead.index(conn);
    
    conn = new SimpleConnection(1, 12, true);
    typeahead.index(conn);
    
    conn = new SimpleConnection(1, 13, true);
    typeahead.index(conn);
    
    conn = new SimpleConnection(1, 499, true);
    typeahead.index(conn);
    
    conn = new SimpleConnection(1, 111, true);
    typeahead.index(conn);
    
    conn = new SimpleConnection(11, 111, true);
    typeahead.index(conn);
    
    conn = new SimpleConnection(11, 112, true);
    typeahead.index(conn);

    conn = new SimpleConnection(12, 1, true);
    typeahead.index(conn);
    
    conn = new SimpleConnection(12, 121, true);
    typeahead.index(conn);

    conn = new SimpleConnection(13, 1, true);
    typeahead.index(conn);
    
    conn = new SimpleConnection(13, 131, true);
    typeahead.index(conn);
    
    typeahead.flush();
    
    // Search
    NetworkTypeaheadContext context = typeahead.createContext(1);
    Collector<SimpleElement> collector = new SortedCollector<SimpleElement>(10, 100);
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"111"}, collector, context);
    assertEquals(1, collector.size());
    Hit<SimpleElement> hit = collector.hits().get(0);
    assertEquals(hit.getProximity(), Proximity.DEGREE_1);
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"0"}, collector, context);
    assertEquals(3, collector.size());
    for(Hit<SimpleElement> h : collector.hits()) {
      assertEquals(h.getProximity(), Proximity.DEGREE_1);
    }
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"univ"}, collector, context);
    assertEquals(1, collector.size());
    for(Hit<SimpleElement> h : collector.hits()) {
      assertEquals(h.getProximity(), Proximity.DEGREE_1);
    }
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"coll"}, collector, context);
    assertEquals(2, collector.size());
    for(Hit<SimpleElement> h : collector.hits()) {
      assertEquals(h.getProximity(), Proximity.DEGREE_1);
    }
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"stu"}, collector, context);
    assertEquals(3, collector.size());
    for(Hit<SimpleElement> h : collector.hits()) {
      assertEquals(h.getProximity(), Proximity.DEGREE_1);
    }
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"arts"}, collector, context);
    assertEquals(1, collector.size());
    for(Hit<SimpleElement> h : collector.hits()) {
      assertEquals(h.getProximity(), Proximity.DEGREE_1);
    }
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"art"}, collector, context);
    assertEquals(3, collector.size());
    for(Hit<SimpleElement> h : collector.hits()) {
      if(h.getElement().getElementId() == 111) {
        assertEquals(h.getProximity(), Proximity.DEGREE_1);
      } else {
        assertEquals(h.getProximity(), Proximity.DEGREE_2);
      }
    }
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"busin"}, collector, context);
    assertEquals(1, collector.size());
    for(Hit<SimpleElement> h : collector.hits()) {
      assertEquals(h.getProximity(), Proximity.DEGREE_2);
    }
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"prof"}, collector, context);
    assertEquals(0, collector.size());
    
    collector.clear();
    typeahead.searchNetwork(1, new String[]{"linked"}, collector, context);
    assertEquals(7, collector.size());
  }
}
