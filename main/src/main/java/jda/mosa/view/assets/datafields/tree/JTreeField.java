package jda.mosa.view.assets.datafields.tree;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.tree.Node;
import jda.modules.common.types.tree.Tree;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.JDataField;

/**
 * @overview 
 *  A {@link JDataField} that uses a {@link JTree} as value and displays this 
 *  object on the field.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public class JTreeField<C> extends JDataField {

  private TreeModel model;
  
  /** actual value (casted from) JDataField.val */
  private Tree tree;

  private Object selectedNodeObject;
  
  public JTreeField(DataValidator validator, Configuration config, C val,
      DAttr domainConstraint, Boolean editable, Boolean autoValidation)
      throws ConstraintViolationException {
    super(validator, config, val, domainConstraint, editable, autoValidation);
    
    if (val != null && !(val instanceof Tree)) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
          new Object[] {val});
    }
    
    if (val != null) {
      createModel((Tree) val);
    }
  }

  /**
   * @modifies this.{@link #model}
   * @effects 
   *  (re)initialises {@link #model} from the nodes and edges contained in <tt>tree</tt>. 
   */
  private void createModel(Tree tree) {
    Node root = tree.getRoot();
    DefaultMutableTreeNode rootN = createNode(root); 
    model = new DefaultTreeModel(rootN);
    
    createSubTree(tree, root, rootN);
  }

  /**
   * @requires 
   *  <tt>nN</tt> is the corresponding node in {@link #model} of <tt>n</tt> 
   *  
   * @effects 
   *  if exists in <tt>tree</tt> a sub-tree of <tt>n</tt> 
   *    then create the corresponding sub-tree in {@link #model} rooted at </tt>nN</tt> 
   *  
   */
  private void createSubTree(Tree tree, Node n, DefaultMutableTreeNode nN) {
    // add other nodes and edges
    List<Node> children = tree.getChildren(n);
    if (children != null) {
      for (Node c : children) {
        DefaultMutableTreeNode cN = createNode(c);
        nN.add(cN);
        
        // recursive:
        createSubTree(tree, c, cN);
      }
    }
  }

  /**
   * @effects 
   *  return a {@link TreeNode} from <tt>n</tt>.
   */
  private DefaultMutableTreeNode createNode(Node n) {
    Object valueObj = n.getValue();
    
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(valueObj);
    
    return node;
  }

  /**
   * @effects
   *  create and return a {@link JTree} as the dipslay component of this  
   */
  @Override
  protected JComponent createDisplayComponent(
      JDataField.DataFieldInputHelper tfh) {
    // create an empty JTree
    JTree tree = new JTree();
    
    //p.setBorder(BorderFactory.createEtchedBorder());

    //tree.putClientProperty("JTree.lineStyle", "Horizontal");
    
    // customise tree nodes
    DefaultTreeCellRenderer nodeRenderer = new DefaultTreeCellRenderer();
    ImageIcon openIcon = GUIToolkit.getImageIcon("containeropen.gif", "openedNode");
    ImageIcon closedIcon = GUIToolkit.getImageIcon("containerclose.gif", "closedNode");
    ImageIcon leafIcon = GUIToolkit.getImageIcon("containerclose.gif", "leafNode");

    nodeRenderer.setOpenIcon(openIcon);
    nodeRenderer.setClosedIcon(closedIcon);
    nodeRenderer.setLeafIcon(leafIcon);
    tree.setCellRenderer(nodeRenderer);
    
    // set up mouse handlling 
    tree.addTreeSelectionListener(new TreeHandler(tree));
    
    
    setGUIComponent(tree);
    
    return tree;
  }

  /**
   * @effects
   *   return this.value directly (using {@link #getValueDirectly()}).
   */
  @Override
  public Object getValue() throws ConstraintViolationException {
    return getValueDirectly();
  }

  /**
   * @effects 
   *  update the display component to display <tt>val</tt>
   */
  @Override
  public void setValue(Object val) throws ConstraintViolationException {
    if (val == null || !(val instanceof Tree)) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
          new Object[] {val});
    }

    super.setValueDirectly(val);
    
    this.tree = (Tree) val;
    
    createModel(tree);
    
    
    JTree display = (JTree) getGUIComponent(); // v5.1c:

    //display.removeAll();
    
    display.setModel(model);
    
    //display.repaint();
  }

  /**
   * @effects 
   * 
   */
  @Override
  public void reset() {
    // do nothing
  }

  /**
   * @effects 
   * 
   */
  @Override
  public void clear() {
    // do nothing
  }
  
  /**
   * @effects 
   *    return the label of the node that user is current selecting 
   */
  public Object getSelectedNodeInfo() {
    return selectedNodeObject;
  }
  
  /**
   * @overview 
   *  Handles tree selection event to notify clients about a new node selection. 
   *  
   * @author Duc Minh Le (ducmle)
   */
  private class TreeHandler implements TreeSelectionListener {

    private JTree jtree;

    public TreeHandler(JTree tree) {
      this.jtree = tree;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    jtree.getLastSelectedPathComponent();

      if (node == null) return;
      
      Object nodeInfo = node.getUserObject();
      
      JTreeField.this.selectedNodeObject = nodeInfo;
      
      // inform listener 
      // fire value change: 
      // strictly speaking, JTreeField.value still holds the same tree, but we can grossly say 
      // that it's "state" has been changed by the fact that a new node has been selected.
      JTreeField.this.fireValueChanged();
    }
  } /** end {@link TreeHandler} */
}
