package jda.test.app.courseman.qproc.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.ObjectJoinExpression;
import jda.modules.oql.def.Query;
import jda.test.app.courseman.qproc.TestQProc;
import jda.test.model.basic.Enrolment;
import jda.test.model.basic.Module;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;

public class JoinQueryQProc extends TestQProc {

  @Test
  public void doJoin2Way() throws DataSourceException {
    System.out.printf("%ndoJoin2Way()%n");

    Query query = create2WayJoinQuery();

    executeQuery(query);
  }

  @Test
  public void doJoin3Way() throws DataSourceException {
    System.out.printf("%ndoJoin3Way()%n");
    
    Query query = create3WayJoinQuery();

    executeQuery(query);
  }

  private Query create3WayJoinQuery() throws DataSourceException {
    Class studentCls = Student.class;
    Class enrolCls = Enrolment.class;
    Class moduleCls = Module.class;
    
    registerClass(studentCls);
    registerClass(moduleCls);
    registerClass(enrolCls);
    
    // create a join query
    Query query = new Query();
    
    //DODM schema = instance.getDomainSchema();
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    // test data: Module.name,Student.name pairs
    String[][] pairs = {
        // all enrolments for module matching "Net"
        {"%Net%", "%%"},      
        // all enrolments for module matching "Technology"
        {"%Technology%", "%%"},
        // all enrolments for module matching "Software" and for student matching "Nguyen"
        {"%Software%", "%Nguyen%"},
        // all enrolments
        {"%%","%%"}
    };
    
    String[] testPair = pairs[1];
    String moduleNamePattern = testPair[0];
    String studentNamePattern = testPair[1];
    
    // a module
    DAttr moduleName = schema.getDomainConstraint(moduleCls, "name");
    Op op = Op.MATCH;
    ObjectExpression exp2 = new ObjectExpression(moduleCls, moduleName, op, moduleNamePattern);
    
    System.out.printf("Module expression: %s%n", exp2);

    // a student
    DAttr studentName = schema.getDomainConstraint(studentCls, "name");
    ObjectExpression exp3 = new ObjectExpression(studentCls, studentName, op, studentNamePattern);
    
    System.out.printf("Student expression: %s%n", exp2);

    // join expression: all enrolments matching the specified student and module
    DAttr enroledModule = schema.getDomainConstraint(enrolCls,
        "module");
    DAttr enroledStudent = schema.getDomainConstraint(enrolCls,
        "student");
    
    op = Op.EQ;
    ObjectJoinExpression exp1a = new ObjectJoinExpression(enrolCls, enroledModule, op,
        exp2);
    ObjectJoinExpression exp1b = new ObjectJoinExpression(enrolCls, enroledStudent, op,
        exp3);
    
    
    query.add(exp1a);
    query.add(exp1b);

    return query;  
  }
  
  private Query create2WayJoinQuery() throws DataSourceException {
    Class sclassCls = SClass.class;
    Class studentCls = Student.class;
    
    registerClass(sclassCls);
    registerClass(studentCls);
    
    // create a join query
    Query query = new Query();
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    // test data
    String[] classNames = {
        "%1%",
        "%2%"
    };
    String className = classNames[0];
    
    DAttr clsName = schema.getDomainConstraint(sclassCls, "name");
    Op op = Op.MATCH;
    ObjectExpression exp2 = new ObjectExpression(sclassCls, clsName, op, className);
    
    System.out.printf("Class expression: %s%n", exp2);
    
    DAttr clsAttrib = schema.getDomainConstraint(studentCls,
        "sclass");
    op = Op.EQ;
    ObjectJoinExpression exp1 = new ObjectJoinExpression(studentCls, clsAttrib, op,
        exp2);
    
    query.add(exp1);
    
    return query;  
  }
  
}
