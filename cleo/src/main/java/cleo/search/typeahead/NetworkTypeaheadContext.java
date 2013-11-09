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
 * NetworkTypeaheadContext provides the information about the connections of a member
 * (see {@link #getSource()}). Given this context, a downstream network typeahead can
 * search for entities or activities within the first degree network connections of
 * the specified member.
 * 
 * <p>
 * NetworkTypeaheadContext defines a mapping from <code>U</code> to <code>U</code> (users to users).
 * When a context is sent to a downstream network typeahead, which specifies a mapping from 
 * <code>U</code> to <code>E</code> (users to entities), network entities <code>E</code>
 * associated with the first degree connections of the specified member are searched.
 * </p>
 * 
 * @author jwu
 * @since  08/31, 2011
 */
public interface NetworkTypeaheadContext extends Serializable {
  
  /**
   * Gets the <code>source</code> (e.g. the searcher).
   */
  public int getSource();
  
  /**
   * Sets the <code>source</code> (e.g. the searcher).
   */
  public void setSource(int source);
  
  /**
   * Gets the connections of the source known to this context.
   */
  public int[] getConnections();
  
  /**
   * Sets the connections of the source known to this context.
   * 
   * @param connections - the members connected to the source.
   */
  public void setConnections(int[] connections);
  
  /**
   * Gets the connections' strengths of the source known to this context.
   */
  public int[] getStrengths();
  
  /**
   * Sets the connections' strengths of the source known to this context.
   * 
   * @param strengths - the strengths for the members connected to the source.
   */
  public void setStrengths(int[] strengths);
  
  /**
   * Gets the connections and connections' strengths of the source known to this context.
   */
  public int[][] getConnectionStrengths();
  
  /**
   * Sets the connections and connections' strengths of the source known to this context.
   * 
   * @param connectionStrengths - the connections and connections' strengths.
   */
  public void setConnectionStrengths(int[][] connectionStrengths);
  
  /**
   * Gets the search timeout measured in milliseconds.
   */
  public long getTimeoutMillis();
  
  /**
   * Sets the search timeout measured in milliseconds.
   */
  public void setTimeoutMillis(long timeoutMillis);
}
