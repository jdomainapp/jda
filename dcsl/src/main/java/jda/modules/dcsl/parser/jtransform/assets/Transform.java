package jda.modules.dcsl.parser.jtransform.assets;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.parser.Dom;

/**
 * @overview 
 *  Represents a source code transformation action.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 * @deprecated (not yet operational)
 */
public abstract class Transform {

  private Dom dom;

  /**
   * @effects 
   *
   * @version 
   */
  public Transform(Dom dom) {
    this.dom = dom;
  }

  /**
   * @effects return dom
   */
  public Dom getDom() {
    return dom;
  }
  
  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the specified transformation type <code>transfType</code> to the element named 
   *  <code>srcElement</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return the affected object.
   *  <p>
   *  Throws NotFoundException if a required element is not found; 
   *  NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public abstract Node apply(String srcElement, String name, Object...args) throws NotFoundException, NotPossibleException;
  
  /**
   * @modifies {@link #dom}
   * @effects 
   *  apply the specified transformation type <code>transfType</code> to the element named 
   *  <code>srcElement</code> in {@link #dom} with arguments <code>args</code>.
   *  <p>If succeeded then 
   *    update {@link #dom} with the transformation result and 
   *    return the affected object.
   *  <p>
   *  Throws NotFoundException if a required element is not found; 
   *  NotPossibleException if failed to perform the specified transformation for some reasons.
   */
  public abstract Node apply(String srcElement, Node currElement, Object...args) throws NotFoundException, NotPossibleException;

  
  /**
   * @requires elements from the specified position are of the type <code>T</code>.
   * 
   * @modifies resultArr
   * @effects 
   *  if <code>args.length > pos </code>
   *    extract from <code>args</code> the remaining elements from position <code>pos</code>
   *    as <code>Modifier[]</code> and return them
   *  else
   *    return null
   */
  protected <T> void getArgArray(Object[] args, int pos, T[] resultArr) {
    List<T> result = new ArrayList<>();
    if (pos >= 0 && pos < args.length) {
      for (int i = pos; i < args.length; i++) {
        result.add((T) args[i]);
      }
    }
    
    result.toArray(resultArr);
  }
}
