package jda.test.view.datafields; 

import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Format;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.ds.viewable.JSingleValueDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.report.model.stats.StatCount;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.text.JTextFieldAuto;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;

public class JTextFieldBoundedSingleSourceTestCase extends ViewTestCase {
  /*
   * Displays a text field bounded to a data source that returns either null or a single object
   */
  
  @DAttr(name = "name", type = Type.String, optional=false)
  private String name;

  @DAttr(name = "date", type = Type.Date, format=Format.Date,
      optional=false)
  private Date date;

  @DAttr(name = "qty", type = Type.Integer, 
      optional=false, length=4, min=0, max=1000)
  private Integer qty;

  @DAttr(name = "unitPrice", type = Type.Double, optional=false)
  private Double unitPrice;

  @DAttr(name = "amount", type = Type.Double, optional=false)
  private Double amount;

  @DAttr(name = "statCount",type = Type.Domain,optional=false)
  private StatCount statCount;

  @DAttr(name = "statCount2",type = Type.Domain,optional=false)
  private StatCount statCount2;

  private static DODMBasic schema;
  private static Configuration config;
  private static DAttr dcName;
  private static DAttr dcDate;
  private static DAttr dcQty;
  private static DAttr dcPrice;
  private static DAttr dcAmount;

  private static DAttr dcStatCount;
  private static DAttr dcStatCount2;
  
  @BeforeClass
  public static void init() throws Exception {
    // register domain classes
    //config = new Configuration();
    config = SwTk.createMemoryBasedConfiguration("");
    GUIToolkit.initInstance(config);
    
    schema = DODMBasic.getInstance(config);
    schema.registerClass(JTextFieldBoundedSingleSourceTestCase.class);
    
    dcName = schema.getDsm().getDomainConstraint(JTextFieldBoundedSingleSourceTestCase.class,
        "name");
    dcDate = schema.getDsm().getDomainConstraint(JTextFieldBoundedSingleSourceTestCase.class,
        "date");
    dcQty = schema.getDsm().getDomainConstraint(JTextFieldBoundedSingleSourceTestCase.class,
        "qty");
    dcPrice = schema.getDsm().getDomainConstraint(JTextFieldBoundedSingleSourceTestCase.class,
        "unitPrice");
    dcAmount = schema.getDsm().getDomainConstraint(JTextFieldBoundedSingleSourceTestCase.class,
        "amount");
    
    dcStatCount = schema.getDsm().getDomainConstraint(JTextFieldBoundedSingleSourceTestCase.class,
        "statCount");

    dcStatCount2 = schema.getDsm().getDomainConstraint(JTextFieldBoundedSingleSourceTestCase.class,
        "statCount2");
  }

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JTextFieldBoundedSingleSourceTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "statValue1: ", //
        "get value: ", //
        SEP, //
        "statValue2: ", //
        "get value: ", //
        SEP, //
    };
    
    numComponents = labels.length;

    JDataField df;
    JButton b;
    JLabel sep;
    int i = 0;

    // name
    Class c = StatCount.class;
    schema.registerClass(c);
    DAttr boundedDc = schema.getDsm().getDomainConstraint(c, "value");

    // first text field
    StatCount value = new StatCount("children",15);
    JSingleValueDataSource ds = createSingleValueDataSource(schema, c, value);
    df = createBoundedTextField(dcStatCount, boundedDc, ds, null, false);
    b = createValueButton(df);
    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], df);
    createLabelledComponent(panel, labels[i++], b);
    createLabelledComponent(panel, labels[i++], sep);
    
    // second text field
    value = new StatCount("adults",50);
    ds = createSingleValueDataSource(schema, c, value);
    df = createBoundedTextField(dcStatCount2, boundedDc, ds, null, false);
    b = createValueButton(df);
    sep = new JLabel(SEP);
    
    createLabelledComponent(panel, labels[i++], df);
    createLabelledComponent(panel, labels[i++], b);
    createLabelledComponent(panel, labels[i++], sep);
//    
//    
//    // date 
//    Calendar cal = Calendar.getInstance();
//    Date today = cal.getTime();
//
//    df = createFormattedTextField(dcDate, today);
//    b = createValueButton(df);
//    sep = new JLabel(SEP);
//    createLabelledComponent(panel, labels[i++], df);
//    createLabelledComponent(panel, labels[i++], b);
//    createLabelledComponent(panel, labels[i++], sep);
//
//    // qty
//    Integer qty = null;
//    df = createFormattedTextField(dcQty, qty);
//    sep = new JLabel(SEP);
//    b = createValueButton(df);
//    createLabelledComponent(panel, labels[i++],df);
//    createLabelledComponent(panel, labels[i++], b);
//    createLabelledComponent(panel, labels[i++], sep);
//
//    // price
//    Double price = null;
//    df = createFormattedTextField(dcPrice, price);
//    sep = new JLabel(SEP);
//    b = createValueButton(df);
//    createLabelledComponent(panel, labels[i++],df);
//    createLabelledComponent(panel, labels[i++], b);
//    createLabelledComponent(panel, labels[i++], sep);
//
//    // amount
//    Double amount = null;
//    df = createFormattedTextField(dcAmount, amount);
//    b = createValueButton(df);
//    createLabelledComponent(panel, labels[i++],df);
//    createLabelledComponent(panel, labels[i++], b);

    return panel;
  }

  private JSingleValueDataSource createSingleValueDataSource(
      DODMBasic schema2, Class c,
      final Object value) {
    // create a test data source
    JSingleValueDataSource ds = new JSingleValueDataSource(schema2, c) {
      
      @Override
      public void connect() throws NotPossibleException {
        // assumes connected
      }
      
      @Override
      public Object loadObject() throws NotPossibleException {
        return value;
      }
    };
    
    return ds;
  }

  public JBindableField createBoundedTextField(DAttr dc,
      DAttr boundDc, 
      JSingleValueDataSource ds, 
      Object initVal, boolean editable) {
    JBindableField df = (JBindableField) DataFieldFactory.
        createSingleValuedDataField(
            getDataValidator(schema,null), 
            config, 
            dc, boundDc,
            //v3.2: JTextField.class,
            JTextFieldAuto.class,
            ds,
            initVal, editable);
    
    df.connectDataSource();
    
    return df;
  }
}
