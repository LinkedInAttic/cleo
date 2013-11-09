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

/**
 * Connection
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public interface Connection {
  
  /**
   * @return the source of connection.
   */
  public int source();
  
  /**
   * @return the target of connection.
   */
  public int target();
  
  /**
   * @return the strength of this connection.
   */
  public int getStrength();
  
  /**
   * Sets the strength of this connection.
   * 
   * @param strength
   */
  public void setStrength(int strength);
  
  /**
   * @return the timestamp of this connection.
   */
  public long getTimestamp();
  
  /**
   * Sets the timestamp of this connection.
   * 
   * @param timestamp
   */
  public void setTimestamp(long timestamp);
  
  /**
   * @return <code>true</code> if this connection is active. Otherwise, <code>false</code>.
   */
  public boolean isActive();
  
  /**
   * Indicate whether this connection is active.
   * 
   * @param b
   */
  public void setActive(boolean b);
}
