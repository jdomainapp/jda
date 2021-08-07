package jda.modules.patterndom.test.dom.mnormaliser.output;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.patterndom.test.dom.mnormaliser.CourseModule;
import jda.modules.patterndom.test.dom.mnormaliser.Student;

@DClass()
public class Enrolment {

   @DAttr(name = "student", type = Type.Domain, optional = false)
   @DAssoc(ascName = "Enrolment-Student", role = "enrolment", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1))
   private Student student;

   @DAttr(name = "courseModule", type = Type.Domain, optional = false)
   @DAssoc(ascName = "CourseModule-Enrolment", role = "enrolment", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = CourseModule.class, cardMin = 1, cardMax = 1))
   private CourseModule courseModule;
}
