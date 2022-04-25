package jda.modules.mosarfrontend.reactnative;

import jda.modules.mosarfrontend.common.anotation.AppTemplateDesc;
import jda.modules.mosarfrontend.reactnative.templates.MainTemplate;
import jda.modules.mosarfrontend.reactnative.templates.data_types.DataType;

@AppTemplateDesc(
        resource = "src/main/resources/reactnative",
        templateRootFolder = "D:\\UET_THS\\JDA\\work\\jda\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\reactnative\\templates",
        fileTemplates = {
                MainTemplate.class
        },
        moduleTemplates = {
                DataType.class
        }
)
public class ReactNativeAppGenerator {
}
