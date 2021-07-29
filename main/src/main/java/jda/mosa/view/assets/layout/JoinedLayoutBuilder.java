package jda.mosa.view.assets.layout;

import jda.mosa.view.assets.panels.card.CardPanel;
import jda.mosa.view.assets.panels.card.JoinedCardPanel;

/**
 * @overview
 *  A sub-type of {@link CardLayoutBuilder} that reflects the joined activity pattern.
 *  
 * @author Duc Minh Le (ducmle)
 * 
 * @version 5.2
 */
public class JoinedLayoutBuilder extends CardLayoutBuilder {

  /**
   * @effects 
   *  return new {@link JoinedCardPanel} object.
   */
  @Override
  protected CardPanel createCardPanel() {
    return new JoinedCardPanel();
  }
  
}
