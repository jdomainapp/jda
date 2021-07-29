package jda.modules.dodm.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;

public class DOMFactory {
  
  // v2.8: use map
  //private static DOMBasic instance;
  private static Map<DODMConfig,DOMBasic> instanceMap = new HashMap();
  
  public static DOMBasic createDOM(DODMConfig config, DSMBasic dsm) throws NotPossibleException {
    DOMBasic instance = instanceMap.get(config);
    
    if (instance == null) {
      Class<? extends DOMBasic> domType = config.getDomType();
      
      try {
        // invoke the single-arg constructor to create object 
        instance = domType.getConstructor(DODMConfig.class, DSMBasic.class).newInstance(config, dsm);
        
        instanceMap.put(config, instance);
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
            "Không thể tạo đối tượng lớp: {0}.{1}({2})", domType.getSimpleName(), "init", config);
      }
    }
    
    return instance;
  }

  /**
   * @effects 
   *  if <tt>dom</tt> exists in the cache of this
   *    remove it
   *  else
   *    do nothing
   * @version 3.0
   */
  public static <T extends DOMBasic> void removeInstance(T dom) {
    Stack<DODMConfig> toRemove = new Stack();
    for (Entry<DODMConfig, DOMBasic> e : instanceMap.entrySet()) {
      if (e.getValue() == dom) {
        toRemove.push(e.getKey());
      }
    }

    if (!toRemove.isEmpty()) {
      for (DODMConfig k : toRemove)
        instanceMap.remove(k);
    }
  }
}
