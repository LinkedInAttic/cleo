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

package cleo.search.copy;

import java.util.ArrayList;
import java.util.List;

import cleo.search.Element;
import cleo.search.connection.ConnectionFilter;
import cleo.search.store.ArrayStoreConnections;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ArrayStoreWeights;
import cleo.search.typeahead.VanillaNetworkTypeahead;
import cleo.search.typeahead.WeightedNetworkTypeahead;
import cleo.search.util.Weight;

/**
 * Copy
 * 
 * @author jwu
 * @since 05/27, 2011
 * 
 * <p>
 * 05/27, 2011 - Added connection filter support <br/>
 */
public final class Copy {

  /**
   * Copy connections from a source store to a target store.
   * 
   * @param source - Source array store for connections 
   * @param target - Target array store for connections
   * @throws Exception
   */
  public static void copy(ArrayStoreConnections source, ArrayStoreConnections target) throws Exception {
    copy(source, target, null);
  }
  
  /**
   * Copy connections from a source store to a target store.
   * 
   * @param source       - Source array store for connections 
   * @param target       - Target array store for connections
   * @param targetFilter - Target connection filter
   * @throws Exception
   */
  public static void copy(ArrayStoreConnections source, ArrayStoreConnections target, ConnectionFilter targetFilter) throws Exception {
    if(source == null) {
      throw new NullPointerException("source is null");
    }
    
    if(target == null) {
      throw new NullPointerException("target is null");
    }
    
    if(targetFilter == null) {
      for(int index = 0, length = target.length(); index < length; index++) {
        if(source.hasIndex(index)) {
          int[] connections = source.getConnections(index);
          target.setConnections(index, connections, target.getHWMark());
        }
      }
    } else {
      List<Integer> connList = new ArrayList<Integer>(1000);
      
      for(int index = 0, length = target.length(); index < length; index++) {
        if(source.hasIndex(index)) {
          int[] connections = source.getConnections(index);
          
          if(connections != null) {
            connList.clear();
            
            for(int i = 0; i < connections.length; i++) {
              if(targetFilter.accept(index, connections[i], true)) {
                connList.add(connections[i]);
              }
            }
            
            if(connections.length != connList.size()) {
              connections = new int[connList.size()];
              for(int i = 0; i < connections.length; i++) {
                connections[i] = connList.get(i);
              }
            }
          }
          
          target.setConnections(index, connections, target.getHWMark());
        }
      }
    }
    
    target.sync();
  }

  /**
   * Copy weights (e.g. connection strengths) from a source store to a target store.
   * 
   * @param source       - Source array store for Weights
   * @param target       - Target array store for Weights
   * @throws Exception
   */
  public static void copy(ArrayStoreWeights source, ArrayStoreWeights target) throws Exception {
    copy(source, target, null);
  }
  
  /**
   * Copy weights (e.g. connection strengths) from a source store to a target store.
   * 
   * @param source       - Source array store for Weights
   * @param target       - Target array store for Weights
   * @param targetFilter - Target connection filter
   * @throws Exception
   */
  public static void copy(ArrayStoreWeights source, ArrayStoreWeights target, ConnectionFilter targetFilter) throws Exception {
    if(source == null) {
      throw new NullPointerException("source is null");
    }
    
    if(target == null) {
      throw new NullPointerException("target is null");
    }
    
    if(targetFilter == null) {
      for(int index = 0, length = target.length(); index < length; index++) {
        if(source.hasIndex(index)) {
          int[][] weightData = source.getWeightData(index);
          target.setWeightData(index, weightData, target.getHWMark());
        }
      }
    } else {
      List<Weight> weightList = new ArrayList<Weight>();
      for(int index = 0, length = target.length(); index < length; index++) {
        if(source.hasIndex(index)) {
          int[][] weightData = source.getWeightData(index);
          
          if(weightData != null) {
            weightList.clear();
            
            int[] connections = weightData[ArrayStoreWeights.ELEMID_SUBARRAY_INDEX];
            int[] weights = weightData[ArrayStoreWeights.WEIGHT_SUBARRAY_INDEX];
            for(int i = 0; i < connections.length; i++) {
              if(targetFilter.accept(index, connections[i], true)) {
                weightList.add(new Weight(connections[i], weights[i]));
              }
            }
            
            if(connections.length != weightList.size()) {
              connections = new int[weightList.size()];
              weights = new int[weightList.size()];
              for(int i = 0; i < connections.length; i++) {
                Weight w = weightList.get(i);
                connections[i] = w.elementId;
                weights[i] = w.elementWeight;
              }
              
              weightData = new int[][] { connections, weights };
            }
          }
          
          target.setWeightData(index, weightData, target.getHWMark());
        }
      }
    }
    
    target.sync();
  }
  
  /**
   * Copy elements from a source store to a target store.
   * 
   * @param <E> Element
   * 
   * @param source - Source element store
   * @param target - Target element store
   * @throws Exception
   */
  public static <E extends Element> void copy(ArrayStoreElement<E> source, ArrayStoreElement<E> target) throws Exception {
    if(source == null) {
      throw new NullPointerException("source is null");
    }
    
    if(target == null) {
      throw new NullPointerException("target is null");
    }
    
    int indexStart = target.getIndexStart();
    
    for(int i = 0, cnt = target.capacity(); i < cnt; i++) {
      int index = indexStart + i;
      
      if(source.hasIndex(index)) {
        byte[] elementBytes = source.getElementBytes(index);
        target.setElementBytes(index, elementBytes, target.getHWMark());
      }
    }
    
    target.sync();
  }
  
  /**
   * Copy elements and connections from a source network typeahead to a target network typeahead in off-line mode.
   * 
   * @param <E> Element
   * @param source - Source network typeahead
   * @param target - Target network typeahead
   * @throws Exception
   */
  public static <E extends Element> void copy(VanillaNetworkTypeahead<E> source, VanillaNetworkTypeahead<E> target) throws Exception {
    ArrayStoreElement<E> sourceStore = source.getElementStore();
    ArrayStoreElement<E> targetStore = target.getElementStore();
    copy(sourceStore, targetStore);
    
    ConnectionFilter connFilter = null;
    if(!source.getConnectionFilter().equals(target.getConnectionFilter())) {
      connFilter = target.getConnectionFilter();
    }
    
    ArrayStoreConnections sourceConnectionsStore = source.getConnectionsStore();
    ArrayStoreConnections targetConnectionsStore = target.getConnectionsStore();
    if(targetConnectionsStore.length() < sourceConnectionsStore.length()) {
      targetConnectionsStore.setConnections(sourceConnectionsStore.length() - 1, (int[])null, targetConnectionsStore.getHWMark());
    }
    copy(sourceConnectionsStore, targetConnectionsStore, connFilter);
  }
  
  /**
   * Copy elements and connections from a source network typeahead to a target network typeahead in off-line mode.
   * 
   * @param <E> Element
   * @param source - Source weighted network typeahead
   * @param target - Target weighted network typeahead
   * @throws Exception
   */
  public static <E extends Element> void copy(WeightedNetworkTypeahead<E> source, WeightedNetworkTypeahead<E> target) throws Exception {
    ArrayStoreElement<E> sourceStore = source.getElementStore();
    ArrayStoreElement<E> targetStore = target.getElementStore();
    copy(sourceStore, targetStore);
    
    ConnectionFilter connFilter = null;
    if(!source.getConnectionFilter().equals(target.getConnectionFilter())) {
      connFilter = target.getConnectionFilter();
    }
    
    ArrayStoreWeights sourceConnectionsStore = source.getConnectionsStore();
    ArrayStoreWeights targetConnectionsStore = target.getConnectionsStore();
    if(targetConnectionsStore.length() < sourceConnectionsStore.length()) {
      targetConnectionsStore.setWeightData(sourceConnectionsStore.length() - 1, (int[][])null, targetConnectionsStore.getHWMark());
    }
    copy(sourceConnectionsStore, targetConnectionsStore, connFilter);
  }
}
