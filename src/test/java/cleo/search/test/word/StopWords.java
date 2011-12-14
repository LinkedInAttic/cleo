package cleo.search.test.word;

/**
 * StopWords
 * 
 * @author jwu
 * @since 01/24, 2011
 */
public interface StopWords extends Words {
  
  public String[] filter(String... words);
  
}
