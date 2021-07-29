package jda.test.app.courseman.enhanced;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.model.basic.City;
import jda.test.model.enhanced.Administrator;
import jda.test.model.enhanced.Instructor;
import jda.test.model.enhanced.Person;
import jda.test.model.enhanced.Staff;
import jda.test.model.enhanced.Student;

public class CourseManEnhancedTester extends CourseManBasicTester {
  public CourseManEnhancedTester() throws NotPossibleException {
    super();
  }

  protected void initClasses() {
    // the addition-order of domain classes must carefully observed to honour
    // their dependencies!
    domainClasses = new Class[] { //
        City.class, //
        Person.class, //
        Staff.class, //
        Instructor.class, //
        Administrator.class, //
        Student.class, //
    };
  }

  // /// Testable methods /////
  // /// To be used by individual test case sub-classes ////
  public void initData() throws DataSourceException {
    // only invoked once for all test cases
    method("initData()");

    initCities();

    List<City> cities = (List<City>) data.get(City.class);
    
    initPersons(cities);

    initStaff(cities);

    initInstructors(cities);

    initAdmins(cities);
  }

  protected void initPersons(List<City> cities) {
    method("initPersons()");
    List<Person> persons = new ArrayList();
    data.put(Person.class, persons);
  }

  protected Collection<Person> initStaff(List<City> cities) {
    method("initStaff()");

    Collection<Person> persons = data.get(Person.class);
    Staff s;
    s = new Staff("Tran Quang Anh", "3/3//1974", cities.get(1), "7/7/2003",
        "FIT");
    persons.add(s);
    s = new Staff("Le Minh Duc", "9/9/1977", cities.get(1), "10/9/2010", "FIT");
    persons.add(s);

    return persons;
  }

  protected Collection<Person> initInstructors(List<City> cities) {
    method("initInstructors()");

    Collection<Person> persons = data.get(Person.class);
    Person p;
    p = new Instructor("Peter J Smith", "5/5/1972", cities.get(3),
        "10/10/1990", "FIT");
    persons.add(p);
    p = new Instructor("Andrew M. Fair", "6/6/1974", cities.get(1), "8/8/1989",
        "FIT");
    persons.add(p);

    return persons;
  }

  protected Collection<Person> initAdmins(List<City> cities) {
    method("initAdmins()");

    Collection<Person> persons = data.get(Person.class);
    Administrator a;
    a = new Administrator("Nguyen Thi Linh", "2/2//1987", cities.get(3),
        "7/7/2011", "FIT", 1);
    persons.add(a);
    a = new Administrator("Tran Thi Huyen", "8/8/1985", cities.get(2),
        "10/3/2012", "FIT", 2);
    persons.add(a);

    return persons;
  }

  protected Collection<Student> addStudents(Collection<City> cities, Collection<Instructor> instructors) {
    method("addStudents()");

    //Collection<Person> persons = data.get(Person.class);
    
    Student s;
    City city;
    Instructor ins;
    Collection<Student> students = new ArrayList();
    
    final Object[][] stsDetails = {
        {"Hoang Van Thu", // name 
          "12/12/1989",   // dob
          3,              // city index
          "thu@gmail.com",// email
          0},             // instructor index
        {"Nguyen Van Thang", "12/12/1989", 1, "thang@gmail.com", 0},
        {"Hoang Van Thu", "12/12/1989", 2, "thu@gmail.com", 1},
        {"Hoang Van Thu", "12/12/1989", 1, "thu@gmail.com", 1},
        {"Hoang Van Thu", "12/12/1989", 3, "thu@gmail.com", 0},
    };
    
    for (Object[] std : stsDetails) {
      city = getObject(cities, (Integer) std[2]);
      ins = getObject(instructors,(Integer) std[4]); 
      
      s = new Student((String)std[0], (String)std[1], city, (String)std[3], ins);
      students.add(s);
  
      //persons.addAll(students);
    }
    
    return students;
  }

  public void updateStudentObject() throws DataSourceException, NotFoundException,
      NotPossibleException {
    //
    method("updateObject()");

    // //// STaff
    Object[] idVals = { "P1" };
    Staff m = (Staff) getObject(Staff.class, idVals);

    System.out.println("Object " + m);

    // update semester
    // Random rand = new Random();
    // int charCode = Math.max(65, 65 + Math.abs(rand.nextInt(52)));
    // String name = "Nguyen Tat " + ((char) charCode); // s.getName();
    String dob = m.getDob();
    System.out.println("old dob: " + dob);
    dob = "1/1/2012";
    System.out.println("new dob: " + dob);
    String name = m.getName();
    System.out.println("old name: " + name);
    // reverse
    name = (new StringBuffer(name)).reverse().toString();
    System.out.println("new name (reversed): " + name);

    Object[] vals = new Object[] { name, dob, m.getAddress(), m.getJoinDate() };

    updateObjectComplete(m, vals);

    System.out.println("updated");

    // /// Administrator
    idVals = new Object[] { "P6" };
    Administrator s = (Administrator) getObject(Administrator.class, idVals);
    System.out.println("Object " + s.toString(true));

    name = s.getName();
    name = (new StringBuffer(name)).reverse().toString(); // reverse it
    System.out.println("updated name: " + name);

    dob = s.getDob();
    City address = s.getAddress();
    String deptName = s.getDeptName();
    String joinDate = s.getJoinDate();
    int level = s.getLevel();

    vals = new Object[] { name, dob, address, joinDate, deptName, level };

    updateObjectComplete(s, vals);

    System.out.println("updated");
  }

  public void updateStudent() throws DataSourceException, NotFoundException,
      NotPossibleException {
    //
    method("updateStudent()");

    // //// 
    Class c = Student.class;
    
    Student m = (Student) getObjects(Student.class).iterator().next();

    System.out.println("Object " + m);

    String dob = m.getDob();
    System.out.println("old dob: " + dob);
    dob = "12/12/1991";
    System.out.println("new dob: " + dob);
    String name = m.getName();
    System.out.println("old name: " + name);
    // reverse
    name = (new StringBuffer(name)).reverse().toString();
    System.out.println("new name (reversed): " + name);

    Object[] vals = new Object[] { name, dob, m.getAddress(), m.getEmail(), m.getSupervisor() };

    updateObjectComplete(m, vals);

    System.out.println("updated");
  }

  public void deleteStudentObject() throws DataSourceException, NotFoundException,
      NotPossibleException {
    //
    method("deleteObject()");

    Class c = Student.class;
    
    System.out.println(c);
    // get an object of this class in memory 
    Collection objects = getObjects(c);
    Object o = objects.iterator().next();
    
    System.out.println("Object " + o);

    deleteObject(o,c);

    System.out.println("deleted");
  }

  protected boolean isAbstract(Class c) {
    if (c == Person.class)
      return true;
    else
      return false;
  }
  
}
