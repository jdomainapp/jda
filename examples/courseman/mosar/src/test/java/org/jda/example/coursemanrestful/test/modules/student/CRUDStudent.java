package org.jda.example.coursemanrestful.test.modules.student;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.expression.Op;
import jda.software.ddd.MicroDomSoftware;
import org.jda.example.coursemanrestful.modules.address.model.Address;
import org.jda.example.coursemanrestful.modules.student.model.Student;
import org.jda.example.coursemanrestful.test.CRUDTestMaster;
import org.jda.example.coursemanrestful.test.modules.address.CRUDAddress;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Supplier;
import static org.jda.example.coursemanrestful.utils.DToolkit.*;

/**
 * @version 1.0
 * @overview
 */
public class CRUDStudent extends CRUDTestMaster<Student> {

  private MicroDomSoftware<Student> ms;
  private Class<Student> cls;

  @Before
  public void initMe() throws DataSourceException {
    cls = Student.class;
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
//   ms.delete();
//   logger().info("AFTER DELETE...");
//   ms.displaySource();
  }

  @Override
  protected Supplier createObject() {
    return () -> {
      logger().info("Creating object Student objects...");

      try {

        // todo: loop to create many random objects (e.g. using the format `value + counter`)

        Student[] objs = {
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
            new Student(randName(), randGender(), randDob(), any(Address.class), randEmail()),
        };

        getSw().createObjects(Student.class, Arrays.asList(objs));
      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    };
  }
}
