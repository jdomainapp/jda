package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotates a method to be a method to update then persist an object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Update {
    boolean byId() default false;
}
