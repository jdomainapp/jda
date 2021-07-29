package jda.modules.ds.viewable;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.Selectx;
import jda.modules.dcsl.syntax.function.AttribFunctor;
import jda.modules.dcsl.syntax.report.Input;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.ds.function.DataFunction;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.modules.report.model.stats.StatCount;
import jda.modules.report.model.stats.StatisticSpec;

/**
 * @overview
 *  A {@link JSingleValueDataSource} that provides access to count statistics about objects of 
 *  a given domain class. 
 * 
 * <p>The data source runs a query whose terms are specified by a statistical specification class.
 * This class is itself a domain class, whose attribute specification defines the query terms. 
 * More than one queries can be defined in the same class simply by adding more attributes.
 * 
 * <p>For example:<pre>
 * Query: 
 *    all Person.id where year(Person.dob) <= currentYear()-40
 * 
 * Attribute specification:
 *    Class: Person.class
 *    AttributeFunctor: (function,attrib,operator) = (year,Person.dob,Op.LTEQ)
 *    val: currentYear()-40
 * </pre>
 *
 * <p>To special this class for different statistical query requires creating sub-types, whose implementations 
 * simply implement the {@link #getStatsName()} method to return the name of the statistical attribute concerned.
 * 
 * <p>The query is executed to count the number of matching object ids that are returned. This number 
 * is the expected statistical output.
 * 
 * <p>To ease the display of the output the statistical name and output value are captured in an object 
 * of a class named {@link StatCount}. The output is displayed by binding this class to a data field (
 * typically text field).
 * 
 * @author dmle
 *
 */
public class StatCountDataSource extends JSingleValueDataSource {

  private StatisticSpec statObj;

  public StatCountDataSource(DODMBasic dodm, Class domainClass) {
    super(dodm,domainClass);
    
    // register the statistic spec class (if not already)
    //Class statsSpecClass = getStatisticalSpecInstance().getClass();
    //if (!dodm.isRegistered(statsSpecClass)) dodm.registerClass(statsSpecClass);
    
    if (!dodm.isRegistered(domainClass)) dodm.registerClass(domainClass);
  }
  
  @Override
  protected Object loadObject() throws NotPossibleException {
    DODMBasic dodm = getDodm();
    DSMBasic dsm = dodm.getDsm();
    
    // the domain class the domain objects of whom are used to generate the statistics
    // captured by this data source
    Class statCountCls = getDomainClass();
    
    //System.out.println("domain class: " + cls);
    
    /*v2.7.4: moved to abstract method
    Class statSpecClass = ReportPersonStatistics.StatisticsSpec.class;
    ReportPersonStatistics.StatisticsSpec statisSpec = ReportPersonStatistics.StatisticsSpec.getInstance();
    */ 
//    StatisticSpec statisSpec = getStatisticalSpecInstance();
//    Class statSpecClass = statisSpec.getClass();

    // also the statistical spec class
    Class<? extends StatisticSpec> statSpecClass = statCountCls;
    if (statObj == null) {
      statObj = dsm.newInstance(statSpecClass);
    }
    
    DAttr boundedAttrib = getBoundedAttribute();
    
    // change this name for different types of statistics
    String statsName = boundedAttrib.name(); //getStatsName(); 
    
    // invokes statisSpec.getX() method for statsName
    Object statsInputVal = dsm.getAttributeValue(statObj, statsName); 
    
    Class<Input> INPUT = Input.class;
    Input statSpec = dsm.getDomainAttributeAnnotation(statSpecClass, INPUT, statsName);
    
    if (statSpec == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, 
          "Không tìm thấy định nghĩa phụ chú cho {0}", statSpecClass.getSimpleName()+"."+statsName);
    }

    Class dataClass = statObj.getDomainClass();
    
    // v2.7.4: support two cases, either (1) reference OR (2) refFunction
    int statsVal; 
    
    Selectx inputSpec = statSpec.reference();
    if (inputSpec != null) {
      // case (1): reference 
      Class[] joinDef = inputSpec.classJoin();
      
      /* 
       * run an object query to get the statistics
       **/
      Query query;
      if (joinDef.length == 1) { 
        // non-join query
        AttribFunctor attribFunc = statSpec.reference().attribFunc(); 
        DAttr attrib = dsm.getDomainConstraint(dataClass, attribFunc.attrib());
        Op op = attribFunc.operator();

        // the query
        query = new Query<ObjectExpression>(
            new ObjectExpression(dataClass, attribFunc, attrib, op, statsInputVal)
        );
      } else {
        // join query
        query = new Query(
            QueryToolKit.createJoinExpressionWithValueConstraint(dsm, inputSpec, statsInputVal));
      }
      
      // run the query and count
      statsVal = dodm.getDom().loadObjectCount(dataClass, query);
    } else {
      // case (2):
      // create the function instance
      Class<? extends DataFunction> inputFuncCls = statSpec.refFuncClass();
      DataFunction func = DataFunction.createInstance(inputFuncCls, dodm);
      
      // execute function for the specified attribute to obtain result
      Object result = func.perform(statsName);
      if (result != null) {
        statsVal = (Integer) result;
      } else {
        statsVal = 0;
      }
    }

    // return StatCount object from the result
    StatCount statCount = (StatCount) dsm.newInstance(statCountCls, new Object[] {statsName, statsVal});//new StatCount(statsName, statsVal);
    return statCount;
  }
}
