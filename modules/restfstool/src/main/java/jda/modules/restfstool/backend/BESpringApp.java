package jda.modules.restfstool.backend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

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
// TODO: subtypes: replace this with actual application package    
//        "com.hanu.courseman.backend",
        "jda.modules.restfstool.backend"
    })
public abstract class BESpringApp implements Consumer<List<Class>>{
  private static final List<Class> generatedClasses = new ArrayList<>();
  private static SoftwareImpl sw;
  
  private Class<?>[] model;

  public BESpringApp(Class<?>[] models) {
    this.model = models;
  }
  
  /**
   * @effects 
   *  Implements call-back behaviour that is executed after back-end application 
   *  has been generated, whose classes are provided through <code>_generatedClasses</code>.
   *  
   *  <p>Instantiates a JDA's DODM instance (managed through {@link #sw}).
   *  
   *  <p>Starts a {@link SpringBootApplication} using this class and <code>_generatedClasses</code>
   *  as the primary sources.
   *  
   *  <p>Registers all the created {@link CrudService} instances into the {@link ServiceRegistry}.
   */
  @Override
  public void accept(List<Class> _generatedClasses) {
    run(_generatedClasses);
//    generatedClasses.addAll(_generatedClasses);
//    sw = SoftwareFactory.createDefaultDomSoftware();
//    sw.init();
//    try {
//        sw.addClasses(model);
//        sw.loadObjects(model);
//    } catch (NotPossibleException
//            | NotFoundException
//            | DataSourceException e) {
//        throw new RuntimeException(e);
//    }
//    // populate the service registry
//    final ServiceRegistry registry = ServiceRegistry.getInstance();
//
//    final int generatedClassesCount = generatedClasses.size();
//    Class[] primarySources = generatedClasses.toArray(
//            new Class[generatedClassesCount + 1]);
//    primarySources[generatedClassesCount] = BackendMain.class;
//
//    ApplicationContext ctx = SpringApplication.run(primarySources, new String[0]);
//
//    ctx.getBeansOfType(CrudService.class).forEach((k, v) -> registry.put(k, v));    
  }
  
  public void run(Collection<? extends Class> components) {
    generatedClasses.addAll(components);
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
    primarySources[generatedClassesCount] = BESpringApp.class;

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
