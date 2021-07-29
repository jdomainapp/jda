package jda.mosa.controller.assets.eventhandler;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import jda.mosa.controller.ControllerBasic;
import jda.mosa.view.View;

/**
   * A sub-class of {@see WindowAdapter} and {@see InternalFrameListener} to
   * handle window-related events.
   * 
   * @author dmle
   * 
   */
  public class WindowHelper extends WindowAdapter implements InternalFrameListener {
    private ControllerBasic mainCtl;

    private static WindowHelper instance;

    private WindowHelper(ControllerBasic main) {
      mainCtl = main;
    }

    public static WindowHelper getInstance(ControllerBasic main) {
      if (instance == null) {
        instance = new WindowHelper(main);
      }

      return instance;
    }

    @Override
    public void windowClosing(WindowEvent e) {
      // shutdown
      //mainCtl.shutDown();
      mainCtl.forceShutDown();
    }

    // @Override
    // public void windowGainedFocus(WindowEvent e) {
    // // focus on the selected frame
    // System.out.println("window gained focus");
    // mainCtl.getGUI().setDefaultFocus();
    // }

    public void internalFrameClosing(InternalFrameEvent e) {
      // hide functional GUI
      // System.out.println("WindowHelper.frame closing");
      // mainCtl.hideGUI(mainCtl.getGUI((JInternalFrame) e.getSource()));
      View gui = mainCtl.getGUI((JInternalFrame) e.getSource());
      gui.iconify();
      // mainCtl.hideFunctionalGUI(gui);
    }

    /**
     * Invoked when an internal frame is iconified.
     */
    public void internalFrameIconified(InternalFrameEvent e) {
      // hide functional GUI
      JInternalFrame frame = (JInternalFrame) e.getSource();
      // System.out.println("WindowHelper.iconified: " + frame);
      // mainCtl.hideGUI(mainCtl.getGUI(frame));
      View gui = mainCtl.getGUI(frame);
      mainCtl.hideFunctionalGUI(gui);
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
      /** version 2.5: removed because duplication with frame activated */
//       JInternalFrame frame = (JInternalFrame) e.getSource();
//       System.out.println("frame " + frame.getTitle() + " opened");
      //
      // /**
      // * sets up the tool bar
      // */
      // setUpToolBar(frame);
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
      // do nothing
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
      /** version 1.5: removed because this is unnecessary */
      /**
       * set up the tool bar button states based on the user permissions
       * associated to the selected frame
       */
      // setUpToolBar((JInternalFrame) e.getSource());
    }

    /**
     *  This event is raised when a frame is selected 
     *  either by {@link View#select()} which follows a {@link ControllerBasic#showGUI()}
     *  or by user clicking on the frame in the desktop
     */
    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
      JInternalFrame frame = (JInternalFrame) e.getSource();

      // System.out.println("frame " + frame.getTitle() + " activated");
      /** activate the data container of this frame */
      View gui = mainCtl.getGUI(frame);
      // TODO: improves this
      ((InputHelper) mainCtl.getInputHelper()).activateGUI(gui);

      /**
       * sets up the tool bar
       */
      // v2.5.4
      //setUpToolBar(frame);
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
      // do nothing
    }

//    private void setUpToolBar(JInternalFrame frame) {
//      /**
//       * set up the tool bar button states based on the user permissions
//       * associated to the selected frame
//       */
//      AppGUI gui = mainCtl.getGUI(frame);
//      // System.out.println("frame " + frame.getTitle() + " opened");
//      mainCtl.updateToolBarPermissions(gui);
//    }
  } // end WindowHelper