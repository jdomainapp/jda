package org.jda.example.coursemansw.software.config;

import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.osm.postgresql.PostgreSQLOSM;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.sccl.syntax.DSDesc;
import org.jda.example.coursemansw.software.ModuleMain;
import org.jda.example.coursemansw.services.coursemodule.ModuleElectiveModule;
import org.jda.example.coursemansw.services.coursemodule.ModuleCompulsoryModule;
import org.jda.example.coursemansw.services.address.ModuleAddress;
import org.jda.example.coursemansw.services.student.ModuleStudent;
import org.jda.example.coursemansw.services.coursemodule.ModuleCourseModule;
import org.jda.example.coursemansw.services.studentclass.ModuleStudentClass;
import org.jda.example.coursemansw.services.enrolment.ModuleEnrolment;
import jda.modules.setup.model.SetUpConfig;
import jda.modules.sccl.syntax.SysSetUpDesc;
import jda.modules.sccl.syntax.SecurityDesc;

@SystemDesc(appName = "CourseMan", splashScreenLogo = "CourseManlogo.jpg", language = Language.English, orgDesc = @OrgDesc(name = "COURSEMAN", address = "", logo = "CourseMan.png", url = "http://CourseMan.com"), dsDesc = @DSDesc(type = "postgresql", dsUrl = "//localhost:5432/coursemands", user = "admin", password = "password", dsmType = DSM.class, domType = DOM.class, osmType = PostgreSQLOSM.class, connType = ConnectionType.Client), modules = { ModuleMain.class, ModuleElectiveModule.class, ModuleCompulsoryModule.class, ModuleAddress.class, ModuleStudent.class, ModuleCourseModule.class, ModuleStudentClass.class, ModuleEnrolment.class }, sysModules = {}, setUpDesc = @SysSetUpDesc(setUpConfigType = SetUpConfig.class), securityDesc = @SecurityDesc(isEnabled = false))
public class SCC1 {
}
