package jda.modules.mosarfrontend.common.anotation.template_desc;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;

public @interface ComponentGenDesc {
    String[] templates() default {};
    Class<?>[] genClasses() default {};
}
