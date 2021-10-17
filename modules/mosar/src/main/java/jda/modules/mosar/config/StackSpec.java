package jda.modules.mosar.config;

/**
 * @overview 
 *  specifies the parts of the stack that are generated.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public enum StackSpec {
  /** back-end only*/
  BE,
  /** front-end only*/
  FE,
  /** full-stack (both {@link #BE} and {@link #FE})*/
  FS;

  /**
   * @effects 
   *  if this includes {@link #FE}
   *    return true
   *  else
   *    return false
   */
  public boolean includesFE() {
    return this.equals(FE) || this.equals(FS);
  }

  /**
   * @effects 
   *  if this includes {@link #BE}
   *    return true
   *  else
   *    return false
   */
  public boolean includesBE() {
    return this.equals(BE) || this.equals(FS);
  }
}
