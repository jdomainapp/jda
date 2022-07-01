package jda.modules.mosarfrontend.vuejs.common_gen;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
import org.modeshape.common.text.Inflector;

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

    @SlotReplacement(slot = "moduleJnames")
    public static String moduleJnames(@RequiredParam.ModuleName String name) {
        return inflector.underscore(inflector.upperCamelCase(inflector.pluralize(name))).replaceAll("_", "-");
    }
}
