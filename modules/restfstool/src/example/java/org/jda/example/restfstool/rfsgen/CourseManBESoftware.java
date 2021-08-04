package org.jda.example.restfstool.rfsgen;

import com.hanu.courseman.SCCCourseMan;

import jda.modules.restfstool.backend.BESoftware;
import jda.modules.restfstool.backend.BESpringApp;
import jda.modules.restfstool.config.RFSGenConfig;
import jda.modules.restfstool.util.RFSGenTk;

/**
 * @overview 
 *  Execute the Back end software from the generated components. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManBESoftware {
  
  public static void main(String[] args) {
    Class scc = SCCCourseMan.class;
    RFSGenConfig cfg = RFSGenTk.parseRFSGenConfig(scc);
    
    Class<? extends BESpringApp> backEndAppCls =  cfg.getBeAppClass(); //Resources.backEndAppCls;
    
    new BESoftware().run(
        cfg.getBeTargetPackage(), 
        backEndAppCls, 
        cfg.getDomainModel()
        );
  }
}
