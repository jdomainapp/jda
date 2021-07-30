package org.jda.example.restfstool.springapp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jda.modules.restfstool.backend.svcdesc.ServiceDescriptionController;
import jda.modules.restfstool.backend.utils.PackageUtils;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.DefaultJacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.text.SimpleDateFormat;

@ApplicationPath("")
public class JaxRsConfig extends ResourceConfig {
    public JaxRsConfig() {
        packages(PackageUtils.basePackageOf(JaxRsApp.resourceClasses.get(0)));
        for (Class<?> cls : JaxRsApp.resourceClasses) {
            register(cls);
        }
        register(ServiceDescriptionController.class);
        register(MyObjectMapperProvider.class); // No need to register this provider if no special configuration is required.
        var provider = new DefaultJacksonJaxbJsonProvider();
        provider.setMapper(createDefaultMapper());
        register(provider);
        register(JacksonFeature.class);
    }

    @Provider
    public static class MyObjectMapperProvider implements ContextResolver<ObjectMapper> {

        final ObjectMapper defaultObjectMapper;

        public MyObjectMapperProvider() {
            defaultObjectMapper = createDefaultMapper();
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return defaultObjectMapper;
        }
    }

    static ObjectMapper createDefaultMapper() {
        return new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(MapperFeature.USE_ANNOTATIONS, true)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            .setConstructorDetector(ConstructorDetector.USE_PROPERTIES_BASED);
    }

}
