package jda.mosa.view.assets.builder;

import jda.modules.common.types.Tuple2;
import jda.mosa.view.assets.JDataContainer;

/**
 * @overview 
 *  A sub-type of {@link Tuple2} whose {@link #toString()} is customised to return 
 *  a user-friendly string that can be used as a label of this. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
public class LabelledContInfo extends Tuple2<String, JDataContainer>{

  public LabelledContInfo(String u, JDataContainer v)
      throws IllegalArgumentException {
    super(u, v);
  }

  /**
   * This is method is used to obtain the label of this object (for display).
   * 
   * @effects
   *  return {@link #getFirst()}
   */
  @Override
  public String toString() {
    return getFirst();
  }
  
  
}
