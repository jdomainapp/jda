package jda.modules.mosarfrontend.angular;

import jda.modules.mosarfrontend.angular.app.AppComponentGen;
import jda.modules.mosarfrontend.angular.app.AppModuleGen;
import jda.modules.mosarfrontend.angular.app.AppRoutingModuleGen;
import jda.modules.mosarfrontend.angular.app.module.form.formComponentGen;
import jda.modules.mosarfrontend.angular.app.module.form.formComponentHtmlGen;
import jda.modules.mosarfrontend.angular.app.module.models.ModelGen;
import jda.modules.mosarfrontend.angular.app.module.moduleComponentGen;
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
                        genClasses = {}
                ),
                Entity = @ComponentGenDesc(
                        genClasses = {ModelGen.class}
                ),
                Main = @ComponentGenDesc(
                        genClasses = {}
                )
        )
)
public class AngularAppTemplate {
}
