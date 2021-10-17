package jda.modules.mosar.software.backend.svcdesc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a Controller to be a REST service controller.
 * @author binh_dh
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceController {
    /**
     * Name of the service.
     */
    String name();

    /**
     * Web API endpoint of the service.
     */
    String endpoint();

    /**
     * The fully-qualified name of the corresponding resource class.
     */
    String className();

    /**
     * Nested ServiceController names.
     */
    String[] nested() default {};
}
