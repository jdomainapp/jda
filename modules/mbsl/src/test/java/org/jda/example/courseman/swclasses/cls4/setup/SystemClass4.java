package org.jda.example.courseman.swclasses.cls4.setup;


import org.jda.example.courseman.modules.ModuleMain;
import org.jda.example.courseman.modules.coursemodule.ModuleCourseModule;
import org.jda.example.courseman.modules.enrolment.ModuleEnrolment;
import org.jda.example.courseman.modules.helprequest.ModuleHelpRequest;
import org.jda.example.courseman.modules.sclass.ModuleSClass;
import org.jda.example.courseman.modules.sclassregist.ModuleSClassRegistration;
import org.jda.example.courseman.swclasses.cls4.modules.enrolmentmgmt.ModuleEnrolmentMgmt;
import org.jda.example.courseman.swclasses.cls4.modules.student.ModuleStudent;

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
 *  
 * @author dmle
 * @version 1.3
 */
@SystemDesc(
    appName="CourseMan",
    splashScreenLogo="hanu.gif",
    language=Language.English,
    orgDesc=@OrgDesc(
        name="Faculty of IT, Hanoi University",
        address="Km9 Nguyen Trai Street, Thanh Xuân District", 
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
        ModuleEnrolmentMgmt.class
        ,ModuleStudent.class 
        ,ModuleHelpRequest.class
        ,ModuleSClassRegistration.class
        ,ModuleCourseModule.class
        ,ModuleSClass.class
        ,ModuleEnrolment.class
    },
    sysModules={}, 
    setUpDesc=@SysSetUpDesc(
      setUpConfigType=SetUpConfig.class
    )
    ,securityDesc=@SecurityDesc(
      isEnabled=false // true: to initialise security schema
    )       
)
public class SystemClass4 {
  // empty
}
