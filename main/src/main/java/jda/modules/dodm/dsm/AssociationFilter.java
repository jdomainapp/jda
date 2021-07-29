package jda.modules.dodm.dsm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.filter.Filter;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;

/**
 * @overview
 *  A sub-type of {@link Filter} that is used to check an {@link DAssoc} for matching 
 *  association-specific conditions (e.g. association type, association end type, etc.) 
 *  
 * @author dmle
 */
public class AssociationFilter implements Filter<DAssoc> {

  private Map<AssocType,AssocEndType> assocTypeMap;

//  private List<AssocType> assocTypes;
//  private List<AssocEndType> assocEndTypes;
//
//  public void addAssociationType(AssocType...assocTypes) {
//    if (assocTypes != null && assocTypes.length > 0) {
//      if (this.assocTypes == null)
//        this.assocTypes = new ArrayList();
//      
//      for (AssocType at : assocTypes)
//        this.assocTypes.add(at);
//    }
//  }
//  
//  public void addAssociationEndType(AssocEndType assocEndType) {
//    if (this.assocEndTypes == null)
//      this.assocEndTypes = new ArrayList();
//
//    this.assocEndTypes.add(assocEndType);
//  }

  public void addAssociationTypeSpec(AssocType assocType, AssocEndType assocEndType) {
    if (assocType != null && assocEndType != null) {
      if (assocTypeMap == null) {
        assocTypeMap = new HashMap();
      }
      
      assocTypeMap.put(assocType, assocEndType);
    }
  }
  
  public void reset() {
//    assocTypes = null;
//    assocEndTypes = null;
    assocTypeMap = null;
  }
  
  @Override
  public boolean check(DAssoc o, Object...args) {
    if (o == null)
      return false;

    boolean ok;
    if (assocTypeMap != null) {
      ok = checkRegid(o);
    }  else {
      ok = true;
    }
    
    return ok;
    
    // checkFlexibly(o);
  }

  /**
   * @requires {@link #assocTypeMap} != null /\ o != null
   */
  private boolean checkRegid(DAssoc o) {
    AssocType at; 
    AssocEndType et;
    
    AssocType ot = o.ascType();
    AssocEndType oe = o.endType();
    
    for (Entry<AssocType, AssocEndType> e : assocTypeMap.entrySet()) {
      at = e.getKey();
      et = e.getValue();
      
      if (at == ot && et == oe) {
        // satisfy
        return true;
      }
    }
    
    return false;
  }

//  private boolean checkFlexibly(Association o) {
//    if (assocTypes == null && assocEndTypes == null)
//      return true;
//    
//    AssocType at = o.type();
//    AssocEndType et = o.endType();
//    
//    boolean ok = false;
//    // check association types if specified
//    if (assocTypes != null) {
//      for (AssocType t : assocTypes) {
//        if (t == at) {
//          // satisfy assoc type
//          // check end types, if specified
//          if (assocEndTypes != null) {
//            for (AssocEndType e : assocEndTypes) {
//              if (e == et) {
//                ok = true;
//                break;
//              }
//            }
//          } else {
//            ok = true;
//          }
//          
//          break;
//        }
//      }
//    }
//    
//    return ok;
//  }
}
