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

package cleo.search.connection;

import java.io.Serializable;

import cleo.search.util.Range;

/**
 * TransitivePartitionConnectionFilter
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public class TransitivePartitionConnectionFilter implements PartitionConnectionFilter, Serializable {
  private static final long serialVersionUID = 1L;
  private final Range transitiveRange;
  
  public TransitivePartitionConnectionFilter(Range transitiveRange) {
    if(transitiveRange == null) {
      throw new NullPointerException("transitiveRange is null");
    }
    this.transitiveRange = transitiveRange;
  }
  
  @Override
  public Range getPartitionRange() {
    return transitiveRange;
  }
  
  @Override
  public boolean accept(Connection conn) {
    return conn == null ? false : (transitiveRange.has(conn.source()) || transitiveRange.has(conn.target()));
  }
  
  @Override
  public boolean accept(int source, int target, boolean active) {
    return transitiveRange.has(source) || transitiveRange.has(target);
  }
  
  @Override
  public String toString() {
    return getClass().getName() + transitiveRange;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this) return true;
    if(obj == null) return false;
    
    if(obj.getClass() == TransitivePartitionConnectionFilter.class) {
      return transitiveRange.equals(((TransitivePartitionConnectionFilter)obj).transitiveRange);
    } else {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    return transitiveRange.hashCode();
  }
}
