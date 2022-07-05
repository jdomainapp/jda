package jda.modules.mosarfrontend.reactjs_new_gen;

import jda.modules.mosarfrontend.common.anotation.template_desc.*;
import jda.modules.mosarfrontend.reactjs_new_gen.templates.src.AppGen;
import jda.modules.mosarfrontend.reactjs_new_gen.templates.src.modules.FormGen;
import jda.modules.mosarfrontend.reactjs_new_gen.templates.src.modules.ListViewGen;
import jda.modules.mosarfrontend.reactjs_new_gen.templates.src.modules.SubmoduleGen;
import jda.modules.mosarfrontend.reactjs_new_gen.templates.src.modules.indexGen;

@AppTemplateDesc(
        resource = "D:\\JDA\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\reactjs_new_gen\\resources.zip",
        templateRootFolder = "D:\\JDA\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\reactjs_new_gen\\templates",
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
