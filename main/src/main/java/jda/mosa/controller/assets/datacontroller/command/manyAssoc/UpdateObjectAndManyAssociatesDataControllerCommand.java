package jda.mosa.controller.assets.datacontroller.command.manyAssoc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.controller.ControllerBasic.DataController;

/**
 * @overview
 *  Update association links between two classes that are associated via many-many association(s)
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class UpdateObjectAndManyAssociatesDataControllerCommand<C> extends ManageObjectAndManyAssociatesCommand {

  /**
   * @effects 
   * 
   */
  public UpdateObjectAndManyAssociatesDataControllerCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    /*
     update object normally 
      if there exist many-many associations then 
        for each such association whose many-to-many attribute's value was changed
          use the normaliser attribute to update the association links to the associate class
     */
    
    DataController<C> dctl = getDataController();
    DSMBasic dsm = getDodm().getDsm();
    Class<C> c = dctl.getDomainClass();
    C myObj = dctl.getCurrentObject();
    
    DAssoc manyAssoc;
    Class manyOppositeCls, normAttribType; // the associate class on the opposite many-side
    DAttr manyAttrib; // the attribute of c that realises the many-many asoc
    String normAttribName;
    Collection manyAttribVal;

    // BEFORE UPDATE: Get existing values of the many attributes
    // must use Object[] here because Collection is changed by update (below)
    Map<DAttr,Object[]> existingValMap = new HashMap<>();
    
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
            existingValMap.put(manyAttrib, 
                // must copy to Object[] because Collection is changed by update (Below)
                manyAttribVal.toArray(new Object[manyAttribVal.size()])
                );
          }
        }
      }
    }
    
    // UPDATE
    Map<DAttr,Object> affectedValMap = dctl.updateObject();
    
    // AFTER UPDATE
    if (affectedValMap != null && manyAssocs != null) { 
      Collection newManyAttribVal = null, newVals, interSect;
      DAttr attrib, normAttrib;
      Object[] existingManyAttribVal;
      Tuple2<String,String> normAttribTuple;
      
      // a flag to check if a many-many attribute was actually updated (the user might have changed 
      // the value selection a few times but then decides to select the old values)
      boolean manyAssocStateChanged = false;
      
      // map of the many-many attribute values in affectedValMap that were actually updated
      Map<DAttr,Object[]> oldManyValMap = new HashMap<>();
      
      for (Entry<DAttr,Object> valEntry : affectedValMap.entrySet()) {
        attrib = valEntry.getKey();
        if (manyAssocs.containsKey(attrib)) {
          // attrib is a many attrib and attrib value was changed

          manyAssoc = manyAssocs.get(attrib);
          normAttribName = manyAssoc.normAttrib();
          normAttribType = getDirectAssociationClass(dsm, c, normAttribName);
          manyAssocStateChanged = false;
          
          if (existingValMap.containsKey(attrib)) {
            // attrib value had existing values (before update)
            
            // get the updated value of the attribute
            newManyAttribVal = (Collection) dsm.getAttributeValue(c, myObj, attrib);           
            
            // compare with existing value and update the normaliser links
            existingManyAttribVal = existingValMap.get(attrib);

            normAttrib = dsm.getDomainConstraint(c, normAttribName);
            
            normAttribTuple = dsm.getLinkAttribsOfNormalisedAssocClass(c, manyAssoc, normAttribType); 
                
            // remove links to vals that are not in newManyAttribVal
            interSect = new Stack();
            for (Object val : existingManyAttribVal) {
              if (newManyAttribVal == null || !newManyAttribVal.contains(val)) {
                // val is not among new values: to remove
                removeLinkToNormalisedAssociate(dsm, c, myObj, normAttrib, normAttribType,
                    normAttribTuple.getFirst(), normAttribTuple.getSecond(), val);
                
                if (!manyAssocStateChanged) manyAssocStateChanged = true;
              } else {
                interSect.add(val);
              }
            }
            
            // create links to new values in newManyAttribVal
            newVals = null;
            if (newManyAttribVal != null) {
              for (Object val : newManyAttribVal) {
                if (!interSect.contains(val)) {
                  // a new val: create link
                  if (newVals == null) newVals = new Stack();
                  newVals.add(val);
                }
              }
            }
            
            if (newVals != null) {
              createNewLinksToNormalisedAssociate(myObj, newVals, normAttribType);
              
              if (!manyAssocStateChanged) manyAssocStateChanged = true;
            }
            
            if (manyAssocStateChanged) oldManyValMap.put(attrib, existingManyAttribVal);
          } else {
            // attrib did not have any existing values (before update)

            // get the updated value of the attribute
            newManyAttribVal = (Collection) dsm.getAttributeValue(c, myObj, attrib);
            
            // all vals are new
            createNewLinksToNormalisedAssociate(myObj, newManyAttribVal, normAttribType);
            
            if (!manyAssocStateChanged) manyAssocStateChanged = true;
            oldManyValMap.put(attrib, null);
          }
        } // end if 
      } // end for affectedValMap

      if (manyAssocStateChanged) {
        // POST-UPDATE: use this to further customise post-update tasks (if needed)
        postUpdateManyAssocState(myObj, oldManyValMap);
      }
    }
  }

  /**
   * Sub-types can use this method to add further post-update tasks which need to be performed concerning 
   * the many-many associations that were updated
   * 
   * @requires 
   *  <tt>updatedObj</tt> has successfully been updated (using {@link #execute(DataController, Object...)} above) and the update involves
   *  at least one many-many association  
   *  <br> /\ <tt>oldManyValMap</tt> contains values of the many-many attributes that were updated, before the update
   *  (entries of attributes whose values were null before the update will contain null)
   *  
   * @effects 
   *  use <tt>oldManyValMap</tt> to analyse any further affect(s) that the update may have on <tt>updatedObj</tt>
   *  concerning the many-many associations. 
   *  If so, perform post-update tasks on <tt>updatedObj</tt> accordingly. 
   * 
   * <br>Throws NotPossibleException if failed for some reason
   * 
   * @version 3.3
   */
  protected void postUpdateManyAssocState(C updatedObj, Map<DAttr, Object[]> oldManyValMap) throws NotPossibleException {
    // do nothing
  }
}
