package jda.test.dodm.metaoperations.valueAdder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.extended.City;
import jda.test.model.extended.CompulsoryModule;
import jda.test.model.extended.Enrolment;
import jda.test.model.extended.Module;
import jda.test.model.extended.Student;

public class AttributeValueMethodsTest extends CourseManExtendedTester {

  private Class Student;
  private Class Enrolment;
  private Class City;

  private Student student;
  private Module m, m2, m3;
  private Enrolment e1, e2,e3;
  private City city1, city2;
  private double internalMark0, examMark0, internalMark1, examMark1, examMark2;
  
  private int enrolCount;
  private double[] finalMarks, avgMarks;
  private char[] finalGrades;
  
  private void initMyData() {
    student = new Student("S2014","Test", "1/1/1990", new City("Hanoi"), "test@gmail.com", null);
    m = new CompulsoryModule("Module test", 1, 1);
    e1 = new Enrolment(student, m);
    
    m2 = new CompulsoryModule("Module test", 1, 1);
    e2 = new Enrolment(student, m2);
    
    m3 = new CompulsoryModule("Module test2", 2, 2);
    e3 = new Enrolment(student, m3);
    
    city1 = new City("HCM");
    city2 = new City("Vinh");

    internalMark0 = 0D;
    examMark0 = 0D;
    internalMark1 = 4D;
    examMark1 = 6D;
    examMark2 = 3D;
    
    enrolCount = 3-1;
    finalMarks = new double[] {
        Math.floor(0.4*internalMark0+0.6*examMark0),
        Math.floor(0.4*internalMark1+0.6*examMark1), 
        Math.floor(0.4*internalMark1+0.6*examMark2)};  // depends on internal and exam marks (above)
    avgMarks = new double[finalMarks.length];  // depends on final mark and enrolment count (above)
    int i = 0;
    for (double fm : finalMarks) {
      avgMarks[i] = fm/enrolCount;
      i++;
    }
    finalGrades = new char[] {'P', 'F'};  // depends on finalmarks (above)
  }
  
  @Test
  public void doTests() throws Exception {
    registerClasses();
    
    initMyData();
    
    System.out.printf("Student: %s%n", student);

    // adder
    adderTest1();
    adderTest2();
    adderTest3();
    
    // deleter
    deleterTest();
    
    // updater
    updaterTest();
  }
  
  public void registerClasses() throws DataSourceException {
    Student = Student.class;
    Enrolment = Enrolment.class;
    City = City.class;
    
    registerClass(Student, false);
    registerClass(Enrolment, false);
    registerClass(City, false);
  }

  public void adderTest1() throws DataSourceException {
    System.out.println("\n"+this.getClass().getSimpleName() + ".adderTest1()");
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    /*
     * Example1: 1-M associaition, value is a single object 
     *  o1 = Student<S2014>
     *  attributeName = "enrolments"
     *  v1 = Enrolment<1>
     *  
     */

    System.out.printf("Enrolment records:%n");
    printStudentEnrolments(student);
    
    String attribName = "enrolments";
    /*v2.7.3 
      dom.updateAssociateLink(student, attribName, e1);
     */
    dom.addAssociateLink(student, attribName, e1);
    
    System.out.printf("Updated enrolment records:%n");
    printStudentEnrolments(student);
  }

  public void adderTest2() throws DataSourceException {
    System.out.println("\n"+this.getClass().getSimpleName() + ".adderTest2()");
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();    
    /*
     * Example2: 1-M associaition, value is a collection 
     *  o2 = Student<S2014>
     *  attributeName = "enrolments"
     *  v2 = List:[Enrolment<2>, Enrolment<3>]
     */
//    Student o1 = new Student("S2014","Test", "1/1/1990", new City("Hanoi"), "test@gmail.com", null);
//    System.out.printf("Student: %s%n", o1);

    System.out.printf("Enrolment records:%n");
    printStudentEnrolments(student);
    
    List<Enrolment> enrols = new ArrayList();
    enrols.add(e2);
   
    enrols.add(e3);
    
    String attribName = "enrolments";
    /*v2.7.3
    dom.updateAssociateLink(student, attribName, enrols);
    */
    dom.addAssociateLink(student, attribName, enrols);
    
    System.out.printf("Updated enrolment records:%n");
    printStudentEnrolments(student);
  }

  public void adderTest3() throws DataSourceException {
    System.out.println("\n"+this.getClass().getSimpleName() + ".adderTest3()");
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    /*
     * Example3: 1-1 associaition 
     *  o3 = Student<S2014>
     *  attributeType = City.class
     *  v3 = City<HCM>
     */
    System.out.printf(" city: %s%n", student.getAddress());
    DAttr attrib1 = schema.getDomainConstraint(Student, "address");
    
    // update 1: using attribute type
    /*v2.7.3
    dom.updateAssociateLink(student, attrib1, city1);
     */
    dom.addAssociateLink(student, attrib1, city1);
    
    System.out.printf(" updated city #1: %s%n", student.getAddress());

    // update 2: using attribute name
    String attribName = "address";
    /*v2.7.3
    dom.updateAssociateLink(student, attribName, city2);
    */ 
    dom.addAssociateLink(student, attribName, city2);
    
    System.out.printf(" updated city #2: %s%n", student.getAddress());
  }

  private void printStudentEnrolments(Student s) {
    Collection<Enrolment> enrolments = s.getEnrolments();
    if (enrolments == null || enrolments.isEmpty()) {
      System.out.println("  No enrolments");
    } else {
      for (Enrolment e : enrolments) {
        System.out.printf("  Enrolment: %s%n", e);
      }
    }
  }
  
  public void deleterTest() {
    System.out.println("\n"+this.getClass().getSimpleName() + ".deleterTest()");
    /*
     * Example1: 1-M associaition, value is a collection 
     *  o = Student<S2014> with enrolments = List:[Enrolment<x>,...] 
     *  v = Enrolment<x>
     */
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    System.out.printf("Enrolment records:%n");
    printStudentEnrolments(student);
    
    // get an enrolment to remove
    Enrolment e = student.getEnrolments().get(0);

    System.out.printf("To remove: %s%n", e);
    
    //schema.updateOneToManyAssociateOnDelete(student, Enrolment, e);
    DAttr attrib = schema.getDomainConstraint(Enrolment, "student");
    dom.updateAssociateToRemoveLink(student, e, attrib);
    
    assert (student.getEnrolmentsCount() == enrolCount) : "Invalid enrolment count: " + student.getEnrolmentsCount() + " (expected: "+enrolCount+")";
    System.out.printf("Updated enrolment records:%n");
    printStudentEnrolments(student);
  }
  
  public void updaterTest() throws DataSourceException {
    System.out.println("\n"+this.getClass().getSimpleName() + ".updaterTest()");

    /*
     * Example1:  
     *  o = Student<S2014>, whose total mark is M 
     *  v = Enrolment<1>: when final mark is changed will cause an update to o
     */
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();

    double avgMark0 = student.getAverageMark();
    assert (avgMark0 == avgMarks[0]) : "Invalid average mark: " + avgMark0 + " (expected: "+avgMarks[0]+")";

    System.out.printf("Enrolments count: %d%n", student.getEnrolmentsCount());
    System.out.printf("Current average mark: %f%n", avgMark0);
    
    // update enrolment with marks, the final mark and grade are updated last
    Enrolment e = student.getEnrolments().get(0);
    Map<DAttr,Object> newVals = new LinkedHashMap<DAttr,Object>();
    DAttr internalMarkAttrib = schema.getDomainConstraint(Enrolment, "internalMark");
    DAttr examMarkAttrib = schema.getDomainConstraint(Enrolment, "examMark");
    newVals.put(internalMarkAttrib, internalMark1);
    newVals.put(examMarkAttrib, examMark1);
    dom.updateObject(e, newVals, false); // update without saving to data base

    assert (e.getFinalMark() == finalMarks[1]) : "Invalid final mark: " + e.getFinalMark() + " (expected: "+finalMarks[1]+")";
    System.out.printf("  %s: final mark update #1 = %d%n", e, e.getFinalMark());

    // update student
    DAttr myAttrib = schema.getDomainConstraint(Student, "enrolments");
    
    dom.updateAssociateOnUpdate(student, Student, myAttrib, Enrolment, e);

    assert (student.getAverageMark() == avgMarks[1]) : "Invalid avg mark: " + student.getAverageMark() + " (expected: "+avgMarks[1]+")";
    System.out.printf("Average mark #1: %f%n", student.getAverageMark());
    
    // update one more time ...
    newVals.clear();
    newVals.put(examMarkAttrib, examMark2);
    dom.updateObject(e, newVals, false);

    assert (e.getFinalMark() == finalMarks[2]) : "Invalid final mark: " + e.getFinalMark() + " (expected: "+finalMarks[2]+")";
    System.out.printf("  %s: final mark update #2 = %d %n", e, e.getFinalMark());

    // update student
    dom.updateAssociateOnUpdate(student, Student, myAttrib, Enrolment, e);

    assert (student.getAverageMark() == avgMarks[2]) : "Invalid avg mark: " + student.getAverageMark() + " (expected: "+avgMarks[2]+")";
    System.out.printf("Average mark #2: %f%n", student.getAverageMark());
  }
}
