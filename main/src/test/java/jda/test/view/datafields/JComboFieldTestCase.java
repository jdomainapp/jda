package jda.test.view.datafields;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;
import jda.test.view.ViewTestCase;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;

public class JComboFieldTestCase extends JAbstractListFieldTestCase  {

  public static void main(String[] args) throws Exception {
    String thisClass = JComboFieldTestCase.class.getName();
    ViewTestCase.main(new String[] { thisClass });
  }

  protected JComponent getContent() {
    JPanel panel = new JPanel(new SpringLayout());
    final String SEP = "------------------------";
    String[] labels = { // 
        "String (month) combo: ", //
        "get value: ", //
        SEP, //
        "Number (year) combo: ", //
        "get value: ", //
        SEP, //
        "bounded combo: ", //
        "get value: ", //
        SEP, //
        "bounded (optional) combo: ", //
        "get value: ", //
        SEP, //
        "bounded (derived) combo: ", //
        "get value: ", //
        SEP, //
        "updatable combo: ", //
        "add all values: ", //
        "get value: ", //
        SEP, //
        "incremental updated combo: ", //
        "add value: ", //
        "get value: ", //
    };
    
    numComponents = labels.length;

    JDataField df;
    JButton b;
    JLabel sep;
    
    final Class dfClass = JComboField.class;
    
    int i = 0;
    // string combo
    df = createNonEditableField(dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);
    
    // number combo
    df = createEditableNumericField(dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // bounded combo
    df = createBoundedStudentField(true,dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);

    // bounded optional combo 
    df = createBoundedOptionalStudentField(dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);
    
    // bounded (derived) combo 
    df = createBoundedDerivedStudentField(true, dfClass);
    createLabelledComponent(panel, labels[i++], df);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    sep = new JLabel(SEP);
    createLabelledComponent(panel, labels[i++], sep);
    
    // empty, bounded and updatable combo
    final TestDataSource ds1 = createBoundedDataSource(null, Student.class);
    df = createEmptyStudentField(ds1,dfClass);
    createLabelledComponent(panel, labels[i++], df);
    final JBindableField fdf5 = (JBindableField) df;
    
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

    // bounded and incrementally updatable combo
    TestDataSource tds2 = createBoundedDataSource(null, Student.class);
    df = createEmptyStudentField(tds2, dfClass);
    df.addComponentListener(new ComponentEventHandler());
    createLabelledComponent(panel, labels[i++], df);
    b = createAddStudentButton((JBindableField)df, tds2);
    createLabelledComponent(panel, labels[i++], b);
    b = createValueButton(df);
    createLabelledComponent(panel, labels[i++], b);

    return panel;
  }

  private class ComponentEventHandler extends ComponentAdapter {
    @Override
    public void componentResized(ComponentEvent e) {
      // TODO Auto-generated method stub
      Component c = e.getComponent();
      System.out.printf("%s.resize: %s%n", c, c.getSize().toString());
    }
  }
 
}
