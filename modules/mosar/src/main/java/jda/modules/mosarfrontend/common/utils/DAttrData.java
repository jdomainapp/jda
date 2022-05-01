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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public boolean isCid() {
        return cid;
    }

    public void setCid(boolean cid) {
        this.cid = cid;
    }

    public String getCcid() {
        return ccid;
    }

    public void setCcid(String ccid) {
        this.ccid = ccid;
    }

    public DAttr.Type getType() {
        return type;
    }

    public void setType(DAttr.Type type) {
        this.type = type;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isMutable() {
        return mutable;
    }

    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public String[] getDerivedForm() {
        return derivedForm;
    }

    public void setDerivedForm(String[] derivedForm) {
        this.derivedForm = derivedForm;
    }

    public String getSourceAttribute() {
        return sourceAttribute;
    }

    public void setSourceAttribute(String sourceAttribute) {
        this.sourceAttribute = sourceAttribute;
    }

    public boolean isSourceQuery() {
        return sourceQuery;
    }

    public void setSourceQuery(boolean sourceQuery) {
        this.sourceQuery = sourceQuery;
    }

    public boolean isSourceQueryHandler() {
        return sourceQueryHandler;
    }

    public void setSourceQueryHandler(boolean sourceQueryHandler) {
        this.sourceQueryHandler = sourceQueryHandler;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

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
