package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotates a method to be a method to create and save new objects.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Create {
    boolean byId() default false;
}
