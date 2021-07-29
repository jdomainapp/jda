package jda.modules.help;

import static jda.modules.mccl.conceptmodel.view.RegionName.Add;
import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.First;
import static jda.modules.mccl.conceptmodel.view.RegionName.Last;
import static jda.modules.mccl.conceptmodel.view.RegionName.New;
import static jda.modules.mccl.conceptmodel.view.RegionName.Next;
import static jda.modules.mccl.conceptmodel.view.RegionName.Previous;

import java.util.List;

import jda.modules.dcsl.syntax.Select;
import jda.modules.help.controller.HelpController;
import jda.modules.help.helpcontent.ModuleHelpContent;
import jda.modules.help.helpitem.ModuleHelpItem;
import jda.modules.help.model.AppHelp;
import jda.modules.help.model.HelpContent;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @overview
 *  A module configuration class for the <tt>Help</tt> module of 
 *  an application.
 *  
 *  <p>This class may be used as is.
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleHelp",
modelDesc=@ModelDesc(
  model=AppHelp.class
),
viewDesc=@ViewDesc(
    formTitle="Trợ giúp mô-đun",
    imageIcon="help.gif",
    viewType=RegionType.Data, 
    view=View.class,
    parent=RegionName.Help,
    widthRatio=0.75f,
    //heightRatio=0.75f,
    topX=0.125d,  // middle of screen
    //topY=0.125d,
    excludeComponents={
      New, Add, Delete, First, Next, Last, Previous,  
      //Actions
    }
),
controllerDesc=@ControllerDesc(
  controller=HelpController.class  
),
isPrimary=true,
type=ModuleType.System
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
,childModules={
  //ModuleConfiguration.class,
  ModuleHelpContent.class,
  ModuleHelpItem.class
}
)
public class ModuleHelp {
  @AttributeDesc(label="Trợ giúp")
  private String title;
  
  @AttributeDesc(label="Chương trình",
      type=JComboField.class,
      //editable=false,
      ref=@Select(clazz=Configuration.class,attributes={"appName"})
      )
  private Configuration config;
  
  @AttributeDesc(label="Các mô-đun <br>chương trình")
  private List<HelpContent> helpContents;
}
