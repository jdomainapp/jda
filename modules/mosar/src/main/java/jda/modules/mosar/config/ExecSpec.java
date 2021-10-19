package jda.modules.mosar.config;

/**
 * @overview 
 *  RFSoftware execution specification.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1 
 */
public enum ExecSpec {
  Gen(1),
  Compile(2),
  Run(4),
  Full(7);
  ;
  
  
  private int val;

  private ExecSpec(int val) {
    this.val = val;
  }
  
  public int value() {
    return val;
  }

  /**
   * @effects 
   *  if this includes {@link #Compile}
   *    return true
   *  else
   *    return false
   */
  boolean isCompile() {
    int bitWise = val & Compile.value();
    return bitWise == Compile.value();
  }
}
