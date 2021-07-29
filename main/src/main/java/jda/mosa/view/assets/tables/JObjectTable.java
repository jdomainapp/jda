package jda.mosa.view.assets.tables;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.List;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.view.assets.DataContainerToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.tables.model.TableDataRow;

/**
 * @overview
 *  A {@link JDataTable} that displays data <b>objects</tt> in a tabular form. Each row of the form 
 *  displays data of an object.
 *  
 * @author dmle
 */
public class JObjectTable extends JDataTable {
  /** 
   * v2.7.2: 
   * 
   * if this is nested then this specifies the link attribute to the parent. 
   * This is related to {@link #upLinkColumn} as follows: 
   * if {@link #upLinkColumn} is specified then its domain constraint is 
   * the same as <tt>linkAttribute</tt>. 
   * 
   * Note that <tt>linkAttribute</tt> may be specified even if {@link #upLinkColumn} is not.
   */
  private DAttr linkAttribute;
  
  /** the column whose values refer to the domain objects of the parent JDataContainer 
   * of this table.
   * <p>This attribute is only used when this table is nested inside another (typically a {@see DefaultPanel})*/
  private int upLinkColumn = -1;
  
  /**
   * The buffer containing the column values of the currently editing row. Only one row 
   * can be in editing at a time. The first buffer element is the row number, while the 
   * second buffer element is an array of the column values. 
   */
  // v2.6.1
  private Tuple2<Integer,Object[]> editingRowBuffer;

  // /// Constructor methods
  /**
   * @effects initialises a new <code>JObjectTable</code> with headers defined
   *          in <code>header</code>
   */
  public JObjectTable(ControllerBasic.DataController controller, List header) {
    super(controller, header);
  }

  /**
   * @effect
   *  initialise this as a nested object table whose parent container is parent
   */
  public JObjectTable(Region containerCfg, ControllerBasic.DataController controller, List header, 
      JDataContainer parent) {
    super(containerCfg, controller, header, parent);
  }
  
  protected DataModel createTableModel(List header) {
    return new ObjectDataModel(header);
  }

  @Override
  public void setLinkColumn(int index) {
    upLinkColumn = index;
  }

  @Override
  public boolean isLinkColumn(int col) {
    return (upLinkColumn == col);
  }

  @Override
  public void setLinkAttribute(DAttr linkAttrib) {
    linkAttribute = linkAttrib;
  }
  
  @Override
  public DAttr getLinkAttribute() {
    /* v2.7.2: use link attribute 
    if (upLinkColumn < 0) {
      // v2.6.4.a: return null instead of throwing error here
      //throw new InternalError("JObjectTable("+getName()+"): no link component");
      return null;
    }
    
    // get the link column and return the constraint name
    return getColumnConstraint(upLinkColumn);
    */
    
    return linkAttribute;
  }

  // v3.0: moved to super-type
//  /**
//   * @effects returns an <code>Object</code> object of the parent container of this,
//   *          which is used as the linked value for the link column 
//   * @see #upLinkColumn 
//   */
//  public Object getLinkValue() {
//    Object parentObject = controller.getParentObject();
//    if (parentObject != null) { // nested
//      return parentObject;
//    } else {
//      return null;
//    }
//  }
  
  /**
   * @effects 
   *  if editRowBuffer != null and editRowBuffer.row != row
   *    prompt user to confirm 
   *  if confirmed or editRowBuffer = null
   *    if editRowBuffer != null /\ editRowBuffer.row != row
   *      reset data at the editRowBuffer.row 
   *      init editRowBuffer to the current row data
   */
  @Override
  public void handleDataFieldEditing(KeyEvent e, JDataField df) {
    int row = getSelectedRow();
    
    // v3.1: added this check to avoid accidental change to table structure causing
    // this method to break
    if (row < 0)
      return;
    
    // initialise editing buffer if needed
    int editingRow = (editingRowBuffer != null ) ? editingRowBuffer.getFirst() : -1;
    boolean editing = editingRow > -1 && editingRow != row;
    
    if (editing || editingRow < 0) {
      initEditingRowBuffer(row);
    }
  }

  @Override
  public boolean editCellAt(int row, int column, EventObject obj) {
     //only allow editing for double-click mouse event
    if (getEditMode().equals(EditMode.ON_DOUBLE_CLICK) && obj instanceof MouseEvent) {
      int ccount = ((MouseEvent) obj).getClickCount();
      if (ccount == 1)
        return false;
    }

    // ensure that user is not currently editing some other row and accidentally clicked on 
    // row
    boolean proceed = checkEditing(row);
    
    if (proceed) {
      // proceed to editing
      return super.doEditCellAt(row, column, obj);
    } else {
      return false;
    }
  }
  
  private void initEditingRowBuffer(int row) {
    int colCount = getColumnCount();
    Object[] vals = new Object[colCount];
    for (int i = 0; i <colCount; i++) {
      vals[i] = getRawValueAt(row, i);
    }
    editingRowBuffer = new Tuple2<Integer,Object[]>(row,vals);  
  }
  
  /**
   * @effects 
   *  if user is not currently editing or currently editing some other row and wishes to abundan the changes
   *    return true
   *  else
   *    return false
   *    
   *  <p>The row with unsaved changes will be reset.
   */
  private boolean checkEditing(Integer newRow) {
    int editingRow = (editingRowBuffer != null ) ? editingRowBuffer.getFirst() : -1;
    boolean editing = editingRow > -1 && 
        (newRow == null || (newRow != null && editingRow != newRow));

    boolean proceed = true;
    if (editing) {
      // confirm 
      proceed = displayConfirm(TableMessageCode.CONFIRM_SWITCH_ROW_WHILE_EDITING);
    }
    
    if (proceed) {
      if (editing) {
        // reset value at the current row
        resetCurrentRow();
      } 
    }
    
    return proceed;
  }

  /**
   * @requires 
   *  editingRowBuffer != null
   * @effects 
   *  reset the column values of the currently editing row to its previous state
   * @version 2.6.1
   */
  private void resetCurrentRow() {
    /**
     *  if current row is a new row currently being added
     *    then remove it
     *  else
     *    reset row data to its previous state.
     *    A simple solution is to refresh the table data. This is not ideal 
     *    but is needed by JTable.
     *    
     *  Note, must do the above via the data controller of this
     */
    int row = editingRowBuffer.getFirst();
    
    if (isNewRow(row)) {
      // new row
      getController().cancel(true);
      // must set row buffer to null here
      editingRowBuffer = null;
    } else {
      // existing row
      getController().reset(true);
      //refreshData();
    }
  }
  
  /**
   * @effects
   *  return the object at row <tt>row</tt> 
   */
  public Object getRowData(int row) {
    return dataModel.getRowData(row);
  }

  @Override
  public int getNewRow() {
    if (editingRowBuffer != null) {
      int row = editingRowBuffer.getFirst();
      return isNewRow(row) ? row : -1;
    } else {
      return -1;
    }
  }
  
  /**
   * @requires  
   *  dataModel != null /\ row is a valid data row
   * @effects
   *  if the row at index <tt>row</tt> is a new row (i.e. its row data is a List rather 
   *  than a domain object)
   *    return true
   *  else
   *    return false  
   */
  private boolean isNewRow(int row) {
    return (getRowData(row) instanceof List);
  }
  
  @Override
  public boolean isEditing(int row) {
    // use editing row buffer
    if (editingRowBuffer != null && editingRowBuffer.getFirst() == row) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * @requires 
   *  this is being edited
   * @effects 
   *  empty the editing buffer that is associated to the current editing operation 
   *  of this; return <tt>true</tt> if editing buffer was modified, <tt>false</tt> if otherwise
   */
  public boolean clearEditing() {
    // to be sure
    boolean bufferModified = false;
    if (editingRowBuffer != null) {
      Object[] rowVals = editingRowBuffer.getSecond();
      if (rowVals != null) {
        // clear all except the unlinked columns
        for (int i = 0; i < rowVals.length; i++) {
          if (!isLinkColumn(i) && rowVals[i] != null) {
            rowVals[i] = null;
            if (!bufferModified)
              bufferModified = true;
          }
        }
      }
    }
    
    return bufferModified;
  }
  
  @Override
  public void deleteRow(int row, boolean askUser) {
    super.deleteRow(row, askUser);
    
    // clear editing buffer if it is set to the deleted row
    if (editingRowBuffer != null && editingRowBuffer.getFirst() == row) {
      editingRowBuffer = null;
    }
  }
  
  /**
   * This is a special version of {@link #addObject(Object)} which differs in that it 
   * does not update the table view immediately. It is performed at the third 
   * step (create) of the three-step object creation procedure: new-edit-create
   *    
   * @requires 
   *  o != null
   * @effects   
   *  add the specified object to the data model of this table
   */
  public void addObjectDelayed(Object o) {
    dataModel.tableData.add(o);
  }

  /**
   * @requires 
   *  o != null
   * @effects   
   *  add the specified object to the data model of this table
   *  and update the table's view
   */
  public void addObject(Object o) {
    ((ObjectDataModel)dataModel).addObject(o);
  }

  /**
   * @requires 
   *  o != null
   * @effects   
   *  set the specified object at the specified row in the data model of this
   */
  public void insertObjectAt(Object o, int row) {
    ((ObjectDataModel)dataModel).insertObjectAt(o, row);
  }

  /**
   * @requires 
   *  o != null
   * @effects   
   *  remove the specified object from the data model of this table
   */
  public void deleteObject(Object o) {
    dataModel.tableData.remove(o);
    refresh();
  }

  /**
   * @effects 
   *  if o is not null AND contained in the data model of this
   *    return true
   *  else
   *    return false
   */
  public boolean containsObject(Object o) {
    return (o != null && dataModel.tableData.contains(o));
  }
  
  @Override
  public void setModel(final Collection tableData) {
    // clear the editing buffer first 
    if (editingRowBuffer != null) {
      editingRowBuffer = null;
    }
    
    super.setModel(tableData);
  }

  @Override /**{@link JDataTable#configureForDomainCustomisation(WrappableCellRenderer, int, int, Object)}*/
  protected void configureForDomainCustomisation(WrappableCellRenderer cellRenderer, int row, int column, Object cellValue) {
    Object rowObj = getRowData(row);
    if (rowObj != null && rowObj instanceof TableDataRow) {
      TableDataRow dataRowObj = (TableDataRow) rowObj;
      
      Color bg = dataRowObj.getBgColor();
      Color fg = dataRowObj.getFgColor();
      
      if (dataRowObj.isAggregated()) {
        // aggregated object
        
        if (bg != null) {
          cellRenderer.setBackgroundColour(bg);
        }
// not yet works!        
//        if (fg != null) {
//          cellRenderer.setForegroundColour(fg);
//        }
      } else {
        // reset colours to default (if needed)
        if (bg == null && !cellRenderer.isDefaultBackground()) {
          // rest
          cellRenderer.setBackgroundColourToDefault();
        }

// not yet works!
//        if (fg == null && !cellRenderer.isDefaultForeground()) {
//          // rest
//          cellRenderer.setForegroundColourToDefault();
//        }
      }
    }
  }

  // /// nested class ObjectDataModel
  protected class ObjectDataModel extends JDataTable.DataModel {
    public ObjectDataModel(List header) {
      super(header);
    }

//    void setModel(final List objects) {
//      this.tableData = objects;
//      fireTableDataChanged();
//    }

    /**
     * @effects initialise a new row with suitable default values.
     *          <p>if this is a nested panel then set the value of the {@see #JObjectTable.uplinkColumn} to 
     *          the parent.
     */
    @Override    
    protected Object initRow() {
      List r = new ArrayList();
      for (int i = 0; i < header.size(); i++) {
        if (i == upLinkColumn) {
          // link column
          r.add(getLinkValue());
        } else { // null for the rest
          r.add(null);
        }
      }
      return r;
    }
    
    
    /**
     *  add a new empty row, ready for entering details of a new object. 
     *  This is performed at the first step (new) of the 3-step procedure: new-edit-create
     */
    @Override
    protected int addRow() {
      // add a new row and use it to initialise the editing buffer
      List newRowData = (List)initRow();
      tableData.add(newRowData);
      int last=tableData.size()-1;
      editingRowBuffer = new Tuple2<Integer,Object[]>(last,newRowData.toArray());  
    
      fireTableRowsInserted(last, last);
      updateTableView();
      return last;
    }
    
    // insert an object directly to the model
    void insertObjectAt(Object o, int row) {
      tableData.add(row, o);
      fireTableRowsInserted(row, row);
      updateTableView();
    }
    
    // add an object directly to the model 
    void addObject(Object o) {
      tableData.add(o);
      int last = tableData.size()-1;          
      fireTableRowsInserted(last, last);
      updateTableView();
    }
    
    /**
     * This method is used to display data on a table cell
     * 
     * @effects returns the display value of the table cell <code>(row,col)</code>.
     * 
     *  @version 2.6.1 support editing row buffer
     */
    public Object getValueAt(int row, int col) {
      // if same row as editing buffer then retrieve value from buffer
      // otherwise get value from table data
      
      // the column constraint
      DAttr dc = getColumnConstraint(col);

      DODMBasic schema = JObjectTable.this.controller.getCreator()
          .getDodm();
      DSMBasic dsm = schema.getDsm();

      if (editingRowBuffer != null && editingRowBuffer.getFirst() == row) {
        Object cobj = editingRowBuffer.getSecond()[col];

        if (dc.type().isDomainType() && cobj != null) {
          // get the value of the bounded attribute
          DAttr bounded = getColumnBoundedConstraint(col);
          String boundedAttribute = bounded.name();
          return dsm.getAttributeValue(cobj, boundedAttribute);
        } else {
          return cobj;
        }
      } else if (tableData != null) {
        Object o = tableData.get(row);

        // for existing object
        // get the name of the attribute col(th)
        String attributeName = colToAttribute(col);

        // if the column type is domain, then return the value of the bounded
        // attribute
        if (dc.type().isDomainType()) {
          DAttr bounded = getColumnBoundedConstraint(col);
          
          // v3.2: added this check
          if (bounded == null) {
            // internal error
            throw new IllegalStateException(String.format("%s.getValueAt(object=%s; attrib=\"%s\"): bound attribute is required but not specified", JObjectTable.this, o, attributeName));
          }
          
          String boundedAttribute = bounded.name();
          return dsm.getBoundedAttributeValue(o, attributeName,
              boundedAttribute);
        } else {
          return dsm.getAttributeValue(o, attributeName);
        }
      } else {
        return null;
      }
    }

//    // TESTING: do not yet work correctly! 
//    private Object getCurrentObjectValueAt(int row, int col, DomainConstraint dc) {
//      // use the value of the cell editor at row,col
//      Object val = getCellEditor(row, col).getCellEditorValue();
//      
//      String attributeName = colToAttribute(col);
//  
//      // if the column type is domain, then return the value of the bounded attribute
//      if (dc.type().isDomainType()) {
//        DomainSchema schema = JObjectTable.this.controller.getCreator()
//        .getDomainSchema();
//        DomainConstraint bounded = getColumnBoundedConstraint(col);
//        String boundedAttribute = bounded.name();
//        return schema.getBoundedAttributeValue(val, attributeName, boundedAttribute);
//      } else {
//        return val;
//      }  
//    }
    
    /**
     * @effects returns the raw (original) value of the table cell <code>(row,col)</code>.
     * 
     *  @version 2.6.1: support editing row buffer
     */    
    public Object getRawValueAt(int row, int col) {
      // if same row as editing buffer then retrieve value from buffer
      // otherwise get value from table data
      
      // the column constraint
      DODMBasic schema = JObjectTable.this.controller.getCreator()
          .getDodm();
      DSMBasic dsm = schema.getDsm();

      if (editingRowBuffer != null && editingRowBuffer.getFirst() == row) {
        Object cobj = editingRowBuffer.getSecond()[col];
        return cobj;
      } else if (tableData != null) {
        Object o = tableData.get(row);

        // get the name of the attribute col(th)
        String attributeName = colToAttribute(col);

        return dsm.getAttributeValue(o, attributeName);
      } else {
        return null;
      }
    }
    
    /**
     * @requires 
     *  editingRowBuffer != null /\ editingRowBuffer.row = row
     * @effects 
     *    if editingRowBuffer = null || editingRowBuffer.row != row
     *      throws NotPossibleException
     *    else 
     *      set editingRowBuffer.vals[col] = value
     *      update table to show value
     * @version 2.6.1 
     */
    @Override
    public void setValueAt(Object value, int row, int col) throws NotPossibleException {
      
      if (editingRowBuffer != null && editingRowBuffer.getFirst() == row) {
        // set value to editing buffer
        Object[] rowData = editingRowBuffer.getSecond();
        rowData[col] = value;
        
        // update table to show the value 
        fireTableCellUpdated(row, col);
      } 
    }    
  } // end ObjectDataModel
  
  /**
   * @requires 
   *  editingRowBuffer != null
   * @effects 
   *  return a map of all values of non-auto columns in the editing row buffer
   */
  @Override
  public LinkedHashMap<DAttr,Object> getUserSpecifiedState() throws ConstraintViolationException {
    if (editingRowBuffer == null) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_EDITING_ROW_BUFFER,
          "Bộ đệm dữ liệu cho dòng {0} chưa được tạo hoặc không khớp", "");      
//      controller.getCreator().displayMessage(MessageCode.ROW_SELECTION_REQUIRED, 
//          "Bạn cần chọn ít nhất một dòng");
    }
    
    /**
     * get the values of the columns in the editing buffer, the corresponding domain
     * attributes of whom are user-specifiable (i.e. non-auto).
     */
    LinkedHashMap<DAttr,Object> vals = new LinkedHashMap<DAttr,Object>();;
    int colCount = getColumnCount();
    Object v;
    DAttr dc;
    int row = editingRowBuffer.getFirst();
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
        vals.put(dc, v);
      }
    }
    
    //clearEditingBuffer();
    //editingRowBuffer = null;
    
    return vals; 
  }
  
  /**
   * @requires 
   *  editingRowBuffer != null
   * @effects 
   *  return a map of values of mutable columns in the editing row buffer
   */
  @Override
  public LinkedHashMap<DAttr,Object> getMutableState() throws ConstraintViolationException {    
    if (editingRowBuffer == null) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_EDITING_ROW_BUFFER,
          "Bộ đệm dữ liệu cho dòng {0} chưa được tạo hoặc không khớp", "");      
//      controller.getCreator().displayMessage(MessageCode.ROW_SELECTION_REQUIRED, 
//          "Bạn cần chọn ít nhất một dòng");
    }

    /** 
     * returns a map of values of columns in the editing row buffer
     * that are editable
     */
    LinkedHashMap<DAttr,Object> vals = new LinkedHashMap();
    
    DAttr dc;
    
    int row = editingRowBuffer.getFirst();
    
    int colCount = getColumnCount();
    Object v;

    for (int i = 0; i < colCount; i++) {
      // only consider mutable columns
      if (isMutableColumn(i)) {
        v = getRawValueAt(row, i);
        dc = getColumnConstraint(i);
        // value may be null (i.e. user does not click on the cell to edit)
        vals.put(dc,v);
      }
    }

    //clearEditingBuffer();
    editingRowBuffer = null;
    
    return (vals.isEmpty() ? null : vals);
  }
  
  @Override
  public void updateDataComponent(String attribName) throws NotFoundException {
    //nothing to do here because table row updates data automatically 
    
    /* Object currentObj = controller.getCurrentObject();
    
    if (currentObj != null) {
      Class c = controller.getDomainClass();
      DSMBasic dsm = controller.getCreator().getDomainSchema();
      DomainConstraint attrib = dsm.getDomainConstraint(c, attribName);
      JComponent comp = getComponent(attrib);
      JDataField dcomp;
      Object fieldVal;
      if (comp != null) {
        if (comp instanceof JDataField) {
          dcomp = (JDataField) comp;
          fieldVal = dsm.getAttributeValue(c, currentObj, attrib);
          // update the value
          dcomp.setValue(fieldVal);
        }
      }
    }*/
  }
  
  @Override
  public void update(Object o) {
    /**
     * Refresh the table data
     * Highlight the row that corresponds to the current object.
     **/
    int rowCount = getRowCount();

    if (rowCount <= 0)
      return;

    // delete editing buffer so that refreshData() (performed next)
    // can get the existing object data (not the updated one in the buffer)  
    editingRowBuffer = null;
    
    // refresh the data to reflect state change of the object
    refreshData();
    
    // select the object's row
    for (int row = 0; row < rowCount; row++) {
      Object ro = dataModel.tableData.get(row);
      if (ro == o) {
        // found
        selectRow(row);
        //setRowSelectionInterval(row, row);
        break;
      }
    }
  }
  
  @Override
  public String toString() {
    return "JObjectTable("+getName()+")";
  }
}
