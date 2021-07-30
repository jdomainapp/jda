package org.jda.example.restfstool.rfsgen.v1_0;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.restfstool.backend.annotations.bridges.TargetType;
import jda.modules.restfstool.backend.base.controllers.ServiceRegistry;
import jda.modules.restfstool.backend.base.services.CrudService;
import jda.modules.restfstool.backend.generators.GenerationMode;
import jda.modules.restfstool.backend.generators.WebServiceGenerator;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.SoftwareImpl;

/**
 * @author binh_dh
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.hanu.courseman.backend",
        "jda.modules.restfstool.backend"})
public class BackendApp {

    private static final List<Class> generatedClasses = new ArrayList<>();
    private static SoftwareImpl sw;

    public static void onGenerateComplete(List<Class> _generatedClasses) {
        generatedClasses.addAll(_generatedClasses);
        sw = SoftwareFactory.createDefaultDomSoftware();
        sw.init();
        try {
            sw.addClasses(CourseManAppGenerator.models);
            sw.loadObjects(CourseManAppGenerator.models);
        } catch (NotPossibleException
                | NotFoundException
                | DataSourceException e) {
            throw new RuntimeException(e);
        }
        // populate the service registry
        final ServiceRegistry registry = ServiceRegistry.getInstance();

        final int generatedClassesCount = generatedClasses.size();
        Class[] primarySources = generatedClasses.toArray(
                new Class[generatedClassesCount + 1]);
        primarySources[generatedClassesCount] = BackendApp.class;

        ApplicationContext ctx = SpringApplication.run(primarySources, new String[0]);

        ctx.getBeansOfType(CrudService.class).forEach((k, v) -> registry.put(k, v));
    }

    static final String backendTargetPackage = "com.hanu.courseman.backend";
    static final String backendOutputPath = "src/example/java";
    
    public static void setup() {
        System.out.println("------------");
        WebServiceGenerator generator = new WebServiceGenerator(
                TargetType.SPRING,
                GenerationMode.SOURCE_CODE,
                backendTargetPackage,
                backendOutputPath);
        generator.generateWebService(CourseManAppGenerator.models);
        System.out.println("------------");
    }

    /**
     * Setup and run the backend process.
     */
    public static void setupAndRun() {
        System.out.println("------------");

        WebServiceGenerator generator = new WebServiceGenerator(
                TargetType.SPRING,
                GenerationMode.SOURCE_CODE,
                backendTargetPackage,
                backendOutputPath);
        generator.setGenerateCompleteCallback(
                BackendApp::onGenerateComplete);
        generator.generateWebService(CourseManAppGenerator.models);
        System.out.println("------------");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PATCH", "DELETE")
                        .allowedOrigins("http://localhost:3000");
            }
        };
    }

    @Bean
    public SoftwareImpl getSoftwareImpl() {
        return sw;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addCustomBigDecimalDeserialization() {
        return builder -> builder.dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .modules(new ParameterNamesModule())
                .serializationInclusion(JsonInclude.Include.NON_NULL);
        //.configure(mapper);
    }
}