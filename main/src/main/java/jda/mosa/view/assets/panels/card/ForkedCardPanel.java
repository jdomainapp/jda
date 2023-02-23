package jda.mosa.view.assets.panels.card;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.view.assets.drawing.activity.ActStrucLabel;
import jda.mosa.view.assets.drawing.activity.ForkedLabel;

/**
 * @overview 
 *  A sub-type of {@link CardPanel} that organises the view in a layout that reflects the structure of the forked activity pattern.
 *  The sub-view of first action is displayed at the top, the sub-views of the actions at the 
 *  forked's branches are displayed in a row underneath.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 * - 5.2: created<br>
 * - 5.6: improved 
 */
public class ForkedCardPanel extends CardPanel {

  /**
   * auto-generated
   */
  private static final long serialVersionUID = 2135321492858208691L;

  /**
   * @modifies {@link #cardButtonsPanel}
   * @effects 
   *  add {@link #cardButts} to {@link #cardButtonsPanel} in a layout that reflects the 
   *  structure of the forked activity pattern.
   */
  @Override
  public void createCardButtonsPanel() {

    JPanel cardButtonsPanel = getCardButtonsPanel();
    
    // use FlowLayout for cardButtonsPanel
    cardButtonsPanel.setLayout(new FlowLayout());

    // forked buttons panel 
    JPanel forkedButtonsPanel = new JPanel(new BorderLayout());
    
    List<JButton> cardButts = getCardButtons();
    
    // a structural panel displayed in CENTER, that draws the structure
    JPanel strucPanel = new JPanel();
    ActStrucLabel lbl = getStructLabel();
    Dimension structSize = lbl.getDrawingSize();
    lbl.setPreferredSize(structSize);
    strucPanel.add(lbl);
    
    forkedButtonsPanel.add(strucPanel, BorderLayout.CENTER);
    
    // forked buttons panel
    JPanel forkedButtSubpanel = new JPanel();
    // other buttons (if any) are placed in a separate  
    JPanel otherButtonsPanel = null, otherButtonsSubpanel = null;
    
    // add forked buttons panel to SOUTH
    forkedButtonsPanel.add(forkedButtSubpanel, BorderLayout.SOUTH);
    // only use the buttons whose corresponding containers are marked "branch" in their configs
    for (JButton but : cardButts) {
      Region cfg = getConfigForComp(but);
      if (cfg.hasPropertyTagValue("branch")) {
        forkedButtSubpanel.add(but);
      } else {
        // other button
        if (otherButtonsPanel == null) {
          otherButtonsPanel = new JPanel(new BorderLayout());
          otherButtonsSubpanel = new JPanel();
          otherButtonsPanel.add(otherButtonsSubpanel, BorderLayout.SOUTH);
          JLabel padding = new JLabel();
          padding.setPreferredSize(strucPanel.getPreferredSize());
          otherButtonsPanel.add(padding, BorderLayout.CENTER);

          // add some decoration 
          forkedButtonsPanel.setBorder(ActiveDashedLineBorder);
          otherButtonsSubpanel.add(SequentialCardPanel.getStructLabel());
        }
        
        otherButtonsSubpanel.add(but);
      }
    }    
    
    // finally add the panels to cardButtonsPanel
    cardButtonsPanel.add(forkedButtonsPanel);
    cardButtonsPanel.add(otherButtonsPanel);
    
    // set first button to 'active'
    JButton firstBut = cardButts.get(0);
    updateButtonOnStateChange(firstBut, true);
    
    // change other buttons to 'inactive'
    toggleOtherButtons(firstBut, false);
  }

//  /**
//   * @effects 
//   *  Organise <tt>cardButtons</tt> on <tt>cardButtonPanel</tt> so that they 
//   *  reflect the fork pattern structure.
//   */
//  protected void layoutForkedButtons(List<JButton> cardButtons, JPanel forkedButtonsPanel) {
//     
//    JPanel buttPanels = new JPanel();
//    forkedButtonsPanel.add(buttPanels, BorderLayout.SOUTH);
//    // only use the buttons whose corresponding containers are marked "branch" in their configs
//    for (JButton but : cardButtons) {
//      Region cfg = getConfigMap().get(but);
//      if (cfg.hasPropertyTagValue("branch")) {
//        buttPanels.add(but);
//      }
//    }
//  }

  protected ActStrucLabel getStructLabel() {
    return new ForkedLabel();
  }
}
