package jda.modules.lang.controller;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import jda.modules.common.Toolkit;
import jda.modules.common.concurrency.Task;
import jda.modules.common.concurrency.TaskManager;
import jda.modules.common.concurrency.Task.TaskName;
import jda.modules.common.exceptions.ApplicationException;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.InfoCode;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.setup.init.RegionConstants;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.view.View;
import jda.util.ObjectComparator;
import jda.util.ObjectComparator.SortBy;
import jda.util.events.StateChangeListener;

/**
 * @overview
 *  A controller responsible for changing the interface language of 
 *  the application. Unlike other controllers, this controller does not 
 *  manipulate data objects as such. It only performs functions. This is why
 *  this controller's domain class is null. In addition, it is not associated
 *  to an <tt>AppGUI</tt> object. For more information about this controller, 
 *  refer to the associated <tt>ModuleDescriptor</tt>.
 *  
 *  <p>It implements <tt>StateChangeListener</tt> to listen for 
 *  the event that the user has changed the language of the application. 
 *  This is typically performed on the <tt>Configuration</tt> GUI.
 *  
 * @author dmle
 */
public class LanguageConfigurator<C> extends ControllerBasic<C> 
implements StateChangeListener {

  // v3.1: changed to Tasks
  private ChangeGUILanguageTask taskChangeGUILanguage;
  private UpdateRegionsTask taskUpdateRegions;
  
  /*v3.1: use TaskManager
  private ExecutorService exec;
  */
  private TaskManager taskMan;
  
  private static final AppState[] STATES = {
    AppState.Updated,
  } ;
  
  /** debug for each parameterised <tt>Controller</tt> class */
  protected static final boolean debug = Toolkit
      .getDebug(LanguageConfigurator.class);

  // constructor
  public LanguageConfigurator(DODMBasic schema, ApplicationModule module,
      Region moduleGui, final ControllerBasic parent, Configuration config)
      throws NotPossibleException {
    super(schema, module, moduleGui, parent, config);
    
    taskChangeGUILanguage = new ChangeGUILanguageTask(TaskName.ChangeGUILanguage);
    taskUpdateRegions = new UpdateRegionsTask(TaskName.UpdateRegionsOnLanguageChange);
    
    // v3.1:  exec = Executors.newFixedThreadPool(2);
    taskMan = new TaskManager();
    taskMan.registerTask(taskChangeGUILanguage);
    taskMan.registerTask(taskUpdateRegions);
  }

  /**
   * Override super's method to change application interface language.
   * This method is invoked by {@link #stateChanged(Object, domainapp.basics.core.ControllerBasic.AppState, String)}
   * when an event was detected about the user changing the system-wide language.  
   */
  @Override
  public void run() {
    // v3.0: only run if internationalisation support is enabled
    boolean supportLang = getMainController().isSupportInternalisation();
    if (!supportLang)
      return;
    
    final Configuration config = getConfig();
    String langCode = config.getLanguage().getLanguageCode();
    final DODMBasic dodm = getDodm();
    DOMBasic dom = dodm.getDom();
    
    boolean serialisedConfig = true;

    final ControllerBasic mainCtl = getMainController();
    
    //if (debug)
      System.out.println("LanguageConfigurator: changing language to " + langCode);

    /**
     * reload all the component labels/titles of the new language and refresh
     * the GUI objects.
     * 
     */
    // reload the text/label of all the existing GUI components of
    // all the controllers

    /** reload the labels of the existing GUI regions */
    // determine the label class name from the language
    langCode = langCode.toLowerCase();
    Class labelClass;
    try {
      String pkgPrefix = RegionConstants.class.getPackage().getName()+".lang."+langCode+".";
      String lblClassName = pkgPrefix+"Label";
      labelClass = Class.forName(lblClassName);
    } catch (ClassNotFoundException e) {
      // throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e,  
      //    "Không tìm thấy lớp nhãn của ngôn ngữ " + lang);
      mainCtl.displayErrorFromCode(MessageCode.ERROR_NO_LANGUAGE_CONFIGURATION_FOUND, 
          e, langCode);
      return;
    }

    // get the new labels
    Collection<Label> newLabels = null;
    try {
      newLabels = dom.retrieveObjectsWithAssociations(labelClass);
      
      if (newLabels.isEmpty()) {
        if (debug)
          System.out.printf("LangugeConfigurator.run: setting new labels...%n");
        // may be the labels have not been set up 
        // try to set up 
        setUpLabels(labelClass);
        newLabels = dom.retrieveObjectsWithAssociations(labelClass);
      }
        
      if (newLabels.isEmpty())
        mainCtl.displayErrorFromCode(MessageCode.ERROR_NO_LABELS_FOUND, langCode);
    } catch (ApplicationException | ApplicationRuntimeException e) {
      InfoCode code = (e instanceof ApplicationException) ? 
          ((ApplicationException)e).getCode() : ((ApplicationRuntimeException)e).getCode();
      mainCtl.displayError(code, e);
      return;
    }

    // retrieve the existing regions
    Collection<Region> regions = dom.getObjects(Region.class);
    
    Label label, domainClsLabel;
    int labelId, domainClsLabelId;
    boolean labelFound, domainClsLabelFound;
    boolean proceedWithUpdate = true;
    RegionGui regionGui;
    
    // replace the label of each existing region by the corresponding new label (i.e.
    // have the same type-id)
    OUTER: for (Region region : regions) {
      label = region.getLabel();
      
      if (label != null) {
        // update label
        labelId = label.getTypeId();
        labelFound = false; // a flag to test
        
        for (Label nl : newLabels) {
          if (nl.getTypeId() == labelId) {
            // found
            labelFound = true;
            
            // v3.0: update label with style (if needed)
            if (nl.getStyle() == null) {
              // new labels with no styles -> update style
              nl.setStyle(label.getStyle());
              try {
                dom.updateObject(nl, null, serialisedConfig);
              } catch (Exception e) {
                displayErrorFromCode(MessageCode.ERROR_UPDATE_LABEL, getRootDataController(), e, new Object[] {nl});
                proceedWithUpdate = false;
                break OUTER;
              }
            }
            
            region.setLabel(nl);
            break;
          }
        } // end inner loop: newlabels
        
        if (!labelFound) {
          // something internally wrong: no matching label found for this region
          displayErrorFromCode(MessageCode.ERROR_NO_MATCHING_REGION_LABEL_FOUND, 
              getRootDataController(), new Object[] {langCode, region, label});
          proceedWithUpdate = false;
          break OUTER;
        }          
      } // end if: update label
      
      // v3.0: support domain class label
      if (region instanceof RegionGui) {
        // update domain class label
        regionGui = ((RegionGui) region);
        domainClsLabel = regionGui.getDomainClassLabel();
        
        if (domainClsLabel != null) {
          domainClsLabelFound = false;
          domainClsLabelId = domainClsLabel.getTypeId();
          for (Label nl : newLabels) {
            if (nl.getTypeId() == domainClsLabelId) {
              // found
              domainClsLabelFound = true;
              
              // v3.0: update label with style (if needed)
              if (nl.getStyle() == null) {
                // new labels with no styles -> update style
                nl.setStyle(domainClsLabel.getStyle());
                try {
                  dom.updateObject(nl, null, serialisedConfig);
                } catch (Exception e) {
                  displayErrorFromCode(MessageCode.ERROR_UPDATE_LABEL, getRootDataController(), e, new Object[] {nl});
                  proceedWithUpdate = false;
                  break OUTER;
                }
              }
              
              regionGui.setDomainClassLabel(nl);
              break;
            }
          } // end inner loop: newlabels
          
          if (!domainClsLabelFound) {
            // something internally wrong: no matching label found for this region
            displayErrorFromCode(MessageCode.ERROR_NO_MATCHING_REGION_LABEL_FOUND, 
                getRootDataController(), new Object[] {langCode, region, domainClsLabel});
            proceedWithUpdate = false;
            break OUTER;
          }   
        }
      } // end regionGui case
    } // end outer loop: regions

    if (proceedWithUpdate) {    // v3.0: added this check
      // run two threads: 
      // one to update the GUI using the (updated) region objects
      // and the other is to save the region objects to the database
      
      //v3.1: use tasks
      // runnableChangeGUILanguage.run(mainCtl);
      
      // for each task, if it is still running (e.g. from a previous run) then wait before executing
      // Task 1: change GUI language
      if (taskMan.isRunning(taskChangeGUILanguage)) {
        boolean stopped = taskMan.waitFor(taskChangeGUILanguage, ChangeGUILanguageTask.MAX_RUN_TIME);
        if (!stopped) {
          // something wrong, task is till running (e.g. a dead-lock occurs)
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WAIT_FOR_TASK, 
              new Object[] {taskChangeGUILanguage.getName().name()});
        }
      }
      
      taskChangeGUILanguage.setParameters(mainCtl, regions);
      taskMan.run(taskChangeGUILanguage);

      // Task 2: update regions
      if (taskMan.isRunning(taskUpdateRegions)) {
        boolean stopped = taskMan.waitFor(taskUpdateRegions, UpdateRegionsTask.MAX_RUN_TIME);
        if (!stopped) {
          // something wrong, task is till running (e.g. a dead-lock occurs)
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WAIT_FOR_TASK, 
              new Object[] {taskUpdateRegions.getName().name()});
        }
      }
      taskUpdateRegions.setParameters(dodm, regions);
      taskMan.run(taskUpdateRegions);
    }
  }
  
  /**
   * @effects 
   *  load the pre-define label objects of <tt>labelClass</tt>
   *  and store them into the database.
   */
  private void setUpLabels(Class labelClass) throws NotFoundException, NotPossibleException {
    // we need to get labels of the built-in regions 
    // as well as those of the domain classes
    Configuration config = getConfig();
    
    /***
     * Built-in labels
     */
    String pkgPrefix = labelClass.getPackage().getName() + ".";
    Class labelConstantsClass;
    try {
      String lblConstantsClassName = pkgPrefix+"LabelConstants";
      labelConstantsClass = Class.forName(lblConstantsClassName);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e,  
          "Không tìm thấy cấu hình các nhãn của ngôn ngữ");      
    }
    
    Map<String,Object> labels = Toolkit.getConstantObjectsAsMap(
        labelConstantsClass, labelClass);
    
    if (labels == null) {      
      return;
    }
    
    DOMBasic dom = getDodm().getDom();

    if (debug)
      System.out.println("Tổng cộng: " + labels.size() + " (nhãn hệ thống)");
    
    try {
      for (Object label : labels.values()) {
        dom.addObject(label);
      }
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, 
          new Object[] {"Không thể lưu nhãn vào cơ sở dữ liệu"}); 
    }
    
    /**
     * Domain labels
     */
    Map<String,Label> domainLabels = getDomainLabels(config);
    
    if (domainLabels != null) {
      if (debug)
        System.out.println("Tổng cộng: " + domainLabels.size() + " (nhãn ứng dụng)");
      
      try {
        for (Label label : domainLabels.values()) {
          dom.addObject(label);
        }
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, 
            "Không thể lưu nhãn vào cơ sở dữ liệu",e); 
      }
    }
  }

  /**
   * @effects 
   *  load from config files the domain-specific labels of all application modules 
   *  for the configured language  
   *    and create them in the database
   * @version 3.0
   */
  private Map<String,Label> getDomainLabels(Configuration config) throws 
      NotFoundException {
    //DODMBasic dodm = getDodm();
    DSMBasic dsm = getDomainSchema();
    
    // get application module names
    // sort the modules by id (because this is the order that they were created 
    //  and thus the order in which the labels were applied)
    // 
    DAttr attribModuleId = dsm.getDomainConstraint(ApplicationModule.class, ApplicationModule.AttributeName_id);
    ObjectComparator sorter = new ObjectComparator(dsm, attribModuleId, SortBy.ASC);
    Collection<ApplicationModule> modules = config.getModules(sorter);
    
    // get the configured language
    Language lang = config.getLanguage();
    String langCode = lang.getLanguageCode();
    
    // the label class is a sub-type of Label.class for the specified language code
    Class labelCls;
    //TODO: improved to support other languages
    if (lang == Language.Vietnamese) {
      labelCls = jda.modules.setup.init.lang.vi.Label.class;
    } else if (lang == Language.English) {
      labelCls = jda.modules.setup.init.lang.en.Label.class;
    } else
      throw new NotImplementedException("Label not supported for language: {0}", langCode);

    // load the property files of the modules and create labels for them
    String moduleName;
    String propFilePath;
    Properties labelProps;
    Map<String,Label> labelMap = new LinkedHashMap();
    Enumeration<String> propNames;
    String propName, propVal, labelName, labelNamePrefix ;
    Label label;
    
    for (ApplicationModule module : modules) {
      moduleName = module.getName();
      propFilePath = config.getAppSubDirPath(
          config.getConfigFolder(),
          "modules", moduleName,
          "resources", "lang", langCode, "Labels.properties"
          );
      labelProps = ToolkitIO.readPropertyFile(propFilePath, "utf-8");
      if (labelProps != null) {
        if (debug)
          System.out.printf(" loaded labels:  %s%n", propFilePath);

        // has properties
        // has labels 
        propNames = (Enumeration<String>) labelProps.propertyNames();
        labelNamePrefix = moduleName + "_"; // e.g: ModuleStudent_
        while (propNames.hasMoreElements()) {
          propName = propNames.nextElement();
          propVal = labelProps.getProperty(propName);
          
          labelName = labelNamePrefix + propName;
          
          // create label object based on language code
          label = Label.createInstance(labelCls, propVal);
          
          labelMap.put(labelName, label);
        }
      }
    }
    
    if (labelMap.isEmpty())
      throw new NotFoundException(NotFoundException.Code.LABELS_NOT_FOUND);
    
    return labelMap;
  }
  
  /**
   * @version 3.1
   */
  @Override // ControllerBasic
  public void close() throws Exception {
    super.close();
      
    // stop task man
    int waitTime = 5; // secs
    taskMan.waitForAll(waitTime);
  }

  @Override
  public void stateChanged(Object src,
      jda.mosa.controller.assets.util.AppState state, String messages,Object...data) {
    // only interested if source is configuration controller
    if (src instanceof ControllerBasic.DataController) {
      DataController srcCtl = (DataController) src;
      final Class cls = Configuration.class;
      if (srcCtl.getCreator().isDomainClassType(cls)) {
        // check that language was changed
        boolean langChanged = false;
        if (data.length > 0) {
          Object stateData = data[0];
          if (stateData != null && stateData instanceof Map) {
            // a map of the attribute values that were changed
            // check that the language attribute was one of these
            Map oldVals = (Map) stateData;
            DODMBasic schema = getDodm();
            String attributeName = Configuration.AttributeName.language.name();
            DAttr dc = schema.getDsm().getDomainConstraint(cls, attributeName);
            if (oldVals.containsKey(dc)) {
              // indeed
              langChanged = true;
            }
          }
        }
        
        if (langChanged)
          run();
      }
    }
  }

  @Override
  public jda.mosa.controller.assets.util.AppState[] getStates() {
    return STATES;
  }

  /**
   * @overview  
   *  Store updates to each {@link Region} to data source
   *  
   * @author dmle
   *
   */
  private class UpdateRegionsTask 
  //v3.1: implements Runnable 
  extends Task 
  {
    public static final int MAX_RUN_TIME = 60000; // millis

    public UpdateRegionsTask(TaskName name) {
      super(name);
    }

    private Collection<Region> regions;
    private DODMBasic dodm;

    void setParameters(DODMBasic dodm, Collection<Region> regions) {
      this.regions = regions;
      this.dodm = dodm;
      //v3.1: exec.execute(this);
    }

    public void run() {
      // v3.1
      setIsStopped(false);
      
      DOMBasic dom = dodm.getDom();
      for (Region region : regions) {
        try {
          dom.updateObject(region, null);
        } catch (Exception e) {
          logError(e + ": " + e.getMessage(), null);
        }
      }
      
      // v3.1
      setIsStopped(true);
    }
  } // end UpdateRegionsRunnable

  /**
   * @overview  
   *  Update labels of each created functional {@link View}
   *  
   * @author dmle
   *
   */
  private class ChangeGUILanguageTask 
  //v3.1: implements Runnable 
  extends Task
  {
    
    public static final int MAX_RUN_TIME = 60000;  // millis

    public ChangeGUILanguageTask(TaskName name) {
      super(name);
    }

    private ControllerBasic mainCtl;
    private Collection<Region> regions;
    
    // v3.1: support attribute name, label mappings
    // update the existing attribute name, label mappings
    void setParameters(ControllerBasic mainCtl, Collection<Region> regions) {
      this.mainCtl = mainCtl;
      this.regions = regions;
      // v3.1: exec.execute(this);
    }
    
    @Override // Runnable
    public void run() {
      // /perform the update
      // v3.1
      setIsStopped(false);

      // change language on the main GUI
      View mainGui = mainCtl.getGUI();
      mainGui.changeGUILanguage();

      // v3.1: update other application-wide resources 
      mainCtl.updateOnLanguageChange(regions);
      
      // change language on the functional GUIs what have been created
      Iterator<ControllerBasic> funcControllers = mainCtl.getControllers();

      ControllerBasic ctl;
      View gui;
      while (funcControllers.hasNext()) {
        ctl = funcControllers.next();
        gui = ctl.getGUI();
        if (gui != null && gui.isCreated()) {
          gui.changeGUILanguage();
        }
        
        // v3.1: update controller-specific resources, e.g. attribute name, label mappings
        // update the existing attribute name, label mappings
        ctl.updateOnLanguageChange(regions);
      }
      
      // v3.1
      setIsStopped(true);
    }
  } // end ChangeGUILanguageRunnable
  
}
