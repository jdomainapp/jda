package jda.modules.patterndom.test.dom.mnormaliser.output;

import jda.modules.dcsl.syntax.DAttr;
import java.util.Collection;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DCSLConstants;

/**
 * @overview Represents Students.
 *           <p>
 *           This class is used to test domain pattern transformation.
 * 
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 */
public class Student {

  private String name;

  @DAttr(name = "courseModule", type = Type.Collection, serialisable = false)
  @DAssoc(ascName = "CourseModule-Student", role = "student", ascType = AssocType.Many2Many, endType = AssocEndType.Many, associate = @Associate(type = CourseModule.class, cardMin = 1, cardMax = DCSLConstants.CARD_MORE), normAttrib = "enrolments")
  private Collection<CourseModule> courseModule;

  @DAttr(name = "enrolments", type = Type.Collection, serialisable = false)
  @DAssoc(ascName = "Enrolment-Student", role = "student", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Enrolment.class, cardMin = 1, cardMax = DCSLConstants.CARD_MORE))
  private Collection<Enrolment> enrolments;
}