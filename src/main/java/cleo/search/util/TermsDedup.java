package cleo.search.util;

import java.util.HashSet;
import java.util.Set;


/**
 * TermsDedup
 * 
 * @author jwu
 * @since 02/17, 2011
 */
public class TermsDedup implements TermsHandler {
  private final Set<String> termSet = new HashSet<String>();
  
  @Override
  public String[] handle(int source, String[] terms) throws Exception {
    termSet.clear();
    
    for(String term : terms) {
      if(term != null) {
        termSet.add(term);
      }
    }
    
    int size = termSet.size();
    if(size < terms.length) {
      int i = 0;
      String[] results = new String[size];
      
      // preserve the original order of terms
      for(String term : terms) {
        if(termSet.remove(term)) {
          if(i < results.length) {
            results[i++] = term;
          }
        }
      }
      
      return results;
    } else {
      return terms;
    }
  }
}
