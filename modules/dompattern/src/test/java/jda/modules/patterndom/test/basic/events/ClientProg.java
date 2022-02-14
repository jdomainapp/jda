package jda.modules.patterndom.test.basic.events;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ClientProg {
  private Map<Class, List> data;

  public ClientProg() {
    data = new LinkedHashMap<>();
  }
  
  // /// To be used by individual test case sub-classes ////
  protected void defaultInitData() {
    // initialise cities
    initCities();

    initSClasses();
    
    // initialise students and modules
    initStudents();

    // modules
    initModules();

    // initialise enrolments
    initEnrolments(); 
  }
  
  protected void initCities() {

    System.out.println("initCities()");

    List<City> cities = new ArrayList();

    data.put(City.class, cities);
    City ci = new City("Hà nội");
    cities.add(ci);
    ci = new City("Vinh");
    cities.add(ci);
    ci = new City("Đà nẵng");
    cities.add(ci);
    ci = new City("Hồ chí minh");
    cities.add(ci);

    //return cities;
  }

  protected void initStudentsWithoutSClasses() {
    System.out.println("initStudents()");

    List<City> cities = (List<City>) data.get(City.class);
    
    if (cities == null)
      throw new InternalError("initStudents: no cities");
    
    Student s1, s2, s3, s4, s5;
    List<Student> students = new ArrayList();

    data.put(Student.class, students);
    s1 = new Student("Nguyen Van Thang", //
        "20/1/1990", cities.get(0), "thangvn@yahoo.com");
    students.add(s1);
    s2 = new Student("Tran Minh Loc", //
        "15/1/1990", cities.get(1), "locmt@yahoo.com");
    students.add(s2);
    s3 = new Student("Le Duc Vinh", //
        "17/2/1990", cities.get(1), "vinhld@yahoo.com");
    students.add(s3);
    s4 = new Student("Dinh Ba Thanh", //
        "21/3/1990", cities.get(3), "thanhdb@yahoo.com");
    students.add(s4);
    s5 = new Student("Nguyen Thi Tho", //
        "23/5/1990", cities.get(0), "thont@yahoo.com");
    students.add(s5);

    //return students;
  }
  
  protected void initStudents() {
    System.out.println("initStudents()");

    List<City> cities = (List<City>) data.get(City.class); 
    List<SClass> sclasses = (List<SClass>) data.get(SClass.class);
    
    if (cities == null || sclasses == null)
      throw new InternalError("initStudents: no cities or sclasses");

    Student s1, s2, s3, s4, s5;
    SClass c;
    List<Student> students = new ArrayList();
    data.put(Student.class, students);
    
    c = sclasses.get(0);
    s1 = new Student("Nguyen Van Thang", //
        "20/1/1990", cities.get(0), "thangvn@yahoo.com", c);
    students.add(s1);
    c = sclasses.get(1);
    s2 = new Student("Tran Minh Loc", //
        "15/1/1990", cities.get(1), "locmt@yahoo.com", c);
    students.add(s2);
    s3 = new Student("Le Duc Vinh", //
        "17/2/1990", cities.get(1), "vinhld@yahoo.com", c);
    students.add(s3);
    c = sclasses.get(2);
    s4 = new Student("Dinh Ba Thanh", //
        "21/3/1990", cities.get(3), "thanhdb@yahoo.com", c);
    students.add(s4);
    c = sclasses.get(3);
    s5 = new Student("Nguyen Thi Tho", //
        "23/5/1990", cities.get(0), "thont@yahoo.com",c);
    
    students.add(s5);

    //return students;
  }

  protected void initModules() {
    System.out.println("initModules()");

    Module m1, m2, m3, m4, m5;
    List<Module> modules = new ArrayList();
    data.put(Module.class, modules);
    m1 = new Module("System Analysis & Design", 4, 5);
    m2 = new Module("Discrete Mathematics", 3, 5);
    m3 = new Module("Software Engineering", 5, 5);
    modules.add(m1);
    modules.add(m2);
    modules.add(m3);

    //return modules;
  }

  protected void initEnrolments() {
    System.out.println("initEnrolments()");

    List<Student> students = (List<Student>) data.get(Student.class); 
    List<Module> modules = (List<Module>) data.get(Module.class);
    
    if (students == null || modules == null)
      throw new InternalError("initEnrolments: no students or modules");
    
    Enrolment e;
    List<Enrolment> enrolments = new ArrayList();
    data.put(Enrolment.class, enrolments);

    e = new Enrolment(students.get(0), modules.get(0), 5.5, 8.5);
    enrolments.add(e);
    e = new Enrolment(students.get(1), modules.get(0));
    enrolments.add(e);
    e = new Enrolment(students.get(2), modules.get(1), 6.5, 8.0);
    enrolments.add(e);
    e = new Enrolment(students.get(3), modules.get(1));
    enrolments.add(e);
    e = new Enrolment(students.get(4), modules.get(2), 8.0, 9.5);
    enrolments.add(e);
    e = new Enrolment(students.get(0), modules.get(1));
    enrolments.add(e);
    e = new Enrolment(students.get(1), modules.get(1), 7.0, 9.0);
    enrolments.add(e);
    e = new Enrolment(students.get(2), modules.get(2));
    enrolments.add(e);
    e = new Enrolment(students.get(3), modules.get(2), 5.0, 10.0);
    enrolments.add(e);
    e = new Enrolment(students.get(4), modules.get(0));
    enrolments.add(e);
    e = new Enrolment(students.get(0), modules.get(2), 6.0, 8.0);
    enrolments.add(e);

    //return enrolments;
  }

  public void initSClasses() {
    System.out.println("initSClasses()");

    List<SClass> classes = new ArrayList<SClass>();
    data.put(SClass.class, classes);
    
    SClass sc = new SClass(1, "class #1");
    classes.add(sc);
    sc = new SClass(2, "class #2");
    classes.add(sc);
    sc = new SClass(3, "class #3");
    classes.add(sc);
    sc = new SClass(4, "class #4");
    classes.add(sc);

    //return classes;
  }
  
  @Test
  public void main() {
    ClientProg app = new ClientProg();
    
    // observe OnCreated event of Enrolments
    app.defaultInitData();
    
    // observe OnUpdated event of an Enrolment
    // change an Enrolment.internalMark
    Enrolment e = app.chooseRandomEnrolment();
    
    System.out.printf("Choose an enrolment: %n  %s%n  internalMark: %.1f%n  finalMark: %d%n", 
        e, e.getInternalMark(), e.getFinalMark());
    
    System.out.println();
    e.setInternalMark(Math.min(10, e.getInternalMark()+2));
    System.out.println();

    // observe the event being fired and handled...
    
    System.out.printf("Updated enrolment: %n  %s%n  internalMark: %.1f%n  finalMark: %d%n", 
        e, e.getInternalMark(), e.getFinalMark());
    
    
    // observe OnRemoved event of an Enrolment
    System.out.println("\nFinalising "+e+"\n   waiting for OnRemoved event to fire....");
    try {
      e.finalize();
    } catch (Throwable e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    /*
    app.removeEnrolment(e);
    System.out.println("\nRemoved " + e);

    e = null;
    */
    // wait
//    Toolkit.sleep((600*1000));
  }

  /**
   * @modifies {@link #data}
   * @effects 
   *  remove from e {@link #data}
   */
  private void removeEnrolment(Enrolment e) {
    List<Enrolment> enrolments = data.get(Enrolment.class);
    
    enrolments.remove(e);
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public Enrolment chooseRandomEnrolment() {
    return (Enrolment) data.get(Enrolment.class).get(0);
  }
}
