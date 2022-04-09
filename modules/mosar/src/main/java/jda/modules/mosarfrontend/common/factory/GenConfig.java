package jda.modules.mosarfrontend.common.factory;

import jda.modules.mccl.conceptualmodel.MCC;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;

@Data
public class GenConfig {
    @NonNull
    private Class sccCls;
    @NonNull
    private MCC mainMCC;
    @NonNull
    private Map<Class, MCC> modelModuleMap;
    @NonNull
    private String outputFolder;
    private String templateRootFolder;
}
