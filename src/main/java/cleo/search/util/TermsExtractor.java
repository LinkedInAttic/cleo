package cleo.search.util;

/**
 * TermsExtractor
 * 
 * @author jwu
 * @since 01/24, 2011
 */
public interface TermsExtractor {
  
  public String[] extract(String line);
  
}
