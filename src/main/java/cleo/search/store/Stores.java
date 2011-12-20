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

package cleo.search.store;

/**
 * Stores - Store static utilities. 
 * 
 * @author jwu
 * @since 05/16, 2011
 * 
 * <p>
 * 05/16, 2011 - Added max/min utility methods for Int/Float/DoubleArrayPartition <br/>
 */
public class Stores {
  
  /**
   * Finds the maximum integer value from an integer array partition.
   * 
   * @param p - An integer array partition.
   * @return the maximum integer value.
   */
  public static int max(IntArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    int max = Integer.MIN_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      int val = p.get(i);
      if(val > max) {
        max = val; 
      }
    }
    
    return max;
  }
  
  /**
   * Finds the minimum integer value from an integer array partition.
   * 
   * @param p - An integer array partition.
   * @return the minimum integer value.
   */
  public static int min(IntArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    int min = Integer.MAX_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      int val = p.get(i);
      if(val < min) {
        min = val; 
      }
    }
    
    return min;
  }
  
  /**
   * Finds the maximum float value from an float array partition.
   * 
   * @param p - A float array partition.
   * @return the maximum float value.
   */
  public static float max(FloatArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    float max = Float.MIN_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      float val = p.get(i);
      if(val > max) {
        max = val; 
      }
    }
    
    return max;
  }
  
  /**
   * Finds the minimum float value from an float array partition.
   * 
   * @param p - A float array partition.
   * @return the minimum float value.
   */
  public static float min(FloatArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    float min = Float.MAX_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      float val = p.get(i);
      if(val < min) {
        min = val; 
      }
    }
    
    return min;
  }
  
  /**
   * Finds the maximum double value from an double array partition.
   * 
   * @param p - A double array partition.
   * @return the maximum double value.
   */
  public static double max(DoubleArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    double max = Double.MIN_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      double val = p.get(i);
      if(val > max) {
        max = val; 
      }
    }
    
    return max;
  }
  
  /**
   * Finds the minimum double value from an double array partition.
   * 
   * @param p - A double array partition.
   * @return the minimum double value.
   */
  public static double min(DoubleArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    double min = Double.MAX_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      double val = p.get(i);
      if(val < min) {
        min = val; 
      }
    }
    
    return min;
  }
}
