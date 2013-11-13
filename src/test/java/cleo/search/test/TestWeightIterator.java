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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import cleo.search.util.Weight;
import cleo.search.util.WeightIterator;
import cleo.search.util.WeightIteratorFromBytes;

import junit.framework.TestCase;

/**
 * TestWeightIterator
 * 
 * @author jwu
 * @since 04/30, 2011
 */
public class TestWeightIterator extends TestCase {
  private Random rand = new Random();
  
  public void testWeightIteratorFromBytes() {
    byte[] bytes;
    int offset, length;
    WeightIterator iter;
    
    bytes = new byte[0];
    offset = 0;
    length = 0;
    
    // Empty WeightIterator
    iter = new WeightIteratorFromBytes(bytes, offset, length);
    assertEquals(false, iter.hasNext());
    
    // Non-empty WeightIterator
    int cnt = rand.nextInt(100) + 1;
    offset = 0;
    length = cnt * (Weight.ELEMENT_ID_NUM_BYTES + Weight.ELEMENT_WEIGHT_NUM_BYTES);
    
    bytes = new byte[length];
    ByteBuffer bb = ByteBuffer.wrap(bytes);
    ArrayList<Weight> list1 = new ArrayList<Weight>(cnt);
    
    for(int i = 0; i < cnt; i++) {
      Weight weight = new Weight(i, rand.nextInt(10000));
      list1.add(weight);
      bb.putInt(weight.elementId);
      bb.putInt(weight.elementWeight);
    }
    
    ArrayList<Weight> list2 = new ArrayList<Weight>(cnt);
    iter = new WeightIteratorFromBytes(bytes, offset, length);
    while(iter.hasNext()) {
      Weight weight = new Weight(0, 0);
      iter.next(weight);
      list2.add(weight);
    }
    
    ArrayList<Weight> list3 = new ArrayList<Weight>(cnt);
    iter = new WeightIteratorFromBytes(bytes, offset, length);
    while(iter.hasNext()) {
      list3.add(iter.next());
    }
    
    assertEquals(cnt, list1.size());
    assertEquals(cnt, list2.size());
    assertEquals(cnt, list3.size());
    
    for(int i = 0; i < cnt; i++) {
      assertEquals(list1.get(i).elementId, list2.get(i).elementId);
      assertEquals(list1.get(i).elementWeight, list2.get(i).elementWeight);
    }
    
    for(int i = 0; i < cnt; i++) {
      assertEquals(list1.get(i).elementId, list3.get(i).elementId);
      assertEquals(list1.get(i).elementWeight, list3.get(i).elementWeight);
    }
    
  }
}
