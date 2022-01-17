package org.jda.example.courseman.software;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.jda.example.courseman.services.student.model.City;
import org.jda.example.courseman.services.student.model.Gender;
import org.jda.example.courseman.services.student.model.Student;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.oql.QRM;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.DomSoftware;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DomStudents {
  
  public static void main(String[] args) {
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
    
    // this should be run subsequent times
    sw.init();
    
    try {
      // register a domain model fragment concerning Student
      Class[] domFrag = {
          Student.class
      };
      sw.addClasses(domFrag);
      
      // create some Student objects
      createStudent(sw);
      
      // read object:
//      querySimple(sw, Student.class, Student.A_id, Op.EQ, "S2020");
      
      queryStudents(sw);
      
      // display the domain model and its instances
//      boolean displayFqn = false;
//      sw.printDomainModel(displayFqn);
      
      // check that a new object is in the object pool
//      sw.printObjectPool(Student.class);

      // check that object is in the database by printing data in the database
      sw.printObjectDB(Student.class);
      
      // update object:
//      updateObject(sw, "S2020");
      
      // delete object:
//      deleteObject(sw, "S2020");
    } catch (DataSourceException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * @effects 
   * 
   * @version 
   * @throws DataSourceException 
   * @throws NotPossibleException 
   * 
   */
  private static void queryStudents(DomSoftware sw) throws NotPossibleException, DataSourceException {
    Map<Oid, Student> result = queryStudentsByNamePattern("Du");
    if (result != null) {
      sw.printObjects(Student.class, result.values());
    } else {
      System.out.println("No match");
    }    
    
    result = queryStudentsByCity("Hanoi");
    if (result != null) {
      sw.printObjects(Student.class, result.values());
    } else {
      System.out.println("No match");
    }        
  }
  
  /**
   * @return 
   * @effects 
   * 
   */
  private static <T> Collection<T> querySimple(DomSoftware sw, Class<T> cls, 
      String attribName, Op op, String val) throws NotPossibleException, DataSourceException {
    
    Collection<T> objects = sw.retrieveObjects(cls, attribName, op, val);
    sw.printObjects(cls, objects);
    return objects;
  }
  
  /**
   * @effects 
   * 
   */
  private static Map<Oid, Student> queryStudentsByNamePattern(String name) throws NotPossibleException, DataSourceException {
    QRM qrm = QRM.getInstance();
    // create query
    String namePattern = "%"+name+"%";
    Query q = QueryToolKit.createSearchQuery(
        qrm.getDsm(), 
        Student.class, 
        new String[] {Student.A_name}, 
        new Op[] {Op.MATCH}, new Object[] {namePattern});
    
    System.out.printf("Querying students with name matching '%s'%n", namePattern);
    Map<Oid, Student> result = qrm.getDom().retrieveObjects(Student.class, q);
    return result; 
  }

  private static Map<Oid, Student> queryStudentsByCity(String cityName) throws NotPossibleException, DataSourceException {
    QRM qrm = QRM.getInstance();
    // create query
    Query q = QueryToolKit.createSimpleJoinQuery(qrm.getDsm(), 
        Student.class, City.class,  
        Student.A_address, 
        City.A_name, 
        Op.MATCH, 
        "%"+cityName+"%");
    
    System.out.printf("Querying students whose address is City(name='%s')%n", cityName);
    Map<Oid, Student> result = qrm.getDom().retrieveObjects(Student.class, q);
    return result;
  }
  

  /**
   * @effects 
   * 
   */
  private static void createStudent(DomSoftware sw) throws NotFoundException, DataSourceException {
    // get a city object
    City city = sw.retrieveObjectById(City.class, 1);
    // create a Student
    Date dob;
    //dob = Toolkit.getDateZeroTime(1, 1, 1970);
    
    // another method of creating Date
    dob = createDateFromString("1/1/1970");
    
    sw.addObject(Student.class,
        new Student("Duc Le", 
            Gender.Male, 
            dob, 
            city,
            "duc@gmail.com")
        );    
  }

  /**
   * @effects 
   *  return a Date object whose string representation is <tt>dateStr</tt>.
   *  If dateStr is invalid
   *    return null
   */
  private static Date createDateFromString(String dateStr) {
    DateFormat dformat = new SimpleDateFormat("dd/MM/yyyy");
    Date dt = null;
    try {
      dt = dformat.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    
    return dt;
  }

  /**
   * @effects 
   * 
   */
  private static void updateObject(DomSoftware sw, Object id) throws NotFoundException, DataSourceException {
    Student s = sw.retrieveObjectById(Student.class, id);
    if (s != null) {
      System.out.printf("Updating object%n%s%n", s);
      sw.updateObject(Student.class, s, 
          new String[] {
              Student.A_email, Student.A_address},
          new Object[] {
              "leminhduc@gmail.com",
              sw.retrieveObjectById(City.class, 2)
          });
      System.out.printf("... after:%n%s%n", s);
    }    
  }

  /**
   * @effects 
   * 
   * @version 
   * @param sw 
   * 
   */
  private static void deleteObject(DomSoftware sw, Object id) throws NotFoundException, DataSourceException {
    Student s = sw.retrieveObjectById(Student.class, id);
    if (s != null) {
      System.out.printf("Deleting object%n%s%n", s);
      sw.deleteObject(s, Student.class);
      sw.printObjectDB(Student.class);
    }    
  }
}
