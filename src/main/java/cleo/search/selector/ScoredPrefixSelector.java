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
 * ScoredPrefixSelector
 * 
 * @author jwu
 * @since 02/10, 2011
 * 
 * <p>
 * 05/16, 2011 - Updated scoring to return 1.0f upon ordered and full-term match (i.e. phrase match). <br/>
 */
public class ScoredPrefixSelector<E extends Element> extends PrefixSelector<E> {
  private static final long serialVersionUID = 1L;
  
  public ScoredPrefixSelector(String... queryTerms) {
    super(queryTerms);
  }
  
  @Override
  public boolean select(E element, SelectorContext ctx) {
    final String[] elemTerms = element.getTerms();
    if(elemTerms == null || elemTerms.length == 0) {
      return false;
    }
    
    final int eCount = elemTerms.length;
    final int qCount = queryTerms.length;
    
    int i = 0;
    int index = 0;
    int lastIndex = -1;
    int distance = 0;
    int countBoost = 0;
    int orderBoost = 0;
    double score = qCount / (double)(qCount + eCount + 1);
    
    for(String prefix : queryTerms) {
      for(i = 0; i < eCount; i++) {
        index = index % eCount;
        if(elemTerms[index].startsWith(prefix)) {
          // Adjust score according to whether two adjacent terms are matched in order
          if(lastIndex < index) {
            score = Math.sqrt(score);
            orderBoost++;
          } else {
            score = score * score;
            orderBoost--;
          }
          
          // Boost score upon each full-term match
          if(prefix.length() == elemTerms[index].length()) {
            score = Math.sqrt(score);
            countBoost++;
          }
          
          // Add distance between the last two matched terms
          distance += Math.abs(index - lastIndex);
          
          lastIndex = index;
          break;
        }
        index++;
      }
      
      // Query term cannot be matched
      if(i == eCount) return false;
    }
    
    if(orderBoost == qCount && countBoost == eCount) {
      score = 1.0f;
    } else {
      score = score * qCount / (distance + eCount + qCount);
    }
    
    ctx.setScore(score);
    
    return true;
  }
}
