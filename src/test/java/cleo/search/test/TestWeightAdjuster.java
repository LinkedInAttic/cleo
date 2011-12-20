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

import cleo.search.util.ConnectionStrengthAdjuster;
import cleo.search.util.WeightAdjuster;

import junit.framework.TestCase;

/**
 * TestWeightAdjuster
 * 
 * @author jwu
 * @since 05/04, 2011
 */
public class TestWeightAdjuster extends TestCase {
  private Random rand = new Random();
  
  public void testConnectionStrengthAdjuster() {
    ConnectionStrengthAdjuster wAdjuster = new ConnectionStrengthAdjuster();
    adjustConnectionStrength(wAdjuster);
    
    wAdjuster.setBase(rand.nextInt(100));
    adjustConnectionStrength(wAdjuster);
    
    wAdjuster.setBase(rand.nextInt(1000));
    adjustConnectionStrength(wAdjuster);
    
    wAdjuster.setBase(rand.nextInt(1000000));
    adjustConnectionStrength(wAdjuster);
  }
  
  private void adjustConnectionStrength(WeightAdjuster wAdjuster) {
    int max = 1000000;
    
    // integer
    int i1 = rand.nextInt(max) + 1;
    int i2 = rand.nextInt(max) + 1;
    int i = wAdjuster.adjust(i1, i2);
    
    assertTrue(i1 > i);
    assertTrue(i2 > i);
    
    // float
    float f1 = rand.nextFloat() + 0.001f;
    float f2 = rand.nextFloat() + 0.001f;
    float f = wAdjuster.adjust(f1, f2);
    
    assertTrue(f1 > f);
    assertTrue(f2 > f);
    
    // double
    double d1 = rand.nextDouble() + 0.001;
    double d2 = rand.nextDouble() + 0.001;
    double d = wAdjuster.adjust(d1, d2);
    
    assertTrue(d1 > d);
    assertTrue(d2 > d);
    
    // monotonicity
    int w1 = rand.nextInt(max) + 10000;
    int w2 = w1 / 2;
    int w3 = w1 / 3;
    
    assertTrue(wAdjuster.adjust(w1, w2) >= wAdjuster.adjust(w1, w3));
    assertTrue(wAdjuster.adjust(w2, w1) >= wAdjuster.adjust(w3, w1));
    assertTrue(wAdjuster.adjust(w1, w2) >= wAdjuster.adjust(w1, w3));
  }
}
