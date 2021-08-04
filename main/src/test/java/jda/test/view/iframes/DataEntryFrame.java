package jda.test.view.iframes;

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

/* Used by InternalFrameDemo.java. */
public class DataEntryFrame extends MyInternalFrame {

  @DAttr(name="name",optional=false,type=Type.String)
  private String name;
  
  private JComponent[] comps;

  protected void createGUI() {
    DODMBasic schema = null;
//    Configuration config = new Configuration();
    Configuration config = SwTk.createMemoryBasedConfiguration("");
    GUIToolkit.initInstance(config);
    
    try {
      schema = DODMBasic.getInstance(config);
      schema.addClass(this.getClass());
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    
    DAttr dc = schema.getDsm().getDomainConstraint(this.getClass(), "name");
    
    int numFields = 3;
    comps = new JDataField[numFields];
    JPanel panel = new JPanel();
    for (int i = 0; i < numFields; i++) {
      JTextField tf = new JTextField(ViewTestCase.getDataValidator(schema,null), config, dc);
      comps[i] = tf;
      panel.add(tf);
    }

    add(panel);
  }

  public void postCreateGUI() {
    comps[1].requestFocusInWindow();
  }
}
