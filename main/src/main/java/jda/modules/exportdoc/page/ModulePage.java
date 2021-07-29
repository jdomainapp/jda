package jda.modules.exportdoc.page;

import java.io.File;

import jda.modules.exportdoc.page.model.Page;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.assets.datafields.html.JHtmlViewerField;

/**
 * @overview
 *  A module used to display content stored in an output file. 
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModulePage",
modelDesc=@ModelDesc(
  model=Page.class    
),
viewDesc=@ViewDesc(
    // no GUI
    formTitle="-",
    imageIcon="-",
    on=false
),
isPrimary=true,
type=ModuleType.System
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModulePage {
  @AttributeDesc(label="",
      editable=false, // v3.2c: 
      type=JHtmlViewerField.class,
      // preferred width,height
      width=800,height=600
      )
  //private String contentString;
  private File outputFile;
}
