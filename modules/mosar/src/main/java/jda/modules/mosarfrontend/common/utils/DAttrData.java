package jda.modules.mosarfrontend.common.utils;

import jda.modules.common.CommonConstants;
import jda.modules.dcsl.parser.statespace.metadef.MetaAttrDef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;


/**
 * present data for {@link DAttr}
 */
@Data
public class DAttrData {
    private String name;
    private boolean id = false;
    private boolean cid = false;
    private String ccid = CommonConstants.NullString;
    private DAttr.Type type = DAttr.Type.Null;
    private boolean auto = false;
    private boolean autoIncrement = false;
    private boolean unique = false;
    private boolean mutable = true;
    private boolean optional = true;
    private int length = DCSLConstants.DEFAULT_DATTR_LENGTH;
    private double min = CommonConstants.DEFAULT_MIN_VALUE;
    private double max = CommonConstants.DEFAULT_MAX_VALUE;
    private String[] derivedForm = new String[0];
    private String sourceAttribute = CommonConstants.NullString;
    private boolean sourceQuery = false;
    private boolean sourceQueryHandler = false;
    private String defaultValue = CommonConstants.NullString;

    public DAttrData(MetaAttrDef metaAttrDef) {
        Collection<Map.Entry<String, Object>> properties = metaAttrDef.getProperties();
        for (Map.Entry<String, Object> property : properties) {
            for (Field field : this.getClass().getDeclaredFields()) {
                if(field.getName().equals(property.getKey())){
                    try {
                        field.set(this, property.getValue().toString());
                    } catch (Exception e) {
                        System.out.println("Warning: failed to cast field from DAttr: " + property.getKey());
                        System.out.println("In progress of: " + properties);
                        e.printStackTrace();
                    }
                    break;
                }
            }

        }
    }
}
