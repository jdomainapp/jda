package org.coursemanmdsa.software.services.coursemgnt.modules.coursemodulemgmt;


import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import org.coursemanmdsa.software.services.coursemgnt.modules.coursemodulemgmt.model.CourseModuleMgmt;

@ModuleDescriptor(
        name = "ModuleCourseModuleMgmt",
        modelDesc = @ModelDesc(
                model = CourseModuleMgmt.class
        )
)
public class ModuleCourseModuleMgmt {
}
