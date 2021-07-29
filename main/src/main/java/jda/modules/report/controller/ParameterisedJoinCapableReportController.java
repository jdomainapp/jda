package jda.modules.report.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.Selectx;
import jda.modules.dcsl.syntax.function.AttribFunctor;
import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dcsl.syntax.query.QueryDef;
import jda.modules.dcsl.syntax.report.Input;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.modules.report.model.Report;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A sub-type of <tt>ReportController</tt> that represents controlers for relatively complex parameterised 
 *  reports. 
 *  
 *  <p>It supports relatively more advanced cases of input attribute definitions for join query as described below.
 * 
 * <ul>
 * <li>Together the input attributes define one big join query.
 * 
 * <li>The can be more than one input attributes that specify the join definitions,
 * 
 * <li>Only the input attributes whose input value is not <tt>null</tt> are added to the report query 
 * 
 * <li>Non-join input attributes must be defined before the join attributes, and they can be specified with 
 * a source query using {@link QueryDef}.
 * 
 * <li>Join attributes result in constraint expressions (WHERE clauses in SQL) 
 * 
 * <li>The first class in the join definitions must be the same, and that it 
 * must also be the one used in the definitions of all the other non-join input attributes, 
 * 
 * <li>When put together the whole join query makes sense (i.e. its expressions can be evaluated correctly).
 * </ul>
 * @author dmle
 * 
 * @version 3.1: complete implementation
 * TODO: relax the join check conditions using FlexiQuery
 */
public class ParameterisedJoinCapableReportController<C> extends ParameterisedReportController<C> {
  
  public ParameterisedJoinCapableReportController(DODMBasic schema, ApplicationModule module,
    Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module,moduleGui, parent, config);
  }

  @Override
  protected Class generateReportQuery(Query<ObjectExpression> q, Report report) {
    /*
     * support 1 (relatively more advanced) case of input attribute definitions for join query.
     * Together the input attributes define one big join query.
     * 
     * The can be more than one input attributes that specify the join definitions,
     * 
     * PROVIDED that the first class in the join definitions must be the same, and that it 
     * must also be the one used in the definitions of all the other non-join input attributes, 
     * 
     * AND when put together the whole join query makes sense 
     *  (i.e. its expressions can be evaluated correctly).
     *  
     * Thus, the role of the non-join input attributes are to define filter-typed expressions 
     * (WHERE clauses in SQL) to narrow down the result.
     * 
     * The referenced class to return is the class mentioned above.
     */
    
    DODMBasic schema = getDodm();
    DSMBasic dsm = schema.getDsm();
    
    Class reportCls = getDomainClass(); //report.getClass();
    DataController reportDctl = getRootDataController(); 

    Map<DAttr,Input> inputAttributes = getInputAttributes();
    
    Class refClass = null;
    Input inputDef;
    Class[] join;

    ObjectExpression exp;
    Op op;
    Object inputVal;
    DAttr refAttrib;
    String refAttribName;
    DAttr inputAttrib;
    AttribFunctor attribFunc;
    Function func; 
    Selectx joinDef;
    
    List<ObjectExpression> njExps = new ArrayList<>();
    
    // loop through the input attributes and create the query expressions from them.
    // add the non-join expressions to a list to add them to the query later 
    for (Entry<DAttr,Input> entry : inputAttributes.entrySet()) {
      inputAttrib = entry.getKey();
      inputDef = entry.getValue();
      join = inputDef.reference().classJoin();
      
      // get the input value
      
      /* v3.1: support the case where input attribute value needs to be retrieved from the data source
      inputVal = reportDctl.getDataFieldValue(inputAttrib);        
       */
      if (inputAttrib.sourceQuery()) {
        // attribute whose value needs to be retrieved from data source
        inputVal = retrieveAttributeValue(reportCls, report, inputAttrib);
      } else {
        // normal attribute
        inputVal = reportDctl.getDataFieldValue(inputAttrib); 
      }
      
      /*v3.1: moved to each if brance below to get refClass in case all input attributes have null values
      if (inputVal == null)
        continue; // skip this attribute if no input is specified
      */
      
      if (join.length == 1) {
        // non join definition
        
        // the referenced class: validate it
        //TODO: relax these check conditions using FlexiQuery
        if (refClass == null)
          refClass = join[0];
        else if (refClass != join[0]) {
          // invalid input specification: all input attributes must come from the same class
          throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED,
              new Object[] {reportCls.getSimpleName()});
        }
          
        if (inputVal == null)
          continue; // skip this attribute if no input is specified

        attribFunc = inputDef.reference().attribFunc();

        refAttribName = attribFunc.attrib(); // one attribute
        refAttrib = schema.getDsm().getDomainConstraint(refClass, refAttribName);
        op = attribFunc.operator();
        func = attribFunc.function();
        if (func.isNil()) {
          // just the attribute, no function
          exp = new ObjectExpression(refClass, null, refAttrib, op, inputVal);
        } else {
          // attribute and function
          exp = new ObjectExpression(refClass, attribFunc, refAttrib, op, inputVal);
        }
        
        njExps.add(exp);
      } else {
        // a join definition: add it to the query
        
        // the domain class on which the join expression is applied is the first class in the join
        // the referenced class: validate it
        //TODO: relax these check conditions using FlexiQuery
        if (refClass == null) {
          if (!njExps.isEmpty()) {
            // invalid input specification: join expression must come after other expressions
            throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
                new Object[] { reportCls.getSimpleName()});
          } else {
            // no non-join expressions: 
            refClass = join[0];
          }
        } else if (refClass != join[0]) {
          // invalid input specification: all input attributes must come from the same class
          throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED,
              new Object[] {reportCls.getSimpleName()});
        }
        
        if (inputVal == null)
          continue; // skip this attribute if no input is specified
        
        // the join definition defined on the attribute
        joinDef = inputDef.reference();
        exp = QueryToolKit.createJoinExpressionWithValueConstraint(dsm, joinDef, inputVal);
        
        q.add(exp);
      }
    } // end for
    
    // finally add non-join expressions (if any) to query
    
    if (!njExps.isEmpty()) {
      for (ObjectExpression ex : njExps) q.add(ex);
    }
    
    return refClass;
  }
}
