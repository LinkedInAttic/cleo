package cleo.search.util;

/**
 * SystemNanoTimeScnFactory
 * 
 * @author jwu
 * @since 05/10, 2011
 */
public class SystemNanoTimeScnFactory implements ScnFactory {
  
  @Override
  public long next() {
    return System.nanoTime();
  }
}
