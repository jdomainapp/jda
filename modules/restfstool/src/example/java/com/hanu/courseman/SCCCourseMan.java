package com.hanu.courseman;

import org.jda.example.restfstool.rfsgen.CourseManBESpringApp;

import com.hanu.courseman.modules.ModuleMain;
import com.hanu.courseman.modules.address.ModuleAddress;
import com.hanu.courseman.modules.coursemodule.ModuleCourseModule;
import com.hanu.courseman.modules.enrolment.ModuleEnrolment;
import com.hanu.courseman.modules.student.ModuleStudent;
import com.hanu.courseman.modules.studentclass.ModuleStudentClass;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.postgresql.PostgreSQLOSM;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.restfstool.config.GenerationMode;
import jda.modules.restfstool.config.LangPlatform;
import jda.modules.restfstool.config.RFSGenDesc;
import jda.modules.restfstool.config.StackSpec;
import jda.modules.sccl.syntax.DSDesc;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.sccl.syntax.SecurityDesc;
import jda.modules.sccl.syntax.SysSetUpDesc;
import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.setup.model.SetUpConfig;

@RFSGenDesc(
    stackSpec = 
      StackSpec.FE, // for testing FE
//      StackSpec.FS, // for production
//      StackSpec.BE, // for testing BE
    genMode = GenerationMode.SOURCE_CODE, 
    beLangPlatform = LangPlatform.SPRING,
    feProjPath = "/home/ducmle/tmp/restfstool-fe",
    feProjName = "fe-courseman",
    feProjResource = "src/main/resources/react",
    feOutputPath = "src/example/java/com/hanu/courseman/frontend",
    bePackage = "com.hanu.courseman",
    beOutputPath = "src/example/java", 
    beTargetPackage = "com.hanu.courseman.backend"
    ,beAppClass = CourseManBESpringApp.class
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
                type = "postgresql",
                dsUrl = "//localhost:5432/domainds",
                user = "admin",
                password = "password",
                dsmType = DSM.class,
                domType = DOM.class,
                osmType = PostgreSQLOSM.class,
                connType = ConnectionType.Client),
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
public class SCCCourseMan {
}
