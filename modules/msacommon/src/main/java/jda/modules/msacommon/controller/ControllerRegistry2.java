package jda.modules.msacommon.controller;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings({"rawtypes"})
public final class ControllerRegistry2 {
    private static ControllerRegistry2 INSTANCE;
    public static ControllerRegistry2 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ControllerRegistry2();
        }
        return INSTANCE;
    }

    private final Map<String, DefaultController2> controllerTypeMap;

    private ControllerRegistry2() {
        this.controllerTypeMap = new ConcurrentHashMap<>();
    }

    public DefaultController2 get(String genericType) {
        for (Map.Entry<String, DefaultController2> entry : controllerTypeMap.entrySet()) {
            if (entry.getKey().toLowerCase(Locale.ROOT)
                    .contains(genericType.toLowerCase(Locale.ROOT).concat("controller"))) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public DefaultController2 get(Class cls) {
    	String genericType = cls.getSimpleName();
        return get(genericType);
    }

    public void put(String genericType, DefaultController2 controllerInstance) {
        this.controllerTypeMap.put(genericType, controllerInstance);
    }
    
    
}
