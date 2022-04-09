package jda.modules.mosarfrontend.common.anotation;

import lombok.Data;

@Data
public class AppTemplate {
    private Class<?>[] fileTemplates;
    private Class<?>[] moduleTemplates;
    private String resource;
    private String templateRootFolder;
}
