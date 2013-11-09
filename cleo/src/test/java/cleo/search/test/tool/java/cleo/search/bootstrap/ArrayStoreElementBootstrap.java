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
