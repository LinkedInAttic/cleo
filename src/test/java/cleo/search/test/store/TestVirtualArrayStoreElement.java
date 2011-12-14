package cleo.search.test.store;

import java.io.File;

import krati.util.SourceWaterMarks;

import cleo.search.SimpleElement;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.VirtualArrayStoreElement;
import cleo.search.util.ScnFactory;
import cleo.search.util.SystemTimeScnFactory;

/**
 * TestVirtualArrayStoreElement
 * 
 * @author jwu
 * @since 05/10, 2011
 */
public class TestVirtualArrayStoreElement extends TestArrayStoreElement {
  
  protected ArrayStoreElement<SimpleElement> createElementStore() throws Exception {
    String source = getClass().getSimpleName();
    ScnFactory scnFactory = new SystemTimeScnFactory();
    SourceWaterMarks waterMarks = new SourceWaterMarks(new File(getStoreHomeDir(), "sourceWaterMarks.scn"));
    ArrayStoreElement<SimpleElement> store = super.createElementStore();
    
    return new VirtualArrayStoreElement<SimpleElement>(source, scnFactory, store, waterMarks);
  }
}
