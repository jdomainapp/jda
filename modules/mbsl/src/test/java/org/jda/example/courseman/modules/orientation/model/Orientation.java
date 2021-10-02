package org.jda.example.courseman.modules.orientation.model;

import org.jda.example.courseman.modules.enrolmentmgmt.merged.model.control.MgEnrolmentProcessing;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 *  Represents orientation information given to students.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@DClass(mutable=false)
public class Orientation {
  @DAttr(name = "id", type = Type.Integer, id = true, auto = true, mutable = false, optional = false, min = 1)
  private int id;

  @DAttr(name = "content", type = Type.String, length = 10000)
  private String content;

  // virtual link
  @DAttr(name="enrolmentProc",type=Type.Domain,serialisable=false)
  private MgEnrolmentProcessing enrolmentProc;
  
  private static int idCounter;

  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value = "id")
  public int getId() {
      return this.id;
  }

  @DOpt(type = DOpt.Type.AutoAttributeValueGen)
  @AttrRef(value = "id")
  private static int genId(Integer id) {
      Integer val;
      if (id == null) {
          idCounter++;
          val = idCounter;
      } else {
          if (id > idCounter) {
              idCounter = id;
          }
          val = id;
      }
      return val;
  }

  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value = "content")
  public String getContent() {
      return this.content;
  }

  @DOpt(type = DOpt.Type.Setter)
  @AttrRef(value = "content")
  public void setContent(String content) {
      this.content = content;
  }

  @DOpt(type = DOpt.Type.DataSourceConstructor)
  public Orientation(Integer id, String content) throws ConstraintViolationException {
      this.id = genId(id);
      this.content = content;
  }

  @DOpt(type = DOpt.Type.ObjectFormConstructor)
  public Orientation(String content) throws ConstraintViolationException {
      this.id = genId(null);
      this.content = content;
  }

  @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
  public static void synchWithSource(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal) throws ConstraintViolationException {
      String attribName = attrib.name();
      if (attribName.equals("id")) {
          int maxIdVal = (Integer) maxVal;
          if (maxIdVal > idCounter)
              idCounter = maxIdVal;
      }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "Orientation (" + id + ", " + content + ")";
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Orientation other = (Orientation) obj;
    if (id != other.id)
      return false;
    return true;
  }
  
  
}
