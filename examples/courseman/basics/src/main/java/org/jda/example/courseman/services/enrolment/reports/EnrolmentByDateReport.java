package org.jda.example.courseman.services.enrolment.reports;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.jda.example.courseman.services.enrolment.model.Enrolment;

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
import jda.modules.dcsl.syntax.DAttr.Format;
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
public class EnrolmentByDateReport {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  /**input: student name */
  @DAttr(name="registDate", type=Type.Date, 
      optional=true, format=Format.Date)
  private Date registDate;
  
  /**output: enrolments whose names match {@link #name} */
  @DAttr(name="students",type=Type.Collection,optional=false, mutable=false,
      serialisable=false, filter=@Select(clazz=Enrolment.class)
      ,derivedFrom={"registDate"}
      )
  @DAssoc(ascName="enrols-by-date-report-has-enrols",role="report",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Enrolment.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE
    ))
  @Output
  private Collection<Enrolment> enrols;

  /**output: number of students found (if any), derived from {@link #enrols} */
  @DAttr(name = "numEnrolments", type = Type.Integer, length = 20, auto=true, mutable=false)
  @Output
  private int numEnrolments;
  
  /**
   * @effects 
   *  initialise this with <tt>name</tt> and use {@link QRM} to retrieve from data source 
   *  all {@link Enrolment} whose names match <tt>name</tt>.
   *  initialise {@link #enrols} with the result if any.
   *  
   *  <p>throws NotPossibleException if failed to generate data source query; 
   *  DataSourceException if fails to read from the data source
   * 
   */
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public EnrolmentByDateReport(@AttrRef("registDate") Date registDate) throws NotPossibleException, DataSourceException {
    this.id=++idCounter;
    
    this.registDate = registDate;
    
    doReportQuery();
  }
  
  /**
   * @effects return registDate
   */
  public Date getRegistDate() {
    return registDate;
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
  public void setRegistDate(Date registDate) throws NotPossibleException, DataSourceException {
//    boolean doReportQuery = (name != null && !name.equals(this.name));
    
    this.registDate = registDate;
    
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
    
    // create a query to look up Enrolment from the data source
    // and then populate the output attribute (students) with the result
    DSMBasic dsm = qrm.getDsm();
    
    //TODO: to conserve memory cache the query and only change the query parameter value(s)
    Query q = QueryToolKit.createSearchQuery(dsm, Enrolment.class, 
        new String[] {"registDate"}, 
        new Op[] {Op.EQ}, 
        new Object[] {registDate});
    
    Map<Oid, Enrolment> result = qrm.getDom().retrieveObjects(Enrolment.class, q);
    
    if (result != null) {
      // update the main output data 
      enrols = result.values();
      
      // update other output (if any)
      numEnrolments = enrols.size();
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
    enrols = null;
    numEnrolments = 0;
  }

  /**
   * A link-adder method for {@link #enrols}, required for the object form to function.
   * However, this method is empty because students have already be recorded in the attribute {@link #enrols}.
   */
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addEnrolment(Collection<Enrolment> students) {
    // do nothing
    return false;
  }
  
  /**
   * @effects return students
   */
  public Collection<Enrolment> getEnrolments() {
    return enrols;
  }
  
  /**
   * @effects return numEnrolments
   */
  public int getNumEnrolments() {
    return numEnrolments;
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
    EnrolmentByDateReport other = (EnrolmentByDateReport) obj;
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
    return "EnrolmentsByDateReport (" + id + ", " + registDate + ")";
  }

}
