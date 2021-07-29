package jda.modules.lang;

import jda.modules.lang.controller.LanguageConfigurator;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.view.ViewDesc;

/**
 * @overview Represents application configuration parameters
 */
@ModuleDescriptor(
name="ModuleLanguageConfigurator",
viewDesc=@ViewDesc(
    formTitle="Cấu hình ngôn ngữ",
    imageIcon="",
    on=false
    // no gui
),
    //controller=domainapp.controller.lang.LanguageConfigurator.class,
    controllerDesc=@ControllerDesc(
        controller=LanguageConfigurator.class,
        isStateListener=true),
    type=ModuleType.System,
    isPrimary=true
    )
public class ModuleLanguageConfigurator {
  // empty
}
