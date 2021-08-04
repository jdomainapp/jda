package jda.test.view.datafields;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;

public abstract class JAbstractListFieldTestCase extends ViewTestCase {
  // shared variables
  protected static DODMBasic schema;
  protected static Configuration config;
  

  @DAttr(name = "year", type = Type.Integer, optional=true,length = 4, min = 1990, max = 9999)
  private Integer year;

  @DAttr(name = "month", type = Type.String, optional=false, length = 10)
  private Object month;

  @DAttr(name = "student",optional=true,type = Type.Domain)
  private Student student;

  @DAttr(name = "student2",optional=false,type = Type.Domain)
  private Student student2;

  protected static DAttr dcYear;
  protected static DAttr dcMonth;
  protected static DAttr dcStudent;
  protected static DAttr dcStudent2;

  protected static DataValidator validator;
  
  @BeforeClass
  public static void init() throws Exception {
    // register domain classes
//    config = new Configuration();
//    GUIToolkit.initInstance(config); 
//    schema = DODM.getInstance(null, false);
    config = SwTk.createMemoryBasedConfiguration("");
    GUIToolkit.initInstance(config); 
    schema = DODMBasic.getInstance(config);
    schema.addClass(Student.class);
    schema.addClass(JAbstractListFieldTestCase.class);
      
    validator = getDataValidator(schema, null);
    
    dcYear = schema.getDsm().getDomainConstraint(JAbstractListFieldTestCase.class, "year");
    dcMonth = schema.getDsm().getDomainConstraint(JAbstractListFieldTestCase.class,
        "month");
    dcStudent = schema.getDsm().getDomainConstraint(JAbstractListFieldTestCase.class,
        "student");
    dcStudent2 = schema.getDsm().getDomainConstraint(JAbstractListFieldTestCase.class,
        "student2");
  }

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JAbstractListFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JButton createAddStudentButton(final JBindableField df, final TestDataSource tds) {
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
          tds.addValue(s);
          
          // fire state change event
          ChangeEventSource ds = new ChangeEventSource();
          // add to list
          ds.add(s);
          ds.setChangeAction(LAName.New);
          // create a change event that carries the data source to the data field
          // when it is raised
          ChangeEvent changeEvent = new ChangeEvent(ds);
          df.stateChanged(changeEvent);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    
    return b;
  }
  
  public JDataField createNonEditableField(Class dfClass) {
    Vector months = getMonthStrings();
    JDataSource ds = createUnboundedDataSource(months);
    Object initVal = months.get(1);
    JBindableField df = (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcMonth, null, 
        dfClass, ds, initVal, false)
    //.createSpinnerField(validator, config, dcMonth, null, ds, false)
        ;
    df.connectDataSource();
    
    return df;
  }
  
  public JDataField createEditableField(Class dfClass) {
    Vector months = getMonthStrings();
    JDataSource ds = createUnboundedDataSource(months);
    JBindableField df = (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcMonth, null, 
        dfClass, ds, "June", true)
    //.createSpinnerField(validator, config, dcMonth, "June", ds, true)
        ;

    df.connectDataSource();
    
    return df;
  }

  public JDataField createEditableEmptyStringField(Class dfClass) {
    Vector months = new Vector();
    JDataSource ds = createUnboundedDataSource(months);
    JBindableField df =  (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcMonth, null, 
        dfClass, ds, null, true)
    //.createSpinnerField(validator,config, dcMonth, null, ds, true)
        ;

    df.connectDataSource();
    
    return df;
  }
  

  public JDataField createEditableNumericField(Class dfClass) {
    Vector years = getYears();
    Integer initVal = null; // 1990
    JDataSource ds = createUnboundedDataSource(years);
    JBindableField df = (JBindableField) 
        DataFieldFactory.createMultiValuedDataField(validator, config, dcYear, null, 
        dfClass, ds, initVal, true)
    //.createSpinnerField(validator, config, dcYear, initVal, ds, true)
        ; 

    df.connectDataSource();

    return df;
  }

  public JDataField createEditableEmptyNumericField(Class dfClass) {
    Vector years = new Vector();
    JDataSource ds = createUnboundedDataSource(years);
    JBindableField df = (JBindableField) 
        DataFieldFactory.createMultiValuedDataField(validator, config, dcYear, null, 
            dfClass, ds, null, true)
        //createSpinnerField(validator, config, dcYear, null, ds, true)
            ;
    
    df.connectDataSource();

    return df;
  }

  public JDataField createBoundedStudentField(boolean editable, Class dfClass) {
    Vector<Student> students = new Vector<Student>();
    Student s = new Student("John", "1/1/1990", new City("hanoi"), "john@gmail.com");
    students.add(s);
    s = new Student("Smith", "2/2/1990", new City("hcm"), "smith@gmail.com");
    students.add(s);
    s = new Student("Alex", "3/3/1989", new City("hcm"), "alex@gmail.com");
    students.add(s);

    DAttr boundConstraint = schema.getDsm().getDomainConstraint(
        Student.class, "id");
    Student initVal = students.get(1);
    
    JDataSource ds = createBoundedDataSource(students, Student.class);
    
    JBindableField df = (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcStudent2, boundConstraint, 
            dfClass, ds, initVal, editable);    

    df.connectDataSource();
    
    return df;
  }

  public JDataField createBoundedDerivedStudentField(boolean editable, Class dfClass) {
    Vector<Student> students = new Vector<Student>();
    Student s = new Student("John", "01/01/1990", new City("hanoi"), "john@gmail.com");
    students.add(s);
    s = new Student("Smith", "02/12/1990", new City("hcm"), "smith@gmail.com");
    students.add(s);
    s = new Student("Alex", "13/03/1989", new City("hcm"), "alex@gmail.com");
    students.add(s);

    DAttr boundConstraint = schema.getDsm().getDomainConstraint(
        Student.class, "studentInfo");
    Student initVal = students.get(1);
    
    JDataSource ds = createBoundedDataSource(students, Student.class);
    
    JBindableField df = (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcStudent2, boundConstraint, 
            dfClass, ds, initVal, editable);    

    df.connectDataSource();
    
    return df;
  }
  
  public JDataField createBoundedOptionalStudentField(Class dfClass) {
    Vector students = new Vector();

    Student s = new Student("John", "1/1/1990", new City("hanoi"), "john@gmail.com");
    students.add(s);
    s = new Student("Smith", "2/2/1990", new City("hcm"), "smith@gmail.com");
    students.add(s);
    s = new Student("Alex", "3/3/1989", new City("hcm"), "alex@gmail.com");
    students.add(s);

    DAttr boundConstraint = schema.getDsm().getDomainConstraint(
        Student.class, "id");
    Object initVal = null;
    
    JDataSource ds = createBoundedDataSource(students, Student.class);
    
    JBindableField df = (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcStudent, boundConstraint, 
            dfClass, ds, initVal, true);

    df.connectDataSource();
    
    return df;
  }

  public JDataField createEmptyStudentField(JDataSource ds, Class dfClass) {
    DAttr boundConstraint = schema.getDsm().getDomainConstraint(
        Student.class, "id");
    Object initVal = null;
        
    // creates an empty spinner data field
    JBindableField df = (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcStudent, boundConstraint, 
            dfClass, ds, initVal, true);

    // register df to be a listener for dataSource1
    ds.addBoundedComponent(df);

    df.connectDataSource();
    
    return df;
  }
  
  protected static TestDataSource createBoundedDataSource(final Collection values, final Class domainCls) {
    TestDataSource tds = new TestDataSource(schema, domainCls);
    tds.setValues(values);
    
    return tds;
  }
  
  public static TestDataSource createBoundedDataSource(DODMBasic schema, 
      final Collection values, final Class domainCls) {
    TestDataSource tds = new TestDataSource(schema, domainCls);
    tds.setValues(values);
    
    return tds;
  }
  
  /**
   * A test data source
   * @author dmle
   */
  static class TestDataSource extends JDataSource {
    private Collection values;
    
    @Override
    public void connect() throws NotPossibleException {
      //
    }
    
    public TestDataSource(DODMBasic schema, Class domainCls) {
      super(schema, domainCls);
    }
    
    public void setValues(Collection vals) {
      values = vals;
    }
    
    public void addValue(Object val) {
      if (values == null) {
        values = new ArrayList();
      }
      
      values.add(val);
    }
    
    @Override
    public Iterator iterator() {
      if (values != null)
        return values.iterator();
      else 
        return null;
    }
    
    @Override
    public boolean isEmpty() {
      return values == null || (values != null && values.isEmpty());
    }
  }
  
  
  public static JDataSource createUnboundedDataSource(final Collection values) {
    return new JDataSource() {
      
      @Override
      public Iterator iterator() {
        return values.iterator();
      }
      
      @Override
      public boolean isEmpty() {
        return values.isEmpty();
      }
    };
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
