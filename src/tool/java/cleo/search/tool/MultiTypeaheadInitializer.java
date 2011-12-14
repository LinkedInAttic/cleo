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
