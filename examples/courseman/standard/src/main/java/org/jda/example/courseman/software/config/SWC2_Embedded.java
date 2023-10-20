package org.jda.example.courseman.software.config;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.javadb.JavaDbOSM;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.sccl.syntax.*;
import jda.modules.setup.model.SetUpConfig;
import org.jda.example.courseman.ModuleMain;
import org.jda.example.courseman.modules.ModuleAddress;
import org.jda.example.courseman.modules.ModuleCourseModule;
import org.jda.example.courseman.modules.ModuleEnrolment;
import org.jda.example.courseman.modules.ModuleStudent;

@SystemDesc(appName = "Courseman", splashScreenLogo = "coursemanapplogo.jpg", 
language = Language.English, 
orgDesc = @OrgDesc(name = "Faculty of IT", address = "K1m9 Nguyen Trai Street, Thanh Xuan District", 
logo = "hanu.gif", url = "http://localhost:5432/domains"), 
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
    ModuleMain.class, ModuleAddress.class,
    ModuleStudent.class
    ,ModuleEnrolment.class
    ,ModuleCourseModule.class
}, 
sysModules = {}, 
    setUpDesc = @SysSetUpDesc(setUpConfigType = SetUpConfig.class), 
    securityDesc = @SecurityDesc(isEnabled = false))
public class SWC2_Embedded {
}
