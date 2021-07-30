package org.jda.example.restfstool.rfsgen.v1_0;

import com.hanu.courseman.modules.address.ModuleAddress;
import com.hanu.courseman.modules.address.model.Address;
import com.hanu.courseman.modules.coursemodule.ModuleCourseModule;
import com.hanu.courseman.modules.coursemodule.model.CompulsoryModule;
import com.hanu.courseman.modules.coursemodule.model.CourseModule;
import com.hanu.courseman.modules.coursemodule.model.ElectiveModule;
import com.hanu.courseman.modules.enrolment.ModuleEnrolment;
import com.hanu.courseman.modules.enrolment.model.Enrolment;
import com.hanu.courseman.modules.student.ModuleStudent;
import com.hanu.courseman.modules.student.model.Gender;
import com.hanu.courseman.modules.student.model.Student;
import com.hanu.courseman.modules.studentclass.ModuleStudentClass;
import com.hanu.courseman.modules.studentclass.model.StudentClass;

import jda.modules.restfstool.frontend.utils.DomainTypeRegistry;

/**
 * The software generator for CourseManApp.
 * @author binh_dh
 */
public class CourseManAppGenerator {
    // initialize the model
    static final Class<?>[] models = {
            CourseModule.class,
            Enrolment.class,
            Student.class,
            Address.class,
            StudentClass.class,
            CompulsoryModule.class,
            ElectiveModule.class
    };
    // initialize module classes
    // one module per INHERITANCE TREE
    static final Class<?>[] modules = {
            ModuleCourseModule.class,
            ModuleEnrolment.class,
            ModuleStudent.class,
            ModuleAddress.class,
            ModuleStudentClass.class
    };

    static {
        DomainTypeRegistry.getInstance().addDomainTypes(models);
        DomainTypeRegistry.getInstance().addDomainType(Gender.class);
    }

//    private static final String frontendOutputPath = "/Users/binh_dh/vscode/courseman-examples-2/src";

    public static void main(String[] args) {
        FrontendGenerator.setupAndGen();
        BackendApp.setupAndRun();
    }
}
