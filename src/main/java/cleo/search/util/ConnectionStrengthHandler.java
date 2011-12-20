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

/**
 * ConnectionStrengthHandler
 * 
 * @author jwu
 * @since 06/13, 2011
 */
public interface ConnectionStrengthHandler {
  
  /**
   * Handles connections and connection strengths accordingly.
   * 
   * @param source      - the connection source
   * @param connections - the connection targets
   * @param strengths   - the connection strengths
   * @throws Exception
   */
  public void handle(int source, int[] connections, int[] strengths) throws Exception;
  
}
