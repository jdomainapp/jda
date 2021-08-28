package org.jda.example.courseman.config;

import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.osm.postgresql.PostgreSQLOSM;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.sccl.syntax.DSDesc;
import org.jda.example.courseman.ModuleMain;
import org.jda.example.courseman.mccl.modules.ModuleAddress;
import jda.modules.setup.model.SetUpConfig;
import jda.modules.sccl.syntax.SysSetUpDesc;
import jda.modules.sccl.syntax.SecurityDesc;

@SystemDesc(appName = "CourseMan", splashScreenLogo = "CourseManlogo.jpg", language = Language.English, orgDesc = @OrgDesc(name = "COURSEMAN", address = "", logo = "CourseMan.png", url = "http://CourseMan.com"), dsDesc = @DSDesc(type = "postgresql", dsUrl = "//localhost:5432/coursemands", user = "user", password = "password", dsmType = DSM.class, domType = DOM.class, osmType = PostgreSQLOSM.class, connType = ConnectionType.Client), modules = { ModuleMain.class, ModuleAddress.class }, sysModules = {}, setUpDesc = @SysSetUpDesc(setUpConfigType = SetUpConfig.class), securityDesc = @SecurityDesc(isEnabled = false))
public class SCC3 {
}
