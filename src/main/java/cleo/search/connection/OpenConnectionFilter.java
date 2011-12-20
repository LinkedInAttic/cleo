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
 * OpenConnectionFilter
 * 
 * @author jwu
 * @since 04/22, 2011
 */
public class OpenConnectionFilter implements ConnectionFilter, Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * Accepts all non-null connections.
   * 
   * @return <code>true</code> for all non-null connections.
   */
  @Override
  public boolean accept(Connection conn) {
    return conn == null ? false : true;
  }
  
  @Override
  public boolean accept(int source, int target, boolean active) {
    return true;
  }
  
  @Override
  public String toString() {
    return getClass().getName();
  }
}
