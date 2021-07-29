package jda.test.search;

import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.FIRST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.LAST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.NEXT;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.PREV;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.BeforeClass;

import jda.modules.common.expression.Op;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.dodm.DODMTesterWithBrowse;
import jda.test.dodm.DODMTesterWithBrowse.BrowsingStep;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;

/**
 * @overview
 *  test the object search function 
 *  
 * @author dmle
 */
public class TestSearch extends DODMEnhancedTester {  
  
  protected static Class c;
  
  @BeforeClass
  public static void init() {
    c = Student.class;
  }
  
  /**
   * @effects 
   *  return a Query over domain objects of class c whose 
   *  matching attribute values are specified in attribValConditions
   */
  public Query getObjectQuery(Class c, Collection<Tuple2<DAttr,Object>> attribValConditions) {
    Query query = new Query();

    DAttr attrib;
    Object val;
    Op op;
    ObjectExpression exp;
    
    for (Tuple2<DAttr, Object> attribVal : attribValConditions) {
      attrib = attribVal.getFirst();
      val = attribVal.getSecond();

      // op depends on the domain constraint type
      // - non-domain type: match
      // - domain type: equals
      if (attrib.type().isDomainType()) {
        op = Op.EQ;
      } else {
        op = Op.MATCH;
      }

      exp = new ObjectExpression(c, attrib, op, val);

      query.add(exp);
    }

    return query;
  }

  /**
   * @effects 
   *  return a Collection<Oid> as result of Query q
   * @param q
   * @return
   */
  public Collection<Oid> search(Class c, Query q) throws Exception {
    //DODM schema = instance.getDomainSchema();
    DSMBasic dsm = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    return dom.retrieveObjectOids(c, q);
  }
  
  /**
   * @effects 
   *  run a search query over a single class and return the Oids (if any)
   */
  public Collection<Oid> searchBasic() throws Exception {
    TestSearch me = (TestSearch) instance;
    
    System.out.printf("Domain class: %s%n", c);

    // create a search query
    Collection<Tuple2<DAttr,Object>> attribValConds = new ArrayList<Tuple2<DAttr,Object>>();
    
    DSMBasic dsm = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Collection<DAttr> attribs = dsm.getDomainConstraints(c); 
    Object[][] attribVals = {
         // {"id", "14"},
        // {"dob", "23"},
        {"name", "Nguyen%"}
        //{"dob", "1990"},
    };
    for (Object[] attribVal : attribVals) {
      for (DAttr attrib : attribs) {
        if (attrib.name().equals(attribVal[0])) {
          attribValConds.add(new Tuple2<DAttr,Object>(attrib, attribVal[1]));
          break;
        }
      }
    }
    
    Query q = me.getObjectQuery(c, attribValConds);
    
    System.out.printf("Search query: %s%n", q);
    
    // search for objects
    System.out.printf("Searching...%n", q);
    Collection<Oid> result = me.search(c, q);
        
    // print result
    return result;
  }

  /**
   * @effects 
   *  run a search query involving association between two classes
   *  and return the Oids matching (if any)
   */
  public Collection<Oid> searchOverAssociation() throws Exception {
    TestSearch me = (TestSearch) instance;
    DSMBasic dsm = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    
    System.out.printf("Domain class: %s%n", c);

    // create a search query
    Collection<Tuple2<DAttr,Object>> attribValConds = new ArrayList<Tuple2<DAttr,Object>>();
    Collection<DAttr> attribs = dsm.getDomainConstraints(c);
    
    // find a domain-type attribute of c and
    Class<SClass> domainType = SClass.class;
    String domainAttribName = "sclass";

    // find a suitable domain object of that type
    Object refObj = me.getRandomObjects(domainType, 1).iterator().next().getSecond();
    
    Object[][] attribVals = {
        {domainAttribName, refObj},
        //{"name", "Nguyen"}
    };
    
    for (Object[] attribVal : attribVals) {
      for (DAttr attrib : attribs) {
        if (attrib.name().equals(attribVal[0])) {
          attribValConds.add(new Tuple2<DAttr,Object>(attrib, attribVal[1]));
          break;
        }
      }
    }
    
    Query q = me.getObjectQuery(c, attribValConds);
    
    System.out.printf("Search query: %s%n", q);
    
    // search for objects
    System.out.printf("Searching...%n", q);
    Collection<Oid> result = me.search(c, q);
        
    // print result
    return result;
  }

  public void browseFirstToLast(Class c, Collection<Oid> oids) throws Exception {
     DODMTesterWithBrowse.browseFirstToLast(c, oids);
  }
  
  public void browse(Class c, Collection<Oid> oids, BrowsingStep[] browseSeq, Boolean[] expected) throws Exception {
    DODMTesterWithBrowse.browse(c, oids, browseSeq, expected);
  }

  public BrowsingStep[] getDefaultBrowsingSequence(Collection<Oid> oids) {
    BrowsingStep[] browseSeq;
    if (oids.size() == 1) {
      browseSeq = new BrowsingStep[] {
        FIRST,
        NEXT,
        LAST,
        PREV
      };          
    } else {
      browseSeq = new BrowsingStep[] {
      FIRST,
      NEXT,
      LAST,
      FIRST,NEXT,
      LAST,
      PREV
      };
    }

    return browseSeq;
  }
}
