package jda.modules.mosarfrontend.common.anotation.gen_controlers;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IfReplacement {
    String id() default "";
    String[] ids() default {};
}
