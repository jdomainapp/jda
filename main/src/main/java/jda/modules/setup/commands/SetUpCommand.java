package jda.modules.setup.commands;

import java.io.File;
import java.io.IOException;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpBasic.MessageCode;

/**
 * @overview 
 *    Represents a setup command.
 *     
 * @author dmle
 */
public abstract class SetUpCommand {
  // application sub-dir names
  protected static final String DIR_EXPORT_TEMPLATES = "templates";
  protected static final String DIR_EXPORT_IMAGES = "images";
  protected static final String DIR_TEMPLATE_STYLES = "styles";
  private static final String DIR_CONFIG_MODULES = "modules";
  private static final String DIR_CONFIG_MODULES_RESOURCES = "resources";
  
  // v3.1: a flag used to quickly check if the application folder has been created
  private static boolean createdAppFolder;
  private static boolean createdExportDir;
  private static boolean createdConfigDir;
  private static boolean createdModulesDir;
  
  private SetUpBasic su;
  
  /** the module descriptor class of the module in which this command is defined */
  private Class moduleDescriptorCls;

  public SetUpCommand(SetUpBasic su, Class moduleDescriptorCls) {
    this.su = su;
    this.moduleDescriptorCls = moduleDescriptorCls;
  }
  
  /**
   * @effects 
   *  create and return a <tt>Command</tt> whose type is <tt>cmdCls</tt> and that 
   *  uses <tt>su</tt> to run.
   */
  public static SetUpCommand createInstance(Class<? extends SetUpCommand> cmdCls, 
      SetUpBasic su
      , Class moduleDescriptorClass // v3.0
      ) throws NotPossibleException {
    try {
      // invoke the constructor to create object 
      SetUpCommand instance = cmdCls.getConstructor(SetUpBasic.class, Class.class).newInstance(su, moduleDescriptorClass);
      
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {cmdCls.getSimpleName(), su});
    }
  }
  
  protected SetUpBasic getSetUp() {
    return su;
  }

  /**
   * @effects 
   *  return the module descriptor class of the module in which this command is defined
   */
  public Class getModuleDescriptorCls() {
    return moduleDescriptorCls;
  }

  /**
   * Sub-types should override if required
   * 
   * @effects 
   *  Copy all necessary set-up resources to application folder and perform 
   *  other tasks (if needed)
   */
  public abstract void run() throws NotPossibleException;
  
  /**
   * @effects 
   *  copy to the application folder all resource files that are stored relative to the source folder 
   *  of {@link #moduleDescriptorCls}
   */
  protected void copyResourceFiles() throws ApplicationRuntimeException {
    // check pre-conditions
    if (!createdAppFolder) {
      su.createApplicationDirIfNotExist();
      createdAppFolder = true;
    }
    
    //TODO: support other resource files if needed
    copyExportResourceFiles(moduleDescriptorCls);
    
    // v3.0: support language-specific resource files
    copyLanguageResourceFiles(moduleDescriptorCls);
  }

  /**
   * @effects 
   *  copy to the application folder all language-specific resource files that are 
   *  stored relative to the source folder of {@link #moduleDescriptorCls}
   */
  protected void copyLanguageResourceFiles() {
    copyLanguageResourceFiles(moduleDescriptorCls);
  }
  
  /**
   * @requires 
   *  <tt>moduleDescrCls</tt> is a Module configuration class /\
   *  application folder has been created
   *   
   * @effects <pre>
   *    if language support is configured
   *      create (delete if exists) a language resource folder (langDir) for the module specified by moduleDescrCls  
   *      copy to <tt>langDir</tt> all <b>language</b>-related resource files that are stored relative to the source folder 
   *        of <tt>moduleDescrCls</tt>
   *        </pre>
   *    <p>throws NotFoundException if no module configuration is found in <tt>moduleDescrCls</tt>
   */
  private void copyLanguageResourceFiles(Class moduleDescrCls) 
      throws NotFoundException, NotPossibleException {
    SetUpBasic su = getSetUp();
    Configuration config = su.getConfig();
    
    // create config folder if not exists
    String configDir = config.getConfigFolder();
    /* v3.1: added check to ease performance
    su.createApplicationSubDir(configDir, false);
    */
    if (!createdConfigDir) {
      su.createApplicationSubDir(configDir, false);
      createdConfigDir = true;
    }
    
    // create modules folder if not exists
    String modulesDirName = DIR_CONFIG_MODULES;
    /* v3.1: added check to ease performance
    su.createApplicationSubDirPath(false, configDir, modulesDirName);
    */
    if (!createdModulesDir) {
      su.createApplicationSubDirPath(false, configDir, modulesDirName);
      createdModulesDir = true;
    }
    
    String modulesDirSubPath = configDir + File.separator + modulesDirName;
    
    // create target module folder
    String moduleName = su.getModuleDescriptor(moduleDescrCls).name();
    File moduleConfigPath = su.createApplicationSubDirPath(true, 
        modulesDirSubPath, 
        moduleName);
    
    // language-related resource file folder
    /*v3.1: support jar-typed path
    
    String resourceDirPath = ToolkitIO.getPath(moduleDescrCls, DIR_CONFIG_MODULES_RESOURCES);
    
    if (resourceDirPath != null) {
      try {
        // copy to target 
        String targetResourceDirPath = path.getPath() + File.separator + DIR_CONFIG_MODULES_RESOURCES; 
        su.copyDir(new File(resourceDirPath), new File(targetResourceDirPath));
      } catch (IOException e) {
        // failed to copy file
        su.log(MessageCode.UNDEFINED, Toolkit.getStackTrace(e, "utf-8"));
      }
    }
    */
    try {
      ToolkitIO.copyDir(moduleDescrCls, DIR_CONFIG_MODULES_RESOURCES, moduleConfigPath.getPath());
    } catch (IOException e) {
      // failed to copy file
      su.log(MessageCode.UNDEFINED, ToolkitIO.getStackTrace(e, "utf-8"));
    }
  }

  /**
   * @effects 
   *  copy to the application folder all <b>export</b>-related resource files that are stored relative to the source folder 
   *  of <tt>rootCls</tt>
   */
  private void copyExportResourceFiles(Class rootCls) {
    // v3.1: check pre-conditions
    // create export path (if not already)
    if (!createdExportDir) {
      String export = su.getConfig().getExportFolder();
      su.createApplicationSubDir(export, true);
      
      String exportTemplates = export + File.separator + DIR_EXPORT_TEMPLATES;
      su.createApplicationSubDir(exportTemplates, true);
      
      createdExportDir = true;
    }
    
    // copy: html template files -> export/templates
    String exportTemplatesPath = getExportTemplatePath();
    
    String fileDir = "view";

    //SetUpBasic su = getSetUp();
    
    // v3.1: su.copyFiles(rootCls, fileDir, exportTemplatesPath);
    ToolkitIO.copyFiles(rootCls, fileDir, exportTemplatesPath);
  }
  
  /**
   * @effects
   *  if export is created with a sub-dir named templates 
   *    return true
   *  else
   *    return false
   *    
   * @version 3.2c
   */
  protected boolean isCreatedExportDir() {
    return createdExportDir;
  }
  
  /**
   * @effects
   *  return the absolute path to the "templates" folder of the export function.
   */
  protected String getExportTemplatePath() {
    SetUpBasic su = getSetUp();
    
    Configuration config = su.getConfig();
    
    String exportPath = config.getExportFolderPath();
    String exportTemplatesPath = exportPath + File.separator + DIR_EXPORT_TEMPLATES; //"templates";
    
    return exportTemplatesPath;
  }
}
