package jda.mosa.controller.assets.datacontroller;

import java.util.Map;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.helper.objectbrowser.ObjectBrowser;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.tables.JObjectTable;

/**
   * A sub-class of {@see TableDataController} for manipulating domain objects
   * that are displayed on {@see JObjectTable} containers.
   * 
   * @author dmle
   * 
   */
  public class ObjectTableController<C> extends TableDataController<C> {
    public ObjectTableController(ControllerBasic creator, ControllerBasic user, ControllerBasic.DataController parent) {
      super(creator,user, parent);
    }

    @Override
    public void onCreateObject(C obj) {
      JDataContainer dataContainer = getDataContainer();  // v5.1c
      
      if (dataContainer != null) // v3.2 added this check
      {
        // v5.1c:
        if (dataContainer != null) {
          dataContainer.onCreateObject(obj);
        }
        
        // v3.3: perform command (if any)
        doOnCreateCommand(obj);
        
        /**
         * add object to the table model 
         * delete the current row to allow the object to be displayed
         */
        JObjectTable table = (JObjectTable) dataContainer;
  
        table.addObjectDelayed(obj);
        
        int row = table.getNewRow(); 
        if (row  > -1) // v3.0
          table.deleteRow(row,  false);
      }
    }
    
    @Override
    public void onDeleteObject(C o) {
      // v3.3: do command (if any)
      doOnDeleteCommand(o);
      
      JObjectTable table = (JObjectTable) dataContainer;
      // delete object from model and refresh the table
      table.deleteObject(o);
    }

    /**
     *  @version 3.0 
     */
    @Override
    public boolean isOpenWithAllObjects() {
      // Use wider range of open policy than super's implementation
      OpenPolicy pol = getOpenPolicy();
      return pol.isWithAllObjects() || pol.isWithObject();
    }
    
    /**
     * @effects 
     *  display <b>all</b> objects on object form
     */
    @Override
    protected void onOpenAndLoad() throws NotPossibleException, NotFoundException {
      /*v3.0: moved to a shared method
      try {
        // load all objects (without updating the GUI)
        boolean forceToIndex = false;
        browseFirstToLast(false,false,forceToIndex);
        
        // back to first, this time updates the GUI and fire state change
        // v3.0: first(true, true,false);
        forceToIndex = false;
        first(true, true, false, forceToIndex);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_BROWSE_ALL,
            e);
      }
      */
      showAllObjects();
    }
    
    /**
     * @effects 
     *  cause all objects in the browser to display on this.dataContainer; 
     *  move focus on the first object  
     * @version 3.0
     */
    private void showAllObjects() {
      try {
        // load all objects (without updating the GUI)
        boolean forceToIndex = false;
        browseFirstToLast(false,false,forceToIndex);
        
        // back to first, this time updates the GUI and fire state change
        // v3.0: first(true, true,false);
        forceToIndex = false;
        first(true, true, false, forceToIndex);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_BROWSE_ALL,
            e);
      }      
    }

    @Override
    public void onOpenAll(Map<Oid,C> objects) throws NotPossibleException, NotFoundException {
      // TODO: is there another way of showing the objects without having to browse
      // e.g. see DataPanelController.onOpenAll
      
      /// v3.0: add indexing & update link to parent (borrowed from DataPanelController.onOpenAll) 
      // index objects (if needed)
      if (isIndexable()) {
        for (C obj : objects.values()) {
          // all objects are freshly loaded
          // v3.0: updateObjectIndex(false, obj);          
          updateObjectIndex(true, obj);
        }
      }
      
      // if this is a child controller then reset the parent object's buffer using these objects
      if (parent != null 
          && isUpdateLinkToParent() //v3.0
          ) {
        //Object parentObj = getParentObject();
        DAttr linkParentAttrib = getLinkAttributeOfParent(); //.name();
        /*v3.0: update parent's GUI if association link update causes a state change
        parent.updateAssociationLink(linkParentAttrib, objects.values());
        */
        boolean parentUpdated = parent.updateAssociationLink(linkParentAttrib, objects.values());
        if (parentUpdated) {
          parent.updateGUI(null);
        }
      }
      
      /*v3.0: use a more specific method 
      // show objects on the table 
      this.onOpenAndLoad();
      */
      showAllObjects();
    }
    
    @Override
    protected void updateGUIOnBrowserStateChanged(ObjectBrowser<C> browser, AppState state) {
      
      C obj = browser.getCurrentObject();

      JObjectTable table = (JObjectTable) dataContainer;
      
      /**
       * if state is FIRST
       *   if table model is emtpy
       *     add a new row and update that row with obj
       *   else if obj not in table model
       *     insert first row and update that row with obj
       *   else
       *     highlight obj's row 
       * else if state is LAST
       *   if table model is emtpy OR obj not in table model
       *     add a new row and update that row with obj
       *   else
       *     highlight obj's row
       * else if state is NEXT
       *   if obj not in table model
       *     insert a new row immediately after the current row and update row with obj
       *   else
       *     highlight obj's row
       * else if state is PREV
       *   if obj not in table model
       *     insert a new row immediately before the current row and update row with obj
       *   else
       *     highlight obj's row
       *    
       */
      
      int ri;
      int numTableRows = table.getRowCount();
      if (state == AppState.First) {
        if (table.isEmpty()) {
          table.addObject(obj);
        } else if (!table.containsObject(obj)) {
          table.insertObjectAt(obj,0);
        } 
        //else {
        table.selectRow(0);
        //}
      } else if (state == AppState.Last) {
        if (table.isEmpty() || !table.containsObject(obj)) {
          table.addObject(obj);
        } 
        //else {
        table.selectRow(numTableRows-1);
        //}
      } else if (state == AppState.Next) {
        ri = table.getSelectedRow();
        if (!table.containsObject(obj)) {
          table.insertObjectAt(obj,ri+1);
        } 
        //else {
        table.selectRow(ri+1);
        //}
      } else if (state == AppState.Previous) {
        ri = table.getSelectedRow();
        if (!table.containsObject(obj)) {
          table.insertObjectAt(obj,ri);
        } 
        //else {
        table.selectRow(ri);
        //}
      }  
    }
    
    @Override
    protected void onReset() {
      /*
       * v2.6.4.a: fixed bug  
       *   if dataContainer is in the New state (i.e. a new object is being created)
       *    empty the editing buffer and update the table's view
       *   else
       *    redisplay the current object by invoking dataContainer.update
       */
      //dataContainer.update(currentObj);
      JObjectTable table = (JObjectTable) dataContainer;
      
      if (isCreating()) {
        boolean modified = table.clearEditing();
        if (modified)
          table.refreshData();
      } else {
        dataContainer.update(getCurrentObject());
      }
    }    
  } // end ObjectTableController