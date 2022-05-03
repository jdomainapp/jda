package jda.modules.mosarfrontend.reactnative;

import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ComponentGenDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.CrossTemplatesDesc;
import jda.modules.mosarfrontend.common.anotation.template_desc.ModuleTemplatesDesc;
import jda.modules.mosarfrontend.reactnative.templates.MainGen;
import jda.modules.mosarfrontend.reactnative.templates.src.data_types.DataTypeGen;
import jda.modules.mosarfrontend.reactnative.templates.src.modules.FormInputsGen;
import jda.modules.mosarfrontend.reactnative.templates.src.modules.ModulesGen;

@AppTemplateDesc(
        templateRootFolder = "D:\\UET_THS\\JDA\\work\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\reactnative\\templates",
        resource = "D:\\UET_THS\\JDA\\work\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\reactnative\\resources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        templates = {"Main.tsx"},
                        genClasses = {MainGen.class}
                ),
                BaseService = @ComponentGenDesc(
                        templates = {},
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        templates = {"Modules.tsx"},
                        genClasses = {ModulesGen.class, FormInputsGen.class}
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
public class ReactNativeAppTemplate {
}
