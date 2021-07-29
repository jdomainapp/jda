package jda.test.db.postgresql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.postgresql.largeobject.LargeObjectManager;

/**
 * @overview 
 *  A database interface program that perform common database interface tasks.
 *  An application needs only to invoke methods of this program, passing suitable
 *  parameters.
 *  
 * @usage 
 *  The following code example demonstrates how to use this class.
 *  
 *  <p>First, create a DBApp object for postgreSQL database(s): 
 *  <br><pre>DBApp da = new DBApp({@link DBApp#DRIVER_POSTGRESQL});</pre>
 *  
 *  <p>To create a database connection to a database named <tt>northwind</tt>:
 *  <br><pre>
 *    boolean success = da.connect("northwind");
 *    if (!success)
 *      // stop 
 *      System.exit(1)
 *    else
 *      // continue with the application
 *  </pre>
 *  
 *  <p>To create a table named <tt>Customers</tt> in the database:
 *  <br><tt>success = da.createTable("Customers");</tt>
 *  
 *  <p>To insert data into the database using an INSERT SQL statement <tt>sql</tt>:
 *  <br><tt>success = da.insert(sql);</tt>
 *
 *  <p>To read database records from the database using a SELECT SQL statement <tt>sql</tt>:
 *  <br><tt>String resultString = da.select(sql);</tt>
 *  
 *  <p>To delete data from a table named <tt>Customers</tt> from the database:
 *  <br><tt>success = da.delete("Customers");</tt>
 *  
 *  <p>To delete the table named <tt>Customers</tt> from the database:
 *  <br><tt>success = da.deleteTable("Customers");</tt>
 *  
 *  <p>To close the database connection after finished:
 *  <br><tt>da.close();</tt>
 *  
 * @author dmle
 */
public class DBApp {
  
  private String dbDriver;
  
  protected Connection conn;
  
  /**
   * the database driver for JavaDB
   */
  public static final String DRIVER_JAVADB = "jdbc:derby:%s;create=true";

  /**
   * the database driver for PostgreSQL database
   */
  public static final String DRIVER_POSTGRESQL = "jdbc:postgresql:%s";
  
  private static final String SQL_CREATE_TABLES = "create_tables.sql";
  private static final String SQL_POPULATE_TABLES = "populate_tables.sql";
  private static final String SQL_QUERY_TABLES = "queries.sql";

  /**
   * @effects 
   *  create a new <tt>DBApp</tt> object for <tt>driver</tt>
   *  
   *  <p>if failed to load the database driver associated to 
   *  <tt>driver</tt> then display an error message and throw <tt>InternalError</tt>
   */
  public DBApp(String driver) throws InternalError {
    this.dbDriver = driver;
    try {
      if (dbDriver == DRIVER_JAVADB) {
        // driver is loaded by default
      } else if (dbDriver == DRIVER_POSTGRESQL) {
        // load the driver
        Class.forName("org.postgresql.Driver");
      }
    } catch (ClassNotFoundException e) {
      System.err.println("Failed to load database driver");
      e.printStackTrace();
      throw new InternalError();
    }
  }
  
  /**
   * This constructor is used by sub-types.
   * 
   * @effects 
   *  initialise this with a default state
   */
  protected DBApp() {
    //
  }
  
  /**
   * @requires 
   *  database <tt>dbName</tt> must have already been created.
   * @modifies <tt>this</tt>
   * @effects Connect to database <tt>dbName</tt> using the database driver  
   *   <tt>dbDriver</tt>. 
   *   
   *   <p><pre>If succeeded
   *          return true
   *        else
   *          display an error message
   *          return false</pre>
   */
  public boolean connect(String dbName) {
    // use default user name, password
    return connect(dbName, null, null);
//    Properties props = new Properties();
//    // set up properties, e.g. user name/password
//    props.setProperty("user","postgres");
//    props.setProperty("password", "postgres");
//    //props.setProperty("ssl","true");
//    
//    String connectString = String.format(dbDriver, dbName);
//    
//    try {
//      conn = DriverManager.getConnection(connectString, props);
//      System.out.println("Connected to database " + dbName);
//      return true;
//    } catch (SQLException e) {
//      System.err.println("Error: failed to connect to database " + dbName);
//      e.printStackTrace();
//      return false;
//    }
  }

  /**
   * @requires 
   *  database <tt>dbName</tt> must have already been created.
   * @modifies <tt>this</tt>
   * @effects Connect to database <tt>dbName</tt> using the database driver  
   *   <tt>dbDriver</tt>. 
   *   
   *   <p><pre>If succeeded
   *          return true
   *        else
   *          display an error message
   *          return false</pre>
   */
  public boolean connect(String dbName, String userName, String password) {
    if (userName == null)
      userName = "postgres";
      
    if (password == null)
      password = "postgres";
    
    Properties props = new Properties();
    // set up properties, e.g. user name/password
    props.setProperty("user",userName);
    props.setProperty("password", password);
    //props.setProperty("ssl","true");
    
    String connectString = String.format(dbDriver, dbName);
    
    try {
      conn = DriverManager.getConnection(connectString, props);
      System.out.println("Connected to database " + dbName);
      return true;
    } catch (SQLException e) {
      System.err.println("Error: failed to connect to database " + dbName);
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Return the current database connection.
   * 
   * @effects
   *    return conn
   */
  public Connection getConnection() {
    return conn;
  }
  
  /**
   * @requires
   *  <tt>sql</tt> is a correct CREATE TABLE statement
   * @effects create a table using CREATE TABLE statement <tt>sql</tt>. 
   *  
   *   <p><pre>If succeeded
   *          return true
   *        else
   *          display an error message
   *          return false</pre>
   */
  public boolean createTable(String sql) {
    Statement s = null;
    try {
      s = conn.createStatement();
      s.executeUpdate(sql);
      return true;
    } catch (SQLException e) {
      System.err.println("Error: failed to create table");
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (s != null) 
          s.close(); 
      } catch (SQLException e) {
        // ignore
      }
    }
  }
  
  /**
   * @effects drop table <tt>tableName</tt> from the database.  
   * 
   *     <p><pre>If succeeded
   *          return true
   *        else
   *          display an error message
   *          return false</pre>
   */
  public boolean deleteTable(String tableName) {
    String sql = "drop table " + tableName;
    Statement s = null;
    try {
      s = conn.createStatement();
      s.executeUpdate(sql);
      return true;
    } catch (SQLException e) {
      System.err.println("Error: failed to delete table: " + tableName);
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (s != null) 
          s.close(); 
      } catch (SQLException e) {
        // ignore
      }
    }
  }
  
  /**
   * @requires
   *  typeName != null /\ typeClassName != null
   * @effects create user-defined type <tt>typeName</tt> in the database that 
   *  references the class <tt>typeClassName</tt>.
   *  
   *     <p><pre>If succeeded
   *          return true
   *        else
   *          display an error message
   *          return false</pre>
   *  
   */
  public boolean createType(String typeName, String typeClassName) {
    StringBuffer sb = new StringBuffer();
    sb.append("CREATE TYPE ").append(typeName).append(" ").
    append("EXTERNAL NAME '").append(typeClassName).append("'").append(" ").
    append("LANGUAGE JAVA");
    
    String sql = sb.toString();
    
    // System.out.println("sql: " + sql);
    Statement s = null;
    try {
      s = conn.createStatement();
      s.executeUpdate(sql);
      return true;
    } catch (SQLException e) {
      System.err.println("Error: failed to create type: " + typeName);
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (s != null) 
          s.close(); 
      } catch (SQLException e) {
        // ignore
      }
    }
  }
  
  /**
   * @requires
   *  typeName != null
   * @effects drop type <tt>typeName</tt> from the database
   *     <p><pre>If succeeded
   *          return true
   *        else
   *          display an error message
   *          return false</pre>
   */
  public boolean deleteType(String typeName) {
    String sql = "drop type " + typeName + " RESTRICT";
    
    Statement s = null;
    try {
      s = conn.createStatement();
      s.executeUpdate(sql);
      return true;
    } catch (SQLException e) {
      System.err.println("Error: failed to delete type: " + typeName);
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (s != null) 
          s.close(); 
      } catch (SQLException e) {
        // ignore
      }
    }
  }
  

  /**
   * @effects Executes each statement in <tt>fname</tt> to over the database
   *          connected to by <tt>this.conn</tt>, throwing
   *          <tt>SQLException</tt> if an error occured.
   * @modifies if <tt>resultMap != null</tt> and there are result sets then
   *           adds <tt><sql,ResultSet></tt> entries to <tt>resultMap</tt>
   */
  private void executeStatementsFromFile(String fname,
      Map<String, String> resultMap) throws SQLException {
    System.out.println("------ Executing " + fname + " ------");

    URL fileIn = DBApp.class.getResource(fname);
    if (fileIn != null) {
      String sql = null;

      Statement s = null;
      try {

        BufferedReader in = new BufferedReader(new FileReader(new File(
            fileIn.getPath())));
        s = conn.createStatement();
        StringBuffer sb = new StringBuffer();
        ResultSet rs = null;
        while ((sql = in.readLine()) != null) {
          sql = sql.trim();
          sb.append(sql);

          if (sql.endsWith(";")) {
            sql = sb.toString();
            System.out.println("-> Statement: \n" + sql);
            try {
              s.execute(sql.substring(0, sql.length() - 1));
              // if result map is specified...
              if (resultMap != null) {
                rs = s.getResultSet();
                if (rs != null) {
                  resultMap.put(sql, resultSetToString(rs));
                }
              }
            } catch (SQLException e) {
              System.err.println(e);
            }
            sb = new StringBuffer();
          } else {
            sb.append("\n");
          }
        }
      } catch (FileNotFoundException e) {
        // should not happen
      } catch (IOException e) {
        // should not happen
        System.err.println("Faild to read file " + fname + ": " + e);
      } finally {
        if (s != null)
          s.close();
      }

    } else {
      System.err.println("File " + fname + " does not exist");
    }
  }

  /**
   * @effects 
   *  executes the SELECT statement <tt>sql</tt>. 
   * 
   *  <p><pre>If succeeded 
   *    return a <tt>String</tt> containing an <b>Html</b> table representation of the result set; or  
   *    "empty" if the result set is empty
   *  else 
   *    display an error message
   *    return null</pre>
   */
  public String select(String sql) {
    Statement s = null;
    try {
      s = conn.createStatement();
      ResultSet rs = s.executeQuery(sql);
      String rss = resultSetToHtml(rs);
      return rss;
    } catch (SQLException e) {
      System.err.println("Error: failed to execute SELECT statement");
      e.printStackTrace();
      return null;
    } finally {
      try {
        if (s != null) 
          s.close(); 
      } catch (SQLException e) {
        // ignore
      }
    }
  }
  
  /**
   * @effects insert data into a table using INSERT statement <tt>sql</tt>. 
   *  <p><pre>If succeeded 
   *    return true
   *  else 
   *    display an error message
   *    return false</pre>
   *  
   */
  public boolean insert(String sql) {
    Statement s = null;
    try {
      s = conn.createStatement();
      s.executeUpdate(sql);
      return true;
    } catch (SQLException e) {
      System.err.println("Error: failed to execute INSERT statement");
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (s != null) 
          s.close(); 
      } catch (SQLException e) {
        // ignore
      }
    }
  }

  /**
   * @effects update data in a table using UPDATE statement <tt>sql</tt>. 
   *  <p><pre>If succeeded 
   *    return true
   *  else 
   *    display an error message
   *    return false</pre>
   *  
   */
  public boolean update(String sql) {
    Statement s = null;
    try {
      s = conn.createStatement();
      s.executeUpdate(sql);
      return true;
    } catch (SQLException e) {
      System.err.println("Error: failed to execute UPDATE statement");
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (s != null) 
          s.close(); 
      } catch (SQLException e) {
        // ignore
      }
    }
  }
  
  /**
   * @effects delete <b>All</b> data from a table named <tt>tableName</tt>. 
   *  <p><pre>If succeeded 
   *    return true
   *  else 
   *    display an error message
   *    return false</pre>
   */
  public boolean deleteAll(String tableName) {
    String sql = "delete from " + tableName;
    Statement s = null;
    try {
      s = conn.createStatement();
      s.executeUpdate(sql);
      return true;
    } catch (SQLException e) {
      System.err.println("Error: failed to delete data from table: " + tableName);
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (s != null) 
          s.close(); 
      } catch (SQLException e) {
        // ignore
      }
    }
  }

  /**
   * @effects delete <b>some</b> data rows from a table using DELETE statement <tt>sql</tt>. 
   *  <p><pre>If succeeded 
   *    return true
   *  else 
   *    display an error message
   *    return false</pre>
   */
  public boolean delete(String sql) {
    Statement s = null;
    try {
      s = conn.createStatement();
      s.executeUpdate(sql);
      return true;
    } catch (SQLException e) {
      System.err.println("Error: failed to delete data from table");
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (s != null) 
          s.close(); 
      } catch (SQLException e) {
        // ignore
      }
    }
  }
  
  /**
   * @effects Return a string containing a sequence of rows of <tt>rs</tt>, one per line, 
   *  or return <tt>empty</tt> if no rows are found.
   */
  private String resultSetToString(ResultSet rs) throws SQLException {
    ResultSetMetaData meta = rs.getMetaData();
    int cols = meta.getColumnCount();

    Object v;
    StringBuffer sb = new StringBuffer();
    int size = 0;
    int colType;
    String vstr;
    while (rs.next()) {
      size++;
      for (int i = 1; i <= cols; i++) {
        v = rs.getObject(i);
        colType = meta.getColumnType(i);
        vstr = toString(v, colType);
        sb.append(vstr);
        if (i < cols)
          sb.append(",");
      }
      sb.append("\n");
    }

    if (size == 0) {
      return "Empty";
    } else {
      return sb.toString();
    }
  }

  /**
   * @effects Return an Html table string containing a 
   *  header row and a sequence of rows in <tt>rs</tt> one per line  
   *  or "empty" if <tt>rs</tt> is empty.
   */
  private String resultSetToHtml(ResultSet rs) throws SQLException {
    ResultSetMetaData meta = rs.getMetaData();
    int cols = meta.getColumnCount();

    Object v;
    StringBuffer sb = new StringBuffer();
    sb.append("<table border=1>");
    
    // table headers
    String colName;
    sb.append("<tr>");
    sb.append("<th>#</th>");
    for (int colInd = 1; colInd <= cols; colInd++) {
      colName = meta.getColumnName(colInd);
      sb.append("<th>").append(colName).append("</th>");
    }
    sb.append("</tr>").append("\n");
    
    // table data rows
    int count = 0;
    int colType;
    String vstr;
    while (rs.next()) {
      count++;
      // process next row
      sb.append("<tr>");
      sb.append("<td>").append(count).append("</td>");
      for (int i = 1; i <= cols; i++) {
        v = rs.getObject(i);
        colType = meta.getColumnType(i);
        vstr = toString(v, colType);
        sb.append("<td>").append(vstr).append("</td>");
      }
      sb.append("</tr>");
      sb.append("\n");
    }

    if (count == 0) {
      sb.append("<tr><td>empty</td></tr>");
    }
    
    sb.append("</table>");
    return sb.toString();
  }

  /**
   * @effects convert <tt>v</tt> to a <tt>String</tt> based on the column type <tt>colType</tt> 
   */
  protected String toString(Object v, int colType) {
    if (v == null)
      return "null";
    
    switch (colType) {
      case Types.BLOB:
        return ((Blob)v).toString();
      case Types.VARBINARY: 
        return new String((byte[])v);
      default:
        return v.toString();
    }
  }
  
  /**
   * @effects close connection to database
   */
  public void close() {
    try {
      this.conn.close();
    } catch (SQLException e) {
      // ignore
    }
  }

  
  /**
   * @effects Executes each statement in <tt>this.SQL_CREATE_TABLES</tt> to
   *          create tables in the database connected to by
   *          <tt>this.conn</tt>, throwing <tt>SQLException</tt> if an
   *          error occured.
   */
  private void createTables() throws SQLException {
    // run the create_tables.sql file
    executeStatementsFromFile(SQL_CREATE_TABLES, null);
  }

  /**
   * @effects Executes each statement in <tt>this.SQL_POPULATE_TABLES</tt>
   *          to insert data into each table in the database connected to by
   *          <tt>this.conn</tt>, throwing <tt>SQLException</tt> if an
   *          error occured.
   */
  private void populateTables() throws SQLException {
    executeStatementsFromFile(SQL_POPULATE_TABLES, null);
  }

  /**
   * @effects Executes each statement in <tt>this.SQL_QUERY_TABLES</tt> to
   *          query data from each table in the database connected to by
   *          <tt>this.conn</tt>, throwing <tt>SQLException</tt> if an
   *          error occured.
   */
  private void queryTables() throws SQLException {
    Map<String, String> resultMap = new HashMap<String, String>();
    executeStatementsFromFile(SQL_QUERY_TABLES, resultMap);
    for (Entry<String, String> e : resultMap.entrySet()) {
      System.out.println("Query: \n" + e.getKey());
      System.out.println("Result:");
      System.out.println(e.getValue());
    }
  }

  /**
   * @effects perform SQL statement <tt>SELECT * from tableName</tt> and
   *    return a <tt>ResultSet</tt> object if succeeds (never <tt>null</tt>), or 
   *    throw <tt>SQLException</tt> if an error occurred.
   */
  private ResultSet queryTable(String tableName) throws SQLException {
    String sql = "Select * from " + tableName;
    Statement s = conn.createStatement();
    ResultSet rs = s.executeQuery(sql);
    return rs;
  }

  /**
   * @effects 
   *  create and return a {@link PreparedStatement} for <tt>sql</tt>
   *   
   *  <p>throws SQLException if failed.
   */
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    if (conn == null)
      return null;
    
    return conn.prepareStatement(sql);
  }

  /**
   * @throws SQLException 
   * @effects 
   *  execute <tt>statement</tt> to update the database and close it 
   *  <p>throws SQLException if failed
   */
  public void executeUpdate(PreparedStatement statement) throws SQLException {
    statement.executeUpdate();
    statement.close();
  }

  public LargeObjectManager getLargeObjectManager() throws SQLException {
    if (conn == null)
      return null;
    
    return ((org.postgresql.PGConnection)conn).getLargeObjectAPI();
  }

}
