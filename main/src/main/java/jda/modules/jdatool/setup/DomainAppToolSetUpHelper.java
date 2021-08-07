package jda.modules.jdatool.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.jdatool.modulegen.ModuleClassGen;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpConfigBasic;
import jda.modules.setup.model.SetUpBasic.MessageCode;

/**
 * @overview
 *  A helper class that performs all shared tasks needed by domainapp-tool-setup classes.
 *  
 * @author dmle
 *
 * @version 3.3 
 */
class DomainAppToolSetUpHelper {
  private List<Class> modelClasses;
  
  /**
   * a super-set of {@link #modelClasses} which may contain super- and ancestor domain 
   * classes
   */
  private List<Class> allModelClasses;

  private List<List<Class>> moduleDescriptors;

  /**
   * the {@link ModuleClassGen} used to generate module descriptors automatically from input domain classes 
   * @version 3.2
   */
  private ModuleClassGen moduleClassGen;

  private SetUpBasic su;
  
  //private static final String DBName = "data"+File.separator+"DomainAppTool";
  
  /**
   * @effects 
   * 
   */
  public DomainAppToolSetUpHelper(SetUpBasic su) {
    this.su = su;
  }

  public Class[] getInputModelClasses() {
    // return the model classes
    return (modelClasses != null) ? 
        modelClasses.toArray(new Class[modelClasses.size()]) : 
          null;
  }
  
  public Class[] getModelClasses() {
    // return the model classes together with their super classes 
//    return (modelClasses != null) ? 
//        modelClasses.toArray(new Class[modelClasses.size()]) : 
//          null;
    if (modelClasses != null && allModelClasses == null) {
      allModelClasses = su.getModelClasses(
          // v2.8: getDomainSchema().getDsm(), 
          modelClasses);
    }
    
    return (allModelClasses != null) ? 
        allModelClasses.toArray(new Class[allModelClasses.size()]) : 
          null;
  }

  public List<List<Class>> getModuleDescriptors() {
    // return the module descriptors of the model classes (if any)
    return moduleDescriptors;
  }
  
  /**
   * @modifies {@link #modelClasses}, {@link #moduleDescriptors}
   * @effects 
   *  read and load the domain class(es) whose FQN names are given in <tt>args</tt>.
   *  
   *  <p>Throws IllegalArgumentException if no valid domain classes can be loaded from <tt>args</tt>; 
   *  NotPossibleException if fails to create the application modules; 
   *  DataSourceException if fails to connect to the data source
   */
  public void loadClasses(String[] args) throws IllegalArgumentException, NotPossibleException, DataSourceException {
    /* v5.1: move loadModelClasses to first, b/c it does not require DODM and that 
     * the loaded classes are needed to check data-serialisable as part of initDODM()
    if (!su.isDodmInit())
      su.initDODM();
    
    su.log(MessageCode.UNDEFINED, "LOADING domain classes...\n{0}", Arrays.toString(args));
    
    loadModelClasses(args);
     */
    su.log(MessageCode.UNDEFINED, "LOADING domain classes...\n{0}", Arrays.toString(args));
    
    loadModelClasses(args);

    if (!su.isDodmInit())
      su.initDODM();
    
    
    loadModuleDescriptors();
  }

  /**
   * @requires
   *  modelClasses != null /\ size(modelClasses) > 0
   * @modifies {@link #moduleDescriptors}
   * @effects 
   *  <pre>
   *    for each class c in modelClasses
   *      if c@moduleDescriptor != null
   *        add c@moduleDescriptor to moduleDescriptors
   *  </pre>
   *  
   *  Note: Only the classes with the configured module descriptors are processed. 
   *  Thus, to ensure correct behaviour either all or none of the model classes are specified with a module descriptor.
   */
  private void loadModuleDescriptors() {
    Class moduleDescriptorCls;
    List<Class> descriptors = new ArrayList<>();
    DSMBasic dsm = su.getDODM().getDsm();
    
    for (Class c : modelClasses) {
      moduleDescriptorCls = dsm.getModuleDescriptor(c);
      if (moduleDescriptorCls != null) {
        // specified
        descriptors.add(moduleDescriptorCls);
      }
    }
    
    if (descriptors.isEmpty()) {
      // no descriptors
      moduleDescriptors = null;
    } else {
      moduleDescriptors = new ArrayList<>();
      moduleDescriptors.add(descriptors);
    }
  }

  /**
   * @modifies {@link #modelClasses}
   * 
   * @effects <pre>
   *  if args.length > 0
   *    for each a in args
   *      load domain class c s.t c.name = a (throws IllegalArgumentException if c is not found)
   *      add c to modelClasses
   *  else
   *    throw IllegalArgumentException
   *  </pre>
   */
  private void loadModelClasses(String[] args) throws IllegalArgumentException {
    if (args.length == 0)
      throw new IllegalArgumentException(
          DomainAppToolSetUp.class.getSimpleName()+".loadModelClasses: classes names are expected but not specified");
    
    modelClasses = new ArrayList<>();
    
    Class c;
    
    for (String a : args) {
      // assume class is in the class path
      try {
        c = Class.forName(a);
        modelClasses.add(c);
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(
            DomainAppToolSetUp.class.getSimpleName()+".loadModelClasses: invalid class name: " + a);
      }
    }
  }

  protected void createDomainConfiguration(SetUpConfigBasic sucfg,
      boolean serialised  // v2.8
      )
      throws DataSourceException, NotFoundException {
    su.log(MessageCode.UNDEFINED,
        //"Tạo cấu hình chương trình"
        "Creating domain configuration"
        );
    /*v2.7.3: create any system modules that this application may use */
    // create system-specific labels
    List<List<Class>> moduleDescriptors = su.getSystemModuleDescriptors();
    Class[] group;

    final String langCode = sucfg.getLanguageCode();

    // v2.6.4b: added null check
    if (moduleDescriptors != null) {
      Map<String,Label> sysLabelMap = null; //sucfg.getSystemLabels(config.getLanguage().getLanguageCode());
      
      for (List<Class> mds : moduleDescriptors) {
        group = mds.toArray(new Class[mds.size()]);
        sucfg.createModules(su, group, sysLabelMap, serialised);
      }
    }
    
    // domain-specific modules
    moduleDescriptors = getModuleDescriptors();
    // create domain-specific labels
    Map<String,Label> labelMap; // v3.0 = getDomainLabels();
    
    if (moduleDescriptors != null) {
      // use module descriptors 
      
      for (List<Class> mds : moduleDescriptors) {
        group = mds.toArray(new Class[mds.size()]);

        labelMap = su.getModuleLabels(group); // v3.0
        
        sucfg.createModules(su, group, labelMap, serialised);
      }
    } else {
      // no module descriptors: use the model classes to create the default modules
      // ENHANCED: this is enhanced from the super-class method 
      Class[] modelClasses = getInputModelClasses(); //getModelClasses();
      
      /* v3.2: use module gen
      // v3.2: use standard api
      // sucfg.createModulesFromClasses(modelClasses, serialised);
      sucfg.createModules(this, modelClasses, null, serialised);
       */
      group = generateModuleDescriptors(modelClasses);
      labelMap = su.getModuleLabels(group); // v3.0
      
      sucfg.createModules(su, group, labelMap, serialised);
    }
  }

  /**
   * @effects 
   *  Generate and return module descriptor classes for each domain class in <tt>modelClasses</tt>
   *   
   * @version 3.2 
   */
  private Class[] generateModuleDescriptors(Class[] modelClasses) {
    if (moduleClassGen == null)
      moduleClassGen = new ModuleClassGen(su.getDODM());
    
    Class[] moduleDescriptors = new Class[modelClasses.length 
                                          + 1 // the main module
                                          ];
    
    String modulePackage = "modules"; // virtual name
    
    // generate main module
    moduleDescriptors[0] = moduleClassGen.generateMainModuleClass(su.getAppName(), modulePackage);
    
    // generate functional modules
    for (int i = 1; i < moduleDescriptors.length; i++) {
      moduleDescriptors[i] = moduleClassGen.generateFunctionalModuleClass(modelClasses[i-1], modulePackage);
    }
    
    return moduleDescriptors;
  }
}
