package jda.modules.restfstool.frontend;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.restfstool.frontend.bootstrap.ViewBootstrapper;

/**
 * @overview
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class FrontEndGen implements ModuleToolable {

  /**
   * @effects
   * 
   * @param
   * <pre>
   *  0: front end output path
   *  1: Class[] of domain model
   *  2: SCC of software
   *  3: MCC of main module
   *  4: Class[] of functional MCCs
   * </pre>
   */
  @Override
  public Object exec(Object... args) throws NotPossibleException {
    String frontEndOutputPath = (String) args[0];
    Class<?>[] model = (Class<?>[]) args[1];
    Class<?> scc = (Class) args[2];
    Class<?> mainMCC = (Class<?>) args[3];
    Class<?>[] funcMCCs = (Class<?>[]) args[4];

    return run(frontEndOutputPath, model, scc, mainMCC, funcMCCs);
  }

  /**
   * @effects 
   * 
   */
  public Object run(String frontEndOutputPath, Class<?>[] model, Class<?> scc,
      Class<?> mainMCC, Class<?>[] funcMCCs) {
    ViewBootstrapper bootstrapper = new ViewBootstrapper(frontEndOutputPath,
        scc, mainMCC, model,
        funcMCCs);

    bootstrapper.bootstrapAndSave();

    return null;
//    ViewBootstrapper bootstrapper = new ViewBootstrapper(frontEndOutputPath,
//        sccClass, ModuleMain.class, CourseManAppGenerator.models,
//        CourseManAppGenerator.modules);
//
//    bootstrapper.bootstrapAndSave();
  }

}
