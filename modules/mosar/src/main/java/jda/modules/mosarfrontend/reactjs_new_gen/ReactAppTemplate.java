package jda.modules.mosarfrontend.reactjs_new_gen;

import jda.modules.mosarfrontend.common.anotation.template_desc.*;
import jda.modules.mosarfrontend.reactjs_new_gen.src.AppGen;
import jda.modules.mosarfrontend.reactjs_new_gen.src.modules.FormGen;
import jda.modules.mosarfrontend.reactjs_new_gen.src.modules.ListViewGen;
import jda.modules.mosarfrontend.reactjs_new_gen.src.modules.SubmoduleGen;
import jda.modules.mosarfrontend.reactjs_new_gen.src.modules.indexGen;

@AppTemplateDesc(
        resource = "fe/react/resources.zip",
        templateRootFolder = "fe/react",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        genClasses = {AppGen.class}
                ),
                BaseService = @ComponentGenDesc(
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {}
                )
        ),
        moduleTemplates = @ModuleTemplatesDesc(
                List = @ComponentGenDesc(
                        genClasses = {ListViewGen.class}
                ),
                Form = @ComponentGenDesc(
                        genClasses = {FormGen.class}
                ),
                Main = @ComponentGenDesc(
                        genClasses = {indexGen.class}
                ),
                Entity = @ComponentGenDesc(
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {}
                )
        ),
        moduleFieldTemplates = @ModuleFieldTemplateDesc(
                Ext = @ComponentGenDesc(
                        genClasses = {SubmoduleGen.class}
                )
        )
)
public class ReactAppTemplate {
}
