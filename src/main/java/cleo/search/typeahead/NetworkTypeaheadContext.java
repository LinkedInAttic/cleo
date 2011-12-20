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

package cleo.search.typeahead;

import java.io.Serializable;

/**
 * 
 * NetworkTypeaheadContext
 * 
 * @author jwu
 * @since  08/31, 2011
 */
public interface NetworkTypeaheadContext extends Serializable {
  
  public int getSource();
  
  public void setSource(int source);
  
  public int[] getConnections();
  
  public void setConnections(int[] connections);
  
  public int[] getStrengths();
  
  public void setStrengths(int[] strengths);
  
  public int[][] getConnectionStrengths();
  
  public void setConnectionStrengths(int[][] connectionStrengths);
  
  public long getTimeoutMillis();
  
  public void setTimeoutMillis(long timeoutMillis);
}
