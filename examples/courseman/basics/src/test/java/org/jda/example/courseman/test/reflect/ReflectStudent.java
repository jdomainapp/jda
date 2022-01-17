/**
 * 
 */
package org.jda.example.courseman.test.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.jda.example.courseman.services.student.model.Student;

/**
 * @overview 
 *
 * @author dmle
 *
 * @version 
 */
public class ReflectStudent {
  public static void main(String[] args) {
    Class c = Student.class;
    
    Method[] methods = c.getDeclaredMethods();
    
    for (Method m : methods) {
      System.out.println(m);
    }
    
    Annotation[] anos = c.getAnnotations();
  }
}
