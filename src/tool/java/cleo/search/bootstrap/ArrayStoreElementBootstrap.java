package cleo.search.bootstrap;

import cleo.search.Element;
import cleo.search.ElementFactory;
import cleo.search.store.ArrayStoreElement;
import cleo.search.util.TermsHandler;
import krati.util.Chronos;

/**
 * ArrayStoreElementBootstrap
 * 
 * @author jwu
 * @since 01/20, 2011
 */
public class ArrayStoreElementBootstrap<E extends Element> implements TermsHandler {
  private final ArrayStoreElement<E> elementStore;
  private final ElementFactory<E> elementFactory;
  
  private volatile long counter = 0;
  private final long monitorBatch = 1000000;
  private final Chronos clock = new Chronos();
  
  public ArrayStoreElementBootstrap(ArrayStoreElement<E> elementStore, ElementFactory<E> elementFactory) throws Exception {
    this.elementStore = elementStore;
    this.elementFactory = elementFactory;
  }
  
  @Override
  public String[] handle(int source, String[] terms) throws Exception {
    counter++;
    
    if(elementStore.hasIndex(source)) {
      long scn = System.currentTimeMillis();
      E element = elementFactory.createElement(source, scn, terms);
      customize(element);
      elementStore.setElement(source, element, scn);
    }
    
    if(counter % monitorBatch == 0) {
      System.out.printf("processed %d in %d ms%n", monitorBatch, clock.tick());
    }
    
    return terms;
  }
  
  /**
   * Subclass needs to override this method.
   */
  protected void customize(E elment) {}
  
}
