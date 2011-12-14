package cleo.search.util;

import java.util.Comparator;

import cleo.search.Element;
import cleo.search.Hit;

/**
 * ElementHitScoreComparator
 * 
 * @author jwu
 * @since 04/26, 2011
 */
public class ElementHitScoreComparator implements Comparator<Hit<Element>> {

  @Override
  public int compare(Hit<Element> h1, Hit<Element> h2) {
    return h1.compareTo(h2);
  }
}
