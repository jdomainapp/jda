package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotates a type to be a controller of a resource type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ResourceController {
    /**
     * Name of the resource controlled by the annotated class.
     * Must be in human-readable, pluralized form.
     * If no name is found, the name will be inferred from the class name.
     */
    String name() default "";
}
