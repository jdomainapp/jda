package jda.mosa.view.assets.desktop;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.tomtessier.scrollabledesktop.DesktopListener;
import com.tomtessier.scrollabledesktop.DesktopMediator;

import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.view.assets.util.ViewComponentMapHandler;

/**
 * @overview
 *  A sub-type of {@link com.tomtessier.scrollabledesktop.JScrollableDesktopPane} that supports localised 
 *  Windows menu.
 *  
 * @author dmle
 */
public class JScrollableDesktopPane extends com.tomtessier.scrollabledesktop.JScrollableDesktopPane {
  /**
   * creates the JScrollableDesktopPane object.
   */
  public JScrollableDesktopPane(boolean withAssociatedToolBar, DesktopListener dlistener) {
    super(withAssociatedToolBar, dlistener);
  }

  /**
   * @requires 
   *  windowMenuCfg != null
   *  
   * @effects
   *  creates a localised Windows menu and register it to the menu bar <tt>mb</tt>;
   *  each menu item is passed to the <tt>compHandler</tt> for further processing; 
   *  return the menu 
   * @version 
   * - 3.1: added compHandler
   */
  public JMenu registerMenuBar(JMenuBar mb, Region windowMenuCfg, 
      ViewComponentMapHandler compHandler  // v3.1
      ) {
    DesktopMediator desktopMediator = getDesktopMediator();
    
    // the desktop listener
    DesktopListener dlistener = desktopMediator.getDesktopListener();
    
    // create a DesktopMenu whose action listener is DesktopMediator.dlistener
    DesktopMenu windowMenu = new DesktopMenu(windowMenuCfg, dlistener, compHandler);
    windowMenu.initMenu();
    
    // register it to the desktop mediator
    desktopMediator.setDesktopMenu(windowMenu);
    
    // register it to the menu bar
    mb.add(windowMenu);
    mb.setBorder(null); // turn off the menubar border (looks better)
    
    //v2.7.2: return this menu
    return windowMenu;
  }
}
