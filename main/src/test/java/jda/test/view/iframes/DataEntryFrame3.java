package jda.test.view.iframes;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.JDataField;
import jda.util.SwTk;

/* data entry frame with: 
 *  - data fields
 *  - a nested panel
 *  - custom focus traversal policy 
 */
public class DataEntryFrame3 extends DataEntryFrame2 {

  @DAttr(name="a1",mutable=false,optional=false,type=Type.String)
  private String a1;

  @DAttr(name="a2",optional=false,type=Type.String)
  private String a2;

  @DAttr(name="a3",mutable=false,optional=false,type=Type.String)
  private String a3;

  @DAttr(name="a4",optional=false,type=Type.String)
  private String a4;

  private JPanel root;
  
  protected void createGUI() {
    DODMBasic schema = null;
    //Configuration config = new Configuration();
    Configuration config = SwTk.createMemoryBasedConfiguration("");

    GUIToolkit.initInstance(config);

    try {
      schema = DODMBasic.getInstance(config);
      schema.addClass(this.getClass());
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    int numFields = 4;
    
    DAttr[] dcs = new DAttr[numFields];
    dcs[0] = schema.getDsm().getDomainConstraint(this.getClass(), "a1");
    dcs[1] = schema.getDsm().getDomainConstraint(this.getClass(), "a2");
    dcs[2] = schema.getDsm().getDomainConstraint(this.getClass(), "a3");
    dcs[3] = schema.getDsm().getDomainConstraint(this.getClass(), "a4");
    
    JComponent[] comps = new JDataField[numFields];
    MyOwnFocusTraversalPolicy traversalPolicy;
    
    root = new JPanel();
    root.setFocusTraversalPolicyProvider(true);
    add(root);

    // set focus on the first editable field
    Vector order = new Vector();
    
    // populate panel with data fields
    createPanel(schema, config, root, dcs, order);
    
    // create an extra subpanel in root, adding 
    // the data fields of this subpanel to root's traversal policy order
    JPanel subPanel = new JPanel();
    subPanel.setPreferredSize(new Dimension(200,200));
    subPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
    root.add(subPanel);
    
    createPanel(schema, config, subPanel, dcs, order);
    
    // root's traversal policy
    traversalPolicy = new MyOwnFocusTraversalPolicy(order);
    root.setFocusTraversalPolicy(traversalPolicy);
  }
  
  public void postCreateGUI() {
    // do nothing
  }
}
