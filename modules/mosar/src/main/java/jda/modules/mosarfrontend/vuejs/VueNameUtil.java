package jda.modules.mosarfrontend.vuejs;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import org.modeshape.common.text.Inflector;

public class VueNameUtil {
    public static final Inflector inflector = Inflector.getInstance();

    @SlotReplacementDesc(slot = "moduleName")
    public static String moduleName(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(name, false);
    }

    @SlotReplacementDesc(slot = "ModuleName")
    public static String ModuleName(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(name, true);
    }

    @SlotReplacementDesc(slot = "ModuleNames")
    public static String ModuleNames(@RequiredParam.ModuleName String name) {
        return inflector.camelCase(inflector.pluralize(name), true);
    }

    @SlotReplacementDesc(slot = "MODULE_NAME")
    public static String MODULE_NAME(@RequiredParam.ModuleName String name) {
        return inflector.underscore(name).toUpperCase();
    }

    @SlotReplacementDesc(slot = "module_name")
    public static String module_name(@RequiredParam.ModuleName String name) {
        return inflector.underscore(name);
    }

    @SlotReplacementDesc(slot = "moduleJname")
    public static String moduleJname(@RequiredParam.ModuleName String name) {
        return inflector.underscore(name).replaceAll("_", "-");
    }

    @SlotReplacementDesc(slot = "module__name")
    public static String module__name(@RequiredParam.ModuleName String name) {
        return inflector.underscore(inflector.upperCamelCase(inflector.pluralize(name))).replaceAll("_", " ");
    }

    @SlotReplacementDesc(slot = "moduleJnames")
    public static String moduleJnames(@RequiredParam.ModuleName String name) {
        return inflector.underscore(inflector.upperCamelCase(inflector.pluralize(name))).replaceAll("_", "-");
    }
}
