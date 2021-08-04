package jda.test.view.datatable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.modules.setup.init.StyleConstants;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.tables.JDataTable;
import jda.mosa.view.assets.tables.TableInputEventsHelper;
import jda.test.model.basic.Student;
import jda.test.view.ViewTestCase;
import jda.test.view.datafields.JSpinnerFieldTestCase;
import jda.util.SwTk;

public class JDataTableTestCase extends ViewTestCase implements ActionListener {
  private static DODMBasic schema;

  private static Configuration config;
  
  @DAttr(name = "year", type = Type.Integer,optional=false,length = 4, min = 1990, max = 9999)
  private Integer year;

  @DAttr(name = "month", type = Type.String, length = 10)
  private Object month;

  @DAttr(name = "city", type = Type.String, length = 10)
  private Object city;

  private static DAttr dcYear;
  private static DAttr dcMonth;
  private static DAttr dcCity;

  private JDataTable table;
  private JTextField numRows;

  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  @BeforeClass
  public static void init() throws Exception {
    config = new Configuration();
    GUIToolkit.initInstance(config);
        
    // register domain classes
    Configuration config = SwTk.createMemoryBasedConfiguration("");
    schema = schema.getInstance(config);
    //schema = schema.getInstance(null, false);
    schema.addClass(Student.class);
    schema.addClass(JDataTableTestCase.class);
    
    dcYear = schema.getDsm().getDomainConstraint(JDataTableTestCase.class, "year");
    dcMonth = schema.getDsm().getDomainConstraint(JDataTableTestCase.class,
        "month");
    dcCity = schema.getDsm().getDomainConstraint(JDataTableTestCase.class,
    "city");

  }
  
  public JComponent getContent() {
    /////// a panel containing: a JDataTable and a button panel ("OK" & "Cancel")    
    JPanel panel = new JPanel(new BorderLayout());
    
    ///// JDataTable
    String[] hnames = {
        "Month...a long explanation. Is this raining outside today?", "Year", "The city name"};
    
    List header = new ArrayList();
    Collections.addAll(header, hnames);
    
    table = new JDataTable(null, header);
    
    // table data (if any)
    
    // set the table columns to use the   
    // the data field objects as cell editors
    
    // months
    boolean fitColumnToEditor = true;
    boolean editable = true;
    List months = new ArrayList();
    months.add("Jan");
    months.add("Feb");    
    JDataSource ds = JSpinnerFieldTestCase.createUnboundedDataSource(months);
    JBindableField bdf = DataFieldFactory.createSpinnerField(getDataValidator(schema,null),config,dcMonth, null, 
        ds, editable); 
    bdf.connectDataSource();
    table.setCellEditor(bdf.toCellEditor(),0,fitColumnToEditor);
    
    // alignment
    table.setColumnAlignment(AlignmentX.Center, AlignmentY.Middle, 0);
    table.setColumnStyle(StyleConstants.Default, 0);    
    table.setHeaderStyle(StyleConstants.DefaultBold, 0);
    
    // years
    //editable = false;
    List years = new ArrayList();
    years.add(2012);
    years.add(2013);
    ds = JSpinnerFieldTestCase.createUnboundedDataSource(years);
    bdf = DataFieldFactory.createSpinnerField(getDataValidator(schema,null), config, dcYear, null, ds, editable);
    bdf.connectDataSource();
    table.setCellEditor(bdf.toCellEditor(),1,fitColumnToEditor);
    
    // alignment
    table.setColumnAlignment(AlignmentX.Right, AlignmentY.Top, 1);
    table.setColumnStyle(StyleConstants.Default, 1);
    table.setHeaderStyle(StyleConstants.Heading1OnWhite, 1);
    table.setColumnForeground(1, Color.RED);

    // text field type columns
    JDataField df = DataFieldFactory.createTextField(getDataValidator(schema,null), config, dcCity);
        //new JDataField(schema, dcCity);
    table.setCellEditor(df.toCellEditor(),2,fitColumnToEditor);
    
    // style
    table.setColumnAlignment(AlignmentX.Right, AlignmentY.Bottom, 1);
    table.setColumnStyle(StyleConstants.DefaultBlue, 2);
    table.setColumnForeground(2, Color.BLUE);
    
    // change the default 1-click editing behaviour
    //-- default: table.setCellEditMode(JDataTable.EditMode.ON_DOUBLE_CLICK);
    //table.setCellEditMode(JDataTable.EditMode.ON_SINGLE_CLICK);
    
    // enable cell editing 
    //    table.setCellEditable(true);
    //table.setCellSelectionEnabled(true);
    
    // selection mode
    //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    
    //initActionMap(table);
    TableInputEventsHelper inputHandler = new TableInputEventsHelper(table);
    
    // important: adjust row heights based on the above column settings
    table.initSizes(JDataTable.DEFAULT_VISIBLE_ROWS);

    panel.add(table.getScrollableGUI(),BorderLayout.CENTER);

    /***Testing */
    //testHidingColumn();
    
    /** test column editable */
    //table.setColumnEditable(2, false);
    
    /////// button panel
    JPanel buttons = new JPanel(new GridLayout(0,2));
    // adds a new table row for editing
    buttons.add(new JLabel("Ctrl-Shift-Enter: "));
    buttons.add(new JLabel("add row"));
    buttons.add(new JLabel("Shift-Enter: "));
    buttons.add(new JLabel("insert row"));
    JButton b = new JButton("Visible rows");
    numRows = new JTextField(4);
    numRows.setText(table.getVisibleRows()+"");
    b.addActionListener(this);
    buttons.add(b);
    buttons.add(numRows);
    
    panel.add(buttons, BorderLayout.SOUTH);
    
    return panel;
  }  
  
  private void testHidingColumn() {
    // hide then un-hide first column
    new Thread() {
      public void run() {
        int colIndex = 0;
        // hide first column from viewing
        try {Thread.sleep(2000); } catch (InterruptedException e) {}
        table.setColumnVisible(colIndex,false);
        System.out.println("First column hidden");
        System.out.printf("  visible(%b) %n", table.isColumnVisible(colIndex));
        
        // unhide the above column        
        // turn on auto-resize
        table.setAutoResize(true);
        // un-hide the column
        try {Thread.sleep(2000); } catch (InterruptedException e) {}
        table.setColumnVisible(colIndex,true);
        System.out.println("First column un-hidden");
        System.out.printf("  visible(%b) %n", table.isColumnVisible(colIndex));
      }
    }.start();
  }
  
  private void initActionMap(final JDataTable table) {
    //KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    //Action al;
    
    ActionMap amap = table.getActionMap();
    
    //table.setActionMap(null);
    

//    Object[] keys = amap.allKeys();
//    for (Object k : keys) {
//      al = amap.get(k);
//      System.out.format("%s -> %s%n",k.toString(), al.getClass());
//    }
//    
//    al = amap.get(enterKey);
//        
//    if (al != null) {
//      amap.remove(enterKey);
//    }
    
    // create a new action 
//    amap.put(enterKey, al);
  }
  
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    
    if (cmd.equals("Visible rows")) {
      table.setVisibleRows(Integer.parseInt(numRows.getText()));
      //frame.validate();
    }
  }
}
