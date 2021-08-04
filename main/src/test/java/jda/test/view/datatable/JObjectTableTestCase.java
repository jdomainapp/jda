package jda.test.view.datatable;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.datacontroller.ObjectTableController;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JSpinnerField;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.tables.JDataTable;
import jda.mosa.view.assets.tables.JObjectTable;
import jda.mosa.view.assets.tables.TableInputEventsHelper;
import jda.test.model.basic.City;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;
import jda.test.view.datafields.JAbstractListFieldTestCase;
import jda.util.SwTk;

//TODO: Not yet working
public class JObjectTableTestCase extends JDataTableTestCase implements ActionListener {
  private static DODMBasic schema;

  private static Configuration config;
  
  private static Class domainClass;
  private static ControllerBasic controller;
  
  private JObjectTable table;
  
  @Test
  public void createAndShowGUI () { super.createAndShowGUI(); }
  
  @BeforeClass
  public static void init() throws Exception {
    config = new Configuration();
    GUIToolkit.initInstance(config);
     
    try {
      // register domain classes
      domainClass = Student.class;
      Configuration config = SwTk.createMemoryBasedConfiguration("");
      schema = schema.getInstance(config);
      schema.addClass(domainClass);

      // to create root data controller
      ApplicationModule module = new ApplicationModule("test", config, 
          null, null, null, new ControllerConfig(ControllerBasic.class, 
              ObjectTableController.class, null, null, 
              null, false, true, 
              0l, 0l, null), 
          null, null, true, null, null, null);
      
      controller = new ControllerBasic(schema, module, null, null, config);
      
      DOMBasic dom = schema.getDom();
      
      // init test data
      schema.addClass(SClass.class);
      SClass c = new SClass("1c12");
      dom.addObject(c);
      c = new SClass("2c12");
      dom.addObject(c);

      schema.addClass(City.class);
      City ci = new City("Hà nội");
      dom.addObject(ci);
      ci = new City("Vinh");
      dom.addObject(ci);
      ci = new City("Đà nẵng");
      dom.addObject(ci);
      ci = new City("Hồ chí minh");
      dom.addObject(ci);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  public JComponent getContent() {
    /////// a panel containing: a JDataTable and a button panel ("OK" & "Cancel")    
    JPanel panel = new JPanel(new BorderLayout());
    
    ///// JDataTable
    // headers come from the domain attributes
    Collection<DAttr> dcs = schema.getDsm().getDomainConstraints(domainClass);
    
    List header = new ArrayList();
    for (DAttr dc : dcs) {
      header.add(dc.name());
    }
    
    ControllerBasic.DataController dctl = controller.getRootDataController();
    
    table = new JObjectTable(dctl, header);
    
    // table data (if any)
    
    // set the table columns to use the   
    // the data field objects as cell editors
    boolean fitColumnToEditor = true;
    boolean editable;
    JDataField df;
    int colIndex = 0;
    Class displayClass; 
    Object val = null;
    DAttr bc;
    Class domainType;
    
    DSMBasic dsm = schema.getDsm();
    DOMBasic dom = schema.getDom();
    
    for (DAttr dc : dcs) {
      editable = dc.mutable();
      
      if (dc.type().isDomainType()) {
        domainType = dsm.getDomainClassFor(domainClass, dc.name());
        Collection domainObjs = dom.getObjects(domainType);
        JDataSource ds = JAbstractListFieldTestCase.createBoundedDataSource(schema, domainObjs, domainType);
        
        displayClass = JSpinnerField.class;
        // bound constraint
        bc = dsm.getIDAttributeConstraints(domainType)[0];
        
        df = DataFieldFactory.createMultiValuedDataField(getDataValidator(schema,null), config, dc, bc, displayClass, ds, val, editable);
      } else {
        displayClass = JTextField.class;
        df = DataFieldFactory.createSingleValuedDataField(getDataValidator(schema,null), config, dc, displayClass, val, editable);
      }
      
      table.setCellEditor(df.toCellEditor(), colIndex, fitColumnToEditor);

      if (!editable) {
        table.setColumnEditable(colIndex, false);
      }
      
      colIndex++;
    }
    
    // selection mode
    //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    
    TableInputEventsHelper inputHandler = new TableInputEventsHelper(table);
    
    // important: adjust row heights based on the above column settings
    table.initSizes(JDataTable.DEFAULT_VISIBLE_ROWS);

    panel.add(table.getScrollableGUI(),BorderLayout.CENTER);

    /////// button panel
    JPanel buttons = new JPanel(new GridLayout(0,2));
    // adds a new table row for editing
    buttons.add(new JLabel("Ctrl-Shift-Enter: "));
    buttons.add(new JLabel("add row"));
    buttons.add(new JLabel("Shift-Enter: "));
    buttons.add(new JLabel("insert row"));
    JButton b = new JButton("Create");
    b.addActionListener(this);
    buttons.add(b);
    b = new JButton("Update");
    b.addActionListener(this);
    buttons.add(b);
    
    panel.add(buttons, BorderLayout.SOUTH);
    
    return panel;
  }  
  
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    
    DSMBasic dsm = schema.getDsm();
    DOMBasic dom = schema.getDom();
    
    if (cmd.equals("Create")) {
      // to create
      System.out.println("To create...");
      Map<DAttr,Object> valMap = table.getUserSpecifiedState();
      Object[] vals = valMap.values().toArray();
      Object o = dsm.newInstance(domainClass, vals);
      System.out.println("  created " + o);
    } else if (cmd.equals("Update")) {
      // to update
      System.out.println("To update...");
      int editingRow = table.getEditingRow();
      if (editingRow > -1) {
        Object currentObj = table.getRowData(table.getEditingRow());
        System.out.println("  current: " + currentObj);
        Map<DAttr,Object> valMap = table.getMutableState();
        try {
          dom.updateObject(currentObj, valMap);
          System.out.println("  updated to: " + currentObj);
        } catch (DataSourceException ex) {
          ex.printStackTrace();
        }
      }
    }

  }
}
