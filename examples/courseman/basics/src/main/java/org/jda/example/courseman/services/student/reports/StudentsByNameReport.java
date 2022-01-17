package org.jda.example.courseman.services.student.reports;

import java.util.Collection;
import java.util.Map;

import org.jda.example.courseman.services.student.model.Student;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.report.Output;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.QRM;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;

/**
 * @overview 
 * 	Represent the reports about students by name.
 * 
 * @author dmle
 *
 * @version 5.0
 */
@DClass(schema="courseman",serialisable=false)
public class StudentsByNameReport {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  /**input: student name */
  @DAttr(name = "name", type = Type.String, length = 30, optional = false)
  private String name;
  
  /**output: students whose names match {@link #name} */
  @DAttr(name="students",type=Type.Collection,optional=false, mutable=false,
      serialisable=false,filter=@Select(clazz=Student.class, 
      attributes={Student.A_id, Student.A_name, Student.A_dob, Student.A_address, 
          Student.A_email, Student.A_rptStudentByName})
      ,derivedFrom={"name"}
      )
  @DAssoc(ascName="students-by-name-report-has-students",role="report",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Student.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE
    ))
  @Output
  private Collection<Student> students;

  /**output: number of students found (if any), derived from {@link #students} */
  @DAttr(name = "numStudents", type = Type.Integer, length = 20, auto=true, mutable=false)
  @Output
  private int numStudents;
  
  /**
   * @effects 
   *  initialise this with <tt>name</tt> and use {@link QRM} to retrieve from data source 
   *  all {@link Student} whose names match <tt>name</tt>.
   *  initialise {@link #students} with the result if any.
   *  
   *  <p>throws NotPossibleException if failed to generate data source query; 
   *  DataSourceException if fails to read from the data source
   * 
   */
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public StudentsByNameReport(@AttrRef("name") String name) throws NotPossibleException, DataSourceException {
    this.id=++idCounter;
    
    this.name = name;
    
    doReportQuery();
  }
  
  /**
   * @effects return name
   */
  public String getName() {
    return name;
  }

  /**
   * @effects <pre>
   *  set this.name = name
   *  if name is changed
   *    invoke {@link #doReportQuery()} to update the output attribute value
   *    throws NotPossibleException if failed to generate data source query; 
   *    DataSourceException if fails to read from the data source.
   *  </pre>
   */
  public void setName(String name) throws NotPossibleException, DataSourceException {
//    boolean doReportQuery = (name != null && !name.equals(this.name));
    
    this.name = name;
    
    // DONOT invoke this here if there are > 1 input attributes!
    doReportQuery();
  }

  /**
   * This method is invoked when the report input has be set by the user. 
   * 
   * @effects <pre>
   *   formulate the object query
   *   execute the query to retrieve from the data source the domain objects that satisfy it 
   *   update the output attributes accordingly.
   *  
   *  <p>throws NotPossibleException if failed to generate data source query; 
   *  DataSourceException if fails to read from the data source. </pre>
   */
  @DOpt(type=DOpt.Type.DerivedAttributeUpdater)
  @AttrRef(value="students")
  public void doReportQuery() throws NotPossibleException, DataSourceException {
    // the query manager instance
    
    QRM qrm = QRM.getInstance();
    
    // create a query to look up Student from the data source
    // and then populate the output attribute (students) with the result
    DSMBasic dsm = qrm.getDsm();
    
    //TODO: to conserve memory cache the query and only change the query parameter value(s)
    Query q = QueryToolKit.createSearchQuery(dsm, Student.class, 
        new String[] {Student.A_name}, 
        new Op[] {Op.MATCH}, 
        new Object[] {"%"+name+"%"});
    
    Map<Oid, Student> result = qrm.getDom().retrieveObjects(Student.class, q);
    
    if (result != null) {
      // update the main output data 
      students = result.values();
      
      // update other output (if any)
      numStudents = students.size();
    } else {
      // no data found: reset output
      resetOutput();
    }
  }

  /**
   * @effects 
   *  reset all output attributes to their initial values
   */
  private void resetOutput() {
    students = null;
    numStudents = 0;
  }

  /**
   * A link-adder method for {@link #students}, required for the object form to function.
   * However, this method is empty because students have already be recorded in the attribute {@link #students}.
   */
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addStudent(Collection<Student> students) {
    // do nothing
    return false;
  }
  
  /**
   * @effects return students
   */
  public Collection<Student> getStudents() {
    return students;
  }
  
  /**
   * @effects return numStudents
   */
  public int getNumStudents() {
    return numStudents;
  }

  /**
   * @effects return id
   */
  public int getId() {
    return id;
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
    StudentsByNameReport other = (StudentsByNameReport) obj;
    if (id != other.id)
      return false;
    return true;
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
    return "StudentsByNameReport (" + id + ", " + name + ")";
  }

}
