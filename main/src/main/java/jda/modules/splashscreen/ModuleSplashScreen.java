package jda.modules.splashscreen;

import static jda.modules.mccl.conceptmodel.view.RegionName.Actions;
import static jda.modules.mccl.conceptmodel.view.RegionName.Add;
import static jda.modules.mccl.conceptmodel.view.RegionName.Chart;
import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.Export;
import static jda.modules.mccl.conceptmodel.view.RegionName.First;
import static jda.modules.mccl.conceptmodel.view.RegionName.Last;
import static jda.modules.mccl.conceptmodel.view.RegionName.New;
import static jda.modules.mccl.conceptmodel.view.RegionName.Next;
import static jda.modules.mccl.conceptmodel.view.RegionName.ObjectScroll;
import static jda.modules.mccl.conceptmodel.view.RegionName.Previous;
import static jda.modules.mccl.conceptmodel.view.RegionName.Update;

import javax.swing.ImageIcon;

import jda.modules.mccl.conceptmodel.SplashInfo;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.modules.splashscreen.controller.SplashScreenController;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JHtmlLabelField;
import jda.mosa.view.assets.datafields.JImageField;

/**
 * @overview
 *  Module for {@link SplashInfo}.
 *   
 * @author dmle
 */
@ModuleDescriptor(name="ModuleSplashScreen",
modelDesc=@ModelDesc(
  model=SplashInfo.class
),
viewDesc=@ViewDesc(
    formTitle="Về chương trình",
    imageIcon="domainapp.jpg",
    viewType=RegionType.Data, 
    view=View.class,
    parent=RegionName.Help,
    topX=0.5d,  // middle of screen
    topY=0.5d,
    resizable=false,
    relocatable=false,
    excludeComponents={
      New, Add, Update, Delete, First, Next, Last, Previous, ObjectScroll, 
      Export, Chart,  
      Actions,
    }
),
controllerDesc=@ControllerDesc(
  controller=SplashScreenController.class,
  openPolicy=OpenPolicy.O_A,
  runTime=2000  // milli-secs
//  ,props={
//      @PropertyDesc(name=PropertyName.ctrl_ObjectScrollUpdate,valueAsString="false",valueType=Boolean.class)
//   }
),
isPrimary=true,
type=ModuleType.System
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleSplashScreen {
//  @AttributeDesc(label="Trợ giúp")
//  private String title;
  
  @AttributeDesc(label="",
      alignX=AlignmentX.Center,
      type=JImageField.class)
  private ImageIcon appLogo;

  @AttributeDesc(label="",
      alignX=AlignmentX.Center,
      styleField=StyleName.HeadingTitle,
      type=JHtmlLabelField.class
      )
  private String orgInfo;

  @AttributeDesc(label="",
      alignX=AlignmentX.Center,
      //styleField=StyleName.DefaultBold,
      type=JHtmlLabelField.class)
  private String copyrightInfo;
}
