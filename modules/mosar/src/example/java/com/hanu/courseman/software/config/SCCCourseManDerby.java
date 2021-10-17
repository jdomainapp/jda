package com.hanu.courseman.software.config;

import com.hanu.courseman.modules.ModuleMain;
import com.hanu.courseman.modules.address.ModuleAddress;
import com.hanu.courseman.modules.coursemodule.ModuleCourseModule;
import com.hanu.courseman.modules.enrolment.ModuleEnrolment;
import com.hanu.courseman.modules.student.ModuleStudent;
import com.hanu.courseman.modules.studentclass.ModuleStudentClass;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.javadb.JavaDbOSM;
import jda.modules.dodm.osm.postgresql.PostgreSQLOSM;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.mosar.config.GenerationMode;
import jda.modules.mosar.config.LangPlatform;
import jda.modules.mosar.config.RFSGenDesc;
import jda.modules.mosar.config.StackSpec;
import jda.modules.mosarbackend.springboot.BESpringApp;
import jda.modules.mosarfrontend.reactjs.FEReactApp;
import jda.modules.sccl.syntax.DSDesc;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.sccl.syntax.SecurityDesc;
import jda.modules.sccl.syntax.SysSetUpDesc;
import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.setup.model.SetUpConfig;

@RFSGenDesc(
  stackSpec = 
//      StackSpec.FE, // for testing FE
    StackSpec.FS, // for production
//      StackSpec.BE, // for testing BE
  genMode = GenerationMode.SOURCE_CODE, 
  beLangPlatform = LangPlatform.SPRING,
  feProjPath = "/home/ducmle/tmp/restfstool-fe",
  feProjName = "fe-courseman",
  feProjResource = "src/main/resources/react",
  feOutputPath = "src/example/java/com/hanu/courseman/frontend",
  feServerPort = 5000,  // default: 3000
  feAppClass=FEReactApp.class,
  feThreaded = true,
  bePackage = "com.hanu.courseman.modules",
  beOutputPath = "src/example/java", 
  beTargetPackage = "com.hanu.courseman.backend"
  ,beAppClass = BESpringApp.class,
  beServerPort = 8080  // default: 8080
    //CourseManBESpringApp.class
)
@SystemDesc(
        appName = "Courseman",
        splashScreenLogo = "coursemanapplogo.jpg",
        language = Language.English,
        orgDesc = @OrgDesc(name = "Faculty of IT",
                address = "Hanoi, Vietnam",
                logo = "hanu.gif",
                url = "http://swinburne.edu.vn"),
        dsDesc = @DSDesc(
                type = "derby",
                dsUrl = "data/domainds",
                user = "admin",
                password = "password",
                dsmType = DSM.class,
                domType = DOM.class,
                osmType = JavaDbOSM.class,
                connType = ConnectionType.Embedded),
        modules = {
                ModuleMain.class,
                ModuleCourseModule.class,
                ModuleEnrolment.class,
                ModuleStudent.class,
                ModuleAddress.class,
                ModuleStudentClass.class
        },
        sysModules = {},
        setUpDesc = @SysSetUpDesc(setUpConfigType = SetUpConfig.class),
        securityDesc = @SecurityDesc(isEnabled = false))
public class SCCCourseManDerby {
}
