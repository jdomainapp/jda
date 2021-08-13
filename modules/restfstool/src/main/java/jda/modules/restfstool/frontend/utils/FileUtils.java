package jda.modules.restfstool.frontend.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static String readWholeFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String readAndAppendContent(String keyLocation, String content, String filePath) {
    	try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
                
                if(line.trim().equals(keyLocation.trim())) {
                	stringBuilder.append(content).append("\n");
                }
            }
            return stringBuilder.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
	}
    
    public static boolean writeFile(String content, boolean isOverwrite, String filePath) {
    	try {
    	    FileWriter fw = new FileWriter(filePath, isOverwrite);
    	    fw.write(content);
    	    fw.close();
    	    return true;
    	} catch (IOException e) {
    		 throw new RuntimeException(e);
    	}   
    }
    
    public static String separatorsToSystem(String res) {
        if (res==null) return null;
        if (File.separatorChar=='\\') {
            return res.replace('/', File.separatorChar);
        } else {
            return res.replace('\\', File.separatorChar);
        }
    }
}
