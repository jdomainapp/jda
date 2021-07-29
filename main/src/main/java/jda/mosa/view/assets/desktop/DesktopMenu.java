package jda.mosa.view.assets.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import com.tomtessier.scrollabledesktop.BaseInternalFrame;
import com.tomtessier.scrollabledesktop.BaseRadioButtonMenuItem;
import com.tomtessier.scrollabledesktop.DesktopListener;

import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.mosa.view.assets.util.ViewComponentMapHandler;

/**
 * @overview 
 *  A sub-type of {@link com.tomtessier.scrollabledesktop.DesktopMenu} that supports localisation 
 *  of the menu items
 * @author dmle
 */
public class DesktopMenu extends com.tomtessier.scrollabledesktop.DesktopMenu {

  private Region menuCfg;
  private DesktopListener dlistener;
  private ViewComponentMapHandler compMapHandler; // v3.1
  
  /**
   * @param compHandler 
   * @requires 
   *  menuCfg != null
   * @effects 
   * creates the DesktopMenu object with the specified desktop listener, tileMode, 
   * and using the specified menuCfg for the menu's text and those of the menu items
   * 
   *  <p>Throws IllegalArgumentException if <tt>menuCfg</tt> is </tt>null</tt> or has no child menu item 
   *  configurations.
   */
  public DesktopMenu(Region menuCfg, DesktopListener dlistener, 
      ViewComponentMapHandler compMapHandler // v3.1
      ) 
  throws IllegalArgumentException {
    super(null, false);
    
    if (menuCfg == null || menuCfg.getChildrenCount() < 1) {
      throw new IllegalArgumentException("DesktopMenu.init: no menu configuration or no child menu item configurations specified");
    }
    
    this.menuCfg = menuCfg;
    this.compMapHandler = compMapHandler; 
    this.dlistener = dlistener;
  }

  @Override
  protected void initMenu() {
    /* changed to use menu configs 
    boolean tileMode = isTileMode();

    // the menu
    setText("Window");  // Window, Cửa sổ
    setMnemonic(KeyEvent.VK_S); // VK_W
    
    // menu items 
    this.add(new BaseMenuItem(dlistener, "Đồng thời", "Tile", KeyEvent.VK_T, -1)); //Tile
    this.add(new BaseMenuItem(dlistener, "Gối nhau", "Cascade", KeyEvent.VK_G, -1)); // Cascade
    this.addSeparator();

    JMenu autoMenu = new JMenu("Tự động"); // Auto
    autoMenu.setMnemonic(KeyEvent.VK_U);
    ButtonGroup autoMenuGroup = new ButtonGroup();
    JRadioButtonMenuItem radioItem = new BaseRadioButtonMenuItem(dlistener, "Đồng thời", "TileRadio", 
        KeyEvent.VK_T, -1, tileMode); // Tile
    autoMenu.add(radioItem);
    autoMenuGroup.add(radioItem);

    radioItem = new BaseRadioButtonMenuItem(dlistener, "Gối nhau", "CascadeRadio", KeyEvent.VK_G, -1,
        !tileMode); // Cascade
    autoMenu.add(radioItem);
    autoMenuGroup.add(radioItem);

    this.add(autoMenu);
    this.addSeparator();

    this
        .add(new BaseMenuItem(dlistener, "Đóng", "Close", KeyEvent.VK_D, KeyEvent.VK_Z));
    this.addSeparator(); // Close    
    */
    //use menuCfg to localise menu
    
    // the menu
    String label = menuCfg.getLabel().getValue();
    String cmd = menuCfg.getName();
    setText(label);
    setActionCommand(cmd);
    
    doOtherConfig(this, menuCfg);
    
    List<Region> itemCfgs = menuCfg.getChildRegions();
    
    // menu items 
    RegionType type;
    for (Region itemCfg : itemCfgs) {
      type = itemCfg.getType();
      if (type != null && type.equals(RegionType.ChoiceMenu)) {  // choice menu
        createChoiceMenu(this, itemCfg);
      } else if (type != null && type.equals(RegionType.Menu)){ // normal menu
        createMenu(this, itemCfg);
      } else {  // menu item
        createMenuItem(this, itemCfg);
      }
    }
    
    // add a separator for windows to be added later
    this.addSeparator();
    
    // update base item count (this is used to determine the menu id number of a new BaseInternalFrame that is 
    // added to the desktop)
    setBaseItemsEndIndex(getItemCount());
  }
  
  private JMenu createChoiceMenu(JMenu parent, Region menuCfg) {
    String label = menuCfg.getLabel().getValue();
    String cmd = menuCfg.getName();

    JMenu menu = new JMenu(label);
    
    menu.setActionCommand(cmd);

    doOtherConfig(menu, menuCfg);

    parent.add(menu);
    
    // v3.1: add to compMap 
    compMapHandler.add(menuCfg, menu);

    List<Region> itemCfgs = menuCfg.getChildRegions();
    
    // menu items 
    ButtonGroup group = new ButtonGroup();
    JMenuItem mitem;
    for (Region itemCfg : itemCfgs) {
      mitem = createChoiceMenuItem(itemCfg, dlistener);
      group.add(mitem);
      menu.add(mitem);
    }
    return menu;
  }
  
  private JMenu createMenu(JMenu parent, Region menuCfg) {
    String label = menuCfg.getLabel().getValue();
    String cmd = menuCfg.getName();

    JMenu menu = new JMenu(label);
    
    menu.setActionCommand(cmd);

    doOtherConfig(menu, menuCfg);

    parent.add(menu);
    
    // v3.1: add to compMap 
    compMapHandler.add(menuCfg, menu);

    List<Region> itemCfgs = menuCfg.getChildRegions();
    
    // menu items 
    RegionType type;
    for (Region itemCfg : itemCfgs) {
      type = itemCfg.getType();
      if (type != null && type.equals(RegionType.ChoiceMenu)) {  // choice menu
        createChoiceMenu(parent, itemCfg);
      } else if (type != null && type.equals(RegionType.Menu)){ // normal menu
        createMenu(parent, itemCfg);
      } else {  // menu item
        createMenuItem(parent, itemCfg);
      }
    }

    return menu;
  }
  
  private JMenuItem createMenuItem(JMenu parent, Region itemCfg) {
    String label = itemCfg.getLabel().getValue();
    String cmd = itemCfg.getName();
    JMenuItem item = new JMenuItem(label);

    item.setActionCommand(cmd);
    item.addActionListener(dlistener);
    
    doOtherConfig(item, itemCfg);
    
    parent.add(item);
    
    // v3.1: add to compMap 
    compMapHandler.add(itemCfg, item);

    return item;
  }
  
  /**
   * @effects returns a <code>JMenuItem</code> whose GUI configuration is
   *          <code>itemConfig</code> and whose action listener is
   *          <code>al</code>.
   */
  private JMenuItem createChoiceMenuItem(Region itemConfig, ActionListener al) {
    JMenuItem mi;
    String label = itemConfig.getLabel().getValue(); //itemConfig.getStringValue("label");
    String name = itemConfig.getName(); //itemConfig.getStringValue("name");
    String val = itemConfig.getDefValue(); //itemConfig.getStringValue("defvalue");
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

    doOtherConfig(mi, itemConfig);
    
    // v3.1: add to compMap 
    compMapHandler.add(itemConfig, mi);
    
    return mi;
  }
  
  private void doOtherConfig(JMenuItem m, Region itemCfg) {
    //TODO: support mnemonic & short-cut
    int mnemonic = -1;
    int shortCut = -1;
    
    if (mnemonic > -1)
      m.setMnemonic(mnemonic);
    
    // set the alt-Shortcut accelerator
    if (shortCut != -1) {
          m.setAccelerator(
                KeyStroke.getKeyStroke(
                        shortCut, ActionEvent.ALT_MASK)); 
    }
  }
  
  // to use the frame's icon on the menu item
  @Override
  protected BaseRadioButtonMenuItem createDesktopMenuItemForFrame(
      ActionListener listener, 
      String label, 
      String cmd, 
      int mnemonic, 
      int shortcut, 
      boolean selected,
      BaseInternalFrame associatedFrame) {
    BaseRadioButtonMenuItem menuItem = new BaseRadioButtonMenuItem(listener,
        label, cmd, mnemonic, shortcut, selected, associatedFrame);
    
    menuItem.setIcon(associatedFrame.getFrameIcon());
    
    return menuItem;
  }
  
  /**
   * @effects   
   *  override super's method to handle internal frame selection
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    //
    dlistener.actionPerformed(e);
  }
}