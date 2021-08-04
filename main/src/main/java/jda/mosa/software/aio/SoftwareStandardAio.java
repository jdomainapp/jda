package jda.mosa.software.aio;

import java.io.IOException;
import java.util.Arrays;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.clientserver.SetUpClient;
import jda.mosa.software.basic.Program;
import jda.software.setup.SetUpProgram;
import jda.util.SwTk;

/**
 * @overview 
 *  A subtype of {@link SoftwareAio} for standard software, i.e. software that are NOT executed by DomainAppTool. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.1
 */
public class SoftwareStandardAio extends SoftwareAio {
  
  /**
   * @effects 
   *  initialise this with a setup class and a system class
   */
  public SoftwareStandardAio(Class<? extends SetUpBasic> setUpCls, Class systemCls) {
    super(setUpCls, systemCls);
  }

  @Override
  public void exec(String[] args) throws Exception {
    if (args == null || args.length == 0) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
          new Object[] { this.getClass().getSimpleName(), "exec",
              "No command specified" });
    }

    String cmd = args[0];

    Cmd cmdObj = Cmd.lookUp(cmd);
    
    if (cmdObj != null) {
      Class<? extends SetUpBasic> setUpCls = getSetUpCls();
      Class systemCls = getSystemCls();
      
      /** Set up debugging and logging */
      /* should not do these b/c logging and debugging are read by each class during loading, 
       * so setting these here do not have any effect
       */
//      ApplicationToolKit.setLogging();
//      ApplicationToolKit.setDebugging();
      
//      /** Set the set-up class */
//      ApplicationToolKit.setSystemProperty(PropertyName.setup_class, setUpCls.getName());
//      
//      /** Set the system class */
//      ApplicationToolKit.setSystemProperty(PropertyName.setup_systemClass, systemCls.getName());
//      
      /** Set simulation mode */
      //ApplicationToolKit.setSystemProperty(PropertyName.setup_SerialiseConfiguration, "false"); 
      
      switch (cmdObj) {
        case Run: // run command
          run(args); break;
        case SetUpSoft:
          setUpSoft(args); break;
        case SetUpClientSoft:
          setUpClientSoft(args); break;
        default:  // set-up commands
          runSetUp(cmdObj, args);
      }
    } else {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
          new Object[] { this.getClass().getSimpleName(), "exec",
              "Invalid command: " + cmd });
    }
  }

  @Override
  protected void runSetUp(Cmd cmdObj, String[] args) throws NotPossibleException {
    SetUpBasic su = getSu();

    try {
      su.run(cmdObj, args);
    } catch (DataSourceException | IOException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
          new Object[] { this.getClass().getSimpleName(), cmdObj,
              Arrays.toString(args)});
    }    
  }

  /**
   * @effects run the software
   */
  @Override
  protected void run(String[] args) throws Exception {
    Program.main(args);
  }
  
  /**
   * @effects 
   *  run the GUI-based set-up software
   */
  protected void setUpSoft(String[] args) throws NotPossibleException {
    String[] argS = new String[args.length+1];
    argS[0] = getSetUpCls().getName();
    System.arraycopy(args, 0, argS, 1, args.length);
    
    SetUpProgram.main(argS);
  }
  
  /**
   * @effects 
   *  run the GUI-based set up software for the client (in a client-server software).
   */
  protected void setUpClientSoft(String[] args) throws NotPossibleException {
    String[] argS = new String[args.length+1];
    argS[0] = SetUpClient.class.getName();
    System.arraycopy(args, 0, argS, 1, args.length);
    
    SetUpProgram.main(argS);
  }
}
