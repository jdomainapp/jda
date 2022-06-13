package jda.modules.mosarfrontend.reactnative;

import jda.modules.mosarfrontend.common.anotation.template_desc.*;
import jda.modules.mosarfrontend.reactnative.templates.src.MainGen;
import jda.modules.mosarfrontend.reactnative.templates.src.data_types.DataTypeGen;
import jda.modules.mosarfrontend.reactnative.templates.src.data_types.EnumTypeGen;
import jda.modules.mosarfrontend.reactnative.templates.src.modules.FormInputsGen;
import jda.modules.mosarfrontend.reactnative.templates.src.modules.module.*;

@AppTemplateDesc(
        templateRootFolder = "D:\\JDA\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\reactnative\\templates",
        resource = "D:\\JDA\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\reactnative\\resources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        genClasses = {MainGen.class}
                ),
                BaseService = @ComponentGenDesc(
                        genClasses = {}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {FormInputsGen.class}
                )
        ),
        moduleTemplates = @ModuleTemplatesDesc(
                List = @ComponentGenDesc(
                        genClasses = {ListConfigGen.class}
                ),
                Form = @ComponentGenDesc(
                        genClasses = {FormConfigGen.class, InputGen.class}
                ),
                Main = @ComponentGenDesc(
                        genClasses = {IndexGen.class}
                ),
                Entity = @ComponentGenDesc(
                        genClasses = {DataTypeGen.class}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {ModuleConfigGen.class}
                )
        ),

        moduleFieldTemplates = @ModuleFieldTemplateDesc(
                Ext = @ComponentGenDesc(
                        genClasses = {EnumTypeGen.class}
                )
        )
)
public class ReactNativeAppTemplate {
}
