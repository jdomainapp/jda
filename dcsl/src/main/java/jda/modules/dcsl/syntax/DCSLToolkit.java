package jda.modules.dcsl.syntax;

import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DAssoc.Associate;

/**
 * @overview 
 *  Defines shared, common operations concerning meta-attributes. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.1
 */
public class DCSLToolkit {
  private DCSLToolkit() {} 
  
  /**
   * @effects 
   *  if <tt>pname</tt> is name of an essential property of {@link Associate}.
   *    return true
   *  else
   *    return false
   * @version 5.2 
   */
  public static boolean isEssentialAssociateProperty(String pname) {
    switch(pname) {
      case "cardMin":
      case "cardMax":
      case "determinant":
      case "updateLink":
        return true;
      default:
        return false;
    }
  }

  /**
   * @effects 
   *  if <tt>cardMax</tt> = toString({@link DCSLConstants#CARD_MORE}) 
   *    return true
   *  else
   *    return false
   * @version 5.2
   */
  public static boolean isCardMore(String cardMax) {
    return (cardMax != null && cardMax.equals(DCSLConstants.CARD_MORE+""));
  }

  /**
   * @effects 
   *  if <tt>pre</tt> equals default pre-condition value
   *    return true
   *  else
   *    return false; 
   */
  public static boolean isDefaultPreCondition(String pre) {
    return (pre != null && pre.equals(DCSLConstants.DEFAULT_DOPT_REQUIRES));
  }

  /**
   * @effects 
   *  if <tt>post</tt> equals default post-condition value
   *    return true
   *  else
   *    return false; 
   */
  public static boolean isDefaultPostCondition(String post) {
    return (post != null && post.equals(DCSLConstants.DEFAULT_DOPT_EFFECTS));
  }
}
