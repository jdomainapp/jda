package jda.test.view.datafields; 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Format;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JSimpleFormattedField;
import jda.mosa.view.assets.datafields.JSpinnerField;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.datafields.text.JFormattedField;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;

public class JFormattedFieldTestCase extends ViewTestCase {

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

  @DAttr(name = "fixedPhone", type = Type.String, optional=false,length=20, 
      format=DAttr.Format.FixedPhone)
  private String fixedPhone;

  @DAttr(name = "cellPhone", type = Type.String, optional=false,length=20, 
      format=DAttr.Format.CellPhone)
  private String cellPhone;

  private static DODMBasic schema;
  private static Configuration config;
  private static DAttr dcName;
  private static DAttr dcDate;
  private static DAttr dcQty;
  private static DAttr dcPrice;
  private static DAttr dcAmount;
  private static DAttr dcFixedPhone;
  private static DAttr dcCellPhone;

  @BeforeClass
  public static void init() throws Exception {
    // register domain classes
//    config = new Configuration();
//    GUIToolkit.initInstance(config);
    
    config = SwTk.createMemoryBasedConfiguration("");
    GUIToolkit.initInstance(config); 
    
    //GUIToolkit.initLookAndFeel();
    
    schema = DODMBasic.getInstance(config);
    //schema = DODM.getInstance(null, false);
    schema.addClass(JFormattedFieldTestCase.class);
    
    dcName = schema.getDsm().getDomainConstraint(JFormattedFieldTestCase.class,
        "name");
    dcDate = schema.getDsm().getDomainConstraint(JFormattedFieldTestCase.class,
        "date");
    dcQty = schema.getDsm().getDomainConstraint(JFormattedFieldTestCase.class,
        "qty");
    dcPrice = schema.getDsm().getDomainConstraint(JFormattedFieldTestCase.class,
        "unitPrice");
    dcAmount = schema.getDsm().getDomainConstraint(JFormattedFieldTestCase.class,
        "amount");
    dcFixedPhone = schema.getDsm().getDomainConstraint(JFormattedFieldTestCase.class,
        "fixedPhone");
    dcCellPhone = schema.getDsm().getDomainConstraint(JFormattedFieldTestCase.class,
        "cellPhone");
  }

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JFormattedFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "name: ", //
        "get value: ", //
        SEP, //
        "date: ", //
        "get value: ", //
        SEP, //
        "quantity: ", //
        "get value: ", //
        SEP, //
        "unit price: ", //
        "get value: ", //
        SEP, //
        "amount: ", //
        "get value: ", //
        SEP, //
        "fixed phone: ", //
        "get value: ", //
        SEP, //
        "cell phone: ", //
        "get value: ", //
    };
    
    numComponents = labels.length;

    JDataField df;
    JButton b;
    JLabel sep;
    int i = 0;

    // name
    String name = "Lê Minh Đức";
    df = createTextField(dcName, name);
    b = createValueButton(df);
    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], df);
    createLabelledComponent(panel, labels[i++], b);
    createLabelledComponent(panel, labels[i++], sep);
    
    
    // date 
    Calendar cal = Calendar.getInstance();
    Date today = cal.getTime();

    df = createFormattedTextField(dcDate, today, JFormattedField.class);
    b = createValueButton(df);
    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], df);
    createLabelledComponent(panel, labels[i++], b);
    createLabelledComponent(panel, labels[i++], sep);

    // qty
    Integer qty = null;
    df = createFormattedTextField(dcQty, qty, JSimpleFormattedField.class);
    sep = new JLabel(SEP);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++],df);
    createLabelledComponent(panel, labels[i++], b);
    createLabelledComponent(panel, labels[i++], sep);

    // price
    Double price = null;
    df = createFormattedTextField(dcPrice, price, JSimpleFormattedField.class);
    sep = new JLabel(SEP);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++],df);
    createLabelledComponent(panel, labels[i++], b);
    createLabelledComponent(panel, labels[i++], sep);

    // amount
    Double amount = null;
    df = createFormattedTextField(dcAmount, amount, JSimpleFormattedField.class);
    b = createValueButton(df);
    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++],df);
    createLabelledComponent(panel, labels[i++], b);
    createLabelledComponent(panel, labels[i++], sep);

    // fixed phone
    String phone = "0431245678"; //"(04) 31245678";
    final String phone2 = "0412345678"; //"(04) 12345678";
    final JFormattedField dfPhone = (JFormattedField) createFormattedTextField(dcFixedPhone, phone, JFormattedField.class);
    b = createValueButton(dfPhone);
    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++],dfPhone);
    createLabelledComponent(panel, labels[i++], b);
    createLabelledComponent(panel, labels[i++], sep);
    
    // cell phone
    String cellPhone = null;//"(04)-31245678";
    df = createFormattedTextField(dcCellPhone, cellPhone, JFormattedField.class);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++],df);
    createLabelledComponent(panel, labels[i++], b);
    
    // simulate the effect of setting formatted value at run-time
    new Thread() {
      public void run() {
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        dfPhone.setValue(phone2);
      }
    }.start();
    
    return panel;
  }


  public JDataField createFormattedTextField(DAttr dc, Object val, Class<? extends JTextField> dataFieldClass) {
    JDataField df = DataFieldFactory.
        createSingleValuedDataField(getDataValidator(schema,null), config, dc, dataFieldClass, 
            val, false, true, true);
    
    // v3.0: to use default value if any
    if (val != null)
      df.reset();
    
    return df;
  }
  
  public JDataField createTextField(DAttr dc, Object val) {
    JDataField df = DataFieldFactory.
        createSingleValuedDataField(getDataValidator(schema,null), config, dc, JTextField.class, 
            val, false, true, true);
    
    // v3.0: to display default val if any
    if (val != null)
      df.reset();
    
    return df;
  }

  protected JButton createValueButton(final JDataField f) {
    JButton b = new JButton("value...");

    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Object v = f.getValue();
          if (v != null)            
            System.out.println("value: " + v + " (Class: " + v.getClass() + ")");
          else
            System.out.println("value: " + null);
        } catch (ConstraintViolationException ex) {
          System.err.println(ex.getMessage());
        }
      }
    });
    
    return b;
  }
  
  protected JButton createAddValueButton(final JSpinnerField df, final List<Student> students) {
//    // create a JDataSource to wrap around the object list
    ChangeEventSource ds = new ChangeEventSource(Student.class) {
      public List getObjects() {
        // return the object list being used
        return students;
      }
      
      @Override
      public boolean isAddNew() {
        return true;
      }

      @Override
      public boolean isDelete() {
        return false;
      }
    };
    
    // create a change event that carries the data source to the data field
    // when it is raised
    final ChangeEvent changeEvent = new ChangeEvent(ds); //new ChangeEvent(ds);
    

    // create a button, which adds a new object to the list and 
    // and invoke the stateChanged method on the data field. 
    // In an actual application, this is invoked indirectly via 
    // addListener() method  
    
    JButton b = new JButton("add...");
    
    b.addActionListener(new ActionListener() {
      int count = 0;
      public void actionPerformed(ActionEvent e) {
        try {
          count++;
          // create object 
          Student s = new Student("John"+count, "1/1/"+(1990+count), new City("hanoi"), "j"+count+"@gmail.com");
          System.out.println("new object: " + s);
          // add to list
          students.add(s);
          // fire state change event
          df.stateChanged(changeEvent);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    
    return b;
  }
  
  protected void createLabelledComponent(JPanel panel, String label,
      JComponent comp) {
    JLabel l = new JLabel(label);
    l.setLabelFor(comp);
    panel.add(l);
    panel.add(comp);
  }
}
