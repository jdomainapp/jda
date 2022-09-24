package jda.modules.mosarfrontend.common.utils.common_gen;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.MethodUtils;
import org.modeshape.common.text.Inflector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class NameFormatter {
    public static final Inflector inflector = Inflector.getInstance();

    @SlotReplacement(id = "moduleName")
    public static String moduleName(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(name, false);
    }

    @SlotReplacement(id = "ModuleName")
    public static String ModuleName(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(name, true);
    }

    @SlotReplacement(id = "ModuleNames")
    public static String ModuleNames(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(inflector.pluralize(name), true);
    }

    @SlotReplacement(id = "moduleNames")
    public static String moduleNames(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(inflector.pluralize(name), false);
    }

    @SlotReplacement(id = "MODULE_NAME")
    public static String MODULE_NAME(@RequiredParam.ModuleName String name) {
        return inflector.underscore(name).toUpperCase();
    }

    @SlotReplacement(id = "module_name")
    public static String module_name(@RequiredParam.ModuleName String name) {
        return inflector.underscore(name);
    }

    @SlotReplacement(id = "module_names")
    public static String module_names(@RequiredParam.ModuleName String name) {
        return module_name(inflector.pluralize(name));
    }

    @SlotReplacement(id = "moduleJnames")
    public static String moduleJnames(@RequiredParam.ModuleName String name) {
        return inflector.underscore(inflector.upperCamelCase(inflector.pluralize(name))).replaceAll("_", "-");
    }

    @SlotReplacement(id = "moduleJname")
    public static String moduleJname(@RequiredParam.ModuleName String name) {
        return inflector.underscore(name).replaceAll("_", "-");
    }

    @SlotReplacement(id = "module__name")
    public static String module__name(@RequiredParam.ModuleName String name) {
        return inflector.underscore(inflector.upperCamelCase(name)).replaceAll("_", " ");
    }

    @SlotReplacement(id = "Module__name")
    public static String Module__name(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(module__name(name), true);
    }

    @SlotReplacement(id = "Module__Name")
    public static String Module__Name(@RequiredParam.ModuleName String name) {
        return inflector.upperCamelCase(module__name(name));
    }

    @SlotReplacement(id = "Module__names")
    public static String Module__names(@RequiredParam.ModuleName String name) {
        return Module__name(inflector.pluralize(name));
    }

    public static Slot[][] getBasicDomainNameSlots(String[] names, String customId) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String name : names) {
            result.add(getBasicDomainNameSlots(name, customId));
        }
        return MethodUtils.toLoopData(result);
    }
    public static Slot[][] getBasicDomainNameSlots(String[] names) {
        return getBasicDomainNameSlots(names,null);
    }
    public static ArrayList<Slot> getBasicDomainNameSlots(String name, String customId) {
        ArrayList<Slot> slotValues = new ArrayList<>();
        for (Method declaredMethod : NameFormatter.class.getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(SlotReplacement.class)) {
                SlotReplacement ano = declaredMethod.getAnnotation(SlotReplacement.class);
                try {
                    String id =  ano.id() ;
                    if(customId != null){
                        id =(String) declaredMethod.invoke(null, customId);
                        id = id.replaceAll("-","J");
                        id = id.replaceAll(" ","__");
                    }
                    slotValues.add(new Slot(id, (String) declaredMethod.invoke(null, name)));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return slotValues;
    }

    public static ArrayList<Slot> getBasicDomainNameSlots(String name) {
        return getBasicDomainNameSlots(name,null);
    }
}
