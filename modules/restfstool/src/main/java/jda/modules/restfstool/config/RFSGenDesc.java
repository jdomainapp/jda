package jda.modules.restfstool.config;

import java.lang.annotation.Documented;

import jda.modules.restfstool.backend.BESpringApp;

/**
 * @overview 
 *  RESTful full-stack generator configuration
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
@Documented
public @interface RFSGenDesc {
  LangPlatform langPlatform();
  String beTargetPackage();
  String beOutputPath();
  Class<? extends BESpringApp> beAppClass();

  GenerationMode genMode();

  String feOutputPath();
}
