package jda.mosa.view.assets.panels.card;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

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
 * @version 5.2 
 */
public class ForkedCardPanel extends DecisionalCardPanel {

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
    // TODO: customise this when needed
    super.createCardButtonsPanel();
  }

  @Override
  protected ActStrucLabel getStructLabel() {
    return new ForkedLabel();
  }
}
