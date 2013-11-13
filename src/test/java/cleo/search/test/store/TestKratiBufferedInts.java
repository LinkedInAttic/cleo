package cleo.search.test.store;

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

import java.io.File;

import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;
import cleo.search.store.KratiBufferedInts;
import cleo.search.store.KratiDataStore;
import cleo.search.store.StoreFactory;
import cleo.search.test.util.FileUtils;

/**
 * TestKratiBufferedInts
 * 
 * @author jwu
 * @since 09/14, 2012
 */
public class TestKratiBufferedInts extends TestDataStoreInts {

  @Override
  protected void setUp() {
    int initialCapacity = 500000;
    String storeName = getClass().getSimpleName();
    File storeHome = FileUtils.getTestDir(storeName);
    
    int indexSegmentFileSizeMB = 8;
    SegmentFactory indexSegmentFactory = new MemorySegmentFactory();
    int storeSegmentFileSizeMB = 32;
    SegmentFactory storeSegmentFactory = new MemorySegmentFactory();
    
    try {
      KratiDataStore underlyingStore =
        StoreFactory.createKratiDataStore(storeHome,
                                          initialCapacity,
                                          indexSegmentFileSizeMB,
                                          indexSegmentFactory,
                                          storeSegmentFileSizeMB,
                                          storeSegmentFactory);
      store = new KratiBufferedInts(underlyingStore);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
