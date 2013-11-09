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
 * SimpleTypeaheadElement
 * 
 * @author jwu
 * @since 02/05, 2011
 */
public class SimpleTypeaheadElement extends SimpleElement implements TypeaheadElement, Cloneable {
  private static final long serialVersionUID = 1L;
  
  private String line1;
  private String line2;
  private String line3;
  private String media;
  
  public SimpleTypeaheadElement(int id) {
    super(id);
  }
  
  @Override
  public void setLine1(String line) {
    this.line1 = line;
  }
  
  @Override
  public String getLine1() {
    return line1;
  }
  
  @Override
  public void setLine2(String line) {
    this.line2 = line;
  }
  
  @Override
  public String getLine2() {
    return line2;
  }
  
  @Override
  public void setLine3(String line) {
    this.line3 = line;
  }
  
  @Override
  public String getLine3() {
    return line3;
  }
  
  @Override
  public void setMedia(String media) {
    this.media = media;
  }
  
  @Override
  public String getMedia() {
    return media;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(super.toString());
    sb.append(" line1=\"").append(getLine1()).append("\"");
    sb.append(" line2=\"").append(getLine2()).append("\"");
    sb.append(" line3=\"").append(getLine3()).append("\"");
    sb.append(" media=\"").append(getMedia()).append("\"");
    return sb.toString();
  }
  
  @Override
  public Object clone() {
    SimpleTypeaheadElement elem = new SimpleTypeaheadElement(getElementId());
    
    elem.setScore(getScore());
    elem.setTimestamp(getTimestamp());
    elem.setTerms((String[])getTerms().clone());
    
    elem.setLine1(getLine1());
    elem.setLine2(getLine2());
    elem.setLine3(getLine3());
    elem.setMedia(getMedia());
    
    return elem;
  }
}
