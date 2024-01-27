package jda.modules.tmsa.tasl.syntax;

import jda.modules.mccl.syntax.containment.CTree;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Cong Nguyen (congnv)
 * @version 5.6
 * @overview Micro service generator configuration
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
public @interface ServiceDesc {
    String name();
    int port();
    CTree serviceTree();

    // optional
    String description() default "";
    String outputPath() default "";
    String outputPackage() default "";

}
