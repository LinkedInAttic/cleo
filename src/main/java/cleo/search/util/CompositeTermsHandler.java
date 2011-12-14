package cleo.search.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CompositeTermsHandler
 * 
 * @author jwu
 * @since 02/17, 2011
 */
public final class CompositeTermsHandler implements TermsHandler {
  private final List<TermsHandler> handlerList;
  
  public CompositeTermsHandler() {
    handlerList = Collections.synchronizedList(new ArrayList<TermsHandler>());
  }
  
  public CompositeTermsHandler add(TermsHandler handler) {
    if(handler != null && handler != this) {
      handlerList.add(handler);
    }
    return this;
  }
  
  public CompositeTermsHandler remove(TermsHandler handler) {
    if(handler != null) {
      handlerList.remove(handler);
    }
    return this;
  }
  
  @Override
  public String[] handle(int source, String[] terms) throws Exception {
    String[] resultTerms = terms;
    for(TermsHandler h : handlerList) {
      resultTerms = h.handle(source, resultTerms);
    }
    return resultTerms;
  }
}
