package jda.modules.mosarfrontend.reactjs;

import jda.modules.mosarfrontend.common.anotation.template_desc.*;
import jda.modules.mosarfrontend.reactjs.src.AppGen;
import jda.modules.mosarfrontend.reactjs.src.modules.*;

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
                        genClasses = {indexGen.class, ModuleStructureGen.class}
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
