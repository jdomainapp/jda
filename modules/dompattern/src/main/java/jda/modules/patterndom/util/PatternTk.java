package jda.modules.patterndom.util;

import jda.modules.patterndom.PatternDomConstants;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class PatternTk {
  private PatternTk() {}
  
  /**
   * @effects 
   *  return the absolute file system path to the root pattern definition folder
   *  where all the pattern def files are stored
   */
  public static String getPatternDefRoot() {
    String rootSrcPath = PatternDomConstants.PatternRootPath;
    return rootSrcPath;
  }
}
