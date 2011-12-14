package cleo.search.test;

import java.util.Random;

import cleo.search.Element;
import cleo.search.ElementHit;
import cleo.search.Hit;
import cleo.search.SimpleElement;
import cleo.search.network.Proximity;
import junit.framework.TestCase;

/**
 * TestElementHit
 * 
 * @author jwu
 * @since 04/29, 2011
 */
public class TestElementHit extends TestCase {

  public void testCompareTo() {
    Random rand = new Random();
    Element e1 = new SimpleElement(10);
    Element e2 = new SimpleElement(11);
    
    String source = getClass().getSimpleName();
    
    // same score, same proximity, different elementId
    double score = rand.nextDouble();
    Hit<Element> hit1 = new ElementHit<Element>(e1, score, source);
    Hit<Element> hit2 = new ElementHit<Element>(e2, score, source);
    assertTrue(hit1.compareTo(hit2) < 0);
    
    hit1 = new ElementHit<Element>(e1, score, source, Proximity.DEGREE_1);
    hit2 = new ElementHit<Element>(e2, score, source, Proximity.DEGREE_1);
    assertTrue(hit1.compareTo(hit2) < 0);
    
    hit1 = new ElementHit<Element>(e1, score, source, Proximity.DEGREE_2);
    hit2 = new ElementHit<Element>(e2, score, source, Proximity.DEGREE_2);
    assertTrue(hit1.compareTo(hit2) < 0);

    hit1 = new ElementHit<Element>(e1, score, source, Proximity.OUT_OF_NETWORK);
    hit2 = new ElementHit<Element>(e2, score, source, Proximity.OUT_OF_NETWORK);
    assertTrue(hit1.compareTo(hit2) < 0);
    
    // same score, different proximity, different elementId
    hit1 = new ElementHit<Element>(e1, score, source, Proximity.DEGREE_1);
    hit2 = new ElementHit<Element>(e2, score, source, Proximity.DEGREE_2);
    assertTrue(hit1.compareTo(hit2) > 0);
    
    hit1 = new ElementHit<Element>(e1, score, source, Proximity.DEGREE_1);
    hit2 = new ElementHit<Element>(e2, score, source, Proximity.OUT_OF_NETWORK);
    assertTrue(hit1.compareTo(hit2) > 0);
    
    hit1 = new ElementHit<Element>(e1, score, source, Proximity.DEGREE_1);
    hit2 = new ElementHit<Element>(e2, score, source, Proximity.NONE);
    assertTrue(hit1.compareTo(hit2) > 0);
    

    hit1 = new ElementHit<Element>(e1, score, source, Proximity.DEGREE_2);
    hit2 = new ElementHit<Element>(e2, score, source, Proximity.OUT_OF_NETWORK);
    assertTrue(hit1.compareTo(hit2) > 0);

    hit1 = new ElementHit<Element>(e1, score, source, Proximity.DEGREE_2);
    hit2 = new ElementHit<Element>(e2, score, source, Proximity.NONE);
    assertTrue(hit1.compareTo(hit2) > 0);
    
    hit1 = new ElementHit<Element>(e1, score, source, Proximity.OUT_OF_NETWORK);
    hit2 = new ElementHit<Element>(e2, score, source, Proximity.NONE);
    assertTrue(hit1.compareTo(hit2) > 0);
    
    // different score
    hit1 = new ElementHit<Element>(e1, score + 0.0001, source, Proximity.DEGREE_1);
    hit2 = new ElementHit<Element>(e2, score + 0.0002, source, Proximity.DEGREE_1);
    assertTrue(hit1.compareTo(hit2) < 0);
    assertTrue(hit2.compareTo(hit1) > 0);
    
    hit1 = new ElementHit<Element>(e1, score + 0.0001, source, Proximity.DEGREE_1);
    hit2 = new ElementHit<Element>(e2, score + 0.0002, source, Proximity.DEGREE_2);
    assertTrue(hit1.compareTo(hit2) < 0);
    assertTrue(hit2.compareTo(hit1) > 0);
  }
}
