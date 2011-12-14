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
