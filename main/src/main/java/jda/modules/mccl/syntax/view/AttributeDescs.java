package jda.modules.mccl.syntax.view;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @overview 
 *  Container annotation for {@link AttributeDesc}
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD, ANNOTATION_TYPE})
@Documented
public @interface AttributeDescs {
  AttributeDesc[] value();
}
