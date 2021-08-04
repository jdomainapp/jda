package jda.test.view.datafields;

import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.file.JFileDownloadField;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;

/**
 * 
 * @overview
 *  Test {@link JFileDownloadField}
 *  
 * @author dmle
 */
public class JFileDownloadFieldTestCase extends JDataFieldTestCase {

  @DAttr(name = "file", type = Type.File, optional=true, length=25)
  private File file;

  private static DODMBasic schema;
  private static Configuration config;
  private static DAttr dcColor;
  private static DAttr dcFile;

  @BeforeClass
  public static void init() throws Exception {
    // register domain classes
//    config = new Configuration();
    config = SwTk.createMemoryBasedConfiguration("");
    GUIToolkit.initInstance(config);
    schema = DODMBasic.getInstance(config);
    //schema = schema.getInstance(null, false);
    schema.addClass(JFileDownloadFieldTestCase.class);
    
    dcFile = schema.getDsm().getDomainConstraint(JFileDownloadFieldTestCase.class, "file");
  }

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JFileDownloadFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "file tf: ", //
        "get value: ", //
    };
    
    numComponents = labels.length;

    JDataField df;
    JButton b;
    
    int i = 0;
    
    // file
    df = createFileDownloadField();
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

//    // sep
//    sep = new JLabel(SEP);
//    createLabelledComponent(panel, labels[i++], sep);

    return panel;
  }

  private JFileDownloadField createFileDownloadField() {
    // use an image
    URL url = this.getClass().getResource("/images/test/sprite.jpeg");
    File file = null;
    if (url != null) {
      file = ToolkitIO.getFile(url.getPath());
    }

    JDataField df = DataFieldFactory.
        createSingleValuedDataField(getDataValidator(schema,null), config, dcFile, JFileDownloadField.class, 
            file, true);
    
    // v3.0
    df.reset();
    return (JFileDownloadField) df;
  }
  
}
