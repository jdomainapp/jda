package jda.modules.mosarfrontend.common.anotation.template_desc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AppTemplateDesc {
    /**
     * @return Path to resource folder - which includes all requirement resource (don't need to generate) like image, common code,...
     */
    String resource() default "";
    String templateRootFolder();
    CrossTemplatesDesc crossTemplates();
    ModuleTemplatesDesc moduleTemplates();
    SubModuleTemplateDesc subModuleTemplates() default @SubModuleTemplateDesc;
    ModuleFieldTemplateDesc moduleFieldTemplates() default @ModuleFieldTemplateDesc;
}
