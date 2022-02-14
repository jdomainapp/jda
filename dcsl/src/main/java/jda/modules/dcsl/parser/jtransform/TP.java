package jda.modules.dcsl.parser.jtransform;

import javax.json.JsonObject;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;

/**
 * @overview 
 *  A transformation procedure that consists in a sequence of {@link JTransform} actions.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 */
public abstract class TP {

  private JTransform transf;
  
  protected TP() {
    //
  }
  
//  /**
//   * @modifies <code>dom</code>
//   * @effects 
//   *  executes a pre-defined (template) transformation procedure defined in this 
//   *  using the parameters defined in <code>paramValMap</code>.
//   *  
//   *  <p>Apply the changes to <code>dom</code>.
//   */
//  public abstract void run(Dom dom, Map<ParamName, Object> paramValMap) 
//      throws NotFoundException, NotPossibleException ;

  /**
   * @modifies <code>dom</code>
   * @effects 
   *  initialises <code>dom</code> from <code>config</code> and 
   *  updates <code>dom</code> by applying a pre-defined (template) transformation procedure defined in this 
   *  using the parameters defined in <code>config</code>.
   */
  public void exec(Dom dom, JsonObject config) 
      throws NotFoundException, NotPossibleException {
    // initialise dom from config
    init(dom, config);
    
    // run the transformation
    run(dom, config);
  }
  
  /**
   * @effects 
   *  initialises <code>dom</code> from <code>config</code> so that 
   *  it is ready for transformation.
   */
  protected abstract void init(Dom dom, JsonObject config);

  /**
   * @modifies <code>dom</code>
   * @effects 
   *  executes a pre-defined (template) transformation procedure defined in this 
   *  using the parameters defined in <code>config</code>.
   *  
   *  <p>Apply the changes to <code>dom</code>.
   */
  public abstract void run(Dom dom, JsonObject config) 
      throws NotFoundException, NotPossibleException;
  
  /**
   * @modifies {@link #transf}
   * @effects 
   *  returns the transformation object that is used to defined the transformation procedure.
   *  
   *  <p>If this has already been defined then update it to point to <code>dom</code>.
   */
  protected JTransform getTransf(Dom dom) {
    if (transf == null) {
      transf = new JTransform(dom);
    } else {
      transf.setDom(dom);
    }
    
    return transf;
  }

  /**
   * @effects 
   *  if this has been executed
   *    print the source code of the specified element
   *  else
   *    print error
   */
  public void print(String fqElementName) {
    if (transf != null) {
      transf.printAst(fqElementName);
    } else {
      System.err.println("You must first invoke run() to execute the transformation procedure.");
    }
  }
  
  @Override
  public String toString() {
    return getPatternName() +":" + this.getClass().getSimpleName();
  }

  /**
   * @effects 
   *  return the name of pattern that is handled by this
   */
  public abstract String getPatternName();
  
  /**
   * @effects 
   *  loads and return {@link Dom} representing all the domain classes defined in the 
   *  pattern model.
   *  
   * @version 5.4.1
   */
  protected Dom loadPatternDom(String patternDefRoot, String pattern,
      String patternFileExt) throws NotPossibleException, NotFoundException {
    Dom patternDom = new Dom(patternDefRoot);
    patternDom.loadClasses(ToolkitIO.getFilePath(pattern, patternFileExt), 
        pattern);
    
    return patternDom;
  }
}
