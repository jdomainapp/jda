package org.jda.example.restfstool.rfsgen;

import com.hanu.courseman.SCCCourseMan;
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

import jda.modules.restfstool.backend.BESpringApp;
import jda.modules.restfstool.config.GenerationMode;
import jda.modules.restfstool.config.LangPlatform;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class Resources {
//initialize the model
  public static final Class<?>[] model = {
          CourseModule.class,
          Enrolment.class,
          Student.class,
          Address.class,
          StudentClass.class,
          CompulsoryModule.class,
          ElectiveModule.class
  };
  
  public static final Class<?>[] auxModel = {
      Gender.class
  };
  
  // initialize module classes
  // one module per INHERITANCE TREE
  public static final Class<?>[] modules = {
          ModuleCourseModule.class,
          ModuleEnrolment.class,
          ModuleStudent.class,
          ModuleAddress.class,
          ModuleStudentClass.class
  };


  public static final String frontendOutputPath = "src/example/java/com/hanu/courseman/frontend";
  public static final String backendTargetPackage = "com.hanu.courseman.backend";
  public static final String backendOutputPath = "src/example/java";
  public static final LangPlatform langPlatform = LangPlatform.SPRING;
  public static final GenerationMode genMode = GenerationMode.SOURCE_CODE;
  
  public static final Class<?> scc = 
      //SCC1.class;
      SCCCourseMan.class;
  
  public static final Class<?> mccMain = ModuleMain.class;
//  public static final CourseManBackendApp runCallBack = new CourseManBackendApp(model);
  public static final Class<? extends BESpringApp> backEndAppCls = CourseManBESpringApp.class;

}
