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

package cleo.search;

/**
 * SimpleElementFactory
 * 
 * @author jwu
 * @since 01/20, 2011
 */
public class SimpleElementFactory implements ElementFactory<SimpleElement> {

  @Override
  public SimpleElement createElement(int elementId, long timestamp, String... terms) {
    SimpleElement elem = new SimpleElement(elementId);
    elem.setTimestamp(timestamp);
    
    String[] termsLowercase = new String[terms.length];
    for(int i = 0; i < terms.length; i++) {
      termsLowercase[i] = terms[i].toLowerCase();
    }
    elem.setTerms(termsLowercase);
    
    return elem;
  }
  
  @Override
  public SimpleElement createElement(Element element) {
    SimpleElement e = new SimpleElement(element.getElementId());
    
    e.setTerms(e.getTerms());
    e.setScore(element.getScore());
    e.setTimestamp(element.getTimestamp());
    
    return e;
  }
}
