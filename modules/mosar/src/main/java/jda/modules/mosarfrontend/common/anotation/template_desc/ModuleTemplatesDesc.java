package jda.modules.mosarfrontend.common.anotation.template_desc;

public @interface ModuleTemplatesDesc {
    ComponentGenDesc Form() default @ComponentGenDesc;
    ComponentGenDesc List() default @ComponentGenDesc;
    ComponentGenDesc Main() default @ComponentGenDesc;
    ComponentGenDesc Entity() default @ComponentGenDesc;
    ComponentGenDesc Ext() default @ComponentGenDesc;
}
