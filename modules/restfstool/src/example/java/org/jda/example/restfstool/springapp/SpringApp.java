package org.jda.example.restfstool.springapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jda.example.restfstool.springapp.services.coursemodule.model.CompulsoryModule;
import org.jda.example.restfstool.springapp.services.coursemodule.model.CourseModule;
import org.jda.example.restfstool.springapp.services.coursemodule.model.ElectiveModule;
import org.jda.example.restfstool.springapp.services.enrolment.model.Enrolment;
import org.jda.example.restfstool.springapp.services.sclass.model.SClass;
import org.jda.example.restfstool.springapp.services.student.model.Address;
import org.jda.example.restfstool.springapp.services.student.model.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
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
        "org.examples.jda.restfstool.springapp",
        "jda.modules.restfstool.backend"})
public class SpringApp {

    // 1. initialise the model
    static final Class<?>[] model = {
            CourseModule.class,
            CompulsoryModule.class,
            ElectiveModule.class,
            Enrolment.class,
            Student.class,
            Address.class,
            SClass.class
    };
    private static final List<Class> generatedClasses = new ArrayList<>();
    private static SoftwareImpl sw;

    /**
     * @param args The arguments of the program.
     */
    public static void main(final String[] args) {
        System.out.println("------------");

        WebServiceGenerator generator = new WebServiceGenerator(
                TargetType.SPRING,
                GenerationMode.SOURCE_CODE,
                "org.examples.jda.restfstool.springapp.services",
                ToolkitIO.getPath(ToolkitIO.getCurrentDir(), 
                    "generated").toString());
        generator.setGenerateCompleteCallback(_generatedClasses -> {
            generatedClasses.addAll(_generatedClasses);
            sw = SoftwareFactory.createDefaultDomSoftware();
            sw.init();
            try {
                sw.addClasses(model);
                sw.loadObjects(model);
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
            primarySources[generatedClassesCount] = SpringApp.class;

            GenericWebApplicationContext ctx = (GenericWebApplicationContext)
                    SpringApplication.run(primarySources, args);

            ctx.getBeansOfType(CrudService.class).forEach((k, v) -> registry.put(k, v));
        });
        generator.generateWebService(model);
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
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .autoDetectFields(true)
                .autoDetectGettersSetters(true);
                //.configure(mapper);
    }
}
