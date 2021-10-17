package jda.modules.mosar.utils;

import org.modeshape.common.text.Inflector;

public class StringUtils {
    private static final Inflector inflector = Inflector.getInstance();
    public static String toUrlEntityString(String original) {
        return inflector.underscore(
                inflector.pluralize(
                        inflector.underscore(original)))
                .replace("_", "-");
    }

    public static void main(String[] args) {
        System.out.println(toUrlEntityString("SClass"));
    }
}
