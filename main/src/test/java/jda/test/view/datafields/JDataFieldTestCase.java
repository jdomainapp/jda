package jda.test.view.datafields;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JSpinnerField;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;

public class JDataFieldTestCase extends ViewTestCase {

  @DAttr(name = "year", type = Type.Integer, optional=true,length = 4, min = 1990, max = 9999)
  private Integer year;

  @DAttr(name = "month", type = Type.String, optional=false, length = 10)
  private Object month;

  @DAttr(name = "student",optional=true,type = Type.Domain)
  private Student student;

  private static DODMBasic schema;
  private static Configuration config;
  private static DAttr dcYear;
  private static DAttr dcMonth;
  private static DAttr dcStudent;

  @BeforeClass
  public static void init() throws Exception {
    // register domain classes
    config = SwTk.createMemoryBasedConfiguration("");
    GUIToolkit.initInstance(config); 
    schema = DODMBasic.getInstance(config);
    //config = new Configuration();
    //schema = DODM.getInstance(null, false);
    schema.addClass(Student.class);
    schema.addClass(JDataFieldTestCase.class);
    
    
    dcYear = schema.getDsm().getDomainConstraint(JDataFieldTestCase.class, "year");
    dcMonth = schema.getDsm().getDomainConstraint(JDataFieldTestCase.class,
        "month");
    dcStudent = schema.getDsm().getDomainConstraint(JDataFieldTestCase.class,
        "student");
    
  }

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JDataFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "editable tf: ", //
        SEP, //
        "editable, non-auto, tf: ", //
        "get value: ", //
        SEP, //
        "non-editable tf: ", //
        SEP, //
        "password tf: ", //
    };
    
    numComponents = labels.length;

    JDataField df;

    // editable text field
    int i = 0;
    createLabelledComponent(panel, labels[i++], createEditableTextField());
    JLabel sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    df = createEditableNonAutoTextField();
    createLabelledComponent(panel, labels[i++], df);
    JButton b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);
    
    // non editable text field
    createLabelledComponent(panel, labels[i++], createNonEditableTextField());
    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // password field
    createLabelledComponent(panel, labels[i++], createPasswordField());

    return panel;
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

  public JDataField createEditableTextField() {
    JDataField df = DataFieldFactory.createTextField(getDataValidator(schema,null), config, dcYear);//new JDataField(schema, dcYear);
    return df;
  }

  public JDataField createEditableNonAutoTextField() {
    final JDataField df = DataFieldFactory.createTextField(getDataValidator(schema,null), config, 
        dcYear, null, true, false);
      //new JDataField(schema, dcYear, false);
    return df;
  }

  public JDataField createNonEditableTextField() {
    JDataField df = DataFieldFactory.createTextField(getDataValidator(schema,null), config, dcYear, 2012, false); 
        //new JDataField(schema, 2012, dcYear, false);
    return df;
  }

  public JDataField createPasswordField() {
    JDataField df = DataFieldFactory.createPasswordField(getDataValidator(schema,null), config, null, null);
    return df;
  }

  
  static Vector getYears() {
    Vector years = new Vector();
    Collections.addAll(years, new Integer[] { 1990, 1991, 1992 });
    return years;
  }

  static protected Vector getMonthStrings() {
    String[] months = new java.text.DateFormatSymbols().getMonths();

    int lastIndex = months.length - 1;

    Vector mv = new Vector();

    if (months[lastIndex] == null || months[lastIndex].length() <= 0) { // last
                                                                        // item
                                                                        // empty
      String[] monthStrings = new String[lastIndex];
      System.arraycopy(months, 0, monthStrings, 0, lastIndex);

      Collections.addAll(mv, monthStrings);
    } else { // last item not empty
      Collections.addAll(mv, months);
    }
    return mv;
  }
}
