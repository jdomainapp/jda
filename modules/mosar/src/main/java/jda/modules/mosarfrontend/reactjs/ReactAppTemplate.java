package jda.modules.mosarfrontend.reactjs;

import jda.modules.mosarfrontend.common.anotation.template_desc.*;
import jda.modules.mosarfrontend.reactjs.src.AppGen;
import jda.modules.mosarfrontend.reactjs.src.modules.FormGen;
import jda.modules.mosarfrontend.reactjs.src.modules.ListViewGen;
import jda.modules.mosarfrontend.reactjs.src.modules.SubmoduleGen;
import jda.modules.mosarfrontend.reactjs.src.modules.indexGen;
import jda.modules.mosarfrontend.reactjs.src.modules.patterns.AccordionStructGen;

@AppTemplateDesc(
        resource = "fe/react/resources_f59.zip",
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
                        genClasses = {AccordionStructGen.class}
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
