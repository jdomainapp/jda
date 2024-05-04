package org.jda.example.coursemanrestful.test.modules.coursemodule;

import jda.modules.common.exceptions.DataSourceException;
import jda.software.ddd.MicroDomSoftware;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanrestful.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanrestful.test.CRUDTestMaster;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @overview
 */
public class CRUDCompulsoryModule extends CRUDTestMaster<CompulsoryModule> {

  private MicroDomSoftware<CompulsoryModule> ms;
  private Class<CompulsoryModule> cls;

  @Before
  public void initMe() throws DataSourceException {
    cls = CompulsoryModule.class;
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

    // update
    // delete
    // ms.delete(1);
  }

  @Override
  protected Supplier createObject() {
    return () -> {
      logger().info("Creating object CompulsoryModule objects...");

      try {

        // todo: loop to create many random objects (e.g. using the format `value + counter`)

        CompulsoryModule[] objs = {
            new CompulsoryModule("IPG", 1, 12),
            new CompulsoryModule("WEB", 2, 12),
            new CompulsoryModule("OOP", 3, 12)
        };

        getSw().createObjects(CompulsoryModule.class, Arrays.asList(objs));
      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    };
  }
}
