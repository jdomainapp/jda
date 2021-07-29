package jda.mosa.controller.assets.datacontroller;

import java.util.Collection;
import java.util.Map;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.helper.objectbrowser.ObjectBrowser;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.JDataContainer;

/**
   * A sub-class of {@see DataController} for manipulating domain objects that
   * are displayed on {@see DefaultPanel} containers.
   * 
   * @author dmle
   * 
   */
  public class DataPanelController<C> extends ControllerBasic.DataController<C> {
    public DataPanelController(ControllerBasic creator, ControllerBasic user, ControllerBasic.DataController parent) {
      super(creator, user, parent);
    }

    @Override
    public void refresh() {
      super.refresh();  // v2.7.4
      
      // display the refreshed state on the data container
      if (getCurrentObject() != null) {
        if (getOpenPolicy().isWithChildren()//contains(OpenPolicy.C)
            ) {
          // with children
          updateGUI(true);                 
        } else {  // without children
          updateGUI();
        }
      } else {
        clearGUI();
      }
    }
    
    /**
     * This method is invoked after the user has pressed the open command
     * button.
     */
    @Override
    public void onOpen() {
//      /** show the current object or clear if none is found */
//      if (currentObj != null) {
//        if (dataContainer != null)
//          dataContainer.update(currentObj);
//      }
      clearGUI();
    }

    /**
     * @effects 
     *  display the first object on object form
     */
    @Override
    protected void onOpenAndLoad() throws NotPossibleException, NotFoundException {
      try {
        first();
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_MOVE_FIRST,
            e, "Không thể mở dòng dữ liệu đầu tiên");
      }
    }
    
    @Override
    public void onOpenAll(Map<Oid,C> objects) throws NotPossibleException, NotFoundException {
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
      
      // move first
      try {
        first();
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_MOVE_FIRST,
            e, "Không thể mở dòng dữ liệu đầu tiên");
      }
    }
    
    @Override
    public void onNewObject() {
      resetGUI(false);
      // v5.1c: added this
      JDataContainer dataContainer = getDataContainer();
      if (dataContainer != null)
        dataContainer.onNewObject(null);
      // v5.1c: end
    }

    @Override
    public void onCreateObject(C obj) {
      JDataContainer dataContainer = getDataContainer();  // v5.1c

      if (dataContainer != null) { // v3.2 added this check
        // v5.1c:
        if (dataContainer != null) {
          dataContainer.onCreateObject(obj);
        }
        
        // v3.3: perform command (if any)
        doOnCreateCommand(obj);
      
        dataContainer.update(obj);
      }
    }

    @Override
    protected void updateGUIOnBrowserStateChanged(ObjectBrowser<C> browser, AppState state) {
      // for sub-classes to handle
      JDataContainer dataContainer = getDataContainer();  // v5.1c

      C o = browser.getCurrentObject();
      if (dataContainer != null 
          && o != getCurrentObject()    // v2.7.2: added this check
          ) {
        dataContainer.update(o);
      }
    }
    
    @Override
    protected void onReset() {
      if (isCreating()) {
        // clear the gui
        //v2.7.4: clearGUI();
        resetGUI();
      } else {
        // editing
        /**
         * if there is a current object then display it, otherwise clear the GUI
         */
        if (getCurrentObject() != null) {
          updateGUI();
        } else {
          //v2.7.4: clearGUI();
          resetGUI();
        }
      }
    }

    @Override
    protected void clearGUIOnDelete() {
      // same as clearGUI
      clearGUI();
    }
    
    @Override
    public void onCancel() {
      // v5.1c: added this 
      onCancelGUI();
      // v5.1c: end
      
      if (getCurrentObject() != null) {
        updateGUI();
      } else {
        clearGUI();
      }
    }

    @Override
    public Collection<C> getSelectedObjects() {
      // TODO: support this
      throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
          this.getClass().getSimpleName(), "");
    }

//    public void onCauseAdd(C obj) {
//      objectBuffer.add(obj);
//    }

    /**v2.5.4: move to parent*/
//    @Override
//    public void onDeleteObject(C obj) {
//      objectBuffer.remove(obj);
//      // TODO: what should we do to the GUI when an object is removed?
//      // dataContainer.update(obj);
//    }

//    protected Element toPDF(PdfWriter pdfWriter) throws DocumentException {
//      if (currentObj == null)
//        return null;
//
//      //final AppGUI gui = getCreator().getGUI();
//      final DefaultPanel panel = (DefaultPanel) dataContainer;
//
//      /***
//       * <pre>
//       * if this is not a root panel 
//       *   get title panel and adds to doc
//       * else
//       *   t = a new pdf table with 2 columns
//       *   for every component c in the panel
//       *     l = c's label
//       *     if c is a data field
//       *       adds row (l,c) to t
//       *     else // c is a data container
//       *      t' = a pdf table created from c
//       *      adds row (l,t') to t
//       * </pre>
//       */
//      // pdf table with 2 columns: one for label, one for component
//      PdfPTable table = new PdfPTable(2);
//
//      // disable the border of non-top-level tables
//      // final int CELL_BORDER = PdfPCell.LEFT | PdfPCell.RIGHT | PdfPCell.TOP |
//      // PdfPCell.BOTTOM;
//      if (parent == null || panel.isNested()) {
//        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//      }
//
//      DomainConstraint dc;
//      List<JComponent> labels = panel.getLabelComponents();
//
//      JLabel label;
//      Component comp;
//      JDataField df;
//      Object val;
//      JDataContainer dcont;
//      DataController dctl;
//      PdfPCell cell;
//      for (JComponent c : labels) {
//        label = (JLabel) c;
//        comp = label.getLabelFor();
//
//        // skip null or invisible components
//        if (comp == null || !comp.isVisible())
//          continue;
//
//        if (comp instanceof JDataField) { // data field
//          df = (JDataField) comp;
//          /*v2.7.2: moved to a separate method and updated
//
//          if (df instanceof JSpinnerField) { // get the bounded value
//            val = ((JSpinnerField) df).getBoundValue();
//          }
//          else {
//            val = df.getValue();
//          }
//
//          // label
//          table.addCell(getPdfLabel(label)); 
//          // value
//          if (val != null) {
//            table.addCell(getPdfText(val.toString(), df.getFont(),
//                df.getForeground()));
//          } else {
//            table.addCell("");
//          }
//          */
//          addDataFieldToPdfTable(pdfWriter, table, label, df);
//        } else { // data container
//          // the label
//          cell = new PdfPCell(getPdfLabel(label));
//          cell.setColspan(2);
//          cell.setBorder(Rectangle.NO_BORDER);
//          cell.setPaddingBottom(cell.getPaddingBottom() + 5);
//          table.addCell(cell);
//
//          // the container's PDF table
//          dcont = gui.toDataContainer(comp);
//          dctl = dcont.getController();
//          // update the document with this container's view
//          PdfPTable nested = (PdfPTable) dctl.toPDF(pdfWriter);
//
//          if (nested != null) {
//            // the table is to span the next row
//            nested.setWidthPercentage(100);
//            cell = new PdfPCell();
//            cell.setColspan(2);
//            cell.addElement(nested);
//            cell.setBorder(Rectangle.NO_BORDER);
//            // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            // cell.disableBorderSide(PdfPCell.LEFT & PdfPCell.TOP &
//            // PdfPCell.RIGHT & PdfPCell.BOTTOM);
//            // cell.setBorderColor(new BaseColor(100,100,100));
//            cell.setPadding(0);
//            table.addCell(cell);
//          }
//        }
//      } // end for
//
//      return table;
//    }
  } // end DataPanelController