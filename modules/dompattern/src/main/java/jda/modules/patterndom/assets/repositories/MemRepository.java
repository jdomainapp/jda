package jda.modules.patterndom.assets.repositories;

import jda.util.SwTk;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class MemRepository extends JdaRepository {
  
  protected MemRepository(String appName) {
    super(SwTk.createMemoryBasedConfiguration(appName));
  }
 
}
