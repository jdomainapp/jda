package jda.mosa.controller.assets.eventhandler;

import static jda.modules.mccl.conceptmodel.controller.LAName.Exit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.view.View;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.panels.DefaultPanel;
import jda.mosa.view.assets.panels.TitlePanel;
import jda.mosa.view.assets.tables.JDataTable;
import jda.mosa.view.assets.tables.JObjectTable;
import jda.util.events.InputHandler;
import jda.util.events.StateChangeListener;

/**
   * A sub-class of {@see InputHandler} which is used as a listener for user
   * mouse and keyboard events on the GUI components on an <code>AppGUI</code>
   * object.
   * 
   */
  public class InputHelper extends InputHandler implements StateChangeListener {
    private JDataContainer currentContainer;
    // private JDataField currentDataField;

    private ControllerBasic mainCtl;
    private InputHelper.TableEventHelper tableHandler;
    
    private static InputHelper instance;
    
    private final Cursor HAND_CURSOR;
    private final Cursor SEARCH_ADD_CURSOR;
    private final Cursor DEFAULT_CURSOR;

    /**Non-editing key codes **/
    private final static int[] NON_EDITING_KEYS = {
        KeyEvent.VK_F1,KeyEvent.VK_F2,KeyEvent.VK_F3,KeyEvent.VK_F4,
        KeyEvent.VK_F5,KeyEvent.VK_F6,KeyEvent.VK_F7,KeyEvent.VK_F8,
        KeyEvent.VK_F9,KeyEvent.VK_F10,KeyEvent.VK_F11,KeyEvent.VK_F12,
        KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,KeyEvent.VK_UP,KeyEvent.VK_DOWN,
        KeyEvent.VK_ESCAPE, KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_WINDOWS, 
        KeyEvent.VK_NUM_LOCK, KeyEvent.VK_PRINTSCREEN, KeyEvent.VK_INSERT, 
        KeyEvent.VK_HOME, KeyEvent.VK_END, KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN
    };

    private InputHelper(ControllerBasic main) {
      mainCtl = main;
      // Toolkit tk = Toolkit.getDefaultToolkit();
      HAND_CURSOR = GUIToolkit.getCursor("handcursor.gif", new Point(0, 0),
          "hand");
      SEARCH_ADD_CURSOR = GUIToolkit.getCursor("finda.gif", new Point(0, 0),
          "search");
      // tk.createCustomCursor(
      // tk.getImage(AppGUI.class.getResource(AppGUI.IMG_LOCATION
      // + "handcursor.gif")),
      // new Point(0, 0), "hand");
      // SEARCH_ADD_CURSOR = tk.createCustomCursor(
      // tk.getImage(AppGUI.class.getResource(AppGUI.IMG_LOCATION
      // + "finda.gif")), new Point(0, 0), "search");
      DEFAULT_CURSOR = Cursor.getDefaultCursor();
      
      tableHandler = new TableEventHelper(this);
    }

    public static InputHelper getInstance(ControllerBasic main) {
      if (instance == null) {
        instance = new InputHelper(main);
      }

      return instance;
    }

    /**
     * @effects 
     *  return this.currentContainer
     */
    public JDataContainer getCurrentContainer() {
      return currentContainer;
    }
    
    /**
     * This method is used in tandem with the focus policy of the data panel. It
     * is tasked to highlight the data panel that contains a currently in-focus
     * data field
     * 
     * 
     * @effects if the focus owner belongs to a data field and the data panel
     *          containing this field is not in focus set the data panel to
     *          focus
     */
    // @Override
    // public void focusGained(FocusEvent e) {
    // Component gain = e.getComponent();
    //
    // focusGainedOnComponent(gain);
    // }

    /**
     * The current implementation of {@link #MouseAdapter.mouseClicked} highlights the first
     * <b>named</b> {@see DefaultPanel} in the containment hierarchy that
     * contains the currently selected component. The <code>hasFocus</code>
     * attribute of the concerned panel is also set to <code>true</code>.
     * 
     * <p>
     * Note that due to the design of the Swing containment hierarchy, only one
     * panel per <code>AppGUI</code> can have focus at a given time.
     * 
     */
    public void mouseClicked(MouseEvent e) {
      /**
       * handles the following mouse-click events:
       * <p>
       * <br>
       * - locates the the container of the source component and sets it's focus
       * <br>
       * - if source component is a label and the component it is labelling for
       * is a {@see JDataContainer} then toggles on/off the container visibility
       */
      Object src = e.getSource();

      if (src instanceof Component) {
        // determines the first 'named' container panel in the container
        // hierarchy
        // and sets its focus to true
        Component comp = (Component) src;

        mouseClickedOnComponent(comp);

        if (comp instanceof JLabel) {
          /**
           * for labels: <br>
           * - single click: if the associated data component is container
           * toggles it on/off else if the associated data field's type is a
           * domain type display a compact view of it 
           * - Ctrl+Click: inform the
           * data controller the event passing label and component as arguments
           */
          JLabel label = (JLabel) src;
          Component c = label.getLabelFor();
          /**
           * if Ctrl is pressed then the user is also interested in getting the
           * value of the data field associated to this label, we inform the
           * data controller responsible for the container fo the data field
           */
          if (e.isControlDown()) {
            mouseClickedWithCtrlOnLabel(e, label);
          } else 
            if (c != null) // v2.6.4.a: added this check  
          {
            // process the associated data field/container of the label
            mouseClickedOnLabel(e, label, c);
          }
        } // end label
        else if (comp instanceof JObjectTable) {
          /**
           * for tables:
           * check if user has clicked on a bounded field, 
           * if so then activate the target GUI to view details
           */
          /*v2.7.4: moved to method
          tableHandler.mouseClicked(e, (JObjectTable) comp);
          */
          mouseClickedOnDataTable(e, (JObjectTable) comp);
        }  
      } // end if
    } // end mouseClicked

    private void focusGainedOnComponent(Component gain) {
      Component fieldGained = gain.getParent();

      JDataContainer containerGained = null;

      if (fieldGained instanceof JDataField) {
        containerGained = ((JDataField) fieldGained).getParentContainer();
      }

      if (containerGained != null && !containerGained.hasFocus()) {
        activateDataContainer(containerGained);
        
        // update tool bar buttons (extra)
        updateToolBarButtons((JDataField)fieldGained);
      }
    }

    /**
     * This handler is invoked when the user clicked the mouse on a <tt>Component (comp)</tt>
     * (which can be part of a data field) of some data container. 
     * 
     * @effects 
     *  Find the lowest-level JDataContainer <tt>cont</tt> of <tt>comp</tt> and activate it
     *  <br>Update Tool bar buttons suitable for <tt>cont</tt> 
     *  <br>if comp is a data field then update tool bar buttons and application state 
     *    based on the field 
     */
    private void mouseClickedOnComponent(Component comp) {
      JDataField dataField = null;
      DAttr dc = null;
      boolean editable;
      Component parent;

      // two special cases of the source component
      if (comp instanceof JTableHeader) {
        comp = ((JTableHeader) comp).getTable();
      } else if ((comp instanceof JTextComponent)
          || (comp instanceof AbstractButton)) {
        dataField = getDataField(comp);
      }

      // if the source component is not a container, find the container
      // otherwise look up domain constraint information about the container 
      // to use later on
      JDataContainer jdc = null;
      if (comp instanceof JDataContainer) {
        jdc = (JDataContainer) comp;
      } else {
        // find the enclosing data container
        /*v2.7.2: added support for TitlePanel (not just JDataContainer)
         * - for TitlePanel, use the top-level container as the container
         **/
        parent = comp.getParent();
        Component thisComp = comp;
        while (parent != null) {
          if (parent instanceof JDataContainer && (parent.getName() != null)
              && parent.isVisible()) {
            jdc = (JDataContainer) parent;  // v2.7.2
            break;
          } 
          // v2.7.2:
          else if (parent instanceof TitlePanel) {
            // comp is part of the TitlePanel -> use root container of the GUI
            jdc = ((TitlePanel)parent).getParentGUI().getRootContainer();
            break;
          }
          thisComp = parent;
          parent = thisComp.getParent();
        } 
        
        //v2.7.2: moved to if block above: jdc = (JDataContainer) parent;
      }

      // update the container
      /**
       * update the tool bar buttons based on the user permissions
       * associated to this container
       */
      // set on-focus
      if (jdc != null // v2.7.2: add null check 
          && currentContainer != jdc) {
        /** version 2.5 */
        // System.out.printf("container changed: %s -> %s%n",
        // currentContainer, jdc);
        activateDataContainer(jdc);

        // update tool bar buttons if user did not click on data field
        if (dataField == null)
          updateToolBarButtons(null);
      }

      // update tool bar buttons (if user clicked on a data field)
      if (dataField != null)
        updateToolBarButtons(dataField);

      // special case: comp is the button of a spinner field
      // and that the field is editable -> set the GUI status to editing
      if (comp instanceof AbstractButton && dataField != null
          && dataField.isEditable()) {
        // v2.6.1: add support for delegation to data container
        jdc.handleDataFieldEditing(null, dataField);
        jdc.getController().setCurrentState(AppState.Editing);
      }      
    }

    private void mouseClickedWithCtrlOnLabel(MouseEvent e, JLabel label) {
      /**
       * if Ctrl is pressed then the user is also interested in getting the
       * value of the data field associated to this label, we inform the data
       * controller responsible for the container fo the data field
       */
      Component dcomp = (Component) label.getLabelFor();
      if (dcomp instanceof JDataField) { // only consider data fields
        JDataField df = (JDataField) dcomp;
        ControllerBasic.DataController ctl = df.getParentContainer().getController();
        ctl.onClick_DataField(df);
      }
    }

    /**
     * @effects 
     *  process mouse-click event, which was performed on the specified label,
     *  on the component c that is associated to the label.
     *  
     *  <p>This may result in the label being updated too 
     */
    private void mouseClickedOnLabel(MouseEvent e, JLabel label, Component c) {
      final int numClicks = 1;
      int clickCount = e.getClickCount();
      if (clickCount == numClicks) {
        if (c instanceof JDataContainer) {
          // toggle visibility
          // data container component
          boolean visible = c.isVisible();
          visible = !visible;
          // apply new visibility to the container
          c.setVisible(visible);
          // v2.6.c: move to AppGUI
          GUIToolkit.updateContainerLabelOnVisibilityUpdate(label, visible);
          /*v2.7.2: to use a separate size update method
          // v2.6.4.a: update GUI size to best fit the container and the desktop
          mainCtl.getActiveGUI().pack();
          */
          mainCtl.getActiveGUI().updateSizeOnComponentChange();
        } else if (c instanceof JDataField) {
          // if the data field is associated to a domain-type attribute
          // then open the viewer GUI to view the currently selected object
          // on this field
          JDataField df = (JDataField) c;
          DAttr dc = df.getDomainConstraint();
          if (dc.type().isDomainReferenceType()) {
            // data field whose type is a domain type
//            Class ownerClass = currentContainer.getController().getCreator()
//                .getDomainClass();
            /* v2.6.4.a: changed to use the data field's value
            // use the current object to find the actual type of the bound
            // attribute
            Object currObject = currentContainer.getController()
                .getCurrentObject();
            if (currObject != null) {
              Object val = schema.getAttributeValue(currObject, dc.name());
              Class domainClass = val.getClass();
              try {
                Controller domainCtl = mainCtl.lookUpViewerWithPermission(domainClass);
                domainCtl.showGUI(true);
              } catch (Exception ex) {
              }
            }
            */
            Object val = df.getValue(true);
            if (val != null) {
              displayDomainObject(val);
            }            
          }
        }
      } // end toggle
    }
    
    private void mouseClickedOnDataTable(MouseEvent e, JDataTable dataTable) {
      tableHandler.mouseClicked(e, dataTable);      
    }
    
    /**
     * @effects 
     *  displays <tt>o</tt> on the object form of the module responsible for managing o's class
     *  @version 
     *  - 3.2: improved to support customised handler
     */
    public void displayDomainObject(Object val) {
      /* v3.2: support custom command
      Class domainClass = val.getClass();
      try {
        ControllerBasic domainCtl = mainCtl.lookUpViewerWithPermission(domainClass);
        if (domainCtl != null) {
          domainCtl.showObject(val);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }*/
      DataControllerCommand cmd = mainCtl.getActiveGUI().getController().getRootDataController().lookUpCommand("HelperMouseClickOnReferencedObject");
      if (cmd != null) {
        try {
          cmd.execute(null, val);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        // default behaviour
        Class domainClass = val.getClass();
        try {
          ControllerBasic domainCtl = mainCtl.lookUpViewerWithPermission(domainClass);
          if (domainCtl != null) {
            domainCtl.showObject(val);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    public void mouseEntered(MouseEvent e) {
      /**
       * (1) shows hand-icon for labels associated to data container components
       * or data field components whose type is a domain-type <br>
       * (2) shows search-icon if Ctrl key is pressed for non-container
       * components
       */
      Object src = e.getSource();

      if (src instanceof Component) {
        // for labels
        if (src instanceof JLabel) {
          JLabel l = (JLabel) src;
          Component c = l.getLabelFor();

          if (c != null) {
            Container w = SwingUtilities.getAncestorOfClass(
                JInternalFrame.class, l);

            if (c instanceof JDataContainer) {
              // container field
              mouseEnteredOnLabelledContainer(l, w);
            } else if (c instanceof JDataField) {
              // normal field
              JDataField df = (JDataField) c;
              mouseEnteredOnLabelledDataField(df, l, w);
            }

            if (e.isControlDown()) {
              // control down
              mouseEnteredWithControlOnLabel(w,l);
            }
          }
        } 
        else if (src instanceof JObjectTable){
          // for object tables
          /* v2.7.4: moved to method
          tableHandler.mouseEntered(e,(JObjectTable) src);
          */
          mouseEnteredOnDataTable(e, (JObjectTable) src);
        }
      }
    } // end mouse-entered

    private void mouseEnteredOnLabelledContainer(JLabel l, Container w) {
      w.setCursor(HAND_CURSOR);
      GUIToolkit.highlightComponentOnFocus(l, true);
    }

    private void mouseEnteredOnLabelledDataField(JDataField df, JComponent toHighlight,
        Container activeFrame) {
      if (df.isEnabled()) {
        // only consider enabled field because disabled fields are used
        // in nested containers
        DAttr dc = df.getDomainConstraint();
        if (dc.type().isDomainReferenceType()){ //isDomainType()) {
          // domain-type field
          // check to make sure that a controller is associated to this type
          if (currentContainer != null) {
//            Class ownerClass = currentContainer.getController().getCreator()
//                .getDomainClass();
            /* v2.6.4.a: changed to use the data field's value
            // use the current object to find the actual type of the bound
            // attribute
            Object currObject = currentContainer.getController()
                .getCurrentObject();
            if (currObject != null) {
              try {
                Object val = schema.getAttributeValue(currObject, dc.name());
                Class domainClass = val.getClass();
                try {
                  Controller domainCtl = mainCtl.lookUpViewerWithPermission(domainClass);
                  activeFrame.setCursor(HAND_CURSOR);
                  GUIToolkit.highlightComponentOnFocus(toHighlight, true);
                } catch (Exception ex) {
                }
              } catch (Exception ex) {
                // ignore, perhaps mouse-over when container is
                // not active
              }
            }
            */
            Object val = df.getValue(true);
            if (val != null) {
              try {
                //Class domainClass = val.getClass();
                try {
                  // ControllerBasic domainCtl = mainCtl.lookUpViewerWithPermission(domainClass);
                  activeFrame.setCursor(HAND_CURSOR);
                  GUIToolkit.highlightComponentOnFocus(toHighlight, true);
                } catch (Exception ex) {
                  // ignore
                }
              } catch (Exception ex) {
                // ignore, perhaps mouse-over when container is
                // not active
              }
            }
          }
        }
      }
    }

    private void mouseEnteredWithControlOnLabel(Container activeFrame, JLabel label) {
      // if search on then enable the search cursor and highlight the label
      boolean searchOn = mainCtl.getActiveGUI().isVisibleContainer(ControllerBasic.getSearchToolBar());
      
      if (searchOn) {
        activeFrame.setCursor(SEARCH_ADD_CURSOR);
        GUIToolkit.highlightComponentOnFocus(label, true);
      }
    }
    
    private void mouseEnteredOnDataTable(MouseEvent e, JDataTable table) {
      tableHandler.mouseEntered(e, table);      
    }

    public void mouseExited(MouseEvent e) {
      /** shows default hand-icon for frame */
      Object src = e.getSource();

      if (src instanceof JLabel) {
        mouseExitedLabel((JLabel)src);
      } 
      else if (src instanceof JObjectTable) {
        tableHandler.mouseExited(e, (JObjectTable) src);
      }
    }

    private void mouseExitedLabel(JLabel label) {
      Component c = label.getLabelFor();

      if (c != null) {
        // reset the mouse cursor and the label highlight
        Container w = SwingUtilities.getAncestorOfClass(JInternalFrame.class,
            label);

        if (w.getCursor() != DEFAULT_CURSOR) {
          w.setCursor(DEFAULT_CURSOR);
        }

        // highlight by drawing a border
        if (label.getBorder() != GUIToolkit.LABEL_EMPTY_BORDER)
          GUIToolkit.highlightComponentOnFocus(label, false);
      }  
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
      // 
      Component src = (Component) e.getSource();
      
      if (src instanceof JObjectTable) {
        tableHandler.mouseMoved(e, (JObjectTable) src);
      }
    }
    
    public void keyPressed(KeyEvent e) {
      // tasks to be performed when key pressed on a GUI component 
      
      // if key pressed is non-control then consider this an editing event
      if(isEditorKey(e)) {
        onFieldEditing(e);
      }
    }

    /**
     * @effects 
     *  if the key associated to key event <tt>e</tt> is a legal editing key
     *    return true
     *  else
     *    return false
     */
    private boolean isEditorKey(KeyEvent e) {
      // not a control key
      boolean editKey = !(e.isControlDown() ||
          e.isShiftDown() ||
          e.isMetaDown() ||
          e.isAltDown() || 
          e.isAltGraphDown());
      
      if (editKey) {
        // not a navigation key
        int code = e.getKeyCode();
        for (int nk : NON_EDITING_KEYS) {
          if (code == nk) {
            editKey = false;
            break;
          }
        }
      }
      
      return editKey;
    }
    
    /**
     * This method is invoked when a key is pressed on a part of a data field
     */
    void onFieldEditing(KeyEvent e) {
      // only handle field editing if search tool bar is not on
//      AppGUI gui = mainCtl.getActiveGUI();
//      boolean searchOn = false;
//      if (gui != null) {
//        searchOn = gui.isVisible(SearchToolBar);
//      }
      
//      if (!searchOn) {
        Component source = (Component) e.getSource();
        
        // find the data field that sources the event and
        // update the application state only if this field is editable
        JDataField dataField = getDataField(source);
        if (dataField != null && dataField.isEditable()) {
          /**
           * Assumption: the key event follows a mouse-click event on a data
           * component of the current container
           */
          if (currentContainer != null) {
            // v2.6.1: delegate handling to container 
            currentContainer.handleDataFieldEditing(e, dataField);
            
            currentContainer.getController().setCurrentState(AppState.Editing);
          }
        }
//      }
    }
    
    /**
     * @requires <tt>comp</tt> is a <tt>Component</tt> of a 
     *            data field
     * @effects return the <tt>JDataField</tt> object that contains
     *          <tt>comp</tt>
     *          (recursively search up the containment hierarchy of of <tt>comp</tt> for  
     *          a 'proper' data field, i.e a field with a <b>non-null</b> domain constraint).
     * @version 2.7.4:
     *   consider the case of composite data field (e.g. date field) which contains multiple components
     *   that are themselves data fields. But these fields are not proper fields and therefore will 
     *   be ignored in the search.
     */
    private JDataField getDataField(Component comp) {
      JDataField dataField;
      
      if (comp instanceof JDataField) {
        dataField = (JDataField) comp;
        
        if (dataField.isConstrained()) {
          // proper field
          return dataField;
        }
      }
      
      // recursive search...
      Component parent = comp.getParent();
      
      return getDataField(parent);
      
      /*v2.7.4: fixed using a loop 
      if (comp instanceof JDataField)
        return (JDataField) comp;

      Component field = comp.getParent();
      JDataField dataField = null;
      
      // TODO: generalise this (use a loop)
      if (field instanceof JDataField) // text field
        dataField = (JDataField) field;
      else { // could be part of a spinner field
        field = field.getParent();
        if (field instanceof JDataField) {  // value button part
          dataField = (JDataField) field;
        } else {  // text field part
          field = field.getParent();
          if (field instanceof JDataField)
            dataField = (JDataField) field;
        }
      }
      return dataField;
      */
    }

    /**
     * @effect s Handle the event where the specified gui is deactivated. This
     *         involves loosing the focus on the root panel of gui and setting
     *         the current container of this to null if it is the same as that
     *         of the root panel.
     */
    public void deactivateGUI(View gui) {
      JDataContainer container = gui.getRootContainer();

      // loose focus
      if (container != null) {
        container.setHasFocus(false);
        // update exclusion (if any)
        // gui.updateExclusion(true);

        container.getController().setCurrentState(AppState.Hidden);

        // if this is the same as the current container
        // set it to null
        /**v 2.5.4: comment this out so that activateGUI()
         * can work correclty. 
         * */
        //if (currentContainer == container) {
        //  currentContainer = null;
        //}
      }
    }

    /**
     * This method is invoked by window's handlers to change the focus and
     * update the status of the GUI commands when the user shows and hides the
     * functional GUIs.
     * 
     * @effects if <code>currentContainer</code> is not contained in
     *          <code>gui</code> then sets <code>currentContainer</code> to
     *          <code>gui.rootPanel</code>, set current state of
     *          <code>currentContainer</code> to <code>OnFocus</code>
     * @see WindowHelper
     */
    void activateGUI(View gui) {
      Component iframe = gui.getGUI();

      boolean descend = false;

      // record the GUIs whose exclusion need to be updated
      // so that we can update them together
      View offFocusGUI = null;
      View onFocusGUI = null;

      if (currentContainer != null) {
        descend = SwingUtilities.isDescendingFrom(
            currentContainer.getGUIComponent(), iframe);

        // remove focus if gui is a different window
        /**v2.5.4: replace this with the code below to work better with deactivateGUI*/
//        if (!descend && currentContainer.hasFocus()) {
//          currentContainer.setHasFocus(false);
//        }
        if (!descend) {
          offFocusGUI = currentContainer.getController().getCreator().getGUI();
          if (currentContainer.hasFocus()) {
            currentContainer.setHasFocus(false);
          }
        }
      }

      // switch container if gui is a different or new window
      boolean newWindow = false;
      if (currentContainer == null || !descend) {
        currentContainer = gui.getRootContainer();
        newWindow = true;
      }

      // at this stage the current container is either a 
      // new container (because the user selects a different GUI)
      // or stays the same as the same GUI has come back from hidden. 
      // In any case, we need to set the focus of this container to on 
      if (currentContainer != null) {
        if (newWindow ||         // new window
            currentContainer.hasFocus()==false // come back from hidden
            ) {
          currentContainer.setHasFocus(true);

          // update excluded components (if any)
          // gui.updateExclusion(false);
          onFocusGUI = gui;
        }

        // currentContainer.getController().setCurrentState(AppState.OnFocus);
      }

      // update exclusion
      if (onFocusGUI != null) {
        onFocusGUI.updateExclusion(offFocusGUI);
        
        // update tool bar buttons (extra)
        updateToolBarButtons(null);
      }

      if (currentContainer != null) {
        currentContainer.getController().setCurrentState(AppState.OnFocus);
      }
    }

    /**
     * @effects loose focus on the current container and set it on newContainer.
     *          Also update the data controller state to reflect this.
     */
    public void activateDataContainer(JDataContainer newContainer) {
      View offFocusGUI = null;
      View onFocusGUI = null;
      
      if (currentContainer != null) {
        currentContainer.setHasFocus(false);
        offFocusGUI = currentContainer.getController().getCreator().getGUI();
      }

      currentContainer = newContainer;
      currentContainer.setHasFocus(true);

      onFocusGUI = newContainer.getController().getCreator().getGUI();

      // update visibility of main components
      // TODO: should we use the current GUI if onFocusGUI = null
      // onFocusGUI is null if the new container is a nested panel created 
      // by a module that has no GUI
      if (onFocusGUI != null) {
        onFocusGUI.updateExclusion(offFocusGUI);
      } else if (offFocusGUI != null)
        offFocusGUI.updateExclusion(true);
      
      ControllerBasic.DataController dctl = currentContainer.getController();
      dctl.setCurrentState(AppState.OnFocus);
    }

    /**
     * @effects 
     *  update the visibility of the tool bar buttons 
     *  based on the user permission on and the editability of the current container
     * @sameas {@link #updateToolBarButtons(null)}
     * 
     */
    public void updateToolBarButtons() {
      updateToolBarButtons(null);
    }
    
    /**
     * @effects
     *  update the visibility of the tool bar buttons 
     *  based on the user permission on and the editability of the current container and if 
     *  the selected data field <tt>selectedField</tt> of this container is specified 
     *  then also the user permission associated to this field. 
     */
    private void updateToolBarButtons(JDataField selectedField) {
      // look up the domain constraint associated to this container 
      //TODO: assume that default panel is the parent
      DefaultPanel panel = (DefaultPanel) currentContainer.getParentContainer();
      DAttr containerField = null;
      if (panel != null)
        containerField = panel.getComponentConstraint(currentContainer.getGUIComponent());
      // look up the gui region of the container
      View activeGUI = mainCtl.getActiveGUI();
      
      boolean editable = activeGUI.isEditable(currentContainer);      
      
      // if tool bar is used then update it
      if (mainCtl.getGUI().hasToolBar()) {
        mainCtl.updateToolBarPermissions(currentContainer.getController().getCreator(),
            containerField, editable, selectedField);  
      }
    }
    
    /**
     * @effects performs Tools menu actions
     */
    @Override // / ActionListener
    public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      Object src = e.getSource();

      // perform the specified action first
      try {
        if (cmd.equals(Exit.name())) {
          /*v2.7: change to forceShutDown (ignoring errors)
          try {
            mainCtl.shutDown();
          } catch (Exception ex) {
            boolean retry = 
                mainCtl.displayError(MessageCode.ERROR_HANDLE_COMMAND_SHUTDOWN, 
                    "Lỗi xử lí lệnh đóng chương trình. Bạn có muốn thử lại không?", ex, true, cmd);
            if (!retry) {
              // end anyway
              mainCtl.forceShutDown();
            }
          }
          */
          mainCtl.forceShutDown();
        } else if (cmd.equals(RegionName.SearchToolBar.name())) {
          //updateSearchToolBar(true);
          // toggle search tool bar on/off
          toggleSearchToolBar();
        } 
        else if (src instanceof Component) {
          // a Module's menu item -> run the associated module
          Region guiCfg = mainCtl.getGUI().getComponentRegion((Component) src);
          mainCtl.runModule(guiCfg);
        }
        // add other actions
      } catch (Exception ex) {
          mainCtl.displayErrorFromCode(MessageCode.ERROR_HANDLE_COMMAND, ex, cmd);
      }
    }

    /**
     * This method is invoked by a composite-typed data field (e.g. combo or date field) when its value has been changed by the user.
     * This is to overcome the limitation of {@link #mouseClickedOnComponent(Component)}, which only 
     * handle such event for non-composite-typed data field (e.g. text box)
     * 
     * @effects 
     *  update application's GUI state when a data field value has been changed by the user
     *  (similar to how {@link #mouseClickedOnComponent(Component)} handles data field event)
     * @version 2.7.4
     */
    @Override // ValueChangeListener (v2.7.4)
    public void fieldValueChanged(ChangeEvent e) {
      JDataField df = (JDataField) e.getSource();

      JDataContainer dataContainer = df.getParentContainer();

      /**v3.1: add activation support for data container of special data fields
       * some type of data fields (e.g. JComboField) does not detect mouse event effectively 
       * and as such that event may not be handled by mouseClickedOnComponent.
       * 
       * <p>v3.2: added handling for {@link JListField}, which does handle mouse-event but this event 
       * is fired concurrently with value changed, and this concurrency causes problems in state transition 
       * of the owner's data container. Specifically, the mouse click event, which is handled by mouseClickedOnComponent
       * is somehow performed after the handling of the value-change event and so the owner data container's state (Editing) 
       * is overriden by OnFocus, causing the Update tool bar button to be disabled.
       * 
       * By adding the case for JListField here, we are making value-changed event handling take precedence over
       * mouse event handling (which still occurs but will not update the owner's data container) 
       * */
      //TODO: remove this check if JComboField is improved to better support mouse events
      if (/*df instanceof JComboField || df instanceof JDateFieldSimple 
          || df instanceof JListField // v3.2*/
          df.isMouseClickConsumableByValueChanged()
          ) {
        if (currentContainer != dataContainer) {
          activateDataContainer(dataContainer);
        }
      }

      // update tool bar buttons
      updateToolBarButtons(df);

      /*v3.1: use a different method which deals more specifically with the value-changed event
       * of the data field 
      // inform data handler of the field that data field's value has changed 
      dataContainer.handleDataFieldEditing(null, df);
       */
      
      // debug
      //System.out.println(this.getClass().getSimpleName()+".Field value changed: " + df);
      
      dataContainer.handleDataFieldValueChanged(df);
    }

    /**
     * This is invoked when user clicks the menu item of the Search tool bar (or using a suitable
     * short-cut key combination)
     * 
     * @effects <pre>
     *  if there is no active GUI
     *    do nothing
     *  else
     *    if user has not selected a data container or the selected container is not suitable for searching
     *      display message
     *    else
     *      toggle the search tool bar visibility 
     *      if visibility = false
     *        clear the search resources
     *        </pre> 
     */
    void toggleSearchToolBar() {
      // turn off -> close search
      View gui = mainCtl.getActiveGUI();
      if (gui != null) {
        // v2.6.4.a
        JDataContainer activeCont = gui.getActiveDataContainer();
        
        if (activeCont == null || !activeCont.isSearchEnabled()) {
          gui.getController().displayMessageFromCode(MessageCode.NO_ACTIVE_CONTAINERS, null);
        } else {
          // toggles the visibility of the search tool bar for cont  
          boolean visibility = !gui.isVisibleContainer(ControllerBasic.getSearchToolBar());
          
          if (visibility) {
            // turn on
            //updateSearchToolBar(gui, activeCont, true, true);
            // toggle search tool bar on/off
            gui.setVisibleContainer(ControllerBasic.getSearchToolBar(), true);
            
            //  make sure that cont is editable 
            activeCont.forceEditable();
            
            // fire SearchToolBarUpdated event on cont
            ControllerBasic.DataController activeDctl = activeCont.getController();
            activeDctl.setCurrentState(AppState.SearchToolBarUpdated);
          } else {
            // turn off
            ControllerBasic.DataController activeDctl = activeCont.getController();
            activeDctl.closeSearch();
//            if (closed) {
//              //updateSearchToolBar(gui, activeCont, false, true);
//              // reset cont to its previous state
//              activeCont.updateDataPermissions();
//
//              // fire SearchToolBarUpdated event on cont
//              Controller.DataController activeDctl = activeCont.getController();
//              activeDctl.setCurrentState(AppState.SearchToolBarUpdated);
//            }
          }
        }
      }        
    }
    
    /**
     * @effects 
     *  <pre>
     *  if activeGui AND activeCont = null
     *    determine activeGui and activeCont
     *  
     *  toggles the visibility of the search tool bar for activeCont
     *  
     *  if updateState = true
     *    fire SearchToolBarUpdated event on cont
     *    </pre>
     *  @deprecated v2.7.2
     */
    void updateSearchToolBar(View activeGui, JDataContainer activeCont, 
        boolean visibility, boolean updateState) {
      // 
      if (activeGui == null)
        activeGui = mainCtl.getActiveGUI();
      
      if (activeGui != null) {
        // v2.6.4.a
        if (activeCont == null)
          activeCont = activeGui.getActiveDataContainer();
        
        // toggles the visibility of the search tool bar for cont  
        activeGui.setVisibleContainer(ControllerBasic.getSearchToolBar(), visibility);
        
        if (visibility) {
          // turn on search tool bar
          //  make sure that cont is editable 
          activeCont.forceEditable();
        } else {
          // reset cont to its previous state
          activeCont.updateDataPermissions();
        }

        if (updateState) {
          // fire SearchToolBarUpdated event on cont
          ControllerBasic.DataController activeDctl = activeCont.getController();
          activeDctl.setCurrentState(AppState.SearchToolBarUpdated);
        }
      }
    }

    // v2.7.2: obsolete
    /**
     * @effects prepare the root panel of the active GUI 
     *  to use for entering search terms
     * @deprecated v2.7.2
     */
    void newSearch() {
      /*v2.6.4b: improved to support nested container (i.e. force the editability of the actual panel on which the search was performed)
      AppGUI gui = mainCtl.getActiveGUI();
      if (gui != null)
        gui.getRootPanel().forceEditable();
      */
      if (currentContainer != null) {
        currentContainer.forceEditable();
      }
    }

    //v2.7.2: obsolete
    /**
     * @effects reset the root panel of the active GUI to the editable state 
     *  before search activated
     *  
     * @deprecated v2.7.2
     */
    void endSearch() {
      /*v2.6.4b: improved to support nested container (i.e. reset the editability of the actual panel on which the search was performed)
      AppGUI gui = mainCtl.getActiveGUI();
      if (gui != null) {
        gui.getRootPanel().updateDataPermissions();
      }
      */
      if (currentContainer != null) {
        currentContainer.updateDataPermissions();
      }
    }
    
    // // StateChangeListener
    @Deprecated
    @Override
    public void stateChanged(Object src, AppState state, String messages,
        Object... data) {
      // handle state change
//      if (state == AppState.Searched) {
//        endSearch();
//      } else if (state == AppState.SearchCleared) {
//        newSearch();
//      } else if (state == AppState.SearchClosed) {
//        updateSearchToolBar(null, null, false, false);
//      }
    }

    @Deprecated
    @Override
    public AppState[] getStates() {
      return new AppState[] { AppState.Searched, AppState.SearchCleared,
          AppState.SearchClosed };
    }
    
    /**
     * @overview
     *    A helper class that handles table-specific events. 
     * @author dmle
     */
    private static class TableEventHelper {
      static InputHelper inputHelper;

      JDataTable currentTable;
      int currentRow;
      int currentCol;
      boolean currentCellOfInterest;
      
      public TableEventHelper(InputHelper inputHelper) {
        this.inputHelper = inputHelper;
        currentTable = null;
        currentCellOfInterest = false;
        currentRow = -1;
        currentCol = -1;
      }
      
      /**
       * @effects 
       *  handle the event when the mouse has entered the specified table
       */
      void mouseEntered(MouseEvent e, JDataTable table) {
        // read the table cell over which the mouse is being moved
        // if this cell refers to a bounded object (similar to a bounded JSpinnerField)
        //   highlight it so that user can click on it to view details
        if (currentTable != table) {
          currentTable = table;
        }
        
        Point p = e.getPoint();
        int row=table.rowAtPoint(p);
        int col=table.columnAtPoint(p);
        
        if (row > -1 && col > -1) {
          if (row != currentRow || col != currentCol) {
            currentRow = row;
            currentCol = col;
            highlightCell(e);
          }
        }
      }
      
      /**
       * @effects 
       *  handle the event when the mouse is moved over the specified table
       */
      void mouseMoved(MouseEvent e, JDataTable table) {
        // update the hand cursor (if needed) 
        // as the user moves the mouse from cell to cell
        if (currentTable != table) {
          currentTable = table;
        }
        
        Point p = e.getPoint();
        int row=table.rowAtPoint(p);
        int col=table.columnAtPoint(p);

        if (row > -1 && col > -1) {
          if (row != currentRow || col != currentCol) {
            // different cell
            //System.out.println("moved: " + row+","+ col);
  
            currentRow = row;
            currentCol = col;
            
            highlightCell(e);
          }
        }
      }

      private void highlightCell(MouseEvent e) {
        /**
         * if cell contains interesting value (value that is bounded to a domain object)
         * and mouse event's control is down then
         *  display the hand cursor to enable look up 
         */
        boolean ctrlOn = e.isControlDown(); 

        Cursor cursor = inputHelper.DEFAULT_CURSOR;
        currentCellOfInterest = false;

        // active GUI is the GUI that contains (i.e. uses) the table
        View activeGUI = currentTable.getController().getUser().getGUI();
        Object val = currentTable.getRawValueAt(currentRow, currentCol);
        
        if (ctrlOn && val != null) {
          // see if the cell is interesting...
          Class domainClass = val.getClass();
          
          // ignore link column
          if (!currentTable.isLinkColumn(currentCol)) {
            try {
              ControllerBasic domainCtl = inputHelper.mainCtl.lookUpViewerWithPermission(domainClass);
              if (domainCtl != null) {
                cursor = inputHelper.HAND_CURSOR;
                currentCellOfInterest = true;
                
                // System.out.printf("cell(%d,%d) of interest%n",currentRow,currentCol);

                // highlight the cell renderer of the cell at currentRow, currentCol
                currentTable.setCellSelected(currentRow, currentCol);
                JComponent toHighlight = (JComponent) currentTable.getCellRenderer(currentRow, currentCol);
                GUIToolkit.highlightComponentOnFocus(toHighlight, true);
                currentTable.repaint(currentRow, currentCol);
              }
            } catch (Exception ex) {
            }        
          }          
        }
        
        // now set the cursor
        JInternalFrame activeFrame = (JInternalFrame) activeGUI.getGUI();
        if (activeFrame.getCursor() != cursor)
          activeFrame.setCursor(cursor);
      }
      
      /**
       * @effects 
       *  handle event when the mouse is clicked on a table
       */
      void mouseClicked(MouseEvent e, JDataTable table) {
        // v2.6.1: update column on focus
        Point p = e.getPoint();
        int row=table.rowAtPoint(p);
        int col=table.columnAtPoint(p);

        if (row > -1 && col > -1) {
          table.setColumnOnFocus(col);
        }
        
        // mouse clicked followed by a mouse entered/mouse moved 
        // we only handle this event if the cell value at the mouse clicked is 
        // of interest
        if (currentCellOfInterest) {
          Object val = currentTable.getRawValueAt(currentRow, currentCol);
          // v2.6.4.a
          inputHelper.displayDomainObject(val);
        } 
      }
      
      /**
       * @effect handle the event when mouse exited the specified table
       */
      void mouseExited(MouseEvent e, JDataTable table) {
        // reset the cursor if necessary
        Container w = (Container) inputHelper.mainCtl.getActiveGUI().getGUI();
        
        if (w.getCursor() != inputHelper.DEFAULT_CURSOR) {
          w.setCursor(inputHelper.DEFAULT_CURSOR);
        }
        
        // reset variables
        currentTable = null;
        currentRow = -1;
        currentCol = -1;
        currentCellOfInterest = false;
      }
    } // end TableEventHelper
    
  } // end InputHelper