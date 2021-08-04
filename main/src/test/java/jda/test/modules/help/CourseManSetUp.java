package jda.test.modules.help;

import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.mosa.view.assets.GUIToolkit;
import jda.util.SwTk;

public class CourseManSetUp extends jda.modules.setup.model.SetUp {

  private static final String DBName = "data" + SEP + "CourseManTest";

  private static final boolean debug = Toolkit.getDebug(CourseManSetUp.class);

  /** domain-specific configuration settings */
  private static final Class[][] moduleDescriptors = new Class[][] {
  // group 2: main and data modules
  { ModuleCourseMan.class, // main
      ModuleCity.class, // v2.7.3

  }, };

  public CourseManSetUp() throws NotPossibleException, NotFoundException {
    super();
  }

  @Override
  public Configuration createInitApplicationConfiguration() {
    Configuration config = SwTk.createSimpleConfigurationInstance("CourseMan", DBName); //new Configuration(DBName);
    return config;
  }

  @Override
  public void createApplicationConfiguration() throws NotPossibleException,
      NotFoundException {
    /*
     * Language configuration: Vietnamese (Default): requires no
     * LabelConstantClass to be specified Other languages (e.g. English):
     * requires a specification of the DomainLabelConstants class and the
     * definition of a LabelConstants class suitable for that language in a
     * sub-package of the domainapp.setup.data.lang package See the two examples
     * for Vietnamese and English in the two sub-packages:
     * domainapp.setup.data.lang.vn and domainapp.setup.data.lang.en
     */
    // set language and label constant class for that language
    final Language Lang =
    // Language.English;
    Language.Vietnamese;
  //v3.0: final String labelConstantClass =
    // vn.com.courseman.setup.config.lang.vi.DomainLabelConstants.class.getName();
    //null;

    config = createInitApplicationConfiguration();

    config.setLanguage(Lang);
  //v3.0: config.setLabelConstantClass(labelConstantClass);

    // the default
    // config.setListSelectionTimeOut(25);
    config.setMainGUISizeRatio(0.75);
    // config.setChildGUISizeRatio(0.75);
    config.setUseSecurity(false);
    // config.setUseSecurity(false);

    /* comment these out will cause the display of Login dialog */
    config.setUserName("duclm");
    config.setPassword("duclm");

    // config.setDefaultModule("ModuleDomainApplicationModule");

    // organisation
    /*
     * If organisation uses a logo picture (preferred format: gif) then
     * GUIToolkit.initInstance(conig) must be invoked first (see below)
     */
    GUIToolkit.initInstance(config);

    config.setOrganisation("Faculty of IT",
        null,
        "km9 Đường Nguyễn Trãi, Quận Thanh Xuân", "http://fit.hanu.edu.vn");
    validate();
  }

  // @Override
  // protected String getDBName() {
  // return DBName;
  // }

 

  @Override
  public Class[] getModelClasses() {
    return getModelClasses(moduleDescriptors);
  }

  @Override
  public List<List<Class>> getModuleDescriptors() {
    // return appModel;
    return getModuleDescriptors(moduleDescriptors);
  }
 
  // main
  public static void main(String[] args) {
    CourseManSetUp su = new CourseManSetUp();
    try {
      run(su, args);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
