package jda.mosa.view.assets;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple2;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.common.types.tree.Node;
import jda.modules.common.types.tree.Tree;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import jda.mosa.view.View;
import jda.mosa.view.assets.builder.LabelledContInfo;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.panels.card.CardPanel;
import jda.mosa.view.assets.tables.JDataTable;

/**
 * @overview
 *   A tool kit class used for operations concerning {@link JDataContainer}.
 *   
 * @author dmle
 * @version 3.0
 */
public class DataContainerToolkit {
  /**
   * @version 5.1
   *  used internally for operations that need to process the {@link JDataContainer} hierarchy.
   */
  private static Stack<JDataContainer> processedContainerBuffer;

  private DataContainerToolkit() {}

  /**
   * @requires 
   *  dataContainer != null /\ prop != null /\ val != null
   *  
   * @effects 
   *  if exist child/descendant data container(s) <tt>d</tt> of <tt>dataContainer</tt> s.t. 
   *    <tt>d.containerCfg.getProperty(propName) eq val</tt>
   *    make <tt>d</tt> visible
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public static <T> boolean showChildContainerIterator(
      JDataContainer dataContainer, 
      PropertyName prop, Class<T> valType, T val) {
    Iterator<JDataContainer> childContainers = dataContainer.getChildContainerIterator();
    
    boolean foundContainer = false;
    
    if (childContainers != null) {
      ControllerBasic controller = dataContainer.getController().getCreator();
      
      JDataContainer childCont;
      Region containerCfg;
      Object propVal;
      
      while (childContainers.hasNext()) {
        childCont = childContainers.next();
        containerCfg = childCont.getContainerConfig(); 
        propVal = containerCfg.getProperty(prop, valType, null);
        
        if (propVal != null && val != null && propVal.equals(val)) {
          controller.showDataContainer(childCont);
          if (!foundContainer) foundContainer = true;
        }
        
        // recursive: repeat for all descendant containers (if any)
        boolean foundDescContainer = showChildContainerIterator(childCont, prop, valType, val);
        if (!foundContainer) foundContainer = foundDescContainer;
      }
    }
    
    return foundContainer;
  }

  /**
   * This method may return a value that is not the same as the actual value recorded by the data field. 
   * To obtain that value, use {@link #getDataFieldActualValue(JDataContainer, DAttr)}.
   * 
   * @requires 
   *  attrib is a valid domain attribute that is displayed on dataContainer
   *  
   * @effects 
   *    retrieve the <b>display value</b> of <tt>attrib</tt> on <tt>dataContainer</tt>  
   *    (i.e. that displayed by a data field of <tt>this.dataContainer</tt> that is mapped to <tt>attrib</tt>) 
   *  else
   *    return <tt>null</tt>
   * @version 3.0
   */
  public static Object getDataFieldValue(JDataContainer dataContainer,
      DAttr attrib) {
    JComponent dataComp = dataContainer.getComponent(attrib);
    if (dataComp != null && dataComp instanceof JDataField) {
      JDataField df = (JDataField) dataComp;
      Object val = df.getValue();
      if (val != null && df.isSupportValueFormatting()) {
        // format value 
        val = df.getFormattedValue(val);
      }
      
      return val;
    }
    
    return null;
  }

  /**
   * This method return the actual value that would be used to set to the attribute <tt>attrib</tt> of a domain class. 
   * 
   * @requires 
   *  attrib is a valid domain attribute that is displayed on dataContainer
   *  
   * @effects 
   *    retrieve the <b>actual value</b> of <tt>attrib</tt> on <tt>dataContainer</tt>  
   *    (i.e. that is recorded by the data field of <tt>this.dataContainer</tt> that is mapped to <tt>attrib</tt>); 
   *  else
   *    return <tt>null</tt>
   * @version 3.2c
   */
  public static Object getDataFieldActualValue(JDataContainer dataContainer, DAttr attrib) {
    JComponent dataComp = dataContainer.getComponent(attrib);
    if (dataComp != null && dataComp instanceof JDataField) {
      JDataField df = (JDataField) dataComp;
      Object val = df.getValue();
      
      return val;
    }
    
    return null;
  }

  /**
   * @effects 
   *   if there defined the initial value of the data field in <tt>dataContainer</tt> that is of the attribute named <tt>attribName</tt>
   *    return it (casted to the expected type <tt>valueType</tt>)
   *   else
   *    return <tt>null</tt>
   * 
   * @version 3.2c
   * @param class1 
   */
  public static <T> T getDataFieldInitialValue(JDataContainer dataContainer, DAttr attrib, 
      Class<T> valueType) {
    JComponent dataComp = dataContainer.getComponent(attrib);
    if (isDataField(dataComp)) {
      JDataField df = (JDataField) dataComp;
      
      return (T) df.getInitValue();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  Implements a handler for {@link JDataContainer#handleDataFieldValueChanged(JDataField)} that is 
   *  invoked on <tt>(container,df)</tt>.
   *  
   *  <p>throws NotPossibleException if failed.
   * @version 3.1
   */
  public static void handleDataFieldValueChanged(JDataContainer container, JDataField df) throws 
    NotPossibleException {
    DataController dctl = container.getController();
    
    // v3.2: if container.controller is configured with a handler for this event then use it 
    // otherwise use a default behaviour (however, this should also be moved to a handler in the future)
    DataControllerCommand handler = dctl.lookUpCommand(PropertyName.controller_dataController_dataFieldValueChangedHandler.getLastName());
    if (handler != null) {
      try {
        handler.execute(dctl, df);
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
            new Object[] {container, "handleDataFieldValueChanged", df});
      }
    } else {
      // TODO: this code is copied from the InputHelper.fieldValueChanged but not sure if it is needed
      // perhaps the last field editing event ?
      container.handleDataFieldEditing(null, df);
      
      // if data field supports auto-update then update the domain object with the data field's value
      RegionDataField dfCfg = df.getDataFieldConfiguration();
      if (!dctl.isCurrentObjectNull() && !dctl.isCreating()) {
        // currentObj is set and that container is not currently in create-new-object mode,
        // i.e. container is in editing mode on a domain object
        boolean autoUpdate = dfCfg.getProperty(PropertyName.view_objectForm_dataField_autoUpdate, Boolean.class, Boolean.FALSE);
        
        if (autoUpdate) {
          //System.out.println("auto-update: " + df);
          DAttr attrib = df.getDomainConstraint();
          Object value = df.getValue();
          boolean silent = true;
          try {
            dctl.updateObject(attrib, value, silent);
          } catch (ConstraintViolationException | DataSourceException e) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_UPDATE_OBJECT_ATTRIBUTE, e, 
                new Object[] {dctl.getDomainClass().getSimpleName(), attrib.name(), value});
          }
        }
      }
      
      // if data field has a target specified then inform target data field
      String target = dfCfg.getProperty(PropertyName.view_objectForm_dataField_target, String.class, null);
      if (target != null) {
        // target specified
        DSMBasic dsm = container.getController().getCreator().getDomainSchema();
        Class domainCls = container.getController().getDomainClass();
        
        DAttr targetAttrib;
        
        try {
          targetAttrib = dsm.getDomainConstraint(domainCls, target);
        } catch (NotFoundException e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
              new Object[] {container, "handleDataFieldValueChanged", df});
        }

        //TODO: generalise this for all fields if needed
        // for now support bounded attribute (bindable data field) and the refresh action
        if (targetAttrib.type().isDomainType()) {
          container.refreshTargetDataBindingOfAttribute(targetAttrib);
        }
      }
    }

// v3.2: moved into else case (above)    
//    // TODO: this code is copied from the InputHelper.fieldValueChanged but not sure if it is needed
//    // perhaps the last field editing event ?
//    container.handleDataFieldEditing(null, df);
//    
//    // if data field supports auto-update then update the domain object with the data field's value
//    RegionDataField dfCfg = df.getDataFieldConfiguration();
//    if (!dctl.isCurrentObjectNull() && !dctl.isCreating()) {
//      // currentObj is set and that container is not currently in create-new-object mode,
//      // i.e. container is in editing mode on a domain object
//      boolean autoUpdate = dfCfg.getProperty(PropertyName.view_objectForm_dataField_autoUpdate, Boolean.class, Boolean.FALSE);
//      
//      if (autoUpdate) {
//        //System.out.println("auto-update: " + df);
//        DomainConstraint attrib = df.getDomainConstraint();
//        Object value = df.getValue();
//        boolean silent = true;
//        try {
//          dctl.updateObject(attrib, value, silent);
//        } catch (ConstraintViolationException | DataSourceException e) {
//          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_UPDATE_OBJECT_ATTRIBUTE, e, 
//              new Object[] {dctl.getDomainClass().getSimpleName(), attrib.name(), value});
//        }
//      }
//    }
//    
//    // if data field has a target specified then inform target data field
//    String target = dfCfg.getProperty(PropertyName.view_objectForm_dataField_target, String.class, null);
//    if (target != null) {
//      // target specified
//      DSMBasic dsm = container.getController().getCreator().getDomainSchema();
//      Class domainCls = container.getController().getDomainClass();
//      
//      DomainConstraint targetAttrib;
//      
//      try {
//        targetAttrib = dsm.getDomainConstraint(domainCls, target);
//      } catch (NotFoundException e) {
//        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
//            new Object[] {container, "handleDataFieldValueChanged", df});
//      }
//
//      //TODO: generalise this for all fields if needed
//      // for now support bounded attribute (bindable data field) and the refresh action
//      if (targetAttrib.type().isDomainType()) {
//        container.refreshTargetDataBindingOfAttribute(targetAttrib);
//      }
//    }
    
  }

  /**
   * @requires 
   *  dataContainer != null
   *  
   * @effects 
   *  clear all data components of <tt>dataContainer</tt> that are mapped to
   *  ALL domain attributes except those in <tt>exclAttribs</tt>, and 
   *  if <tt>withChildren = true</tt> and any of these are sub-forms then 
   *  recursively clear the GUIs of them and those of their descendants
   * @version 
   * - 3.1: created <br>
   * - 3.3: improved to allow exclAttribs = null
   */
  public static void clearExceptFor(JDataContainer dataContainer,
      Collection<DAttr> exclAttribs, boolean withChildren) {
    
    if (dataContainer == null 
        // v3.3|| exclAttribs == null || exclAttribs.isEmpty()
        )
      return;
    
    DataController dctl = dataContainer.getController();
    DSMBasic dsm = dctl.getDodm().getDsm();
    
    Collection<DAttr> attribs = dsm.getDomainConstraints(dctl.getDomainClass());
    Collection<DAttr> inclAttribs = new ArrayList();
    // identify the included domain attributes
    for (DAttr attrib: attribs) {
      if ((exclAttribs == null) || (exclAttribs != null &&  // v3.3 
          !exclAttribs.contains(attrib))) {
        // an attribute whose data component needs clearing
        inclAttribs.add(attrib);
      }
    }
    
    // identify the data components
    Component[] inclComps = dataContainer.getComponents(inclAttribs);
        
    // determine the link component and value (if any)
    DAttr linkAttrib = dataContainer.getLinkAttribute();
    JComponent linkComponent = null;
    if (linkAttrib != null) {
      linkComponent = dataContainer.getComponent(linkAttrib);
    }
    Object linkValue = getLinkValue(dataContainer);
    
    // clear the data components
    JDataContainer subForm;
    JDataField df;
    for (Component dataComp : inclComps) {
      if (isDataField(dataComp)) {
        // data field
        df = (JDataField) dataComp;
        if (df == linkComponent) {
          if (linkValue != null)
            df.setValue(linkValue);
          else
            df.clear();
        } else {
          df.clear();
        }
      } else {
        // a sub-form: only clear if withChildren = true 
        if (withChildren) {
          subForm = toDataContainer(dataComp);
          if (subForm != null)
            subForm.getController().clearGUIOnly(true);
        }
      }
    }
  }

  /**
   * @requires 
   *  dataContainer != null
   * @effects 
   *  if <tt>dataContainer</tt> is a sub-form
   *    return the current <code>Object</code> of <tt>dataContainer.parent</tt>,
   *    which is used as the linked value for the link column of <tt>dataContainer</tt>
   *  else
   *    return <tt>null</tt>
   */
  public static Object getLinkValue(JDataContainer dataContainer) {
    if (dataContainer == null)
      return null;
    
    DataController controller = dataContainer.getController();
    Object parentObject = controller.getParentObject();
    if (parentObject != null) { // nested
      return parentObject;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if <tt>dataComp</tt> is a {@link JDataContainer}
   *    return casting to {@link JDataContainer}
   *  else
   *    return <tt>null</tt>
   *  @version 3.1
   */
  public static JDataContainer toDataContainer(Component dataComp) {
    if (dataComp == null)
        return null;
    
    if (dataComp instanceof JDataContainer) {
      // nested container
      return ((JDataContainer) dataComp);
    } else if (dataComp instanceof JScrollPane) {
      // scrollable nested container
      JDataContainer dcont = (JDataContainer) ((JScrollPane) dataComp).getViewport().getView();
      
      return dcont;
    } else {
      // not a data container
      return null;
    }
  }

  /**
   * @effects 
   *  if <tt>dataComp</tt> is a {@link JDataField}
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  @version 3.1
   */
  public static boolean isDataField(Component dataComp) {
    return (dataComp != null) && (dataComp instanceof JDataField);
  }

  /**
   * @effects 
   *  if the data field of the domain attribute <tt>dc</tt> realises the dependent end of an one-one association
   *  of its <tt>domainClass</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt> 
   *  
   * @version 3.3
   */
  public static boolean isDataFieldRealisingADependentAttribute(final DAttr dc, 
      final Class domainClass, final DSMBasic dsm) {
    return dsm.isDeterminedByAssociate(domainClass, dc);
  }
  
  /**
   * @requires 
   *  <tt>rootContainer != null /\ targetCls != null</tt>
   * @effects 
   *  if exists a descendant {@link JDataContainer} in the containment hierarchy of <tt>rootContainer</tt> (inclusive of <tt>rootContainer</tt>)
   *  whose bounded domain class is <tt>domainCls</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   * @version 3.2c
   */
  public static JDataContainer getDescendantContainer(JDataContainer rootContainer, Class domainCls) {
    if (rootContainer == null || domainCls == null)
      return null;
    
    if (rootContainer.getController().getDomainClass().equals(domainCls)) {
      // rootContainer is the one!
      return rootContainer;
    }
    
    JDataContainer target = null;
    JDataContainer child;
    Iterator<JDataContainer> children = rootContainer.getChildContainerIterator();
    if (children != null) {
      while (children.hasNext()) {
        child = children.next();
        if (child.getController().getDomainClass().equals(domainCls)) {
          // found a match
          target = child;
          break;
        } else if (child.getController().isNested()) {
          // has children, recursive search...
          target = getDescendantContainer(child, domainCls);
          if (target != null) {
            // found target in recursive search
            break;
          }
        }
      }
    }
    
    return target;
  }

  /**
   * @effects 
   *  if exists a descendant {@link JDataContainer} of <tt>rootContainer</tt> whose 
   *  label equals <tt>contLabel</tt>
   *    return it 
   *  else
   *    return null
   * @version 5.2 
   */
  public static JDataContainer getDescendantContainer(
      JDataContainer rootContainer, LabelledContInfo selectedContInfo) {
    if (rootContainer == null || selectedContInfo == null)
      return null;
    
    //String contLabel = selectedContInfo.getFirst();
    JDataContainer selectedCont = selectedContInfo.getSecond();
    
    if (rootContainer.equalsStrictly(selectedCont)) {
      // rootContainer is the one!
      return rootContainer;
    }
    
    JDataContainer target = null;
    JDataContainer child;
    Iterator<JDataContainer> children = rootContainer.getChildContainerIterator();
    if (children != null) {
      while (children.hasNext()) {
        child = children.next();
        if (child.equalsStrictly(selectedCont)) {
          // found a match
          target = child;
          break;
        } else if (child.getController().isNested()) {
          // has children, recursive search...
          target = getDescendantContainer(child, selectedContInfo);
          if (target != null) {
            // found target in recursive search
            break;
          }
        }
      }
    }
    
    return target;
  }
  
  /**
   * @effects 
   *  if <tt>dataContainer</tt> is not a root container the containment hierarchy of which it is a part
   *    return the {@link JDataContainer} that is the root of this containment hierarchy
   *  else
   *    return <tt>dataContainer</tt>
   *    
   * @version 3.2c
   */
  public static JDataContainer getRootContainer(JDataContainer dataContainer) {
    JDataContainer parent = dataContainer.getParentContainer();
    if (parent == null) {
      // dataContainer is the root
      return dataContainer;
    } else {
      // (recursively) ask the parent for who the root is
      return getRootContainer(parent);
    }
    /*
    // more efficient code BUT does not always work b/c root container may not be set
     * when this is invoked
    JDataContainer parent = dataContainer.getParentContainer();
    if (parent == null) {
      // dataContainer is the root
      return dataContainer;
    } else {
      // root is the root of ther user view
      return dataContainer.getController().getUserGUI().getRootContainer();
    }
    */
  }
  
  /**
   * @requires 
   * <tt>dataContainer != null /\ the scope-defs of container hierarchy of dataContainer (if any) have been 
   * defined in dataContainer.config</tt>
   * 
   * @modifies {@link #processedContainerBuffer}
   * 
   * @effects 
   *  determine and return the editability of <tt>dataContainer</tt> from its configuration and from the configurations 
   *  of all its ancestors (if any), i.e. <pre>
   *  if exists one ancestor whose editable = false
   *    return false
   *  else
   *    return the configured editable of <tt>dataContainer</tt>
   *  </pre>
   *  
   * @version 
   * - 3.3c: created<br>
   * - 5.1: improved to overcome cycles in the container graph
   */
  public static boolean getDataContainerEditableFromConfigHierarchy(
      final JDataContainer dataContainer) {
    /* v5.1: improved
    if (dataContainer == null) return false;

    boolean editable = true;
    
    ApplicationModule userModule = dataContainer.getController().getUser().getApplicationModule();
    Region containerCfg = dataContainer.getContainerConfig();
    ScopeDef containerScopeDef = containerCfg.lookUpUserModuleScope(userModule); 
        
    Boolean scopeDefEditable = null;
    
    if (containerScopeDef != null) {
      scopeDefEditable = containerScopeDef.isEditable();
      if (scopeDefEditable != null)
        editable = scopeDefEditable;
    }

    if (scopeDefEditable == null) { // (1) if container-scope-def's editable is specified then takes precedence 
      // check ancestor's editable (if any)
      if (dataContainer.getController().isNestedIn()) {
        // has ancestors
        JDataContainer parentContainer = dataContainer.getParentContainer();
        boolean ancestorEditable = getDataContainerEditableFromConfigHierarchy(parentContainer);
        
        if (!ancestorEditable) { // (2) if ancestor's editable is false then takes precendence
          editable = false;
        } else {  // (3) use containerCfg's editable
          editable = containerCfg.getEditable();
        }
      } else {
        // top-level container: use containerCfg's editable
        editable = containerCfg.getEditable();
      }
    }
    
    return editable;
    */
    // TODO ? put processedBuffer on the stack of this method if concurrent invocation is used
    if (processedContainerBuffer != null) {
      processedContainerBuffer.clear();
    } else {
      processedContainerBuffer = new Stack<>();
    }
    
    return getDataContainerEditableFromConfigHierarchy(dataContainer, processedContainerBuffer);
  }

  /**
   * @requires 
   * <tt>dataContainer != null /\ the scope-defs of container hierarchy of dataContainer (if any) have been 
   * defined in dataContainer.config</tt>
   * 
   * @effects 
   *  determine and return the editability of <tt>dataContainer</tt> from its configuration and from the configurations 
   *  of all its ancestors (if any), i.e. <pre>
   *  if exists one ancestor whose editable = false
   *    return false
   *  else
   *    return the configured editable of <tt>dataContainer</tt>
   *  </pre>
   *  
   * @version 
   * - 5.1: created
   */
  private static boolean getDataContainerEditableFromConfigHierarchy(
      final JDataContainer dataContainer, Stack<JDataContainer> processedBuffer) {
    if (dataContainer == null) return false;

    boolean editable = true;
    
    ApplicationModule userModule = dataContainer.getController().getUser().getApplicationModule();
    Region containerCfg = dataContainer.getContainerConfig();
    ScopeDef containerScopeDef = containerCfg.lookUpUserModuleScope(userModule); 
        
    Boolean scopeDefEditable = null;
    
    if (containerScopeDef != null) {
      scopeDefEditable = containerScopeDef.isEditable();
      if (scopeDefEditable != null)
        editable = scopeDefEditable;
    }

    if (scopeDefEditable == null) { // (1) if container-scope-def's editable is specified then takes precedence 
      // check ancestor's editable (if any)
      if (dataContainer.getController().isNestedIn()) {
        // has ancestors
        JDataContainer parentContainer = dataContainer.getParentContainer();
        if (processedBuffer.contains(parentContainer)) {
          // parentContainer already processed
          editable = containerCfg.getEditable();
        } else {
          // parentContainer not yet processed
          processedBuffer.push(parentContainer);
          
          boolean ancestorEditable = getDataContainerEditableFromConfigHierarchy(parentContainer, processedBuffer);
          
          if (!ancestorEditable) { // (2) if ancestor's editable is false then takes precendence
            editable = false;
          } else {  // (3) use containerCfg's editable
            editable = containerCfg.getEditable();
          }
        }
        
      } else {
        // top-level container: use containerCfg's editable
        editable = containerCfg.getEditable();
      }
    }
    
    return editable;
  }
  
  /**
   * @effects 
   *  if <tt>dataContainer</tt> is contained in container (e.g. tab group) that contains multiple parts, 
   *  one of which containing dataContainer 
   *    select this part
   *  else
   *    do nothing
   *    
   * @version 
   * - 4.0: created <br>
   * - 5.2: updated to support CardPanel
   */
  public static void updateContainerOnVisibilityChange(final JDataContainer dataContainer) {
    Component comp = dataContainer.getGUIComponent();
    final Component dcomp = comp;
    Container parent = comp.getParent();
    
    while (parent != null) {
      if (parent instanceof JTabbedPane) {
        // found such a container: select the tab
        JTabbedPane tabGroup = (JTabbedPane) parent;
        
        GUIToolkit.updateTabOnComponentVisible(tabGroup, dcomp);
        break;
      } 
      // v5.2:
      else if (parent instanceof CardPanel) {
        // a card-layout panel: perform the card button's command
        CardPanel cardPanel = (CardPanel) parent;
        GUIToolkit.updateCardOnComponentVisible(cardPanel, dcomp);
      }
      // TODO: support other types of container here if needed
      comp = parent;
      parent = comp.getParent();
    }
    
  }

  /**
   * @effects 
   *  if <tt>container</tt> equals to <tt>obj</tt>
   *    return true
   *  else
   *    return false
   * @version  5.1
   */
  public static boolean equals(JDataContainer container, Object obj) {
    return obj != null && 
        (obj.getClass() == container.getClass()) &&
        container.getController().equals(((JDataContainer)obj).getController());
  }

  /**
   * @effects 
   *  Implements the method {@link JDataContainer#onNewObject()}. 
   *   
   * @version 
   * - 5.1c: created <br>
   * - 5.2: improved to set all except auto fields
   */
  public static void onNewObject(JDataContainer container) {
    // get all the immutable, non-auto data fields
    // make them 'view-editable'
    //v5.2: setImmutableNonAutoCompsEditableView(container, true);
    setNonAutoCompsEditable(container, true);
  }

  /**
   * @effects 
   *  Implements the method {@link JDataContainer#onCreateObject(JDataContainer, Object)}. 
   *   
   * @version 
   * - 5.1c: Created <br>
   * - 5.2: improved to set all except auto fields
   */
  public static void onCreateObject(JDataContainer container, Object obj) {
    // reverse editabilty setting of all the immutable, non-auto data fields
    // to their original settings (non-editable)
    // v5.2 : setImmutableNonAutoCompsEditableView(container, false);
    // to support 2 cases: mutable class and immutable class
    // if container.getDomainCls is immutable then sets all non-auto fields to non-editable
    DSMBasic dsm = container.getController().getDodm().getDsm();
    Class domainCls = container.getController().getDomainClass();

    if (dsm.isEditable(domainCls)) { // mutable domain class
      // as before (above)
      setImmutableNonAutoCompsEditableView(container, false);
    } else { // immutable domain class
      setNonAutoCompsEditable(container, false);
    }
  }
  
  /**
   * @effects 
   *  Implements {@link JDataContainer#onCancel()}.
   *  
   * @version 
   * - 5.1c: Created <br>
   * - 5.2: improved to set all except auto fields
   */
  public static void onCancel(JDataContainer container) {
    // similar to onCreateObject...
    // get all the immutable, non-auto data fields
    // make them NOT 'view-editable'
    // v5.2: setImmutableNonAutoCompsEditableView(container, false);
    
    // to support 2 cases: mutable class and immutable class
    // if container.getDomainCls is immutable then sets all non-auto fields to non-editable
    DSMBasic dsm = container.getController().getDodm().getDsm();
    Class domainCls = container.getController().getDomainClass();

    if (dsm.isEditable(domainCls)) { // mutable domain class
      // as before (above)
      setImmutableNonAutoCompsEditableView(container, false);
    } else { // immutable domain class
      setNonAutoCompsEditable(container, false);
    }
  }
  
  /**
   * @effects 
   *  make all <b>immutable, non-auto</b> data field components of <tt>container</tt> have their 'view-editable'
   *  setting set to <tt>tf</tt>
   *  
   * @version 
   * - 5.1c: created <br>
   */
  private static void setImmutableNonAutoCompsEditableView(
      JDataContainer container, boolean tf) {
    Map<DAttr,Component> comps = container.getComps(null);

    if (comps != null) {
      for (Entry<DAttr, Component> e : comps.entrySet()) {
        DAttr attr = e.getKey();
        Component c = e.getValue(); 
        if (attr.mutable() == false && attr.auto() == false && isDataField(c)) {
          // immutable, non-auto data field
          ((JDataField)c).setEditableView(tf);
        }
      }
    }    
  }
 
  /**
   * @effects 
   *  make all <b>non-auto</b> data field components of <tt>container</tt> have their 'view-editable'
   *  setting set to <tt>tf</tt> (if not already)
   *  
   * @version 
   * - 5.2: created <br>
   */
  private static void setNonAutoCompsEditable(
      JDataContainer container, boolean tf) {
    Map<DAttr,Component> comps = container.getComps(null);

    if (comps != null) {
      for (Entry<DAttr, Component> e : comps.entrySet()) {
        DAttr attr = e.getKey();
        Component c = e.getValue(); 
        if (attr.auto() == false && isDataField(c)) { // non-auto data field
          JDataField f = (JDataField) c;
          if (f.isViewEnabled() != tf) {
            f.setEditableView(tf);
          }
        }
      }
    }    
  }

  /**
   * @effects 
   *  create and return {@link Tree} whose nodes are {@link Tuple2} objects containing 
   *  labels of the descendant {@link JDataContainer}s of <tt>view</tt> and those descendant objects themselves,
   *  and whose edges are containments among these containers.
   *  
   * @version 5.2
   */
  public static Tree<LabelledContInfo> getViewContainmentTree(final View view) {
    if (view == null) return null;
    
    JDataContainer cont = view.getRootContainer();
    
    Node<LabelledContInfo> root = new Node(new LabelledContInfo(cont.getLabel(), cont));
    Tree<LabelledContInfo> ctree = new Tree<>(root);
    
    addChildContainersOf(ctree, cont, root);
    
    return ctree;
  }

  /**
   * @requires <tt>n</tt> is the corresponding node in <tt>ctree</tt> that contains <tt>cont</tt>
   * @modifies ctree
   * @effects 
   *  if there are child containers of <tt>cont</tt>
   *    create a {@link Node} for each of them and add to <tt>ctree</tt> as a child of <tt>n</tt>
   * @version 5.2
   */
  private static void addChildContainersOf(final Tree<LabelledContInfo> ctree,
      final JDataContainer cont, final Node<LabelledContInfo> root) {

    Iterator<JDataContainer> childIt = cont.getChildContainerIterator();

    if (childIt != null) {
      while (childIt.hasNext()) {
        JDataContainer childCont = childIt.next();
        Node<LabelledContInfo> cn = new Node(new LabelledContInfo(childCont.getLabel(), childCont));
        ctree.addNode(cn, root);
        
        // recursive for cn
        addChildContainersOf(ctree, childCont, cn);
      }
    }
  }

  /**
   * @effects 
   *  if exists {@link Component} in <tt>cont</tt> rendering the domain field named <tt>attribName</tt>
   *    return it
   *  else
   *    return null
   *  
   * @version 5.2
   */
  public static Component getDataFieldGUIComponent(final JDataContainer cont,
      final DAttr attrib) {
    return cont.getComponent(attrib);
  }

  /**
   * @effects 
   *  if exists a level-1 {@link JDataContainer} of <tt>rootContainer</tt> that contains <tt>cont</tt> 
   *  as a descendant 
   *    return it
   *  else
   *    return null
   *    
   *  <p>Level-1 containers are children of <tt>rootContainer</tt>
   *  
   * @version 5.2 
   */
  public static JDataContainer getLevel1AncestorContainer(
      JDataContainer rootContainer, JDataContainer cont) {
    // loop upward from cont (does not need to use rootContainer)
    JDataContainer parent = cont.getParentContainer();

    if (parent == null || parent == rootContainer) return null;
    
    JDataContainer firstCont = null;
    do {
      if (parent.getParentContainer() == rootContainer) {
        firstCont = parent;
      } else { 
        parent = parent.getParentContainer();
      }
    } while (firstCont == null && parent != null);
    
    return firstCont;
  }

  /**
   * @effects 
   *  if exists <tt>cont</tt> is a proper descendant of <tt>rootContainer</tt>
   *    return the path from <tt>cont</tt> to <tt>rootContainer</tt>
   *  else
   *    return null
   *  
   * @version 5.2 
   */
  public static Collection<JDataContainer> getProperContainerPath(
      JDataContainer rootContainer, JDataContainer cont) {
    // loop upward from cont (does not need to use rootContainer)
    JDataContainer parent = cont.getParentContainer();

    if (parent == null || parent == rootContainer) return null;
    
    Collection<JDataContainer> path = new ArrayList<>();
    
    do {
      path.add(parent);
      parent = parent.getParentContainer();
    } while (parent != null);
    
    return !path.isEmpty() ? path : null;
  }
  
  /**
   * @effects 
   *  if <tt>cont</tt> is a proper descendant of <tt>rootContainer</tt>
   *    return the path from <tt>cont</tt> to <tt>rootContainer</tt>
   *  else
   *    return null
   *  
   * @version 5.2 
   */
  public static Collection<JDataContainer> getProperContainerPath(
      JDataContainer rootContainer, LabelledContInfo contInfo) {
    if (rootContainer == null || contInfo == null)
      return null;
    
    JDataContainer cont = contInfo.getSecond();
    
    // loop upward from cont
    JDataContainer parent = cont.getParentContainer();

    if (parent == null) return null;

    Collection<JDataContainer> path = new ArrayList<>();

    path.add(cont);
    do {
      path.add(parent);
      parent = parent.getParentContainer();
    } while (parent != null);
    
    return !path.isEmpty() ? path : null;
  }
}
