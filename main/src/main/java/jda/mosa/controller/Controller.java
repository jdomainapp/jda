package jda.mosa.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.UserRegion;
import jda.modules.security.authentication.controller.SecurityController;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.Security;
import jda.mosa.module.Module;
import jda.util.SwTk;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;

/**
 * @overview
 *  A sub-type of {@link ControllerBasic} that supports additional functionality.  
 *   
 * @author dmle
 * 
 * @version 
 * - 2.7.3: created<br>
 */
public class Controller<C> extends ControllerBasic<C> {

  /**
   * The client modules (if any) that use the service of the {@link Module} represented by this.
   * 
   * @version 5.2
   */
  private Collection<Module> clientModules;

  public Controller(DODMBasic dodm, ApplicationModule module, Region moduleGui,
      ControllerBasic parent, Configuration config) throws NotPossibleException {
    super(dodm, module, moduleGui, parent, config);
  }

  /**
   * @effects 
   *  register domain class <tt>cls</tt> in <tt>dodm</tt>, which can be a language-aware class
   */
  @Override
  protected void registerDomainClass(Class<C> cls) throws NotPossibleException {
    // support language aware domain classes
    DODMBasic dodm = getDodm();
    
    DSMBasic dsm = dodm.getDsm();
    
    if (dsm.isLanguageAware(cls)) {
      // load and register the sub-class of cls that is defined in a package named after the configured language
      // Note that this also registers cls but it is *not* used as the domain class of the controller 
      
      Class[] subTypes = dsm.getSubClasses(cls);
      Configuration config = getConfig();
      Language lang = config.getLanguage();
      String langCode = lang.getLanguageCode();
      
      if (subTypes == null) {
        throw new NotPossibleException(NotPossibleException.Code.NO_LANGUAGE_AWARE_SUBTYPE_DEFINED, new Object[] {langCode, cls.getSimpleName()});
      }
      
      String pkgLastName;
      boolean found = false;
      for (Class sub : subTypes) {
        pkgLastName = Toolkit.getPackageLastName(sub);
        if (pkgLastName.equals(langCode)) {
          // found cls
          super.registerDomainClass(sub);
          found = true;
          break;
        }
      }
      
      if (!found)
        throw new NotPossibleException(NotPossibleException.Code.NO_LANGUAGE_AWARE_SUBTYPE_DEFINED, new Object[] {langCode, cls.getSimpleName()});
      
    } else {
      super.registerDomainClass(cls);
    }
  }
  
  @Override
  protected void loadUserConfiguration() {
    // if user configuration settings are defined then load them
    boolean hasUserConfiguration = getConfig().getUseSecurity();
    
    if (hasUserConfiguration) {
      
      // TODO: improve this to support more settings
      // for now, support just RegionGui settings
      
      // the logged in user
      DomainUser user = getDomainUser();
      
      // the GUI region
      Region reg = getApplicationModule().getViewCfg();

      /* get UserConfig object uc of the user
       *  if uc.attributeVals != null
       *    update RegionGui attributes matching uc.attributeVals
       */
      DOMBasic dom = getDodm().getDom();
      try {
        UserRegion uc = dom.retrieveObject(UserRegion.class, 
            new String[] {UserRegion.AttributeName_User, UserRegion.AttributeName_Region}, 
            new Op[] {Op.EQ, Op.EQ}, 
            new Object[] {user, reg});
        
        if (uc != null)
          uc.updateRegion(dom);
        
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_LOAD_USER_CONFIGURATION, e, new Object[] {user.getLogin()});
      }
    }    
  }

  
  
  /**
   * @effects 
   *  
   * @version 5.2
   */
  @Override
  public void postCreateFunctionalModules() {
    // do the super-type thing first
    super.postCreateFunctionalModules();
    
    // then some extra tasks
    Map<ApplicationModule, ControllerBasic> funcControllerMap = getFuncControllerMap();
    
    for (Entry<ApplicationModule, ControllerBasic> e : funcControllerMap.entrySet()) {
      ApplicationModule funcMod = e.getKey();
      ControllerBasic ctrl = e.getValue();
      // if funcMod has a dependent service module then configure it for funcMod
      Iterator<ApplicationModule> depServModules = funcMod.getDependentServiceModulesIterator();
      if (depServModules != null) { // ctrl has dependent service modules
        while (depServModules.hasNext()) {
          ApplicationModule depServModule = depServModules.next();
          Controller servCtrl = (Controller) lookUpByModuleWithPermission(depServModule);
          servCtrl.addClientModule(ctrl);
        }
      }      
    }
  }

  /**
   * This method is used to save GUI configuration settings to data source. The settings 
   * are loaded at run-time by {@link #loadUserConfiguration()}.
   * 
   * @requires gui != null
   * @effects store <tt>changedSettings</tt> of this.gui.config to data source
   * 
   * <br>
   *          Throws NotPossibleException if failed to do so.
   * @version 
   *  - 2.7.3
   *  <br>
   *  - 2.8: added support for user-specific configuration settings
   */
  @Override
  public void saveGuiConfig(Map<DAttr, Object> changedSettings)
      throws NotPossibleException {
    if (!hasGUI())
      return;

    // if security is enabled then save settings to UserRegion
    // else save settings directly to Region
    
    boolean saveDefault = !isSecurityEnabled() || !isLoggedIn(); //  no security OR not logged in
     
    if (saveDefault) {
      // use default storage
      super.saveGuiConfig(changedSettings);
    } else {
      // has security
      DODMBasic dodm = getDodm();
      DOMBasic dom = dodm.getDom();
      DomainUser user = getDomainUser();
      Region guiCfg = getGUI().getGUIConfig();
      
      // if UserRegion exists then update it, else add new one
      Class<UserRegion> userCfgCls = UserRegion.class;
      UserRegion userCfg;
      try {
        userCfg = dom.retrieveObject(userCfgCls, 
            new String[] {UserRegion.AttributeName_User, UserRegion.AttributeName_Region},
            new Op[] {Op.EQ, Op.EQ},
            new Object[] {user, guiCfg}
            );
      } catch (Exception e) {
        throw new NotPossibleException(
            NotPossibleException.Code.FAIL_TO_SAVE_GUI_CONFIG, e, new Object[] { gui.getTitle() });
      }
      
      if (userCfg == null) {
        // add new 
        userCfg = new UserRegion(user, guiCfg);
        // update config properties from the attribute values
        userCfg.setAttributeVals(changedSettings);
        
        PropertySet attributeVals = userCfg.getRegionAttributeVals();
        
        try {
          SwTk.createPropertySet(dodm, attributeVals, true, 0);
          dom.addObject(userCfg);
        } catch (Exception e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_SAVE_GUI_CONFIG, e, new Object[] { gui.getTitle() });
        } 
      } else {
        // update
        userCfg.setAttributeVals(changedSettings);
        PropertySet attributeVals = userCfg.getRegionAttributeVals();

        try {
          Collection<Property> props = attributeVals.getProps();
          for (Property prop : props) {
            dom.updateObject(prop, null);
          }
          dom.updateObject(userCfg, null);
        } catch (Exception e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_SAVE_GUI_CONFIG, e, new Object[] { gui.getTitle() });
        }        
      }
    }
  }

  /**
   * @effects
   *  if security is enabled  
   *    return the security domain class
   *  else
   *    return <tt>null</tt>
   */
  public Security getSecurity() {
    SecurityController secCtl = (SecurityController) lookUp(Security.class);
    if (secCtl != null)
      return secCtl.getSecurity();
    else
        return null;
  }
  
  /**
   * @effects 
   *  if security is enabled AND {@link #isLoggedIn()}
   *    return the logged in user
   *  else
   *    return <tt>null</tt>
   *    
   * @version 2.8
   */
  public DomainUser getDomainUser() {
    if (isSecurityEnabled() && isLoggedIn()) {
      Security sec = getSecurity();
      if (sec != null)
        return sec.getDomainUser();
    }
    
    return null;
  }

  /**
   * @effects 
   *  add <tt>clientModule</tt> to this.{@link #clientModule}.
   *      
   * @version 5.2
   */
  public void addClientModule(Module clientModule) {
    if (clientModules == null)
      clientModules = new ArrayList<>();
    
    clientModules.add(clientModule);
  }
  
  /**
   * @effects 
   *  if exists {@link Module}s that are the client modules of this
   *    return them
   *  else
   *    return null
   *    
   * @version 5.2
   */
  public Iterator<Module> getClientModules() {
    if (clientModules != null) {
      return clientModules.iterator();
    } else {
      return null;
    }
  }
}
