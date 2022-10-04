package jda.modules.mosarfrontend.vuejs;

import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ComponentGenDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.CrossTemplatesDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ModuleTemplatesDesc;
import jda.modules.mosarfrontend.vuejs.src.api.moduleAPIGen;
import jda.modules.mosarfrontend.vuejs.src.components.module.addGen;
import jda.modules.mosarfrontend.vuejs.src.components.module.indexGen;
import jda.modules.mosarfrontend.vuejs.src.components.module.listGen;
import jda.modules.mosarfrontend.vuejs.src.components.module.template.addHtmlGen;
import jda.modules.mosarfrontend.vuejs.src.components.module.template.indexHtmlGen;
import jda.modules.mosarfrontend.vuejs.src.components.module.template.listHtmlGen;
import jda.modules.mosarfrontend.vuejs.src.constants.messageGen;
import jda.modules.mosarfrontend.vuejs.src.layouts.headerGen;
import jda.modules.mosarfrontend.vuejs.src.model.modelGen;
import jda.modules.mosarfrontend.vuejs.src.router.componentGen;
import jda.modules.mosarfrontend.vuejs.src.router.nameGen;
import jda.modules.mosarfrontend.vuejs.src.router.pathGen;
import jda.modules.mosarfrontend.vuejs.src.router.routerGen;

@AppTemplateDesc(
        templateRootFolder = "fe/vuejs",
        resource = "fe/vuejs/vueTemplateResources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        genClasses = {routerGen.class, pathGen.class, nameGen.class, headerGen.class}
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
                        genClasses = {addGen.class, addHtmlGen.class}
                ),
                Main = @ComponentGenDesc(
                        genClasses = {indexGen.class, indexHtmlGen.class}
                ),
                Entity = @ComponentGenDesc(
                        genClasses = {modelGen.class}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {moduleAPIGen.class}
                )
        )
)
public class VueAppTemplate {
}
