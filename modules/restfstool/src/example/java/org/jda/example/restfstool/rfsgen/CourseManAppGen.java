package org.jda.example.restfstool.rfsgen;

import java.util.List;
import java.util.function.Consumer;

import com.hanu.courseman.SCC1;
import com.hanu.courseman.modules.ModuleMain;
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

import jda.modules.restfstool.RFSGen;

/**
 * The software generator for CourseManApp.
 * @author binh_dh
 */
public class CourseManAppGen {
    // initialize the model
    static final Class<?>[] model = {
            CourseModule.class,
            Enrolment.class,
            Student.class,
            Address.class,
            StudentClass.class,
            CompulsoryModule.class,
            ElectiveModule.class
    };
    
    static final Class<?>[] auxModel = {
        Gender.class
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

    
    public static void main(String[] args) {
      final String frontendOutputPath = "src/example/java/com/hanu/courseman/frontend";
      final String backendTargetPackage = "com.hanu.courseman.backend";
      final String backendOutputPath = "src/example/java";
      final Class<?> scc = SCC1.class;
      final Class<?> mccMain = ModuleMain.class;
      final Consumer<List<Class>> runCallBack = new BackendMain(model);
      
      new RFSGen().run(
          // front-end config + some shared configs
          frontendOutputPath, model, auxModel, scc, mccMain, modules, 
          // back-end config
          backendTargetPackage, backendOutputPath, runCallBack);
    }
}
