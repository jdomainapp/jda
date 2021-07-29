package jda.modules.dodm.osm.relational;

import java.lang.reflect.Field;
import java.util.List;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.relational.sql.function.DataSourceFunction;

/**
 * 
 * @author dmle
 * 
 * @version 3.0
 */
public class RelationalOSMToolkit {
  private RelationalOSMToolkit() {}

// v3.1  
//  /**
//   * @effects 
//   *  return the name of the table column that is mapped to the attribute named <tt>attribName</tt> 
//   *  of the domain class <tt>c</tt> 
//   */
//  public static String getColName(DSMBasic dsm, Class c, DomainConstraint attrib) {
//    Field f = dsm.getDomainAttribute(c, attrib.name());
//    //v3.0: return getColumName(c, f);
//    return RelationalOSMToolkit.getColumName(dsm, c, f);
//  }
//  
//  /**
//   * @effects
//   *  return the name of the table column that is mapped to the domain field <tt>f</tt>
//   *  of the domain class <tt>c</tt> (<tt>f</tt> may be declared in the super-class of <tt>c</tt>)
//   * @see #genCreate(Class)
//   */
//  public static String getColumName(DSMBasic dsm, Class c, Field f) {
//    DomainConstraint dc = f.getAnnotation(DSMBasic.DC);
//    Type type = dc.type();
//    
//    String name = f.getName().toLowerCase();
//    // default: colName is same as field name (to lowercase)
//    String colName = name;
//
//    Class refType, sup;
//    boolean inheritedID;
//    DomainConstraint dcRef=null;
//    
//    sup = c.getSuperclass();
//    inheritedID = false;
//
//    if (dc.id() && (f.getDeclaringClass() != c)) {
//      inheritedID = true;
//    }
//    
//    // some exceptions
//    if (!type.isDomainType()) {
//      if (inheritedID) {
//        // this is an inherited id
//        refType = sup;// f.getDeclaringClass();
//        dcRef = dc;
//      }
//    } else {
//      // domain type
//      // get the referenced type and the referenced pk name to use as
//      // the table name for this field
//      if (!inheritedID) {
//        refType = f.getType();
//        DomainConstraint[] dcRefs = dsm.getIDAttributeConstraints(refType);
//        if (dcRefs == null)
//          throw new ApplicationRuntimeException(null, "No id attributes found for {0}", refType);
//        
//        dcRef = dcRefs[0];
//      } else { // inherited ids also result in FK constraints
//        refType = sup; 
//        dcRef = dc;
//      }
//    } 
//
//    // if this field is an FK, then creates its name differently
//    if (dcRef != null) {
//      // use tablename_col naming convention for FKs
//      String refTypePK = dcRef.name();
//      // fk column def: e.g. student_id
//      if (!inheritedID)
//        colName = name + "_" + refTypePK;
//      else
//        colName = refTypePK;
//    }
//    
//    return colName;
//  }
  
  /**
   * @effects 
   *  return the name of the table column that is mapped to the attribute <tt>f</tt>
   *  of the domain class <tt>c</tt> 
   *  and if <tt>func</tt> is specified then that name is updated to include the corresponding 
   *  SQL function 
   *  
   *  @version 3.1
   */
  public static String getColumName(RelationalOSMBasic osm, Class c, Field f, Function func) {
    DSMBasic dsm = osm.getDom().getDsm();
    DAttr dc = f.getAnnotation(DSMBasic.DC);
    Type type = dc.type();
    
    String name = f.getName().toLowerCase();
    // default: colName is same as field name (to lowercase)
    String colName = name;

    Class refType, sup;
    boolean inheritedID;
    DAttr dcRef=null;
    
    sup = c.getSuperclass();
    inheritedID = false;

    if (dc.id() && (f.getDeclaringClass() != c)) {
      inheritedID = true;
    }
    
    // some exceptions
    if (!type.isDomainType()) {
      if (inheritedID) {
        // this is an inherited id
        refType = sup;// f.getDeclaringClass();
        dcRef = dc;
      }
    } else {
      // domain type
      // get the referenced type and the referenced pk name to use as
      // the table name for this field
      if (!inheritedID) {
        refType = f.getType();
        List<DAttr> dcRefs = dsm.getIDDomainConstraints(refType);
        if (dcRefs == null)
          throw new ApplicationRuntimeException(null, "No id attributes found for {0}", refType);
        
        dcRef = dcRefs.get(0);
      } else { // inherited ids also result in FK constraints
        refType = sup; 
        dcRef = dc;
      }
    } 

    // if this field is an FK, then creates its name differently
    if (dcRef != null) {
      // use tablename_col naming convention for FKs
      String refTypePK = dcRef.name();
      // fk column def: e.g. student_id
      if (!inheritedID)
        colName = name + "_" + refTypePK;
      else
        colName = refTypePK;
    }
    
    // finally if func is specified then create an SQL function
    if (func != null) {
      DataSourceFunction sqlFunc = osm.getDataSourceFunctionFor(func);
      colName = sqlFunc.toString(colName);
    }
    
    return colName;
  }
}
