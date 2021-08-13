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
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("windows")) {
			runFEInWin();
		}else {
			runFEInLinux();
		}
	}
	
	public void runFEInWin() {
		String indexPath=demoReactPath+"\\public\\index.html";
		
		String cmd1 ="cd /d "+feProjPath; 
		String cmd2 = "npx create-react-app "+feProjName;
		String cmd3 = "mkdir src\\base && xcopy "+feProjResource+"\\base src\\base /e /i /h /y";
		String cmd4 = "mkdir src\\common && xcopy "+feProjResource+"\\common src\\common /e /i /h /y";
		String cmd5 = "xcopy "+feProjResource+"\\package.json "+demoReactPath+" /y";
		String cmd6 = "xcopy "+feOutputPath+" "+ demoReactPath +"\\src /e /i /h /y";
		String cmd7 = "npm install";
		String cmd8 = "npm start";
		
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
		
		boolean result = executeCommandInWin(cmd1, null);
		if(result) {
			result = executeCommandInWin(cmd2, null);
		}

		if(result) {
			result = executeCommandInWin(cmd3, demoReactPath);
		}
		
		if(result) {
			result = executeCommandInWin(cmd4, demoReactPath);
		}
		
		if(result) {
			result = executeCommandInWin(cmd5, null);
		}
		if(result) {
			result = executeCommandInWin(cmd6, null);
		}
		
		result= FileUtils.writeFile(indexFileContent, false, indexPath);
		
		if(result) {
			result = executeCommandInWin(cmd7, demoReactPath);
		}
		
		if(result) {
			result = executeCommandInWin(cmd8, demoReactPath);
		}
		
		if(result) {
			System.out.println("==============FINSH===========");
		}else {
			System.out.println("==============Error Run Front-End==============");
		}
	}
	
	private boolean executeCommandInWin(String command, String directory) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder();
			if(directory!=null) {
				processBuilder.command("cmd", "/c", command).directory(new File(directory));
			}else {
				processBuilder.command("cmd", "/c", command);
			}
			Process process = processBuilder.start();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String line = "";
		    while ((line = reader.readLine()) != null) {
		        System.out.println(line);
		    }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void runFEInLinux() {
		String indexPath=demoReactPath+"/public/index.html";
		
		String cmd1 = "cd "+feProjPath;
		String cmd2 = "npx create-react-app "+feProjName;
		String cmd3 = "cd "+feProjName;
		String cmd4 = "cp -r "+feProjResource+"/base "+feProjResource+"/common src/";
		String cmd5 = "cp -f "+feProjResource+"/package.json .";
		String cmd6 = "cp -r "+feOutputPath+"/* src/";
		String cmd7 = "npm install";
		String cmd8 = "npm start";
		
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
		
		boolean result = executeCommandInLinux(cmd1);
		if(result) {
			result = executeCommandInLinux(cmd2);
		}

		if(result) {
			result = executeCommandInLinux(cmd3);
		}
		
		if(result) {
			result = executeCommandInLinux(cmd4);
		}
		
		if(result) {
			result = executeCommandInLinux(cmd5);
		}
		if(result) {
			result = executeCommandInLinux(cmd6);
		}
		
		if(result) {
			result = executeCommandInLinux(cmd7);
		}
		
		result= FileUtils.writeFile(indexFileContent, false, indexPath);
		
		if(result) {
			result = executeCommandInLinux(cmd8);
		}
		
		if(result) {
			System.out.println("==============FINSH===========");
		}else {
			System.out.println("==============Error Run Front-End==============");
		}
	}
	
	private boolean executeCommandInLinux(String command) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command("bash", "-c", command);
			Process process = processBuilder.start();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String line = "";
		    while ((line = reader.readLine()) != null) {
		        System.out.println(line);
		    }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
