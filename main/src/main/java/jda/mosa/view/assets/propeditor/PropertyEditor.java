/**
 *
 *
 * @author Duc M Le  <a href="mailto:dmle@doc.ic.ac.uk"><i>dmle@doc.ic.ac.uk</i></a>
 * @version 1.0
 * Department of Computing, Imperial College
 */
package jda.mosa.view.assets.propeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


public class PropertyEditor {

  private JPanel panel;
  private RecordScroller recSet;
  private Object selectedObject;

  protected JSplitPane jsplit;
  protected AbstractTableModel td;
  protected JTable propTable;

  protected Component host;
  protected Object[][] tableData = null;
  protected String[] header;
  protected Collection propObjs;

  protected JEditorPane outputArea;
  protected Object activeShape;

  protected Point currentPoint;

  protected List listeners;
  protected List propListeners;

  boolean recordScrollerOn = true;

  private String name;
  private EditorHandler ehandler;

  public static enum EditorEvent {
    PROPERTY_CHANGED, //
    ITEM_CHANGED, //
  }

  public PropertyEditor(Component host) {
    this(host, true);
  }

  public PropertyEditor(Component host, boolean recordScrollerOn) {
    this.host = host;
    listeners = new LinkedList();
    propListeners = new LinkedList();
    this.recordScrollerOn = recordScrollerOn;
    initCommon();
    init();
  }

  private void initCommon() {
    name = "Property Editor";
    panel = new JPanel();

    // initialise the propery table;
    propObjs = new HashSet();
    header = new String[] { "Property", "Value" };

    // initialise a common handler for all actions that the user performs
    // using either mouse or keyboard on this editor
    ehandler = new EditorHandler();

    // the text area used for displaying content on the popup menu
    outputArea = new JEditorPane();

    // register a key board handler to handle the Enter key (when displaying
    // editable objects)
    // not working yet: --> outputArea.addKeyListener(ehandler);

    currentPoint = new Point(0, 0);
  }

  protected void init() {
    // panel.setLayout(new BorderLayout());
    // the popup menu

    JPanel subPanel = new JPanel();
    subPanel.setLayout(new BorderLayout());

    JScrollPane jts = getPropertyPanel();
    double jtsW = 100;
    double jtsH = 150;
    jts.setPreferredSize(new Dimension((int) jtsW, (int) jtsH));

    // initialise a record scroller for record-set type properties
    if (recordScrollerOn) {
      recSet = new RecordScroller(false);
      JComponent recSetGui = recSet.getUI();
      recSet.addListener(ehandler);

      // add the record set and property table to the sub-panel
      subPanel.add(BorderLayout.NORTH, recSetGui);
    }
    subPanel.add(BorderLayout.CENTER, jts);

    // String text = "";
    // float[] hsb = Color.RGBtoHSB(255, 255, 220, null); // light yellow
    // Color lightYellow = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    // propTable.setBackground(lightYellow);

    // set the content type to HTML
    outputArea.setContentType("text/html");
    outputArea.setEditorKit(new HTMLEditorKit());
    // set the editable to false by default, only enable when displaying an
    // editable object

    outputArea.setEditable(false);

    JScrollPane jout = new JScrollPane(outputArea,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    int joutW = (int) (jtsW);
    int joutH = (int) (jtsH / 4);
    Dimension joutD = new Dimension(joutW, joutH);
    jout.setPreferredSize(joutD);
    jout.setSize(joutD);
    outputArea.setPreferredSize(joutD);
    outputArea.setSize(joutD);

    jsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, subPanel, jout);
    jsplit.setResizeWeight(0.1);
    jsplit.setOneTouchExpandable(true);
    jsplit.setContinuousLayout(true);
    jsplit.setDividerLocation(-1);
    jsplit.setName(name);
    propTable.setName(name);
    panel.add(BorderLayout.CENTER, jsplit);
    // don't set the panel size to honor the
    // preferred sizes of the splitpane's components
  }

  public String getName() {
    return name;
  }

  /**
   * Users of this class interested in handling changes to property must
   * register an ActionListener object through this method.
   * 
   * @param listener
   */
  public void addPropertyChangedListener(ActionListener listener) {
    propListeners.add(listener);
  }

  private void notifyPropertyChangeListeners(Object paramName, Object val) {
    ActionEvent e = new ActionEvent(new Object[] { paramName, val },
        EditorEvent.PROPERTY_CHANGED.ordinal(), "PropertyChanged");
    for (Iterator pit = propListeners.iterator(); pit.hasNext();) {
      ActionListener l = (ActionListener) pit.next();
      l.actionPerformed(e);
    }
  }

  /**
   * Users of this class interested in acting upon the currently active object
   * on this editor must register an <code>ActionListener</code> via this method
   * 
   * The handling code should obtain the object reference of this class from the
   * source object of the thrown <code>ActionEvent<code>.
   * The current object can then be accessed by invoking the <code>getSelectedObject()</code>
   * method.
   * 
   * @param listener
   */
  public void addListener(ActionListener listener) {
    listeners.add(listener);
  }

  /**
   * Notify listeners when the currently active object has been changed
   */
  private void notifyListeners() {
    ActionEvent e = new ActionEvent(this, EditorEvent.ITEM_CHANGED.ordinal(),
        "ItemChanged");
    for (Iterator it = listeners.iterator(); it.hasNext();) {
      ActionListener l = (ActionListener) it.next();
      l.actionPerformed(e);
    }
  }

  /**
   * Invoked by user application to know which object is currently being
   * selected
   * 
   * @return
   */
  public Object getSelectedObject() {
    if (selectedObject == null && recSet != null)
      selectedObject = recSet.getSelectedObject();

    return selectedObject;
  }

  /**
   * 
   * @param shape
   * @param propObjs
   */
  public void setProperties(Object shape, Collection propObjs) {
    setProperties(null, shape, propObjs);
  }

  /**
   * Invoked by GUI components to display a collection of GVisualisable objects
   * We need a record set for this
   * 
   * @param mousePoint
   * @param shape
   * @param propObjs
   */
  public void setGVisualisableObjects(Object shape, Collection gvizObjs) {
    // display first object
    // if (gvizObjs == null || gvizObjs.size() < 1)
    // return;
    //
    // GVisualisable gobj = (GVisualisable) gvizObjs.iterator().next();
    // setProperties(shape, gobj.getPropertyObjects());
    //
    // selectedObject = gobj;
    //
    // // pass the whole collection to a record scroller
    // if (recSet != null)
    // recSet.setData(gvizObjs, false);
    setGVisualisableObjects(shape, gvizObjs, 0, null);
  }

  public void addGVisualisableObjects(Object shape, Collection gvizObjs,
      int selectedIndex, ActionListener handler) {
    setObjects(shape, gvizObjs, selectedIndex, handler);

    if (recSet != null) {
      for (Iterator it = gvizObjs.iterator(); it.hasNext();) {
        recSet.addNew(it.next(), false);
      }
    }
  }

  public void setGVisualisableObjects(Object shape, Collection gvizObjs,
      int selectedIndex, ActionListener handler) {
    setObjects(shape, gvizObjs, selectedIndex, handler);

    // pass the whole collection to a record scroller
    if (recSet != null) {
      recSet.setData(gvizObjs, false, selectedIndex);
    }
  }

  private void setObjects(Object shape, Collection gvizObjs, int selectedIndex,
      ActionListener handler) {
    // display first object
    if (gvizObjs == null || gvizObjs.size() < 1)
      return;

    Visualisable obj = null;
    int index = 0;
    for (Iterator it = gvizObjs.iterator(); it.hasNext();) {
      obj = (Visualisable) it.next();
      if (index == selectedIndex)
        break;
      else
        index++;
    }

    // display the selected object
    setProperties(shape, obj.getPropertyObjects());

    this.selectedObject = obj;

    if (handler != null) {
      listeners.add(handler);
    }
  }

  /**
   * Invoked by GUI components to display property objects of one GVisualsable
   * object
   * 
   * @param mousePoint
   * @param shape
   * @param propObjs
   */
  public void setProperties(Point mousePoint, Object shape, Collection propObjs) {
    if (shape != null)
      activeShape = shape;

    this.propObjs = propObjs;
    tableData = new Object[propObjs.size()][];
    int i = 0;
    for (Iterator pit = propObjs.iterator(); pit.hasNext();) {
      PropertyObject propObj = (PropertyObject) pit.next();
      String paramName = propObj.getName();
      tableData[i] = new Object[] { paramName, propObj.getValue() };
      i++;
    }

    if (mousePoint != null)
      currentPoint = mousePoint;

    // clear output area
    outputArea.setText("");
    td.fireTableDataChanged();
    propTable.setRowSelectionInterval(0, 0);
    propTable.clearSelection();
    // propTable.repaint();
  }

  public Object getSelectedShape() {
    return activeShape;
  }

  protected JScrollPane getPropertyPanel() {
    // table model object

    if (tableData == null)
      tableData = new Object[][] { { "", "" } };

    td = new AbstractTableModel() {

      public String getColumnName(int col) {
        return header[col].toString();
      }

      public int getRowCount() {
        return tableData.length;
      }

      public int getColumnCount() {
        return header.length;
      }

      public Object getValueAt(int row, int col) {
        if (tableData != null && row > -1 && col > -1)
          return tableData[row][col];
        else
          return null;
      }

      public boolean isCellEditable(int row, int col) {
        // check the property object
        boolean editable = false;
        if (row > -1 && col > -1) {
          if (col == 0) {
            editable = false;
          } else {
            String name = (String) getValueAt(row, 0);
            PropertyObject pobj = getPropertyObject(name);
            if (pobj != null)
              editable = pobj.isEditable();
          }
        }

        return editable;
      }

      public void setValueAt(Object value, int row, int col) {
        if (tableData != null) {
          tableData[row][col] = value;
          // updateParam(data[row][0], value);
          fireTableCellUpdated(row, col);
        }
      }
    };

    propTable = new JTable(td);
    // cfgTable.setShowGrid(false);
    propTable.setGridColor(Color.LIGHT_GRAY);
    propTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    propTable.setCellSelectionEnabled(true);

    // listen to mouse event to display appropriate cell editor
    propTable.addMouseListener(ehandler);
    propTable.addKeyListener(ehandler);

    JScrollPane jst = new JScrollPane(propTable);
    jst.setViewportView(propTable);
    jst.setColumnHeader(null);
    return jst;
  }

  /**
   * Find the property object corresponding to the selected property of the
   * current object
   * 
   * @param paramName
   * @return
   */
  private PropertyObject getPropertyObject(String paramName) {
    if (propObjs != null) {
      for (Iterator it = propObjs.iterator(); it.hasNext();) {
        PropertyObject propObj = (PropertyObject) it.next();
        String name = propObj.getName();
        if (name.equals(paramName))
          return propObj;
      }
    }

    return null;
  }

  /**
   * Display the selected property object content onto the output area
   * 
   * @param paramName
   * @return
   */
  private PropertyObject displayObject(PropertyObject propObj) {
    Object value = propObj.getValue();
    boolean editable = propObj.isEditable();

    StringBuffer sb = new StringBuffer();
    // sb.append("<html><header><title></title></header><body>");
    // if (value instanceof HTMLOutput) {
    // String clsname = value.getClass().getSimpleName();
    // sb.append(" Object  <b>" + clsname + "</b>:").append(
    // ((HTMLOutput) value).toHTML());
    // } else if (value instanceof Object[]) {
    // // array of objects
    // Object[] objs = (Object[]) value;
    // String clsname = objs[0].getClass().getSimpleName();
    // sb.append("List of <b>" + clsname + "</b>:").append("<ol>");
    // for (int i = 0; i < objs.length; i++) {
    // Object obj = objs[i];
    // String objstr = null;
    // if (obj instanceof HTMLOutput) {
    // objstr = ((HTMLOutput) obj).toHTML();
    // } else {
    // objstr = obj + "";
    // // objstr = objstr.replaceAll("<<", "&lt;&lt;");
    // // objstr = objstr.replaceAll(">>", "&gt;&gt;");
    // objstr = objstr.replaceAll("<", "&lt;");
    // objstr = objstr.replaceAll(">", "&gt;");
    // }
    // sb.append("<li>").append(objstr).append("</li>");
    // }
    // sb.append("</ol>");
    // } else {
    String vstr = value + "";
    // vstr = vstr.replaceAll("<<", "&lt;&lt;");
    // vstr = vstr.replaceAll(">>", "&gt;&gt;");
    vstr = vstr.replaceAll("<", "&lt;");
    vstr = vstr.replaceAll(">", "&gt;");
    sb.append(vstr);
    // }

    // sb.append("</body></html>");
    // System.out.println(sb.toString());
    outputArea.setText(sb.toString());
    // if the property object is editable then prepare the output area
    // so that user can edit it
    if (editable) {
      outputArea.setEditable(true);
    }
    return null;
  }

  public JComponent getUI() {
    return jsplit;
  }

  public boolean isVisible() {
    return true;
  }

  public void clear() {
    tableData = new Object[][] { { "", "" } };
    if (recSet != null) {
      recSet.clear();
    }
    outputArea.setText("");
    ehandler.clear();

    td.fireTableDataChanged();
  }

  // notified when an ojbect is selected -> display it on the editor
  class EditorHandler extends MouseAdapter implements ActionListener,
      KeyListener {
    // rememeber the previous row on the property table
    // so that we can update its vallue when the user has moved the
    // mouse to the next row
    private int prevRow = -1;
    private Object prevObj = null;

    //
    // /////////////// an implementation of the listener for the ActionListener
    // event
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();

      if (src instanceof RecordScroller) {
        // user clicks on the record scroller
        RecordScroller rs = (RecordScroller) src;
        Visualisable gobj = (Visualisable) rs.getSelectedObject();
        // display it
        if (gobj != null) {
          setProperties(null, gobj.getPropertyObjects());

          selectedObject = gobj;

          // propage this event to listeners of this class as well
          notifyListeners();
        }
      }
      // other cases here
    }

    // /////// mouse handling (for property table)
    public void mousePressed(MouseEvent me) {
      Object src = me.getSource();

      if (src.equals(propTable)) {
        handleMouseOnTable(me);
      }
    }

    private void handleMouseOnTable(MouseEvent me) {
      // display a detached text editor for cell's value
      // Logger.debug("CacheViewerFrame", "mouseClicked()", "Displaying a
      // detached editor...");
      Point p = me.getPoint();

      // boolean doubleClick = me.getClickCount() == 2;
      int row = propTable.rowAtPoint(p);
      int col = propTable.columnAtPoint(p);
      String paramName = null;
      Object val = null;
      if (col == 0) {
        val = propTable.getValueAt(row, col + 1);
        paramName = (String) propTable.getValueAt(row, col);
      } else {
        val = propTable.getValueAt(row, col);
        paramName = (String) propTable.getValueAt(row, col - 1);
      }

      // Object val = propTable.getValueAt(row, col);
      if (prevRow > -1)
        prevObj = getOutputText();
      else
        prevObj = val;

      if (prevRow > -1 && prevObj != null) {
        // update the property object of the previous row first
        // System.out.println("previous row: " + prevRow);
        // System.out.println("previous text: " + prevObj);
        updateTableRow(prevRow, prevObj);
      }

      prevRow = row;
      // then process thsi row

      if (paramName != null) {
        PropertyObject obj = getPropertyObject(paramName);
        // get the table value
        displayObject(obj);
      }
    }

    // ////////////// key listener
    public void keyPressed(KeyEvent e) {
      //
      // handleKeyEvent(e);
    }

    public void keyTyped(KeyEvent e) {
      //
    }

    public void keyReleased(KeyEvent e) {
      handleKeyEvent(e);
    }

    private void handleKeyEvent(KeyEvent e) {
      Object src = e.getSource();

      if (src.equals(propTable)) {
        handleKeyOnTable(e);
      } else if (src.equals(outputArea)) {
        handleKeyOnOutputArea(e);
      }
    }

    private void handleKeyOnTable(KeyEvent e) {
      // if the user has typed in esc then skip
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        return;

      int row = propTable.getSelectedRow();
      Object val = propTable.getValueAt(row, 1);
      updateTableRow(row, val);
    }

    private void updateTableRow(int row, Object val) {
      // set the paramter value
      // if it is editable then change its value
      if (row > -1 && row < propTable.getRowCount()) {
        // int col = propTable.columnAtPoint(p);
        String paramName = (String) propTable.getValueAt(row, 0);
        PropertyObject propObj = getPropertyObject(paramName);
        if (propObj != null) {
          Object eval = propObj.getValue();

          if (eval == null || (eval != null && !eval.equals(val))) {
            // this only works if the property is editable!
            propObj.setValue(val);

            propTable.setValueAt(val, row, 1);

            notifyPropertyChangeListeners(paramName, val);
          }
        }
      }
    }

    private String getOutputText() {
      HTMLDocument doc = (HTMLDocument) outputArea.getDocument();
      // HTMLEditorKit ekit = (HTMLEditorKit) outputArea.getEditorKit();

      Element root = doc.getDefaultRootElement(); // html

      // System.out.println("root: \n" + root);
      Element body = root.getElement(0);

      String txt = null;
      try {
        // System.out.println(outputArea.getText());
        txt = body.getDocument().getText(0, body.getDocument().getLength());
      } catch (Exception e) {
        //
      }
      return txt.trim();
      // outputArea.getDocument().getDefaultRootElement().getElement()Text().trim();
    }

    private void handleKeyOnOutputArea(KeyEvent e) {
      // user has finished editing text on the output area
      // if the user has typed in esc then skip
      boolean shift = e.isShiftDown();

      if (e.getKeyCode() == KeyEvent.VK_ENTER && shift) {
        // set the paramter value
        // if it is editable then change its value
        int row = propTable.getSelectedRow();
        Object val = outputArea.getText().trim();
        updateTableRow(row, val);
      }
    }

    private void clear() {
      prevRow = -1;
    }
  }
}
