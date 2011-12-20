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

package cleo.search.selector;

import cleo.search.Element;

/**
 * ScoredElementSelector
 * 
 * @author jwu
 * @since 05/04, 2011
 */
public class ScoredElementSelector<E extends Element> extends ScoredPrefixSelector<E> {
  private static final long serialVersionUID = 1L;
  
  public ScoredElementSelector(String... queryTerms) {
    super(queryTerms);
  }
  
  @Override
  public boolean select(E element, SelectorContext ctx) {
    boolean b = super.select(element, ctx);
    if(b) {
      ctx.setScore(ctx.getScore() * element.getScore());
    }
    return b;
  }
}
