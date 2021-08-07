package jda.modules.patterndom;

import java.io.File;

import jda.modules.common.io.ToolkitIO;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class PatternDomConstants {
  private PatternDomConstants() {}
  
  private static final String PkgSrcPath = 
      ToolkitIO.getPackagePath(PatternDomConstants.class) + "";
  
  public static final String PatternRootPath = PkgSrcPath + 
      File.separator + "patterndefs";
  
  private static final String PatternsConfigFileName = "patterns-config.json";
  public static String PatternsConfigPath = ToolkitIO.getPath(PatternDomConstants.class, 
      PatternsConfigFileName);
}
