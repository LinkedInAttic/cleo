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
 * SimpleTypeaheadElementFactory
 * 
 * @author jwu
 * @since 02/05, 2011
 */
public class SimpleTypeaheadElementFactory implements ElementFactory<TypeaheadElement> {

  @Override
  public TypeaheadElement createElement(int elementId, long timestamp, String... terms) {
    TypeaheadElement elem = new SimpleTypeaheadElement(elementId);
    elem.setTimestamp(timestamp);
    elem.setTerms((String[])terms.clone());
    
    return elem;
  }
  
  @Override
  public TypeaheadElement createElement(Element element) {
    TypeaheadElement e = new SimpleTypeaheadElement(element.getElementId());
    
    e.setTerms(element.getTerms());
    e.setScore(element.getScore());
    e.setTimestamp(element.getTimestamp());
    
    if(element instanceof TypeaheadElement) {
      TypeaheadElement n = (TypeaheadElement)element;
      e.setLine1(n.getLine1());
      e.setLine2(n.getLine2());
      e.setLine3(e.getLine3());
      e.setMedia(n.getMedia());
    }
    
    return e;
  }
}
