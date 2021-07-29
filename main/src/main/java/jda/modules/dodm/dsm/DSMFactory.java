package jda.modules.dodm.dsm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;

public class DSMFactory {

  // v2.8: use instance map
  //private static DSMBasic instance;
  private static Map<DODMConfig,DSMBasic> instanceMap = new HashMap();

  public static DSMBasic createDSM(DODMConfig config) throws NotPossibleException {
    DSMBasic instance = instanceMap.get(config);
    
    if (instance == null) {
      Class<? extends DSMBasic> dsmType = config.getDsmType();
      
      try {
        // invoke the single-arg constructor to create object 
        instance = dsmType.getConstructor(DODMConfig.class).newInstance(config);
        
        instanceMap.put(config, instance);
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
            "Không thể tạo đối tượng lớp: {0}.{1}({2})", dsmType.getSimpleName(), "init", config);
      }
    }
    
    return instance;
  }

  /**
   * @effects 
   *  if <tt>dsm</tt> exists in the cache of this
   *    remove it
   *  else
   *    do nothing
   * @version 3.0
   */
  public static <T extends DSMBasic> void removeInstance(T dsm) {
    Stack<DODMConfig> toRemove = new Stack();
    for (Entry<DODMConfig, DSMBasic> e : instanceMap.entrySet()) {
      if (e.getValue() == dsm) {
        toRemove.push(e.getKey());
      }
    }

    if (!toRemove.isEmpty()) {
      for (DODMConfig k : toRemove)
        instanceMap.remove(k);
    }
  }
}
