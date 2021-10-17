package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotate the current parameter to hold the ID of the entity.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Inherited
public @interface ID {

    String value() default "";
}
