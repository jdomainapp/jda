package jda.mosa.view.assets.datafields.list;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;

/**
 * @overview
 *   A <tt>Combo-style</tt> field that displays values that can be bound to the domain field of a domain class. 
 *   
 * @author dmle
 */
public class JListField<C> extends JAbstractListDataField {

  private static final int DEFAULT_NUMBER_VISIBLE_ROWS = 5;
  private static final int DEFAULT_SELECTION_MODE = 
      ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
      //ListSelectionModel.SINGLE_SELECTION;
  
  private static final int DEFAULT_WIDTH = 200;

  /**should be calculated based on {@link #DEFAULT_NUMBER_VISIBLE_ROWS}*/
  private static final int DEFAULT_HEIGHT = computeHeight(DEFAULT_NUMBER_VISIBLE_ROWS); //90;
  
  private DefaultListModel model;
  
  /**
   * Creates a list data field constrained by the domain constraint
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
  public JListField(DataValidator validator, Configuration config,  
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
  
  public JListField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, DAttr boundConstraint,
      Boolean editable, Boolean autoValidation) {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, autoValidation);
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {

    int length = getDomainFieldWidth();

    // mutable combo box model
    model = new DefaultListModel<>();
    
    JList list = new JList(model);

    list.setVisibleRowCount(DEFAULT_NUMBER_VISIBLE_ROWS);
    list.setSelectionMode(DEFAULT_SELECTION_MODE);
    
    // listen to item selection event
    ListEventHandler handler = new ListEventHandler();
    list.addListSelectionListener(handler);
    list.addKeyListener(handler);
    
    //TODO: convert length into actual width
    //int height = combo.getPreferredSize().height;
    //combo.setMinimumSize(new Dimension(length*10, height));
//    javax.swing.JTextField editor = (javax.swing.JTextField) combo.getEditor().getEditorComponent();
//    
//    int textLength = Math.min(MAX_DISPLAYABLE_TEXT_WIDTH, length);
//    editor.setColumns(textLength);
    
    setGUIComponent(list); //display = list;

    setEditable(//v5.1c: isEditable()
        getEditable());

    // put list in a scrollpane and return that 
    JScrollPane scrollable = new JScrollPane(list);
//    ((javax.swing.JTextField) combo.getEditor().getEditorComponent()).
//      setDisabledTextColor(GUIToolkit.COLOUR_FOREGROUND_DISABLED);
    
    int width, height;
    Dimension configDim = getConfiguredDimension();
    if (configDim == null) {
      width = DEFAULT_WIDTH; 
      height = DEFAULT_HEIGHT;
    } else {
      // configured width = actual width (in pixels)
      width = (int) configDim.getWidth(); 

      // configured height = visible rows
      int numVisibleRows = (int) configDim.getHeight();
      height = computeHeight(numVisibleRows);
    }
    
    scrollable.setPreferredSize(new Dimension(width, height));
    
    return scrollable; // display
  }

  /**
   * @effects 
   *  return a rough estimation of the actual height of JList (in pixels) based on
   *  <tt>numVisibleRows</tt>  
   */
  private static int computeHeight(int numVisibleRows) {
    return 18 * numVisibleRows;
  }

  @Override
  public void setEditable(boolean state) {
    super.setEditable(state);
    
    // it is not enough to disable the text field of the combo box
    // because the value selector button is still enabled
    // thus the only possible way is to disable the whole spinner field!
    
    JList list = (JList)getGUIComponent();
    
    boolean currState = list.isEnabled();
    
    if (state != currState) {
      list.setEnabled(state);
    }

    if (!state) {
      list.getCellRenderer().getListCellRendererComponent(list, 
          null, 0, false, false).
        setForeground(GUIToolkit.COLOUR_FOREGROUND_DISABLED);
    }
  }
  
  @Override
  protected Object getSelectedValue() {
    JList list = (JList) getGUIComponent();
    List selectedVals = list.getSelectedValuesList();
    
    if (!selectedVals.isEmpty())
      return selectedVals.get(0);
    else  // empty
      return null;
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
    JList list = (JList) getGUIComponent();
    List selectedVals = list.getSelectedValuesList();
    
    if (!selectedVals.isEmpty()) // may contain Nil
      return selectedVals;
    else  // empty
      return null;
  }
  
  /* (non-Javadoc)
   * @see domainapp.view.datafields.list.JAbstractListDataField#setSelectedValues(java.util.List)
   */
  @Override
  protected void setSelectedValues(List selectedIndices) {
    JList list = (JList) getGUIComponent();
    int[] intIndices = new int[selectedIndices.size()];
    for (int i = 0; i < intIndices.length; i++) intIndices[i] = (Integer) selectedIndices.get(i);
    
    list.setSelectedIndices(intIndices);
  }

  /* (non-Javadoc)
   * @see domainapp.view.datafields.list.JAbstractListDataField#getValueIndex(java.lang.Object)
   */
  @Override
  protected int getValueIndex(Object dispVal) {
    return model.indexOf(dispVal);
  }
  
  @Override
  protected void setDisplayValue(Object dispVal) {
    boolean shouldScroll = true;
    JList list = (JList) getGUIComponent(); 
    list.setSelectedValue(dispVal, shouldScroll);    
    
    // v2.7.4
    setIsValidated(true); //if (!validated) validated = true;
    
    updateGUI(false);
    
    //TODO: use this
    // list.setSelectedIndices(indices);
  }  
  
  @Override
  protected void addValues(List displayValues) {
    // add all elements to model
    for (Object o : displayValues) {
      model.addElement(o);
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
      model.removeElement(o);
    }
    
    if (model.getSize() == 0) {
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
    model.removeAllElements();
    if (!isBounded()) {
      setValueDirectly(getInitValue()); //value = getInitValue();
    }
  }

  /**
   * @effects 
   *  if there are selected values 
   *    remove them
   *  else
   *    do nothing
   * @version 3.0
   */
  public void deleteSelectedValues() {
    JList list = (JList) getGUIComponent();
    int[] selectedIndices = list.getSelectedIndices();
    if (selectedIndices.length > 0) {
      for (int i = selectedIndices.length-1; i >=0; i--) {
        // must delete in reverse order
        model.remove(selectedIndices[i]);
      }
    }
  }
  
  @Override
  protected void setValues(List displayValues) {
    for (Object o : displayValues) {
      model.addElement(o);
    }
  }

  @Override
  protected void updateValue(Object o, Object oldVal, Object newVal) {
    int ind = model.indexOf(oldVal);
    
    // add first, then remove
    model.insertElementAt(newVal, ind);
    
    model.removeElementAt(ind+1);
  }

  @Override
  protected Object getFirstValue() {
    return model.getElementAt(0);
  }

  @Override
  public boolean isMouseClickConsumableByValueChanged() {
    return true;
  }
  
  /**
   * @overview 
   *  A helper class to handle list and other user events on JList
   *  
   * @author dmle
   */
  private class ListEventHandler 
    extends KeyAdapter   // v3.0
    implements ListSelectionListener
    {

    //TODO: handle 'Esc' key event to clear all selection and set value to null  
    
    @Override // ListSelectionListener
    public void valueChanged(ListSelectionEvent e) {
      boolean stillSelecting = e.getValueIsAdjusting();
      if (!stillSelecting) {
        // done selecting
        JList list = (JList) e.getSource();
        List selected = list.getSelectedValuesList();
        
        // v3.2: use focus to determine whether user is directly interacting with this field OR 
        // this field is being manipulated via code (i.e. calling setSelectedValues)
        boolean hasFocus = list.isFocusOwner();
        
        if (!selected.isEmpty()) {
          if (debug)
            System.out.printf("%s: selected: %n  %s %n", JListField.this, selected);
          
          Object selectedObj = selected.get(0);
          
          setValueOnSelection(selectedObj);
          
          if (hasFocus) { // v3.2: only fire state change if this field has focus (i.e. user is directly interacting with it)
            setIsValidatedOnItemChanged(false);
            
            //System.out.printf("fire state changed from...%s%n", JComboField.this);
            //v3.2: fireStateChanged();
            fireValueChanged();
          }
        }
      }
    }

    @Override // KeyAdapter
    public void keyTyped(KeyEvent e) {
      // handle special keys: Delete
      char key = e.getKeyChar();
      if (key == KeyEvent.VK_DELETE) {
        // delete the selected items
        deleteSelectedValues();
      }
    }
  } /**end {@link ListEventHandler}*/
}
