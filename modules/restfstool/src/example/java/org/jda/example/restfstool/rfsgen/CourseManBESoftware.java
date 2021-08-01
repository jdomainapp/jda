package org.jda.example.restfstool.rfsgen;

import com.hanu.courseman.modules.address.model.Address;
import com.hanu.courseman.modules.coursemodule.model.CompulsoryModule;
import com.hanu.courseman.modules.coursemodule.model.CourseModule;
import com.hanu.courseman.modules.coursemodule.model.ElectiveModule;
import com.hanu.courseman.modules.enrolment.model.Enrolment;
import com.hanu.courseman.modules.student.model.Gender;
import com.hanu.courseman.modules.student.model.Student;
import com.hanu.courseman.modules.studentclass.model.StudentClass;

import jda.modules.restfstool.BackEndSoftware;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManBESoftware {
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
//  static final Class<?>[] modules = {
//          ModuleCourseModule.class,
//          ModuleEnrolment.class,
//          ModuleStudent.class,
//          ModuleAddress.class,
//          ModuleStudentClass.class
//  };

  
  public static void main(String[] args) {
//    final String frontendOutputPath = "src/example/java/com/hanu/courseman/frontend";
    final String backendTargetPackage = "com.hanu.courseman.backend";
//    final String backendOutputPath = "src/example/java";
//    final Class<?> scc = SCC1.class;
//    final Class<?> mccMain = ModuleMain.class;
//    final Consumer<List<Class>> runCallBack = new BackendMain(model);
    
    new BackEndSoftware().run(
        model, auxModel,
        backendTargetPackage);
  }
}
