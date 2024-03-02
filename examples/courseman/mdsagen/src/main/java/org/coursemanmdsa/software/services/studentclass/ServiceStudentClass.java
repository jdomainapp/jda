package org.coursemanmdsa.software.services.studentclass;

import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.tmsa.tasl.syntax.ServiceDesc;
import org.coursemanmdsa.software.services.studentclass.modules.studentclass.ModuleStudentClass;
import org.coursemanmdsa.software.services.acadadmin.modules.acadadmin.ModuleAcadAdmin;

@ServiceDesc(
        name = "class-service",
        description = "Class Service",
        port = 8083,
        serviceTree = @CTree(
                root = ModuleStudentClass.class
                // other modules
        )
)
public class ServiceStudentClass extends ModuleAcadAdmin {
}
