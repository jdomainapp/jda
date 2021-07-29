package jda.modules.iexport.controller;

import java.io.File;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.tree.Node;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.CsvOSM;
import jda.modules.dodm.osm.OSM;
import jda.modules.dodm.osm.OSMFactory;
import jda.modules.iexport.model.DomainClassType;
import jda.modules.iexport.model.Import;
import jda.modules.iexport.model.dodm.OSMType;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.CompositeController;
import jda.mosa.controller.assets.composite.RunComponent;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.controller.assets.util.MethodName;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;
import jda.util.events.ChangeListener;

public class ImportController extends CompositeController<Import> implements ChangeListener {

  public ImportController(DODMBasic dodm, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config)
      throws NotPossibleException {
    super(dodm, module, moduleGui, parent, config);
  }

  @Override
  protected void initModule() throws NotPossibleException {
    super.initModule();
    
    DODMBasic dodm = getDodm();
    
    try {
      // load OSMTypes
      dodm.addClass(OSMType.class);
      dodm.addConstantObjects(OSMType.class);
      
      // register DomainClassType
      dodm.addClass(DomainClassType.class);
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e);
    }
    
    // register this to listen to class-registration event
    dodm.getDsm().addChangeListener(LAName.RegisterClass, this);
  }

  @Override
  protected void initRunTree() throws NotPossibleException {
    setRestartPolicy(
        RestartPolicy.Node
        );
    
    setProperty("show.message.popup", Boolean.FALSE);
    DataController dctl = getRootDataController();    
    //final Class reportClass = Report.class;
    final Class domainClass = getDomainClass();
    
    // add a node that runs once for all the subsequent runs of
    // this controller to initialise the resources
    RunComponent comp;
    Node n;
    
    // show GUI node
    comp = new RunComponent(this, MethodName.showGUI.name(), null);
    //n = add(comp,n);
    n = init(comp);

    // add create new object component (run once) 
    comp = new RunComponent(dctl,MethodName.newObject.name(),
        null);
    comp.setSingleRun(true);
    add(comp, n);

    // add a component to wait for the user to create or update object
    // this is repeated for each run
    comp = new RunComponent(dctl, AppState.Created, AppState.Updated);
    Node m1 = add(comp, n);

//    // use this to clear the children (if any)
//    comp = new RunComponent(dctl,
//        MethodName.clearChildren.name(),
//        null);
//    add(comp,m1);

    // add a node to obtain the created object
    comp = new RunComponent(dctl,
        MethodName.getCurrentObject.name(),
        null);
    Node refreshStartNode = add(comp,m1);

    // add a node to do the rest of the report
    comp = new RunComponent(this, MethodName.doTask.name(), 
        new Class[] {domainClass});
    Node refreshStopNode = add(comp,refreshStartNode);

    // restart when finished 
    comp = new RunComponent(this, MethodName.restart.name(), null);
    add(comp,refreshStartNode);
  }
  
//  /**
//   * @effects 
//   * clear the current report object and re-run it
//   */
//  @Override
//  public void refresh() {
//    // runs the sub-tree of the execution tree that is concerned with
//    // running the report. 
//    // IMPORTANT: this sub-tree must not include the final "restart" Node.
//    if (refreshStartNode != null && refreshStopNode != null)
//      runASubTree(refreshStartNode, refreshStopNode);
//  }
  
  @Override
  protected void onTerminateRunOnError() {
    // reset the tree nodes to make them ready for next execution and restart them
    // this is needed for the tree to be executed again when user clicks the "Update" button 
    // (without using "Refresh")
    // restart will stop at the node that waits for the user to either hit the Update or Create button
    // this restarting happens in the background without the user having to know
    super.restart();
  }
  
  /**
   * @effects 
   *  perform the import using the configuration specified in <tt>iport</tt>
   */
  public void doTask(Import iport) throws ApplicationRuntimeException, DataSourceException {
    
    OSMType osmType = iport.getOsmType();
    Class osmCls = osmType.getCls();
    DomainClassType clsType  = iport.getDomainClass();
    Class domainCls = clsType.getCls();
    
    String domainClsLabel = ControllerBasic.getDomainClassLabel(domainCls);
    
    DataController myDctl = getRootDataController();
    
    // confirm with user
    boolean confirmed = displayConfirmFromCode(MessageCode.CONFIRM_IMPORT_OBJECTS, 
        myDctl, 
        domainClsLabel, 
        osmType.getClassLabel());
    
    if (!confirmed)
      return;
    
    // make sure to load the object pool metadata of the domain class
    ControllerBasic targetCtl = lookUp(domainCls);
    if (targetCtl == null)
      throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND, domainClsLabel);
    
    targetCtl.getRootDataController().openMetadata();
    
    DODMBasic dodm = getDodm();
    DOMBasic dom = dodm.getDom();
    Configuration config = getConfig();
    
    // obtain (create if necessary) an OSM instance of the specified type
    if (osmCls != CsvOSM.class) {
      //TODO: support other osmtypes
      throw new NotImplementedException(
          NotImplementedException.Code.OSM_Type_Not_Supported, osmCls.getSimpleName());
    }
    
    // for now: support Csv only 
    // create url = import-folder + cls name + .csv
    final String SEP = System.getProperty("file.separator");
    String importFolderPath = config.getImportFolderPath(); //config.getAppFolder() + SEP + config.getImportFolder();
    File importFolder = new File(importFolderPath);
    if (!importFolder.exists()) {
      throw new NotFoundException(NotFoundException.Code.FOLDER_NOT_FOUND, importFolderPath);
    }
    
    String fileName = domainCls.getSimpleName() + ".csv";
    String importFilePath = importFolderPath + SEP + fileName;
    File importFile = new File(importFilePath);
    if (!importFile.exists()) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, importFilePath);
    }
    
    OsmConfig osmCfg = OSMFactory.getStandardOsmConfig("csv", importFilePath);

    //osmCfg.setDataSourcePath(importFilePath);
    
    // clone the configuration with the OsmConfig
    OSM osm = OSMFactory.getOsmInstance(osmCls, osmCfg, dom);
    
    // connect to the data source
    osm.connect();
    
    // import object records of the specified domain class
    dom.importObjects(osm, domainCls);
    
    // inform user
    displayMessageFromCode(MessageCode.OBJECTS_IMPORTED, this.getRootDataController(), 
        domainClsLabel);
  }
  
  @Override // ChangeListener
  public void stateChanged(ChangeEvent e) {
    // this is fired when a domain class has been registered into the system (see preRun above)
    ChangeEventSource src = (ChangeEventSource) e.getSource();
    Class c = src.getDomainClass();
    
    registerSuitableDomainClass(c);
  }

  /**
   * @effects 
   *  if the domain class <tt>c</tt> is is suitable for import
   *    register it for import
   *  else
   *    do nothing
   */
  private void registerSuitableDomainClass(Class c) {
    DODMBasic dodm = getDodm();

    try {
      // create a domain class type and register it
      DOMBasic dom = dodm.getDom();
      
      if (!isSuitableForImport(c))
        return;
      
      //ControllerBasic mainCtl  = getMainController();
      
      DomainClassType dst;
      
      dst = new DomainClassType(c, ControllerBasic.getDomainClassLabel(c));
      dom.addObject(dst);
    } catch (DataSourceException ex) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, ex);
    }    
  }

  private boolean isSuitableForImport(Class c) {
    DSMBasic dodm = getDomainSchema();

    // ignore if c is the domain class of this module (possible b/c domain class
    // is registered after initModule is invoked)
    if (c == Import.class)
      return false;
    
    // ignore if c is abstract
    if (DSMBasic.isAbstract(c)) {
      return false;
    }
    
    // c is ok
    return true;
  }
}
