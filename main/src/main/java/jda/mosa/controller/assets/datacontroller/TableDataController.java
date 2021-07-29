package jda.mosa.controller.assets.datacontroller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.helper.objectbrowser.ObjectBrowser;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.tables.JDataTable;

/**
   * A sub-class of {@see DataController} for manipulating domain objects that
   * are displayed on {@see JDataTable} containers.
   * 
   * @author dmle
   * 
   */
  public class TableDataController<C> extends ControllerBasic.DataController<C> implements
      KeyListener {
    public TableDataController(ControllerBasic creator, ControllerBasic user, ControllerBasic.DataController parent) {
      super(creator, user, parent);
    }

    /**
     * @effects inserts a new row ready for a new object, before the row
     *          <code>beforeRow</code>
     */
    protected void insertObject(int beforeRow) {
      JDataTable table = (JDataTable) dataContainer;
      table.insertRow(beforeRow);
    }

    /**
     * This method is invoked after the user has pressed the open command
     * button.
     */
    @Override
    public void onOpen() {
      /**
       * clear the current table
       */
      JDataTable table = (JDataTable) dataContainer;

      //todo: not show the loaded objects, instead update the table when user browse  

//      if (isBufferInit()) {
//        Object o;
//        List data = new ArrayList();
//        List row;
//
//        int colCount = table.getColumnCount();
//        Iterator objectsIt = getObjectBufferIterator();
//        while (objectsIt.hasNext()) {
//          o = objectsIt.next();
//          row = schema.getAttributeValues(o);
//          data.add(row);
//        }
//
//        table.setModel(data);
//      } else {
      if (table.getRowCount() > 0)
        table.clear();
//      }
    }

    @Override
    public void onOpenAll(Map<Oid,C> objects) throws NotFoundException, NotPossibleException {
      // for sub-type to implement
    }
    
    /**
     * This method is invoked after the user has pressed the new-object command
     * button.
     */
    @Override
    public void onNewObject() {
      JDataTable table = (JDataTable) getDataContainer(); //dataContainer;
      
      // insert a new row
      int row = table.addRow();

      // highlight the new row
      if (row > -1) {
        /*v3.1:use the updated selectRow method
        table.setRowSelectionInterval(row, row);
        */
        table.selectRow(row);
        
        //v2.7.2: update the user's GUI size to best fit the new row
        getUserGUI().updateSizeOnComponentChange();
        
        // v5.1c: added this to reset all cell editor
        table.onNewObject(row);
      }
    }

    @Override
    public void onCreateObject(C obj) {
      /**
       * - add the created object to the table model at the same position as
       * that of the current row <br>
       * - update the current row with the values of the just created object <br>
       */
       /**v2.5.4*/
      throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
          "TableDataController.onCreateObject()");
//      JDataTable table = (JDataTable) dataContainer;
//      int row = table.getSelectedRow();
//      if (row > -1) {
//        // update(row, obj);
//        List vals = schema.getAttributeValues(obj);
//        table.setRowData(row, vals);
//      }
    }

    // @Override
    // protected void onReset() {
    // /**
    // * if there is a current object then display it, otherwise reset the
    // * current row
    // */
    // // TODO: resetting the values using the currentObj has no effect for
    // // tables
    // // since currentObj is modified directly by user editing
    // // JDataTable table = (JDataTable) dataContainer;
    // // int row = table.getSelectedRow();
    // // if (row > -1) {
    // // if (currentObj != null) {
    // // table.setRowData(row, currentObj);
    // // } else {
    // // table.setInitRowData(row);
    // // }
    // // }
    // }

    @Override
    public void onCancel() {
      // v5.1c: added this 
      onCancelGUI();
      // v5.1c: end
      
      ControllerBasic controller = getCreator();
      
      // reverse onNewObject
      // delete the last row
      JDataTable table = (JDataTable) dataContainer;

      int row = table.getNewRow(); //table.getRowCount() - 1;
      if (row > -1) {
        table.deleteRow(row, false);
      } else {
        // something wrong
        controller.logError("Could not find new row in table: " + table, null);
      }
    }

    @Override
    public void onCauseAdd(C obj) {
      onNewObject();
      onCreateObject(obj);
    }

    @Override
    public void onDeleteObject(C o) {
      // v3.3: do command (if any)
      doOnDeleteCommand(o);
      
      // delete the row corresponding to the object from the table, which also
      // removes object from the object buffer
      /**v2.5.4: only refresh the table */
      JDataTable table = (JDataTable) dataContainer;
//      int row = table.getSelectedRow(o);
//      if (row > -1) {
//        table.deleteRows(new int[] { row }, false);
//      } else {
        table.refresh();
//      }
    }

    @Override
    protected void updateGUIOnBrowserStateChanged(ObjectBrowser<C> browser, AppState state) {
      C obj = browser.getCurrentObject();

      ControllerBasic controller = getCreator();
      DSMBasic dsm = controller.getDodm().getDsm();

      // create table row from object state
      List rowData = dsm.getAttributeValues(obj);
      
      // add table row to table
      JDataTable table = (JDataTable) dataContainer;
      int rowIndex = table.addRow();
      table.setRowData(rowIndex, rowData);
    }
    
    @Override
    public void refresh() {
      super.refresh();  // v2.7.4

      // display the refreshed state on the data container
      JDataTable table = (JDataTable) dataContainer;
      if (table.getRowCount() > 0)
        table.refresh();
    }
//    /**
//     * @effects if <code>this.objectBuffer</code> contains <code>obj</code> then
//     *          refreshes the GUI (possibily) to show it.
//     */
//    protected void refreshBuffer(C obj) throws DBException {
//      if (!isBrowserOpened()){ //objectBuffer == null) {
//        //openBuffer(true);
//        open(true);
//      } else {
//        // could also use indexOf (but could be costly for large buffer)
//        // objectBuffer.indexOf(obj) > -1)
//        Query parent = getParentObjectQuery();
//        if (parent != null && parent.eval(schema, obj)) {
//          // a child controller
//          // refresh the table data
//          JDataTable table = (JDataTable) dataContainer;
//          table.refresh();
//        }
//      }
//    }

    // /////////////////// Mouse events
    public void mouseClicked(MouseEvent e) {
      // user clicked on table
      JDataTable table = (JDataTable) getDataContainer(); // v5.1c: dataContainer;

      // switch the current object to the selected object
      // support single-selection only
      int row = table.getSelectedRow();
      
      if (row > -1) {
//        int currIndex = getObjectIndex(currentObj);
        C selectedObject = (C) table.getSelectedObject();
        
        final C currObj = getCurrentObject();
        
        if (selectedObject != currObj) {
          // different selected object
          
          // set it to the current object
          Oid id = setCurrentObject(selectedObject, false);
          getBrowser().move(id, selectedObject);  
          
          /**
           * v2.6.1: if the selected row is being edited then swicth to editing
           * state else switch to object-changed state
           */
          if (table.isEditing(row)) {
            setCurrentState(AppState.Editing);
          } else {
            setCurrentState(AppState.CurrentObjectChanged);
          }
        }
        
//        // v3.0: update list of selected objects
//        boolean ctlOn = e.isControlDown();
//        if (ctlOn) {
//          // multiple selection: add to list (if not already)
//          addSelectedObject(selectedObject);
//        } else {
//          // single selection: clear list first then add
//          setSelectedObject(selectedObject);
//        }
      }
    }

    // /////////////////// Keyboard events
    /**
     * Invoked when a key has been pressed. See the class description for
     * {@link KeyEvent} for a definition of a key pressed event.
     */
    // TODO: seems to be ignored after first use (related to table's action-map
    // being set to null)
    public void keyPressed(KeyEvent e) {
      ControllerBasic controller = getCreator();
      
      //
      // System.out.println("key pressed");
      JDataTable table = (JDataTable) dataContainer;
      int key = e.getKeyCode();

      // v2.6.1: add support for move right, move left
      if (key == e.VK_ENTER) {
        if (e.isShiftDown()) { // SHIFT+ENTER
          if (e.isControlDown()) { // CTRL+SHIFT+ENTER: add row at the end
            // adds a new object
            newObject();
          } else { // SHIFT+ENTER
            // insert a row before the currrently selected row
            int[] rows = table.getSelectedRows();
            // support single row only
            if (rows.length > 0)
              insertObject(rows[0]);
          }
        }
      } else if (!table.isEmpty()) { 
        // navigation keys
        if (key == e.VK_DELETE) {
          // delete the current row
          try {
            deleteObject();
          } catch (DataSourceException ex) {
            controller.displayError(ex.getCode(), this, ex);
          }
        } else if (key == e.VK_UP) {
          // move previous
          //getBrowser().previous();
          try {
            getBrowser().prev();
            C o = getBrowser().getCurrentObject();
            setCurrentObject(o, true);
          } catch (DataSourceException ex) {
            //TODO: display to user
            ex.printStackTrace();
          }
        } else if (key == e.VK_DOWN) {
          // moves next
          //getBrowser().next();
          try {
            getBrowser().next();
            C o = getBrowser().getCurrentObject();
            setCurrentObject(o, true);
          } catch (DataSourceException ex) {
            //TODO: display to user
            ex.printStackTrace();
          }
        } else if (key == e.VK_RIGHT) {
          // move right
          int row = table.getSelectedRow();
          int col = table.getColumnOnFocus();
          if (col == table.getColumnCount()) {
            col = 0;
          } else {
            col = col+1;
          }
          table.setCellSelected(row, col);
          table.setColumnOnFocus(col);
        } else if (key == e.VK_LEFT) {
          // move left
          int row = table.getSelectedRow();
          int col = table.getColumnOnFocus();
          if (col <= 0)
            col = table.getColumnCount();
          else
            col = col-1;
          table.setCellSelected(row, col);
          table.setColumnOnFocus(col);
        } else if (key == e.VK_HOME) {
          //getBrowser().first();
          try {
            getBrowser().first();
            C o = getBrowser().getCurrentObject();
            setCurrentObject(o, true);
          } catch (DataSourceException ex) {
            //TODO: display to user
            ex.printStackTrace();
          }
        } else if (key == e.VK_END) {
          //getBrowser().last();
          try {
            getBrowser().last();
            C o = getBrowser().getCurrentObject();
            setCurrentObject(o, true);
          } catch (DataSourceException ex) {
            //TODO: display to user
            ex.printStackTrace();
          }
          
        }
      }
    }

    @Override
    public Collection<C> getSelectedObjects() {
      JDataTable table = (JDataTable) dataContainer;

      return table.getSelectedObjects();
    }

//    /**
//     * @effects invokes <code>super.getDefaultMap</code> and remove some
//     *          unsupported GUI actions.
//     */
//    @Override
//    protected Map getDefaultStateMap() {
//      ProtectedMap stateMap = (ProtectedMap) super.getDefaultStateMap();
//
//      // sets value for unsupported actions
//      try {
//        stateMap.put(Reset, Boolean.FALSE, true);
//      } catch (IllegalAccessException e) {
//        // should not happen
//      }
//
//      return stateMap;
//    }

//    protected Element toPDF(PdfWriter pdfWriter) throws DocumentException {
//      // TODO: not sure why this block was needed before!
//      // if (objectBuffer == null) {
//      // return null;
//      // }
//
//      /**
//       * adds the data model of this table to the document
//       */
//      final JDataTable dtable = (JDataTable) dataContainer;
//      // v2.5.4: ignore columns that are not visible
//      int colCount = dtable.getColumnCount();
//      int visibleColCount = dtable.getColumnCount(true); 
//      // a table with columns = dtable.columnCount
//      PdfPTable table = new PdfPTable(visibleColCount);
//
//      // header
//      table.setHeaderRows(1);
//
//      Font font;
//      Component renderer;
//      Color color;
//      JTableHeader jheader = dtable.getTableHeader();
//      font = jheader.getFont();
//      color = jheader.getForeground();
//
//      // v2.5.4: ignore headers of columns that are not visible
//      for (int i = 0; i < colCount; i++) {
//        // if not visible then skip
//        if (!dtable.isColumnVisible(i)) {
//          continue;
//        }
//        table.addCell(getPdfText(dtable.getColumnName(i), font, color));
//      }
//
//      // data: 
//      // v2.5.4: ignore columns that are not visible
//      // v2.7.2: TODO: to convert special data field values (e.g. image), See DataPanelController.toPdf for details.
//      Object val;
//      int rowCount = dtable.getRowCount();
//      for (int i = 0; i < rowCount; i++) {
//        for (int j = 0; j < colCount; j++) {
//          // if not visible then skip
//          if (!dtable.isColumnVisible(j)) {
//            continue;
//          }
//          val = dtable.getValueAt(i, j);
//          renderer = dtable.getTableCellEditorComponent(j);
//          font = renderer.getFont();
//          color = renderer.getForeground();
//          if (val != null)
//            table.addCell(getPdfText(val.toString(), font, color));
//          else
//            table.addCell("");
//        }
//      }
//
//      return table;
//    }
  } // end TableDataController