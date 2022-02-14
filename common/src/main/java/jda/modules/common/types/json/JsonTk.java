package jda.modules.common.types.json;

import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

/**
 * @overview 
 *  Provides shared functionalities concernin JSON. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.2
 */
public class JsonTk {
  private JsonTk() {}

  /**
   * @effects 
   *  if val neq null and represents a string
   *    return the proper Java String object of the value
   *  else
   *    return null 
   */
  public static String getString(JsonValue val) {
    if (val == null || val.getValueType() !=  ValueType.STRING) return null;
    
    // remove quotes around the value returned by JsonValue.toString() 
    return val.toString().replaceAll("\"", "");
  }
  
}
