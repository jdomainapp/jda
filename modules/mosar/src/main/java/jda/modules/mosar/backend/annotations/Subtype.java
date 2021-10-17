package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotate a parameter to represent a discriminator of a subtype.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Inherited
public @interface Subtype {

}
