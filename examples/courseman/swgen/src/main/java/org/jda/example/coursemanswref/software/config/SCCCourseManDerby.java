package org.jda.example.coursemanswref.software.config;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.javadb.JavaDbOSM;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.sccl.syntax.*;
import jda.modules.setup.model.SetUpConfig;
import org.jda.example.coursemanswref.modules.ModuleMain;
import org.jda.example.coursemanswref.modules.address.ModuleAddress;
import org.jda.example.coursemanswref.modules.coursemodule.ModuleCompulsoryModule;
import org.jda.example.coursemanswref.modules.coursemodule.ModuleElectiveModule;
import org.jda.example.coursemanswref.modules.coursemodule.ModuleCourseModule;
import org.jda.example.coursemanswref.modules.enrolment.ModuleEnrolment;
import org.jda.example.coursemanswref.modules.student.ModuleStudent;
import org.jda.example.coursemanswref.modules.studentclass.ModuleStudentClass;


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
                ModuleCompulsoryModule.class,
                ModuleElectiveModule.class,
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


