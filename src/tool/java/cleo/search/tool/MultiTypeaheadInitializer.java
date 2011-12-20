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
import cleo.search.typeahead.MultiTypeahead;
import cleo.search.typeahead.Typeahead;

/**
 * MultiTypeaheadInitializer
 * 
 * @author jwu
 * @since 02/10, 2011
 */
public class MultiTypeaheadInitializer<E extends Element> implements TypeaheadInitializer<E> {
  private MultiTypeahead<E> multiTypeahead; 
  
  public MultiTypeaheadInitializer(String name, List<TypeaheadInitializer<E>> subInitializers) {
    List<Typeahead<E>> subTypeaheads = new ArrayList<Typeahead<E>>();
    for(TypeaheadInitializer<E> i : subInitializers) {
      subTypeaheads.add(i.getTypeahead());
    }
    multiTypeahead = new MultiTypeahead<E>(name, subTypeaheads);
  }
  
  @Override
  public Typeahead<E> getTypeahead() {
    return multiTypeahead;
  }
}
