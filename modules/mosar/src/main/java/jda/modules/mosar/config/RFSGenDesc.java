package jda.modules.mosar.config;

import jda.modules.mosar.software.backend.BEApp;
import jda.modules.mosar.software.frontend.FEApp;
import jda.modules.mosarfrontend.reactnative.ReactNativeAppTemplate;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Duc Minh Le (ducmle)
 * @version 5.4.1
 * @overview RESTful full-stack generator configuration
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
public @interface RFSGenDesc {
    /**
     * execution spec
     */
    ExecSpec execSpec() default ExecSpec.Full;


    StackSpec stackSpec();

    /**
     * backend language platform
     */
    LangPlatform beLangPlatform();

    /**
     * backend top-level package that contains the domain model
     */
    String bePackage();

    /**
     * front-end platform
     */
    FEPlatform fePlatform() default FEPlatform.REACT;

    /**
     * backend target top-level package (for all generated code)
     */
    String beTargetPackage();

    /**
     * backend output path (where {@link #beTargetPackage()} lives)
     */
    String beOutputPath();

    /**
     * backend main application that is executed by the web server.
     * For now, it is Spring-specific. This may be changed in the future to suit the {@link #beLangPlatform()}
     */
    Class<? extends BEApp> beAppClass(); // default Null.class;

    /**
     * backend server port
     */
    long beServerPort() default 8080;

    boolean beThreaded() default false;

    /**
     * code generation mode (source code or byte code)
     */
    GenerationMode genMode();

    /**
     * frontend output path
     */
    String feOutputPath();

    /**
     * front-end project path
     */
    @Deprecated
    String feProjPath();

    /**
     * front-end project name
     */
    @Deprecated
    String feProjName();

    /**
     * front-end's shared resources for project
     */
    @Deprecated
    String feProjResource();

    /**
     * frontend server port
     */
    @Deprecated
    long feServerPort() default 3000;

    /**
     * frontend main application that is executed by the web server.
     */
    Class<? extends FEApp> feAppClass();

    boolean feThreaded() default false;
}
