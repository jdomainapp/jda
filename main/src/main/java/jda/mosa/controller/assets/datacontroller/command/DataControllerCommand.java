package jda.mosa.controller.assets.datacontroller.command;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.SecurityException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.Security;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.view.assets.JDataContainer;

/**
 * @overview
 *  Represents a data controller command 
 *  
 * @author dmle
 * @version 3.0
 */
public abstract class DataControllerCommand<C> {
  private DataController<C> dataController;
  
  public DataControllerCommand(DataController<C> dctl) {
    this.dataController = dctl;
  }
  
  /**
   * @effects 
   *  create and return a <tt>DataControllerCommand</tt> whose type is <tt>cmdCls</tt> and that 
   *  is to support <tt>dataController</tt>.
   */
  public static <T extends DataControllerCommand, C> T createInstance(Class<T> cmdCls, DataController<C> dataController) throws NotPossibleException {
    try {
      // invoke the constructor to create object 
      T instance = cmdCls.getConstructor(DataController.class).newInstance(dataController);
      
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {cmdCls.getSimpleName(), dataController});
    }
  }
  
  /**
   * @effects 
   *  return the {@link DataController} of this command
   */
  public DataController<C> getDataController() {
    return dataController;
  }


  /**
   * User module is the root module of the containment tree that contains {@link #getDataController()}. 
   * That is, this module has the top-level data controller of {@link #getDataController()} as its root data controller.
   * 
   * @effects 
   *  return the domain class of the user module of {@link #getDataController()} 
   *  
   * @version 3.3
   */
  protected Class getUserModuleModel() {
    return getDataController().getUser().getDomainClass();
  }


  /**
   * User module is the root module of the containment tree that contains {@link #getDataController()}.
   * That is, this module has the top-level data controller of {@link #getDataController()} as its root data controller.
   * 
   * @effects 
   *  return the current object of the user module of {@link #getDataController()}
   *  
   *  <br>if no object is found there then return <tt>null</tt> 
   *  
   * @version 3.3
   */
  protected Object getUserModuleCurrentObject() {
    return getDataController().getUser().getRootDataController().getCurrentObject();
  }
  
  /**
   * @effects 
   *  return {@link #getDataController()}.getDomainClass
   * @version 3.3
   */
  protected Class getDomainClass() {
    return getDataController().getDomainClass();
  }
  
  /**
   * @effects 
   *  return the target data controller configured for <tt>dctl</tt> such that both 
   *  are children of the same parent data controller; 
   *  or return <tt>null</tt> if no configuration is found for the target data controller
   *  
   *  <p>throws 
   *  NotFoundException if the target data controller is not found  
   */
  protected DataController getTargetDataController(DataController dctl) throws NotFoundException {
    JDataContainer dcont = dctl.getDataContainer();
    String targetAttribName = dcont.getContainerConfig().getProperty(PropertyName.view_objectForm_targetForm, 
        String.class, null);
    
    final DSMBasic dsm = dctl.getDodm().getDsm();

    DataController parentDctl = dctl.getParent();

    if (targetAttribName == null) {
      return null;
    } else {
      DAttr targetAttrib = dsm.getDomainConstraint(parentDctl.getDomainClass(), targetAttribName);
      DataController targetDctl = dctl.getParent().getChildController(targetAttrib);
      
      if (targetDctl == null)
        throw new NotFoundException(NotFoundException.Code.TARGET_OBJECT_FORM_NOT_FOUND, new Object[] {dcont});
      
      return targetDctl;
    }
  }

  /**
   * @effects 
   *  look up and return the <b>root</b> {@link DataController} of the <b>primary</b> module(<tt>c</tt>); 
   *  throws NotFoundException if no such controller exists or 
   *  SecurityException if user has no permission to access the controller
   *   
   * @version 3.2
   */
  protected <T> DataController<T> lookUpRootDataController(Class<T> c) throws NotFoundException, SecurityException {
    //ControllerBasic<C> mainCtl = getDataController().getCreator().getMainController();

    ControllerBasic<T> ctl = ControllerBasic.lookUpPrimary(c);
    
    if (ctl != null) {
      DataController<T> dctl = ctl.getRootDataController();
      
      return dctl;
    } else {
      throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND, new Object[] {c});
    }
  }
  

  /**
   * @effects 
   *  if exists an ancestor module <tt>m</tt> of <tt>this</tt> whose domain class is <tt>domainCls</tt>
   *    return m.dataController
   *  else
   *    throws NotFoundException
   *  
   * @version 3.2
   */
  protected <T> DataController<T> lookUpAncestorDataController (Class<T> domainCls) throws NotFoundException {
    DataController dctl = getDataController();
    DataController parent = dctl.getParent();
    Class parentCls;
    while (parent != null) {
      parentCls = parent.getDomainClass();
      if (parentCls == domainCls) {// TODO: relax this check to support type hierarchy if needed
        // found 
        return (DataController<T>) parent;
      } else {
        // keep going...
        parent = parent.getParent();
      }
    }
    
    // not found
    throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND, new Object[] {domainCls});
  }
  
  /**
   * @effects 
   *  return {@link #dataController.dodm}
   */
  protected DODMBasic getDodm() {
    return dataController.getDodm();
  }

  /**
   * This method is needed by {@link DataControllerCommand} that needs to perform {@link DataController#createObject(Object[])}
   * directly from code. 
   * 
   * <br><b>Note: DONOT</b> call this method if domain objects are created from the View using {@link #createNewObject(DataController, Object[])} 
   * 
   * @effects 
   *  prepare <tt>dctl</tt> ready for calling {@link DataController#createObject(Object[])} 
   *  directly from code 
   *  
   *  <p> throws NotPossibleException if data source is not connected, 
   *  DataSourceException if failed to operate on data source
   *  
   * @requires
   *  dctl is to be called with {@link DataController#createObject(Object[])} to create domain objects
   *   
   * @version 3.2
   */
  protected static void prepareForCreateObject(DataController dctl) throws NotPossibleException, DataSourceException {
    // make sure that object metadata is opened. 
    if (!dctl.isOpenMetadata()) {
      dctl.openMetadata();
    }
    
    // reset the object index if dctl has a view and if objects of this controller are indexable
    if (dctl.hasView())
      dctl.resetIndexCounter();
  }
  
  /**
   * <b>Note</b> this method <b>DOES-NOT</b> require the use of {@link #prepareForCreateObject(DataController)}. 
   * 
   * @effects 
   *  Create a new domain object by <tt>dctl</tt> using <tt>values</tt>, 
   *  <br>i.e.
   *  call {@link DataController#newObject()} followed by <br> 
   *  call {@link DataController#createObject(Object[])}
   * 
   * <p>Throws Exception if failed
   */
  protected static <T> T createNewObject(DataController<T> dctl, Object[] values) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    return createNewObject(dctl, values, true);
  }
  
  /**
   * This methods supports two modes of create new object: (1) interactive via object form and (2) non-interactive
   * by creating the object directly (without relying on the form) 
   * 
   * @effects 
   *  Create a new domain object by <tt>dctl</tt> using <tt>values</tt> in one of two ways:
   *  <pre>
   *  if initForm = true 
   *    call {@link DataController#newObject()} followed by <br> 
   *    call {@link DataController#createObject(Object[])}
   *  else
   *    call {@link #prepareForCreateObject(DataController)} followed by <br> 
   *    call {@link DataController#createObject(Object[])}
   * 
   * <p>Throws Exception if failed
   */
  protected static <T> T createNewObject(DataController<T> dctl, Object[] values, boolean initForm) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    if (initForm)
      dctl.newObject();
    else
      prepareForCreateObject(dctl);
    
    return dctl.createObject(values);
  }
  
  /**
   * This method extends {@link #createNewObject(DataController, Object[], boolean)} to support an option to shut the pop-up message. 
   * 
   * @effects 
   *  Create a new domain object by <tt>dctl</tt> using <tt>values</tt> in one of two ways:
   *  <pre>
   *  if initForm = true 
   *    call {@link DataController#newObject()} followed by <br> 
   *    call {@link DataController#createObject(Object[])}
   *  else
   *    call {@link #prepareForCreateObject(DataController)} followed by <br> 
   *    call {@link DataController#createObject(Object[])} </pre>
   * 
   * <p>if <tt>showPopUpMesg = false</tt> then turn off the pop-up message while creating.
   * 
   * <p>Throws Exception if failed
   */
  protected static <T> T createNewObject(DataController<T> dctl, Object[] values, boolean initForm, boolean showPopUpMesg) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    Boolean oldPop = null;
    
    if (!showPopUpMesg)
      oldPop = setPropertyShowPopUpMessage(dctl, Boolean.FALSE);
    
    try {
      if (initForm)
        dctl.newObject();
      else
        prepareForCreateObject(dctl);
      
      T obj = dctl.createObject(values);
      return obj;
    } catch (Exception e) {
      throw e;
    } finally {
      if (!showPopUpMesg)
        setPropertyShowPopUpMessage(dctl, oldPop);
    }
  }

  /**
   * A more convenient method for {@link #createNewObject(DataController, Object[], boolean, boolean)}.
   * 
   * @effects 
   *  let <tt>dctl = </tt> root data controller of <tt>c</tt>
   *  call {@link #createNewObject(DataController, Object[], boolean, boolean)} for <tt>(dctl, values, initForm, showPopUpMesg)</tt>
   *  
   *  <p>throws Exception if failed
   * @version 3.3
   */
  protected <T> void createNewObject(Class<T> c, Object[] values,
      final boolean initForm, final boolean showPopUpMesg) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    
    DataController<T> dctl = lookUpRootDataController(c);
    createNewObject(dctl, values, initForm, showPopUpMesg);
  }
  
  /**
   * @effects 
   *  Create and return an object of <tt>subType</tt> using the data entered on the object form 
   *  of this
   *  
   * @version 3.3
   */
  protected <S extends C> S createSubTypeObject(Class<S> subType) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    // TODO: for now assume that sub-type uses all the input data of the object form of this
    DataController<S> sdctl = lookUpRootDataController(subType);
    if (sdctl != null) {
      DataController<C> dctl = getDataController();
      S obj = sdctl.createObject(dctl);
      return obj;
    } else {
      throw new NotFoundException(NotFoundException.Code.DATA_CONTROLLER_NOT_FOUND, new Object[] {subType});
    }
  }

  /**
   * @effects 
   *  let <tt>dctl = </tt> root data controller of <tt>c</tt>
   *  call <tt>dctl.deleteObject(o)</tt>
   *  
   *  <p>throws Exception if failed
   * @version 3.3
   */
  protected <T> void deleteObject(Class<T> c, T o) throws NotPossibleException, NotFoundException, DataSourceException {
    DataController<T> dctl = lookUpRootDataController(c);
    
    boolean toConfirm = false;
    dctl.deleteObject(o, toConfirm);
  }
  
  /**
   * @effects 
   *  set value of property concerning "show pop-up message" for <tt>dctl.creator</tt> to <tt>newVal</tt> and 
   *  return the old value of this property
   *  
   * @version 3.2
   */
  protected static <T> T setPropertyShowPopUpMessage(DataController dctl, T newVal) {
    ControllerBasic ctl = dctl.getCreator();
    Object oldVal = ctl.setProperty("show.message.popup", newVal);
    
    return (T) oldVal;
  }
  
  /**
   * @effects 
   *  if security is enabled
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.3
   */
  protected boolean isSecurityEnabled() {
    return getDataController().getCreator().isSecurityEnabled();
  }
  
  /**
   * @effects 
   *  if security is enabled AND user is logged in
   *    return the user
   *  else
   *    throws SecurityException 
   *    
   * @version 3.3
   */
  protected DomainUser getCurrentUser() throws SecurityException {
    Security sec = getDataController().getCreator().getSecurity(); 
    if (sec != null)
      return sec.getDomainUser();
    
    throw new SecurityException(SecurityException.Code.NOT_LOGGED_IN);
  }

  /**
   * @effects 
   *  if security is enabled and user is logged in 
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *    
   * @version 3.3
   */
  protected boolean isLoggedIn() {
    ControllerBasic controller = getDataController().getCreator(); 
    return controller.isSecurityEnabled() && controller.isLoggedIn();
  }
  
  /**
   * @effects 
   *  run <tt>this</tt> using <tt>args</tt> as input (that were provided by <tt>src</tt>)
   *  <p>Note: <tt>src</tt> may be the same as <tt>this.</tt>{@link #dataController}
   */
  public abstract void execute(DataController src, Object...args) throws Exception;
}
