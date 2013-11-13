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

package cleo.search.typeahead;

import cleo.search.store.ArrayStoreWeights;

/**
 * NetworkTypeaheadContextPlain
 * 
 * @author jwu
 * @since  04/28, 2011
 */
public class NetworkTypeaheadContextPlain implements NetworkTypeaheadContext {
  private static final long serialVersionUID = 1L;
  
  protected int source;
  protected int[] connections = null;
  protected int[] strengths = null;
  protected long timeoutMillis = 15;
  
  public NetworkTypeaheadContextPlain(int source) {
    this.source = source;
  }
  
  @Override
  public final int getSource() {
    return source;
  }
  
  @Override
  public final void setSource(int source) {
    this.source = source;
  }
  
  @Override
  public final int[] getConnections() {
    return connections;
  }
  
  @Override
  public final void setConnections(int[] connections) {
    this.connections = connections;
  }
  
  @Override
  public final int[] getStrengths() {
    return strengths;
  }
  
  @Override
  public final void setStrengths(int[] strengths) {
    this.strengths = strengths;
  }
  
  @Override
  public final int[][] getConnectionStrengths() {
    return new int[][] { connections, strengths };
  }
  
  public final void setConnectionStrengths(int[][] connectionStrengths) {
    if(connectionStrengths == null) {
      connections = null;
      strengths = null;
      return;
    }
    
    if(connectionStrengths.length == 2) {
      connections = connectionStrengths[ArrayStoreWeights.ELEMID_SUBARRAY_INDEX];
      strengths = connectionStrengths[ArrayStoreWeights.WEIGHT_SUBARRAY_INDEX];
      if(connections == null) {
        strengths = null;
      }
    }
    
    if(!validate()) {
      throw new RuntimeException("Invalid data: connectionStrengths");
    }
  }
  
  @Override
  public final long getTimeoutMillis() {
    return timeoutMillis;
  }
  
  @Override
  public final void setTimeoutMillis(long timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
  }
  
  protected boolean validate() {
    /**
     * Correct Combinations:
     * 
     * connections  strengths  status
     *    int[]       int[]      OK   on condition (connections.length == strengths.length)
     *    int[]       null       OK
     *    null        null       OK
     */
    if(connections != null && strengths != null && connections.length != strengths.length) {
      return false;
    }
    
    if(connections == null) {
      strengths = null;
    }
    
    return true;
  }
}
