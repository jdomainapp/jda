package jda.modules.mosarfrontend.common.factory;

import com.github.javaparser.ast.body.FieldDeclaration;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.frontend.MCCUtils;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParamsFactory {
    private static ParamsFactory instance;
    private final HashMap<Annotation, Method> methods = new HashMap<>();
    private MCC currentMCC;
    private Map<String, MCC> modules;

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
    public void setCurrentModule(Class<?> module) {
        this.currentMCC = modules.get(module.getSimpleName());
    }

    private RFSGenConfig rfsGenConfig;

    public void setRFSGenConfig(RFSGenConfig rfsGenConfig) {
        this.rfsGenConfig = rfsGenConfig;
        Class<?>[] models = rfsGenConfig.getDomainModel();
        Class<?>[] mccClasses = rfsGenConfig.getMCCFuncs();
        this.modules = IntStream.range(0, mccClasses.length)
                .mapToObj(i -> MCCUtils.readMCC(models[i],
                        mccClasses[i]))
                .collect(Collectors.toMap(MCC::getName, mcc -> mcc));
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
    private MCC getMCC() {
        return this.currentMCC;
    }

    private String[] modulesName; // Save for later trigger getModulesName()

    @RequiredParam.ModulesName
    private String[] getModulesName() {
        if (modulesName == null) {
            modulesName = modules.values().stream().map(m-> m.getDomainClass().getName()).toArray(String[]::new);
        }
        return modulesName;
    }

    @RequiredParam.ModuleName
    private String getModuleName() {
        return this.currentMCC.getDomainClass().getName();
    }

    @RequiredParam.ModuleFields
    private FieldDef[] getModuleFields() {
        FieldDeclaration[] fieldDeclarations = this.currentMCC.getDomainClass().getFields().toArray(FieldDeclaration[]::new);
        Collection<FieldDeclaration> fields = this.currentMCC.getViewFields();
        return Arrays.stream(fieldDeclarations).map(ParserToolkit::getFieldDefFull).filter(e -> e.getAnnotation(DAttr.class) != null).toArray(FieldDef[]::new);
    }
}
