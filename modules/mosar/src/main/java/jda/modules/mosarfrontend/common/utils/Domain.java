package jda.modules.mosarfrontend.common.utils;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.mccl.syntax.view.AttributeDesc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Domain {
    public DClass getDClass() {
        return dClass;
    }

    public void setDClass(DClass dClass) {
        this.dClass = dClass;
    }

    public DField[] getDFields() {
        return dFields;
    }

    public void setDFields(DField[] dFields) {
        this.dFields = dFields;
    }

    public DField getIdField() {
        return idField;
    }

    public void setIdField(DField idField) {
        this.idField = idField;
    }

    public Class<?> getDomainClass() {
        return domainClass;
    }

    public Map<String, Domain> getSubDomains() {
        return subDomains;
    }

    public void addSubDomain(Domain domain, String type) {
        this.subDomains.put(type, domain);
    }

    protected DClass dClass;
    protected String type;// use for sub domain
    protected DField[] dFields;
    protected DField idField;
    protected Map<String, Domain> subDomains = new HashMap<>();

    protected Class<?> domainClass;

    protected void readDomain(Class<?> domainCls, Class<?> mccClass) {
        this.domainClass = domainCls;
        if (domainCls.isAnnotationPresent(DClass.class)) {
            this.dClass = (DClass) domainCls.getAnnotation(DClass.class);
            ArrayList<DField> fields = new ArrayList<>();
            Arrays.stream(domainCls.getDeclaredFields()).forEach(field -> {
                DField dField = new DField();
                if (field.isAnnotationPresent(DAttr.class)) {
                    dField.setDAttr(field.getAnnotation(DAttr.class));
                    if (field.isAnnotationPresent(DAssoc.class)) {
                        dField.setDAssoc(field.getAnnotation(DAssoc.class));
                    } else if (dField.getDAttr().type() == DAttr.Type.Domain && field.getType().isEnum()) {
                        Enum[] values = Arrays.stream(field.getType().getEnumConstants()).toArray(Enum[]::new);
                        dField.setEnumName(field.getType().getSimpleName());
                        dField.setEnumValues(values);
                    }
                    //TODO below code add field Label from ModuleDescriptor
                    Field[] viewField = Arrays.stream(mccClass.getDeclaredFields()).filter(f -> f.isAnnotationPresent(AttributeDesc.class) && f.getName() == field.getName()).toArray(Field[]::new);
                    if (viewField.length > 0) {
                        dField.setAttributeDesc(viewField[0].getAnnotation(AttributeDesc.class));
                    }
                    fields.add(dField);
                    if (dField.getDAttr().id()) this.idField = dField;
                }
            });
            this.dFields = fields.toArray(DField[]::new);

            // Read subModule
            if (domainCls.isAnnotationPresent(JsonTypeName.class)) {
                JsonTypeName anno = (JsonTypeName) domainCls.getAnnotation(JsonTypeName.class);
                this.type = anno.value();
            }
        }

    }
}
