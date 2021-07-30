package org.jda.example.restfstool.rfsgen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.jda.example.restfstool.rfsgen.v1_0.CourseManAppGenerator;
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
import jda.modules.restfstool.backend.base.controllers.ServiceRegistry;
import jda.modules.restfstool.backend.base.services.CrudService;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.SoftwareImpl;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.hanu.courseman.backend",
        "jda.modules.restfstool.backend"})
public class BackendMain implements Consumer<List<Class>>{
  private static final List<Class> generatedClasses = new ArrayList<>();
  private static SoftwareImpl sw;
  
  private Class<?>[] model;

  public BackendMain(Class<?>[] models) {
    this.model = models;
  }
  
  @Override
  public void accept(List<Class> _generatedClasses) {
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
    primarySources[generatedClassesCount] = BackendMain.class;

    ApplicationContext ctx = SpringApplication.run(primarySources, new String[0]);

    ctx.getBeansOfType(CrudService.class).forEach((k, v) -> registry.put(k, v));    
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
