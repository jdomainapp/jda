package jda.mosa.software.aio;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpBasic;
import jda.util.SwTk;

/**
 * @overview 
 *  Represents software-all-in-one, which defines a "all-in-one" interface for 
 *  performing all types of operations relating to the setting up 
 *  and running of software. 
 *  
 *  <p>A typical use case of this class is to create an instance of {@link SoftwareAio} (using one of its
 *  sub-types) and invoke its {@link #exec(String[])} operation. 
 *  
 *  <p>The input arguments includes the name of the desired {@link Cmd} and (optionally) other program arguments.
 *  
 *  <p>Note:
 *  <ul> 
 *  <li>Informative logging information can be observed by setting the VM argument:<br>
 *  <tt>-Dlogging=true</tt> 
 *  <li>Informative debugging information can be observed by setting the VM argument: <br>
 *  <tt>-Ddebug=...</tt>
 *  </ul>  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.1
 */
public abstract class SoftwareAio {
  private Class systemCls;
  private Class<? extends SetUpBasic> setUpCls;
  private SetUpBasic su;
  
  /**
   * @effects 
   *  Initialise this with a setup class and a system class.
   *  <p>Set {@link #su} = an instance of <tt>setUpCls</tt>. Throws NotPossibleException if fails to create this instance. 
   */
  public SoftwareAio(Class<? extends SetUpBasic> setUpCls, Class systemCls) throws NotPossibleException {
    this.setUpCls = setUpCls;
    this.systemCls = systemCls;
    
    try {
      this.su = setUpCls.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, new Object[] {setUpCls, ""});
    }
    
    /** Set the set-up class */
    SwTk.setSystemProperty(PropertyName.setup_class, setUpCls.getName());
    
    /** Set the system class */
    SwTk.setSystemProperty(PropertyName.setup_systemClass, systemCls.getName());
  }

  /**
   * @effects 
   *  return {@link #systemCls}
   */
  public Class getSystemCls() {
    return systemCls;
  }
  
  
  /**
   * @effects return setUpCls
   */
  public Class<? extends SetUpBasic> getSetUpCls() {
    return setUpCls;
  }

  /**
   * @effects return su
   */
  public SetUpBasic getSu() {
    return su;
  }

  /**
   * @requires 
   *  args.length > 0 /\ args[0] = name of a {@link Cmd}.
   *
   * @effects Execute the {@link Cmd} whose name matches args[0] using other arguments (if specified).
   * 
   */
  public abstract void exec(String[] args) throws Exception;

  /**
   * @effects run the software
   */
  protected abstract void run(String[] args) throws Exception;

  /**
   * @effects 
   *  execute the set-up command <tt>cmdObj</tt> with <tt>args</tt>
   */
  protected abstract void runSetUp(Cmd cmdObj, String[] args)
      throws NotPossibleException;

}
