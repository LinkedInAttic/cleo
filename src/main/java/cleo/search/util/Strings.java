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

package cleo.search.util;

/**
 * Strings
 * 
 * @author jwu
 * @since 02/09, 2011
 */
public class Strings {
  
  public static String concatenate(char sep, String... words) {
    StringBuilder sb = new StringBuilder();
    
    for(String s : words) {
      sb.append(s).append(sep);
    }
    
    int lastIndex = sb.length() - 1;
    if(lastIndex >= 0 && sb.charAt(lastIndex) == sep) {
      sb.deleteCharAt(lastIndex);
    }
    
    return sb.toString();
  }
  
  public static String concatenate(String... words) {
    return concatenate(',', words);
  }
  
  public static String toArray(String... words) {
    StringBuilder sb = new StringBuilder();
    
    sb.append('[');
    for(String s : words) {
      sb.append(s).append(',');
    }
    int lastIndex = sb.length() - 1;
    if(sb.charAt(lastIndex) == ',') {
      sb.deleteCharAt(lastIndex);
    }
    sb.append(']');
    
    return sb.toString();
  }
  
  public static String toSet(String... words) {
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
