package jda.modules.helpviewer;

import static jda.modules.mccl.conceptmodel.view.RegionName.Actions;
import static jda.modules.mccl.conceptmodel.view.RegionName.Add;
import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.First;
import static jda.modules.mccl.conceptmodel.view.RegionName.HelpButton;
import static jda.modules.mccl.conceptmodel.view.RegionName.Last;
import static jda.modules.mccl.conceptmodel.view.RegionName.New;
import static jda.modules.mccl.conceptmodel.view.RegionName.Next;
import static jda.modules.mccl.conceptmodel.view.RegionName.Open;
import static jda.modules.mccl.conceptmodel.view.RegionName.Previous;
import static jda.modules.mccl.conceptmodel.view.RegionName.Update;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.exportdoc.page.ModulePage;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.helpviewer.controller.command.OpenHelpViewerDctlCommand;
import jda.modules.helpviewer.model.print.PrintDesc;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.MCCLConstants.PageFormat;
import jda.modules.mccl.syntax.MCCLConstants.PaperSize;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.controller.assets.helper.objectbrowser.SingularIdPooledObjectBrowser;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.SingleDataComponentLayoutBuilder;

/**
 * @overview
 *  A module used to display help content stored in an html file. 
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleHelpViewer",
modelDesc=@ModelDesc(
  model=Page.class  
),
viewDesc=@ViewDesc(
    formTitle="Trợ giúp",
    imageIcon="help.gif",
    viewType=RegionType.Data, 
    view=View.class,
    //parent=RegionName.Tools,
    // hide buttons
    excludeComponents = {
      Open, First, Last, Next, Previous, 
      Add, New, Delete, Update, 
      //Export, 
      HelpButton,// v3.2: required
      Actions
    },
    layoutBuilderType=SingleDataComponentLayoutBuilder.class,
    topX=0.0d, topY=0.0d,widthRatio=0.7f,heightRatio=0.9f
    ,props={ // v3.2: create the view on start-up 
      @PropertyDesc(name=PropertyName.view_createOnStartUp,valueAsString="true",valueType=Boolean.class)}
),
controllerDesc=@ControllerDesc(
  controller=Controller.class,
  openPolicy=OpenPolicy.O,      // open all objects
  defaultCommand=LAName.Open,   // call open() when run
  objectBrowser=SingularIdPooledObjectBrowser.class,  // a single object is recorded in buffer at a time
  props={
    // action performable: LAName.Help (only)
    @PropertyDesc(name=PropertyName.controller_dataController_actions,valueType=LAName[].class,valueAsString="HelpButton"),
    // always handle action 
    @PropertyDesc(name=PropertyName.controller_dataController_isCheckActionPerformed,valueType=Boolean.class,valueAsString="false"),
    // customise the help() command 
    @PropertyDesc(name=PropertyName.controller_dataController_help,valueIsClass=OpenHelpViewerDctlCommand.class,valueType=Class.class,valueAsString=CommonConstants.NullValue)
  }  
),
isPrimary=true,
type=ModuleType.System
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
@PrintDesc(
    pageFormat=PageFormat.Portrait,
    paperSize=PaperSize.A4
)
public class ModuleHelpViewer extends ModulePage {
  // inherited
}
