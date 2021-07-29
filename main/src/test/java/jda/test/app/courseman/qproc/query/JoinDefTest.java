package jda.test.app.courseman.qproc.query;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.Selectx;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.function.AttribFunctor;
import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dcsl.syntax.report.Input;
import jda.test.model.extended.City;
import jda.test.model.extended.Enrolment;
import jda.test.model.extended.Module;
import jda.test.model.extended.SClass;
import jda.test.model.extended.Student;

public class JoinDefTest {
  
  /**supported join definitions */
  public static class SupportedCourseMan {
    public static final Object[] values = {
      1,
      "%2%",
      10
    };
    
    @Input(reference=@Selectx(
        classJoin={Student.class, SClass.class},
        joinAssocs={"class-has-student"},
        attribFunc=@AttribFunctor(function=Function.nil, attrib="id", operator=Op.EQ)))
    @DAttr(name="testAttrib1",type=Type.Integer)
    private int testAttrib1;
  
    @Input(reference=@Selectx(
        classJoin={Enrolment.class, Student.class, SClass.class},
        joinAssocs={"student-has-enrolments","class-has-student"},
        attribFunc=@AttribFunctor(function=Function.nil, attrib="name", operator=Op.MATCH)))
    @DAttr(name="testAttrib2",type=Type.String)
    private String testAttrib2;
  
    @Input(reference=@Selectx(
        classJoin={Enrolment.class, Student.class, SClass.class},
        joinAssocs={"student-has-enrolments","class-has-student"},
        attribFunc=@AttribFunctor(function=Function.month, attrib="createdDate", operator=Op.EQ)))
    @DAttr(name="testAttrib3",type=Type.Integer)
    private int testAttrib3;
  }
  
  /**supported join definitions */
  public static class Supported {
    public static final Object[] values = {
      1,
      "%2%",
      "%3%",
      8
    };
    
    @Input(reference=@Selectx(
        classJoin={Student.class, SClass.class},
        joinAssocs={"class-has-student"},
        attribFunc=@AttribFunctor(function=Function.nil, attrib="id", operator=Op.EQ)))
    @DAttr(name="testAttrib1",type=Type.Integer)
    private int testAttrib1;
  
    @Input(reference=@Selectx(
        classJoin={Enrolment.class, Student.class, SClass.class},
        joinAssocs={"student-has-enrolments", "class-has-student"},
        attribFunc=@AttribFunctor(function=Function.nil, attrib="name", operator=Op.MATCH)))
    @DAttr(name="testAttrib2",type=Type.String)
    private String testAttrib2;
  
    @Input(reference=@Selectx(
        classJoin={City.class, Student.class, SClass.class},
        joinAssocs={"student-has-city", "class-has-student"},
        attribFunc=@AttribFunctor(function=Function.nil, attrib="name", operator=Op.MATCH)))
    @DAttr(name="testAttrib3",type=Type.String)
    private String testAttrib3;
    
    @Input(reference=@Selectx(
        classJoin={City.class, Student.class, SClass.class},
        joinAssocs={"student-has-city", "class-has-student"},
        attribFunc=@AttribFunctor(function=Function.month, attrib="createdDate", operator=Op.EQ)))
    @DAttr(name="testAttrib4",type=Type.Integer)
    private int testAttrib4;
  }
  
  /** not suported join definitions: contains 1-M associations */
  public static class NotSupported {
    public static final Object[] values = {
      "X",
      "Y",
    };
    
    @Input(reference=@Selectx(
        classJoin={SClass.class, Student.class},
        joinAssocs={"class-has-student"},      
        attribFunc=@AttribFunctor(function=Function.nil, attrib="name", operator=Op.MATCH)))
    @DAttr(name="testAttribX",type=Type.String)
    private String testAttribX;
    
    @Input(reference=@Selectx(
        classJoin={Module.class, Enrolment.class, Student.class, SClass.class},
        joinAssocs={"module-has-enrolments", "student-has-enrolments", "class-has-student"},      
        attribFunc=@AttribFunctor(function=Function.nil, attrib="name", operator=Op.MATCH)))
    @DAttr(name="testAttribY",type=Type.String)  
    private String testAttribY;
  }
}
