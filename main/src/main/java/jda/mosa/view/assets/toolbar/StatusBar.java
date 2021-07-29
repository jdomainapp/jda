package jda.mosa.view.assets.toolbar;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.view.View;
import jda.util.events.StateChangeListener;

/**
 * @overview
 *  A sub-class of JPanel that represents the status bar. 
 *  
 *  <p>This is used to display user and application state information, including 
 *  current date time and information about the current task that is being performed. 
 *   
 * @author dmle
 */
public class StatusBar extends JPanel implements StateChangeListener {
  private JLabel userInfo;
  private JLabel dateTimeInfo;
  private JLabel taskMesg;
  private JLabel stateInfo;
  
  // state constants
  private static final AppState[] STATES = {
      AppState.LoggedIn,
      AppState.LoggedOut,
      AppState.Opened,
      AppState.Created,
      AppState.Deleted,
      AppState.Editing,
      AppState.Updated,        
      AppState.NewObject,
      AppState.Cancelled,
      AppState.Reset,
      AppState.Searched, 
      AppState.ViewNormal,
      AppState.ViewCompact,
      AppState.Print_Started, AppState.Print_Completed  // v3.2c
    };
  
  public StatusBar(View parentGUI, Region config) {
    super();
    
    setBorder(BorderFactory.createEmptyBorder(
        5,
        5,
        5,
        5));
    
    setLayout(new GridBagLayout());
    
    final ControllerBasic controller = parentGUI.getController();
    
    List<Region> configs = controller.getSettings(config);

    // create the components specified in the configs
    String regionName;
    Style styleCfg;
    
    //v3.0: TODO: get this from the status bar config?
    boolean iconDisplay = true;
    boolean textDisplay = true;
    
    for (Region cfg : configs) {
      regionName = cfg.getName();
      styleCfg = controller.getStyleSettings(cfg);

      if (regionName.equals(RegionName.StatusUserInfo.name())) {
        // user info
        userInfo = parentGUI.createLabel(cfg, styleCfg, false, false, iconDisplay, textDisplay);
        addLabel(userInfo, cfg.getDefValue(),0,0,GridBagConstraints.NONE,0,0, 
            new Dimension(cfg.getWidth(),cfg.getHeight()),true);
      } else if (regionName.equals(RegionName.StatusDateTimeInfo.name())) {
        // date time info
        dateTimeInfo = parentGUI.createLabel(cfg, styleCfg, false, false, iconDisplay, textDisplay);
        String currentDateTime = getCurrentDateTime();
        addLabel(dateTimeInfo,currentDateTime,0,0,GridBagConstraints.NONE,2,0, 
            new Dimension(cfg.getWidth(),cfg.getHeight()),true);
      } else if (regionName.equals(RegionName.StatusTaskInfo.name())) {
        // task info
        taskMesg = parentGUI.createLabel(cfg, styleCfg, false, false, iconDisplay, textDisplay);
        addLabel(taskMesg, cfg.getDefValue(),1,0,GridBagConstraints.HORIZONTAL,4,0, 
            new Dimension(cfg.getWidth(),cfg.getHeight()),true);
      } else if (regionName.equals(RegionName.StatusStateInfo.name())) {
        // state info
        stateInfo = parentGUI.createLabel(cfg, styleCfg, false, false, iconDisplay, textDisplay);
        addLabel(stateInfo, cfg.getDefValue(),0,0,GridBagConstraints.NONE,6,0, 
            new Dimension(cfg.getWidth(),cfg.getHeight()),false);   
      } 
    }
    
    // initialise the timer task thread to update the time
    new Thread(new TimerTask()).start();
  }

  /**
   * @requires
   *  this.layout is a GridBagLayout
   * @effect 
   *  configure <tt>label</tt> with the specified grid bag constraints and dimension
   *  and add it to this
   *  if separator = true
   *    add a separator to this 
   */
  private void addLabel(JLabel label, String txt, 
      int weightx, int weighty, int fill, int gridx, int gridy, 
      Dimension preferredWidth, 
      boolean separator) {
    label.setHorizontalAlignment(JLabel.CENTER);
    
    if (txt != null)
      label.setText(txt);
    
    label.setPreferredSize(preferredWidth);
    
    GridBagConstraints c = new GridBagConstraints();    
    c.weightx = weightx;  
    c.weighty = weighty;  
    c.fill = fill; 
    c.gridx = gridx; 
    c.gridy = gridy;
    
    add(label, c);
  
    // separator 
    if (separator) {
      c.fill=GridBagConstraints.VERTICAL;
      c.weightx=0;
      c.weighty=1;
      c.gridx=gridx+1;
      JSeparator sep = new JSeparator(JSeparator.VERTICAL);
      sep.setPreferredSize(new Dimension(5,preferredWidth.height));    
      add(sep, c);
    }
  }
  
  @Override
  public AppState[] getStates() {
    return STATES;
  }
  
  @Override
  public void stateChanged(Object src, AppState state, String message,Object...data) {
    if (state == AppState.LoggedIn) {
      userInfo.setText(message);
      taskMesg.setText(null);
    } else if (state == AppState.LoggedOut) {
      userInfo.setText("User");
      taskMesg.setText(message);
    } else {
      // task and state information
      taskMesg.setText(message);
    }
    stateInfo.setText(state.name());      
  }
  
  /**
   * @effects 
   *  return date-time string in a suitable format
   */
  private String getCurrentDateTime() {
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    
    return DateFormat.getDateTimeInstance(
        DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.UK).format(date);
  }
  
  /**
   * @overview 
   *  Represent a timer task that  gets the current date time  
   *  and update the date time label.
   */
  private class TimerTask implements Runnable {
    private boolean stopTask;

//    private final Border borderRed = BorderFactory.createLineBorder(Color.RED, 2);
//    private final Border borderGreen = BorderFactory.createLineBorder(Color.GREEN, 2);
    
    public TimerTask() {
      stopTask = false;
    }
    
//    @Override
//    public void mouseClicked(MouseEvent e) {
//      // stop/start the task
//      if (!stopTask) {
//        stopTask = true;
//      } else {
//        stopTask = false;
//      }
//    }
    
//    /**
//     * @effects turn on label border and display tool tip text
//     */
//    @Override
//    public void mouseEntered(MouseEvent e) {
//      lblOutput.setBorder((stopTask ? borderGreen : borderRed));
//      lblOutput.setToolTipText("Click to " + 
//          (stopTask ? "start" : "stop"));
//    }

//    /**
//     * @effects turn of label border 
//     */
//    @Override
//    public void mouseExited(MouseEvent e) {
//      lblOutput.setBorder(null);
//    } 
    
    @Override
    public void run() {
      while (true) {
        try {Thread.sleep(1000);} catch (InterruptedException e) {}
        if (!stopTask) {
          dateTimeInfo.setText(getCurrentDateTime());
        }
      }        
    }
  } // end TimerTask
}
