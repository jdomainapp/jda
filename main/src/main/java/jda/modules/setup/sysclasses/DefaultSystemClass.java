package jda.modules.setup.sysclasses;

import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.javadb.JavaDbOSM;
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
 *  A system class for software that has its entire data base (incl. configuration and data) stored 
 *  the default type of relational database.
 *   
 *  <p>The typical default relational database is JavaDb.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0 
 */
@SystemDesc(
    appName="DefaultDomainApp",
    splashScreenLogo="domainapp.jpg",
    language=Language.English,
    orgDesc=@OrgDesc(
        name="My Organisation",
        address="My Address", 
        logo="domainapp.jpg", 
        url="http://myorg.vn"
    ), 
    dsDesc=@DSDesc(
        type="derby", 
        dsUrl="data/domainds", 
        user="admin",
        password="password",
        dsmType=DSM.class,
        domType=DOM.class,
        osmType=JavaDbOSM.class,
        connType=ConnectionType.Embedded
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
public class DefaultSystemClass {
  
}
