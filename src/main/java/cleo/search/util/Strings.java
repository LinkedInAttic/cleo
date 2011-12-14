package cleo.search.util;

/**
 * Strings
 * 
 * @author jwu
 * @since 02/09, 2011
 */
public class Strings {

  public static String concatenate(String... words) {
    StringBuilder sb = new StringBuilder();
    
    sb.append('{');
    for(String s : words) {
      sb.append(s).append(',');
    }
    int lastIndex = sb.length() - 1;
    if(sb.charAt(lastIndex) == ',') {
      sb.deleteCharAt(lastIndex);
    }
    sb.append('}');
    
    return sb.toString();
  }
  
  private static final String HEXES = "0123456789ABCDEF";
  
  public static String hex(byte[] raw) {
    if(raw == null) {
      return null;
    }
    
    StringBuilder hex = new StringBuilder(raw.length << 1);
    for(byte b : raw) {
      hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
    }
    
    return hex.toString();
  }
  
  public static String hex(char[] raw) {
    if(raw == null) {
      return null;
    }
    
    StringBuilder hex = new StringBuilder(raw.length << 2);
    for(char c : raw) {
      hex.append(HEXES.charAt((c & 0xF000) >> 16))
         .append(HEXES.charAt((c & 0x0F00) >>  8))
         .append(HEXES.charAt((c & 0x00F0) >>  4))
         .append(HEXES.charAt((c & 0x000F)));
    }
    
    return hex.toString();
  }
  
  public static String hex(String word) {
    return (word == null) ? null : hex(word.toCharArray());
  }
}
