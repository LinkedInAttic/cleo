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
 * ConnectionFilter
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public interface ConnectionFilter {

  /**
   * Checks whether a connection is acceptable or not.
   * 
   * @param conn - connection to test
   * @return <code>true</code> if the connection is acceptable. Otherwise, <code>false</code>.
   */
  public boolean accept(Connection conn);
  
  /**
   * Checks whether a connection is acceptable or not.
   *  
   * @param source - connection source
   * @param target - connection target
   * @param active - whether the connection is active or not.
   * @return <code>true</code> if the connection is acceptable. Otherwise, <code>false</code>.
   */
  public boolean accept(int source, int target, boolean active);
}
