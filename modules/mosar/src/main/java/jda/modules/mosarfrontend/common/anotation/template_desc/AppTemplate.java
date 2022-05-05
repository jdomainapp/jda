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
	public String getTemplateRootFolder() {
		// TODO Auto-generated method stub
		return this.templateRootFolder;
	}
	public String getResource() {
		// TODO Auto-generated method stub
		return this.resource;
	}
	public CrossTemplatesDesc getCrossTemplates() {
		// TODO Auto-generated method stub
		return this.getCrossTemplates();
	}
	public ModuleTemplatesDesc getModuleTemplates() {
		// TODO Auto-generated method stub
		return this.moduleTemplates;
	}
}
