package org.coursemanmdsa.software.services.coursemgnt.modules.studentenrolment;


import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import org.coursemanmdsa.software.services.coursemgnt.modules.studentenrolment.model.StudentEnrolment;

@ModuleDescriptor(
        name = "studentenrolment",
        modelDesc = @ModelDesc(
                model = StudentEnrolment.class
        )
)
public class ModuleStudentEnrolment {
}
