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

import cleo.search.store.FloatArrayPartition;

/**
 * ElementScoreHandler
 * 
 * @author jwu
 * @since 03/25, 2011
 */
public class ElementScoreHandler implements ScoreHandler {
  private final FloatArrayPartition partition;
  
  public ElementScoreHandler(FloatArrayPartition partition) {
    this.partition = partition;
  }
  
  public FloatArrayPartition getScorePartition() {
    return partition;
  }
  
  @Override
  public double handle(int elementId, double elementScore) {
    if(partition.hasIndex(elementId)) {
      partition.set(elementId, (float)elementScore);
    }
    
    return elementScore;
  }
}
