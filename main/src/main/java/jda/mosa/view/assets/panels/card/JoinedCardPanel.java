package jda.mosa.view.assets.panels.card;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import jda.mosa.view.assets.drawing.activity.ActStrucLabel;
import jda.mosa.view.assets.drawing.activity.JoinedLabel;

/**
 * @overview 
 *  A sub-type of {@link CardPanel} that organises the view in a layout that reflects the structure of the joined activity pattern.
 *  The sub-view of first action is displayed at the top, the sub-views of the actions at the 
 *  forked's branches are displayed in a row underneath.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public class JoinedCardPanel extends DecisionalCardPanel {

  /**
   * auto-generated
   */
  private static final long serialVersionUID = -7395347132672915630L;

  /**
   * @effects 
   *  Organise <tt>cardButtons</tt> on <tt>cardButtonPanel</tt> so that they 
   *  reflect the activity pattern structure.
   */
  @Override
  protected void layoutCardButtons(List<JButton> cardButtons, JPanel cardButtonsPanel) {
    int butIndx = 0;
    // a separate panel for the other buttons
    JPanel otherButts = new JPanel();
    cardButtonsPanel.add(otherButts, BorderLayout.NORTH);
    
    int numButts = cardButtons.size();
    for (JButton but : cardButtons) {
      if (butIndx == numButts-1) { // last button
        // add to the top
        cardButtonsPanel.add(but, BorderLayout.SOUTH);
      } else {  // other buttons
        // add to a panel in the bottom
        otherButts.add(but);
      }
      
      butIndx++;
    }
  }
  
  @Override
  protected ActStrucLabel getStructLabel() {
    return new JoinedLabel();
  }
}
