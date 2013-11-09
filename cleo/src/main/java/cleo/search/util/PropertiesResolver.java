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
