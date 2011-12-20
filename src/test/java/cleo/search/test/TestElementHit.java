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
