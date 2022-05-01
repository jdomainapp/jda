package jda.modules.mosarfrontend.common.anotation.template_desc;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
public class AppTemplate {
    private String resource;
    private String templateRootFolder;
    private CrossTemplatesDesc crossTemplates;
    private ModuleTemplatesDesc moduleTemplates;
}
