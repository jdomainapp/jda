package org.jda.example.coursemanmsa.common.controller;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.repository.PagingAndSortingRepository;


@SuppressWarnings({"rawtypes"})
public final class ServiceRegistry {
    private static ServiceRegistry INSTANCE;
    public static ServiceRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServiceRegistry();
        }
        return INSTANCE;
    }

    private final Map<String, PagingAndSortingRepository> serviceTypeMap;

    private ServiceRegistry() {
        this.serviceTypeMap = new ConcurrentHashMap<>();
    }

    public PagingAndSortingRepository get(String type) {
        for (Map.Entry<String, PagingAndSortingRepository> entry : serviceTypeMap.entrySet()) {
            if (entry.getKey().toLowerCase(Locale.ROOT)
                    .contains(type.toLowerCase(Locale.ROOT).concat("repository"))) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void put(String genericType, PagingAndSortingRepository serviceInstance) {
        this.serviceTypeMap.put(genericType, serviceInstance);
    }
}
