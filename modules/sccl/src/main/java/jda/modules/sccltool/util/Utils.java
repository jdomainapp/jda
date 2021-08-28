package jda.modules.sccltool.util;

import java.io.File;
import java.io.FileFilter;

public class Utils {
	public static File[] listFileInFolder(String filePath,  String pattern) {
		File folder = new File(filePath);
		File[] fileList;
			fileList = folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File file) {
						if(file.getName().matches(pattern)) {
							return true;
						}
						
					return false;
				}
			});
		
		return fileList;
	}

}
