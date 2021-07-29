package jda.util.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.event.ChangeEvent;

/**
 * Represents a typical input handler capable of handling both mouse and keyboard actions.
 * 
 * <p>It is a convenient class which extends {@see MouseAdapter} and implements {@see KeyListener}
 * but provides no implements for the inherited methods.
 * 
 * @author dmle
 *
 */
public abstract class InputHandler 
  extends MouseAdapter 
  implements MouseMotionListener, 
  KeyListener, ActionListener, FocusListener, ItemListener
  ,ValueChangeListener  // v2.7.4: domainapp-specific listener
  {

  ///// MouseMotionListener
  @Override
  public void mouseDragged(MouseEvent e) {
    // empty
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    // empty
  }
  
  ///// KeyListener
  @Override
  public void keyTyped(KeyEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void keyPressed(KeyEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void keyReleased(KeyEvent e) {
    // TODO Auto-generated method stub

  }

  //// Action Listener
  @Override
  public void actionPerformed(ActionEvent e) {
    //
  }

  //// Focus Listener
  @Override
  public void focusGained(FocusEvent e) {
    //
  }
  
  @Override
  public void focusLost(FocusEvent e) {
    //
  }
  
  //// Item Listener
  @Override
  public void itemStateChanged(ItemEvent e) {
    //
  }

  @Override // ValueChangeListener
  public void fieldValueChanged(ChangeEvent e) {
  }  
}
