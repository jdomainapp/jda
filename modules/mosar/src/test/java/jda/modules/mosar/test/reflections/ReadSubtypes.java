package jda.modules.mosar.test.reflections;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.courseman.modules.coursemodule.model.CourseModule;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ReadSubtypes {
  public static void main(String[] args) {
    
    // turn off logging messages
    Logger root = (Logger) LoggerFactory.getLogger("org.reflections");
    root.setLevel(Level.OFF);
    
    readSimple();
    
    readProject();
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private static void readProject() {
    Class c = CourseModule.class;
    String pkgPath = "org.courseman.modules.coursemodule.model";
    Reflections refls = new Reflections(pkgPath);

    Set<Class> descendants = refls.getSubTypesOf(c);
    System.out.println(descendants);

  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private static void readSimple() {
    Class c = CourseModule.class;
    
    String pkgPath = c.getPackage().getName();
    Reflections refls = new Reflections(pkgPath);
    Set<Class> descendants = refls.getSubTypesOf(c);
    
    System.out.println(descendants);    
  }
}
