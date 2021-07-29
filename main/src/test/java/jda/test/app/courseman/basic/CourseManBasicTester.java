package jda.test.app.courseman.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.test.dodm.DODMBasicTester;
import jda.test.model.basic.City;
import jda.test.model.basic.CompulsoryModule;
import jda.test.model.basic.ElectiveModule;
import jda.test.model.basic.Enrolment;
import jda.test.model.basic.Module;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;

public class CourseManBasicTester extends DODMBasicTester {

  @Override
  protected void initClasses() {
    // the addition-order of domain classes must carefully observed to honour
    // their dependencies!
    domainClasses = new Class[] { //
        City.class, //
        SClass.class, //
        Student.class, //
        Module.class, //
        CompulsoryModule.class, //
        ElectiveModule.class, //
        Enrolment.class, //
    };
  }
  
  // /// To be used by individual test case sub-classes ////
  @Override
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
    if (data == null)
      data = new LinkedHashMap<Class, Collection>();

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

    Map<Class,Collection> data = instance.getData();

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
    Map<Class,Collection> data = instance.getData();
    
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
    m1 = new CompulsoryModule("System Analysis & Design", 4, 5);
    m2 = new CompulsoryModule("Discrete Mathematics", 3, 5);
    m3 = new CompulsoryModule("Software Engineering", 5, 5);
    m4 = new ElectiveModule(".Net Technology", 6, 5, "FIT");
    m5 = new ElectiveModule("Java Technology", 6, 5, "FIT");
    modules.add(m1);
    modules.add(m2);
    modules.add(m3);
    modules.add(m4);
    modules.add(m5);

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
    e = new Enrolment(students.get(0), modules.get(2));
    enrolments.add(e);
    e = new Enrolment(students.get(1), modules.get(3), 7.0, 9.0);
    enrolments.add(e);
    e = new Enrolment(students.get(2), modules.get(3));
    enrolments.add(e);
    e = new Enrolment(students.get(3), modules.get(4), 5.0, 10.0);
    enrolments.add(e);
    e = new Enrolment(students.get(4), modules.get(4));
    enrolments.add(e);
    e = new Enrolment(students.get(0), modules.get(3), 6.0, 8.0);
    enrolments.add(e);

    //return enrolments;
  }

  public void initSClasses() {
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
  
  public void createCourseModuleObject() throws DataSourceException {
    System.out.println("saveObject()");

    Module m = new CompulsoryModule("NEW module", 6, 5);
    schema.getDom().addObject(m);
  }
  
  @Override
  public void createObjects() throws DataSourceException {
    createObjects(true);
  }
  
  public void createObjects(boolean createCityObjects) throws DataSourceException {
    System.out.printf("createObjects(withCity=%b)%n", createCityObjects);

    DODMBasic schema = instance.getDODM();
    
    Map<Class,Collection> data = instance.getData();

    Class c;
    Collection objects;
    for (Entry<Class, Collection> e : data.entrySet()) {
      c = e.getKey();
      if (!createCityObjects && c == City.class) {
        // skip City
        continue;
      }
      
      objects = e.getValue();
      System.out.println("Creating objects for: " + c.getSimpleName());
      for (Object o : objects) {
        schema.getDom().addObject(o);
      }
    }
  }
  
  public Collection<Module> addCourseModules() throws DataSourceException {
    System.out.println("addModules()");

    Module m;
    Collection<Module> modules = new ArrayList();
    
    m = new CompulsoryModule("CModule 1", 4, 5);
    modules.add(m);
    schema.getDom().addObject(m);
    m = new CompulsoryModule("CModule 2", 3, 5);
    modules.add(m);
    schema.getDom().addObject(m);
    m = new CompulsoryModule("CModule 3", 5, 5);
    modules.add(m);
    schema.getDom().addObject(m);
    
    m = new ElectiveModule("EModule 1", 6, 5, "FIT");
    modules.add(m);
    schema.getDom().addObject(m);
    m = new ElectiveModule("EModule 2", 6, 5, "FIT");
    modules.add(m);
    schema.getDom().addObject(m);

    return modules;
  }
  
  /**
   * @requires
   *  {@link #addClasses()} /\
   *  cities have already been added to data source
   */
  public Collection<Student> addStudents(Collection<City> cities) throws DataSourceException {
    method("initStudents()");

    Class c = Student.class;
    
    Student s;
    Collection<Student> students = new ArrayList<Student>();
    
    City city = getObject(cities, 0);
    s = new Student("Trần Lý (1)", //
        "1/1/1981", city, "ly1@yahoo.com");
    schema.getDom().addObject(s);
    students.add(s);
    
    city = getObject(cities, 1);
    s = new Student("Trần Lý (2)", //
        "2/2/1982", city, "ly2@yahoo.com");
    schema.getDom().addObject(s);
    students.add(s);

    return students;
  }
  
  public void updateStudentObject() throws DataSourceException, NotFoundException,
      NotPossibleException {
    //
    System.out.println("updateObject()");
    Object[] idVals = { "S2012" };
    Student s = (Student) getObject(Student.class, idVals);

    System.out.println("Student object " + s.toString(true));

    Random rand = new Random();
    int charCode = Math.max(65, 65 + Math.abs(rand.nextInt(52)));
    String name = "Nguyen Tat " + ((char) charCode); // s.getName();
    System.out.println("updated name: " + name);
    String dob = s.getDob();
    City address = s.getAddress();
    String email = s.getEmail();

    Object[] vals = new Object[] { name, dob, address, email };

    schema.getDom().updateObjectComplete(s, vals);
    System.out.println("updated");
  }

  public void updateCompoundDomainTypeKeyEnrolmentObject() throws DataSourceException,
      NotFoundException, NotPossibleException {
    //
    System.out.println("updateCompoundObject()");
    Student s;
    Module m;

    // Student s = (Student) getObject(Student.class, new String[] { "S2012" });
    // Module m = (Module) getObject(Module.class, new String[] { "M500" });

    // Object[] idVals = { s, m };
    Object[] idVals = { 6 };

    Enrolment e = (Enrolment) getObject(Enrolment.class, idVals);

    System.out.println("Object " + e.toString(true));

    Random rand = new Random();

    Double imark = Math.max(1.0, Math.abs(rand.nextInt(10)));
    System.out.println("updated internal mark : " + imark);
    Double emark = e.getExamMark();
    char grade = e.getFinalGrade();
    s = e.getStudent();
    m = e.getModule();

    Object[] vals = new Object[] { s, m, imark, emark, grade };

    schema.getDom().updateObjectComplete(e, vals);
    System.out.println("updated");
  }

  public void updateCourseModuleSubtype() throws DataSourceException, NotFoundException,
      NotPossibleException {
    // get a module
    method("updateSubtype()");
    Object[] idVals = { "M600" };
    Module m = (Module) getObject(Module.class, idVals);

    System.out.println("Object " + m);

    // update semester
    // Random rand = new Random();
    // int charCode = Math.max(65, 65 + Math.abs(rand.nextInt(52)));
    // String name = "Nguyen Tat " + ((char) charCode); // s.getName();
    int credits = m.getCredits();
    System.out.println("old credits: " + credits);
    credits = credits + 2;
    System.out.println("new credits: " + credits);
    System.out.println("old name: " + m.getName());
    // reverse
    String name = (new StringBuffer(m.getName())).reverse().toString();
    System.out.println("new name (reversed): " + name);

    Object[] vals;
    if (m instanceof ElectiveModule) {
      vals = new Object[] { name, m.getSemester(), credits,
        ((ElectiveModule)m).getDeptName()};
    } else {
      vals = new Object[] { name, m.getSemester(), credits };      
    }
    
    schema.getDom().updateObjectComplete(m, vals);

    System.out.println("updated");
  }
  
  /**
   * @effects 
   *  delete a <tt>Student</tt> object
   * @throws DataSourceException
   * @throws NotFoundException
   */
  public void deleteStudentObject() throws DataSourceException, NotFoundException {
    method("deleteObject()");
    
    Object[] idVals = { "S2012" };

    Student s = (Student) getObject(Student.class, idVals);

    System.out.println("Student object " + s.toString(true));

    schema.getDom().deleteObject(Student.class, idVals);
    System.out.println("deleted");
  }

  public void getStudentObject() throws NotFoundException, NotPossibleException {
    method("getObject()");
    Class cls = Student.class;

    Object[] obids = { "2012" };

    System.out.println("Object id(s):" + obids[0]);

    Object obj = getObject(cls, obids);
    System.out.println("Found object: " + obj);

  }
  
}
