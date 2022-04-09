package jda.modules.mosarfrontend.common.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AppTemplateDesc {
    /**
     * @return Array of template class which use {@link FileTemplateDesc} annotation
     */
    Class<?>[] fileTemplates();

    /**
     * @return Array of template class for each module which use {@link FileTemplateDesc} annotation
     */
    Class<?>[] moduleTemplates();

    /**
     * @return Path to resource folder - which includes all requirement resource (don't need to generate) like image, common code,...
     */
    String resource() default "";

    String templateRootFolder();
}
