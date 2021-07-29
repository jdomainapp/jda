package jda.modules.dodm.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * v2.7.3 Represents a mapping of transformation class into table (i.e. a class
 * field and a table column)
 * 
 * @author congnv
 */
@DClass(schema = DCSLConstants.CONFIG_SCHEMA)
public class Mapping {
  @DAttr(name = "mappingId", id = true, auto = true, type = Type.String, mutable = false, optional = false)
  private String mappingId; // formed by className_fieldIndex

  @DAttr(name = "className", type = Type.String, mutable = false, optional = false)
  private String className;
  @DAttr(name = "fieldName", type = Type.String, optional = false)
  private String fieldName;
  @DAttr(name = "fieldIndex", type = Type.Integer, mutable = false, optional = false)
  private int fieldIndex;

  // domain constraint
  @DAttr(name = "serialisable", type = Type.Boolean, optional = false, length = 5)
  public boolean serialisable;
  @DAttr(name = "id", type = Type.Boolean, optional = false, length = 5)
  private boolean id;
  @DAttr(name = "type", type = Type.Domain, optional = false)
  private Type type;
  @DAttr(name = "autoIncrement", type = Type.Boolean, optional = false, length = 5)
  private boolean autoIncrement;
  @DAttr(name = "isUnique", type = Type.Boolean, optional = false, length = 5)
  private boolean isUnique;
  @DAttr(name = "isOptional", type = Type.Boolean, optional = false, length = 5)
  private boolean isOptional;
  @DAttr(name = "maxLength", type = Type.Integer, optional = false)
  private int maxLength;
  @DAttr(name = "defaultValue", type = Type.String, optional = true)
  public String defaultValue;

  // constructor for creating objects from data source
  public Mapping(String mappingId, String className, String fieldName,
      Integer fieldIndex, Boolean serialisable, Boolean id, Type type,
      Boolean autoIncrement, Boolean isUnique, Boolean isOptional,
      Integer maxLength, String defaultValue) {
    this.mappingId = mappingId;
    this.className = className;
    this.fieldName = fieldName;
    this.fieldIndex = fieldIndex;
    this.id = id;
    this.type = type;
    this.autoIncrement = autoIncrement;
    this.isUnique = isUnique;
    this.isOptional = isOptional;
    this.maxLength = maxLength;
    this.defaultValue = defaultValue;
    this.serialisable = serialisable;
  }

  // constructor for creating objects from object form
  public Mapping(String className, String fieldName, Integer fieldIndex,
      Boolean serialisable, Boolean id, Type type, Boolean autoIncrement,
      Boolean isUnique, Boolean isOptional, Integer maxLength,
      String defaultValue) {
    this(className + "_" + fieldIndex, className, fieldName, fieldIndex,
        serialisable, id, type, autoIncrement, isUnique, isOptional, maxLength,
        defaultValue);
  }

  public String getMappingId() {
    return mappingId;
  }

  public String getClassName() {
    return className;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public int getFieldIndex() {
    return fieldIndex;
  }

  public boolean getId() {
    return id;
  }

  public void setId(boolean id) {
    this.id = id;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public boolean getAutoIncrement() {
    return autoIncrement;
  }

  public void setAutoIncrement(boolean autoIncrement) {
    this.autoIncrement = autoIncrement;
  }

  public boolean getIsUnique() {
    return isUnique;
  }

  public void setIsUnique(boolean isUnique) {
    this.isUnique = isUnique;
  }

  public boolean getIsOptional() {
    return isOptional;
  }

  public void setIsOptional(boolean isOptional) {
    this.isOptional = isOptional;
  }

  public int getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public boolean getSerialisable() {
    return serialisable;
  }

  public void setSerialisable(boolean serialisable) {
    this.serialisable = serialisable;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (autoIncrement ? 1231 : 1237);
    result = prime * result + ((className == null) ? 0 : className.hashCode());
    result = prime * result
        + ((defaultValue == null) ? 0 : defaultValue.hashCode());
    result = prime * result + fieldIndex;
    result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
    result = prime * result + (id ? 1231 : 1237);
    result = prime * result + maxLength;
    result = prime * result + ((mappingId == null) ? 0 : mappingId.hashCode());
    result = prime * result + (isOptional ? 1231 : 1237);
    result = prime * result + (serialisable ? 1231 : 1237);
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + (isUnique ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Mapping other = (Mapping) obj;
    if (autoIncrement != other.autoIncrement)
      return false;
    if (className == null) {
      if (other.className != null)
        return false;
    } else if (!className.equals(other.className))
      return false;
    if (defaultValue == null) {
      if (other.defaultValue != null)
        return false;
    } else if (!defaultValue.equals(other.defaultValue))
      return false;
    if (fieldIndex != other.fieldIndex)
      return false;
    if (fieldName == null) {
      if (other.fieldName != null)
        return false;
    } else if (!fieldName.equals(other.fieldName))
      return false;
    if (id != other.id)
      return false;
    if (maxLength != other.maxLength)
      return false;
    if (mappingId == null) {
      if (other.mappingId != null)
        return false;
    } else if (!mappingId.equals(other.mappingId))
      return false;
    if (isOptional != other.isOptional)
      return false;
    if (serialisable != other.serialisable)
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (isUnique != other.isUnique)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Mapping (mappingId=" + mappingId + ", className=" + className
        + ", fieldName=" + fieldName + ", fieldIndex=" + fieldIndex + ", id="
        + id + ", type=" + type + ", autoIncrement=" + autoIncrement
        + ", isUnique=" + isUnique + ", isOptional=" + isOptional
        + ", maxLength=" + maxLength + ", defaultValue=" + defaultValue
        + ", serialisable=" + serialisable + ")";
  }
}
