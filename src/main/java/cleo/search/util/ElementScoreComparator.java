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
