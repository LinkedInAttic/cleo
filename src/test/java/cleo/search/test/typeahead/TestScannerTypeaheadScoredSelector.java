package cleo.search.test.typeahead;

import java.util.List;

import cleo.search.Hit;
import cleo.search.SimpleElement;
import cleo.search.collector.Collector;
import cleo.search.collector.SortedCollector;
import cleo.search.selector.ScoredElementSelectorFactory;
import cleo.search.selector.SelectorFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.MemoryArrayStoreElement;

/**
 * TestScannerTypeaheadScoredSelector
 * 
 * @author jwu
 * @since 04/26, 2011
 */
public class TestScannerTypeaheadScoredSelector extends TestScannerTypeahead {

  @Override
  protected SelectorFactory<SimpleElement> createSelectorFactory() {
    return new ScoredElementSelectorFactory<SimpleElement>();
  }
  
  @Override
  protected ArrayStoreElement<SimpleElement> createElementStore() throws Exception {
    ArrayStoreElement<SimpleElement> elementStore = super.createElementStore();
    elementStore = new MemoryArrayStoreElement<SimpleElement>(elementStore);
    return elementStore;
  }
  
  public void testScoredElements() throws Exception {
    int uid = 0;
    double score;
    List<Hit<SimpleElement>> hits;
    Collector<SimpleElement> collector;
    
    // index elements
    for(int i = 0, cnt = getElementStoreCapacity(); i < cnt; i++) {
      SimpleElement elem = new SimpleElement(i);
      elem.setScore(rand.nextFloat());
      elem.setTimestamp(System.currentTimeMillis());
      elem.setTerms(i+"bloom", i+"filter", i+"typeahead");
      typeahead.index(elem);
    }
    
    collector = new SortedCollector<SimpleElement>(10);
    
    for(int i = 1; i < 10; i++) {
      collector.clear();
      collector = typeahead.search(uid, new String[] {""+i}, collector);
      assertEquals(10, collector.capacity());
      assertEquals(10, collector.size());
      
      score = Double.MAX_VALUE;
      hits = collector.hits();
      for(int j = 0; j < hits.size(); j++) {
        assertTrue(score >= hits.get(j).getScore());
        score = hits.get(j).getScore();
      }
    }
  }
}
