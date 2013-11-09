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

import java.io.Serializable;

/**
 * Weight
 * 
 * @author jwu
 * @since 04/26, 2011
 */
public final class Weight implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final int ELEMENT_ID_NUM_BYTES = 4;     // Number of integer bytes
  public static final int ELEMENT_WEIGHT_NUM_BYTES = 4; // Number of integer bytes
  
  public int elementId;
  public int elementWeight;
  
  public Weight(int elementId, int elementWeight) {
    this.elementId = elementId;
    this.elementWeight = elementWeight;
  }
  
  @Override
  public boolean equals(Object o) {
    if(o == this) return true;
    if(o == null) return false;
    return o.getClass() == Weight.class ? (elementId == ((Weight)o).elementId) : false;
  }
  
  @Override
  public int hashCode() {
    return elementId;
  }
  
  @Override
  public String toString() {
    return elementId + ":" + elementWeight;
  }
}
