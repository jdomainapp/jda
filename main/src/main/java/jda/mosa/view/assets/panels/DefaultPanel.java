package jda.mosa.view.assets.panels;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.view.View;
import jda.mosa.view.assets.DataContainerToolkit;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JSpinnerField;
import jda.mosa.view.assets.layout.LayoutBuilder;
import jda.util.properties.PropertySet;

/**
 * Represents a standard component panel in which the components are the data
 * fields representing the domain attributes and these components are organised
 * in a group layout.
 * 
 * @see GroupLayout
 * @author dmle
 * 
 */
public class DefaultPanel extends JPanel implements JDataContainer {
  
  // v2.7.2
  private Region containerCfg;
  private PropertySet printConfig;

  protected ControllerBasic.DataController controller;

  /**
   * if this is the nested panel then this is set to its parent {@see
   * JDataContainer}
   */
  protected JDataContainer parent;

  protected List<JComponent> labels;

  protected LinkedHashMap<DAttr, JComponent> comps;

  protected LinkedHashMap<JComponent,Region> cfgMap;

  /**
   * one of the components in {@link #comps.values} whose values are taken from the
   * domain objects of the {@link #parent} container (if it is specified)
   */
  private JComponent linkComponent;

  /** 
   * v2.7.2: 
   * 
   * if this is nested then this specifies the link attribute to the parent. 
   * This is related to {@link #linkComponent} as follows: 
   * if {@link #linkComponent} is specified then its domain constraint is 
   * the same as <tt>linkAttribute</tt>. 
   * 
   * Note that <tt>linkAttribute</tt> may be specified even if {@link #linkComponent} is not.
   */
  private DAttr linkAttribute;
  
  /**
   * whether or not the current panel is active.
   * <p>
   * A panel is active if one of its child components is active, i.e. it is
   * either an active sub-panel or a component that currently has the mouse
   * focus
   */
  private boolean hasFocus;

  /***
   * A custom {@link FocusTraversalPolicy} object that traverses only 
   * the user-editable data fields of this.  
   */
  private CustomFocusTraversalPolicy focusTravelPolicy;
  
  /**v3.1: true if this has a complex layout */
  private boolean hasComplexLayout;
  
  /**v3.1: the container components used by this.layout to organise the components 
   * (only used if this {@link #hasComplexLayout} = true)*/
  private Collection<JComponent> compContainers;
  
  /**
   * (for performance) caches child data containers of this (if any)
   * 
   * @version 5.2
   */
  private ArrayList<JDataContainer> childDataContainers;
  
  /**
   * @version 5.1
   * used internally by operations that need to process the data container hierarchy
   */
  private static Stack<JDataContainer> processedContainerBuffer;

  /**
   * @effects
   *  create and return a <tt>DefaultPanel</tt> or a sub-type of it specified by <tt>displayClass</tt>
   *  
   *  Throws NotPossibleException if failed to do so
   */
  public static DefaultPanel createInstance(Class<? extends DefaultPanel> displayClass,
      Region containerCfg, 
      ControllerBasic.DataController dctl, 
      String name, 
      JDataContainer parentContainer) throws NotPossibleException {
    DefaultPanel panel;
    try {
      panel = displayClass.getConstructor(
          Region.class, 
          DataController.class, String.class, JDataContainer.class).
          newInstance(containerCfg, dctl,name,parentContainer);
      
      return panel;
    } catch (Exception e) {
      // could not create 
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT,
          e, "Không thể tạo đối tượng lớp: {0}({1})", displayClass, dctl, name, parentContainer);
    }
  }
  
  // constructor methods
  
  /**
   * @effects 
   *  initialise this as top-level panel
   */
  public DefaultPanel(ControllerBasic.DataController controller, String name) {
    this(null, controller, name, null);
  }
  
//  /**
//   * @effects 
//   *  if parent != null
//   *    initialise this as a sub-panel of parent
//   *  else
//   *    initialise this as top-level panel
//   */
//  public DefaultPanel(Controller.DataController controller, String name, 
//      JDataContainer parent) {
//    this(null, controller, name, parent);
//  }
  
  public DefaultPanel(Region containerCfg, ControllerBasic.DataController controller, String name, 
      JDataContainer parent) {
    super();
    
    this.containerCfg = containerCfg;
    
    this.controller = controller;
    
    // sets the panel into the controller
    this.controller.setDataContainer(this);
    
    this.parent = parent;
    
    labels = new ArrayList<>();
    comps = new LinkedHashMap<>();
    cfgMap = new LinkedHashMap<>();
    
    this.hasFocus = false;

    super.setName(name);

    GUIToolkit.highlightContainerInit(getGUIComponent());
    
    // custom traversal policy for the root panel 
    if (parent == null) {
      focusTravelPolicy = new CustomFocusTraversalPolicy();
      setFocusTraversalPolicyProvider(true);
      setFocusTraversalPolicy(focusTravelPolicy);
    }
  }

  /**
   * Create the components and add them to this panel using a pre-defined
   * layout. Returns <code>this</code>.
   * 
   * <p>
   * Layout: {@see GroupLayout}
   */
  @Override
  public void createLayout() throws NotPossibleException {
    if (comps.isEmpty()) // no components
      throw new InternalError(this+": no components");

    Class lmc = containerCfg.getLayoutBuilderType();
    
    if (lmc == null)
      throw new NotPossibleException(NotPossibleException.Code.NO_LAYOUT_BUILDER, 
          new Object[] {this});
    
    //System.out.println(this+".createLayout(): layout builder: " + lmc.getSimpleName());

    LayoutBuilder lm = LayoutBuilder.getInstance(lmc);
    
    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    
    // v3.1: support containers returned as result
    Collection<JComponent> containers = lm.createLayout(layout, labels, comps.values(), cfgMap.values());
    
    if (containers != null) {
      hasComplexLayout = true;
      compContainers = containers;
    }
    
    // v5.2: for performance reason, init internal data structures needed to perform other operations
    initChildDataContainers();
  }

  /**
   * @modifies this
   * @effects 
   *  if this is a parent 
   *    init {@link #childDataContainers} to contain all child containers 
   *  else
   *    do nothing
   * @version 5.2
   */
  private Collection<JDataContainer> initChildDataContainers() {
    Component comp;
    Component[] comps = getComponents(null);
    childDataContainers = new ArrayList<JDataContainer>();
    
    for (int i = 0; i < comps.length; i++) {
      comp = comps[i];
      if (comp instanceof JScrollPane) {
        comp = ((JScrollPane) comp).getViewport().getComponent(0);
      }

      if (comp instanceof JDataContainer) {
        childDataContainers.add((JDataContainer)comp);
      }
    }

    return (!childDataContainers.isEmpty() ? childDataContainers : null);
  }
  
//  private Alignment toGroupAlignment(AlignmentX alignX) {
//    if (alignX == AlignmentX.Right) {
//      return Alignment.TRAILING; 
//    } else if (alignX == AlignmentX.Center) {
//      return Alignment.CENTER;
//    } else {
//      // rest
//      return Alignment.LEADING;
//    }
//  }
  
  @Override
  public Region getContainerConfig() {
    return containerCfg;
  }

  @Override
  public PropertySet getContainerPrintConfig() {
    return printConfig;
  }

  @Override
  public void setContainerPrintConfig(PropertySet printCfg) {
    this.printConfig = printCfg;
  }

  @Override
  public JComponent getComponent(DAttr attrib) {
    DAttr dc;
    for (Entry<DAttr,JComponent> e : comps.entrySet()) {
      dc = e.getKey();
      if (dc.equals(attrib)) {
        return e.getValue();
      }
    }
    
    return null;
  }
  
  @Override
  public boolean containsComponentForAttribute(String attributeName) {
    for (DAttr dc : comps.keySet()) {
      if (dc.name().equals(attributeName)) {
        return true;
      }
    }
    
    return false;
  }
  
  @Override
  public Collection<DAttr> getDomainAttributes(boolean printable) {
    if (!printable) {
      return comps.keySet();
    } else { 
      // printable
      Collection<DAttr> attribs = new ArrayList<>();
      
      DAttr attrib;
      JComponent c;
      PropertySet printfCfg;
      for (Entry<DAttr,JComponent> e : comps.entrySet()) {
        attrib = e.getKey();
        c = e.getValue();
        printfCfg = null;
        
        // only consider attrib if its associate component is visible for printing
        if (c.isVisible()) { // because of printable=true (above)
          if (printConfig != null) {
            printfCfg = printConfig.getExtension(attrib.name());
          }
          if (printfCfg == null || 
              printfCfg.getPropertyValue("isVisible", Boolean.class, true) == true) {
            attribs.add(e.getKey());
          }
        }
      }
      
      return (attribs.isEmpty()) ? null : attribs;
    }
  }
  
  
  @Override
  public DAttr getSelectedDomainAttribute() {
    boolean printable=false;
    Collection<DAttr> attribs = getDomainAttributes(printable);
    DAttr selectedAttrib = null;
    for (DAttr attrib : attribs) {
      if (getComponent(attrib).isFocusOwner()) {
        selectedAttrib = attrib;
        break;
      }
    }
    
    return selectedAttrib;
  }

  /**
   * @effects 
   *  return the component at the specified index or null if no such component exists
   */
  protected JComponent getComponentAt(int index) {
    int i = 0;
    for (DAttr key : comps.keySet()) {
      if (i == index) {
        return comps.get(key);
      }
      i++;
    }
    
    return null;
  }
  
  /**
   * @effects returns the <code>DataController</code> object of
   *          <code>this</code>
   */
  @Override  
  public ControllerBasic.DataController getController() {
    return controller;
  }

  /**
   * @modifies this
   * 
   * @effects 
   *  add a data field component with its label to this
   *  if the component is editable
   *    add it to the traversal policy object
   */
  public <T extends JComponent> void addLabelledComponent(DAttr co, 
      Region cfg,
      T label,
      JComponent comp) {
    labels.add(label);
    comps.put(co, comp);
    cfgMap.put(comp,cfg);
    
    updateFocusTraversalPolicy(co, comp);
  }

  /**
   * Add component comp to the traversal policy of the root panel of this 
   * containment hierarchy.
   * 
   * @requires
   *  parent has been initialised
   *  
   * @effects 
   *  if this is a sub-panel then 
   *    update the parent's panel's policy with component comp
   *  else
   *    add comp to this panel's policy if it is mutable according to 
   *    the domain constraint co
   *  
   * @version 
   * - 5.1: updated to overcome cycles in the parent hierarchy.
   * 
   */
  protected void updateFocusTraversalPolicy(DAttr co, JComponent comp) {
    /** v5.1: improved
    if (parent != null) {
      //TODO: assume parent panel is a DefaultPanel
      ((DefaultPanel)parent).updateFocusTraversalPolicy(co, comp);
    } else {
      if (focusTravelPolicy != null) {
        //TODO: improve traversal policy to support data field permissions at run-time
        // must add the display component of this field
        Component display = null;

        if (comp instanceof JDataField && co.mutable()) {
          
           // two cases:
           // (1) display is spinner field
           //      add text field 
           //  (2) non-spinner field 
           //    add display
           //
          if (comp instanceof JSpinnerField) {
            display = ((JSpinner.DefaultEditor)((JSpinner) ((JDataField)comp).getGUIComponent()).
                getEditor()).getTextField();
          } else {
            // editable data field
            display = ((JDataField)comp).getGUIComponent();            
          }
        } else if (comp instanceof JDataContainer) {
          display = ((JDataContainer) comp).getGUIComponent();
        }
        
        if (display != null) {
          focusTravelPolicy.addComponent(display);
        }
      }
    }
    */
    
    // TODO ? put processedBuffer on the stack of this method if concurrent invocation is used
    if (processedContainerBuffer != null) {
      processedContainerBuffer.clear();
    } else {
      processedContainerBuffer = new Stack<>();
    }
    
    updateFocusTraversalPolicy(co, comp, processedContainerBuffer);
  }
  
  /**
   * Add component comp to the traversal policy of the root panel of this 
   * containment hierarchy.
   * 
   * @requires
   *  parent has been initialised
   *  
   * @modifies {@link #processedContainerBuffer}
   * 
   * @effects 
   *  if this is a sub-panel then 
   *    update the parent's panel's policy with component comp
   *  else
   *    add comp to this panel's policy if it is mutable according to 
   *    the domain constraint co
   *  
   * @version 
   * - 5.1: created to overcome cycles in the parent hierarchy.
   */
  private void updateFocusTraversalPolicy(DAttr co, JComponent comp, Stack<JDataContainer> processedBuffer) {
    if (parent != null) {
      //TODO: assume parent panel is a DefaultPanel
      if (!processedBuffer.contains(parent)) {
        processedBuffer.push(parent);
        
        ((DefaultPanel)parent).updateFocusTraversalPolicy(co, comp, processedBuffer);
      }
    } else {
      if (focusTravelPolicy != null) {
        //TODO: improve traversal policy to support data field permissions at run-time
        // must add the display component of this field
        Component display = null;

        if (comp instanceof JDataField && co.mutable()) {
          /**
           * two cases:
           * (1) display is spinner field
           *      add text field 
           *  (2) non-spinner field 
           *    add display
           */
          if (comp instanceof JSpinnerField) {
            display = ((JSpinner.DefaultEditor)((JSpinner) ((JDataField)comp).getGUIComponent()).
                getEditor()).getTextField();
          } else {
            // editable data field
            display = ((JDataField)comp).getGUIComponent();            
          }
        } else if (comp instanceof JDataContainer) {
          display = ((JDataContainer) comp).getGUIComponent();
        }
        
        if (display != null) {
          focusTravelPolicy.addComponent(display);
        }
      }
    }
  }
  
  /**
   * @see #linkComponent
   */
  public void setLinkComponent(JComponent comp) {
    this.linkComponent = comp;
  }

  @Override
  public void setLinkAttribute(DAttr linkAttrib) {
    linkAttribute = linkAttrib;
  }
  
  @Override
  public DAttr getLinkAttribute() {
    /*v2.7.2: use linkAttribute
    if (linkComponent != null) {
      return getComponentConstraint(linkComponent); //getComponentConstraint(linkComponent).name();
    }
    return null;
    */
    return linkAttribute;
  }
  
  /**
   * @effects returns an <code>Object</code> representing the current value of
   *          the {@link #linkComponent}
   */
  private Object getLinkValue() {
    if (linkComponent != null) { // bounded
      Object parentObject = controller.getParentObject();
      return parentObject;
    } else {
      return null;
    }
  }

  /**
   * Use this method to reverse look up the domain constraint of a given display
   * component in this panel.
   * 
   * @effects returns the <code>DomainConstraint</code> of the component
   *          <code>comp</code> in <code>this</code> or null if not found
   */
  public DAttr getComponentConstraint(JComponent comp) {
    for (Entry<DAttr, JComponent> e : comps.entrySet()) {
      if (e.getValue().equals(comp)) {
        return e.getKey();
      }
    }

    return null;
  }

  /**
   * @effects 
   *  return the name of the domain attribute whose view component in this 
   *  is <tt>comp</tt>, or null if no such component exist.
   */
  public String getAttributeName(JComponent comp) {
    DAttr dc = getComponentConstraint(comp);
    if (dc != null) {
      return dc.name();
    }
    
    return null;
  }
  
  @Override
  public Region getComponentConfig(JComponent comp) {
    return cfgMap.get(comp);
  }

  /**
   * Use this method to look up the view configuration region of a given display
   * component in this panel.
   * 
   * @effects return the <code>Region</code> of the component
   *          in <code>this</code> that was created for the attribute <tt>attrib</tt>, or null if not found
   */  
  public Region getComponentConfig(DAttr attrib) {
    JComponent comp = comps.get(attrib);
    if (comp != null) {
      return cfgMap.get(comp);
    }
    
    return null;
  }

  /**
   * @effects returns all the <code>JComponent</code>s that were created by
   *          {@link #createComponents()}.
   *          
   */
  @Override
  public Component[] getComponents(Collection cons) {
    if (cons == null) { // return all components
      return (Component[]) comps.values().toArray(new Component[comps.size()]);
    } else {
      // return only the components that match the specified constraints
      Stack<Component> c = new Stack();
      for (Entry<DAttr, JComponent> e : comps.entrySet()) {
        if (cons.contains(e.getKey())) {
          c.push(e.getValue());
        }
      }
      return c.toArray(new Component[c.size()]);
    }
  }
  
  @Override
  public Map<DAttr, JComponent> getComps(Collection dattrs) {
    if (dattrs == null) { // return all components
      return comps;
    } else {
      // return only the components that match the specified constraints
      Map<DAttr, JComponent> m = new LinkedHashMap<>();
      for (Entry<DAttr, JComponent> e : comps.entrySet()) {
        DAttr attr = e.getKey();
        if (dattrs.contains(attr)) {
          m.put(attr, e.getValue());
        }
      }
      
      if (m.isEmpty())
        return null;
      else
        return m;
    }
  }

  /**
   * @effects 
   *  look up and return a Collection of all <b>visible</tt> components of this; 
   *  return null if no such components can be found
   */
  public Collection<JComponent> getVisibleComponents() {
    Collection<JComponent> visibles = new ArrayList<>();
    for (JComponent c : comps.values()) {
      if (c.isVisible()) visibles.add(c);
    }
    
    return (visibles.isEmpty()) ? null : visibles;
  }
  
  @Override
  public void addStateListener(DataController dctl, boolean recursive) {
    Region cfg;
    JComponent comp;
    JDataContainer subCont;
    for (Entry<JComponent,Region> entry : cfgMap.entrySet()) {
      cfg = entry.getValue();
      comp = entry.getKey();
      if (cfg.getIsStateEventSource()) {
        // found an event source
        if (comp instanceof JDataField) {
          ((JDataField)comp).addChangeListener(dctl);
        } else if (recursive){
          subCont = getDataContainerFrom(comp);
          subCont.addStateListener(dctl, true);
        }
      }
    }
  }
  
  /**
   * @requires 
   *  <tt>comp</tt> is the GUI component of a <tt>JDataContainer</tt> 
   *   
   * @effects 
   *  return the <tt>JDataContainer</tt> component that is contained in <tt>comp</tt>; 
   *  or <tt>null</tt> if it is not so
   */
  protected JDataContainer getDataContainerFrom(JComponent comp) {
    if (comp instanceof JDataContainer) {
      return ((JDataContainer) comp);
    } else if (comp instanceof JScrollPane) {
      // scrollable nested container
      JDataContainer dcont = (JDataContainer) ((JScrollPane) comp).getViewport().getView();
      return dcont;
    } else {
      // not a container
      return null;
    }
  }
  
  @Override
  public void preRunConfigure(boolean recursive) throws NotPossibleException {
    // if there are any bounded field components then connect them to source

    Collection<JComponent> dataComps = comps.values();
    
    JBindableField f;
    for (JComponent comp : dataComps) {
      if (comp instanceof JBindableField) {
        f = (JBindableField) comp;
        //v2.7: if (f.isBounded())
        f.connectDataSource();
      } else if (recursive) {
        if (comp instanceof JDataContainer) {
          // nested container
          ((JDataContainer) comp).preRunConfigure(recursive);
          
        } else if (comp instanceof JScrollPane) {
          // scrollable nested container
          JDataContainer dcont = (JDataContainer) ((JScrollPane) comp).getViewport().getView();
          dcont.preRunConfigure(recursive);
        }
      }
    }
  }
  
  /**
   * @effects returns <code>this.labels</code>
   */
  public List<JComponent> getLabelComponents() {
    return labels;
  }
  
  /**
   * @effects
   *  return the <tt>JLabel</tt> object that was set to be the label for 
   *  component <tt>comp</tt> in this; or return <tt>null</tt> if no such component was found
   */
  @Override
  public JLabel getLabelFor(JComponent comp) {
    int i = 0;
    JComponent c;
    for (Entry<DAttr,JComponent> e : comps.entrySet()) {
      c = e.getValue();
      if (c == comp) {
        return (JLabel) labels.get(i);
      }
      i++;
    }

    return null;
  }

  /**
   * @effects 
   *  return the <tt>JLabel</tt> object that was set to be the label for 
   *  the data field component whose associated domain attribute is <tt>attrib</tt>; 
   *  or return <tt>null</tt> if no such component is found
   */
  public JLabel getLabelFor(DAttr attrib) {
    int i = 0;
    for (DAttr compAttrib : comps.keySet()) {
      if (compAttrib.equals(attrib)) {
        // found it
        return (JLabel) labels.get(i);
      }
      i++;
    }
    
    return null;
  }
  
  @Override
  public String getLabel() {
    if (parent == null) {
      return controller.getCreator().getGUI().getTitle();
    } else {
      DefaultPanel parentPanel = (DefaultPanel) parent;
      JLabel label = parentPanel.getLabelFor(this);
      
      String txt = controller.getLabelText(label);
      if (txt.equals("")) {
        return null;
      } else {
        return txt;
      }
    }
  }
  
  @Override
  public void setHasFocus(boolean hasFocus) {
    //debug 
    //System.out.println(this + ".setHasFocus: " + hasFocus);
    
    this.hasFocus = hasFocus;
    
    if (hasFocus) {
      // highlight the container
      GUIToolkit.highlightContainerOnFocus(getGUIComponent());
      //this.requestFocusInWindow();
    } else {
      GUIToolkit.highlightContainerInit(getGUIComponent());
    }
  }

  @Override
  public boolean hasFocus() {
    return hasFocus;
  }

//  /**
//   * @effects initialises <code>this.parent</code> to <code>parent</code>, i.e.
//   *          making <code>this</code> a panel nested inside another.
//   */
//  public void setParentContainer(JDataContainer parent) {
//    this.parent = parent;
//  }

  /**
   * @effects if <code>this.parent != null</code> (i.e. this is a panel nested
   *          inside another) then returns the {@see JDataContainer} object
   *          representing the parent container, else returns <code>null</code>.
   */
  @Override
  public JDataContainer getParentContainer() {
    return parent;
  }

  @Override
  public String toString() {
    return "DefaultPanel(" + getController().toString() + ")";
  }

  /**
   * @version 
   * - 3.1: improved to better support complex layouts (e.g. tab group): with these layouts, it is more correct 
   *    to hide/unhide the container used to organise the subcontainers (e.g. tab group)
   *    rather than the individual sub-containers themselves
   */
  @Override
  public void compact(final boolean tf) {
    /*v3.1: support complex layouts 
    Component c;
    Component comp;
    DomainConstraint dc;
    DomainConstraint.Type type;
    JLabel label;
    for (JComponent labelComp: labels) {
      label = (JLabel) labelComp;
      c = label.getLabelFor();
      
      for (Entry<DomainConstraint,JComponent> e : comps.entrySet()) {
        comp = e.getValue();
        // comp may be scrollable 
        if (c == comp || 
            (comp instanceof JScrollPane &&
             c == ((JScrollPane)comp).getViewport().getView())) {          
          dc = e.getKey();
          type =  dc.type();
          if (type.isCollection() && 
              dc.filter().clazz() != MetaConstants.NullType) { //NullType.class) {
            // collection-type domain attribute -> hide/show
            // v2.6.c: use AppGUI to update label based on comp's visibility
            //label.setVisible(!tf);
            comp.setVisible(!tf);
            controller.getCreator().getGUI().updateContainerLabelOnVisibilityUpdate(label, !tf);
          }
          break;
        }
      }
    }
    */
    if (hasComplexLayout && compContainers != null) {
      // complex layout: operate on the container component that contains the sub-containers
      for (JComponent container : compContainers) {
        container.setVisible(!tf);
        //TODO: update container label to match visibility (if this is specified)
      }
    } else {
      // simple layout: operate on the individual sub-containers
      Component c;
      Component comp;
      DAttr dc;
      DAttr.Type type;
      JLabel label;
      for (JComponent labelComp: labels) {
        label = (JLabel) labelComp;
        c = label.getLabelFor();
        
        for (Entry<DAttr,JComponent> e : comps.entrySet()) {
          comp = e.getValue();
          // comp may be scrollable 
          if (c == comp || 
              (comp instanceof JScrollPane &&
               c == ((JScrollPane)comp).getViewport().getView())) {          
            dc = e.getKey();
            type =  dc.type();
            if (type.isCollection() && 
                dc.filter().clazz() != CommonConstants.NullType) { //NullType.class) {
              // collection-type domain attribute -> hide/show
              // v2.6.c: use AppGUI to update label based on comp's visibility
              //label.setVisible(!tf);
              comp.setVisible(!tf);
              GUIToolkit.updateContainerLabelOnVisibilityUpdate(label, !tf);
            }
            break;
          }
        }
      }      
    }
  }
  
  
  @Override
  public JComponent getGUIComponent() {
    return this;
  }

  @Override
  public LinkedHashMap<DAttr,Object> getUserSpecifiedState() throws ConstraintViolationException {
    //List vals = new ArrayList();
    LinkedHashMap<DAttr,Object> vals = new LinkedHashMap<DAttr, Object>();
    
    JDataField df;
    DAttr dc;
    JComponent c;
    //for (Component c : comps.values()) {
    for (Entry<DAttr,JComponent> e : comps.entrySet()) {
      // TODO: improve this to support nested panel and table component
      dc = e.getKey();
      c = e.getValue();
      if (c instanceof JDataField) {
        df = (JDataField) c;
        if (df == null)
          continue;

        //dc = df.getDomainConstraint();
        // process the component being labelled
        if (dc == null || 
            // v3.3: support also the case that field is at the dependent end of an one-one association
            // !dc.auto()
            (!dc.auto() && !DataContainerToolkit.isDataFieldRealisingADependentAttribute(dc, 
                controller.getDomainClass(), controller.getDodm().getDsm()))
            ) {
          // get the value
          // throws exception if the value of df is
          // invalid w.r.t its domain constraint
          vals.put(dc, df.getValue());
        }
      } 
      /*v3.0: not used
      else if (c instanceof JScrollPane) {
        // TODO: read objects from the table (?)
        // process if dc.auto=false
        if (dc == null || !dc.auto()) {
          vals.put(dc, null);
        }
      }
      */
    }

    return vals;
    //return vals.toArray();
  }

  /**
   * @effects returns an array of values of all the <b>mutable</b> data fields
   *          of the {@link #components} panel
   */
  @Override
  public LinkedHashMap<DAttr,Object> getMutableState() throws ConstraintViolationException {
    LinkedHashMap<DAttr,Object> vals = new LinkedHashMap();
    
    JDataField df;
    DAttr dc;
    boolean mutable;

    for (Component c : comps.values()) {
      // TODO: improve this to support nested panel and table component
      if (c instanceof JDataField) {
        df = (JDataField) c;
        // process the component being labelled
        if (df != null) {
          dc = df.getDomainConstraint();
          mutable = (dc == null || (dc != null && dc.mutable()));
          // get the value
          // throws exception if the value of df is
          // invalid w.r.t its domain constraint
          if (mutable)
            vals.put(dc,df.getValue());
        }
      } 
      /*v3.0: not used
      else if (c instanceof JScrollPane) {
        // TODO: read all objects stored in the table
        //vals.add(null);
        //vals.put(dc,null);
      }
      */
    }

    return vals; //vals.toArray();
  }
  
  @Override
  public void setMutableState(DAttr attrib, Object val) {
    JDataField df;
    DAttr dc;
    
    JComponent c = comps.get(attrib);
    if (c != null && c instanceof JDataField) {
      df = (JDataField) c;
      // assumes df is mutable
      // mutable = (dc == null || (dc != null && dc.mutable()));
      // update the value
      df.setValue(val);
    }
  }
      
  @Override
  public void setMutableState(Object[] vals) {
    // find all mutable components and update their values
    JDataField df;
    JComponent c;
    int fieldIndex = 0;
    DAttr dc;
    boolean mutable;
    Object fieldVal;
    
    for (Entry<DAttr, JComponent> e : comps.entrySet()) {
      c = e.getValue();
      dc = e.getKey();
      if (c instanceof JDataField) {
        df = (JDataField) c;
        // process the component being labelled
        if (df != null) {
          mutable = (dc == null || (dc != null && dc.mutable()));
          if (mutable) {
            fieldVal = vals[fieldIndex];
            // update the value
            df.setValue(fieldVal);
            fieldIndex++;
          } else {
            // reset
            df.reset();
          }
        }
      }
    }
  }
  
  @Override
  public void setUserSpecifiedState(Object[] vals) {
    // find all non-auto components and update their values
    JDataField df;
    JComponent c;
    int fieldIndex = 0;
    DAttr dc;
    boolean settable;
    Object fieldVal;
    
    for (Entry<DAttr, JComponent> e : comps.entrySet()) {
      c = e.getValue();
      dc = e.getKey();
      if (c instanceof JDataField) {
        df = (JDataField) c;
        // process the component being labelled
        if (df != null) {
          settable = (dc == null || (dc != null && !dc.auto()));
          if (settable) {
            fieldVal = vals[fieldIndex];
            // update the value
            df.setValue(fieldVal);
            fieldIndex++;
          } else {
            // reset
            df.reset();
          }
        }
      }
    }
  }
  
  /* (non-Javadoc)
   * @see domainapp.basics.view.JDataContainer#setDataFieldValue(domainapp.basics.model.meta.DAttr, java.lang.Object)
   */
  @Override
  public Object setDataFieldValue(DAttr attrib, Object value) {
    JComponent c = getComponent(attrib);
    JDataField df;
    Object oldVal = null;
    if (c != null && c instanceof JDataField) {
      df = (JDataField) c;
      try {
        oldVal = df.getValue();
      } catch (Exception e) {} // ignore
      // update the value
      df.setValue(value);
    } 
    
    return oldVal;
  }
  
  /**
   * @effects returns the values of the data fields in the GUI region
   *          <code>region</code>.
   * 
   * @requires <code>region</code> is in {@link #containerMap}
   * 
   */
  @Override
  public Object[] getSearchState() {
    /**
     * the search tool bar is shared by all the panels in the same AppGUI, thus,
     * if this panel is nested then get the parent's search state and so on else
     * get its own
     */
    if (parent != null)
      return parent.getSearchState();
    else
      return controller.getCreator().getGUI().getSearchToolBarTextState();
  }
  
//  @Override
//  public List getDataModel() {
//    return controller.getCurrentObjects();
//  }

  @Override
  public void updateDataComponent(String attribName) throws NotFoundException {
    Object currentObj = controller.getCurrentObject();
    
    if (currentObj != null) {
      Class c = controller.getDomainClass();
      DSMBasic dsm = controller.getCreator().getDomainSchema();
      DAttr attrib = dsm.getDomainConstraint(c, attribName);
      JComponent comp = getComponent(attrib);
      JDataField dcomp;
      Object fieldVal;
      if (comp != null) {
        if (comp instanceof JDataField) {
          dcomp = (JDataField) comp;
          fieldVal = dsm.getAttributeValue(c, currentObj, attrib);
          // update the value
          dcomp.setValue(fieldVal);
        } 
        // v3.4: added support for sub-containers
        else {
          JDataContainer cont = DataContainerToolkit.toDataContainer(comp);
          if (cont != null) {
            cont.getController().refresh();
          }
        }
      }
    }
  }
  
  /**
   * This method is invoked when the data values of a domain object of the
   * domain class <code>this.cls</code> have been changed.
   * 
   * @effects updates the data fields in the {@link #components} panel that
   *          display the values of the domain attributes of the domain object
   *          <code>object</code>.
   */
  @Override
  public void update(Object o) {
    //Object object = controller.getCurrentObject();

    final DODMBasic schema = controller.getCreator().getDodm();
    final DSMBasic dsm = schema.getDsm();
    
    // find all data components and update their values
    JDataField dcomp;
    JComponent c;
    int fieldIndex = 0;
    DAttr dc;
    Object fieldVal;
    for (Entry<DAttr, JComponent> e : comps.entrySet()) {
      c = e.getValue();
      dc = e.getKey();
      if (c instanceof JDataField) {
        dcomp = (JDataField) c;
        // process the component being labelled
        if (dcomp != null) {
          fieldVal = dsm.getAttributeValue(o, dc.name());
          // update the value
          dcomp.setValue(fieldVal);
          fieldIndex++;
        }
      }
    }
  }

  @Override
  public void updateGUI() {
    /*
     *   for each derived field
     *    read the values of fields from which this field is derived. 
     *    invoke DataController.derive(), which invokes the function C.derive() of the domain class C
     *    to obtain the derived value
     */
    // find all data components and update their values
    JDataField dcomp;
    JComponent c;
    DAttr dc;
    Object fieldVal;
    String[] derivedFrom;
    List vals;
    Object currentObj = controller.getCurrentObject();
    
    for (Entry<DAttr, JComponent> e : comps.entrySet()) {
      c = e.getValue();
      dc = e.getKey();
      derivedFrom = dc.derivedFrom();
      if (!Arrays.equals(derivedFrom, CommonConstants.EmptyArray)) {
        // a derived field
        dcomp = (JDataField) c;
        
        // get the deriving values
        vals = new ArrayList();
        for (int i = 0; i < derivedFrom.length; i++) {
          vals.add(getCompValue(derivedFrom[i]));
        }
        
        /*
         *  if there is a current object, 
         *    invoke derive method against this object
         *  else
         *    invoke static derive method against the class
         */
        if (currentObj != null) {
          fieldVal = controller.derive(currentObj, dc.name(), vals);
        } else {
          fieldVal = controller.derive(null, dc.name(), vals);
        }
        
        // update the value
        dcomp.setValue(fieldVal);
      }
    }    
  }
  
  /**
   * @effects 
   *  return the display value of the <tt>JDataField</tt> component of the 
   *  attribute <tt>attributeName</tt> in this, or 
   *  <tt>null</tt> if such attribute is not managed by <tt>this</tt>
   */
  private Object getCompValue(String attributeName) {
    JComponent c;
    JDataContainer dcont;
    DAttr dc;
    for (Entry<DAttr, JComponent> e : comps.entrySet()) {
      c = e.getValue();
      dc = e.getKey();
      
      if (dc.name().equals(attributeName)) {
        // found component
        if (c instanceof JDataField) {
          return ((JDataField) c).getValue();
        } 
//        else if (c instanceof JScrollPane){ 
//          dcont = (JDataContainer) ((JScrollPane) c).getViewport().getView();
//          return dcont.getDataModel();
//        } else if (c instanceof JDataContainer) {
//          return ((JDataContainer) c).getDataModel();
//        }
      }
    }
    
    // should not happen
    return null;
  }
  
  /**
   * This method is used to determine whether or not this panel contains another data container as 
   * one of its components. 
   * 
   * @effects if <code>this.comps</code> contains a <code>JDataContainer</code> component 
   *          returns <code>true</code>, else returns <code>false</code>  
   */
  public boolean isNested() {
    for (Component c: comps.values()) {
      if (View.isContainer(c)) {
        return true;
      }
    }
    
    return false;
  }
  
  @Override
  public void clear() {
    // find all data fields and update their values
    JDataField df;
    Object linkValue = getLinkValue();  // v3.1
    
    for (Component c : comps.values()) {
      // TODO: improve this to support nested panel and table component
      if (c instanceof JDataField) {
        df = (JDataField) c;
        if (df != null) {
          // if this is the link component then sets it to the link value
          // otherwise reset
          if (df == linkComponent) {
            // v3.1: moved up
            // Object linkValue = getLinkValue();
            if (linkValue != null)
              df.setValue(linkValue);
            else
              // v2.7.4: df.reset();
              df.clear();
          } else {
            // v2.7.4: df.reset();
            df.clear();
          }
        }
      }
    }
  }

  /* (non-Javadoc)
   * @see domainapp.basics.view.JDataContainer#onNewObject(Object)
   */
  @Override
  public void onNewObject(Object index) {
    DataContainerToolkit.onNewObject(this);
  }
  
  /* (non-Javadoc)
   * @see domainapp.basics.view.JDataContainer#onCreateObject(java.lang.Object)
   */
  @Override
  public void onCreateObject(Object obj) {
    DataContainerToolkit.onCreateObject(this, obj);
  }
  
  /* (non-Javadoc)
   * @see domainapp.basics.view.JDataContainer#onCancel()
   */
  @Override
  public void onCancel() {
    DataContainerToolkit.onCancel(this);
  }

  @Override
  public void reset() {
    // find all data fields and reset their values ready for user input
    JDataField df;
    for (Component c : comps.values()) {
      if (c instanceof JDataField) {
        df = (JDataField) c;
        if (df != null) {
          // if this is the link component then sets it to the link value
          // otherwise reset
          if (df == linkComponent) {
            Object linkValue = getLinkValue();
            if (linkValue != null)
              df.setValue(linkValue);
            else
              df.reset();
          } else {
            df.reset();
          }
        }
      }
    }
  }
  
  @Override
  public void refreshLinkedData() {
    // find the bounded data fields that are configured with a property to reload data on refresh 
    // and refresh their bindings to their target data sources
    JDataField df;
    JBindableField bdf;
    JComponent c;
    Region compCfg;
    for (Entry<JComponent,Region> e : cfgMap.entrySet()) {
      c = e.getKey();
      compCfg = e.getValue();
      if (c instanceof JDataField) {
        df = (JDataField) c;
        if (df != null) {
          if (df instanceof JBindableField
              && compCfg.getProperty(PropertyName.view_objectForm_dataField_reloadBoundedDataOnRefresh, 
                  Boolean.class, Boolean.FALSE) // added check that bdf.config has property reload set to true
              ) {
            bdf = (JBindableField ) df;

            // reload binding

            // clear attached data source buffer
            bdf.clearDataSource();
            
            // reload data
            bdf.reloadBoundedData();
            
            // if this is the link column then try resetting the value
            if (bdf == linkComponent) {
              Object linkValue = getLinkValue();
              if (linkValue != null)
                bdf.setValue(linkValue);
              else
                bdf.reset();
            }
          }
        }
      }
    }
  }
  
  @Override
  public void refreshTargetDataBindings() {
    // find all bounded data fields and refresh their bindings to their target data sources
    JDataField df;
    JBindableField bdf;
    for (Component c : comps.values()) {
      if (c instanceof JDataField) {
        df = (JDataField) c;
        if (df != null) {
          if (df instanceof JBindableField) {
            bdf = (JBindableField ) df;
            // reload binding

            // clear attached data source buffer
            bdf.clearDataSource();
            
            // reload data
            bdf.reloadBoundedData();
            
            // if this is the link column then try resetting the value
            if (bdf == linkComponent) {
              Object linkValue = getLinkValue();
              if (linkValue != null)
                bdf.setValue(linkValue);
              else
                bdf.reset();
            }
          }
        }
      }
    }
  }
  
  @Override
  public void refreshTargetDataBindingOfAttribute(DAttr attrib) {
    // find the bounded data field of attrib and refresh its binding to the target data source
    JDataField df;
    JBindableField bdf;
    for (Component c : comps.values()) {
      if (c instanceof JDataField) {
        df = (JDataField) c;
        if (df != null) {
          if (df instanceof JBindableField && df.getDomainConstraint() == attrib) {

            // exists the data field of attrib
            bdf = (JBindableField ) df;
            // reload binding

            // clear attached data source buffer
            bdf.clearDataSource();
            
            // reload data
            bdf.reloadBoundedData();
            
            // if this is the link column then try resetting the value
            if (bdf == linkComponent) {
              Object linkValue = getLinkValue();
              if (linkValue != null)
                bdf.setValue(linkValue);
              else
                bdf.reset();
            }
            
            break;
          }
        }
      }
    }
  }
  
  /**
   *  update the <tt>editable</tt> states of the data field components of this based on 
   *  the mutability of the field and the current user permissions.
   *  
   * @effects 
   *  for each data field
   *    if mutable = false
   *      do nothing
   *    else
   *      if current permission disallow update on the field
   *        set editable to false
   *      else
   *        do nothing
   */
  public void updateDataPermissions() {
    Collection<JComponent> dataComps = comps.values();
    
    JDataField df;
    DAttr dc;
    
    ControllerBasic ctl = controller.getCreator();
    /*v2.6.4b: fixed this - 
     * if this is a top-level container then use the userGUI's editability;
     * otherwise use the editability of the userGUI's component config of this container
    AppGUI userGUI = controller.getUser().getGUI();
    boolean userGUIEditable = userGUI.isEditable();
    */
    boolean containerEditable = isEditable();
    
    String attributeName; 
    boolean state;
    boolean currentState;
    Region compRegion;
    for (JComponent comp : dataComps) {
      if (comp instanceof JDataField) {
        df = (JDataField) comp;
        // retrieve the view region config of the component
        compRegion = cfgMap.get(comp);
        
        dc = df.getDomainConstraint();
        currentState = df.getEditable(); //v5.1c: df.isEditable();

        // v2.5.4: consider also view config.editable
        //if (dc.mutable()) {
        if (dc.mutable() && compRegion.getEditable() && containerEditable) {
          // permission is only applicable to fields that are both mutable and 
          // view config.editable = true
          
          // check the security permission
          attributeName = df.getDomainConstraint().name();
          state = ctl.getAttributeEditableState(attributeName);
          
          //debug
          //  System.out.printf("Attribute: %s; permission: %b%n", attributeName, state);
          
          // update editability if current state and state donot agree
          // but need to also take into consideration the mutability of the field:
          // - there is no need to change the immutable attribute ever regardless
          // of what the permission is
          if ((currentState && !state) || (!currentState && state)) {
            df.setEditable(state);
          } 
        } else if (currentState == true) {
          // for non-editable fields whose states were overriden before
          df.setEditable(false);
        }
      }
    }
  }
  
  @Override
  public boolean isEditable() {
    if (parent != null) {
      // this is a child container
      return 
          //parent.isEditable() && 
          //parent.getComponentConfig(this.getGUIComponent()).getEditable();
          containerCfg.getEditable();
    } else {
      // top-level container
      return controller.getUser().getGUI().isEditable();
    }
  }
  
  /**
   * @version 
   * - 3.2: support scope definition 
   */
  @Override
  public void setEditable(final JDataContainer sourceContainer, final boolean tf, final boolean recursive) {
    Collection<JComponent> dataComps = comps.values();
    
    JDataField df;
    JDataContainer dcont;
    DAttr dc;
    boolean currentState; 
    /* v3.2: support scope definition of this container */
    Boolean scopeDefEditable = getController().getUserGUI().getEditableByScope(this);
    
    for (JComponent comp : dataComps) {
      if (comp instanceof JDataField) {
        // normal data field
        /* v3.2
        df = (JDataField) comp;
        dc = df.getDomainConstraint();
        currentState = df.isEditable();

        if (currentState != tf) {
          if (dc.mutable()) {
            df.setEditable(tf);
          } else if (currentState == true) {
            // for non-editable fields whose states were overriden before
            df.setEditable(false);
          }
        } */ 
        if (this == sourceContainer || scopeDefEditable == null) {
          // only do setting if this is the source container (i.e. this is called when the container is created)
          // OR if scopeDefEditable is not defined; because if it is defined then 
          // its editability takes precedence and had already been set when this container was created
          df = (JDataField) comp;
          dc = df.getDomainConstraint();
          currentState = df.getEditable(); // v5.1c: .isEditable();

          if (currentState != tf) {
            if (dc.mutable()) {
              df.setEditable(tf);
            } else if (currentState == true) {
              // for non-editable fields whose states were overriden before
              df.setEditable(false);
            }
          }           
        } 
      } 
      /* v3.2: simplified
      else if (comp instanceof JDataContainer) {
        // nested container
        if (recursive) {
          ((JDataContainer) comp).setEditable(tf, recursive);
        }
      } else if (comp instanceof JScrollPane) {
        // scrollable nested container
        // container, process if recursive = true
        if (recursive) {
          JDataContainer dcont = (JDataContainer) ((JScrollPane) comp).getViewport().getView();
          dcont.setEditable(tf, recursive);
        }
      }*/
      else if (recursive && 
          ((comp instanceof JDataContainer) || (comp instanceof JScrollPane))) {
        // a sub-container: recursive processing (process on)
        dcont = DataContainerToolkit.toDataContainer(comp);
        dcont.setEditable(sourceContainer, tf, recursive);
      }
    } // end for: comp
  }
  
//  /**
//   * @version 
//   * - 3.2: support scope definition 
//   */
//  private void setEditable(JDataContainer sourceContainer, boolean tf, boolean recursive) {
//    Collection<JComponent> dataComps = comps.values();
//    
//    JDataField df;
//    JDataContainer dcont;
//    DomainConstraint dc;
//    boolean currentState; 
//    /* v3.2: support scope definition of this container */
//    Boolean scopeDefEditable = getController().getUserGUI().getEditableByScope(this);
//    
//    for (JComponent comp : dataComps) {
//      if (comp instanceof JDataField) {
//        // normal data field
//        if (this == sourceContainer || scopeDefEditable == null) {
//          // only do setting if scopeDefEditable is not defined; because if it is defined then 
//          // its editability takes precedence and had already been set when this container was created
//          df = (JDataField) comp;
//          dc = df.getDomainConstraint();
//          currentState = df.isEditable();
//
//          if (currentState != tf) {
//            if (dc.mutable()) {
//              df.setEditable(tf);
//            } else if (currentState == true) {
//              // for non-editable fields whose states were overriden before
//              df.setEditable(false);
//            }
//          }           
//        } 
//      } 
//      else if (recursive && 
//          ((comp instanceof JDataContainer) || (comp instanceof JScrollPane))) {
//        // a sub-container: recursive processing (process on)
//        dcont = DataContainerToolkit.toDataContainer(comp);
//        
//        if (dcont instanceof DefaultPanel)
//          ((DefaultPanel)dcont).setEditable(sourceContainer, tf, recursive);
//        else
//          dcont.setEditable(tf, recursive);
//      }
//    } // end for: comp
//  }
  
  @Override
  public void forceEditable() {
    Collection<JComponent> dataComps = comps.values();
    JDataField df;
    
    for (JComponent comp : dataComps) {
      if (comp instanceof JDataField) {
        df = (JDataField) comp;
        if (!df.isEditable()
            && !df.isDerived() // v2.7.2
            ) {
          df.setEditable(true);
        }
      }
    }
  }

  /**
   * @effects returns <code>true</code> if <code>panel</code> is the parent or
   *          an ancestor of the data field <code>df</code>.
   */
  public static boolean isAncestorOf(DefaultPanel panel, JDataField df) {
    Component comp = df;
    Container parent;
    do {
      parent = comp.getParent();
      if (parent == panel) {
        return true;
      }
      comp = parent;
    } while (comp != null);

    return false;
  }

  @Override
  public void handleDataFieldEditing(KeyEvent e, JDataField df) {
    // empty
  }
  
  @Override
  public void handleDataFieldValueChanged(JDataField df) {
    // use a shared handler
    DataContainerToolkit.handleDataFieldValueChanged(this, df);
  }

  /**
   * 
   * @version 
   * - 5.2: (performance) use {@link #childDataContainers} derived attribute
   */
  @Override
  public Iterator<JDataContainer> getChildContainerIterator() {
    /* v5.2 
    Component comp;
    Component[] comps = getComponents(null);
    Collection<JDataContainer> childDataContainers = new ArrayList<JDataContainer>();
    
    for (int i = 0; i < comps.length; i++) {
      comp = comps[i];
      if (comp instanceof JScrollPane) {
        comp = ((JScrollPane) comp).getViewport().getComponent(0);
      }

      if (comp instanceof JDataContainer) {
        childDataContainers.add((JDataContainer)comp);
      }
    }
    */
    
    return (!childDataContainers.isEmpty() ? childDataContainers.iterator() : null);
  }

  @Override
  public boolean isSearchEnabled() {
    return true;
  }
  
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /**
   * @effects 
   * 
   * @version 5.1
   */
  @Override
  public boolean equals(Object obj) {
    return DataContainerToolkit.equals(this, obj);
  }

  @Override
  public boolean equalsStrictly(JDataContainer obj) {
    return obj != null && this == obj;
  }
  
  
  @Override
  public int getChildContainerCount() {
    return (childDataContainers != null) ? childDataContainers.size() : 0;
  }


  /**
   * @overview 
   *  A custom policy that traverses only a sub-set of the data fields of this gui.
   *   
   * @author dmle
   */
  private static class CustomFocusTraversalPolicy extends FocusTraversalPolicy {
    Vector<Component> order;

    /**
     * @requires order != null
     */
    public CustomFocusTraversalPolicy() {
      this.order = new Vector<Component>();
    }

    /**
     * @requires
     *  comp != null
     * @effects 
     *  add next component that will receive the focus
     */
    void addComponent(Component comp) {
      order.add(comp);
    }
    
    public Component getComponentAfter(Container focusCycleRoot,
        Component aComponent) {
      if (isEmpty())
        return null;
      
      int idx = (order.indexOf(aComponent) + 1) % order.size();
      return order.get(idx);
    }

    public Component getComponentBefore(Container focusCycleRoot,
        Component aComponent) {
      if (isEmpty())
        return null;

      int idx = order.indexOf(aComponent) - 1;
      if (idx < 0) {
        idx = order.size() - 1;
      }
      return order.get(idx);
    }

    public Component getDefaultComponent(Container focusCycleRoot) {
      if (isEmpty())
        return null;

      return order.get(0);
    }

    public Component getLastComponent(Container focusCycleRoot) {
      if (isEmpty())
        return null;

      return order.lastElement();
    }

    public Component getFirstComponent(Container focusCycleRoot) {
      if (isEmpty())
        return null;

      return order.get(0);
    }
    
    public boolean isEmpty() {
      return order.isEmpty();
    }
  } // end CustomTraversalPolicy  
} // end DefaultPanel


 