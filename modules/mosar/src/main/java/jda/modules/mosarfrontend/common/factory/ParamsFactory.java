package jda.modules.mosarfrontend.common.factory;

import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ParamsFactory {
    public String[] modulesName; // Save for later trigger getModulesName()
    private static ParamsFactory instance;
    private final HashMap<Annotation, Method> methods = new HashMap<>();
    private NewMCC currentNewMCC;
    private Domain currentSubDomain;
    private DField currentField;
    private Map<String, NewMCC> domains;



    private ParamsFactory() {
        //init methods map
        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            Annotation[] methodAnnotations = declaredMethod.getAnnotations();
            if (methodAnnotations.length > 0) {
                methods.put(methodAnnotations[0], declaredMethod);
            }
        }
    }

    public static ParamsFactory getInstance() {
        if (instance == null) {
            instance = new ParamsFactory();
        }
        return instance;
    }

    @RequiredParam.ModuleMap
    public Map<String, NewMCC> getDomains() {
        return domains;
    }

    public void setCurrentModule(String module) {
        this.currentNewMCC = domains.get(module);
    }

    public void setCurrentSubDomain(String subDomainName) {
        this.currentSubDomain = this.currentNewMCC.getSubDomains().get(subDomainName);
    }


    public void setCurrentModuleField(DField field) {
//        this.currentMCC = modules.get(module.getSimpleName());
        this.currentField = field;
    }

    public String[] setRFSGenConfig(RFSGenConfig rfsGenConfig) {
        Class<?>[] mccClasses = rfsGenConfig.getMCCFuncs();
        this.domains = Arrays.stream(mccClasses).map(NewMCC::readMCC).collect((Collectors.toMap(k -> k.getModuleDescriptor().modelDesc().model().getSimpleName(), k -> k)));
        // link domain to field in each dField
        for (String domain : this.domains.keySet()) {
            for (DField dField : this.domains.get(domain).getDFields()) {
                if (dField.getDAssoc() != null)
                    dField.setLinkedDomain(this.domains.get(dField.getDAssoc().associate().type().getSimpleName()));
            }
        }
        return this.domains.keySet().toArray(String[]::new);
    }

    public Object[] getParamsForMethod(Method method) {
        Parameter[] parameters = method.getParameters();
        ArrayList<Object> args = new ArrayList<>();
        for (Parameter p : parameters) {
            Annotation[] annotations = p.getAnnotations();
            if (annotations.length > 0) {
                Method getParamMethod = methods.get(annotations[0]);

                if (getParamMethod != null) {
                    try {
                        args.add(getParamMethod.invoke(this));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else continue;

            }
            ;
        }
        return args.toArray();
    }
    @RequiredParam.MCC
    public NewMCC getMCC() {
        return this.currentNewMCC;
    }

    @RequiredParam.CurrentSubDomain
    private Domain getCurrentSubDomain(){
        return this.currentSubDomain;
    }

    @RequiredParam.ModulesName
    public String[] getModulesName() {
        if (modulesName == null) {
            modulesName = domains.values().stream().map(m -> m.getModuleDescriptor().modelDesc().model().getSimpleName()).toArray(String[]::new);
        }
        return modulesName;
    }

    @RequiredParam.ModuleName
    public String getModuleName() {
        return this.currentNewMCC.getModuleDescriptor().modelDesc().model().getSimpleName();
    }

    @RequiredParam.ModuleFields
    public DField[] getModuleFields() {
        return this.currentNewMCC.getDFields();
    }

    @RequiredParam.ModuleField
    public DField getCurrentField() {
        return this.currentField;
    }

    @RequiredParam.AngularProp
    private AngularSlotProperty getAngularSlotProperty() {
        return new AngularSlotProperty(this.currentNewMCC);
    }
    
    @RequiredParam.DomainFields
    public DField[] getDomainFields() {
    	DField[] moduleFields = this.currentNewMCC.getDFields();
    	ArrayList<DField> result = new ArrayList<>();
        for (DField field : moduleFields) {
        	if (field.getDAssoc() != null) {
        		result.add(field);
        	}
        }
        DField[] domainFields = result.stream().toArray(DField[]::new);
    	return domainFields;
    }    
}
