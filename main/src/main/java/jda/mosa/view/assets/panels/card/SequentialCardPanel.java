package jda.mosa.view.assets.panels.card;

import java.awt.CardLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jda.mosa.view.assets.drawing.activity.ActStrucLabel;

/**
 * @overview 
 *  A sub-type of {@link JPanel}, whose layout is {@link CardLayout} and is controlled by 
 *  a button panel. This button panel is created within this panel but it is not contained in it.
 *  
 *   <p>Client code must manage the organisation of this panel and the button panel separately.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public class SequentialCardPanel extends CardPanel {

  /**
   * auto-generated
   */
  private static final long serialVersionUID = -5313427315505966265L;

  /**
   * @modifies {@link #cardButtonsPanel}
   * @effects 
   *  add {@link #cardButts} to {@link #cardButtonsPanel}, sandwiched by arrowed-lined {@link JLabel}s.
   */
  @Override
  public void createCardButtonsPanel() {
    List<JButton> cardButts = getCardButtons();
    JPanel cardButtonsPanel = getCardButtonsPanel();
    
    int numButtons = cardButts.size();
    int butIndx = 0;
    JButton firstBut = null;
    for (JButton but : cardButts) {
      cardButtonsPanel.add(but);   
      
      if (butIndx == 0) firstBut = but;
      
      // add an arrow label between two buttons
      if (butIndx < numButtons - 1) {
        JLabel arrowLabel = getStructLabel(); // v5.6: new JLabel("\u27F6");
        /* v5.6
        arrowLabel.setFont(labFont);
        arrowLabel.setForeground(fgColorArrow);
        arrowLabel.setBorder(null);
        */
        cardButtonsPanel.add(arrowLabel);
      }
      butIndx++;
    }
    
    // set first button to 'active'
    updateButtonOnStateChange(firstBut, true);
    
    // change other buttons to 'inactive'
    toggleOtherButtons(firstBut, false);
  }
  
  /**
   * @effects 
   * return the Component that has the visual symbol representing the underlying activity pattern
   * 
   * @version 5.6 
   *
   */
  public static JLabel getStructLabel() {
    JLabel arrowLabel = new JLabel("\u27F6");
    arrowLabel.setFont(labFont);
    arrowLabel.setForeground(fgColorArrow);
    arrowLabel.setBorder(null);
    
    return arrowLabel;

  }
}
