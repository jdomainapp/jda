package jda.modules.restfstool.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jda.modules.restfstool.backend.BESpringApp;

/**
 * @overview 
 *  RESTful full-stack generator configuration
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
@Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value={java.lang.annotation.ElementType.TYPE})
@Documented
public @interface RFSGenDesc {
  StackSpec stackSpec();
  
  /** backend language platform */
  LangPlatform beLangPlatform();
  
  /** backend top-level package that contains the domain model */
  String bePackage();
  
  /** backend target top-level package (for all generated code) */
  String beTargetPackage();
  
  /** backend output path (where {@link #beTargetPackage()} lives)*/
  String beOutputPath();
  
  /** backend main application that is executed by the web server.
   * For now, it is Spring-specific. This may be changed in the future to suit the {@link #beLangPlatform()} */
  Class<? extends BESpringApp> beAppClass(); // default Null.class;

  /** code generation mode (source code or byte code) */
  GenerationMode genMode();

  /** frontend output path */
  String feOutputPath();

  /** front-end project path */
  String feProjPath();

  /** front-end project name */
  String feProjName();

  /** front-end's shared resources for project */
  String feProjResource();
}
