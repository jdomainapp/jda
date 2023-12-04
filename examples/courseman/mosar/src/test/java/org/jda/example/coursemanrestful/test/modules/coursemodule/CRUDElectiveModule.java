package org.jda.example.coursemanrestful.test.modules.coursemodule;

import jda.modules.common.exceptions.DataSourceException;
import jda.software.ddd.MicroDomSoftware;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanrestful.modules.coursemodule.model.ElectiveModule;
import org.jda.example.coursemanrestful.test.CRUDTestMaster;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * @version 1.0
 * @overview
 */
public class CRUDElectiveModule extends CRUDTestMaster<ElectiveModule> {

  private MicroDomSoftware<ElectiveModule> ms;
  private Class<ElectiveModule> cls;

  @Before
  public void initMe() throws DataSourceException {
    cls = ElectiveModule.class;
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

//    ms.reset();

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
      logger().info("Creating object ElectiveModule objects...");

      try {
        // todo: loop to create many random objects (e.g. using the format `value + counter`)
        ElectiveModule[] objs = {
            new ElectiveModule("FIT","UCD", 1, 12),
            new ElectiveModule("BIZ", "Marketing 1", 2, 12),
            new ElectiveModule("MED", "PR 1", 2, 12)
        };

        getSw().createObjects(ElectiveModule.class, Arrays.asList(objs));
      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    };
  }
}
