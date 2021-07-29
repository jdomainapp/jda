/**
 * @overview
 *
 * @author dmle
 */
package jda.mosa.controller.assets.datacontroller.command.manyAssoc;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.controller.ControllerBasic.DataController;

/**
 * @overview
 *  Create an object of this data controller and the associated objects of a domain class that  
 *  realises the many-many association(s) between this domain class and other domain class(es). 
 *  
 * @example
 * <pre>
 *  this data controller: DataController(DomainUser)
 *  many-side data controller: DataController(Role)
 *  associate class: DataController(UserRole)
 *  
 *  where:
 *  UserRole realises the many-many association (DomainUser, Role)
 *  
 *  -> create new DomainUser u and new UserRole for each Role specified as part of u 
 * </pre>
 *  
 * @author dmle
 *
 */
public class CreateObjectAndManyAssociatesDataControllerCommand<C> extends ManageObjectAndManyAssociatesCommand {

  public CreateObjectAndManyAssociatesDataControllerCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    /*
     * create object normally
       if there exist many-many associations then 
          for each such association 
            use the normaliser attribute to create new association links to the associate class
     * 
     */
    DataController<C> dctl = getDataController();
    DSMBasic dsm = getDodm().getDsm();
    Class<C> c = dctl.getDomainClass();
    
    DAssoc manyAssoc;
    Class manyOppositeCls, normAttribType; // the associate class on the opposite many-side
    DAttr manyAttrib; // the attribute of c that realises the many-many asoc
    String normAttribName;
    Collection manyAttribVal;

    // create a new object normally
    C myObj = createObject();
    
    // if there are many-many associations then create links to them
    Map<DAttr,DAssoc> manyAssocs = dsm.getManyToManyAssociations(c);
    
    if (manyAssocs != null) {
      // has many-many associations
      ASSOC: for (Entry<DAttr,DAssoc> e : manyAssocs.entrySet()) {
        manyAttrib = e.getKey();
        manyAssoc = e.getValue();
        manyOppositeCls = manyAssoc.associate().type();

        // only consider serialisable classes
        if (!DSMBasic.isTransient(manyOppositeCls)) {
          manyAttribVal = (Collection) dsm.getAttributeValue(myObj, manyAttrib.name());
          if (manyAttribVal != null) {
            // manyAttrib has value(s): use them to create links to normalised association class
            normAttribName = manyAssoc.normAttrib();
            normAttribType = getDirectAssociationClass(dsm, c, normAttribName);
            
            createNewLinksToNormalisedAssociate(myObj, manyAttribVal, normAttribType);
          }
        }
      }
    }
  }
  
  /**
   * Sub-types can customise this method to support the creation of sub-type objects of the type 
   * directly supported by {@link #getDataController()}. 
   * 
   * @effects 
   *  create and return a domain object of the type supported by {@link #getDataController()}
   *  
   *  <p>throws Exception if fails
   */
  protected C createObject() throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    DataController<C> dctl = getDataController();

    return dctl.createObject();
  }
}
