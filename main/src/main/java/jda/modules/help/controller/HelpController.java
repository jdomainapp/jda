package jda.modules.help.controller;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.help.model.AppHelp;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.TaskController;
import jda.mosa.view.View;
import jda.mosa.view.assets.JDataContainer;

/**
 * @overview
 *  Represents the controller of the help module.
 *   
 * @author dmle
 *
 */
public class HelpController extends TaskController<AppHelp> {
  
  private ApplicationModule activeModule;
  private JDataContainer<Page> outputView;
  
  public HelpController(DODMBasic schema, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module, moduleGui, parent, config);
  }
  
  @Override
  public void preRun() throws ApplicationRuntimeException {
    super.preRun();
    
    // get the active module 
    View activeGUI = getMainController().getActiveGUI();
    
    if (activeGUI == null) { 
      throw new NotPossibleException(NotPossibleException.Code.NO_ACTIVE_DATA_CONTAINER);
    }
    
    ApplicationModule newModule = activeGUI.getController().getApplicationModule();
    
    // use the current active container if the new one is NOT the same as the root panel 
    // (could be the same if the user clicked Update) 
    if (newModule != activeModule) {
      activeModule = newModule;
    }
  }
  
  @Override
  public void doTask(AppHelp doc) throws ApplicationRuntimeException,
      DataSourceException {
    System.out.println(HelpController.class);
    
//    // clear doc and GUI (if doc has been created)
//    if (!doc.isEmpty()) {
//      doc.clear();
//    }
    
    // TODO: do these on the object form
//    String docName = activeDataContainer.getController().getCreator().getName();
//    String docTitle = null;
//    doc.setName(docName);
//    doc.setDocTitle(docTitle);
//
//    // create a document builder
//    //TODO: allow user to choose this on the object form (above)
//    Class<? extends DocumentBuilder> builderType = DefaultHtmlDocumentBuilder.class;
//
//    DocumentBuilder docBuilder = DocumentBuilder.createDocumentBuilderInstance(builderType, 
//        this, 
//        getConfig());
//    
//    docBuilder.init(doc);
//    
//    PropertySet printCfg = getContainerPrintCfg(activeDataContainer);
//    
//    // generate document content
//    docBuilder.buildContent(getDodm(), activeDataContainer, printCfg, doc);
//    
//    docBuilder.finalise(doc);
//
//    docBuilder.close();
    
    // display document content
//    displayResult(doc);
    
    // update size to fit the content
    getGUI().updateSizeOnComponentChange();
  }
  
//  private void displayResult(DataDocument doc) throws ApplicationRuntimeException, DataSourceException {
//    Class<DataDocument> docCls = getDomainClass();
//    
//    // clear existing query result first
//    clearChildDataController(outputView.getController());
//
//    if (!doc.isEmpty()) {
//      // display result
//      DataController<Page> outputDCtl = outputView.getController();
//      
//      activateDataContainer(outputView);
//      showDataContainer(outputView);
//      
//      Collection<Page> pages = doc.getPages();
//      
//      outputDCtl.openObjects(pages);
//      
//      showAutoChildDataContainer(docCls, outputView);
//
//      // v2.7.2: update GUI size to best fit the result
//      //getGUI().updateSizeOnComponentChange();      
//    } else {
//      throw new NotPossibleException(NotPossibleException.Code.DATA_DOCUMENT_EMPTY);
//    }
//  }
  

}
