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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import cleo.search.Element;
import cleo.search.Hit;
import cleo.search.ElementHit;
import cleo.search.SimpleElement;
import cleo.search.selector.PrefixSelector;
import cleo.search.selector.ScoredElementSelector;
import cleo.search.selector.ScoredPrefixSelector;
import cleo.search.selector.Selector;
import cleo.search.selector.SelectorContext;
import cleo.search.selector.StrictPrefixSelector;
import cleo.search.util.ElementHitScoreComparator;
import cleo.search.util.ElementScoreComparator;
import junit.framework.TestCase;

/**
 * TestSelector
 * 
 * @author jwu
 * @since 01/15, 2011
 */
public class TestSelector extends TestCase {
  
  public TestSelector(String name) {
    super(name);
  }
  
  public void testPrefixSelector() {
    Element elem = new SimpleElement(1);
    elem.setTerms(new String[] {"Bloom", "filter", "technique"});
    elem.setTimestamp(System.currentTimeMillis());
    
    SelectorContext ctx = new SelectorContext();
    
    Selector<Element> selector1 = new PrefixSelector<Element>();
    assertTrue(selector1.select(elem, ctx));
    
    Selector<Element> selector2 = new PrefixSelector<Element>("Bloom");
    assertTrue(selector2.select(elem, ctx));
    
    Selector<Element> selector3 = new PrefixSelector<Element>("tech", "Bloom");
    assertTrue(selector3.select(elem, ctx));
    
    Selector<Element> selector4 = new PrefixSelector<Element>("technology");
    assertFalse(selector4.select(elem, ctx));
    
    Selector<Element> selector5 = new PrefixSelector<Element>("Bloom", "filter", "technology");
    assertFalse(selector5.select(elem, ctx));
    
    Selector<Element> selector6 = new PrefixSelector<Element>("Bloom", "filter", "technique", "technology");
    assertFalse(selector6.select(elem, ctx));
  }
  
  public void testScoredPrefixSelector() {
    Element elem1 = new SimpleElement(1);
    elem1.setTerms(new String[] {"ibm", "services"});
    elem1.setTimestamp(System.currentTimeMillis());
    
    Element elem2 = new SimpleElement(1);
    elem2.setTerms(new String[] {"ibm", "global", "services"});
    elem2.setTimestamp(System.currentTimeMillis());
    
    Element elem3 = new SimpleElement(1);
    elem3.setTerms(new String[] {"ibm", "global", "business", "services"});
    elem3.setTimestamp(System.currentTimeMillis());
    
    SelectorContext ctx = new SelectorContext();
    Selector<Element> selector;
    
    // First selector
    selector = new ScoredPrefixSelector<Element>("ibm");
    double score11, score12, score13; 
    
    ctx.clear();
    selector.select(elem1, ctx);
    score11 = ctx.getScore();
    
    ctx.clear();
    selector.select(elem2, ctx);
    score12 = ctx.getScore();
    
    ctx.clear();
    selector.select(elem3, ctx);
    score13 = ctx.getScore();
    
    assertTrue(score11 > score12);
    assertTrue(score12 > score13);
    
    // Second selector
    selector = new ScoredPrefixSelector<Element>("service");
    double score21, score22, score23; 
    
    ctx.clear();
    selector.select(elem1, ctx);
    score21 = ctx.getScore();
    
    ctx.clear();
    selector.select(elem2, ctx);
    score22 = ctx.getScore();
    
    ctx.clear();
    selector.select(elem3, ctx);
    score23 = ctx.getScore();
    
    assertTrue(score21 > score22);
    assertTrue(score22 > score23);
    
    // Third selector
    selector = new ScoredPrefixSelector<Element>("ibm", "service");
    double score31, score32, score33; 
    
    ctx.clear();
    selector.select(elem1, ctx);
    score31 = ctx.getScore();
    
    ctx.clear();
    selector.select(elem2, ctx);
    score32 = ctx.getScore();
    
    ctx.clear();
    selector.select(elem3, ctx);
    score33 = ctx.getScore();
    
    assertTrue(score31 > score32);
    assertTrue(score32 > score33);
    
    // check query "ibm" v.s. "service"
    assertTrue(score11 > score21);
    assertTrue(score12 > score22);
    assertTrue(score13 > score23);
    
    // check query "ibm service" v.s. "ibm"
    assertTrue(score31 > score11);
    assertTrue(score32 > score12);
    assertTrue(score33 > score13);
  }
  
  public void testScoredPrefixSelectorFullMatch() {
    Element elem1 = new SimpleElement(1);
    elem1.setTerms(new String[] {"ibm", "services"});
    elem1.setTimestamp(System.currentTimeMillis());
    
    Element elem2 = new SimpleElement(1);
    elem2.setTerms(new String[] {"ibm", "global", "services"});
    elem2.setTimestamp(System.currentTimeMillis());
    
    Element elem3 = new SimpleElement(1);
    elem3.setTerms(new String[] {"ibm", "global", "business", "services"});
    elem3.setTimestamp(System.currentTimeMillis());
    
    SelectorContext ctx = new SelectorContext();
    Selector<Element> selector;
    double score; 
    
    selector = new ScoredPrefixSelector<Element>("ibm", "services");
    ctx.clear();
    selector.select(elem1, ctx);
    score = ctx.getScore();
    assertEquals(1.0, score);
    
    selector = new ScoredPrefixSelector<Element>("ibm", "global", "services");
    ctx.clear();
    selector.select(elem2, ctx);
    score = ctx.getScore();
    assertEquals(1.0, score);
    
    selector = new ScoredPrefixSelector<Element>("ibm", "global", "business", "services");
    ctx.clear();
    selector.select(elem3, ctx);
    score = ctx.getScore();
    assertEquals(1.0, score);
  }
  
  public void testScoredPrefixSelectorDistance() {
    Element elem = new SimpleElement(1);
    elem.setTerms(new String[] {"ibm", "global", "business", "services"});
    elem.setTimestamp(System.currentTimeMillis());
    
    SelectorContext ctx = new SelectorContext();
    Selector<Element> selector1, selector2, selector3;
    double score1, score2, score3; 
    
    selector1 = new ScoredPrefixSelector<Element>("ibm", "global");
    selector2 = new ScoredPrefixSelector<Element>("ibm", "services");
    selector3 = new ScoredPrefixSelector<Element>("global", "services");
    
    ctx.clear();
    selector1.select(elem, ctx);
    score1 = ctx.getScore();
    
    ctx.clear();
    selector2.select(elem, ctx);
    score2 = ctx.getScore();
    
    ctx.clear();
    selector3.select(elem, ctx);
    score3 = ctx.getScore();
    
    assertTrue(score1 > score2);
    assertTrue(score1 > score3);
    assertTrue(score2 == score3);
  }
  
  public void testScoredPrefixSelectorOrdering() {
    Element elem = new SimpleElement(1);
    elem.setTerms(new String[] {"ibm", "global", "business", "services"});
    elem.setTimestamp(System.currentTimeMillis());
    
    SelectorContext ctx = new SelectorContext();
    Selector<Element> selector1, selector2;
    double score1, score2; 
    
    // case 1
    selector1 = new ScoredPrefixSelector<Element>("ibm", "global");
    selector2 = new ScoredPrefixSelector<Element>("global", "ibm");
    
    ctx.clear();
    selector1.select(elem, ctx);
    score1 = ctx.getScore();
    
    ctx.clear();
    selector2.select(elem, ctx);
    score2 = ctx.getScore();
    
    assertTrue(score1 > score2);
    
    // case 2
    selector1 = new ScoredPrefixSelector<Element>("global", "business");
    selector2 = new ScoredPrefixSelector<Element>("business", "global");
    
    ctx.clear();
    selector1.select(elem, ctx);
    score1 = ctx.getScore();
    
    ctx.clear();
    selector2.select(elem, ctx);
    score2 = ctx.getScore();
    
    assertTrue(score1 > score2);
    
    // case 3
    selector1 = new ScoredPrefixSelector<Element>("global", "services");
    selector2 = new ScoredPrefixSelector<Element>("services", "global");
    
    ctx.clear();
    selector1.select(elem, ctx);
    score1 = ctx.getScore();
    
    ctx.clear();
    selector2.select(elem, ctx);
    score2 = ctx.getScore();
    
    assertTrue(score1 > score2);
    
    // case 4
    selector1 = new ScoredPrefixSelector<Element>("ibm", "services");
    selector2 = new ScoredPrefixSelector<Element>("services", "ibm");
    
    ctx.clear();
    selector1.select(elem, ctx);
    score1 = ctx.getScore();
    
    ctx.clear();
    selector2.select(elem, ctx);
    score2 = ctx.getScore();
    
    assertTrue(score1 > score2);
    
    // case 5
    selector1 = new ScoredPrefixSelector<Element>("ibm", "global", "business", "services");
    selector2 = new ScoredPrefixSelector<Element>("ibm", "business", "global", "services");
    
    ctx.clear();
    selector1.select(elem, ctx);
    score1 = ctx.getScore();
    
    ctx.clear();
    selector2.select(elem, ctx);
    score2 = ctx.getScore();
    
    assertTrue(score1 > score2);
  }
  
  public void testScoredPrefixSelectorSpecifics() {
    Element elem1 = new SimpleElement(1);
    elem1.setTerms(new String[] {"zynga"});
    elem1.setTimestamp(System.currentTimeMillis());
    
    Element elem2 = new SimpleElement(1);
    elem2.setTerms(new String[] {"zyrra"});
    elem2.setTimestamp(System.currentTimeMillis());
    
    Element elem3 = new SimpleElement(1);
    elem3.setTerms(new String[] {"zyken"});
    elem3.setTimestamp(System.currentTimeMillis());
    
    SelectorContext ctx = new SelectorContext();
    Selector<Element> selector = new ScoredPrefixSelector<Element>("zy");
    
    double score1, score2, score3;
    
    ctx.clear();
    selector.select(elem1, ctx);
    score1 = ctx.getScore();
    
    ctx.clear();
    selector.select(elem2, ctx);
    score2 = ctx.getScore();
    
    ctx.clear();
    selector.select(elem3, ctx);
    score3 = ctx.getScore();
    
    assertEquals(score1, score2);
    assertEquals(score2, score3);
    
    Hit<Element> s1 = new ElementHit<Element>(elem1, 0);
    Hit<Element> s2 = new ElementHit<Element>(elem2, 0);
    Hit<Element> s3 = new ElementHit<Element>(elem1, 0);
    
    s1.setScore(score1 * 956);
    s2.setScore(score2 * 4);
    s3.setScore(score3 * 2);
    
    assertTrue(s1.compareTo(s2) == 1);
    assertTrue(s2.compareTo(s3) == 1);
  }
  
  public void testScoredElementSelector() {
    double score;
    Random rand = new Random();
    String[] terms = new String[] {"ibm", "global", "consulting", "services"};
    
    SelectorContext ctx = new SelectorContext();
    Selector<Element> selector = new ScoredElementSelector<Element>("ibm", "serv");
    
    int cnt = rand.nextInt(100) + 10;
    ArrayList<Element> elemList = new ArrayList<Element>(cnt);
    ArrayList<Hit<Element>> hitList = new ArrayList<Hit<Element>>(cnt);
    
    for(int i = 0; i < cnt; i++) {
      Element elem = new SimpleElement(i);
      elem.setTerms(terms);
      elem.setScore(rand.nextFloat());
      elem.setTimestamp(System.currentTimeMillis());
      elemList.add(elem);
      
      ctx.clear();
      selector.select(elem, ctx);
      hitList.add(new ElementHit<Element>(elem, ctx.getScore()));
    }
    
    Collections.sort(elemList, new ElementScoreComparator());
    
    score = 0;
    for(int i = 0; i < cnt; i++) {
      assertTrue(score <= elemList.get(i).getScore());
      score = elemList.get(i).getScore();
    }
    
    Collections.sort(hitList, new ElementHitScoreComparator());
    
    score = 0;
    for(int i = 0; i < cnt; i++) {
      assertTrue(score <= hitList.get(i).getScore());
      score = hitList.get(i).getScore();
    }
    
    for(int i = 0; i < cnt; i++) {
      assertEquals(elemList.get(i), hitList.get(i).getElement());
    }
  }
  
  public void testStringPrefixSelector() {
    Element elem = new SimpleElement(1);
    elem.setTerms(new String[] {"open", "source", "software", "systems"});
    elem.setTimestamp(System.currentTimeMillis());
    
    SelectorContext ctx = new SelectorContext();
    
    Selector<Element> selector = new StrictPrefixSelector<Element>("soft", "system");
    assertTrue(selector.select(elem, ctx));
    
    selector = new StrictPrefixSelector<Element>("open", "system");
    assertTrue(selector.select(elem, ctx));
    
    selector = new StrictPrefixSelector<Element>("system", "open");
    assertFalse(selector.select(elem, ctx));
    
    selector = new StrictPrefixSelector<Element>("sour", "system");
    assertTrue(selector.select(elem, ctx));
    
    selector = new StrictPrefixSelector<Element>("system", "sour");
    assertFalse(selector.select(elem, ctx));
  }
}
