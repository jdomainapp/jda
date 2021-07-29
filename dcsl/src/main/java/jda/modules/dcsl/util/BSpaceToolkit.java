package jda.modules.dcsl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.type.Type;

import jda.modules.dcsl.parser.ClassAST;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAssoc.AssocType;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class BSpaceToolkit {
  private BSpaceToolkit() {}
  
  /**
   * @requires <tt>dcls.stateSpace</tt> is not empty
   * 
   * @effects 
   *   return a {@link LinkedHashMap} containing mappings of the class fields in <tt>dcls</tt>'s state space that are 
   *   involved in creating the parameter types for 
   *   the operation whose type is {@link DOpt.Type#AutoAttributeValueGen} to the actual parameter types
   *   
   *   Result contains <tt>(f, fieldType)</tt> as the first entry and then one entry for each 
   *   derived fields of <tt>f</tt> in <tt>dcls</tt>.
   */
  public static LinkedHashMap<FieldDef, Type> getAutoAttributeValueGenParamTypes(ClassAST dcls,
      FieldDef f, Type fieldType, DAttr dc) {
    Map<DAttrDef, FieldDef> stateSpace = dcls.getStateSpace();
    
    if (stateSpace == null) {
      // empty state space
      return null;
    }

    LinkedHashMap<FieldDef, Type> paramTypes = new LinkedHashMap<>();
    
    // add (f, fieldType) first
    paramTypes.put(f, fieldType);
    
    // then add the derived fields (if any)
    String[] derivedNames = dc.derivedFrom();
    if (derivedNames.length > 0) {
      // has derived attributes
      FieldDef df;
      for (String dn : derivedNames) {
        df = lookUpFieldDef(stateSpace, dn);
        paramTypes.put(df, df.getType());
      }
    } 
    
    return paramTypes;
  }


  /**
   * This is a simplified version of {@link #getAutoAttributeValueGenParamTypes(ClassAST, FieldDef, Type, DAttr)}.
   * 
   * @requires <tt>dcls.stateSpace</tt> is not empty
   * 
   * @effects 
   *   return a List of {@link Type}s containing the parameter types for 
   *   the operation {@link DOpt.Type#AutoAttributeValueGen} for the input domain field.
   */
  public static List<Type> getAutoAttributeValueGenParamTypesArray(ClassAST dcls,
      FieldDef f, Type fieldType, DAttrDef dc) {

    Map<DAttrDef, FieldDef> stateSpace = dcls.getStateSpace();

    if (stateSpace == null) return null;
    
    List<Type> paramTypes = new ArrayList<>();

    // add (f, fieldType) first
    Type objType = ParserToolkit.getObjectType(fieldType);
    paramTypes.add(objType);


    // then add the derived fields (if any)
    String[] derivedNames = dc.derivedFrom();
    if (derivedNames.length > 0) {
      // has derived attributes
      for (String dn : derivedNames) {
        FieldDef df = lookUpFieldDef(stateSpace, dn);
        objType = ParserToolkit.getObjectType(df.getType());
        paramTypes.add(objType);
      }
    } 
    
    return paramTypes;
  }
  
  /**
   * @effects 
   *  if exists in <tt>stateSpaceMap</tt> {@link FieldDef} whose name is <tt>fieldName</tt>
   *    return it
   *  else
   *    return null;
   */
  public static FieldDef lookUpFieldDef(Map<DAttrDef, FieldDef> stateSpaceMap,
      String fieldName) {
    Collection<FieldDef> fieldDefs = stateSpaceMap.values();
    for (FieldDef fieldDef : fieldDefs) {
      if (fieldDef.getName().equals(fieldName)) {
        return fieldDef;
      }
    }
    
    // not found
    return null;
  }

  /**
   * @requires assoc != null
   * @effects 
   *  if <tt>assoc</tt> realises the one end of an one-one association and this end depends on the associate's end
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public static boolean isDependentOneEnd(DAssoc assoc) {
    if (assoc.ascType().equals(AssocType.One2One) && assoc.associate().determinant()) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  return the standard JavaBean name of a method for the field <tt>fieldName</tt> and that has prefix <tt>prefix</tt>
   */
  public static String genMethodNameForField(String prefix, String fieldName) {
    return prefix+ Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
  }
  
  /**
   * @effects 
   *  return the specified method name for field named <tt>fieldName</tt> 
   */
  public static String genAutoAttributeValueGenMethodName(String fieldName) {
    return genMethodNameForField("gen", fieldName);
  }
}
