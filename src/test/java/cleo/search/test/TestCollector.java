package cleo.search.test;

import java.util.List;
import java.util.Random;

import cleo.search.Element;
import cleo.search.ElementHit;
import cleo.search.SimpleElement;
import cleo.search.collector.Collector;
import cleo.search.collector.SimpleCollector;
import cleo.search.collector.SortedCollector;

import junit.framework.TestCase;

/**
 * TestCollector
 * 
 * @author jwu
 * @since 02/06, 2011
 */
public class TestCollector extends TestCase {
  private final Random rand = new Random();
  
  public TestCollector(String name) {
    super(name);
  }
  
  public void testSimpleCollector() {
    int capacity = Math.max(1, rand.nextInt(1000));
    Collector<Element> c = new SimpleCollector<Element>(capacity);
    assertTrue(c.isEmpty());
    assertEquals(capacity, c.capacity());
    assertEquals(0, c.size());
    
    int cnt = Math.max(1, rand.nextInt(1000));
    for(int i = 0; i < cnt; i++) {
      Element element = new SimpleElement(i);
      c.add(new ElementHit<Element>(element, rand.nextDouble()));
    }
    
    int size = Math.min(c.capacity(), cnt);
    assertEquals(size, c.size());
    assertEquals(size, c.elements().size());
    
    c.clear();
    assertEquals(capacity, c.capacity());
    assertEquals(0, c.size());
    assertEquals(0, c.elements().size());
    assertTrue(c.isEmpty());
  }
  
  public void testSortedCollector() {
    int capacity = 10;
    float maxScore = 0;
    Collector<Element> c = new SortedCollector<Element>(capacity);
    assertTrue(c.isEmpty());
    
    for(int i = 0; i < 100; i++) {
      Element element = new SimpleElement(i);
      float score = rand.nextFloat();
      element.setScore(score);
      maxScore = Math.max(maxScore, score);
      c.add(new ElementHit<Element>(element, score));
    }
    
    assertEquals(capacity, c.capacity());
    assertEquals(capacity, c.size());
    assertFalse(c.isEmpty());
    
    List<Element> results = c.elements();
    assertEquals(capacity, results.size());
    assertEquals(maxScore, results.get(0).getScore());
    
    float score = maxScore;
    for(int i = 0, cnt = results.size(); i < cnt; i++) {
      Element element = results.get(i);
      assertTrue(score >= element.getScore());
      score = element.getScore();
    }
    
    c.clear();
    assertEquals(capacity, c.capacity());
    assertEquals(0, c.size());
    assertEquals(0, c.elements().size());
    assertTrue(c.isEmpty());
  }
  
  public void testSortedCollectorSameScores() {
    int numElems = 100;
    int capacity = Math.max(1, rand.nextInt(numElems));
    Collector<Element> c = new SortedCollector<Element>(capacity);
    assertTrue(c.isEmpty());
    
    double score = rand.nextDouble();
    for(int i = 0; i < numElems; i++) {
      Element element = new SimpleElement(i);
      c.add(element, score, getClass().getSimpleName());
    }
    
    assertEquals(capacity, c.capacity());
    assertEquals(capacity, c.size());
    assertFalse(c.isEmpty());
  }
  
  public void testAddCollector() {
    Collector<Element> collector = new SortedCollector<Element>(10, 20);
    
    Collector<Element> c = new SortedCollector<Element>(10, 10);
    assertEquals(10, c.capacity());
    
    double score = rand.nextDouble();
    for(int i = 0; i < c.capacity(); i++) {
      Element element = new SimpleElement(i);
      c.add(element, score, getClass().getSimpleName());
    }
    
    assertEquals(c.capacity(), c.size());
    
    for(int i = 0; i < c.capacity(); i++) {
      Element element = new SimpleElement(i + c.capacity());
      c.add(element, score, getClass().getSimpleName());
    }
    
    assertEquals(c.capacity(), c.size());
    
    collector.add(c);
    assertEquals(c.size(), collector.size());
  }
}
