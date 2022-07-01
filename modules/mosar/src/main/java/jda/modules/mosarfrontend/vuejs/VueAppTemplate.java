package jda.modules.mosarfrontend.vuejs;

import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ComponentGenDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.CrossTemplatesDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ModuleTemplatesDesc;
<<<<<<< Updated upstream
import jda.modules.mosarfrontend.vuejs.templates.src.layouts.headerGen;
import jda.modules.mosarfrontend.vuejs.templates.src.router.componentGen;
import jda.modules.mosarfrontend.vuejs.templates.src.router.nameGen;
import jda.modules.mosarfrontend.vuejs.templates.src.router.pathGen;
import jda.modules.mosarfrontend.vuejs.templates.src.router.routerGen;
=======
import jda.modules.mosarfrontend.reactnative.templates.src.data_types.DataTypeGen;
import jda.modules.mosarfrontend.reactnative.templates.src.data_types.enums.ModulesGen;
import jda.modules.mosarfrontend.vuejs.templates.src.Router;
>>>>>>> Stashed changes

@AppTemplateDesc(
        templateRootFolder = "D:\\JDA\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\vuejs\\templates",
        resource = "D:\\JDA\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\vuejs\\vueTemplateResources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        templates = {},
                        genClasses = {routerGen.class, pathGen.class, nameGen.class, headerGen.class}
                ),
                BaseService = @ComponentGenDesc(
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {componentGen.class}
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
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                )
        )
)
public class VueAppTemplate {
}
