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

package cleo.search.network;

/**
 * Network
 * 
 * @author jwu
 * @since 04/28, 2011
 */
public interface Network {
  
  /**
   * The index of connection sub-array in the results of <code>getConnectionStrengths</code>. 
   */
  public static final int CONNECTION_SUBARRAY_INDEX = 0;
  
  /**
   * The index of strength sub-array in the results of <code>getConnectionStrengths</code>. 
   */
  public static final int STRENGTH_SUBARRAY_INDEX = 1;
  
  /**
   * Gets the first degree connections.
   * 
   * @param uid the user Id
   * @return the first degree connections.
   */
  public int[] getConnections(int uid);
  
  /**
   * Gets the first degree connection strengths.
   * 
   * @param uid the user Id
   * @return the first degree connection strengths.
   */
  public int[][] getConnectionStrengths(int uid);
  
  /**
   * Gets a user's first degree network.
   * 
   * @param uid the user Id.
   * @return the first degree NetworkInfo.
   */
  public NetworkInfo getFirstDegreeNetwork(int uid);
  
  /**
   * Gets a user's second degree network.
   * 
   * @param uid the user Id.
   * @return the second degree NetworkInfo.
   */
  public NetworkInfo getSecondDegreeNetwork(int uid);
  
  /**
   * Gets a user's network which typically contains connections at the first and second degrees.
   * 
   * @param uid the user Id.
   * @return the user's NetworkInfo.
   */
  public NetworkInfo[] getNetworkInfo(int uid);
}
