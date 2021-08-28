package jda.modules.restfstool.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.restfstool.config.RFSGenConfig;
import jda.modules.restfstool.frontend.bootstrap.ViewBootstrapper;

/**
 * @overview 
 *  Executes the frontend software automation flow.
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class FESoftware {
  private RFSGenConfig cfg;

  private static Logger logger = (Logger) LoggerFactory.getLogger("module.restfstool");

  public FESoftware(RFSGenConfig cfg) {
    this.cfg = cfg;
    
  }

  /**
   * @effects 
   *  initialises this using <code>cfg</code>
   */
  public FESoftware init() {
    // do nothing 
    return this;
  }
  
  /**
   * @effects 
   *  Generates the front-end software.
   *  
   *  <p>The source code is saved to the target front-end output path pecified in <code>cfg</code>. 
   */
  public FESoftware generate() {
    
    ViewBootstrapper bootstrapper = new ViewBootstrapper(
        //frontEndOutputPath,
        cfg.getFeOutputPath(),
//        scc, 
        cfg.getSCC(),
//        mainMCC, 
        cfg.getMCCMain(),
        //model
        cfg.getDomainModel(),
//        funcMCCs
        cfg.getMCCFuncs()
        );

    bootstrapper.bootstrapAndSave();
    
    return this;
  }
  
  
  /**
   * Use this method IMMEDIATELY AFTER the generated classes have been compiled by the generator
   * @effects 
   *  run the back-end software after it has been generated by {@link #generate(RFSGenConfig)}.
   *  
   */
  public FESoftware run() {
    new FEReactApp(cfg).start();
    return this;
  }
}
