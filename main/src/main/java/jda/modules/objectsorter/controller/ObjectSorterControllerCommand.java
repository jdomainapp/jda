package jda.modules.objectsorter.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.objectsorter.model.DomainConstraintType;
import jda.modules.objectsorter.model.ObjectSorter;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.command.ControllerCommand;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.view.View;
import jda.mosa.view.assets.JDataContainer;
import jda.util.ObjectComparator;
import jda.util.ObjectComparator.SortBy;

public class ObjectSorterControllerCommand extends ControllerCommand {

  private boolean firstTime;
  private JDataContainer activeDataContainer;
  private DAttr selectedAttribute; 
  private ObjectComparator comparator;

  public ObjectSorterControllerCommand(ControllerBasic controller) {
    super(controller);
    firstTime = true;
  }

  @Override
  public void preRun() throws ApplicationRuntimeException {
    final ControllerBasic ctl = getController();

    // get the active data container 
    JDataContainer newContainer = ctl.getMainController().getActiveDataContainer();
    
    if (newContainer == null) { 
      throw new NotPossibleException(NotPossibleException.Code.NO_ACTIVE_DATA_CONTAINER);
    }
    
    JDataContainer myContainer = ctl.getRootDataController().getDataContainer(); 
        
    // use the current active container if the new one is NOT the same as the root panel 
    // (could be the same if the user clicked Update) 
    if (newContainer != myContainer) {
      if (newContainer != activeDataContainer) {
        // make sure that newContainer supports sorting
        if (!isSortable(newContainer))
          throw new NotPossibleException(NotPossibleException.Code.DATA_CONTAINER_NOT_SORTABLE,
              new Object[] {newContainer});
        
        activeDataContainer = newContainer;
      }
      
      // get the selected domain attribute (even when newContainer = activeDataContainer)
      selectedAttribute = View.getSelectedDomainAttribute(activeDataContainer);
    }
  }
  
  /**
   * @effects 
   *  if <tt>dcont</tt> is configured to support sorting
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  private boolean isSortable(JDataContainer dcont) {
    //TODO: add check conditions for sortable here!
    return true;
  }

  @Override
  public void createObjectActively() throws ApplicationRuntimeException  {
    final ControllerBasic ctl = getController();
    final DataController<ObjectSorter> dctl = ctl.getRootDataController();
    //final Class domainCls = ctl.getDomainClass();
    
    ObjectSorter sorter; 

    // get the active module 
    if (activeDataContainer == null) {
      ctl.displayErrorFromCode(MessageCode.ERROR_NO_ACTIVE_DATA_CONTAINER, dctl);
    } else {
      DataController activeDCtl = activeDataContainer.getController();
      Class activeCls = activeDCtl.getDomainClass();
      
      // get the domain attributes of its domain class
      Collection<DomainConstraintType> dcTs = getViewableDomainAttributes(activeDCtl);
      
      DomainConstraintType selectedAttributeType = null;
      SortBy sortBy = null;
      
      // if there is a pre-configured sorting setting then use it
      ObjectComparator comparator = activeDCtl.getObjectSortingConfig();
      if (selectedAttribute == null) {
        // no selected attribute specified
        if (comparator != null) {
          selectedAttribute = comparator.getSortAttrib();
          sortBy = comparator.getSortBy();
        } else {
          sortBy = SortBy.ASC;  // default sorting
        }
      } else {
        // use currently selected attribute and default sort-by (if non specified)
        if (comparator != null && selectedAttribute == comparator.getSortAttrib())
          sortBy = comparator.getSortBy();
        else
          sortBy = SortBy.ASC;  // default
      }
        
      if (selectedAttribute != null)
        selectedAttributeType = getSelectedDomainAttribute(selectedAttribute, dcTs);
      
      // create a new ObjectSorter (once) for use
      try {
        sorter = dctl.createObject(new Object[] { sortBy, dcTs, selectedAttributeType, activeDCtl });
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
            new Object[] {sortBy, activeCls.getSimpleName(), activeDCtl});
      }
    }      
  }

  @Override
  public void refreshOnShown() throws ApplicationRuntimeException {
    
    final ControllerBasic ctl = getController();
    final DataController<ObjectSorter> dctl = ctl.getRootDataController();
    
    ObjectSorter sorter = dctl.getCurrentObject();

    if (firstTime) {
      // first run
      firstTime = false;
      
      // refresh data bindings needed because bindings have not been populated with objects the first time
      // the GUI is displayed
      dctl.refreshTargetDataBindings();
      
      // update and show GUI
      updateAndShowGUI();
    } else {
      // subsequent run
      if (activeDataContainer == null) {
        // no active module
        throw new NotPossibleException(NotPossibleException.Code.NO_ACTIVE_DATA_CONTAINER);
      }
      
      // get the active module
      DataController activeDCtl = activeDataContainer.getController();
      
      // if there is a pre-configured sorting setting then use it
      ObjectComparator comparator = activeDCtl.getObjectSortingConfig();
      
      DomainConstraintType selectedAttributeType = null;

      if (activeDCtl == sorter.getTargetModule()) {
        // same module as previous run, 
        
        // if selected attribute has been changed -> update sorter
        if (selectedAttribute != sorter.getSelectedAttribDc()) {
          selectedAttributeType = getSelectedDomainAttribute(selectedAttribute, sorter.getAttributes());
          sorter.setSelectedAttrib(selectedAttributeType);
          
          // use the pre-configured sort-by setting for this attribute (if any)
          if (comparator != null && selectedAttribute == comparator.getSortAttrib())
            sorter.setSortBy(comparator.getSortBy());
          // else: use the current sort-by setting
        }
        
        // redisplay the sorter settings
        updateAndShowGUI();
      } else {
        // a different module is selected
        // update the sorter settings based on the active module

        // get the domain attributes of its domain class
        Collection<DomainConstraintType> dcTs = getViewableDomainAttributes(activeDCtl);
        
        SortBy sortBy = null;
        
        // if there is a pre-configured sorting setting then use it
        if (selectedAttribute == null) {
          // no selected attribute specified
          if (comparator != null) {
            selectedAttribute = comparator.getSortAttrib();
            sortBy = comparator.getSortBy();
          } else {
            sortBy = SortBy.ASC;  // default sorting
          }
        } else {
          // use currently selected attribute and default sort-by (if non specified)
          if (comparator != null && selectedAttribute == comparator.getSortAttrib())
            sortBy = comparator.getSortBy();
          else
            sortBy = SortBy.ASC;  // default
        }

        if (selectedAttribute != null)
          selectedAttributeType = getSelectedDomainAttribute(selectedAttribute, dcTs);
        
        sorter.setSortBy(sortBy);

        sorter.setAttributes(dcTs);

        sorter.setSelectedAttrib(selectedAttributeType);

        sorter.setTargetModule(activeDCtl);

        // refresh all data bindings of the bounded data fields of the data
        // container of dctl (this include selectedAttrib)
        dctl.refreshTargetDataBindings();

        updateAndShowGUI();
      }
    }
    
    // if there is a selected attribute the execute do task
    if (sorter.getSelectedAttrib() != null) {
      doTask();
    }
  }

  @Override
  public void doTask() throws ApplicationRuntimeException {
    final ControllerBasic ctl = getController();
    final DataController<ObjectSorter> dctl = ctl.getRootDataController();
    
    ObjectSorter sorter = dctl.getCurrentObject();

    //perform sorting from the setting in the sorter
    
    DataController activeDCtl = sorter.getTargetModule();
    
    DAttr selectedAttribute = sorter.getSelectedAttribDc();
    SortBy sortBy = sorter.getSortBy();
    
    DSMBasic dsm = ctl.getDomainSchema();
    
    if (comparator == null) {
      comparator = new ObjectComparator(dsm, selectedAttribute, sortBy);
    } else {
      comparator.setSortAttrib(selectedAttribute);
      comparator.setSortBy(sortBy);
    }

    activeDCtl.sort(comparator);
  }

  /**
   * @effects 
   *  if a domain attribute wrapper of the domain attribute is contained in <tt>attribWrappers</tt>
   *    return it 
   *  else
   *    return <tt>null</tt>   
   */
  private DomainConstraintType getSelectedDomainAttribute(
      DAttr attrib, Collection<DomainConstraintType> attribWrappers) {
    for (DomainConstraintType dct : attribWrappers) {
      if (dct.getDc() == attrib) {
        return dct;
      }
    }
    
    return null;
  }

  private Collection<DomainConstraintType> getViewableDomainAttributes(
      DataController activeDCtl) {
    // get the domain attributes of its domain class
    
    boolean printable=false;
    Map<DAttr,String> dcs = View.getViewableDomainAttributesWithLabel(
        activeDCtl.getDataContainer(), printable);
    
    // wrap the domain attributes using DomainConstraintType
    Collection<DomainConstraintType> dcTs = new ArrayList();
    DomainConstraintType dct;
    DAttr dc; String label;
    for (Entry<DAttr,String> entry : dcs.entrySet()) {
      dc = entry.getKey();
      label = entry.getValue();
      dct = new DomainConstraintType(dc, label);
      dcTs.add(dct);
    }
    
    return dcTs;
  }

}
