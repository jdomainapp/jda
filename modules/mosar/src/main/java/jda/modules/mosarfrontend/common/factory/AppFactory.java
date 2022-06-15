package jda.modules.mosarfrontend.common.factory;

import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosarfrontend.angular.AngularAppTemplate;
import jda.modules.mosarfrontend.common.anotation.template_desc.*;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.reactnative.ReactNativeAppTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppFactory {
    private RFSGenConfig rfsGenConfig;

    public AppFactory(RFSGenConfig rfsGenConfig) {
        this.rfsGenConfig = rfsGenConfig;
    }

    public void unzip(String zipFilePath, String outputPath) {
        try {
            // Open the zip file
            ZipFile zipFile = new ZipFile(zipFilePath);
            Enumeration<?> enu = zipFile.entries();
            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enu.nextElement();

                String name = zipEntry.getName();
                long size = zipEntry.getSize();
                long compressedSize = zipEntry.getCompressedSize();
//                System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n",
//                        name, size, compressedSize);

                // Do we need to create a directory ?
                File file = new File(outputPath + "/" + name);
                if (name.endsWith("/")) {
                    file.mkdirs();
                    continue;
                }

                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                // Extract the file
                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                is.close();
                fos.close();

            }
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        public void gen(Class<?> genClass) throws Exception;
    }

    private void loopGenMethod(Annotation genDesc, Callback callback) {
        Method[] genMethods = genDesc.annotationType().getDeclaredMethods();
        
        for (Method m : genMethods) {
        	
            try {
                ComponentGenDesc componentGenDesc = (ComponentGenDesc) m.invoke(genDesc, (new ArrayList<Object>()).toArray());
                for (Class<?> fileTemplateDesc : componentGenDesc.genClasses()) {
//                	System.out.println(fileTemplateDesc);
                    try {
                        callback.gen(fileTemplateDesc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }

    public void genAndSave() {
        if (this.rfsGenConfig.getFeTemplate() == null) {
            // use default Template
            switch (this.rfsGenConfig.getFePlatform()) {
                case REACT:
                case ANGULAR:
                    this.rfsGenConfig.setFeTemplate(AngularAppTemplate.class.getAnnotation(AppTemplateDesc.class));
                case REACT_NATIVE:
                    this.rfsGenConfig.setFeTemplate(ReactNativeAppTemplate.class.getAnnotation(AppTemplateDesc.class));
                case VUE_JS:
            }
        }
        if (this.rfsGenConfig.getFeTemplate() != null) {
            String[] appDomains = ParamsFactory.getInstance().setRFSGenConfig(rfsGenConfig);
            AppTemplateDesc appTemplate = this.rfsGenConfig.getFeTemplate();

            String templateFolder = appTemplate.templateRootFolder();
            // TODO: Clean output folder before gen/
            /** Copy resource to output*/
            unzip(appTemplate.resource(), rfsGenConfig.getFeOutputPath());


            /** Các Component chỉ gen 1 lần*/
            CrossTemplatesDesc crossTemplatesDesc = appTemplate.crossTemplates();
            loopGenMethod(crossTemplatesDesc, (genClass) -> {
                (new FileFactory(genClass, rfsGenConfig.getFeOutputPath(), templateFolder))
                        .genAndSave();
            });


            for (String domain : appDomains) {
                ParamsFactory.getInstance().setCurrentModule(domain);
                /**
                 * Các file gen với mỗi miền (module in domain model) , Ex: Student, Class in CourseMan example
                 */
                ModuleTemplatesDesc moduleTemplatesDesc = appTemplate.moduleTemplates();
                
                loopGenMethod(moduleTemplatesDesc, (genClass) -> {
                    (new FileFactory(genClass, rfsGenConfig.getFeOutputPath(), templateFolder))
                            .genAndSave();

                    /**
                     * Các file gen với mỗi field trong miền (module in domain model) , Ex: Student, Class in CourseMan example
                     */
                    for (DField field : ParamsFactory.getInstance().getModuleFields()) {
                        ParamsFactory.getInstance().setCurrentModuleField(field);
                        ModuleFieldTemplateDesc moduleFieldTemplateDesc = appTemplate.moduleFieldTemplates();
                        loopGenMethod(moduleFieldTemplateDesc, (genClassForField -> {
                            (new FileFactory(genClassForField, rfsGenConfig.getFeOutputPath(), templateFolder))
                                    .genAndSave();
                        }));
                    }
                    /**
                     * Các file gen với mỗi sub domain. Ex: CompulsoryCourseModule vs ElectiveCourseModule
                     */
                    for (String subDomain : ParamsFactory.getInstance().getMCC().getSubDomains().keySet()) {
                        ParamsFactory.getInstance().setCurrentSubDomain(subDomain);
                        SubModuleTemplateDesc subModuleTemplateDesc = appTemplate.subModuleTemplates();
                        loopGenMethod(subModuleTemplateDesc, (genClassForSubDomain -> {
                            (new FileFactory(genClassForSubDomain, rfsGenConfig.getFeOutputPath(), templateFolder))
                                    .genAndSave();
                        }));
                    }
                    ParamsFactory.getInstance().setCurrentSubDomain(null);

                });


            }
        }
    }
}
