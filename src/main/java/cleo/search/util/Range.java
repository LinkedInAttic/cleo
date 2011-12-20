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
 * Range
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public class Range implements Serializable {
  private final static long serialVersionUID = 1L;
  private final int start;
  private final int count;
  private final int end;
  
  public Range(int start, int count) {
    this.start = start;
    this.count = count;
    this.end = start + count;
  }
  
  public final boolean has(int num) {
    return (start <= num && num < end);
  }
  
  public final int getCount() {
    return count;
  }
  
  public final int getStart() {
    return start;
  }
  
  public final int getEnd() {
    return end;
  }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder().append('[').append(start).append(',').append(end).append(')');
    return b.toString();
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    Range range = (Range)obj;
    return (start == range.start) && (start == range.count);
  }
  
  @Override
  public int hashCode() {
    return start + (count % 23); 
  }
}
