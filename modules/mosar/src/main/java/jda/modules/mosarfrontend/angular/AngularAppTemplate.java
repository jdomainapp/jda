package jda.modules.mosarfrontend.angular;

import jda.modules.mosarfrontend.angular.templates.AppHtmlTemplate;
import jda.modules.mosarfrontend.angular.templates.AppModuleTemplate;
import jda.modules.mosarfrontend.angular.templates.AppRoutingTemplate;
import jda.modules.mosarfrontend.angular.templates.modules.FormHtml;
import jda.modules.mosarfrontend.angular.templates.modules.FormTs;
import jda.modules.mosarfrontend.angular.templates.modules.MainModuleTs;
import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ComponentGenDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.CrossTemplatesDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ModuleTemplatesDesc;

@AppTemplateDesc(
        templateRootFolder = "D:\\Laptrinh\\4_DDD\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\angular\\templates",
        resource = "D:\\Laptrinh\\4_DDD\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\angular\\resources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        genClasses = {AppRoutingTemplate.class}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {AppModuleTemplate.class, AppHtmlTemplate.class}
                )
        ),
        moduleTemplates = @ModuleTemplatesDesc(
                Form = @ComponentGenDesc(
                        genClasses = {FormTs.class, FormHtml.class}
                ),
                Main = @ComponentGenDesc(
                        genClasses = {MainModuleTs.class}
                )

        )
)
public class AngularAppTemplate {
}
