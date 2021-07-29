package jda.modules.dcsl.syntax.query;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jda.modules.dcsl.syntax.query.AttribExp;

/**
 * @overview 
 *  Allows definition of simple object queries, such as those specified in the examples below.
 *  
 * @example
 *  Natural language: find all <tt>Student</tt> objects whose name contains the string "Le".
 *  
 *  <p>SQL query: <pre>
 *    select * from Student where name like '%Le%';
 *    
 *  </pre>
 *  <p>Object query: <pre>
 *    select 
 *      Student
 *    where
 *      Student.name matches "%Le%"
 *  </pre>
 *  
 * @author dmle
 * 
 * @version 3.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryDef {

  /**
   * The domain class (e.g. <tt>Student</tt>) whose objects are the subject of this query
   */
  Class clazz();

  /**
   * (Optional) specifies names of the domain attributes of {@link #clazz()} whose values will be 
   * retrieved for each object. It has the same meaning as the <tt>SELECT</tt> clause of the SQL language.
   * 
   * <p>Default: <tt>{}</tt> (empty, i.e. only id attribute)
   */
  String[] selector() default {};
  
  /**
   * (Optional) The attribute constraint expressions that describe the criteria for the objects of 
   * {@link #clazz()} that satisfy this query.
   * 
   * <p>Default: <tt>{}</tt> (no expressions)
   */
  AttribExp[] exps() default {};
}
