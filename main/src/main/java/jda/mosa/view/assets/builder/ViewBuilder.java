package jda.mosa.view.assets.builder;

import java.awt.Component;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.View;

/**
 * @overview 
 *  A generic view-builder for a type of view component in a given View. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public abstract class ViewBuilder {

  /**
   * @effects 
   *  construct a desired view component in <tt>view</tt> and return it.
   */
  public abstract Component build(View view, Configuration config);

}
