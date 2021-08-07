package jda.modules.setup.model;

import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *  Represents the command <b>name</b>s that can be executed by the set up process. 
 *  
 * @author dmle
 *
 */
///
public enum Cmd {
  /**Set up everything, used for the stand-alone configuration or for the server in a client/server configuration*/
  SetUp, // run everthing
  // v2.7.3: a command to start a database server
  StartJavaDbServer, StopJavaDbServer,
  ////// APPLICATION CONFIGURATION ////////////////////
  Configure, //
  ConfigureDomain, //
  DeleteConfig,
  CreateDomainSchema, // v3.1
  DeleteDomainSchema, //
  /** create pre-configured data for selected domain classes */
  CreateBasicDomainData, // v3.2c
  CreateDemoDomainData, // v3.2c
  DeleteDomainData, //
  PostSetUpDb, // 
  PostSetUp,  // v2.7.2
  PostSetUpModule, // v2.7.4
  /** initial client-side configuration */
  SaveInitClientConfiguration,  // v3.1
  /** initial server-side configuration */
  SaveInitServerConfiguration, // v3.1
  // v2.6.4b: added new utility commands 
  RegisterConfigurationSchema,
  LoadConfiguration,
  //// SECURITY //////
  ConfigureSecurity, //
  SetUpSecurity, DeleteSecuritySchema, DeleteSecurityConfiguration,
  RegisterSecuritySchema,
  /**@version 3.3: create security configuration for selected types (e.g. DomainUser, Role), etc.*/
  ConfigureDomainSecurity, 
  /**@version 3.3: create domain data for selected types (e.g. CourseModule, Enrolment), etc.*/
  CreateDomainDataSet, // v3.3
  /** list all commands*/
  List, // v5.1
  /** Run the software */
  Run, // v5.1
  /**
   * @version 5.1
   * run the master set-up software 
   */
  SetUpSoft, // v5.1 
  /**
   * @version 5.1
   * run the set-up software for client (in a client-server software)
   * */
  SetUpClientSoft, 
  
  /**
   * @version 5.1
   * A light-weight version of {@link #SetUp}
   */
  SetUpLight, // v5.1
  ;

  /**
   * @effects 
   *  if exists Command c whose name is equal to <tt>cmd</tt> (ignoring case)
   *    return c
   *  else
   *    return null
   */
  public static Cmd lookUp(String cmd) {
    for (Cmd c : values()) {
      if (c.name().equalsIgnoreCase(cmd)) {
        return c;
      }
    }
    
    // not found
    return null;
  } //
  
  @DAttr(name="name",id=true,type=DAttr.Type.String)
  public String getName() {
    return name();
  }
}