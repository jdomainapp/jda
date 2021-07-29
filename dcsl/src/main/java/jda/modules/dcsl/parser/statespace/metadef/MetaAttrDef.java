/**
 * 
 */
package jda.modules.dcsl.parser.statespace.metadef;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @overview 
 *  A shared interface representing the definitions of the meta-attributes. 
 *  
 * @author dmle
 *
 * @version 3.4
 */
public abstract class MetaAttrDef {
  
  protected Map<String,Object> propValMap;
  
  /**
   * @effects
   *  initialise this with an emtpy {@link #propValMap} 
   */
  protected MetaAttrDef() {
    propValMap = new LinkedHashMap<>();
  }
  
  /**
   * 
   * @effects
   *    put <tt>(key,val)</tt> to  {@link #propValMap}
   */
  public void setPropertyValue(String key, Object val) {
    propValMap.put(key, val);
  }
  

  /**
   * @effects 
   *  return all entries in this as Collection
   * @version 5.2 
   */
  public Collection<Entry<String, Object>> getProperties() {
   return propValMap.entrySet();
  }
}
