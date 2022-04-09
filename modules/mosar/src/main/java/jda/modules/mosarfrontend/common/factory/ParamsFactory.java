package jda.modules.mosarfrontend.common.factory;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ParamsFactory {
    private static ParamsFactory instance;

    private ParamsFactory() {
    }

    public static synchronized ParamsFactory getInstance() {
        if (instance == null) {
            instance = new ParamsFactory();
        }
        return instance;
    }

    @Setter
    private Class<?> moduleClass;
    @Setter
    private Map<Class, MCC> modelModuleMap;
    private final Method[] getParamMethods = this.getClass().getDeclaredMethods();

    public Object[] getParamsForMethod(Method method) {
        Parameter[] parameters = method.getParameters();
        ArrayList<Object> args = new ArrayList<>();
        for (Parameter p : parameters) {
            Annotation[] annotations = p.getAnnotations();
            if (annotations.length > 0) {
                Annotation paramAno = annotations[0];
                for (Method getParamMethod : this.getClass().getDeclaredMethods()) {
                    Boolean right = false;
                    for (Annotation methodAno : getParamMethod.getAnnotations()) {
                        if(Objects.equals(paramAno, methodAno)){
                            right=true;
                            break;
                        }
                    }
                    if (right){
                        try {
                            args.add(getParamMethod.invoke(this));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            } else args.add(null);

        }
        return args.toArray();
    }

    @RequiredParam.MCC
    private MCC getMCC() {
        return this.modelModuleMap.get(this.moduleClass);
    }

    @RequiredParam.ModuleMap
    private Map<Class, MCC> getModuleMap() {
        return this.modelModuleMap;
    }
}
