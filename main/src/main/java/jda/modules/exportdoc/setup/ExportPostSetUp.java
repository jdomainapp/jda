package jda.modules.exportdoc.setup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.exportdoc.htmlpage.ModuleHtmlPage;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.setup.commands.SetUpCommand;
import jda.modules.setup.model.SetUpBasic;
import jda.mosa.view.assets.GUIToolkit;

public class ExportPostSetUp extends SetUpCommand {

  public ExportPostSetUp(SetUpBasic su, Class moduleDescriptorCls) {
    super(su, moduleDescriptorCls);
  }

  @Override
  public void run() throws NotPossibleException {
    // create dirs: export, export/templates, export/images 
    SetUpBasic su = getSetUp();
    
    Configuration config = su.getConfig();
    
    String export = config.getExportFolder();
    /*v3.1: moved to SetUpCommand.copyExport... 
    su.createApplicationSubDir(export, true);
    
    String exportTemplates = export + File.separator + DIR_EXPORT_TEMPLATES;
    File tempDir = su.createApplicationSubDir(exportTemplates, true);
    */
    
    // images dir
    String exportImages = export + File.separator + DIR_EXPORT_IMAGES;
    File imgDir = su.createApplicationSubDir(exportImages, true);

    // copy: html template files -> export/templates
    Class rootCls = ModuleHtmlPage.class;
    String exportTemplatesPath = getExportTemplatePath();
    
    //v3.1: su.copyFiles(rootCls, "view", exportTemplatesPath);
    ToolkitIO.copyFiles(rootCls, "view", exportTemplatesPath);

    // v3.1: styles dir
    //File srcStyleDir = ToolkitIO.getPath(rootCls, fileOrFolderName);
    /* v3.2c: avoid removing templates dir */
    if (!isCreatedExportDir())
      su.createApplicationSubDirPath(true, export, DIR_EXPORT_TEMPLATES);
    
    String templatesDir = export + File.separator + DIR_EXPORT_TEMPLATES;
    File targetStyleDir = su.createApplicationSubDirPath(true, templatesDir, DIR_TEMPLATE_STYLES);
    
    //File targetStyleDir = su.createApplicationSubDirPath(true, export, DIR_EXPORT_TEMPLATES, DIR_TEMPLATE_STYLES);
    
    ToolkitIO.copyFiles(rootCls, "view" + File.separator + "styles", targetStyleDir.getPath());
    
    // copy: icon -> export/images
    String srcImgFile = "logoHeader.png";
    String imgFile = "logo.png";
    String imgDirPath = imgDir.getPath();
    String destFile;
    
    InputStream fins = GUIToolkit.getImageFileAsStream(srcImgFile);
    if (fins != null) {
      destFile = imgDirPath + File.separator + imgFile;
      try {
        su.copyFile(fins, destFile);
      } catch (IOException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_COPY_FILE, e, 
            new Object[] {srcImgFile, destFile});
      }
    }
    
    // v3.0: copy language resource files resource files
    copyLanguageResourceFiles();
  }
}
