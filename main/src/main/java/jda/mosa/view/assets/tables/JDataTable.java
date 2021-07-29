package jda.mosa.view.assets.tables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.InfoCode;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.view.assets.DataContainerToolkit;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JDataField.DataCellEditor;
import jda.mosa.view.assets.panels.DefaultPanel;
import jda.util.properties.PropertySet;

/**
 * @overview
 *  A {@link JDataContainer} that displays data in a tabular form 
 *  
 * @author dmle
 */
public class JDataTable extends JTable implements JDataContainer {
  
//  /**
  //   * @overview
  //   *  An event handler used to handle component events whose sources are 
  //   *  the data fields that are the table cell editors of this. 
  //   *  
  //   *  <p>A particular event of interest is <tt>componentResized</tt> which is fired 
  //   *  when the data field size is changed (b/c its data model has been changed). This will 
  //   *  result in the update of the corresponding table's column.
  //   *  
  //   * @author dmle
  //   *
  //   * @version 3.0
  //   */
  //  private class ComponentEventHandler extends ComponentAdapter {
  //
  //    @Override
  //    public void componentResized(ComponentEvent e) {
  //      // update column width if component's resized to become bigger
  //      Component src = e.getComponent();
  //      
  //      if (src instanceof JDataField) {
  //        // component is a data field-typed editor of this
  //        JDataField df = (JDataField) src;
  //        TableColumn tc = getColumn(df.getDomainConstraint().name());
  //        
  //        updateColumnWidth(tc, df);
  //      }
  //    }
  //  }

  /**
   * @overview
   *  Defines the message code constants used by {@link JDataTable}'s operations.
   *  
   * @author dmle
   * @version 3.2
   */
  public static enum TableMessageCode implements InfoCode {
    CONFIRM_SWITCH_ROW_WHILE_EDITING("Chuyển sang dòng dữ liệu khác sẽ xóa dữ liệu sửa hiên tại. Bạn có muốn tiếp tục không?"), 
    CONFIRM_DELETE_ROWS("Bạn có muốn xóa (các) dòng đã chọn không?"),
    /**
     * 0: row number (starting from 1)
     */
    CONFIRM_DELETE_SELECTED_ROWS("Bạn có muốn xóa dòng số {0} không?"), 
    CONFIRM_INSERT_ROWS("Bọn có muốn thêm dòng mới trước (các) dòng đã chọn không?"), 
    CONFIRM_INSERT_A_ROW("Bọn có muốn thêm dòng mới trước dòng đã chọn không?")
    ;
    
    private String text;

    /**The {@link MessageFormat} object for formatting {@link #text} using context-specific data arguments*/
    private MessageFormat messageFormat;

    private TableMessageCode(String text) {
      this.text = text;
    }

    @Override
    public String getText() {
      return text;
    }
    
    @Override
    public MessageFormat getMessageFormat() {
      if (messageFormat == null) {
        messageFormat = new MessageFormat(text);
      }
      
      return messageFormat;
    }
  } /**end {@link TableMessageCode}*/

  // v2.7.2
  private Region containerCfg; 
  private PropertySet printConfig;

  private JDataContainer parent;
  protected ControllerBasic.DataController controller;

  protected DataModel dataModel;
  private JScrollPane scrollable;
  private EditMode editMode;

  private boolean hasFocus;

  // index of the column currently on-focus (if any)
  private int columnOnFocus;
  
  /**
   * the number of visible rows on the table (the minimum number of rows that
   * are displayed on the view port of the scroll pane containing this component
   */
  private int numVisibleRows = DEFAULT_VISIBLE_ROWS;

  private int maxNumVisibleRows = DEFAULT_MAX_VISIBLE_ROWS;

  /** the minimum number of visible rows to display */
  private int minNumVisibleRows = 0;

  /** a gui config map that maps the <tt>Region</tt> to a cell editor.
   * The Region object is used to obtain view-specific settings.
   **/
  private Map<TableCellEditor,Region> cfgMap;
  
  // v3.0: to handle ComponentEvents raised by data field-typed cell editors of this
  //private ComponentEventHandler componentEventHandler;
  
  public static enum EditMode {
    ON_SINGLE_CLICK, //
    ON_DOUBLE_CLICK,
  };

  public static final int DEFAULT_VISIBLE_ROWS = 5;

  public static final int DEFAULT_MAX_VISIBLE_ROWS = DEFAULT_VISIBLE_ROWS * 2;

  public static final Border TABLE_BORDER = BorderFactory.createLineBorder(
      Color.GRAY, 1);

  private static final int MIN_ROW_HEIGHT = 20;
  private static final int MIN_HEADER_ROW_HEIGHT = 35;
  
  /**
   * @effects
   *  create and return a <tt>JDataTable</tt> or a sub-type of it specified by <tt>tableClass</tt>
   *  
   *  Throws NotPossibleException if failed to do so
   */
  public static JDataTable createInstance(Class<? extends JDataTable> tableClass, 
      Region cfg,
      ControllerBasic.DataController controller, List header, JDataContainer parent) throws NotPossibleException {
    try {
      Constructor<? extends JDataTable> cons = tableClass.getConstructor(
          Region.class,
          ControllerBasic.DataController.class, 
          List.class, JDataContainer.class);
      
        return cons.newInstance(cfg, controller, header, parent);
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,  
            tableClass);
      }  
  }
  
  // /// Constructor methods
  public JDataTable(ControllerBasic.DataController controller, List header) {
    this(null, controller, header, null);
  }
  
//  public JDataTable(Controller.DataController controller, List header, JDataContainer parent) {
//    this(null, controller, header, parent);
//  }
  
  /**
   * @effects 
   *          if parent != null
   *            initialises a new <code>JDataTable</code> with headers defined in
   *          <code>header</code> and whose parent is parent
   *          else
   *            initialises a top-level <code>JDataTable</code> with headers defined in
   *          <code>header</code>
   */
  public JDataTable(Region containerCfg, 
      ControllerBasic.DataController controller, List header, JDataContainer parent) {
    super();

    this.containerCfg = containerCfg; 
    
    this.controller = controller;
    this.parent = parent;
    
    // sets the panel in the controller
    if (controller != null)
      controller.setDataContainer(this);

    dataModel = (DataModel) createTableModel(header); // new DataModel(header);
    setModel(dataModel);

    // v2.7.2: create column headers
    createColumnHeaders(header);
    
    setGridColor(Color.LIGHT_GRAY);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setCellSelectionEnabled(true);
    // start cell editing only on double-click (JTable default: on single-click)
    setCellEditMode(EditMode.ON_DOUBLE_CLICK);

    // turn off auto-resize to honor the preferred column's widths
    // when window is resized
    setAutoResize(false);

    // initialise cell renderers
    initColumnRenderers();
    
    // set up the table event handler
    // tableInputHelper = new TableInputEventsHelper(this);
    
    setActionMap(null);

    // do not fill view port to show extra spaces when the area is resized
    setFillsViewportHeight(false);

    // draw a border around the scroll pane
    scrollable = new JScrollPane(this);

    setBorder(TABLE_BORDER);

    GUIToolkit.highlightContainerInit(getGUIComponent());
    
    cfgMap = new HashMap();
  }

  @Override
  public void createLayout() {
    // nothing needs to be done for table
  }
  
  @Override
  public Region getContainerConfig() {
    return containerCfg;
  }

  @Override
  public PropertySet getContainerPrintConfig() {
    return printConfig;
  }

  @Override
  public void setContainerPrintConfig(PropertySet printCfg) {
    this.printConfig = printCfg;
  }

  /**
   * @requires 
   *  propName != null
   * @effects 
   *  if exists property <tt>propName</tt> of {@link #containerCfg} whose value type is assignable to <tt>valueType</tt> 
   *    return its value
   *  else
   *    return <tt>defaultVal</tt>
   *    
   * @version 3.3
   */
  public <T> T getConfigProperty(PropertyName propName, Class<T> valueType, T defVal) {
    if (containerCfg != null)
      return containerCfg.getProperty(propName, valueType, defVal);
    else
      return defVal;
  }
  
  /**
   * 
   * @effects 
   *  Create column headers for this using the header texts specified in <tt>header</tt>.
   *  The column header is a {@link WrappableHeaderRenderer} initialised with a default dimension.
   *  The actual dimension will be updated later by {@link #initSizes()}. 
   *  
   * @version 2.7.2
   */
  private void createColumnHeaders(List header) {
    String h = null;
    for (int i = 0; i < header.size(); i++) {
      h = (String) header.get(i);
      TableColumn tc = columnModel.getColumn(i);
      WrappableHeaderRenderer tr = new WrappableHeaderRenderer();
      tr.addText(h.toString());
      tc.setHeaderRenderer(tr);
    }
  }
  

  /**
   * @effects
   *  initialise the cell renderer each column of this table to be an instance of 
   *  <tt>WrappableCellRenderer</tt> 
   */
  private void initColumnRenderers() {
    TableColumnModel tcm = getColumnModel();
    int colCount = tcm.getColumnCount();
    
    TableCellRenderer tcr;
    TableColumn tc;
    for (int i = 0; i < colCount; i++) {
      tcr = new WrappableCellRenderer(this);
      tc = tcm.getColumn(i);
      tc.setCellRenderer(tcr);
    }
  }

  /**
   * @effects initialises the row, column and view port sizes of this using the
   *          {@link #DEFAULT_VISIBLE_ROWS}
   * @see #initSizes(int)
   */
  public void initSizes() {
    initSizes(DEFAULT_VISIBLE_ROWS);
  }

  /**
   * 
   * @effects update the actual dimensions of the table headers, rows and columns and view port sizes of this using the
   *          <code>numVisibleRows</code>.
   *          
   *          <p>
   *          Source: adapted from Sun's tutorial for JTable
   * @version 
   * - 5.2: improved to update table header width to be the max width of the header's render and the current col width
   */
  public void initSizes(int numVisibleRows) {
    //v5.2: final TableModel model = getModel();
    final TableColumnModel tableColumnModel = getColumnModel();
    TableColumn column = null;
    Component comp = null, headerComp;
    TableCellEditor cedit;
    // int headerWidth = 0;
    // int colWidth = 0;
    int columnCount = getColumnCount();
    JTableHeader tableHeader = getTableHeader();
    
    // v 2.5.3
    WrappableHeaderRenderer headerRenderer;
    //TableCellRenderer crend;
    // v 2.5.3 ]
    
    double maxRowHeight = MIN_ROW_HEIGHT; 
    double maxHeaderRowHeight = MIN_HEADER_ROW_HEIGHT;
    double rowHeight, colWidth; 
    double headerHeight, headerWidth = -1;
    String headerVal;
    Dimension dim;
    
    // set header width
    for (int i = 0; i < columnCount; i++) {
      // get the column
      column = tableColumnModel.getColumn(i);
      // get the table render component for the column

      // crend = (TableCellRenderer) headerRenderer.getTableCellRendererComponent(null, 
      //    column.getHeaderValue(), false, false, 0, 0);
      // headerWidth = comp.getPreferredSize().width;
      
      cedit = column.getCellEditor();
      headerRenderer = (WrappableHeaderRenderer) column.getHeaderRenderer();
      headerVal = (String)column.getHeaderValue();
      /* v5.2: improved
      headerRowHeight = headerRenderer.getHeaderHeightBestFit(this, i, headerVal);
      */
      Dimension headerDim = headerRenderer.getHeaderHeightBestFit(this, i, headerVal);
      headerHeight = headerDim.getHeight();
      headerWidth = headerDim.getWidth();
      
      colWidth = column.getPreferredWidth();  // v5.2
      
      // TODO: find the longest value of this column
      Object longValue = (!isEmpty()) ? getValueAt(0, i) : null;
      
      if (cedit != null) { // use cell editor if possible
        // the column must be wide enough to contain it
        /*v3.0: use method
        comp = cedit.getTableCellEditorComponent(this, longValue, false, 0, i);
        */
        comp = getTableCellEditorComponent(cedit, longValue, false, 0, i);
        
        dim = comp.getPreferredSize();
        // v5.2:
        //colWidth = dim.getWidth();
        
        rowHeight = dim.getHeight();
      } else { // use the cell renderer instead
        comp = //getDefaultRenderer(model.getColumnClass(i))
            getCellRenderer(0, i)
            .getTableCellRendererComponent(this, longValue, false, false, 0, i);
        // it is this cell render that is used to render the longest
        // value on the column
        dim = comp.getPreferredSize();
        // v5.2:
        //colWidth = dim.getWidth();
        
        rowHeight = dim.getHeight();
        // colWidth = Math.max(headerWidth, colWidth);
      }
      
      if (maxRowHeight < rowHeight) { // update maxRowHeight
        maxRowHeight = rowHeight;
      }
      
      if (maxHeaderRowHeight < headerHeight) { // update maxHeaderRowHeight
        maxHeaderRowHeight = headerHeight;
      }
      
      // v5.2: update column width for header
      if (colWidth > 0 && headerWidth > colWidth) {
        // 0 colWidth means column has been made invisible by setColumnVisible
        setColumnWidth(column, (int) headerWidth, true);
      }
      // end v5.2
      
      // if (debug) {
      // System.out.println("Initializing width of column " + i + ". "
      // + "headerWidth = " + headerWidth + "; cellWidth = " + cellWidth);
      // }
      // column.setPreferredWidth(colWidth);
      // column.setMaxWidth(colWidth);
    } // end for

    // set row height
    if (maxRowHeight >= 1)
      setRowHeight((int) maxRowHeight);

    // set header row height
    // v5.2: 
    //if (maxHeaderRowHeight >= 1) {
    Dimension headerBound = tableHeader.getPreferredSize();
    if (headerBound.getHeight() < maxHeaderRowHeight) {
      /*v5.2:
      tableHeader.setPreferredSize(new Dimension(headerBound.width, 
        (int)maxHeaderRowHeight));
        */
      Dimension newHeaderDim = new Dimension();
      newHeaderDim.setSize(headerBound.getWidth(), maxHeaderRowHeight);
      tableHeader.setPreferredSize(newHeaderDim);
    }
    // end v5.2

    setVisibleRows(numVisibleRows);
  }

  protected DataModel createTableModel(List header) {
    return new DataModel(header);
  }

  /**
   * @effects returns the <code>DataController</code> object of
   *          <code>this</code>
   */
  public ControllerBasic.DataController getController() {
    return controller;
  }

  /**
   * Sets the table header to <code>header</code>
   */
  public void setHeader(List header) {
    dataModel.setHeader(header);
  }

//  /**
//   * @effects Update the table data of <code>this</code> and its view
//   */
//  public void setModel(final List tableData) {
//    //
//    dataModel.setModel(tableData);
//
//    updateTableView();
//  }

  /**
   * This method is used to populate table data with elements from an existing 
   * collection. Manual update of the table model is required when the collection is changed. 
   * 
   * @effects 
   *  copy the elements of <tt>tableData</tt> over to the table model of this. 
   *  update the table view.
   */
  public void setModel(final Collection tableData) {
    //
    dataModel.setModel(tableData);

    updateTableView();
  }
  
  /**
   * A short-cut for {@link #setCellEditor(TableCellEditor, int, boolean)}, with
   * the last argument is set to <code>true</code>.
   */
  public void setCellEditor(Region cfg, TableCellEditor cedit, int colIndex) {
    setCellEditor(cfg, cedit, colIndex, true);
  }

  /**
   * A short-cut for {@link #setCellEditor(Region, TableCellEditor, int, boolean)}
   * with the first argument set to <tt>null</tt>
   */
  public void setCellEditor( 
      TableCellEditor cedit, int colIndex,
      boolean fitColumnToEditor) {
    setCellEditor(null, cedit, colIndex, fitColumnToEditor);
  }
  
  /**
   * @effects sets the cell editor for the column at the index
   *          <code>colIndex</code> to <code>cedit</code>. If
   *          <code>fitColumnToEditor = true</code> then also fix the column
   *          width so that it always fits the preferred width of the editor
   */
  public void setCellEditor(Region viewCfg, 
      TableCellEditor cedit, int colIndex,
      boolean fitColumnToEditor) {
    TableColumnModel tcm = getColumnModel();
    TableColumn tc = tcm.getColumn(colIndex);
    tc.setCellEditor(cedit);

    // if fitColumnToEditor = true then
    Component comp = null;
    int colWidth = 0;
    Dimension dim;

    /*v3.0: use method
    comp = cedit.getTableCellEditorComponent(this, null, false, 0, colIndex);
    */
    comp = getTableCellEditorComponent(cedit, null, false, 0, colIndex);
    
    dim = comp.getPreferredSize();
    colWidth = dim.width + tcm.getColumnMargin();

    // fix the column width to the editor's component's width
    tc.setMinWidth(colWidth);

    // fix the specified column width because it will use the
    // size of the cell editor
    if (fitColumnToEditor) {
      tc.setMaxWidth(colWidth);
    }
    
    // v3.0
//    if (componentEventHandler == null)
//      componentEventHandler = new ComponentEventHandler();
//    
//    comp.addComponentListener(componentEventHandler);
    
    // register viewCfg and cell editor into a cfgMap
    cfgMap.put(cedit, viewCfg);
  }
  
  /**
   * @effects if <code>minRows </code> is in <code>(0,maxNumVisibleRows]</code>
   *          then sets <code>minNumVisibleRows</code> to <code>minRows</code>
   * @param minRows
   */
  public void setMinVisibleRows(int minRows) {
    if (minRows > 0 && minRows <= maxNumVisibleRows) {
      minNumVisibleRows = minRows;

      updateTableView();
    }
  }

  /**
   * @effects sets {@link #numVisibleRows} to <code>num</code> and update the
   *          view-port size accordingly.
   * 
   */
  public void setVisibleRows(int num) {
    if (num >= minNumVisibleRows && num <= maxNumVisibleRows) {
      numVisibleRows = num;
      updateViewPort();
    }
  }

  public int getVisibleRows() {
    return numVisibleRows;
  }

  /**
   * @effects if <code>maxRows > numVisibleRows</code> sets
   *          {@link #maxNumVisibleRows} to <code>maxRows</code>
   */
  public void setMaxVisibleRows(int maxRows) {
    if (maxRows >= minNumVisibleRows) {
      maxNumVisibleRows = maxRows;

      updateTableView();
    }
  }

  @Override
  public void setVisible(boolean visible) {
    scrollable.setVisible(visible);
  }
  
  /**
   * Note: this method only has effect if cell selection is allowed. 
   * 
   * @effects
   *  set the cell at the intersection of row <tt>row</tt> and column <tt>col</tt>
   *  to selected
   *    
   */
  public void setCellSelected(int row, int col) {
    try {
      setRowSelectionInterval(row, row);
      setColumnSelectionInterval(col, col);
    } catch (IllegalArgumentException e) {
      // ignore index out of range errors
      e.printStackTrace();
    }
  }
  
  @Override
  public boolean isVisible() {
    return scrollable.isVisible();
  }
  

  /**
   * @effects updates the table view (e.g the visible rows and view port) when
   *          the data model is changed (especially when new rows have been
   *          inserted).
   */
  protected void updateTableView() {
    /**
     * if there are more rows than the current number of visible rows then
     * increase this current number up to max number else if there are less rows
     * than the current number decrease the current number up to the minimum
     * number of visible rows
     * 
     * update view port
     */
    int size = getRowCount();
    if (size > numVisibleRows && numVisibleRows < maxNumVisibleRows) {
      // increase
      setVisibleRows(Math.min(size, maxNumVisibleRows));
    } else if (size < numVisibleRows && numVisibleRows > minNumVisibleRows) {
      // decrease
      setVisibleRows(Math.max(size, minNumVisibleRows));
    }
  }

  /**
   * This method is invoked when {@link #numVisibleRows} is updated.
   * 
   * @effects adjusts the size of the view port object containing this to display the current rows
   */
  private void updateViewPort() {
    // set up table view to adjust with the view port
    // set view port size to table's preferred size to remove extra spaces in
    // the otherwise default view port
    Dimension tableSize = getPreferredSize();// getPreferredSize();
    int barWidth = scrollable.getVerticalScrollBar().getPreferredSize().width;
    int viewWidth = tableSize.width + barWidth;//
    int rowHeight = getRowHeight();
    int viewHeight = numVisibleRows * rowHeight;

    Dimension viewPortSize = getPreferredScrollableViewportSize();// new
                                                                  // Dimension(viewWidth,
                                                                  // viewHeight);
    viewPortSize.setSize(viewWidth, viewHeight);
    // setPreferredScrollableViewportSize(viewPortSize);

    // refresh the display
    invalidate(); // mark for re-layout
    // force layout from the top-level container
    Window w = SwingUtilities.getWindowAncestor(this);
    if (w != null)
      w.validate();
  }

  public JScrollPane getScrollableGUI() {
    return scrollable;
  }

  public void setLinkColumn(int index) {
    // not used
  }

  public boolean isLinkColumn(int col) {
    // not used
    return false;
  }
  
  /**
   * @effects returns the name of the attribute that corresponds to the column
   *          <code>colIndex</code> in this table.
   */
  protected String colToAttribute(int colIndex) {
    DAttr dc = getColumnConstraint(colIndex);

    return dc.name();
  }

  /**
   * @effects 
   *  return the column object at the specified <tt>colIndex</tt> or <tt>null</tt> if no such column exists
   */
  protected TableColumn getTableColumn(int colIndex) {
    TableColumnModel tcm = getColumnModel();
    int colCount = getColumnCount();
    if (colIndex >= 0 && colIndex < colCount) {
      return tcm.getColumn(colIndex);
    } else
      return null;
  }
  
  /**
   * @effects returns the <code>DomainConstraint</code> object that is
   *          associated to the cell editor of the column at <code>index</code>
   *          of this table.
   * 
   * @see #setCellEditor(TableCellEditor, int, boolean)
   */
  public DAttr getColumnConstraint(int index) {
    TableColumnModel tcm = getColumnModel();

    TableColumn tc = tcm.getColumn(index);

    UpdatableCellEditor cedit = (UpdatableCellEditor) tc.getCellEditor();

    return cedit.getDomainConstraints()[0];
  }

  /**
   * @effects returns the bounded <code>DomainConstraint</code> object that is
   *          associated to the cell editor of the column at <code>index</code>
   *          of this table.
   * 
   * @see #setCellEditor(TableCellEditor, int, boolean)
   */
  protected DAttr getColumnBoundedConstraint(int index) {
    TableColumnModel tcm = getColumnModel();

    TableColumn tc = tcm.getColumn(index);

    UpdatableCellEditor cedit = (UpdatableCellEditor) tc.getCellEditor();

    return cedit.getDomainConstraints()[1];
  }

  /**
   * @effects returns the <code>TableColumn</code> object in the
   *          <code>TableColumnModel</code> of <code>this</code>, whose
   *          associated domain constraint's name is <code>domainName</code>; 
   *          or return <tt>null</tt> if no such column exists
   * 
   *          <p>
   *          The associated domain constraint of a column is a property of the
   *          <code>TableCellEditor</code> that is set via the method
   *          {@link #setCellEditor(TableCellEditor, int, boolean)}.
   * 
   * @see #setCellEditor(TableCellEditor, int, boolean)
   */
  protected TableColumn getColumn(String domainName) {
    int colCount = getColumnCount();
    TableColumn tc = null;
    DAttr dc;
    TableColumnModel tcm = getColumnModel();
    for (int i = 0; i < colCount; i++) {
      dc = getColumnConstraint(i);
      if (dc.name().equals(domainName)) { // found it
        tc = tcm.getColumn(i);
        break;
      }
    }

    return tc;
  }

  /**
   * @effects 
   *  if colIndex is a valid column index in this
   *    return the width of the column at <tt>colIndex</tt>
   *  else
   *    return -1
   */
  public int getColumnWidth(int colIndex) {
    TableColumnModel tcm = getColumnModel();
    TableColumn tc;
    if (colIndex >= 0 && colIndex < tcm.getColumnCount()) {
      tc = tcm.getColumn(colIndex);
      return tc.getWidth();
    } 
    
    return -1;
  }
  
  public Region getColumnViewConfig(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
      TableCellEditor tce = tc.getCellEditor();
      
      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
        return null;

      JDataField df = ((JDataField.DataCellEditor) tce).getDataField();
      return df.getDataFieldConfiguration();
    } else {
      return null;
    }
  }
  
  public Font getHeaderFont(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
      
      return ((WrappableHeaderRenderer)tc.getHeaderRenderer()).getFont();
    } else {
      return null;
    }
  }

  public Color getHeaderForeground(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
      
      return ((WrappableHeaderRenderer)tc.getHeaderRenderer()).getForeground();
    } else {
      return null;
    }
  }

  public Color getHeaderBackground(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
      
      return ((WrappableHeaderRenderer)tc.getHeaderRenderer()).getBackground();
    } else {
      return null;
    }
  }

  public JComponent getHeaderRenderer(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
      
      return (WrappableHeaderRenderer)tc.getHeaderRenderer();
    } else {
      return null;
    }
  }
  
  public Dimension getHeaderPreferredSize(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
      
      return ((WrappableHeaderRenderer)tc.getHeaderRenderer()).getPreferredSize();
    } else {
      return null;
    }  
  }

  public AlignmentX getHeaderAlignmentX(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
      
      return ((WrappableHeaderRenderer)tc.getHeaderRenderer()).getAlignX();
    } else {
      return null;
    }  
  }
  
  public AlignmentY getHeaderAlignmentY(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
      
      return ((WrappableHeaderRenderer)tc.getHeaderRenderer()).getAlignY();
    } else {
      return null;
    }  
  }
  
  public Dimension getCellRendererPreferredSize(int colIndex) {
    return getTableCellRendererComponent(colIndex).getPreferredSize();
  }

  public Dimension getCellEditorPreferredSize(int colIndex) {
    return getTableCellEditorComponent(colIndex).getPreferredSize();
  }

  public Font getColumnFont(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
      
      return ((WrappableCellRenderer) tc.getCellRenderer()).getFont();
//      TableCellEditor tce = tc.getCellEditor();
//      
//      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
//        return getTableCellRendererComponent(colIndex).getFont();
//
//      JDataField df = ((JDataField.DataCellEditor) tce).getDataField();
//      return df.getTextFont();
    } else {
      return null;
    }
  }

  public Color getColumnForeground(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
//      TableCellEditor tce = tc.getCellEditor();
//      
//      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
//        return getTableCellRendererComponent(colIndex).getForeground();
//
//      JDataField df = ((JDataField.DataCellEditor) tce).getDataField();
//      return df.getForegroundColor();
      return ((WrappableCellRenderer) tc.getCellRenderer()).getForeground();
    } else {
      return null;
    }
  }

  public Color getColumnBackground(int colIndex) {
    TableColumn tc;
    if (colIndex >= 0 && colIndex < getColumnCount()) {
      tc = getTableColumn(colIndex);
//      TableCellEditor tce = tc.getCellEditor();
//      
//      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
//        return getTableCellRendererComponent(colIndex).getBackground();
//
//      JDataField df = ((JDataField.DataCellEditor) tce).getDataField();
//      return df.getBackgroundColor();
      return ((WrappableCellRenderer) tc.getCellRenderer()).getBackground();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if this is not empty
   *    if visibleOnly  = false
   *      return the number of columns in this
   *    else
   *      return the number of visible columns in this
   *  else
   *    return 0 
   */
  public int getColumnCount(boolean visibleOnly) {
    return dataModel.getColumnCount(visibleOnly);
  }
  
  /**
   * @effects returns an <code>Object[]</code> array of the values in the row
   *          <code>row</code> at the id columns.
   * 
   *          <p>
   *          An id column is a column whose cell editor's domain constraint has
   *          <code>id() = true</code>.
   * @see #isIdColumn(int index)
   * @requires <code>0 <= row < getRowCount()</code>
   */
  public Object[] getIdColumnValues(int row) {
    int colCount = getColumnCount();
    Stack vals = new Stack();
    for (int col = 0; col < colCount; col++) {
      if (isIdColumn(col)) {
        vals.push(getRawValueAt(row, col));
      }
    }

    return (!vals.isEmpty()) ? vals.toArray() : null;
  }

  /**
   * @effects returns the raw (original) value of the table cell
   *          <code>(row,col)</code>.
   */
  public Object getRawValueAt(int row, int col) {
    return dataModel.getRawValueAt(row, col);
  }

  /**
   * @effects 
   *  if colIndex is valid AND there are data rows
   *    return a <tt>String</tt> array containing the values of the column cells at
   *    the index <tt>colIndex</tt>
   *  else
   *    return null
   */
  public String[] getValuesAtColumnAsString(int colIndex) {
    Object[] vals = getValuesAtColumn(colIndex);
    
    if (vals != null) {
      // convert values to strings
      String[] strings = new String[vals.length];
      for (int i = 0; i < vals.length; i++) {
        strings[i] = (vals[i] != null) ? vals[i].toString() : null;
      }
      
      return strings;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if colIndex is valid AND there are data rows
   *    return an <tt>Object</tt> array containing the values of the column cells at
   *    the index <tt>colIndex</tt>
   *  else
   *    return null
   */
  public Object[] getValuesAtColumn(int colIndex) {
    int colCount = getColumnCount();
    if (colIndex >= 0 && colIndex < colCount) {
      int rowCount = getRowCount();
      if (rowCount > 0) {
        Object[] vals = new Object[rowCount];
        for (int row = 0; row < rowCount; row++) {
          vals[row] = getValueAt(row,colIndex);
        }
        return vals;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
  
  /**
   * @effects change the raw (original) value of the table cell <tt>(row,col)</tt> to <tt>object</tt>.
   */
  public void setRawValueAt(Object object, int row, int col) {
    dataModel.setRawValueAt(object,row,col);
  }
  
  /**
   * @effects if <code>o != null && dataModel != null</code> returns the row
   *          matching the row object <code>o</code>, else returns
   *          <code>-1</code>.
   * 
   * @requires <code>o</code> has the same type as that of the elements in the
   *           data model of <code>this</code>
   */
  public int getSelectedRow(Object o) {
    if (o != null && dataModel != null) {
      return dataModel.getRow(o);
    } else {
      return -1;
    }
  }

  /**
   * @effects 
   *  if there is a selected row
   *    return the row object of the selected row
   *  else
   *    return null
   */
  public Object getSelectedObject() {
    int selected = getSelectedRow();
    
    if (selected > -1) {
      return dataModel.getRowData(selected);
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if there are selected row(s)
   *    return the row object(s) of the selected row(s)
   *  else
   *    return null
   * @version 3.0
   */
  public Collection getSelectedObjects() {
    int[] selectedRows = getSelectedRows();
    
    if (selectedRows.length > 0) {
      Collection selectedObjs = new ArrayList();
      Object selectedObj;
      for (int row : selectedRows) {
        selectedObj = dataModel.getRowData(row);
        selectedObjs.add(selectedObj);
      }
      
      return selectedObjs;
    } else {
      return null;
    }
  
  }
  
  /**
   * @requires 
   *  a new row has recently been inserted into the table
   * @effects
   *  return the new row that has recently been inserted
   */
  public int getNewRow() {
    // TODO: support rows inserted in the middle
    // for now assume row is added at the end
    return getRowCount()-1;
  }

  /**
   * @effects 
   *  record column at <tt>col</tt> as being on-focus 
   */
  public void setColumnOnFocus(int col) {
    columnOnFocus = col;
  }
  
  /**
   * @effects 
   *  if there is a column being on-focus (by a previous user mouse-click on a cell)
   *    return the index of that column
   *  else
   *    return -1
   */
  public int getColumnOnFocus() {
    return columnOnFocus;
  }
  
  /**
   * @requires 
   *  alignX != null /\ alignY != null
   * @effects 
   *  set cell's horizontal alignment based on alignX, 
   *  cell's vertical alignment based on alignY
   */
  public void setColumnAlignment(AlignmentX alignX, AlignmentY alignY, int col) {
    TableColumn tc = getTableColumn(col);
    if (tc == null)
      return;

    // the column header
    //JTableHeader thead = getTableHeader();
    WrappableHeaderRenderer thead = (WrappableHeaderRenderer) tc.getHeaderRenderer();
    
    // the cell renderer style
    WrappableCellRenderer cr = (WrappableCellRenderer) tc.getCellRenderer();
    
    if (alignX != null) {
      thead.setAlignmentX(alignX);
      cr.setAlignmentX(alignX);
    }
    
    if (alignY != null) {
      //TODO: implement these
      thead.setAlignmentY(alignY);
      cr.setAlignmentY(alignY);
    }
  }
  
  /**
   * @requires 
   *  style != null
   *  @effects
   *    if exists column at the position <tt>col</tt>
   *      update the display style of the column to <tt>style</tt>
   */
  public void setColumnStyle(Style style, int col) {
    TableColumn tc = getTableColumn(col);
    if (tc == null)
      return;

    // style specification
    Font font = null;
    Color fg = null;
    Color bg = null;
    
    font = GUIToolkit.getFontValue(style.getFont()); 
    fg = GUIToolkit.getColorValue(style.getFgColor());
    bg = GUIToolkit.getColorValue(style.getBgColor());

    // the column header
    // the cell renderer style
    WrappableCellRenderer cr = (WrappableCellRenderer) tc.getCellRenderer();
    
    if (font != null) {
      cr.setFontSetting(font);
    }
    
    if (fg != null) {
      cr.setDefaultForegroundColour(fg); // v3.3
      cr.setForegroundColour(fg);
    }

    if (bg == null) { // v3.3
      bg = WrappableCellRenderer.Color_BgDefault;
    }
      
    // v3.3: if (bg != null) {
    cr.setDefaultBackgroundColour(bg); // v3.3
    cr.setBackgroundColour(bg);
    //}
  }
  
  /**
   * @requires 
   *  style != null
   *  @effects
   *    if exists column header at the position <tt>col</tt>
   *      update the display style of the header to <tt>style</tt>
   */
  public void setHeaderStyle(Style style, int col) {
    TableColumn tc = getTableColumn(col);
    if (tc == null)
      return;

    // style specification
    Font font = null;
    Color fg = null;
    Color bg = null;
    
    font = GUIToolkit.getFontValue(style.getFont()); 
    fg = GUIToolkit.getColorValue(style.getFgColor());
    bg = GUIToolkit.getColorValue(style.getBgColor());

    // the column header
    WrappableHeaderRenderer thead = (WrappableHeaderRenderer) tc.getHeaderRenderer();
    
    // the cell renderer style
    if (font != null) {
      thead.setFontSetting(font);
    }
    
    if (fg != null) {
      thead.setDefaultForegroundColour(fg); // v3.3

      thead.setForegroundColour(fg);
    }

    if (bg == null) { // v3.3
      bg = WrappableHeaderRenderer.Color_BgDefault;
    }
    
    //v3.3: if (bg != null) {
    thead.setDefaultBackgroundColour(bg); // v3.3

    thead.setBackgroundColour(bg);
    //}
  }
  
  /**
   * @requires 
   *  row is a valid row
   * @effects 
   *  if a cell is being edited at row 
   *    return true
   *  else
   *    return false
   */
  public boolean isEditing(int row) {
    if (dataModel != null) {
      return getEditingRow() == row;
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  return the editing mode of this
   */
  public EditMode getEditMode() {
    return editMode;
  }
  
  /**
   * @effects returns <code>true</code> if the column at the specified index
   *          contains auto-generated values, else returns <code>false</code>.
   * 
   *          <p>
   *          A column is auto-generated if the domain constraint associated to
   *          the {@see TableCellEditor} object of that column is defined with
   *          <code>auto=true</code>.
   * 
   * @see #setCellEditor(TableCellEditor, int, boolean)
   */
  public boolean isAutoColumn(int index) {
    DAttr dc = getColumnConstraint(index);

    if (dc != null) {
      return dc.auto();
    } else {
      return false;
    }
  }

  /**
   * @effects 
   *  if the column <tt>index</tt> corresponds to an immutable, non-auto domain attribute
   *    return true
   *  else
   *    return false 
   *   
   * @version 5.1c
   * 
   */
  public boolean isImmutableNonAutoColumn(int index) {
    DAttr dc = getColumnConstraint(index);

    if (dc != null) {
      return dc.mutable() == false && dc.auto() == false;
    } else {
      return false;
    }
  }
  
  /**
   * @effects returns <code>true</code> if the column at the specified index
   *          contains mutable values, else returns <code>false</code>.
   * 
   *          <p>
   *          A column is mutable if the domain constraint associated to
   *          the {@see TableCellEditor} object of that column is defined with
   *          <code>mutable=true</code>.
   * 
   * @see #setCellEditor(TableCellEditor, int, boolean)
   */
  public boolean isMutableColumn(int index) {
    DAttr dc = getColumnConstraint(index);

    if (dc != null) {
      return dc.mutable();
    } else {
      return false;
    }
  }
  
  /**
   * @effects returns <code>true</code> if the column at the specified index
   *          requires values to be specified by the user, else returns
   *          <code>false</code>.
   * 
   *          <p>
   *          A column is optional if the domain constraint associated to the
   *          {@see TableCellEditor} object of that column is defined with
   *          <code>optional=true</code>.
   * 
   * @see #setCellEditor(TableCellEditor, int, boolean)
   */
  public boolean isOptionalColumn(int index) {
    DAttr dc = getColumnConstraint(index);

    if (dc != null) {
      return dc.optional();
    } else {
      return false;
    }
  }

  /**
   * @effects returns <code>true</code> if the column at the specified index is
   *          an id column, else returns <code>false</code>.
   * 
   *          <p>
   *          A column is an id-column if the domain constraint associated to
   *          the {@see TableCellEditor} object of that column is defined with
   *          <code>id=true</code>.
   * 
   * @see #setCellEditor(TableCellEditor, int, boolean)
   */
  public boolean isIdColumn(int index) {
    DAttr dc = getColumnConstraint(index);

    if (dc != null) {
      return dc.id();
    } else {
      return false;
    }
  }

  /**
   * @effects returns <code>true</code> if the column at the index
   *          <code>index</code> is editable.
   * @see #setColumnEditable(int, boolean)
   */
  public boolean isColumnEditable(int index) {
    return dataModel.isColumnEditable(index);
  }

  // setter/getter for edit mode
  public void setCellEditMode(EditMode newEditMode) {
    editMode = newEditMode;
  }

  public EditMode getCellEditMode() {
    return editMode;
  }

  /**
   * @effects sets the editability of all the table cells to <code>b</code>
   */
  public void setCellEditable(boolean b) {
    dataModel.setCellEditable(b);
  }

  /**
   * @effects 
   *  if column at <tt>colIndex</tt> is visible (i.e. its width is non-zero)
   *    return true
   *  else
   *    return false
   */
  public boolean isColumnVisible(int colIndex) {
    TableColumnModel tcm = getColumnModel();
    
    if (colIndex >=0 && colIndex < tcm.getColumnCount()) {
      TableColumn col = tcm.getColumn(colIndex);
      int miw = col.getMinWidth();
      int mxw = col.getMaxWidth();
      
      return !(miw == mxw && miw == 0);
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if <tt>tf = true</tt>
   *    hide column at <tt>colIndex</tt> from viewing
   *  else
   *    unhide column at <tt>colIndex</tt>
   */
  public void setColumnVisible(int colIndex, boolean tf) {
    if (tf == false) {
      // hide the column by forcing the column width to 0
      setColumnWidth(colIndex,0,true);
    } else {
      // unhide the column by setting its width to its cell editor's
      setColumnWidthBestFit(colIndex, true);
    }
  }
  
  /**
   * @effects 
   *  set the width of the column at <tt>colIndex</tt> to best fit the 
   *  preferred width of the cell editor component for that column.
   *  If force = true
   *    fix the column's width so that it may not be adjusted
   */
  public void setColumnWidthBestFit(int colIndex, boolean force) {
    TableColumnModel tcm = getColumnModel();
    TableColumn tc = tcm.getColumn(colIndex);

    Component comp = null;
    int colWidth = 0;
    Dimension dim;

    comp = getTableCellEditorComponent(colIndex);
    dim = comp.getPreferredSize();
    colWidth = dim.width + tcm.getColumnMargin();

    if (force) {
      // fix the column width to the editor's component's width
      tc.setMinWidth(colWidth);
      tc.setMaxWidth(colWidth);
    } else {
      //tc.setWidth(colWidth);
      tc.setPreferredWidth(colWidth);
    }

    // important: must do this to update the table size and make the column appear
    resizeAndRepaint();
  }
  
  /**
   * @effects 
   *  set the preferred width of the column at <tt>colIndex</tt> of this to <tt>width</tt>
   */
  public void setColumnWidth(int colIndex, int width, boolean force) {
    TableColumnModel tcm = getColumnModel();
    
    if (colIndex >=0 && colIndex < tcm.getColumnCount()) {
      TableColumn col = tcm.getColumn(colIndex);
      /* v5.2: call method
      if (force) {
        col.setMinWidth(width);
        col.setMaxWidth(width);
      } else {
        col.setPreferredWidth(width);
      }
      */
      setColumnWidth(col, width, force);
    }
  }
  
  /**
   * A variant of {@link #setColumnWidth(int, int, boolean)} that takes a {@link TableColumn} directly as input. 
   * 
   * @effects 
   *  set the preferred width of the column at <tt>col</tt> of this to <tt>width</tt>
   * @version 5.2
   */
  public void setColumnWidth(TableColumn col, int width, boolean force) {
    if (col == null) return;
    
    if (force) {
      col.setMinWidth(width);
      col.setMaxWidth(width);
      
      //v5.2: must do this for it to take effect
      col.setPreferredWidth(width);
    } else {
      col.setPreferredWidth(width);
    }
    
    // not-needed
    // resizeAndRepaint();
  }
  
  /**
   * @effects 
   *  if the current width of column <tt>tc</tt> differs  from the 
   *  preferred width of <tt>df</tt> plus the column margins
   *    set.tc.width to the preferred width
   *  else
   *    do nothing
   * @version 3.0 
   */
  public void updateColumnWidth(TableColumn tc, JDataField df) {
    TableColumnModel tcm = getColumnModel();

    Dimension dim = df.getPreferredSize();
    int newColWidth = dim.width + tcm.getColumnMargin();
    
    int currWidth = tc.getPreferredWidth();
    
    if (newColWidth != currWidth) {
      tc.setPreferredWidth(newColWidth);
      // important: must do this to update the table size and make the column appear
      resizeAndRepaint();
    }
  }
  
  /**
   * @effects sets the editability of all cells of the column
   *          <code>colIndex</code> to <code>b</code>
   */
  public void setColumnEditable(int colIndex, boolean b) {
    dataModel.setColumnEditable(colIndex, b);
    
    // update the data field bound to this column
    // TODO: this does not work properly: the cell renderer foreground 
    // colour is changed for all columns, not just this column
    setBoundedDataFieldEditable(colIndex, b);
  }

  /**
   * @effects 
   *  if colIndex is a valid column index
   *    change the foreground colour of all cells of the specified column to <tt>c</tt>
   *  else
   *    do nothing 
   */
  public void setColumnForeground(int colIndex, Color c) {
    // WrappableCellRenderer component
    WrappableCellRenderer tcr = (WrappableCellRenderer) 
        getCellRenderer(0, colIndex); 
    
    if (tcr != null) {
      Color myColor = tcr.getForeground(); 
      if (!myColor.equals(c))
        tcr.setForeground(c);//.setForegroundColour(c);
    }
  }
  
  /**
   * @effects 
   *  if tf = true
   *    turn on the default auto-resize behaviour of this
   *  else
   *    turn off auto resize 
   */
  public void setAutoResize(boolean tf) {
    if (tf) {
      setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    } else {
      setAutoResizeMode(AUTO_RESIZE_OFF);
    }
  }
  
  /**
   * @effects returns the <code>Component</code> that is used to edit the cells of the column <code>column</code>
   *          of this table
   * @see {@link #getTableCellEditor(int)}
   */
  public Component getTableCellEditorComponent(int column) {
    TableColumnModel tcm = getColumnModel();
    TableColumn tc = tcm.getColumn(column);
    /*v3.0: use shared method
    return tc.getCellEditor().getTableCellEditorComponent(this, null, false, 0, column);
    */
    TableCellEditor cedit = tc.getCellEditor();
    
    return getTableCellEditorComponent(cedit, null, false, 0, column);
  }
  
  /**
   * @effects 
   *  return the table cell editor component at cell <tt>(row,col)</tt> 
   *  that is needed to display <tt>cellValue</tt>
   * @version 3.0 
   */
  private Component getTableCellEditorComponent(TableCellEditor cedit, 
      Object cellValue, boolean isSelected,
      int row, int col) {
    Component comp;
    if (cedit instanceof DataCellEditor)
      comp = ((DataCellEditor)cedit).getDataField();
    else
      comp = cedit.getTableCellEditorComponent(this, cellValue, isSelected, row, col);
    
    return comp;
  }
  
  /**
   * @effects 
   *  if a <code>JDataField</code> is used as the <tt>TableCellEditor</tt> for the specified column
   *    return it
   *  else
   *    return null
   */
  public JDataField getTableCellEditor(int column) {
    TableColumnModel tcm = getColumnModel();
    TableColumn tc = tcm.getColumn(column);
    TableCellEditor tce = tc.getCellEditor();
    
    // column may not have been set with a cell editor that we want 
    if (tce == null || !(tce instanceof JDataField.DataCellEditor))
      return null;
    
    return ((JDataField.DataCellEditor) tce).getDataField();
  }
  
  /**
   * @effects returns the <code>Component</code> that is used to display the cells of the column <code>column</code>
   *          of this table
   */
  public Component getTableCellRendererComponent(int column) {
    TableColumnModel tcm = getColumnModel();
    TableColumn tc = tcm.getColumn(column);
    return tc.getCellRenderer().getTableCellRendererComponent(this, null, false, false, 0, column);
  }
  
  //TODO: should we combine this with the method getTableCellRendererComponent above?
//  @Override
//  public TableCellRenderer getDefaultRenderer(Class columnClass) {
//    return defaultCellRenderer;
//  }
  
  /**
   * @effects adds a new (empty) data row to the table
   */
  public int addRow() {
    return dataModel.addRow();
  }

  /**
   * @effects sets the row at the index <code>row</code> to <code>vals</code>
   */
  public void setRowData(int row, Object vals) {
    dataModel.setRowData(row, vals);
  }

  /**
   * @effects sets the row at the index <code>row</code> to the init row object
   *          created by <code>dataModel</code>.
   */
  public void setInitRowData(int row) {
    Object vals = dataModel.initRow();
    setRowData(row, vals);
  }

  /**
   * @effects delete the rows whose index positions are in <code>rows</code>.
   * @requires <code>rows != null</code>
   */
  public void deleteRows(int[] rows, boolean askUser) {
    if (rows == null) {
      return;
    }

    boolean user;
    if (askUser) {
      user = GUIToolkit.confirm(TableMessageCode.CONFIRM_DELETE_ROWS, this, "Xóa dòng", null);
    } else
      user = true;

    if (user) {
      dataModel.deleteRows(rows);
    }
  }

  /**
   * @effects delete the row whose index position is <code>row</code>.
   */
  public void deleteRow(int row, boolean askUser) {
    boolean user;
    if (askUser) {
      user = GUIToolkit.confirm(TableMessageCode.CONFIRM_DELETE_SELECTED_ROWS, this, "Xóa dòng", 
          new Object[] {row + 1});
    } else
      user = true;

    if (user) {
      dataModel.deleteRow(row);
    }
  }

  /**
   * @effects insert a row before each row in <code>rows</code>.
   * @requires <code>rows != null</code>
   */
  public void insertRow(int[] rows) {
    if (rows == null)
      return;

    if (GUIToolkit.confirm(TableMessageCode.CONFIRM_INSERT_ROWS, this, "Thêm dòng", null)) {
      dataModel.insertRow(rows);
    }
  }

  /**
   * @effects 
   *  prompt user for confirmation to insert a row before row <code>row</code>
   *  if confirmed
   *    insert the row
   *  else
   *    do nothing
   */
  public void insertRow(int row) {
    insertRow(row, true);
  }

  /**
   * @effects 
   *  if askUser = true
   *    prompt user for confirmation to insert a row before row <code>row</code>
   *  
   *  if askUser = false or user confirmed 
   *    insert the row
   *  else
   *    do nothing
   */
  public void insertRow(int row, boolean askUser) {
    boolean user;
    if (askUser) {
      user = GUIToolkit.confirm(TableMessageCode.CONFIRM_INSERT_A_ROW, this, "Thêm dòng", null);
    } else
      user = true;

    if (user) {
      dataModel.insertRow(row);
    }
  }
  
  /**
   * @effects 
   *  if this.selectionModel = CellSelection
   *    select the cell of row that is either at the same column as 
   *      the currently selected cell or the first cell 
   *  else
   *    select row 
   *  @version 
   *  - 3.1: improved to nagivate to the selected row
   */
  public void selectRow(int row) {
    // find the currently selected column (if any)
    int col = getColumnOnFocus();
    if (col < 0)
      col = 0;
    
    if (getCellSelectionEnabled()) {
//      // cell selection 
//      setCellSelected(row, col);
      // v3.1: navigate to the selected row
      changeSelection(row, col, false, false);
    } else {
      //setRowSelectionInterval(row, row);
      changeSelection(row, 0, false, false);
    }
  }
  
  /**
   * Overrides super-class method to update the cell editor with the current
   * cell's value, especially when the editor is a list-type component (e.g. a
   * JSpinner).
   * 
   * @see #setCellEditor(TableCellEditor, int, boolean)
   */
  public boolean editCellAt(int row, int column, EventObject obj) {

    // if edit mode is ON_DOUBLE_CLICK and obj is MouseEvent with click-count =
    // 1 then returns false
    // System.out.println("Event object: " + obj.getClass());

    // only allow editing for double-click mouse event
    if (editMode.equals(EditMode.ON_DOUBLE_CLICK) && obj instanceof MouseEvent) {
      int ccount = ((MouseEvent) obj).getClickCount();
      if (ccount == 1)
        return false;
    }

    return doEditCellAt(row, column, obj);
    
//    // get the raw value from the cell to use on the editor
//    Object val = getRawValueAt(row, column);
//
//    TableCellEditor tedit = getCellEditor(row, column);
//    if (tedit instanceof UpdatableCellEditor) {
//      UpdatableCellEditor cedit = (UpdatableCellEditor) tedit;
//
//      try {
//        if (val != null && !val.equals("")) {
//          cedit.setCellEditorValue(val);
//        } else {
//          // reset values
//          cedit.reset();
//        }
//        return super.editCellAt(row, column, obj);
//      } catch (Exception e) {
//        e.printStackTrace();
//        return false;
//      }
//    } else {
//      return super.editCellAt(row, column, obj);
//    }
  }

  /**
   * @effects
   *  prepares the cell at <tt>(row, column)</tt> for editing by the user.
   *  
   *  This involves initialising (if not already done so) a suitable {@link UpdatableCellEditor} and 
   *  setting the current value of that editor to the cell's value.
   */
  protected boolean doEditCellAt(int row, int column, EventObject obj) {
    // get the raw value from the cell to use on the editor
    Object val = getRawValueAt(row, column);

    TableCellEditor tedit = getCellEditor(row, column);
    if (tedit instanceof UpdatableCellEditor) {
      UpdatableCellEditor cedit = (UpdatableCellEditor) tedit;
      
      try {
        if (val != null && !val.equals("")) {
          cedit.setCellEditorValue(val);
        } else {
          // reset values
          cedit.reset();
        }
        return super.editCellAt(row, column, obj);
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    } else {
      return super.editCellAt(row, column, obj);
    }
  }

  @Override
  protected JTableHeader createDefaultTableHeader() {
    // create a custome table header which
    // supports mapping from table columns to their GUI IDs

    return new CustomTableHeader(columnModel);
  }

  /**
   * @effects 
   *  if fromIndex is valid
   *    return a <tt>String</tt> array of the header names starting from 
   *    column <tt>fromIndex</tt> and, if <tt>visibleOnly=true</tt>, ignoring
   *    the hidden columns
   *  else
   *    return null
   */
  public String[] getTableHeadersAsString(int fromIndex, boolean visibleOnly) {
    int colCount = getColumnCount();
    
    if (fromIndex >= 0 && fromIndex < colCount) {
      // use list since we dont know for sure how many visible columns
      // starting from fromIndex
      List<String> headers = new ArrayList();
      for (int i = fromIndex; i < colCount; i++) {
        if (isColumnVisible(i)) {
          headers.add(getColumnName(i));
        }
      }
      return headers.toArray(new String[headers.size()]);
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if colIndex is valid
   *    return the name of the column at <tt>colIndex</tt>
   *  else
   *    return null
   * @return
   */
  public String getTableHeaderAsString(int colIndex) {
    int colCount = getColumnCount();
    if (colIndex >= 0 && colIndex < colCount) {
      return getColumnName(colIndex);
    } else {
      return null;
    }
  }
  
  /* (non-Javadoc)
   * @see domainapp.basics.view.JDataContainer#onNewObject(Object)
   */
  @Override
  public void onNewObject(Object index) {
    DataContainerToolkit.onNewObject(this);
    
    // inform data model that user is creating a new object on row  = index
    dataModel.setNewRowIndex((Integer)index);
  }
  
  
  /* (non-Javadoc)
   * @see domainapp.basics.view.JDataContainer#onCreateObject(java.lang.Object)
   */
  @Override
  public void onCreateObject(Object obj) {
    DataContainerToolkit.onCreateObject(this, obj);
    
    // inform data model that user has FINISHED creating a new object
    dataModel.setNewRowIndex(null);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.view.JDataContainer#onCancel()
   */
  @Override
  public void onCancel() {
    DataContainerToolkit.onCancel(this);
    
    // inform data model that user has FINISHED creating a new object
    dataModel.setNewRowIndex(null);
  }
  
  /**
   * Change the table model to its initial state, which in most case mean to
   * clear the table data.
   */
  //v2.7.4
  @Override
  public void reset() {
    dataModel.clear();
  }

  /**
   * This method is used to force the table to refresh its display of the data
   * when the model has been changed (possibly by other source) without the
   * table knowing it.
   * 
   * @effects invokes <code>fireTableDataChanged</code> on the data model and 
   *  update table view
   */
  public void refresh() {
    if (dataModel != null) {
      dataModel.fireTableDataChanged();
      updateTableView();
    }
  }

  @Override
  public void refreshLinkedData() {
    // find the bounded data fields that are configured with a property to reload data on refresh 
    // and refresh their bindings to their target data sources
    TableColumnModel tcm = getColumnModel();
    int colCount = tcm.getColumnCount();
    
    TableColumn tc;
    TableCellEditor tce;
    JDataField.DataCellEditor dce;
    JDataField df;
    JBindableField bdf;
    Region compCfg;
    for (int col = 0; col < colCount; col++) {
      tc = tcm.getColumn(col);
      tce = tc.getCellEditor();
      compCfg = cfgMap.get(tce);
      
      // column may not have been set with a cell editor that we want 
      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
        continue;
      
      dce = (JDataField.DataCellEditor) tce;
      df = dce.getDataField();    
      if (df instanceof JBindableField
          && compCfg.getProperty(PropertyName.view_objectForm_dataField_reloadBoundedDataOnRefresh, 
              Boolean.class, Boolean.FALSE) // added check that bdf.config has property reload set to true
          ) {
        bdf = (JBindableField) df;
        bdf.reloadBoundedData();

        //if this is the link column the try resetting the value
        if (isLinkColumn(col)) {
          Object linkValue = getLinkValue();
          if (linkValue != null)
            bdf.setValue(linkValue);
          else
            bdf.reset();
        }
      }     
    }    
  }
  
  @Override
  public void refreshTargetDataBindings() {
    // find all bounded data fields and refresh their bindings to their target data sources
    /**
     * the data fields are in the column cell editors
     * (see setCellEditor() for details)
     */
    TableColumnModel tcm = getColumnModel();
    int colCount = tcm.getColumnCount();
    
    TableColumn tc;
    TableCellEditor tce;
    JDataField.DataCellEditor dce;
    JDataField df;
    JBindableField bdf;
    for (int col = 0; col < colCount; col++) {
      tc = tcm.getColumn(col);
      tce = tc.getCellEditor();
      
      // column may not have been set with a cell editor that we want 
      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
        continue;
      
      dce = (JDataField.DataCellEditor) tce;
      df = dce.getDataField();    
      if (df instanceof JBindableField) {
        bdf = (JBindableField) df;
        bdf.reloadBoundedData();

        //if this is the link column the try resetting the value
        if (isLinkColumn(col)) {
          Object linkValue = getLinkValue();
          if (linkValue != null)
            bdf.setValue(linkValue);
          else
            bdf.reset();
        }
      }     
    }
  }
  
  @Override
  public void refreshTargetDataBindingOfAttribute(DAttr attrib) {
    // find the bounded data field of attrib and refresh its binding to the target data source
    /**
     * the data fields are in the column cell editors
     * (see setCellEditor() for details)
     */
    TableColumnModel tcm = getColumnModel();
    int colCount = tcm.getColumnCount();
    
    TableColumn tc;
    TableCellEditor tce;
    JDataField.DataCellEditor dce;
    JDataField df;
    JBindableField bdf;
    for (int col = 0; col < colCount; col++) {
      tc = tcm.getColumn(col);
      tce = tc.getCellEditor();
      
      // column may not have been set with a cell editor that we want 
      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
        continue;
      
      dce = (JDataField.DataCellEditor) tce;
      df = dce.getDataField();    
      if (df instanceof JBindableField && df.getDomainConstraint() == attrib) {
        // exists data field of attrib
        bdf = (JBindableField) df;
        bdf.reloadBoundedData();

        //if this is the link column the try resetting the value
        if (isLinkColumn(col)) {
          Object linkValue = getLinkValue();
          if (linkValue != null)
            bdf.setValue(linkValue);
          else
            bdf.reset();
        }
        
        break;
      }     
    }
  }

  /**
   * This method is used to force the table to refresh its display of the data
   * when the model has been changed (possibly by other source) without the
   * table knowing it.
   * 
   * @effects invokes <code>fireTableDataChanged</code> on the data model 
   */
  public void refreshData() {
    if (dataModel != null) {
      dataModel.fireTableDataChanged();
    }
  }
  
  @Override
  public void addMouseListener(MouseListener ml) {
    // registers the mouse listener to both the table and its header components
    super.addMouseListener(ml);
    getTableHeader().addMouseListener(ml);
  }

  @Override
  public String toString() {
    return "JDataTable("+getName()+")";
  }
  
//  @Override
//  protected void paintComponent(Graphics g) {
//    super.paintComponent(g);
//  }
  
  /**
   * This method is used to update the table GUI after changes are made to its 
   * interface (e.g. after the cell's renderer has been updated). To reduce performance
   * overhead, we only update the rectangle corresponding to a cell that is changed. 
   * 
   * @effects 
   *  repaint the cell rectangle at the intersection of row <tt>row</tt> and 
   *  column <tt>col</tt>
   */
  public void repaint(int row, int col) {
    /**
     * forces the table to repaint the cell at (row,col)
     */
    Rectangle cellRect = getCellRect(row, col, false);
    repaint(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
  }
  
  /**
   * @effects 
   *  if parent container != null 
   *    use it as parent to display <tt>mesg</tt> with message data <tt>data</tt>
   *  else 
   *    display <tt>mesg</tt> with message data <tt>data</tt> on this table
   */
  protected boolean displayConfirm(
      // v3.2: String mesg,
      InfoCode mesgCode, 
      Object...data) {
    if (parent != null) {
      // v3.2:
//      return parent.getController().getCreator().displayConfirm(null, 
//          parent.getController(),
//          mesg, data);
      return parent.getController().getCreator().displayConfirmFromCode(mesgCode, 
          parent.getController(), data);

    } else {
      String mesg = mesgCode.getMessageFormat().format(data);
      return GUIToolkit.confirm(mesgCode, this, "Xác nhận", mesg, data);
    }
  }
  
  // ///// END of JDataTable definition
  
  private static class CustomTextPane extends JTextPane {
    protected static final Border LINE_BORDER = 
        BorderFactory.createLineBorder(Color.BLUE,2);
    protected static final Border EMPTY_BORDER = 
        BorderFactory.createEmptyBorder(2,2,2,2);
    protected static final Border RAISED_BORDER = 
        BorderFactory.createRaisedBevelBorder();

    // v3.3
    protected static final Color COLOR_LIGHT_GRAY = new Color(214,217,223);

    protected Border defBorder;
    private javax.swing.text.Style style;
    
    private Font font;
    private Color defFg; //3.3
    private Color fg;
    private Color defBg; // v3.3
    private Color bg;
    
    private StyledDocument doc;
    private AlignmentY alignY;
    private AlignmentX alignX;
    
    private UIDefaults defaults;  // v3.3
    private static final Object TextPaneBgPainterUIDefault = "TextPane[Enabled].backgroundPainter";

    protected CustomTextPane(Border border) {
      super();

      this.defBorder = border;
      setBorder(border);
      
      doc = getStyledDocument();
      javax.swing.text.Style def = doc.getStyle(StyleContext.DEFAULT_STYLE);
      
      style = doc.addStyle("custom", def);
      
      // v3.3: use this to change the background colour  
      defaults = new UIDefaults();
      putClientProperty("Nimbus.Overrides", defaults);
      putClientProperty("Nimbus.Overrides.InheritDefaults", true);
    }
    
    public CustomTextPane() {
      this(EMPTY_BORDER);
    }
    
    public void highlight(boolean tf) {
      if (tf) {
        setBorder(LINE_BORDER);
      } else {
        setBorder(defBorder);
      }
    }
    
    protected void setFontSetting(Font f) {
      font = f;
      String family = f.getFamily();
      StyleConstants.setFontFamily(style, family);
      StyleConstants.setFontSize(style, f.getSize());
      int fstyle = f.getStyle();
      
      boolean isBold = (fstyle == Font.BOLD) || (fstyle == (Font.BOLD | Font.ITALIC));
      boolean isItalic = (fstyle == Font.ITALIC) || (fstyle == (Font.BOLD | Font.ITALIC));
      if (isBold) {
        // bold
        StyleConstants.setBold(style, true);
      } 
      
      if (isItalic) {
        StyleConstants.setItalic(style, true);
      }
      
      applyCharStyle();

    }
    
    @Override
    public Font getFont() {
      return (font != null) ? font : super.getFont(); 
    }

    @Override
    public Color getForeground() {
      return (fg != null) ? fg : super.getForeground();
    }

    /**
     * @effects 
     *  return the initial foreground colour of this (i.e. that which was set the first time
     *  this was created by {@link #setDefaultForegroundColour(Color)})
     *  
     * @version 3.3 
     */
    protected Color getDefaultForegroundColour() {
      return defFg;
    }
    
    /**
     * @effects 
     *  set the initial foreground colour of this (i.e. that which was set the first time
     *  this was created)
     *  
     * @version 3.3 
     */
    protected void setDefaultForegroundColour(Color defFg) {
      this.defFg = defFg;
    }
    
    @Override
    public Color getBackground() {
      return (bg != null) ? bg : super.getBackground();
    }

    /**
     * @effects 
     *  set the initial background colour of this (i.e. that which was set the first time
     *  this was created)
     *  
     * @version 3.3 
     */
    protected void setDefaultBackgroundColour(Color bg) {
      this.defBg = bg;
    }
    
    /**
     * @effects 
     *  return the initial background colour of this (i.e. that which was set by {@link #setDefaultBackgroundColour(Color)})
     *  
     * @version 3.3 
     */
    protected Color getDefaultBackgroundColour() {
      return this.defBg;
    }
    
    protected void setForegroundColour(Color c) {
      fg = c;
      //StyledDocument doc = getStyledDocument();
      StyleConstants.setForeground(style, c);
      applyCharStyle();
    }

    /**
     * @effects 
     *  reset foreground colour of this to {@link #defFg}
     *  
     * @version 3.3
     */
    protected void setForegroundColourToDefault() {
      setForegroundColour(getDefaultForegroundColour());      
    }
    
    protected void setBackgroundColour(Color c) {
      bg = c;
      // v3.3: this does not work
      //setBackground(c);
      defaults.put(TextPaneBgPainterUIDefault, c);
      setBackground(c);
      
      //this only sets text background 
//      String bgColorStyle = "background-color:" + getHTMLColor(c);
//      SimpleAttributeSet bgColorAttr = new SimpleAttributeSet();
//      bgColorAttr.addAttribute(HTML.Attribute.STYLE, bgColorStyle);
//      
//      //MutableAttributeSet attrSet = new SimpleAttributeSet();
//      style.addAttribute(HTML.Tag.SPAN, bgColorAttr);
//      
//      //Next line is just an instruction to editor to change color
//      StyleConstants.setBackground(style, c);
//      setCharacterAttributes(style, false);
    }

    /**
     * @effects 
     *  reset background colour of this to {@link #defBg}
     *  
     * @version 3.3
     */
    protected void setBackgroundColourToDefault() {
      setBackgroundColour(getDefaultBackgroundColour());      
    }
    
    /**
     * @effects 
     *  if this background color is same as {@link #defBg}
     *    return true
     *  else
     *    return false
     * @version 3.3
     */
    public boolean isDefaultBackground() {
      return ((bg == null && defBg == null) ||  
          (bg != null && defBg != null && bg.equals(defBg)));
    }
    
    /**
     * @effects 
     *  if this background color is same as {@link #defFg}
     *    return true
     *  else
     *    return false
     * @version 3.3
     */
    public boolean isDefaultForeground() {
      return ((fg == null && defFg == null) || 
          (fg != null && defFg != null && fg.equals(defFg)));
    }    
//    @Override
//    public void setBackground(Color c) {
//      System.out.println(this.getClass().getSimpleName()+"("+hashCode()+"): bg = " + c);
//      super.setBackground(c);
//    }
    
    protected void setAlignmentX(AlignmentX alignX) {
      this.alignX = alignX;
      int ax = GUIToolkit.toEditorPaneAlignmentX(alignX);
      StyleConstants.setAlignment(style, ax);
      // apply style to all existing text
      applyParStyle();
    }
    
    public AlignmentX getAlignX() {
      return alignX;
    }

    protected void setAlignmentY(AlignmentY alignY) {
      this.alignY = alignY;
      //TODO: support this 
      /*
      //int aY = GUIToolkit.toEditorPaneAlignmentY(alignY);
      float spaceAbove = StyleConstants.getSpaceAbove(style);
      float spaceBelow = StyleConstants.getSpaceBelow(style);
      
      
      // recompute space above to position text in the middle
      if (alignY == AlignmentY.Top) {
        // do nothing
      } else if (alignY == AlignmentY.Middle){
        float space = 0.5f*(spaceAbove + spaceBelow);
        StyleConstants.setSpaceAbove(style, space);
        StyleConstants.setSpaceAbove(style, space);
      }
      
      applyParStyle();
      */
    }
    
    public AlignmentY getAlignY() {
      return alignY;
    }
    
    private void applyParStyle() {
      doc.setParagraphAttributes(0, doc.getLength(), style, true);  
    }

    private void applyCharStyle() {
      doc.setCharacterAttributes(0, doc.getLength(), style, true);  
    }

    protected void addText(String s) {
      try {
        int offSet = doc.getLength();
        doc.setParagraphAttributes(offSet, s.length(), style, false);

        doc.insertString(offSet, s, style);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    }
  } /** end {@link CustomTextPane} */
  
  /**
   * @overview 
   *  A <tt>TableCellRenderer</tt> that supports text wrapping
   * @author dmle
   */
  static class WrappableCellRenderer 
    extends CustomTextPane 
    implements TableCellRenderer {
    
    private static final Color Color_BgDefault = Color.WHITE; // v3.3

    // record the row heights
//    Map<Integer,MaxHeight> heights;
    
    // user a panel with border layout to 
    // expand the text area automatically 
    private JPanel panel;
    
    private JDataTable owner;
    
    public WrappableCellRenderer(JDataTable owner) {
      //super(2,5);
      super(EMPTY_BORDER);
      //setLineWrap(true);
      //v2.7.2
      //setWrapStyleWord(true);
      
      panel = new JPanel(new BorderLayout());
      //panel.setOpaque(true);
      
      panel.add(this);

      this.owner = owner;
      
//      heights = new HashMap();
    }
    
    public JDataTable getTable() {
      return owner;
    }
    
//    @Override // v3.3
//    protected void setBackgroundColour(Color c) {
//      super.setBackgroundColour(c);
//      
//      // need to set background colour for panel b/c it is used as the renderer
//      panel.setBackground(c);
//    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      
      /**v2.5.4: display the value in its original form (e.g. formatted) */
      if (value != null) {
        TableCellEditor tce = owner.getCellEditor(row, column);
        if (tce == null || !(tce instanceof JDataField.DataCellEditor)) {
          // not the editor we know -> use default format
          setText(value.toString());
        } else {
          JDataField.DataCellEditor dce = (JDataField.DataCellEditor) tce;
          JDataField df = dce.getDataField();
          
          if (df.isSupportValueFormatting()) {
            setText(df.getFormattedValue(value));
          } else {
            setText(value.toString());
          }
          
          // v3.3: support group-by 
          configureForGroupBy(df, value, row, column);
        }
      } else {
        setText(null);
      }
      
      // v3.3: support other customisation settings
      owner.configureForDomainCustomisation(this, row, column, value);
      
      // if selected then highlight 
      if (isSelected) {
        setBorder(LINE_BORDER);
      } else {
        setBorder(defBorder);
      }
      
      return panel;
    }

    /**
     * @effects 
     *  if group-by configuration is specified for <tt>column</tt> AND value is part of a value group 
     *    configure this cell render suitable for group-by setting
     *  else
     *    do nothing
     * @version 3.3
     */
    private void configureForGroupBy(JDataField df, Object value, int row, int column) {
      String[] groupByAttribs = owner.getConfigProperty(PropertyName.view_objectForm_groupBy, String[].class, null);
      if (groupByAttribs != null) {
        // group-by is specified, check that it is applied to this column
        String colAttrib = df.getDomainConstraint().name();
        boolean groupByApplicable = false;
        for (String groupByAttrib : groupByAttribs)
          if (colAttrib.equals(groupByAttrib)) {groupByApplicable = true; break;}
          
        if (groupByApplicable) {
          // group by is indeed applied to this column
          // check if value is part of a group
          Object prevValue = null;
          if (row > 0)
            prevValue = owner.getValueAt(row-1, column);
          
          boolean sameGroup = prevValue != null && value != null && value.equals(prevValue); 
              
          if (sameGroup) {
            // value belongs to same value group
            // a simple configuration: hide value by setting foreground of this to be a light-gray color
            // (do not use back-ground color b/c this does not differientiate with null values)
            Color bg = COLOR_LIGHT_GRAY; // getBackground()
            //cannot not do this check here b/c setForegroundColour also sets the text fore-ground
            // if (bg == null || !bg.equals(getForeground())){
            setForegroundColour(bg);
            //}
          } else {
            // value does not belong to same group: reset color
            setForegroundColourToDefault();
          }
        }
      }
    }
  } // END WrappableCellRenderer
  
  
  /**
   * 
   * @overview Represents a custom multi-line header render for {@link JDataTable}. 
   *
   * @author Duc Minh Le (ducmle)
   */
  private static class WrappableHeaderRenderer extends CustomTextPane 
  implements TableCellRenderer {
    private static final Color Color_BgDefault = new Color(255, 255, 220);
    private static final Color Color_BgHighlighted = new Color(100, 100, 100);
    
    private static final Font DefaultHeaderFont = new Font("Arial", Font.BOLD, 13);
    
    /**
     * @effects initialises this with default size and {@link #DefaultHeaderFont}. 
     *
     * @version
     */
    public WrappableHeaderRenderer() {
      this(DefaultHeaderFont, true);
    }
    
    /**
     * @effects initialises this with {@link #DefaultHeaderFont} and if <tt>withDefaultSize = true</tt> then 
     * with the default size. 
     *
     * @version
     * - 5.2: changed to use a new constructor
     */
    public WrappableHeaderRenderer(boolean withDefaultSize) {
      /*
      super(RAISED_BORDER);
      
      //v3.3: not needed b/c this bg color was set later via setBackgroundColor
      // setBackgroundColour(Color_BgDefault);
      
      setFontSetting(DefaultHeaderFont);
      setForegroundColour(Color.GRAY);
      
      //TODO: can we adjust this based on column width?
      if (withDefaultSize) {
        setPreferredSize(new Dimension(10,40));
        setMinimumSize(new Dimension(10,30));
      }
      */
      this(DefaultHeaderFont, withDefaultSize);
    }
    
    /**
     * @effects
     *  initialises this with <tt>font</tt> and if <tt>withDefaultSize = true</tt> then 
     * with the default size. 
     * 
     * @version 5.2 
     */
    public WrappableHeaderRenderer(Font font, boolean withDefaultSize) {
      super(RAISED_BORDER);
      
      //v3.3: not needed b/c this bg color was set later via setBackgroundColor
      // setBackgroundColour(Color_BgDefault);
      
      setFontSetting(font);
      setForegroundColour(Color.GRAY);
      
      //TODO: can we adjust this based on column width?
      if (withDefaultSize) {
        setPreferredSize(new Dimension(10,40));
        setMinimumSize(new Dimension(10,30));
      }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus, int row,
        int column) {
//      if (isSelected)
//        setBackgroundColour(Color_BgHighlighted);
//      else
//        setBackgroundColour(Color_BgDefault);
      return this;
    }
    
    /**
     * @effects 
     *  calculate the best-fit size of a {@link WrappableHeaderRenderer} whose value is set to <tt>headerText</tt>
     *  that would be used for the column <tt>column</tt> of <tt>owner</tt>
     *   
     * @version 
     * - 5.2: improved to use the font of owner's parent container and to return {@link Dimension}.
     */
    public Dimension getHeaderHeightBestFit(JDataTable owner, int column, String headerText) {
      int colWidth = owner.getColumnWidth(column);
      
      // create a renderer object with the default size and use it 
      // to compute the best fit height 
      /*v5.2: improved to use container View's font
      WrappableHeaderRenderer r = new WrappableHeaderRenderer(false);
      */
      /*v5.3: fix NullPointerException 
      Font font = owner.getParentContainer().getGUIComponent().getFont();
      */
      Font font;
      JDataContainer ownerCont = owner.getParentContainer();
      if (ownerCont != null) {
        font = ownerCont.getGUIComponent().getFont();
      } else {
        font = owner.getFont();
      }
      
      WrappableHeaderRenderer r = new WrappableHeaderRenderer(font, false);
      
      r.setText(headerText);
      Dimension prefSize = r.getPreferredSize();
      // re-size the width of this to the column width so that its height
      // can be recalculated based on the header text
      r.setSize(colWidth, prefSize.height);
      
      // obtain preferred size again and return its height
      prefSize = r.getPreferredSize();
      // v5.2: return (int)prefSize.getHeight();
      return prefSize;
    }
    
//    Component getTableCellRendererComponentForSizeMeasure(JTable table,
//        Object value, boolean isSelected, boolean hasFocus, int row,
//        int column) {
//      int colWidth = ((JDataTable)table).getColumnWidth(column);
//      String headerText = value.toString();
//      
//      WrappableHeaderRenderer r = new WrappableHeaderRenderer(false);
//      r.setText(headerText);
//      Dimension prefSize = r.getPreferredSize();
//      // re-size the width of this to the column width so that its height
//      // can be recalculated based on the header text
//      r.setSize(colWidth, prefSize.height);
//      return r;
//    }
  }
  
  /**
   * @requires 
   *  row, column are valid
   * @effects 
   *  if there is domain-specific settings for <tt>cell(row, column)</tt> whose value is <tt>cellValue</tt>  
   *    use them to configure <tt>cellRenderer</tt> of that cell
   *  else
   *    do nothing  
   *  
   * @version 3.3
   */
  protected void configureForDomainCustomisation(WrappableCellRenderer cellRenderer, int row, int column, Object cellValue) {
    // for sub-types to implement
  }
  
  /**
   * A sub-class of <code>JTableHeader</code> used to customise how table
   * headers are manipulated.
   * <p>
   * This class supports a feature that allows table columns to be associated to
   * a numeric GUI ID. This is the id of the configuration setting held in the
   * database for that column. An id is used to update the GUI settings of the
   * column to which it is associated.
   */
  public class CustomTableHeader extends JTableHeader {

    public CustomTableHeader(TableColumnModel columnModel) {
      super(columnModel);
    }

    /**
     * @effects sets the column header value whose domain column name is
     *          <code>colName</code> to <code>txt</code>
     */
    public void setText(String domainName, String txt) {
      // find the column has this name and changes its header
      TableColumn col = getColumn(domainName);
      if (col != null) { // v3.0: added this check (should not be needed)
        col.setHeaderValue(txt);
        // v3.0: FIXED: added this
        ((WrappableHeaderRenderer) col.getHeaderRenderer()).setText(txt);
      }
    }

//    /**
//     * @effects returns a custom <code>TableCellRenderer</code> which uses a
//     *          <code>JTextArea</code> to render the headers.
//     * 
//     *          <p>
//     *          The benefit of using <code>JTextArea</code> is that it supports
//     *          line-wrapping of long text.
//     */
//    public TableCellRenderer getDefaultRenderer() {
//      // TODO: make the text area's rows adjustable depending on the text that
//      // is displayed
//      // - for now, use the common sense value of 2
//      
//      final JTextArea ta = new JTextArea(2, 3);
//      ta.setLineWrap(true);
//      ta.setWrapStyleWord(true); // wrap at word boundary
//      ta.setBackground(new Color(255, 255, 220));
//      ta.setBorder(BorderFactory.createRaisedBevelBorder());
//      TableCellRenderer headerRenderer = new TableCellRenderer() {
//        public Component getTableCellRendererComponent(JTable table,
//            Object value, boolean isSelected, boolean hasFocus, int row,
//            int column) {
//          ta.setText(value.toString());
//
//          return ta;
//        }
//      };
//      
//      return headerRenderer;
//
//    }
  } // end CustomTableHeader

  // /// nested class DataModel
  protected class DataModel extends AbstractTableModel {
    protected List tableData;
    protected List<String> header;
    private boolean cellEditable;
    private Boolean[] colEditables;

    private Integer newRowIndex;  // v5.1c
    
    public DataModel() {
      this(null);
    }

    public DataModel(List header) {
      this.header = header;
      cellEditable = true; // default: can edit all cells
      colEditables = new Boolean[header.size()]; // column
      Arrays.fill(colEditables, null);

      createInitData();
    }

    protected void createInitData() {
      // initialise an empty table data
      tableData = new ArrayList();
    }

    protected Object initRow() {
      List r = new ArrayList();
      for (int i = 0; i < header.size(); i++) {
        // r.add("");
        r.add(null);
      }
      return r;
    }

    void setHeader(List newHeader) {
      boolean fireEvent = false;
      if (header != null) {
        tableData.clear();
        fireEvent = true;
      }

      header = newHeader;
      // initialise the data map with a single row
      tableData.add(initRow());

      if (fireEvent)
        fireTableDataChanged();
    }

//    void setModel(final List data) {
//      this.tableData = data;
//      fireTableDataChanged();
//    }

    /**
     * This method is used to populate table data with elements from an existing 
     * collection. Manual update of the table model is required when the collection is changed. 
     * 
     * @effects 
     *  copy the elements of <tt>tableData</tt> over to the table model of this. 
     *  update the table view.
     */
    void setModel(final Collection data) {
      tableData.clear();
      
      tableData.addAll(data);
      fireTableDataChanged();
    }
    
    protected int addRow() {
      tableData.add(initRow());
      int last=tableData.size()-1;
      //fireTableDataChanged();
      fireTableRowsInserted(last, last);
      updateTableView();
      return last;
    }

    /**
     * Note: This is invoked when the user has requested to create a new data object/record. 
     * Although this is invoked at the same as {@link #addRow()} and it appears logical do do so, 
     * we dont invoke {@link #setNewRowIndex(Integer)} from within {@link #addRow()}. 
     * This is because {@link #addRow()} is actually used  
     * by other processes that are different from add-new-object.
     * 
     * @effects 
     *  sets {@link #newRowIndex} = <tt>rowIndex</tt>   
     * @version 5.1c
     */
    void setNewRowIndex(Integer rowIndex) {
      newRowIndex = rowIndex;
    }
    
    void deleteRows(int[] rows) {
      for (int i = rows.length - 1; i >= 0; i--) {
        tableData.remove(rows[i]);
      }

      fireTableDataChanged();
      updateTableView();
    }

    void deleteRow(int row) {
      tableData.remove(row);

      fireTableDataChanged();
      updateTableView();
    }

    void insertRow(int[] rows) {
      for (int i = rows.length - 1; i >= 0; i--) {
        tableData.add(rows[i], initRow());
      }

      fireTableDataChanged();
      updateTableView();
    }

    void insertRow(int row) {
      tableData.add(row, initRow());

      //v2.6.4.a fireTableDataChanged();
      fireTableRowsInserted(row, row);
      updateTableView();
    }

    @Override
    public String getColumnName(int col) {
      if (header != null)
        return header.get(col);
      else
        return null;
    }

    @Override
    public int getRowCount() {
      if (tableData != null)
        return tableData.size();
      else
        return 0;
    }

    /**
     * @effects 
     *  if this is not empty
     *    if visibleOnly  = false
     *      return the number of columns in this
     *    else
     *      return the number of visible columns in this
     *  else
     *    return 0 
     */
    public int getColumnCount(boolean visibleOnly) {
      int colCount = 0;
      if (header != null) {
        int count = header.size();
        if (visibleOnly == false) {
          colCount = count;
        } else {
          for (int i = 0; i < count; i++) {
            // only count visible cols
            if (isColumnVisible(i)) {
              colCount++;
            }
          }
        }
      }
      
      return colCount;
    }

    @Override
    public int getColumnCount() {
      // count all columns
      return getColumnCount(false);
    }

    /**
     * This method is used to display data on a table cell 
     * 
     * @effects returns the display value of the table cell
     *          <code>(row,col)</code>.
     */
    @Override
    public Object getValueAt(int row, int col) {
      if (tableData != null) {
        List rowData = (List) tableData.get(row);
        return rowData.get(col);
      } else
        return null;
    }

    /**
     * @effects returns the raw (original) value of the table cell
     *          <code>(row,col)</code>.
     */
    protected Object getRawValueAt(int row, int col) {
      return getValueAt(row, col);
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
      if (tableData != null) {
        List rowData = (List) tableData.get(row);
        rowData.set(col, value);
        fireTableCellUpdated(row, col);
      }
    }
    
    /**
     * @effects change the raw (original) value of the table cell
     *          <code>(row,col)</code> to <tt>obj</tt>
     */
    protected void setRawValueAt(Object obj, int row, int col) {
      setValueAt(obj, row, col);
    }
    
    protected void setRowData(int row, Object o) {
      if (tableData != null) {
        tableData.set(row, o);
        fireTableDataChanged();
      }
    }
    
    protected Object getRowData(int row) {
      if (tableData != null) {
        return tableData.get(row);
      } else {
        return null;
      }
    }

    /**
     * @effects 
     *  override supertype's method with an additional feature that 
     *  takes into account the fact that <tt>row</tt> is a new row that
     *  has been created for inputting a new object/record (i.e. <tt>row = {@link #newRowIndex}<tt>)  
     *  
     *  <p>In this case, all mutable and immutable, non-auto cells are editable.  
     *  
     * @version 5.1c
     */
    @Override
    public boolean isCellEditable(int row, int col) {
      // if column's editable is set then returns it,
      // otherwise returns the overall cellEditable value
      Boolean ce = colEditables[col];
      if (ce != null) {
        //v5.1c: return ce.booleanValue();
        
        /* v5.1c: 
         * if ce == false and newRowIndex != null and row = newRowIndex and col is immutable, non-auto
         *   then return true
         * else
         *   return ce
         */
        boolean tf = ce.booleanValue();
        if (!tf && newRowIndex != null && row == newRowIndex && isImmutableNonAutoColumn(col)) {
          return true;
        } else {
          return tf;
        }
        // end v5.1c:
      } else {
        return cellEditable;
      }
    }

    void setCellEditable(boolean b) {
      cellEditable = b;
    }

    void setColumnEditable(int colIndex, boolean b) {
      colEditables[colIndex] = b;
    }

    boolean isColumnEditable(int index) {
      // a column's editability is the same for all of its cell
      return isCellEditable(-1, index);
    }

    /**
     * @effects clears the data
     */
    void clear() {
      if (tableData != null && !tableData.isEmpty()) {
        /**v2.5.4: why is it that we have to create a new array list instead 
         * of clearing the existing tableData? 
         * Answer: tableData initially holds reference to an object buffer, 
         * so if we clear it here, we may delete objects from the buffer 
         * that arent supposed to be removed! 
         * */
//        tableData.clear();
        tableData = new ArrayList();
        fireTableDataChanged();
        updateTableView();
      }
    }

    /**
     * @requires <code>o != null</code>
     */
    int getRow(Object o) {
      int i = 0;
      for (Object e : tableData) {
        if (e.equals(o)) {
          return i;
        }
        i++;
      }
      return -1;
    }
  } // end DataModel

  @Override
  public void setLinkAttribute(DAttr linkAttrib) {
    // do nothing
  }
  
  @Override
  public DAttr getLinkAttribute() {
    return null;
    // throw new
    // NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED,"");
  }

  /**
   * @effects returns an <code>Object</code> object of the parent container of this,
   *          which is used as the linked value for the link column 
   */
  public Object getLinkValue() {
    Object parentObject = controller.getParentObject();
    if (parentObject != null) { // nested
      return parentObject;
    } else {
      return null;
    }
  }

  @Override
  public JComponent getGUIComponent() {
    return getScrollableGUI();
  }

  @Override
  public Region getComponentConfig(JComponent comp) {
    // v3.0: error -> return cfgMap.get(comp);
    Region cfg;
    TableCellEditor tce;
    JDataField df;
    for (Entry<TableCellEditor,Region> entry : cfgMap.entrySet()) {
      cfg = entry.getValue();
      tce = entry.getKey();
      
      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
        continue;
      
      // found an event source
      df = ((JDataField.DataCellEditor) tce).getDataField();
      if (df == comp) {
        return cfg;
      }
    }
    
    return null;
  }
  
  @Override
  public boolean containsComponentForAttribute(String attributeName) {
    Region cfg;
    TableCellEditor tce;
    JDataField df;
    for (Entry<TableCellEditor,Region> entry : cfgMap.entrySet()) {
      cfg = entry.getValue();
      tce = entry.getKey();
      
      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
        continue;
      
      // found an event source
      df = ((JDataField.DataCellEditor) tce).getDataField();
      if (df.getDomainConstraint().name().equals(attributeName)) {
        return true;
      }
    }
    
    return false;
//    TableColumnModel tcm = getColumnModel();
//    int colCount = tcm.getColumnCount();
//    
//    TableColumn tc;
//    TableCellEditor tce;
//    JDataField.DataCellEditor dce;
//    JDataField df;
//    JBindableField f;
//    for (int col = 0; col < colCount; col++) {
//      tc = tcm.getColumn(col);
//      tce = tc.getCellEditor();
//      
//      // column may not have been set with a cell editor that we want 
//      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
//        continue;
//      
//      dce = (JDataField.DataCellEditor) tce;
//      df = dce.getDataField();    
//      if (df.getDomainConstraint().name().equals(attributeName)) {
//        return true;
//      }
//    }
//    
//    return false;
  }

  @Override
  public Collection<DAttr> getDomainAttributes(boolean printable) {
    int count = getColumnCount();
    TableColumn tc;
    TableCellEditor tce;
    Collection<DAttr> attribs = new ArrayList<>();
    DAttr attrib;
    PropertySet printfCfg;
    for (int i = 0; i < count; i++) {
      if (printable && !isColumnVisible(i)) 
        continue; // skip

      tc = getTableColumn(i);
      tce = tc.getCellEditor();
      if (tce != null && tce instanceof JDataField.DataCellEditor) {
        attrib = ((JDataField.DataCellEditor) tce).getDomainConstraints()[0];
        
        printfCfg = null;
        if (printConfig != null) {
          printfCfg = printConfig.getExtension(attrib.name());
        }
        
        if (printfCfg == null || !printable || 
            printfCfg.getPropertyValue("isVisible", Boolean.class, true) == true) {
          attribs.add(attrib);
        }
      }
    }
    
    return (attribs.isEmpty()) ? null : attribs;
  }

  
  
  @Override
  public DAttr getSelectedDomainAttribute() {
    // find the selected column and return the domain attribute of the JDataField of that column
    DAttr selectedAttrib = null;
    
    TableColumn tc;
    TableCellEditor tce;
    
    int i = getSelectedColumn();
    if (i > -1) {
      // a column is selected
      tc = getTableColumn(i);
      tce = tc.getCellEditor();
      if (tce != null && tce instanceof JDataField.DataCellEditor) {
        selectedAttrib = ((JDataField.DataCellEditor) tce).getDomainConstraints()[0];
      }        
    }
    
    return selectedAttrib;
  }

  @Override
  public void addStateListener(DataController dctl, boolean recursive) {
    Region cfg;
    TableCellEditor tce;
    JDataField df;
    for (Entry<TableCellEditor,Region> entry : cfgMap.entrySet()) {
      cfg = entry.getValue();
      tce = entry.getKey();
      
      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
        continue;
      
      if (cfg.getIsStateEventSource()) {
        // found an event source
        df = ((JDataField.DataCellEditor) tce).getDataField();
        df.addChangeListener(dctl);
      }
    }
  }
  
  @Override
  public void preRunConfigure(boolean recursive) throws NotPossibleException {
    // if there are any bounded field components then connect them to source

    /**
     * the data fields are in the column cell editors
     * (see setCellEditor() for details)
     */
    TableColumnModel tcm = getColumnModel();
    int colCount = tcm.getColumnCount();
    
    TableColumn tc;
    TableCellEditor tce;
    JDataField.DataCellEditor dce;
    JDataField df;
    JBindableField f;
    for (int col = 0; col < colCount; col++) {
      tc = tcm.getColumn(col);
      tce = tc.getCellEditor();
      
      // column may not have been set with a cell editor that we want 
      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
        continue;
      
      dce = (JDataField.DataCellEditor) tce;
      df = dce.getDataField();    
      if (df instanceof JBindableField) {
        // ignore if a link column
        if (!isLinkColumn(col)) {
          f = (JBindableField) df;
        //v2.7: if (f.isBounded())
          f.connectDataSource();
        }
      }     
    }
  }
  
//  @Override
//  public void setParentContainer(JDataContainer parent) {
//    this.parent = parent;
//  }

  @Override
  public JDataContainer getParentContainer() {
    return parent;
  }

  @Override
  public void setHasFocus(boolean hasFocus) {
    this.hasFocus = hasFocus;

    if (hasFocus)
      GUIToolkit.highlightContainerOnFocus(getGUIComponent());
    else
      GUIToolkit.highlightContainerInit(getGUIComponent());
  }

  public boolean hasFocus() {
    return hasFocus;
  }

  @Override
  public LinkedHashMap<DAttr,Object> getUserSpecifiedState() throws ConstraintViolationException {
    /**
     * get the cell values of the current row, the corresponding domain
     * attributes of whom are user-specifiable (i.e. non-auto).
     */
    int row = getSelectedRow();
    //Stack vals = null;
    LinkedHashMap<DAttr,Object> vals = null;
    if (row >= 0) {
      int colCount = getColumnCount();
      //vals = new Stack();
      vals = new LinkedHashMap<DAttr,Object>();
      Object v;
      DAttr dc;
      for (int i = 0; i < colCount; i++) {
        // skip auto-gen columns
        dc = this.getColumnConstraint(i); // v3.3
        if (// v3.3: support also the case that field is at the dependent end of an one-one association
            // !isAutoColumn(i)          
            (!isAutoColumn(i) && !DataContainerToolkit.isDataFieldRealisingADependentAttribute(dc, 
                controller.getDomainClass(), controller.getDodm().getDsm()))
            ) {
          v = getRawValueAt(row, i);
          // value may be null (i.e. user does not click on the cell to edit)
          if (!isOptionalColumn(i) && v == null) {
            throw new ConstraintViolationException(
                ConstraintViolationException.Code.INVALID_VALUE,
                "Bạn cần nhập dữ liệu cho ô {0}", getColumnName(i));
          }
          
          // v3.3: moved up 
          // dc = this.getColumnConstraint(i);
          
          //vals.push(v);
          vals.put(dc, v);
        }
      }
    } else {
      controller.getCreator().displayMessageFromCode(MessageCode.ROW_SELECTION_REQUIRED, controller);
    }

    return vals; //(vals != null) ? vals.toArray() : null;
  }

  @Override
  public void setMutableState(DAttr attrib, Object val) {
    int row = getSelectedRow();

    if (row >= 0) { // make sure that a row is being selected 
      int colCount = getColumnCount();
      for (int i = 0; i < colCount; i++) {
        if (getColumnConstraint(i).equals(attrib)) {
          // found the column
          // assumes column is mutable
          //if (isMutableColumn(i)) { // make sure that column is mutable
          setRawValueAt(val,row, i);
          //}
          break;
        }
      }
    }
  }
  
  @Override
  public void setMutableState(Object[] vals) {
    /**
     * set the cell values of the current row, the corresponding domain
     * attributes of whom are mutable.
     */
    int row = getSelectedRow();
    int fieldIndex = 0;
    
    if (row >= 0) {
      int colCount = getColumnCount();
      for (int i = 0; i < colCount; i++) {
        // only processes mutable columns
        if (isMutableColumn(i)) {
          setRawValueAt(vals[fieldIndex],row, i);
          fieldIndex++;
        }
      }
    } else {
      controller.getCreator().displayMessageFromCode(
          MessageCode.ROW_SELECTION_REQUIRED, controller);
    }
  }

  @Override
  public void setUserSpecifiedState(Object[] vals) {
    /**
     * set the cell values of the current row, the corresponding domain
     * attributes of whom are non-auto.
     */
    int row = getSelectedRow();
    int fieldIndex = 0;
    
    if (row >= 0) {
      int colCount = getColumnCount();
      for (int i = 0; i < colCount; i++) {
        // only processes non-auto columns
        if (!isAutoColumn(i)) {
          setRawValueAt(vals[fieldIndex],row, i);
          fieldIndex++;
        }
      }
    } else {
      controller.getCreator().displayMessageFromCode(
          MessageCode.ROW_SELECTION_REQUIRED, controller);
    }
  }
  
  /* (non-Javadoc)
   * @see domainapp.basics.view.JDataContainer#setDataFieldValue(domainapp.basics.model.meta.DAttr, java.lang.Object)
   */
  @Override
  public Object setDataFieldValue(DAttr attrib, Object value) {
    int row = getSelectedRow();
    
    Object oldVal = null;
    
    if (row >= 0) { // make sure that a row is being selected 
      int colCount = getColumnCount();
      for (int i = 0; i < colCount; i++) {
        if (getColumnConstraint(i).equals(attrib)) {
          // found the column
          oldVal = getRawValueAt(row, i);
          setRawValueAt(value,row, i);
          break;
        }
      }
    }
    
    return oldVal;
  }

  /**
   * @effects returns an array of values of all the <b>mutable</b> data fields
   *          of the row corresponding to the currently selected object
   */
  @Override
  public LinkedHashMap<DAttr,Object> getMutableState() throws ConstraintViolationException {
    // uncomment the code below if the object editing behaviour of table is changed
    // for now, just return null because table is directly edited on table
    return null;
    
    //TODO: can we improve this by keeping track of which data columns were changed?
//    /** returns a full map since table updates object data automatically */
//    Map<DomainConstraint,Object> vals = new LinkedHashMap();
//    
//    DomainConstraint dc;
//    /**
//     * get the cell values of the current row, the corresponding domain
//     * attributes of whom are mutable
//     */
//    int row = getSelectedRow();
//    if (row >= 0) {
//      int colCount = getColumnCount();
//      Object v;
//
//      for (int i = 0; i < colCount; i++) {
//        // only consider mutable columns
//        if (isMutableColumn(i)) {
//          v = getRawValueAt(row, i);
//          dc = getColumnConstraint(i);
//          // value may be null (i.e. user does not click on the cell to edit)
//          vals.put(dc,v);
//        }
//      }
//    } else {
//      controller.getCreator().displayMessage("Bạn cần chọn ít nhất một dòng");
//    }
//
//    //return null;
//    return (vals.isEmpty() ? null : vals);
  }

  @Override
  public Object[] getSearchState() {
    /**
     * the search tool bar is shared by all the panels in the same AppGUI, thus,
     * if this panel is nested then get the parent's search state and so on else
     * get its own
     */
    if (parent != null)
      return parent.getSearchState();
    else
      return controller.getCreator().getGUI().getSearchToolBarTextState();
  }

//  @Override
//  public List getDataModel() {
//    return controller.getCurrentObjects();
//  }

  @Override
  public void updateDataComponent(String attribName) throws NotFoundException {
    // for sub-types to implement
  }
  
  //TODO: fix this method to highlight the row matching the input object
  @Override
  public void update(Object o) {
    // refresh the table to make sure that the object is there
    // refresh();

    /**
     * Highlight the row that corresponds to the current object.
     **/
    DODMBasic schema = controller.getCreator().getDodm();
    DSMBasic dsm = schema.getDsm();
    
    int row = getSelectedRow();
    if (row >= 0) {
      List vals = dsm.getAttributeValues(o);
      // compare two lists
      //TODO: this seems incorrect!
      setRowData(row, vals);
    }
  }

  @Override
  public void updateGUI() {
    //TODO: implement this when needed
    //throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
    //    "Chưa triển khai phương thức {0}.{1}", this.getClass(), "validateGUI");
  }
  
  @Override
  public void clear() {
    reset();
  }

  @Override
  public void updateDataPermissions() {
    /**
     * the data fields are in the column cell editors
     * (see setCellEditor() for details)
     */
    TableColumnModel tcm = getColumnModel();
    int colCount = tcm.getColumnCount();
    
    TableColumn tc;
    JDataField df;
    TableCellEditor tce;
    Region cfg;
    WrappableCellRenderer tcr;
    DAttr dc;
    JDataField.DataCellEditor dce;
    boolean currentState, state;
    String attributeName;
    
    ControllerBasic ctl = controller.getCreator();
    /*v2.6.4b: fixed this - 
     * if this is a top-level container then use the userGUI's editability;
     * otherwise use the editability of the userGUI's component config of this container
    AppGUI userGUI = controller.getUser().getGUI();
    boolean userGUIEditable = userGUI.isEditable();
    */
    boolean containerEditable = isEditable();

    for (int i = 0; i < colCount; i++) {
      tc = tcm.getColumn(i);
      tce = tc.getCellEditor();
      
      // column may not have been set with a cell editor that we want 
      if (tce == null || !(tce instanceof JDataField.DataCellEditor))
        continue;
      
      cfg = cfgMap.get(tce);
      dce = (JDataField.DataCellEditor) tce;
      df = dce.getDataField();
      
      dc = df.getDomainConstraint();
      currentState = df.getEditable(); //v5.1c: df.isEditable();
      
      // check the security permission
      attributeName = df.getDomainConstraint().name();
      state = ctl.getAttributeEditableState(attributeName);
      // update editability if current state and state donot agree
      // but need to also take into consideration the mutability of the field:
      // - there is no need to change the immutable attribute ever regardless
      //    of what the permission is
      if ((currentState && !state) || (!currentState && state)) {
        if (dc.mutable() && cfg.getEditable() && containerEditable) {
          df.setEditable(state);
          
          // update display colour of the cell renderer using the colour of the data field
          tcr = (WrappableCellRenderer) tc.getCellRenderer(); 
              //getDefaultRenderer(Object.class);
          if (tcr != null) {
            Color fg = df.getForegroundColor();
            // TODO: assume that the default renderer is the actual 
            // component that renders the cell value
            // Component tcrComp =  tcr.getTableCellRendererComponent(this, null, false, false, 0, i);
            if (!tcr.getForeground().equals(fg))
              tcr.setForeground(fg);
          }
        }
      }
    }
  }
  
  @Override
  public boolean isEditable() {
    if (parent != null) {
      // this is a child container
      return //parent.isEditable() && 
          //parent.getComponentConfig(this.getGUIComponent()).getEditable();
          containerCfg.getEditable();
    } else {
      // top-level container
      return controller.getUser().getGUI().isEditable();
    }
  }
  
  /**
   * @version 
   * - 3.2: support scope def editable
   */
  @Override
  public void setEditable(final JDataContainer sourceContainer, final boolean tf, final boolean recursive) {
    /* v3.2: support scope definition of this container
    TableColumnModel tcm = getColumnModel();
    int colCount = tcm.getColumnCount();
    
    for (int i = 0; i < colCount; i++) {
      setBoundedDataFieldEditable(i, tf);
    }
    */
    
    Boolean scopeDefEditable = getController().getUserGUI().getEditableByScope(this);
    if (this == sourceContainer || scopeDefEditable == null) {
      // only do setting if this is the source container (i.e. this is called when the container is created)
      // OR if scopeDefEditable is not defined; because if it is defined then 
      // its editability takes precedence and had already been set when this container was created
      TableColumnModel tcm = getColumnModel();
      int colCount = tcm.getColumnCount();
      
      for (int i = 0; i < colCount; i++) {
        setBoundedDataFieldEditable(i, tf);
      }
    }
  }
  
  /**
   * @requires
   *  i is a valid column index
   * @effects
   *  if the data field bound to column ith is mutable
   *    and the editable state of this field is not <tt>tf</tt>
   *      set editable of this field to df
   *  update the cell renderer of each cell of the column to reflect the value of <tt>tf</tt>
   *    
   */
  private void setBoundedDataFieldEditable(int i, boolean tf) {
    TableColumn tc;
    JDataField df;
    TableCellEditor tce;
    DAttr dc;
    WrappableCellRenderer tcr;
    JDataField.DataCellEditor dce;
    boolean currentState;
    
    TableColumnModel tcm = getColumnModel();
    tc = tcm.getColumn(i);
    tce = tc.getCellEditor();

    // column may not have been set with a cell editor that we want
    if (tce == null || !(tce instanceof JDataField.DataCellEditor))
      return;

    dce = (JDataField.DataCellEditor) tce;
    df = dce.getDataField();
    dc = df.getDomainConstraint();
    currentState = df.getEditable(); //v5.1c: df.isEditable();

    // update editability if current state and state donot agree
    // but also taking into consideration if the field is mutable
    boolean mutable = dc.mutable();
    if (currentState != tf) {
      if (mutable) {
        df.setEditable(tf);
      }
    }
    
    // update display colour of the cell renderer using the colour of the
    // data field
    setColumnForeground(i, df.getForegroundColor());
  }
  
  @Override
  public String getLabel() {
    if (parent == null) {
      return controller.getCreator().getGUI().getTitle();
    } else {
      DefaultPanel parentPanel = (DefaultPanel) parent;
      JLabel label = parentPanel.getLabelFor(this.getGUIComponent());
      
      String txt = controller.getLabelText(label);
      if (txt.equals("")) {
        return null;
      } else {
        return txt;
      }
    }
  }

//  // /////// Helper methods
//  /**
//   * @effects draws an empty border around a panel.
//   * 
//   *          <p>
//   *          Note: the border's width needs to be the same as the width of the
//   *          coloured border that is drawn around the same panel when it is
//   *          being highlighted (see method {@see MouseAdapter.highlightOnFocus}
//   *          ).
//   */
//  private void highlightInit() {
//    // System.out.println("init panel " + p.getName());
//    this.getGUIComponent().setBorder(EMPTY_BORDER);
//  }
//
//  private void highlightOnFocus() {
//    this.getGUIComponent().setBorder(PANEL_BORDER);
//  }

  @Override
  public void handleDataFieldEditing(KeyEvent e, JDataField df) {
    // empty
  }

  @Override
  public void handleDataFieldValueChanged(JDataField df) {
    // use a shared handler
    DataContainerToolkit.handleDataFieldValueChanged(this, df);
  }

  /**
   * @efffects 
   *  if data model is null or contains no row
   *    return true
   *  else
   *    return false
   */
  public boolean isEmpty() {
    return dataModel == null || dataModel.getRowCount() == 0;
  }

  @Override
  public Iterator<JDataContainer> getChildContainerIterator() {
    // TODO: change this if JDataTable can be nested
    return null;
  }

  
  @Override
  public int getChildContainerCount() {
    // TODO: change this if JDataTable can be nested
    return 0;
  }
  
  
  @Override
  public boolean isSearchEnabled() {
    return false;
  }

  @Override
  public void forceEditable() {
    // empty
  }

  @Override
  public void compact(boolean tf) {
    // TODO: implement this if this can be nested with sub-containers
  }

  @Override
  public Component[] getComponents(Collection cons) {
    //TODO: see #getComps
    //return null;
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED,
        new Object[]{this.getClass().getSimpleName(), "getComponents"});
  }

  @Override
  public Map<DAttr, Component> getComps(Collection dattrs) {
    
    int count = getColumnCount();
    TableColumn tc;
    TableCellEditor tce;
    DAttr attrib;
    
    Map<DAttr, Component> comps = new LinkedHashMap<>();

    for (int i = 0; i < count; i++) {
      tc = getTableColumn(i);
      tce = tc.getCellEditor();
      if (tce != null && tce instanceof JDataField.DataCellEditor) {
        JDataField.DataCellEditor compEditor = (JDataField.DataCellEditor) tce;
        attrib = compEditor.getDomainConstraints()[0];
        
        if (dattrs == null || dattrs.contains(attrib)) {
          comps.put(attrib, compEditor.getDataField());
        }
      }
    }
    
    if (comps.isEmpty())
      return null;
    else
      return comps;
  }
  
  @Override
  public JComponent getComponent(DAttr attrib) {
    TableColumn tc = getColumn(attrib.name());
    
    if (tc == null)
      return null;
    
    TableCellEditor tce = tc.getCellEditor();
    
    // column may not have been set with a cell editor that we want 
    if (tce == null || !(tce instanceof JDataField.DataCellEditor))
      return null;
    
    return ((JDataField.DataCellEditor) tce).getDataField();
  }
  
  @Override
  public JLabel getLabelFor(JComponent comp) {
    //TODO
    return null;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /**
   * @effects 
   * 
   * @version 5.1
   */
  @Override
  public boolean equals(Object obj) {
    return DataContainerToolkit.equals(this, obj);
  }
  
  @Override
  public boolean equalsStrictly(JDataContainer obj) {
    return obj != null && this == obj;
  }

  /**
   * @overview 
   *  A <tt>TableCellRenderer</tt> that supports text wrapping
   * @author dmle
   */
  private static class TextAreaCellRenderer 
    extends JTextArea 
    implements TableCellRenderer {
    
    private static final Border LINE_BORDER = 
        BorderFactory.createLineBorder(Color.BLUE,2);
    private static final Border EMPTY_BORDER = 
        BorderFactory.createEmptyBorder(2,2,2,2);
    
    // record the row heights
//    Map<Integer,MaxHeight> heights;
    
    // user a panel with border layout to 
    // expand the text area automatically 
    private JPanel panel;
    
    private JDataTable owner;
    
    public TextAreaCellRenderer(JDataTable owner) {
      //super(2,5);
      super();
      setLineWrap(true);
      //v2.7.2
      setWrapStyleWord(true);
      
      panel = new JPanel(new BorderLayout());
      panel.add(this);

      this.owner = owner;
      
      setBorder(EMPTY_BORDER);

//      heights = new HashMap();
    }
    
    public JDataTable getTable() {
      return owner;
    }
    
    public void highlight(boolean tf) {
      if (tf) {
        setBorder(LINE_BORDER);
      } else {
        setBorder(EMPTY_BORDER);
      }
    }
    
//    /**
//     * @overview
//     *  A helper class to record the current maximum height of a given table row. 
//     *  This height is determined based on the preferred size of the 
//     *  cell render when it is set to hold the value of a given column of the row. 
//     *  It is used to set the row height. 
//     */
//    private class MaxHeight {
//      int rowIndex;
//      int columnIndex;
//      int height;
//      
//      MaxHeight(int row, int column, int height) {
//        columnIndex=column;
//        rowIndex = row;
//        this.height=height;
//      }
//    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      
      /**v2.5.4: display the value in its original form (e.g. formatted) */
//      if (value != null) 
//        setText(value.toString());
//      else 
//        setText(null);
      if (value != null) {
        TableCellEditor tce = owner.getCellEditor(row, column);
        if (tce == null || !(tce instanceof JDataField.DataCellEditor)) {
          // not the editor we know -> use default format
          setText(value.toString());
        } else {
          JDataField.DataCellEditor dce = (JDataField.DataCellEditor) tce;
          JDataField df = dce.getDataField();
          if (df.isSupportValueFormatting()) {
            setText(df.getFormattedValue(value));
          } else {
            setText(value.toString());
          }
        }
      } else {
        setText(null);
      }
      // if selected then highlight 
      if (isSelected) {
        setBorder(LINE_BORDER);
      } else {
        setBorder(EMPTY_BORDER);
      }
      
      // uncomment this if need to adjust row height
      // Note: this is not needed in this class since row-height is set up at initialisation time by 
      // another method
//      int myHeight = getPreferredSize().height;
//      setSize(table.getColumnModel().getColumn(column).getWidth(),myHeight);     
//      
//      // adjust table height if necessary
//      MaxHeight rowHeight = heights.get(row);
//      
//      if (rowHeight == null) {
//        rowHeight = new MaxHeight(row,column,myHeight);
//        heights.put(row, rowHeight);
//        table.setRowHeight(row, myHeight); 
//      } else if (rowHeight.height < myHeight) {
//        // new height
//        rowHeight.columnIndex=column;
//        rowHeight.height = myHeight;
//        table.setRowHeight(row, myHeight);
//      } else if (rowHeight.columnIndex == column && 
//          rowHeight.height > myHeight) {
//        // height reduced
//        // determine new max height
//        rowHeight.height=myHeight;
//        // EXPERIMENTAL:
//        // adjustRowHeight(table, rowHeight);
//        table.setRowHeight(row, rowHeight.height);
//      }
      
      return panel;
    }
    
//    /**
//     * EXPERIMENTAL: DOES NOT YET WORK CORRECTLY
//     * @modifies height
//     */
//    private void adjustRowHeight(JTable table, MaxHeight height) {
//      // find the new max height from all the columns of the current row
//      final TableModel model = table.getModel();
//      //final TableColumnModel tableColumnModel = table.getColumnModel();
////      TableColumn column = null;
//      Component comp = null;
//      //TableCellEditor cedit;
//      int columnCount = table.getColumnCount();
//
//      final int row = height.rowIndex;
//      int rowHeight;
//      Dimension dim;
//
//      // set header width
//      for (int i = 0; i < columnCount && i != height.columnIndex; i++) {
//        // get the column
////        column = tableColumnModel.getColumn(i);
//        
////          // TODO: find the longest value of this column
//        Object cellValue = table.getValueAt(row, i);
//        // convert to string
//        String cellValueStr = (cellValue!=null) ? cellValue.toString() : "";
//        
////        comp = table.getDefaultRenderer(model.getColumnClass(i))
////            .getTableCellRendererComponent(table, longValue, false, false, row, i);
//        dim = getPreferredSize(cellValueStr);
//        
//        rowHeight = dim.height;
//
//        if (height.height < rowHeight) {
//          // new max height
//          height.height = rowHeight;
//          height.columnIndex = i;
//          System.out.printf("adjusted height to %d @col%d%n", rowHeight, i);
//        }
//      }
//    }
    
    /**
     * EXPERIMENTAL: DOES NOT YET WORK CORRECTLY
     */
//    private Dimension getPreferredSize(String value) {
//      String old = getText();
//      
//      setText(value);
//      
//      // the updated size
//      Dimension prefSize = getPreferredSize();
//      // make a copy
//      Dimension dim = new Dimension(prefSize.width, prefSize.height);
//
//      // reset text
//      setText(old);
//      
//      return dim;
//    }
  } // END TextAreaCellRenderer
}
