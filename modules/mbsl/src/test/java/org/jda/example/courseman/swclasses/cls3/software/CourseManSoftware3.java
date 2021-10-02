package org.jda.example.courseman.swclasses.cls3.software;

import org.jda.example.courseman.swclasses.cls3.setup.SystemClass3;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpGen;
import jda.mosa.software.aio.SoftwareAio;
import jda.mosa.software.aio.SoftwareStandardAio;

/**
 * @overview 
 *  Standard CourseMan software.
 *  
 *  <p>Note: 
 *  Informative logging information can be observed by setting the VM argument:<br>
 *  <tt>-Dlogging=true</tt> 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManSoftware3 {


  /**
   * @requires 
   *  args.length > 0 /\ args[0] = name of a {@link Cmd}
   */
  public static void main(String[] args) {
    final Class SystemCls = SystemClass3.class;
    final Class SetUpCls = SetUpGen.class;
    
    SoftwareAio sw = new SoftwareStandardAio(SetUpCls, SystemCls);
    
    try {
      sw.exec(args);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
