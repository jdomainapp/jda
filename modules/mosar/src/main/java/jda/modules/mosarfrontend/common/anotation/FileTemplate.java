package jda.modules.mosarfrontend.common.anotation;

import lombok.Data;

@Data
public class FileTemplate {
    private String templateFile;

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }
}
