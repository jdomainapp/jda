/**
 * 
 */
package org.jda.example.courseman.software.config.ref;

import org.jda.example.courseman.modulesref.ModuleMain;
import org.jda.example.courseman.modulesref.enrolmentmgmt.ModuleEnrolmentMgmt;
import org.jda.example.courseman.modulesref.enrolmentmgmt.ModuleEnrolmentMgmtWithCustomScopeDescriptor;
import org.jda.example.courseman.modulesref.student.ModuleStudent;

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
 *  The system class of the CourseMan application.
 * @author dmle
 * @version 4.0
 */
@SystemDesc(
    appName="CourseMan",
    splashScreenLogo="coursemanapplogo.jpg",
    language=Language.English,
    orgDesc=@OrgDesc(
        name="Faculty of IT",
        address="K1m9 Đường Nguyễn Trãi, Quận Thanh Xuân", 
        logo="hanu.gif", 
        url="http://fit.hanu.edu.vn"
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
    modules={         
      ModuleMain.class,  // main
      // data
      ModuleEnrolmentMgmtWithCustomScopeDescriptor.class,
      // IMPORTANT: needed to indicate that we need to serialise data!!!!
      ModuleStudent.class 
    },
    sysModules={}, 
    setUpDesc=@SysSetUpDesc(
      setUpConfigType=SetUpConfig.class
    ),
    securityDesc=@SecurityDesc(
      isEnabled=false
    )
)
public class CourseManWithScopeDescConfigClass {
  // empty
}
