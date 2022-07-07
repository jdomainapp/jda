package jda.modules.mosarfrontend.common.utils;

import jda.modules.mccl.syntax.ModuleDescriptor;
import lombok.Data;

@Data
public class NewMCC extends Domain {
    private ModuleDescriptor moduleDescriptor;


    public static NewMCC readMCC(Class<?> cls) {
        NewMCC newMCC = new NewMCC();
        newMCC.setModuleDescriptor(cls.getAnnotation(ModuleDescriptor.class));
        Class<?> domainCls = newMCC.getModuleDescriptor().modelDesc().model();
        newMCC.readDomain(domainCls, cls);
        // read sub module:
        for (Class aClass : newMCC.moduleDescriptor.subtypes()) {
            NewMCC subMCC = NewMCC.readMCC(aClass);
            newMCC.addSubDomain(subMCC, subMCC.type);
        }
        return newMCC;
    }

    public ModuleDescriptor getModuleDescriptor() {
        return moduleDescriptor;
    }

    public void setModuleDescriptor(ModuleDescriptor moduleDescriptor) {
        this.moduleDescriptor = moduleDescriptor;
    }

}
