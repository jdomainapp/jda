package org.jda.example.coursemankafka.services.studentregist2;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class StudentRegist {
  private String student;
  
  private String courseModule;

  /**
   * @effects 
   *
   * @version 
   */
  public StudentRegist(String student, String courseModule) {
    super();
    this.student = student;
    this.courseModule = courseModule;
  }

  /**
   * @effects return student
   */
  public String getStudent() {
    return student;
  }

  /**
   * @effects set student = student
   */
  public void setStudent(String student) {
    this.student = student;
  }

  /**
   * @effects return courseModule
   */
  public String getCourseModule() {
    return courseModule;
  }

  /**
   * @effects set courseModule = courseModule
   */
  public void setCourseModule(String courseModule) {
    this.courseModule = courseModule;
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "StudentRegist (student=" + student + ", courseModule="
        + courseModule + ")";
  }
  
  
  
  
}
