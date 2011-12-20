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

package cleo.search.tool;

import java.util.ArrayList;
import java.util.List;

import cleo.search.Element;
import cleo.search.Indexer;
import cleo.search.MultiIndexer;

/**
 * MultiIndexerInitializer
 * 
 * @author jwu
 * @since 03/16, 2011
 */
public class MultiIndexerInitializer<E extends Element> implements IndexerInitializer<E> {
  protected final String name;
  protected final MultiIndexer<E> multiIndexer;
  
  public MultiIndexerInitializer(String name, List<IndexerInitializer<E>> subInitializers) {
    List<Indexer<E>> indexerList = new ArrayList<Indexer<E>>(); 
    for(IndexerInitializer<E> i : subInitializers) {
      if(i != null) {
        Indexer<E> indexer = i.getIndexer();
        if(indexer != null) indexerList.add(indexer);
      }
    }
    
    this.name = name;
    this.multiIndexer = new MultiIndexer<E>(name, indexerList);
  }
  
  public final String getName() {
    return name;
  }
  
  @Override
  public final Indexer<E> getIndexer() {
    return  multiIndexer;
  }
}
