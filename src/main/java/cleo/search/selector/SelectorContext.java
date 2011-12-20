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

import java.io.Serializable;

import cleo.search.Score;

/**
 * SelectorContext
 * 
 * @author jwu
 * @since 02/11, 2011
 */
public class SelectorContext implements Score, Serializable {
  private static final long serialVersionUID = 1L;
  
  /**
   * Element score.
   */
  private double score;
  
  @Override
  public final double getScore() {
    return score;
  }
  
  @Override
  public final void setScore(double score) {
    this.score = score;
  }
  
  public void clear() {
    score = 0;
  }
}
