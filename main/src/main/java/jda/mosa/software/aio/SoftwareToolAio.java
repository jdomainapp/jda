package jda.mosa.software.aio;

import java.util.Collection;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.jdatool.DomainAppTool;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpBasic;
import jda.util.SwTk;

/**
 * @overview 
 *   A subtype of {@link SoftwareAio} for tool-based software, i.e. software 
 *   that are executed by DomainAppTool. 
 *   
 *   <p>The main difference between {@link SoftwareToolAio} and {@link SoftwareStandardAio} is that
 *    {@link SoftwareToolAio} executes directly on the domain classes that are specified as input. 
 *    The modules' MCCs are generated automatically from these. In contrast, {@link SoftwareStandardAio} comes
 *    with the MCCs (typically specified in the {@link SystemClass}) and these are used to generate the modules 
 *    and the software.
 *   
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.1
 */
public class SoftwareToolAio extends SoftwareAio {

  private Class[] domainModel;

  /**
   * @effects 
   *  initialise this with a setup class and a system class
   */
  public SoftwareToolAio(Class<? extends SetUpBasic> setUpCls, Class systemCls) {
    super(setUpCls, systemCls);
  }

  /**
   * @effects return domainModel
   */
  public Class[] getDomainModel() {
    if (domainModel == null) {
      Collection<Class> domainModelCol = SwTk.parseDomainModel(getSystemCls());
      
      domainModel = domainModelCol.toArray(new Class[domainModelCol.size()]);
    }
    return domainModel;
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
      //SetUpBasic su = getSu();
      
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
        case Run:
          run(args); break;
        default:
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
  protected void run(String[] args) throws Exception {
    //TODO: may extract domain classes from args and use them as argument for set-up here
    DomainAppTool.run(getDomainModel());    
  }
  
  @Override
  protected void runSetUp(Cmd cmdObj, String[] args) throws NotPossibleException {
    //TODO: may extract domain classes from args and use them as argument for set-up here
    DomainAppTool.runSetUp(cmdObj, getDomainModel());
  }
}
