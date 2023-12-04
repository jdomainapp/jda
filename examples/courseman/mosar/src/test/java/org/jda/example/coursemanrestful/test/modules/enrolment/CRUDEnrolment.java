package org.jda.example.coursemanrestful.test.modules.enrolment;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.expression.Op;
import jda.mosa.controller.assets.helper.objectbrowser.ObjectBrowser;
import jda.software.ddd.MicroDomSoftware;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanrestful.modules.coursemodule.model.ElectiveModule;
import org.jda.example.coursemanrestful.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanrestful.modules.student.model.Student;
import org.jda.example.coursemanrestful.test.CRUDTestMaster;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import static org.jda.example.coursemanrestful.utils.DToolkit.randDate;

/**
 * @version 1.0
 * @overview
 */
public class CRUDEnrolment extends CRUDTestMaster<Enrolment> {

  private MicroDomSoftware<Enrolment> ms;
  private Class<Enrolment> cls;

  @Before
  public void initMe() throws DataSourceException {
    cls = Enrolment.class;
    ms = createMicroSoftware(
        getSw(),
        cls,
        // create
        createObject(),
        // update
        updateObject()
    );

    // REQUIRED
    ms.initDom();

    // also register the CourseModule subtypes
    // initDom() above only registers the associated CourseModule and all its ancestor types (if any)
    ms.getSw().registerClasses(new Class[] {CompulsoryModule.class, ElectiveModule.class});
  }

  @Test
  public void run() throws DataSourceException {

    logger().info("BEFORE...");
    ms.loadAndDisplay()
        .displaySource()
    ;

    // create
    ms.create();
    logger().info("AFTER CREATE...");
    ms.displaySource();

    // update: todo

    // delete
//    ms.deleteDomainData();
//    logger().info("AFTER DELETE...");
//    ms.displaySource();
  }

  @Override
  protected Supplier createObject() {
    return () -> {
      logger().info("Creating object Enrolment objects...");

      try {
        // todo: loop to create many random objects (e.g. using the format `value + counter`)

        Student s = ms.getSw().retrieveObject(Student.class, "id", Op.EQ, "S2023");
        logger().info(s+ "");

        // browse through CourseModule objects to create enrolments
        Collection<Enrolment> objs = new ArrayList<>();
        ObjectBrowser<CourseModule> browser = ms.getSw().createObjectBrowser(CourseModule.class);
        browser.first();
        do {
          CourseModule m = browser.getCurrentObject();
          logger().info(m+ "");
          Enrolment e = new Enrolment(s, m, randDate(2020, 2021), randDate(2022,2023));
          logger().info(e+"");
          objs.add(e);

          if (browser.hasNext())
            browser.next();
        } while(browser.hasNext());

        getSw().createObjects(Enrolment.class, objs);

      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    };
  }
}
