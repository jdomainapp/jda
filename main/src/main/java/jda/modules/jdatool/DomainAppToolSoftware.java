package jda.modules.jdatool;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.setup.model.Cmd;

/**
 * @overview 
 *  Encapsulate the basic functions for setting up and running a software given its domain model.  
 *  
 * @author dmle
 * 
 * @deprecated use {@link DomSoftware} instead
 * @version 4.0
 */
@Deprecated
public abstract class DomainAppToolSoftware {
    
  /**
   * Sub-classes must override this operation to return the actual model.
   * 
   * @effects 
   *  return the model that is to be run with this software
   */
  protected abstract Class[] getModel();
  
  /**
   * @requires 
   *  args.length > 0 /\ args[0] is a command
   *  
   * @effects 
   *  execute the software with the command specified in args[0] and its {@link #model}.
   *  
   */
  protected void exec(String[] args) {
    if (args == null || args.length == 0) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          new Object[] {DomainAppToolSoftware.class.getSimpleName(), "exec", "No command specified"});
    }
    
    String cmd = args[0];
    
    if (cmd.equalsIgnoreCase("configure")) {
      configure();
    } else if (cmd.equalsIgnoreCase("run")) {
      run();
    } else if (cmd.equalsIgnoreCase("deleteDomainData")) {
      deleteDomainData();
    } else if (cmd.equalsIgnoreCase("deleteDomainSchema")) {
      deleteDomainSchema();
    } else if (cmd.equalsIgnoreCase("createDomainSchema")) {
      createDomainSchema();
    } else if (cmd.equalsIgnoreCase("deleteApplicationConfig")) {
      deleteApplicationConfig();
    } else {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          new Object[] {DomainAppToolSoftware.class.getSimpleName(), "main", "Invalid command: " + cmd});
    }    
  }

  /**
   * @effects 
   *  configure and run the software.
   *  
   *  <p>Throws ApplicationRuntimeException if an error occured.
   * 
   * @version 
   */
  private void configure() throws ApplicationRuntimeException {
    DomainAppTool.runSetUp(Cmd.Configure, getModel());    
  }
  
  /**
   * @effects 
   *  Run the software.
   *  
   *  <p>Throws ApplicationRuntimeException if an error occured.
   *  
   * @version 
   * 
   */
  private void run() throws ApplicationRuntimeException {
    DomainAppTool.run(getModel());    
  }

  ////// Other set-up commands ///////
  /**
   * @effects 
   *  Delete domain data of the domain classes specified in <tt>domainClasses</tt>.
   *  
   *  <p>Throws ApplicationRuntimeException if an error occured.
   * 
   * @version 
   */
  private void deleteDomainData(Class...domainClasses) throws ApplicationRuntimeException {
    if (domainClasses == null || domainClasses.length == 0) {
      //return;
      domainClasses = getModel();
    }
    
    DomainAppTool.runSetUp(Cmd.DeleteDomainData, domainClasses);
  }
  
  /**
   * @effects 
   *  Delete domain schema of the domain classes specified in <tt>domainClasses</tt>.
   *  
   *  <p>Throws ApplicationRuntimeException if an error occured.
   * 
   * @version 
   */
  private void deleteDomainSchema(Class...domainClasses) throws ApplicationRuntimeException {
    if (domainClasses == null || domainClasses.length == 0){
      //return;
      domainClasses = getModel();
    }
    
    DomainAppTool.runSetUp(Cmd.DeleteDomainSchema, domainClasses);    
  }

  /**
   * @effects 
   *  Create domain schema of the domain classes specified in <tt>domainClasses</tt>.
   *  
   *  <p>Throws ApplicationRuntimeException if an error occured.
   * 
   * @version 
   */
  private void createDomainSchema(Class...domainClasses) throws ApplicationRuntimeException {
    if (domainClasses == null || domainClasses.length == 0){
      //return;
      domainClasses = getModel();
    }
    
    DomainAppTool.runSetUp(Cmd.CreateDomainSchema, domainClasses);    
  }

  /**
   * @effects 
   *  Delete application configuration
   *  
   *  <p>Throws ApplicationRuntimeException if an error occured.
   * 
   * @version 
   */
  private void deleteApplicationConfig() throws ApplicationRuntimeException {
    DomainAppTool.runSetUp(Cmd.DeleteConfig);    
  }
}
