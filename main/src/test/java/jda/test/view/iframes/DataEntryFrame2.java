package jda.test.view.iframes;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JTextField;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;

/* data entry frame with custom focus traversal policy */
public class DataEntryFrame2 extends MyInternalFrame {

  @DAttr(name="a1",mutable=false,optional=false,type=Type.String)
  private String a1;

  @DAttr(name="a2",optional=false,type=Type.String)
  private String a2;

  @DAttr(name="a3",mutable=false,optional=false,type=Type.String)
  private String a3;

  @DAttr(name="a4",optional=false,type=Type.String)
  private String a4;

  private JPanel root;
  
  private MyOwnFocusTraversalPolicy traversalPolicy;
  
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
    root = new JPanel();
    root.setFocusTraversalPolicyProvider(true);

    DAttr dc;
    
    Vector order = new Vector();
    
    // populate panel with data fields
    createPanel(schema, config, root, dcs, order);
    
    add(root);

    // custom traversal policy
    traversalPolicy = new MyOwnFocusTraversalPolicy(order);
  }
  
  protected void createPanel(DODMBasic schema, Configuration config, 
      JPanel panel, DAttr[] dcs, Vector order) {
    DAttr dc;
    
    for (int i = 0; i < dcs.length; i++) {
      dc = dcs[i];
      JTextField tf = new JTextField(ViewTestCase.getDataValidator(schema,null), config, dc);
      
      if (dc.mutable()) {
        // IMPORTANT: must add the display component of data field to 
        // the traversal policy order
        order.add(tf.getGUIComponent());
      }
      panel.add(tf);
    }
  }
  
  public void postCreateGUI() {
    root.setFocusTraversalPolicy(traversalPolicy);

    // set focus on the first editable field
    Component comp = traversalPolicy.getDefaultComponent(root);
    comp.requestFocusInWindow();
  }
  
  public static class MyOwnFocusTraversalPolicy extends FocusTraversalPolicy {
    Vector<Component> order;

    public MyOwnFocusTraversalPolicy(Vector<Component> order) {
      //System.out.println("Created policy with components: \n" + order);
      
      this.order = new Vector<Component>(order.size());
      this.order.addAll(order);
    }

    public Component getComponentAfter(Container focusCycleRoot,
        Component aComponent) {
      int idx = (order.indexOf(aComponent) + 1) % order.size();
      Component comp = order.get(idx);
      
      //System.out.println("Component after " + comp);
      
      return comp;
    }

    public Component getComponentBefore(Container focusCycleRoot,
        Component aComponent) {
      int idx = order.indexOf(aComponent) - 1;
      if (idx < 0) {
        idx = order.size() - 1;
      }
      
      Component comp = order.get(idx);
      
      //System.out.println("Component before " + comp);
      
      return comp;      
    }

    public Component getDefaultComponent(Container focusCycleRoot) {
      Component comp = order.get(0);
      
      //System.out.println("default component " + comp);
      
      return comp;
    }

    public Component getLastComponent(Container focusCycleRoot) {
      Component comp = order.lastElement();
      
      //System.out.println("Last component " + comp);
      
      return comp;
    }

    public Component getFirstComponent(Container focusCycleRoot) {
      Component comp = order.get(0);
      
      //System.out.println("First component " + comp);
      
      return comp;      
    }
  }
}
