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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import cleo.search.store.ConnectionsStore;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;

import junit.framework.TestCase;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;
import krati.core.segment.WriteBufferSegmentFactory;

/**
 * TestConnectionsStore
 * 
 * @author jwu
 * @since 02/12, 2011
 */
public class TestConnectionsStore extends TestCase {
  protected Random rand = new Random();
  protected File storeHome;
  protected ConnectionsStore<String> store;
  
  protected int getInitialCapacity() {
    return 100000;
  }
  
  protected ConnectionsStore<String> createConnectionsStore() throws Exception {
    int indexSegmentFileSizeMB = 8;
    SegmentFactory indexSegmentFactory = new MemorySegmentFactory();
    int storeSegmentFileSizeMB = 32;
    SegmentFactory storeSegmentFactory = new WriteBufferSegmentFactory(storeSegmentFileSizeMB);
    
    return StoreFactory.createConnectionsStore(storeHome, getInitialCapacity(), indexSegmentFileSizeMB, indexSegmentFactory, storeSegmentFileSizeMB, storeSegmentFactory);
  }
  
  @Override
  protected void setUp() {
    String storeName = getClass().getSimpleName();
    storeHome = FileUtils.getTestDir(storeName);
    
    try {
      store = createConnectionsStore();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  @Override
  protected void tearDown() {
    if(storeHome != null) {
      try {
        store.close();
        FileUtils.deleteDirectory(storeHome);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void testApiBasics() throws Exception {
    String source = "key" + rand.nextInt();
    int[] connections;
    int newConnection;
    
    connections = new int[] { 1, 2, 3, 7, 19 };
    newConnection = 23;
    runApiBasics(source, connections, newConnection);
    
    connections = new int[] {};
    newConnection = rand.nextInt();
    runApiBasics(source, connections, newConnection);
    
    connections = null;
    newConnection = rand.nextInt();
    runApiBasics(source, connections, newConnection);
  }
  
  private void runApiBasics(String source, int[] connections, int newConnection) throws Exception {
    int[] connectionsRead;
    int cnt = connections == null ? 0 : connections.length;
    
    store.putConnections(source, connections, System.currentTimeMillis());
    connectionsRead = store.getConnections(source);
    assertTrue(Arrays.equals(connections, connectionsRead));
    
    store.addConnection(source, newConnection, System.currentTimeMillis());
    connectionsRead = store.getConnections(source);
    assertEquals(cnt + 1, connectionsRead.length);

    assertTrue(store.getLWMark() <= store.getHWMark());
    store.persist();
    assertTrue(store.getLWMark() == store.getHWMark());
    
    store.removeConnection(source, newConnection, System.currentTimeMillis());
    connectionsRead = store.getConnections(source);
    assertEquals(cnt, connectionsRead.length);
    
    store.deleteConnections(source, System.currentTimeMillis());
    connectionsRead = store.getConnections(source);
    assertEquals(null, connectionsRead);
    
    assertTrue(store.getLWMark() <= store.getHWMark());
    store.sync();
    assertTrue(store.getLWMark() == store.getHWMark());
  }
}
