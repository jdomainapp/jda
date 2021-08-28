package jda.modules.sccltool.test;


import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.sccltool.SCCGenTool;

public class SCCGenTest {

	@Test
	public void doTest() throws FileNotFoundException {
		  String domainName = "CourseMan";
//			String rootSrcPath="D:\\Thesis\\Ha\\domainapp-light_20180119_5.1-for-teaching-SS2-b4-update-ScopeDef\\modules\\swcl\\src/example/java";
		  Path rootSrcPath = ToolkitIO.getPath("/home","ducmle",
	        "projects","jda","modules","sccl","src", "test", "java");
	    String rootSrcPathStr = rootSrcPath.toString();
	    
			List<String> mccClsFQNs = new ArrayList<>();
			String mccClsFQN="org.jda.example.courseman.mccl.modules.ModuleAddress";
			mccClsFQNs.add(mccClsFQN);
			String moduleMainClsFQN="org.jda.example.courseman.ModuleMain";
			//du lieu dau vao cac truong appName, appLogo, appLanguage, orgName, orgAddress, orgLogo, orgAddress,
			//dsType,dsUrl, dsUser, dsPassword, dsConnectionType, securityIsEnable
			Map<String,Object> newTemplateData = new HashMap<>();
			SCCGenTool tool = new SCCGenTool(domainName, rootSrcPathStr, mccClsFQNs, moduleMainClsFQN,newTemplateData);
			Object result = tool.exec();
			
			System.out.println("Result: \n" + result);
	}

}
