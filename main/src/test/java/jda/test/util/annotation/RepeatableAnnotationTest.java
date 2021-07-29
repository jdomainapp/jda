/**
 * 
 */
package jda.test.util.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @overview 
 *
 * @author dmle
 *
 * @version 
 *  requires Java 1.8
 */
public class RepeatableAnnotationTest {
  @A(name="method1", type=1)
  @A(name="method1", type=2)
  public void method1() {}
}

// repeatable annotation
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(As.class)
@interface A {
  String name();
  int type();
}

// container annotation
@Retention(RetentionPolicy.RUNTIME)
@interface As {
  A[] value();
}
