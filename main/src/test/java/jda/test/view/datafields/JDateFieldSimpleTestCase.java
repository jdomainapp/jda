package jda.test.view.datafields;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.junit.BeforeClass;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.GUIToolkit.LookAndFeel;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.datetime.JDateFieldSimple;

public class JDateFieldSimpleTestCase extends JAbstractListFieldTestCase {

  @DAttr(name = "dob", type = Type.Date,optional=false)
  private Date dob;

  @DAttr(name = "startDate", type = Type.Date,optional=true)
  private Date startDate;

  protected static DAttr dcDob;
  protected static DAttr dcStartDate;

  @BeforeClass
  public static void init() throws Exception {
    // register domain classes
    JAbstractListFieldTestCase.init();
    
    System.out.println(JDateFieldSimpleTestCase.class.getSimpleName() +".init()");
    
    GUIToolkit.initLookAndFeel(
        //LookAndFeel.Default
        LookAndFeel.Nimbus
        );

    schema.registerClass(JDateFieldSimpleTestCase.class);
    
    dcDob = schema.getDsm().getDomainConstraint(JDateFieldSimpleTestCase.class, "dob");
    dcStartDate = schema.getDsm().getDomainConstraint(JDateFieldSimpleTestCase.class, "startDate");
  }
  
  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());

    final String SEP = "------------------------";
    String[] labels = { // 
        "Non-optional date: ", //
        "get value: ", //
        SEP,
        "Optional date: ", //
        "get value: ", //
        "reset"
    };
    
    numComponents = labels.length;

    JDataField df;
    JButton b;
    JLabel sep;
    
    final Class dfClass = JDateFieldSimple.class;
    
    int i = 0;
    
    // dob field 
    df = DataFieldFactory.createMultiValuedDataField(validator, config, dcDob, null, 
        dfClass, null, null);
    
    // initial value 
    Calendar cal = Calendar.getInstance();
    cal.set(2015,0,1);  // 1/1/2015 
    Date initDate = cal.getTime();
    df.setValue(initDate);
    df.setEditable(false);
    createLabelledComponent(panel, labels[i++], df);
    
    // get value button
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    // separator
    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);
    
    // start date
    df = DataFieldFactory.createMultiValuedDataField(validator, config, dcStartDate, null, 
        dfClass, null, null);
    createLabelledComponent(panel, labels[i++], df);
    
    // get value button
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    // reset button
    b = createResetButton(df);
    createLabelledComponent(panel, labels[i++], b);
    
    return panel;
  }
}
