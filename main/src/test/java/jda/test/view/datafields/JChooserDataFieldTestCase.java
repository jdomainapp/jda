package jda.test.view.datafields;

import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.chooser.JColorChooserField;
import jda.mosa.view.assets.datafields.chooser.JFileChooserField;
import jda.mosa.view.assets.datafields.chooser.JImageChooserField;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;

public class JChooserDataFieldTestCase extends JDataFieldTestCase {

  @DAttr(name = "color", type = Type.Color, optional=true, length=25)
  private Object color;

  @DAttr(name = "file", type = Type.File, optional=true, length=25)
  private Object file;

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
    schema.addClass(JChooserDataFieldTestCase.class);
    
    dcColor = schema.getDsm().getDomainConstraint(JChooserDataFieldTestCase.class, "color");
    dcFile = schema.getDsm().getDomainConstraint(JChooserDataFieldTestCase.class, "file");
  }

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JChooserDataFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "color tf: ", //
        "get value: ", //
        SEP, //
        "file tf: ", //
        "get value: ", //
        SEP, //
        "image tf: ", //
        "get value: ", //
    };
    
    numComponents = labels.length;

    JDataField df;

    // color
    int i = 0;
    df = createColorTextField();
    createLabelledComponent(panel, labels[i++], df);
    JButton b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    // sep
    JLabel sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // file
    df = createFileTextField();
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    // sep
    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // image
    df = createImageTextField();
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    return panel;
  }
  
  private JColorChooserField createColorTextField() {
    JColorChooserField df = (JColorChooserField) DataFieldFactory.
        //createColorChooserField(schema, dcColor,Color.BLUE);
        createSingleValuedDataField(getDataValidator(schema,null), config, dcColor, JColorChooserField.class, 
            Color.BLUE, true);
    
    // v3.0
    df.reset();

    return df;
  }
  
  private JFileChooserField createFileTextField() {
    JFileChooserField df = (JFileChooserField) DataFieldFactory.
        //createFileChooserField(schema, dcFile, null);
        createSingleValuedDataField(getDataValidator(schema,null), config, dcFile, JFileChooserField.class, 
            null, true);
    
    // v3.0
    df.reset();

    return df;
  }
  
  private JImageChooserField createImageTextField() {
    // use an image
    URL imgURL = this.getClass().getResource("/images/test/sprite.jpeg");
    ImageIcon img = null;
    if (imgURL != null) {
      img = GUIToolkit.getImageIcon(imgURL.getPath(), null);
    }

    JImageChooserField df = (JImageChooserField) DataFieldFactory.
        //createImageChooserField(schema, dcFile, img);
        createSingleValuedDataField(getDataValidator(schema,null), config, dcFile, JImageChooserField.class, 
            img, true);
    
    // v3.0
    df.reset();
    return df;
  }
  
}
