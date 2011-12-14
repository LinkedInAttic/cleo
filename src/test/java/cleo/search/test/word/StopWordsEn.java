package cleo.search.test.word;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * StopWordsEn
 * 
 * @author jwu
 * @since 01/24, 2011
 */
public class StopWordsEn implements StopWords {

  public final static String[] stopWords = new String[] {
    "a", "able", "about", "across", "after", "all", "almost", "also",
    "am", "amn't", "among", "an", "and", "any", "are", "as", "at", "be", "because",
    "been", "but", "by", "can", "can't", "cannot", "could", "couldn't", "dear", "did", "didn't", "do", "don't",
    "does", "doesn't", "either", "else", "ever", "every", "for", "from", "get", "got", "gotta",
    "had", "hadn't", "has", "hasn't", "have", "haven't", "he", "her", "hers", "hi", "him", "his", "how", "however",
    "i", "i'm", "if", "in", "into", "is", "isn't", "it", "its", "just", "least", "let", "like",
    "likely", "may", "me", "might", "mine", "most", "must", "my", "neither", "no",
    "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "ours",
    "own", "rather", "said", "say", "says", "she", "should", "since", "so",
    "some", "than", "that", "the", "their", "them", "then", "there", "these",
    "they", "this", "those", "to", "too", "us", "up", "until", "wants", "was", "wasn't",
    "we", "were", "weren't", "will", "with", "won't", "would", "wouldn't",
    "yet", "you", "your", "yours"
  };
  
  static final Set<String> stopWordSet = new HashSet<String>(stopWords.length * 3);
  static {
    for(String s : stopWords) {
      stopWordSet.add(s);
    }
  }
  
  @Override
  public String[] filter(String... words) {
    List<String> list = new ArrayList<String>(words.length);
    
    for(String s : words) {
      if(!has(s)) list.add(s);
    }
    
    String[] results = new String[list.size()];
    list.toArray(results);
    return results;
  }
  
  @Override
  public boolean has(String word) {
    return word == null ? false : stopWordSet.contains(word.toLowerCase());
  }
  
  @Override
  public List<String> list() {
    List<String> list = new ArrayList<String>(stopWords.length);
    for(String s : stopWords) {
      list.add(s);
    }
    return list;
  }
}
