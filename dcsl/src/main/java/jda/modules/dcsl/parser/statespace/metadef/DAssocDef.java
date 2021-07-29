/**
 * 
 */
package jda.modules.dcsl.parser.statespace.metadef;

import java.lang.annotation.Annotation;

import jda.modules.common.CommonConstants;
import jda.modules.dcsl.syntax.DAssoc;

/**
 * @overview 
 *  An implementation of {@link DAssoc} to encapsulate property definitions extracted directly from 
 *  the source code.
 *  
 * @author dmle
 *
 * @version 3.4 
 */
public class DAssocDef extends MetaAttrDef implements DAssoc{

  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "@DAssoc (ascName()=" + ascName() + ", role()=" + role()
        + ", ascType()=" + ascType() + ", endType()=" + endType()
        + ", associate()=" + associate() + ", dependsOn()=" + dependsOn()
        + ", derivedFrom()=" + derivedFrom() + ", normAttrib()=" + normAttrib()
        + ", reflexive()=" + reflexive() + ")";
  }

  /* (non-Javadoc)
   * @see java.lang.annotation.Annotation#annotationType()
   */
  @Override
  public Class<? extends Annotation> annotationType() {
    return DAssoc.class;
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc#ascName()
   */
  @Override
  public String ascName() {
    return (String) propValMap.get("ascName");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc#role()
   */
  @Override
  public String role() {
    return (String) propValMap.get("role");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc#ascType()
   */
  @Override
  public AssocType ascType() {
    return (AssocType) propValMap.get("ascType");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc#endType()
   */
  @Override
  public AssocEndType endType() {
    return (AssocEndType) propValMap.get("endType");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc#associate()
   */
  @Override
  public Associate associate() {
    return (Associate) propValMap.get("associate");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc#dependsOn()
   */
  @Override
  public boolean dependsOn() {
    return (Boolean) propValMap.getOrDefault("dependsOn", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc#derivedFrom()
   */
  @Override
  public boolean derivedFrom() {
    return (Boolean) propValMap.getOrDefault("derivedFrom", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc#normAttrib()
   */
  @Override
  public String normAttrib() {
    return (String) propValMap.getOrDefault("normalAttrib", CommonConstants.NullString);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc#reflexive()
   */
  @Override
  public boolean reflexive() {
    return (Boolean) propValMap.getOrDefault("reflexive", Boolean.FALSE);
  }

}
