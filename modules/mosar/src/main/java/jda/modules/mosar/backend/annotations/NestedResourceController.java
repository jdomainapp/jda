package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotates a controller to control a resource that is nested (has many-one
 * relationship).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface NestedResourceController {
    String innerType() default "";
    String outerType() default "";
}
