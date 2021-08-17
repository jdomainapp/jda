package jda.util;

import static java.lang.System.out;

import java.awt.print.PrinterException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.NotPossibleException.Code;
import jda.modules.common.expression.Op;
import jda.modules.common.net.ProtocolSpec;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.common.types.tree.Tree;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.OSMFactory;
import jda.modules.exportdoc.controller.DocumentExportController;
import jda.modules.exportdoc.controller.DocumentExportDataController;
import jda.modules.exportdoc.model.DataDocument;
import jda.modules.mccl.conceptmodel.AppResource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.SplashInfo;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmClientServerConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionLinking;
import jda.modules.mccl.conceptmodel.view.RegionMap;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.ObjectJoinExpression;
import jda.modules.oql.def.Query;
import jda.modules.sccl.syntax.DSDesc;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.sccl.syntax.SecurityDesc;
import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.sccl.syntax.security.DomainSecurityDesc;
import jda.modules.setup.model.SetUp;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;

public class SwTk {
  private static final boolean debug = Toolkit.getDebug(SwTk.class);
  
  /**The object id separator*/
  private static final String OBJECT_ID_SEP = "::";
  
  private SwTk() {}

  /**
   * @requires 
   *  Module splash screen has been loaded and created  
   *  
   * @effects
   *  if splashScreen module exists 
   *    run it 
   *  else
   *    do nothing
   * @version 3.1
   */  
  public static void startSplashScreen(ControllerBasic main) {
    ControllerBasic splashScreenCtl = main.lookUp(SplashInfo.class);
    
    if (splashScreenCtl != null) {
      splashScreenCtl.run();
    }
  }
  
  /**
   * @requires 
   *  <tt>rootModuleCtl</tt> is the controller of the {@link ApplicationModule} that owns <tt>containmentTree</tt>
   *  
   * @effects 
   *  if exists in <tt>containmentTree</tt> a containment edge between <tt>parentCls</tt> and <tt>childCls</tt> 
   *  and the scope of which is specified
   *    return the scope elements as <tt>String[]</tt>
   *  else
   *    return null
   *  
   *  <p>Throws NotPossibleException if fails to retrieve module containment scope from data source (when needed).
   *  
   * @version 
   * - 3.1: created <br>
   * - 3.2: added parameter rootModuleCtl to support extended scope def <br>
   * - 5.1: improved to support ScopeDesc, added NotPossibleException
   */
  public static String[] getContainmentScope(Tree containmentTree, ControllerBasic rootModuleCtl, Class parentCls, Class childCls) 
  throws NotPossibleException {
    String parentNode = parentCls.getName();
    String childNode = childCls.getName();
    
    Object tag = containmentTree.getEdgeTagByNodeValue(parentNode, childNode);
    if (tag != null) {
      // scope is defined
      String scope = tag.toString();
      /*v3.2: support scopeDef 
      String[] scopeElements = scope.split(",");
      return scopeElements;
      */
      ScopeDef scopeDef;
      String[] scopeElements = null;
      if (scope.startsWith(".")) {
        // a ScopeDef
        String scopeDefName = scope.substring(1);
        // retrieve the StyleDef constant object from the module
        scopeDef = SwTk.getContainmentScopeDefObject(rootModuleCtl, scopeDefName);
        scopeElements = scopeDef.scope();
        if (scopeElements.length == 0 || 
            (scopeElements.length==1 && scopeElements[0].equals("")) 
            || scopeElements[0].equals("*")) {
          // all attributes
          scopeElements = null;
        }
      } else {
        /* v5.1: support ScopeDesc (RegionLinking) 
        scopeElements = scope.split(",");
        */
        // not a scopeDef: either a comma-separated string of attribute names OR RegionLinking::obj-id (created from ScopeDesc)
        if (isObjectId(RegionLinking.class, scope)) {
          // RegionLinking object id: retrieve it to obtain the scope string
          RegionLinking rl;
          try {
            rl = retrieveModuleContainmentConfig(rootModuleCtl.getDodm(), scope);
            String scopeStr = rl.getProperty(PropertyName.module_containment_scope, String.class, "");
            scopeElements = scopeStr.split(",");
          } catch (DataSourceException e) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_OBJECT, e, new Object[] {scope});
          }
        } else {
          // non-object-id
          scopeElements = scope.split(",");
        }
      }
      
      return scopeElements;
    }
    
    // not found
    return null;
  }
  
  /**
   * @requires 
   *  <tt>objIdStr</tt> is a valid object-id string of {@link RegionLinking} as per {@link #isObjectId(Class, String)}.
   * 
   * @effects 
   *  if exists {@link RegionLinking} object whose object-id string is <tt>objIdStr</tt>
   *    retrieve (using <tt>dodm</tt>) and return that object
   *  else
   *    return null
   *    
   * @version 5.1 
   */
  public static RegionLinking retrieveModuleContainmentConfig(
      DODMBasic dodm, final String objIdStr) throws NotFoundException, DataSourceException {
    Class<RegionLinking> cls = RegionLinking.class;
    DSMBasic dsm = dodm.getDsm();
    DOMBasic dom = dodm.getDom();

    DAttr idAttr = dsm.getIDDomainConstraint(cls);
    String idValStr = objIdStr.split(OBJECT_ID_SEP)[1];
    Object idVal = dom.validateDomainValue(cls, idAttr, idValStr);
    
    Oid id = dom.retrieveObjectId(cls, new DAttr[] {idAttr}, new Object[] {idVal});
    
    RegionLinking rl = dom.retrieveObject(cls, id);
    
    // retrieve children objects
    // determine whether to look up or to retrieve objects
    boolean serialisedCfg = getSystemPropertyBoolean(PropertyName.setup_SerialiseConfiguration, Boolean.TRUE);
    
    if (serialisedCfg) {  // to retrieve objects
      dom.retrieveAssociatedObjects(rl, Region.class, RegionMap.class, Region.Assoc_hasChildren);
    } else {
      // nothing todo (objects are already created in memory)
    }
    
    return rl;
  }

  /**
   * @effects 
   *  if <tt>str</tt> is an object-id string of <tt>cls</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *    
   *  @example <pre>
   *    cls = Student /\ str = Student::1 => isObjectId(cls, str) = true
   *  </pre>
   * @version 5.1 
   */
  public static boolean isObjectId(Class<RegionLinking> cls, final String str) {
    return (str != null && str.indexOf(OBJECT_ID_SEP) > -1 && str.startsWith(cls.getSimpleName()));
  }

  /**
   * This method works similar to {@link #getContainmentScope(Tree, ControllerBasic, Class, Class)} except that it 
   * returns the raw {@link ScopeDef} object rather than just the scope value of it. 
   * 
   * @requires 
   *  <tt>rootModuleCtl</tt> is the controller of the {@link ApplicationModule} that owns <tt>containmentTree</tt>
   *  
   * @effects 
   *  if exists in <tt>containmentTree</tt> a containment edge between <tt>parentCls</tt> and <tt>childCls</tt> 
   *  and the {@link ScopeDef} of which is either explicitly specified or can be re-constructed  
   *    return this {@link ScopeDef}
   *  else
   *    return null
   *    
   * @version 
   * - 3.3c: created <br>
   * - 5.1: updated to support scope definition reconstruction from {@link RegionLinking} 
   */
  public static ScopeDef getContainmentScopeDefObject(Tree containmentTree, ControllerBasic rootModuleCtl, Class parentCls, Class childCls) {
    String parentNode = parentCls.getName();
    String childNode = childCls.getName();
    
    Object tag = containmentTree.getEdgeTagByNodeValue(parentNode, childNode);
    
    ScopeDef scopeDef = null;
    
    if (tag != null) {
      // scope is defined
      String scope = tag.toString();
      if (scope.startsWith(".")) {
        // a ScopeDef
        String scopeDefName = scope.substring(1);
        // retrieve the ScopeDef constant object from the module
        scopeDef = SwTk.getContainmentScopeDefObject(rootModuleCtl, scopeDefName);
      } else {
        // not a scopeDef
        // v5.1: it could be a RegionLinking id
        if (isObjectId(RegionLinking.class, scope)) {
          // RegionLinking object id: retrieve it
          RegionLinking rl;
          try {
            rl = retrieveModuleContainmentConfig(rootModuleCtl.getDodm(), scope);
            String scopeStr = rl.getProperty(PropertyName.module_containment_scope, String.class, "");
            String[] scopeElements = scopeStr.split(",");
            scopeDef = new ScopeDef(childCls, scopeElements, rl); // wrapper object for RegionLinking
          } catch (DataSourceException e) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_OBJECT, e, new Object[] {scope});
          }
        }
      }
    }
    
    return scopeDef;
  }
  
  /**
   * @effects 
   *  if exists a constant {@link ScopeDef} named <tt>scopeDefObjName</tt> defined for the {@link ApplicationModule}
   *  whose controller is <tt>controller</tt>
   *    return the object
   *  else 
   *    throws NotFoundException
   * @version 3.2
   */
  public static ScopeDef getContainmentScopeDefObject(ControllerBasic controller, String scopeDefObjName) throws NotFoundException {
    // the scope def constants are defined in the domain class
    Class domainCls = controller.getDomainClass();
    
    ScopeDef scopeDef;
    boolean inherited=false;
    
    try {
      scopeDef = Toolkit.getConstantObject(domainCls, scopeDefObjName, ScopeDef.class, inherited);
    } catch (NotFoundException e) {
      // try searching up the hierarchy
      inherited = true;
      scopeDef = Toolkit.getConstantObject(domainCls, scopeDefObjName, ScopeDef.class, inherited);
    }

    return scopeDef;
  }
  
  /**
   * @effects 
   *  if exists in <tt>containmentTree</tt> a containment edge between <tt>parentCls</tt> and <tt>childCls</tt> 
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *    
   * @version 3.2
   */
  public static boolean hasContainmentEdge(Tree containmentTree, Class parentCls, Class childCls) {
    String parentNode = parentCls.getName();
    String childNode = childCls.getName();
    
    return containmentTree.hasEdgeByNodeValue(parentNode, childNode);
  }
  
  /**
   * @effects 
   *  if exists in <tt>tree</tt> a containment edge <tt>(parentCls,sub)</tt> where <tt>sub</tt> is a descendent domain class of <tt>cls</tt>
   *    return <tt>sub</tt> (the first one that is found, ignoring others if any)
   *  else
   *    return <tt>null</tt>
   *    
   * @version 3.3
   */
  public static Class findDescendantTypeInTree(DSMBasic dsm, Tree tree, Class cls, Class parentCls) {
    Class subCls = null;
    
    Class[] subTypes = dsm.getSubClasses(cls);
    if (subTypes != null) {
      // has sub-types
      for (Class sub : subTypes) {
        if (hasContainmentEdge(tree, parentCls, sub)) {
          // found the first edge to a sub-type: use it and ignore others (if any)
          subCls = sub;
          break;
        } else {
          // sub is not in tree, find descendants (if any) (recursive)
          subCls = findDescendantTypeInTree(dsm, tree, sub, parentCls);
          if (subCls != null) { // found a descendant: use it 
            break;
          }
        }
      }      
    }
    
    return subCls;
  }

  /**
   * @effects 
   *  if the state scope of the root module of <tt>containmentTree</tt> is specified 
   *    return the scope elements as <tt>String[]</tt>
   *  else
   *    return null
   *    
   * @version 3.1
   */
  public static String[] getRootStateScope(Tree containmentTree) {
    String stateScope = containmentTree.getRoot().getTagAsString();
    
    if (stateScope != null) {
      String[] scopeElements = stateScope.split(",");
      return scopeElements;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  load and initialise security modules
   */
  public static void createSecurityModules(ControllerBasic mainCtl) throws NotFoundException, DataSourceException {
    Collection<ApplicationModule> modules = 
        // v5.2: getModulesByType(mainCtl, ModuleType.Security, false);
        getModulesByType(mainCtl, false, ModuleType.Security);
    
    if (modules != null) {
      if (debug)
        System.out.println("Tạo các mô-đun bảo mật");
      // initialise the modules
      boolean withGUI = true;
      createModules(modules, mainCtl, withGUI);
    }    
  }

  /**
   * @effects 
   *  load and initialise the system modules
   */
  public static void createSystemModules(
      ControllerBasic mainCtl) throws NotFoundException, DataSourceException {
    Collection<ApplicationModule> modules = 
        // v5.2: getModulesByType(mainCtl, ModuleType.System, false);
        getModulesByType(mainCtl, false, ModuleType.systemTypes());
    
    if (modules != null) {
      if (debug)
        System.out.println("Tạo các mô-đun hệ thống");
      // initialise the modules
      createModules(modules, mainCtl);
    }
  }
  
  /**
   * @requires 
   *  main module has been created
   *  
   * @effects 
   *  if security is enabled
   *    load and initialise the functional modules that the user has permissions for
   *  else
   *    load and initialise all functional modules
   */
  public static void createAllFunctionalModules(ControllerBasic mainCtl) throws NotFoundException, DataSourceException {
    // get the data modules
    Collection<ApplicationModule> dataModules = 
        // v5.2: getModulesByType(mainCtl, ModuleType.DomainData, true);
        getModulesByType(mainCtl, true, ModuleType.DomainData);
    
    if (dataModules != null) {
      // initialise the modules
      if (debug)
        System.out.println("Tạo các mô-đun dữ liệu");
      createModules(dataModules, mainCtl);
    }

    // get the report modules
    Collection<ApplicationModule> reptModules = 
        // v5.2: getModulesByType(mainCtl, ModuleType.DomainReport, true);
        getModulesByType(mainCtl, true, ModuleType.DomainReport);

    if (reptModules != null) {
      // initialise the modules
      if (debug)
        System.out.println("Tạo các mô-đun báo cáo");
      
      createModules(reptModules, mainCtl);
    }
  }
  
  /**
   * @requires 
   *  main module has been created /\ inputDomainClasses != null
   *  
   * @effects 
   *  if security is enabled
   *    load and initialise the <b>selected</b> functional modules that the user specified as input and that she  
   *    has permissions for
   *  else
   *    load and initialise the <b>selected</b> functional modules that the user specified as input
   *  
   *  <p>Throws NotPossibleException if no modules are found matching the specified input domain classes
   */
  public static void createFunctionalModules(ControllerBasic mainCtl, Class[] inputDomainClasses) 
      throws NotFoundException, NotPossibleException, DataSourceException {
    // get the data modules
    Collection<ApplicationModule> dataModules = 
        // v5.2: getModulesByType(mainCtl, ModuleType.DomainData, true);
        getModulesByType(mainCtl, true, ModuleType.DomainData);
    
    if (dataModules != null) {
      // initialise the modules
      if (debug)
        System.out.println("Tạo các mô-đun dữ liệu");
      
      //createModules(dataModules, mainCtl);
      // filter the modules based on the inputDomainClasses:
      //    only create modules that use one of these classes
      createFilteredModules(dataModules, mainCtl, inputDomainClasses);
    }

    // get the report modules
    Collection<ApplicationModule> reptModules = 
        // v5.2: getModulesByType(mainCtl, ModuleType.DomainReport, true);
        getModulesByType(mainCtl, true, ModuleType.DomainReport);
    
    if (reptModules != null) {
      // initialise the modules
      if (debug)
        System.out.println("Tạo các mô-đun báo cáo");
      
      //createModules(reptModules, mainCtl);
      // filter the modules based on the inputDomainClasses:
      //    only create modules that use one of these classes
      createFilteredModules(reptModules, mainCtl, inputDomainClasses);
    }
  }

  private static void createFilteredModules(
      Collection<ApplicationModule> modules, ControllerBasic mainCtl,
      Class[] inputDomainClasses) {
    Collection<ApplicationModule> filtered = new ArrayList<>();
    Collection<ApplicationModule> composites = new Stack<>();
    Class modelCls;
    for (ApplicationModule dm : modules) {
      if (!dm.isComposite()) {
        modelCls = dm.getDomainClassCls();
        if (modelCls == null) { // load modules without a model classes
          filtered.add(dm);
        } else {
          for (Class ic : inputDomainClasses) { 
            if (ic == modelCls) {
              // match a module
              filtered.add(dm);
              break;
            }
          }
        }
      } else { // composite module: process later
        composites.add(dm);
      }
    }
    
    // process composites (if any): add a composite module if one of its children is already in the filtered list
    if (!composites.isEmpty()) {
      Iterator<ApplicationModule> children;
      for (ApplicationModule cm: composites) {
        children = cm.getChildModulesIterator();
        while (children.hasNext()) {
          if (filtered.contains(children.next())) {
            // this composite module uses one of the input domain classes
            filtered.add(cm);
            break;
          }
        }
      }
    }
    
    // now create the modules
    if (filtered.isEmpty()) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_INPUT_CLASSES_ARGUMENT, filtered);
    } else {
      createModules(filtered, mainCtl);
    }    
  }

  /**
   * @effects 
   *  load (from the data source, if necessary) and return the module whose name is <tt>moduleName</tt>
   *  or return <tt>null</tt> if that module does not exist.
   * @version 3.1  
   */
  public static ApplicationModule retrieveModuleByName(DODMBasic dodm, String moduleName) 
      throws NotPossibleException, NotFoundException, DataSourceException {
    Class<ApplicationModule> moduleCls = ApplicationModule.class;
    
    DSMBasic dsm = dodm.getDsm();
    DOMBasic dom = dodm.getDom();
    
    ObjectExpression exp = QueryToolKit.createObjectExpression(dsm, moduleCls, ApplicationModule.AttributeName_name, Op.EQ, moduleName);
    Query q = new Query();
    q.add(exp);

    // check in object pool first
    Collection<ApplicationModule> mods = dom.getObjects(moduleCls, q);
    
    if (mods == null) {
      // no in pool: try the data source
      Map<Oid, ApplicationModule> modules = dom.retrieveObjects(
          ApplicationModule.class, q);

      if (modules != null) {
        return modules.values().iterator().next();
      } else {
        return null;
      }
    } else {
      return mods.iterator().next();
    }
  }

  /**
   * @effects 
   *  look up (from the object pool) and return the module whose name is <tt>moduleName</tt>
   *  or return <tt>null</tt> if that module does not exist.
   */
  public static ApplicationModule getModuleByName(DODMBasic dodm, String moduleName)  
      throws NotPossibleException, NotFoundException, DataSourceException {
    Class<ApplicationModule> moduleCls = ApplicationModule.class;
    
    DSMBasic dsm = dodm.getDsm();
    DOMBasic dom = dodm.getDom();
    
    ObjectExpression exp = QueryToolKit.createObjectExpression(dsm, moduleCls, ApplicationModule.AttributeName_name, Op.EQ, moduleName);
    Query q = new Query();
    q.add(exp);

    // check in object pool
    Collection<ApplicationModule> mods = dom.getObjects(moduleCls, q);
    
    if (mods == null) {
      return null;
    } else {
      return mods.iterator().next();
    }
  }
  
  /**
   * @effects 
   *  load (from the data source, if necessary) and return the module whose <tt>model</tt>'s class is <tt>domainCls</tt>
   *  (if there are several of such modules then return the first one), 
   *  or return <tt>null</tt> if that module does not exist.
   */
  public static ApplicationModule retrieveModule(DODMBasic dodm, Class domainCls)
      throws NotFoundException, NotPossibleException, DataSourceException {
    // create a join between ApplicationModule, ModelConfig with ModelConfig.domainClass = domainCls
    Class[] joinClasses = {
        ApplicationModule.class,
        ModelConfig.class
    };
    String[] assocNames = {
        ApplicationModule.Association_WithModelConfig        //"module-has-modelConfig"
    };

    String clsName = domainCls.getName();
    
    ObjectJoinExpression jexp = QueryToolKit.createJoinExpressionWithValueConstraint(dodm.getDsm(), joinClasses, assocNames, 
        ModelConfig.AttributeName_domainClass,
        Op.EQ,
        clsName);
    
    Query q = new Query(jexp);

    DOMBasic dom = dodm.getDom();
   
    Class<ApplicationModule> moduleCls = ApplicationModule.class;

    // check in object pool first
    Collection<ApplicationModule> mods = dom.getObjects(moduleCls, q);
    
    if (mods == null) {
      // no in pool: try the data source
      Map<Oid, ApplicationModule> modules = dom.retrieveObjects(
          ApplicationModule.class, q);

      if (modules != null) {
        return modules.values().iterator().next();
      } else {
        return null;
      }
    } else {
      return mods.iterator().next();
    }
  }
  
  /**
   * @effects 
   *  look up (from the object pool) and return the module whose <tt>model</tt>'s class is <tt>domainCls</tt>
   *  (if there are several of such modules then return the first one), 
   *  or return <tt>null</tt> if that module does not exist.
   */
  public static ApplicationModule getModule(DODMBasic schema, Class domainCls)
      throws NotFoundException, NotPossibleException, DataSourceException {
    String clsName = domainCls.getName();
    
    DSMBasic dsm = schema.getDsm();
    DAttr attrib = dsm.getDomainConstraint(ModelConfig.class, ModelConfig.AttributeName_domainClass);
    
    ObjectExpression exp = new ObjectExpression(ModelConfig.class, attrib,Op.EQ,clsName);
    
    Query q = new Query(exp);

    DOMBasic dom = schema.getDom();
    
    Collection<ModelConfig> modCfgs = dom.getObjects(ModelConfig.class, q);

    if (modCfgs == null) {
      return null;
    } else {
      ModelConfig modCfg = modCfgs.iterator().next();
      return modCfg.getApplicationModule();
    }
  }
  
  /**
   * @effects 
   *  if exist in the configuration schema object pools <tt>ApplicationModule</tt>s that satisfy the 
   *  specified arguments
   *    return them
   *  else
   *    return <tt>null</tt> 
   * @version 
   * - 3.1: improved to support module permission filter using object group <br>
   * - v5.2: improved to support retrieving for multiple types
   */
  private static Collection<ApplicationModule> getModulesByType(
      ControllerBasic mainCtl,
      /* v5.2:
      ModuleType type, 
      boolean checkPermission
      */
      boolean checkPermission, 
      ModuleType...types
      ) throws NotFoundException, DataSourceException {
    DODMBasic schema = mainCtl.getDodm();

    // get the data modules
    Class<ApplicationModule> moduleCls = ApplicationModule.class;
    String attribName = ApplicationModule.AttributeName_type;
    /* v5.2: 
    Op op = Op.EQ;
    ModuleType val = type;
    
    //debug: 
//    System.out.printf("loading modules: %s%n  filter list:%n  %s%n", type, 
//        schema.getDom().getObjects(c));
    
    Collection<ApplicationModule> modules = schema.getDom().getObjects(moduleCls, attribName, op, val);
    */
    Op op = Op.IN;

    Collection<ApplicationModule> modules = schema.getDom().getObjects(moduleCls, attribName, op, types);

    // if security is enabled then filter the modules to return only those that the
    // user has permission for
    /* v3.1: use object group permission on ApplicationModule
    if (checkPermission && modules != null && mainCtl.isSecurityEnabled()) {
      Collection<ApplicationModule> allowedModules = new ArrayList();
      for (ApplicationModule m : modules) {
        if (mainCtl.getResourceStateOfModuleByDomainClass(m) == true) {
          allowedModules.add(m);
        }
      }
      
      modules = allowedModules;
    }
    */

    if (checkPermission && modules != null && mainCtl.isSecurityEnabled()
        && mainCtl.hasObjectGroupPermission(moduleCls)) {
      Collection<ApplicationModule> allowedModules = new ArrayList();
      for (ApplicationModule m : modules) {
        if (mainCtl.getResourceStateOfModule(m) == true) {
          allowedModules.add(m);
        }
      }
      
      modules = allowedModules;
    }

    return (modules != null && !modules.isEmpty()) ? modules : null;
  }
  
  /**
   * @effects 
   *  load the main module and initialise its <tt>Controller</tt> and gui. 
   *  Return the <tt>Controller</tt> of this module (the main controller).
   */
  public static ControllerBasic createMainModule(DODMBasic schema, Configuration config) throws NotFoundException, DataSourceException {
    // get the module
    Class<ApplicationModule> c = ApplicationModule.class;
    String attribName = ApplicationModule.AttributeName_type;
    Op op = Op.EQ;
    ModuleType val = ModuleType.DomainMain;
    ApplicationModule module = schema.getDom().getObject(c, attribName, op, val);
    
    if (module == null) {
      throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, 
          "Không tìm thấy mô-đun {0}", RegionType.Main.name());
    }
    
    // create main controller
    ControllerBasic main = ControllerBasic.createController(schema, module, null, config);

    // create main gui
    main.createGUI();
    
    return main;
  }
  
  /**
   * @effects 
   *  look up the module whose domain class is <tt>domainClass</tt> and initialise its <tt>Controller</tt> and gui.
   *  (if <tt>serialised = true</tt> then also try to load from data source, if necessary)
   *   
   *  <p>Return the <tt>Controller</tt> of this module (the main controller) or <tt>null</tt>
   *  if module does not exist. 
   *  @version 
   *  - 3.2: renamed : createModule -> createModuleWithView
   */
  public static ControllerBasic createModuleWithView(ControllerBasic mainCtl, Class domainClass, boolean serialised) 
      throws NotFoundException, NotPossibleException, DataSourceException {
    DODMBasic dodm = mainCtl.getDodm();
    Configuration config = mainCtl.getConfig();
    
    ApplicationModule module = null; 
    ControllerBasic ctl = null;
    //try {
    if (serialised)
      module = retrieveModule(dodm, domainClass);
    else
      module = getModule(dodm, domainClass);
      
    if (module != null) {
      if (module.hasController()) {
        // create controller
        ctl = ControllerBasic.createController(dodm, module, mainCtl, config);

        if (ctl.hasGUI()) {
          // create gui
          ctl.createGUI();
          ctl.postCreateGUI();
        }
      }
    }
//    } catch (NotFoundException e) {
//      // not found
//    }
    
    return ctl;
  }
  
  /**
   * @effects <pre>
   *  for each ApplicationModule m in modules
   *    if m has not already been created
   *      creates m's controller and m's gui
   *  </pre>
   *  
   *  <p>This method does not touch the modules that have already been created. These modules
   *  are simply hidden from the user.
   */
  private static void createModules(Collection<ApplicationModule> modules, 
      ControllerBasic mainCtl) {
    boolean withGUI = false;
    createModules(modules, mainCtl, withGUI);
  }
  
  /**
   * @effects <pre>
   *  for each ApplicationModule m in modules
   *    if m has not already been created
   *      creates m's controller and 
   *        if withGUI = true then also m's gui
   *  </pre>
   *  
   *  <p>This method does not touch the modules that have already been created. These modules
   *  are simply hidden from the user.
   *  
   * @version 
   * - 2.7.4 <br>
   */
  private static void createModules(Collection<ApplicationModule> modules,
      ControllerBasic mainCtl, boolean withGUI) {
    DODMBasic dodm = mainCtl.getDodm();
    
    Configuration config = mainCtl.getConfig();

    List<ApplicationModule> composites = new ArrayList();
    List<ControllerBasic> controllers = new ArrayList();
    ControllerBasic controller;
    
    for (ApplicationModule module : modules) {
      if (mainCtl.lookUpModule(module)) {
        // already created: ignore
        if (debug)
          System.out.printf("...Mô-đun đã được tạo (bỏ qua): %s%n", module.getName());
        continue;
      }
      
      if (module.isComposite()) {
        composites.add(module);
        continue;
      }     
      
      // create controller
      // get the gui region for this module may be null
      if (debug)
        System.out.println("..." + module);

      if (module.hasController()) { // v2.7.2
        controller = ControllerBasic.createController(dodm, module, mainCtl, config);
  
        if (debug)
          System.out.println("......: " + controller);
  
        controllers.add(controller);
      }
    } // end modules

    // create module GUIs for non-composite controllers
    /*v2.7.4: add check */
    if (withGUI) {
      if (debug)
        System.out.println("Tạo giao diện các mô-đun");
      Iterator<ControllerBasic> cit = controllers.iterator();
      while (cit.hasNext()) {
        controller = cit.next();
        if (controller.hasGUI()) { // v3.2: getGUI() != null) {
          if (debug)
            System.out.println("..." + controller.getName());
          controller.createGUI();
          controller.postCreateGUI();
        }
      }
    }
    // v3.2: added this case to support modules whose views that are created at start-up
    else { // withGUI = false
      Iterator<ControllerBasic> cit = controllers.iterator();
      while (cit.hasNext()) {
        controller = cit.next();
        if (controller.hasGUI() &&
            controller.getGUI().getGUIConfig().getProperty(PropertyName.view_createOnStartUp, Boolean.class, Boolean.FALSE)) { 
          // create on start-up 
          if (debug)
            System.out.println("...tạo giao diện: " + controller.getName());
          controller.createGUI();
          controller.postCreateGUI();
        }
      }      
    }
    
    // initialise composite controllers last because they may need to access 
    // data controllers of the other controllers
    if (debug)
      System.out.println("Tạo các mô-đun phức hợp");
    
    for (ApplicationModule module : composites) {
      if (debug)
        System.out.println("..." + module);
      
      if (module.hasController()) { // v2.7.2
        controller = ControllerBasic.createController(dodm, module, mainCtl, config);
        
        /*v2.7.4: add check */
        if (withGUI) {
          if (controller.hasGUI()) { //v3.2: getGUI() != null) {
            if (debug)
              System.out.println("-> giao diện: " + controller.getName());
            controller.createGUI();
            controller.postCreateGUI();
          }
        }// v3.2: added this case to support modules whose views that are created at start-up
        else { // withGUI = false
          if (controller.hasGUI() &&
              controller.getGUI().getGUIConfig().getProperty(PropertyName.view_createOnStartUp, Boolean.class, Boolean.FALSE)) { 
            // create on start-up 
            if (debug)
              System.out.println("...tạo giao diện: " + controller.getName());
            controller.createGUI();
            controller.postCreateGUI();
          }
        }
      }
    }    
  }
  
  /**
   * @effects 
   *  return a simple <tt>Configuration</tt> object that does not require storing objects to an external storage
   */
  public static Configuration createMemoryBasedConfiguration(String appName) {
    final String AppFolder = //v3.1: "/home/dmle/tmp/"+appName; 
        getWellKnownAppFolder(appName);
    final String SetUpFolder = System.getProperty("user.dir");
    
    // set language and label constant class for that language
    final Language Lang =
//         Language.English;
        Language.Vietnamese;
    //v3.0: final String labelConstantClass = null;
    
    Configuration config = new Configuration();
    config.setAppName(appName);
    config.setSetUpFolder(SetUpFolder);
    config.setAppFolder(AppFolder);

    config.setLanguage(Lang);
    //config.setLabelConstantClass(labelConstantClass);

    // v2.7.3: create a memory-based DODM config
    DODMConfig dodmConfig = new DODMConfig(config);
    config.setDodmConfig(dodmConfig);
    
    // organisation
    GUIToolkit.initInstance(config);
    ImageIcon logo = null;
    try {
      logo = GUIToolkit.getImageIcon("domainapp.jpg", null);
    } catch (Exception e) {
      // ignore logo 
    }
    
    config.setOrganisation("Domain Application", logo, 
        "Hà nội, Việt nam", "http://domainapp.com.vn");
    
    return config;
  }
  
  /**
   * @effects 
   *  return a simple <tt>Configuration</tt> object, which uses <b>Embedded JavaDB</tt> 
   */
  public static Configuration createSimpleConfigurationInstance(
      String appName,
      String dbName) {
    // create an application configuration for testing
     
    //final String AppName = "DomainAppTest";
    final String AppFolder = getWellKnownAppFolder(appName); // v3.1: "/home/dmle/tmp/"+appName;
    final String SetUpFolder = System.getProperty("user.dir");
    
    // set language and label constant class for that language
    final Language Lang =
//         Language.English;
        Language.Vietnamese;
  //v3.0: final String labelConstantClass = null;
    
    Configuration config = new Configuration(); //v2.7.3: new Configuration(dbName);
    
    config.setAppName(appName);
    config.setSetUpFolder(SetUpFolder);
    config.setAppFolder(AppFolder);

    config.setLanguage(Lang);
  //v3.0: config.setLabelConstantClass(labelConstantClass);

    // v2.7.3
    DODMConfig dodmConfig = new DODMConfig(config, OSMFactory.getStandardOsmConfig("derby", dbName));
    config.setDodmConfig(dodmConfig);
    
    // the default
    //config.setListSelectionTimeOut(25);
//    config.setUseSecurity(true);
//    config.setUserName("admin");
//    config.setPassword("admin");
    
    //config.setDefaultModule("Customer");
    
    // organisation
    GUIToolkit.initInstance(config);
    ImageIcon logo = null;
    try {
      logo = GUIToolkit.getImageIcon("domainapp.jpg", null);
    } catch (Exception e) {
      // ignore logo 
    }
    
    config.setOrganisation("Domain Application", logo, 
        "Hà nội, Việt nam", "http://domainapp.com.vn");
    
    return config;
  }

  /**
   * This invokes {@link #createSimpleConfigurationInstance(String, String)}
   * 
   * @effects 
   *  return a simple <tt>Configuration</tt> object, whose application name is extracted from <tt>moduleMainCls</tt> and 
   *  which uses <b>Embedded JavaDB</tt> 
   */
  public static Configuration createSimpleConfigurationInstance(DODMBasic schema, String dbName, Class moduleMainCls) {
    ModuleDescriptor moduleDesc = (ModuleDescriptor) 
        moduleMainCls.getAnnotation(ModuleDescriptor.class); 

    //String dbName = schema.getDom().getDataSourceName();
    String appName = moduleDesc.name();    
    
    return createSimpleConfigurationInstance(appName, dbName);
  }


  /**
   * @effects 
   *  return a default initial application {@link Configuration} with the following settings:
   *  <pre>
   *    application name = appName
   *    application folder = user_home_directory/appName
   *    setup folder = current_working_directory
   *    (<b>embedded JavaDB</b>) data source specification = "jdbc:derby:" + dbUrl
   *  </pre>
   */
  public static Configuration createDefaultInitApplicationConfiguration(
      String appName, String dbUrl) {
    //System.out.println("debug: " + ApplicationToolKit.class);
    
    Configuration config = createDefaultConfig(appName);
    
    DODMConfig dodmConfig = new DODMConfig(config, OSMFactory.getStandardOsmConfig("derby", dbUrl));
    config.setDodmConfig(dodmConfig);

    return config;
  }
  
  /**
   * @effects 
   *  return a default initial application {@link Configuration} with the following settings:
   *  <pre>
   *    application name = appName
   *    application folder = user_home_directory/appName
   *    setup folder = current_working_directory
   *    (<b>embedded JavaDB</b>) data source specification = "jdbc:derby:" + dbUrl
   *  </pre>
   *  @version 3.3
   */
  public static Configuration createDefaultInitApplicationConfiguration(
      String appName, String dsType, String dbUrl, String user, String pwd) {
    //System.out.println("debug: " + ApplicationToolKit.class);
    
    Configuration config = createDefaultConfig(appName);

    //TODO: do we need user and pwd?
    
    DODMConfig dodmConfig = new DODMConfig(config, OSMFactory.getStandardOsmConfig(dsType, dbUrl));
    config.setDodmConfig(dodmConfig);

    return config;
  }
  
  /**
   * @effects 
   *  return a default initial application {@link Configuration} with the following settings:
   *  <pre>
   *    application name = appName
   *    application folder = user_home_directory/appName
   *    setup folder = current_working_directory
   *    (<b>JavaDB-typed</b>) data source protocol = protSpec
   *  </pre>
   */
  public static Configuration createDefaultInitApplicationConfiguration (
      String appName, ProtocolSpec protSpec) {
    Configuration config = createDefaultConfig(appName);
    
    OsmConfig osmConfig = OSMFactory.getStandardOsmConfig("derby", protSpec);
    
    DODMConfig dodmConfig = new DODMConfig(config, osmConfig);
    config.setDodmConfig(dodmConfig);

    return config;
  }

  /**
   * @effects 
   *  return a default <b>initial</b> application {@link Configuration} with the following settings:
   *  <pre>
   *    application name = appName
   *    application folder = user_home_directory/appName
   *    setup folder = current_working_directory
   *  </pre> 
   */
  public static Configuration createDefaultConfig(String appName) {
    final String AppFolder = getWellKnownAppFolder(appName); 
    final String SetUpFolder = System.getProperty("user.dir");
    
    Configuration config = new Configuration();
    config.setAppName(appName);
    config.setSetUpFolder(SetUpFolder);
    config.setAppFolder(AppFolder);
    
    return config;
  }

  /**
   * @param config 
   * @effects 
   *  if <tt>config</tt> is a <b>default</b> client/server configuration, 
   *  i.e. using Java-DB data source in the client/server mode
   *    return <tt>true</tt>
   *  else 
   *    return <tt>false</tt>
   */
  public static boolean isDefaultClientServerConfig(OsmConfig config) {
    if (config instanceof OsmClientServerConfig) {
      return config.isDataSourceTypeJavaDb(); //v3.0 config.getDataSourceType().equals("derby");
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  return an initial application {@link Configuration} with the following settings:
   *  <pre>
   *    application name = appName
   *    application folder = user_home_directory/appName
   *    setup folder = current_working_directory
   *    data source config = osmConfig
   *  </pre>
   */
  public static Configuration createInitApplicationConfiguration(
      String appName, OsmConfig osmConfig) {
    Configuration config = createDefaultConfig(appName);
    
    DODMConfig dodmConfig = new DODMConfig(config, osmConfig);
    
    config.setDodmConfig(dodmConfig);

    return config;
  }
  
  /**
   * @effects 
   *  create and return an <b>init</b> <tt>Configuration</tt> whose OSM runs in <b>client/server</b> mode with
   *  the following settings:
   *  <pre>
   *    data source type = dsType
   *    client URL = clientUrl
   *    server URL = serverUrl
   *  </pre>
   *  
   *  <p>The init configuration is created by {@link #createInitApplicationConfiguration(String, OsmConfig)}.
   *  @version 3.0  
   */
  public static Configuration createClientServerApplicationConfiguration(String appName, String dsType, String clientUrl, String serverUrl) {
    OsmClientServerConfig osmConfig = OSMFactory.getStandardOsmClientServerConfig(dsType, clientUrl, serverUrl);
    
    Configuration config = createInitApplicationConfiguration(appName, osmConfig);
    return config;
  }

  /**
   * @effects 
   *  create and return an <b>init</b> <tt>Configuration</tt> whose OSM runs in <b>client-only</b> mode with
   *  the following settings:
   *  <pre>
   *    data source type = dsType
   *    client URL = clientUrl
   *  </pre>
   *  
   *  <p>The init configuration is created by {@link #createInitApplicationConfiguration(String, OsmConfig)}.
   *  @version 3.0  
   */
  public static Configuration createClientApplicationConfiguration(String appName, 
      String dsType, String clientUrl, String userName, String password) {
    OsmClientServerConfig osmConfig = OSMFactory.getStandardOsmClientConfig(dsType, clientUrl);
    
    osmConfig.setProperty("user", userName);
    osmConfig.setProperty("password", password);
    
    Configuration config = createInitApplicationConfiguration(appName, osmConfig);
    return config;
  }

  /**
   * @effects 
   *  create (in data source if <tt>serialised=true</tt>) <tt>PropertySet propSet</tt>
   * @version 2.8 
   */
  public static void createPropertySet(DODMBasic dodm, PropertySet  propSet, 
      boolean serialised, // v2.8 
      int gapDistance) throws DataSourceException {
    DOMBasic dom = dodm.getDom();
    
    StringBuffer indent = new StringBuffer();
    for (int i = 0; i < gapDistance;i++) indent.append(" ");
    
    StringBuffer subIndent = new StringBuffer(indent);
    subIndent.append("  ");
    
    gapDistance = gapDistance + 4;

    // add property set to data source
    if (debug)
      out.printf("%sProperty set: %s%n", indent, propSet.getName());
    
    dom.addObject(propSet, serialised);
    
    if (debug)
      out.printf("%sProperties:%n", indent);
    Collection<Property> props = propSet.getProps();
    for (Property p : props) {
      if (debug)
        out.printf("%s%s: \"%s\" (%s<%s>)%n", subIndent, 
          p.getPkey(), p.getValueAsString(), p.getType(), p.getValue());
      
      // add property set to data source
      dom.addObject(p, serialised);
    }

    Collection<PropertySet> extents = propSet.getExtensions();
    if (extents != null && !extents.isEmpty()) {
      if (debug)
        out.printf("%sExtension(s):%n", subIndent);
      for (PropertySet pset : extents) {
        // recursive
        createPropertySet(dodm, pset, serialised, gapDistance);
      }
    }
  }
  
  /**
   * @effects 
   *  set the system property whose name is <tt>propName.name()</tt> to <tt>val</tt>
   *  <br>return the previous value of the property (if exists) or return </tt>null</tt> if otherwise
   * @version 4.0 
   */
  public static String setSystemProperty(PropertyName propName, String val) {
    String property = propName.getSysPropName();
    
    String old = System.getProperty(property);
    System.setProperty(property, val);
    
    return old;
  }
  
  
  /**
   * @effects 
   *  if exists system property named <tt>propName</tt>
   *    return its value parsed into Boolean
   *  else
   *    return <tt>defValue</tt>
   * @version 5.1
   */
  public static Boolean getSystemPropertyBoolean(PropertyName propName, Boolean defValue) {
    
    Boolean val;
    String propValStr = System.getProperty(propName.getSysPropName());
    if (propValStr == null) {
      // no property
      val = defValue;
    } else {
      try {
        val = Boolean.parseBoolean(propValStr);
      } catch (Exception e) { // invalid value
        System.out.printf(SwTk.class.getSimpleName()+".getSystemPropertyBoolean: invalid property value: %s = %s%n", propName, propValStr);
        val = defValue;
      }
    }
    
    return val;
  }
  
  /**
   * @effects 
   *  if exists <tt>System</tt> property named <tt>propName</tt>
   *    return its value
   *  else
   *    throw NotFoundException 
   * @version 4.0
   */
  public static String getSystemProperty(String propName) throws NotFoundException {
    String val = System.getProperty(propName);
    
    if (val == null) {
      throw new NotFoundException(NotFoundException.Code.PROPERTY_NOT_FOUND, new Object[] {propName});
    } else {
      return val;
    }
  }
  
  /**
   * @effects 
   *  return a well known application folder suitable for the underlying operating system
   */
  public static String getWellKnownAppFolder(String appName) {
    //String os = System.getProperty("os.name");
    String fsep = System.getProperty("file.separator");
    
    String parentFolder = System.getProperty("user.home");

    return parentFolder+fsep+appName;
    
    /* use well-known application folder
    if (os.startsWith("Window")) {
      return "C:"+fsep+"program files"+fsep+appName;
    } else {
      return fsep+"opt"+fsep+appName;
    }
    */
  }
  
  /**
   * @effects
   *   print the content of <tt>textComp</tt>  
   */
  public static void print(JTextComponent textComp) throws PrinterException {
    textComp.print();
  }
  
  /**
   * @effects 
   *  print the content (which conforms to <tt>flavor</tt>) that is contained in <tt>ins</tt>
   */
  public static void print(InputStream ins, DocFlavor flavor) throws PrintException {
    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
    
    if (flavor == null) {
      // use default flavour for ins
      flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
    }
    
    PrintService printService[] = PrintServiceLookup.lookupPrintServices(
        flavor, pras);
    
    PrintService defaultService = PrintServiceLookup
        .lookupDefaultPrintService();
    
    int x = 200, y = 200;
    PrintService service = ServiceUI.printDialog(null, x, y,
        printService, defaultService, flavor, pras);
    
    if (service != null) {
      DocPrintJob job = service.createPrintJob();
      /* use this to be informed of print event */ 
      PrinterListener listener = new PrinterListener();
      job.addPrintJobListener(listener);

      DocAttributeSet das = new HashDocAttributeSet();
      
      Doc doc = new SimpleDoc(ins, flavor, das);
      
      boolean blocking = false;
      
      job.print(doc, pras);
      
      listener.waitForJob(blocking);
    }
  }
  
  private static class PrinterListener extends PrintJobAdapter implements Runnable {
    private boolean stopped;

    private static final long MAX_WAIT_TIME = (long) (0.1 * 60 * 1000);  // (millisecs)
    private static final int sleepTime = 500;
    
    public PrinterListener() {
      System.out.print("P"); 
      stopped = false;  
    }
    
    @Override
    public void printJobCompleted(PrintJobEvent e) {
      // print has completed
      System.out.println("t");
      stopped = true;
    }

    @Override
    public void printDataTransferCompleted(PrintJobEvent e) {
      // data has been transferred to print service (printing may now start)
      System.out.print("rin");            
    }

    public void waitForJob(boolean blocking) {
      if (blocking) {
        waitForJob();
      } else {
        // starts this on a separate thread
        new Thread(this).start();
      }
    }
    
    public void waitForJob() {
      int timeElapsed = 0;
      
      do {
        try {
          Thread.sleep(sleepTime);
          timeElapsed += sleepTime;
          System.out.print(".");
        } catch (InterruptedException e1) {
        }
      } while (!stopped && timeElapsed < MAX_WAIT_TIME);
    }
    
    @Override // Runnable
    public void run() {
      waitForJob();
    }
  }

  /**
   * @effects 
   *  pause for <tt>sleepTime</tt> (millis)
   */
  public static void sleep(long sleepTime) {
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      // ignore
    }
  }

  /**
   * @effects
   *  if export function is supported 
   *    perform document export function on <tt>dataContainer</tt>
   *  else
   *    throws NotFoundException 
   *    
   * @version 3.2
   */
  public static void exportDocument(JDataContainer dataContainer) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    Class<DocumentExportController> exportCls = DocumentExportController.class;
    DocumentExportController<DataDocument> exportCtl = ControllerBasic.lookUpByControllerType(exportCls);

    if (exportCtl != null) {
      DocumentExportDataController exportDctl = (DocumentExportDataController) exportCtl.getRootDataController();
      exportDctl.export(dataContainer);
    }
  }

  /**
   * @effects 
   *  return the default absolute path to the help file of <tt>module</tt>
   *   
   * @version 3.2c
   */
  public static String getModuleHelpFilePath(Configuration config, ApplicationModule module) {
    String appFolder = config.getAppFolder();
    String userModuleName = module.getName();
    String langCode = config.getLanguage().getLanguageCode();
    final String fileExt = ".html";
    
    String helpFilePath = AppResource.fvarHelp.getPathWithVars(appFolder, 
        new String[] {userModuleName, langCode, userModuleName+fileExt});
    
    return helpFilePath;
  }

//  /**
//   * @effects <pre>
//   *   if sysCls != null
//   *     parse the meta-attributes of <tt>sysCls</tt> to create and return a {@link Configuration} object
//   *     throws NotFoundException if no meta-attributes are found
//   *   else
//   *     return <tt>null</tt>
//   *     </pre>
//   * @version 3.3
//   */
//  public static Configuration parseApplicationConfiguration(SetUpBasic su, Class sysCls) throws NotFoundException {
//  }

//  /**
//   * @effects <pre>
//   *  if sysCls != null
//   *    parse the {@link DSDesc} meta-attribute of <tt>sysCls</tt> to create and return a {@link Configuration} object
//   *    that contains only the data source specific configuration (other mandatory configuration items are set to default)
//   *  else 
//   *    return <tt>null</tt> </pre>
//   * @version 3.3
//   */
//  public static Configuration parseDataSourceConfiguration(Class sysCls) {
//    // TODO Auto-generated method stub
//  }

  /**
   * @effects <pre>
   *  if sysCls is not null
   *    return sysCls@SystemDesc.modules or if <tt>moduleTypes != null</tt> return a sub-set of these whose module-type is among <tt>moduleTypes</tt>
   *  else
   *    return null  
   * </pre>
   */
  public static Class[] parseModuleConfigs(Class sysCls, ModuleType...moduleTypes) throws NotFoundException {
    if (sysCls == null)
      return null;
    
    //debug;
//    System.out.println(SystemDesc.class);
    
    SystemDesc sysDesc = (SystemDesc) sysCls.getAnnotation(SystemDesc.class);
    
    if (sysDesc == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {SystemDesc.class, sysCls});
    }
    
    Class[] moduleConfigs = sysDesc.modules();
    if (moduleConfigs.length > 0) {
      
      if (moduleTypes != null && moduleTypes.length > 0) {
        // return a sub-set
        List<Class> subSet = new ArrayList<>();
        ModuleType mtype;
        ModuleDescriptor mdesc;
        for (Class modCfg : moduleConfigs) {
          mdesc = DSMBasic.getModuleDescriptorObject(modCfg);
          if (mdesc != null) {
            mtype = mdesc.type();
            for (ModuleType mt : moduleTypes) {
              if (mtype.equals(mt)) {
                // found one
                subSet.add(modCfg);
                break;
              }
            }
          }
        }
        
        if (!subSet.isEmpty()) {
          return subSet.toArray(new Class[subSet.size()]);
        } else {
          return null;
        }
      } else {
        // return all
        return moduleConfigs;
      }
    } else {
      return null;
    }
  }

  /**
   * @effects <pre>
   *  if sysCls is not null
   *    return sysCls@SystemDesc.systemModules
   *  else
   *    return null  
   * </pre>
   */
  public static Class[] parseSystemModuleConfigs(Class sysCls) throws NotFoundException {
    if (sysCls == null)
      return null;
    
    SystemDesc sysDesc = (SystemDesc) sysCls.getAnnotation(SystemDesc.class);
    
    if (sysDesc == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {SystemDesc.class, sysCls});
    }
    
    Class[] moduleConfigs = sysDesc.sysModules();
    if (moduleConfigs.length > 0) {
      return moduleConfigs;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  Create and return the domain model as {@link Collection} of the domain classes of the MCCs configured in <tt>systemCls</tt>.
   *  
   *  <p>If <tt>systemCls</tt> has no MCCs then return null.
   *  
   * @version 5.1 
   */
  public static Collection<Class> parseDomainModel(Class systemCls) {
    // model has not been initialised. Initialise it once!
    //
    // These models are extracted directly from the module descriptor configuration in the system class
    // Note: the module descriptors in the system class are NOT used to create modules directly. 
    // The way that DomainAppTool works is that it creates new module descriptors from the domain classes!
    Class[] MCCs = parseModuleConfigs(systemCls, 
        ModuleType.System,
        ModuleType.DomainData, 
        ModuleType.DomainReport);
    
    if (MCCs == null) {
      return null;
    } else {
      //debug
//      System.out.println("Module descriptors: \n" + Arrays.toString(MCCs) + "\n");
      
      Collection<Class> modelCol = new ArrayList<>();
      for (Class md : MCCs) {
        Class c = DSMBasic.getDomainClass(md);
        
        if (!modelCol.contains(c)) modelCol.add(c);          
      }
      
      return modelCol;
    }
  }
  
  /**
   * @effects <pre>
   *  if sysCls is not null
   *    return sysCls@SystemDesc.dataFileLoaders
   *  else
   *    return null  
   * </pre>
   *
   * @version 3.3
   */
  public static Class[] parseDataFileLoaders(Class sysCls) throws NotFoundException {
    if (sysCls == null)
      return null;
    
    SystemDesc sysDesc = (SystemDesc) sysCls.getAnnotation(SystemDesc.class);
    
    if (sysDesc == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {SystemDesc.class, sysCls});
    }
    
    Class[] dfLoaders = sysDesc.dataFileLoaders();
    
    if (dfLoaders.length > 0) {
      return dfLoaders;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if exists a setup-class specified in the system property
   *    load this class and return it
   *  else
   *    return null
   *    
   * @version 3.3
   */
  public static <T extends SetUp> Class<T> getSetUpClassFromProperty() {
    String setUpClsName = System.getProperty(PropertyName.setup_class.getSysPropName());
    
    Class<T> suCls = null;
    
    try {
      if (setUpClsName != null) {
        suCls = (Class<T>) Class.forName(setUpClsName);
      } 
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return suCls;
  }

  /**
   * @requires 
   *  sysCls != null
   * @effects 
   *  if exists {@link SystemDesc} attached to <tt>sysCls</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   */
  public static SystemDesc getSystemDesc(Class sysCls) {
    if (sysCls == null)
      return null;
    
    SystemDesc sysDesc = (SystemDesc) sysCls.getAnnotation(SystemDesc.class);
    return sysDesc;
  }
  
  /**
   * @requires
   *  sysDesc != null
   * @effects 
   *  Return the {@link DomainSecurityDesc} object that is defined in <tt>sysDesc</tt>, or 
   *  return <tt>null</tt> if no such object is defined. 
   *  
   * @version 3.3
   */
  public static DomainSecurityDesc getDomainSecurityDesc(SystemDesc sysDesc) {
    if (sysDesc == null)
      return null;
    
    Class defCls = sysDesc.securityDesc().domainSecurityDesc();
    
    if (defCls == CommonConstants.NullType)
      return null;
    
    DomainSecurityDesc domSecDesc = (DomainSecurityDesc) defCls.getAnnotation(DomainSecurityDesc.class);
    
    return domSecDesc;
  }
  
  /**
   * @requires 
   *  <tt>sysCls != null</tt>
   *  
   * @effects
   *   parse the meta-attributes of <tt>sysCls</tt> to create and return a {@link Configuration} object
   *   throws NotFoundException if no meta-attributes are found attached to <tt>sysCls</tt>
   */
  public static Configuration parseApplicationConfiguration(Class sysCls) throws NotFoundException {
    SystemDesc sysDesc = getSystemDesc(sysCls);
    
    if (sysDesc == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {SystemDesc.class, sysCls});
    }
    
    OrgDesc orgDesc = sysDesc.orgDesc();
    SecurityDesc secDesc = sysDesc.securityDesc();
    
    // create initial config
    Configuration config = parseInitApplicationConfiguration(sysCls);

    //  config.setLanguage(Lang);
    //v3.0: config.setLabelConstantClass(labelConstantClass);
  
    // TODO: support these
    //config.setListSelectionTimeOut(25);
    config.setMainGUISizeRatio(0.75);
    //config.setChildGUISizeRatio(0.75);
    
    // security configuration
    if (secDesc.isEnabled()) {
      //    config.setUseSecurity(false);
      config.setUseSecurity(true);
      
      /* comment these out will cause the display of Login dialog*/
      DomainSecurityDesc securityDesc = getDomainSecurityDesc(sysDesc);
      
      if (securityDesc == null) {
        throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {DomainSecurityDesc.class, sysDesc});
      }
      
      String user = securityDesc.appUser();
      String password = securityDesc.appPassword();
      if (!user.equals(CommonConstants.NullString)) {
        config.setUserName(user);
        if (!password.equals(CommonConstants.NullString)) {
          config.setPassword(password);
        }
      }
    } else {
      config.setUseSecurity(false);
    }
    
    // init GUI tool kit
    GUIToolkit.initInstance(config);
    
    // TODO
    //config.setDefaultModule("ModuleDomainApplicationModule");
    
    // organisation
    /*
     * If organisation uses a logo picture (preferred format: gif) then 
     * GUIToolkit.initInstance(conig) must be invoked first (see below) 
     */
    ImageIcon orgLogo;
    try {
      orgLogo = GUIToolkit.getImageIcon(orgDesc.logo(), null);
    } catch (NotFoundException ex) {
      orgLogo = null;
    }
    config.setOrganisation(
        //name: "Faculty of IT",
        orgDesc.name(),
        // logo: GUIToolkit.getImageIcon("hanu.gif", null),
        orgLogo,
        // address: "Km9 Đường Nguyễn Trãi, Quận Thanh Xuân",
        orgDesc.address(),
        // url: "http://fit.hanu.edu.vn"
        orgDesc.url()
        );
    
    return config;
  }

  /**
   * @requires 
   *  <tt>sysCls != null</tt>
   *  
   * @effects
   *   if exists <tt>module configuration classes (MCCs) </tt> in sysCls.SystemDesc
   *    return them as Class[]
   *   else 
   *    return an empty array
   */
  public static Class[] parseMCCs(Class sysCls) throws NotFoundException {
    SystemDesc sysDesc = getSystemDesc(sysCls);
    
    if (sysDesc == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {SystemDesc.class, sysCls});
    }
    
    Class[] modules = sysDesc.modules();
    return modules;
  }
  
  /**
   * @requires 
   *  <tt>sysCls != null</tt>
   *  
   * @effects
   *   parse the {@link DSDesc} meta-attribute of <tt>sysCls</tt> to create and return a {@link Configuration} object
   *   that contains the data source specific configuration and number of other basic configuration (e.g. language, etc.)
   *   
   * @todo
   * - support other data source configuration modes: client/server, embedded, etc.
   */
  public static Configuration parseInitApplicationConfiguration(Class sysCls) throws NotFoundException {
    SystemDesc sysDesc = getSystemDesc(sysCls);
    
    if (sysDesc == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {SystemDesc.class, sysCls});
    }
    
    String appName = sysDesc.appName();
    DSDesc dsDesc = sysDesc.dsDesc();
    String dsType = dsDesc.type();
    String dsUrl = dsDesc.dsUrl();
    String user = dsDesc.user();
    String pwd = dsDesc.password();
    
    //TODO: support other data source configuration modes: client/server, embedded, etc.
    ConnectionType dsMode = dsDesc.connType();
    //Configuration config = ApplicationToolKit.createClientApplicationConfiguration(AppName, "postgresql", clientUrl, dataSourceUserName, dataSourcePassword);
    
    Configuration config; 
    if (dsMode.equals(ConnectionType.Embedded)) {
      config = SwTk.createDefaultInitApplicationConfiguration(appName, dsType, dsUrl, user, pwd);
    } else {
      // TODO: support other modes here
      config = SwTk.createClientApplicationConfiguration(appName, dsType, dsUrl, user, pwd);
    }
    
    // customise dodm types
    DODMConfig dodmCfg = config.getDodmConfig();
    
    // dodm parts
    Class dsmType = dsDesc.dsmType();
    Class osmType = dsDesc.osmType();
    Class domType = dsDesc.domType();
    dodmCfg.setDsmType(dsmType);
    dodmCfg.setOsmType(osmType);
    dodmCfg.setDomType(domType);
    
    // set language
    Language lang = sysDesc.language();
    config.setLanguage(lang);
    
    // setup config type
    Class suCfgCls = sysDesc.setUpDesc().setUpConfigType();
    config.setSetUpConfigurationType(suCfgCls);    
    
    return config;
  }
  
  /**
   * <b>Note</b>:
   * THIS IS NOT EFFECTIVE FOR CLASSES THAT READ logging and debugging DURING LOADING (VIA STATIC VARIABLES)!!
   * 
   * @effects 
   *  if logging is not specified through system property
   *    set it to the default
   *  else
   *    do nothing
   * @version 5.1 
   */
  public static void setLogging() {
    PropertyName propName = PropertyName.Logging;
    try {
      getSystemProperty(propName.getSysPropName());
      // already set
    } catch (NotFoundException e) {
      // not set
      setSystemProperty(propName, "true");
    }
  }
  
  /**
   * <b>Note</b>:
   * THIS IS NOT EFFECTIVE FOR CLASSES THAT READ logging and debugging DURING LOADING (VIA STATIC VARIABLES)!!
   * 
   * @effects 
   *  if debugging is not specified through system property
   *    set it to the default
   *  else
   *    do nothing
   * @version 5.1 
   */
  public static void setDebugging() {
    PropertyName propName = PropertyName.Debug;
    try {
      getSystemProperty(propName.getSysPropName());
      // already set
    } catch (NotFoundException e) {
      // not set
      setSystemProperty(propName, "false");
    }
  }

  /**
   * @effects 
   *  execute the method <tt>main</tt> of c using <tt>args</tt> as arguments.
   *  
   *  Throws Exception if failed.
   */
  public static void executeMain(Class c, String[] args) throws Exception {
    Method main = null;
    try {
      main = c.getMethod("main", String[].class);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, e, new String[] {c.getSimpleName(), "main"});
    }
    
    try {
      main.invoke(null, new Object[] {args});
    } catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new NotPossibleException(Code.FAIL_TO_PERFORM_METHOD, e, new String[] {c.getSimpleName(), "main", Arrays.toString(args)});
    }
  }

  /**
   * @effects 
   *  execute the standard software specified by the software class <tt>swc</tt>
   *  
   *  Throws Exception if failed.
   *     
   * @version 5.2 
   */
  public static void executeSoftware(Class swc) throws Exception {
    // execute the software class
    String[] args = new String[] {"run"};
    
    // run with in-memory configuration
//    setSystemProperty(PropertyName.setup_SerialiseConfiguration, Boolean.FALSE+"");
    
    executeMain(swc, args);
  }
}
