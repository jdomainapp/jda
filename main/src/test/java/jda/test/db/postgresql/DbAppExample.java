package jda.test.db.postgresql;

public class DbAppExample {
  
  // application main method
  public static void main(String[] args) {
    // general tasks: preparation
    DBApp dba = null;
    dba = new DBApp(DBApp.DRIVER_POSTGRESQL);

    // connect to database
    boolean ok = dba.connect("northwind");
    /** alternative: connect using specific username, password
     * (requires the updated library file)
    String dbName = "northwind";
    String userName = "postgres";
    String password = "postgres";
    boolean ok = dba.connect(dbName, userName, password);
    */

    if (!ok)
      System.exit(1);
      
    // application specific tasks
    try {
      String sql = "Select * from Customers";
      
      System.out.println("Executing query: " + sql);
      
      // execute SQL statement to get result
      String result = dba.select(sql);
      
      //System.out.println(result);
      
      // write result to file
//      String userDir = System.getProperty("user.dir");
//      String fileChar = System.getProperty("file.separator");
//      String file = userDir+fileChar+"sqloutput.html";
      
      System.out.println(result);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // general tasks: close
    dba.close();
  }
}
