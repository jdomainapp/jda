package jda.modules.mosarfrontend.angular;

import jda.modules.mosarfrontend.angular.app.AppComponentGen;
import jda.modules.mosarfrontend.angular.app.AppModuleGen;
import jda.modules.mosarfrontend.angular.app.AppRoutingModuleGen;
import jda.modules.mosarfrontend.angular.app.module.components.module.ModuleHtmlGen;
import jda.modules.mosarfrontend.angular.app.module.components.module.ModuleTsGen;
import jda.modules.mosarfrontend.angular.app.module.components.module_form.FormTsGen;
import jda.modules.mosarfrontend.angular.app.module.components.module_list.ModuleListGen;
import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ComponentGenDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.CrossTemplatesDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ModuleTemplatesDesc;

@AppTemplateDesc(
        templateRootFolder = "fe/angular",
        resource = "fe/angular/resources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        genClasses = {AppComponentGen.class, AppRoutingModuleGen.class}
                ),
                BaseService = @ComponentGenDesc(
                        genClasses = {AppModuleGen.class}
                )

        ),
        moduleTemplates = @ModuleTemplatesDesc(
                Form = @ComponentGenDesc(
                        genClasses = {FormTsGen.class}
                ),
                List = @ComponentGenDesc(
                        genClasses = {ModuleListGen.class}
                ),
                Entity = @ComponentGenDesc(
                        genClasses = {}
                ),
                Main = @ComponentGenDesc(
                        genClasses = {ModuleTsGen.class, ModuleHtmlGen.class}
                )
        )
)
public class AngularAppTemplate {
}
