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

import cleo.search.network.Proximity;

/**
 * ElementHit
 * 
 * @author jwu
 * @since 02/11, 2011
 * 
 * @param <E> Element
 */
public final class ElementHit<E extends Element> implements Hit<E>, Cloneable {
  private static final long serialVersionUID = 1L;
  
  private E element;
  private double score;
  private String source;
  private Proximity proximity = Proximity.NONE;
  
  public ElementHit(E element) {
    this.element = element;
  }
  
  public ElementHit(E element, double score) {
    this.element = element;
    this.score = score;
  }
  
  public ElementHit(E element, double score, String source) {
    this.element = element;
    this.score = score;
    this.source = source;
  }
  
  public ElementHit(E element, double score, String source, Proximity proximity) {
    this.element = element;
    this.score = score;
    this.source = source;
    setProximity(proximity);
  }
  
  @Override
  public E getElement() {
    return element;
  }
  
  @Override
  public void setElement(E element) {
    this.element = element;
  }
  
  @Override
  public double getScore() {
    return score;
  }
  
  @Override
  public void setScore(double score) {
    this.score = score;
  }
  
  @Override
  public String getSource() {
    return source;
  }
  
  @Override
  public void setSource(String source) {
    this.source = source;
  }
  
  @Override
  public Proximity getProximity() {
    return proximity;
  }
  
  @Override
  public void setProximity(Proximity proximity) {
    this.proximity = (proximity == null) ? Proximity.NONE : proximity;
  }
  
  @Override
  public void clear() {
    element = null;
    score = 0;
  }
  
  @Override
  public int compareTo(Hit<? super E> s) {
    double score2 = s.getScore();
    if(score < score2) {
      return -1;
    } else if(score > score2) {
      return 1;
    } else {
      // The lower the proximity ordinal, the greater the value.
      int cmp = s.getProximity().ordinal() - proximity.ordinal();
      return cmp == 0 ? (element.getElementId() - s.getElement().getElementId()) : cmp;
    }
  }
  
  @Override
  public int hashCode() {
    return element.hashCode();
  }
  
  @Override
  @SuppressWarnings({"unchecked"})
  public boolean equals(Object o) {
    if(o == this) return true;
    if(o == null) return false;
    
    if(o.getClass() == ElementHit.class) {
      return element == ((ElementHit)o).element;
    }
    
    return false;
  }
  
  @Override
  public Object clone() {
    ElementHit<E> hit = new ElementHit<E>(element, score, source);
    hit.proximity = proximity;
    return hit;
  }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append(ElementHit.class.getSimpleName()).append(':');
    
    // Append element
    b.append(' ').append("element=").append('{').append(element).append('}');
    
    // Append source
    if(source != null) {
      b.append(' ').append("source=").append(source);
    }
    
    // Append proximity
    if(proximity != null && proximity != Proximity.NONE) {
      b.append(' ').append("proximity=").append(proximity);
    }
    
    // Append score
    b.append(' ').append("score=").append(score);
    
    return b.toString();
  }
}
