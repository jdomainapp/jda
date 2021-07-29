package jda.mosa.view.assets.layout;

import jda.mosa.view.assets.panels.card.CardPanel;
import jda.mosa.view.assets.panels.card.MergedCardPanel;

/**
 * @overview
 *  A sub-type of {@link CardLayoutBuilder} that reflects the merged activity pattern.
 *  
 * @author Duc Minh Le (ducmle)
 * 
 * @version 5.2
 */
public class MergedLayoutBuilder extends CardLayoutBuilder {

  /**
   * @effects 
   *  return new {@link MergedCardPanel} object.
   */
  @Override
  protected CardPanel createCardPanel() {
    return new MergedCardPanel();
  }
  
}
