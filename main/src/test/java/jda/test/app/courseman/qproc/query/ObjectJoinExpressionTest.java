package jda.test.app.courseman.qproc.query;

import org.junit.Test;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.ObjectJoinExpression;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;

public class ObjectJoinExpressionTest extends CourseManBasicTester {
  @Test
  public void doTest() throws Exception {
    
    Class sclassCls = SClass.class;
    Class studentCls = Student.class;
    
    registerClass(sclassCls);
    registerClass(studentCls);
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    DAttr clsName = schema.getDomainConstraint(sclassCls, "name");
    Op op = Op.MATCH;
    ObjectExpression exp2 = new ObjectExpression(sclassCls, clsName, op, "2014");

    System.out.printf("Class expression: %s%n", exp2);
    
    DAttr clsAttrib = schema.getDomainConstraint(studentCls,
        "sclass");
    op = Op.EQ;
    ObjectJoinExpression exp1 = new ObjectJoinExpression(studentCls, clsAttrib, op,
        exp2);

    System.out.printf("Student expression: %s%n", exp1);

  }
}
