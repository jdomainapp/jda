package jda.modules.mosarfrontend.angular;

import jda.modules.mosarfrontend.angular.templates.AppHtmlTemplate;
import jda.modules.mosarfrontend.angular.templates.AppModuleTemplate;
import jda.modules.mosarfrontend.angular.templates.AppRoutingTemplate;
import jda.modules.mosarfrontend.angular.templates.modules.MainModuleTs;
import jda.modules.mosarfrontend.common.FEAppGen;
import jda.modules.mosarfrontend.common.anotation.AppTemplateDesc;
import jda.modules.mosarfrontend.reactnative.templates.MainTemplate;
import jda.modules.mosarfrontend.reactnative.templates.data_types.DataType;

@AppTemplateDesc(
        resource = "src/main/resources/angular",
        templateRootFolder = "D:\\Laptrinh\\4_DDD\\jda_moi\\modules\\mosar\\src\\main\\java\\jda\\modules\\mosarfrontend\\angular\\templates",
        fileTemplates = {
                AppRoutingTemplate.class,
                AppModuleTemplate.class,
                AppHtmlTemplate.class
        },
        moduleTemplates = {
        		MainModuleTs.class
//                DataType.class
        }
)
public class AngularAppGenerator implements FEAppGen{
	
}
