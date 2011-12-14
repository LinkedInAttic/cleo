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
