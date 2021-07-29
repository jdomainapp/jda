package jda.test.view.datafields;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JSpinnerField;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;
import jda.test.view.ViewTestCase;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;

public class JSpinnerFieldTestCase extends JAbstractListFieldTestCase {
  
  public static void main(String[] args) throws Exception {
    String thisClass = JSpinnerFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "non-editable jspinner: ", //
        "get value: ", //
        SEP, //
        "[-] editable month jspinner: ", //
        SEP, //
        "[-] editable (empty) month list: ", //
        "get value: ", //
        SEP, //
        "[-] editable year list: ", //
        "get value: ", //
        SEP, //
        "[-] editable, empty year list: ", //
        "get value: ", //
        SEP, //
        "bounded field: ", //
        "get value: ", //
        SEP, //
        "bounded (optional) field: ", //
        "get value: ", //
        SEP, //
        "updatable student list: ", //
        "populate values: ", //
        "get value: ", //
        SEP, //
        "incremental student list: ", //
        "add an object: ", //
        "get value: ", //
    };
    
    numComponents = labels.length;

    JDataField df;
    JButton b;
    JLabel sep;
    
    final Class dfClass = JSpinnerField.class;
    
    int i = 0;
    // non editable spinner
    df = createNonEditableField(dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);
    
    // editable, pre-filled String spinner
    createLabelledComponent(panel, labels[i++], createEditableField(dfClass));

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // editable & empty String spinner
    df = createEditableEmptyStringField(dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // editable numeric spinner
    df = createEditableNumericField(dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // editable, empty numeric spinner
    df = createEditableEmptyNumericField(dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // bounded spinner
    df = createBoundedStudentField(true, dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // bounded spinner
    df = createBoundedOptionalStudentField(dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);
    
    // empty, bounded and updatable spinner
    final TestDataSource ds1 = createBoundedDataSource(null, Student.class);
    df = createEmptyStudentField(ds1, dfClass);
    
    createLabelledComponent(panel, labels[i++], df);
    
    final JSpinnerField fdf5 = (JSpinnerField) df;
    
    b = new JButton("populate...");
    //final List sts = students;
    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          ChangeEventSource ces = new ChangeEventSource();
          
          // now populate the vector with some objects and update the data field
          Student s = new Student("John", "1/1/1990", new City("hanoi"), "john@gmail.com");
          ds1.addValue(s);
          ces.add(s);
          s = new Student("Smith", "2/2/1990", new City("hcm"), "smith@gmail.com");
          ds1.addValue(s);
          ces.add(s);
          s = new Student("Alex", "3/3/1989", new City("hcm"), "alex@gmail.com");
          ds1.addValue(s);
          ces.add(s);

          ces.setChangeAction(LAName.New);
          ChangeEvent ce = new ChangeEvent(ces);
          fdf5.stateChanged(ce);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    createLabelledComponent(panel, labels[i++], b);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);
    
    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // empty, bounded and incrementally updatable spinner
    TestDataSource tds2 = createBoundedDataSource(null, Student.class);
    df = createEmptyStudentField(tds2, dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createAddStudentButton((JSpinnerField)df, tds2);
    createLabelledComponent(panel, labels[i++], b);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    return panel;
  }
}
