package jda.mosa.view.assets.layout;

import jda.mosa.view.assets.panels.card.CardPanel;
import jda.mosa.view.assets.panels.card.ForkedCardPanel;

/**
 * @overview
 *  A sub-type of {@link CardLayoutBuilder} that reflects the forked activity pattern.
 *  
 * @author Duc Minh Le (ducmle)
 * 
 * @version 5.2
 */
public class ForkedLayoutBuilder extends CardLayoutBuilder {

  /**
   * @effects 
   *  return new {@link ForkedCardPanel} object.
   */
  @Override
  protected CardPanel createCardPanel() {
    return new ForkedCardPanel();
  }
  
}
