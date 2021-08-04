package jda.test.view.datafields;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JCounterField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;

public class JCounterFieldTestCase extends JDataFieldTestCase {

  @DAttr(name="count",type=Type.Integer, length=5)
  private Integer count;

  private static DODMBasic schema;
  private static Configuration config; 
  private static DAttr dcCount;

  @BeforeClass
  public static void init() throws Exception {
    config = new Configuration();
    GUIToolkit.initInstance(config);
    
    // register domain classes
    Configuration config = SwTk.createMemoryBasedConfiguration("");
    schema = DODMBasic.getInstance(config);
    //schema = schema.getInstance(null, false);
    schema.addClass(JCounterFieldTestCase.class);
    
    dcCount = schema.getDsm().getDomainConstraint(JCounterFieldTestCase.class, "count");
  }

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JCounterFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "counter", //
        "get value: ", //
    };
    
    numComponents = labels.length;

    JDataField df;

    // product images
    int i = 0;
    
    Integer initVal = null;
    boolean editable = true;
    df = //DataFieldFactory.createCounterField(schema, dcCount, i);
        DataFieldFactory.createSingleValuedDataField(getDataValidator(schema,null), config, dcCount,  
            JCounterField.class, initVal, editable);
    createLabelledComponent(panel, labels[i++], df);
    JButton b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    return panel;
  } 
}
