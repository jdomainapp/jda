package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotates a method to be a method to remove objects one at a time.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Delete {
    boolean byId() default false;
}
