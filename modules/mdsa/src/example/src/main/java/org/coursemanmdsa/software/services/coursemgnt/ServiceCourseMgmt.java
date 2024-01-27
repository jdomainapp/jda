package org.coursemanmdsa.software.services.coursemgnt;

import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.tmsa.tasl.syntax.ServiceDesc;
import org.coursemanmdsa.software.services.address.modules.address.ModuleAddress;
import org.coursemanmdsa.software.services.assessmenthub.modules.teacher.ModuleTeacher;
import org.coursemanmdsa.software.services.coursemgnt.modules.coursemgmt.ModuleCourseMgmt;
import org.coursemanmdsa.software.services.coursemgnt.modules.coursemodule.ModuleCompulsoryModule;
import org.coursemanmdsa.software.services.coursemgnt.modules.coursemodule.ModuleCourseModule;
import org.coursemanmdsa.software.services.coursemgnt.modules.coursemodule.ModuleElectiveModule;
import org.coursemanmdsa.software.services.coursemgnt.modules.coursemodulemgmt.ModuleCourseModuleMgmt;
import org.coursemanmdsa.software.services.coursemgnt.modules.student.ModuleStudent;
import org.coursemanmdsa.software.services.coursemgnt.modules.studentenrolment.ModuleStudentEnrolment;

@ServiceDesc(
        name = "coursemgmt-service",
        description = "Service: Course Management",
//        gateway = "http://gateway-courseman",
        port = 8089,
        serviceTree = @CTree(
                root = ModuleCourseMgmt.class,
                edges = {
                        @CEdge(parent = ModuleCourseMgmt.class, child = ModuleAddress.class),
//                        @CEdge(parent = ModuleCourseMgmt.class, child = ModuleCourseModuleMgmt.class),
                        @CEdge(parent = ModuleCourseMgmt.class, child = ModuleStudentEnrolment.class),
//
                        @CEdge(parent = ModuleCourseModuleMgmt.class, child = ModuleCourseModule.class),
                        @CEdge(parent = ModuleCourseModuleMgmt.class, child = ModuleCompulsoryModule.class),
                        @CEdge(parent = ModuleCourseModuleMgmt.class, child = ModuleElectiveModule.class),

                        @CEdge(parent = ModuleCourseModuleMgmt.class, child = ModuleTeacher.class),
//
                        @CEdge(parent = ModuleStudentEnrolment.class, child = ModuleStudent.class),
//                        @CEdge(parent = ModuleStudentEnrolment.class, child = ModuleEnrolment.class),
                }
        )
)
public class ServiceCourseMgmt extends ModuleCourseMgmt {
}
