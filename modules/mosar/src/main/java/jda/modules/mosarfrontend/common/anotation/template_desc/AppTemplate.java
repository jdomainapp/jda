package jda.modules.mosarfrontend.common.anotation.template_desc;

import lombok.Data;

@Data
public class AppTemplate {
	private String resource;
	private String templateRootFolder;
	private CrossTemplatesDesc crossTemplates;
	private ModuleTemplatesDesc moduleTemplates;

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

	public CrossTemplatesDesc getCrossTemplates() {
		return crossTemplates;
	}

	public void setCrossTemplates(CrossTemplatesDesc crossTemplates) {
		this.crossTemplates = crossTemplates;
	}

	public ModuleTemplatesDesc getModuleTemplates() {
		return moduleTemplates;
	}

	public void setModuleTemplates(ModuleTemplatesDesc moduleTemplates) {
		this.moduleTemplates = moduleTemplates;
	}

}
