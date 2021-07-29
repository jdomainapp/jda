/**
 * 
 */
package jda.modules.dcsl.parser.statespace.metadef;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.javaparser.ast.type.Type;

/**
 * @overview 
 *  Encapsulate definition information of a class field
 *  
 * @author dmle
 *
 * @version 3.4 
 */
public class FieldDef {
  private Type type;
  private String name;
  
  // annotations associated to this (if any)
  private Map<Class<? extends Annotation>, MetaAttrDef> anoMap;
  
  /**
   * @effects 
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @effects 
   */
  public void setType(Type type) {
    this.type = type;
  }
 
  /**
   * @effects 
   *  return name
   */
  public String getName() {
    return name;
  }
  
  
  /**
   * @effects return type
   */
  public Type getType() {
    return type;
  }
  
  /**
   * @effects 
   *  add <tt>(anoType, anoDef)</tt> to this
   */
  public void addAnnotation(Class<? extends Annotation> anoType, MetaAttrDef anoDef) {
    if (anoMap == null) anoMap = new LinkedHashMap<>();
    
    anoMap.put(anoType, anoDef);
  }
  /**
   * @effects 
   *  if exists annotations associated to this
   *    return them
   *  else
   *    return null
   */
  public Collection<MetaAttrDef> getAnnotations() {
    if (anoMap != null)
      return anoMap.values();
    else
      return null;
  }
  /**
   * @effects 
   *  if exists in this a {@link MetaAttrDef} for annotation typed <tt>anoType</tt>
   *    return it
   *  else
   *    return null
   */
  public MetaAttrDef getAnnotation(Class<? extends Annotation> anoType) {
    if (anoMap != null) {
      return anoMap.get(anoType);
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    return "FieldDef (" + type + "," + name + ")";
  }
}
