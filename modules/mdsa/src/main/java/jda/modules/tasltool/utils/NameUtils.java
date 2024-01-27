package jda.modules.tasltool.utils;

import org.apache.commons.lang.WordUtils;

public class NameUtils {
    public static String toPackageName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]+", "_").toLowerCase();
    }

    public static String toLowerCamelCase(String name) {
        name = toUpperCamelCase(name);
        name = name.substring(0,1).toLowerCase()+name.substring(1);

        return name;
    }

    public static String toUpperCamelCase(String name) {
        name = name.replaceAll("[^a-zA-Z0-9]+", " ");
        name = WordUtils.capitalizeFully(name);
        name = name.replace(" ", "");

        return name;
    }
}
