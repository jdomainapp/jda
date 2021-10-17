package jda.modules.mosar.backend.annotations;

import java.lang.annotation.*;

/**
 * Annotate a parameter to be the conditions for paging.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Inherited
public @interface PagingCondition {

}
