package jda.modules.report.controller;

import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.Selectx;
import jda.modules.dcsl.syntax.function.AttribFunctor;
import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dcsl.syntax.report.Input;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.ObjectJoinExpression;
import jda.modules.oql.def.Query;
import jda.modules.report.model.Report;
import jda.mosa.controller.ControllerBasic;

/**
 * @overview 
 *  A sub-type of {@link ParameterisedReportController} that represents controlers for parameterised 
 *  reports whose input can be a combination of the input attributes.
 *  
 *  <p>Support 2 basic cases of input attribute definitions:
 * 
 * <br>(1) non-join (join.length=1): 
 *    ALL input attributes are defined over the same input class AND 
 *    ONLY non-null input attributes are considered
 *    
 *    generate a normal object query on the input class, whose expressions are those defined by the input attributes
 *    (these attributes belong to the input class)
 *    
 * <br>(2) a single join (join.length >=2)
 *    generate a JoinQuery: 
 *      the join expression of the query is defined from the first input attribute
 *      other attributes (if any) will be used to filter the query result
 *
 *  <p>This differs from {@link ParameterisedSimpleReportController} in that only non-null input attributes 
 *  are considered.
 *    
 * @author dmle
 */
public class ParameterisedSearchReportController<C> extends ParameterisedReportController<C> {
  
  public ParameterisedSearchReportController(DODMBasic schema, ApplicationModule module,
    Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module,moduleGui, parent, config);
  }

  @Override
  protected Class generateReportQuery(Query<ObjectExpression> q, Report report) {
    /*
     * support 2 basic cases of input attribute definitions:
     * 
     * (1) non-join (join.length=1): 
     *    ALL input attributes are defined over the same input class AND 
     *    ONLY non-null input attributes are considered
     *    
     *    generate a normal object query on the input class, whose expressions are those defined by the input attributes
     *    (these attributes belong to the input class)
     *    
     * (2) a single join (join.length >=2)
     *    generate a JoinQuery: 
     *      the join expression of the query is defined from the first input attribute
     *      other attributes (if any) will be used to filter the query result
     */
    
    DODMBasic schema = getDodm();
    DSMBasic dsm = schema.getDsm();
    
    Class reportCls = getDomainClass(); //report.getClass();
    DataController reportDctl = getRootDataController(); 
        
    Map<DAttr,Input> inputAttributes = getInputAttributes();
    
    Class refClass = null, joinCls;
    Input inputDef;
    Class[] join;
  
    // NOTE: In case (2), only the first input attribute has the join definition
    // (other attributes carry additional value constraints), so lets check that first
    Entry<DAttr,Input> firstAttribEntry = inputAttributes.entrySet().iterator().next();
    inputDef = firstAttribEntry.getValue(); 
    join = inputDef.reference().classJoin();
    if (join.length == 1) {
      // case (1): all input attributes are defined over the same class
      // create an object expression for each input and adds it to the query
      ObjectExpression exp;
      Op op;
      Object inputVal;
      DAttr refAttrib;
      String refAttribName;
      DAttr inputAttrib;
      AttribFunctor attribFunc;
      Function func; 
      
      for (Entry<DAttr,Input> entry : inputAttributes.entrySet()) {
        inputAttrib = entry.getKey();
        inputDef = entry.getValue();
        attribFunc = inputDef.reference().attribFunc();
        join = inputDef.reference().classJoin();
        
        joinCls = join[0];
        
        // v2.7.4: skip attributes that are without attribute specification (these are 
        // used later by the filter)
        if (joinCls == CommonConstants.NullType)
          continue; // skip 

        // the referenced class
        if (refClass == null)
          refClass = joinCls;
        else if (refClass != joinCls) {
          // invalid input specification: all input attributes must come from the same class
          throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
              "Lớp không được định nghĩa đúng {0}", reportCls.getSimpleName());
        }

        // get the input value
        /*v3.0: get value from object form (in case formatting is used)
        inputVal = schema.getDsm().getAttributeValue(reportCls, report, inputAttrib);
        */
        inputVal = reportDctl.getDataFieldValue(inputAttrib);
        
        if (inputVal == null)
          continue; // skip this attribute if no input is specified
        
        refAttribName = attribFunc.attrib(); // one attribute
        refAttrib = dsm.getDomainConstraint(refClass, refAttribName);
        op = attribFunc.operator();

        //v2.7.3: use functor 
        // exp = new ObjectExpression(refClass, refAttrib, op, inputVal);

        func = attribFunc.function();
        if (func.isNil()) {
          // just the attribute, no function
          exp = new ObjectExpression(refClass, null, refAttrib, op, inputVal);
        } else {
          // attribute and function
          exp = new ObjectExpression(refClass, attribFunc, refAttrib, op, inputVal);
        }
        
        q.add(exp);
      }
    } else {
      // case (2): generate a join expression from the first input attribute
      // and use that as the query (other attributes (if any) will be used to filter the query result)
      
      // the join definition defined on the attribute
      Selectx joinDef = inputDef.reference();
      
      // get the attribute input value
      DAttr inputAttrib = firstAttribEntry.getKey();
      Object inputVal;
      /*v3.0: get value from object form (in case formatting is used)
      Object inputVal = schema.getDsm().getAttributeValue(reportCls, report, inputAttrib);
      */
      inputVal = reportDctl.getDataFieldValue(inputAttrib);
      
      // the domain class on which the join expression is applied is the first class in the join
      refClass = join[0];
      
      ObjectJoinExpression jexp = QueryToolKit.createJoinExpressionWithValueConstraint(dsm, joinDef, inputVal);
      
      q.add(jexp);
    }
    
    return refClass;
  }
}
