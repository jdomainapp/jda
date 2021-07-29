package jda.modules.mccl.test.utils;

import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.sccl.syntax.DSDesc;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.sccl.syntax.SysSetUpDesc;
import jda.modules.sccl.syntax.SystemDesc;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@SystemDesc(
    appName="CourseMan",
    splashScreenLogo="coursemanapplogo.jpg",
    language=Language.English, 
    dsDesc = @DSDesc(connType = ConnectionType.Client, dsUrl = "", password = "", type = "", user = ""), 
    modules = { }, orgDesc = @OrgDesc(address = "", logo = "", name = ""), setUpDesc = @SysSetUpDesc)
public class TestCode {
  //
}
