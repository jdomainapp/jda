package jda.modules.printing.controller;

import static jda.modules.mccl.conceptmodel.controller.LAName.Print;

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
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.exportdoc.controller.DocumentExportController;
import jda.modules.exportdoc.model.DataDocument;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.datacontroller.DataPanelController;
import jda.mosa.controller.assets.util.MessageCode;

/**
 * @overview
 *  A data controller responsible for printing.
 *  
 * @author dmle
 * @version 3.2
 */
public class PrintDataController<C> extends DataPanelController<C> {

  public PrintDataController(ControllerBasic creator, ControllerBasic user, ControllerBasic.DataController parent)
      throws NotPossibleException {
    super(creator, user, parent);
  }

  /**
   * @effects 
   *  if cmd = Print
   *    return true
   *  else 
   *    return false 
   */
  @Override
  public boolean actionPerformable(String cmd) {
    if (cmd.equals(Print.name())) {
      return true;
    } else{
      return false;
    }
  }

  @Override
  protected boolean actionPerformedPreConditions(ActionEvent e) {
    return true;
  }

  @Override
  protected void actionPerformed(String cmd) {
    // only perform Chart
    try {
      print();
    } catch (Exception ex) {
      controller.displayErrorFromCode(MessageCode.ERROR_HANDLE_COMMAND, this, ex, cmd);
    }
  }

  /**
   * @effects if this.currentObj != null print the content of this object
   */
  protected void print() throws 
  // v3.1: IOException, PrinterException 
    NotPossibleException
  {
    Class<DocumentExportController> exportCls = DocumentExportController.class;
    DocumentExportController exportCtl = ControllerBasic.lookUpByControllerType(exportCls);

    // v2.7.3: DocumentBuilder doc =
    // exportCtl.getRootDataController().getCurrentObject();
    DataDocument doc = (DataDocument) exportCtl.getRootDataController()
        .getCurrentObject();

    if (doc != null) {
      // InputStream contentStream = doc.getContentStream();

      JTextComponent textComp = exportCtl.getPrintableComponent();

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
      try {
        if (textComp instanceof JEditorPane) {
          // print using a special form specifically designed for editor pane
          JEditorPane editor = (JEditorPane) textComp;
          HtmlTextPanePrintForm printForm = HtmlTextPanePrintForm.getInstance(editor);
          //printForm.setVisible(true);

          // get page format from doc
          int orientation = doc.getPageOrientation();
          MediaSizeName mediaSize = doc.getMediaSizeName();
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
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PRINT, 
            new Object[] {controller.getDomainClassLabel()}, e);
      }
    }
  }
} 