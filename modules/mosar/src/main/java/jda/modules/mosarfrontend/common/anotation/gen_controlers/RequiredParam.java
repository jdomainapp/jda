package jda.modules.mosarfrontend.common.anotation.gen_controlers;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface RequiredParam {
	@Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @interface TemplateFolder {
	}
	@Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface AppName {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface SystemDesc {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface RFSGenConfig {
    }

    /**
    * @Ussage Require MCC of current module as a parameter of function
    * @DataType {@link jda.modules.mccl.conceptualmodel.MCC}
    */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface MCC {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface ModuleMap {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface ModulesName {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface ModuleFields {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface ModuleName {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface ModuleField {
    }

	@Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface CurrentSubDomain {
    }

    /**
    * @Ussage Require map of subDomainType to  subDomain (Map<\String,Domain>) of current module as a parameter of function
     * Type: Map<String, Domain>
    * @DataType {@link jda.modules.common.collection.Map<String,jda.modules.mosarfrontend.common.utils.Domain>}
    */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface SubDomains {
    }

    /**
     * @Ussage Require list linked domains of current module as a parameter of function
     * @DataType {@link jda.modules.mosarfrontend.common.utils.Domain}
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface LinkedDomains {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Deprecated
    @Documented
    @interface AngularProp {
    }

    /**
     * @Ussage Require list linked domains of current module as a parameter of function
     * @DataType {@link jda.modules.mosarfrontend.common.utils.Domain}
     */
	@Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Documented
    @interface LinkedFields {
    }
}
