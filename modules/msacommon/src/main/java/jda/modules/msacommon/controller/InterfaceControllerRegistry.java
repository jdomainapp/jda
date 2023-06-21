package jda.modules.msacommon.controller;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings({"rawtypes"})
public final class InterfaceControllerRegistry {
    private static InterfaceControllerRegistry INSTANCE;
    public static InterfaceControllerRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InterfaceControllerRegistry();
        }
        return INSTANCE;
    }

    private final Map<String, InterfaceController> interfaceControllerTypeMap;

    private InterfaceControllerRegistry() {
        this.interfaceControllerTypeMap = new ConcurrentHashMap<>();
    }

    public InterfaceController get(String genericType) {
        for (Map.Entry<String, InterfaceController> entry : interfaceControllerTypeMap.entrySet()) {
            if (entry.getKey().toLowerCase(Locale.ROOT)
                    .contains(genericType.toLowerCase(Locale.ROOT).concat("controller"))) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    public InterfaceController get(Class cls) {
    	String genericType = cls.getSimpleName();
        return get(genericType);
    }

    public void put(String genericType, InterfaceController controllerInstance) {
        this.interfaceControllerTypeMap.put(genericType, controllerInstance);
    }
}
