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

package cleo.search.typeahead;

import java.io.File;

import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.selector.PrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;

/**
 * BrowseTypeaheadConfig
 * 
 * @author jwu
 * @since 02/13, 2011
 */
public class BrowseTypeaheadConfig<E extends Element> {
  private String name;
  
  // elementStore 
  private File elementStoreDir;
  private int elementStoreIndexStart;
  private int elementStoreCapacity;
  private int elementStoreSegmentMB;
  
  // BloomFilter prefixLength
  private int filterPrefixLength = 2;
  
  // elementSerializer
  private ElementSerializer<E> elementSerializer;
  
  // elementSelectorFactory
  private SelectorFactory<E> selectorFactory = new PrefixSelectorFactory<E>();
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void setElementStoreDir(File elementStoreDir) {
    this.elementStoreDir = elementStoreDir;
  }
  
  public File getElementStoreDir() {
    return elementStoreDir;
  }
  
  public void setElementStoreIndexStart(int elementStoreIndexStart) {
    this.elementStoreIndexStart = elementStoreIndexStart;
  }
  
  public int getElementStoreIndexStart() {
    return elementStoreIndexStart;
  }
  
  public void setElementStoreCapacity(int elementStoreCapacity) {
    this.elementStoreCapacity = elementStoreCapacity;
  }
  
  public int getElementStoreCapacity() {
    return elementStoreCapacity;
  }
  
  public void setElementStoreSegmentMB(int elementStoreSegmentMB) {
    this.elementStoreSegmentMB = elementStoreSegmentMB;
  }
  
  public int getElementStoreSegmentMB() {
    return elementStoreSegmentMB;
  }
  
  public void setFilterPrefixLength(int filterPrefixLength) {
    this.filterPrefixLength = filterPrefixLength;
  }
  
  public int getFilterPrefixLength() {
    return filterPrefixLength;
  }
  
  public void setElementSerializer(ElementSerializer<E> elementSerializer) {
    this.elementSerializer = elementSerializer;
  }
  
  public ElementSerializer<E> getElementSerializer() {
    return elementSerializer;
  }
  
  public void setSelectorFactory(SelectorFactory<E> selectorFactory) {
    this.selectorFactory = selectorFactory;
  }
  
  public SelectorFactory<E> getSelectorFactory() {
    return selectorFactory;
  }
}
