package jda.modules.mosarfrontend.common.anotation.template_desc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FileTemplateDesc {
    String templateFile();
}

