package jda.modules.mosar.software.backend.svcdesc;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import jda.modules.mosar.utils.ClassAssocUtils;
import jda.modules.mosar.utils.ClassUtils;

/**
 * The utility class looking into Web service classes and returning their
 * descriptions.
 * @author binh_dh
 */
final class ServiceDescriptor {
    private final Map<String, ServiceDescription> descriptions;
    private final Map<String, String> mappedServices;

    ServiceDescriptor() {
        descriptions = new HashMap<>();
        mappedServices = new HashMap<>();
    }

    /**
     * Describe a web service class.
     */
    public ServiceDescription describe(Class<?> serviceClass) {
        return describeNested(serviceClass, true);
    }

    private ServiceDescription describeNested(Class<?> serviceClass, boolean moreNesting) {
        ServiceController annotation =
            serviceClass.getAnnotation(ServiceController.class);
        if (annotation == null) return null;
        mappedServices.putIfAbsent(annotation.className(), serviceClass.getName());
        if (!moreNesting) return ServiceDescription.from(annotation);
        Class<?> cls;
        try {
            cls = Class.forName(annotation.className());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        List<Class<?>> nestedEntityTypes = ClassAssocUtils.getNested(cls);

        if (nestedEntityTypes.isEmpty()) return ServiceDescription.from(annotation);
        ServiceDescription[] descriptions = new ServiceDescription[nestedEntityTypes.size()];

        descriptions = nestedEntityTypes.stream()
            .map(c -> mappedServices.get(c.getName()))
            .map(svcName -> {
                try { return Class.forName(svcName); }
                catch (ClassNotFoundException ex) { return null; }
            }).filter(Objects::nonNull)
            .map(svc -> describeNested(svc, false))
            .collect(Collectors.toList()).toArray(descriptions);
        return ServiceDescription.from(annotation, descriptions);
    }

    /**
     * Describe a list of service classes.
     */
    public List<ServiceDescription> describeList(Collection<Class<?>> serviceClasses) {
        List<ServiceDescription> serviceDescriptions = new ArrayList<>();
        for (Class<?> serviceClass : serviceClasses) {
            String fqClassName = serviceClass.getName();
            if (!descriptions.containsKey(fqClassName)) {
                ServiceDescription description = describeNested(serviceClass, false);
                if (description == null) continue;
                descriptions.put(fqClassName, description);
            }
        }
        for (String className : descriptions.keySet()) {
            Class<?> serviceClass;
            try {
                serviceClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            ServiceDescription newDesc = describeNested(serviceClass, true);
            descriptions.put(className, newDesc);
            serviceDescriptions.add(newDesc);
        }
        return serviceDescriptions;
    }

    /**
     * Get descriptions of all annotated service controllers within a package.
     */
    public List<ServiceDescription> describePackage(String packageName) {
        try {
            List<Class<?>> serviceClasses = ClassUtils.getClasses(packageName);
            return this.describeList(serviceClasses);
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalArgumentException("Invalid package name: " + packageName);
        }

    }

    private static ServiceDescriptor instance;
    public static ServiceDescriptor getDescriber() {
        if (instance == null) instance = new ServiceDescriptor();
        return instance;
    }
}
