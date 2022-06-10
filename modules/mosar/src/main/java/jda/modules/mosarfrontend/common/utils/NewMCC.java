package jda.modules.mosarfrontend.common.utils;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

@Data
public class NewMCC {
    private ModuleDescriptor moduleDescriptor;
    private DClass dClass;
    private DField[] dFields;
    private DField idField;

    public static NewMCC readMCC(Class<?> cls) {
        System.out.println(cls);
        NewMCC newMCC = new NewMCC();
        newMCC.setModuleDescriptor(cls.getAnnotation(ModuleDescriptor.class));
        Class<?> domainCls = newMCC.getModuleDescriptor().modelDesc().model();
        if (domainCls.isAnnotationPresent(DClass.class)) {
            newMCC.dClass = domainCls.getAnnotation(DClass.class);
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
                    //TODO below code add field Label from ModuleDescriptor, but need config label in @DClass -> request to Mr. duc.ml
                    Field[] viewField = Arrays.stream(cls.getDeclaredFields()).filter(f -> f.isAnnotationPresent(AttributeDesc.class) && f.getName() == field.getName()).toArray(Field[]::new);
                    if(viewField.length > 0 ){
                        dField.setAttributeDesc(viewField[0].getAnnotation(AttributeDesc.class));
                    }
                    fields.add(dField);
                    if (dField.getDAttr().id()) newMCC.setIdField(dField);
                }
            });
            newMCC.setDFields(fields.toArray(DField[]::new));


        }
        return newMCC;
    }

    public ModuleDescriptor getModuleDescriptor() {
        return moduleDescriptor;
    }

    public void setModuleDescriptor(ModuleDescriptor moduleDescriptor) {
        this.moduleDescriptor = moduleDescriptor;
    }

    public DClass getdClass() {
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
}
