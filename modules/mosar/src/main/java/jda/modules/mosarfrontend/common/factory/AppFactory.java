package jda.modules.mosarfrontend.common.factory;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.utils.RFSGenTk;
import jda.modules.mosarfrontend.common.anotation.AppTemplate;
import jda.modules.mosarfrontend.common.anotation.AppTemplateDesc;
import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Data
public class AppFactory {
    @NonNull
    private Class<?> AppTemplateCls;
    @NonNull
    private RFSGenConfig rfsGenConfig;


    public void genAndSave() {
        if (this.AppTemplateCls.isAnnotationPresent(AppTemplateDesc.class)) {
            ParamsFactory.getInstance().setRFSGenConfig(rfsGenConfig);

            AppTemplateDesc ano = this.AppTemplateCls.getAnnotation(AppTemplateDesc.class);
            AppTemplate appTemplate = new AppTemplate();
            RFSGenTk.parseAnnotation2Config(ano, appTemplate);
            String templateFolder = appTemplate.getTemplateRootFolder();

            for (Class<?> fileTemplateDesc : appTemplate.getFileTemplates()) {
                try {
                    (new FileFactory(fileTemplateDesc, rfsGenConfig.getFeOutputPath(), templateFolder))
                            .genAndSave();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Class<?> moduleTemplateDesc : appTemplate.getModuleTemplates()) {
                try {
                    for (Class<?> module : rfsGenConfig.getMCCFuncs()) {
                        ParamsFactory.getInstance().setCurrentModule(module);
                        (new FileFactory(moduleTemplateDesc, rfsGenConfig.getFeOutputPath(), templateFolder))
                                .genAndSave();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
