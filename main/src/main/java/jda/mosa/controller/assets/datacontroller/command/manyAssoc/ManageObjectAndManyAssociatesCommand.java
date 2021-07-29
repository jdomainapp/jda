package jda.mosa.controller.assets.datacontroller.command.manyAssoc;

import java.util.Collection;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.filter.Filter;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;

/**
 * @overview
 *
 * @author dmle
 *
 * @version 3.3
 */
public abstract class ManageObjectAndManyAssociatesCommand<C> extends DataControllerCommand {

  /**
   * @overview
   *  A specialised filter that is used to check if a normalised attribute value satifies the arguments 
   *  
   * @author dmle
   */
  class NormalisedAttributeValFilter<T> implements Filter<T> {

    private Object manyAssocObj1;
    private Object manyAssocObj2;
    private DSMBasic dsm;
    private String normAttrib1;
    private String normAttrib2;
    private Class<T> normAssocCls;

    /**
     * @requires 
     *  <tt>normAssocCls</tt> is registered in <tt>dsm</tt>, 
     *  <tt>normAttrib1, normAttrib2</tt> are valid domain attributes of <tt>normAssocCls</tt>, 
     *  <tt>manyAssocObj1, manyAssocObj2</tt> are both not null 
     *  
     * @effects 
     */
    public NormalisedAttributeValFilter(DSMBasic dsm, Class<T> normAssocCls, C manyAssocObj1, String normAttrib1, Object manyAssocObj2, String normAttrib2) {
      this.dsm = dsm;
      this.normAssocCls = normAssocCls;
      this.manyAssocObj1 = manyAssocObj1;
      this.manyAssocObj2 = manyAssocObj2;
      this.normAttrib1 = normAttrib1;
      this.normAttrib2 = normAttrib2;
    }

    
    /**
     * @requires 
     *  <tt>normAssocCls</tt> is registered in <tt>dsm</tt>,
     *  <tt>normAttrib1, normAttrib2</tt> are valid domain attributes of <tt>normAssocCls</tt>, 
     *  <tt>manyAssocObj1, manyAssocObj2</tt> are both not null
     *  
     * @effects 
     */
    public void setManyAssocObjects(DSMBasic dsm, Class<T> normAssocCls, C manyAssocObj1, String normAttrib1, Object manyAssocObj2, String normAttrib2) {
      this.dsm = dsm;
      this.normAssocCls = normAssocCls;
      this.manyAssocObj1 = manyAssocObj1;
      this.manyAssocObj2 = manyAssocObj2;
      this.normAttrib1 = normAttrib1;
      this.normAttrib2 = normAttrib2;
    }
    
    /**
     * @effects
     *  if o is a normalised object for <tt>({@link #manyAssocObj1},{@link #manyAssocObj2)</tt>
     *    return <tt>true</tt>
     *  else
     *    return <tt>false</tt>
     *    
     *  <p>Throws NotPossibleException if failed to get attribute value of {@link #normAttrib1} or {@link #normAttrib2} in <tt>o</tt>
     */
    /* (non-Javadoc)
     * @see domainapp.basics.util.Filter#check(java.lang.Object, java.lang.Object[])
     */
    @Override
    public boolean check(T o, Object... args) throws NotPossibleException {
      if (o != null) {
        Object val1, val2;
        val1 = dsm.getAttributeValue(o, normAttrib1);
        if (val1 != null) {
          val2 = dsm.getAttributeValue(o, normAttrib2);
          if (val2 != null) {
            return (val1.equals(manyAssocObj1) && val2.equals(manyAssocObj2));
          }
        }
      }
      
      return false;
    }

  } /**end {@link NormalisedAttributeValFilter} */


  private NormalisedAttributeValFilter valFilter;


  /**
   * @effects 
   * 
   */
  public ManageObjectAndManyAssociatesCommand(DataController dctl) {
    super(dctl);
  }

  /**
   * @effects 
   *  if values == null
   *    create and return a new <tt>Object[]</tt> containing the user-specified values, 
   *    that must include <tt>myVal, associateVal</tt>
   *    return the array
   *  else 
   *    update <tt>values</tt> to include <tt>myVal, associateVal</tt>
   *    return <tt>values</tt>
   *    
   */
  protected Object[] getDirectAssociateObjectValues(Object[] values, Object myVal, Object associateVal) {
    if (values == null) {
      return new Object[] {myVal, associateVal};
    } else {
      values[0] = myVal; values[1] = associateVal;
    }
    
    return values;
  }
  
  /**
   * @effects 
   *  for each val in newVals
   *    create a new link from <tt>myObj</tt> to an object of type <tt>normalAttribType</tt>
   */
  protected void createNewLinksToNormalisedAssociate(C myObj, Collection newVals, Class normalAttribType) 
      throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    DataController assocDctl = lookUpRootDataController(normalAttribType);
    
    Object[] values = null;
    boolean initForm = false;
    boolean showPopUpMesg = false;
    /* this results in ConcurrentModificationException because createNewObject potentially also touches
     * the newVals collection of the target object
      for (Object val : newVals) {
     * solution: convert to array
     */
    Object[] newValArr = new Object[newVals.size()];
    newVals.toArray(newValArr);
    for (Object val: newValArr) {
      values = getDirectAssociateObjectValues(values, myObj, val);
      createNewObject(assocDctl, values, initForm, showPopUpMesg);
    }
  }
  
  /**
   * @effects 
   *  Remove link from myObj to objects of <tt>normAssocCls</tt> that normalise the many association links 
   *  between <tt>myObj,  manyAssocObj</tt>
   *  
   * @version 3.3
   */
  protected void removeLinkToNormalisedAssociate(DSMBasic dsm, Class<C> myCls, C myObj, DAttr normAttrib, Class normAttribType,
      String normAttrib1, String normAttrib2, 
      Object manyAssocObj) throws NotPossibleException, NotFoundException, DataSourceException {
    
    if (valFilter == null)
      valFilter = new NormalisedAttributeValFilter(dsm, normAttribType, myObj, normAttrib1, manyAssocObj, normAttrib2);
    else 
      valFilter.setManyAssocObjects(dsm, normAttribType, myObj, normAttrib1, manyAssocObj, normAttrib2);
      
    // look up in myObj for the 'normalised' objects
    Collection normAttribVal = (Collection) dsm.getAttributeValue(myCls, myObj, normAttrib, valFilter);
    
    if (normAttribVal != null) { // should not need this check, but to be sure...
      // remove links to objects in normAttribVal
      
      DataController domainDCtl = lookUpRootDataController(normAttribType);
      ControllerBasic domainCtl = domainDCtl.getCreator();
      
      Object oldProp = domainCtl.getProperty("show.message.popup");
      // disable pop-up
      
      domainCtl.setProperty("show.message.popup", false);
      try {
        for (Object normVal : normAttribVal) {
          //domainDCtl.removeLinkToAssociate(normVal, myObj, normAttrib);
          domainDCtl.deleteObject(normVal, false);
        }
      } catch (Exception e) {
        throw e;
      } finally {
        domainCtl.setProperty("show.message.popup", oldProp);  
      }
    }
  }
  

  /**
   * @requires 
   *  <tt>c</tt> contains a many-many association that is normalised via <tt>normAttribName</tt>
   *  
   * @effects 
   *  look up an <tt>c</tt> the normalised association <tt>a</tt> of <tt>normAttribName</tt> 
   *  and return <tt>a.associate.type</tt>
   *  
   */
  protected Class getDirectAssociationClass(DSMBasic dsm, Class<C> c,
      String normAttribName) {
    DAssoc normAssoc = dsm.getAssociationObj(c, normAttribName);
    return normAssoc.associate().type();
  }
}
