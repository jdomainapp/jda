package jda.modules.dcsl.syntax;

import java.lang.annotation.Documented;

import jda.modules.common.types.Null;


/**
 * A field annotation that is used to annotate a collection-type field as to which domain class 
 * and its attributes are to be "selected" for the objects stored in a collection. This annotation 
 * has the same purpose as the SQL SELECT statement.  
 *  
 * @author dmle
 */
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.FIELD})
//@java.lang.annotation.Inherited
@Documented
public @interface Select {
  /** 
   * the domain class, e.g. <tt>courseman.model.Student</tt>
   * Default: <tt>Null.class</tt>
   **/
  Class clazz() default Null.class; //Type.class;
  
  /**
   * (optional) an array of domain attribute names of the class specified by {@link #clazz()}.
   * For example: <pre>{"id", "name"}</pre> are attributes of class <tt>courseman.model.Student</tt>
   * which can be set for this field.<br>
   * Default: empty array ([])
   **/
  String[] attributes() default {};
}
