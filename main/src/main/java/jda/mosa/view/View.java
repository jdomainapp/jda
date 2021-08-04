package jda.mosa.view;

import static jda.mosa.controller.ControllerBasic.Actions;
import static jda.mosa.controller.ControllerBasic.Components;
import static jda.mosa.controller.ControllerBasic.Desktop;
import static jda.mosa.controller.ControllerBasic.LoginActions;
import static jda.mosa.controller.ControllerBasic.MenuBar;
import static jda.mosa.controller.ControllerBasic.SearchToolBar;
import static jda.mosa.controller.ControllerBasic.SidePane;
import static jda.mosa.controller.ControllerBasic.StatusBar;
import static jda.mosa.controller.ControllerBasic.ToolBar;
import static jda.mosa.controller.ControllerBasic.Tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.beans.PropertyVetoException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

import com.tomtessier.scrollabledesktop.BaseInternalFrame;

import jda.modules.common.CommonConstants;
import jda.modules.common.collection.Map;
import jda.modules.common.exceptions.ApplicationException;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.filter.Filter;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.common.types.tree.Tree;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DomainValueDesc;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.ds.viewable.JDataSourceFactory;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.mccl.conceptmodel.view.RegionLinking;
import jda.modules.mccl.conceptmodel.view.RegionMap;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionToolMenuItem;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.modules.setup.init.RegionConstants;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import jda.mosa.controller.assets.eventhandler.WindowHelper;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.view.assets.DataContainerToolkit;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.builder.LabelledContInfo;
import jda.mosa.view.assets.builder.ViewBuilder;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.desktop.DesktopListener;
import jda.mosa.view.assets.desktop.JScrollableDesktopPane;
import jda.mosa.view.assets.panels.DefaultPanel;
import jda.mosa.view.assets.panels.TitlePanel;
import jda.mosa.view.assets.swing.JHtmlLabel;
import jda.mosa.view.assets.tables.JDataTable;
import jda.mosa.view.assets.tables.JObjectTable;
import jda.mosa.view.assets.toolbar.StatusBar;
import jda.mosa.view.assets.util.ViewComponentMapHandler;
import jda.mosa.view.assets.util.function.value.DataFieldValueFunction;
import jda.util.SwTk;
import jda.util.SysConstants;
import jda.util.events.InputHandler;
import jda.util.events.StateChangeListener;
import jda.util.properties.PropertySet;

/**
 * Represents a generic application GUI of a domain class <code>C</code>.
 * 
 * @author dmle
 * 
 * @param <C>
 *          a domain class (e.g. Student)
 */
public class View<C> {

  /**
   * @overview
   *  Represents an {@link Action} of an {@link AbstractButton} that can forward {@link ActionEvent}s
   *  to a collection of {@link ActionListener}s.  
   *  
   * @author dmle
   * @version 3.1
   */
  private static class ButtonAction extends AbstractAction {

    private Collection<ActionListener> actionListeners;

    public ButtonAction(AbstractButton button, ActionListener al) {
      super(button.getText(), button.getIcon());
      
      String cmd = button.getActionCommand();
      String toolTip = button.getToolTipText();
      
      // IMPORTANT: must do these to retain other settings
      putValue(Action.SHORT_DESCRIPTION, toolTip);
      putValue(Action.ACTION_COMMAND_KEY, cmd);
      
      actionListeners = new ArrayList();
      actionListeners.add(al);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      // forward event to each registered action listener
      for (ActionListener al : actionListeners)
        al.actionPerformed(e);
    }

    /**
     * @effects 
     *  register <tt>al</tt> to receive {@link ActionEvent}s handled by {@link #actionPerformed(ActionEvent)} 
     */
    public void addActionListener(ActionListener al) {
      actionListeners.add(al);
    }
  };  // end ButtonAction

  /** the controller object responsible for creating and handling this GUI */
  private ControllerBasic<C> controller;

  /** the associated {@see Region} object that defines configuration settings of <code>this</code>.<br>
   * The configurations of all the child components that are displayed on <code>this</code> are 
   * retrieved using this configuration object.
   * */
  private RegionGui guiConfig;
  
  /**
   * the parent <code>AppGUI</code> object to which this belongs. <br>
   * Applicable only to functional GUIs to refer to the main GUI.
   */
  private View parentGUI;

  /**
   * the actual Swing GUI container object that is used by this to display the
   * GUI components. It is a {@see JFrame} for <code>main</code> GUI or a {@see
   * JInternalFrame} for functional GUIs
   */
  private Container w;

  /**
   * The root container of this
   */
  //v2.7.2
  private JDataContainer rootContainer;

  /**
   * To map {@link Region} representing a gui region to the GUI component representing that region. <br>
   * This is a more coarse-grain map than {@link #compMap} in that it maps the
   * container components of the regions not the individual components in each
   * region.
   * 
   * A standard {@link View} has the following GUI regions:
   * 
   * <i><b>The menu bar</b></i> <br>
   * Applicable only to main GUI.
   * 
   * <i><b>The tool bar (below the menu bar)</b></i> <br>
   * Applicable to both the main and functional GUIs. For the main GUI, it
   * contains the common command buttons defined in {@see AppGUI.Action}, such
   * as <code>Create, Update, Next, Previous</code>. <br>
   * For functional GUIs, it is the search tool bar which contains a small
   * search query text field and a command button. This enables a user to
   * quickly look up an object.
   * 
   * <i><b>The components panel</b></i> <br>
   * Applicable only to functional GUIs. The components panel of a data entry
   * and report GUI typically contains a panel ({@see DefaultPanel}) of labels
   * and input data fields ({@see JDataField}).
   * <p>
   * Note that a report GUI also contains another small panel for displaying the
   * report output. This panel can be a {@see DefaultPanel} or a {@see
   * TabularPanel}, depending on the type of {@see Report} for which it is
   * created.
   * 
   * <i><b>The actions panel</b></i> <br>
   * Applicable only to function GUIs, this panel contains a small set of common
   * command buttons, such as <code>Create, Cancel</code> for data entry GUIs
   * and <code>Execute,Cancel</code> for report GUIs. These commands are defined
   * in {@see AppGUI.Action}.
   * 
   * <i><b>The status bar panel</b></i> <br>
   * Applicable to both main and functional GUIs. This panel is not yet
   * implemented.
   */
  protected Map<Region, Component> containerMap;

  /**
   * an array of {@see Container} components in {@link #containerMap} used to speed
   * up the search for command and menu buttons
   */
  private Container[] guiContainers;

  /**
   * maps {@see Region}s of display components to their Swing objects. <br>
   * This map is populated when this GUI is created and with entries about all
   * the Swing components that have one of the following properties: title,
   * label, text. These include the container {@link w} itself, all the menus
   * and menu items of {@link #menuBar}, the command buttons in the
   * {@link #toolBar} and the {@link #actions} panel, together with labels and
   * system texts associated to the Swing components contained in the
   * {@link #components} panel.
   * 
   * <br>
   * This map is used by the {@link #changeGUILanguage()} method to quickly
   * retrieve the display components and to change their
   * title(s)/label(s)/text(s) when the system language has been changed.
   */
  protected Map<Region, Component> compMap;

  /**
   * a {@see Runnable} object that is run in a Swing thread to perform the
   * switching of the application language. <br>
   * It is executed by the {@link #changeGUILanguage()} method.
   * */
  private Runnable runUpdateGUILanguage;

  /**
   * this is true if this gui is currently in the compact view mode (
   * by invoking {@link #compact(boolean)}, otherwise this is false. 
   */
  private boolean compact;

  /**
   * Applicable only to functional GUI, this attribute is true if the JInternalFrame 
   * of this gui was set to be pack but not yet packed; otherwise this is false.
   *
   * This attribute is set to true by {@link #updateSize()} when this GUI was first created
   * and the configuration setting is to pack the GUI display. It is to delay the actual 
   * packing of the JInternalFrame until show time (in order to overcome a problem
   * with the JInternalFrame not fully packed before it is made visible). 
   **/
  private boolean toPack = false;
  
  /**v2.7.4: whether or not the location of this.w has been set from the gui config */
  private boolean isLocated = false;
  
//  /** v2.7.4: added to record the packed size of this.w when it is not configured with
//   * initial width, height. This is used to avoid computing the packed size everytime the 
//   * frame is packed and to enable other calculations that rely on the size (e.g. location)
//   * before this.w is made visible
//   */
//  private Dimension packedSize;
  
  /**
   * This attribute is used to finalise the initialisations of the GUI components immediately 
   *  before they are shown for the first time.
   *  
   * <p>It is initialised to <tt>true</tt> to indicate that this GUI has not been shown before (
   * and thus any last-minute initialisations of the components can be performed). 
   * Once it has become visible (by way of invoking method {@link #setVisible(boolean)}), 
   * then the value of this attribute becomes <tt>false</tt> thereafter.
   */
  private boolean notActivatedBefore;

  /**
   * Attribute of <b>main</b> {@link View}. 
   * 
   * <p>Maps {@link JButton} components to {@link String} values that are the keyboard key definition  
   * as specified by {@link KeyStroke#getKeyStroke(String)}.
   * 
   * <p>This is populated with entries by the main {@link View} when it creates the tool bar
   * and by the functional {@link View} when it creates the actions panel.
   * 
   * <p>Once populated, this map is used by each functional {@link View} to 
   * {@link #registerDataController(DataController)} for the {@link DataController}s of its forms 
   * to the {@link JButton} components with the 
   * option of creating suitable shot-cut keys for these components if such keys are defined in the map. 
   *   
   * @version 3.1
   */
  private java.util.Map<JComponent, String> buttonShotCutKeyMap;

  /**
   * Attribute of <b>main</b> {@link View}. 
   * 
   *  <p>Maps {@link AbstractButton} components (e.g. {@link JButton}) to {@link ButtonAction}s 
   *  that are used to handle the {@link ActionEvent}s fired by the components. 
   *  The components that form the key set of this map are exactly those that form the key set of 
   *  {@link #buttonShotCutKeyMap} (i.e. those that requite global shot cuts to be set)
   *  
   *  <p>Other {@link AbstractButton}s register action listeners using the standard <tt>add*Listener</tt> method. 
   *  
   * @version 3.1
   */
  private java.util.Map<AbstractButton, ButtonAction> buttonActionMap;

  /**
   * a helper object used to filter data component configurations that are not visible.
   * It is used by {@link #createComponents(Region)} to exclude the data components that 
   * are not visible.
   * 
   * @version 2.7.2
   */
  private static Filter<Region> dfConfigFilterByVisible = new Filter<Region>() {
    @Override
    public boolean check(Region region, Object...args) {
      /* v3.0: 
      if (o instanceof RegionDataField) {
        RegionDataField dfCfg = (RegionDataField) o;
        return dfCfg.getVisible();
      } 
      return true;
      */
      //return region.getVisible();
      // use visibility property
      return region.isDisplayVisible(); //getProperty(PropertyName.view_objectForm_dataField_visible, Boolean.class, true);
    }
  };
  
  /**
   * a helper object used to filter data component configurations based on the state scope of the application module
   * It is used by {@link #createComponents(Region)} 
   * 
   * @version 3.0
   */
  private static ConfigFilterByStateScope<Region> dfConfigFilterByStateScope = new ConfigFilterByStateScope<Region>();
  
  private static class ConfigFilterByStateScope<V> implements Filter<V> {
    private String stateScope;
    private String[] scopeElements;
    
    /**
     * @requires 
     *  stateScope != null
     */
    public void setStateScope(String stateScope) {
      if (stateScope != null) {
        this.stateScope = stateScope;
        scopeElements = stateScope.split(",");
      }
    }
    
    @Override
    public boolean check(V o, Object... args) {
      if (scopeElements == null)
        return true;
      
      Region region = (Region) o;
      
      // region name must be a state scope elements
      String regionName = region.getName();
      for (String scopeE : scopeElements) {
        if (scopeE.equals(regionName))
          return true;
      }
      
      // no in scope elements
      return false;
    }
  }
  
  /**
   * a static initialiser that is used to set up the base settings for all the
   * <code>AppGUI</code> objects.
   */
  static {
    initCommon();
  }

  // /// Constants
  
  /**
   * whether or not to write debug messages on the standard console. <br>
   * By default, it is set to <code>false</code>. This can be changed by using
   * the the JVM argument <code>debug</code> when running the application (e.g.
   * 
   * <pre>
   * java -Ddebug=true MyApplication
   * </pre>
   * 
   * )
   */
  protected static final boolean debug = jda.modules.common.Toolkit.getDebug(View.class);

  /**
   * @effects 
   *          If <code>parentGUI = null</code> then 
   *            initialise <code>this</code> to be the main GUI
   *          else 
   *            initialise <tt>this</tt> to be a functional GUI whose parent is <code>parentGUI</code>.
   */
  public View(ControllerBasic<C> ctl, Region guiConfig, View parentGUI)
      throws NotPossibleException {
    //
    this.guiConfig = (RegionGui) guiConfig;
    this.controller = ctl;

    this.parentGUI = parentGUI;

    this.notActivatedBefore = true;
    
    init();

//    // create the root panel first (if any)
//    if (parentGUI != null) {
//      // the root data panel
//      /*v2.7.2: 
//      DefaultPanel rootComponentsPanel = createRootPanel();
//      if (rootComponentsPanel != null)
//        containerMap.put(Components, rootComponentsPanel);
//        */
//      JDataContainer rootContainer = initRootContainer();
//      if (rootContainer != null)
//        containerMap.put(Components, rootContainer.getGUIComponent());
//    }
  }

  /**
   * @requires 
   *  <tt>
   *  containmentTree != null => containmentTree is the containment-tree of {@link #controller}.module
   *  </tt>
   *  
   * @effects returns a new <code>JDataContainer</code> object used as the root
   *          panel for the data components
   */
  protected JDataContainer createRootContainer(
      Region rootContainerRegion, 
      List<Region> childRegions, 
      final Tree containmentTree  // v3.0
      ) throws NotFoundException {
    ControllerBasic.DataController dctl = controller.getRootDataController();
    Style style = controller.getStyleSettings(guiConfig);
    
    Class displayClass = guiConfig.getRootContainerType();
    
    // create root as a container based on the display class
    JDataContainer root = createContainerComponent(
        Components.getName(), null, 
        displayClass, 
        rootContainerRegion, 
        dctl, style 
        , childRegions, null,
        containmentTree
        );

    // puts the pair into a map for look up
    controller.putDataController(root, dctl);
    return root;
  }

  
  /**
   * This is the initial static initialiser, which is run once for all
   * <code>AppGUI</code> objects.
   * 
   * @effects sets default look-and-feel and performs other initialisation tasks
   */
  private static void initCommon() {
    /*v2.7.4: moved to GUIToolKit 
    // L&F names: "Metal", "System", "Motif", "Nimbus", and "GTK"
    String[] preferLAFs = { "Nimbus", "System", "Metal" };

    OUTER: for (int i = 0; i < preferLAFs.length; i++) {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if (preferLAFs[i].equals(info.getName())) {
          try {
            UIManager.setLookAndFeel(info.getClassName());
            break OUTER;
          } catch (Exception e) {
            // ignore if not supported
            break;
          }
        }
      }
    }
    */
  }

  /**
   * @effects initialises the base data structures needed by each GUI.
   */
  protected void init() {
    // region map
    containerMap = new Map<Region,Component>();

    // component map
    compMap = new Map();
  }
  
  private void initGUILanguageUpdater() {
    runUpdateGUILanguage = new Runnable() {
      public void run() {

        // reloads the text/labels of the GUI components
        Region region;
        Component c;
        // v3.1: support special component types
        AbstractButton button;
        
        //Map cfg;
        String compName;
        String guiName;
        Label labelObj;
        String label;
        String val;
        
        // debug
        if (debug)
          System.out.printf(View.this+"initGUILanguageUpdater:...");
        
        // need to update labels of components in compMap. 
        // 
        for (Entry<Region, Component> e : compMap.entrySet()) {
          region = e.getKey();
          c = e.getValue();
          compName = c.getName();
          labelObj = region.getLabel();
          if (labelObj == null)
            continue;
          
          label = labelObj.getValue(); 
          guiName = region.getName(); 
          if ((c instanceof JFrame) || (c instanceof JInternalFrame)) {
            // view
            val = label;
            // invoke the setTitle method to change the text
            // assume: this method is defined
            try {
              Method m = c.getClass().getMethod("setTitle", String.class);
              if (m != null) {
                m.invoke(c, val);
              }
            } catch (Exception ex) {
              // failed to change text for component
              System.err.println("AppGUI.changeGUILanguage: Failed to change title for component "
                  + c.getClass() + ", name: " + compName);
            }
          } else {
            // view component
            Method m = null;
            if (c instanceof JTextComponent || c instanceof JDataField) {
              val = region.getDefValue();
            } else {
              val = label;
            }

            if (c instanceof JDataField) {
              try {
                m = c.getClass().getMethod("setValue", Object.class);
                m.invoke(c, val);
              } catch (Exception ex2) {
                // give up
                ex2.printStackTrace();
                System.err.println("AppGUI.changeGUILanguage: Failed to change text/label for component "
                    + c.getClass() + ", name: " + compName);
              }
            } else {
              // invoke the setText method to change the text
              // assume: this method is defined
              
              // debug
              // System.out.printf("View.initGUILanguageUpdater: %n c: %s(%s)%n", c.getClass().getName(), c.getName());
              
              /* v3.1 support special component types (e.g. toolbar buttons) that are configured to not display text label
              // use method getText to check first if there is a text label 
              try {
                // try setText(String)
                m = c.getClass().getMethod("setText", String.class);
                m.invoke(c, val);
              } catch (Exception ex) {
                try {
                  // try: setText(String,String)
                  m = c.getClass().getMethod("setText", String.class,String.class);
                  m.invoke(c, guiName, val);
                } catch (Exception ex1) {
                  // give up
                  ex1.printStackTrace();
                  System.err
                      .println("AppGUI.changeGUILanguage: Failed to change text/label for component "
                          + c.getClass() + ", name: " + compName);
                }
              }
              */
              if (c instanceof AbstractButton) {  // TODO: add other component types here if needed
                // abstract button type
                button = (AbstractButton)c;
                if (button.getText() != null) {
                  // there is a label text, update it
                  button.setText(val);
                }
              } else {
                // rest of the component types
                try {
                  // try setText(String)
                  m = c.getClass().getMethod("setText", String.class);
                  m.invoke(c, val);
                } catch (Exception ex) {
                  try {
                    // try: setText(String,String)
                    m = c.getClass().getMethod("setText", String.class,String.class);
                    m.invoke(c, guiName, val);
                  } catch (Exception ex1) {
                    // give up
                    ex1.printStackTrace();
                    System.err
                        .println("AppGUI.changeGUILanguage: Failed to change text/label for component "
                            + c.getClass() + ", name: " + compName);
                  }
                }
              }
            }
          }
        } // end for: compMap
      }
    };  
  }
  

  /**
   * This method is invoked to refresh the text and labels of all the
   * displayable components in {@link #compMap} when the user changes the
   * language setting.
   * 
   * @effects refreshes the texts/labels of all the GUI components in
   *          {@link #compMap}.
   */
  public void changeGUILanguage() {
    SwingUtilities.invokeLater(runUpdateGUILanguage);
  }
  
  /**
   * @effects initialises <code>this.gui</code> using the GUI configuration
   *          settings identified by {@link #guiID}.
   * 
   *          <p>
   *          Such configuration typically contains settings for
   *          <code>title</code>, <code>width, height</code>, and
   *          {@link #toolBar}. For <code>main</code> GUIs, it also has
   *          {@link #menuBar} and {@link #statusBar}. For functional GUIs, it
   *          has {@link #components}, {@link #actions}, and {@link #statusBar}.
   */
  public void createGUI() throws NotPossibleException {
    // create and setup gui

    if (parentGUI != null) {
      // functional GUI
      BaseInternalFrame iframe = createFunctionalFrame();
      w = iframe;

      // v2.5.4: move to after update size
//      // add this gui to the parent
//      parentGUI.addFrameComponent(iframe, true);
//
//      // remove the associated window menu item on hidden
//      // IMPORTANT: this line must appear after invocation of addFrameComponent
//      // above
//      iframe.setRemoveAssociatesOnHidden(true);
    } else {
      // top-level GUI
      String title = guiConfig.getLabel().getValue(); //config.getStringValue("label");
      // v2.7: support for icon image
      ImageIcon icon = guiConfig.getImageIconObject();
      
      // this is the top-level GUI to contain other child windows
      JFrame mainFrame = new JFrame(title);
      
      if (icon != null)
        mainFrame.setIconImage(icon.getImage());
      
      WindowHelper whelper = controller.getWindowHelper();
      mainFrame.addWindowListener(whelper);
      
      w = mainFrame;
    }

    // get all child regions (in a pre-defined order)
    /*v2.7.2: separate into two steps for main GUI
    createAllGUIRegions();
    */
    createGUIRegions();

    updateSize();
    
    // v2.5.4: add internal frame to desktop after setting size so that
    // we can determine the location
    if (parentGUI != null) {
      // add this gui to the parent
      BaseInternalFrame iframe = (BaseInternalFrame) w;
      parentGUI.addFrameComponent(iframe, true);
  
      // remove the associated window menu item on hidden
      // IMPORTANT: this line must appear after invocation of addFrameComponent
      // above
      iframe.setRemoveAssociatesOnHidden(true);
    }
    
    /**v2.7.4: moved to before made visible
    // v2.7.2: update location (MUST do this after adding the frame to desktop (above))
    updateLocation();
    */

    // add to component map
    compMap.put(guiConfig, w);
  }

  /**
   * @effects 
   *  if this.w has been created (i.e. it has been initialised)
   *    return true
   *  else
   *    return false
   *  @version 2.7.4
   */  
  public boolean isCreated() {
    return w != null;
  }

  /**
   * @effects 
   *  create and return a <tt>BaseInternalFrame</tt> that is used as a the window that contains  
   *  the view of the functional module represented by this.
   */
  protected BaseInternalFrame createFunctionalFrame() {
    String title = guiConfig.getLabel().getValue();
    BaseInternalFrame iframe = new BaseInternalFrame(title, //
        true, // resizable
        false, // closable
        true, // maximizable
        true // iconifiable
    );

    // v2.7: support frame icon on the title bar
    ImageIcon icon = guiConfig.getImageIconObject();
    if (icon != null)
      iframe.setFrameIcon(icon);
    
    WindowHelper whelper = controller.getWindowHelper();
    iframe.addInternalFrameListener(whelper);
    
    return iframe;
  }

  /**
   * @effects 
   *  if this is main gui
   *    create the base child GUI regions of <tt>guiConfig</tt> (enough to start the GUI)
   *  else
   *    create all child GUI regions of <tt>guiConfig</tt>
   * 
   * @version 
   * - 5.2: to support side-panel region
   */
  protected void createGUIRegions() {
    List<Region> regions = controller.getGUIRegions(guiConfig);

    // v3.0: support application-wise gui properties that apply to the regions which will be created (below)
    //TODO: support gui-specific properties (using guiConfig.getProperties) if needed 
    PropertySet guiProps = controller.getMainController().getGUI().getGUIConfig().getProperties();
    
    // v3.1: support application-wise keyboard shotcuts
    java.util.Map<PropertyName,String> shotCutKeyMap = null;
    if (guiProps != null) {
      shotCutKeyMap = guiProps.getPropertyValuesByKeyPrefix(PropertyName.view_shotcuts, String.class, null);
    }

    for (Region region : regions) {
      if (debug) {
        controller.log("Create region %s", region);
      }
      /*regions of main GUI*/
      if (region.equals(MenuBar)) {
        // menu
        createMenuBar(region);
      } 
      else if (region.equals(ToolBar)) {
        // tool bar
        /*v3.0: support GUI properties
        createToolBar(region);
        */
        createToolBar(region, guiProps
            , shotCutKeyMap // v3.1
            );
      } 
      else if (region.equals(Desktop)) {
        // desktop
        createDesktop(region);
      } 
      else if (region.equals(StatusBar)) {
        // status bar
        createStatusBar(region);
      }
      /*regions of functional GUI*/
      else if (region.equals(SearchToolBar)) {
        // search tool bar
        createSearchToolBar(region
            , guiProps  // v3.0
            , shotCutKeyMap // v3.1
            );
      } else if (region.equalsByName(Components)) {
        // tool bar
        createComponents(region);
//      } 
//      // v5.2: support side panel (if specified)
//      else if (region.equalsByName(SidePane)) {
//        // side panel
//        createSidePanel(region);
//      // end v5.2
      } else if (region.equalsByName(Actions)
          || region.equalsByName(LoginActions)) {
        // actions
        createActions(region
            , guiProps  // v3.0
            , shotCutKeyMap // v3.1
            );
      } 
    }
  }
  
  /**
   * @requires 
   *  this is the top-level gui
   * @effects 
   *    post-process the child GUI regions of <tt>guiConfig</tt> 
   */
  private void postCreateMainGUIRegions() {
    postCreateMenuBar();
    
    postCreateToolBar();
  }
 

//  /**
//   * @effects 
//   *  create <b>all</b> child GUI regions of <tt>guiConfig</tt>
//   */
//  protected void createAllGUIRegions() {
//    List<Region> regions = controller.getGUIRegions(guiConfig);
//
//    for (Region region : regions) {
//      if (debug) {
//        controller.displayConsoleMessage("Tạo vùng " + region);
//      }
//      if (region.equals(MenuBar)) {
//        // menu
//        createMenuBar(region);
//      } else if (region.equals(ToolBar)) {
//        // tool bar
//        createToolBar(region);
//      } else if (region.equals(Desktop)) {
//        // desktop
//        createDesktop(region);
//      } else if (region.equals(SearchToolBar)) {
//        // search tool bar
//        createSearchToolBar(region);
//      } else if (region.equalsByName(Components)) {
//        // tool bar
//        createComponents(region);
//      } else if (region.equalsByName(Actions)
//          || region.equalsByName(LoginActions)) {
//        // actions
//        createActions(region);
//      } else if (region.equals(StatusBar)) {
//        // status bar
//        createStatusBar(region);
//      }
//    }
//  }

  /**
   * This is invoked after {@link #createGUI()} to perform post-creation tasks.
   * 
   * @effects if <code>this</code> is a main GUI then perform post-creation
   *          tasks, such as setting the input method and initialising the
   *          enabled states of the command and menu buttons
   */
  public void postCreateGUI() {
    /*v2.7.2: add post for main*/
    if (isTopLevel()) {
      postCreateMainGUIRegions();
    }
    
    // a runnable to update the gui language
    boolean supportLang = controller.getMainController().isSupportInternalisation();
    if (supportLang)
      initGUILanguageUpdater();
    
    /*v3.3c: removed due to the new update to createDataFieldComponents 
    // if gui.config.editable = false, update the data components
    if (guiConfig.getEditable() == false) {
      boolean recursive=true;
      setEditable(false,recursive);
    }
    */
  }

  /**
   * @effects 
   *  if this has not been activated before
   *    performs any necessary last-minute configuration before showing this GUI
   *    return true
   *  else 
   *    return false
   *  
   */
  public boolean preRunConfigure() throws NotPossibleException {
    /*v2.7.2: 
    if (notActivatedBefore) {
      //v2.7.2: DefaultPanel topPanel = getRootPanel();
      JDataContainer topContainer = getRootContainer();
      
      //TODO: customise this
      boolean recursive = true;
      
      if (topContainer != null) {
        // recursively pre-configure the top panel and all its sub-panels
        topContainer.preRunConfigure(recursive);
      }
      
      notActivatedBefore = false;
      return true;
    } else {
      return false;
    }*/
    return preRunConfigure(false);
  }

  /**
   * @requires this is a functional GUI
   * 
   * @effects 
   *  if this has not been activated before
   *    performs any necessary last-minute configuration before showing this GUI
   *    
   *    if withData = true
   *      also perform pre-run config on the data 
   *    
   *    sets state to activated
   *    
   *    return true
   *  else 
   *    return false
   *  
   */
  public boolean preRunConfigure(boolean withData) throws NotPossibleException {
    if (notActivatedBefore) {
      //v2.7.2: DefaultPanel topPanel = getRootPanel();
      JDataContainer topContainer = getRootContainer();
      
      //TODO: customise this
      boolean recursive = true;
      
      if (topContainer != null) {
        // recursively pre-configure the top panel and all its sub-panels
        topContainer.preRunConfigure(recursive);
        
        // v3.0: show all child containers that are configured with auto-activate
        //TODO: speed up by caching the auto-activate childs
        boolean autoActivated = DataContainerToolkit.showChildContainerIterator(topContainer, PropertyName.view_objectForm_autoActivate, Boolean.class, Boolean.TRUE);
        if (autoActivated) {
          // if there were containers shown then update the user gui size 
          updateSizeOnComponentChange();
        }
      }
      
      notActivatedBefore = false;
      
      if (withData) {
        try {
          controller.preRunConfigure();
        } catch (ApplicationException e) {
          controller.displayError(e.getCode(), e, e.getMessage());
        }
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * @requires 
   *  width is specified 
   *  
   * @effects 
   *  return the configured width of this
   */
  private int getConfiguredWidth(Dimension bound) {
    int width;
    
    width = guiConfig.getWidth();
    if (width == -1) {
      // use ratio
      float widthRatio = guiConfig.getWidthRatio();
      if (widthRatio > -1) {
        width = (int) (widthRatio * bound.getWidth()); 
      }
    }
    
    return width;
  }

  /**
   * @requires 
   *  height is specified 
   *  
   * @effects 
   *  return the configured height of this
   */
  private int getConfiguredHeight(Dimension bound) {
    int height;
    
    height = guiConfig.getHeight();
    if (height == -1) {
      // use ratio
      float heightRatio = guiConfig.getHeightRatio();
      if (heightRatio > -1) {
        height = (int) (heightRatio * bound.getHeight()); 
      }
    }
    
    return height;
  }

  /**
   * Update this.w.size based on both the pre-configured size, <tt>cfgSize</tt>, (if any) and based 
   * on the preferred size, <tt>prefSize</tt>.
   * 
   *  @requires 
   *    this.w != null
   *    
   *  @effects 
   *    <pre>
   *    if cfgSize is not specified OR prefSize > cfgSize
   *      if prefSize <= desktopSize 
   *        set this.w.size = prefSize 
   *      else
   *        set this.w.size = desktopSize
   *    else // prefSize <= cfgSize
   *      do nothing
   */
  public void updateSizeOnComponentChange() {
    Dimension bound = isTopLevel() ? GUIToolkit.getScreenSize() : getDesktop().getSize();

    // cfgSize
    boolean cfgSizeSpec = guiConfig.isSizeConfigured(); 
    int cfgW = -1, cfgH=-1; 
    if (cfgSizeSpec) {
      cfgW = getConfiguredWidth(bound);  
      cfgH = getConfiguredHeight(bound);
    }

    // prefSize
    Dimension prefSize = w.getPreferredSize();
    Dimension currSize = w.getSize();
    
    double prefW = prefSize.getWidth(), prefH = prefSize.getHeight();
    double currW = currSize.getWidth(), currH = currSize.getHeight();
    
    // the updated width, height (if any)
    double uw=-1, uh=-1;
    
    if (!cfgSizeSpec || prefW > cfgW || prefH > cfgH) {
      // cfgSize is not specified OR prefSize > cfgSize
      //double defaultChildRatio = controller.getConfig().getDefaultFixedChildGUISizeRatio();
      boolean toUpdateSize = false; 
      if (prefW > cfgW) {
        if (prefW <= bound.getWidth()) {
          if (prefW > currW) { // takes into account the current size
            uw = prefW; toUpdateSize = true;
          } else {
            uw = currW; // keep same
          }
        } else { 
          uw = bound.getWidth(); toUpdateSize = true;
        }
      } else if (cfgW > currW) {  // takes into account the current size
        uw = cfgW; toUpdateSize = true;
      } else {
        uw = currW;  // keep same
      }

      if (prefH > cfgH) {
        if (prefH <= bound.getHeight()) {
          if (prefH > currH) { // takes into account the current size
            uh = prefH; toUpdateSize = true;
          } else {
            uh = currH; // keep same
          }
        } else {
          uh = bound.getHeight(); toUpdateSize = true;         
        }
      } else if (cfgH > currH) { // takes into account the current size
        uh = cfgH; toUpdateSize = true;
      } else {
        uh = currH;   // keep same
      }
      
      if (toUpdateSize)
        w.setSize((int)uw, (int)uh);
    } else {
      // prefSize <= cfgSize
      // do nothing
    }
  }
  
  /**
   * @effects 
   *  update the size of this.w based on the GUI configuration
   *  
   * @requires 
   *  this.w != null
   */
  private void updateSize() {
    Dimension bound = isTopLevel() ? GUIToolkit.getScreenSize() : getDesktop().getSize();

    // cfgSize
    boolean cfgSizeSpec = guiConfig.isSizeConfigured(); 
    int width = -1, height=-1; 
    if (cfgSizeSpec) {
      width = getConfiguredWidth(bound);  
      height = getConfiguredHeight(bound);
    }

    //v2.7.2: if fixed width, height are available use them; otherwise use ratio setting
    if (cfgSizeSpec) {
      w.setSize(width, height);
    } else {
      // no fixed width, height
      /**
       * if any of the dimensions is not specified then auto-set the size to a
       * given ratio of the screen size (for main) or of the main size (for child)
       */
      Configuration config = controller.getConfig();
      double PARENT_RATIO = config.getMainGUISizeRatio();
      double CHILD_RATIO = config.getChildGUISizeRatio(); //-1f; // pack
      double defaultChildRatio = config.getDefaultFixedChildGUISizeRatio();//3f/4;
      
      //v2.7.2: Dimension parentSize = null;
      double ratio;

      boolean topLevel = isTopLevel();
      
      if (!topLevel) { //(w instanceof JInternalFrame) {
        //parentSize = parentGUI.getSize();
        ratio = CHILD_RATIO;
        
        // if ratio = -1 but w's preferred size is bigger than the desktop then 
        // reset it to a suitable portion of the desktop
        if (ratio < 0 && (w.getPreferredSize().width > bound.width))
          ratio = defaultChildRatio;
      } else {
        // main GUI 
        ratio = PARENT_RATIO;
        
        if (ratio < 0)
          // default: = screen size
          ratio = 1;
      }

      if (ratio > -1) {
        /*v2.7.2: simplified
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        if (width == null) {
          width = (int) (ratio * ((parentSize != null) ? parentSize.width
              : screenSize.width));
        }

        if (height == null) {
          height = (int) (ratio * ((parentSize != null) ? parentSize.height
              : screenSize.height));
        } */
        //Toolkit tk = Toolkit.getDefaultToolkit();
        //Dimension screenSize = GUIToolkit.getScreenSize(); //tk.getScreenSize();
        width = //(int) (ratio * ((parentSize != null) ? parentSize.width : screenSize.width));
            (int) (ratio * bound.width);
        height = //(int) (ratio * ((parentSize != null) ? parentSize.height : screenSize.height));
            (int) (ratio * bound.height);

        if (debug)
          System.out.format("GUI %s size: (%d,%d)%n", toString(), width, height);

        w.setSize(width, height);
      } else { 
        // no size configuration: pack 
        /***
         * because pack() only works well when JInternalFrame is visible
         * we mark this gui as candidate for pack and pack it later
         * if the internal frame is not yet visible
         */
        if (!topLevel && !isVisible()) {
          toPack=true;
        } else {
          pack(w);
        }
      }      
    }
  }
  
//  /**
//   * @effects 
//   *  compute the size of this.w if it were made compact (just big enough to contain the content)
//   */
//  private void calcPackedSize(Container container) {
//    Dimension prefSize = container.getPreferredSize();
//    
//    // v2.6.4.a: adjust preferred size to not exceed the desktop size
//    Dimension parentSize = getDesktop().getSize(); //v2.7.2: parentGUI.getSize();
//    Configuration config = controller.getConfig();
//    double defaultChildRatio = config.getDefaultFixedChildGUISizeRatio();//3f/4;
//    
//    int width, height;
//    if (prefSize.width > parentSize.width) {
//      width = (int) (defaultChildRatio * parentSize.width); 
//    } else {
//      width = prefSize.width;
//    }
//
//    if (prefSize.height > parentSize.height) {
//      height = (int) (defaultChildRatio * parentSize.height); 
//    } else {
//      height = prefSize.height;
//    }
//    
//    // need to increase to cover the entire JScrollPane of the components region
//    // TODO: it seems that we need to determine the number of nested JDataTables
//    // in this and multiply that with this gap
////    final int gap = 20;
////    prefSize.width = prefSize.width+20;
////    prefSize.height = prefSize.height+20;
//
//    packedSize = new Dimension(width, height);
//  }
  
  /**
   * @effects 
   *  return the current size of this
   * @version 2.7.3
   */
  private Dimension getCurrentSize() {
    //debug
    //System.out.printf("%s.size = %s%n", this, w.getSize());
    
    return w.getSize();
  }
  
  /**
   * @effects 
   *  update the location of this.w based on the GUI configuration
   *  
   * @requires 
   *  this.w != null /\ {@link #updateSize()} has been invoked
   */
  public void updateLocation() {
    // v2.7.2: support (x,y)
    // v2.7.3: support two cases of topX,topY: (1) relative: in (0,1], (2) absolute 
    // v2.7.4: support another special case: relative x (y) = 0.5, which means middle of x (y) axis
    
    double xRat = guiConfig.getTopX();
    double yRat = guiConfig.getTopY();
    if (xRat > -1 && yRat > -1) {
      double x, y;
      Dimension bound;
      
      if (w instanceof JInternalFrame) {
        // child frame: use desktop size as bound
        JComponent desktop = getDesktop();
        bound = desktop.getSize(); 
      } else {
        // main frame: use screen size as bound
        Toolkit tk = Toolkit.getDefaultToolkit();
        bound = tk.getScreenSize();
      }

      /* v2.7.4: support xRat, yRat = 0.5
      x = (xRat <= 1) ? // relative
            bound.getWidth() * xRat :  
            xRat;   // absolute 
      y = (yRat <= 1) ? // relative
          bound.getHeight() * yRat :   
          yRat;     // absolute
      */
      if (xRat == 0.5) {
        // middle of x-axis
        double myW = getCurrentSize().getWidth();
        x = Math.max((bound.getWidth() - myW)/2,0D);  // in case myW is bigger then bound 
      } else {
        x = (xRat <= 1) ? // relative
            bound.getWidth() * xRat :  
            xRat;   // absolute         
      }
      
      if (yRat == 0.5) {
        // middle of y-axis
        double myH = getCurrentSize().getHeight();
        y = Math.max((bound.getHeight() - myH)/2,0D); // in case myH is bigger then bound
      } else {
        y = (yRat <= 1) ? // relative
            bound.getHeight() * yRat :   
            yRat;     // absolute
      }
      
      w.setLocation((int)x,(int)y);
    }
    
    isLocated = true;
  }

  /**
   * @effects 
   *  return the current location of this
   * @version 2.7.3
   */
  private Point getCurrentLocation() {
    //debug
    //System.out.printf("%s.location = %s%n", this, w.getLocation());

    return w.getLocation();
  }
  
  public boolean isSized() {
    return !toPack;
  }
  
  /**
   * @effects 
   *  if this.location has been set
   *    return true
   *  else
   *    return false
   * @version 2.7.4
   */
  public boolean isLocated() {
    return isLocated;
  }
  
  public void pack() {
    pack(w);
    
    if (toPack)
      toPack = false;
  }
  
  /**
   * @effects pack the display of the gui component represented by this
   *  just enough to contain all of its content.   
   *  
   */
  private void pack(Container container) {
    if (container instanceof JFrame) {
      // top level
      ((JFrame) container).pack();
    } else {
      // functional GUI
      JInternalFrame iframe = (JInternalFrame) container;
      /**
       * simulate the internal frame's pack logic without changing its
       * visibility status
       */
      Dimension prefSize = iframe.getPreferredSize();
      
      // v2.6.4.a: adjust preferred size to not exceed the desktop size
      Dimension parentSize = getDesktop().getSize(); //v2.7.2: parentGUI.getSize();
      Configuration config = controller.getConfig();
      double defaultChildRatio = config.getDefaultFixedChildGUISizeRatio();//3f/4;
      
      int width, height;
      if (prefSize.width > parentSize.width) {
        width = (int) (defaultChildRatio * parentSize.width); 
      } else {
        width = prefSize.width;
      }

      if (prefSize.height > parentSize.height) {
        height = (int) (defaultChildRatio * parentSize.height); 
      } else {
        height = prefSize.height;
      }
      
      // need to increase to cover the entire JScrollPane of the components region
      // TODO: it seems that we need to determine the number of nested JDataTables
      // in this and multiply that with this gap
//      final int gap = 20;
//      prefSize.width = prefSize.width+20;
//      prefSize.height = prefSize.height+20;
      
      iframe.setSize(width, height);
      iframe.validate();
    }
  }

  /**
   * This method should only be invoked on the main controller.
   * @requires this is main view
   * @effects if <code>lang</code> is different from the current locale's then
   *          sets the display and input method to the locale of the specified
   *          language, throws <code>NotPossibleException</code> if the language
   *          is not supported by the JVM.
   */
  public void changeInputMethod(Language language) throws NotPossibleException {

    InputContext inputContext = w.getInputContext();
    Locale currLocale = inputContext.getLocale();
    if (debug) {
      System.out.println("Current locale: " + currLocale);
    }

    String lang = language.getLanguageCode();
    
    if (!currLocale.getLanguage().equals(lang)) {
      Locale locale = new Locale(lang);

      System.out.println("Changing to locale: " + locale);
      boolean success = inputContext.selectInputMethod(locale);

      if (!success) {
        throw new NotPossibleException(
            NotPossibleException.Code.LANGUAGE_NOT_SUPPORTED,
            "Không hỗ trợ ngôn ngữ: {0}", lang);
      }
    }
  }

  /**
   * Used by a functional <code>AppGUI</code> to add itself to the parent GUI's
   * desktop.
   * 
   * @effects adds <code>f</code> to <code>this.w</code> as an internal frame.
   *          <p>
   *          If <code>iconified=true</code> then <code>f</code> is iconified on
   *          the desktop.
   */
  void addFrameComponent(JInternalFrame f, boolean iconified) {
    JScrollableDesktopPane desktop = (JScrollableDesktopPane) containerMap
        .get(Desktop);
    // determine the location (x,y) such that the frame is (at best) positioned at the centre 
    // of the desktop
    int x, y;
    x = 0;
    y = 0;
    
    /* v2.7.4: handle error */
    desktop.add(f, iconified, x, y);
    
  }

  /**
   * @requires 
   *  this is the top-level gui
   *  
   * @effects initialises {@link menuBar} based on the GUI configuration
   *          settings identified by <code>region</code>.
   */
  protected void createMenuBar(Region region) {
    ActionListener al = controller.getInputHelper();

    // get style settings for menu bar
    Style style = controller.getStyleSettings(region);

    if (debug) {
      System.out.println("menu style " + style);
    }

    JMenuBar menuBar = new JMenuBar();
    menuBar.setBackground(GUIToolkit.getColorValue(style.getBgColor()));
    menuBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    // get the menu bar configuration (which includes all of its menus)
    List<RegionMap> menuCfgs = region.getChildren();
    
    JMenu m;
    Region menuCfg;
    
    /*
     * v2.6.4.a: use the config for the Windows menu region separately from other menus
     */
    Region windowMenuCfg = null;
    
    // create the menus
    java.util.Map<Region,Character> menuMenMap = new HashMap();
    char mne; Character mneChar;
    
    if (menuCfgs != null) {
      /**v2.5.4: create menu mnemonics for top-level menus using menu labels*/
      Stack<Character> mnemonics = new Stack();
      
      // v3.1: get menu mnemonics first before creating the menus (b/c otherwise 
      // we may run out of mnemonics after creating the menu items)
      String menuLabel;
      for (RegionMap menuMap : menuCfgs) {
        menuCfg = menuMap.getChild();
        menuLabel = menuCfg.getLabelAsString(); 
        try {
          mne = GUIToolkit.getMnemonicFromASCIIName(menuLabel, mnemonics);
          menuMenMap.put(menuCfg, mne);
        } catch (NotFoundException e) {
          // should not happen -> log error
          controller.logError(e.getMessage(), e);
        }
      }
      
      // now create menus
      for (RegionMap menuMap : menuCfgs) {
        menuCfg = menuMap.getChild();
        
         // v2.6.4.a: checks Windows menu region (add it last, see bottom of this method)
        if (menuCfg.getName().equals(RegionName.Window.name())) {
          windowMenuCfg = menuCfg;
          continue;
        }
        
        if (debug) {
          System.out.println("Tạo menu: " + menuCfg);
        }
        
        m = createMenu(menuCfg, al, 
                            mnemonics // v3.1
            );
        if (m != null) {
          /*v3.1: set mnemonic from map
          menuLabel = menuCfg.getLabelAsString(); //.getLabel().getValue();
          try {
            mne = GUIToolkit.getMnemonicFromASCIIName(menuLabel, mnemonics);
            m.setMnemonic(mne);
          } catch (NotFoundException e) {
            // should not happen -> log error
            controller.logError(e.getMessage(), e);
          }
          */
          mneChar = menuMenMap.get(menuCfg);
          if (mneChar != null) m.setMnemonic(mneChar);
          
          /*v2.7.2: hide all menus except the File menu*/
          if (!menuCfg.getName().equals(RegionName.File.name())) {
            m.setVisible(false);
          }
          
          menuBar.add(m);
        }
      }
    }

    JFrame frame = (JFrame) w;
    // add menu bar to the frame
    frame.setJMenuBar(menuBar);

    // register the menu bar to the desktop, which also creates an additional Windows menu
    JScrollableDesktopPane desktop = (JScrollableDesktopPane) containerMap
        .get(Desktop);
    
    /**v2.6.4.a: (this will add a Windows menu with some window management menu items)
     *  use a customised implementation which supports the localisation of the window management menu items
    desktop.registerMenuBar(menuBar);
    */
    if (windowMenuCfg != null) {
      // v3.1: pass a viewCompMapHandler argument as a call-back used to register 
      // each menu item of winMenu to compMap
      ViewComponentMapHandler viewCompMapHandler = new ViewComponentMapHandler(compMap);
      JMenu winMenu = desktop.registerMenuBar(menuBar, windowMenuCfg, viewCompMapHandler);
  
      // v3.1: add mnemonic
      mneChar = menuMenMap.get(windowMenuCfg);
      if (mneChar != null) winMenu.setMnemonic(mneChar);
  
      // v3.1: add winMenu to compMap (for label update)
      compMap.put(windowMenuCfg, winMenu);
    }
    
    // adds to region map
    containerMap.put(region, menuBar);
  }

  /**
   * @requires  
   *  this is the top-level GUI 
   * @effects 
   *  post-process the menu bar (e.g. unhide all menus) for use 
   * @version 2.7.2
   */
  private void postCreateMenuBar() {
    JMenuBar menuBar = (JMenuBar) getContainerOf(MenuBar);
    int count = menuBar.getMenuCount();
    JMenu m;
    for (int i = 0; i < count; i++) {
      m = menuBar.getMenu(i);
      if (!m.isVisible()) m.setVisible(true);
    }
  }
  
  /**
   * @param existingMnemonics 
   * @requires this is top-level view
   * 
   * @effects
   *  if menuCfg has children (i.e. menu items)
   *    return a <code>JMenu</code> whose whose GUI configurations is
   *    identified by <code>menuCfg</code> and all items of whom have
   *    <code>al</code> as the action listener. 
   * 
   *    <p>Registers <code>filterdPropListener</code> as the item listener of
   *    any checkbox and radio menu items of this menu.
   * 
   *    <p>In addition, adds all the menu items of this menu to
   *    {@link #compMap}, using their gui-IDs.
   *  else
   *    return null
   */
  protected JMenu createMenu(Region menuCfg, ActionListener al, Stack<Character> existingMnemonics) {   
    List<RegionMap> items = menuCfg.getChildren();
    JMenu m = null;

    // only create menu if it has children 
    if (items != null) {
      /**v2.5.3: remove */
      //Properties props = controller.getApplicationProperties();

      // the menu
      int id = menuCfg.getId(); 
      String menuName = menuCfg.getName(); 
      String label = menuCfg.getLabelAsString();//getLabel().getValue();
      /* v2.7.1
      ImageIcon icon = null;
      try {
        icon = GUIToolkit.getImageIcon(menuCfg.getImageIcon(), label);
      } catch (NotFoundException e) {}
      */
      ImageIcon icon = menuCfg.getImageIconObject();
      
      Boolean enabledB = menuCfg.getEnabled();
      boolean enabled = (enabledB != null) ? enabledB : true; //itemConfig.getBooleanValue("enabled", true);

      /**v2.5.4: is this a tools menu? (used later) */
      final boolean toolsMenu = menuCfg.equalsByName(Tools);
      
      m = new JMenu(label);
      m.setName(menuName);
      if (icon != null)
        m.setIcon(icon);
      
      m.setActionCommand(menuName);
      m.setEnabled(enabled);

      // add menu to component map
      compMap.put(menuCfg, m);

      RegionType menuType = menuCfg.getType(); //itemConfig.getStringValue("type");
      JMenuItem mi;
      RegionMap itemMap;
      Region itemConfig;
      RegionType menuItemType;  // v3.1
      int shotCutKey;  // v3.1
      char mne; // v3.1
      if (menuType != null && menuType.equals(RegionType.ChoiceMenu)) { 
        // a menu of choices
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < items.size(); i++) {
          itemMap = items.get(i);
          itemConfig = itemMap.getChild();
          mi = createChoiceMenuItem(itemConfig, al);
          group.add(mi);
          m.add(mi);
        }
        // if this menu is one of the application property then set it
      } else {
        // a normal menu (possibly containing sub-menus)
        for (int i = 0; i < items.size(); i++) {
          itemMap = items.get(i);
          itemConfig = itemMap.getChild();
          /* v3.1: moved gen key stroke to method
          mi = createMenuItem(itemConfig, al);
          if (mi != null) {
            // v2.5.4: if m = Tools menu  
            //    create keyboard short-cut for each item as Alt+<item-index>
            //
            if (toolsMenu && !(mi instanceof JMenu)) {
              try {
                int keyCode = getMenuItemKeyCode(i + 1);
                mi.setAccelerator(KeyStroke.getKeyStroke(keyCode,
                    KeyEvent.ALT_DOWN_MASK));
              } catch (NotFoundException e) {
                // perhaps item index is larger than 10, log error
                controller.logError(e.getMessage(), e);
              }
            }
            m.add(mi);
          }
          */
          menuItemType = itemConfig.getType();
          if (menuItemType != null && (menuItemType.equals(RegionType.Menu) || menuItemType.equals(RegionType.ChoiceMenu))) {
            // a submenu: recursive call
            if (debug) {
              System.out.println("Tạo menu con: " + itemConfig);
            }
            mi = createMenu(itemConfig, al, existingMnemonics);
          } else {
            // a menu item
            if (toolsMenu) {
              // to add shot-cut key
              try {
                // for shot-cuts, we need to use mnemonics in UPPERCASE
                mne = GUIToolkit.getMnemonicFromASCIIName(itemConfig.getLabelAsString().toUpperCase(), existingMnemonics);
                shotCutKey = GUIToolkit.getAlphabeticKeyCode(mne);
              } catch (NotFoundException e) {
                // should not happen -> log error
                shotCutKey = -1;
                controller.logError(e.getMessage(), e);
              }
            } else {
              shotCutKey = -1;
            }
            mi = createMenuItem(itemConfig, al, shotCutKey);
          }
          
          if (mi != null) {
            m.add(mi);
          }
        }
      }
    }
    
    return m;
  }

  /**
   * @effects returns a <code>JMenuItem</code> whose GUI configuration is
   *          <code>itemConfig</code> and whose action listener is
   *          <code>al</code>.
   */
  protected JMenuItem createChoiceMenuItem(Region itemConfig, ActionListener al) {
    JMenuItem mi;
    String label = itemConfig.getLabel().getValue(); //itemConfig.getStringValue("label");
    String name = itemConfig.getName(); //itemConfig.getStringValue("name");
    String val = itemConfig.getDefValue(); //itemConfig.getStringValue("defvalue");
    /*v2.7
    String imageIcon = itemConfig.getImageIcon();
    ImageIcon icon = null; 
    
    try {
      icon = GUIToolkit.getImageIcon(imageIcon, label);
    } catch (NotFoundException e) {}
    */
    ImageIcon icon = itemConfig.getImageIconObject();
    
    Boolean enabledB = itemConfig.getEnabled();
    boolean enabled = (enabledB != null) ? enabledB : true; //itemConfig.getBooleanValue("enabled", true);

    // a menu item
    if (val != null && val.equals("true")) {
      mi = new JRadioButtonMenuItem(label, true);
    } else {
      mi = new JRadioButtonMenuItem(label, false);
    }
    mi.setName(name);
    mi.addActionListener(al);

    if (icon != null) {
      mi.setIcon(icon);
    }

    mi.setActionCommand(name);
    mi.setEnabled(enabled);

    // if region is a state change listener then register  
    if (itemConfig.getIsStateListener()) {
      registerStateListener((StateChangeListener)mi);
    }

    // add to component map
    compMap.put(itemConfig, mi);
    
    return mi;
  }

  /**
   * @effects 
   *  create and return a <code>JMenuItem</code> whose GUI configuration is
   *  <code>itemConfig</code> and whose action listener is <code>al</code>.
   *  
   *  <p>If <tt>shotCutKey > -1</tt> then also add it as shot-cut key for the menu item
   */
  protected JMenuItem createMenuItem(Region itemConfig, ActionListener al, final int shotCutKey) {
    JMenuItem mi;
    String label = itemConfig.getLabel().getValue(); 
    String name = itemConfig.getName(); 
    RegionType type = itemConfig.getType(); 
    String val = itemConfig.getDefValue();
    /*v2.7.1
    String imageIcon = itemConfig.getImageIcon();
    ImageIcon icon = null;
    try {
      icon = GUIToolkit.getImageIcon(imageIcon, label);
    } catch (NotFoundException e) {}
    */
    ImageIcon icon = itemConfig.getImageIconObject();
    
    Boolean enabledB = itemConfig.getEnabled();
    boolean enabled = (enabledB != null) ? enabledB : true; //itemConfig.getBooleanValue("enabled", true);

    /*v3.1: moved sub-menus to the caller
    // if there are sub-menus associated to this one then create them
    if (type != null && (type.equals(Type.Menu) || type.equals(Type.ChoiceMenu))) {
      // a submenu
      
      if (debug) {
        System.out.println("Tạo menu con: " + itemConfig);
      }
      
      mi = createMenu(itemConfig, al);
    } else {
    */
      // a menu item
      if (debug) {
        System.out.println("menu item " + itemConfig);
      }
      
      // a menu item
      if (type != null && type.equals(RegionType.Check)) { //type.equals("check")) {
        // checked menu item
        if (val != null && val.equals("true")) {
          mi = new JCheckBoxMenuItem(label, true);
        } else {
          mi = new JCheckBoxMenuItem(label, false);
        }
      } else {
        mi = new JMenuItem(label);
      }
      mi.setName(name);
      if (icon != null)
        mi.setIcon(icon);
      
      mi.setActionCommand(name);
      mi.addActionListener(al);

      mi.setEnabled(enabled);

      // if region is a state change listener then register  
      if (itemConfig.getIsStateListener()) {
        registerStateListener((StateChangeListener)mi);
      }
      
      // v3.1: add shot-cut (if needed)
      if (shotCutKey > -1)
        mi.setAccelerator(KeyStroke.getKeyStroke(shotCutKey, KeyEvent.ALT_DOWN_MASK));
      
      // add to component map
      compMap.put(itemConfig, mi);
    // v3.1 } // end else (menu item)

    return mi;
  }

  /**
   * @param guiProps the GUI configuration properties that may apply to the tool bar as a whole
   * 
   * @requires  
   *  this is the top-level GUI /\ region is the GUI configuration for toolbar 
   *  
   * @effects initialises {@link #toolBar} based on the GUI configuration
   *          identified by <code>region</code>.
   * @version 
   * - 3.0: support GUI properties <br>
   * - 3.1: support keyboard shotcuts
   */
  private void createToolBar(Region region, PropertySet guiProps, java.util.Map<PropertyName, String> shotCutKeyMap) {
    /* v2.7.3: support exclusion
    JToolBar toolBar = buildToolBar(region, null);
    */
    List<Region> exclusion = getExclusion();
    
    // v3.0: add support for gui properties
    boolean iconDisplay, textDisplay;
    if (guiProps != null) {
      iconDisplay = guiProps.getPropertyValue(
          PropertyName.view_toolBar_buttonIconDisplay, Boolean.class, Boolean.TRUE);
      textDisplay = guiProps.getPropertyValue(
          PropertyName.view_toolBar_buttonTextDisplay, Boolean.class, Boolean.TRUE);
    } else {  // default
      iconDisplay = true; textDisplay = true;
    }
    
    JToolBar toolBar = buildToolBar(region
        // v3.1: , null
        , exclusion, 
        iconDisplay, textDisplay  // v3.0
        ,shotCutKeyMap
        );

    /*v2.7: hide toolbar */
    toolBar.setVisible(false);
    
    containerMap.put(region, toolBar);

    w.add(toolBar, getGUILocation(region));
  }

  /**
   * @requires  
   *  this is the top-level GUI 
   * @effects 
   *  post-process the tool bar (e.g. unhide it) for use 
   * @version 2.7.2
   */
  private void postCreateToolBar() {
    JToolBar toolBar = (JToolBar) getContainerOf(ToolBar);
    if (toolBar != null)
      toolBar.setVisible(true);
  }
  
  /**
   * @effects initialises the search tool bar ({@see #toolBar}) based on the GUI
   *          configuration identified by <code>regionID</code>.
   * 
   *          <p>
   *          If property <code>{@link #PropertyName.FindToolBar} = false</code>
   *          then hides the tool bar, otherwise shows it (default).
   * @version 
   * - 3.0: support gui properties <br>
   * - 3.1: support keyboard shotcuts
   */
  private void createSearchToolBar(Region region
      , PropertySet guiProps  // v3.0  
      , java.util.Map<PropertyName, String> shotCutKeyMap
      ) {
    // create the tool bar

    // v3.0: add support for gui properties
    boolean iconDisplay, textDisplay;
    if (guiProps != null) {
      iconDisplay = guiProps.getPropertyValue(
          PropertyName.view_searchToolBar_buttonIconDisplay, Boolean.class, Boolean.TRUE);
      textDisplay = guiProps.getPropertyValue(
          PropertyName.view_searchToolBar_buttonTextDisplay, Boolean.class, Boolean.TRUE);
    } else {  // default
      iconDisplay = true; textDisplay = true;
    }
    
    JToolBar toolBar = buildToolBar(region 
        // v3.1: , null
        , null  // v3.0
        , iconDisplay, textDisplay  // v3.0
        , shotCutKeyMap
        );

    containerMap.put(region, toolBar);

    w.add(toolBar, getGUILocation(region));

    // toogle of search tool bar
    setVisibleContainer(region, false);
  }

  // v3.0
//  /**
//   * @effects returns a <code>JToolBar</code> which is created based on the GUI
//   *          configuration identified by <code>regionID</code> and using
//   *          <code>al</code> as the action listener for its command buttons.
//   */
//  protected JToolBar buildToolBar(Region region, ActionListener al
//      , PropertySet guiProps  // v3.0
//      ) {
//    // get style settings for menu bar
//    Style tbstyle = controller.getStyleSettings(region);
//
//    if (debug)
//      System.out.println("tool bar style: " + tbstyle);
//
//    JToolBar toolBar = new JToolBar();
//    toolBar.setFloatable(false);
//    toolBar.setBackground(GUIToolkit.getColorValue(tbstyle.getBgColor()));//tbstyle.getColorValue("bgcolor"));
//
//    toolBar.setName(region.getName());
//
//    // the tool bar buttons
//    JComponent b = null;
//    Type type;
//    Style styleConfig;
//    List<RegionMap> items = region.getChildren();
//    RegionMap itemMap;
//    Region itemConfig;
//    if (items != null) {
//      // v3.0: add support for gui properties
//      boolean iconDisplay, textDisplay;
//      if (guiProps != null) {
//        iconDisplay = guiProps.getPropertyValue(
//            PropertyName.view_searchToolBar_buttonIconDisplay, Boolean.class, Boolean.TRUE);
//        textDisplay = guiProps.getPropertyValue(
//            PropertyName.view_searchToolBar_buttonTextDisplay, Boolean.class, Boolean.TRUE);
//      } else {  // default
//        iconDisplay = true; textDisplay = true;
//      }
//      
//      for (int i = 0; i < items.size(); i++) {
//        itemMap = items.get(i);
//        itemConfig = itemMap.getChild();
//        type = itemConfig.getType(); // (String) itemConfig.get("type");
//        styleConfig = controller.getStyleSettings(itemConfig);
//        if (type != null) {
//          if (type.equals(Type.Label)) {
//            // label
//            b = createLabel(itemConfig, styleConfig, false, false);
//          } else if (type.equals(Type.Text)) {
//            // text field
//            b = createTextDataField(itemConfig, styleConfig);
//          } else if (type.equals(Type.Check)) {
//            // check box item
//            b = createCheckBoxItem(itemConfig, styleConfig);
//          } else
//            throw new NotImplementedException(
//                NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//                "Tính năng hiện không được hỗ trợ: {0}", type);
//        } else { // default is button
//          b = createButton(itemConfig, al, 
//              iconDisplay, textDisplay  // v3.0
//              );
//        }
//
//        toolBar.add(b);
//      }
//    }
//
//    return toolBar;
//  }

  /**
   * @effects returns a <code>JToolBar</code> which is created based on the GUI
   *          configuration identified by <code>region</code> and using
   *          <code>al</code> as the action listener for its command buttons.
   *          
   *          <p>If<tt>exclusion != null</tt> then the tool bar elements whose configurations are in <tt>exclusion</tt>
   *          will not be created.
   *  @version 
   *  - 2.7.3 <br>
   *  - 3.1: support keyboard shotcuts
   */
  protected JToolBar buildToolBar(Region region 
      // v3.1: , final ActionListener al
      , List<Region> exclusion, 
      boolean iconDisplay, boolean textDisplay  // v3.0
      , java.util.Map<PropertyName, String> shotCutKeyMap
      ) {
    // get style settings for menu bar
    Style tbstyle = controller.getStyleSettings(region);

    if (debug)
      System.out.println("tool bar style: " + tbstyle);

    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setBackground(GUIToolkit.getColorValue(tbstyle.getBgColor()));//tbstyle.getColorValue("bgcolor"));

    toolBar.setName(region.getName());

    // the tool bar buttons
    JComponent toolComp = null;
    RegionType type;
    Style styleConfig;
    List<RegionMap> items = region.getChildren();
    RegionMap itemMap;
    Region itemConfig;
    String shotCutKey; // v3.1
    if (items != null) {
      for (int i = 0; i < items.size(); i++) {
        itemMap = items.get(i);
        itemConfig = itemMap.getChild();
        
        // ignore items that are in exclusion (if specified)
        if (exclusion != null && exclusion.contains(itemConfig))
          continue;
        
        type = itemConfig.getType(); // (String) itemConfig.get("type");
        styleConfig = controller.getStyleSettings(itemConfig);
        if (type != null) {
          if (type.equals(RegionType.Label)) {
            // label
            /* v3.0: add support for gui properties
            toolComp = createLabel(itemConfig, styleConfig, false, false);
            */
            toolComp = createLabel(itemConfig, styleConfig, false, false, 
                iconDisplay, textDisplay);
          } else if (type.equals(RegionType.Text)) {
            // text field
            toolComp = createTextDataField(itemConfig, styleConfig);
          } else if (type.equals(RegionType.Check)) {
            // check box item
            toolComp = createCheckBoxItem(itemConfig, styleConfig, true, true);
            /*v3.0: if check box supports icon then use this to turn on/of icon/text 
            toolComp = createCheckBoxItem(itemConfig, styleConfig, iconDisplay, textDisplay);
            */
          } else
            throw new NotImplementedException(
                NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
                "Tính năng hiện không được hỗ trợ: {0}", type);
        } else { // default is button
          /* v3.0: add support for gui properties
          toolComp = createButton(itemConfig, al);
          */
          if (shotCutKeyMap != null) {
            shotCutKey = lookUpKeyboardShotCut(itemConfig, shotCutKeyMap);
          } else {
            shotCutKey = null;
          }

          toolComp = createButton(itemConfig, 
              // v3.1: al, 
              iconDisplay, textDisplay  // v3.0
              , shotCutKey  // v3.1
              );
        }
        
        toolBar.add(toolComp);
      }
    }

    return toolBar;
  }
      
  /**
   * @modifies {@link #compMap}, 
   *           <tt>mainView.</tt>{@link #buttonShotCutKeyMap}
   *   
   * @effects 
   *  create and return a new <code>JButton</code> whose GUI configuration is
   *   <code>itemConfig</code>. 
   *  <p>Add button to {@link #compMap} and if <tt>shotCutKey > -1</tt> then also 
   *  add entry <tt>(button, shotCutKey)</tt> to <tt>mainView.</tt>{@link #buttonShotCutKeyMap}
   * @version 
   * - 3.0: support icon and text display <br>
   * - 3.1: support keyboard shot-cuts
   */
  protected JButton createButton(final Region itemConfig, 
      // v3.1: final ActionListener al, 
      final boolean iconDisplay, final boolean textDisplay  // v3.0
      , final String shotCutKey
      ) {

    JButton button = new JButton();
    String name = itemConfig.getName(); 
    String label = itemConfig.getLabel().getValue(); 
    Boolean enabledB = itemConfig.getEnabled();
    boolean enabled = (enabledB != null) ? enabledB : true;
    
    // retrieve button style
    Style bstyle = controller.getStyleSettings(itemConfig);

    if (debug)
      System.out.println("Button " + name + " config: " + itemConfig
          + "; style: " + bstyle);

    button.setActionCommand(name);

    ImageIcon icon = itemConfig.getImageIconObject();
    
    /* v3.0: support config for text display
    b.setText(label);
    */
    if (textDisplay) {
      button.setText(label);
    } else {
      button.setText(null);  // v3.1
      // use tool tip text instead
      button.setToolTipText(label);
    }
    
    if (icon != null) { // image found
      /* v3.0: support config for icon display
      b.setIcon(icon);
      */
      if (iconDisplay) {
        button.setIcon(icon);
      }
    }

    Integer width = itemConfig.getWidth(); 
    Integer height = itemConfig.getHeight(); 
    if (width != null && height != null)
      button.setMaximumSize(new Dimension(width, height));

    button.setBorderPainted(false);
    Font font = GUIToolkit.getFontValue(bstyle.getFont()); //bstyle.getFontValue("font");
    if (font != null) {
      button.setFont(font);
    }
    button.setBackground(GUIToolkit.getColorValue(bstyle.getBgColor()));//bstyle.getColorValue("bgcolor"));
    button.setForeground(GUIToolkit.getColorValue(bstyle.getFgColor()));//bstyle.getColorValue("fgcolor"));

    /* v3.1: not used
    if (al != null)
      b.addActionListener(al);     
     */
    button.setEnabled(enabled);

    // if region is a state change listener then register  
    if (itemConfig.getIsStateListener()) {
      registerStateListener((StateChangeListener)button);
    }
    
    // v3.1:
    if (shotCutKey != null) {
      addButtonShotCutKeyMapping(button, shotCutKey);
    }
    
    // add to component map
    compMap.put(itemConfig, button);

    return button;
  }

  /**
   * @requires 
   *   this is the main View
   * 
   * @effects 
   *  return the shot-cut key map of all the components of this
   * @version 3.1
   */
  private java.util.Map<JComponent, String> getButtonShotCutKeyMap() {
    return buttonShotCutKeyMap;
  }

  /**
   * @effects 
   *  add entry <tt>(button, shotCutKey)</tt> to <tt>mainView.</tt>{@link #buttonShotCutKeyMap}
   */
  private void addButtonShotCutKeyMapping(JButton button, String shotCutKey) {
    View main = getParentGUI();
    if (main != null) {
      // not main: forwards to main
      main.addButtonShotCutKeyMapping(button, shotCutKey);
    } else {
      // this is main
      if (buttonShotCutKeyMap == null) buttonShotCutKeyMap = new HashMap();
      
      buttonShotCutKeyMap.put(button, shotCutKey);
    }
  }

  /**
   * @effects
   *  if this not main view
   *    call <tt>mainView</tt>.{@link #getButtonActionMappping(JButton)}
   *  else
   *    return {@link ButtonAction} of <tt>button</tt> or return <tt>null</tt>
   *    if no such mapping is defined 
   * @version 3.1
   */
  private ButtonAction getButtonActionMappping(JButton button) {
    View main = getParentGUI();
    if (main != null) {
      // not main view
      return main.getButtonActionMappping(button);
    } else {
      if (buttonActionMap != null)
        return buttonActionMap.get(button);
      else
        return null;
    }
  }
  
  /**
   * @requires 
   *  {@link #getButtonActionMappping(JButton)}<tt>(button) = null</tt>
   * @effects
   *  if this not main view
   *    call <tt>mainView</tt>.{@link #addButtonActionMapping(JButton, ButtonAction)}
   *  else
   *    add entry <tt>(button, buttonAction)</tt> to {@link #buttonActionMap}
   * @version 3.1
   */
  private void addButtonActionMapping(JButton button, ButtonAction buttonAction) {
    View main = getParentGUI();
    if (main != null) {
      // not main view
      main.addButtonActionMapping(button, buttonAction);
    } else {
      if (buttonActionMap == null) buttonActionMap = new HashMap();
      
      buttonActionMap.put(button,  buttonAction);
    }
  }
  
  /**
   * @effects 
   *  create and return a user-interactable, <code>JLabel</code> object to be used for a container component, 
   *  whose GUI configuration is
   *  <code>itemConfig</code>, whose GUI style configuration is
   *  <code>configStyle</code>, and whose wrap setting is <tt>wrapped</tt>.
   *  
   *  <p>Update label based on the visibility of the associated container (<tt>containerVisible</tt>)
   */
  public JLabel createContainerLabel(final Region itemConfig, 
      final Style configStyle, boolean wrapped, boolean containerVisible) {
    // create a label
    JLabel label = createLabel(itemConfig, configStyle, true, wrapped, true, true);
    
    // decorate it specially for container
    // e.g. add an arrow icon at the end to prompt the user to interact with it
    ImageIcon iconContainerLabel = GUIToolkit.getImageIcon("containerclose.gif", label.getText());
    label.setIcon(iconContainerLabel);
    label.setHorizontalTextPosition(
        //SwingConstants.LEADING  // text before image
        SwingConstants.TRAILING
        );  
    
    GUIToolkit.updateContainerLabelOnVisibilityUpdate(label, containerVisible);
    
    return label;
  }
  
  // v3.0
//  /**
//   * @effects 
//   *  create a user-interactable, <code>JLabel</code> object whose GUI configuration is
//   *  <code>itemConfig</code> and whose GUI style configuration is
//   *  <code>configStyle</code>.
//   *  
//   *  return the label object
//   * @deprecated version 2.7.4          
//   */
//  public JLabel createLabel(final Region itemConfig, 
//      final Style configStyle) {
//    return createLabel(itemConfig, configStyle, true, true, true, true);
//  }

  /**
   * @param textDisplay 
   * @param iconDisplay 
   * @effects 
   *  create a standard <code>JLabel</code> whose GUI configuration is
   *  <code>itemConfig</code> and whose GUI style configuration is
   *  <code>regionStyle</code>.
   *  
   *  <br>if <tt>wrapped = true</tt>
   *    create a label that can display text on multiple lines.  
   *    
   *  <br>if <tt>userInteractable = true</tt> 
   *      add this.inputHandler to listen to label's mouse event
   *      
   *  <br>return the label object
   */
  public JLabel createStandardLabel(final Region itemConfig, 
      final Style regionStyle, 
      boolean userInteractable, 
      boolean wrapped, 
      boolean iconDisplay, boolean textDisplay  // v3.0
      ) {
    //return createLabel(itemConfig, configStyle, true, true);
    String name = itemConfig.getName(); 
    Label labelObj = itemConfig.getLabel();
    String lbl = null;

    /*v2.7: support label style 
    if (labelObj != null)
      lbl = labelObj.getValue();
      */
    Style configStyle;
    if (labelObj != null) {
      lbl = labelObj.getValue();
      configStyle = labelObj.getStyle();  // use label style
      if (configStyle == null) configStyle = regionStyle;
    } else {
      // uses region style instead
      configStyle = regionStyle;
    }
    
    ImageIcon icon = itemConfig.getImageIconObject();
    
    Font font = null;
    Color fg = null;
    Color bg = null;
    
    if (configStyle != null) {
      font = GUIToolkit.getFontValue(configStyle.getFont()); 
      fg = GUIToolkit.getColorValue(configStyle.getFgColor());
      bg = GUIToolkit.getColorValue(configStyle.getBgColor());
    }

    JLabel label;

    // v2.7.4: support display class
    Class displayClass = itemConfig.getDisplayClassType();
    
    /* v2.7.4: support custom display type
    if (wrapped) { 
      label = new domainapp.basics.view.swing.JHtmlLabel(lbl);
    } 
    */
    if (displayClass != null && JLabel.class.isAssignableFrom(displayClass)) {
      try {
        label = (JLabel) displayClass.newInstance();
        label.setText(lbl);
      } catch (InstantiationException | IllegalAccessException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
            e, displayClass, "");
      }
    } else if (wrapped) { 
      label = new JHtmlLabel(lbl);
    } else { 
      label = new JLabel(lbl);
    }
    
    // v3.0: support textDisplay option
    if (!textDisplay) {
      label.setText(null);
      label.setToolTipText(lbl);
    }
    
    if (icon != null) {
      // v3.0: label.setIcon(icon);
      if (iconDisplay)
        label.setIcon(icon);
    }
    
    if (font != null) {
      label.setFont(font);
    }
    if (fg != null) {
      label.setForeground(fg);
    }

    if (bg != null) {
      label.setOpaque(true);
      label.setBackground(bg);
    }

    // if region is a state change listener then register  
    if (itemConfig.getIsStateListener()) {
      registerStateListener((StateChangeListener)label);
    }
    
    // put into the compMap
    compMap.put(itemConfig, label);

    if (userInteractable) {
      label.addMouseListener(controller.getInputHelper());
    }
    
    // standardise label border (if it has not got one). 
    // Border standardisation is needed to avoid the label texts to appear unaligned on the form 
    // when they are highlighted upon user moving the mouse over them. This highlighting uses a custom border 
    Border border = label.getBorder();
    
    // FIXME: this check provides a temporary solution for not overriding labels with Line-typed
    // border (e.g. JObjectScroll). Labels created with other types of border will still 
    // be overriden by this.
    // 
    if (!(border instanceof LineBorder))
      GUIToolkit.highlightComponentOnFocus(label, false);

    return label;
  }

  /**
   * @param textDisplay 
   * @param iconDisplay 
   * @effects 
   *  create a new <code>JLabel</code> object whose GUI configuration is
   *  <code>itemConfig</code> and whose GUI style configuration is
   *  <code>configStyle</code>.
   *  
   *  if userInteractable = true 
   *    register input handler of this.controller as a mouse handler of the label
   *    
   *  return the label object
   *          
   */
  public JLabel createLabel(final Region itemConfig, 
      /*v2.7: final Style configStyle, */ 
      final Style regionStyle,
      final boolean userInteractable, 
      final boolean wrapped, 
      boolean iconDisplay, boolean textDisplay  // v3.0
      ) {
    // v2.7.4: 
    JLabel label = createStandardLabel(itemConfig, regionStyle, userInteractable, wrapped
        , iconDisplay, textDisplay  // v3.0
        );
    

    // v2.7.4: width, height
    if (itemConfig.isSizeConfigured()) {
      int width = itemConfig.getWidth(), height = itemConfig.getHeight();

      label.setPreferredSize(new Dimension(width, height));
    }

    // v2.7.4: alignment X
    AlignmentX alignX = itemConfig.getAlignX();
    if (alignX != null) {
      int aX = GUIToolkit.toSwingAlignmentX(alignX);
      label.setHorizontalAlignment(aX);
    }

    return label;
  }

  /**
   * This method is the short cut for {@link GUIToolkit#updateContainerLabelOnVisibilityUpdate(JLabel, boolean)} 
   * 
   * @effects 
   *  if the style of <tt>container</tt>'s <tt>label</tt> style does not match <tt>container</tt>'s visibility
   *    update <tt>label</tt>'s style to match
   *  else
   *    do nothing
   * @requires 
   *  this is the <b>main</b> gui
   * 
   * @version
   * - 5.2: updated to take visibility as input
   */
  public void updateContainerLabelOnVisibilityUpdate(JDataContainer container,
      final boolean visibility // v5.2
      ) {
    // look up the container label in this
    JDataContainer parent = container.getParentContainer();
    
    if (parent == null) {
      // container is in the root panel
      View user = container.getController().getUserGUI();
      parent = user.getRootContainer();
    } else {
      // container is nested inside the parent
    }
    
    JLabel containerLabel = parent.getLabelFor(container.getGUIComponent());
    
    if (containerLabel != null) {
      //v5.2: boolean visible = container.isVisible();
      GUIToolkit.updateContainerLabelOnVisibilityUpdate(containerLabel, visibility);
    }
  }
  
  /**
   * @effects 
   *  if <tt>label</tt>'s style does not match <tt>compVisible</tt>
   *  (the visibility status of the component to which <tt>label</tt> is associated)
   *    update <tt>label</tt>'s style to match
   *  else
   *    do nothing
   * @requires 
   *  this is the <b>main</b> gui
   * @version 3.0
   *  - changed to static
   * @deprecated Use {@link GUIToolkit#updateContainerLabelOnVisibilityUpdate(JLabel,boolean)} instead
   */
  public static void updateContainerLabelOnVisibilityUpdate(JLabel label, boolean compVisible) {
    GUIToolkit.updateContainerLabelOnVisibilityUpdate(label, compVisible);
  }
  
  /**
   * @effects returns a <code>JComponent</code> whose configuration is specified
   *          in <code>itemConfig</code>.
   */
  protected JComponent createTextDataField(final Region itemConfig,
      final Style styleConfig) {
    String name = itemConfig.getName(); //(String) itemConfig.get("name");
    String lbl = itemConfig.getLabel().getValue(); //(String) itemConfig.get("label");
    Integer w = itemConfig.getWidth(); //itemConfig.getIntegerValue("width", -1);
    String val = itemConfig.getDefValue(); //itemConfig.getStringValue("defvalue");
    
    // v2.6.4b: support editable
    boolean editable = itemConfig.getEditable();
    
    // TODO: use style config
    JDataField tf = 
        DataFieldFactory.createTextField(
            //v2.6.4.a: use data validator
            controller.getDataValidatorInstance(),
            //controller.getDomainSchema(), 
            controller.getConfig(), 
            null,
            // v2.6.4b
            null,
            editable
            );
    
    if (val != null) {
      tf.setValue(val);
    }

    JTextField txtf = (JTextField) tf.getGUIComponent();

    if (w != null) {
      txtf.setColumns(w.intValue());
    }

    // if region is a state change listener then register  
    if (itemConfig.getIsStateListener()) {
      registerStateListener((StateChangeListener)tf);
    }
    
    if (debug)
      System.out.println("Text data field " + name + " config: " + itemConfig);

    // put into the compMap
    compMap.put(itemConfig, tf);

    return tf;
  }

  /**
   * @effects 
   *  create and return a JCheckBox object whose configuration is 
   *  specified by <tt>itemConfig</tt> and whose style is specified by 
   *  <tt>styleConfig</tt>
   */
  private JComponent createCheckBoxItem(final Region itemConfig,
      final Style styleConfig, 
      boolean iconDisplay, boolean textDisplay // v3.0
      ) throws NotPossibleException {
    String name = itemConfig.getName(); 
    String lbl = itemConfig.getLabel().getValue(); 
    Integer w = itemConfig.getWidth(); 
    String val = itemConfig.getDefValue(); 
    Boolean enabledB = itemConfig.getEnabled();
    boolean enabled = (enabledB != null) ? enabledB : true;
    
    // find view class and constructor method to create object
    JCheckBox check = null;
    
    String displayClass = itemConfig.getDisplayClass();
    if (displayClass != null) {
      Class viewClass = null;
      Constructor cons = null;
      try {
        viewClass = Class.forName(displayClass);
        cons = viewClass.getDeclaredConstructor(ControllerBasic.class);
        check = (JCheckBox) cons.newInstance(controller);
      } catch (ClassNotFoundException e ) {
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
            e, "Không có lớp {0}", displayClass);
      } catch (NoSuchMethodException e ) {
        throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND,
            e, "Không có phương thức {0}", "init(Controller)");
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
            e, "Không thể thực thi phương thức {0}", "newInstance()");
      }
    } else {
      // use the default Swing check box
      check = new JCheckBox();
    }
    
    // retrieve button style
    Style bstyle = controller.getStyleSettings(itemConfig);

    if (debug)
      System.out.println("Check item " + name + " config: " + itemConfig
          + "; style: " + bstyle);
    
    check.setActionCommand(name);
    
    if (val != null && val.equals("true")) {
      check.setSelected(true);
    }

    ImageIcon icon = itemConfig.getImageIconObject();

    // TODO: suport icon??
    
    /* v3.0
    check.setText(lbl);
    */
    if (textDisplay) {
      check.setText(lbl);
    } else {
      check.setToolTipText(lbl);
    }
    
    Integer width = itemConfig.getWidth(); 
    Integer height = itemConfig.getHeight(); 
    if (width != null && height != null)
      check.setMaximumSize(new Dimension(width, height));

    Font font = GUIToolkit.getFontValue(bstyle.getFont()); 
    if (font != null) {
      check.setFont(font);
    }
    check.setBackground(GUIToolkit.getColorValue(bstyle.getBgColor()));
    check.setForeground(GUIToolkit.getColorValue(bstyle.getFgColor()));

    check.setEnabled(enabled);

    // if region is a state change listener then register  
    if (itemConfig.getIsStateListener()) {
      registerStateListener((StateChangeListener)check);
    }

    // put into the compMap
    compMap.put(itemConfig, check);

    return check;
  }
  
  /**
   * @requires
   *  styleConfig != null /\ comp != null
   * @effects
   *  changes the display style (e.g. font, colour, etc.) of <tt>comp</tt> based on the 
   *  style specification in <tt>styleConfig</tt> 
   */
  public void setComponentStyle(Style styleConfig, JComponent comp) {
    Font font = null;
    Color fg = null;
    Color bg = null;
    
    font = GUIToolkit.getFontValue(styleConfig.getFont()); 
    fg = GUIToolkit.getColorValue(styleConfig.getFgColor());
    bg = GUIToolkit.getColorValue(styleConfig.getBgColor());

    if (font != null) {
      comp.setFont(font);
    }
    if (fg != null) {
      comp.setForeground(fg);
    }

    if (bg != null) {
      comp.setOpaque(true);
      comp.setBackground(bg);
    }
  }
  
  /**
   * @requires this is top-level view
   * @effects initialises {@link #desktop} </code> to be a panel of internal
   *          frames. <br>
   */
  private void createDesktop(Region region) {
    boolean withDesktopToolbar = false;
    // v2.6.4.a: support a custom desktop listener
    DesktopListener dlistener = new DesktopListener();
    JScrollableDesktopPane desktop = new JScrollableDesktopPane(withDesktopToolbar, dlistener);

    w.add(desktop, getGUILocation(region));

    // places into region map
    containerMap.put(region, desktop);
  }


  /**
   * This method creates the {@link #components} panel of a functional GUI. It
   * is a {@see JScrollBar} whose view port is a {@see JPanel}. This panel
   * contains the {@link #components} panel in the <code>CENTRE</code> region of
   * its border layout.
   * 
   * @requires side panel (if configured) is created (see {@link #createSidePanel(Region)}) before this method is invoked.
   * 
   * @effects initialises {@link #components} to be a panel containing a set of
   *          other <code>JComponent</code>s that represent the labels and input
   *          data fields of <code>this.cls</code>.
   */
  private void createComponents(Region componentsRegion) throws NotPossibleException {
    final DSMBasic schema = controller.getDomainSchema();
    final Class cls = controller.getDomainClass();

    // v 2.5.4
    final String rootRegionName = controller.getModuleName();

    // v3.0: support containment tree
    Tree containmentTree = controller.getApplicationModule().getContTreeObj();

    // the Region of the GUI component of this View that will be created
    Region rootContainerRegion = controller.getSettingsForChild(componentsRegion, rootRegionName);
    
    // get all child regions configured for this components region
    //v3.0: support module's state scope
    // List<Region> childRegions = controller.getSettings(rootContainerRegion, dfConfigFilterByVisible);
    List<Region> childRegions;
    String stateScope = null;
    if (containmentTree != null) 
      stateScope = containmentTree.getRoot().getTagAsString();
    
    if (stateScope != null) {
      // module's state scope is specified: use it
      dfConfigFilterByStateScope.setStateScope(stateScope);
      childRegions = controller.getSettings(rootContainerRegion, dfConfigFilterByStateScope);        
    } else { 
      // no module state scope: use visibility filter
      childRegions = controller.getSettings(rootContainerRegion, dfConfigFilterByVisible);
    }

    // v2.7.2: create the root container first
    rootContainer = createRootContainer(rootContainerRegion, childRegions,
        containmentTree);
    
    /*
    // create the components and add them to the top-level panel (but do not yet create
    //  it). This may involve creating nested lower-level panels.
     createDataPanel((DefaultPanel) rootContainer, cls, configs, null);
    // create the layout (after all the components have been added)
    rootContainer.createLayout();
    */
    containerMap.put(Components, rootContainer.getGUIComponent());
    
    // the container used for components
    JPanel container = new JPanel(new BorderLayout());

    // set up bg & border
    container.setBackground(Color.WHITE);
    container.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(2, 2, 2, 2),
        BorderFactory.createLineBorder(Color.LIGHT_GRAY)));

    // create the two GUI components for the domain class
    // one is title and the other is a data panel

    // the title panel
    Region titleCfg = controller.getSettingsForChild(rootContainerRegion, "title");
    if (titleCfg != null) {
      JComponent title = createTitlePanel(titleCfg);
      title.setName(titleCfg.getName());
      container.add(title, BorderLayout.NORTH);
    }
    
    // registers the data controller of the top-level data panel to the
    // object actions
    ControllerBasic.DataController rootDctl = rootContainer.getController();
    registerDataController(rootDctl);

    /**
     * create the components and add them to the top-level panel (but do not yet create
     * it). This may involve creating nested lower-level panels.
     */
    // add it to the CENTER
    //v2.7.2: container.add(rootComponentsPanel, BorderLayout.CENTER);
    container.add(rootContainer.getGUIComponent(), BorderLayout.CENTER);

    // finally, put components into a JScrollBar
    JScrollPane js = new JScrollPane(container);
    
    // v5.2: support side panel
    // w.add(js, getGUILocation(componentsRegion));
    addComponentsRegionToWindow(js, getGUILocation(componentsRegion));
    // end v5.2
  }

  /**
   * This method creates the side panel of a functional GUI, if it is specified and adds both the side panel 
   * and <tt>compPane</tt> to this split pane and then add the split pane to {@link #w}. 
   * Which part (left or right) that contains the side panel
   * depends on the alignment property of the side-panel region.
   *   
   * @effects 
   *  if exists a side panel region in this
   *    create the panel (using {@link #createSidePanel()}), 
   *    create a {@link JSplitPane} to contain this panel and <tt>compPane</tt> 
   *    and add the split pane to {@link #w} at <tt>location</tt>
   *  else
   *    add <tt>compPane</tt> to {@link #w} at <tt>location</tt>
   *  
   * @version 5.2 
   */
  private void addComponentsRegionToWindow(Component compPane, String location) {
    final String moduleName = controller.getModuleName();

    // is the side-panel Region of this module exist?
    Region mySidePanelReg = null; 
    try {
      String mySidePanelRegName = RegionConstants.genSidePaneRegionNameForModule(moduleName);
      mySidePanelReg = controller.getSettingsForChild(SidePane, mySidePanelRegName);
    } catch (NotFoundException e) {
      // no side-panel region
    }
    
    if (mySidePanelReg != null) {
      // a side-panel is configured for this view

      // First, create a 2-part SplitPane with the compPane and add it to w.
      // IMPORTANT: this is needed for side-panel to obtain label of the title panel!!
      JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      splitPane.setOneTouchExpandable(true);
      splitPane.setDividerLocation(-1); // 150

      AlignmentX loc = mySidePanelReg.getAlignX();
      if (loc.equals(AlignmentX.Left)) {
        splitPane.setRightComponent(compPane);
      } else {
        splitPane.setLeftComponent(compPane);
      }
      
      w.add(splitPane, location);
      
      // now create the side panel
      Component sidePanel = createSidePanel();

      // then set it into the split pane
      if (loc.equals(AlignmentX.Left)) {
        splitPane.setLeftComponent(sidePanel);
      } else {
        splitPane.setRightComponent(sidePanel);
      }
    } else {
      // no side panel: only the components region
      w.add(compPane, location);
    }
  }

//  /**
//   * @effects
//   *  initialise the chart panel (ready to contain chart objects)
//   */
//  private void createChartPanel(Region region) {
//    DefaultChartPanel chartPanel = new DefaultChartPanel();
//    
//    // hide chart panel at first
//    chartPanel.setVisible(false);
//    
//    String location = getGUILocation(region);
//    
//    containerMap.put(region, chartPanel);
//    
//    w.add(chartPanel, location);
//  }
  
  /**
   * @effects 
   *  if exists side-panel's region as a child of SidePane
   *    create a {@link JPanel} for the side panel and return it
   *  ; else
   *    return null
   * @version 5.2
   */
  private Component createSidePanel() throws NotPossibleException {
    final String moduleName = controller.getModuleName();

    // is the side-panel Region of this module exist?
    String mySidePanelRegName = RegionConstants.genSidePaneRegionNameForModule(moduleName);
    
    Region mySidePanelReg = null; 
    try {
      mySidePanelReg = controller.getSettingsForChild(SidePane, mySidePanelRegName);
      
      if (mySidePanelReg != null) {
        // a side-panel is configured for this view

        // use panel builder to build it
        ViewBuilder sidePanelBuilder = lookUpViewBuilder(SidePane);
        if (sidePanelBuilder == null) {
          throw new NotPossibleException(NotPossibleException.Code.NO_VIEW_BUILDER_SPECIFIED_WHEN_REQUIRED, 
              new Object[] {mySidePanelRegName});
        }
        
        Component sidePanelComp = sidePanelBuilder.build(this, controller.getConfig());
        
        // set up bg & border
        sidePanelComp.setBackground(Color.WHITE);
        if (sidePanelComp instanceof JComponent) {
          ((JComponent) sidePanelComp).setBorder(GUIToolkit.PANEL_BORDER);
        }
        
        // put sidePanelComp into a scrollbar
        JScrollPane jsSidePanel = new JScrollPane(sidePanelComp);
        
        containerMap.put(SidePane, sidePanelComp);
        
        return jsSidePanel;
      } else {
        return null;
      }
    } catch (NotFoundException e) {
      // no side-panel region -> exit
      return null;
    }
  }
  
  /**
   * @effects 
   *    if exists {@link ViewBuilder} class configured in {@link #guiConfig} for <tt>region</tt>
   *      return an instance of it
   *    else
   *      return null
   * @version 5.2
   */
  private ViewBuilder lookUpViewBuilder(Region region) throws NotPossibleException {
    String regName = region.getName();
    
    PropertyName builderPropName = PropertyName.lookUpByName(PropertyName.prefix_view_builder, regName);
    
    Class builderCls = guiConfig.getProperty(builderPropName, Class.class, null);
    
    if (builderCls == null) return null;
    
    try {
      return (ViewBuilder) builderCls.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
          e, new Object[] {builderCls, ""});
    }
  }

  /**
   * @effects 
   *  create a <tt>TitlePanel</tt> that contains the title text and (possibly) other 
   *  header-related components (e.g. logo). The components are as specified 
   *  in <tt>region</tt>.
   */
  protected TitlePanel createTitlePanel(Region region) throws NotFoundException {
    Class displayClass = null;
    
    displayClass = region.getDisplayClassType();
    
    if (displayClass == null) {
      // the default
      displayClass = TitlePanel.class;
    }
    
    // create the title panel
    Style styleConfig = controller
        .getStyleSettings(region);
    
    // create the title label
    // v2.6.4.a: added interactable option
    //JLabel title = createLabel(region, styleConfig, false, false);
    boolean userInteractable = true; 
    JLabel title = createLabel(region, styleConfig, userInteractable, false, true, true);
    
    // centrally aligned
    title.setHorizontalAlignment(JLabel.CENTER);
    
    // create the title panel containing the title label and (possibly)
    // some other ones
    TitlePanel titlePanel;
    
    try {
      Constructor<TitlePanel> cons = displayClass.getConstructor(View.class,
          JLabel.class);
      titlePanel = cons.newInstance(this, title);
    } catch (Exception e) {
      throw new NotFoundException(
          NotFoundException.Code.CONSTRUCTOR_METHOD_NOT_FOUND,
          e, new Object[] {displayClass, View.class + "," + JLabel.class});
    }

    return titlePanel;
  }

  /**
   * @requires <tt>
   *  containmentTree != null => containmentTree is the containment-tree of the user-module of dctl
   *  </tt>
   * 
   * @effects 
   *  create and return a JDataContainer for the specified arguments. 
   *  
   *  <p>throws NotImplementedException if containerClass is not supported.
   * 
   * @version 2.7.2
   * @param containmentTree 
   */
  private JDataContainer createContainerComponent (
      String name, 
      DAttr containerAttrib,
      Class containerClass,
      Region containerRegion, 
      DataController dctl,
      Style style,
      List<Region> compConfigs, 
      DefaultPanel parent, 
      Tree containmentTree    // v3.0
      ) throws NotImplementedException {
    DSMBasic schema = controller.getDomainSchema();
    Class cls = controller.getDomainClass();
    
    JDataContainer container;
    if (DefaultPanel.class.isAssignableFrom(containerClass)) {
      // a type of DefaultPanel
      try {
        DefaultPanel panel = DefaultPanel.createInstance(containerClass, containerRegion, dctl, name, parent);

        // add components to panel
        createDataPanelComponents(panel, cls, compConfigs, null, containmentTree);
        // layout the panel
        panel.createLayout();
        
        container = panel;
      } catch (Exception e) {
        // could not create 
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT,
            e, 
          "Không thể tạo đối tượng lớp: {0}({1})", containerClass, dctl, name, parent);        
      }
    } else {
      // a type of table 
      boolean tableType = false; 
      
      try {
        tableType = (containerClass.asSubclass(JDataTable.class) != null);
      } catch (ClassCastException e) {
        // not a table
      }

      if (tableType) {
        //v3.3: support containment tree for table 
        Collection<DAttr> fieldConstraints = schema.getAttributeConstraints(cls, null);
        container = createTableComponent(containerRegion, name, containerClass, cls, dctl, null, 
            fieldConstraints, compConfigs, parent, containmentTree);
        
      } else {
        // not a table
        throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
          "Không hỗ trợ tính năng {0}", containerClass);
      }
    }
    
    // v2.7.2: add this check to make sure that nested containers always have a link attribute 
    if (parent != null) {
      if (container.getLinkAttribute() == null) {
        throw new ApplicationRuntimeException(null, "AppGUI.createDataContainer: no link attribute \n  to parent: "+parent+"\n  for child: " + container);
      }
    }    

    // v2.7.2: support print config
    PropertySet printCfg = getContainerPrintConfig(containerAttrib);
    container.setContainerPrintConfig(printCfg);

    // set up style
    JComponent containerGui = container.getGUIComponent();
    
    //if (guiCfg != null && guiCfg.getStyle() != null) {
    if (style != null) {
      //Style style = controller.getStyleSettings(guiCfg);
      Font font = GUIToolkit.getFontValue(style.getFont());
      Color fg = GUIToolkit.getColorValue(style.getFgColor());
      Color bg = GUIToolkit.getColorValue(style.getBgColor());
      
      if (font != null) {
        containerGui.setFont(font);
      }
      
      if (fg != null) {
        containerGui.setForeground(fg);
      }
      
      if (bg != null) {
        containerGui.setOpaque(true);
        containerGui.setBackground(bg);
      }
    }
    
    return container;
  }
  
  /**
   * @requires <tt>
   *  containmentTree != null => containmentTree is the containment-tree of the user-module of parentPanel.controller
   * </tt>
   * @effects 
   *  created a <tt>JDataContainer</tt> to be used as a sub-container of <tt>parentPanel</tt>, 
   *  whose displayClass is <tt>displayClass</tt>,
   *  domain constraint is <tt>containerAttrib</tt>, referenced domain class is <tt>domainType</tt>, 
   *  configuration is <tt>containerCfg</tt>
   * @version 
   * - 3.2.3: improved to support the specification of a sub-type of domainType in containment tree
   */
  // v3.1: add return type
  //private void createSubContainerComponent(
  private JDataContainer createSubContainerComponent(
      String name,
      Class displayClass, 
      DAttr containerAttrib, 
      Class domainType, Region containerCfg, 
      DefaultPanel parentPanel, 
      Tree containmentTree // v3.0
      ) {
    JDataContainer dcont = null;

    /*v3.2: 
     * - if a sub-type of domainType is specified in containment tree then use it instead of domainType to create 
     *    the sub-container
     * - support the use of extended scope definition 
     */
    Class parentCls = parentPanel.getController().getDomainClass();
    Class subCls = null;
    if (containmentTree != null) {
      DSMBasic dsm = controller.getDomainSchema();

      subCls = SwTk.findDescendantTypeInTree(dsm, containmentTree, domainType, parentCls);
    }
    
    if (subCls != null) {
      // replace domainType by the sub-type
      domainType = subCls;
    }

    // get the attribute view configs of the module of the specified type
    List<Region> attribViewConfigs = controller.getReferralSettings((RegionLinking) containerCfg, 
        domainType, 
        dfConfigFilterByVisible);
    
    ScopeDef childScopeDef = null;  // v3.2
    if (containmentTree != null) {
      // containment tree is specified 
      // if exist a scope definition for
      // this sub-container then filter compConfigs to remove those not in this scope
      /*v3.2: support extended scope def
      filterViewConfigsByContainmentScope(containmentTree, parentCls, domainType, attribViewConfigs);
      */
      childScopeDef = filterViewConfigsByContainmentScope(containmentTree, parentCls, domainType, attribViewConfigs);
      
      if (childScopeDef != null) {
        // update containerCfg to contain mapping (module,childScopeDef), this mapping is used latter to configure
        // run-time properties of the container
        containerCfg.addUserModuleScope(controller.getApplicationModule(), childScopeDef);
        
        // get scope-specific display class (if any)
        Class scopeDisplayCls = childScopeDef.getDisplayClass();
        if (scopeDisplayCls != null) { 
          displayClass = scopeDisplayCls;
        }
      }
    }
    // end 3.2
    
    if (DefaultPanel.class.isAssignableFrom(displayClass)) { 
      // a panel
      // get the GUI configurations for this panel
      dcont = createSubDataPanel(name, containerAttrib, domainType, containerCfg, attribViewConfigs, displayClass, parentPanel, containmentTree);
    } else { 
      // data table
      dcont = createSubTable(name, displayClass, containerAttrib, domainType, containerCfg, attribViewConfigs, parentPanel, containmentTree);
    }
    
    // v2.7.2: add this check to make sure that nested containers always have a link attribute
    if (parentPanel != null) {
      if (dcont.getLinkAttribute() == null) {
        throw new ApplicationRuntimeException(null, "AppGUI.createDataContainer: no link attribute \n  to parent: "+parentPanel+"\n  for child: " + dcont);
      }
    }    
    
    // v2.7.2: support print config
    PropertySet printCfg = getContainerPrintConfig(containerAttrib);
    dcont.setContainerPrintConfig(printCfg);
    
    // v2.6.c: hide the container 
    dcont.setVisible(false);
    
    // TODO: move this code to DataController.setDataContainer if needs to support 
    // app state listener for top-level data container
    // v3.1: support for app state listener of the data container
    DataController dctl = dcont.getController();
    DataControllerCommand appStateCommand = dctl.lookUpCommand("AppStateEventHandler");
    if (appStateCommand != null && (appStateCommand instanceof StateChangeListener)) {
      StateChangeListener appStateHandler = (StateChangeListener) appStateCommand;
      // register it to listen to app state events of the dcont's data controller
      dctl.getCreator().addApplicationStateChangedListener(appStateHandler, appStateHandler.getStates());
    }

    return dcont;
  }
  
  /**
   * @effects 
   *  if containerAttrib is null
   *    return the print configuration of this.module  
   *    or return null if no such configuration is defined
   *  else
   *    return the print configuration of the data field associated to containerAttrib
   *    or return null if no such configuration is defined  
   *    
   */
  private PropertySet getContainerPrintConfig(DAttr containerAttrib) {
    PropertySet modulePrintConfig = controller.getApplicationModule().getPrintConfig();
    
    if (modulePrintConfig == null) return null; // v2.7.3
    
    if (containerAttrib == null) {
      return modulePrintConfig;
    } else {
      String attribName = containerAttrib.name();
      PropertySet printfConfig = modulePrintConfig.getExtension(attribName);
      if (printfConfig != null)
        return printfConfig;
      else
        return null;
    }
  }

  /**
   * @requires <tt>
   *  containmentTree != null => containmentTree is the containment-tree of the user-module of parentPanel.controller
   * </tt>
   * @effects 
   *  create a sub <tt>DefaultPanel</tt> of <tt>parentPanel</tt> for the
   *  domain attribute of the domain class <tt>domainType</tt>, whose domain constraint is <tt>containerAttrib</tt> and gui configuration 
   *  is <tt>containerCfg</tt>. 
   */
  private DefaultPanel createSubDataPanel(
      String name, 
      DAttr containerAttrib, 
      Class domainType, Region containerCfg, 
      List<Region> attribViewConfigs, // v3.2
      Class displayClass,     // v2.7.2
      DefaultPanel parentPanel, 
      Tree containmentTree  // v3.0
      ) {
    final DSMBasic schema = controller.getDomainSchema();
    final DataController parentDCtl = parentPanel.getController();

    // get the filtered attributes (if any)
    Select filter = containerAttrib.filter();
    String[] filterElements = filter.attributes();
    if (filterElements.length == 0)
      filterElements = null;
    
    Collection<DAttr> attribsOfInterest = schema.getAttributeConstraints(domainType, filterElements);

    // the creator of the data controller of the component
    ControllerBasic creator = controller.lookUp(domainType);
    
    // v3.0: added this check
    if (creator == null)
      throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND, new Object[] {domainType.getSimpleName()});
    
    View creatorGUI = creator.getGUI();
    
    // get the GUI configurations for this panel
    // create a new data controller for this panel and registers it
    DataController dctl = ControllerBasic.createChildDataController(
        controller.getDodm(),
        controller.getConfig(), 
        ((RegionLinking) containerCfg), 
        SysConstants.DEFAULT_PANEL_DATA_CONTROLLER,
        creator, controller, parentDCtl);
    
    Style style = null;
    
    style=controller.getStyleSettings(containerCfg);
    
    DefaultPanel nestedPanel = DefaultPanel.createInstance(displayClass, containerCfg, dctl, name, parentPanel);

    
    // set up the panel
    if (style != null) {
      setSubContainerStyle(nestedPanel, style);
    }

    // create data field components in the panel
    createDataPanelComponents(nestedPanel, domainType, attribViewConfigs, attribsOfInterest, containmentTree); 

    // layout the components on the GUI
    nestedPanel.createLayout();

    // v2.7.4
    registerDataController(dctl);

    creator.putDataController(nestedPanel, dctl);

    // add the panel to region map
    //containerMap.put(nestedPanel.getName(), nestedPanel);
    containerMap.put(containerCfg, nestedPanel);

    // create a labelled nested panel
    // the nested panel's label is used as the title panel
    Region titleCfg = controller.getSettingsForChild(containerCfg, "title");
    Style styleCfg = controller.getStyleSettings(titleCfg);
    JLabel label = createContainerLabel(titleCfg, styleCfg, true, false);

    label.setLabelFor(nestedPanel);
    
    // label
    parentPanel.addLabelledComponent(containerAttrib, containerCfg, label, nestedPanel);
    
    return nestedPanel;
  }
  
  /**
   * This method is similar to {@link #createSubDataPanel(String, DAttr, Class, Region, List, Class, DefaultPanel, Tree)} except 
   * that the container that it creates is a {@link JDataTable} instead of a {@link DefaultPanel}.
   * 
   * @requires <tt>
   *  containmentTree != null => containmentTree is the containment-tree of the user-module of parentPanel.controller
   * </tt>
   * 
   * @version 
   * - 3.2: add attribViewConfigs parameter
   */
  private JDataTable createSubTable(String name, Class displayClass, DAttr containerAttrib, 
      Class domainType, Region containerCfg, 
      List<Region> attribViewConfigs, // v3.2
      DefaultPanel parentPanel, 
      Tree containmentTree    // v3.0
      ) {
    final DSMBasic schema = controller.getDomainSchema();
    final ControllerBasic.DataController parentDCtl = parentPanel.getController();

    Select filter = containerAttrib.filter();
    String[] filterElements = filter.attributes();
    if (filterElements.length == 0)
      filterElements = null;
    
    Collection<DAttr> attribsOfInterest = schema
        .getAttributeConstraints(domainType, filterElements);
    
    JComponent[] comps;

    // the creator of the data controller of the component
    ControllerBasic creator = controller.lookUp(domainType);
    
    if (creator == null) {  // v2.7.4
      throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND, new Object[] {domainType.getSimpleName()});
    }
    
    DataController dctl = ControllerBasic.createChildDataController(
        controller.getDodm(),
        controller.getConfig(), 
        ((RegionLinking) containerCfg),
        SysConstants.DEFAULT_TABLE_DATA_CONTROLLER, 
        creator, controller, parentDCtl);
    
    // create the table
    
    // v3.3: support containment tree
    JDataTable table = createTableComponent(containerCfg, name, displayClass, domainType, dctl, containerAttrib,
        attribsOfInterest, attribViewConfigs, parentPanel, containmentTree);

    // v3.2: support styling
    Style style = null;
    style=controller.getStyleSettings(containerCfg);
    if (style != null)
      setSubContainerStyle(table, style);
   
    // register control
    registerDataController(dctl);

    creator.putDataController(table, dctl);

    /* v2.6.4b: changed to getGUIComponent*/
    JComponent tableGUI = table.getGUIComponent();

    // add the table to region map
    containerMap.put(containerCfg, tableGUI);
    
    //v2.7.2: create the label for the table
    Region titleCfg = controller.getSettingsForChild(containerCfg, "title");
    String label = titleCfg.getLabel().getValue(); 
    Style titleStyleCfg = controller.getStyleSettings(titleCfg);
    // v2.6.c: change to container label
    //JLabel labelComp = createLabel(titleCfg, titleStyleCfg); 
    JLabel labelComp = createContainerLabel(titleCfg, titleStyleCfg, true, false);
    labelComp.setLabelFor(table);
    
    //parentPanel.addLabelledComponent(co, cfg, comps[0], tableGUI);
    parentPanel.addLabelledComponent(containerAttrib, containerCfg, labelComp, tableGUI);
    
    return table;
  }

//v3.2: removed
//private DefaultPanel createDataPanel(
//    //Region guiCfg,
//    Region containerCfg, 
//    Style style,
//    DataController dctl, 
//    String name, 
//    Class displayClass,   // v2.7.2
//    DefaultPanel parent) throws NotPossibleException {
//  DefaultPanel panel = DefaultPanel.createInstance(displayClass, containerCfg, dctl, name, parent);
//
//  
//  // set up the panel
//  //if (guiCfg != null && guiCfg.getStyle() != null) {
//  if (style != null) {
//    //Style style = controller.getStyleSettings(guiCfg);
//    setSubContainerStyle(panel, style);
///* v3.2: moved to shared method 
//    Font font = GUIToolkit.getFontValue(style.getFont());
//    Color fg = GUIToolkit.getColorValue(style.getFgColor());
//    Color bg = GUIToolkit.getColorValue(style.getBgColor());
//    
//    if (font != null) {
//      panel.setFont(font);
//    }
//    
//    if (fg != null) {
//      panel.setForeground(fg);
//    }
//    
//    if (bg != null) {
//      panel.setOpaque(true);
//      panel.setBackground(bg);
//    } else {
//      // v3.2: make transparent
//      panel.setOpaque(false);
//    }
//*/    
//  }
//  
//  return panel;
//}


  /**
   * @requires 
   *  style != null
   * @effects 
   *  sets up <tt>container.gui</tt> based on settings in <tt>style</tt>
   *  
   * @version 3.2
   */
  private void setSubContainerStyle(JDataContainer container, Style style) {
    JComponent containerView = container.getGUIComponent();
    
    Font font = GUIToolkit.getFontValue(style.getFont());
    Color fg = GUIToolkit.getColorValue(style.getFgColor());
    Color bg = GUIToolkit.getColorValue(style.getBgColor());
    
    if (font != null) {
      containerView.setFont(font);
    }
    
    if (fg != null) {
      containerView.setForeground(fg);
    }
    
    if (bg != null) {
      containerView.setOpaque(true);
      containerView.setBackground(bg);
    } else {
      // v3.2: make transparent
      containerView.setOpaque(false);
    }
  }

  /**
   * @requires 
   *  containmentTree != null /\ parentCls != null /\ childCls != null 
   *    /\ attribViewConfigs != null /\ attribViewConfigs.size() > 0
   * @modifies <tt>attribViewConfigs</tt>
   * @effects <pre>  
   *  if exist in <tt>containmentTree</tt> a containment scope defined for 
   *  <tt>childCls</tt> of <tt>parentCls</tt>
   *    filter <tt>attribViewConfigs</tt> to remove those not in the scope
   *    
   *    if scope is a {@link ScopeDef}
   *      return it
   *    else
   *      return <tt>null</tt>
   *  else
   *    return <tt>null</tt> 
   *    
   *  <p>Throws NotFoundException of containment scope is a {@link ScopeDef} but this object can not be found.
   *  
   * </pre>
   * @version 
   * - 3.0: created <br>
   * - v3.2: updated to return ScopeDef and throws <br>
   * - 5.1: improved to support RegionLinking, added NotPossibleException
   * 
   * @see {@link SwTk#getContainmentScope(Tree, Class, Class)}
   */
  private ScopeDef filterViewConfigsByContainmentScope(Tree containmentTree,
      Class parentCls, Class childCls, List<Region> attribViewConfigs) throws NotFoundException, NotPossibleException {
    String parentNode = parentCls.getName();
    String childNode = childCls.getName();
    
    Object tag = containmentTree.getEdgeTagByNodeValue(parentNode, childNode);
    
    // v3.2: 
    ScopeDef scopeDef = null;
    
    if (tag != null) {
      // scope is defined
      String scope = tag.toString();
      
      String[] scopeElements;
      if (scope.startsWith(".")) {
        // a ScopeDef
        String scopeDefName = scope.substring(1);
        // retrieve the ScopeDef constant object from the module
        scopeDef = SwTk.getContainmentScopeDefObject(controller, scopeDefName);
        scopeElements = scopeDef.scope();
        if (scopeElements.length == 0 || 
            (scopeElements.length==1 && scopeElements[0].equals("")) 
            || scopeElements[0].equals("*")) {
          // all attributes
          scopeElements = null;
        }
      } else {
        // not a scopeDef
        /* v5.1: support ScopeDesc (RegionLinking) 
        scopeElements = scope.split(",");
        */
        // not a scopeDef: either a comma-separated string of attribute names OR RegionLinking::obj-id (created from ScopeDesc)
        if (SwTk.isObjectId(RegionLinking.class, scope)) {
          // RegionLinking object id: retrieve it to obtain the scope string
          RegionLinking rl;
          try {
            rl = SwTk.retrieveModuleContainmentConfig(controller.getDodm(), scope);
            /* v5.2b: fixed bug 
            String scopeStr = rl.getProperty(PropertyName.module_containment_scope, String.class, "");
            scopeElements = scopeStr.split(",");
            
            scopeDef = new ScopeDef(childCls, scopeElements, rl);
            */
            String scopeStr = rl.getProperty(PropertyName.module_containment_scope, String.class, null);
            if (scopeStr == null) {
              scopeElements = null;
              scopeDef = new ScopeDef(childCls, new String[] {""}, rl);
            } else {
              scopeElements = scopeStr.split(",");
              scopeDef = new ScopeDef(childCls, scopeElements, rl);
            }
            // end 5.2b
          } catch (DataSourceException e) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_OBJECT, e, new Object[] {scope});
          }
        } else {
          // non-object-id
          scopeElements = scope.split(",");
        }
      }
      
      // filter if scopeElements is specified
      if (scopeElements != null) {
        Region attribViewCfg;
        String attribName;
        boolean inScope;
        for (int i = 0; i < attribViewConfigs.size(); i++) {
          attribViewCfg = attribViewConfigs.get(i);
          attribName = attribViewCfg.getName();
          inScope = false;
          for (String scopeEl : scopeElements) {
            if (attribName.equals(scopeEl)) {
              // attrib in scope
              inScope = true; break;
            }
          }
          
          if (!inScope) {
            // attrib not in scope -> remove
            attribViewConfigs.remove(i);
            i--;
          }
        }
      }
    }
    
    return scopeDef;
  }

  /**
   * Use this method to intialise a <code>DefaultPanel</code> object for
   * capturing and presenting data of a given domain class. This method is
   * recursive in that it will create child panels of the given panel if the
   * specified domain class contains collection-type domain attributes (i.e.
   * collections whose elements type is another domain class).
   * 
   * <p>
   * The following intuitive algorithm is used:
   * 
   * <pre>
   * for each cons in the attribute constraints of the panel
   *    Map cfg: the GUI config of the attribute 
   *    if cons is not a domain collection-type then
   *      creates a JDataField from <cons,cfg>
   *    else
   *      if the target domain type of cons does not contain any collection-type domain attributes
   *        create a JDataTable from <cons,cfg>
   *      else
   *        create a child DefaultPanel from <cons,cfg>
   * end for
   * </pre>
   * 
   * <p>
   * A GUI component of a <code>DefaultPanel</code> is either a
   * <code>JDataField</code>, a <code>JDataTable</code> or another
   * <code>DefaultPanel</code> object, depending on the constraint definition of
   * the domain attribute for whom this component is being created (see below
   * for further details). The later two types are sub-types of {@see
   * JDataContainer} -- naturally because they represent containers -- and are
   * created to contain <code>JDataField</code> components and, for the case of
   * <code>DefaultPanel</code>, also other containers.
   * 
   * <p>
   * Every GUI component is created with a label (typed {@see JLabel}), which is
   * set to be the label for the component ( via its <code>setLabelFor()</code>
   * method). For <code>DefaultPanel</code> components, their labels are
   * displayed as a title of the GUI region used for that panel. For the other
   * two types, the labels are displayed right-aligned and to the left of the
   * components.
   * 
   * <p>
   * A domain class contains a collection-type attribute if the domain
   * constraint of the attribute has <code>type=Type.Collection</code>. For
   * example:
   * 
   * <pre>
   * class A {
   *   &#064;DomainConstraint(name = &quot;students&quot;, type = Type.Collection)
   *   List&lt;Student&gt; students;
   * }
   * </pre>
   * 
   * is a collection-type attribute of a domain class <code>A</code>. In this
   * case the type of the collection element is <code>Student</code>. Now, if
   * class <code>Student</code> also has a collection-type attribute, say
   * <code>enrolments</code>, then when creating the <code>DataPanel</code> for
   * <code>A</code>, we need to create a child <code>DefaultPanel</code> for the
   * <code>students</code> attribute. This panel will contain a nested {@see
   * JObjectTable} (a sub-class of <code>JDataTable</code>) for the
   * <code>enrolments</code> attribute. However, if <code>Student</code> does
   * not have any collection-type attributes then we only create
   * <code>JDataField</code> components for each of its attributes.
   * 
   * <p>
   * Thus, child <code>JDataContainer</code>s are (semantically) linked to their
   * parent container via the associated domain attributes (called <b>linked
   * attributes</b>). In the above example, the <code>Student</code> child panel
   * is linked to the panel of class <code>A</code> via the attribute
   * <code>A.students</code>. Similarly, the <code>JObjectTable</code> for the
   * <code>Enrolment</code> objects is linked to the aforementioned
   * <code>Student</code> panel via the attribute
   * <code>Student.enrolments</code>.
   * 
   * <p>
   * Linked attributes are used to set up the link components (for
   * <code>DefaultPanel</code> child components) or link columns (for
   * <code>JDataTable</code> children) in the child containers of a panel. For
   * the usage of these features, please refer to the documentations of the
   * respective types.
   * 
   * @effects initialises <code>panel</code> with the data components necessary
   *          to capture data for the domain attributes of domain class
   *          <code>cls</code>, whose GUI configurations are defined in
   *          <code>configs</code>.
   * @requires <code>panel != null /\ cls != null /\ attribViewConfigs != null /\ 
   *  attribsOfInterest are attributes of cls /\ 
   *  containmentTree != null => containmentTree is the containment-tree of the user-module of panel.controller
   * 
   * </code>
   * 
   * @version 
   * - 3.2: improved to support (1) type hierarchy, (2) multi-valued data field<br>
   * - 5.1c: support DClass(cls).mutable 
   */
  private void createDataPanelComponents(
      final DefaultPanel panel, 
      Class cls,
      final List<Region> attribViewConfigs, 
      Collection<DAttr> attribsOfInterest,
      final Tree containmentTree    // v3.0
      ) {
    final DSMBasic schema = controller.getDomainSchema();
    
    if (attribsOfInterest == null) {
      // use all attributes of the domain class
      attribsOfInterest = schema.getDomainConstraints(cls);//schema.getAttributeConstraints(cls);
    }
    
    // v5.1c: support for DClass(cls).mutable
//    final boolean clsMutable = schema.isEditable(cls); 
    
    String attributeName, attribViewCfgName;
    JComponent[] comps;
    JComponent comp;
    JDataContainer dcont; // v3.1
    final DataController myCtl = panel.getController();
    final DataController parentCtl = myCtl.getParent();
    Class parentCls = (parentCtl != null) ? 
        parentCtl.getCreator().getDomainClass() : 
          null;
        
    Class refClass;
    DAttr.Type attributeType;
    boolean isExplicitContainer;  // v3.2: renamed from isContainerType
    boolean isExplicitDataField;  // v3.2
    Class displayClass;
    
    boolean visible;  // v3.0
    
    // v3.1: support attrib, label mappings
    final boolean hasRootDctl = myCtl.getCreator().hasRootDataController(); 
    final boolean isRootDctl = hasRootDctl && myCtl.getCreator().isRootDataController(myCtl);
    
    /**
     * loop over the attribute constraints to create the corresponding
     * components
     */
    String containerName;
    DAttr linkAttrib = null;
    
    // v3.3c: support attribute configuration in the scope-def of domainClass 
    // look up the scope-def of dataController in containmentTree
    ScopeDef containerScopeDef = null;
    if (containmentTree != null) {
      containerScopeDef = lookUpContainerScopeDefInTree(myCtl, cls, containmentTree);
    }

    for (Region attribViewCfg : attribViewConfigs) {
      attribViewCfgName = attribViewCfg.getName();
      for (DAttr attrib : attribsOfInterest) {
        attributeName = attrib.name();
        // the configuration setting of this field's
        if (attribViewCfgName.equals(attributeName)) {
          attributeType = attrib.type();
          // v3.2: isExplicitContainer=false;
          
          /* v5.1: support custom displayClass in containerScopeDef 
          displayClass = attribViewCfg.getDisplayClassType();
          */
          displayClass = null;
          if (containerScopeDef != null) {
            displayClass = containerScopeDef.getDisplayClass(attributeName);
          }
          if (displayClass == null) {
            // not specified in the custom config
            displayClass = attribViewCfg.getDisplayClassType();
          }
          // end v5.1
          
          /*v3.2
          if (displayClass != null)
            isExplicitContainerType = !JDataField.class.isAssignableFrom(displayClass);
          */
          if (displayClass != null) {
            isExplicitDataField = JDataField.class.isAssignableFrom(displayClass);
            isExplicitContainer = !isExplicitDataField;
          } else { 
            isExplicitDataField = false; isExplicitContainer=false;
          }
          
          // v3.0: support visibility
          visible = attribViewCfg.getVisible();
          
          if (attributeType.isCollection()
              && !isExplicitDataField // v3.2: added this case to support multi-valued fields
              ) { 
            // one-many association /\ not a data field (as per configuration)
            // -> create a sub-container component
            /**
             * if the domain type of the collection elements itself contains a
             * nested collection-type attribute then we must recursively create
             * a panel for it, otherwise, we just create a JDataTable.
             */
            /**@version 2.1b: update filter*/
            Select filter = attrib.filter();
            if (filter.equals(CommonConstants.NullString))
              throw new InternalError("Type filter is expected but not set");
            
            // the domain class used as the type of the collection elements
            
            final Class domainType = filter.clazz();

            if (displayClass == null) { // display class not specified in config
              /* v2.7.3: try to determine a suitable display class from the domainType
               *  if domainType contains 1-M associations 
               *    displayClass = DefaultPanel
               *  else
               *    displayClass = ObjectTable 
               */              
              //displayClass = JObjectTable.class;
              if (schema.hasAssociation(domainType, AssocType.One2Many, AssocEndType.One)) {
                // has 1:M associations
                displayClass = DefaultPanel.class;
              } else {
                displayClass = JObjectTable.class;
              }
            }
            containerName = attribViewCfgName; //attribViewCfg.getName();
            // TODO: v5.1: PROBLEM is attribViewCfg can be a RegionDataField (e.g. when configured
            // explicitly in the referenced module)
            dcont = createSubContainerComponent(containerName, displayClass, attrib, domainType, attribViewCfg, panel, containmentTree);
          } else if (
              //v2.7.2: !attributeType.isPrimitive() &&
              attributeType.isDomainType() &&
              isExplicitContainer) { 
            // one-one or many-one association /\ a container-typed view field (as per configuration)
            // -> create a sub-container component
            final Class domainType = schema.getDomainClassFor(cls, attributeName);
            // one-to-one association
            // create a nested panel (similar to the above case)
            // get the constraints of the domain type
            containerName = attribViewCfgName; //attribViewCfg.getName();
            // TODO: v5.1: PROBLEM is attribViewCfg can be a RegionDataField (e.g. when configured
            // explicitly in the referenced module)
            dcont = createSubContainerComponent(containerName, displayClass, attrib, domainType, attribViewCfg, panel, containmentTree);
          } else { 
            // use data field component
            // v3.3c: support containment tree
            // v5.1c: support clsMutable
            comps = createDataFieldComponent(panel, cls, attrib, attribViewCfg, containerScopeDef, true);
            comp = comps[1];
            panel.addLabelledComponent(attrib, attribViewCfg, comps[0], comp);

            // v3.1: support attrib,label mapping
            if (isRootDctl || !hasRootDctl) {
              // either myCtl is root data controller or a non-root data controller in a view that does not have a root data controller
              myCtl.getCreator().addAttribNameLabelMapping(attributeName, attribViewCfg.getLabelAsString());
            }
            
            /**
             * if this field is bounded to the parent of panel then set this up
             * as the link component
             */
            if (parentCtl != null) {
              refClass = schema.getDomainClassFor(cls, attributeName);
              /*v3.2: improved to allow refClass be either same or a super-type of parentCls
              if (refClass == parentCls) {
              */
              if (refClass == parentCls || refClass.isAssignableFrom(parentCls)) {
                // bounded
                panel.setLinkComponent(comp);
                
                // v2.7.2: 
                panel.setLinkAttribute(attrib);  
                linkAttrib = attrib;
                
                // hide this component and its label
                comps[0].setVisible(false);
                comp.setVisible(false);
              }
            }
            
            // v3.0: support visibility
            if (!visible && comp.isVisible()) {
              // hide this component and its label
              comps[0].setVisible(false);
              comp.setVisible(false);
            }
          }
          break;
        }
      } // end config  loop
    } // end constraint loop
    
    // v2.7.2: if link attrib not found among those in the view configs and this is a sub-container 
    // then search others to find it
    // some containers (e.g. reports) may not include link attribute as part of its view components
    if (linkAttrib == null) {
      /*v3.2: improved to allow refClass be either same or a super-type of parentCls and 
       * to move the parentCtl's null check to outside the loop 
      */
      if (parentCtl != null) {
        for (DAttr attrib : attribsOfInterest) {
          attributeName = attrib.name();
          refClass = schema.getDomainClassFor(cls, attributeName);
          if (refClass == parentCls || refClass.isAssignableFrom(parentCls)) {
            // found link attribute
            linkAttrib = attrib;
            panel.setLinkAttribute(attrib);
            break;
          }
        }
      }
    }
  }
  
  /**
   * Creates a {@see JDataTable} representing a collection-type domain
   * attribute of a domain class.
   * 
   * 
   * <p>
   * Get the domain class that is used as the type of the element of the
   * collection type specified by the domain constraint. Use the domain
   * constraints of this class to create the JDataFields that are used as the
   * table cell editors.
   * 
   * <p>
   * The <tt>fieldConfigs</tt> associated to the above constraints are
   * retrieved from a GUI sub-region corresponding to the table. 
   * 
   * @requires 
   * <tt> 
   *  containmentTree != null => containmentTree is the containment-tree of the user-module of dataController
   *  </tt>
   * @effects 
   *  create and return a JDataTable from the specified arguments
   *  
   * @version 
   * - 2.7.2: created<br>
   * - v3.3c: support containment tree<br>
   * - 5.1c: support DClass(cls).mutable 
   */
  private JDataTable createTableComponent(
      Region containerCfg, 
      String name, 
      Class tableClass,
      Class domainClass,
      DataController dataController, 
      // the attribute that is associated to this container (optional)
      DAttr containerAttrib, 
      // the domain constraints of the attributes in the domain class (optional)
      final Collection<DAttr> attribsOfInterest, 
      List<Region> attribViewConfigs,
      JDataContainer parent, 
      final Tree containmentTree    // v3.3
      ) {
    final DSMBasic schema = controller.getDomainSchema();

    // create the label for the table
    //final String name = config.getName(); 
    
    // number of visible rows of the table
    final int visibleRows = 1;

    /**
     * get the domain class that is used as the type of the element of the
     * collection type specified by the domain constraint. Use the domain
     * constraints of this class to create the JDataFields that are used as the
     * table cell editors.
     */

    // use a JDataTable to organise the data fields
    // the table headers are created from the component labels
    // the table columns to use the data field components as their cell
    // editors

    // table header
    Label lbl;
    String label;
    List header = new ArrayList();
    /*v3.0: fixed to swap the two loops (outer loop to iterate over the view configs)
    for (DomainConstraint attrib : attribsOfInterest) {
      for (Region attribViewCfg : attribViewConfigs) {
      */
    String attribViewCfgName;
    String attribName;

    // v5.1c: support for DClass(cls).mutable
//    final boolean clsMutable = schema.isEditable(domainClass); 

    // v3.1: support attrib, label mappings
    final boolean hasRootDctl = dataController.getCreator().hasRootDataController(); 
    final boolean isRootDctl = hasRootDctl && dataController.getCreator().isRootDataController(dataController);

    for (Region attribViewCfg : attribViewConfigs) {
      attribViewCfgName = attribViewCfg.getName();
      for (DAttr attrib : attribsOfInterest) {
        attribName = attrib.name();
        if (attribViewCfgName.equals(attribName)) {
          // the header
          lbl = attribViewCfg.getLabel();
          if (lbl == null) {
            // something wrong, use empty
            controller.logError("AppGUI.createTableComponent("+name+" [parent: "+parent+"]): " +
                "could not find label for " + domainClass.getName() + "." + attrib.name(), null);
            header.add("");
          } else {
            label = lbl.getValue(); 
            header.add(label);            
          }
          
          // v3.1: support attrib,label mapping
          if (isRootDctl || !hasRootDctl) {
            // either myCtl is root data controller or a non-root data controller in a view that does not have a root data controller
            dataController.getCreator().addAttribNameLabelMapping(attribName, attribViewCfg.getLabelAsString());
          }
        }
      }
    }

    // table
    JDataTable table = 
        JDataTable.createInstance(tableClass, containerCfg, dataController, header, parent);
        //createNewTable(tableClass, containerCfg, dataController, header, parent);
    table.setName(name);

    // get the Swing table header object to register to the component map
    JTableHeader theader = table.getTableHeader();

    // colum cell editors
    //v 5.2: final boolean fitColumnToEditor = true;
    JDataField df;
    int colIndex = 0;
    DataController parentCtl = dataController.getParent();
    Class parentCls = (parentCtl != null) ? 
        parentCtl.getCreator().getDomainClass() : 
          null;
    
    Class refClass;
    boolean editable;
    boolean bounded;
    boolean visible;  // v3.0
    Style style;    // v2.7
    
    DAttr linkAttrib = null;
    AlignmentX alignX;
    AlignmentY alignY;
    // v3.3c: support attribute configuration in the scope-def of domainClass 
    // look up the scope-def of dataController in containmentTree
    ScopeDef containerScopeDef = null;
    if (containmentTree != null) {
      containerScopeDef = lookUpContainerScopeDefInTree(dataController, domainClass, containmentTree);
    }
    
    for (Region attribViewCfg : attribViewConfigs) {
      attribViewCfgName = attribViewCfg.getName();
      for (DAttr attrib : attribsOfInterest) {
        attribName = attrib.name();
        if (attribViewCfgName.equals(attribName)) {
          
          // check if this field is a bounded to the parent (i.e. a linking column)
          refClass = schema.getDomainClassFor(domainClass, attribName);
          if (parent != null)
            bounded = (refClass == parentCls
              || refClass.isAssignableFrom(parentCls));
          else
            bounded = false;
            
          // the column is editable only if both the parent constraint (co)
          // and this constraint (c) are mutable and that it is not a bounded field
          /*v2.7.2 : support null cons
          editable = cons.mutable() && dc.mutable() && !bounded;
          */
          editable = ((containerAttrib != null) ? containerAttrib.mutable() : true) 
                        && attrib.mutable() && !bounded;
          
          // create the column's cell editor, regardless of whether the column is editable or not
          // (because editability may change during the course of the application)
          // v3.3c: support containment tree
          df = (JDataField) createDataFieldComponent(table, domainClass, attrib,
            attribViewCfg, containerScopeDef, false)[0];
          
          table.setCellEditor(attribViewCfg, df.toCellEditor(), colIndex);

          // v2.7: support display style: if label style is specified use it, else use data field region's style
          lbl = attribViewCfg.getLabel();
          style = (lbl != null) ? lbl.getStyle() : null; 
          if (style == null) style = controller.getStyleSettings(attribViewCfg);
          table.setColumnStyle(style, colIndex);
          table.setHeaderStyle(style, colIndex);
          
          // v2.7.2: support column alignment 
          alignX = attribViewCfg.getAlignX();
          alignY = attribViewCfg.getAlignY();
          table.setColumnAlignment(alignX, alignY, colIndex);
          
          // v3.0: support visibility
          visible = attribViewCfg.getVisible();
          if (!visible) {
            // hide this column
            table.setColumnVisible(colIndex, false);
          }
          
          if (!editable) {
            table.setColumnEditable(colIndex, false);
          }

          /**
           * if this field is bounded to the parent the set this up as the link
           * column
           */
           if (bounded) {
            // same or compatible
            table.setLinkColumn(colIndex);
            
            // v2.7.2: 
            table.setLinkAttribute(attrib);
            linkAttrib = attrib;
            
            // hide the link column from viewing
            if (table.isColumnVisible(colIndex)) // v3.0: added this check
              table.setColumnVisible(colIndex, false);
          } else if (!Arrays.equals(attrib.derivedFrom(),CommonConstants.EmptyArray)) {
            // derived attribute
            /**v2.5.4: if the associated attribute is derived from the parent 
             * and the attribute is already included in the parent 
             * then hide the column
             */
            if (parent != null) {
              String[] derivedFrom = attrib.derivedFrom();
              for (String dfrom : derivedFrom) {
                try {
                  Class derivedType = schema.getDomainClassFor(domainClass, dfrom);
                  if (derivedType == parentCls
                      || derivedType.isAssignableFrom(parentCls)) {
                    // derived attribute refers to parent
                    // check that this attribute is not already in the config of the parent
                    if (parent.containsComponentForAttribute(attribName)) {
                      // parent contains this field -> hide this column
                      if (table.isColumnVisible(colIndex)) // v3.0: added this check
                        table.setColumnVisible(colIndex, false);
                      break;
                    }
                  }
                } catch (NotFoundException e) {
                  // derived attribute is properly not a domain attribute (e.g. a static attribute)
                  // -> ignore
                }
              } // end for derived 
            }
          }

          // lastly, register the table header to comp map under the GUIID of
          // this column
          compMap.put(attribViewCfg, theader);

          colIndex++;
          break;
        }
      } // end constraint loop
    } // end config for loop

    // v2.7.2: if link attrib not found among those in the view configs and this is a sub-container 
    // then search others to find it
    // some containers (e.g. reports) may not include link attribute as part of its view components
    if (linkAttrib == null) {
      /*v3.2: improved to allow refClass be either same or a super-type of parentCls and 
       * to move the parentCtl's null check to outside the loop 
      */
      if (parentCtl != null) {
        for (DAttr attrib : attribsOfInterest) {
          attribName = attrib.name();
          refClass = schema.getDomainClassFor(domainClass, attribName);
          if (refClass == parentCls || refClass.isAssignableFrom(parentCls)) {
            // found link attribute
            linkAttrib = attrib;
            table.setLinkAttribute(attrib);
            break;
          }
        }
      }
    }
    
    // enable cell selection
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    table.setCellSelectionEnabled(true);

    // important: adjust row heights based on the above column settings
    table.setMinVisibleRows(visibleRows);
    table.initSizes(visibleRows);

    // application-wide table event handling
    InputHandler inputHandler = controller.getInputHelper(); 
    table.addMouseListener(inputHandler);
    table.addMouseMotionListener(inputHandler);
    
    // table-specific event handling
    table.addMouseListener(dataController);
    table.addKeyListener(dataController);

    return table;
  }

//  /**
//   * Creates a labelled {@see JDataTable} representing a collection-type domain
//   * attribute of a domain class.
//   * 
//   * 
//   * <p>
//   * Get the domain class that is used as the type of the element of the
//   * collection type specified by the domain constraint. Use the domain
//   * constraints of this class to create the JDataFields that are used as the
//   * table cell editors.
//   * 
//   * <p>
//   * Since the type is a collection, the filter field of the domain constraint
//   * specifies the actual type of the element. The filter has the following
//   * form: <br>
//   * 
//   * <pre>
//   * Class-name[:attribute*]
//   * </pre>
//   * 
//   * <br>
//   * 
//   * For example: <code>filter=Student</code> specifies just the Student class,
//   * filter=Student:id,name specifies the Student class, with only two
//   * attributes id and name. The second example is used in Report attributes,
//   * where only certain attributes appear in the projection of the report query.
//   * 
//   * <p>
//   * The component configurations associated to the above constraints are
//   * retrieved from a GUI sub-region of the current region. The name of this
//   * sub-region is the name of the constraint attribute.
//   * 
//   */
//  private JDataTable createTableComponent(
//      String name,    // table name
//      final Class tableClass, // the display class of the table
//      final Class domainClass, // the domain class of each data object stored in
//      // the table      
//      final Controller.DataController dataController, //
//      final DomainConstraint cons, // the
//                                   // domain
//                                   // constraint
//                                   // of the
//                                   // collection-type
//                                   // attribute
//      final List<DomainConstraint> fieldConstraints, // the domain constraints of
//                                                    // the attributes in the
//                                                    // domain class
//      //final Region config, // the GUI config of the above attribute
//      List<Region> fieldConfigs, // the configs for the fields above      
//      final DefaultPanel parent // the
//      // panel
//      // that
//      // contains
//      // this
//      // table      
//  ) {
//    final DomainSchema schema = controller.getDomainSchema();
//
//    // create the label for the table
////    final String name = config.getName(); 
//
////    // get the title configuration
////    Region titleCfg = controller.getSettingsForChild(config, "title");
////    String label = titleCfg.getLabel().getValue(); 
////    Style titleStyleCfg = controller.getStyleSettings(titleCfg);
////    // v2.6.c: change to container label
////    //JLabel labelComp = createLabel(titleCfg, titleStyleCfg); 
////    JLabel labelComp = createContainerLabel(titleCfg, titleStyleCfg, true, false);
//
//    // number of visible rows of the table
//    final int visibleRows = 1;
//
//    /**
//     * get the domain class that is used as the type of the element of the
//     * collection type specified by the domain constraint. Use the domain
//     * constraints of this class to create the JDataFields that are used as the
//     * table cell editors.
//     */
//
//    // use a JDataTable to organise the data fields
//    // the table headers are created from the component labels
//    // the table columns to use the data field components as their cell
//    // editors
//
//    // the GUI configurations of the domain attributes (specified in the
//    // fieldConstraints)
//    //v2.7.2: List<Region> compConfigs = controller.getReferralSettings(config);
//
//    // table header
//    // IMPORTANT: the outer loop must be over the field constraints
//    // (because some tables (e.g. those in reports) donot display all the
//    // attributes of a class!)
//    Label lbl;
//    String label;
//    List header = new ArrayList();
//    for (DomainConstraint c : fieldConstraints) {
//      for (Region cfg : fieldConfigs) {
//        if (cfg.getName().equals(c.name())) {
//          // the header
//          lbl = cfg.getLabel();
//          if (lbl == null) {
//            // something wrong, use empty
//            controller.logError("AppGUI.createTableComponent("+name+" [parent: "+parent+"]): " +
//            		"could not find label for " + domainClass.getName() + "." + c.name(), null);
//            header.add("");
//          } else {
//            label = lbl.getValue(); 
//            header.add(label);            
//          }
//        }
//      }
//    }
//
//    // table
//    JDataTable table = createNewTable(tableClass, dataController, header, parent);
//    table.setName(name);
//
//    // get the Swing table header object to register to the component map
//    JTableHeader theader = table.getTableHeader();
//
//    // colum cell editors
//    final boolean fitColumnToEditor = true;
//    JDataField df;
//    int colGuiId;
//    int colIndex = 0;
//    String fieldName;
//    final Controller.DataController parentCtl = dataController.getParent();
//    final Class parentCls = parentCtl.getCreator().getDomainClass();
//    
//    Class refClass;
//    boolean editable;
//    boolean bounded;
//    Style style;    // v2.7
//    //TODO; // swap the two loops, with the outer loop checking for the link attribute to use
//    // the nested loop is used to create the display components
//    for (Region cfg : fieldConfigs) {
//      for (DomainConstraint dc : fieldConstraints) {
//        fieldName = dc.name();
//        if (cfg.getName().equals(fieldName)) {
//          // check if this field is a bounded to the parent (i.e. a linking column)
//          refClass = schema.getDomainClassFor(domainClass, fieldName);
//          bounded = (refClass == parentCls
//              || refClass.isAssignableFrom(parentCls));
//            
//          // the column is editable only if both the parent constraint (co)
//          // and this constraint (c) are mutable and that it is not a bounded field
//          editable = cons.mutable() && dc.mutable() && !bounded;
//          
//          // create the column's cell editor, regardless of whether the column is editable or not
//          // (because editability may change during the course of the application)
//          df = (JDataField) createDataFieldComponent(table, domainClass, dc,
//            cfg, false)[0];
//          
//          table.setCellEditor(cfg, df.toCellEditor(), colIndex);
//
//          // v2.7: support display style: if label style is specified use it, else use data field region's style
//          lbl = cfg.getLabel();
//          style = (lbl != null) ? lbl.getStyle() : null; 
//          if (style == null) style = controller.getStyleSettings(cfg);
//          table.setColumnStyle(style, colIndex);
//          
//          if (!editable) {
//            table.setColumnEditable(colIndex, false);
//          }
//
//          /**
//           * if this field is bounded to the parent the set this up as the link
//           * column
//           */
//           if (bounded) {
//            // same or compatible
//            table.setLinkColumn(colIndex);
//            
//            // v2.7.2: 
//            table.setLinkAttribute(dc);
//            
//            // hide the link column from viewing
//            table.setColumnVisible(colIndex, false);
//          } else if (!Arrays.equals(dc.derivedFrom(),MetaConstants.EmptyArray)) {
//            // derived attribute
//            /**v2.5.4: if the associated attribute is derived from the parent 
//             * and the attribute is already included in the parent 
//             * then hide the column
//             */
//              String[] derivedFrom = dc.derivedFrom();
//              for (String dfrom : derivedFrom) {
//                try {
//                  Class derivedType = schema.getDomainClassFor(domainClass, dfrom);
//                  if (derivedType == parentCls
//                      || derivedType.isAssignableFrom(parentCls)) {
//                    // derived attribute refers to parent
//                    // check that this attribute is not already in the config of the parent
//                    if (parent.containsComponentForAttribute(fieldName)) {
//                      // parent contains this field -> hide this column
//                      table.setColumnVisible(colIndex, false);
//                      break;
//                    }
//                  }
//                } catch (NotFoundException e) {
//                  // derived attribute is properly not a domain attribute (e.g. a static attribute)
//                  // -> ignore
//                }
//              } // end for derived 
//            }
//
//          // lastly, register the table header to comp map under the GUIID of
//          // this column
//          compMap.put(cfg, theader);
//
//          colIndex++;
//          break;
//        }
//      } // end configs loop
//    } // end constraint for loop
//
//    // enable cell selection
//    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//    table.setCellSelectionEnabled(true);
//
//    // important: adjust row heights based on the above column settings
//    table.setMinVisibleRows(visibleRows);
//    table.initSizes(visibleRows);
//
//    // application-wide table event handling
//    InputHandler inputHandler = controller.getInputHelper(); 
//    table.addMouseListener(inputHandler);
//    table.addMouseMotionListener(inputHandler);
//    
//    // table-specific event handling
//    table.addMouseListener(dataController);
//    table.addKeyListener(dataController);
//
//    /*v2.7.2: 
//    labelComp.setLabelFor(table);
//    return new JComponent[] { labelComp, table };
//    */
//    return table;
//  }

  /**
   * This method is for sub-classes to override to return a specific type of
   * table that they want to use.
   *
   * @requires
   *  tableClass != null
   * @effects returns a new empty <code>JDataTable</code>.
   * 
   * @deprecated as of version 2.7.2
   */
  protected JDataTable createNewTable(Class tableClass,
      Region containerCfg, 
      ControllerBasic.DataController dataController,
      List header, JDataContainer parent) throws NotPossibleException {
    // use object tables
//    try {
//    Constructor cons = tableClass.getConstructor(Controller.DataController.class, 
//        List.class, JDataContainer.class);
//    
//      return (JDataTable) cons.newInstance(dataController, header, parent);
//    } catch (Exception e) {
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,  
//          "Không thể tạo đối tượng của lớp {0}", tableClass);
//    }
    return JDataTable.createInstance(tableClass, containerCfg, dataController, header, parent);
    //return new JObjectTable(dataController, header, parent);
  }

  /**
   * Create the component and (optionally) a label to represent a domain attribute of a domain class.
   * 
   * @requires 
   *  <tt>attrib is an attribute of cls /\ cfg is the data-field-configuration for attrib /\ 
   *  containerScopeDef != null => containerScopeDef is scope-def cls in the containment-tree of the user-module of dataContainer.controller
   *  </tt>
   *  
   * @modifies <code>comps</code> and <code>labels</code> if
   *           <code>withLabels = true</code>
   * @effects
   *  Create and return the components to represent the domain attribute <tt>attrib</tt> of <tt>cls</tt>, whose configuration 
   *  is <tt>cfg</tt>. If
   * <code>withLabels = true</code> then also create the <code>JLabel</code>s
   * for the components.
   * 
   * @version 
   * - 3.3c: improved to support containerScopeDef (contains containment-tree configuration for attrib (if any)), 
   *  and support a new method for determining data field's editability based on 4 factors.<br>
   */
  private JComponent[] createDataFieldComponent(
      final JDataContainer dataContainer, 
      final Class cls,     // the owner domain class
      final DAttr attrib,  // a domain attribute of the domain class cls, whose data field is to be created
      final Region cfg,
      final ScopeDef containerScopeDef, // v3.3
      final boolean withLabels
      ) 
  throws NotPossibleException
  , NotFoundException // v3.0 
  {
    //////////             PREPARATION            ////////////////////////////////////////
    final DataController dataController = dataContainer
        .getController();
    final DSMBasic dsm = controller.getDomainSchema();
    final DOMBasic dom = controller.getDodm().getDom();
    final Configuration config = controller.getConfig();
    
    if (! (cfg instanceof RegionDataField)) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_DATA_FIELD_CONFIG, 
          new Object[] {cfg, RegionDataField.class.getSimpleName()});
    }
    
    RegionDataField dfCfg = (RegionDataField) cfg;
    
    // get the domain attribute and add a (label,text field) pair for each of them
    String attributeName = attrib.name();
    boolean masked = attrib.type().isStringMasked();
    
    //////////             DATA FIELD LABEL      ////////////////////////////////////////
    JLabel label = null;
    
    Style fieldStyleConfig = controller.getStyleSettings(dfCfg);
    if (withLabels) {
      Style labelStyleConfig = dfCfg.getLabel().getStyle();
      if (labelStyleConfig == null)
          labelStyleConfig = fieldStyleConfig;
      
      // the field label
      label = createStandardLabel(dfCfg, labelStyleConfig, true, true, true, true);
    }

    // the actual class that is used to display the data field
    Class displayClass = null;
    /* v4.0: improved to support display-class spec in containerScopeDef (if specified)
    */
    displayClass = (containerScopeDef != null) ? containerScopeDef.getDisplayClass(attributeName) : null;
    if (displayClass == null) { // not specified in containerScopeDef: use dfCfg
      String displayClassName = dfCfg.getDisplayClass();
      if (displayClassName != null) {
        try {
          displayClass = Class.forName(displayClassName);
        } catch (ClassNotFoundException e) {
          // some thing wrong, log it
          controller.logError("Không tìm thấy lớp hiển thị trường dữ liệu", e);
        }
      }
    }
    
    // if this field's type is another domain class
    // then we will extract the list of allowed values
    // from the bound-attributes of the objects of that class to use here
    JDataSource dataSource = null;
    DAttr boundAttrib = null; // domain attribute
    // constraint (if bounded)
    DAttr.Type type = attrib.type();
    
    // the data validator
    // TODO (v3.2c) should we create validator for the domainType case (below)?
    final DataValidator validator = ControllerBasic.getDataValidatorInstance(cls);
    
    //debug
    if (debug && validator == null)
      System.err.printf("AppGUI.createDataFieldComponent: No validator for %s.%s%n",cls.getSimpleName(), attrib.name());
      
    if (type.isDomainReferenceType()) {
      //////////             DOMAIN-TYPED RESOURCES            ////////////////////////////////////////
      // the bounded domain type

      Class domainType = null;
      //boolean loadBoundValues = false;
      boolean displayOidWithBoundValue = false;
      boolean loadOidWithBoundValue = false;
      
      // v3.1: moved to below to support the use of referenced class in modelCfg
      //domainType = dsm.getDomainClassFor(cls, attributeName);
      
      /*v2.7.2: support the pre-configured data source type
      */
      ModelConfig modelCfg = dfCfg.getModelCfg();
      Class<JDataSource> dsType = null;
      if (modelCfg != null) {  // pre-configured data source type
        dsType = modelCfg.getDataSourceCls();
        
        // v3.1: use model class (if specified) as domainType
        Class refDomainCls = modelCfg.getDomainClassCls();
        if (refDomainCls != null)
          domainType = refDomainCls;
      }
      
      /* v5.1: improved to support extracting the domain type from a generic collection type 
      if (domainType == null) 
        domainType = dsm.getDomainClassFor(cls, attributeName);
      */
      if (domainType == null) { // no domain type specified
        if (type.isCollection()) {
          domainType = dsm.getGenericCollectionType(cls, attributeName);
          
          if (domainType == null) {
            throw new NotPossibleException(NotPossibleException.Code.INVALID_ATTRIBUTE_TYPE, 
                new Object[] {cls.getSimpleName(), attributeName, dsm.getDomainClassFor(cls, attributeName)});
          }
        } else {
          domainType = dsm.getDomainClassFor(cls, attributeName);
        }
      }
      // end v5.1
          
      /* v3.0: support the use of sourceAttrib for data source
       */
      String srcAttribName = attrib.sourceAttribute();
      if (!srcAttribName.equals(CommonConstants.NullString)) {
        // check that source attribute actually exists in the same class AND that it
        // is a Collection-typed attribute
        DAttr sourceAttrib = dsm.getDomainConstraint(cls, srcAttribName); 
        
        boolean isCollectionTyped = dsm.isCollectionTypedAttribute(cls, sourceAttrib);
        
        if (!isCollectionTyped) {
          throw new NotPossibleException(NotPossibleException.Code.INVALID_SOURCE_ATTRIBUTE_TYPED, 
              new Object[] {cls.getSimpleName(), srcAttribName});
        }
        
        // source attribute is specified -> create a special data source 
        // that will be use to extract the values of the source attribute 
        // at run-time and use them 
        
        dataSource=JDataSourceFactory.createAttributeDerivedInstance(dataController, sourceAttrib, domainType);
      } else {
        // no source attribute: create data source normally...
        if (dsType == null) {
          // use the data controller to create the data source for the domain type
          dataSource = ControllerBasic.getDataSourceInstance(domainType);
        } else {
          // create the data source instance
          /*v3.3: moved to ControllerBasic.getgetDataSourceInstance so that it can be managed there
           * with other data sources (e.g. when closing at log-out)
          */
          dataSource = ControllerBasic.getDataSourceInstance(domainType, dsType);
        }        
      }
      
      // v2.6.4.b: support displayOid option
      loadOidWithBoundValue = dfCfg.getLoadOidWithBoundValue();
      displayOidWithBoundValue = dfCfg.getDisplayOidWithBoundValue();      
      
      // determine the bound constraint based on the 
      // view configuration of the field and on the id field
      // of the domain type
      String boundAttributeStr = dfCfg.getBoundAttributes();
      // parse the attribute string into array
      List<DAttr> boundAttributes = null;
      if (boundAttributeStr != null) {
        String[] boundAttributeArr = boundAttributeStr.split(",");
        boundAttributes = new ArrayList<>(); //new DAttr[boundAttributeArr.length];
        for (int i = 0; i < boundAttributeArr.length; i++) {
          boundAttributes.add(dsm.getDomainConstraint(domainType, boundAttributeArr[i]));
        }
      } else {
        // use id attributes
        boundAttributes = dsm.getIDDomainConstraints(domainType);
      }
      
      if (boundAttributes  == null) {
        throw new NotPossibleException(NotPossibleException.Code.NO_BOUND_ATTRIBUTES,  
            new Object[] {domainType}
            );
      }
      
      // TODO: support multiple bound constraints
      // extract the domain constraint of the bounded attribute
      if (boundAttributes.size() > 1) {
        throw new NotImplementedException(
            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
            new Object[] {domainType + " has more than one bounded attributes"});
      }
      boundAttrib = boundAttributes.get(0);
      
      /* v2.6.4.a: determine if dataSource should load-and-cache object ids with the bounded attribute
       * TODO: add a configuration in RegionDataField for this
       *  for now, decide based on the comparability of the data type
       */
      if (loadOidWithBoundValue ||  // v2.6.4.b: add support for config option 
          !boundAttrib.type().isComparable()) {
        // binary-typed bounded values (e.g image) require Oids to be loaded with them
        dataSource.setLoadOidFor(boundAttrib);
      }
      
      // v2.6.4.b: support displayOid
      dataSource.setDisplayOidWithBoundValue(boundAttrib, displayOidWithBoundValue);
    }  // END    DOMAIN-TYPED RESOURCES 


    //////////             DATA FIELD            ////////////////////////////////////////
    
    // the field data component
    /*v3.3: use a 4-factor method for determining editability: (1) attrib.mutable, (2) containerScopeDef's config for attrib
     * (3) containerCfg.editable (4) dfCfg.editable
      boolean editable = attrib.mutable() && dfCfg.getEditable();
     v5.1c: added support for clsMutable 
    */
    boolean editable = attrib.mutable();
    /* v5.1c: 
    boolean editable;
    if (!clsMutable) 
      editable = false;
    else 
      editable = attrib.mutable();
     end 5.1c */
    
    if (editable) { // (1) attribute's mutability: if set to false will take precendence
      Boolean containerEditableForAttrib = null;
      if (containerScopeDef != null) 
        containerEditableForAttrib = containerScopeDef.isEditable(attrib.name());
      
      if (containerEditableForAttrib != null) { // editable specified in container
        editable = containerEditableForAttrib;  // (2): scope-def's editable for attribute (if specified) takes precendence 
      } else {  // editable not specified in container
        boolean containerEditable = DataContainerToolkit.getDataContainerEditableFromConfigHierarchy(dataContainer);
        
        if (!containerEditable) { // (3): non-editable container takes precedence!
          editable = false;
        } else {
          editable = dfCfg.getEditable(); // (4)
        }
      }
    }
    
    /* v2.7: added this check for auto-conversion to multi-value data field
    */
    boolean singleValueField;
    if (type.isListType()){ //type.isMultiValued()) {
      singleValueField = false;
    } else {
      singleValueField = (boundAttrib == null || editable==false);
    }
    
    InputHandler inputHelper = controller.getInputHelper();

    JDataField df;
    
    /* v3.2c: support the use of data field value function for complex initial value computation
    // if the attribute is specified with a default value use it 
    Object initVal = dom.getAttributeValueDefault(cls, attrib);
    */
    // if the attribute is specified with a default value use it 
    Object initVal = dom.getAttributeValueDefault(cls, attrib);
    DataFieldValueFunction initValFunc = null;
    if (initVal == null) {
      // no default value set via domain constraint, see if a default value function is specified
      DomainValueDesc defValDesc = dsm.getAttributeDefaultValueDesc(cls, attrib);
      if (defValDesc != null) {
        // value descriptor is defined -> create a value function for it
        JDataContainer rootContainer = DataContainerToolkit.getRootContainer(dataContainer); 
        initValFunc = new DataFieldValueFunction(dsm, rootContainer, defValDesc);
      } 
    }
    
    if (singleValueField) {
      if (boundAttrib == null) {
        // non-bounded field
        df = DataFieldFactory.createSingleValuedDataField(
            validator, config,
            dfCfg,  // v2.7
            dataContainer, // v2.7
            inputHelper,  // v2.7
            attrib, displayClass,  
            //v2.7.4: null,
            initVal, 
            masked,editable,true);
      } else {
        // bounded field
        df = DataFieldFactory.createSingleValuedDataField(
            validator, config,
            dfCfg,  // v2.7
            dataContainer, // v2.7
            inputHelper,  // v2.7
            attrib, boundAttrib, displayClass, dataSource,  
            //v2.7.4: null,
            initVal, 
            masked, editable,true);
      }
    } else {
      // multi-valued field
      df = DataFieldFactory.createMultiValuedDataField(
          validator,config, 
          dfCfg,  // v2.7
          dataContainer, // v2.7
          inputHelper,  // v2.7
          attrib, boundAttrib, displayClass, 
          dataSource, 
          //v2.7.4: null,
          initVal,
          editable);
    }

    // v3.2c: support init val function
    if (initValFunc != null) 
      df.setInitValFunction(initValFunc);
    
    //////////             POST-PROCESSING            ////////////////////////////////////////

    /**
     * if panel is nested and the parent's domain class is the reference type of
     * this data field then disable it so that user cannot enter data
     */
    JDataContainer parent = dataContainer.getParentContainer();
    if (parent != null) { // nested
      Class parentCls = parent.getController().getCreator().getDomainClass();
      Class fieldType = dsm.getDomainClassFor(cls, attrib.name());
      if (parentCls == fieldType) {
        df.setEnabled(false);
      }
    }

    // set up field style
    if (fieldStyleConfig != null)
      df.setStyle(fieldStyleConfig);

    // set label for data component
    if (label != null)
      label.setLabelFor(df);

    // v4.0: support customised width, height in containerScopeDef
    Integer width = null, height = null;
    if (containerScopeDef != null) {
      width = containerScopeDef.getWidth(attributeName);
      height = containerScopeDef.getHeight(attributeName);
    }
    
    if (width != null && height != null) { // BOTH width & height are specified in containerScopeDef
      df.setCustomSize(width, height);
    }
    // end v4.0
    
    if (withLabels)
      return new JComponent[] { label, df };
    else
      return new JComponent[] { df };
  }

  /**
   * @requires 
   *  <tt>cls is the domain class of <tt>dataController /\
   *  containmentTree != null /\ 
   *  containmentTree is the containment-tree of the user-module of dataController
   *  </tt>
   * 
   * @effects 
   *  if exists in <tt>containmentTree</tt> a {@link ScopeDef} for <tt>cls</tt> or, if <tt>cls</tt> is a descendant, 
   *  the containment edge <tt>(parentCls, cls)</tt> (where <tt>parentCls = dataController.parent.cls</tt>)
   *    return the {@link ScopeDef}
   *  else
   *    return null
   *    
   * @version 3.3c
   */
  private ScopeDef lookUpContainerScopeDefInTree(final DataController dctl,
      final Class cls, final Tree containmentTree) {
    if (containmentTree == null) return null;
    
    ScopeDef childScopeDef = null;
    
    if (dctl.isNestedIn()) {
      // data container is a child container
      Class parentCls = dctl.getParent().getDomainClass();
      ControllerBasic rootModuleCtl = dctl.getUser();
      childScopeDef = SwTk.getContainmentScopeDefObject(containmentTree, rootModuleCtl, parentCls, cls);
    } else {
      // data container is a top-level container
      // TODO: support scope-def for this case
    }
    
    return childScopeDef;
  }

  /**
   * @effects registers the <code>DataController</code> object
   *          <code>dataController</code> to listen to object-specific action
   *          events raised by some of the command and menu buttons of
   *          <code>this</code>.
   *          
   *          <br>Also if <tt>dataController.isListenToStateEvent=true</tt> then register it to listen to state change events 
   *          raised by any data fields of its container that are registered to be the state event sources.  
   *          
   * @requires the toolbar and action regions have been created
   * 
   * @version 
   * - 2.7.4: added support for state change events <br>
   * - 3.1: support shot cut keys
   */
  private void registerDataController(ControllerBasic.DataController dataController) {
    
    /////////// REGISTER GLOBALLY TO THE MAIN VIEW
    View parentGUI = getParentGUI();
    
    // register to the menu items
    JMenuBar menuBar = (JMenuBar) parentGUI.containerMap.get(MenuBar);
    JMenu menu;
    for (int i = 0; i < menuBar.getMenuCount(); i++) {
      menu = menuBar.getMenu(i);
      registerDataControllerTo(dataController, menu);
    }

    // register to the tool bar buttons
    JToolBar toolBar = (JToolBar) parentGUI.containerMap.get(ToolBar);
    java.util.Map<JComponent,String> shotCutKeyMap = parentGUI.getButtonShotCutKeyMap();
    
    registerDataControllerTo(dataController, toolBar, shotCutKeyMap);
    
    //////////// REGISTER TO THIS VIEW
    // actions panel
    JPanel actions = (JPanel) containerMap.get(Actions);

    registerDataControllerTo(dataController, actions, shotCutKeyMap);

    // search tool bar (for child GUIs)
    toolBar = (JToolBar) containerMap.get(SearchToolBar);
    registerDataControllerTo(dataController, toolBar, shotCutKeyMap);
    
    // v2.7.4: 
    if (dataController.isDataFieldStateListener()) {
      // a state event listener
      JDataContainer dcont = dataController.getDataContainer();
      boolean recursive=false;
      dcont.addStateListener(dataController, recursive);
    }
  }

  /**
   * @effects registers <code>dataController</code> as the
   *          <code>ActionListener</code> of all the <code>JMenuItem</code>
   *          components in the <code>menu</code> (and in its sub-menus if any).
   */
  private void registerDataControllerTo(
      ControllerBasic.DataController dataController, JMenu menu) {
    JMenuItem mi;
    String cmd;
    for (int i = 0; i < menu.getItemCount(); i++) {
      mi = menu.getItem(i);
      if (mi instanceof JMenu) {
        // nested
        registerDataControllerTo(dataController, (JMenu) mi);
      } else if (mi != null) {
        try {
          cmd = mi.getActionCommand();
          if (cmd != null && 
              //DataAction.contains(LogicalAction.getAction(cmd))) {
              dataController.actionPerformable(cmd)) {
            mi.addActionListener(dataController);
          }
        } catch (IllegalArgumentException e) {
          // ignore
        }
      }
    }
  }

  /**
   * @effects registers <code>dataController</code> as an
   *          <code>ActionListener</code> of all the <code>JButton</code>
   *          components of <code>container</code>.
   * @version 
   * - 3.1: support button actions
   * @param shotCutKeyMap 
   */
  private void registerDataControllerTo(
      ControllerBasic.DataController dataController, Container container, java.util.Map<JComponent, String> shotCutKeyMap) {
    JButton button;
    Component comp;
    String cmd;
    if (container != null) {
      for (int i = 0; i < container.getComponentCount(); i++) {
        comp = container.getComponent(i);
        if (comp instanceof JButton) {
          // button
          button = (JButton) comp;
          cmd = button.getActionCommand();

          // debug
//          if (cmd.equals("HelpButton")) {
//            System.out.println();
//          }
          
          try {
            if (cmd != null
                //&& DataAction.contains(LogicalAction.getAction(cmd))) {
                && dataController.actionPerformable(cmd)) {
              // v3.1: button.addActionListener(dataController);
              registerDataControllerToButton(button, dataController, shotCutKeyMap);
            }
          } catch (IllegalArgumentException e) {
            // ignore
          }
        } else if (comp instanceof JCheckBox) {
          // check box
          JCheckBox chk = (JCheckBox) comp;
          cmd = chk.getActionCommand();

          try {
            if (cmd != null
                //&& DataAction.contains(LogicalAction.getAction(cmd))) {
                && dataController.actionPerformable(cmd)) {
              chk.addItemListener(dataController);
            }
          } catch (IllegalArgumentException e) {
            // ignore
          }
        }
      }
    }
  }

  /**
   * @effects 
   *  register <tt>dataController</tt> as action listener of <tt>button</tt>
   * @version 3.1
   */
  private void registerDataControllerToButton(JButton button, DataController dataController, java.util.Map<JComponent, String> shotCutKeyMap) {
    // if there is a shotCutKey then define it for button with dataController as the handler
    // otherwise, register dataController in the usual way
    String shotCutKey;
    if (shotCutKeyMap != null)
      shotCutKey = shotCutKeyMap.get(button);
    else
      shotCutKey = null;
    
    if (shotCutKey != null) {
      createGlobalShotCut(button, dataController, shotCutKey);
    } else {
      button.addActionListener(dataController);
    }
  }

  /**
   * @requires 
   *  b !=null /\ b.text != null /\ al != null /\ 
   *  shotCutKey is a valid as defined by {@link KeyStroke#getKeyStroke(String)} 
   * 
   * @modifies {@link #buttonActions}
   * 
   * @effects
   *  create application-wise shot-cut for <tt>button</tt> using <tt>shotCutKey</tt>
   */
  private void createGlobalShotCut(final JButton button, final ActionListener al, final String shotCutKey) {
    /*
     * If button already has an Action then add al to the list of its action listeners
     *  otherwise create new Action for button
     */
    
    //if (buttonActionMap == null) buttonActionMap = new HashMap();
    
    ButtonAction buttonAction = getButtonActionMappping(button);

    if (buttonAction == null) {
      // create action from txt and icon
      buttonAction = new ButtonAction(button, al);
      button.setAction(buttonAction);
      
      addButtonActionMapping(button, buttonAction);
      
      //uncomment this to set mnemonic (applicable if button text is shown)
      //  buttonAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
  
      // now: map button action to the shot cut using a shared key
      String key = button.getActionCommand(); // or can be anything unique (even the button itself)
  
      // 0: means no modifier is needed
      button
        .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
          .put(KeyStroke.getKeyStroke(shotCutKey), key);
      
      button.getActionMap().put(key, buttonAction);
    } else {
      // button action already created: add al to its list of listeners
      buttonAction.addActionListener(al);
    }
  }

// v3.2: removed  
//  /**
//   * @effects 
//   *  register <tt>listener</tt> as a <tt>ChangeListener</tt> of each data field 
//   *  of the top-level (root) panel of this.
//   */
//  private void registerChangeListener(ChangeListener listener) {
//  //v2.7.2: DefaultPanel topPanel = getRootPanel();
//    JDataContainer top = getRootContainer();
//    //DefaultPanel top = (DefaultPanel) getRootContainer();
//    
//    Component[] comps = top.getComponents(null);
//    if (comps != null) {
//      JDataField f;
//      for (Component c : comps) {
//        if (c instanceof JDataField) {
//          f = (JDataField) c;
//          f.addChangeListener(listener);
//        }
//      }
//    }
//  }
  
  /**
   * @requires
   *  listener != null
   * @effects 
   *  register <tt>listener</tt> to listen to application state change events.
   */
  private void registerStateListener(StateChangeListener listener) {
    AppState[] states = listener.getStates();
    controller.addApplicationStateChangedListener(listener, states);
  }
  
  /**
   * @effects
   *  return the top-level data panel of this
   */
  /*v2.7.2: change to JDataContainer
  public DefaultPanel getRootContainer() {
    return (DefaultPanel) containerMap.get(Components);
  }
  */
  public JDataContainer getRootContainer() {
    return rootContainer;
  }

  /**
   * @effects initialises the {@link #actions} panel based on the GUI
   *          configuration identified by <code>region</code>.
   * @version 
   * - 3.0: support GUI properties <br>
   * - 3.1: support keyboard shot-cuts 
   */
  private void createActions(Region region
      , PropertySet guiProps  // v3.0
      , java.util.Map<PropertyName, String> shotCutKeyMap
      ) {
    // ActionListener al = controller.getActionListener();

    LayoutManager layout = getGUILayout(region);

    JPanel buttonPanel = (layout != null) ? new JPanel(layout) : new JPanel();

    List<Region> configs = controller.getSettings(region);

    // v3.0: add support for gui properties
    // v3.1: support keyboard shut-cuts
    boolean iconDisplay, textDisplay;
    if (guiProps != null) {
      iconDisplay = guiProps.getPropertyValue(
          PropertyName.view_objectForm_actions_buttonIconDisplay, Boolean.class, Boolean.TRUE);
      textDisplay = guiProps.getPropertyValue(
          PropertyName.view_objectForm_actions_buttonTextDisplay, Boolean.class, Boolean.TRUE);
    } else {  // default
      iconDisplay = true; textDisplay = true;
    }
    
    // create the buttons based on the configuration settings
    String shotCutKey; // v3.1
    Region buttonCfg;
    JButton but;
    for (int i = 0; i < configs.size(); i++) {
      buttonCfg = configs.get(i);
      
      if (shotCutKeyMap != null) {
        shotCutKey = lookUpKeyboardShotCut(buttonCfg, shotCutKeyMap);
      } else {
        shotCutKey = null;
      }
      
      but = createButton(buttonCfg
          // v3.1: , null
         , iconDisplay, textDisplay // v3.0
         , shotCutKey    // v3.1
          );
      buttonPanel.add(but);
    }

    JPanel actions = buttonPanel;

    w.add(actions, getGUILocation(region));

    // places this into regionMap
    containerMap.put(Actions, actions);
  }

  /**
   * @requires 
   *  region != null /\ shotCutKeyMap != null
   *  
   * @effects 
   *  if exists entry <tt>(p,i)</tt> in <tt>shotCutKeyMap</tt> s.t. 
   *   <tt>p.lastName eq region.name</tt>
   *    return <tt>i</tt> as the keyboard shot cut for <tt>region</tt>
   *  else
   *    return -1;
   * @version 3.1
   */
  private String lookUpKeyboardShotCut(Region region,
      java.util.Map<PropertyName, String> shotCutKeyMap) {
    PropertyName p;
    String s;
    String regionName = region.getName();
    
    for (Entry<PropertyName, String> e : shotCutKeyMap.entrySet()) {
      p = e.getKey();
      s = e.getValue();
      
      if (p.getLastName().equals(regionName)) {
        return s;
      }
    }
    
    return null;
  }

  /**
   * @effects 
   *  initialises {@link #statusBar} panel based on the GUI
   *  configuration identified by <code>region</code>.
   * 
   */
  private void createStatusBar(Region region) {
    StatusBar statusBar = new StatusBar(this, region);
    
    // if region is a state change listener then register  
    if (region.getIsStateListener()) {
      registerStateListener((StateChangeListener)statusBar);
    }

    // add to map
    containerMap.put(region, statusBar);

    // add to gui
    w.add(statusBar, getGUILocation(region));
  }

  /**
   * @effects returns the location of the GUI component to be placed in the
   *          region <code>region</code>.
   * 
   *          <p>
   *          The location is one of the {@see BorderLayout} location constants.
   * 
   */
  protected String getGUILocation(final Region region) {
    if (region.equals(ToolBar) || region.equals(SearchToolBar)) {
      return BorderLayout.NORTH;
    } else if (region.equals(Components) 
        || region.equals(SidePane) // v5.2
        || region.equals(Desktop)) {
      return BorderLayout.CENTER;
    } else if (region.equalsByName(Actions) || 
        region.equals(StatusBar) || region.equals(LoginActions)) {
      return BorderLayout.SOUTH;
    }

    return null;
  }

  /**
   * @effects returns the <code>JComponent</code> that was put into
   *          <code>this.w</code> under the name <code>name</code>
   * 
   * @requires <code>this</code> is a functional GUI (i.e. <code>w</code> is a
   *           <code>JInternalFrame</code>)
   */
  private JComponent getGUIComponent(final String name) {
    JInternalFrame iframe = (JInternalFrame) w;
    return getGUIComponent(iframe.getContentPane(), name);
  }

  private JComponent getGUIComponent(final Container container,
      final String name) {
    Component[] comps = container.getComponents();
    String cname;
    JComponent co;
    for (Component c : comps) {
      cname = c.getName();
      if (cname != null && cname.equals(name)) {
        return (JComponent) c;
      } else if (c instanceof Container) {
        co = getGUIComponent((Container) c, name);
        if (co != null) {
          return co;
        }
      }
    }

    return null;
  }

  /**
   * This method provides sub-classes with a way to specify a different layout
   * manager for certain GUI regions. The default behaviour of this method is
   * that it returns <code>null</code>, which means that all regions use a
   * default layout manager.
   * 
   * @effects Returns the <code>LayoutManager</code> object used for organising
   *          components in the region <code>region</code>.
   * 
   */
  protected LayoutManager getGUILayout(final Region region) {
    return null;
  }
  
  /**
   * @effects <pre>
   *  if tf = true
   *    compact the GUI represented by <tt>this.w</tt> by performing the followings:
   *    - compacting the root panel
   *    - hiding the actions panel
   *    - disabling all the tool bar buttons except the "compact view" button
   *  else
   *    uncompact the GUI to normal (i.e. reverse the above)
   *    </pre>  
   */
  public void compact(boolean tf) {
    //System.out.printf("AppGUI: compact = %b%n", compact);
    
    if (tf == this.compact)
      return;
    
    // compact/uncompact the root panel 
    JDataContainer root = getRootContainer();
    root.compact(tf);
    
    compact = tf;
    
    // update application state
    root.getController().setCurrentState(
        ((tf) ? AppState.ViewCompact : 
          AppState.ViewNormal));
    
    // hide/unhide the actions panel
    JPanel actions = (JPanel) containerMap.get(Actions);
    if (actions != null)
      actions.setVisible(!tf);
    
    // if this is currently in compact view
    //   pack it
    // else
    //   return to original size
    if (tf)
      pack(w);
    else
      updateSize();
  }
  
  /**
   * This method is similar to {@link #compact(boolean)} except that it only compact the view and does not 
   * update the application state and the tool bar buttons. 
   * 
   * @effects <pre>
   *  if tf = true
   *    compact the GUI represented by <tt>this.w</tt> by performing the followings:
   *    - compacting the root panel
   *  else
   *    uncompact the GUI to normal (i.e. reverse the above)
   *    </pre>  
   */
  public void compactViewOnly(boolean tf) {
    //System.out.printf("AppGUI: compact = %b%n", compact);
    if (tf)
      pack(w);
    else
      updateSize();
  }
  
  /**
   * @effects 
   *  if this was previously made compact by invoking {@link #compact(boolean)}
   *    return true
   *  else
   *    return false
   */
  public boolean isCompact() {
    return compact;
  }
  
  /**
   * Shows/hides the GUI represented by <code>this.w</code>.
   * 
   * @effects same as <code>w.setVisible(visible)</code>
   */
  public void setVisible(boolean visible) {
    w.setVisible(visible);
  }

  /**
   * @effects toggles visibility of the <b>container</b> region <code>containerCfg</code> of
   *          <code>this</code>.
   */
  public void setVisibleContainer(Region containerCfg, boolean visible) {
    Component c = containerMap.get(containerCfg);
    if (c != null) {
      c.setVisible(visible);
      /*v2.7.2: improved to use a new method 
      // v2.6: update GUI size to accommodate component if child gui size auto is set to true
      */
      updateSizeOnComponentChange();
    }
  }


  /**
   * @effects toggles visibility of a <b>component</b> region named <code>name</code> 
   */
  public void setVisibleComponent(String name, boolean visible) {
    // look up the GUI region whose name is name    
    // toggle visibility of the corresponding component
    Region region;
    Component comp;
    for (Entry<Region,Component> e: compMap.entrySet()) {
      region = e.getKey();
      if (region.getName().equals(name)) {
        comp = e.getValue();
        comp.setVisible(visible);
        break;
      }
    }
  }

  /**
   * @requires 
   *  this is the main GUI /\ 
   *  this.menuBar is initialised
   *      
   * @effects 
   *  make sure that the menu items corresponding to the specified modules are visibile
   *  hide other module-related menu items
   *    
   * @version 2.7.3
   */
  public void setVisibleModuleMenuItems(
      Collection<ApplicationModule> modules) {
    JMenu toolMenu = getMenuTools();
    
    if (toolMenu != null) {
      setVisibleModuleMenuItems(toolMenu, modules);
    }
  }

  /**
   * @requires 
   *  this is the main GUI /\ 
   *  this.menuBar is initialised
   *  
   * @effects 
   *  recursively make visible the menu items in <tt>menu</tt> (and in its sub-menus if any)
   *  that correspond to the specified modules.
   *  
   *  All other module-related menu items are made visible.
   */
  private void setVisibleModuleMenuItems(JMenu menu, Collection<ApplicationModule> modules) {
    JMenuItem mi;
    int count = menu.getItemCount();
    for (int i = 0; i < count; i++) {
      mi = menu.getItem(i);
      if (mi instanceof JMenu) {
        // sub-menu: recursive
        setVisibleModuleMenuItems((JMenu)mi, modules);
      } else {
        Region guiCfg = getComponentRegion((Component) mi);

        if (guiCfg instanceof RegionToolMenuItem) {
          // a module-related menu item
          ApplicationModule m = ((RegionToolMenuItem)guiCfg).getApplicationModule();
          if (modules.contains(m)) {
            // found module: make visible
            if (!mi.isVisible()) mi.setVisible(true);
          } else {
            // not among the specified modules -> hide
            mi.setVisible(false);
          }
        }
      }
    }
  }
  
  /**
   * @requires 
   *  this is the main gui 
   *  
   * @effects 
   *  if this.menuBar is not null AND exists menu Tools
   *    return this menu
   *  else
   *    return null
   */
  private JMenu getMenuTools() {
    JMenuBar menuBar = (JMenuBar) getContainerOf(MenuBar);
    if (menuBar != null) {
      int count = menuBar.getMenuCount();
      JMenu m;
      Region menuRegion;
      for (int i = 0; i < count; i++) {
        m = menuBar.getMenu(i);
        menuRegion = getComponentRegion(m);
        if (menuRegion == Tools)
          return m;
      }
    }
    
    // not found
    return null;
  }

  /**
   * @effects <pre>if the GUI component associated to region region is a container
   *                 set enabled status of all its components to enabled
   *               else 
   *                 set enabled status of the component to enabled 
   *          </pre>
   */
  public void setEnabled(Region containerCfg, boolean enabled) {
    Component c = containerMap.get(containerCfg);

    if (c == null)
      return;
    
    if (c instanceof Container) {
      Container cont = (Container)c;
      int count = cont.getComponentCount();
      for (int i = 0; i < count; i++) {
        cont.getComponent(i).setEnabled(enabled);
      }
    } else {
      c.setEnabled(enabled);
    }
  }
  
  /**
   * @effects returns the visibility of the <b>container</b> component at the GUI region
   *          <code>containerCfg</code>.
   */
  public boolean isVisibleContainer(
      Region containerCfg
//      String regionName
      ) {
    Component c = containerMap.get(
//        regionName
        containerCfg
        );
    return (c != null && c.isVisible());
  }

  /**
   * @effects returns the visibility of the <b>component</b> at the GUI region
   *          <code>cfg</code>.
   */
  public boolean isVisibleComponent(Region cfg) {
    // look up the GUI region whose name is name    
    Component comp = compMap.get(cfg);
    
    return (comp != null && comp.isVisible());
  }
  
  /**
   * @effects 
   *  if the <b>component</b> at the GUI region named <code>regionName</code> is visible
   *    return true
   *  else
   *    return false
   * @version 2.7.3
   */
  public boolean isVisibleComponent(RegionName regionName) {
    Region reg;
    Component comp;
    for (Entry<Region,Component> entry : compMap.entrySet()) {
      reg = entry.getKey();
      comp = entry.getValue();
      if (reg.getName().equals(regionName.name())) {
        return comp.isVisible();
      }
    }
    
    // region not found in this -> not visible
    return false;
  }
  

  /**
   * @effects 
   *  if the desktop component (i.e. those managed
   *  by the main GUI, e.g. toolbar or menu items) named <tt>name</tt>
   *  is visible  
   *    return true
   *  else
   *    return false
   */
  public boolean isVisibleDesktopComponent(RegionName name) {
    return controller.getMainController().getGUI().isVisibleComponent(name);
  }
  
  /**
   * @effects 
   *  if <tt>w</tt> is not null
   *    return <code>w.isVisible</code>
   *  else 
   *    return </tt>false</tt>
   */
  public boolean isVisible() {
    return w != null && w.isVisible();
  }

  /**
   * @effects if <code>w</code> is top-level and is-active or <code>w</code> is
   *          not top-level and is-selected then returns <code>true</code>,
   *          otherwise returns <code>false</code>.
   */
  public boolean isActive() {
    // this check is to handle the case this method is invoked
    // before this has been inialised completely
    if (w == null)
      return false;
    
    if (isTopLevel()) {
      return ((JFrame) w).isActive();
    } else {
      JInternalFrame iframe = (JInternalFrame) w; 
      boolean ia = iframe.isSelected();

      return ia;
    }
  }
  
  /**
   * Determines whether this component is valid. A component is valid when it is correctly sized and positioned 
   * within its parent container and all its children are also valid. 
   * In order to account for peers' size requirements, components are invalidated before they are first shown on 
   * the screen. 
   *  By the time the parent container is fully realized, all its components will be valid.
   *   
   * @effects <pre>
   *  if <tt>{@link #w} neq null </tt> AND is valid (i.e. correctly sized and positioned within its parent container
   *  and all its children are also valid)
   *    return true
   *  else
   *    return false </pre> 
   * @version 3.2c
   * 
   * @see {@link #isSized()} and {@link Component#isValid()}
   */
  public boolean isValid() {
    // this check is to handle the case this method is invoked
    // before this has been inialised completely
    if (w == null)
      return false;
    
    return w.isValid() && isSized();
  }

  /**
   * @effects 
   *  if the gui config of has editable=true
   *    return true
   *  else
   *    return false
   */
  public boolean isEditable() {
    return guiConfig.getEditable();
  }
  
  /**
   * @requires 
   *  cont is a sub-container in the containment tree of this
   *  
   * @effect 
   *  if this.gui.editable = true && the view config of cont in this has editable=true
   *    return true
   *  else
   *    return false
   *  @version 
   *  - 3.2: improved to support user module scope definition 
   */
  //TODO: performance: cache editability to improve performance?
  public boolean isEditable(JDataContainer cont) {
    /**
     * recursively check this gui's editable and the editable field of the
     * view config of cont and (if necessary) of all the parent and ancester 
     * containers of cont
     **/
    Region contCfg = getContainerRegion(cont.getGUIComponent());
    
    boolean editable;
    Boolean moduleSpecifiedEditable = null;
    if (contCfg != null) moduleSpecifiedEditable = contCfg.getEditable(controller.getApplicationModule());
    if (moduleSpecifiedEditable != null) {
      // use module-specified setting (regardless of this.editable)
      editable = moduleSpecifiedEditable;
    } else {
      // use default
      editable = this.isEditable() && 
          (contCfg == null || contCfg.getEditable());
      
      JDataContainer thisCont = cont;
      View userGUI;
      while (editable) {
        // recursively search upward to see if there is an uneditable container or GUI
        thisCont = thisCont.getParentContainer();
        if (thisCont != null) {
          userGUI = thisCont.getController().getUser().getGUI();
          /*v3.2: use raw editability checking, in light of the change above to support scopeDef
          editable = userGUI.isEditable(thisCont);
          */
          editable = userGUI.isConfiguredEditable(thisCont);
        } else {
          // no more containers
          break;
        }
      }
    }

    return editable;
  }
  
  /**
   * This method differs from {@link #isEditable(JDataContainer)} in that it does not take into account 
   * any scope definition that may be defined for <tt>cont</tt> in this. 
   * 
   * @requires 
   *  cont is a sub-container in the containment tree of this
   *  
   * @effect 
   *  if <tt>this.gui.editable = true</tt> AND the <b>originally configured</b> editability setting of <tt>cont</tt>
   *   is <tt>true</tt>
   *    return true
   *  else
   *    return false
   *  @version 3.2
   */
  private boolean isConfiguredEditable(JDataContainer cont) {
    /**
     * recursively check this gui's editable and the editable field of the
     * view config of cont and (if necessary) of all the parent and ancester 
     * containers of cont
     **/
    Region contCfg = getContainerRegion(cont.getGUIComponent());
    
    boolean editable = this.isEditable() && 
        (contCfg == null || contCfg.getEditable());
    
    JDataContainer thisCont = cont;
    View userGUI;
    while (editable) {
      // recursively search upward to see if there is an uneditable container or GUI
      thisCont = thisCont.getParentContainer();
      if (thisCont != null) {
        userGUI = thisCont.getController().getUser().getGUI();
        editable = userGUI.isConfiguredEditable(thisCont);
      } else {
        // no more containers
        break;
      }
    }

    return editable;
  }

  /**
   * @requires 
   *  cont is a sub-container in the containment tree of this
   *   
   * @effects 
   *  if exists {@link ScopeDef} for <tt>cont</tt> in the containment tree of this
   *    return its editability value (if any)
   *  else
   *    return <tt>null</tt>  
   * @version 3.2
   */
  public Boolean getEditableByScope(JDataContainer cont) {
    Region contCfg = getContainerRegion(cont.getGUIComponent());
    
    if (contCfg != null) {
      Boolean scopeDefEditable = contCfg.getEditable(controller.getApplicationModule());
      
      return scopeDefEditable;
    } else {
      // no container config
      return null;
    }
  }
  
  /**
   * Unlike other <tt>isX</tt> methods, which only checks the state of this {@link View} object,
   * this method additionally checks the state of the root data container. This is needed
   * when the resources associated to this container (e.g. buttons) need to be set to 
   * the ready states for data entry.    
   * 
   * @effects 
   *  if this is visible AND {@link #rootContainer}.<tt>state = OnFocus</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public boolean isDataEntryReady() {
    return (w != null) && 
        isVisible() && controller.getRootDataController().isState(AppState.OnFocus);
  }
  
  /**
   * @requires
   *  this is the main GUI
   * @effects 
   *  if toolbar is used
   *    return true
   *  else
   *    return false
   */
  public boolean hasToolBar() {
    if (!isTopLevel())
      return false;
    
    JToolBar toolBar = (JToolBar) containerMap.get(ToolBar);
    return (toolBar != null);
  }
  
  /**
   * This method is invoked to activate a functional GUI (e.g. by the user clicking 
   * the window or when it is opened for the first time). 
   * 
   * @effects 
   *  activate the internal frame represented by this by selecting it
   *  in the desktop
   *  and requests GUI and controller to perform pre-run configuration.
   */
  public void select() {
    // v3.2: moved here from bottom
    JInternalFrame iframe = (JInternalFrame) w;

    // deselect/select the frame
    JScrollableDesktopPane desktop = getDesktop();
    desktop.setSelectedFrame(iframe);
    // end: v3.2
    
    // if this is the first time this is shown
    // then finalise the initialisation of the data fields (if needed)
    preRunConfigure(true);
  }

//  void setDefaultFocus() {
//    JScrollableDesktopPane desktop = getDesktop();
//    JInternalFrame iframe = desktop.getSelectedFrame();
//    if (iframe != null) {
//      System.out.println("set focus on " + iframe.getTitle());
//      iframe.requestFocusInWindow();
//    }
//  }
  
  /**
   * Iconify the internal frame represented by <code>this.w</code>
   * 
   * @requires <code>w</code> is an internal frame
   */
  public void iconify() {
    JInternalFrame iframe = (JInternalFrame) w;
    try {
      iframe.setIcon(true);
    } catch (PropertyVetoException e) {
      //
    }
  }
  // v2.5: iconification is not used
//  void setIconified(boolean iconified) {
//    try {
//      JInternalFrame iframe = (JInternalFrame) w;
//
//      if (iconified == true)
//        iframe.setIcon(iconified);
//
//      if (iconified == false) {
//        // select the deiconified frame
//        JScrollableDesktopPane desktop = getDesktop();
//        desktop.setSelectedFrame(iframe);
//      }
//    } catch (PropertyVetoException e) {
//      //
//    }
//  }

  /**
   * 
   * @effects if a setter method named <code>"set" + name</code> in
   *          <code>w</code>'s type exists then invoke it passing
   *          <code>value</code> as the argument, else does nothing.
   */
  public void setProperty(String name, Object value) {
    try {
      // capitalise first character
      String c = name.charAt(0) + "";
      name = c.toUpperCase() + name.substring(1);

      Method m = JFrame.class.getMethod("set" + name, value.getClass());
      m.invoke(w, value);
    } catch (Exception e) {
      controller.logError("AppGUI: Failed to set property: " + name
          + " with value: " + value, null);
    }
  }

  /**
   * @effects resets the data fields in the GUI region <code>region</code>.
   */
  public void clear(
      Region region
      ) {

    // find all data fields in the specified region
    Container comp = (Container) containerMap.get(
//        regionName
        region
        );

    Component[] comps = comp.getComponents();

    JDataField df;
    for (Component c : comps) {
      // TODO: look up the default value in the config of the component and use them
      if (c instanceof JDataField) {
        df = (JDataField) c;
        if (df != null) {
          df.reset();
        }
      } else if (c instanceof JDataTable) {
        ((JDataTable) c).reset();
      } else if (c instanceof JTextComponent) {
        ((JTextComponent) c).setText("");
      } // v2.7.2
      else if (c instanceof JCheckBox) {
        ((JCheckBox)c).setSelected(false);
      }
    }
  }

  /**
   * @effects 
   *  reinitialises this to the initial state
   */
  public void reset() {
    //TODO: reset other configuration properties (e.g. 
    // language, search tool bar etc.)
    if (isCompact()) {
      compact(false);
    }
    
    // v2.6.2c reset this flag 
    this.notActivatedBefore = true;
  }
  
  /**
   * @effects disposes <code>this.w</code>
   */
  public void shutDown() {
    if (isTopLevel())
      ((JFrame) w).dispose();
    else
      ((JInternalFrame) w).dispose();
  }

  /**
   * This method is used to save GUI configuration settings to data source 
   *  
   * @requires
   *  object serialisation = true
   *   
   * @effects 
   *  store this.guiConfig to data source
   *  
   *  <br>Throws NotPossibleException if failed to do so.
   * @version 2.7.3
   */
  public void saveConfig() throws NotPossibleException {
    // save current location and size if 
    // (1) this gui has been activated AND (2) is not being compacted AND (3) when settings were changed
    if (isActivated() && !isCompact()) {
      // has been activated
      
      Point loc = getCurrentLocation();
      Dimension sz = getCurrentSize();
      int newW = (int)sz.getWidth(), newH = (int) sz.getHeight();
      double newX = loc.getX(), newY = loc.getY();
      final Dimension screenSz = GUIToolkit.getScreenSize();
      
      // record the settings that were changed
      Object[][] toSave = {
          {"width", newW},
          {"height", newH},
          {"topX", newX},
          {"topY", newY}
      };
      
      java.util.Map<DAttr,Object> valMap = new HashMap<>();
      DSMBasic dsm = controller.getDomainSchema();
      String attribName; Object newVal, currVal;
      Class cls = RegionGui.class;
      DAttr attrib;
      for (Object[] attribValPair : toSave) {
        attribName = (String) attribValPair[0];
        
        // v2.7.4: support resizable, relocatable
        if (!guiConfig.getResizable()) {
          if (attribName.equals("width") || attribName.equals("height")) {
            // skip
            continue;
          }
        }
        
        if (!guiConfig.getRelocatable()) {
          if (attribName.equals("topX") || attribName.equals("topY")) {
            // skip
            continue;
          }
        }
        
        // v3.1: validate new values to ensure that they are within acceptable bounds
        if (  (attribName.equals("topX") && (newX <= 0 || newX >= screenSz.getWidth())) ||
              (attribName.equals("topY") && (newY <= 0 || newY >= screenSz.getHeight()))
           )
            continue; // not acceptable -> ignore
          
        // record the new value if it is different from the current one
        attrib = dsm.getDomainConstraint(cls, attribName);

        currVal = dsm.getAttributeValue(guiConfig, attribName);
        newVal = attribValPair[1];
        if (currVal == null || !newVal.equals(currVal)) {
          // changed
          valMap.put(attrib, newVal);
        }
      }
      
      if (!valMap.isEmpty()) {
        // debug
        if (debug)
          System.out.printf("%s.saveConfig%n", this);
        controller.saveGuiConfig(valMap);
      }
    }
  }

  /**
   * @effects 
   *  if this has been activated
   *    return true
   *  else
   *    return false
   */
  boolean isActivated() {
    return isTopLevel() || notActivatedBefore == false;
  }

  /**
   * @effects returns an array of the values of the <b>text components</b> in
   *          <code>search-tool-bar</code>.
   */
  public Object[] getSearchToolBarTextState() {
    boolean textOnly = true;
    return getSearchToolBarState(textOnly);
  }
  
  /**
   * @effects 
   *  if textOnly = true
   *    if search tool bar is available AND the <b>text components</b> of 
   *    the tool bar has values
   *      returns these as an array
   *    else
   *      return null; 
   *  else 
   *    returns an array of the values of <b>all components</b> in
   *          <code>search-tool-bar</code>; or return null if no components has values
   * @version 2.7.2
   */
  public Object[] getSearchToolBarState(boolean textOnly) {
    // find all the text components of this tool bar and get their values
    JToolBar toolBar = (JToolBar) containerMap.get(SearchToolBar);

    if (toolBar == null)
      return null;

    List vals = new ArrayList();
    int count = toolBar.getComponentCount();
    Component comp;
    JTextComponent tfc;
    Object val;
    for (int i = 0; i < count; i++) {
      comp = toolBar.getComponent(i);
      val = null;
      if (comp instanceof JTextComponent) {
        val = ((JTextComponent) comp).getText().trim();
      } else if (comp instanceof JDataField) {
        val = ((JDataField) comp).getValue();
      } else if (!textOnly) { // v2.7.2: support other components
        // process all value-oriented components
        if (comp instanceof JCheckBox) {
          val = ((JCheckBox)comp).isSelected();
        }
      }

      if (val != null && !val.equals(""))
        vals.add(val);
    }

    if (!vals.isEmpty()) {
      return vals.toArray();
    } else {
      return null;
    }
  }

  /**
   * @effects sets the values of the <code>Component</code>s contained in the
   *          <code>search-tool-bar</code> to <code>vals</code>, respectively
   * @requires <code>vals != null && vals.length = </code> number of components
   *           in the tool bar whose states are updatable.
   */
  public void updateSearchToolBarState(Object[] vals) {
    JToolBar toolBar = (JToolBar) containerMap.get(SearchToolBar);

    Component comp;
    //JTextComponent tfc;
    for (int i = 0; i < vals.length; i++) {
      comp = toolBar.getComponent(i);
      if (comp instanceof JTextComponent) {
        ((JTextComponent) comp).setText(vals[i].toString());
      } else if (comp instanceof JDataField) {
        ((JDataField) comp).setValue(vals[i]);
      }
    }
  }

  /**
   * @effects updates the <tt>enabled</tt> state of the <code>GUIAction</code>s in the menu-bar
   *          and tool bar of <code>this</code> as defined in <code>stateMap</code>
   */
  public void setEnabled(java.util.Map<LAName,Boolean> stateMap) {
    LAName k;
    String name;
    Boolean v;
    JComponent comp;
    for (Entry<LAName,Boolean> e : stateMap.entrySet()) {
      //Entry e = (Entry) o;
      k = e.getKey();
      v = e.getValue();
      name = k.name();
      setEnabled(name, v.booleanValue());
    }
  }

  /**
   * This method is synchronized to prevent update threads to overwrite each others states. 
   * 
   * @modifies stateMap
   * @effects updates the <tt>enabled</tt> state of the <code>GUIAction</code>s in the menu-bar
   *          and tool bar of <code>this</code> as defined in <code>stateMap</code>; 
   *          
   *          <p>modifies the corresponding entries in <tt>stateMap</tt> to hold the old states 
   *          of each component.
   * @version 3.2c 
   */
  public synchronized void setComponentsEnabled(java.util.Map<LAName,Boolean> stateMap) {
    LAName k;
    String name;
    Boolean v;
    JComponent comp;
    Boolean oldState;
    for (Entry<LAName,Boolean> e : stateMap.entrySet()) {
      //Entry e = (Entry) o;
      k = e.getKey();
      v = e.getValue();
      name = k.name();
      oldState = setEnabled(name, v.booleanValue());
      
      if (oldState != null) // record oldState into stateMap
        stateMap.put(k, oldState);
    }
  }

  /**
   * @effects for every <code>JComponent</code> <code>comp</code> in
   *          <code>this</code> whose command name is <code>commandName</code>
   *          invokes <code>comp.setEnabled(enabled)</code>.
   *          
   *          <p>return the old enabled state of the component or return <tt>null</tt> if component is not found
   * @version 
   *  - 3.2c: improved to return old state of the component 
   */
  private Boolean setEnabled(String commandName, boolean enabled) {
    // the relevant containers of buttons come from the top-level GUI and this
    // AppGUI
    if (guiContainers == null) {
      guiContainers = new Container[] {
          (Container) parentGUI.containerMap.get(MenuBar),
          (Container) parentGUI.containerMap.get(ToolBar),
          (Container) containerMap.get(SearchToolBar),
          (Container) containerMap.get(Actions) };
    }

    Container container;
    Component comp;
    String cmd;
    Boolean oldState = null; // v3.2c
    CONTAINER: for (int j = 0; j < guiContainers.length; j++) {
      container = guiContainers[j];
      // if (container == null) {
      // System.err.format("Container %d is null%n", j);
      // continue CONTAINER;
      // }
      if (container == null)
        continue CONTAINER;

      int count = container.getComponentCount();
      COMPONENT: for (int i = 0; i < count; i++) {
        comp = container.getComponent(i);
        if (comp instanceof JButton) {
          cmd = ((JButton) comp).getActionCommand();

          if (cmd != null && cmd.equals(commandName)) {
            // found the component
            // v3.2c: if (comp.isEnabled() != enabled) {
            oldState = comp.isEnabled();
            if (!oldState.equals(enabled)) {
              comp.setEnabled(enabled);
            }
            break COMPONENT;
          }
        }
      }
    }
    
    return oldState;
  }

  /**
   * @effects <pre>
   *          if this.gui is not the main AppGUI
   *            do nothing
   *          else
   *            updates the visibility of the menus in the menu bar 
   *            based on the states of the current application state</pre>
   */
  public void updateMenuBarPermissions() {
    if (!isTopLevel())
      return;

    JMenuBar menuBar = (JMenuBar) containerMap.get(MenuBar);
    JMenu menu;
    int mcount = menuBar.getMenuCount();
    for (int i = 0; i < mcount; i++) {
      menu = menuBar.getMenu(i);
      // TODO: update this if command menu items are placed in menus other than
      // Tools
      if (menu.getActionCommand().equals("Tools")) {
        updateMenuPermissions(menu);
        break;
      }
    }
  }

  /**
   * @effects updates the visibility states of menu items and sub-menus of <tt>menu</tt>
   *          based on the current user permission
   * @requires <code>this.gui</code> is the top-level <code>AppGUI</code> (which
   *           have access to the menu bar) and <tt>menu</tt> is a menu in 
   *           the menu bar
   * @version 
   * - 3.1: improved to check permission on the modules rather than on the domain classes of those modules
   */
  private boolean updateMenuPermissions(JMenu menu) {
    /* v3.1: improved to check permission on the modules rather than on the domain classes of those modules
    JMenuItem mi;
    Region compRegion;
    ControllerBasic moduleCtl; 
    Class domainClass;
    String clsName = null;
    final DSMBasic schema = controller.getDomainSchema();
    final ControllerBasic mainCtl = controller.getMainController();
    String cmd;
    
    int miCount = menu.getItemCount();
    boolean state;
    boolean menuState = false;
    
    for (int j = 0; j < miCount; j++) {
      mi = menu.getItem(j);
      compRegion = getComponentRegion(mi);
      if (mi instanceof JMenu) {
        // submenu: recursive        
        state = updateMenuPermissions((JMenu) mi);
      } else {
        // menu item
        moduleCtl = controller.lookUpByRegion(compRegion);
        clsName = null;
        
        if (moduleCtl != null) {
          // menu item of an application module
          domainClass = moduleCtl.getDomainClass();
          if (domainClass != null)
            clsName = schema.getResourceNameFor(domainClass); //v2.7.2: schema.getDomainClassName(domainClass);
        }
        
        cmd = mi.getActionCommand();
        
        if (clsName != null) {
          // check user permission on the domain class of the module 
          state = mainCtl.getResourceState(null, clsName);
        } else {
          // check permission on the menu item's command action
          state = mainCtl.getResourceState(null, cmd);
        }
        
        //System.out.println("cmd,state: " + cmd+","+state);        
        //System.out.println("clsName: " + clsName);        
        
        if (state && !menuState) {
          menuState = true;
        }
      }
      //TODO: can do either (1) setEnabled or (2) setVisible     
      //mi.setEnabled(enabled);
      if (state != mi.isVisible()) {
        if (debug) {
          System.out.printf("Quyền %s %s = %b%n", mi.getClass().getSimpleName(), mi.getText(), state);
        }

        mi.setVisible(state);
      }
    }
    */
    
    JMenuItem mi;
    Region compRegion;
    //ControllerBasic moduleCtl; 
    //Class domainClass;
    //String clsName = null;
    ApplicationModule module;
    //final DSMBasic schema = controller.getDomainSchema();
    final ControllerBasic mainCtl = controller.getMainController();
    String cmd;
    
    int miCount = menu.getItemCount();
    boolean state;
    boolean menuState = false;
    
    for (int j = 0; j < miCount; j++) {
      mi = menu.getItem(j);
      compRegion = getComponentRegion(mi);
      if (mi instanceof JMenu) {
        // submenu: recursive        
        state = updateMenuPermissions((JMenu) mi);
      } else {
        // menu item
        module = mainCtl.lookUpModuleByViewRelatedRegion(compRegion);
        //clsName = null;
        
        /*
        if (moduleCtl != null) {
          // menu item of an application module
          domainClass = moduleCtl.getDomainClass();
          if (domainClass != null)
            clsName = schema.getResourceNameFor(domainClass);
             
        }
        */
        
        if (module != null) {
          // check user permission on viewing the module 
          state = mainCtl.getViewResourceStateOfModule(module);
        } else {
          // check permission on the menu item's command action
          cmd = mi.getActionCommand();
          
          state = mainCtl.getResourceState(null, cmd);
        }
        
        //System.out.println("cmd,state: " + cmd+","+state);        
        //System.out.println("clsName: " + clsName);        
        
        if (state && !menuState) {
          menuState = true;
        }
      }
      //TODO: can do either (1) setEnabled or (2) setVisible     
      //mi.setEnabled(enabled);
      if (state != mi.isVisible()) {
        if (debug) {
          System.out.printf("Quyền %s %s = %b%n", mi.getClass().getSimpleName(), mi.getText(), state);
        }

        mi.setVisible(state);
      }
    }
    
    return menuState;
  }

  /**
   * This method is used to force the change of editability of the display components of 
   * this GUI (e.g. in reports).  
   * 
   * @effects 
   *  update the editability of the display components of this GUI 
   *  and, if <t>recursive=true</tt>, recursively those of all the child containers of this GUI and so on,
   *  based on <tt>tf</tt> 
   * @deprecated as of version 3.3c (due to the new method of setting editability used by {@link #createDataFieldComponent(JDataContainer, Class, DAttr, Region, ScopeDef, boolean, boolean)}
   */
  private void setEditable(boolean tf, boolean recursive) {
    JDataContainer top = getRootContainer();
    top.setEditable(top, // v3.2 
          tf, recursive);
  }
  
  /**
   * @requires
   *  this is a top-level gui
   * @effects <pre>
   *          if this.gui is not the main AppGUI
   *            do nothing
   *          else
   *            updates the visibility of the tool bar 
   *            based on the user permissions set for the domain class
   *            associated to currentGUI, 
   *            the domain constraint <tt>dc</tt> of the current container associated to this gui (if any), 
   *            and <tt>editable</tt> of the gui.

   *            If <tt>df != null</tt>,   
   *              also check permissions associated to the data field <tt>df</tt></pre>
   * @version 
   * - 3.4: improved to support more complex check using a combination of 3 factors: 
   *    (1) containerField and editable, (2) security permission, and (3) df.editable
   */
  public void updateToolBarPermissions(
      final ControllerBasic currentCtl,
      final DAttr containerField, 
      final boolean editable,
      final JDataField df) {
    if (!isTopLevel())
      return;

    JToolBar toolBar = (JToolBar) containerMap.get(ToolBar);
    int compCount = toolBar.getComponentCount();
    Component comp;
    String cmd;

    final ControllerBasic mainCtl = controller.getMainController();
    final boolean securityEnabled = mainCtl.isSecurityEnabled();
    
    boolean state;
    final DSMBasic schema = controller.getDomainSchema();
    
    // the exclusion list of currentGUI
    View currentGUI = currentCtl.getGUI();
    List<Region> exclusion = (currentGUI != null) ? 
        currentGUI.getExclusion() : null;
    
    // the exclusion list based on the domain constraint of the current container
    // (if any)
    List<LAName> containerDisallow = null;
    containerDisallow = controller.getDisallowedActionsByConfig(containerField, editable);  
    
    Class domainClass = currentCtl.getDomainClass();
    String clsName = (domainClass != null) ? 
        schema.getResourceNameFor(domainClass): //v2.7.2: schema.getDomainClassName(domainClass) 
        null;
    String attributeName = (df != null) ? df.getDomainConstraint().name() : null; 
    Region compRegion;
    
    COMP: for (int i = 0; i < compCount; i++) {
      comp = toolBar.getComponent(i);
      // TODO: update this if components can be non-button
      if (comp instanceof AbstractButton) {  
        // ignore if in exclusion list
        if (exclusion != null) {
          compRegion = getComponentRegion(comp);
          if (exclusion.contains(compRegion))
            continue COMP;
        }
        cmd = ((AbstractButton)comp).getActionCommand();
      } 
      /*v2.8: support other components
      else
        continue;
        */
      else {
        compRegion = getComponentRegion(comp);
        if (exclusion != null && exclusion.contains(compRegion)) {
          // excluded
          continue COMP;
        }
        // set command to be the same as region name
        cmd = compRegion.getName();
      }
      // end v2.8
      
      /** state is determined based on two policies:
       * (1) if the current container of this gui is specified 
       *      through dc
       *        then state = true only if dc.mutable=true
       * (2)  the user has permission to operate on the gui and 
       *      if df != null and also this data field
       */ 
      state = true;
      if (containerDisallow != null) {
        // check if user is allowed based on domain constraint first 
        compRegion = getComponentRegion(comp);
        for (LAName actName : containerDisallow) {
          // NOTE: action names of the data actions are the same as the
          // region names used to create GUI components for those actions
          if (actName.name().equals(compRegion.getName())) {
            // disallow
            state = false;
            break;
          }
        }
      }
      
      /** v3.4: support a more complex state check...
      if (state == true && securityEnabled) {
        // containerDisallow does not apply to comp /\ security is enabled
        //    check security permission for cmd on domainClass.attributeName or on clsName 
        if (clsName != null) {
          if (attributeName != null)
            //v2.7.2: state = controller.getMainController().getResourceState(cmd, clsName, attributeName);
            state = controller.getMainController().getResourceStateOfDomainAttribute(cmd, domainClass, attributeName);
          else
            state = controller.getMainController().getResourceState(cmd, clsName);
        } else  // no domain class, state is false
          state = false;
      }
      */
      boolean isPermissionSet = false;  // flag whether or not security permission is set on the resource
      if (securityEnabled) {
        // check security permission for cmd on domainClass.attributeName or on clsName 
        Boolean fieldAllowed = null, classAllowed = null;
        if (domainClass != null) {
          try {
            if (attributeName != null) {
              fieldAllowed = controller.getMainController().getResourceStateOfDomainAttributeStrictly(cmd, domainClass, attributeName);
            } else {
              classAllowed = controller.getMainController().getResourceStateOfDomainClassStrictly(cmd, domainClass);
            }
            
            state = (fieldAllowed != null) ? fieldAllowed : classAllowed;
            isPermissionSet = true;
          } catch (NotFoundException e) {
            // no permission was configured for domainClass.attributeName or clsName 
            // isPermissionSet = false
          }
        } else { // no domain class, state is false
          if (state) state = false;
          isPermissionSet = true;
        }        
      }
      
      if (!isPermissionSet && df != null && LAName.isDataFieldAction(cmd)) { 
        // no security permission was set /\ df is specified /\ cmd is a data field action
        // check field's editable setting and use it to overwrite the container's state
        if (state != df.isEditable()) {
          // set permission based on field rather than on the container's setting
          state = df.isEditable();
        }
      }
      // end v3.4
      
      //TODO: can do either (1) setEnabled or (2) setVisible
      if (state != comp.isVisible())
        comp.setVisible(state);
    } // end for
  }

  /**
   * @effects 
   *  return a list of <tt>Region</tt> that describes the components that
   *  are excluded from the display of this gui; 
   *  or return <tt>null</tt> if no such regions exist
   */
  List<Region> getExclusion() {
    return guiConfig.getExcludedRegions();
  }
  
  /**
   * @requires
   *  this is a functional GUI
   * @effects   
   *  
   *  If gui <tt>offFocus</tt> is null
   *    for each child region of this that are in exclusion list
   *      turn visibility to off
   *  else 
   *    for each region shared between offFocus and this
   *      if region in exclusion list of this
   *        turn visibility to off 
   *      else 
   *        turn visibility to on 
   *      (if the visibility is the same after processing the exclusion 
   *        of this then keep it unchanged)
   *    for other child regions of gui <tt>offFocus</tt> that are in exclusion list of offFocus
   *      turn visibility to on
   *    for other child regions of this that are in exclusion list
   *      turn visibility to off
   */
  public void updateExclusion(View offFocus) {
    //TODO: update the following child regions may include 
    // those of non-main regions
    // for now, to reduce performance overhead we assume that 
    //  + child regions are those kept in the main GUI
    //  + (thus) all child regions are shared regions
    List<Region> shared = updateExclusion(null,false);
    
    // run update on offFocus passing in exclusionOfThis as argument
    if (offFocus != null) {
      offFocus.updateExclusion(shared,true);
    }
  }
  
  /**
   * @requires
   *  this is a functional GUI
   * @effects   
   *  turn visibility of each excluded region specified in the gui config
   *  of this to the specified <tt>visible</tt> value.
   */
  public List<Region> updateExclusion(boolean visibility) {
    return updateExclusion(null, visibility);
  }
  
  /**
   * @requires
   *  this is a functional GUI
   * @effects   
   *  turn visibility of each excluded region specified in the gui config of this 
   *  and is not in the list <tt>shared</tt> to the specified <tt>visible</tt> value.
   */
  private List<Region> updateExclusion(List<Region> shared, boolean visibility) {
    List<Region> exclusion = getExclusion();
    if (exclusion != null) {
      List<Region> mainChildren = parentGUI.getGUIConfig().getChildRegions();
      
      final Map<String,Component> mainRegionMap = parentGUI.containerMap;
      final Map<Region,Component> mainCompMap = parentGUI.compMap;
      // determine where each of the target excluded region is 
      // and turn its visibility to the specified visibility
      JComponent comp = null;
      for (Region target : exclusion) {
        // ignore if target is a shared region
        if (shared != null && shared.contains(target))
          continue;
        
        // two cases which need to be handled differently
        // (1) target region is a child of the main gui
        // (2) target region is a child of the current gui
        comp = null;
        if (mainChildren.contains(target)) { 
          // look in the region map of the parent GUI
          comp = (JComponent) mainRegionMap.get(target);
        } else {
          // target may be a descendant of a main's child region
          // e.g. a button in the ToolBar
          // TODO: do we also need to support the case where target 
          // is a decendant of one of this gui's child regions (e.g. button 
          // CREATE in the actions region) ?
          List<Region> descendants;
          Component[] descComps;
          for (Region mainChild : mainChildren) {
            descendants = mainChild.getChildRegions();
            if (descendants != null && descendants.contains(target)) {
              // target is a descendant
              //TODO: update this check if we have more than 1-level of nesting of 
              // child regions
              // retrieve component of the descendant region from comp map of this gui
              comp = (JComponent) mainCompMap.get(target);
              break;
            }
          }
        }

        if (comp != null && comp.isVisible() != visibility) {
          comp.setVisible(visibility);
        }
      }
    }
    
    return exclusion;
  }
  
  /**
   * @effects returns the <code>Region</code> object that is mapped to the <code>Component</code> 
   *          <code>comp</code> in 
   *          <code>this.compMap</code> or <code>null</code> if no such object exists
   */
  public Region getComponentRegion(Component comp) {
    if(compMap != null) {      
      for (Entry<Region,Component> e: compMap.entrySet()) {
        if (e.getValue() == comp) {
          return e.getKey();
        }
      }
    }
    
    return null;
  }
  
  /**
   * @effects returns the <code>Component</code> object that is mapped to the <code>Region</code> named  
   *          <code>regionName</code> in 
   *          <code>this.compMap</code> or <code>null</code> if no such object exists
   * @version 2.7.4
   */
  public Component getComponent(RegionName regionName) {
    if(compMap != null) {      
      for (Entry<Region,Component> e: compMap.entrySet()) {
        if (e.getKey().getName().equals(regionName.name())) {
          return e.getValue();
        }
      }
    }
    
    return null;
  }

  /**
   * This method works with container components in this in the same as 
   * {@link #getComponentRegion(Component)} with individual components in this.  
   * 
   * @effects returns the <code>Region</code> object that is mapped to the container component 
   *          <code>container</code> in 
   *          <code>this.containerMap</code> or <code>null</code> if no such object exists
   */
  public Region getContainerRegion(Component container) {
    if(containerMap != null) {      
      for (Entry<Region,Component> e: containerMap.entrySet()) {
        if (e.getValue() == container) {
          return e.getKey();
        }
      }
    }
    
    return null;
  }
  
  /**
   * This method is the reverse of {@link #getContainerRegion(Component)}
   * 
   * @requires 
   *  this is the top-level gui
   *  
   * @effects 
   *  look up and return the <tt>Container Component</tt> that was created in this
   *  from <tt>region</tt>; or return <tt>null</tt> if no such component is found. 
   */
  public Component getContainerOf(Region region) {
    return containerMap.get(region);
  }
  

  /**
   * @effects 
   *  return the data container of this that has focus; 
   *  or return <tt>null</tt> if no data container(s) of this have focus 
   */
  public JDataContainer getActiveDataContainer() {
    //DefaultPanel rootPanel = getRootPanel();
    JDataContainer cont = getRootContainer();// rootPanel;
    if (cont.hasFocus())
      return cont;
    else
      return getActiveDataContainer(cont);
  }
  
  /**
   * @effects 
   *  return the child data container of <tt>parent</tt> that has focus; 
   *  or return <tt>null</tt> if no child data container(s) have focus 
   */
  private JDataContainer getActiveDataContainer(JDataContainer parent) {
    Iterator<JDataContainer> cit = parent.getChildContainerIterator();
    if (cit != null) {
      JDataContainer cc;
      while (cit.hasNext()) {
        cc = cit.next();
        if (cc.hasFocus()) 
          return cc;
        else { 
          cc = getActiveDataContainer(cc);
          if (cc != null) {
            return cc;
          }
        }
      }
    }

    // no child containers found
    return null;
  }
  
  /**
   * @effects returns the actual GUI component that is created by this.
   */
  public Component getGUI() {
    return w;
  }

  /**
   * This method returns the parent GUI, which should normally be the 
   * main GUI of the application.  
   * 
   * @effects 
   *  return the parent GUI of this
   */
  public View getParentGUI() {
    return parentGUI;
  }

  public RegionGui getGUIConfig() {
    return guiConfig;
  }
  /**
   * @effects returns the <code>JScrollableDesktopPane</code> component that is
   *          created by the <code>parentGUI</code>
   */
  public JScrollableDesktopPane getDesktop() {
    if (isTopLevel()) {
      return (JScrollableDesktopPane) containerMap.get(Desktop);
    } else {
      return (JScrollableDesktopPane) parentGUI.containerMap.get(Desktop);      
    }
  }

  Dimension getSize() {
    return w.getSize();
  }

//  /**
//   * @effects if <code>this.rootPanel != null</code> returns the
//   *          <code>JLabel</code> component that carries the title of the root
//   *          panel of <code>this.w</code>, else returns <code>null</code>
//   */
//  public JLabel getTitle() {
//    if (getRootPanel() != null) {
//      JPanel titlePanel = (JPanel) getGUIComponent("title");
//      if (titlePanel != null) {
//        JLabel titleLabel = (JLabel) titlePanel.getComponent(0);
//        return titleLabel;
//      } else {
//        return null;
//      }
//    } else {
//      return null;
//    }
//  }

  /**
   * @effects if <code>this.rootPanel != null</code> returns the
   *          <code>TitlePanel</code> component that carries the title of the root
   *          panel of <code>this.w</code>, else returns <code>null</code>
   */
  public TitlePanel getTitlePanel() {
    if (getRootContainer() != null) {
      TitlePanel titlePanel = (TitlePanel) getGUIComponent("title");
      return titlePanel;
    } else {
      return null;
    }
  }
  
  /**
   * @effects
   *  if this has a title 
   *    return the title text of this
   *  else
   *    return null
   */
  public String getTitle() {
    TitlePanel titlePanel = getTitlePanel();
    
    if (titlePanel != null) {
      JLabel centre = (JLabel) titlePanel.getComponent(TitlePanel.ComponentIndex.Centre);
      return centre.getText();
    } else {
      return null;
    }
  }
  
  /**
   * @effects
   *  if this has a title 
   *    return the title component of this as JLabel
   *  else
   *    return null
   *  @version 5.2
   */
  public JLabel getTitleLabel() {
    TitlePanel titlePanel = getTitlePanel();
    
    if (titlePanel != null) {
      JLabel centre = (JLabel) titlePanel.getComponent(TitlePanel.ComponentIndex.Centre);
      return centre;
    } else {
      return null;
    }
  }
  
  public ControllerBasic<C> getController() {
    return controller;
  }

  /**
   * @effects returns <code>true</code> if <code>w</code> is a
   *          <code>JFrame</code>, else returns <code>false</code>.
   */
  public boolean isTopLevel() {
    return (w instanceof JFrame);
  }
  
  public String toString() {
    return "View(" + controller.getName() + ")";
  }
  
  // //////////////// HELPERs //////////////////////////////////
  
  /**
   * @effects returns <code>true</code> if <code>c</code> or, for
   *          <code>JDataTable</code>s, the view-port's component of
   *          <code>c</code> is an instance of <code>JDataContainer</code>.
   */
  public static boolean isContainer(Component c) {
    if (c instanceof JScrollPane) {
      return (((JScrollPane) c).getViewport().getComponent(0) instanceof JDataContainer);
    } else {
      return (c instanceof JDataContainer);
    }
  }

  /**
   * @effects returns the <code>JDataContainer</code> component that
   *          <code>c</code> either represents or contains
   * @requires <code>c</code> is either a <code>JScrollPane</code> that contains
   *           a <code>JDataContainer</code> or a <code>JDataContainer</code>
   *           object
   * @see {@link DataContainerToolkit#toDataContainer(Component)}
   */
  public static JDataContainer toDataContainer(Component c) {
    if (c instanceof JScrollPane) {
      return (JDataContainer) ((JScrollPane) c).getViewport().getComponent(0);
    } else {
      return (JDataContainer) c;
    }
  }

  /**
   * @requires 
   *  containerCfg != null
   * @effects 
   *  if exists the label that was configured for the data container whose
   *  container config is <tt>containerCfg</tt>
   *    return the label's value
   *  else
   *    return null
   *  
   *  <p> Throws NotFoundException if the label does not exist
   * 
   *  @version 3.0
   */
  public static String getDataContainerLabelAsString(Region containerCfg) throws NotFoundException {
    // the label was set as the sub-region of containerCfg named "title" 
    Region titleRegion = ControllerBasic.getSettingsForChild(containerCfg, "title");
    
    if (titleRegion != null) {
      return titleRegion.getLabelAsString();
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *    return a <tt>Map</tt> of (dc,s) where <tt>dc</tt> is a <b>displayable</tt>, and if <tt>printable = true</tt> then also  
   *    printable, domain attribute of 
   *    <tt>dcont</tt> and <tt>l</tt> is the attribute's label; 
   *    or return <tt>null</tt> if no domain attributes exist. 
   *    
   * @version 3.0 
   */
  public static java.util.Map<DAttr, String> getViewableDomainAttributesWithLabel(
      JDataContainer dcont, boolean printable) {
    java.util.Map<DAttr, String> attribMap = new LinkedHashMap();
    
    if (dcont != null) {
      Collection<DAttr> attribs = dcont.getDomainAttributes(printable);
      Region attribViewCfg;
      String label;
      for (DAttr attrib : attribs) {
        attribViewCfg = dcont.getComponentConfig(dcont.getComponent(attrib));
        label = attribViewCfg.getLabelAsString();
        
        attribMap.put(attrib, label);
      }
    }
    
    if (attribMap.isEmpty())
      return null;
    else
      return attribMap;
  }

  /**
   * @effects 
   *  if a data component of <tt>dcont</tt> that renders a domain attribute currently has a focus
   *    return the domain attribute 
   *  else
   *    return <tt>null</tt>
   * @version 3.0   
   */
  public static DAttr getSelectedDomainAttribute(JDataContainer dcont) {
    
    DAttr selectedAttrib = null;
    
    if (dcont != null) {
      selectedAttrib = dcont.getSelectedDomainAttribute();
    }
    
    return selectedAttrib;
  }

  /**
   * @modifies the Region of this, whose name is <tt>targetRegion</tt>
   * 
   * @effects 
   *  if exists in this a GUI region whose name is <tt>targetRegion</tt> 
   *    adds <tt>comp</tt> as the next component in that region
   *  else
   *    throws NotFoundException
   *    
   *  <p>If the target component is not a {@link Container} throws NotPossibleException 
   *  
   * @version 5.2 
   */
  public boolean addComponentToSidePanel(Component comp) throws NotFoundException, NotPossibleException {
    return addComponentToRegion(comp, RegionName.SidePane);
  }

  /**
   * @modifies the Region of this, whose name is <tt>targetRegion</tt>
   * 
   * @effects 
   *  if exists in this a GUI region whose name is <tt>targetRegion</tt> 
   *    adds <tt>comp</tt> as the next component in that region
   *  else
   *    throws NotFoundException
   *    
   *  <p>If the target component is not a {@link Container} throws NotPossibleException 
   * 
   * @version 5.2 
   * 
   */
  public boolean addComponentToRegion(Component comp, RegionName regName) throws NotFoundException, NotPossibleException {
    
    Component region = getComponent(regName);
    
    if (region == null) {
      throw new NotFoundException(NotFoundException.Code.COMPONENT_NOT_FOUND, new Object[] {regName} );
    }

    if (!(region instanceof Container)) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_REGION_COMPONENT, new Object[] {region.getClass(), "Container"} );
    }

    Container regCont = (Container) region;
    regCont.add(comp);
    
    return true;
  }

  /**
   * @requires {@link #rootContainer} is specified
   * 
   * @effects 
   *  look up the descendant data container of this whose info is <tt>contInfo</tt> and 
   *  , it it exists, activate it. If there are any intermediate ancestor containers in the path 
   *  to {@link #rootContainer} then make these containers visible (if not already).
   *  
   *  <p>A data container's label is obtained by {@link JDataContainer#getLabel()}.
   *  
   * @version 5.2
   */
  public void activateDataContainerView(final LabelledContInfo contInfo) {
    if (rootContainer == null) return;

    JDataContainer cont = contInfo.getSecond();
    
    // if cont is a proper descendant of one of them then show all containers in the path first
    Collection<JDataContainer> contPath = DataContainerToolkit.getProperContainerPath(rootContainer, contInfo); 
    if (contPath != null && // proper descendant 
        contPath.size() > 2 // has at least one intermediate ancestors
        ) {
      // make visible (if not already) all the intermediate ancestors in the path (excluding rootContainer, 
      // which is assumed to be visible)
      for (JDataContainer c : contPath) {
        if (c != cont && c != rootContainer)
          controller.showDataContainer(c);
      }
    }
     
    // now, activate the container
    controller.activateView(cont);

    // scroll to the component
    scrollToContainer(cont);
  }

  /**
   * @effects 
   *  Use the scroll-bar associated to each data container in this to scroll to 
   *  the top-left position of the label associated to <tt>cont</tt>.
   *  
   *  <p>If <tt>cont = {@link #rootContainer}</tt> then the label is taken to be the title label
   *  created by {@link #createTitlePanel(Region)}.
   *  
   * @version 5.2
   */
  private void scrollToContainer(JDataContainer cont) {
    Rectangle bounds;
    JLabel labelComp;
    JDataContainer parent = cont.getParentContainer();
    JComponent contGUI;
    if(parent != null) {  // child container
      contGUI = cont.getGUIComponent();
      labelComp = parent.getLabelFor(contGUI);
      bounds = labelComp.getBounds();
    } else {
      // top-level: use the title label or the top-left point
      labelComp = getTitleLabel();
      if (labelComp != null)
        bounds = (labelComp.getBounds());
      else
        bounds = new Rectangle(0,0,0,0);
      
      if (rootContainer != null)
        contGUI = (JComponent) rootContainer.getGUIComponent();
      else // should not happen: use w instead
        contGUI = (JComponent) w;
    }

    ((JComponent)contGUI.getParent()).scrollRectToVisible(bounds);    
  }
}
