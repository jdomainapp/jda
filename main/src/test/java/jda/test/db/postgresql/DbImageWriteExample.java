package jda.test.db.postgresql;

import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;

public class DbImageWriteExample {
  public static final String fileName = "person_1.jpg";
  
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
    
    // insert image into database table
    String sep = File.separator;
    String imgDir = System.getProperty("user.home") + sep + "Pictures" + sep + "persons" + sep;
    
    File file = new File(imgDir+fileName);
    
    try {
      FileInputStream fis = new FileInputStream(file);
  
      String sql = "INSERT INTO images VALUES (?, ?)";
      PreparedStatement ps = dba.prepareStatement(sql);
      
      ps.setString(1, file.getName());
      ps.setBinaryStream(2, fis, (int)file.length());
      dba.executeUpdate(ps);
      
      System.out.printf("Written file '%s' to db%n", fileName);

      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
