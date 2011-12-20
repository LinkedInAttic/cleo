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
import java.util.Random;

/**
 * RandomConnectionFilter
 * 
 * @author jwu
 * @since 04/22, 2011
 */
public class RandomConnectionFilter implements ConnectionFilter, Serializable {
  private static final long serialVersionUID = 1L;
  private final Random random = new Random();
  
  /**
   * Accepts a connection randomly.
   */
  @Override
  public boolean accept(Connection conn) {
    return conn == null ? false : (random.nextFloat() < 0.5F ? false : true);
  }
  
  @Override
  public boolean accept(int source, int target, boolean active) {
    return random.nextFloat() < 0.5F ? false : true;
  }
  
  @Override
  public String toString() {
    return getClass().getName();
  }
}
