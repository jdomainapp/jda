package jda.modules.mosarfrontend.common.factory;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.utils.DField;
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
    private static ParamsFactory instance;
    private final HashMap<Annotation, Method> methods = new HashMap<>();
    private MCC currentMCC;
    private NewMCC currentNewMCC;
    private Map<String, MCC> modules;
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

    //TODO for @linh.tq : check domainClass meaning and update in readMCC method
    public void setCurrentModule(String module) {
//        this.currentMCC = modules.get(module.getSimpleName());
        this.currentNewMCC = domains.get(module);
    }

    private RFSGenConfig rfsGenConfig;

    public String[] setRFSGenConfig(RFSGenConfig rfsGenConfig) {
        this.rfsGenConfig = rfsGenConfig;
        Class<?>[] models = rfsGenConfig.getDomainModel();
        Class<?>[] mccClasses = rfsGenConfig.getMCCFuncs();
        this.domains = Arrays.stream(mccClasses).map(NewMCC::readMCC).collect((Collectors.toMap(k -> k.getModuleDescriptor().name(), k -> k)));
//        this.modules = IntStream.range(0, mccClasses.length).mapToObj(i -> MCCUtils.readMCC(models[i], mccClasses[i])).collect(Collectors.toMap(MCC::getName, mcc -> mcc));
        System.out.println("");
        return this.domains.keySet().toArray(new String[0]);
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
                    break;
                }

            }
            ;
        }
        return args.toArray();
    }

    @RequiredParam.MCC
    private NewMCC getMCC() {
        return this.currentNewMCC;
    }

    private String[] modulesName; // Save for later trigger getModulesName()

    @RequiredParam.ModulesName
    private String[] getModulesName() {
        if (modulesName == null) {
            modulesName = domains.values().stream().map(m -> m.getModuleDescriptor().modelDesc().model().getSimpleName()).toArray(String[]::new);
        }
        return modulesName;
    }

    @RequiredParam.ModuleName
    private String getModuleName() {
        return this.currentNewMCC.getModuleDescriptor().modelDesc().model().getSimpleName();
    }

    @RequiredParam.ModuleFields

    private DField[] getModuleFields() {
        return this.currentNewMCC.getDFields();
    }


}
