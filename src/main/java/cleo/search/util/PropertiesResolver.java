package cleo.search.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PropertiesResolver
 * 
 * @author jwu
 * @since 02/28, 2011
 */
public class PropertiesResolver {
  
  final static String regexKey = "\\$\\{.*\\}";
  final static Pattern pattern = Pattern.compile(regexKey);
  
  public static void resolve(Properties properties) {
    Map<String, String> map = new HashMap<String, String>();
    
    for(Object obj : properties.keySet()) {
      String key = (String)obj;
      String val = properties.get(key).toString();
      
      Matcher m = pattern.matcher(val);
      if(m.find()) {
        String source = m.group();
        source = source.substring(2, source.length()-1).trim();
        map.put(key, source);
      }
    }
    
    for(String key : map.keySet()) {
      String val = properties.remove(key).toString();
      String replace = properties.getProperty(map.get(key));
      
      Matcher m = pattern.matcher(val);
      String newVal = m.replaceFirst(replace);
      properties.put(key, newVal);
    }
  }
}
