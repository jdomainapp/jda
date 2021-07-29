package jda.modules.common.types.tree;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview
 *  An interface for transforming {@link Tree} to XML text. 
 *  
 * @author dmle
 */
public interface TreeXMLTransformer {

  /**
   * @requires 
   *  tree != null
   *  
   * @effects 
   *  convert <tt>tree</tt> to an XML string and return it
   *  
   *  <p>throws NotPossibleException if failed
   */
  String treeToXML(Tree tree, String charSetName) throws NotPossibleException;

  /**
   * @requires 
   *  treeXml != null
   *  
   * @effects 
   *  convert <tt>treeXml</tt> back to a {@link Tree} from which it was created 
   *  by the method {@link #treeToXML(Tree, String)}
   *  
   *  <p>throws NotPossibleException if failed
   */
  Tree treeFromXML(String treeXml, String charSetName)
      throws NotPossibleException;
}
