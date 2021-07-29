package jda.test.app.courseman.qproc.query;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.Selectx;
import jda.modules.dcsl.syntax.report.Input;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.ObjectJoinExpression;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.app.courseman.qproc.query.JoinDefTest.NotSupported;
import jda.test.app.courseman.qproc.query.JoinDefTest.Supported;

/**
 * @overview
 *  automatically translate a class join definition into object join expression
 *  
 * @author dmle
 *
 */
public class TranslateClassJoin2ObjectJoinExpressionTest extends CourseManExtendedTester {

  public ObjectJoinExpression[] doTranslation(
      Class defCls, // the class containing the join definitions
      Object[] attribVals, // values of the attributes defined in defCls
      Object[] expected // the expected result to check (if any)
      ) throws DataSourceException {
    
    System.out.println(this.getClass().getSimpleName());
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    // register this class to extract the join definitions
    registerClass(defCls);

    // test data
    // extract the join definitions
    Map<DAttr,Input> joinDefs = schema.getAnnotatedDomainAttributes(
        defCls, Input.class);

    ObjectJoinExpression[] exps = new ObjectJoinExpression[joinDefs.size()];
    
    DAttr attrib;
    Input inputDef;
    Selectx joinDef;
    Class[] classDef;
    ObjectJoinExpression exp;
    Object val;
    
    System.out.printf("Creating join expressions...%n%n");

    int index = 0;
    for (Entry<DAttr,Input> ej : joinDefs.entrySet()) {
      attrib = ej.getKey();
      inputDef = ej.getValue();
      val = attribVals[index];
      
      System.out.printf("  attribute: %s%n", attrib.name());
      joinDef = inputDef.reference();
      System.out.printf("  join def: %s%n", joinDef);

      // register all the join classes (if not already done so)
      classDef = joinDef.classJoin();
      for (Class c : classDef) {
        registerClass(c);
      }

      try {
        exp = QueryToolKit.createJoinExpressionWithValueConstraint(schema, joinDef, val);
        
        System.out.printf("  join expression: %s%n%n", exp);
        
        if (expected != null)
          assert ((Boolean)expected[index]);
        
        exps[index] = exp;
      } catch (Exception e) {
        // exception must be expected
        if (expected != null) {
          assert (e.getClass() == expected[index]) : "Invalid exception " + e + ": " + e.getMessage();
          System.out.printf("  [Expected] Could not create expression: %s%n%n", e.getMessage());
        } else {
          throw e;
        }
      }
      
      index++;
    }
    
    return exps;
  }
  
  @Test
  public void doValidTranslation() throws Exception {
    
    System.out.println("\ndoValidTranslation()");
    Class defCls = JoinDefTest.Supported.class;

    // test result
    Object[] expected = new Object[Supported.values.length]; 
    Arrays.fill(expected, Boolean.TRUE);
    
    doTranslation(defCls, Supported.values, expected);
  }
  
  @Test
  public void doInvalidTranslation() throws Exception {
    
    System.out.println("\ndoInvalidTranslation()");
    Class defCls = JoinDefTest.NotSupported.class;

    // test result
    Object[] expected = {
      NotImplementedException.class,
      NotImplementedException.class
    };
    
    doTranslation(defCls, NotSupported.values, expected);
  }
}
