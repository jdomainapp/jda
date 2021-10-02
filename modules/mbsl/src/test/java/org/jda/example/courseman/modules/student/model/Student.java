package org.jda.example.courseman.modules.student.model;

import java.util.ArrayList;
import java.util.Collection;

import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.courseman.modules.enrolment.model.Enrolment;
import org.jda.example.courseman.modules.sclass.model.SClass;
import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 * 
 * Represents a student.
 * 
 * @author dmle
 * @version 
 * -  support many-many association normaliser
 */
//@Pattern(name="pattern1", role="M1")
public class Student{
  public static final String A_id = "id";
  public static final String A_name = "name";
  //public static final String A_address = "address";
  public static final String A_modules = "modules";
  public static final String A_enrolments = "enrolments";

  
  public static final String A_helpRequested = "helpRequested";

  /*** STATE SPACE **/
  @DAttr(name="id",type=Type.Integer,id=true,auto=true,mutable=false,optional=false,min=1)
  private int id;
  
  @DAttr(name=A_name,type=Type.String,length=30,optional=false)
  private String name;
  
  @DAttr(name=A_helpRequested,type=Type.Boolean, serialisable=false)
  private boolean helpRequested;
  
  ///// Many-Many Association to SClass
  //@Pattern(name = "pattern1", role = "a2")
  @DAttr(name = "sclasses", type = Type.Collection, serialisable = false, filter = @Select(clazz = SClass.class))
  @DAssoc(ascName = "M12-m-assoc", role = "r1", ascType = AssocType.Many2Many, endType = AssocEndType.Many, associate = @Associate(type = SClass.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE), normAttrib = "classRegists")
  private Collection<SClass> sclasses;

  //@Pattern(name = "pattern1", role = "aNorm")
  @DAttr(name = "classRegists", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = SClassRegistration.class))
  @DAssoc(ascName = "M1-assoc-I", role = "r1", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = SClassRegistration.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
  private Collection<SClassRegistration> classRegists;  
  
  //derived
   private int classRegistsCount;
  ///// End Association to SClass

  ///// Many-Many Association to CourseModule
  @DAttr(name=A_modules,type=Type.Collection,serialisable=false,filter = @Select(clazz = CourseModule.class))
  @DAssoc(ascName="enrols-in",role="std",
    ascType=AssocType.Many2Many,endType=AssocEndType.Many,
    associate=@Associate(type=CourseModule.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE),
    normAttrib="enrolments")
  private Collection<CourseModule> modules;
  
  @DAttr(name=A_enrolments,type=Type.Collection,optional=false,serialisable=false,
		  filter=@Select(clazz=Enrolment.class))
  @DAssoc(ascName="std-has-enrols",role="std",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Enrolment.class,cardMin=0,cardMax=30))
  private Collection<Enrolment> enrolments;  
  
  // derived
  private int enrolmentCount;
  ///// End Association to CourseModule

  // virtual link to EnrolmentMgmt (sequential)
  @DAttr(name="enrolmentMgmt1",type=Type.Domain,serialisable=false,virtual=true)
  private org.jda.example.courseman.modules.enrolmentmgmt.sequential.model.EnrolmentMgmt enrolmentMgmt1;

  // virtual link to EnrolmentMgmt (decisional)
  @DAttr(name="enrolmentMgmt2",type=Type.Domain,serialisable=false,virtual=true)
  private org.jda.example.courseman.modules.enrolmentmgmt.decisional.model.EnrolmentMgmt enrolmentMgmt2;

  // virtual link to EnrolmentMgmt (forked)
  @DAttr(name="enrolmentMgmt3a",type=Type.Domain,serialisable=false,virtual=true)
  private org.jda.example.courseman.modules.enrolmentmgmt.forked.model.EnrolmentMgmt enrolmentMgmt3a;

  // virtual link to EnrolmentMgmt (forkedAndJoined)
  @DAttr(name="enrolmentMgmt3b",type=Type.Domain,serialisable=false,virtual=true)
  private org.jda.example.courseman.modules.enrolmentmgmt.forkedandjoined.model.EnrolmentMgmt enrolmentMgmt3b;


  /*** BEHAVIOUR SPACE **/
  // static variable to keep track of student id
  private static int idCounter = 0;

  // constructor methods
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Student(String name, Boolean helpRequested, Collection<SClass> sclasses, Collection<CourseModule> modules) {
    this(null, name, helpRequested, sclasses, modules);
  }

  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  // decisional activity
  public Student(String name, Boolean helpRequested, Collection<CourseModule> modules) {
    this(null, name, helpRequested, null, modules);
  }
  
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  // forked activity
  public Student(String name, Collection<CourseModule> modules) {
    this(null, name, null, null, modules);
  }
  
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public Student(String name) {
    this(null, name, null, null, null);
  }

  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Student(Integer id, String name) {
    this(id, name, null, null, null);
  }
  
  private Student(Integer id, String name, Boolean helpRequested, Collection<SClass> sclasses, Collection<CourseModule> modules) {
    // generate an id
    this.id = nextID(id);
    this.name = name;
    
    if (helpRequested != null)
      this.helpRequested = helpRequested;
    else
      this.helpRequested = false;
    
    this.sclasses = sclasses;
    this.classRegists = new ArrayList<>();
    this.modules = modules;
    this.enrolments = new ArrayList<>();
  }
  
  @DOpt(type=DOpt.Type.AutoAttributeValueGen)
  private static int nextID(Integer currID) {
	// automatically generate the next student id
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num = currID.intValue();
      
      if (num > idCounter) {
        idCounter=num;
      }   
      return currID;
    }
  }
  
  // setter methods
  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="name")
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @effects 
   *  return {@link #helpRequested}
   */
  public boolean getHelpRequested() {
    return helpRequested;
  }
  
  /**
   * @effects 
   *  set this.{@link #helpRequested} = helpRequested
   */
  public void setHelpRequested(boolean helpRequested) {
    this.helpRequested = helpRequested;
  }
  
  /**Association {@link #enrolments} */
  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="enrolments")
  public Collection<Enrolment> getEnrolments() {
    return enrolments;
  }

  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="enrolments")
  public void setEnrolments(Collection<Enrolment> en) {
    this.enrolments = en;
    enrolmentCount = en.size();
  }

  
  @DOpt(type=DOpt.Type.LinkAdderNew)  @AttrRef("enrolments")
  public boolean addNewEnrolment(Enrolment e) {
    enrolments.add(e);
    
    // update course module
    addModule(e.getModule());
    
    enrolmentCount++;
    
    // no other attributes changed (average mark is not serialisable!!!)
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdderNew)  @AttrRef("enrolments")
  public boolean addNewEnrolment(Collection<Enrolment> enrols) {
    enrolments.addAll(enrols);
    
    // update course modules
    for (Enrolment en: enrols)
      addModule(en.getModule());

    enrolmentCount+=enrols.size();
    
    // no other attributes changed (average mark is not serialisable!!!)
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)  @AttrRef("enrolments")
  public boolean addEnrolment(Enrolment e) {
    if (!enrolments.contains(e)) {
      enrolments.add(e);
    
      // update course module
      addModule(e.getModule());

      enrolmentCount++;
    }
    
    // no other attributes changed
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)  @AttrRef("enrolments")
  public boolean addEnrolment(Collection<Enrolment> enrols) {
    for (Enrolment e : enrols) {
      if (!enrolments.contains(e)) {
        enrolments.add(e);
        
        // update course modules
        addModule(e.getModule());
        
        enrolmentCount++;
      }
    }

    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkRemover)  @AttrRef("enrolments")
  public boolean removeEnrolment(Enrolment e) throws ConstraintViolationException {
    boolean removed = enrolments.remove(e);
    
    if (removed) {
      // update course modules
      removeModule(e.getModule());
      
      enrolmentCount--;
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)  @AttrRef("enrolments")
  public Integer getEnrolmentsCount() {
    return enrolmentCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)  @AttrRef("enrolments")

  public void setEnrolmentsCount(int count) {
    enrolmentCount = count;
  }
  
  /**END Association {@link #enrolments} */

  /** ASSOCIATION {@link #modules}: maintained via {@link #enrolments} */
  @DOpt(type=DOpt.Type.Getter) @AttrRef(value="modules")
  public Collection<CourseModule> getModules() {
    return modules;
  }

  @DOpt(type=DOpt.Type.Setter) @AttrRef(value="modules")
  public void setModules(Collection<CourseModule> modules) {
    this.modules = modules;
  }

  /**
   * @effects 
   *  add <tt>mod</tt> to {@link #modules}
   */
  private void addModule(CourseModule mod) {
    if (modules == null) this.modules = new ArrayList<>();
    if (!modules.contains(mod))
      modules.add(mod);
  }
  
  /**
   * @effects 
   *  remove <tt>mod</tt> to {@link #modules}
   */
  private void removeModule(CourseModule mod) {
    if (modules != null) {
      modules.remove(mod);
    }
  }
  
  /** end ASSOCIATION {@link #modules}*/
  
  // getter methods
  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="id")
  public int getId() {
    return id;
  }

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="name")
  public String getName() {
    return name;
  }

  /**
   * @effects return {@link #toString(boolean)}<tt> -> (true)</tt>
   */
  @Override
  public String toString() {
    return toString(true);
  }

  /**
   * @effects returns <code>Student(id,name,dob,address,email)</code>.
   */
  public String toString(boolean full) {
    if (full)
      return "Student(" + id + "," + name + ")";
    else
      return "Student(" + id + ")";
  }

  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Student other = (Student) obj;
    if (id != other.id)
      return false;
    return true;
  }

  /**
   * @requires 
   *  minVal != null /\ maxVal != null
   * @effects 
   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
   */
  @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(
      DAttr attrib,
      Tuple derivingValue, 
      Object minVal, 
      Object maxVal) throws ConstraintViolationException {
    if (minVal != null && maxVal != null) {
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)  
          idCounter = maxIdVal;
      } 
      // TODO add support for other attributes here 
    }    
  }
  
  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value = "sclasses")
  public Collection<SClass> getSclasses() {
      return sclasses;
  }

  @DOpt(type = DOpt.Type.Setter)
  @AttrRef(value = "sclasses")
  public void setSclasses(Collection<SClass> associates) {
      this.sclasses = associates;
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getClassRegistsCount() {
    return classRegistsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setClassRegistsCount(int count) {
    classRegistsCount = count;
  }
  
  /**
 * @effects 
 *  add <tt>associate</tt> to {@link #sclasses}
 */
  @AttrRef(value = "sclasses")
  private void addSClass(SClass associate) {
      if (sclasses == null)
          sclasses = new ArrayList<>();
      if (!sclasses.contains(associate)) {
          sclasses.add(associate);
      }
  }

  /**
 * @effects 
 *  remove <tt>associate</tt> from {@link #sclasses}
 */
  @AttrRef(value = "sclasses")
  private void removeSClass(SClass associate) {
      if (sclasses != null) {
          sclasses.remove(associate);
      }
  }

  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value = "classRegists")
  public Collection<SClassRegistration> getClassRegists() {
      return classRegists;
  }

  @DOpt(type = DOpt.Type.Setter)
  @AttrRef(value = "classRegists")
  public void setClassRegists(Collection<SClassRegistration> associates) {
      this.classRegists = associates;
      classRegistsCount = associates.size();
  }

  @DOpt(type = DOpt.Type.LinkAdderNew)
  @AttrRef(value = "classRegists")
  public boolean addNewClassRegists(SClassRegistration associate) {
      classRegists.add(associate);
      // update sclasses
      addSClass(associate.getSClass());
      classRegistsCount++;
      // no other attributes changed
      return false;
  }

  @DOpt(type = DOpt.Type.LinkAdderNew)
  @AttrRef(value = "classRegists")
  public boolean addNewClassRegists(Collection<SClassRegistration> associates) {
      classRegists.addAll(associates);
      // update sclasses
      for (SClassRegistration assoc : associates) {
          addSClass(assoc.getSClass());
      }
      classRegistsCount += associates.size();
      // no other attributes changed
      return false;
  }

  @DOpt(type = DOpt.Type.LinkAdder)
  @AttrRef(value = "classRegists")
  public boolean addClassRegists(SClassRegistration associate) {
      if (!classRegists.contains(associate)) {
          classRegists.add(associate);
          // update sclasses
          addSClass(associate.getSClass());
          classRegistsCount++;
      }
      // no other attributes changed
      return false;
  }

  @DOpt(type = DOpt.Type.LinkAdder)
  @AttrRef(value = "classRegists")
  public boolean addClassRegists(Collection<SClassRegistration> associates) {
      for (SClassRegistration assoc : associates) {
          if (!classRegists.contains(assoc)) {
              classRegists.add(assoc);
              // update sclasses
              addSClass(assoc.getSClass());
              classRegistsCount++;
          }
      }
      // no other attributes changed
      return false;
  }

  @DOpt(type = DOpt.Type.LinkRemover)
  @AttrRef(value = "classRegists")
  public boolean removeClassRegists(SClassRegistration associate) throws ConstraintViolationException {
      boolean removed = classRegists.remove(associate);
      if (removed) {
          // update sclasses
          removeSClass(associate.getSClass());
          classRegistsCount--;
      }
      // no other attributes changed
      return false;
  }
}
