package jda.test.dodm.dom;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMCustomTester;
import jda.test.model.basic.City;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;

public class ReloadObjectTest extends DODMCustomTester {
  
  @Ignore
  @Test
  public void reloadCityObject() throws Exception {
    printf("%nreloadCityObject()%n");
    
    Class<City> c = City.class;
    
    // register City
    registerClass(c);
    
    // load City objects
    loadObjects(c);
    
    // retrieve first City object and its Oid
    Tuple2<Oid,City> tuple = getRandomObject(c); //getRandomObject(c); 
        //getFirstObject(c);
    
    Oid id = tuple.getFirst();
    City o = tuple.getSecond();
    
    printf("Existing object: %n  class: %s%n  id: %s%n  object: %s%n", c.getName(), id, o);
    
    // reload first City object from data source
    City newO = reloadObjectWithOid(c, id);
    
    // check that: (1) two objects are different, but (2) the two Oids point to the same
    // object (and therefore the new object is placed into pool to replace the
    // old object)
    
    validateReloadObject(newO, c, o, id);
  }
  
  //@Ignore
  @Test
  public void reloadStudentObject() throws Exception {
    printf("%nreloadStudentObject()%n");

    Class<Student> c = Student.class;
    
    // register Student
    registerClass(c);
//    registerClass(SClass.class);
//    registerClass(City.class);
    
    // load City objects
    loadObjects(c);
    
    // retrieve first City object and its Oid
    Tuple2<Oid,Student> tuple = getRandomObject(c); //getRandomObject(c); 
        //getFirstObject(c);
    
    Oid id = tuple.getFirst();
    Student o = tuple.getSecond();
    City oldCity = o.getAddress();
    SClass oldSClass = o.getSclass();
    
    // load students of this class
    loadAssociatedObjects(oldSClass);
    
    Collection<Student> oldClassStudents = oldSClass.getStudents();
    
    printf("Existing object: %n  class: %s%n  id: %s%n  object: %s%n  city: %s%n  sclass: %s%n", 
        c.getName(), id, o, oldCity, oldSClass);
    
    // reload first City object from data source
    Student newO = reloadObjectWithOid(c, id);
    
    // check new object
    validateReloadObject(newO, c, o, id);
    
    // check that the associated objects are the same
    City city = newO.getAddress();
    SClass sclass = newO.getSclass();

    boolean sameCity = (city == oldCity);
    boolean sameSClass = (sclass == oldSClass);
    
    printf("Check associated objects stay the same: %n  same city: %b%n  same sclass: %b%n", 
        sameCity, sameSClass);
    
    assert sameCity : "Associated object not the same: " + city;
    assert sameSClass : "Associated object not the same: " + sclass;
    
    // check that associate objects have been updated that: 
    // (1) not containing old object
    // (2) containing new object
    Collection<Student> students = sclass.getStudents();
    boolean sameNumLinks = students.size() == oldClassStudents.size();
    
    if (sameNumLinks && students.size() == 0) {
      // no objects
      printf("Associated objects have same empty links: %n  associate: %s%n", sclass);
    } else {
      boolean containOldStudent = false;
      boolean containNewStudent = false;
      for (Student s : students) {
        if (s == o) {
          containOldStudent = true;
        } 
        
        if (s == newO) {
          containNewStudent = true;
        }
      }
      
      printf("Check associated objects have replaced the links: %n  associate: %s%n  contain old link: %b%n  contain new link: %b%n", 
          sclass, containOldStudent, containNewStudent);
      
      assert !containOldStudent : sclass+" must NOT have link to old object: " + o;
      assert containNewStudent: sclass+" must have link to new object: " + o;
    }
  }

  private <T> void validateReloadObject(T newO, Class<T> c, T o, Oid id) {
    // check that: (1) two objects are different, but (2) the two Oids point to the same
    // object (and therefore the new object is placed into pool to replace the
    // old object)
    
    // try getting Oid in object pool using newO
    Oid reId = getObjectId(c, newO); 

    boolean diff = (newO != o);
    printf("Newly reloaded object: %n  %s%n  diff. object: %b%n", 
        newO, diff);

    boolean sameId = (id == reId);
    printf("Checking Oid: %n  re-read id: %s%n  same id: %b%n", 
        reId, sameId);

    // try getting the new object using the old id
    T reNewO = getObject(c, id);
    boolean sameNewObject = (reNewO == newO);
    printf("Try getting the reloaded object using old id: %n  object: %s%n  same new object: %b%n", 
        reNewO, sameNewObject);
    
    assert diff : "Error: reloaded object is not different from the existing one";
    assert sameId : "Error: Oid of reloaded object is not the same in the object pool";
    assert sameNewObject : "Error: reloaded object is not the same if loading using old Id";    
  }
  
}
