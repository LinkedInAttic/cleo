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
