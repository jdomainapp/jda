package jda.modules.mosarfrontend.common.factory;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.utils.RFSGenTk;
import jda.modules.mosarfrontend.common.anotation.AppTemplate;
import jda.modules.mosarfrontend.common.anotation.AppTemplateDesc;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Map;

@Data
public class AppGenerator {
    @NonNull
    private Class<?> AppTemplateCls;
    @NonNull
    private GenConfig genConfig;

    private ArrayList<FileGenerator> fileGenerators = new ArrayList<>();

    private void initAppTemplateConfig() {
        if (this.AppTemplateCls.isAnnotationPresent(AppTemplateDesc.class)) {
            AppTemplateDesc ano = this.AppTemplateCls.getAnnotation(AppTemplateDesc.class);
            AppTemplate appTemplate = new AppTemplate();
            RFSGenTk.parseAnnotation2Config(ano, appTemplate);
            this.genConfig.setTemplateRootFolder(appTemplate.getTemplateRootFolder());
            for (Class<?> fileTemplateDesc : appTemplate.getFileTemplates()) {
                try {
                    this.fileGenerators.add(new FileGenerator(fileTemplateDesc, genConfig));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Class<?> moduleTemplateDesc : appTemplate.getModuleTemplates()) {
                try {
                    for (Map.Entry<Class, MCC> entry : genConfig.getModelModuleMap().entrySet()) {
                        FileGenerator fileGenerator = new FileGenerator(moduleTemplateDesc, genConfig);
                        fileGenerator.setModuleClass(entry.getKey());
                        this.fileGenerators.add(fileGenerator);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void gen() {
        initAppTemplateConfig();
        for (FileGenerator fileGenerator : this.fileGenerators) {
            try {
                fileGenerator.genAndSave();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
