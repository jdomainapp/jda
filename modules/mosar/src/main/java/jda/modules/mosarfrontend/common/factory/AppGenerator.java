package jda.modules.mosarfrontend.common.factory;

import jda.modules.mosar.utils.RFSGenTk;
import jda.modules.mosarfrontend.common.anotation.AppTemplate;
import jda.modules.mosarfrontend.common.anotation.AppTemplateDesc;
import lombok.Data;
import lombok.NonNull;

@Data
public class AppGenerator {
    @NonNull
    private Class<?> AppTemplateCls;
    @NonNull
    private String outputFolder;
    @NonNull
    private Class<?>[] moduleClasses;

    public void genAnSave() {
        if (this.AppTemplateCls.isAnnotationPresent(AppTemplateDesc.class)) {
            AppTemplateDesc ano = this.AppTemplateCls.getAnnotation(AppTemplateDesc.class);
            AppTemplate appTemplate = new AppTemplate();
            RFSGenTk.parseAnnotation2Config(ano, appTemplate);
            String templateFolder = appTemplate.getTemplateRootFolder();
            for (Class<?> fileTemplateDesc : appTemplate.getFileTemplates()) {
                try {
                    (new FileGenerator(fileTemplateDesc, outputFolder, templateFolder)).genAndSave();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Class<?> moduleTemplateDesc : appTemplate.getModuleTemplates()) {
                try {
                    for (Class<?> moduleCls : moduleClasses) {
                        ParamsFactory.getInstance().setCurrentModuleCls(moduleCls);
                        (new FileGenerator(moduleTemplateDesc, outputFolder, templateFolder)).genAndSave();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
