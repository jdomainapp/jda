package jda.modules.restfstool.frontend;

import java.io.*;

import jda.modules.common.io.ToolkitIO;
import jda.modules.restfstool.config.RFSGenConfig;
import jda.modules.restfstool.frontend.utils.FileUtils;

public class FERun extends Thread{
	String feProjPath = "";
	String feProjResource="";
	String feOutputPath="";
	String feProjName="";
	String demoReactPath="";
	
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
	
	public FERun(RFSGenConfig config) {
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
		String cmd2 = "mkdir src\\base && xcopy "+feProjResource+"\\base src\\base /e /i /h /y";
		String cmd3 = "mkdir src\\common && xcopy "+feProjResource+"\\common src\\common /e /i /h /y";
		String cmd4 = "xcopy "+feProjResource+"\\package.json "+demoReactPath+" /y";
		String cmd5 = "xcopy "+feOutputPath+" "+ demoReactPath +"\\src /e /i /h /y";
		String cmd6 = "npm install";
		String cmd7 = "npm start";
	
		
		boolean result = ToolkitIO.executeBashCommand(new File(feProjPath), cmd1);
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd2);
		}

		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd3);
		}
		
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd4);
		}
		
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd5);
		}
		
		ToolkitIO.writeTextFile(new File(indexPath), indexFileContent, true);
		
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd6);
		}
		
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd7);
		}
		
		if(result) {
			System.out.println("==============FINSH===========");
		}else {
			System.out.println("==============Error Run Front-End==============");
		}
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
		
		
		boolean result = ToolkitIO.executeBashCommand(new File(feProjPath), cmd1);
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd2);
		}

		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd3);
		}
		
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd4);
		}
		
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd5);
		}
		
		ToolkitIO.writeTextFile(new File(indexPath), indexFileContent, true);
		
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd6);
		}
		
		if(result) {
			result = ToolkitIO.executeBashCommand(new File(demoReactPath), cmd7);
		}
		
		if(result) {
			System.out.println("==============FINSH===========");
		}else {
			System.out.println("==============Error Run Front-End==============");
		}
	}
}
