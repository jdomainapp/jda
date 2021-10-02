package org.jda.example.courseman.swclasses.cls1.software;

import org.jda.example.courseman.swclasses.cls1.setup.SystemClass4Tool;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.jdatool.setup.DomainAppToolSetUpGen;
import jda.modules.setup.model.Cmd;
import jda.mosa.software.aio.SoftwareAio;
import jda.mosa.software.aio.SoftwareToolAio;

/**
 * @overview 
 *  Tool-based ProcessMan software, i.e. software that is executed by DomainAppTool.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManTool {
  
  /**
   * @requires 
   *  args.length > 0 /\ args[0] = name of a {@link Cmd}.
   */
  public static void main(String[] args) {
    final Class SystemCls = SystemClass4Tool.class;
    final Class SetUpCls = DomainAppToolSetUpGen.class;
    
    SoftwareAio tool = new SoftwareToolAio(SetUpCls, SystemCls);
    
    try {
      tool.exec(args);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
