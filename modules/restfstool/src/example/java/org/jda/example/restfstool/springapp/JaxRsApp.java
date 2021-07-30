package org.jda.example.restfstool.springapp;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.hanu.courseman.modules.address.model.Address;
import com.hanu.courseman.modules.coursemodule.model.CompulsoryModule;
import com.hanu.courseman.modules.coursemodule.model.CourseModule;
import com.hanu.courseman.modules.coursemodule.model.ElectiveModule;
import com.hanu.courseman.modules.enrolment.model.Enrolment;
import com.hanu.courseman.modules.student.model.Student;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.restfstool.backend.annotations.bridges.TargetType;
import jda.modules.restfstool.backend.base.controllers.ServiceRegistry;
import jda.modules.restfstool.backend.base.services.CrudService;
import jda.modules.restfstool.backend.generators.GenerationMode;
import jda.modules.restfstool.backend.generators.WebServiceGenerator;
import jda.modules.restfstool.backend.utils.InheritanceUtils;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.SoftwareImpl;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JaxRsApp {
    static List<Class> resourceClasses = new LinkedList<>();

    // 1. initialise the model
    static final Class<?>[] model = {
        CourseModule.class,
        CompulsoryModule.class,
        ElectiveModule.class,
        Enrolment.class,
        Student.class,
        Address.class
    };

    private static SoftwareImpl sw;

    private static CrudService<?> createDefault(Class<CrudService> cls,
                                                SoftwareImpl arg0) {
        if (cls.getConstructors().length < 2) {
            String className = ((Qualifier)cls.getConstructors()[0].getParameterAnnotations()[1][0]).value();
            try {
                return createInherited(cls, arg0, InheritanceUtils.getSubtypeMapFor(Class.forName(className)));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return cls.getDeclaredConstructor(SoftwareImpl.class)
                .newInstance(arg0);
        } catch (NoSuchMethodException | InvocationTargetException
                | InstantiationException | IllegalAccessException ex) {

            throw new RuntimeException(ex);
        }
    }

    private static CrudService<?> createInherited(Class<CrudService> cls,
                                                  SoftwareImpl arg0,
                                                  Map<String, String> arg1) {
        try {
            return (CrudService<?>) cls.getConstructors()[0].newInstance(arg0, arg1);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("------------");

        WebServiceGenerator generator = new WebServiceGenerator(
                TargetType.SPRING,
                GenerationMode.SOURCE_CODE,
                "examples.domainapp.modules.webappgen.backend.services",
                "/Users/binh_dh/Documents/generated");
        generator.setGenerateCompleteCallback(generatedClasses -> {
            sw = SoftwareFactory.createDefaultDomSoftware();
            sw.init();
            try {
                sw.addClasses(model);
            } catch (NotPossibleException
                | NotFoundException
                | DataSourceException e) {
                throw new RuntimeException(e);
            }
            // populate the service registry
            final ServiceRegistry registry = ServiceRegistry.getInstance();

            generator.getGeneratedServiceClasses().forEach((k, v) -> {
                registry.put(k, createDefault(v, sw));
            });
            resourceClasses.addAll(generator.getGeneratedControllerClasses());

            startJettyServer();
        });
        generator.generateWebService(model);
        System.out.println("------------");
    }
    static {
        ((Logger) LoggerFactory.getLogger("org.eclipse.jetty")).setLevel(Level.INFO);
    }

    static void startJettyServer() {
        System.setProperty("rootLogger.level", "INFO");
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.setConnectors(new Connector[] { connector });

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class.getCanonicalName(), "/*");

//        FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*", EnumSet.of(DispatcherType.REQUEST));
//        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
//        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
//        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,PUT,PATCH,DELETE,OPTION,HEAD");
//        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter("jakarta.ws.rs.Application", JaxRsConfig.class.getCanonicalName());
        jerseyServlet.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES,
            JaxRsConfig.class.getCanonicalName() + ";" + JaxRsConfig.MyObjectMapperProvider.class.getCanonicalName());
        jerseyServlet.setInitParameter(ServerProperties.MOXY_JSON_FEATURE_DISABLE, "true");
        jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "org.jda.example.courseman.modules.simple");
        try {
            server.start();
            server.join();
        } catch (Exception ex) {
        } finally {
            server.destroy();
        }
    }
}
