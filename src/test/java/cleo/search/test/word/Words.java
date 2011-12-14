package cleo.search.test.word;

import java.util.List;

/**
 * Words
 * 
 * @author jwu
 * @since 01/24, 2011
 */
public interface Words {
  
  public List<String> list();
  
  public boolean has(String word);
  
}
