package cleo.search.util;

/**
 * TermsHandler
 * 
 * @author jwu
 * @since 01/20, 2011
 */
public interface TermsHandler {
  
  public String[] handle(int source, String[] terms) throws Exception;
  
}
