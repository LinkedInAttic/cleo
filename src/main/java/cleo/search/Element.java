package cleo.search;

import java.io.Serializable;

/**
 * Element
 * 
 * @author jwu
 * @since 01/12, 2011
 */
public interface Element extends Serializable, Comparable<Element> {
  
  public int getElementId();
  
  public void setElementId(int id);
  
  public long getTimestamp();
  
  public void setTimestamp(long timestamp);
  
  public String[] getTerms();
  
  public void  setTerms(String... terms);
  
  public float getScore();
  
  public void setScore(float score);
  
}
