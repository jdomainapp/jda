package org.coursemanmdsa.software.config;

import jda.modules.tmsa.tasl.syntax.MDSGenDesc;
import org.coursemanmdsa.software.services.address.ServiceAddress;

@MDSGenDesc(
        name = "CoursemanMdsa",
        description = "Example: Generated: Courseman Microservice App",
        outputPackage = "org.courseman.coursemanmdsa",
        outputPath = "E:\\jda-f90\\jda\\modules\\mdsa\\src\\example",
        modelsPath = "E:\\jda-f90\\jda\\modules\\mdsa\\src\\example\\src\\main\\java\\org\\coursemanmdsa\\models",
        mccServices = {
//                ServiceAcadAdmin.class,
//                ServiceCourseModuleMgmt.class,
//                ServiceAssessmentHub.class,A
//                ServiceFinancialHub.class,
//                ServiceCourseMgmt.class,
                ServiceAddress.class
        }
)
public class SCCCourseMan {
}
