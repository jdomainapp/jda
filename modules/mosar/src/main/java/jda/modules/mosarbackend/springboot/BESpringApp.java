package jda.modules.mosarbackend.springboot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import jda.modules.mosar.backend.base.controllers.ServiceRegistry;
import jda.modules.mosar.backend.base.services.CrudService;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.software.backend.BEApp;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.DomSoftware;
import jda.mosa.software.impl.SoftwareImpl;

/**
 * @overview 
 *  The base {@link SpringApplication} for running the generated RFS.
 *   
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
@SpringBootApplication
@ComponentScan(basePackages = { 
    "jda.modules.mosar.backend", // system beans
    "${domainBasePackages}",  // domain beans
})
public class BESpringApp implements BEApp, Consumer<List<Class>>{
  private static Logger logger = (Logger) LoggerFactory.getLogger("module.restfstool");

  private static final List<Class> generatedClasses = new ArrayList<>();
  private static SoftwareImpl sw;
  
  // static: (similar to sw) for available as shared Bean 
  // so that it is accessible by @Bean method corsConfigurer() (below)
  private static RFSGenConfig cfg;

  public BESpringApp() {
    // for SpringBoot
  }
  
  public BESpringApp(RFSGenConfig cfg) {
    this.cfg = cfg;
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
  }
  
  /**
   * @effects 
   *  create a {@link SpringBootApplication} from <code>components</code> and 
   *  run it. The web application is served using the default port (8080).
   *  
   *  <p>The data management back-end is managed by a {@link DomSoftware} that is created
   *  as a bean (see {@link #getSoftwareImpl()} using the data source configuration specified in <code>cfg</code>.
   */
  @Override
  public void run(Collection<? extends Class> components) {
    generatedClasses.addAll(components);
    sw = SoftwareFactory.createStandardDomSoftware(cfg.getSCC());
    sw.init();
    
    Class<?>[] model = cfg.getDomainModel();
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
    primarySources[generatedClassesCount] = this.getClass(); //BESpringApp.class;

    // ducmle: added command line argument to use a random port
    String domainBasePackages = cfg.getBeTargetPackage();
    long serverPort = getBeServerPort();
    
    // TODO: use VM properties in place of args below in later versions of Spring
    String[] args = {
        "--server.port=" + serverPort,
//      "--server.port=8080",
//        "--logging.level.org.springframework.web=debug"
        "--domainBasePackages=" + domainBasePackages
    }; // new String[0]
    
    logger.info(this.getClass().getSimpleName() + ": starting server with args: \n   " + Arrays.toString(args));
    
    ApplicationContext ctx = SpringApplication.run(primarySources, args);
    
    ctx.getBeansOfType(CrudService.class).forEach((k, v) -> registry.put(k, v));    
  }
  
  @Bean
  public WebMvcConfigurer corsConfigurer() {
      return new WebMvcConfigurer() {
          @Override
          public void addCorsMappings(CorsRegistry registry) {
              registry.addMapping("/**")
                      .allowedMethods("GET", "POST", "PATCH", "DELETE")
                      // ducmle: leave this commented for development
                      // (uncomment for production settings)
                      // .allowedOrigins("http://localhost:"+cfg.getFeServerPort())
                      ;
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

  /**
   * @effects 
   *  return the configured back-end server port
   */
  public long getBeServerPort() {
    return cfg.getBeServerPort();
  }
}
