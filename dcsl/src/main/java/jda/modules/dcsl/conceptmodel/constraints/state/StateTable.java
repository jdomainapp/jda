package jda.modules.dcsl.conceptmodel.constraints.state;

import java.util.HashMap;
import java.util.Map;

import jda.modules.dcsl.conceptmodel.constraints.Constraint;

/**
 * @overview 
 *  Represents the shared state table that is updated by each constraint evaluation. 
 *  A {@link StateTable} object is passed to the evaluation method of each {@link Constraint}'s object so that
 *  it can be used for the evaluation and be updated (if necessary) when this evaluation is completed. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class StateTable {
  private Map<Object,Object> stateTable;
  
  public StateTable() {
    stateTable = new HashMap<>();
  }
  
  public void put(Object key, Object value) {
    stateTable.put(key, value);
  }
  
  public Object get(Object key) {
    return stateTable.get(key);
  }
}
