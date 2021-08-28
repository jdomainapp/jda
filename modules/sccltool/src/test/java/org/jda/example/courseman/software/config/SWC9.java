package org.jda.example.courseman.software.config;

import org.jda.example.courseman.ModuleMain;
import org.jda.example.courseman.mccl.modules.ModuleAddress;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.OSM;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.sccl.syntax.DSDesc;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.sccl.syntax.SecurityDesc;
import jda.modules.sccl.syntax.SysSetUpDesc;
import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.setup.model.SetUpConfig;

@SystemDesc(appName = "Courseman", splashScreenLogo = "coursemanapplogo.jpg", language = Language.English, orgDesc = @OrgDesc(name = "Faculty of IT", address = "K1m9 Nguyen Trai Street, Thanh Xuan District", logo = "hanu.gif", url = "http://localhost:5432/domains"), dsDesc = @DSDesc(type = "postgresql", dsUrl = "http://localhost:5432/domains", user = "admin", password = "password", dsmType = DSM.class, domType = DOM.class, osmType = OSM.class, connType = ConnectionType.Client), modules = { ModuleMain.class, ModuleAddress.class }, sysModules = {}, setUpDesc = @SysSetUpDesc(setUpConfigType = SetUpConfig.class), securityDesc = @SecurityDesc(isEnabled = false))
public class SWC9 {
}
