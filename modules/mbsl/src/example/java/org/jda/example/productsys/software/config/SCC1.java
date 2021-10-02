package org.jda.example.productsys.software.config;

import org.jda.example.productsys.services.machine.modules.ModuleAssembler;
import org.jda.example.productsys.services.machine.modules.ModuleGenBars;
import org.jda.example.productsys.services.machine.modules.ModuleGenCylinders;
import org.jda.example.productsys.services.machine.modules.ModuleGenerator;
import org.jda.example.productsys.services.machine.modules.ModuleMachine;
import org.jda.example.productsys.services.machine.modules.ModulePackage;
import org.jda.example.productsys.services.machine.modules.ModuleQuality;
import org.jda.example.productsys.services.machine.modules.ModuleRepair;
import org.jda.example.productsys.software.ModuleMain;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.postgresql.PostgreSQLOSM;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.sccl.syntax.DSDesc;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.sccl.syntax.SecurityDesc;
import jda.modules.sccl.syntax.SysSetUpDesc;
import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.setup.model.SetUpConfig;

@SystemDesc(appName = "AGCL", splashScreenLogo = "AGCLlogo.jpg", language = Language.English, orgDesc = @OrgDesc(name = "AGCL", address = "", logo = "AGCL.png", url = "http://AGCL.com"), dsDesc = @DSDesc(type = "postgresql", dsUrl = "//localhost:5432/agclds", user = "user", password = "password", dsmType = DSM.class, domType = DOM.class, osmType = PostgreSQLOSM.class, connType = ConnectionType.Client), modules = { ModuleMain.class, ModuleAssembler.class, ModuleGenBars.class, ModuleGenCylinders.class, ModuleGenerator.class, ModuleMachine.class, ModulePackage.class, ModuleQuality.class, ModuleRepair.class }, sysModules = {}, setUpDesc = @SysSetUpDesc(setUpConfigType = SetUpConfig.class), securityDesc = @SecurityDesc(isEnabled = false))
public class SCC1 {
}
