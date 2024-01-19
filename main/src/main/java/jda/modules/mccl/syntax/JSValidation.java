package jda.modules.mccl.syntax;

import java.lang.annotation.Documented;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
//@java.lang.annotation.Inherited
@Documented
public @interface JSValidation {
    public String regex() default "";
    public String invalidMsg() default "";
    public String validMsg() default "";
    public boolean optional() default false;
}
