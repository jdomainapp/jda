package jda.mosa.view.assets.layout;

import jda.mosa.view.assets.panels.card.CardPanel;
import jda.mosa.view.assets.panels.card.SequentialCardPanel;

/**
 * @overview
 *  A sub-type of {@link CardLayoutBuilder} that reflects the sequential activity pattern.
 *  
 * @author Duc Minh Le (ducmle)
 * 
 * @version 5.2
 */
public class SequentialLayoutBuilder extends CardLayoutBuilder {

  /**
   * @effects
   *  return a {@link SequentialCardPanel}. 
   */
  @Override
  protected CardPanel createCardPanel() {
    return new SequentialCardPanel();
  }
}
