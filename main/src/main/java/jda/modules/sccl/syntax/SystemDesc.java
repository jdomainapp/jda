/**
 * 
 */
package jda.modules.sccl.syntax;

import java.lang.annotation.Documented;

import jda.modules.common.CommonConstants;
import jda.modules.mccl.conceptmodel.Configuration.Language;

/**
 * @overview 
 *  An annotation that is used as the place-holder for application-wide configuration. 
 *  
 * @author dmle
 *
 * @version 3.3
 */
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
@Documented
public @interface SystemDesc {
  /***
   * Organisation description 
   */
  public OrgDesc orgDesc();
  
  /***
   * data source description 
   */
  public DSDesc dsDesc();

  /***
   * (Optional) The system  modules 
   * <br>Default: empty 
   */
  public Class[] sysModules() default {};

  /***
   * The domain-specific modules 
   */
  public Class[] modules();

  /**
   * @effects 
   *  (Optional) Name of the image file that contains the splash screen logo
   *  <br>Default: empty 
   */
  public String splashScreenLogo() default CommonConstants.EmptyString;

  /**
   * @effects 
   *  (Optional) security configuration of the application
   *  <br>Default: @{@link SecurityDesc} 
   */
  SecurityDesc securityDesc() default @SecurityDesc();

  /**
   * (Optional) The system set-up configuration
   * <br>Default: </tt>@{@link SysSetUpDesc()}</tt>
   */
  SysSetUpDesc setUpDesc();

  /**
   * (Optional) the application language
   * <br>Default: {@link Language#English} 
   */
  Language language() default Language.English;

  /**
   * The application name  
   */
  String appName();

  /**
   * 
   * (Optional): the classes that define the data file loaders, which contain pre-defined domain-specific data
   *  that need to be inserted into the underlying data store of the application
   *  
   * <br>Default: {}
   * @version 3.3
   */
  public Class[] dataFileLoaders() default {};
}
