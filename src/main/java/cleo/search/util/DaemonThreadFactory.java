package cleo.search.util;

import java.util.concurrent.ThreadFactory;

/**
 * DaemonThreadFactory
 * 
 * @author jwu
 * @since 03/02, 2011
 */
public class DaemonThreadFactory implements ThreadFactory {
  
  @Override
  public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setDaemon(true);
      return t;
  }
}