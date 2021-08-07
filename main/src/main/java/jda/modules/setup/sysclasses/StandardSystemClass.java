package jda.modules.setup.sysclasses;

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

/**
 * @overview 
 *  A system class for software that has its entire data base (incl. configuration and data) stored in 
 *  a pre-defined type of relational database.
 *   
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0 
 */
@SystemDesc(
    appName="StandardDomainApp",
    splashScreenLogo="domainapp.jpg",
    language=Language.English,
    orgDesc=@OrgDesc(
        name="My Organisation",
        address="My Address", 
        logo="domainapp.jpg", 
        url="http://myorg.vn"
    ), 
    dsDesc=@DSDesc(
        type="postgresql", 
        dsUrl="//localhost:5432/domainds", 
        user="admin",
        password="password",
        dsmType=DSM.class,
        domType=DOM.class,
        osmType=PostgreSQLOSM.class,
        connType=ConnectionType.Client
    ), 
    // empty: to be specified from command line
    modules={}, 
    sysModules={}, 
    setUpDesc=@SysSetUpDesc(
      setUpConfigType=SetUpConfig.class
    ),
    securityDesc=@SecurityDesc(
      isEnabled=false
    )
)
public class StandardSystemClass {
  
}
