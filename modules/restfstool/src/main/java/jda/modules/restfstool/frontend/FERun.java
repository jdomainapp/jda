package jda.modules.restfstool.frontend;

import java.io.File;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import jda.modules.common.io.ToolkitIO;
import jda.modules.restfstool.config.RFSGenConfig;
import jda.modules.restfstool.frontend.utils.FileUtils;

/**
 * 
 * @overview 
 *
 * @author 
 * Ha Thanh Vu<br>
 * Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public class FERun extends Thread{
	private String feProjPath = "";
	private String feProjResource="";
	private String feOutputPath="";
	private String feProjName="";
	private String demoReactPath="";
	
	String indexFileContent = "<!DOCTYPE html>\r\n"
			+ "<html lang=\"en\">\r\n"
			+ "  <head>\r\n"
			+ "    <meta charset=\"utf-8\" />\r\n"
			+ "    <link rel=\"icon\" href=\"%PUBLIC_URL%/favicon.ico\" />\r\n"
			+ "	<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css\" integrity=\"sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2\" crossorigin=\"anonymous\">\r\n"
			+ "\r\n"
			+ "	<script src=\"https://code.jquery.com/jquery-3.5.1.slim.min.js\" integrity=\"sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj\" crossorigin=\"anonymous\"></script>\r\n"
			+ "	<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-ho+j7jyWK8fNQe+A12Hb8AhRq26LrZ/JpcUGGOn+Y7RsweNrtN/tE3MoK7ZeZDyx\" crossorigin=\"anonymous\"></script>\r\n"
			+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\r\n"
			+ "    <meta name=\"theme-color\" content=\"#000000\" />\r\n"
			+ "    <meta\r\n"
			+ "      name=\"description\"\r\n"
			+ "      content=\"Web site created using create-react-app\"/>\r\n"
			+ "    <link rel=\"apple-touch-icon\" href=\"%PUBLIC_URL%/logo192.png\" />\r\n"
			+ "\r\n"
			+ "    <link rel=\"manifest\" href=\"%PUBLIC_URL%/manifest.json\" />\r\n"
			+ "\r\n"
			+ "    <title>React App</title>\r\n"
			+ "  </head>\r\n"
			+ "  <body>\r\n"
			+ "    <noscript>You need to enable JavaScript to run this app.</noscript>\r\n"
			+ "    <div id=\"root\"></div>\r\n"
			+ "\r\n"
			+ "  </body>\r\n"
			+ "</html>";
	
  private static Logger logger = (Logger) LoggerFactory.getLogger("module.restfstool");

	public FERun(RFSGenConfig config) {
	  // TODO: (ducmle) improve this to:
	  // 1. use user.profile directory for the feParentProjPath (maven dir is not available for the case of jar distribution)
	  // 2. read and copy resources for the case of jar-file distribution 
	  
		String feParentProjPath = ToolkitIO.getMavenProjectRootPath(FERun.class, true);
		feProjPath = FileUtils.separatorsToSystem(config.getFeProjPath());
		if(feProjPath.isEmpty()) {
			feProjPath = feParentProjPath;
		}
		feOutputPath = feParentProjPath+File.separator+ FileUtils.separatorsToSystem(config.getFeOutputPath());
		feProjResource = feParentProjPath+File.separator+ FileUtils.separatorsToSystem(config.getFeProjResource());
		feProjName = config.getFeProjName();
		demoReactPath = feProjPath+ File.separator+ feProjName;
	};

	public void run() {
		if (File.separatorChar=='\\') {
			runFEInWin();
		}else {
			runFEInLinux();
		}
	}
	
	public void runFEInWin() {
		String indexPath=demoReactPath+"\\public\\index.html";
	
		String cmd1 = "npx create-react-app "+feProjName;
		String cmd2 = "xcopy "+feProjResource+"\\base src\\base /i /h /y";
		String cmd3 = "xcopy "+feProjResource+"\\common src\\common /i /h /y";
		String cmd4 = "xcopy "+feProjResource+"\\package.json "+demoReactPath+" /y";
		String cmd5 = "xcopy "+feOutputPath+" "+ demoReactPath +"\\src /e /i /h /y";
		String cmd6 = "npm install";
		String cmd7 = "npm start";
	
    runFECmds(indexPath, cmd1, cmd2, cmd3, cmd4, cmd5, cmd6, cmd7);
	}
	
	public void runFEInLinux() {
		String indexPath=demoReactPath+"/public/index.html";
		
		String cmd1 = "npx create-react-app "+feProjName;
		String cmd2 = "cp -rf "+feProjResource+"/base src/";
		String cmd3 = "cp -rf "+feProjResource+"/common src/";
		String cmd4 = "cp -f "+feProjResource+"/package.json .";
		String cmd5 = "cp -rf "+feOutputPath+"/* src/";
		String cmd6 = "npm install";
		String cmd7 = "npm start";
		
    runFECmds(indexPath, cmd1, cmd2, cmd3, cmd4, cmd5, cmd6, cmd7);
	}

	/**
	 * @requires cmds.length = 7
	 * @effects 
	 */
	 public void runFECmds(String indexPath, String...cmds) {
    File feProjDir = new File(feProjPath);
    File demoReactDir = new File(demoReactPath);
    
    boolean result = true;
    // if demoReactDir exists then do not run first command
    if (!demoReactDir.exists() || !ToolkitIO.dirContains(demoReactDir, "package.json")) {
      result = ToolkitIO.executeBashCommand(feProjDir, cmds[0]);
    }
    
    if(result) {
      result = ToolkitIO.executeBashCommand(demoReactDir, cmds[1]);
    }
  
    if(result) {
      result = ToolkitIO.executeBashCommand(demoReactDir, cmds[2]);
    }
    
    if(result) {
      result = ToolkitIO.executeBashCommand(demoReactDir, cmds[3]);
    }
    
    if(result) {
      result = ToolkitIO.executeBashCommand(demoReactDir, cmds[4]);
    }
    
    ToolkitIO.writeTextFile(new File(indexPath), indexFileContent, true);
    
    if(result) {
      result = ToolkitIO.executeBashCommand(demoReactDir, cmds[5]);
    }
    
    if(result) {
      result = ToolkitIO.executeBashCommand(demoReactDir, cmds[6]);
    }
    
    if(result) {
      logger.info("==============FINSH FERun ===========");
    }else {
      logger.error("Error Run Front-End");
    }
  }

//	  public void runFEInWin() {
//	    String indexPath=demoReactPath+"\\public\\index.html";
//	  
//	    File feProjDir = new File(feProjPath);
//	    File demoReactDir = new File(demoReactPath);
//	    
//	    String cmd1 = "npx create-react-app "+feProjName;
//	    String cmd2 = "mkdir src\\base && xcopy "+feProjResource+"\\base src\\base /e /i /h /y";
//	    String cmd3 = "mkdir src\\common && xcopy "+feProjResource+"\\common src\\common /e /i /h /y";
//	    String cmd4 = "xcopy "+feProjResource+"\\package.json "+demoReactPath+" /y";
//	    String cmd5 = "xcopy "+feOutputPath+" "+ demoReactPath +"\\src /e /i /h /y";
//	    String cmd6 = "npm install";
//	    String cmd7 = "npm start";
//	  
//	    
//	    boolean result = ToolkitIO.executeBashCommand(feProjDir, cmd1);
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd2);
//	    }
//
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd3);
//	    }
//	    
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd4);
//	    }
//	    
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd5);
//	    }
//	    
//	    ToolkitIO.writeTextFile(new File(indexPath), indexFileContent, true);
//	    
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd6);
//	    }
//	    
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd7);
//	    }
//	    
//	    if(result) {
//	      System.out.println("==============FINSH===========");
//	    }else {
//	      System.out.println("==============Error Run Front-End==============");
//	    }
//	  }
//	  
//	  
//	  public void runFEInLinux() {
//	    String indexPath=demoReactPath+"/public/index.html";
//	    
//
//	    String cmd1 = "npx create-react-app "+feProjName;
//	    String cmd2 = "cp -rf "+feProjResource+"/base src/";
//	    String cmd3 = "cp -rf "+feProjResource+"/common src/";
//	    String cmd4 = "cp -f "+feProjResource+"/package.json .";
//	    String cmd5 = "cp -rf "+feOutputPath+"/* src/";
//	    String cmd6 = "npm install";
//	    String cmd7 = "npm start";
//	    
//	    File feProjDir = new File(feProjPath);
//	    File demoReactDir = new File(demoReactPath);
//	    
//	    boolean result = ToolkitIO.executeBashCommand(new File(feProjPath), cmd1);
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd2);
//	    }
//
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd3);
//	    }
//	    
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd4);
//	    }
//	    
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd5);
//	    }
//	    
//	    ToolkitIO.writeTextFile(new File(indexPath), indexFileContent, true);
//	    
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd6);
//	    }
//	    
//	    if(result) {
//	      result = ToolkitIO.executeBashCommand(demoReactDir, cmd7);
//	    }
//	    
//	    if(result) {
//	      System.out.println("==============FINSH===========");
//	    }else {
//	      System.out.println("==============Error Run Front-End==============");
//	    }
//	  }
}
