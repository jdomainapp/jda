package jda.test.view.datafields;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.junit.BeforeClass;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JSpinnerField;
import jda.test.model.extended.City;
import jda.test.model.extended.SClass;
import jda.test.model.extended.Student;
import jda.test.view.ViewTestCase;
import jda.util.SwTk;

public class JSpinnerFieldWithAssociationConstraintTestCase extends JAbstractListFieldTestCase {

  @DAttr(name = "student",optional=true,type = Type.Domain)
  private Student student;

  private static DAttr dcStudent;
  protected static DAttr dcStudentSClass;

  @BeforeClass
  public static void init() throws Exception {
     //register domain classes
//    config = new Configuration();
//    GUIToolkit.initInstance(config); 
//    schema = DODM.getInstance(null, false);
    config = SwTk.createMemoryBasedConfiguration("");
    GUIToolkit.initInstance(config); 
    schema = DODMBasic.getInstance(config);
    
    schema.addClass(SClass.class);
    schema.addClass(Student.class);
    schema.addClass(JSpinnerFieldWithAssociationConstraintTestCase.class);
    
    dcStudent = schema.getDsm().getDomainConstraint(JSpinnerFieldWithAssociationConstraintTestCase.class, "student");
    dcStudentSClass = schema.getDsm().getDomainConstraint(Student.class, "sclass");
  }
  
  public static void main(String[] args) throws Exception {
    String thisClass = JSpinnerFieldWithAssociationConstraintTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "Students: ", //
        SEP, //
        "Classes: ", //
        "get value: ", //
        "commit:",
    };
    
    numComponents = labels.length;

    JDataField df;
    JButton b;
    JLabel sep;
    
    int i = 0;

    List<SClass> sclasses = initSClasses();

    // students spinner
    List<Student> students = initStudents(sclasses);
    DataValidator validator = getDataValidator(schema, null);
    JDataField dfStudent = createBoundedStudentJSpinner(students, validator, true); createLabelledComponent(panel, labels[i++], dfStudent);
    
    //b = createValueButton(df); createLabelledComponent(panel, labels[i++], b);
    sep = new JLabel(SEP); createLabelledComponent(panel, labels[i++], sep);


    // sclasses spinner
    validator = getDataValidator(schema, Student.class);
    JBindableField dfSClass = (JBindableField) createBoundedSClassJSpinner(sclasses, validator, true); createLabelledComponent(panel, labels[i++], dfSClass);
    
    // get value button
    b = createSClassValueButton(dfSClass); createLabelledComponent(panel, labels[i++], b);
    
    // commit value button
    b = createAddStudentToClassButton(dfSClass, dfStudent, validator, "..."); createLabelledComponent(panel, labels[i++], b);

    return panel;
  }
  
  protected JButton createSClassValueButton(final JBindableField f) {
    JButton b = new JButton("value...");

    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          SClass v = (SClass) f.getValue();
          
          if (v != null)            
            System.out.println("value: " + v + " (Class: " + v.getClass() + ")" + 
          "\nStudents count: " + v.getStudentsCount());
          else
            System.out.println("value: " + null);
        } catch (ConstraintViolationException ex) {
          System.err.println(ex.getMessage());
        }
      }
    });
    
    return b;
  }
  
  public JDataField createBoundedStudentJSpinner(List<Student> students, DataValidator validator, boolean editable) {
    DAttr boundConstraint = schema.getDsm().getDomainConstraint(
        Student.class, "id");
    
    Student initVal = students.get(0);
    
    JDataSource ds = createBoundedDataSource(students, Student.class);
    
    JBindableField df = (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcStudent, boundConstraint, 
            JSpinnerField.class, ds, initVal, editable);    

    df.connectDataSource();
    
    return df;
  }
  
  public JButton createAddStudentToClassButton(final JBindableField dfSClass, final JDataField dfStudent, 
      final DataValidator sclassValidator, String label) {
    // create a button, which commits the currently selected value of the data field 
    
    JButton b = new JButton(label);
    
    b.addActionListener(new ActionListener() {
      private DataValidator sClassValidator = sclassValidator;
      public void actionPerformed(ActionEvent e) {
        // this involves checking the selected value and committing it
        try {
          SClass c = (SClass) dfSClass.getValue();
          
          // add current student to this class
          Student s = (Student) dfStudent.getValue();
          SClass c1 = s.getSclass();
          if (c1 != c) {
            // on delete
            sclassValidator.validateBoundedValueOnDelete(dfSClass, c1);
            
            // on create (already validated by dfSClass.getValue above?
            
            // shift student
            s.setSclass(c);
            
            c.addStudent(s);
            c1.removeStudent(s);
            
            int count = c.getStudentsCount();
            int count1 = c1.getStudentsCount();
            
            displayMessage("Shifted: " + s + 
                "\nFrom: " + c1 + " (count*: " + count1 + ")" + 
                "\nTo: " + c + " (count*: " + count + ")");
          } else {
            displayWarning("Student: " + s + "\nalready in class: " + c);
          }
        } catch (ConstraintViolationException ex) {
          displayError(ex);
        }
      }
    });
    
    return b;
  }
  
  public JDataField createBoundedSClassJSpinner(List<SClass> classes, 
      DataValidator validator, boolean editable) {

    DAttr boundConstraint = schema.getDsm().getDomainConstraint(
        SClass.class, "name");
    
    SClass initVal = classes.get(1);
    
    JDataSource ds = createBoundedDataSource(classes, SClass.class);
    
    JBindableField df = (JBindableField) DataFieldFactory.
        createMultiValuedDataField(validator, config, dcStudentSClass, boundConstraint, 
            JSpinnerField.class, ds, initVal, editable);    

    df.connectDataSource();
    
    return df;
  }
  
  private List<SClass> initSClasses() {
    List<SClass> classes = new ArrayList<SClass>();
    
    SClass sc = new SClass(1, "class #1");
    classes.add(sc);
    sc = new SClass(2, "class #2");
    classes.add(sc);
    sc = new SClass(3, "class #3");
    classes.add(sc);
    sc = new SClass(4, "class #4");
    classes.add(sc);
    
    return classes;
  }
  
  private List<Student> initStudents(List<SClass> classes) {
    Vector<Student> students = new Vector<Student>();
    SClass sclass;
    City city;
    
    sclass = classes.get(0);
    city = new City("hanoi"); 
    Student s = new Student("John", "1/1/1990", city, "john@gmail.com", sclass);
    students.add(s);
  
    sclass.addStudent(s);
    
    //sclass = classes.get(0);
    city = new City("hcm");
    s = new Student("Smith", "2/2/1990", city, "smith@gmail.com", sclass);
    students.add(s);
    sclass.addStudent(s);
    
    sclass = classes.get(1);
    s = new Student("Alex", "3/3/1989", city, "alex@gmail.com", sclass);
    students.add(s);
    sclass.addStudent(s);

    sclass = classes.get(1);
    s = new Student("Peter", "4/4/1989", city, "peter@gmail.com", sclass);
    students.add(s);
    sclass.addStudent(s);

    sclass = classes.get(2);
    s = new Student("Steve", "5/5/1989", city, "steve@gmail.com", sclass);
    students.add(s);
    sclass.addStudent(s);

    return students;
  }
}
