package jda.modules.msacommon.controller;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings({"rawtypes"})
public final class ControllerRegistry {
    private static ControllerRegistry INSTANCE;
    public static ControllerRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ControllerRegistry();
        }
        return INSTANCE;
    }

    private final Map<String, DefaultController> controllerTypeMap;

    private ControllerRegistry() {
        this.controllerTypeMap = new ConcurrentHashMap<>();
    }

    public DefaultController get(String genericType) {
        for (Map.Entry<String, DefaultController> entry : controllerTypeMap.entrySet()) {
            if (entry.getKey().toLowerCase(Locale.ROOT)
                    .contains(genericType.toLowerCase(Locale.ROOT).concat("controller"))) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public DefaultController get(Class cls) {
    	String genericType = cls.getSimpleName();
        return get(genericType);
    }

    public void put(String genericType, DefaultController controllerInstance) {
        this.controllerTypeMap.put(genericType, controllerInstance);
    }
}
