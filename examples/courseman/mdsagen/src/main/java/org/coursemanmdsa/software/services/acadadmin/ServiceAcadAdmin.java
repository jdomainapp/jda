package org.coursemanmdsa.software.services.acadadmin;

import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.tmsa.tasl.syntax.ServiceDesc;
import org.coursemanmdsa.software.services.acadadmin.modules.acadadmin.ModuleAcadAdmin;
import org.coursemanmdsa.software.services.coursemgnt.ServiceCourseMgmt;

@ServiceDesc(
        name = "acadadmin-service",
        description = "Academic Administration Service",
        port = 8087,
        serviceTree = @CTree(
                root = ServiceAcadAdmin.class,
                edges = {
                        @CEdge(parent = ServiceAcadAdmin.class, child = ServiceCourseMgmt.class),
//                        @CEdge(parent = ServiceCourseMgmt.class, child = ModuleA.class),
//                        @CEdge(parent = ModuleX.class, child = ServiceX.class),
//                        @CEdge(parent = ModuleY.class, child = ServiceY.class),
//                        @CEdge(parent =  ModuleAcadAdmin.class, child = ServiceAssessmentHub.class)
                }
        )
)
public class ServiceAcadAdmin extends ModuleAcadAdmin {
}
