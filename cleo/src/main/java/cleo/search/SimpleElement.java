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

import java.util.Arrays;

/**
 * SimpleElement
 * 
 * @author jwu
 * @since 01/20, 2011
 */
public class SimpleElement implements Element, Cloneable {
  private static final long serialVersionUID = 1L;
  
  private int elementId;
  private long timestamp;
  private String[] terms;
  private float score;
  
  public SimpleElement(int id) {
    this.elementId = id;
  }
  
  @Override
  public int getElementId() {
    return elementId;
  }
  
  @Override
  public void setElementId(int id) {
    this.elementId = id;
  }
  
  @Override
  public String[] getTerms() {
    return terms == null ? new String[0] : terms;
  }
  
  @Override
  public void setTerms(String... terms) {
    this.terms = terms;
  }
  
  @Override
  public long getTimestamp() {
    return timestamp;
  }
  
  @Override
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  @Override
  public float getScore() {
    return score;
  }
  
  @Override
  public void setScore(float score) {
    this.score = score;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    sb.append(elementId).append(' ');
    sb.append(timestamp).append(' ');
    
    sb.append('{');
    for(String s : getTerms()) {
      sb.append(s).append(',');
    }
    int lastIndex = sb.length()-1;
    if(sb.charAt(lastIndex) == ',') {
      sb.deleteCharAt(lastIndex);
    }
    sb.append('}');
    
    sb.append(" score=").append(score);
    return sb.toString();
  }
  
  @Override
  public Object clone() {
    SimpleElement elem = new SimpleElement(getElementId());
    elem.setScore(getScore());
    elem.setTimestamp(getTimestamp());
    elem.setTerms((String[])getTerms().clone());
    return elem;
  }
  
  @Override
  public int hashCode() {
    int hashCode = elementId;
    hashCode += timestamp / 23;
    
    if(terms != null) {
      for(String t : terms) {
        hashCode += t.hashCode();
      }
    }
    
    return hashCode;
  }
  
  @Override
  public boolean equals(Object o) {
    if(o == null) return false;
    if(o.getClass() == getClass()) {
      SimpleElement e = (SimpleElement)o;
      return elementId == e.elementId &&
             timestamp == e.timestamp &&
             Arrays.equals(terms, e.terms) &&
             score == e.score;
    } else {
      return false;
    }
  }
  
  @Override
  public int compareTo(Element e) {
    return score < e.getScore() ? -1 : (score == e.getScore() ? (elementId - e.getElementId()) : 1);
  }
}
