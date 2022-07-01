package org.jda.example.coursemanmsa.coursemgnt.utils.controller;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings({"rawtypes"})
public final class RedirectControllerRegistry {
    private static RedirectControllerRegistry INSTANCE;
    public static RedirectControllerRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RedirectControllerRegistry();
        }
        return INSTANCE;
    }

    private final Map<String, RedirectController> redirectControllerTypeMap;

    private RedirectControllerRegistry() {
        this.redirectControllerTypeMap = new ConcurrentHashMap<>();
    }

    public RedirectController get(String genericType) {
        for (Map.Entry<String, RedirectController> entry : redirectControllerTypeMap.entrySet()) {
            if (entry.getKey().toLowerCase(Locale.ROOT)
                    .contains(genericType.toLowerCase(Locale.ROOT).concat("controller"))) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public RedirectController get(Class cls) {
    	String genericType = cls.getSimpleName();
        return get(genericType);
    }

    public void put(String genericType, RedirectController controllerInstance) {
        this.redirectControllerTypeMap.put(genericType, controllerInstance);
    }
}
