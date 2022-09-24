package org.jda.example.coursemanrestful.software.config;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.javadb.JavaDbOSM;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.mosar.config.*;
import jda.modules.mosar.software.frontend.FEApp;
import jda.modules.mosarbackend.springboot.BESpringApp;
import jda.modules.sccl.syntax.*;
import jda.modules.setup.model.SetUpConfig;
import org.jda.example.coursemanrestful.modules.ModuleMain;
import org.jda.example.coursemanrestful.modules.address.ModuleAddress;
import org.jda.example.coursemanrestful.modules.coursemodule.ModuleCourseModule;
import org.jda.example.coursemanrestful.modules.enrolment.ModuleEnrolment;
import org.jda.example.coursemanrestful.modules.student.ModuleStudent;
import org.jda.example.coursemanrestful.modules.studentclass.ModuleStudentClass;


@RFSGenDesc(
        stackSpec = StackSpec.FE,
        execSpec = ExecSpec.Gen,
        genMode = GenerationMode.SOURCE_CODE,
        feProjPath = "/home/ducmle/tmp/restfstool-fe",
        feProjName = "fe-courseman",
        feProjResource = "src/main/resources/angular",
        fePlatform = FEPlatform.VUE_JS,
        feOutputPath = "E:\\Linh\\JDA\\FEVue",
        feServerPort = 5000,  // default: 3000
        feAppClass = FEApp.class,
        feThreaded = true,
        beLangPlatform = LangPlatform.SPRING,
        bePackage = "org.jda.example.coursemanrestful.modules",
        beOutputPath = "E:\\Linh\\JDA\\jda\\examples\\courseman\\mosar\\src\\main\\java",
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




