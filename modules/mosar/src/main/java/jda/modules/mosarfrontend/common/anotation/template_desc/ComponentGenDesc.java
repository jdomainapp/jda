package jda.modules.mosarfrontend.common.anotation.template_desc;

public @interface ComponentGenDesc {
    String[] templates() default {};
    Class<?>[] genClasses() default {};
}
