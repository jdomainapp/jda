package jda.modules.mosarfrontend.reactnative;

import jda.modules.mosarfrontend.common.anotation.template_desc.*;
import jda.modules.mosarfrontend.reactnative.src.AppConfigGen;
import jda.modules.mosarfrontend.reactnative.src.MainGen;
import jda.modules.mosarfrontend.reactnative.src.data_types.DataTypeGen;
import jda.modules.mosarfrontend.reactnative.src.data_types.SubTypeGen;
import jda.modules.mosarfrontend.reactnative.src.data_types.enums.EnumTypeGen;
import jda.modules.mosarfrontend.reactnative.src.data_types.enums.ModuleTypeGen;
import jda.modules.mosarfrontend.reactnative.src.data_types.enums.ModulesGen;
import jda.modules.mosarfrontend.reactnative.src.modules.FormInputsGen;
import jda.modules.mosarfrontend.reactnative.src.modules.module.*;
import jda.modules.mosarfrontend.reactnative.src.modules.module.sub_modules.*;

@AppTemplateDesc(
        templateRootFolder = "fe/reactnative",
        resource = "fe/reactnative/resources.zip",
        crossTemplates = @CrossTemplatesDesc(
                Router = @ComponentGenDesc(
                        genClasses = {MainGen.class}
                ),
                Ext = @ComponentGenDesc(
                        genClasses = {FormInputsGen.class, ModulesGen.class, AppConfigGen.class}
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
                        genClasses = {ModuleConfigGen.class, ModuleTypeGen.class}
                )
        ),

        subModuleTemplates = @SubModuleTemplateDesc(
                Ext = @ComponentGenDesc(
                        genClasses = {SubTypeGen.class, SubFormConfigGen.class,
                                SubIndexGen.class, SubListConfigGen.class,
                                SubModuleConfigGen.class, SubInputGen.class}
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
