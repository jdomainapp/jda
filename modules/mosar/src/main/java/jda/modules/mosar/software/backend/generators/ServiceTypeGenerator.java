package jda.modules.mosar.software.backend.generators;

import jda.modules.mosar.backend.base.services.CrudService;
import jda.modules.mosar.config.GenerationMode;
import jda.modules.mosar.config.RFSGenConfig;

public interface ServiceTypeGenerator {
  /**
   * 
   * @effects 
   *  generate the source code file for a suitable service class for <code>type</code>. 
   *  
   *  <p>If <code>config</code> means to compile then 
   *    compile the source code file and return the class object
   *  else
   *    return null
   *  @author Duc Minh Le  
   */
    <T> Class<CrudService<T>> generateAutowiredServiceType(Class<T> type, RFSGenConfig config);

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
