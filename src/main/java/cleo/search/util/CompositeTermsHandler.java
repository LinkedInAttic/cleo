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
