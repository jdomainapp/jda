package jda.modules.tmsa.tasl.syntax;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Cong Nguyen (congnv)
 * @version 5.6
 * @overview Micro services app generator configuration
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
public @interface MDSGenDesc {
    String name();
    Class[] mccServices();

    String modelsPath();

    // optional
    String description() default "";
    String outputPackage() default "";
    String outputPath() default "";
}
