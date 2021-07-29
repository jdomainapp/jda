package jda.modules.exportdoc.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.text.JTextComponent;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.tree.Node;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.exportdoc.controller.html.DefaultHtmlDocumentBuilder;
import jda.modules.exportdoc.model.DataDocument;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.RunComponent;
import jda.mosa.controller.assets.composite.TaskController;
import jda.mosa.controller.assets.util.MethodName;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.JDataField;
import jda.util.properties.PropertySet;

/**
 * @overview
 *  Represents the controller of the document export module.
 *   
 * @author dmle
 *
 */
public class DocumentExportController<C> extends TaskController<DataDocument> {
  
  // v3.0: private JDataContainer activeDataContainer;
  private JDataContainer<Page> outputView;
  
  public DocumentExportController(DODMBasic schema, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module, moduleGui, parent, config);
  }
  
  // v3.0: added to simplify run tree
  @Override
  protected void initRunTree() throws NotPossibleException {
    setRestartPolicy(
        RestartPolicy.None
        );
    
    setProperty("show.message.popup", Boolean.FALSE);
    DataController dctl = getRootDataController();    
    //final Class reportClass = Report.class;
    //final Class domainClass = getDomainClass();
    
    // add a node that runs once for all the subsequent runs of
    // this controller to initialise the resources
    RunComponent comp;
    Node n;

// v3.0: moved out to DataController.export
//    // v2.7.4: init node
//    comp = new RunComponent(this, MethodName.init.name(), null);
//    n = init(comp);

    // preparation node
    comp = new RunComponent(this, MethodName.preRun.name(), null);
    n = init(comp);//add(comp,n);
    
    // show GUI node
    comp = new RunComponent(this, MethodName.showGUI.name(), null);
    n = add(comp,n);

    /* no need for these steps as DataDocument object is created before running this
     *  
    // add create new object component (run once) 
    comp = new RunComponent(dctl,MethodName.newObject.name(),
        null);
    comp.setSingleRun(true);
    add(comp, n);

    // add a component to create object once (first-time) 
    //comp = new RunComponent(dctl, AppState.Created, AppState.Updated);
    comp = new RunComponent(dctl, AppState.Created, MethodName.createObject.name(), null);
    comp.setSingleRun(true);
    Node m1 = add(comp, n);
     */
    
    // add a node to obtain the created object
    comp = new RunComponent(dctl,
        MethodName.getCurrentObject.name(),
        null);
    //v3.0: refreshStartNode = add(comp,m1);
    refreshStartNode = add(comp,n);

    // add a node to do the rest of the report
    comp = new RunComponent(this, MethodName.doTask.name(), 
        new Class[] { Object.class });
    refreshStopNode = add(comp,refreshStartNode);

//    // reset when finished
//    comp = new RunComponent(this, MethodName.resetTree.name(), null);
//    add(comp,refreshStartNode);
  }
  
  @Override
  public void preRun() throws ApplicationRuntimeException {
    super.preRun();

// v3.0: moved to DataDocument
//    // get the active data container 
//    JDataContainer newContainer = getActiveDataContainer();
//    if (newContainer == null) { 
//      throw new NotPossibleException(NotPossibleException.Code.NO_ACTIVE_DATA_CONTAINER);
//    }
    
    if (outputView == null) {
      Class<DataDocument> ParentCls = getDomainClass();
      Class<Page> ChildCls = Page.class;

      outputView = getChildDataContainer(ParentCls, ChildCls);
    }

    // v3.0
//    // use the current active container if the new one is NOT the same as the root panel 
//    // (could be the same if the user clicked Update) 
//    if (newContainer != outputView && newContainer != activeDataContainer) {
//      activeDataContainer = newContainer;
//    }
  }
  
  @Override
  public void doTask(DataDocument doc) throws ApplicationRuntimeException,
      DataSourceException {
    
    // clear doc and GUI (if doc has been created)
    if (!doc.isEmpty()) {
      doc.clear();
    }
    
    // v3.0: 
    JDataContainer activeDataContainer = doc.getDataContainer();
    
    // v2.7.4
    PropertySet printCfg = getContainerPrintCfg(activeDataContainer);

    // v2.7.4 make sure that all resources associated to printCfg are prepared
    preTask(printCfg);
    
// v3.0: moved out of this     
//    // TODO: do these on the object form
//    String docName = activeDataContainer.getController().getCreator().getName();
//    String docTitle = null;
//    doc.setName(docName);
//    doc.setDocTitle(docTitle);

    // create a document builder
    //TODO: allow user to choose this on the object form (above)
    /*v2.7.4: read from print config
    Class<? extends DocumentBuilder> builderType = DefaultHtmlDocumentBuilder.class;
    */
    Class<? extends DocumentBuilder> builderType = null;
    if (printCfg != null) {
      builderType = printCfg.getPropertyValue("docBuilderType", 
        Class.class);
    }

    if (builderType == null) {
      builderType = DefaultHtmlDocumentBuilder.class;
    }
    
    DocumentBuilder docBuilder = DocumentBuilder.createDocumentBuilderInstance(builderType, 
        this, 
        getConfig());
    
    docBuilder.init(doc, printCfg);
    
    /*v2.7.4: moved to above 
    PropertySet printCfg = getContainerPrintCfg(activeDataContainer);
    */
    
    // generate document content
    docBuilder.buildContent(getDodm(), activeDataContainer, printCfg, doc);
    
    docBuilder.finalise(doc);

    docBuilder.close();
    
    // display document content
    displayResult(doc);
    
    // update size to fit the content
    getGUI().updateSizeOnComponentChange();
  }
  
  /**
   * @effects 
   *  make sure that all resources associated to <tt>printCfg</tt> are prepared
   */
  private void preTask(PropertySet printCfg) {
    if (printCfg != null) {
      Class docDataCls = printCfg.getPropertyValue("docDataClass", Class.class);
      if (docDataCls != null) {
        // register it if not already
        DODMBasic dodm = getDodm();
        if (!dodm.isRegistered(docDataCls)) {
          dodm.registerClass(docDataCls);
        }
      }
    }
  }

  private void displayResult(DataDocument doc) throws ApplicationRuntimeException, DataSourceException {
    Class<DataDocument> docCls = getDomainClass();
    
    // clear existing query result first
    clearChildDataController(outputView.getController());

    if (!doc.isEmpty()) {
      // display result
      DataController<Page> outputDCtl = outputView.getController();
      
      activateDataContainer(outputView);
      showDataContainer(outputView);
      
      Collection<Page> pages = doc.getPages();
      
      outputDCtl.openObjects(pages, false);
      
      showAutoChildDataContainer(docCls, outputView);

      // v2.7.2: update GUI size to best fit the result
      //getGUI().updateSizeOnComponentChange();      
    } else {
      throw new NotPossibleException(NotPossibleException.Code.DATA_DOCUMENT_EMPTY);
    }
  }
  
//  /**
//   * @modifies this
//   * 
//   * @effects <pre> 
//   *  create a {@link DocumentBuilder} whose type is docType containing 
//   *    the data values of the data fields of <tt>activeDataContainer</tt>
//   *    and recursively in its sub-containers (if any)
//   *  
//   *  sets the document into the root data controller of this. </pre>
//   */
//  public void runExport(JDataContainer activeDataContainer) throws NotPossibleException {
// 
//  }
  
//  /**
//   * @effects <pre>
//   *  if the document of the root data controller of this is not null
//   *    store its content to a designated folder
//   *  else 
//   *    do nothing</pre>
//   */
//  public void save() {
//    DocumentBuilder doc = getRootDataController().getCurrentObject();
//
//    if (doc != null) {
//      String file = doc.getDocTitle();
//      
//      doc.save(file);
//    }
//  }

  /**
   * @effects 
   *  return the data component of the root data container of this that is responsible for displaying printable content 
   */
  public JTextComponent getPrintableComponent() {
    DataController dctl = getRootDataController();
    // the root panel
    JDataContainer rootCont = dctl.getDataContainer(); 
    
    /* v3.2c: use shared method
    // the page panel
    Iterator<JDataContainer> childCont = rootCont.getChildContainerIterator(); 
    JDataContainer pageCont = childCont.next();
    
    // the html field that displays the page
    JDataField textComp = (JDataField) pageCont.getComponents(null)[0];
    
    return (JTextComponent)textComp.getGUIComponent();
    */
    return getPrintableComponent(rootCont);
  }

  /**
   * @effects 
   *  if <tt>container</tt> has printable content
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2c
   */
  public boolean hasPrintableContent(JDataContainer container) {
    return Page.class.isAssignableFrom(container.getController().getDomainClass());
  }
  
  /**
   * @requires 
   *  {@link #hasPrintableContent(JDataContainer)}(<tt>container</tt>) = <tt>true</tt> (i.e. has printable content component)
   *  
   * @effects 
   *  return the data component of the <tt>container</tt> that is responsible for displaying printable content
   * @version 3.2c  
   */
  public JTextComponent getPrintableComponent(JDataContainer container) {
    JDataContainer printableContainer; 
    
    // the container containing the printable component: either the first child container (if container is nested) or the container itself
    if (container.getController().isNested()) {
      Iterator<JDataContainer> childCont = container.getChildContainerIterator(); 
      printableContainer = childCont.next();
    } else {
      printableContainer = container;
    }
    
    // the printable component
    JDataField textComp = (JDataField) printableContainer.getComponents(null)[0];
    
    return (JTextComponent)textComp.getGUIComponent();
  }

  /**
   * @effects 
   *  if there is no print configuration defined for <tt>dcont</tt>
   *    return null
   *  else
   *    return it 
   */
  public PropertySet getContainerPrintCfg(JDataContainer dcont) {
    PropertySet printCfg = dcont.getContainerPrintConfig();
    return printCfg;
  }

  /**
   * @effects 
   *  if attribConfig represents that of a sub-data container of <tt>container</tt>
   *    return true
   *  else
   *    return false
   */
  public boolean isDataContainer(JDataContainer container, Region attribConfig) {
    return JDataContainer.class.isAssignableFrom(attribConfig.getDisplayClassType());
  }

  /**
   * @requires 
   *  <tt>config</tt> is the configuration of a sub data container of <tt>container</tt>
   * @effects 
   *  return the child <tt>JDataContainer</tt> of <tt>container</tt> whose view configuration is <tt>config</tt>.
   *  <br>If for some reasons this container is not found, return <tt>null</tt> 
   */
  public JDataContainer getDataContainer(JDataContainer container, Region config) {
    Iterator<JDataContainer> childContainers = container.getChildContainerIterator();
    if (childContainers != null) {
      JDataContainer child;
      while (childContainers.hasNext()) {
        child = childContainers.next();
        if (child.getContainerConfig() == config) {
          return child;
        }
      }
    }
    
    // not found (should not happen)
    return null;
  }

  /**
   * @effects 
   *  return a <tt>Collection</tt> of the domain object(s) that are 
   *  linked to <tt>obj</tt> via a domain attribute <tt>a</tt> of <tt>o.class</tt>
   *  and that are displayed by <tt>containerOfLinkAttrib</tt>.
   *  
   *  <p>Return <tt>null</tt> if no such objects are found.
   */
  public Collection getAssociateObjects(Object obj, JDataContainer containerOfLinkAttrib) {
    Collection linkedObjs = null;
    DODMBasic schema = getDodm();
    Object assocObj = schema.getDsm().getAttributeValue(obj, 
        containerOfLinkAttrib.getController().getLinkAttributeOfParent().name());
    if (assocObj != null) {
      if (assocObj instanceof Collection) {
        linkedObjs = (Collection) assocObj;
      } else {
        linkedObjs = new ArrayList();
        linkedObjs.add(assocObj);
      }
    }
    
    return linkedObjs;
  }
  
  /**
   * @effects 
   *  return a <tt>Collection</tt> of the domain object(s) that are 
   *  linked to <tt>obj</tt> via the domain attribute <tt>attrib</tt> of <tt>o.class</tt>
   *  
   *  <p>Return <tt>null</tt> if no such objects are found.
   */
  public Collection getAssociateObjects(Object obj, DAttr attrib) {
    Collection linkedObjs = null;
    DODMBasic schema = getDodm();
    Object assocObj = schema.getDsm().getAttributeValue(obj, attrib.name());
    if (assocObj != null) {
      if (assocObj instanceof Collection) {
        linkedObjs = (Collection) assocObj;
      } else {
        linkedObjs = new ArrayList();
        linkedObjs.add(assocObj);
      }
    }
    
    return linkedObjs;
  }

//  /**
//   * @effects <pre>
//   *  if the document of the root data controller of this is not null
//   *  AND this.gui is not null
//   *    show its content on this.gui
//   *  else 
//   *    do nothing</pre>
//   *  
//   */
//  public void viewDocument() {
//    if (hasGUI()) {
//      DataController dctl = getRootDataController();
//      dctl.updateGUI();
//    }
//  }
  
//  public void runImport() {
//    //TODO
//  }
}
