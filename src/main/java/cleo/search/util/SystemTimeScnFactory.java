package cleo.search.util;

/**
 * SystemTimeScnFactory
 * 
 * @author jwu
 * @since 05/10, 2011
 */
public final class SystemTimeScnFactory implements ScnFactory {

  @Override
  public long next() {
    return System.currentTimeMillis();
  }
}
