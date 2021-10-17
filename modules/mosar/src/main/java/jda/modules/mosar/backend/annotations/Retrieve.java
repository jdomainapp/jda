package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotates a method to be a method to retrieve objects from persistence store.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Retrieve {
    boolean byId() default false;
    boolean ignored() default false;
}
