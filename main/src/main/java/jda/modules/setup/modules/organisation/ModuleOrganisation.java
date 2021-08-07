package jda.modules.setup.modules.organisation;

import javax.swing.ImageIcon;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.assets.datafields.JImageField;

@ModuleDescriptor(
name="ModuleOrganisation",
modelDesc=@ModelDesc(
    model=jda.modules.mccl.conceptmodel.Configuration.Organisation.class
),
viewDesc=@ViewDesc(
    on=false
//    formTitle="-",
//    imageIcon=""
//  guiType=domainapp.model.config.Region.Type.Data,
// uncomment this requires also uncommenting guiClass attribute (b/c exclusion must be associated to a GUI)
//  excludeComponents={ 
//    // exclude some tool bar buttons and the Actions panel
//    RegionName.Open, RegionName.Refresh, RegionName.New, RegionName.Update,
//    RegionName.Delete, 
//    RegionName.First, RegionName.Previous,RegionName.Next, RegionName.Last,
//    RegionName.Actions
//  },
//  guiClass=AppGUI.class,
// no gui   
    //style=StyleName.DefaultBoldOnWhite
),
type=ModuleType.System,
 isPrimary=true
 ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleOrganisation {
//  @View(label="Công ty")
//  private String title;
  
  @AttributeDesc(label="Tên"
      //styleLabel=StyleName.DefaultOnWhite
      )
  private String name;
  
  @AttributeDesc(label="Lô-gô",
      type=JImageField.class)
  private ImageIcon logo;
  
  @AttributeDesc(label="Liên hệ"//,type=JLabelField.class
      )
  private String contactDetails;
  
  @AttributeDesc(label="Trang web"//,type=JLabelField.class
      )
  private String url; 
}
