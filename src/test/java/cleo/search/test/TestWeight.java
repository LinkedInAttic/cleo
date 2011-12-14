package cleo.search.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cleo.search.util.Weight;
import cleo.search.util.Weights;

import junit.framework.TestCase;

/**
 * TestWeight
 * 
 * @author jwu
 * @since 04/26, 2011
 */
public class TestWeight extends TestCase {
  private Random rand = new Random();
  
  public void testApiBasics() {
    int elemId = rand.nextInt();
    
    Weight w1 = new Weight(elemId, rand.nextInt());
    Weight w2 = new Weight(elemId, rand.nextInt());
    Weight w3 = new Weight(elemId, rand.nextInt());
    
    assertTrue(w1.equals(w2));
    assertTrue(w2.equals(w3));
    assertEquals(w1.hashCode(), w2.hashCode());
    assertEquals(w2.hashCode(), w3.hashCode());
    
    HashSet<Weight> set1 = new HashSet<Weight>();
    set1.add(w1);
    set1.add(w2);
    set1.add(w3);
    assertEquals(1, set1.size());
    
    HashSet<Weight> set2 = new HashSet<Weight>();
    set2.add(w1);
    set2.add(w2);
    set2.add(w3);
    
    Weight w4 = new Weight(++elemId, rand.nextInt());
    Weight w5 = new Weight(++elemId, rand.nextInt());
    set2.add(w4);
    set2.add(w5);
    assertEquals(3, set2.size());
    
    List<Weight> list1 = new ArrayList<Weight>();
    list1.addAll(set1);

    List<Weight> list2 = new ArrayList<Weight>();
    list2.addAll(set2);
    
    List<Weight> resultList = Weights.merge(list1, list2);
    assertEquals(1, resultList.size());
    assertEquals(list1.get(0), resultList.get(0));
    
    int totalWeight = 0;
    for(Weight w : resultList) {
      totalWeight += w.elementWeight;
    }
    
    int totalWeight1 = 0;
    for(Weight w : list1) {
      totalWeight1 += w.elementWeight;
    }
    
    assertEquals(totalWeight1, totalWeight);
    
    resultList = Weights.merge(list2, list1);
    assertEquals(3, resultList.size());
    
    totalWeight = 0;
    for(Weight w : resultList) {
      totalWeight += w.elementWeight;
    }
    
    int totalWeight2 = 0;
    for(Weight w : list2) {
      totalWeight2 += w.elementWeight;
    }
    
    assertEquals(totalWeight2, totalWeight);
  }
  
  public void testMerge() {
    Set<Weight> activeSet = new HashSet<Weight>();
    Set<Weight> changeSet = new HashSet<Weight>();
    Set<Weight> resultSet = new HashSet<Weight>();
    List<Weight> activeList = new ArrayList<Weight>();
    List<Weight> changeList = new ArrayList<Weight>();
    List<Weight> resultList;
    
    int elemId = rand.nextInt();
    
    Weight w1 = new Weight(elemId, rand.nextInt());
    Weight w2 = new Weight(elemId, rand.nextInt());
    Weight w3 = new Weight(elemId, rand.nextInt());
    
    Weight w4 = new Weight(++elemId, rand.nextInt());
    Weight w5 = new Weight(++elemId, rand.nextInt());
    Weight w6 = new Weight(++elemId, rand.nextInt());
    
    // Test Set
    activeSet.add(w1);
    activeSet.add(w2);
    activeSet.add(w3);
    activeSet.add(w4);
    assertEquals(2, activeSet.size());
    
    changeSet.add(w1);
    changeSet.add(w2);
    changeSet.add(w3);
    changeSet.add(w5);
    changeSet.add(w6);
    assertEquals(3, changeSet.size());
    
    resultSet.add(w1);
    resultSet.add(w4);
    assertEquals(2, resultSet.size());
    
    // Test List
    activeList.add(w1);
    activeList.add(w2);
    activeList.add(w3);
    activeList.add(w4);
    assertEquals(4, activeList.size());
    
    changeList.add(w1);
    changeList.add(w2);
    changeList.add(w3);
    changeList.add(w5);
    changeList.add(w6);
    assertEquals(5, changeList.size());
    
    // Test merge
    resultList = Weights.merge(null, null);
    assertEquals(0, resultList.size());
    
    resultList = Weights.merge(null, changeList);
    assertEquals(0, resultList.size());
    
    resultList = Weights.merge(activeList, null);
    assertEquals(activeSet.size(), resultList.size());
    
    resultList = Weights.merge(activeList, changeList);
    assertEquals(activeSet.size(), resultList.size());
    assertEquals(resultSet.size(), resultList.size());
  }
}
