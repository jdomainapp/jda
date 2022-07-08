package jda.modules.mosarfrontend.vuejs;

import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ComponentGenDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.CrossTemplatesDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ModuleTemplatesDesc;
import jda.modules.mosarfrontend.vuejs.templates.src.api.moduleAPIGen;
import jda.modules.mosarfrontend.vuejs.templates.src.components.module.addGen;
import jda.modules.mosarfrontend.vuejs.templates.src.components.module.editGen;
import jda.modules.mosarfrontend.vuejs.templates.src.components.module.indexGen;
import jda.modules.mosarfrontend.vuejs.templates.src.components.module.listGen;
import jda.modules.mosarfrontend.vuejs.templates.src.components.module.template.addHtmlGen;
import jda.modules.mosarfrontend.vuejs.templates.src.components.module.template.editHtmlGen;
import jda.modules.mosarfrontend.vuejs.templates.src.components.module.template.indexHtmlGen;
import jda.modules.mosarfrontend.vuejs.templates.src.components.module.template.listHtmlGen;
import jda.modules.mosarfrontend.vuejs.templates.src.constants.messageGen;
import jda.modules.mosarfrontend.vuejs.templates.src.layouts.headerGen;
import jda.modules.mosarfrontend.vuejs.templates.src.model.form.formGen;
import jda.modules.mosarfrontend.vuejs.templates.src.model.modelGen;
import jda.modules.mosarfrontend.vuejs.templates.src.router.componentGen;
import jda.modules.mosarfrontend.vuejs.templates.src.router.nameGen;
import jda.modules.mosarfrontend.vuejs.templates.src.router.pathGen;
import jda.modules.mosarfrontend.vuejs.templates.src.router.routerGen;

@AppTemplateDesc(
        templateRootFolder = "D:\\JDA\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\vuejs\\templates",
        resource = "D:\\JDA\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\vuejs\\vueTemplateResources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        genClasses = {routerGen.class, pathGen.class, nameGen.class, headerGen.class}
                ),
                BaseService = @ComponentGenDesc(
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {componentGen.class, messageGen.class}
                )
        ),
        moduleTemplates = @ModuleTemplatesDesc(
                List = @ComponentGenDesc(
                        genClasses = {listGen.class, listHtmlGen.class}
                ),
                Form = @ComponentGenDesc(
                        genClasses = {addGen.class, addHtmlGen.class, editGen.class, editHtmlGen.class}
                ),
                Main = @ComponentGenDesc(
                        genClasses = {indexGen.class, indexHtmlGen.class}
                ),
                Entity = @ComponentGenDesc(
                        genClasses = {modelGen.class, formGen.class}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {moduleAPIGen.class}
                )
        )
)
public class VueAppTemplate {
}
