package jda.modules.mosarfrontend.common.utils;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.syntax.view.AttributeDesc;
import lombok.Data;

@Data
public class DField {
    private DAttr dAttr;
    private DAssoc dAssoc;
    private AttributeDesc attributeDesc;
    private String enumName; // null if not enum
    private NewMCC linkedDomain;
    private DField linkedField;

    public DAttr getDAttr() {
        return dAttr;
    }

    public void setDAttr(DAttr dAttr) {
        this.dAttr = dAttr;
    }

    public DAssoc getDAssoc() {
        return dAssoc;
    }

    public void setDAssoc(DAssoc dAssoc) {
        this.dAssoc = dAssoc;
    }

    public AttributeDesc getAttributeDesc() {
        return attributeDesc;
    }

    public void setAttributeDesc(AttributeDesc attributeDesc) {
        this.attributeDesc = attributeDesc;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }

    public Enum<?>[] getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(Enum<?>[] enumValues) {
        this.enumValues = enumValues;
    }

    private Enum<?>[] enumValues;

    public NewMCC getLinkedDomain() {
        return linkedDomain;
    }

    public void setLinkedDomain(NewMCC linkedDomain) {
        this.linkedDomain = linkedDomain;
    }

    public DField getLinkedField() {
        return linkedField;
    }

    public void setLinkedField(DField linkedField) {
        this.linkedField = linkedField;
    }
}
