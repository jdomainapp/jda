package org.jda.example.coursemanrestful.software.config;

import org.courseman.modules.ModuleMain;
import org.courseman.modules.address.ModuleAddress;
import org.courseman.modules.coursemodule.ModuleCourseModule;
import org.courseman.modules.enrolment.ModuleEnrolment;
import org.courseman.modules.student.ModuleStudent;
import org.courseman.modules.studentclass.ModuleStudentClass;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.javadb.JavaDbOSM;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.mosar.config.*;
import jda.modules.mosarbackend.springboot.BESpringApp;
import jda.modules.mosarfrontend.reactjs.FEReactApp;
import jda.modules.sccl.syntax.*;
import jda.modules.setup.model.SetUpConfig;

@RFSGenDesc(
        stackSpec = StackSpec.FE,
        execSpec = ExecSpec.Gen,
        genMode = GenerationMode.SOURCE_CODE,
        beLangPlatform = LangPlatform.SPRING,
        feProjPath = "",
        feProjName = "fe-courseman",
        feProjResource = "src/main/resources/angular",
        fePlatform = FEPlatform.VUE_JS,
        feOutputPath = "D:\\UET_THS\\JDA\\VUEJS",
        feServerPort = 5000,  // default: 3000
        feAppClass = FEReactApp.class,
        feThreaded = true,
        bePackage = "org.jda.example.coursemanrestful.modules",
        beOutputPath = "D:\\JDA\\jda\\examples\\courseman\\mosar\\src\\main\\java",
        beTargetPackage = "org.jda.example.coursemanrestful.backend"
        , beAppClass = BESpringApp.class,
        beServerPort = 8080  // default: 8080
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
