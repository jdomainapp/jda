package jda.mosa.view.assets.tables;

/**
 * This class can handle the following input events of a <code>JDataTable</code>:
 * <pre>
 *   - mouse & mouse motion events: show pop-up with the selected table cell's content when 
 *           it is longer than the cell's width)
 *   - keyboard events: control keys Shift_Enter (to add a new row), Delete (to delete current row)
 * </pre>
 * 
 * 
 * <p>It also creates a generic pop-up object which can 
 * be attached to a table cell. The pop-up is responsible
 * for rendering and displaying the pop-up but relies on the
 * host table for providing the appropriate content in 
 * response to mouse event
 *
 * @author Duc M Le  <a href="mailto:dmle@doc.ic.ac.uk"><i>dmle@doc.ic.ac.uk</i></a>
 * @version 1.0
 * Department of Computing, Imperial College
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jda.mosa.view.assets.GUIToolkit;

public class TableInputEventsHelper extends MouseAdapter implements KeyListener {

  private JDataTable host;

  private JPopupMenu popup;

  private JTextArea content;

  private boolean stopMove = false;

  private Point point;

  // used to keep track of whereabout of the pop-up menu
  private int[] prevPositions = { -1, -1 };

  // constants
  private static boolean debugMouse = false;
  private static boolean debugKey = false;

  public TableInputEventsHelper(final JDataTable host) {
    this.host = host;
    init();
  }

  public void setEditable(final boolean editable) {
    content.setEditable(editable);
  }

  private void init() {
    // the popup menu
    popup = new JPopupMenu();

    // the text area used for displaying content on the popup menu
    String text = "";
    content = new JTextArea(text, 4, 15);
    JScrollPane jsp = new JScrollPane(content,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    Font f = content.getFont();
    Font f1 = new Font(f.getFontName(), f.getStyle(), 13);
    content.setFont(f1);
    content.setLineWrap(true);
    content.setEnabled(true);
    float[] hsb = Color.RGBtoHSB(255, 255, 220, null); // light yellow
    Color lightYellow = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    Color fg = Color.BLACK;
    content.setBackground(lightYellow);
    content.setForeground(fg);
    // content.setAutoscrolls(true);

    content.addKeyListener(this);
    content.requestFocusInWindow();
    jsp.setViewportView(content);
    popup.add(jsp);

    // Add a listener to components that can bring up popup menus.
    host.addMouseListener(this);
    host.addMouseMotionListener(this);

    // handle keyboard events
    host.addKeyListener(this);

    // also listen to mouse events on popup
    popup.addMouseMotionListener(this);
  }

  /**
   * Return the content of the text area displayed on the pop-up box. This
   * method is to be used by caller to retrieve the pop-up content for further
   * processing.
   * 
   * @return
   */
  public String getPopUpContent() {
    return content.getText();
  }

  // /// Interface methods
  // /// mouse motion listener interface
  public void mouseDragged(MouseEvent e) {
    //
  }

  // when mouse is moved, move with mouse
  public void mouseMoved(MouseEvent e) {
    // get content
    if (debugMouse)
      System.out.println("Mouse moved");

    String text = GUIToolkit.getTableContent(prevPositions,
        TableInputEventsHelper.this, host, e);

    if (text != null && text.equals("")) {
      // content.setText(text);
      if (popup.isVisible()) {
        if (!stopMove)
          popup.show(e.getComponent(), e.getX() + 20, e.getY());
        else {
          if (!popup.isVisible()) {
            popup.setVisible(true);
          }
          content.requestFocus();
        }
      }
    } else if (text != null) {
      content.setText(text);
      if (!stopMove)
        popup.show(e.getComponent(), e.getX() + 20, e.getY());
      else {
        if (!popup.isVisible()) {
          popup.setVisible(true);
        }
        content.requestFocus();
      }
    } else {
      content.setText("");
      popup.setVisible(false);
    }
  }

  // /// mouse motion adaptor
  public void mouseEntered(MouseEvent e) {
    // if (debugMouse)
    // System.out.println("Mouse entered");
    // // get content
    // String text = mouseHandler.getContent(e);
    // if (text != null) {
    // content.setText(text);
    // popup.show(e.getComponent(), e.getX(), e.getY());
    // }
  }

  // notify host component of this event for further processing
  public void mouseExited(MouseEvent e) {
    // popup.setVisible(false);
    if (debugMouse)
      System.out.println("Mouse exited");
  }

  // // use these methods for mouse-clicked pop-up
  public void mousePressed(MouseEvent e) {
    // maybeShowPopup(e);
    // if right-button then hide

    if (debugMouse)
      System.out.println("Mouse pressed");

    if (e.isPopupTrigger()) {
      popup.setVisible(false);
    } else { // if left button clicked then we can stop/start moving popup
      // if it is being moved/stationed
      if (!popup.isVisible() && !content.getText().equals("")) {
        popup.setVisible(true);
      }
      if (stopMove)
        stopMove = false;
      else
        stopMove = true;
    }
    point = e.getPoint();
  }

  //
  // public void mouseReleased(MouseEvent e) {
  // //maybeShowPopup(e);
  // }
  //
  // private void maybeShowPopup(MouseEvent e) {
  // // if (e.isPopupTrigger()) {
  // // only show on left button click
  // if (e.getButton() == e.BUTTON1) {
  // String text = mouseHandler.getContent(e);
  // if (text != null) {
  // content.setText(text);
  // popup.show(e.getComponent(), e.getX(), e.getY());
  // }
  // }
  // // }
  // }

  // ////////////////// Key Listener
  /**
   * Invoked when a key has been typed. See the class description for
   * {@link KeyEvent} for a definition of a key typed event.
   */
  public void keyTyped(KeyEvent e) {
    // System.out.println("key typed");
    // isEdited = true;
  }

  /**
   * Invoked when a key has been pressed. See the class description for
   * {@link KeyEvent} for a definition of a key pressed event.
   */
  public void keyPressed(KeyEvent e) {
    //
    // System.out.println("key pressed");
    if (e.getKeyCode() == e.VK_ENTER) {
      if (e.isShiftDown()) {  // row manipulation
        if (e.isControlDown()) {
          // adds a new row at end
          int row = host.addRow();
          // move to the new row
          host.setRowSelectionInterval(row, row);
        } else {
          // insert a row row before the currrently selected row
          // if more than one such rows then insert one after each of them
          int[] rows = host.getSelectedRows();
          if (rows.length > 0)
            host.insertRow(rows);
        }
      } 
//      else {  // row/cell navigation (depending on the cell selection option)
//        //
//      }
    } else if (e.getKeyCode() == e.VK_DELETE) {
      // delete the current row
      int[] rows = host.getSelectedRows();
      if (rows.length > 0)
        host.deleteRows(rows, true);
    }
  }

  // if Shift+Enter then change content
  // JComponent src = (JComponent) e.getSource();
  // if (src.equals(content)) {
  // if (e.isShiftDown() & e.getKeyCode() == e.VK_ENTER) {
  // // System.out.println("sync. pop-up content with host");
  // contentHandler.setContent(getPopUpContent());
  // }
  // }

  /**
   * Invoked when a key has been released. See the class description for
   * {@link KeyEvent} for a definition of a key released event.
   */
  public void keyReleased(KeyEvent e) {
    //
  }
}
