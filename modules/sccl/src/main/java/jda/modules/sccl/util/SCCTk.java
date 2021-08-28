package jda.modules.sccl.util;

import jda.modules.sccl.conceptualmodel.SCC;
import jda.modules.sccl.syntax.SystemDesc;
import jda.util.SwTk;

/**
 * @overview A tool kit class for {@link SCC}.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2c
 */
public class SCCTk {
  
  private static final Class<SystemDesc> SD = SystemDesc.class;

  private SCCTk() {}

  /**
   * @effects 
   *  if scc is configured with a {@link SystemDesc} annotation sm
   *    return sm
   *  else
   *    return null
   * 
   * @deprecated as of v5.4.1 (use {@link SwTk#getSystemDesc(Class)}
   */
  public static SystemDesc getSystemDescObject(Class scc) {
    SystemDesc dm = (SystemDesc) scc.getAnnotation(SD);
    
    return dm;
  }

}
