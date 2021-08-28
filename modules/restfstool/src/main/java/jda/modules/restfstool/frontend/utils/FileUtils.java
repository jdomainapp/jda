package jda.modules.restfstool.frontend.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

//import javax.json.JsonReader;
//import jakarta.json.Json;
//import jakarta.json.JsonObject;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;

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
    
    public static String separatorsToSystem(String res) {
        if (res==null) return null;
        if (File.separatorChar=='\\') {
            return res.replace('/', File.separatorChar);
        } else {
            return res.replace('\\', File.separatorChar);
        }
    }
    
//    /**
//     * @effects 
//     *  if fileName represents a valid Json file
//     *    read and return {@link JsonObject} representing it
//     *  else
//     *    throw {@link NotPossibleException}
//     * @version 5.4
//     */
//    public static JsonObject readJSonObjectFile(Class c, String fileName) throws NotPossibleException {
//      InputStream ins;
//      try {
//        ins = ToolkitIO.getFileInputStream(c, fileName);
//        JsonReader reader = Json.createReader(ins); 
//        JsonObject json = reader.readObject();
//        return json;
//      } catch (FileNotFoundException e) {
//        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, new Object[] {fileName}, e);
//      }
//
//    }
}
