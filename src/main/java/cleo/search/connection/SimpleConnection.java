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

/**
 * SimpleConnection
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public class SimpleConnection implements Connection, Serializable {
  private static final long serialVersionUID = 1L;
  private int source;
  private int target;
  private int strength;
  private long timestamp;
  private boolean active;

  public SimpleConnection(int source, int target, boolean active) {
    this.source = source;
    this.target = target;
    this.active = active;
  }

  @Override
  public int source() {
    return source;
  }

  @Override
  public int target() {
    return target;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void setActive(boolean b) {
    this.active = b;
  }

  @Override
  public int getStrength() {
    return strength;
  }

  @Override
  public void setStrength(int strength) {
    this.strength = strength;
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    
    b.append(getClass().getName()).append('{')
     .append("source").append('=').append(source).append(',')
     .append("target").append('=').append(target).append(',')
     .append("active").append('=').append(active).append(',')
     .append("strength").append('=').append(strength).append(',')
     .append("timestamp").append('=').append(timestamp).append('}');
    
    return b.toString();
  }
}
