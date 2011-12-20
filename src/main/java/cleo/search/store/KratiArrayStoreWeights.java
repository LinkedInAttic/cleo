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

import java.nio.ByteBuffer;
import java.util.List;

import cleo.search.util.Weight;

/**
 * KratiArrayStoreWeights
 * 
 * @author jwu
 * @since 04/19, 2011
 */
public class KratiArrayStoreWeights extends KratiArrayStoreFilters implements ArrayStoreWeights {
  
  public KratiArrayStoreWeights(KratiArrayStore store) {
    super(store);
  }
  
  @Override
  public int getWeight(int index, int elemId) {
    byte[] dat = store.get(index);
    if(dat == null) return 0;
    
    ByteBuffer bb = ByteBuffer.wrap(dat);
    int cnt = dat.length / NUM_BYTES_IN_INT / 2;
    for (int i = 0; i < cnt; i++) {
      if(bb.getInt() == elemId) {
        return bb.getInt();
      }
      
      // Bypass filter if elemIds do not match
      bb.position(bb.position() + NUM_BYTES_IN_INT);
    }
    
    return 0;
  }
  
  @Override
  public void setWeight(int index, int elemId, int elemWeight, long scn) throws Exception {
    add(index, elemId, elemWeight, scn);
  }
  
  @Override
  public int[][] getWeightData(int index) {
    return get(index);
  }
  
  @Override
  public void setWeightData(int index, int[][] weightData, long scn) throws Exception {
    set(index, weightData, scn);
  }

  @Override
  public void setWeightData(int index, List<Weight> weightData, long scn) throws Exception {
    if(weightData == null) {
      set(index, null, scn);
      return;
    }
    
    int cnt = weightData.size();
    int[] elemIds = new int[cnt];
    int[] weights = new int[cnt];
    for(int i = 0; i < cnt; i++) {
      Weight w = weightData.get(i);
      elemIds[i] = w.elementId;
      weights[i] = w.elementWeight;
    }
    
    set(index, new int[][]{elemIds, weights}, scn);
  }
}
