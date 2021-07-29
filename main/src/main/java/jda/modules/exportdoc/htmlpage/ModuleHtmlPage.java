package jda.modules.exportdoc.htmlpage;

import jda.modules.exportdoc.htmlpage.model.HtmlPage;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;

/**
 * @overview
 *  A module for {@link HtmlPage}
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleHtmlPage",
modelDesc=@ModelDesc(
  model=HtmlPage.class    
),
viewDesc=@ViewDesc(
    // no GUI
    formTitle="-",
    imageIcon="-",
    on=false
),
controllerDesc=@ControllerDesc(
    openPolicy=OpenPolicy.O
),
isPrimary=true,
type=ModuleType.System
)
public class ModuleHtmlPage {
//  @AttributeDesc(label="",
//      type=JHtmlViewerField.class)
//  //private String contentString;
//  private File outputFile;
}
