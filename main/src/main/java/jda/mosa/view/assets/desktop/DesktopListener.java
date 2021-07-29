package jda.mosa.view.assets.desktop;

import java.awt.event.ActionEvent;

import com.tomtessier.scrollabledesktop.DesktopMediator;

import jda.modules.mccl.conceptmodel.view.RegionName;

/**
 * @overview
 *  A sub-type of {@link com.tomtessier.scrollabledesktop.DesktopListener} to handle action events 
 *  that were set-up using the new configuration scheme.
 * @author dmle
 */

public class DesktopListener extends com.tomtessier.scrollabledesktop.DesktopListener {

  public DesktopListener() {
    super(null);
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {

    DesktopMediator desktopMediator = getDesktopMediator();
    
    String actionCmd = e.getActionCommand();

    if (actionCmd.equals(RegionName.WindowTile.name())) {  //"Tile"
      desktopMediator.tileInternalFrames();
    } else if (actionCmd.equals(RegionName.WindowCascade.name())) { // "Cascade"
      desktopMediator.cascadeInternalFrames();
    } else if (actionCmd.equals(RegionName.WindowClose.name())) { // "Close"
      desktopMediator.closeSelectedFrame();
    }

    else if (actionCmd.equals(RegionName.WindowAutoTile.name())) { // "TileRadio"
      desktopMediator.setAutoTile(true);
    } else if (actionCmd.equals(RegionName.WindowAutoCascade.name())) { // "CascadeRadio"
      desktopMediator.setAutoTile(false);
    }
    else { 
      // delegate
      super.actionPerformed(e);
    }

  }
}