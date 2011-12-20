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

import java.util.Comparator;

import cleo.search.Element;

/**
 * ElementScoreComparator
 * 
 * @author jwu
 * @since 04/26, 2011
 */
public class ElementScoreComparator implements Comparator<Element> {
  
  @Override
  public int compare(Element e1, Element e2) {
    return e1.getScore() < e2.getScore() ? -1 : (e1.getScore() == e2.getScore() ? (e1.getElementId() - e2.getElementId()) : 1);
  }
}
