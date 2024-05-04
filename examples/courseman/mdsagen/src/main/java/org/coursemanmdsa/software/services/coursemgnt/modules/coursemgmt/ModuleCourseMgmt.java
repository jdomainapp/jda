package org.coursemanmdsa.software.services.coursemgnt.modules.coursemgmt;


import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import org.coursemanmdsa.software.services.coursemgnt.modules.coursemgmt.model.CourseMgmt;

@ModuleDescriptor(
        name = "ModuleCourseMgmt",
        modelDesc = @ModelDesc(
                model = CourseMgmt.class
        )
)
public class ModuleCourseMgmt {
}
