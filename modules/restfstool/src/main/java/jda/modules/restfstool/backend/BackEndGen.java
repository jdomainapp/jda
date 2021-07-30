package jda.modules.restfstool.backend;

import java.util.List;
import java.util.function.Consumer;

import jda.modules.restfstool.backend.annotations.bridges.TargetType;
import jda.modules.restfstool.backend.generators.GenerationMode;
import jda.modules.restfstool.backend.generators.WebServiceGenerator;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class BackEndGen {
  public void run(String backendTargetPackage, String backendOutputPath,
      Class[] model, 
      Consumer<List<Class>> callBack) {
    System.out.println("------------");

    WebServiceGenerator generator = new WebServiceGenerator(
            TargetType.SPRING,
            GenerationMode.SOURCE_CODE,
            backendTargetPackage,
            backendOutputPath);
    generator.setGenerateCompleteCallback(callBack);
    generator.generateWebService(model);
    System.out.println("------------");
  }
}
