package jda.test.app.courseman.extended;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.extended.City;
import jda.test.model.extended.CompulsoryModule;
import jda.test.model.extended.ElectiveModule;
import jda.test.model.extended.Enrolment;
import jda.test.model.extended.Module;
import jda.test.model.extended.SClass;
import jda.test.model.extended.Student;

public class CourseManExtendedTester extends DODMEnhancedTester { //TestDBEnhanced {
  protected void initClasses() {
      domainClasses = new Class[] {
      City.class, //
      SClass.class, //
      Student.class, //
      Module.class, //
      CompulsoryModule.class, //
      ElectiveModule.class, //
      Enrolment.class, //
    };
  }
  
  // /// Testable methods /////
  // /// To be used by individual test case sub-classes ////
  public void initData() throws DataSourceException {
    // only invoked once for all test cases
    method("initData()");

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
    
    Calendar cal = Calendar.getInstance();
    int dateCount = 1; 
    final long dateLength = 24 * 60 * 60 * 1000 * 15; // 1-day in millis
    Date creation =  cal.getTime();
    
    SClass sc = new SClass(1, "class #1", creation);
    classes.add(sc);
    
    cal.setTimeInMillis(cal.getTimeInMillis()+(dateLength*dateCount));
    dateCount++;
    creation = cal.getTime();
    sc = new SClass(2, "class #2", creation);
    classes.add(sc);
    
    cal.setTimeInMillis(cal.getTimeInMillis()+(dateLength*dateCount));
    dateCount++;
    creation = cal.getTime();
    sc = new SClass(3, "class #3", creation);
    classes.add(sc);
    
    cal.setTimeInMillis(cal.getTimeInMillis()+(dateLength*dateCount));
    dateCount++;
    creation = cal.getTime();
    sc = new SClass(4, "class #4", creation);
    classes.add(sc);

    //return classes;
  }
  
  protected Tuple2<Oid,Object> getObject(Class cls, Object[] obids)
      throws NotFoundException, NotPossibleException {
    return instance.getDODM().getDom().lookUpObjectById(cls, obids);
  }

  public int getAssociationLinkCount(Class c1, DAttr attrib1, 
      Object o) throws Exception {
    
    DODMBasic schema = instance.getDODM();
    int linkCount = schema.getDom().getAssociationLinkCount(c1, attrib1, o); 
    return linkCount;
  }
  
  public void setAssociationLinkCount(Class c1, DAttr attrib1, 
      Object o, int linkCount) throws Exception {
    
    DODMBasic schema = instance.getDODM();
    schema.getDom().setAssociationLinkCount(c1, attrib1, o, linkCount); 
  }
  
  public int loadAssociationLinkCount(Class c2, DAttr attrib2, 
      Object o, Oid id) throws Exception {
    
    DODMBasic schema = instance.getDODM();
    
    int linkCount = schema.getDom().loadAssociationLinkCount(c2, attrib2, o, id); 
    return linkCount;
  }
  
  public int getAssociationCardMin(Class c, DAttr attrib) throws Exception {
    DODMBasic schema = instance.getDODM();

    Tuple2<DAttr,DAssoc> t = schema.getDsm().getAssociation(c, attrib);
    
    return t.getSecond().associate().cardMin();
  }

  public int getAssociationCardMax(Class c, DAttr attrib) throws Exception {
    DODMBasic schema = instance.getDODM();

    Tuple2<DAttr,DAssoc> t = schema.getDsm().getAssociation(c, attrib);
    
    return t.getSecond().associate().cardMax();
  }
  
  public void deleteObject(Class c, Object o, Oid oid) throws DataSourceException {
    DODMBasic schema = instance.getDODM();

    schema.getDom().deleteObject(o, oid, c);
  }

  /**
   * @effects 
   *  load object from db
   */
  public Tuple2<Oid,Object> loadObjectWithOid(Class c, Query q) throws DataSourceException {
    DODMBasic schema = instance.getDODM();
    
    Map<Oid,Object> objs = schema.getDom().retrieveObjects(c, q);
    if (objs != null) {
      Entry<Oid,Object> e = objs.entrySet().iterator().next();
      Tuple2<Oid,Object> t = new Tuple2<Oid,Object>(e.getKey(), e.getValue());
      return t;
    } else {
      return null;
    }
  }
  
//  public Tuple2<Oid, Object> deleteRandom(Class c) throws DBException {
//    DomainSchema schema = instance.getDomainSchema();
//
//    // get a random object
//    int countObjs = schema.getDom().getObjectCount(c);
//    Iterator<Entry<Oid,Object>> objects = schema.getDom().getObjectIterator(c);
//    
//    int rand = (int) (Math.random()*countObjs);
//    Tuple2<Oid,Object> tuple = getObject(objects, rand);
//    
//    // delete the object
//    Oid oid = tuple.getFirst();
//    Object o = tuple.getSecond();
//    deleteObject(c, o, oid);
//    
//    return tuple;
//  }
  
//  public <T> Tuple2<Oid,T> getObject(Iterator<Entry<Oid,T>> mapIt, int index) {
//    int i = 0;
//    while (i < index) {
//      mapIt.next();
//      i++;
//    }
//  
//    Entry<Oid,T> entry = mapIt.next();
//    
//    Tuple2<Oid,T> tuple = new Tuple2<Oid,T>(entry.getKey(), entry.getValue());
//    return tuple;
//  }
}
