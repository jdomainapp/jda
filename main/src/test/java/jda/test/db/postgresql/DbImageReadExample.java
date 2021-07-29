package jda.test.db.postgresql;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.postgresql.largeobject.LargeObjectManager;

import jda.modules.common.exceptions.NotPossibleException;

public class DbImageReadExample {
  public static void main(String[] args) {
    DBApp dba = null;
    dba = new DBApp(DBApp.DRIVER_POSTGRESQL);

    // connect to database
    String dbName = "test";
    String userName = "postgres";
    String password = "postgres";
    boolean ok = dba.connect(dbName, userName, password);

    if (!ok) {
      System.err.println("Failed to connect to database...");
      System.exit(1);
    }

    // read image
    // Get the Large Object Manager to perform operations with

    String fileName = DbImageWriteExample.fileName;
    try {
      PreparedStatement ps = dba.prepareStatement("SELECT img FROM images WHERE name = ?");
      ps.setString(1, fileName);
      
      ResultSet rs = ps.executeQuery();
  
      LargeObjectManager lobj = dba.getLargeObjectManager();
  
      while (rs.next()) {
        // Open the large object for reading
        /*
        long oid = rs.get(1);
        LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
  
        // Read the data
        byte buf[] = new byte[obj.size()];
        obj.read(buf, 0, obj.size());
        // Do something with the data read here
        
        InputStream is  = obj.getInputStream();

        // Close the object
        obj.close();
        */
        InputStream is = rs.getBinaryStream(1);
        
        //ImageIcon img = inputStreamToImage(is);
        
        //System.out.printf("read db image: %s%n", img);
        
        // write to file 
        String sep = File.separator;
        String outFileName = System.getProperty("java.io.tmpdir") + sep + fileName;
        FileOutputStream fout = new FileOutputStream(outFileName, true);
        byte[] buff = new byte[is.available()];
        while (is.read(buff, 0, buff.length) > -1) {
          fout.write(buff);
        }
        
        System.out.printf("written out to file : %s%n", outFileName);

        fout.close();
        is.close();
      }
      
      rs.close();
      ps.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * @requires 
   *  <tt>ins</tt> represents bytes of an ImageIcon
   *  
   * @effects return an <tt>ImageIcon</tt> from the bytes read from <tt>ins</tt>
   * @version 2.7.4
   */
  private static ImageIcon inputStreamToImage(InputStream ins) throws NotPossibleException {
    try {
      BufferedImage bimg = ImageIO.read(ins);

      if (bimg != null) {
        ImageIcon img = new ImageIcon(bimg);
      
        return img;
      } else {
        return null;
      }
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
          e, new Object[] {"inputStreamToImage", "", ""});
    }
  }
}
