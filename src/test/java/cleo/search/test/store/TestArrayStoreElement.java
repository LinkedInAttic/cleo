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

import cleo.search.ElementSerializer;
import cleo.search.SimpleElement;
import cleo.search.SimpleElementSerializer;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;
import junit.framework.TestCase;
import krati.core.segment.Segment;

/**
 * TestArrayStoreElement
 * 
 * @author jwu
 * @since 05/10, 2011
 */
public class TestArrayStoreElement extends TestCase {
  protected final Random rand = new Random();
  protected ArrayStoreElement<SimpleElement> elementStore;
  
  protected File getStoreHomeDir() {
    String storeName = getClass().getSimpleName();
    File storeHomeDir = FileUtils.getTestDir(storeName);
    return storeHomeDir;
  }
  
  protected ElementSerializer<SimpleElement> createSerializer() {
    return new SimpleElementSerializer();
  }
  
  protected ArrayStoreElement<SimpleElement> createElementStore() throws Exception {
    File storeHomeDir = getStoreHomeDir();
    int idStart = rand.nextInt(1000);
    int idCount = 1000 + rand.nextInt(1000);
    int segmentFileSizeMB = Segment.minSegmentFileSizeMB;
    ElementSerializer<SimpleElement> serializer = createSerializer();
    
    return StoreFactory.createElementStorePartition(storeHomeDir, idStart, idCount, segmentFileSizeMB, serializer);
  }
  
  protected ArrayStoreElement<SimpleElement> createElementStore(int idStart, int idCount) throws Exception {
    File storeHomeDir = getStoreHomeDir();
    int segmentFileSizeMB = Segment.minSegmentFileSizeMB;
    ElementSerializer<SimpleElement> serializer = createSerializer();
    
    return StoreFactory.createElementStorePartition(storeHomeDir, idStart, idCount, segmentFileSizeMB, serializer);
  }
  
  @Override
  protected void setUp() {
    try {
      elementStore = createElementStore();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Override
  protected void tearDown() {
    try {
      elementStore = null;
      FileUtils.deleteDirectory(getStoreHomeDir());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  protected int inRangeId() {
    return elementStore.getIndexStart() + rand.nextInt(elementStore.capacity());
  }
  
  protected int outOfRangeId() {
    return elementStore.getIndexStart() + elementStore.capacity() + rand.nextInt(elementStore.capacity());
  }
  
  public void testApiBasics() throws Exception {
    SimpleElement element;
    SimpleElement elementRead;
    
    int elementId = inRangeId(); 
    element = new SimpleElement(elementId);
    element.setTimestamp(System.currentTimeMillis());
    element.setTerms(new String[] {"simple", "element"});
    element.setScore(rand.nextFloat());
    
    long scn = System.currentTimeMillis();
    elementStore.setElement(elementId, element, scn);
    elementStore.sync();
    assertEquals(scn, elementStore.getLWMark());
    assertEquals(elementStore.getLWMark(), elementStore.getHWMark());
    
    elementRead = elementStore.getElement(elementId); 
    assertTrue(elementRead != null);
    assertEquals(element.getElementId(), elementRead.getElementId());
    assertEquals(element.getTimestamp(), elementRead.getTimestamp());
    assertEquals(element.getScore(), elementRead.getScore());
    assertTrue(Arrays.equals(element.getTerms(), elementRead.getTerms()));
    
    scn++;
    elementStore.deleteElement(elementId, scn);
    assertTrue(elementStore.getElement(elementId) == null);
    assertEquals(scn, elementStore.getHWMark());
    assertTrue(elementStore.getLWMark() < elementStore.getHWMark());
    
    elementStore.persist();
    assertEquals(scn, elementStore.getLWMark());
    assertEquals(elementStore.getLWMark(), elementStore.getHWMark());
    
    elementId = outOfRangeId();
    try {
      elementRead = elementStore.getElement(elementId);
      assertEquals(null, elementRead);
    } catch(Exception e) {
      assertEquals(ArrayIndexOutOfBoundsException.class, e.getClass());
    }
    
    ElementSerializer<SimpleElement> serializer = createSerializer();
    byte[] elementBytes;

    // Test delete/setElement/getElementBytes
    elementId = inRangeId();
    element.setElementId(elementId);
    
    scn++;
    elementStore.deleteElement(elementId, scn);
    assertEquals(null, elementStore.getElement(elementId));
    
    scn++;
    elementStore.setElement(elementId, element, scn);
    elementBytes = elementStore.getElementBytes(elementId);
    SimpleElement deserializedElement = serializer.deserialize(elementBytes);
    assertEquals(element.getElementId(), deserializedElement.getElementId());
    assertTrue(Arrays.equals(element.getTerms(), deserializedElement.getTerms()));
    
    // Test delete/setElementBytes/getElement
    elementId = inRangeId();
    element.setElementId(elementId);
    
    scn++;
    elementStore.deleteElement(elementId, scn);
    assertEquals(null, elementStore.getElement(elementId));
    
    scn++;
    elementBytes = serializer.serialize(element);
    elementStore.setElementBytes(elementId, elementBytes, scn);
    
    elementRead = elementStore.getElement(elementId);
    assertEquals(element.getElementId(), elementRead.getElementId());
    assertTrue(Arrays.equals(element.getTerms(), elementRead.getTerms()));
  }
}
