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

package cleo.search.test;

import java.util.List;
import java.util.Random;

import cleo.search.Element;
import cleo.search.ElementHit;
import cleo.search.Hit;
import cleo.search.SimpleElement;
import cleo.search.collector.Collector;
import cleo.search.collector.NetworkSortedCollector;
import cleo.search.network.Proximity;

import junit.framework.TestCase;

/**
 * TestNetworkSortedCollector
 * 
 * @author jwu
 * @since 07/26, 2011
 */
public class TestNetworkSortedCollector extends TestCase {
  private final Random rand = new Random();
  
  public TestNetworkSortedCollector() {}
  
  private Proximity getNetworkDegree() {
    int num = rand.nextInt(4);
    
    if(num == 0) return Proximity.DEGREE_1;
    else if(num == 1) return Proximity.DEGREE_2;
    else if(num == 2) return Proximity.DEGREE_3;
    else if(num == 3) return Proximity.OUT_OF_NETWORK;
    else return Proximity.NONE;
  }
  
  public void testSortedCollector() {
    int capacity = 100;
    float maxScore = 0;
    Collector<Element> c = new NetworkSortedCollector<Element>(capacity);
    assertTrue(c.isEmpty());
    
    for(int i = 0; i < 100; i++) {
      Element element = new SimpleElement(i);
      float score = rand.nextFloat();
      element.setScore(score);
      maxScore = Math.max(maxScore, score);
      c.add(new ElementHit<Element>(element, score, "network", getNetworkDegree()));
    }
    
    assertEquals(capacity, c.capacity());
    assertEquals(capacity, c.size());
    assertFalse(c.isEmpty());
    
    List<Hit<Element>> results = c.hits();
    assertEquals(capacity, results.size());
    
    double score = maxScore;
    Proximity degree = Proximity.DEGREE_1;
    for(int i = 0, cnt = results.size(); i < cnt; i++) {
      Hit<Element> hit = results.get(i);
      
      assertTrue(degree.ordinal() <= hit.getProximity().ordinal());
      if(degree.ordinal() == hit.getProximity().ordinal()) {
        assertTrue(score >= hit.getScore());
      }
      score = hit.getScore();
    }
    
    c.clear();
    assertEquals(capacity, c.capacity());
    assertEquals(0, c.size());
    assertEquals(0, c.hits().size());
    assertEquals(0, c.elements().size());
    assertTrue(c.isEmpty());
  }
}
