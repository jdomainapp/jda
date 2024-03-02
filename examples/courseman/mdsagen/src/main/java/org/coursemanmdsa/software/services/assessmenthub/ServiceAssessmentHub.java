package org.coursemanmdsa.software.services.assessmenthub;

import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.tmsa.tasl.syntax.ServiceDesc;
import org.coursemanmdsa.software.services.coursemgnt.modules.coursemodule.ModuleCourseModule;
import org.coursemanmdsa.software.services.coursemgnt.modules.enrolment.ModuleEnrolment;
import org.coursemanmdsa.software.services.coursemgnt.modules.student.ModuleStudent;
import org.coursemanmdsa.software.services.assessmenthub.modules.assessmenthub.ModuleAssessmentHub;

@ServiceDesc(
        name = "assessmenthub-service",
        description = "Service: AssessmentHub",
        port = 8090,
        serviceTree = @CTree(
                root = ModuleAssessmentHub.class,
                edges = {
                        @CEdge(parent = ModuleAssessmentHub.class, child = ModuleStudent.class),
                        @CEdge(parent = ModuleAssessmentHub.class, child = ModuleEnrolment.class),
                        @CEdge(parent = ModuleEnrolment.class, child = ModuleCourseModule.class),
//                        @CEdge(parent = ModuleAssessmentHub.class, child = ModuleTeacher.class), // TODO: enable after completing ModuleTeacher
                }
        )
)
public class ServiceAssessmentHub extends ModuleAssessmentHub {
}
