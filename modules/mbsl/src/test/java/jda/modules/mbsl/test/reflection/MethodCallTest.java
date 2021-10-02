package jda.modules.mbsl.test.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class MethodCallTest {
  public void method1(String[] a, Object[] b) {
    System.out.printf("method1(String[], Object[])-> (%s,%s)%n", Arrays.toString(a), Arrays.toString(b));
  }

  public void method2(String[] a, Object...b) {
    System.out.printf("method2(String[], Object...) -> (%s,%s)%n", Arrays.toString(a), Arrays.toString(b));
  }

  public static void main(String[] args) throws Exception {
    MethodCallTest test = new MethodCallTest();
    
    String[] a = {"arg1"};
    Object[] b = new Object[] {"v1"};
    
    // call method1
    Method m1 = MethodCallTest.class.getMethod("method1", String[].class, Object[].class);
    
    m1.invoke(test, a, b);
    
    // call method2
    Method m2 = MethodCallTest.class.getMethod("method2", String[].class, Object[].class);
    
    m2.invoke(test, a, b);

    
    // call method2 (directly)
    //IMPORTANT: notice how b1 is wrapped inside another array!!!
    Object b1 = new Object[] {"v2"};
    test.method2(a, b1);
  }
 
}
