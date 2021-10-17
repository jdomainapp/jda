package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotates a parameter to be an entity that may be changed after the method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Inherited
public @interface Modifying {

}
