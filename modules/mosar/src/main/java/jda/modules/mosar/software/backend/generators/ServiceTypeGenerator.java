package jda.modules.mosar.software.backend.generators;

import jda.modules.mosar.backend.base.services.CrudService;
import jda.modules.mosar.config.GenerationMode;

public interface ServiceTypeGenerator {
    <T> Class<CrudService<T>> generateAutowiredServiceType(Class<T> type);

    static ServiceTypeGenerator getInstance(GenerationMode mode, String outputPackage, Object... args) {
        switch (mode) {
            case BYTECODE:
                return new BytecodeServiceTypeGenerator(outputPackage);
            case SOURCE_CODE:
                return new SourceCodeServiceTypeGenerator(
                        outputPackage, (String) args[0]);
            default:
                throw new IllegalArgumentException();
        }
    }
}
