package jda.mosa.view.assets.datafields.list;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;

/**
 * @overview
 *   A <tt>Combo-style</tt> field that displays values that can be bound to the domain field of a domain class. 
 *   
 * @author dmle
 */
public class JComboField<C> extends JAbstractListDataField {

  //private static final int DEFAULT_HEIGHT = 27;
  
  private DefaultComboBoxModel model;
  private JComboBox combo; 
  
  /** a shared Runnable that is used to highlight the text field component 
   * of a JSpinner when it is on focus*/
//  private TextFieldAndButtonHandler textFieldAndButtonHandler;
  
  /**
   * Creates a combo data field constrained by the domain constraint
   * <code>domainConstraint</code>, whose list of values is <code>values</code> may be bounded to 
   * a domain field specified by <tt>boundConstraint</tt>, the 
   * initially selected value is <code>val</code>, and whose editability is
   * determined by <code>editable</code>.
   * 
   * <p>
   * If <code>val = null</code> then the first element of the list (if any) is
   * selected.
   * 
   * <p>
   * If <code>values</code> is empty then the list is initialised to contain an
   * empty string (
   * 
   * <pre>
   * &quot;&quot;
   * </pre>
   * 
   * ) element.
   * 
   */
  public JComboField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, DAttr boundConstraint,
      Boolean editable) {
    this(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, 
        false /* auto-validation: generally there is no need to auto-validate since values are selected from a
               * list of allowed values. An exception to this is for bounded fields whose bounded domain objects 
               * are specified with cardinality constraints. For performance reason such constraints
               * should be validated by the application rather by this field.
               * If this feature is important for an application, however, then auto-validation can still be 
               * turned-on by using the second constructor (below)
               */
        );
  }
  
  public JComboField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, DAttr boundConstraint,
      Boolean editable, Boolean autoValidation) {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, autoValidation);
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    ComboEventHandler eventHandler = new ComboEventHandler();

    // mutable combo box model
    model = new DefaultComboBoxModel<>();
    
    combo = new JComboBox(model);

    // use this to detect user's action on the combo-box item list
    //v3.1: this does not work properly
    // combo.addPopupMenuListener(eventHandler);
    
    // listen to item selection event
    combo.addItemListener(eventHandler);
    
    // v2.7.4: combo does not use text field to render so domain field width cannot be used.
    // use the width, height properties instead
    int width, height;
    Dimension configDim = getConfiguredDimension();
    if (configDim != null) {
      // configured width, height(in pixels)
      width = (int) configDim.getWidth(); 
      height = (int) configDim.getHeight();
      combo.setPreferredSize(new Dimension(width, height));
    }

    // set up alignment
    int align = getAlignX();
    ((JLabel)combo.getRenderer()).setHorizontalAlignment(align);
    
//    // v3.0: estimate initial width from content (not good estimate)
//    else {
//      int contentLength = getDomainFieldWidth();
//      Dimension prefSize = combo.getPreferredSize();
//      int defWidth = (int) prefSize.getWidth();
//      
//      width = computeWidth(contentLength, defWidth); 
//      height = (int) prefSize.getHeight();
//      combo.setPreferredSize(new Dimension(width, height));
//    }
    
    setGUIComponent(combo); //display = combo;  // this must be placed before setEditable (Below)
    
    //setEditable(editable);
    setEditable(//v5.1c: isEditable()
        getEditable()
        );

    return combo;//display;
  }

//  /**
//   * @effects 
//   *  roughly estimate and return a width (in pixel) from the content length (typically number of text chars)
//   */
//  private int computeWidth(int contentLength, int defWidth) {
//    // 5 times the default width
//    return (int) (contentLength * 0.45d * defWidth); //Math.max(contentLength, defWidth);
//  }

// v2.7.4: this code is obsolete by the use of fireStateChanged()  
//  @Override
//  public void addMouseListener(MouseListener ml) {
//    JComboBox combo = (JComboBox) display;
//
//    addMouseListener(combo, ml);
//    
//    // these have no effect!!!!
//    //    combo.addMouseListener(ml);
//    //    combo.getEditor().getEditorComponent().addMouseListener(ml);
//
//    /* neither do these!!!
//      Component editorComp = combo.getEditor().getEditorComponent();
//    
//      if (editorComp instanceof Container)
//        addMouseListener((Container)editorComp, ml);
//    */
//  }
//
//  // add mouse listener to comp and its child components
//  private void addMouseListener(Container comp, MouseListener ml) {
//    comp.addMouseListener(ml);
//    
//    Component[] comps = comp.getComponents();
//    for (Component c: comps) {
//      c.addMouseListener(ml);
//    }
//  }

  @Override
  public void setEditable(boolean state) {
    super.setEditable(state);
    
    // it is not enough to disable the text field of the combo box
    // because the value selector button is still enabled
    // thus the only possible way is to disable the whole spinner field!
    
    JComboBox combo = (JComboBox)getGUIComponent();
    
    boolean currState = combo.isEnabled();
    
    if (state != currState) {
      combo.setEnabled(state);
    }

//    if (!state) {
//      ((JLabel) combo.getRenderer()).
//      setForeground(GUIToolkit.COLOUR_FOREGROUND_DISABLED);
//    }
  }
  
  @Override
  protected Object getSelectedValue() {
    Object displayVal = ((JComboBox) getGUIComponent()).getSelectedItem();
    
    if (displayVal != null && displayVal.equals(Nil))
      return null;
    else
      return displayVal;
  }
  

  /**
   * @effects
   * @version 3.2
   */
  /* (non-Javadoc)
   * @see domainapp.view.datafields.list.JAbstractListDataField#getSelectedValues()
   */
  @Override
  protected Collection getSelectedValues() {
    //TODO: implement this if combo allows multiple-selected values
    throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
        new Object[] {this.getClass().getSimpleName()+".getSelectedValues()"});
  }
  

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.view.datafields.list.JAbstractListDataField#setSelectedValues(java.util.List)
   */
  @Override
  protected void setSelectedValues(List selectedIndices) {
    // TODO: support multiple selected values if needed
    ((JComboBox) getGUIComponent()).setSelectedIndex((Integer)selectedIndices.get(0));
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.view.datafields.list.JAbstractListDataField#getValueIndex(java.lang.Object)
   */
  @Override
  protected int getValueIndex(Object dispVal) {
    return model.getIndexOf(dispVal);
  }
  
  @Override
  protected void setDisplayValue(Object dispVal) {
    /* v3.3: improved to clear the selection if dispVal is not in the list
    ((JComboBox) getGUIComponent()).setSelectedItem(dispVal);
    */
    JComboBox combo = (JComboBox) getGUIComponent(); 
    int dispValIndex = getValueIndex(dispVal);
    if (dispValIndex >= 0 || 
        combo.isEditable()    // editable combo: add item to combo
        ) { 
      // set selected
      combo.setSelectedItem(dispVal);
    } else { // not in list
      // clear selection
      combo.setSelectedItem(null);
    }
    
    // v2.7.4: reset status
    setIsValidatedOnItemChanged(true); //if (!validatedOnItemChanged) validatedOnItemChanged = true;
    
    setIsValidated(true); //if (!validated) validated=true;
    
    updateGUI(false);
  }  
  
  @Override
  protected void addValues(List displayValues) {
    //System.out.println(this.getClass().getSimpleName()+"("+getAttributeName()+").addValues()");

    // add all elements to model
    for (Object o : displayValues) {
      // v3.1: use combo
      //model.addElement(o);
      combo.addItem(o);
    }
  }
  
  /**
   * @effects 
   *  remove the display objects contained in <tt>displayObjects</tt> from 
   *  this. 
   *  
   *  <p>If this results in an empty state 
   *      refresh the data
   *      set this.value to null
   *     Else if this.value is contained in objects
   *      set this.value to the object matching the new display value in this
   */
  @Override
  protected void deleteValues(List objects, List displayObjects) {
    // remove from model
    for (Object o : displayObjects) {
      // v3.1: use combo
      // model.removeElement(o);
      combo.removeItem(o);
    }
    
    if (// v3.1: use combo -> model.getSize() == 0
        combo.getItemCount() == 0
        ) {
      setValueDirectly(null); //value = null;
      refresh();
    } else if (objects.contains(getValueDirectly())) { //value)) {
      // set this.value to the object matching the new display value in this
      //TODO: should we wait for state change update (fired by the deletion on the model) to finish?
      // value = getRawValue();
      setValueDirectly(null); //value = null;
    }
  }

  @Override
  protected void deleteValues() {
    //v3.1: use combo 
    // model.removeAllElements();

//    System.out.println(this.getClass().getSimpleName()+"("+getAttributeName()+").deleteValues()");
//
//    System.out.println("BEFORE");
//    System.out.println("  current count: " + combo.getItemCount());

    combo.removeAllItems();
    
//    System.out.println("AFTER delete");
//    System.out.println("  item count: " + combo.getItemCount());
  
    /* v3.1
    if (!isBounded()) {
      value = getInitValue();
    } */
    setValueDirectly(null); //value = null;
  }
  
  @Override
  protected void setValues(List displayValues) {
    // debug
    //System.out.println(this.getClass().getSimpleName()+"("+getAttributeName()+").setValues()");
//    System.out.println("BEFORE");
//    System.out.println("  current count: " + combo.getItemCount());
//    System.out.println("  new items: " + displayValues);
    
    for (Object o : displayValues) {
      //v3.1: use combo
      // model.addElement(o);
      combo.addItem(o);
    }
    
//    System.out.println("AFTER");
//    System.out.println("  current count: " + combo.getItemCount());
  }

  @Override
  protected void updateValue(Object o, Object oldVal, Object newVal) {
    int ind = model.getIndexOf(oldVal);

    // add first, then remove
    /*v3.1: use combo
    model.insertElementAt(newVal, ind);
    model.removeElementAt(ind+1);
    */
    
    if (ind == -1) { // v3.1: added this check
      // error (should not happen)
      System.err.println(this+"updateValue: Error: Could not find item " + oldVal);
    } else {
      combo.insertItemAt(newVal, ind);
      combo.removeItemAt(ind+1);
    }
  }

  @Override
  protected Object getFirstValue() {
    return // v3.1: use combo 
        //model.getElementAt(0);
        combo.getItemAt(0);
  }

  public Font getTextFont() {
    if (hasGUIComponent()) { //display != null) { 
      JComboBox combo = (JComboBox) getGUIComponent();
      return combo.getEditor().getEditorComponent().getFont();
    } else {
      return super.getFont();
    }
  }
  
  @Override
  public boolean isMouseClickConsumableByValueChanged() {
    return true;
  }

  /**
   * @overview 
   *  A helper class to handle item and mouse actions on the combo box.
   *  
   * @version 
   * - 3.0<br>
   * - 3.1: improved to handle model event
   * @author dmle
   */
  private class ComboEventHandler 
    implements ItemListener 
               //v3.1: not works properly 
               // PopupMenuListener
               {
    
    // replaced by state values;
    //private boolean popUpVisible;
    
    @Override // ItemListener: when item is selected on the combo
    public void itemStateChanged(ItemEvent e) {
      // update this.value
      int state = e.getStateChange();
      
      if (state == ItemEvent.SELECTED) {
        boolean popUpVisible = combo.isPopupVisible();
        
        Object selectedObj = //v3.1: use combo -> model.getSelectedItem();
            combo.getSelectedItem();
        
        setValueOnSelection(selectedObj);
        
        // debug
//        System.out.printf("PopupVisible? %b%n", popUpVisible);

        // only fire value changed when user has *completed* making the selection:
        // this happens either by user either 
        //    (1) using just the mouse (popUpVisible = true) or 
        //    (2) using keys and concluded by pressing Enter or a mouse click
        
        // only fire state changed if user made the item selection by first openning
        // the combo's popup (other causes: e.g. by setting value directly from the code 
        // are excluded)
        if (//v3.1: mouseClickedOnButton
            popUpVisible   
            ) {
          // v2.7.4: flag validate on item changed
          setIsValidatedOnItemChanged(false); //validatedOnItemChanged = false;
          
          // v2.7.4: call InputHelper directly here b/c 
          // combo box's mouse event is not detected effectively by InputHelper
          fireValueChanged();
          
          // debug
          //System.out.println(this.getClass().getSimpleName()+".Field value changed: " + this);
        }
      }
    }

//    @Override // PopupMenuListener
//    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
////      System.out.println("pup: visible");
//      // v3.1: mouseClickedOnButton = true;
////      if (!popUpVisible)
////        popUpVisible = true;
//    }
//    
//    @Override // PopupMenuListener
//    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//      // not useful!!
//    }
//    
//    @Override // PopupMenuListener
//    public void popupMenuCanceled(PopupMenuEvent e) {
//      // not useful!!
//    }
  } // end ComboEventHandler

}
