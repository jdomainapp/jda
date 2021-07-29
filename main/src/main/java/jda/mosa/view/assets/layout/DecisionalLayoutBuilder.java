package jda.mosa.view.assets.layout;

import jda.mosa.view.assets.panels.card.CardPanel;
import jda.mosa.view.assets.panels.card.DecisionalCardPanel;

/**
 * @overview
 *  A sub-type of {@link CardLayoutBuilder} that reflects the decisional activity pattern.
 *  
 * @author Duc Minh Le (ducmle)
 * 
 * @version 5.2
 */
public class DecisionalLayoutBuilder extends CardLayoutBuilder {

  /**
   * @effects 
   *  return new {@link DecisionalCardPanel} object.
   */
  @Override
  protected CardPanel createCardPanel() {
    return new DecisionalCardPanel();
  }
  
}
