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

package cleo.search.network;

import java.io.Serializable;

/**
 * NetworkInfo
 * 
 * @author jwu
 * @since 04/28, 2011
 */
public class NetworkInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  private int source;
  private int[] targets;
  private int[] strengths;
  private Proximity proximity;
  
  public int getSource() {
    return source;
  }
  
  public int[] getTargets() {
    return targets;
  }
  
  public int[] getStrengths() {
    return strengths;
  }
  
  public Proximity getProximity() {
    return proximity;
  }
  
  public void setSource(int source) {
    this.source = source;
  }
  
  public void setTargets(int[] targets) {
    this.targets = targets;
  }
  
  public void setStrengths(int[] strengths) {
    this.strengths = strengths;
  }
  
  public void setProximity(Proximity proximity) {
    this.proximity = proximity;
  }
}
