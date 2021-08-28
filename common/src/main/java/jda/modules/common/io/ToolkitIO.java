package jda.modules.common.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple2;

/**
 * @overview 
 *  A tool kit for performing shared I/O tasks.  
 *  
 * @author dmle
 */
public class ToolkitIO {
  public static final String fileSep = File.separator;
  public static final String jarFileSep = "/";

  /** pseudo file separator that abstracts from platform-dependent {@link #fileSep} for 
   * the purpose of generating {@link FilePath} */
  public static final String filePseudoSep = "|";

  public static final boolean debug = Toolkit.getDebug(ToolkitIO.class);
  public static final String FILE_URL_PREFIX = "file:";
  public static final String FILE_TEMP_EXT = ".tmp";
  public static final String FILE_JAVA_EXT = ".java";
  
  public static final String ENCODE_UTF8 = "UTF-8";
  
  /** name filter for all .java files */
  private static final FilenameFilter JavaFileFilter = 
      new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return name.endsWith(FILE_JAVA_EXT);
    }
  };
  
  /** A {@link Scanner} that wraps out System.in */
  private static Scanner InputScanner;

  
  private ToolkitIO() {}

  /**
   * @effects 
   *  write <tt>o</tt> to a file whose absolute path is <tt>filePath</tt>.
   *  If file does not exist then it is created first. 
   */
  public static File writeObject(String filePath, Serializable o) 
  throws NotFoundException, NotPossibleException {
    File file = new File(filePath);

    // create file if not exists
    if (!file.exists()) {
      boolean created;
      try {
        created = file.createNewFile();
        if (!created)
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_FILE, new Object[] {"filePath"});
      } catch (IOException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_FILE, e, new Object[] {"filePath"});
      }
    }
    
    FileOutputStream fout;
    ObjectOutputStream oout = null;
    try {
      fout = new FileOutputStream(file);
      oout = new ObjectOutputStream(fout);
    } catch (FileNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, e, new Object[] {filePath});
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, e, new Object[] {filePath});
    }
    
    try {
      oout.writeObject(o);
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WRITE_OBJECT, e, new Object[] {o});
    } finally {
      try {
        oout.close();
      } catch (IOException e) {
        // ignore 
      }
    }
    
    return file;
  }

  /**
   * @effects 
   *  read an object of <tt>c</tt> from the file whose absolute path is <tt>filePath</tt>
   *  <p>Throws ClassNotFoundException 
   */
  public static <T> T readObject(String filePath, Class<T> c) 
      throws NotFoundException, NotPossibleException {
    
    FileInputStream fin = null;
    ObjectInputStream oin = null;
    try {
      fin = new FileInputStream(filePath);
      oin = new ObjectInputStream(fin);
      
    } catch (FileNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, e, new Object[] {filePath});
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, e, new Object[] {filePath});
    }
    
    Object o;
    try {
      o = oin.readObject();
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new Object[] {c.getSimpleName()});
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_OBJECT, e, new Object[] {c.getSimpleName()});
    } finally {
      try {
        oin.close();
      } catch (IOException e) {
        // ignore 
      }
    }

    if (o != null && !c.isInstance(o)) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_OBJECT, new Object[] {c.getSimpleName()});
    } else {
      return (T) o;
    }
  }

  /**
   * This method is capable of both reading file from a jar file and from the file system. 
   * 
   * @requires relativePathElements.length > 0
   * 
   * @effects 
   *  read and return {@link Properties} object from content of properties file whose 
   *  relative path elements are specified by <tt>relativePathElements</tt> that is relative to the source folder containing class <tt>c</tt>; 
   *  if the specified file does not exist or is empty return <tt>null</tt> 
   *  
   *  <p>throws NotPossibleException if failed to read the specified file
   * @version 
   * - 3.0 <br>
   * - 3.1: change parameters to support jar file path
   */
  public static Properties readPropertyFile(final Class c, 
      final String encoding, String...relativePathElements) throws NotPossibleException {
    
    if (c == null || relativePathElements == null || relativePathElements.length < 1) {
      return null;
    }
    
    StringBuffer propFilePathBuf = new StringBuffer();
    // construct suitable relative file path depending on whether we are reading from 
    // the file system or from a jar file
    String fileSep;
    if (isExistInJar(c, relativePathElements[0])) {
      // in a jar
      fileSep = "/";  // regardless of the host operating system
    } else {
      // from file system
      fileSep = File.separator; // depends on the host
    }
    
    for (String element : relativePathElements) {
      if (propFilePathBuf.length() == 0)
        propFilePathBuf.append(element);
      else
        propFilePathBuf.append(fileSep).append(element);        
    }
    
    String propFilePath = propFilePathBuf.toString();
    
    Properties props = null;
    InputStream fins = c.getResourceAsStream(propFilePath);
    if (fins != null) {
      // file exists
      try {
        props = new Properties();
        if (encoding != null) {
          Reader reader = 
              new InputStreamReader(fins, encoding);
          props.load(reader);
        } else {
          props.load(fins);
        }
      } catch (IOException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_LOAD_PROPERTIES, e, 
            new Object[] {c.getName() + "."+propFilePath});
      }
    }
    
    return props;
  }
  
  /**
   * @effects
   *  if resource file named <tt>resName</tt> is stored in the source folder of <tt>c</tt> and in a jar file
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1 
   */
  public static boolean isExistInJar(Class c, String resName) {
    // v3.2c: remove all '\' in resName by '/' before calling getResource
    final String backSlash = "\\";
    final String backSlashPattern = "\\\\";
    if (resName.indexOf(backSlash) > -1) {
      resName = resName.replaceAll(backSlashPattern, "/");
    }
    
    URL res = c.getResource(resName);
    
    boolean inJar = res != null && res.toString().startsWith("jar:");
    return inJar;
  }

  /**
   * @requires 
   *  propFilePath is the absolute path to a property file
   * @effects 
   *  read and return {@link Properties} object from content of properties file in  
   *    the file whose absolute path is <tt>propFilePath</tt> 
   *  if the specified file does not exist or is empty return <tt>null</tt> 
   *  
   *  <p>throws NotPossibleException if failed to read the specified file
   * @version 3.0
   */
  public static Properties readPropertyFile(final String propFilePath, final String encoding) throws NotPossibleException {
    
    Properties props = null;
    
    File file = new File(propFilePath);
    
    if (file.exists()) {
      // file exists
      try {
        FileInputStream fins = new FileInputStream(file);
        props = new Properties();
        if (encoding != null) {
          Reader reader = 
              new InputStreamReader(fins, encoding);
          props.load(reader);
        } else {
          props.load(fins);
        }
      } catch (IOException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_LOAD_PROPERTIES, e, 
            new Object[] {propFilePath});
      }
    }
    
    return props;
  }
  
  /**
   * @effects 
   *  return the absolute path to file or folder whose name is <tt>fileOrFolderName</tt>
   *  and is stored in the source folder of class <tt>c</tt>; 
   *  return <tt>null</tt> if the specified file or folder is not found
   */
  public static String getPath(Class c, 
      String fileOrFolderName) {
    
    URL fileURL = c.getResource(fileOrFolderName);
    
    if (fileURL == null)
      return null;
    else
      return fileURL.getPath();
  }
  
  /**
   * @effects 
   *  return the absolute path to a resource (file or folder) whose name is <tt>resName</tt>
   *  and is stored in the source folder of class <tt>c</tt>; 
   *  return <tt>null</tt> if the specified resName is not found.
   *  
   *  <p>
   *  <code>resName</code> can be a simple name (e.g. test.txt) or a relative path name in the parent folder (e.g. ../test.txt)
   * 
   */
  public static String getPathExt(Class c, 
      String resName) {
    if (resName == null) 
      return null;
    
    File pkgPath = getPackagePath(c);
    String simpleName;
    File path = null;
    if (resName.startsWith("..")) {
      // switch up to parent or ancestor folder
      String[] elements = resName.split("\\.\\.\\/");
      simpleName = elements[elements.length-1];
      int numLevels = elements.length - 1;
      // switch directory up numLevels
      path = pkgPath;
      for (int i = 0; i < numLevels; i++) {
        path = path.getParentFile();
      }
    } else {
      simpleName = resName;
      path = pkgPath;
    }
    
    String resPath = path.getPath() + File.separator + simpleName;
    File file = new File(resPath);
    if (file.exists()) {
      return resPath;
    } else {
      return null;
    }
  }


  /**
   * @requires pkgName is the Java package name that corresponds to the file
   * @effects 
   *  return the file system representation of the file located in the specified 
   *  package and has the extension <code>fileExt</code>
   * @version 5.4 
   */
  public static String getFilePath(String pkgName, String fileExt) {
    String[] pkg = pkgName.split("\\.");
    return String.join(File.separator, pkg) + 
        (fileExt.startsWith(".") ? fileExt : "." + fileExt);
  }
  
  /**
   * @effects 
   *  Rename <tt>file</tt> to have <tt>name</tt> (replace any existing file having that name); 
   *  return the new {@link File}. 
   *  
   *  <p>throws NotPossibleException if failed.
   * @version 3.2
   */
  public static File renameFile(java.io.File file, String name) {
    File target = new File(newFilePath(file.getParent(), name));
    try {
      Files.move(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
      
      return target;
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_COPY_FILE, e, new Object[] { file, target});
    }
  }
  
  /**
   * @requires
   *  path != null
   *  
   * @effects 
   *  if exists file whose <b>absolute path</b> is <tt>path</tt>
   *    return {@link File} object for it
   *  else
   *    return <tt>null</tt>
   * @version 3.2
   */
  public static File getFile(String path) {
    if (path == null)
      return null;
    
    File file = new File(path);
    
    if (file.exists()) {
      return file;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  read and return an {@link InputStream} to {@link File} whose name is <tt>fileName</tt>
   *  and is stored in the source folder of class <tt>c</tt> 
   *  
   * <p>throws FileNotFoundException if file is not found
   */
  public static InputStream getFileInputStream(Class c, String fileName) throws FileNotFoundException {

    URL fileURL = c.getResource(fileName);
    
    if (fileURL == null)
      throw new FileNotFoundException("File: " + fileName);
    
    InputStream ins = c.getResourceAsStream(fileName);

    return ins;
  }

  /**
   * @effects 
   *  If <code>filePath</code> exists
   *    return {@link InputStream} object representing it
   *  else
   *    throws {@link FileNotFoundException}
   * @version 5.4
   */
  public static InputStream getFileInputStream(String filePath) throws FileNotFoundException {
    return new FileInputStream(filePath);
  }
  
  /**
   * <b>IMPORTANT</b>: This method WORKS in both cases: (1) source class is stored in a file system 
   * and (2) source class is stored in a jar file. 
   * 
   * @requires 
   *  targetPath is an absolute path to a folder that exists
   *  
   * @effects 
   *  if exists the sub-directory named <tt>srcDirName</tt> relative to the 
   *    package directory of the class <tt>c</tt> 
   *    copy all files from that sub-directory in to the target directory <tt>target</tt>
   *    
   *  <p>if <tt>srcDirName</tt> is a sub-path (i.e. containing path separators) then replaces all 
   *  path separator chars by {@link #jarFileSep} before processing.
   *  
   *  <p>Throws NotPossibleException if failed for some reasons.
   *  
   * @version 3.1
   */
  public static void copyFiles(Class c, String srcDirName, String targetPath) throws NotPossibleException {
    try {
      // check if we are reading off a jar file or from file system
      if (isExistInJar(c, srcDirName)) {
        // in jar file
        
        // srcDirName is a sub-path (i.e. containing path separators) then replaces all 
        // path separator chars by {@link #jarFileSep} before processing.
        if (srcDirName.indexOf(fileSep) > -1) {
          // NOTE: not to use replaceAll !!!
          srcDirName = srcDirName.replace(fileSep, jarFileSep);
        }
        
        copyFilesFromJar(c, srcDirName, targetPath);
      } else {
        // not in a jar file: assumes file system
        copyFilesFromFileSystem(c, srcDirName, targetPath);
      }
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_COPY_DIR, e, 
          new Object[] {srcDirName, targetPath});
    }
  }

  /**
   * @requires 
   *  targetPath is an absolute path to a folder that exists
   *  
   * @effects 
   *  if exists the sub-directory named <tt>srcDirName</tt> relative to the 
   *    package directory of the class <tt>c</tt> 
   *    copy all files from that sub-directory in to the target directory <tt>target</tt>
   *  
   *  <p>Throws NotPossibleException if failed for some reasons.
   *  
   * @version 3.1
   */
  public static void copyFilesFromFileSystem(Class c, String srcDirName, String targetPath) throws NotPossibleException {
    InputStream fins;
    String destFile = null;

    // debug
    if (debug)
      System.out.printf("ToolkitIO.copyFilesFromFileSystem: %n   srcDirName: %s%n   class: %s%n   Target path: %s%n",
        srcDirName, c, targetPath);
    
    URL srcDirUrl = c.getResource(srcDirName);
    if (srcDirUrl != null) {
      // get all files and copy
      File srcDir = new File(srcDirUrl.getPath());
      File[] files = srcDir.listFiles();
      if (files != null && files.length > 0) {
        for (File file : files) {
          if (file.isDirectory()) // skip directories
            continue;

          try {
            fins = new FileInputStream(file);
            destFile = targetPath + fileSep + file.getName();
            copyFile(fins, destFile);
          } catch (IOException e) {
            throw new NotPossibleException(
                NotPossibleException.Code.FAIL_TO_COPY_FILE, e, new Object[] {
                    file, destFile });
          }
        }
      }
    }
  }
  
  /**
   * @requires 
   *  targetPath is an absolute path to a folder that exists
   *  
   * @effects 
   *  if exists the sub-directory named <tt>srcDirName</tt> relative to the 
   *    package directory of the class <tt>c</tt> <b>in a jar file</b>
   *    copy all files from that sub-directory in to the target directory <tt>target</tt>
   *  
   *  <p><tt>srcDirName</tt> can be a sub-path (i.e. containing path separators).
   *  
   *  <p>Throws IOException if failed for some reasons.
   *  
   * @version 3.1
   */
  public static void copyFilesFromJar(Class c, String srcDirName, String targetPath) throws IOException {
    // get the jar file containing c and read content off it
    File jarFile = new File(c.getProtectionDomain().getCodeSource()
        .getLocation().getPath());

    if (jarFile.isFile()) {
      // debug
      if (debug)
        System.out.printf("ToolkitIO.copyFilesFromJar: %n   srcDirName: %s%n   Jar file: %s%n   Target path: %s%n",
          srcDirName, jarFile, targetPath);
      
      String cname = c.getName();
      String cPackagePath;
      int lastDot = cname.lastIndexOf(".");
      if (lastDot > -1) {
        cPackagePath = cname.substring(0, lastDot);
        cPackagePath = cPackagePath.replaceAll("\\.","/");
        cPackagePath = cPackagePath + jarFileSep; 
      } else { 
        // no package
        cPackagePath = "";
      }
      
      if (debug) 
        System.out.println("   class package path: " + cPackagePath);
      
      String fodName, subPath, targetFile;
      JarEntry jarEntry;
      InputStream fins;
      
      if (!targetPath.endsWith(fileSep)) targetPath = targetPath + fileSep;

      JarFile jar = null;
      boolean isAtSrcDirLevel;
      
      try {
        jar = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jar.entries();
        final String srcFolderPath = cPackagePath + srcDirName + jarFileSep;

        // debug
        if (debug) System.out.printf("   srcFolderPath: %s%n", srcFolderPath);

        String targetFilePath;
        while (entries.hasMoreElements()) {
          jarEntry = entries.nextElement();
          fodName = jarEntry.getName();
          if (debug) System.out.println("..." + fodName);
          if (fodName.startsWith(srcFolderPath)) {
            // found entries in sourceFolder
            subPath = fodName.substring(cPackagePath.length());
            
            /* v3.2c: BUG: this does not work when srcDirName is a sub-path (e.g. view/styles)
            isAtSrcDirLevel = isSinglePathLevel(subPath, jarFileSep);
            */
            // whether or not jarEntry is actually an immediate child entry of the srcFolderPath  
            isAtSrcDirLevel = (fodName.lastIndexOf(jarFileSep) == srcFolderPath.lastIndexOf(jarFileSep));
            
            if (!jarEntry.isDirectory() && isAtSrcDirLevel) {
              // a file entry of the source folder: copy it to target
              fins = jar.getInputStream(jarEntry);

              targetFile = subPath.substring(subPath.
                  // v3.2c (above): indexOf(jarFileSep)+1
                  lastIndexOf(jarFileSep)+1);
              
              targetFilePath = targetPath + targetFile;
              
              // debug
              //System.out.printf("   copy file: %s%n", jarEntry);
              
              copyFile(fins, new File(targetFilePath));

              if (debug) System.out.printf("   -> copied to: %s%n", targetFilePath);
            }
          }
        }
      } catch (IOException e) {
        throw e;
      } finally {
        if (jar != null) jar.close();
      }
    } else {
      // not in a jar file
      throw new IOException("Jar file '"+jarFile+"' is not recognised as a valid file for class: " + c);
    }        
  }
  
  /**
   * @effects 
   *  if <tt>path</tt> represents a single-level directory path
   *    return true
   *  else
   *    return false
   */
  private static boolean isSinglePathLevel(final String path, final String pathSeparator) {
    int fi = path.indexOf(pathSeparator);
    int li = path.lastIndexOf(pathSeparator);
    
    return fi == li;
  }

  /**
   * <b>IMPORTANT</b>: This method WORKS in both cases: (1) source class is stored in a file system 
   * and (2) source class is stored in a jar file. 
   * 
   * @requires 
   *  c != null /\ srcFolderName != null /\ targetDirPath != null
   * @effects <pre> 
   *  if exists a folder <tt>srcDir</tt> whose name is <tt>srcFolderName</tt> 
   *    that is stored in the source folder of class <tt>c</tt>
   *    copy the entire directory tree of <tt>srcDir</tt> (including <tt>srcDir</tt> itself) to the directory whose absolute path is 
   *    <tt>targetDirPath</tt> (this directory is created if not yet exists)
   *    
   *  else
   *    do nothing 
   *    
   *  <p>Throws IOException if failed to copy directory.
   *  </pre>
   *  
   * @version 3.1
   */
  public static void copyDir(Class c, String srcFolderName, String targetDirPath) throws IOException {
    File targetDir = new File(targetDirPath);
    
    if (!targetDir.exists()) {  
      // not yet exists: create
      boolean created = targetDir.mkdir();
      if (!created)
        throw new IOException("Failed to create target directory path: " + targetDirPath);
    } else if (!targetDir.isDirectory()) {
      // not a directory
      throw new IOException("Target path is not a directory: " + targetDirPath);
    }
    
    if (debug) {
      System.out.println("\nCopying from Jar...");
      System.out.println("  Source class: " + c);
      System.out.println("  Source folder name: " + srcFolderName);
      System.out.println("  Target path: " + targetDir.getPath());
    }
    
    // check if we are reading off a jar file or from file system
    if (isExistInJar(c, srcFolderName)) {
      // in jar file 
      copyDirFromJar(c, srcFolderName, targetDirPath);
    } else {
      // not in a jar file: assumes file system
      copyDirFromFileSystem(c, srcFolderName, targetDirPath);
    }
  }

  /**
   * <b>IMPORTANT</b> This method ONLY works when <tt>c</tt> is stored in a file system. 
   * For more general support use {@link #copyDir(Class, String, String)} instead.
   * 
   * @requires 
   *  c is stored in a file system /\ 
   *  srcFolderName must be a single name (i.e. not containing any path elements) 
   *  
   * @effects <pre>
   *  if exists a sub-directory (srcDir) named <tt>srcFolderName</tt> stored in the source directory 
   *  of <tt>c</tt> <b>in the file system</b>
   *    copy its entire content (including <tt>srcDir</tt> itself) to <tt>targetPath</tt> (folder at <tt>targetPath</tt> is created
   *    if not already exists)
   *    
   *  ; else
   *    do nothing
   *  
   *  <p>throws IOException if failed.
   *  </pre>
   * @version 3.1
   */
  public static void copyDirFromFileSystem(Class c, String srcFolderName,
      String targetPath) throws IOException {
    String srcFolderPath = getPath(c, srcFolderName);
    if (srcFolderPath != null) {
      File srcFolder = new File(srcFolderPath);
      
      // create target folder path if not already there
      String targetFolderPath = targetPath + fileSep + srcFolderName;
      File targetFolder = new File(targetFolderPath);
      
      copyDir(srcFolder, targetFolder);
    }
  }

  /**
   * <b>IMPORTANT</b> This method ONLY works when <tt>c</tt> is stored in a jar file. 
   * For more general support use {@link #copyDir(Class, String, String)} instead.
   * 
   * @requires 
   *  c is stored in a jar file /\ srcFolderName must be a single name (i.e. not containing any path elements)  
   *  
   * @effects <pre>
   *  if exists a sub-directory (srcDir) named <tt>srcFolderName</tt> stored in the source directory 
   *  of <tt>c</tt> <b>in a jar file</b>
   *    copy its entire content (including <tt>srcDir</tt> itself) to <tt>targetPath</tt> (folder at <tt>targetPath</tt> is created
   *    if not already exists)
   *    
   *  ; else 
   *    do nothing
   *    
   *  <p>throws IOException if failed to copy.
   *  </pre>
   *  
   * @version 3.1
   */  
  public static void copyDirFromJar(Class c, String srcFolderName, String targetPath) throws IOException {
    // get the jar file containing c and read content off it
    File jarFile = new File(c.getProtectionDomain().getCodeSource()
        .getLocation().getPath());

    if (jarFile.isFile()) {
      if (debug) System.out.println("Jar file: " + jarFile);
      
      String cname = c.getName();
      String cPackagePath;
      int lastDot = cname.lastIndexOf(".");
      if (lastDot > -1) {
        cPackagePath = cname.substring(0, lastDot);
        cPackagePath = cPackagePath.replaceAll("\\.","/");
        cPackagePath = cPackagePath + jarFileSep; 
      } else { 
        // no package
        cPackagePath = "";
      }
      
      if (debug) System.out.println("Class file path: " + cPackagePath);
      
      String fodName, subPath;
      JarEntry jarEntry;
      File subDir;
      boolean createdSubFolder;
      InputStream fins;
      
      if (!targetPath.endsWith(fileSep)) targetPath = targetPath + fileSep;

      JarFile jar = null;
      try {
        jar = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jar.entries();
        String srcFolderPath = cPackagePath + srcFolderName + jarFileSep;
        while (entries.hasMoreElements()) {
          jarEntry = entries.nextElement();
          fodName = jarEntry.getName();
          if (debug) System.out.println("..." + fodName);
          if (fodName.startsWith(srcFolderPath)) {
            // found entries in sourceFolder

            subPath = fodName.substring(cPackagePath.length());
            // replace jar file separator with actual file separate
            subPath.replaceAll(jarFileSep, Matcher.quoteReplacement(fileSep));
            subPath = targetPath + subPath;

            if (jarEntry.isDirectory()) {
              // a directory: create it in target path if has not already been created
              subDir = new File(subPath);
              if (!subDir.exists()) {
                createdSubFolder = subDir.mkdirs();
              
                if (!createdSubFolder)
                  throw new IOException("Failed to create sub-folder: " + subPath);
              }
            } else {
              // an entry in source folder: copy it to target
              fins = jar.getInputStream(jarEntry);

              copyFile(fins, new File(subPath));

              if (debug) System.out.printf("   -> copied to: %s%n", subPath);
            }
          }
        }

      } catch (IOException e) {
        throw e;
      } finally {
        if (jar != null) jar.close();
      }
    } else {
      // not in a jar file
      throw new IOException("Jar file '"+jarFile+"' is not recognised as a valid file for class: " + c);
    }    
  }

  /**
   * @effects <pre>
   *  copy the complete content directory tree of the source directory <tt>sd</tt>
   *  (except <tt>sd</tt> itself)
   *  to the target directory <tt>dd</tt>. 
   *  Target directory <tt>dd</tt> is created if not already exists; 
   *  target files with same name are overriden;  
   *  target sub-folders with same name are merged
   *   
   *  <p>throws IOException if failed
   *  </pre>
   */
  public static void copyDir(File sd, File dd) throws IOException {
    if (debug) System.out.printf("  Copying folder: %s -> %s%n", sd, dd);

    // create destination dir first
    if (!dd.exists()) {
      boolean ok = dd.mkdir();
      if (!ok)
        throw new IOException("Không thể tạo thư mục " + dd.getAbsolutePath());
    }

    File[] files = sd.listFiles();
    if (files != null) {
      File df;
      for (File f : files) {
        df = new File(dd + fileSep + f.getName());
        if (f.isDirectory()) { // sub-directory
          // recursive
          copyDir(f, df);
        } else { // file
          copyFile(f, df);
        }
      }
    } else {
      if (debug) System.out.printf("  folder is empty: %s%n", sd);
    }
  }

  /**
   * @effects 
   *  if <tt>dest</tt> does not exist then create new
   *  copy content of <tt>src</tt> to <tt>dest</tt>, overriding if exists
   *  
   *  <p>throws IOException if failed
   */
  protected static void copyFile(File src, File dest) throws IOException {
    // create destination file first
    if (!dest.exists()) {
      boolean ok = dest.createNewFile();
      if (!ok)
        throw new IOException("Không thể tạo file " + dest.getAbsolutePath());
    }
    
    InputStream is = new FileInputStream(src);

    if (debug) System.out.printf("...Copying file: %s -> %s%n", src, dest);

    copyFile(is, dest);
    is.close();
  }
  
  /**
   * @effects 
   *  Copy the file content captured in <tt>fins</tt> to <tt>dest</tt>
   *  File <tt>dest</tt> is created if not exists.
   */
  public static void copyFile(InputStream fins, String dest) throws IOException {
    File destFile = new File(dest);
    
    // create destination file first
    if (!destFile.exists()) {
      boolean ok = destFile.createNewFile();
      if (!ok)
        throw new IOException("Không thể tạo file " + destFile.getAbsolutePath());
    }
    
    copyFile(fins, destFile);
  }
  
  /**
   * @effects 
   *  Copy the file content captured in <tt>fins</tt> to <tt>dest</tt>. 
   *  File <tt>dest</tt> is created if not exists.
   */
  protected static void copyFile(InputStream is, File dest) throws IOException {
    OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
    byte[] data = new byte[1000];
    int count;
    while ((count = is.read(data, 0, 1000)) != -1) {
      out.write(data, 0, count);
    }
    out.flush();
    out.close();    
  }

  /**
   * @requires 
   *  jarEntryPath != null /\ jarEntryPath is a valid entry path in a jar file /\ 
   *  jarEntryPath is a text file
   *  
   * @effects 
   *  read the {@link InputStream} of the {@link JarEntry} specified by <tt>jarEntryPath</tt>.
   *  
   *   <p>Throws IllegalArgumentException if <tt>jarEntryPath</tt> is not a valid jar entry path; 
   *   IOException if failed to read the entry
   *   
   * @example <pre>
   * Given:
   *    jarEntryPath = file:/home/dmle/tmp/myJarFile.jar!/resources/MyFile.csv
   * Then:
   *    readJarEntryAsStream(jarEntryPath) = InputStream of file /resources/MyFile.csv located 
   *    in the jar file /home/dmle/tmp/myJarFile.jar
   *  </pre>
   * @version 5.4
   */
  public static InputStream readJarFileEntry(String jarEntryPath) throws IllegalArgumentException, IOException {
    final String pathPrefix = FILE_URL_PREFIX;
    final String sep = "!";
    
    if (jarEntryPath == null || !jarEntryPath.startsWith(pathPrefix)) {
      // invalid path
      throw new IllegalArgumentException("Invalid jar entry path (expected prefix: "+pathPrefix+"): " + jarEntryPath);
    }
    
    // parse jarEntryPath into jar file and entry file
    String path = jarEntryPath.substring(pathPrefix.length());
    String[] pathElements = path.split(sep);
    
    if (pathElements.length != 2) {
      // invalid path
      throw new IllegalArgumentException("Invalid jar entry path (expected 2 parts separated by: "+sep+"): " + jarEntryPath);    
    }
    
    String jarFilePath = pathElements[0];
    String entryPath = pathElements[1];
    // remove "/"
    if (entryPath.startsWith(jarFileSep)) entryPath = entryPath.substring(1); 
      
    // search for entry in jar file
    File jarFile = new File(jarFilePath);

    if (jarFile.isFile()) {
      if (debug) System.out.println("Jar file: " + jarFile);
      
      String fodName;
      JarEntry jarEntry;
      JarFile jar = null;
      
      InputStream entryIns;
      Collection<String> entryContent = null;
      try {
        jar = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
          jarEntry = entries.nextElement();
          fodName = jarEntry.getName();
          if (debug) System.out.println("..." + fodName);
          if (fodName.equals(entryPath)) {
            // found the entry
            if (debug) System.out.println("   -> FOUND");
            entryIns = jar.getInputStream(jarEntry);
            
            return entryIns;
          }
        }
      } catch (IOException e) {
        throw e;
      } finally {
        // do not close JAR
        // to use the InputStream
      }
      
      // not exist
      throw new IOException("Entry "+entryPath+" not exist in jar file: " + jarFilePath);        
    } else {
      // not in a jar file
      throw new IOException("Not in a jar file: " + jarFilePath);
    }     
  }
  
  /**
   * 
   * @effects 
   *  call {@link #readJarTextFileEntry(String, String)} with <tt>charSetName = null</tt>
   * @version 3.3
   */
  public static Collection<String> readJarTextFileEntry(String jarEntryPath) throws IllegalArgumentException, IOException {
    return readJarTextFileEntry(jarEntryPath, null);
  }
  
  /**
   * @requires 
   *  jarEntryPath != null /\ jarEntryPath is a valid entry path in a jar file /\ 
   *  jarEntryPath is a text file
   *  
   * @effects 
   *  read the text content of the {@link JarEntry} specified by <tt>jarEntryPath</tt> as 
   *  {@link Collection} of lines and return it 
   *  
   *   <p>Throws IllegalArgumentException if <tt>jarEntryPath</tt> is not a valid jar entry path; 
   *   IOException if failed to read the entry
   *   
   * @example <pre>
   * Given:
   *    jarEntryPath = file:/home/dmle/tmp/myJarFile.jar!/resources/MyFile.csv
   * Then:
   *    readJarEntryAsStream(jarEntryPath) = content lines of file /resources/MyFile.csv located 
   *    in the jar file /home/dmle/tmp/myJarFile.jar
   *  </pre>
   * @version 3.1
   */
  public static Collection<String> readJarTextFileEntry(String jarEntryPath, String charSetName) throws IllegalArgumentException, IOException {
    final String pathPrefix = FILE_URL_PREFIX;
    final String sep = "!";
    
    if (jarEntryPath == null || !jarEntryPath.startsWith(pathPrefix)) {
      // invalid path
      throw new IllegalArgumentException("Invalid jar entry path (expected prefix: "+pathPrefix+"): " + jarEntryPath);
    }
    
    // parse jarEntryPath into jar file and entry file
    String path = jarEntryPath.substring(pathPrefix.length());
    String[] pathElements = path.split(sep);
    
    if (pathElements.length != 2) {
      // invalid path
      throw new IllegalArgumentException("Invalid jar entry path (expected 2 parts separated by: "+sep+"): " + jarEntryPath);    
    }
    
    String jarFilePath = pathElements[0];
    String entryPath = pathElements[1];
    // remove "/"
    if (entryPath.startsWith(jarFileSep)) entryPath = entryPath.substring(1); 
      
    // search for entry in jar file
    File jarFile = new File(jarFilePath);

    if (jarFile.isFile()) {
      if (debug) System.out.println("Jar file: " + jarFile);
      
      String fodName;
      JarEntry jarEntry;
      JarFile jar = null;
      
      InputStream entryIns;
      Collection<String> entryContent = null;
      try {
        jar = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
          jarEntry = entries.nextElement();
          fodName = jarEntry.getName();
          if (debug) System.out.println("..." + fodName);
          if (fodName.equals(entryPath)) {
            // found the entry
            if (debug) System.out.println("   -> FOUND");
            entryIns = jar.getInputStream(jarEntry);
            
            // read entry directly:
            // NOTE: cannot return entryIns directly if jar.close() is used (below) because 
            // this will close the stream before it is used!!
            entryContent = readTextFileWithEncoding(entryIns, charSetName);
            
            break;
          }
        }
      } catch (IOException e) {
        throw e;
      } finally {
        if (jar != null) jar.close();
      }
      
      if (entryContent == null) {
        // not found
        throw new IOException("Entry "+entryPath+" not exist in jar file: " + jarFilePath);        
      } else {
        return entryContent;
      }
    } else {
      // not in a jar file
      throw new IOException("Not in a jar file: " + jarFilePath);
    }     
  }
  
  /**
   * <b>IMPORTANT</b>: This method does not support character encoding!
   * 
   * @effects 
   *  if file represented by <tt>ins</tt> contains non-empty text and if succeeds in 
   *  reading the file
   *    return a Collection of the non-empty lines
   *  else
   *    return null
   */
  public static Collection<String> readTextFile(InputStream ins) throws IOException {
    String line;
    Collection<String> lines = new ArrayList();
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
    
    try {
      while ((line = reader.readLine()) != null) {
        if (line.length() > 0)  // ignore empty lines
          lines.add(line);
      }
  
      return (lines.isEmpty()) ? null : lines;
    } catch (IOException e) {
      throw e;
    } finally {
      try { reader.close(); } catch (Exception e) {}
    }
  }
  

  /**
   * A short-cut for {@link #readTextFile(InputStream)}.
   * 
   * <b>IMPORTANT</b>: This method does not support character encoding!
   * 
   * @effects 
   *  if file whose path is <tt>filePath</tt> contains non-empty text and if succeeds in 
   *  reading the file
   *    return a Collection of the non-empty lines
   *  else
   *    return null 
   *  
   * @version 5.2
   */
  public static Collection<String> readTextFile(String filePath) throws IOException {
    FileInputStream fin = new FileInputStream(filePath);
    
    return readTextFile(fin);
  }

  /**
   * <b>IMPORTANT</b>: This method DOES support character encoding!
   * 
   * @effects 
   *  if file named <tt>fileName</tt> contains non-empty text and if succeeds in 
   *  reading the file
   *    return a Collection of the non-empty lines
   *  else
   *    return null
   */
  public static Collection<String> readTextFileWithEncoding(InputStream ins, final String charSetName) throws IOException {
    String line;
    Collection<String> lines = new ArrayList();
    
    InputStreamReader insr;
    if (charSetName != null) {
      insr = new InputStreamReader(ins, charSetName);
    } else {
      insr = new InputStreamReader(ins);
    }
    
    BufferedReader reader = new BufferedReader(insr );
    
    try {
      while ((line = reader.readLine()) != null) {
        if (line.length() > 0)  // ignore empty lines
          lines.add(line);
      }
  
      return (lines.isEmpty()) ? null : lines;
    } catch (IOException e) {
      throw e;
    } finally {
      try { reader.close(); } catch (Exception e) {}
    }
  }
  
  /**
   * <b>IMPORTANT</b>: This method does not support character encoding!
   * 
   * @effects 
   *  Write <tt>content</tt> into <tt>file</tt>, erasing existing content if <tt>toOverwrite = true</tt>
   *  
   *  <p>Throws NotPossibleException if failed to write to file for some reasons. 
   * @version 3.3
   */
  public static void writeTextFile(File file, String content, boolean toOverwrite) throws NotPossibleException {
    boolean append = !toOverwrite;
    
    FileWriter writer = null;
    try {
      writer = new FileWriter(file, append);
      
      writer.write(content);
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WRITE_TO_FILE, e, new Object[] {file.getName()});
    } finally {
      if (writer != null)
        try {
          writer.close();
        } catch (IOException e) {
          // ignore
        }
    }
  }


  /**
   * <b>IMPORTANT</b>: This method DOES support character encoding!
   * 
   * @effects 
   *  Write <tt>content</tt> (whose char-set name is <tt>charSetName</tt>) into <tt>file</tt>, erasing existing content if <tt>toOverwrite = true</tt>
   *  
   *  <p>Throws NotPossibleException if failed to write to file for some reasons. 
   * @version 3.3
   */
  public static void writeTextFileWithEncoding(File file, final String content, final String charSetName, final boolean toOverwrite) {
    boolean append = !toOverwrite;
    
    OutputStreamWriter writer = null;
    try {
      Charset charSet = Charset.forName(charSetName);
      
      writer = new OutputStreamWriter(new FileOutputStream(file, append), charSet);
      
      writer.write(content);
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WRITE_TO_FILE, e, new Object[] {file.getName() + " (char-set: " + charSetName + ")"});
    } finally {
      if (writer != null)
        try {
          writer.close();
        } catch (IOException e) {
          // ignore
        }
    }
  }
  
  /**
   * @effects 
   *  call {@link #writeTextFileWithEncoding(File, String, String, boolean)} with <tt>charSet="UTF-8"</tt>
   *  
   * @version 3.3
   */
  public static void writeUTF8TextFile(File file, final String content, final boolean toOverwrite) {
    writeTextFileWithEncoding(file, content, "UTF-8", toOverwrite);
  }
  
  /**
   * @effects 
   *  Write to <tt>outputDir</tt> <tt>srcCode</tt> into the Java source file of the class whose FQN is <tt>name</tt> (e.g. vn.com.test.Test). 
   *  <p>The file is created (together with its package directory structure, if any) if not already exists.
   *  
   *  <p>If the file already exists and <tt>toOverwrite = true</tt>, then its existing content is erased; otherwise
   *  <tt>srcCode</tt> is appended to the file. 
   *  
   *  <p>Return the {@link File} object of the created file
   *   
   *  <p>Throws NotPossibleException if failed to write to file for some reasons.
   *  
   * @version 3.3 
   */
  public static File writeJavaSourceFile(String outputDir,
      String name, String srcCode, boolean toOverwrite) throws NotPossibleException {
    // file path 
    String[] filePathElems = name.split("\\.");
    StringBuffer filePathWoutExt = new StringBuffer(outputDir);
    for (String e : filePathElems) {
      filePathWoutExt.append(fileSep).append(e);
    }

    // now the complete file path
    String filePath = filePathWoutExt.toString()+FILE_JAVA_EXT;
    File clsFile = new File(filePath);

    boolean fileExist = clsFile.exists(); 
    // v3.4: erase file content first (if exists and toOverwrite = true)
    try {
      if (toOverwrite && fileExist) {
        Files.write(clsFile.toPath(), srcCode.getBytes());
      } else {
        if (!fileExist) {
          // create directory path if not already exists
          int lastSepIndex = filePathWoutExt.lastIndexOf(fileSep);
          File dirPath = new File(filePathWoutExt.substring(0,lastSepIndex));
          if (!dirPath.exists()) {
            boolean ok = dirPath.mkdirs();
            
            if (!ok) {
              // failed to create path
              throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_DIRECTORY, new Object[] {dirPath});
            }
          }
        }

        if (toOverwrite)
          Files.write(clsFile.toPath(), srcCode.getBytes());
        else
          Files.write(clsFile.toPath(), srcCode.getBytes(), StandardOpenOption.APPEND);
      }
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WRITE_TO_FILE, new Object[] {clsFile});
    }
    
//    // create directory path if not already exists
//    int lastSepIndex = filePathWoutExt.lastIndexOf(fileSep);
//    File dirPath = new File(filePathWoutExt.substring(0,lastSepIndex));
//    if (!dirPath.exists()) {
//      boolean ok = dirPath.mkdirs();
//      
//      if (!ok) {
//        // failed to create path
//        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_DIRECTORY, new Object[] {dirPath});
//      }
//    }
//
//    writeTextFile(clsFile, srcCode, toOverwrite);
    
    return clsFile;
  }
  
//  /**
//   * <b>IMPORTANT</b>This method supports both file-system path and jar-file system path.
//   * @requires 
//   *  filePath != null /\ filePath is a valid entry path in a jar file
//   *  
//   * @effects 
//   *  read the specified <tt>filePath</tt> as {@link InputStream} and return it 
//   *  
//   *   <p>Throws IllegalArgumentException if <tt>filePath</tt> is not a valid path; 
//   *   IOException if failed to read the entry
//   * @version 3.1
//   */
//  public static InputStream getFileInputStream(String filePath) throws IllegalArgumentException, IOException {
//    InputStream fileIns;
//    if (filePath.startsWith(FILE_URL_PREFIX)) {
//      // a jar path: read the file entry specified by filePath as InputStream 
//      fileIns = ToolkitIO.readJarEntryAsStream(filePath);
//    } else {
//      // a normal file path
//      fileIns = new FileInputStream(filePath);
//    }
//    
//    return fileIns;
//  }

  /**
   * @effects 
   *  if <tt>filePath</tt> starts with {@link #FILE_URL_PREFIX}
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public static boolean isFileUrl(String filePath) {
    return filePath != null && filePath.startsWith(FILE_URL_PREFIX);
  }

  /**
   * @requires 
   *  file != null
   * @effects 
   *  get and return the raw <tt>byte[]</tt> array of <tt>file</tt>
   *  
   *  <p>throws NotPossibleException if failed to do so
   *  
   * @version 3.2
   */
  public static byte[] getFileAsBytes(File file) throws NotPossibleException {
    if (file == null)
      throw new NullPointerException("ToolkitIO.getFileAsBytes: Input file is required but not specified");

    Path path = file.toPath();
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, e, new Object[] {file.getPath()});
    }
    /*try {
      // determine file size
      FileInputStream is = new FileInputStream(file);
      long sz = file.length();

      // read file in one go
      if (sz > 0) {
        byte[] data = new byte[(int)sz];
        is.read(data, 0, (int)sz);
        return data;
      } else {
        // file does not exist or not a valid file
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, new Object[] {file.getPath()});
      }
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, new Object[] {file.getPath()});
    }*/
  }

  /**
   * @effects 
   *  Create and return a {@link File} created in the OS's temporary directory that contains the bytes 
   *  in <tt>ins</tt>  
   *  
   * @version 3.2
   */
  public static File createTempFile(InputStream ins) throws NotPossibleException {
    String fileName = genRandomFileName();
    try {
      //Path path =  Files.createTempFile(fileName, FILE_TEMP_EXT);
      File tempFile = new File(newFilePath(getSystemTempDir(), fileName));
      Files.copy(ins, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      return tempFile;
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_FILE, e, new Object[] {fileName});
    }
  }

  /**
   * @effects 
   *  Return the value of the {@link System} property for the os temporary directory
   *   
   * @version 3.2
   */
  private static String getSystemTempDir() {
    return System.getProperty("java.io.tmpdir");
  }

  /**
   * @effects 
   *  Create and return a random file name, which is the hash code of the current data time
   *  
   * @version 3.2
   */
  private static String genRandomFileName() {
    return "file_"+System.nanoTime();
  }

  /**
   * @effects 
   *  Copy {@link File} whose path is <tt>srcFilePath</tt> to a target {@link File} whose directory 
   *  is <tt>destFileDir</tt> and whose name is <tt>destFileName</tt>, using <tt>copyOptions</tt>
   *  
   *  <p>Throws NotPossibleException if failed to copy
   *  
   * @version 3.2
   */
  public static void copyFile(Path srcFilePath, Path destFileDir, String destFileName, 
      CopyOption...copyOptions) throws NotPossibleException {
    String targetFilePath = destFileDir.toString()+File.separator+destFileName;
    File targetFile = new File(targetFilePath);
    try {
      Files.copy(srcFilePath, targetFile.toPath(), copyOptions);
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_COPY_FILE, e, new Object[] {srcFilePath, targetFilePath});
    }    
  }

  /**
   * @effects 
   *  return a new path constructed from <tt>path + {@link File#separator} + name</tt>
   * @version 3.2
   */
  private static String newFilePath(String path, String name) {
    return path + fileSep + name;
  }

  /**
   * This method does not create the folder path elements if they donot exist. 
   * For this behaviour, see {@link #touchPath(String)}.
   *  
   * @requires 
   *  folder != null
   * @effects 
   *  if <tt>folder</tt> does not exists
   *    create it in the local file system or 
   *    throws NotPossibleException if failed to do so 
   *  
   */
  public static void createFolderIfNotExists(File folder) throws NotPossibleException {
    if (folder == null) 
      return;
    
    if (!folder.exists()) {
      boolean created = folder.mkdir();
      if (!created) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_FOLDER, new Object[]{folder.getPath()});
      }
    }    
  }

  /**
   * This method does not create the folder path elements if they donot exist. 
   * For this behaviour, see {@link #touchPath(String)}.
   *  
   * @effects 
   *  if the folder whose path is <tt>folderPath</tt> does not exist
   *    create it or 
   *    throws NotPossibleException if failed to do so   
   * @version 5.2
   */
  public static void createFolderIfNotExists(String folderPath) throws NotPossibleException {
    if (folderPath == null) return;
    
    createFolderIfNotExists(new File(folderPath));
  }
  
  /**
   * @requires 
   *  file != null
   * @effects 
   *  if <tt>file</tt> does not exists
   *    create it in the local file system
   *    throws NotPossibleException if failed to do so 
   */
  public static void createFileIfNotExists(File file) throws NotPossibleException {
    if (file == null) 
      return;
    
    if (!file.exists()) {
      boolean created;
      try {
        created = file.createNewFile();
        
        if (!created) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_FILE, new Object[]{file.getPath()});
        }
      } catch (IOException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_FILE, new Object[]{file.getPath()});
      }
    }    
  }

  /**
   * @requires <tt>Path(rootSrcPath)</tt> contains <tt>srcPkg, destPkg</tt> /\ 
   *    <tt>srcPkg, destPkg</tt> exist
   * 
   * @modifies <tt>Folder(destPkg)</tt> contains the refactored source files from the <tt>srcPkg</tt>
   * @effects 
   *  if there exists Java files (.java) in the package <tt>srcPkg</tt> of <tt>rootSrcPath</tt>
   *    create in <tt>destPkg</tt> a 'refactored copy' of each by replacing the class name with a new name containing <tt>newNameId</tt> as the suffix 
   *    (i.e. <tt>ClassA</tt> becomes <tt>classA + newNameId</tt>).
   *    
   *    return {@link Map}(FQN,File) which maps FQN of each new class to its {@link File}.
   *    
   *   <p>Throws {@link NotPossibleException} if fails for some reasons.
   * @version 
   *  - 5.4.1: added return type to ease processing   
   */
  public static Map<String,File> refactorSrcFilesInPkg(String rootSrcPath, String srcPkg,
      int newNameId, String destPkg) throws NotPossibleException {
    String dirPath = ToolkitIO.getPath(
        rootSrcPath, ToolkitIO.splitPackageName(srcPkg)).toString();

    String destDirPath = ToolkitIO.getPath(
        rootSrcPath, ToolkitIO.splitPackageName(destPkg)).toString();

    if (debug) System.out.println("Package dir: " + dirPath);
    
    // read files in dirPath
    File sd = new File(dirPath);
    File[] files = sd.listFiles(
        JavaFileFilter  // v5.4.1
        );
    Map<File, Tuple2<String,String>> fileMap = new HashMap<>();
    
    if (files != null) {
      File df;
      for (File f : files) {
        String fname = f.getName();
        fname = fname.substring(0, fname.lastIndexOf("."));
        // create new name for each file 
        String newName = fname + newNameId;
        fileMap.put(f,  new Tuple2(fname, newName));
      }
      
      /* for each file f
       *    read its content
       *    replace all references to each old class name by the corresponding new class names
       *    write content to file under new name    
       */
      Collection<Tuple2<String,String>> newNames = fileMap.values();
      boolean toOverwrite = true;
      
      // ducmle: 5.4.1
      Map<String,File> destFiles = new HashMap<>();
      
      for (Entry<File, Tuple2<String,String>> e : fileMap.entrySet()) {
        File f = e.getKey();
        String oldName = e.getValue().getFirst(),
               newName = e.getValue().getSecond();

        if (debug) System.out.printf("  File (%s -> %s)%n", oldName, newName);
        
        Collection<String> content = null;
        try {
          content = ToolkitIO.readTextFileWithEncoding(new FileInputStream(f), ENCODE_UTF8);
        } catch (IOException ex) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, ex, new Object[] {f.getName()});
        }
        
        StringBuilder newContent = new StringBuilder();
        for (String line : content) {
          // replace package name
          if (line.trim().startsWith("package")) {
            line = line.replaceAll(srcPkg, destPkg);
          }
          
          // replace oldName -> newName (references)
          for (Tuple2<String,String> name : newNames) {
            String oname = name.getFirst(), nname = name.getSecond();
            line = line.replaceAll(oname, nname);
          }
          newContent.append(line).append("\n");
        }
        
        File newFile = ToolkitIO.writeJavaSourceFile(destDirPath, newName, newContent.toString(), toOverwrite);
        String newClsFQN = destPkg + "." + newName;
        destFiles.put(newClsFQN, newFile);  // v5.4.1
        if (debug) System.out.printf("  written to new file: %s%n", newFile.getName());
      }
      
      return destFiles; // v5.4.1
    } else {
      if (debug) System.out.printf("  folder is empty: %s%n", sd);
      
      return null; // v5.4.1
    }
  }
  
  /**
   * @requires e != null
   * 
   * @effects
   *  generate and return a complete stack trace of <tt>e</tt>
   * @version 3.0  
   */
  public static String getStackTrace(Throwable e, String encoding) {
    if (e == null)
      return null;
    
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    
    PrintStream ps;
    boolean autoFlush = false;
    
    if (encoding != null) {
      try {
        ps = new PrintStream(byteOut, autoFlush, encoding);
      } catch (UnsupportedEncodingException e1) {
        // wrong encoding: omit
        ps = new PrintStream(byteOut, autoFlush);
      }
    } else 
      ps = new PrintStream(byteOut, autoFlush);
    
    e.printStackTrace(ps);
    
    return byteOut.toString();
  }

  /**
   * @effects 
   *  return the system-dependent path of <tt>path</tt> 
   * @version 4.0
   */
  public static String getSysDependentPath(String path) {
    if (path == null) return null;
    
    boolean forwardPath = path.indexOf("/") > -1;
    boolean systemForwardPath = fileSep.indexOf("/") > -1;
    if (forwardPath) {
      if (systemForwardPath) {
        // both path and system use "/"
        // no need to do anything
        return path;
      } else {
        // system uses "\\"
        return path.replaceAll("/", Matcher.quoteReplacement(fileSep));
      }
    } else {
      // path contains "\\"
      if (!systemForwardPath) { 
        // both path and system use "\\"
        return path;
      } else {
        // system uses "/"
        return path.replaceAll(Matcher.quoteReplacement("\\"), Matcher.quoteReplacement(fileSep));
      }
    }
  }

  /**
   * @effects 
   *  create and return {@link Path} from <tt>first, more</tt> as specified in 
   *  {@link Paths#get(String, String...)} 
   *  
   * @version 4.0 
   */
  public static Path getPath(String first, String...more) {
    return Paths.get(first, more);
  }

  /**
   * @effects 
   *  create and return {@link File} from <tt>first, more</tt> as specified in 
   *  {@link Paths#get(String, String...)} 
   *  
   * @version 5.4
   */
  public static File getPathAsFile(String first, String...more) {
    Path path = Paths.get(first, more);
    return path.toFile();
  }
  
  /**
   * @effects 
   *  return the file system path constructed from: 
   *  <code>parentPath + sep + packagePath</code>
   *  where <code>packagePath</code> is the filesystem representation of 
   *  <code>packageName</code>.
   * 
   * @version 5.4
   */
  public static String getPackagePath(String parentPath,
      String pkgName) {
    Path p = 
        (pkgName != null) 
          ? getPath(parentPath, splitPackageName(pkgName))
          : getPath(parentPath);
    return p.toString();
  }
  
  /**
   * @effects 
   *  return <tt>String[]</tt> array containing elements of <tt>clsPkgName</tt>.
   *  (if <tt>clsPkgName</tt> does not contain '.' then result has a single element).
   *  
   * @example
   *  <pre>splitPackageName("mypackage.p1") = ["mypackage", "p1"]</pre> 
   * @version 4.0
   */
  public static String[] splitPackageName(String clsPkgName) {
    if (clsPkgName == null) return null;
    
    return clsPkgName.split("\\.");
  }

  /**
   * @effects 
   *  Prompt user to press the ENTER key on the console to continue
   * @version 5.2c
   */
  public static void consolePause() {
    System.out.println("\nEnter ENTER key to continue...");
    BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
    try {
      bin.readLine();
    } catch (IOException e) {
      // do nothing
    }
  }

  /**
   * @effects 
   *  return the file-system source code path of the package of <code>cls</code>.
   *  
   * @version 5.4 
   */
  public static File getPackagePath(Class cls) {
//    String pkgFQN = cls.getPackage().getName();
//    return getPath("", splitPackageName(pkgFQN)).toString();
    String clsFileName = cls.getSimpleName() + ".class";
    String absClsFilePath = getPath(cls, clsFileName);
    // package path is the parent of this path
    return new File(absClsFilePath).getParentFile();
  }

  /**
   * @requires
   * <code>srcPath</code> is a result from {@link #getMavenRootSrcPath(Class, boolean)}.
   * 
   * @effects 
   *  return the project root folder of <tt>srcPath</tt>
   *  <p>e.g.
   *  if <code>srcPath = myproj/src/test/java</code> then <code>result = myproj</code>
   * @version 5.4
   */
  public static String getMavenProjectRootPath(File srcPath) {
    if (srcPath == null) return null;
    
    return srcPath.getParentFile().getParentFile().getParent();
  }

  /**
   * This is a combination of {@link #getMavenRootSrcPath(Class, boolean)} and {@link #getMavenProjectRootPath(File)}.
   * 
   * @requires
   * <code>srcPath</code> is a result from {@link #getMavenRootSrcPath(Class, boolean)}.
   * 
   * @effects 
   *  return the project root folder of <tt>srcPath</tt> of <code>c</code>
   *  <p>e.g.
   *  if <code>srcPath = myproj/src/test/java</code> then <code>result = myproj</code>
   * @version 5.4
   */
  public static String getMavenProjectRootPath(Class c, boolean mainSrc) {
    File rootSrcPath = getMavenRootSrcPath(c, mainSrc);
    if (rootSrcPath == null)
      return null;
    else
      return getMavenProjectRootPath(rootSrcPath);
  }
  
  /**
   * @effects 
   *  return the absolute path to the <code>src</code> directory of the current 
   *  code project. 
   *  <p>This is the child folder of the sub-folder <code>src/main/java</code> of the 
   *  current project path which contains the input class.
   *  
   * @version 5.4 
   */
  public static File getMavenRootSrcPath(Class c, boolean mainSrc) {
    File pkgPath = getPackagePath(c);
    
    String srcNodeName = (mainSrc) ? "main" : "test";
    
    if (pkgPath != null) {
      File targetNode = null;
      File currNode = pkgPath;
      while (currNode != null) {
        if (currNode.getName().equals("target")) {
          targetNode = currNode;
          break;
        }
        currNode = currNode.getParentFile();
      }
      
      if (targetNode != null) {
        try {
          File result = 
              targetNode.getParentFile().listFiles((dir, name) -> name.equals("src"))[0]
                .listFiles((dir, name) -> name.equals(srcNodeName))[0]
                  .listFiles((dir, name) -> name.equals("java"))[0];
          return result;
        } catch (Exception e) {
          // not a standard maven structure
          return null;
        } 
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if fileName represents a valid Json file relative to <code>c</code>
   *    read and return {@link JsonObject} or {@link JsonArray} it
   *  else
   *    throw {@link NotPossibleException}
   * @version 5.4
   */
  public static <T extends JsonValue> T readJSonFile(InputStream ins, Class<T> jsonClass) 
      throws NotPossibleException {
    JsonReader reader = Json.createReader(ins);
    T json;
    if (JsonArray.class.isAssignableFrom(jsonClass)) {
      // json array
      json = (T) reader.readArray();
    } else if (JsonObject.class.isAssignableFrom(jsonClass)) {
      // json object
      json = (T) reader.readObject();
    } else {
      // wrong class
      throw new NotPossibleException(NotPossibleException.Code.INVALID_INPUT_CLASSES_ARGUMENT,
          new Object[] {jsonClass});
    }
    return json;
  }
  
  /**
   * @effects 
   *  if fileName represents a valid Json file relative to <code>c</code>
   *    read and return {@link JsonObject} or {@link JsonArray} it
   *  else
   *    throw {@link NotPossibleException}
   * @version 5.4
   */
  public static <T extends JsonValue> T readJSonFile(Class c, 
      Class<T> jsonClass, String fileName) 
      throws NotPossibleException {
    InputStream ins;
    try {
      ins = getFileInputStream(c, fileName);
      
      return readJSonFile(ins, jsonClass);
    } catch (FileNotFoundException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, new Object[] {fileName}, e);
    }
  }
  
  /**
   * @effects 
   *  if filePath represents a valid Json file
   *    read and return {@link JsonObject} or {@link JsonArray} it
   *  else
   *    throw {@link NotPossibleException}
   * @version 5.4
   */
  public static <T extends JsonValue> T readJSonFile( 
      Class<T> jsonClass, String filePath) 
      throws NotPossibleException {
    InputStream ins;
    try {
      if (isFileUrl(filePath)) {
        // jar file path
        ins = readJarFileEntry(filePath);
      } else {
        // normal path
        ins = getFileInputStream(filePath);
      }

      return readJSonFile(ins, jsonClass);
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, e, new Object[] {filePath});
    }
  }
  
  /**
   * @effects 
   *  if fileName represents a valid Json file
   *    read and return {@link JsonObject} representing it
   *  else
   *    throw {@link NotPossibleException}
   * @version 5.4
   */
  public static JsonObject readJSonObjectFile(Class c, String fileName) throws NotPossibleException {
    InputStream ins;
    try {
      ins = getFileInputStream(c, fileName);
      JsonReader reader = Json.createReader(ins); 
      JsonObject json = reader.readObject();
      return json;
    } catch (FileNotFoundException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, new Object[] {fileName}, e);
    }

  }

  /**
   * @effects 
   *  if exists files in the specified folder then
   *    return {@link Map}(fileName, filePath) for them
   *  else
   *    return null 
   * @version 5.4
   */
  public static Map<String, String> getFilePaths(String folderPath) {
    File folder = new File(folderPath);
    if (!folder.exists()) return null;
    
    File[] files = folder.listFiles(JavaFileFilter);
    if (files == null || files.length == 0) {
      return null;
    } else {
      Map<String, String> fileMap = new LinkedHashMap<>();
      for (File file : files) {
        fileMap.put(file.getName(), file.getPath());
      }
      return fileMap;
    }
    
  }

  /**
   * @effects 
   *  if subPath is not yet created in dir
   *    create and return its full path (all path elements that have not yet existed will also be created)
   *  else
   *    return the path 
   *    
   *   If fails then return null
   * @version 5.4
   */
  public static String touchPath(String parentDirPath, String subPath) {
    File subPathDir = new File(parentDirPath + File.separator + subPath);
    if (!subPathDir.exists()) {
      boolean dir = subPathDir.mkdirs();
      if (dir) {
        return subPathDir.getPath();
      } else {
        return null;
      }
    } else {
      return subPathDir.getPath();
    }
  }

  /**
   * @effects 
   *  if path is a valid directory path
   *    return true
   *  else 
   *    create the directories in the path  (including all path elements that have not yet)
   *    return true if succeeds, false if fails
   * @version 5.4
   */
  public static boolean touchPath(String path) {
    File pathDir = new File(path);
    if (!pathDir.exists()) {
      boolean dir = pathDir.mkdirs();
      return dir;
    } else {
      return true;
    }
  }

  /**
   * @effects 
   *  Create and return the full path to the Java source file given the root source path, 
   *  the class package name and the class name 
   * @version 5.4
   */
  public static String getJavaFilePath(String rootSrcPath, String pkgName,
      String className) {
    return getPackagePath(rootSrcPath, pkgName) + File.separator +  
        className + ToolkitIO.FILE_JAVA_EXT;
  }

  /**
   * @effects 
   *  return the current work directory of the JVM
   * @version 5.4
   */
  public static String getCurrentDir() {
    return System.getProperty("user.dir");
  }

  /**
   * @effects 
   *  executes <code>command</code> in the system shell using <code>workDir</code> as the working directory
   *  (if specified).
   *  Returns <code>true</code> if succeeds, <code>false</code> if otherwise
   *  
   *  <p>System shell (Windows or Linux) is determined based on the OS's feature. 
   */
  public static boolean executeSysCommand(File workDir, String command) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder();
      // -c: command
      // -i: interactive shell (strictly not necessary  but needed to read ALL PATH info of the system)
      //    helps avoid command not found error for 'npx'
      if(File.separatorChar=='\\') {  // windows
    	  processBuilder.command("cmd", "/c", command);
      }else { // linux
    	  processBuilder.command("bash", "-ci", command);
      }
      if (workDir != null)
          processBuilder.directory(workDir);
      
      Process process = processBuilder.start();
      
      printStream(process.getInputStream(), System.out);
      
      int exitCode = process.waitFor();
      
      if (exitCode > 0) {
        // read error stream
        printStream(process.getErrorStream(), System.out);
      }
      
      return (exitCode == 0);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  /**
   * @effects 
   *  if there are buffered content in <code>instream</code>
   *    print each line to <code>out</code>
   *  else
   *    do nothing
   *  
   *  <p>Prints stack trace if an exception occurs while processing <code>instream</code>
   */
  public static void printStream(InputStream instream, PrintStream out) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
    String line = "";
    try {
      while ((line = reader.readLine()) != null) {
        out.println(line);
      }
    } catch (IOException e) {
      out.println(getStackTrace(e, ENCODE_UTF8));
    }
  }

  /**
   * @effects 
   *  if <code>dir</code> contains <code>resource</code>
   *    return true
   *  else
   *    return false
   * @version 5.4.1
   */
  public static boolean dirContains(File dir, String resource) {
    if (dir == null) return false;
    File[] content = dir.listFiles();
    if (content != null) {
      for (File f : content) {
        if (f.getName().equalsIgnoreCase(resource)) {
          return true;
        }
      }
    }
    
    return false;
  }

  /**
   * @effects 
   *  displays the requested prompt message and wait for user to enter any key.
   *  
   * @version 5.4.1
   */
  public static void promptAny(String string) {
    System.out.print(string);
    Scanner inputScanner = getInputScanner();
    inputScanner.nextLine();
  }

  /**
   * @effects 
   *  if InputScanner has not been initialised
   *    initialise it to wrap arround System.in
   *    return InputScanner
   *  else
   *    do nothing
   * @version 5.4.1
   * 
   */
  private static Scanner getInputScanner() {
    if (InputScanner == null) {
      InputScanner = new Scanner(System.in);
    }
    
    return InputScanner;
  }
  
//  /**
//   * @effects 
//   *  Determine (in a most efficient way possible) the size of a {@link File} encapsulated in {@link FileInputStream} 
//   *  <tt>fins</tt> in the number of bytes
//   *  
//   *  <p>if file is empty return 0
//   * @version 3.2
//   */
//  public static int getFileSize(FileInputStream fins) {
//  }
}
