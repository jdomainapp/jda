package jda.modules.mosar.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.util.DClassTk;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.config.RFSGenDesc;
import jda.modules.mosar.software.RFSoftware;
import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.sccl.syntax.SystemDesc;
import jda.util.SwTk;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Duc Minh Le (ducmle)
 * @version 5.4
 * @overview Toolkit class for {@link RFSoftware}.
 */
public class RFSGenTk {
    private RFSGenTk() {
    }

    /**
     * @effects extract and return {@link RFSGenConfig} from the scc or return null if
     * it is not available.
     * <p>
     * Throws {@link NotFoundException} if system configuration is not specified in scc.
     */
    public static RFSGenConfig parseRFSGenConfig(Class scc) throws NotFoundException {
        if (scc == null)
            return null;

        RFSGenDesc rfsGenDesc = (RFSGenDesc) scc.getAnnotation(RFSGenDesc.class);

        if (rfsGenDesc == null) {
            throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[]{RFSGenDesc.class, scc});
        }

        RFSGenConfig cfg = new RFSGenConfig();

        parseAnnotation2Config(rfsGenDesc, cfg);

        cfg.setFeTemplate((AppTemplateDesc) scc.getAnnotation(AppTemplateDesc.class));
        cfg.setSystemDesc((SystemDesc) scc.getAnnotation(SystemDesc.class));

        // the domain model
        Collection<Class> modelAsCol = SwTk.parseDomainModel(scc);
        // read all domain classes (including the subtypes)
        addDescendantTypes(cfg.getBePackage(), modelAsCol);

        Class[] model = modelAsCol.toArray(Class[]::new);
        cfg.setDomainModel(model);

        Class[] mccs = SwTk.parseMCCs(scc);
        for (Class mcc : mccs) {
            ModuleDescriptor md = (ModuleDescriptor) mcc.getAnnotation(ModuleDescriptor.class);
            if (md.type().isMain()) {
                cfg.setMCCMain(mcc);
            } else if (md.type().isDomain()) {
                // functional modules
                cfg.addMCCFunc(mcc);
            }
        }

        cfg.setSCC(scc);

        return cfg;
    }


    /**
     * @modifies domainModel
     * @effects if exists descendant types of domain classes in <code>domainModel</code>
     * add them to domainModel
     * else
     * do nothing
     */
    public static void addDescendantTypes(String topPkgFQN, Collection<Class> domainModel) {
        Set<Class> descendants = new HashSet<>();
        domainModel.forEach(dcls ->
                descendants.addAll(getDescendantTypesOf(topPkgFQN, dcls)));

        domainModel.addAll(descendants);
    }

    /**
     * @requires config.class has a corresponding field for each annotation element in ano
     * @effects for each element e in ano
     * copy its value to the corresponding f in config
     *
     * <p>Throws NotFoundException if config.class does not have a required corresponding field,
     * NotPossibleException if failed to set value of the corresponding field
     */
    public static <T extends Annotation> void parseAnnotation2Config(T ano, Object config) throws NotFoundException {
        Class<? extends Annotation> anoType = ano.annotationType();
        Class cfgCls = config.getClass();

        Method[] methods = anoType.getDeclaredMethods();

        for (Method m : methods) {
            String name = m.getName();
            Method corMethod = DClassTk.findSetterMethod(cfgCls, name);

            if (corMethod == null)
                throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND,
                        new Object[]{cfgCls, "set-" + name});

            Object val = null;
            try {
                val = m.invoke(ano);
            } catch (IllegalAccessException | IllegalArgumentException
                     | InvocationTargetException e) {
                throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
                        new Object[]{anoType, name, ""});
            }

            try {
                corMethod.invoke(config, val);
            } catch (IllegalAccessException | IllegalArgumentException
                     | InvocationTargetException e) {
                throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
                        new Object[]{cfgCls, "set-" + name, val});
            }
        }
    }


    /**
     * @effects registers domain classes in <code>model</code> to the system.
     * @todo replace DomainTypeRegistry by using DSM
     */
    public static void init(Class<?>[] model) {
        DomainTypeRegistry regist = DomainTypeRegistry.getInstance();
        regist.addDomainTypes(model);

        // register all the enum types of the domain attributes in the domain classes
        for (Class<?> dcls : model) {
            DClassTk.getDomainEnumTypedAttribs(dcls).ifPresent(col -> {
                col.forEach(enumType -> regist.addDomainType(enumType.getType()));
            });
        }
    }

    /**
     * @effects if exists descendant types of <code>c</code> in the project package <code>pkgFQN</code>
     * return them as Set
     * else
     * return an empty set
     */
    @SuppressWarnings("unchecked")
    public static Set<Class> getDescendantTypesOf(String pkgFQN, Class c) {
        loggingOff("org.reflections");

        Reflections refls = new Reflections(pkgFQN);
        Set<Class> descendants = refls.getSubTypesOf(c);
//    return (descendants != null && !descendants.isEmpty()) ? descendants : null;
        return descendants;
    }

    /**
     * @effects turn off logging for the logger named <code>loggerName</code> (if one exists).
     */
    public static void loggingOff(String loggerName) {
        Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
        logger.setLevel(Level.OFF);
    }
}
