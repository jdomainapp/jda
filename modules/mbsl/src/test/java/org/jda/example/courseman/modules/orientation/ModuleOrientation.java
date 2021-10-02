package org.jda.example.courseman.modules.orientation;

import org.jda.example.courseman.modules.orientation.model.Orientation;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.assets.datafields.html.JHtmlViewerField;

/**
 * @Overview
 *  Module for {@link Orientation}
 * 
 * @author dmle
 */
@ModuleDescriptor(name="ModuleOrientation",
modelDesc=@ModelDesc(
    model=Orientation.class
),
viewDesc=@ViewDesc(
    formTitle="Manage Orientation",
    domainClassLabel="Orientation"
    ,imageIcon="orientation.jpg"
    //,viewType=Region.Type.Data,
    //parent=RegionName.Tools,
    //view=View.class
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true
),
isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleOrientation  {

  @AttributeDesc(label="Orientation")
  private String title;
  
//  // attributes
//  @AttributeDesc(label="Id", alignX=AlignmentX.Center)
//  private int id;
  
  @AttributeDesc(label="", 
      editable=false, 
      type=JHtmlViewerField.class,
      width=600,height=480)
  private String content;
  
}
