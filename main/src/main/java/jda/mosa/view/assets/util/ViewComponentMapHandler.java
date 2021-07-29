package jda.mosa.view.assets.util;

import java.awt.Component;

import jda.modules.common.collection.Map;
import jda.modules.mccl.conceptmodel.view.Region;

/**
 * @overview
 *  A helper class used to handle the registration of view component to the component map 
 *  which maps components to their view configurations.  
 *  
 * @author dmle
 *
 * @version 3.1
 */
public class ViewComponentMapHandler {

  private Map<Region, Component> compMap;

  public ViewComponentMapHandler(Map<Region, Component> compMap) {
    this.compMap = compMap;
  }

  public void add(Region compCfg, Component comp) {
    this.compMap.put(compCfg, comp);
  }
  
}
