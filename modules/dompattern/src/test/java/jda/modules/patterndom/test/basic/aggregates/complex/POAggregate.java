package jda.modules.patterndom.test.basic.aggregates.complex;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.patterndom.assets.aggregates.complex.Aggregate;
import jda.modules.patterndom.assets.domevents.Publisher;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class POAggregate extends Aggregate<PurchaseOrder> {

  /**
   * @effects 
   *
   * @version 
   */
  public POAggregate(String name, PurchaseOrder root
//      , Repository rep
      , Class<? extends Publisher>[] boundary)
      throws NotPossibleException {
    super(name, root
//        , rep
        , boundary);
  }
}
