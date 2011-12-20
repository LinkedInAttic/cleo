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
import cleo.search.Hit;
import cleo.search.SimpleElement;
import cleo.search.SimpleElementSerializer;
import cleo.search.collector.Collector;
import cleo.search.collector.SortedCollector;
import cleo.search.typeahead.RangeException;
import cleo.search.typeahead.RangeTypeahead;

/**
 * TestScannerTypeahead
 * 
 * @author jwu
 * @since 03/24, 2011
 */
public class TestScannerTypeahead extends AbstractTestScannerTypeahead<SimpleElement> {
  
  @Override
  protected ElementSerializer<SimpleElement> createElementSerializer() {
    return new SimpleElementSerializer();
  }
  
  public void testApiBasics() throws Exception {
    int uid = 0;
    double score;
    List<Hit<SimpleElement>> hits;
    Collector<SimpleElement> collector;
    
    /**
     * Test ScannerTypeahead
     */
    
    // index elements
    for(int i = 0; i < 1000; i++) {
      SimpleElement elem = new SimpleElement(i);
      elem.setTimestamp(System.currentTimeMillis());
      elem.setTerms(i+"bloom", i+"filter", i+"typeahead");
      typeahead.index(elem);
    }
    
    collector = new SortedCollector<SimpleElement>(10);
    collector = typeahead.search(uid, new String[] {"1"}, collector);
    assertEquals(10, collector.capacity());
    assertEquals(10, collector.size());
    
    score = 1;
    hits = collector.hits();
    for(Hit<SimpleElement> s : hits) {
      assertTrue(score >= s.getScore());
      score = s.getScore();
    }
    
    collector = new SortedCollector<SimpleElement>(1000);
    collector = typeahead.search(uid, new String[] {"1"}, collector);
    assertEquals(1000, collector.capacity());
    assertEquals(111, collector.size());
    
    score = 1;
    hits = collector.hits();
    for(Hit<SimpleElement> s : hits) {
      assertTrue(score >= s.getScore());
      score = s.getScore();
    }
    
    /**
     * Test RangeTypeahead
     */
    
    // normal range
    doRangeTypeaheadOperations(0, 10, new String[] {"1"}, 1);
    doRangeTypeaheadOperations(0, 11, new String[] {"1"}, 2);
    doRangeTypeaheadOperations(0, 20, new String[] {"1"}, 11);

    doRangeTypeaheadOperations(20, 30, new String[] {"1"}, 0);
    doRangeTypeaheadOperations(20, 30, new String[] {"2"}, 10);
    
    // zero-range
    doRangeTypeaheadOperations(20, 20, new String[] {"1"}, 0);
    doRangeTypeaheadOperations(20, 20, new String[] {"2"}, 0);
    
    // full range
    doRangeTypeaheadOperations(0, 1000, new String[] {"1"}, 111);
    doRangeTypeaheadOperations(0, 1000, new String[] {"3"}, 111);
    doRangeTypeaheadOperations(0, 1000, new String[] {"31"}, 11);
    
    // illegal range
    try {
      doRangeTypeaheadOperations(1000, 10, new String[] {"1"}, 0);
      assertTrue(false);
    } catch(Exception e) {
      assertTrue(e.getClass() == RangeException.class);
    }
  }
  
  private void doRangeTypeaheadOperations(int rangeStart, int rangeEnd, String[] terms, int hitCount) {
    Collector<SimpleElement> collector = new SortedCollector<SimpleElement>(rangeEnd - rangeStart);
    RangeTypeahead<SimpleElement> ta = typeahead.openRangeTypeahead("RangeTypeahead", rangeStart, rangeEnd);
    
    assertEquals(rangeStart, ta.getRangeStart());
    assertEquals(rangeEnd, ta.getRangeEnd());
    
    assertEquals(rangeStart, ta.getRange().getStart());
    assertEquals(rangeEnd, ta.getRange().getEnd());
    
    collector = ta.search(0, terms, collector);
    assertEquals(hitCount, collector.size());
  }
}
