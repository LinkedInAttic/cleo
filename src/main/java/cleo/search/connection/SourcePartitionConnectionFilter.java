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
 * SourcePartitionConnectionFilter
 * 
 * @author jwu
 * @since 04/22, 2011
 */
public class SourcePartitionConnectionFilter implements PartitionConnectionFilter, Serializable {
  private static final long serialVersionUID = 1L;
  private final Range sourceRange;
  
  public SourcePartitionConnectionFilter(Range sourceRange) {
    if(sourceRange == null) {
      throw new NullPointerException("sourceRange is null");
    }
    this.sourceRange = sourceRange;
  }
  
  @Override
  public Range getPartitionRange() {
    return sourceRange;
  }
  
  @Override
  public boolean accept(Connection conn) {
    return conn == null ? false : sourceRange.has(conn.source());
  }
  
  @Override
  public boolean accept(int source, int target, boolean active) {
    return sourceRange.has(source);
  }
  
  @Override
  public String toString() {
    return getClass().getName() + sourceRange;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj == this) return true;
    if(obj == null) return false;
    
    if(obj.getClass() == SourcePartitionConnectionFilter.class) {
      return sourceRange.equals(((SourcePartitionConnectionFilter)obj).sourceRange);
    } else {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    return sourceRange.hashCode();
  }
}
