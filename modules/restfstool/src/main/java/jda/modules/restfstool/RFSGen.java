package jda.modules.restfstool;

import java.util.List;
import java.util.function.Consumer;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.restfstool.backend.BEGen;
import jda.modules.restfstool.backend.BEGenOutput;
import jda.modules.restfstool.backend.BESoftware;
import jda.modules.restfstool.backend.BESpringApp;
import jda.modules.restfstool.config.RFSGenConfig;
import jda.modules.restfstool.frontend.FEGen;
import jda.modules.restfstool.frontend.utils.DomainTypeRegistry;
import jda.modules.restfstool.util.RFSGenTk;

/**
 * @overview 
 *  Automatically generates an RFS with front-end and back-end modules.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class RFSGen implements ModuleToolable {
  
  /**
   * @effects 
   *  executes the generator logic, which consists in 2 main steps: 
   *  (1) generates the front end
   *  (2) generates the back end
   */
  public void run(Class<?> scc) {
    RFSGenConfig rfsGenCfg = RFSGenTk.parseRFSGenConfig(scc);
    
    // initialisation
    RFSGenTk.init(rfsGenCfg.getDomainModel());
    
    // run front-end
    new FEGen().run(rfsGenCfg);
    
    // generate back-end
    BEGenOutput beOut = new BEGen().run(rfsGenCfg);
    
    // run back end
    Class<? extends BESpringApp> appCls = rfsGenCfg.getBEAppClass();
    
    if (appCls != null) {
      new BESoftware().run(beOut.getComponents(), 
          appCls,
          rfsGenCfg.getDomainModel());
    }
  }
  
  @Deprecated
  @Override
  public Object exec(Object... args) throws NotPossibleException {
    String frontEndOutputPath = (String) args[0];
    Class<?>[] model = (Class<?>[]) args[1];
    Class<?>[] auxModel = (Class<?>[]) args[2];
    Class<?> scc = (Class) args[3];
    Class<?> mainMCC = (Class<?>) args[4];
    Class<?>[] funcMCCs = (Class<?>[]) args[5];
    //
    String backendTargetPackage = (String) args[6];
    String backendOutputPath = (String) args[7];
    Consumer<List<Class>> runCallBack = (Consumer<List<Class>> ) args[8];
    
//    FrontendGenerator.setupAndGen();
//    BackendApp.setupAndRun();
    
    run(frontEndOutputPath, model, auxModel, scc, mainMCC, funcMCCs, 
        backendTargetPackage, backendOutputPath, runCallBack
        );
    return null;
  }

  /**
   * @effects 
   *  executes the generator logic, which consists in 2 main steps: 
   *  (1) generates the front end
   *  (2) generates the back end
   * @deprecated
   */
  public void run(String frontEndOutputPath, Class<?>[] model, Class<?>[] auxModel, 
      Class<?> scc,
      Class<?> mainMCC, Class<?>[] funcMCCs, String backendTargetPackage,
      String backendOutputPath, Consumer<List<Class>> runCallBack) {
    // initialisation
    
    init(model, auxModel);
    
    // run front-end
    new FEGen().run(frontEndOutputPath, model, scc, mainMCC, funcMCCs);
    
    // run back-end
    new BEGen().run(backendTargetPackage, backendOutputPath, model,
        runCallBack);    
  }

  /**
   * @effects 
   * @deprecated
   */
  private void init(Class<?>[] model, Class<?>[] auxModel) {
    DomainTypeRegistry regist = DomainTypeRegistry.getInstance();
    regist.addDomainTypes(model);
    for (Class<?> other : auxModel) {
      regist.addDomainType(other);
    }
  }
}
