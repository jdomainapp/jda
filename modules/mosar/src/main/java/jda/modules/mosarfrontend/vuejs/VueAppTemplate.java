package jda.modules.mosarfrontend.vuejs;

import jda.modules.mosarfrontend.angular.templates.AppHtmlTemplate;
import jda.modules.mosarfrontend.angular.templates.AppModuleTemplate;
import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ComponentGenDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.CrossTemplatesDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ModuleTemplatesDesc;
import jda.modules.mosarfrontend.reactnative.templates.MainGen;
import jda.modules.mosarfrontend.reactnative.templates.src.data_types.DataTypeGen;
import jda.modules.mosarfrontend.reactnative.templates.src.modules.FormInputsGen;
import jda.modules.mosarfrontend.reactnative.templates.src.modules.ModulesGen;

@AppTemplateDesc(
        templateRootFolder = "D:\\HOC_THS\\JDA\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\vuejs\\templates",
        resource = "D:\\HOC_THS\\JDA\\jda\\examples\\courseman\\mosar\\src\\main\\resources\\vuejs\\api.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                ),
                BaseService = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        templates = {"app.component.html"},
                        genClasses = {AppHtmlTemplate.class}
                )
        ),
        moduleTemplates = @ModuleTemplatesDesc(
                List = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                ),
                Form = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                ),
                Main = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                ),
                Entity = @ComponentGenDesc(
                        templates = {},
                        genClasses = {DataTypeGen.class}
                ),
                Ext = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                )
        )
)
public class VueAppTemplate {
}
