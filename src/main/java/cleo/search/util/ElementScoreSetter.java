package cleo.search.util;

import cleo.search.Element;
import cleo.search.store.ArrayStoreElement;

/**
 * ElementScoreSetter
 * 
 * @author jwu
 * @since 02/18, 2011
 */
public class ElementScoreSetter<E extends Element> implements ScoreHandler {
  ArrayStoreElement<E> elementStore;
  
  public ElementScoreSetter(ArrayStoreElement<E> elementStore) {
    this.elementStore = elementStore;
  }
  
  @Override
  public double handle(int elementId, double elementScore) {
    if(elementStore.hasIndex(elementId)) {
      E element = elementStore.getElement(elementId);
      if(element != null) {
        element.setScore((float)elementScore);
      }
    }
    
    return elementScore;
  }
}
