package jda.mosa.view.assets.panels.card;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @overview
 *  A mouse handler to listen to mouse click event on the button components of a card panel (ie a {@link JPanel} with 
 *  a {@link CardLayout}).
 *  
 * @author ducmle
 * @version 5.2
 */
public class CardMouseHandler implements ActionListener {
  private CardPanel cardPanel;
  
  /**
   * @requires <tt>cardPanel != null</tt>.
   * @effects 
   *  initialise this with <tt>cardPanel</tt>
   *  
   */
  public CardMouseHandler(CardPanel cardPanel) {
    this.cardPanel = cardPanel;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    // the clicked button
    JButton selectedButton = (JButton) e.getSource();
    
    // show the card corresponding to this button
    cardPanel.showCardForButton(selectedButton);
  }
}