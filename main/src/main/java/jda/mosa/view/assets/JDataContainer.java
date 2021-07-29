package jda.mosa.view.assets;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.view.assets.datafields.JDataField;
import jda.util.properties.PropertySet;


public interface JDataContainer<C> {    
  
  /**
   * @effects 
   *  organise the components of this a pre-defined layout    
   */
  public void createLayout() throws NotPossibleException;
  
  /**
   * @effects 
   *  if tf = true
   *    compact this by hiding all the collection-type domain attributes
   *  else 
   *    reverse the above 
   */
  public void compact(boolean tf);
  
  /**
   * @effects 
   *  return the JComponent that is used for displaying this container on a GUI.
   */
  public JComponent getGUIComponent(); 
  
  /**
   * @effects 
   *  perform pre-run configuration on this and, if recursive=true, then also on 
   *  all the sub-containers of this (if any).
   *   
   *  <p>Throws NotPossibleException if fails
   */
  public void preRunConfigure(boolean recursive) throws NotPossibleException;
  
  public boolean hasFocus();
  
  public void setHasFocus(boolean hasFocus);
  
  public Region getContainerConfig();
  
  public PropertySet getContainerPrintConfig();
  
  public void setContainerPrintConfig(PropertySet printCfg);

  public ControllerBasic.DataController getController();  
  
  public String getName();

  public JDataContainer getParentContainer();
  
  /**
   * @effects 
   *  if there are child <tt>JDataContainer</tt> of this 
   *    return an Iterator of these
   *  else
   *    return null
   */
  public Iterator<JDataContainer> getChildContainerIterator();

  /**
   * Use this method to look up the view configuration region of a given display
   * component in this panel.
   * 
   * @effects return the <code>Region</code> of the component
   *          <code>comp</code> in <code>this</code>, or null if not found
   */
  public Region getComponentConfig(JComponent comp);

  /**
   * @effects returns all the <code>JComponent</code>s of this that were created 
   *  for the domain attributes <tt>attributes</tt>; or return return <b>all</b>
   *  components if <tt>attributes = null</tt>
   */
  public Component[] getComponents(Collection attributes);

  /**
   * @effects 
   *  if exists <tt>JDataField</tt> component of this that was created 
   *  for the domain attribute <tt>attrib</tt>
   *    return it
   *  else
   *    return null; 
   */
  public JComponent getComponent(DAttr attrib);

  /**
   * @effects 
   *  if this contains a component whose domain constraint's name equals
   *  <tt>attributeName</tt>
   *    return true
   *  else
   *    return false 
   */
  public boolean containsComponentForAttribute(String attributeName);
  
  /**
   * @effects 
   *  return a <tt>Collection</tt> of the domain attributes associated to the data fields that 
   *  are contained in this and, if <tt>printable=true</tt>, that are used for printing 
   */
  public Collection<DAttr> getDomainAttributes(boolean printable);

  /**
   * @effects 
   *  if a data component of <tt>this</tt> that renders a domain attribute currently has a focus
   *    return the domain attribute 
   *  else
   *    return <tt>null</tt> 
   * @version 3.0
   */
  public DAttr getSelectedDomainAttribute();

  /**
   * @effects 
   *  if this is suitable for searching
   *    return true
   *  else
   *    return false
   */
  public boolean isSearchEnabled();

  /**
   * This method is used to override the editable status of the data fields of this 
   * container. For example, it is used to make it possible for users of 
   * a read-only gui to use it to enter search query terms. 
   * 
   * @effects <pre>
   *  for each data field df of this 
   *    if df is not a derived field AND df is not editable
   *      make editable
   *    else 
   *      do nothing </pre>
   * @version 2.7.2: add check for derived field
   */
  public void forceEditable();
  
  /**
   * @effects 
   *  if this container is a sub-container of another container (i.e. <code>{@link #getParentContainer()} != null</code> then 
   *  sets the domain attribute of the domain class that this container is representing, 
   *  which links the data of this container to the parent.
   *   
   *  <p>E.g.: If this container is a panel or table that presents data for the Enrolment class and 
   *  the parent is the Student panel then the link attribute of this container is <code>Enrolment:student</code>
   */
  public void setLinkAttribute(DAttr linkAttrib);
  
  /**
   * @effects 
   *  if this container is a sub-container of another container (i.e. <code>{@link #getParentContainer()} != null</code> then 
   *  returns the domain attribute of the domain class that this container is representing, 
   *  which links the data of this container to the parent.
   *   
   *  <p>E.g.: If this container is a panel or table that presents data for the Enrolment class and 
   *  the parent is the Student panel then the link attribute of this container is <code>Enrolment:student</code>
   */
  public DAttr getLinkAttribute();
  
  //public void setParentContainer(JDataContainer parent);
  
//  public void onOpen();
//  public void onCreateObject(Object obj);
//  public void onNewObject();  
//  public void onDeleteObject(Object o);
//  public void onNext();  
//  public void onPrevious();  
  
  /**
   * @effects obtain the values of <b>non-auto</b> data fields of this container 
   *  and return as a <tt>LinkedHashMap<DomainConstraint,Object></tt> containing binary tuples 
   *  <tt><dc,val></tt> where 
   *  <tt>val</tt> is the value of a data field and  
   *  <tt>dc</tt> is the domain constraint of the associated domain attribute;
   *  
   *  <p>Throws ConstraintViolationException if erroneous data values were specified
   *   
   */
  // v2.6: change return type
  // public Object[] getUserSpecifiedState() throws ConstraintViolationException;
  public LinkedHashMap<DAttr,Object> getUserSpecifiedState() throws ConstraintViolationException;
  
  /**
   * @effects obtain the content of the data fields of the <tt>mutable</tt> attributes of this container 
   *  and return as an <tt>Object[]</tt> array. 
   *  
   *  Throws <code>ConstraintViolationException</code> if some data field value
   *          is not valid (w.r.t to its domain constraint).
   */
  //public Object[] getMutableState() throws ConstraintViolationException;
  public LinkedHashMap<DAttr,Object> getMutableState() throws ConstraintViolationException;
  
  public Object[] getSearchState();
  
//  /**
//   * @effects 
//   *  return a <tt>List</tt> of the data objects currently being displayed by this
//   *  or <tt>null</tt> if no objects are being displayed
//   */
//  public List getDataModel(); 
  
  /**
   * @effects
   *  return the <tt>JLabel</tt> object that was set to be the label for 
   *  component <tt>comp</tt> in this
   */
  public JLabel getLabelFor(JComponent comp);
  
  /**
   * @effects 
   *  if this is a top-level controller
   *    return the title of the creator GUI
   *  else
   *    return the label associated to this container in the user GUI 
   */
  public String getLabel();

  /**
   * @effects 
   *  if current object is specified
   *    update the data component of this that is bound to the domain attribute <tt>attribName</tt>
   *    to display the most up-to-date value of the attribute of current object
   *  else
   *    do nothing
   *  
   * @version 3.2
   */
  public void updateDataComponent(String attribName);

  /**
   * This method is used to update the data container when the current object has been updated 
   * in the system (e.g. after user has made changes to its state and clicked 'update'). 
   * 
   * @effects 
   *  display the state of the object <tt>o</tt> on the data fields of this container  
   */
  public void update(Object o);
  
  /**
   * This method is used to update the display values of the data fields managed by this container, 
   * but without relying on the current domain object. Such an update typically includes updating 
   * the values of the derived fields.
   * 
   * @effects 
   *  update the display values of the data fields managed by this container
   */
  public void updateGUI();
  
  /**
   * @effects 
   *  <b>clear</b> all the data fields of this container.
   *  
   *  <p>For text fields, their contents are set to <tt>null</tt>.
   */
  public void clear();

  /**
   * @effects 
   *  <b>reset</b> all the data fields of this container, ready to get user input
   *  
   *  <p>For text fields, their contents are set to empty. For combo data fields, 
   *  their selected items are reset to those which were used as the default when
   *  the fields were created 
   */
  public void reset();

  /**
   * This method differs from {@link #refreshTargetDataBindings()} in the followings: 
   * (1) it only considers data fields under certain configuration options 
   * (2) it takes into account all kinds of bounded data, not just those linked to the bounded data fields 
   * 
   * @effects 
   *  refresh the domain data associated to the data components of this (e.g. a data source
   *  that is bounded to a data field) to obtain the most up-to-date data
   * @version 3.1
   */
  public void refreshLinkedData();
  
  /**
   * @effects <pre>
   *  if exists bounded bounded data fields of this data container
   *    clear their bindings with their associated data sources
   *    reestablish the bindings (effectively reloading the objects)
   *  else
   *    do nothing </pre>
   * @version 3.0
   */
  public void refreshTargetDataBindings();
  
  /**
   * This differs from {@link #refreshTargetDataBindings()} in that it operates ONLY on the  
   * data field of a given attribute, as opposed to all the bounded data fields of the data container
   * 
   * @effects <pre>
   *  if exists bounded bounded data field of <tt>attrib</tt> in {@link #dataContainer}
   *    clear its binding with the associated data source
   *    reestablish the binding (effectively reloading the objects)
   *  else
   *    do nothing </pre>
   * @version 3.1
   */
  public void refreshTargetDataBindingOfAttribute(DAttr attrib);
  
  /**
   * @effects 
   *  if this is not contained in a JScrollPane object
   *    apply visibile to this
   *  else
   *    apply visible to the JScrollPane object 
   */
  public void setVisible(boolean visible);
  
  public boolean isVisible();

  /**
   * @requires <tt>vals != null /\ </tt> elements of <tt>vals</tt> are in the same order as the mutable 
   *  attributes of the domain class.
   *   
   * @effects cause the components of the mutable attributes of this container 
   *  to display <tt>vals</tt> and others to reset
   */
  public void setMutableState(Object[] vals);

  /**
   * This method is more fine-grained than {@link #setMutableState(Object[])} in that it allows
   * application to change the value of the data field of this that corresponds to 
   * a <b>a specific</b> attribute
   *   
   * @effects
   *  set the value of the data field of this that corresponds to <tt>attrib</tt>
   *  to <tt>val</tt> 
   */
  public void setMutableState(DAttr attrib, Object val);

  /**
   * @requires <tt>vals != null /\ </tt> elements of <tt>vals</tt> are in the same order as the 
   * <b>non-auto</b> attributes of the domain class.
   *   
   * @effects cause the components of the non-auto attributes of this container 
   *  to display <tt>vals</tt> and others to reset
   */
  public void setUserSpecifiedState(Object[] vals);
  
  /**
   * This differs from {@link #setMutableState(DAttr, Object)} in that it supports both editable and 
   * non-editable domain attributes. 
   * 
   * @modifies data field of the domain attribute whose name is <tt>attribName</tt>
   * @effects 
   *  change value of data field of the domain attribute whose name is <tt>attribName</tt> to <tt>value</tt>, 
   *  return the former value of the data field
   * @version 4.0 
   */
  public Object setDataFieldValue(DAttr attrib, Object value);
  
  /**
   * @effects
   *  update the editable state of all the data fields of this container
   *  based on the current user permissions.
   */
  public void updateDataPermissions();
  
  /**
   * This method is used to determine the editability of a data container based on where it is configured within an object form. 
   * 
   * @effects <pre>
   *  if this is a top-level container 
   *    return the editability of its user GUI
   *  else
   *    if the component config of this container within the parent specifies editable=true
   *      return true
   *    else
   *      return false</pre>  
   */
  public boolean isEditable();
  
  /**
   * Unlike {@link #updateDataPermissions()} which updates editability based on permissions, 
   * this method updates the editability based on the view configuration setting. This is applied
   * , in particular, to report-type GUIs. 
   * 
   * @effects 
   *  update the editable state of the data components of this container 
   *  and, if <tt>recursive=true</tt>, then those of the sub-containers (and so on),
   *  to <tt>tf</tt>. 
   *  
   *  <p>If a sub-container has a scope definition specified in the containment tree (of the root container) 
   *  that has a different editability value then skip that sub-container and process on the 
   *  descendants (if any).
   * @version 
   * - 3.2: added parameter <tt>sourceContainer</tt> (the container on which this operation was first invoked) 
   *        and support for scope definition   
   */
  public void setEditable(final JDataContainer sourceContainer, final boolean tf, final boolean recursive);

  /**
   * This method is invoked when a data field of this container is being edited by the user (e.g. 
   * following a key-pressed event on the field).
   * 
   * @effects 
   *  handle the editing event on the data field <tt>df</tt> of this container, 
   *  possibly caused by the keyboard event <tt>e</tt>    
   */
  public void handleDataFieldEditing(KeyEvent e, JDataField df);


  /**
   * This method differs from {@link #handleDataFieldEditing(KeyEvent, JDataField)} in that that other method
   * deals with the intermediate value editing events (e.g. key press), while this method deals with the event 
   * after the value has been changed.
   * 
   * @effects 
   *  handle the event that is raised after <tt>df</tt>'s value has been changed by the user
   *  
   * @version 3.1
   */
  public void handleDataFieldValueChanged(JDataField df);
  
  /**
   * @effects 
   *  registers <tt>dctl</tt> as the state listener of all the data fields of this, 
   *  and those of the nested containers (if <tt>recursive = true</tt>), whose 
   *  are configured as the state event source.
   */
  public void addStateListener(DataController dctl, boolean recursive);

  /**
   * @effects 
   *  return all the {@link Component}s of this whose corresponding {@link DAttr}s match those 
   *  specified in <tt>dattrs</tt> (if any).
   *  <p>If <tt>dattrs = null</tt> then return all {@link Component}s.
   *  
   *  <p>The {@link Component}s are returned in the same order as they are stored in this and 
   *  in the case that <tt>dattrs = null</tt> in the same {@link Map} that records
   *  the components of this.
   *    
   * @version 5.1c
   */
  public Map<DAttr, Component> getComps(Collection<DAttr> dattrs);

  /**
   * @effects 
   *  prepare this such that it is ready to receive user input for a new object.
   *  This new object is being created at the given <tt>index</tt>.
   *  
   *  This <tt>index</tt> is set as the newly-added row's index for table-typed data container and is <tt>null</tt>
   *  for other types. 
   *  
   *   <p>In particular, this involves making all the <b>immutable, non-auto</b> data fields
   *   to editable, so that they can receive input from the user for the new object.  
   *   
   * @version 5.1c
   */
  public void onNewObject(Object index);
  
  /**
   * @effects 
   *   prepare this for post-object-creation task. 
   *   
   *   <p>In particular, it reverses the editability of the <b>immutable, non-auto</b> data fields (which were changed by {@link #onNewObject()}) 
   *   back to the original setting.
   *   
   * @version 5.1c
   */
  public void onCreateObject(C obj);

  /**
   * @effects 
   *  perform part of {@link #getController()#onCancel()} that concerns this. 
   * @version 5.1c
   */
  public void onCancel();

  /**
   * @effects 
   *  strictly compares equality based on object equality
   * @version 5.2
   */
  public boolean equalsStrictly(JDataContainer other);

  /**
   * @effects 
   *  if this has children containers
   *    return the number of those containers
   *  else
   *    return 0 
   * @version 5.2
   */
  public int getChildContainerCount();
}
