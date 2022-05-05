package jda.modules.mosarfrontend.common.anotation.template_desc;

public @interface CrossTemplatesDesc {
    ComponentGenDesc Router() default  @ComponentGenDesc();
    ComponentGenDesc BaseService() default @ComponentGenDesc();
    ComponentGenDesc Ext() default @ComponentGenDesc();
}
