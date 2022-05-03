package jda.modules.mosarfrontend.common.factory;

import jda.modules.mosar.utils.RFSGenTk;
import jda.modules.mosarfrontend.angular.AngularAppGenerator;
import jda.modules.mosarfrontend.common.FEAppGen;
import jda.modules.mosarfrontend.common.anotation.AppTemplate;
import jda.modules.mosarfrontend.common.anotation.AppTemplateDesc;
import jda.modules.mosarfrontend.reactnative.ReactNativeAppGenerator;
import lombok.Data;
import lombok.NonNull;

@Data
public class AppFactory {
    @NonNull
    private Class<?> AppTemplateCls;
    @NonNull
    private String outputFolder;
    @NonNull
    private Class<?>[] moduleClasses;

    public AppFactory(Class<AngularAppGenerator> class1, String projectSrcDir, Class[] array) {
		// TODO Auto-generated constructor stub
    	this.AppTemplateCls = class1;
    	this.outputFolder = projectSrcDir;
    	this.moduleClasses = array;
//    	System.out.print(this.AppTemplateCls.toString());
	}

	public void genAnSave() {
		
        if (this.AppTemplateCls.isAnnotationPresent(AppTemplateDesc.class)) {
        	
            AppTemplateDesc ano = this.AppTemplateCls.getAnnotation(AppTemplateDesc.class);
            AppTemplate appTemplate = new AppTemplate();
            RFSGenTk.parseAnnotation2Config(ano, appTemplate);
            String templateFolder = appTemplate.getTemplateRootFolder();
//            System.out.print(templateFolder);
            for (Class<?> fileTemplateDesc : appTemplate.getFileTemplates()) {
                try {
                    (new FileFactory(fileTemplateDesc, outputFolder, templateFolder)).genAndSave();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Class<?> moduleTemplateDesc : appTemplate.getModuleTemplates()) {
//            	System.out.print("Output: " + outputFolder);
                try {
                    for (Class<?> moduleCls : moduleClasses) {
                        ParamsFactory.getInstance().setCurrentModuleCls(moduleCls);
                        (new FileFactory(moduleTemplateDesc, outputFolder, templateFolder)).genAndSave();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } 
//        else {
//        	System.out.print("Lỗi 1");
//        }
    }
}
