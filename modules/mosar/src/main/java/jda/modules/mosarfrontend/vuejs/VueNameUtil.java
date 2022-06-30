package jda.modules.mosarfrontend.vuejs;

import org.modeshape.common.text.Inflector;

public class VueNameUtil {
    public static final Inflector inflector = Inflector.getInstance();

    public static String moduleName(String name) {
        return inflector.camelCase(name, false);
    }

    public static String ModuleName(String name) {
        return inflector.camelCase(name, true);
    }

    public static String MODULE_NAME(String name) {
        return inflector.underscore(name).toUpperCase();
    }

    public static String module_name(String name) {
        return inflector.underscore(name).replaceAll("_", "-");
    }

    public static String Module__name(String name) {
        return inflector.underscore(inflector.upperCamelCase(inflector.pluralize(name))).replaceAll("_"," ");
    }
}
