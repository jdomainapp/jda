package jda.modules.mosarfrontend.common.anotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IfReplacement {
    String id() default "";
}
