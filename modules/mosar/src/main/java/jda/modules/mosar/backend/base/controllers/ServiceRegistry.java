package jda.modules.mosar.backend.base.controllers;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.services.CrudService;

@SuppressWarnings({"rawtypes"})
public final class ServiceRegistry {
    private static ServiceRegistry INSTANCE;
    public static ServiceRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServiceRegistry();
        }
        return INSTANCE;
    }

    private final Map<String, CrudService> serviceTypeMap;

    private ServiceRegistry() {
        this.serviceTypeMap = new ConcurrentHashMap<>();
    }

    public CrudService get(String type) {
        for (Map.Entry<String, CrudService> entry : serviceTypeMap.entrySet()) {
            if (entry.getKey().toLowerCase(Locale.ROOT)
                    .contains(type.toLowerCase(Locale.ROOT).concat("service"))) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void put(String genericType, CrudService serviceInstance) {
        this.serviceTypeMap.put(genericType, serviceInstance);
        serviceInstance.setOnCascadeUpdate(
                (BiConsumer<Identifier, Object>) this::handleDomainObjectUpdate);
    }

    private void handleDomainObjectUpdate(Identifier identifier, Object object) {
        ServiceRegistry.getInstance().get(object.getClass().getSimpleName())
                .updateEntity(identifier, object);
    }
}
