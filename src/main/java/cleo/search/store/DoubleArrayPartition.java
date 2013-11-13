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

import krati.array.Array;

/**
 * DoubleArrayPartition
 * 
 * @author jwu
 * @since 03/25, 2011
 */
public interface DoubleArrayPartition extends Array {

  public int capacity();
  
  public int getIndexStart();
  
  public int getIndexEnd();
  
  public double get(int index);
  
  public void set(int index, double value);
  
  public double[] getInternalArray();
}
