package jda.modules.mosarfrontend.common.utils.common_gen;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
import jda.modules.mosarfrontend.common.factory.Slot;
import org.modeshape.common.text.Inflector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class DomainNameUtil {
    public static final Inflector inflector = Inflector.getInstance();

    @SlotReplacement(slot = "moduleName")
    public static String moduleName(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(name, false);
    }

    @SlotReplacement(slot = "ModuleName")
    public static String ModuleName(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(name, true);
    }

    @SlotReplacement(slot = "ModuleNames")
    public static String ModuleNames(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(inflector.pluralize(name), true);
    }

    @SlotReplacement(slot = "moduleNames")
    public static String moduleNames(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(inflector.pluralize(name), false);
    }

    @SlotReplacement(slot = "MODULE_NAME")
    public static String MODULE_NAME(@RequiredParam.ModuleName String name) {
        return inflector.underscore(name).toUpperCase();
    }

    @SlotReplacement(slot = "module_name")
    public static String module_name(@RequiredParam.ModuleName String name) {
        return inflector.underscore(name);
    }

    @SlotReplacement(slot = "module_names")
    public static String module_names(@RequiredParam.ModuleName String name) {
        return module_name(inflector.pluralize(name));
    }

    @SlotReplacement(slot = "moduleJnames")
    public static String moduleJnames(@RequiredParam.ModuleName String name) {
        return inflector.underscore(inflector.upperCamelCase(inflector.pluralize(name))).replaceAll("_", "-");
    }

    @SlotReplacement(slot = "moduleJname")
    public static String moduleJname(@RequiredParam.ModuleName String name) {
        return inflector.underscore(name).replaceAll("_", "-");
    }

    @SlotReplacement(slot = "module__name")
    public static String module__name(@RequiredParam.ModuleName String name) {
        return inflector.underscore(inflector.upperCamelCase(name)).replaceAll("_", " ");
    }

    @SlotReplacement(slot = "Module__name")
    public static String Module__name(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(module__name(name), true);
    }

    @SlotReplacement(slot = "Module__names")
    public static String Module__names(@RequiredParam.ModuleName String name) {
        return Module__name(inflector.pluralize(name));
    }

    public static Slot[][] getBasicDomainNameSlots(String[] names) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String name : names) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            for (Method declaredMethod : DomainNameUtil.class.getDeclaredMethods()) {
                if (declaredMethod.isAnnotationPresent(SlotReplacement.class)) {
                    SlotReplacement ano = declaredMethod.getAnnotation(SlotReplacement.class);
                    try {
                        slotValues.add(new Slot(ano.slot(), (String) declaredMethod.invoke(null, name)));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
