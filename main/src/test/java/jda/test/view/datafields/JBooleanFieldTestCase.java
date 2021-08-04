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
import jda.mosa.view.assets.datafields.JBooleanField;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;

public class JBooleanFieldTestCase extends JDataFieldTestCase {

  @DAttr(name="count",type=Type.Boolean, length=5)
  private Boolean tf;

  private static DODMBasic schema;
  private static Configuration config; 
  private static DAttr dcTf;

  @BeforeClass
  public static void init() throws Exception {
    //config = new Configuration();
    config = SwTk.createMemoryBasedConfiguration("");
    config.setLanguage(
        Configuration.Language.Vietnamese
//        Configuration.Language.English
        );
    
    GUIToolkit.initInstance(config);
    
    // register domain classes
    schema = DODMBasic.getInstance(config);
    //schema = schema.getInstance(null, false);
    schema.addClass(JBooleanFieldTestCase.class);
    
    dcTf = schema.getDsm().getDomainConstraint(JBooleanFieldTestCase.class, "tf");
  }

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JBooleanFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "true or false", //
        "get value: ", //
    };
    
    numComponents = labels.length;

    int i = 0;
    
    Boolean val = true;
    JBooleanField df = (JBooleanField) 
        DataFieldFactory.createMultiValuedDataField(
            getDataValidator(schema,null), 
            config, 
            dcTf, 
            JBooleanField.class, 
            val, 
            true);

    df.connectDataSource();
    df.reset();
    
    createLabelledComponent(panel, labels[i++], df);
    JButton b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    return panel;
  } 
}
