package jda.modules.mosar.software.backend.svcdesc;

/**
 * Represent the description of a Web Service
 * @author binh_dh
 */
final class ServiceDescription {
    private final String name;
    private final String endpoint;
    private final ServiceDescription[] nested;

    private ServiceDescription(String name, String endpoint) {
        this(name, endpoint, null);
    }

    private ServiceDescription(String name, String endpoint, ServiceDescription[] nested) {
        this.name = name;
        this.endpoint = endpoint;
        this.nested = nested;
    }

    public String getName() {
        return name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public ServiceDescription[] getNested() {
        return nested;
    }

    public static ServiceDescription from(ServiceController annotation) {
        if (annotation == null) return null;
        return new ServiceDescription(annotation.name(), annotation.endpoint());
    }

    public static ServiceDescription from(ServiceController annotation, ServiceDescription[] nested) {
        if (annotation == null) return null;
        int counter = 0;
        for (ServiceDescription nestedDesc : nested) {
            nested[counter++] = new ServiceDescription(nestedDesc.name,
                annotation.endpoint() + "/{id}" + nestedDesc.endpoint);
        }
        return new ServiceDescription(annotation.name(), annotation.endpoint(), nested);
    }
}
