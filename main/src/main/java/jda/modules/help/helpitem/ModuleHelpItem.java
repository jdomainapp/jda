package jda.modules.help.helpitem;

import jda.modules.dcsl.syntax.Select;
import jda.modules.help.model.HelpContent;
import jda.modules.help.model.HelpItem;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @overview
 *  A template view configuration class for the <tt>HelpContent</tt> module of 
 *  an application.
 *  
 *  <p>This class may be used as is.
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleHelpItem",
modelDesc=@ModelDesc(
  model=HelpItem.class
),
viewDesc=@ViewDesc(
    formTitle="-",
    imageIcon="-",
    on=false
),
type=ModuleType.System
)
public class ModuleHelpItem {
//  @AttributeDesc(label="Trợ giúp")
//  private String title;
  
  @AttributeDesc(label="Thành phần <br>giao diện",
      type=JComboField.class,
      ref=@Select(clazz=Region.class,attributes={"name"})
      )
  private Region region;
  
  @AttributeDesc(label="Mô tả")
  private String description;
  
  @AttributeDesc(label="Mô đun")
  private HelpContent helpContent;
}
