package jda.modules.mosarfrontend.angular;
import jda.modules.mosarfrontend.angular.templates.src.AppRoutingTemplate;
import jda.modules.mosarfrontend.angular.templates.src.modules.MainModuleTs;
import jda.modules.mosarfrontend.angular.templates.src.AppHtmlTemplate;
import jda.modules.mosarfrontend.angular.templates.src.AppModuleTemplate;
import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ComponentGenDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.CrossTemplatesDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ModuleTemplatesDesc;

@AppTemplateDesc(
        templateRootFolder = "D:\\Laptrinh\\4_DDD\\jda_update\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\angular\\templates",
        resource = "D:\\Laptrinh\\4_DDD\\jda_update\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\angular\\resources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        templates = {},
                        genClasses = {AppRoutingTemplate.class}
                ),
                BaseService = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        templates = {},
                        genClasses = {AppModuleTemplate.class, AppHtmlTemplate.class}
                )
        ),
        moduleTemplates = @ModuleTemplatesDesc(
                List = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                ),
                Form = @ComponentGenDesc(
                        templates = {},
                        genClasses = {MainModuleTs.class}
                ),
                Main = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
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
