package org.jda.example.courseman.software;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.DomSoftware;
import org.jda.example.courseman.services.student.model.Student;
import org.jda.example.courseman.services.student.reports.StudentsByNameReport;

import java.util.Collection;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DomReport {
  public static void main(String[] args) {
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();

    // this should be run subsequent times
    sw.init();

    try {
      sw.addClasses(MainUI.model);

      StudentsByNameReport rept = new StudentsByNameReport("Duc");

      Collection<Student> objs = rept.getStudents();

      System.out.println("Report result:");
      if (objs != null && !objs.isEmpty()) {
        for (Student s : objs) {
          System.out.println(s);
        }
      } else {
        System.out.println("Empty");
      }

    } catch (NotPossibleException | DataSourceException e) {
      e.printStackTrace();
    }
  }
}

//public class DomReport {
//  public static void main(String[] args) {
//    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
//
//    // this should be run subsequent times
//    sw.init();
//
//    try {
//      sw.addClasses(Main.model);
//
//      StudentsByNameReport rept = new StudentsByNameReport("Duc");
//
//      Collection<Student> objs = rept.getStudents();
//
//      System.out.println("Report result:");
//      if (objs != null && !objs.isEmpty()) {
//        for (Student s : objs) {
//          System.out.println(s);
//        }
//      } else {
//        System.out.println("Empty");
//      }
//
//    } catch (NotPossibleException | DataSourceException e) {
//      e.printStackTrace();
//    }
//  }
//}
