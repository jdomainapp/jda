package jda.modules.mosar.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DomainTypeRegistry {
    private final Map<String, Class<?>> domainTypeMap;

    private DomainTypeRegistry() {
        domainTypeMap = new ConcurrentHashMap<>();
    }

    public void addDomainType(Class<?> domainType) {
        domainTypeMap.putIfAbsent(domainType.getSimpleName(), domainType);
    }

    public void addDomainTypes(Class<?>[] domainTypes) {
        for (Class<?> domainType : domainTypes) {
            domainTypeMap.putIfAbsent(domainType.getSimpleName(), domainType);
        }
    }

    public Class<?> getDomainTypeByName(String simpleName) {
        return domainTypeMap.getOrDefault(simpleName, null);
    }

    private static final DomainTypeRegistry instance = new DomainTypeRegistry();

    public static DomainTypeRegistry getInstance() {
        return instance;
    }
}
