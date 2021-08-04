package jda.modules.dodm.dsm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.model.Mapping;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;

public class DSM extends DSMBasic {

  /**
   * This method is useful when we want to use DSM in the memory, without worrying 
   * too much about how objects of the class are stored. 
   * 
   * @effects 
   *  initialises this without a {@link DODMConfig}. 
   * @version 5.4.1
   */
  public DSM() {
    super(null);
  }
  
  public DSM(DODMConfig config) {
    super(config);
  }

  // v2.7.3 congnv
  /**
   * @effects 
   *  return null if no mappings
   */
  public Collection<Mapping> generateMappings(Class c) {
    // get the declared fields of this class
    Map<Field,DAttr> fields = getSerialisableAttributes(c);

    if (fields == null)
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTES_NOT_FOUND,
          new Object[] { c });
    
    String className = getDomainClassName(c);
    
    Collection<Mapping> mappings = new ArrayList<>();
    Mapping mapping;
    DAttr dc;
    /* v5.0: for (int i = 0; i < fields.size(); i++) {
        Field f = (Field) fields.get(i);
        dc = f.getAnnotation(DC);
        */
    int i = -1;
    for(Entry<Field,DAttr> entry : fields.entrySet()) {
      i++;
      Field f = entry.getKey();
      dc = entry.getValue();
      if (!dc.type().isDomainType()) {
        // generate mapping
        mapping = generateMapping(className, dc, i);
        mappings.add(mapping);
      }
    }
    
    return (!mappings.isEmpty()) ? mappings : null;
  }
  
  // v2.7.3 congnv
  /**
   * @requires 
   *  dc.type != Type.Domain
   */
  public Mapping generateMapping(String className, DAttr dc, int fieldIndex) 
      throws NotImplementedException {
    String fieldName = dc.name();
    
    Type dcType = dc.type();
    boolean id = dc.id();
    boolean autoIncrement = dc.autoIncrement();
    boolean unique = dc.unique();
    boolean optional = dc.optional();
    int length = dc.length();
    String defaultValue = dc.defaultValue();
    boolean serialisable = dc.serialisable();
  
    Mapping mapping = new Mapping(className, fieldName, fieldIndex, serialisable, id, 
        //type,
        dcType,
        autoIncrement, unique, optional, length, defaultValue);
    
    return mapping;
  }
}
