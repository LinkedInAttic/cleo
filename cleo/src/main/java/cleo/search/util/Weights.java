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

package cleo.search.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Weights
 * 
 * @author jwu
 * @since 04/26, 2011
 */
public class Weights {

  /**
   * Merges two lists of <code>Weight</code> into a new list. The resulting list
   * contains weights from <code>activeList</code> with elementIds not shared with
   * <code>changeList</code> and weights from <code>changeList</code> with elementIds
   * shared with <code>activeList</code>.
   * 
   * @param activeList - Active weight list
   * @param changeList - Change weight list
   * @return a new list of weights (without changing the original input lists).
   */
  public static List<Weight> merge(List<Weight> activeList, List<Weight> changeList) {
    Set<Weight> resultSet;
    
    if(activeList != null) {
      resultSet = new HashSet<Weight>(activeList.size());
      
      if(changeList != null) {
        HashMap<Integer, Weight> map = new HashMap<Integer, Weight>();
        for(Weight w : changeList) {
          map.put(w.elementId, w);
        }
        
        for(Weight w1 : activeList) {
          Weight w2 = map.get(w1.elementId);
          resultSet.add(w2 == null ? w1 : w2);
        }
      } else {
        resultSet.addAll(activeList);
      }
    } else {
      resultSet = new HashSet<Weight>();
    }
    
    List<Weight> resultList = new ArrayList<Weight>(resultSet.size());
    resultList.addAll(resultSet);
    return resultList;
  }
  
  /**
   * Merges two collections of <code>Weight</code> into a new collection. The resulting
   * collection contains weights from <code>activeCol</code> with elementIds not shared
   * with <code>changeCol</code> and weights from <code>changeCol</code> with elementIds
   * shared with <code>activeCol</code>.
   * 
   * @param activeCol - Active weight collection
   * @param changeCol - Change weight collection
   * @return a new collection of weights (without changing the original input collections).
   */
  public static Collection<Weight> merge(Collection<Weight> activeCol, Collection<Weight> changeCol) {
    Set<Weight> resultSet;
    
    if(activeCol != null) {
      resultSet = new HashSet<Weight>(activeCol.size());
      
      if(changeCol != null) {
        HashMap<Integer, Weight> map = new HashMap<Integer, Weight>();
        for(Weight w : changeCol) {
          map.put(w.elementId, w);
        }
        
        for(Weight w1 : activeCol) {
          Weight w2 = map.get(w1.elementId);
          resultSet.add(w2 == null ? w1 : w2);
        }
      } else {
        resultSet.addAll(activeCol);
      }
    } else {
      resultSet = new HashSet<Weight>();
    }
    
    return resultSet;
  }
}
