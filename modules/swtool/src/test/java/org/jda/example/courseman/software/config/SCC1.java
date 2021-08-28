package org.jda.example.courseman.software.config;

import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.osm.postgresql.PostgreSQLOSM;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.sccl.syntax.DSDesc;
import org.jda.example.courseman.software.ModuleMain;
import org.jda.example.courseman.modules.ModuleEnrolment1;
import org.jda.example.courseman.modules.ModuleElectiveModule1;
import org.jda.example.courseman.modules.ModuleCourseModule1;
import org.jda.example.courseman.modules.ModuleCompulsoryModule1;
import org.jda.example.courseman.modules.ModuleAddress1;
import org.jda.example.courseman.modules.ModuleStudent1;
import org.jda.example.courseman.modules.ModuleStudent2;
import org.jda.example.courseman.modules.ModuleEnrolment2;
import org.jda.example.courseman.modules.ModuleElectiveModule2;
import org.jda.example.courseman.modules.ModuleCourseModule2;
import org.jda.example.courseman.modules.ModuleAddress2;
import org.jda.example.courseman.modules.ModuleCompulsoryModule2;
import jda.modules.setup.model.SetUpConfig;
import jda.modules.sccl.syntax.SysSetUpDesc;
import jda.modules.sccl.syntax.SecurityDesc;

@SystemDesc(appName = "CourseMan", splashScreenLogo = "CourseManlogo.jpg", language = Language.English, orgDesc = @OrgDesc(name = "COURSEMAN", address = "", logo = "CourseMan.png", url = "http://CourseMan.com"), dsDesc = @DSDesc(type = "postgresql", dsUrl = "//localhost:5432/coursemands", user = "user", password = "password", dsmType = DSM.class, domType = DOM.class, osmType = PostgreSQLOSM.class, connType = ConnectionType.Client), modules = { ModuleMain.class, ModuleEnrolment1.class, ModuleElectiveModule1.class, ModuleCourseModule1.class, ModuleCompulsoryModule1.class, ModuleAddress1.class, ModuleStudent1.class, ModuleStudent2.class, ModuleEnrolment2.class, ModuleElectiveModule2.class, ModuleCourseModule2.class, ModuleAddress2.class, ModuleCompulsoryModule2.class }, sysModules = {}, setUpDesc = @SysSetUpDesc(setUpConfigType = SetUpConfig.class), securityDesc = @SecurityDesc(isEnabled = false))
public class SCC1 {
}
