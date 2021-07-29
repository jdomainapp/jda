package jda.modules.exportdoc.controller;

import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;

import jscaledhtmltextpane.print.HtmlTextPanePrintForm;
import jscaledhtmltextpane.print.HtmlTextPanePrintForm.PrintDestination;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.exportdoc.model.DataDocument;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.syntax.MCCLConstants;
import jda.modules.mccl.syntax.MCCLConstants.PaperSize;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.SimpleDataController;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.view.View;
import jda.mosa.view.assets.JDataContainer;
import jda.util.properties.PropertySet;

/**
 * @overview
 *  A sub-type of {@link SimpleDataController} specifically used to handle {@link LAName#Export} and 
 *  {@link LAName#Print} 
 *   
 * @author dmle
 * 
 * @version 3.2
 */
public class DocumentExportDataController extends SimpleDataController<DataDocument> {

  public DocumentExportDataController(ControllerBasic creator,
      ControllerBasic user, DataController parent) throws NotPossibleException {
    super(creator, user, parent);
  }

  @Override
  public boolean actionPerformable(String cmd) {
    // only handling Export
    if (cmd.equals(LAName.Export.name())
        || cmd.equals(LAName.Print.name())) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected boolean actionPerformedPreConditions(ActionEvent e) {
    // always available for handling
    return true;
  }

  @Override
  protected void actionPerformed(String cmd) {
    // only handle Export
    try {
      if (cmd.equals(LAName.Export.name()))
        export();
      else if (cmd.equals(LAName.Print.name()))
        print();
    } catch (Exception ex) {
      controller.displayErrorFromCode(MessageCode.ERROR_HANDLE_COMMAND, this, ex, cmd);
    }
  }
  
  /**
   * @effects if <code>objectBuffer != null</code> then export the data contained 
   *          in the <code>dataContainer</code> to a specified format and display it to  
   *          the user
   */
  public void export() throws NotPossibleException, ConstraintViolationException, NotFoundException, DataSourceException {
    // v3.0: the report data container whose data to be exported (must be done before exportCtl.init (below)) 
    JDataContainer activeDataContainer = controller.getMainController().getActiveDataContainer();

    if (activeDataContainer == null)
      throw new NotPossibleException(NotPossibleException.Code.NO_ACTIVE_DATA_CONTAINER);

    export(activeDataContainer);
  }

  /**
   * @effects 
   *  run export on the specified data container
   */
  public void export(final JDataContainer dataContainer) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    // the export module
//    Class<DocumentExportController> exportCls = DocumentExportController.class;
//    DocumentExportController<DataDocument> exportCtl = ControllerBasic
//        .lookUpByControllerType(exportCls);
//
//    exportCtl.init(); // to ensure that GUI and other resources are initialised
    
    DocumentExportController exportCtl = (DocumentExportController) getCreator();
    
    exportCtl.init(); // to ensure that GUI and other resources are initialised
    
    // the doc object
    DataController<DataDocument> exportDctl = exportCtl.getRootDataController();
    DataDocument doc = exportDctl.getCurrentObject();
    
    if (doc == null) {
      // not yet created
      doc = exportDctl.createNewObject(true);
    }
    
    // TODO: pass as arguments in createNewObject (above)
    // set doc properties 
    // - only do this if data container is not a container of the export module (itself)
    // TODO: remove this check if Export action can be excluded for child data container of ModulePage/ModuleHtmlPage
    //    of ModuleExportDocument
    if (dataContainer.getController().getUser() != exportCtl) {
      doc.setDataContainer(dataContainer);
      ControllerBasic activeController = dataContainer.getController().getCreator();
      String docName = activeController.getName();
      String docTitle = null;
      doc.setName(docName);
      doc.setDocTitle(docTitle);
      
      // v3.1: set doc's print settings based on the print config of the target module
      PropertySet printCfg = activeController.getApplicationModule().getPrintConfig();
      doc.setPrintConfig(printCfg);
    }
    
    // run export using doc
    exportCtl.run();
  }
  
  /**
   * @effects 
   *  if {@link DocumentExportController} is providing a {@link DataDocument} <tt>d</tt>
   *    send content of <tt>d</tt> to the printer
   *  else
   *    do nothing
   *  @version 
   *  - 3.2c: improved to support module-specific printing (for modules that already have content in HTML ready to be printed, i.e. without the need to 
   *  first exporting that content to HTML)
   */
  public void print() throws NotPossibleException {
    
    DocumentExportController exportCtl = (DocumentExportController) getCreator();
    View exportGUI = exportCtl.getGUI();
    DataController exportDctl = exportCtl.getRootDataController();
    JDataContainer exportDataContainer = exportDctl.getDataContainer();
    
    ControllerBasic mainCtl = exportCtl.getMainController();
    
    // the active data container
    JDataContainer activeDataContainer = mainCtl.getActiveDataContainer();
    View activeGUI = (activeDataContainer != null) ? activeDataContainer.getController().getUserGUI() : null;
    
    JTextComponent textComp = null;
    int orientation = 0;
    MediaSizeName mediaSize = null;
    
    if (activeDataContainer != null && activeGUI != exportGUI && exportCtl.hasPrintableContent(activeDataContainer)) {
      textComp = exportCtl.getPrintableComponent(activeDataContainer);
      // get page format from module or use default if not specifed
      ControllerBasic activeCtl = activeGUI.getController();
      PropertySet modulePrintCfg = activeCtl.getApplicationModule().getPrintConfig();
      if (modulePrintCfg != null) {
        // use page format from module
        orientation = modulePrintCfg.getPropertyValue(PropertyName.pageFormat, MCCLConstants.PageFormat.class, MCCLConstants.PageFormat.Portrait).getOrientation();
        mediaSize = modulePrintCfg.getPropertyValue(PropertyName.paperSize, PaperSize.class, PaperSize.A4).getMediaSize();
      } else {
        // use default
        orientation = DataDocument.defaultPageOrientation;
        mediaSize = DataDocument.defaultMediaSize;
      }
    } 
    
    if (textComp == null) {
      // no active container OR no printable content in the active container
      // use the export controller's printable component
      DataDocument doc = (DataDocument) exportDctl.getCurrentObject();
      if (doc != null) {
        textComp = exportCtl.getPrintableComponent();
        
        // get page format from doc
        orientation = doc.getPageOrientation();
        mediaSize = doc.getMediaSizeName();
      } else {
        throw new NotPossibleException(NotPossibleException.Code.NO_PRINT_DOCUMENT);      
      }
    }
    
    // print textComp
    /* v3.1: use printing library
    PrintRequestAttributeSet printAttrib = new HashPrintRequestAttributeSet();
    printAttrib.add(OrientationRequested.PORTRAIT);

    textComp.print(null, // header
        null, // footer
        true, // show dialog
        null, // service (default)
        printAttrib, // attributes
        true // interactive
        );
    */
    //v3.2c: added for status message
    final String domainClassLabel = exportCtl.getDomainClassLabel();
    Object[] mesgArgs = new Object[] {domainClassLabel};
    String mesg = MessageCode.OBJECT_PRINT_STARTED.getMessageFormat().format(mesgArgs);
    setCurrentStateSimple(AppState.Print_Started, mesg);

    try {
      if (textComp instanceof JEditorPane) {
        // print using a special form specifically designed for editor pane
        JEditorPane editor = (JEditorPane) textComp;
        HtmlTextPanePrintForm printForm = HtmlTextPanePrintForm.getInstance(editor);
        //printForm.setVisible(true);

        PageFormat pgFormat = new PageFormat(); 
        HtmlTextPanePrintForm.getPageFormat(pgFormat, mediaSize, orientation);
        //printForm.printf(pgFormat, PrintDestination.File);
        printForm.printf(pgFormat, PrintDestination.Printer);
      } else {
        // print component directly...
        PrintRequestAttributeSet printAttrib = new HashPrintRequestAttributeSet();
        printAttrib.add(OrientationRequested.PORTRAIT);

        textComp.print(null, // header
            null, // footer
            true, // show dialog
            null, // service (default)
            printAttrib, // attributes
            true // interactive
            );
      }
      
      // v3.2c: added for status message
      if (exportCtl.getProperties()
          .getBooleanValue("show.message.popup", true)) {
        exportCtl.displayMessageFromCode(MessageCode.OBJECT_PRINT_COMPLETED, this,domainClassLabel);
      }
      
      mesg = MessageCode.OBJECT_PRINT_COMPLETED.getMessageFormat().format(mesgArgs);
      setCurrentStateSimple(AppState.Print_Completed, mesg);
      
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PRINT, e,  
          new Object[] {controller.getDomainClassLabel()});
    }
  }
}
