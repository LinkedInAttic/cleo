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
 * ScoredClicksSelector
 * 
 * @author jwu
 * @since 02/18, 2011
 */
public class ScoredClicksSelector<E extends Element> extends ScoredPrefixSelector<E> {
  private static final long serialVersionUID = 1L;
  
  public ScoredClicksSelector(String... queryTerms) {
    super(queryTerms);
  }
  
  @Override
  public boolean select(E element, SelectorContext ctx) {
    boolean b = super.select(element, ctx);
    if(b) {
      float clicks = element.getScore() + 1;
      ctx.setScore(ctx.getScore() * clicks);
    }
    return b;
  }
}
