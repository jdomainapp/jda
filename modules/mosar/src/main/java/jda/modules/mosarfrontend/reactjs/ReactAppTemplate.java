package jda.modules.mosarfrontend.reactjs;

import jda.modules.mosarfrontend.common.anotation.template_desc.*;
import jda.modules.mosarfrontend.reactjs.src.AppGen;
import jda.modules.mosarfrontend.reactjs.src.modules.FormGen;
import jda.modules.mosarfrontend.reactjs.src.modules.ListViewGen;
import jda.modules.mosarfrontend.reactjs.src.modules.SubmoduleGen;
import jda.modules.mosarfrontend.reactjs.src.modules.indexGen;
import jda.modules.mosarfrontend.reactjs.src.modules.model.ModelGen;
import jda.modules.mosarfrontend.reactjs.src.modules.patterns.accordion.AccordionIndexGen;
import jda.modules.mosarfrontend.reactjs.src.modules.patterns.accordion.MenuStateGen;
import jda.modules.mosarfrontend.reactjs.src.modules.patterns.search.SearchConsumerGen;
import jda.modules.mosarfrontend.reactjs.src.modules.patterns.search.SearchIndexGen;

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
                        genClasses = {ModelGen.class}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {MenuStateGen.class, AccordionIndexGen.class, SearchIndexGen.class, SearchConsumerGen.class}
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
