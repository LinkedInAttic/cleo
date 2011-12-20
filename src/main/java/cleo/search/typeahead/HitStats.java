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

package cleo.search.typeahead;

import java.io.Serializable;

/**
 * HitStats
 * 
 * @author jwu
 * @since 02/03, 2011
 */
public final class HitStats implements Serializable {
  private static final long serialVersionUID = 1L;
  
  int numBrowseHits = 0;
  int numFilterHits = 0;
  int numResultHits = 0;
  long startTime = 0;
  long totalTime = 0;
  long lastTickTime = 0;
  
  void start() {
    startTime = System.currentTimeMillis();
    lastTickTime = startTime;
  }
  
  void stop() {
    totalTime = System.currentTimeMillis() - startTime;
  }
  
  public long tick() {
    long now = System.currentTimeMillis();
    long elapsedTime = now - lastTickTime;
    lastTickTime = now;
    return elapsedTime;
  }
  
  public final int getNumBrowseHits() {
    return numBrowseHits;
  }
  
  public final int getNumFilterHits() {
    return numFilterHits;
  }
  
  public final int getNumResultHits() {
    return numResultHits;
  }
  
  public final long getStartTime() {
    return startTime;
  }
  
  public final long getTotalTime() {
    return totalTime;
  }
}
