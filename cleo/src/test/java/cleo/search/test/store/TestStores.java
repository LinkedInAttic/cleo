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

package cleo.search.test.store;

import java.util.Random;

import cleo.search.store.DoubleArrayPartition;
import cleo.search.store.FloatArrayPartition;
import cleo.search.store.IntArrayPartition;
import cleo.search.store.StaticDoubleArrayPartition;
import cleo.search.store.StaticFloatArrayPartition;
import cleo.search.store.StaticIntArrayPartition;
import cleo.search.store.Stores;

import junit.framework.TestCase;

/**
 * TestStores
 * 
 * @author jwu
 * @since 05/16, 2011
 * 
 * <p>
 * 05/16, 2011 - Added tests for Stores.max/min <br/>
 */
public class TestStores extends TestCase {
  private final Random rand = new Random(); 
  
  public void testMinMaxOnIntArrayPartition() {
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    
    // Normal case
    IntArrayPartition p = new StaticIntArrayPartition(rand.nextInt(1000), 1000);
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      int val = rand.nextInt();
      if(val > max) max = val;
      if(val < min) min = val;
      p.set(i, val);
    }
    
    assertEquals(max, Stores.max(p));
    assertEquals(min, Stores.min(p));
    
    // Corner cases
    p = null;
    assertEquals(0, Stores.max(p));
    assertEquals(0, Stores.min(p));
    
    p = new StaticIntArrayPartition(rand.nextInt(1000), 0);
    assertEquals(0, Stores.max(p));
    assertEquals(0, Stores.min(p));
  }
  

  public void testMinMaxOnFloatArrayPartition() {
    float min = Float.MAX_VALUE;
    float max = Float.MIN_VALUE;
    
    // Normal case
    FloatArrayPartition p = new StaticFloatArrayPartition(rand.nextInt(1000), 1000);
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      float val = rand.nextFloat();
      if(val > max) max = val;
      if(val < min) min = val;
      p.set(i, val);
    }
    
    assertEquals(max, Stores.max(p));
    assertEquals(min, Stores.min(p));
    
    // Corner cases
    p = null;
    assertEquals(0f, Stores.max(p));
    assertEquals(0f, Stores.min(p));
    
    p = new StaticFloatArrayPartition(rand.nextInt(1000), 0);
    assertEquals(0f, Stores.max(p));
    assertEquals(0f, Stores.min(p));
  }
  
  public void testMinMaxOnDoubleArrayPartition() {
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    
    // Normal case
    DoubleArrayPartition p = new StaticDoubleArrayPartition(rand.nextInt(1000), 1000);
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      double val = rand.nextDouble();
      if(val > max) max = val;
      if(val < min) min = val;
      p.set(i, val);
    }
    
    assertEquals(max, Stores.max(p));
    assertEquals(min, Stores.min(p));
    
    // Corner cases
    p = null;
    assertEquals(0d, Stores.max(p));
    assertEquals(0d, Stores.min(p));
    
    p = new StaticDoubleArrayPartition(rand.nextInt(1000), 0);
    assertEquals(0d, Stores.max(p));
    assertEquals(0d, Stores.min(p));
  }
}
