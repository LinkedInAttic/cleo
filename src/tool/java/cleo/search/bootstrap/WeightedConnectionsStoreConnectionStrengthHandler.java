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

package cleo.search.bootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cleo.search.connection.ConnectionFilter;
import cleo.search.store.ArrayStoreWeights;
import cleo.search.util.ConnectionStrengthHandler;
import cleo.search.util.Weight;
import cleo.search.util.Weights;

/**
 * WeightedConnectionsStoreConnectionStrengthHandler
 * 
 * @author jwu
 * @since 06/13, 2011
 */
public class WeightedConnectionsStoreConnectionStrengthHandler implements ConnectionStrengthHandler {
  private final ArrayStoreWeights weightedConnectionsStore;
  private final ConnectionFilter connectionFilter;
  private final long timestamp;
  
  public WeightedConnectionsStoreConnectionStrengthHandler(ArrayStoreWeights weightedConnectionsStore, ConnectionFilter connectionFilter, long timestamp) {
    this.weightedConnectionsStore = weightedConnectionsStore;
    this.connectionFilter = connectionFilter;
    this.timestamp = timestamp;
  }
  
  public final ArrayStoreWeights getWeightedConnectionsStore() {
    return weightedConnectionsStore;
  }
  
  public final ConnectionFilter getConnectionFilter() {
    return connectionFilter;
  }
  
  @Override
  public void handle(int source, int[] connections, int[] strengths) throws Exception {
    if(source >= 0) {
      int[][] weightData = null;
      if(weightedConnectionsStore.hasIndex(source)) {
        weightData = weightedConnectionsStore.getWeightData(source);
      }
      
      int[][] newWeightData = new int[][]{connections, strengths};
      
      try {
        List<Weight> weightList = merge(source, weightData, newWeightData);
        weightedConnectionsStore.setWeightData(source, weightList, timestamp);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  static Comparator<Weight> weightCmpDsc = new Comparator<Weight>() {
    @Override
    public int compare(Weight w0, Weight w1) {
      return w1.elementWeight - w0.elementWeight;
    }
  };
  
  protected List<Weight> merge(int source, int[][] oldWeightData, int[][] newWeightData) {
    ArrayList<Weight> oldList = new ArrayList<Weight>();
    ArrayList<Weight> newList = new ArrayList<Weight>();
    
    if(oldWeightData != null) {
      int[] elemIds = oldWeightData[ArrayStoreWeights.ELEMID_SUBARRAY_INDEX];
      int[] weights = oldWeightData[ArrayStoreWeights.WEIGHT_SUBARRAY_INDEX];
      for(int i = 0, cnt = elemIds.length; i < cnt; i++) {
        if(connectionFilter.accept(source, elemIds[i], true)) {
          oldList.add(new Weight(elemIds[i], weights[i]));
        }
      }
    }
    
    if(newWeightData != null) {
      int[] elemIds = newWeightData[ArrayStoreWeights.ELEMID_SUBARRAY_INDEX];
      int[] weights = newWeightData[ArrayStoreWeights.WEIGHT_SUBARRAY_INDEX];
      for(int i = 0, cnt = elemIds.length; i < cnt; i++) {
        if(connectionFilter.accept(source, elemIds[i], true)) {
          newList.add(new Weight(elemIds[i], weights[i]));
        }
      }
    }
    
    List<Weight> list = Weights.merge(oldList, newList);
    Collections.sort(list, weightCmpDsc);
    return list;
  }
}
