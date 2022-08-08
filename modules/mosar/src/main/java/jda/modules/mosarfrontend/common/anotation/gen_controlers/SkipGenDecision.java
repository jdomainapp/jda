package jda.modules.mosarfrontend.common.anotation.gen_controlers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation use to define a function which return a boolean value,
 * if TRUE: AppFactory will skip gen this file
 * if not use, default value is False
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SkipGenDecision {
}
