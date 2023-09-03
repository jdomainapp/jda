package jda.modules.common.zipextractor;

import jda.modules.common.exceptions.NotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @overview 
 *  Extracts all compressed files (type: zip or rar) that are contained in 
 *  a given folder  
 *  
 * @author dmle
 */
public class ZipExtractor {

  private static final Logger logger = LoggerFactory.getLogger(ZipExtractor.class);

  /**
   * @effects 
   *  extract the specified Zip file <tt>zipFile</tt> to a sub-folder of <tt>toFolder</tt> that has the same name as the file's name.
   *  
   *  <p>Return the extracted folder as {@link File}.
   */
  public static File extractFile(final File fileZip, final File toFolder) throws NotPossibleException {
    final String sep = File.separator;
    String fileName = fileZip.getName();

    try {
      ZipFile zipFile = new ZipFile(fileZip);
      if (zipFile.size() == 0) {
        // empty zip file
        throw new NotPossibleException(NotPossibleException.Code.FOLDER_EMPTY, new String[] { fileZip.getPath()});
      } else { // non-empty zip file
        String ext = fileName.substring(fileName.lastIndexOf("."));

        // the extracted folder
        File fd = new File(toFolder, fileName.substring(0, fileName.lastIndexOf(ext)));
        fd.mkdir();

        // extract zip into the extracted folder
        logger.info("Extracting: " + fileZip.getPath() + " to: " + fd);

        String zen = null;
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry ze = (ZipEntry) entries.nextElement();
          if (!ze.isDirectory()) {  // ze is a file
            zen = ze.getName();

            // determine if zen contains dir path
            int sepIndx = zen.lastIndexOf(sep);
            if (sepIndx > -1) {
              // zen contains dir path: make sure that all dirs have been created
              String dirPath = zen.substring(0, sepIndx);
              File dirPathDir = new File(fd + sep + dirPath);
              if (!dirPathDir.exists())
                dirPathDir.mkdirs();
            }

            logger.debug("> " + zen);
            // write zip entry to a file in folder fd
            InputStream is = zipFile.getInputStream(ze);
            BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(fd + sep
                + zen));
            byte[] data;
            data = new byte[1000];
            int count;
            while ((count = is.read(data, 0, 1000)) != -1) {
              fout.write(data, 0, count);
            }
            fout.flush();
            fout.close();
          } else {  // ze is a directory
            logger.debug("Directory " + ze.getName());
            File zd = new File(fd + sep + ze.getName());
            zd.mkdir();
            logger.debug("Created");
          }
        } // end while loop for extraction

        return fd;

      }
    } catch (Exception e) {
      // error zip file
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, new String[] {ZipExtractor.class.getSimpleName(), "extractZipFile", fileName});
    }
  }
}
