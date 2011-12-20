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

import java.io.IOException;

/**
 * ConnectionIndexer
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public interface ConnectionIndexer extends ConnectionFilter {
  
  /**
   * Indexes a connection.
   * 
   * @param  conn - a connection to be indexed.
   * @return <code>true</code> if the underlying indexes changed
   *         as a result of this operation. Otherwise, <code>false</code>.
   * @throws Exception if this operation failed.
   */
  public boolean index(Connection conn) throws Exception;
  
  /**
   * Flushes indexes.
   * 
   * @throws IOException if this operation failed.
   */
  public void flush() throws IOException;
}
