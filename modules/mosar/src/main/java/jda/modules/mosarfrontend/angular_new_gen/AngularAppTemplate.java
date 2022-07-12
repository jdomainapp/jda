package jda.modules.mosarfrontend.angular_new_gen;

import jda.modules.mosarfrontend.angular_new_gen.app.AppComponentGen;
import jda.modules.mosarfrontend.angular_new_gen.app.AppModuleGen;
import jda.modules.mosarfrontend.angular_new_gen.app.AppRoutingModuleGen;
import jda.modules.mosarfrontend.angular_new_gen.app.module.form.formComponentGen;
import jda.modules.mosarfrontend.angular_new_gen.app.module.form.formComponentHtmlGen;
import jda.modules.mosarfrontend.angular_new_gen.app.module.moduleComponentGen;
import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ComponentGenDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.CrossTemplatesDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ModuleTemplatesDesc;

@AppTemplateDesc(
        templateRootFolder = "fe/angular",
        resource = "fe/angular/angularResources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        templates = {},
                        genClasses = {AppComponentGen.class, AppRoutingModuleGen.class}
                ),
                BaseService = @ComponentGenDesc(
                        templates = {},
                        genClasses = {AppModuleGen.class}
                ),
                Ext = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                )
        ),
        moduleTemplates = @ModuleTemplatesDesc(
                Form = @ComponentGenDesc(
                        genClasses = {formComponentGen.class, formComponentHtmlGen.class}
                ),
                Main = @ComponentGenDesc(
                        templates = {},
                        genClasses = {moduleComponentGen.class}
                ),
                Entity = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                )
        )
)
public class AngularAppTemplate {
}
