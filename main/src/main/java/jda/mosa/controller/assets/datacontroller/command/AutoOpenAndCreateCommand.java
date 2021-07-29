package jda.mosa.controller.assets.datacontroller.command;

import java.util.Collection;
import java.util.Map;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.dom.DOMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.model.Oid;

/**
 * @overview
 *  A {@link DataControllerCommand} that works with a <b>child</b> data controller. 
 *  If there existing domain objects then it opens them as normal, otherwise 
 *  it automatically creates the domain objects, as specified by users via {@link #createNewObjectValArrays(DOMBasic, Object)},  
 *  and display them on the object form for the user to manipulate.
 *  
 *  <p>To use this class, sub-types must implement the abstract method(s).
 *  
 * @author dmle
 *
 */
public abstract class AutoOpenAndCreateCommand<C> extends DataControllerCommand {

  public AutoOpenAndCreateCommand(DataController<C> dctl) {
    super(dctl);
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DataController<C> dctl = getDataController();
    DataController parentDctl = dctl.getParent();
    
    if (parentDctl == null)
      throw new NotPossibleException(NotPossibleException.Code.DATA_CONTROLLER_NOT_A_CHILD, new Object[] {dctl});

    //Class<C> myCls = dctl.getDomainClass();
    //Class parentCls = parentDctl.getDomainClass();
    Object parentObj = parentDctl.getCurrentObject();
    
    if (parentObj == null) {
      throw new NotPossibleException(NotPossibleException.Code.NO_PARENT_OBJECT, new Object[] {
          parentDctl.getCreator().getDomainClassLabel()});
    }
    
    if (parentObj != null) {
      DOMBasic dom = getDodm().getDom();
      //DSMBasic dsm = getDodm().getDsm();
      
      // make sure that object metadata is opened. 
      // NOTE: this must be done before openning objects (below)
      if (!dctl.isOpenMetadata()) {
        dctl.openMetadata();
      }
      
      // reset the object index
      dctl.resetIndexCounter();
      
      // retrieve existing objects (if any) that are linked to parentObj 
      Map<Oid,C> retrievedDat = 
          // v3.1: use dctl to retrieve: 
          // dom.retrieveObjects(myCls, assocName, parentObj, parentCls);
          dctl.retrieveObjects();
      
      if (retrievedDat != null) {
        // has existing objects: open all retrieved objects
        //v3.1: dctl.open(retrievedDat.keySet());
        dctl.openObjects(retrievedDat.values(), true);
      } else {
        // not found: to create new default objects and display them
        // turn-off message pop-up
        ControllerBasic ctl = dctl.getCreator();
        Object messageState = ctl.setProperty("show.message.popup", false);
        try {
          Collection<Object[]> newObjectVals = createNewObjectValArrays(dom, parentObj);
          
          if (newObjectVals != null) {
            for (Object[] entryVals : newObjectVals) {
              dctl.newObject();
              dctl.createObject(entryVals);
              //dctl.setUserSpecifiedState(entryVals);
            }
          }
        } catch (Exception e) {
          throw e;
        } finally {
          // reset pop-up
          ctl.setProperty("show.message.popup", messageState);
        }
      }
    }
  }

  /**
   * @requires 
   *  parentObj != null
   * @effects 
   *  create and return <tt>Collection(Object[])</tt> each element of which is an 
   *  array of values that are used as arguments for the constructor to create new objects
   *  of <tt>this.dctl</tt>; or return <tt>null</tt> if no such array can be created.
   *  
   *  <p>throws Exception if failed to do so
   */
  protected abstract Collection<Object[]> createNewObjectValArrays(
      final DOMBasic dom, final Object parentObj) throws Exception;
}
