package cleo.search.typeahead;

import java.util.concurrent.Callable;

import cleo.search.Element;
import cleo.search.collector.Collector;

/**
 * TypeaheadTask
 * 
 * @author jwu
 * @since 02/10, 2011
 */
public class TypeaheadTask<E extends Element> implements Callable<Collector<E>> {
  private final Typeahead<E> ta;
  private final int uid;
  private final String[] terms;
  private final Collector<E> collector;
  private final long timeoutMillis;
  
  public TypeaheadTask(Typeahead<E> ta, int uid, String[] terms, Collector<E> collector, long timeoutMillis) {
    this.ta = ta;
    this.uid = uid;
    this.terms = terms;
    this.collector = collector;
    this.timeoutMillis = timeoutMillis;
  }
  
  @Override
  public Collector<E> call() throws Exception {
    return ta.search(uid, terms, collector, timeoutMillis);
  }
  
  public final Typeahead<E> getTypeahead() {
    return ta;
  }

  public final int getUid() {
    return uid;
  }
  
  public final String[] getTerms() {
    return terms;
  }
  
  public final Collector<E> getCollector() {
    return collector;
  }
  
  public final long getTimeoutMillis() {
    return timeoutMillis;
  }
}
