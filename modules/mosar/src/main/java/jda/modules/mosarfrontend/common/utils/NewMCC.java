package jda.modules.mosarfrontend.common.utils;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.mccl.syntax.ModuleDescriptor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;

@Data
public class NewMCC {
    private ModuleDescriptor moduleDescriptor;
    private DClass dClass;
    private DField[] dFields;

    public static NewMCC readMCC(Class<?> cls) {
        System.out.println(cls);
        NewMCC mccUtils = new NewMCC();
        mccUtils.setModuleDescriptor((ModuleDescriptor) cls.getAnnotation(ModuleDescriptor.class));
        Class<?> domainCls =  mccUtils.getModuleDescriptor().modelDesc().model();
        if(domainCls.isAnnotationPresent(DClass.class)){
            mccUtils.dClass = domainCls.getAnnotation(DClass.class);
            ArrayList<DField> fields = new ArrayList<>();
            Arrays.stream(domainCls.getDeclaredFields()).forEach(field -> {
                DField dField = new DField();
                if ( field.isAnnotationPresent(DAttr.class)){
                    dField.setDAttr(field.getAnnotation(DAttr.class));
                    if(field.isAnnotationPresent(DAssoc.class)){
                        dField.setDAssoc(field.getAnnotation(DAssoc.class));
                    }
                    fields.add(dField);
                }
            });
            mccUtils.setDFields(fields.toArray(DField[]::new));
        }
        return mccUtils;
    }
}
