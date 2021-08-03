package jda.modules.restfstool.backend;

import java.util.List;
import java.util.function.Consumer;

import jda.modules.restfstool.backend.generators.RESTfulBackEndGenerator;
import jda.modules.restfstool.config.GenerationMode;
import jda.modules.restfstool.config.LangPlatform;
import jda.modules.restfstool.config.RFSGenConfig;

/**
 * @overview 
 *  Generate a RESTful Java back-end.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class BEGen {
  
  @Deprecated
  public void run(String backendTargetPackage, String backendOutputPath,
      Class[] model, 
      Consumer<List<Class>> callBack) {
    System.out.println("------------");

    RESTfulBackEndGenerator generator = new RESTfulBackEndGenerator(
            LangPlatform.SPRING,
            GenerationMode.SOURCE_CODE,
            backendTargetPackage,
            backendOutputPath);
    generator.setGenerateCompleteCallback(callBack);
    generator.run(model);
    System.out.println("------------");
  }

  /**
   * @effects 
   * 
   * @param model 
   */
  public BEGenOutput run(RFSGenConfig rfsGenCfg) {
//    System.out.println("------------");

    RESTfulBackEndGenerator generator = new RESTfulBackEndGenerator(
        rfsGenCfg.getLangPlatform(),
        rfsGenCfg.getGenMode(),
        rfsGenCfg.getBeTargetPackage(),
        rfsGenCfg.getBeOutputPath()
//            GenerationMode.SOURCE_CODE,
//            backendTargetPackage,
//            backendOutputPath
            );
    
    return generator.run(rfsGenCfg.getDomainModel());
    
//    System.out.println("------------");
  }
}
