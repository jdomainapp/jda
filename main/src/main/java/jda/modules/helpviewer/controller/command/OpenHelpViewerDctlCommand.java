/**
 * 
 */
package jda.modules.helpviewer.controller.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.exportdoc.htmlpage.model.HtmlPage;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import jda.mosa.model.Oid;
import jda.mosa.view.View;
import jda.mosa.view.assets.JDataContainer;
import jda.util.SwTk;

/**
 * 
 * @overview
 *  Create a {@link Page} object that encapsulates the html help file configured for the user module of the 
 *  <b>active</b> data container; then display (open) this object for viewing.
 *  
 *  <p>If there is no active data container or no html help file is specified for the user module then 
 *  informs user with an error message. 
 *   
 * @version 3.2c
 *
 * @author dmle
 *
 */
public class OpenHelpViewerDctlCommand<C> extends DataControllerCommand {

  private Map<Oid, Page> helpPage;
  
  /**
   * @effects 
   *
   */
  public OpenHelpViewerDctlCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  /**
   * @effects 
   *  Create a {@link Page} object that encapsulates the html help file configured for the user module of the 
   *  <b>active</b> data container; then display (open) this object for viewing.
   *  
   *  <p>If there is no active data container or no html help file is specified for the user module then 
   *  informs user with an error message. 
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DataController helpViewerDctl = getDataController();
    ControllerBasic helpViewerController = helpViewerDctl.getCreator();
    ControllerBasic mainCtl = helpViewerController.getMainController();
    
    // the active data container
    JDataContainer activeDataContainer = mainCtl.getActiveDataContainer();

    if (activeDataContainer == null) {
      // show program help
      //throw new NotPossibleException(NotPossibleException.Code.NO_ACTIVE_DATA_CONTAINER);
      viewHelp(mainCtl.getApplicationModule());
    } else {    
      // the user module of the active data container
      View userGui = activeDataContainer.getController().getUserGUI();
      ApplicationModule userModule = userGui.getController().getApplicationModule();
      
      if (userModule == null) {
        // no user module defined
        throw new NotPossibleException(NotPossibleException.Code.NO_MODULE_DEFINED, new Object[] {userGui});
      } 
      
      viewHelp(userModule);
    }
  }

  /**
   * @effects 
   *  Open help file of <tt>module</tt> for viewing
   */
  private void viewHelp(ApplicationModule module) throws Exception {
    DataController helpViewerDctl = getDataController();
    ControllerBasic helpViewerController = helpViewerDctl.getCreator();
    DOMBasic dom = helpViewerDctl.getDodm().getDom();
    
    // the html help file of the user module (if any)
    String helpFilePath = SwTk.getModuleHelpFilePath(helpViewerController.getConfig(), module);
    
    File file = new File(helpFilePath);
    if (!file.exists()) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, new Object[] {helpFilePath});
    }

    // create a Page object for the help file and open it
    
    //TODO: fins is not needed b/c page is viewed via its output file
    InputStream fins = null;
    try {
      fins = new FileInputStream(file);
      Page apage = HtmlPage.createTemplatePage(fins);
      
      // IMPORTANT: needed for viewing on the GUI
      apage.setOutputFile(file);
      
      if (helpPage == null) {
        helpPage = new HashMap();
      } else {
        helpPage.clear();
        // remove existing pages
        Page p; Oid pid;
        for (Entry<Oid,Page> e : helpPage.entrySet()) {
          pid = e.getKey();
          p = e.getValue();
          dom.deleteObject(p, pid, HtmlPage.class);
        }
      }
      
      Oid pid = dom.addObject(apage);
      
      helpPage.put(pid, apage);
      
      helpViewerDctl.openObjects(helpPage.values(), false);
      
      // show GUI (already created)
      helpViewerController.showGUI();
      
    } catch (FileNotFoundException e) {
      // should not happen
    } finally {
      if (fins != null)
        fins.close();
    }    
  }

}
