package org.jda.example.courseman.services.student.reports;

import java.util.Collection;
import java.util.Map;

import org.jda.example.courseman.services.student.model.City;
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
 * 	Represent a report about students by city whose view is expressed by a join query.
 * 
 * @author ducmle
 *
 * @version 5.3
 */
@DClass(schema="courseman",serialisable=false)
public class StudentsByCityJoinReport {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  /**input: city name */
  @DAttr(name = "cityName", type = Type.String, length = 30, optional = false)
  private String cityName;
  
  /**output: students whose names match {@link #cityName} */
  @DAttr(name="students",type=Type.Collection,optional=false, mutable=false,
      serialisable=false,filter=@Select(clazz=Student.class, 
      attributes={Student.A_id, Student.A_name, Student.A_dob, Student.A_address, 
          Student.A_email, Student.A_rptStudentByCity})
      ,derivedFrom={"cityName"}
      )
  @DAssoc(ascName="students-by-cityName-report-has-students",role="report",
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
   *  initialise this with <tt>cityName</tt> and use {@link QRM} to retrieve from data source 
   *  all {@link Student} whose addresses match {@link City}, whose names match <tt>cityName</tt>.
   *  initialise {@link #students} with the result if any.
   *  
   *  <p>throws NotPossibleException if failed to generate data source query; 
   *  DataSourceException if fails to read from the data source
   * 
   */
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public StudentsByCityJoinReport(@AttrRef("cityName") String name) throws NotPossibleException, DataSourceException {
    this.id=++idCounter;
    
    this.cityName = name;
    
    doReportQuery();
  }
  
  /**
   * @effects return cityName
   */
  public String getCityName() {
    return cityName;
  }

  /**
   * @effects <pre>
   *  set this.name = cityName
   *  if cityName is changed
   *    invoke {@link #doReportQuery()} to update the output attribute value
   *    throws NotPossibleException if failed to generate data source query; 
   *    DataSourceException if fails to read from the data source.
   *  </pre>
   */
  public void setCityName(String name) throws NotPossibleException, DataSourceException {
//    boolean doReportQuery = (cityName != null && !cityName.equals(this.name));
    
    this.cityName = name;
    
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
  @AttrRef("students")
  public void doReportQuery() throws NotPossibleException, DataSourceException {
    // the query manager instance
    
    QRM qrm = QRM.getInstance();
    
    // create a query to look up Student from the data source
    // and then populate the output attribute (students) with the result
    DSMBasic dsm = qrm.getDsm();
    
    ////TODO: create a 2-way join query
    Query q = QueryToolKit.createSimpleJoinQuery(dsm, Student.class, City.class,  
        Student.A_address, 
        City.A_name, 
        Op.MATCH, 
        "%"+cityName+"%");
    
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
    StudentsByCityJoinReport other = (StudentsByCityJoinReport) obj;
    if (id != other.id)
      return false;
    return true;
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName()+ " (" + id + ", " + cityName + ")";
  }
}
