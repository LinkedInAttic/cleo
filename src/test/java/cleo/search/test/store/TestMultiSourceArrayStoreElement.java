package cleo.search.test.store;

import java.io.File;

import krati.util.SourceWaterMarks;

import cleo.search.SimpleElement;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.VirtualArrayStoreElement;
import cleo.search.util.ScnFactory;
import cleo.search.util.SystemTimeScnFactory;

/**
 * TestMultiSourceArrayStoreElement
 * 
 * @author jwu
 * @since 05/10, 2011
 */
public class TestMultiSourceArrayStoreElement extends TestArrayStoreElement {
  
  public void testMultiSource() throws Exception {
    ScnFactory scnFactory = new SystemTimeScnFactory();
    SourceWaterMarks waterMarks = new SourceWaterMarks(new File(getStoreHomeDir(), "sourceWaterMarks.scn"));
    
    String sourceA = getClass().getSimpleName() + ".A";
    String sourceB = getClass().getSimpleName() + ".B";
    String sourceC = getClass().getSimpleName() + ".C";
    
    ArrayStoreElement<SimpleElement> storeA =
      new VirtualArrayStoreElement<SimpleElement>(sourceA, scnFactory, elementStore, waterMarks);
    
    ArrayStoreElement<SimpleElement> storeB =
      new VirtualArrayStoreElement<SimpleElement>(sourceB, scnFactory, elementStore, waterMarks);
    
    ArrayStoreElement<SimpleElement> storeC =
      new VirtualArrayStoreElement<SimpleElement>(sourceC, scnFactory, elementStore, waterMarks);
    
    SimpleElement element;
    long scn0 = System.currentTimeMillis();
    long scnA = scn0 + 100;
    long scnB = scn0 + 200;
    long scnC = scn0 + 300;
    
    for(long scn = scn0; scn <= scnA; scn++) {
      int elementId = inRangeId(); 
      element = new SimpleElement(elementId);
      element.setTimestamp(System.currentTimeMillis());
      element.setTerms(new String[] {"simple", "element", "A"});
      element.setScore(rand.nextFloat());
      
      storeA.setElement(elementId, element, scn);
    }
    assertEquals(scnA, storeA.getHWMark());
    assertTrue(storeA.getLWMark() <= storeA.getHWMark());
    
    for(long scn = scn0; scn <= scnB; scn++) {
      int elementId = inRangeId(); 
      element = new SimpleElement(elementId);
      element.setTimestamp(System.currentTimeMillis());
      element.setTerms(new String[] {"simple", "element", "B"});
      element.setScore(rand.nextFloat());
      
      storeB.setElement(elementId, element, scn);
    }
    assertEquals(scnB, storeB.getHWMark());
    assertTrue(storeB.getLWMark() <= storeB.getHWMark());
    
    for(long scn = scn0; scn <= scnC; scn++) {
      int elementId = inRangeId(); 
      element = new SimpleElement(elementId);
      element.setTimestamp(System.currentTimeMillis());
      element.setTerms(new String[] {"simple", "element", "C"});
      element.setScore(rand.nextFloat());
      
      storeC.setElement(elementId, element, scn);
    }
    assertEquals(scnC, storeC.getHWMark());
    assertTrue(storeC.getLWMark() <= storeC.getHWMark());
    
    storeA.persist();
    assertEquals(scnA, storeA.getLWMark());
    assertEquals(storeA.getLWMark(), storeA.getHWMark());
    
    long underlyingScn = elementStore.getHWMark();
    assertEquals(elementStore.getHWMark(), elementStore.getLWMark());
    
    storeB.persist();
    assertEquals(scnB, storeB.getLWMark());
    assertEquals(storeB.getLWMark(), storeB.getHWMark());
    
    assertEquals(underlyingScn, elementStore.getHWMark());
    assertEquals(underlyingScn, elementStore.getLWMark());
    
    storeC.persist();
    assertEquals(scnC, storeC.getLWMark());
    assertEquals(storeC.getLWMark(), storeC.getHWMark());
    
    assertEquals(underlyingScn, elementStore.getHWMark());
    assertEquals(underlyingScn, elementStore.getLWMark());
  }
}
