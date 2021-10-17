package jda.modules.mosar.utils;

import java.util.stream.Stream;

public final class PackageUtils {
    public static String basePackageOf(Class<?> cls) {
        return Stream.of(cls.getClassLoader().getDefinedPackages())
            .map(p -> p.getName())
            .filter(cls.getName()::contains)
            .sorted()
            .findFirst().orElse("");
    }

    static String getShortBaseModulePackage(Class<?> modelClass) {
        String[] parts = modelClass.getPackageName()
                .replace(".models", "")
                .replace(".model", "")
                .trim()
                .split("\\.");
        return parts[parts.length - 1];
    }

    static String getBaseModulePackage(Class<?> modelClass) {
        return modelClass.getPackageName()
                .replace(".models", "")
                .replace(".model", "")
                .trim();
    }

    public static String basePackageFrom(String outputPackage, Class<?> modelClass) {
        return outputPackage != null ?
                outputPackage.concat(".").concat(getShortBaseModulePackage(modelClass))
                : PackageUtils.getBaseModulePackage(modelClass);
    }
}
