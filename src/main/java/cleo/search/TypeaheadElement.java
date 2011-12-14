package cleo.search;

import java.io.Serializable;

/**
 * TypeaheadElement
 * 
 * @author jwu
 * @since 02/05, 2011
 */
public interface TypeaheadElement extends Element, Serializable {
  
  public void setLine1(String line);
  
  public String getLine1();
  
  public void setLine2(String line);
  
  public String getLine2();
  
  public void setLine3(String line);
  
  public String getLine3();
  
  public void setMedia(String media);
  
  public String getMedia();
}
