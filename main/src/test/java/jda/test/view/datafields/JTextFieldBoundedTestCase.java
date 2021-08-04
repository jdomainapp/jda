package jda.test.view.datafields; 

import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.junit.BeforeClass;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JTextField;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;
import jda.test.view.datafields.JAbstractListFieldTestCase.TestDataSource;
import jda.util.SwTk;


public class JTextFieldBoundedTestCase extends JDataFieldTestCase {
  /*
   * Displays a text field bounded to a Student-typed data source that returns a collection of objects 
   * but only one of which is selected for display on the text field 
   */
  
  protected static DODMBasic schema;
  protected static Configuration config;

  protected static DAttr dcStudent;

  private static DataValidator validator;
  
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
    
    dcStudent = schema.getDsm().getDomainConstraint(JAbstractListFieldTestCase.class,
        "student");
  }
  

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "student: ", //
        "get value: ", //
        SEP, //
    };
    
    numComponents = labels.length;

    JDataField df;
    JButton b;
    JLabel sep;
    
    final Class dfClass = JTextField.class;
    
    int i = 0;
    
    // editable text field
    
    // require this!!!!
    boolean editable=false; 
    
    df = createBoundedStudentField(editable, dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    return panel;
  }  
  
  public JDataField createBoundedStudentField(boolean editable, Class dfClass) {
    List<Student> students = new Vector<>();
    Student s = new Student("John", "1/1/1990", new City("hanoi"), "john@gmail.com");
    students.add(s);
    s = new Student("Smith", "2/2/1990", new City("hcm"), "smith@gmail.com");
    students.add(s);
    s = new Student("Alex", "3/3/1989", new City("hcm"), "alex@gmail.com");
    students.add(s);

    DAttr boundConstraint = schema.getDsm().getDomainConstraint(
        Student.class, "name");
    Student initVal = students.get(0);
    
    TestDataSource ds = new TestDataSource(schema, Student.class);
    ds.setValues(students);
    
    JBindableField df = (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcStudent, boundConstraint, 
            dfClass, ds, initVal, editable);    

    df.connectDataSource();
    
    // v3.2: 
    df.setValue(initVal);
    
    return df;
  }
}
