package jda.modules.mosarfrontend.common;

import jda.modules.mosarfrontend.common.utils.NewMCC;

import org.modeshape.common.text.Inflector;

public class AngularSlotProperty {
    private static final Inflector inflector = Inflector.getInstance();
    private NewMCC mcc;
//    private final String mainAPI;
    private final String title;
    private final String moduleName;
//    private final String api;
    
    public AngularSlotProperty(NewMCC currentNewMCC) {
        this.mcc = currentNewMCC;
//        this.title = escapeQuotes(createTitle(currentNewMCC));
//        this.mainAPI = inflector.lowerCamelCase(viewDesc.getDomainClass().getName()).concat("API");
//        this.moduleName = viewDesc.getDomainClass().getName();
        this.moduleName = currentNewMCC.getModuleDescriptor().modelDesc().model().getSimpleName();
        this.title = this.mcc.getModuleDescriptor().viewDesc().formTitle();
//        this.mainAPI = inflector.lowerCamelCase(viewDesc.getDomainClass().getName()).concat("API");
//        System.out.print("mainAPI" + this.mainAPI);
//        this.api = createApi(MCC mcc);
    }
    
    public AngularSlotProperty(String moduleName) {
    	this.moduleName = moduleName;
    	this.title = moduleName;
    }
    
//    private static String createTitle(MCC mcc) {
//        return mcc.getPropertyVal("viewDesc", "formTitle").toString();
//    }
      
    private static String makeFileName(String backingClass) {
    	
    	return inflector.underscore(backingClass).replace("_", "-");
      }

    public String getFolder() {
        return inflector.underscore(
                        this.moduleName)
                .replace("_", "-");
    }

    public String getTitle() {
    	return this.title;
    }
    public String getModuleName() {
        return this.moduleName;
    }
    
    
    public String getAPI() {
    	return inflector.pluralize(
                inflector.underscore(
                        this.moduleName)
                .replace("_", "-"));
    }
    
    public String getMainImport() {
    	return "import {" + this.moduleName + "Component" + "} from " + getMainPath() + ";";
    }
    
    public String getFormImport() {
    	return "import {" + this.moduleName + "FormComponent" + "} from " + getFormPath() + ";";
    }
    
    public String getFormImportFull() {
    	return "import {" + this.moduleName + "FormComponent" + "} from " + getFormPathFull() + ";";
    }    
    
    private String getMainPath() {
    	return "'./" + makeFileName(this.moduleName) + "/" + makeFileName(this.moduleName) + ".component'";
    }
    
    public String getFormFileName() {
  	  return makeFileName(this.moduleName) + "-form";
    }
 
    private String getFormPath() {
    	return "'./" + getFormFileName() + "/" + getFormFileName() + ".component'";
      }
    
    private String getFormPathFull() {
    	return "'./" + makeFileName(this.moduleName) + "/"+ getFormFileName() + "/" + getFormFileName() + ".component'";
    }
    private static String makePlural(String original) {
        return inflector.pluralize(inflector.pluralize(inflector.underscore(original)))
                .replace("_", "-");
    }

    private static String escapeQuotes(String str) {
        return str.replace("\"", "").replace("'", "");
    }

//    private static String makeApiDeclaration(final String apiName) {
//        final String objName = apiName.replace("API", "");
//        return String.format("const %sAPI = new BaseAPI(\"%s\", providers.axios);\n",
//                objName, lowerFirstChar(makePlural(objName)));
//    }

    private static String lowerFirstChar(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }    

  
  public String getFileName() {
	return makeFileName(this.moduleName);
  }
  public String getSelector() {
    return "app-" + inflector.underscore(this.moduleName).replace("_", "-");
  }    
    

}
