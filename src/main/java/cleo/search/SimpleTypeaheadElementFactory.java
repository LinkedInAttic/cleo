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
