package jda.mosa.view.assets.panels.card;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import jda.mosa.view.assets.drawing.activity.ActStrucLabel;
import jda.mosa.view.assets.drawing.activity.DecisionalLabel;

/**
 * @overview 
 *  A sub-type of {@link CardPanel} that organises the view in a layout that reflects the structure of the decisional activity pattern.
 *  The sub-view of first action is displayed at the top, the sub-views of the actions at the 
 *  decision's branches are displayed in a row underneath. Only the sub-view of the chosen action
 *  is enabled, all other sub-views are hidden.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public class DecisionalCardPanel extends CardPanel {

  /**
   * auto-generated
   */
  private static final long serialVersionUID = -7337928341900335293L;

  /**
   * @modifies {@link #cardButtonsPanel}
   * @effects 
   *  add {@link #cardButts} to {@link #cardButtonsPanel} in a layout that reflects the structure of the decisional activity pattern.
   */
  @Override
  public void createCardButtonsPanel() {
    List<JButton> cardButts = getCardButtons();
    JPanel cardButtonsPanel = getCardButtonsPanel();

    // use BorderLayout for cardButtonsPanel
    cardButtonsPanel.setLayout(new BorderLayout());
    
    // a structural panel displayed in CENTER, that draws the structure
    JPanel strucPanel = new JPanel();
    ActStrucLabel lbl = getStructLabel();
    lbl.setPreferredSize(lbl.getDrawingSize());
    strucPanel.add(lbl);
    cardButtonsPanel.add(strucPanel, BorderLayout.CENTER);
    
    //
    layoutCardButtons(cardButts, cardButtonsPanel);
    
    JButton firstBut = cardButts.get(0);
    
    // set first button to 'active'
    updateButtonOnStateChange(firstBut, true);
    
    // change other buttons to 'inactive'
    toggleOtherButtons(firstBut, false);
  }

  /**
   * @effects 
   *  Organise <tt>cardButtons</tt> on <tt>cardButtonPanel</tt> so that they 
   *  reflect the activity pattern structure.
   */
  protected void layoutCardButtons(List<JButton> cardButtons, JPanel cardButtonsPanel) {
    int butIndx = 0;
    // a separate panel for the other buttons
    JPanel otherButts = new JPanel();
    cardButtonsPanel.add(otherButts, BorderLayout.SOUTH);
    
    for (JButton but : cardButtons) {
      if (butIndx == 0) { // first button
        // add to the top
        cardButtonsPanel.add(but, BorderLayout.NORTH);
      } else {  // other buttons
        // add to a panel in the bottom
        otherButts.add(but);
      }
      
      butIndx++;
    }
  }

  /**
   * @effects 
   *  return an instance of {@link ActStrucLabel} suitable for this
   */
  protected ActStrucLabel getStructLabel() {
    return new DecisionalLabel();
  }
}
