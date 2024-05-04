package jda.modules.mccl.syntax;

import java.lang.annotation.Documented;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
//@java.lang.annotation.Inherited
@Documented
public @interface JSValidation {
    String regex() default "";
    String invalidMsg() default "";
    String validMsg() default "";
    boolean optional() default false;
}
