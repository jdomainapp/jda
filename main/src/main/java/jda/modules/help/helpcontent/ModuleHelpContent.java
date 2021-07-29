package jda.modules.help.helpcontent;

import java.util.List;

import jda.modules.dcsl.syntax.Select;
import jda.modules.help.model.AppHelp;
import jda.modules.help.model.HelpContent;
import jda.modules.help.model.HelpItem;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.panels.DefaultPanel;

/**
 * @overview
 *  A template view configuration class for the <tt>HelpContent</tt> module of 
 *  an application.
 *  
 *  <p>This class may be used as is.
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleHelpContent",
modelDesc=@ModelDesc(
  model=HelpContent.class
),
viewDesc=@ViewDesc(
    formTitle="-",
    imageIcon="-",
    on=false
),
type=ModuleType.System
)
public class ModuleHelpContent {
//  @AttributeDesc(label="Trợ giúp")
//  private String title;
  
  @AttributeDesc(label="Mô-đun",
      type=JComboField.class,
      ref=@Select(clazz=ApplicationModule.class,attributes={"name"})
      )
  private ApplicationModule module;
  
  @AttributeDesc(label="Mô tả chung")
  private String overview;
  
  @AttributeDesc(label="Tiêu đề")
  private String titleDesc;

  @AttributeDesc(label="Chương trình")
  private AppHelp appHelp;
  
  @AttributeDesc(label="Nội dung",
      type=DefaultPanel.class)
  private List<HelpItem> helpItems;
}
