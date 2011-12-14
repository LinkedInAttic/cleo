package cleo.search.test.typeahead;

import java.util.List;

import cleo.search.ElementSerializer;
import cleo.search.Hit;
import cleo.search.SimpleElement;
import cleo.search.SimpleElementSerializer;
import cleo.search.collector.Collector;
import cleo.search.collector.SortedCollector;

/**
 * TestBrowseTypeahead
 * 
 * @author jwu
 * @since 02/12, 2011
 */
public class TestBrowseTypeahead extends AbstractTestBrowseTypeahead<SimpleElement> {
  
  @Override
  protected ElementSerializer<SimpleElement> createElementSerializer() {
    return new SimpleElementSerializer();
  }
  
  public void testApiBasics() throws Exception {
    int uid = 0;
    long timeoutMillis = 100;
    Collector<SimpleElement> collector;

    collector = new SortedCollector<SimpleElement>(10);
    
    for(int i = 0; i < 1000; i++) {
      SimpleElement elem = new SimpleElement(i);
      elem.setTimestamp(System.currentTimeMillis());
      elem.setTerms(i+"bloom", i+"filter", i+"typeahead");
      typeahead.getElementStore().setElement(i, elem, elem.getTimestamp());
    }
    
    // No elements to browse
    typeahead.update(null);
    
    collector = typeahead.search(uid, new String[] {"1"}, collector);
    assertEquals(10, collector.capacity());
    assertEquals(0, collector.size());
    
    collector = typeahead.search(uid, new String[] {"1"}, collector, timeoutMillis);
    assertEquals(10, collector.capacity());
    assertEquals(0, collector.size());
    
    // No elements to browse
    typeahead.update(new int[0]);
    
    collector = typeahead.search(uid, new String[] {"1"}, collector);
    assertEquals(10, collector.capacity());
    assertEquals(0, collector.size());
    
    collector = typeahead.search(uid, new String[] {"1"}, collector, timeoutMillis);
    assertEquals(10, collector.capacity());
    assertEquals(0, collector.size());
    
    // First 100 elements to browse
    int[] elementIds = new int[100];
    for(int i = 0; i < elementIds.length; i++) {
      elementIds[i] = i;
    }
    
    typeahead.update(elementIds);
    
    collector = new SortedCollector<SimpleElement>(10);
    collector = typeahead.search(uid, new String[] {"1"}, collector);
    assertEquals(10, collector.capacity());
    assertEquals(collector.capacity(), collector.size());
    
    collector = typeahead.search(uid, new String[] {"1"}, collector, timeoutMillis);
    assertEquals(10, collector.capacity());
    assertTrue(collector.capacity() >= collector.size());
    
    collector = new SortedCollector<SimpleElement>(100);
    collector = typeahead.search(uid, new String[] {"1"}, collector);
    assertEquals(100, collector.capacity());
    assertEquals(11, collector.size());
    
    List<Hit<SimpleElement>> hits = collector.hits();
    double score = 1;
    for(Hit<SimpleElement> s : hits) {
      assertTrue(score >= s.getScore());
      score = s.getScore();
    }
  }
}
