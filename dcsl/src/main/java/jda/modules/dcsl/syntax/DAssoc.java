package jda.modules.dcsl.syntax;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jda.modules.common.CommonConstants;

/**
 * @overview
 *  Define an <b>binary</b> association between domain classes, which is to be applied 
 *  to the domain attributes of the domain classes that implement each association.
 *  
 *  <p>For example, the following definitions define the association ends that are 
 *  implemented by two domain attributes, <tt>SClass.students, Student.sclass</tt>, 
 *  of the 1:M association between two domain classes <tt>SClass</tt> and <tt>Student</tt>:
 *  <pre>
 *  public class SClass {
 *    @Association(name="has",type=AssocType.One2Many,endType=AssocEndType.One,
 *                 role="student-class",
 *                 associates={@AssocEnd(type=Student.class,cardMin=1,cardMax=30)}
 *    private List<Student> students;
 *  }
 *  
 *  public class Student {
 *    @Association(name="has",type=AssocType.One2Many,endType=AssocEndType.Many,
 *                 role="student",
 *                 associates={@AssocEnd(type=SClass.class,cardMin=1,cardMax=1)}
 *    private SClass sclass;
 *  }
 *  
 *  </pre>
 *   
 * @author dmle
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DAssoc {

/**
   * The association name, which must be unique per domain class
   */
  public String ascName();

  /**
   * The role that is played by the current domain class in the association
   */
  public String role();
  
  /**
   * The association type as specified in {@link AssocType}
   */
  public AssocType ascType();
  
  /**
   * The association end type as specified in {@link AssocEndType}
   */
  public AssocEndType endType();
  
  /**
   * The associated classes together with the cardinality constraints of their participation 
   * in the association (as specified in {@link Associate})
   */
  public Associate associate();

  /**
   * whether or not this end of the association depends on the associate's end (i.e. that specified by {@link #associate())}.
   * <br>Default: <tt>false</tt>
   */
  public boolean dependsOn() default false;
  

  /**
   * Whether or not the state of the object at this end is derived from that of those at the other end (the associates).
   * 
   * <br>This is used to determine whether to receive state update from the associates.
   *  
   * <br>Default: <tt>false</tt>
   * 
   * @example
   * <pre>
   *  Given: 
   *    StudentClass.averageMark is derived from StudentClass.students
   *  
   *  class StudentClass {
   *  
   *    &at;Association(...,associate=...(type=Student.class,...),<b>derivedFrom</b>=true)
   *    private List<Student> students
   *    
   *  }
   * </pre>
   * @version 2.7.4
   */
  public boolean derivedFrom() default false;
  

  /**
   * <b>ONLY applicable</b> when {@link #ascType()} = {@link AssocType#Many2Many}
   * 
   * <p>Specifies the name of the domain attribute of the same class (e.g. <tt>Student.enrolments</tt>) that is used 
   * to normalise this end (e.g. <tt>Student.modules</tt>) of a many-many association (e.g. association <tt>enrols(Student,CourseModule)</tt>) 
   * by realising a one-many association to an associative class (e.g. <tt>Enrolment</tt> is associative class between <tt>Student, CourseModule</tt>).
   * 
   * <p>Default: {@link CommonConstants#NullString} 
   * 
   * @version 3.2
   */
  public String normAttrib() default CommonConstants.NullString;
  
  /**
   * 
   * @effects 
   *  Whether or not this is part of a reflexive association
   *  <br>Default: <tt>false</tt>
   * @version 3.3
   */
  public boolean reflexive() default false;

  /**
   * @overview a helper annotation used by {@link DAssoc} that defines an association end.
   */
  @Documented
  public static @interface Associate {
    
    /** the domain class that participates at this end */
    public Class type();
    
    /** min cardinality */
    public int cardMin();
    
    /** max cardinality */
    public int cardMax();

    /**
     * Applicable ONLY to 1:1 associations.
     *  
     * <p>Specifies whether or not the associate is the one that determines the relationship (i.e. strong).
     * It should <b>only be set to <tt>true</tt> for 1:1 associations</b> and will cause 
     * the PK of the associate to be exported as the FK of 
     * this class when it is serialised to a relational database, for example.
     * 
     * <p>It must be left to the default value <tt>false</tt> for all other types of associations.
     * 
     * <p>Default: <tt>false</tt> 
     */
    public boolean determinant() default false;

    /**
     * Whether or not the <b>associate</b> will update the association to this class with 
     * new association links when its objects are created.  
     * 
     * <p>This property is set to <tt>false</tt> for some associations (e.g. those used in a process-specific domain class) 
     * to block link update from the associates.  
     * 
     * <p>Default: <tt>true</tt> (i.e. to update the association with links)
     * @version 3.0
     */
    public boolean updateLink() default true;
  } // end @AssocEnd
  
  /**
   * Defines the association types
   */
  public static enum AssocType {
    /**1:1*/
    One2One,
    /**1:M*/
    One2Many,
    /**M:N*/
    Many2Many;
    
    /**
     * @effects 
     *  if this == other
     *    return true
     *  else
     *    return false
     */
    public boolean equals(AssocType other) {
      return this == other;
    }
  } // end AssocType
  
  /**
   * Defines the association end types
   */
  public static enum AssocEndType {
    One,
    Many;
    
    /**
     * @effects 
     *  if this == other
     *    return true
     *  else
     *    return false
     */
    public boolean equals(AssocEndType other) {
      return this == other;
    }
  } // end AssocEndType
}
