package jda.modules.mosarfrontend.common.factory;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ParamsFactory {
    private static ParamsFactory instance;
    private HashMap<Annotation,Method> methods = new HashMap<>();
    private ParamsFactory() {
        //init methods map
        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            Annotation[] methodAnnotations = declaredMethod.getAnnotations();
            if(methodAnnotations.length> 0){
                methods.put(methodAnnotations[0],declaredMethod);
            }
        }
    }
    public static synchronized ParamsFactory getInstance() {
        if (instance == null) {
            instance = new ParamsFactory();
        }
        return instance;
    }
    @Setter
    /**
     * current module class in generate process. Ex: Student, Course,.. in courseman example
     */
    private Class<?> currentModuleCls;
    @Setter
    private Map<Class, MCC> modelModuleMap;
    private final Method[] getParamMethods = this.getClass().getDeclaredMethods();

    public Object[] getParamsForMethod(Method method) {
        Parameter[] parameters = method.getParameters();
        ArrayList<Object> args = new ArrayList<>();
        for (Parameter p : parameters) {
            Annotation[] annotations = p.getAnnotations();
            if (annotations.length > 0) {
                Method getParamMethod = methods.get(annotations[0]);
                if(getParamMethod != null) {
                    try {
                        args.add(getParamMethod.invoke(this));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            } else args.add(null);
        }
        return args.toArray();
    }

    @RequiredParam.MCC
    private MCC getMCC() {
        return this.modelModuleMap.get(this.currentModuleCls);
    }

    @RequiredParam.ModuleMap
    private Map<Class, MCC> getModuleMap() {
        return this.modelModuleMap;
    }

    @RequiredParam.ModuleFields
    private void getModuleFields(){

    }
}
