package jda.modules.mosarfrontend.common.anotation;

import lombok.Data;

public class AppTemplate {
    private Class<?>[] fileTemplates;
    private Class<?>[] moduleTemplates;
    private String resource;
    private String templateRootFolder;

    public Class<?>[] getFileTemplates() {
        return fileTemplates;
    }

    public void setFileTemplates(Class<?>[] fileTemplates) {
        this.fileTemplates = fileTemplates;
    }

    public Class<?>[] getModuleTemplates() {
        return moduleTemplates;
    }

    public void setModuleTemplates(Class<?>[] moduleTemplates) {
        this.moduleTemplates = moduleTemplates;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getTemplateRootFolder() {
        return templateRootFolder;
    }

    public void setTemplateRootFolder(String templateRootFolder) {
        this.templateRootFolder = templateRootFolder;
    }
}
