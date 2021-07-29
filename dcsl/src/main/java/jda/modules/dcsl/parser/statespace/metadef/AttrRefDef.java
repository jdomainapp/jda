package jda.modules.dcsl.parser.statespace.metadef;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;

import jda.modules.dcsl.syntax.AttrRef;

/**
 * @overview 
 *   An implementation of {@link AttrRef} to encapsulate property definitions extracted directly from 
 *   the source code.
 *   
 * @author ducmle
 *
 * @version 3.4
 */
public class AttrRefDef extends MetaAttrDef implements AttrRef {
  
  @Override
  public String toString() {
    return "AttrRefDef (name=" + value() + ")";
  }

  /* (non-Javadoc)
   * @see java.lang.annotation.Annotation#annotationType()
   */
  @Override
  public Class<? extends Annotation> annotationType() {
    return AttrRef.class;
  }
  
  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.AttrRef#name()
   */
  @Override
  public String value() {
    return (String) propValMap.get("name");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.AttrRef#type()
   */
  @Override
  public ElementType type() {
    return (ElementType) propValMap.get("type");
  }    
}
