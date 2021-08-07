package jda.modules.setup.model;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionMap;
import jda.modules.mccl.syntax.ModuleDescriptor;

public class MasterSetUpConfig extends SetUpConfig {

  /**
   * Use this constructor to load configuration to run the application
   */
  public MasterSetUpConfig(DODMBasic schema) {
    super(schema);
  }
  
  /**
   * Use this constructor to create configuration at set-up
   */
  public MasterSetUpConfig(DODMBasic schema, Configuration config) {
    super(schema, config);
  }

  /**
   * @effects 
   *  same as super-type except that the Region objects are cloned so that they 
   *  do not intefere with the target setup  
   */
  @Override
  protected Map<String, Region> getRegionConstants() throws NotPossibleException {
    Map<String,Region> objects = super.getRegionConstants();
    
    if (objects != null) {
      try {
        /* clone in two steps:
         * (1) : clone just the regions
         * (2) : clone the region maps  
         */
        
        // (1) clone regions
        Map<String,Region> cloneObjects = objects.getClass().newInstance();
        Region region, cloned;
        for (Entry<String,Region> e : objects.entrySet()) {
          region = e.getValue();
          cloned = region.clone();
          
          cloneObjects.put(e.getKey(), cloned);
        }
        
        
        // (2) clone region maps
        RegionMap rmap;
        Collection<Region> regions = cloneObjects.values();
        
        for (Region r : regions) {
          r.cloneMappings(regions);
        }
        
        return cloneObjects;
      } catch (InstantiationException | IllegalAccessException e) {
        // should not happen
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, new Object[] {objects.getClass(), ""});
      }
    } else {
      return null;
    }
  }

  @Override 
  protected void postSetUpModule(SetUpBasic su, Class moduleDescrCls,
      ModuleDescriptor moduleCfg) throws NotPossibleException {
    //TODO: change this if there are significant resources needed to run set-up
    // for now do nothing, i.e set-up will use the default settings of each module.
    // The reason is because set-up does not run as a full-featured program as such
    // and thus requires no application folder to created. This folder is however needed 
    // to perform post-setup tasks (e.g. copying files)
  }
}
