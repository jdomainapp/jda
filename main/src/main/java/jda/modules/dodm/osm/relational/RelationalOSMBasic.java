package jda.modules.dodm.osm.relational;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.collection.Map;
import jda.modules.common.collection.MapList;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.common.io.ToolkitIO;
import jda.modules.common.types.Tuple;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.function.AttribFunctor;
import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dodm.DODMToolkit;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.OSM;
import jda.modules.dodm.osm.relational.sql.SqlOp;
import jda.modules.dodm.osm.relational.sql.function.DataSourceFunction;
import jda.modules.dodm.osm.relational.util.DualMap;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.oql.def.AttributeExpression;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.FlexiQuery;
import jda.modules.oql.def.IdExpression;
import jda.modules.oql.def.ObjectAttributeExpression;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.ObjectJoinExpression;
import jda.modules.oql.def.ObjectJoinOnAttributeExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  An abstract implementation of {@link OSM} for relational DBMS. Sub-types of this provide 
 *  specialised behaviours for specific types of RDBMS (e.g. JavaDB, PostgreSQL, etc.) 
 *  
 * @author dmle
 */
public abstract class RelationalOSMBasic extends OSM {
//  private static RelationalOSM instance;
  //private String dbFilePath;

  private Connection conn;

  //TODO: uncomment this for caching
  //private Cache cache;

//  public static final String DB_SCHEMA = "APP";
  //private static final String DBNAME = "myapp";
  //private static final String DBMS = "derby";
  public static final String SQL_CREATE_TABLES = "create_tables"; // should
                                                                  // append .sql
  public static final String SQL_POPULATE_TABLES = "populate_tables"; // should
                                                                      // append
                                                                      // .sql
  public static final String SQL_QUERY_TABLES = "queries"; // append .sql

  // constants fields
  private final static char LF = System.getProperty("line.separator").charAt(0);
  private final static int DEFAULT_LENGTH = 100;
  private static final Class<DAttr> DC = DAttr.class;

  private static boolean debug = Toolkit.getDebug(RelationalOSMBasic.class);
  private boolean oldDebug; // v3.1

//  private RelationalOSM(DomainSchema domainSchema, String dbFilePath)
//      throws DBException {
//    schema = domainSchema;
//    this.dbName = dbFilePath;
//    connect(dbFilePath);
//    
//    //TODO: uncomment this for caching
//    //cache = new Cache();
//  }

  public RelationalOSMBasic(OsmConfig config, DOMBasic dom)
      throws DataSourceException {
    super(config, dom);
    //this.dbFilePath = config.getDataSourceName();
    connect();
    
    //TODO: uncomment this for caching
    //cache = new Cache();
  }

//  /**
//   * @effects returns <code>this</code> (singleton) instance
//   */
//  public static RelationalOSM getInstance(DomainSchema schema) throws DBException {
//    return getInstance(schema, DBNAME);
//  }

//  public static RelationalOSM getInstance(DomainSchema domainSchema, String dbFilePath)
//      throws DBException {
//    if (instance == null) {
//      instance = new RelationalOSM(domainSchema, dbFilePath);
//    }
//
//    return instance;
//  }

//  /**
//   * @effects
//   *  return the data source name of this
//   */
//  public String getDataSourceName() {
//    return dbFilePath;
//  }

  /**
   * @effects 
   *  return a source-specific SELECT SQL query that check in the system catalog for the existence 
   *  of a database schema named <tt>schemaName</tt>
   * @version 3.0
   */
  protected abstract String getQuerySchemaExist(String schemaName);

  /**
   * @effects 
   *  return an SQL SELECT meta query that is used to retrieve names of all relations in the schema 
   *  named <tt>schemaName</tt> of the underlying data source
   * @version 3.0 
   */
  protected abstract String getQueryRelationNames(String schemaName);

  /**
   * @effects 
   *  return an SQL DROP TABLE query that is used to drop the table named <tt>tableName</tt>
   *  
   * @version 3.2
   */
  protected abstract String getQueryDropTable(String tableName);
  
  /**
   * @effects 
   *  create a new <code>Connection</code> to the underlying data source specified by {@link #getConfig()}
   *  <p>throws DataSourceException if failed
   *  
   * @modifies <code>this</code>
   */
  @Override
  public void connect() throws DataSourceException {
    /*v3.0: use config's properties
    Properties connectionProps = new Properties();
    connectionProps.setProperty("create", "true");
    */

    // set up properties, e.g. user name/password
    OsmConfig config = getConfig();

    Properties connectionProps = config.getProperties();
    
    /*v3.0: use data source URL without properties, b/c these are already set up 
     * in connectionProps (above)
    String connectionURL = config.getProtocolURL();
     */
    String connectionURL = config.getDataSourceURL();
    
    try {
      conn = DriverManager.getConnection(
          //"jdbc:" + DBMS + ":" + dbFilePath
          // + ";create=true", connectionProps
          connectionURL, connectionProps
          );
      if (debug)
        System.out.println("Connected to database " + connectionURL);
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_CONNECT, e,
          new Object[] {connectionURL, e.getMessage()});
    }
  }

  /**
   * @effects 
   *  if connection to data source is valid
   *    return true
   *  else
   *    return false
   */
  @Override
  public boolean isConnected() {
    return (conn != null);
  }
  
  /**
   * @effects closes the active connection
   */
  @Override
  public void disconnect() {
    try {
      conn.close();
    } catch (SQLException e) {
      // ignore
    }
  }

//  /**
//   * @effects Returns a new <code>Connection</code> object to database
//   *          <code>dbFilePath</code> on host <code>host</code> at port
//   *          <code>port</code>. Database <code>dbFilePath</code> is created if not
//   *          yet exists.
//   * @modifies <code>this</code>
//   */
//  private void connectServer(String host, int port, String dbName)
//      throws DataSourceException {
//    Properties connectionProps = new Properties();
//    // set up properties, e.g. user name/password
//    try {
//      conn = DriverManager.getConnection("jdbc:" + DBMS + "://" + host
//          + ":port/" + dbName + ";create=true", connectionProps);
//
//      if (debug)
//        System.out.println("Connected to database " + dbName);
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_CONNECT,
//          "Failed to connect to db", e.getMessage());
//    }
//  }

  /**
   * @effects Executes each statement in <code>this.SQL_CREATE_TABLES</code> to
   *          create tables in the database connected to by
   *          <code>this.conn</code>, throwing <code>SQLException</code> if an
   *          error occured.
   */
  @Override
  public void createSchemaFromFile(String sqlFile) throws DataSourceException {
    // run the create_tables.sql file
    executeStatementsFromFile(sqlFile, null);
  }

  /**
   * @effects Executes each statement in <code>this.SQL_POPULATE_TABLES</code>
   *          to insert data into each table in the database connected to by
   *          <code>this.conn</code>, throwing <code>SQLException</code> if an
   *          error occured.
   */
  @Override
  public void createObjectsFromFile(String sqlFile) throws DataSourceException {
    executeStatementsFromFile(sqlFile, null);
  }

  /**
   * @effects Executes each statement in <code>this.SQL_QUERY_TABLES</code> to
   *          query data from each table in the database connected to by
   *          <code>this.conn</code>, throwing <code>SQLException</code> if an
   *          error occured.
   */
  public void queryTables(String sqlFile) throws DataSourceException {
    Map<String, String> resultMap = new Map<String, String>();
    executeStatementsFromFile(sqlFile, resultMap);
    for (Entry<String, String> e : resultMap.entrySet()) {
      System.out.println("Query: \n" + e.getKey());
      System.out.println("Result:");
      System.out.println(e.getValue());
    }
  }

  /**
   * @effects invokes <code>executeStatementsFromFile(filePath,null)</code>.
   */
  public void executeStatementsFromFile(String filePath) throws DataSourceException {
    executeStatementsFromFile(filePath, null);
  }

  /**
   * @effects Executes each statement in <code>filePath</code> to over the
   *          database connected to by <code>this.conn</code>, throwing
   *          <code>SQLException</code> if an error occured.
   * @modifies if <code>resultMap != null</code> and there are result sets then
   *           adds
   * 
   *           <pre>
   * <sql,ResultSet>
   * </pre>
   * 
   *           entries to <code>resultMap</code>
   * 
   */
  private void executeStatementsFromFile(String filePath,
      Map<String, String> resultMap) throws DataSourceException {

    if (debug)
      System.out.println("------ Executing " + filePath + " ------");

    // URL fileIn = DBToolKit.class.getResource(fname);
    String sql = null;

    Statement s = null;
    try {

      BufferedReader in = new BufferedReader(new FileReader(new File(filePath)));
      s = conn.createStatement();
      StringBuffer sb = new StringBuffer();
      ResultSet rs = null;
      while ((sql = in.readLine()) != null) {
        sql = sql.trim();
        sb.append(sql);

        if (sql.endsWith(";")) {
          sql = sb.toString();
          if (debug)
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
    } catch (SQLException e) {
      // e.printStackTrace();
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY, e, 
          new Object[] {sql});
    } catch (FileNotFoundException e) {
      // should not happen
    } catch (IOException e) {
      // should not happen
      System.err.println("Faild to read file " + filePath + ": " + e);
    } finally {
      try {
        if (s != null)
          s.close();
      } catch (SQLException e) {
        //
      }
    }
  }

  /**
   * @effects returns <code>true</code> if a table with name
   *          <code>tableName</code> exists in the database schema named
   *          <code>dbSchema</code> , else returns <code>false</code>
   * @version 
   * - 3.0: improved to support data source-specific query
   */
  @Override
  public boolean exists(String dbSchema, String tableName) {
    String sql = getQueryRelationNames(dbSchema);
    
    ResultSet rs = null;
    try {
      rs = executeQuery(sql);

      if (rs != null) {
        String tname;
        while (rs.next()) {
          tname = rs.getString(1);
          if (tname.equalsIgnoreCase(tableName)) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      // something wrong, but dont care
    } finally {
      try {
        if (rs != null)
          rs.close();
      } catch (Exception e) {
      }
    }
    return false;
  }
  

  /**
   * @effects returns <code>List<Map></code> object, each entry of which is a
   *          <code>Map<String,Object></code>, which represents a record in the
   *          result of executing <code>sql</code>. A map entry maps a column
   *          name to its value.
   * @deprecated use the method {@link #queryAsMap(String)} instead
   */
  public List<Map<String, Object>> query(String sql) throws DataSourceException {
    Statement s = null;
    ResultSet rs = null;

    try {
      s = conn.createStatement();
      rs = s.executeQuery(sql);

      List<Map<String, Object>> recs = new ArrayList<Map<String, Object>>();
      int cols = rs.getMetaData().getColumnCount();
      Object v;
      Map<String, Object> rec;
      while (rs.next()) {
        rec = new Map<String, Object>();
        for (int i = 1; i <= cols; i++) {
          v = rs.getObject(i);
          rec.put(rs.getMetaData().getColumnName(i).toLowerCase(), v);
        }
        recs.add(rec);
      }

      s.close();
      if (recs.isEmpty())
        return null;
      else
        return recs;
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY, e, 
          new Object[] {sql});
    }
  }

  /**
   * @effects returns a <code>MapList</code> object, the keys of which are the
   *          SQL output column names and the values of which are
   *          <code>List</code>s of values of the columns
   */
  public MapList queryAsMap(String sql) throws DataSourceException {
    Statement s = null;
    ResultSet rs = null;

    try {
      s = conn.createStatement();
      rs = s.executeQuery(sql);

      MapList recs = new MapList();
      int cols = rs.getMetaData().getColumnCount();
      Object v;
      int rowIndex = 0;
      while (rs.next()) {
        // rec = new Map<String, Object>();
        for (int i = 1; i <= cols; i++) {
          v = rs.getObject(i);
          // rec.put(rs.getMetaData().getColumnName(i).toLowerCase(), v);
          recs.put(rs.getMetaData().getColumnName(i).toLowerCase(), v, rowIndex);
        }
        rowIndex++;
        // recs.add(rec);
      }

      s.close();
      if (recs.isEmpty())
        return null;
      else
        return recs;
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY, e, 
          new Object[] {sql});
    }
  }

  /**
   * @effects Executes <code>sql</code> over the database connected to by
   *          <code>this.conn</code>. If successful then returns a <code>ResultSet</code> object, 
   *          otherwise throws
   *          <code>SQLException</code>.
   *          
   *          <p>This method never returns <tt>null</tt>. Caller should invoke <tt>rs.next()</tt> to check 
   *          if the result set is empty.
   */
  private ResultSet executeQuery(String sql) throws DataSourceException {
    try {
      // v2.7.4: add statement options so that we can invoke first(), etc. on the ResultSet
      //Statement s = conn.createStatement();
      Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
      
      ResultSet rs = s.executeQuery(sql);
      // String rss = resultSetToString(rs);
      // -- dont close statement here --> this s.close();
      return rs;
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY, e,
          new Object[] {sql});
    }
  }

  private void executeUpdate(String sql) throws DataSourceException {
    try {
      Statement s = conn.createStatement();
      s.executeUpdate(sql);
      s.close();
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_UPDATE_OBJECT_BY_QUERY, e,
          new Object[]{sql, ""});
    }
  }

  /**
   * @effects 
   *  execute the parameterised SQL query <tt>sql</tt> for the domain object <tt>o</tt>
   *  of the domain class <tt>c</tt>, with the values of the attributes contained 
   *  in <tt>updateAttributes</tt>.
   *  
   *  <p>Throws DBException if failed to execute the SQL statement. 
   */
  private void executeParameterisedUpdate(String sql, Class c, List<Field> updateAttributes, Object o) 
      throws DataSourceException {
    
    PreparedStatement s;
    DAttr dc;
    Type type;
    int index=1;
    Object d = null;
    Field f = null;
    try {
      s = conn.prepareStatement(sql);

      for (int i = 0; i < updateAttributes.size();i++) {
        f = updateAttributes.get(i);
        
        dc = f.getAnnotation(DC);
        
        // field type is either the native type or
        // the type specified in the DomainConstraint annotation of the field
        type = dc.type();
        d = dom.getDsm().getAttributeValue(f, o);
        
        //if (d != null) {          
        javaToSQL(s, index, type, f.getType(), d);
        index++;
        //}
      }
      
      s.execute();
      
      s.close();
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_UPDATE_OBJECT_ATTRIB, e,
          new Object[] { sql, o, index, ((f!=null) ? f.getName() : null), d});
    }
  }
  
//  /**
//   * @effects 
//   *  execute the parameterised SQL query <tt>sql</tt> for the domain object <tt>o</tt>
//   *  of the domain class <tt>c</tt>. 
//   *  
//   *  <p>Throws NotFoundException if <tt>c</tt> is not a domain class, DBException if failed to execute
//   *  the SQL statement. 
//   */
//  private void executeParameterisedInsert(String sql, Class c, 
//      Object o) throws NotFoundException, 
//    DBException {
//    List fields = null;
//    Type type;
//    DomainConstraint dc;
//    int index=1;
//    try {
//      PreparedStatement s = conn.prepareStatement(sql);
//      
//      fields = schema.getDsm().getRelationalAttributes(c);
//
//      if (fields == null)
//        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
//            "Không tìm thấy lớp: {0}", c);
//
//      for (int i = 0; i < fields.size(); i++) {
//        Field f = (Field) fields.get(i);
//        dc = f.getAnnotation(DC);
//        
//        // field type is either the native type or
//        // the type specified in the DomainConstraint annotation of the field
//        type = dc.type();
//        Object d = schema.getDsm().getAttributeValue(f, o);
//        
//        if (d != null) {          
//          javaToSQL(s, index, type, f.getType(), d);
//          index++;
//        }
//      }
//      
//      s.execute();
//      
//      s.close();
//    } catch (SQLException e) {
//      throw new DBException(DBException.Code.FAIL_TO_UPDATE, e,
//          "Lỗi thực thi truy vấn {0} (object: {1})", sql, o);
//    }
//  }

  /**
   * @effects 
   *  update the prepared SQL statement <tt>s</tt> with the data <tt>d</tt> for the column <tt>colIndex</tt>. 
   *  Throws <tt>SQLException</tt> if fails to update the statement or <tt>NotPossibleException </tt>
   *  if could not convert <tt>d</tt> to the required SQL value.
   *  
   * @version 
   * - 3.2: improved to support Type.File
   */
  private void javaToSQL(PreparedStatement s, int colIndex, Type type, Class typeClass, Object d) 
  throws SQLException, NotPossibleException, NotFoundException {
    if (type.isString() || type.isChar()) {
      if (d != null)
        s.setString(colIndex, d+"");
      else 
        s.setString(colIndex, null);
    } else if (type.isInteger()) {
      if (d==null)
        s.setNull(colIndex, Types.INTEGER);
      else {
        s.setInt(colIndex, (Integer)d);
      }
    } else if (type.isLong()) {
      if (d==null)
        s.setNull(colIndex, Types.INTEGER);
      else
        s.setLong(colIndex, (Long)d);
    } else if (type.isFloat()) {
      if (d==null)
        s.setNull(colIndex, Types.FLOAT);
      else
        s.setFloat(colIndex, (Float)d);
    } else if (type.isDouble()) {
      if (d==null)
        s.setNull(colIndex, Types.FLOAT);
      else
        s.setDouble(colIndex, (Double)d);
    } 
    // v2.7.2 
    else if (type.isDecimal()) {
      if (d == null)
        s.setNull(colIndex, Types.DECIMAL);
      else
        s.setBigDecimal(colIndex, DODMToolkit.toDecimal(type, d));
    }
    else if (type.isBoolean()) {
      // use varchar for boolean type (see javaToDB)
      if (d != null)
        s.setString(colIndex, d+"");
      else 
        s.setNull(colIndex,
            /*v2.7.3: changed to VARCHAR b/c boolean is stored as such 
            Types.BOOLEAN
            */
            Types.VARCHAR 
            );
    } 
    else if (type.isDate()) {
      /**v2.5.4: support date type*/
      if (d==null)
        s.setNull(colIndex, Types.DATE);
      else {
        // convert to SQL Date type if necessary
        Date date;
        if (d instanceof java.util.Date)
          date = javaDateToSQL((java.util.Date) d);
        else
          date = (Date) d;
        s.setDate(colIndex, date);
      }
    } else if (type.isSerializable()) {
      s.setObject(colIndex, d);
    } else if (type.isImage()) {
      // get image bytes
      if (d != null) {
        byte[] bytes = imageToBytes((ImageIcon) d, "jpg");
        s.setBytes(colIndex, bytes);
      } else {
        s.setNull(colIndex, 
            /*v3.0: use method
            SqlType.getMapping(type).getIntValue()
            */
            getDataSourceTypeFor(getDataSourceTypeClass(), type).getIntValue()
            );
            
      }
    } else if (type.isFile()) {  // v3.2
      // get bytes
      if (d != null) {
        byte[] bytes = fileToBytes((File) d);
        s.setBytes(colIndex, bytes);
      } else {
        s.setNull(colIndex, 
            getDataSourceTypeFor(getDataSourceTypeClass(), type).getIntValue()
            );
      }
    } else if (type.isDomainType()) {
      List<DAttr> dcFKs = dom.getDsm().getIDDomainConstraints(typeClass);
      for (DAttr dcFK : dcFKs) {
        if (d != null)
          d = dom.getDsm().getAttributeValue(d, dcFK.name());
        javaToSQL(s, colIndex, dcFK.type(), null, d);
      }
    } else {
      throw new NotImplementedException(
          NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
          "Không hỗ trợ kiểu dữ liệu: {0}", type.name());
    }
  }
  
  /**
   * This is the reverse of {@link #sqlDateToJava(java.util.Date)}.
   * 
   * @requires
   *  d != null
   * @effects 
   *  return a <tt>java.sql.Date</tt> object equivalence of the <tt>java.util.Date</tt> object <tt>d</tt>
   */
  private java.sql.Date javaDateToSQL(java.util.Date d) {
    return new java.sql.Date(d.getTime());
  }
  
  /**
   * This is the reverse of {@link #javaDateToSQL(java.util.Date)}.
   * 
   * @requires
   *  d != null
   * @effects 
   *  return a <tt>java.util.Date</tt> object equivalence of the <tt>java.sql.Date</tt> object <tt>d</tt>
   */
  private java.util.Date sqlDateToJava(java.sql.Date d) {
    return new java.util.Date(d.getTime());
  }
  
  /**
   * The reverse of method {@link #javaToSQL(PreparedStatement, int, Type, Class, Object)}. 
   * 
   * @requires <tt>dc != null /\ rs != null /\ rs</tt> is currently pointing to a valid current row /\ 
   *  colIndex >= 1 and is a valid column index
   *    
   * @effects <pre> 
   *  read the SQL value at the column <tt>colIndex</tt> of the current row of the 
   *  result set </tt>rs</tt> and convert it to the corresponding Java value, 
   *  using the domain constraint <tt>dc</tt>. 
   *  If succeeds
   *    return the value
   *  else if <tt>dc.type()</tt> is not supported
   *    throws NotImplementedException
   *  else 
   *    throws NotPossibleException
   *    
   *  </pre>
   *  
   * @version 
   * - 3.2: improved to support File type
   */
  private Object sqlToJava(Class cls, // v2.7.4: added this parameter 
      DAttr dc, ResultSet rs, final int colIndex) 
      throws NotImplementedException, NotPossibleException {
    Object val = null;

    Type type = dc.type();

    try {
      
      /** 
       * if data type is non-BLOB 
       *    get the column value first 
       *    if it is not null 
       *      get the value again using the correct getter for the specified data type
       * else
       *  get the value as blob and convert if needed
       */
      // v2.6.4.a: changed to isByteArray to support all BLOB-type
      if (!type.isByteArray()) { //(!type.isImage()) {
        val = rs.getObject(colIndex);
        // convert if val is not null (i.e. SQL null)
        if (val != null) {
          if (type.isString()) {
            /* v2.7.3: support char 
            val = rs.getString(colIndex);
            */
            String valStr = rs.getString(colIndex);
            if (type.isChar()) {
              val = (valStr.length() > 0) ? 
                  valStr.charAt(0) :  // get the char 
                  '\u0000'              // no char = null char
                    ;
            } else {
              val = valStr;
            }
          }
          else if (type.isInteger()) {
            val = rs.getInt(colIndex);
          } else if (type.isLong()) {
            val = rs.getLong(colIndex);
          } else if (type.isFloat()) {
            val = rs.getFloat(colIndex);
          } else if (type.isDouble()) {
            val = rs.getDouble(colIndex);
          }  
          // v2.7.2 
          else if (type.isDecimal()) {
            val = rs.getBigDecimal(colIndex);
            // convert
            val = DODMToolkit.fromDecimal(type, (BigDecimal) val);
          }
          else if (type.isBoolean()) {
            // Note: rs.getBoolean() does not work correctly
            val = Boolean.parseBoolean(val.toString());
          } else if (type.isDate()) {
            // convert to java Date
            val = rs.getDate(colIndex);
            val = sqlDateToJava((java.sql.Date) val);
          }
          // add other data type here
          else {
            throw new NotImplementedException(
                NotImplementedException.Code.DATA_TYPE_NOT_SUPPORTED,
                new Object[] {cls.getSimpleName() + "." + dc.name() +".type() = "+ type});
          }
        }
      } else {
        // blob-type
        // get blob and convert to bytes
        /*v2.7.4: use binary stream
        Blob blob = rs.getBlob(colIndex);
        if (blob != null) {
          long blobSize = blob.length();
          val = blob.getBytes(1, (int) blobSize);
          // v2.6.4.a
          if (type.isImage()) {
            // image type -> convert to image
            val = bytesToImage((byte[])val);
          }
        }
        */
        /*v2.7.4: this code is slightly better than the above for the image-typed case in the support for client/server config
         * FIXME: BUT for client/server configuration it still does not successfully read images for all cases 
         * For some cases (e.g. Configuration.appLogo) this works; for others (e.g. Person.idPhoto)
         * it returns a null input stream (ins). 
         * The remaining blob-typed cases (else branch) are thus also not guaranteed to work correctly.
         */
        if (type.isImage()) { // image-type data -> read into ImageIcon 
          InputStream ins = rs.getBinaryStream(colIndex);
          if (ins != null)
            val = inputStreamToImage(ins);
        } else if (type.isFile()) { // v3.2: File typed 
          InputStream ins = rs.getBinaryStream(colIndex);
          if (ins != null)
            val = inputStreamToFile(ins);
        } else {  // other binary data types
          Blob blob = rs.getBlob(colIndex);
          if (blob != null) {
            long blobSize = blob.length();
            val = blob.getBytes(1, (int) blobSize);
          }
        }
        /*v2.7.4: this mimics what was done in the insert() method BUT throws exception 
         * for client/server driver  
        val = rs.getBytes(colIndex);
        // v2.6.4.a
        if (val != null && type.isImage()) {
          // image type -> convert to image
          val = bytesToImage((byte[])val);
        }
        */
      }
    } catch (SQLException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_READ_TABLE_COLUMN_VALUE, e, new Object[] {cls.getSimpleName(), dc.name(), colIndex, type});
    }
    
    // if we get here then ok
    return val;
  }
  
  /**
   * @effects return array <tt>byte[]</tt> of the content of the <tt>Image img</tt>
   */
  private byte[] imageToBytes(ImageIcon img, String imgType) throws NotPossibleException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    try {
      int width = img.getIconWidth();
      int height = img.getIconHeight();
      BufferedImage bimg = new BufferedImage(
          width,
          height, 
          BufferedImage.TYPE_INT_RGB);
      Graphics2D g = bimg.createGraphics();
      g.drawImage(img.getImage(), 0, 0, null);
      ImageIO.write(bimg, imgType, bout);
      
      return bout.toByteArray();
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
          e, new Object[] {"imageToBytes", img, e.getMessage()});
    }
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
  private byte[] fileToBytes(File file) throws NotPossibleException {
    return ToolkitIO.getFileAsBytes(file);
  }

  /**
   * @requires 
   *  <tt>ins</tt> represents bytes of a {@link File} object that was converted using {@link #fileToBytes(File)}
   *  
   * @effects return a <tt>File</tt> from the bytes read from <tt>ins</tt> or <tt>null</tt> if no bytes
   * @version 3.2
   */
  private File inputStreamToFile(InputStream ins) throws NotPossibleException {
    // create a temporary file for the data and return that file. The file is named randomly and will later be 
    // renamed to a propery name by the application that uses it 
    return ToolkitIO.createTempFile(ins);
  }
  
  /**
   * @requires 
   *  <tt>ins</tt> represents bytes of an ImageIcon
   *  
   * @effects return an <tt>ImageIcon</tt> from the bytes read from <tt>ins</tt>
   * @version 2.7.4
   */
  private ImageIcon inputStreamToImage(InputStream ins) throws NotPossibleException {
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
  
  /**
   * @effects return an <tt>Image</tt> object whose content is <tt>byteArray</tt>
   */
  private ImageIcon bytesToImage(byte[] byteArray) {
    
    ImageIcon icon = new ImageIcon(byteArray);
    return icon; //icon.getImage();
  }
  
  /**
   * @effects Returns each row in result set <code>rs</code> concatenated.
   */
  private String resultSetToString(ResultSet rs) throws DataSourceException {
    try {
      int cols = rs.getMetaData().getColumnCount();

      Object v;
      StringBuffer sb = new StringBuffer();
      while (rs.next()) {
        for (int i = 1; i <= cols; i++) {
          v = rs.getObject(i);
          sb.append(v.toString()).append(" ");
        }
        sb.append(LF);
      }

      return sb.toString();
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e, 
          new Object[] {""});
    }
  }

  /**
   * @effects Closes <code>this.conn</code>
   */
  public void close() {
    try {
      this.conn.close();
    } catch (SQLException e) {
      // ignore
    }
  }

  // public boolean exists(Class c) {
  // String sql = "Select ";
  // }

  // ////////// NEW METHODS /////////////
  /**
   * Store the data of a domain object into the database table created for its
   * class.<br>
   * 
   * @effects inserts a database record for a domain <code>obj</code> into the
   *          database table of the domain class <code>c</code>.
   */
  @Override
  public void putObject(Class c, Object obj) throws DataSourceException {
    String sql = null;

    List<Field> updateAttributes;
    
    try {
      // TODO: fix the comments below for caching
//      Class actualClass = obj.getClass();
//      Object[] cached = cache.getCacheEntry(actualClass);
//      
//      if (cached != null) {
//        sql = (String) cached[0];
//        updateAttributes = (List<Field>) cached[1];
//        if (debug)
//          System.out.println("sql in cache: \n\t" + sql);
//      } else {
        updateAttributes = new ArrayList();
        sql = genParameterisedInsert(c, obj, updateAttributes);
        if (debug)
          System.out.println(sql);
        // cache 
//        cache.putCacheEntry(actualClass,sql,updateAttributes);
//      }
    } catch (NotFoundException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_INSERT_OBJECT, e,
          new Object[] {c.getSimpleName(), obj});
    }

    executeParameterisedUpdate(sql, c, updateAttributes, obj);
  }

  /**
   * @effects updates the database record of the domain object <code>obj</code>
   *          in the database table of the domain class <code>c</code>, or
   *          throws <code>DBException</code> if an error occured.
   */
  @Override
  public void updateObject(Object obj, Class c) throws DataSourceException {
    String sql = null;

    List<Field> updateAttributes = new ArrayList();
    try {
      //sql = genUpdate(c, obj);
      sql = genParameterisedUpdate(c, obj, updateAttributes);

      //System.out.println("update attributes: " + updateAttributes);
      
      if (debug)
        System.out.println(sql);
    } catch (NotFoundException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_UPDATE_OBJECT_BY_QUERY, e,
          new Object[] {sql, obj} );
    }

    if (sql != null)
      //executeUpdate(sql);
      executeParameterisedUpdate(sql,c,updateAttributes,obj);
    else {
      // TODO: log
    }
  }

  /**
   * @effects 
   *  update database records of <tt>c</tt> that satisfies <tt>searchExp</tt>
   *  using the expressions in <tt>updateQuery</tt>
   *   
   * @requires the database table of <code>c</code> has been created
   */
  @Override
  public void updateObjects(Class c, Query<ObjectExpression> searchQuery,
      Query<ObjectExpression> updateQuery) throws DataSourceException {
    String sql = null;

    try {
      sql = genUpdate(c, searchQuery, updateQuery);

      if (debug)
        System.out.println("DBToolKit.updateObjects: " + sql);
    } catch (NotFoundException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_UPDATE_OBJECT_BY_QUERY, e,
          new Object[] {sql, ""});
    }

    executeUpdate(sql);
  }
  
  /**
   * @effects delete database record of the domain object <code>obj</code> from
   *          the database table of the domain class <code>c</code> or throws
   *          <code>DBException</code> if failed to do so.
   * @requires the database table of <code>c</code> contains a record for
   *           <code>obj</code>
   */
  @Override
  public void deleteObject(Class c, Object obj) throws DataSourceException {
    String sql = null;

    try {
      sql = genDelete(c, obj);
      if (debug)
        System.out.println(sql);
    } catch (NotFoundException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_DELETE_OBJECT, e,
          new Object[]{ c.getSimpleName(), obj});
    }

    executeUpdate(sql);
  }
  
  /**
   * @effects 
   *  delete database records of <tt>c</tt> that satisfies <tt>searchQuery</tt> 
   *  
   * @requires the database table of <code>c</code> has been created
   */
  @Override
  public void deleteObjects(Class c, Query<ObjectExpression> searchExp) throws DataSourceException {
    String sql = null;

    try {
      sql = genDelete(c, searchExp);
      if (debug)
        System.out.println("DBToolKit.deleteObjects: " + sql);
    } catch (NotFoundException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_DELETE_OBJECT, e,
          new Object[] {c.getSimpleName(), searchExp});
    }

    executeUpdate(sql);
  }

  /**
   * @effects returns <code>num</code> objects of a class <code>c</code> from
   *          the database, or <code>null</code> if no objects were found;
   *          
   *          <br>throws <code>DataSourceException</code> if errors occurred in reading
   *          objects from the database; NotPossibleException if fail to create object
   *          
   * @requires the objects that are referenced by the retrieved objects of
   *           <code>c</code> must have already been read from the database.
   */
  private List readObjects(final Class c, Expression[] conditions, int num)
      throws NotPossibleException, DataSourceException {
    final String cname = dom.getDsm().getDomainClassName(c);

    // read database records for c's objects
    ResultSet rs = readRecords(c, conditions);

    if (rs == null)
      return null;

    // read the domain attributes of the class
    // we will use these attributes to parse the object values
    java.util.Map<Field,DAttr> fields = dom.getDsm().getSerialisableDomainAttributes(c);

    // determine if c has a reflexive association (reflexive relationship)
    // if so then we must take care of the order in which we read objects from database
    // (see below)
    boolean reflexive = dom.getDsm().isReflexive(c, fields);
    
    // the objects that will be read
    List objects = new ArrayList();

    // get the id columns and check if one of them is auto-generated
    Field f = null;
    DAttr dc;
    List<DAttr> refDcs;
    Object[] refValues;
    Type type;
    Class domainType;
    int recCount = 0;

    List values;
    Object o = null;
    Object v = null;

    // an object stack to keep those that will be processed 
    // later in the case reflexive=true
    // stack is used because we want to process entries that are added
    // later first
    Stack<List> delayedStack = null;
    if (reflexive)
      delayedStack = new Stack();
    
    // use a flag to flag a record as member of the delayedQueue
    boolean delayed;
    
    try {
      // make a pass through the database records in the record set
      REC: while (rs.next()) {
        values = new ArrayList();

        // reset delayed to false
        delayed = false;
        
        /*
         *  make a pass through the domain attributes and read their values
         *  from the current record. 
         *  
         *  If reflexive=true and the value of the concerned FK attribute is not null and 
         *  the referred object has not been read then we put the current values into 
         *  the delayed queue to process later
         */
        Collection<Entry<Field,DAttr>> fieldEntries = fields.entrySet();
        int i = -1;
        //v5.0: for (int i = 0; i < fields.size(); i++) {
        for (Entry<Field,DAttr> entry : fieldEntries) {
          /*f = (Field) fields.get(i);
          dc = f.getAnnotation(DC);
          */
          i++;
          f = entry.getKey();
          dc = entry.getValue();
          type = dc.type();
          
          // we only use id and non-auto-generated attribute to create object
          // v2.7.3: if (dc.id() || !dc.auto()) {
            if (!type.isDomainType()) {
              // read the sql value 
              v = sqlToJava(c, dc, rs, i+1);
            } else {
                // domain type attribute
                // query the object whose id is the value of this field
                // and use that for the object
                domainType = f.getType();
                refDcs = dom.getDsm().getIDDomainConstraints(domainType);

                refValues = new Object[refDcs.size()];
                v = sqlToJava(c, refDcs.get(0), rs, i+1);
                if (v != null) {  
                  // ref value is specified, look it up
                  refValues[0] = v;
                  if (refDcs.size() > 1) {
                    // if it is a compound key then we must
                    // read the subsequent values in this record to complete
                    // the id
                    int j = 1;
                    for (i = i + 1; i < i + refDcs.size(); i++) {
                      /*v5.0: Field f1 = (Field) fields.get(i);
                      DAttr dc1 = f1.getAnnotation(DC);
                      */
                      refValues[j] = sqlToJava(c, refDcs.get(j), rs, i);
                      j++;
                    }
                  }
                  
                  /**
                   * if reflexive=true and this field is the FK attribute
                   *  look up for object in objects
                   * else
                   *  look up for object in schema
                   */
                  if (reflexive && domainType == c) {
                    v = dom.lookUpObjectByID(objects, refValues);
                    if (v == null) { // referenced object not yet processed
                      //  record this value as normal to be processed later
                      // reset value to keep, also set delayed to true
                      if (!delayed) delayed = true;
                      v = refValues;
                    }        
                  } else {
                    // use the id to look up the object directly
                    v = dom.lookUpObjectByID(domainType, refValues); 
                    if (v == null) { // referenced object not found
                      // log and skip this record
                      System.err
                          .println(
                              String.format("Referenced object required but *not found*: %s(%s).%s -> %s.%s[%s])", 
                                  cname, values, f.getName(), domainType.getSimpleName(), refDcs.get(0).name(), refValues[0]+""));
                      if (debug)
                        System.exit(1);
  
                      // skip this record
                      continue REC;
                    }                    
                  }
                } else {
                  // v2.6.4.a:
                  // ref value is not specified; if it is required then data integrity
                  // is violated, print error
                  if (!dc.optional()) {
                    // log
                    if (debug) {
                      System.err
                      .println(
                          String.format("Referenced object required but *not specified*: %s(%s).%s -> %s.%s[%s])", 
                              cname, values, f.getName(), domainType.getSimpleName(), refDcs.get(0).name(), refValues[0]+""));

                      //System.exit(1);
                    }
                  }
                }
              } // end domain type
          
            values.add(v);
            
            //debug
//            if (cname.equals("CustomerOrder")) {
//              System.out.printf("      v=%s%n",v);
//            }
          //} // end if
        } // end for(fields) loop

        /* 
         * if the current record is not a member of the delayed queue (i.e delayed = false) 
         *  create object
         * else 
         *  add values to delayed queue  
         **/
        if (!delayed) {
          try {
            o = dom.getDsm().newInstance(c, values.toArray());
            objects.add(o);
          } catch (Exception e) {
            //e.printStackTrace();
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,
                new Object[] {c.getSimpleName(), values});
          }
  
          if (num > 0) {
            recCount++;
            if (recCount >= num)
              break;
          }
        } else {
          delayedStack.push(values);
        }
      } // end while rs
    } catch (SQLException ex) {
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, ex,
          new Object[] {c.getSimpleName()});
    } finally {
      try {
        rs.close();
        rs.getStatement().close();
      } catch (SQLException e) {
        //
      }
    }

    /*
     * if reflexive=true and delayed queue is not empty 
     *  process values in the delay queue.
     *  This should work since all the referred-to objects have now been loaded
     */
    if (reflexive && !delayedStack.isEmpty()) {
      STACK: while (!delayedStack.isEmpty()) {
        values = delayedStack.pop();
        /* make a pass through the domain attributes (similar to the inner loop of the main loop above)
        /* except that this time we only need to process the value 
         * that corresponds to the domain-type attribute. In particular, 
         * we will look up the reference object.
         */
        Collection<Entry<Field,DAttr>> fieldEntries = fields.entrySet();
        int i = -1;
        // v5.0: for (int i = 0; i < fields.size(); i++) {
        for (Entry<Field,DAttr> entry : fieldEntries) {
          /*f = fields.get(i);
          dc = f.getAnnotation(DC);
          */
          i++;
          f = entry.getKey();
          dc = entry.getValue();
          type = dc.type();
          if (type.isDomainType()) {
            domainType = f.getType();
            if (domainType == c) {
              // the FK attribute that causes reflexive association
              refValues = (Object[]) values.get(i);
              // look up the object in objects 
              v = dom.lookUpObjectByID(objects, refValues);
              if (v == null) {
                // should not happen
                System.err
                    .println(String
                        .format(
                            "Referenced *reflexive* object required but not found: %s.%s -> %s[%s])",
                            cname, f.getName(), domainType, refValues[0] + ""));
                if (debug)
                  System.exit(1);

                // skip this record
                continue STACK;
              }
              // put v back into vals
              values.set(i, v);
            }
          } // end domainType attribute
        } // end field pass
        
        // now create this object
        try {
          o = dom.getDsm().newInstance(c, values.toArray());
          objects.add(o);
        } catch (Exception e) {
          e.printStackTrace();
        }

        if (num > 0) {
          recCount++;
          if (recCount >= num)
            break STACK;
        }        
      } // end values loop
    } // end case: delayStack
    
    if (objects.isEmpty())
      return null;
    else
      return objects;
  }  

  // version 2.5.5
//  /**
//   * @effects returns <code>num</code> objects of a class <code>c</code> from
//   *          the database, or <code>null</code> if no objects were found;
//   *          throws <code>DBException</code> if errors occurred in reading
//   *          objects from the database.
//   * @requires the objects that are referenced by the retrieved objects of
//   *           <code>c</code> must have already been read from the database.
//   */
//  private List readObjects(final Class c, Expression[] conditions, int num)
//      throws DBException {
//    final String cname = schema.getDsm().getDomainClassName(c);
//
//    // read database records for c's objects
//    ResultSet rs = readRecords(c, conditions);
//
//    if (rs == null)
//      return null;
//
//    // read the domain attributes of the class
//    // we will use these attributes to parse the object values
//    List<Field> fields = schema.getDsm().getSerialisableDomainAttributes(c);
//
//    // the objects that will be read
//    List objects = new ArrayList();
//
//    // get the id columns and check if one of them is auto-generated
//    Field f = null;
//    DomainConstraint dc;
//    DomainConstraint[] refDcs;
//    Object[] refValues;
//    Type type;
//    Class domainType;
//    int recCount = 0;
//
//    List values;
//    Object o = null;
//    Object v = null;
//
//    try {
//      REC: while (rs.next()) {
//        values = new ArrayList();
//
//        // System.out.println(fields);
//        for (int i = 0; i < fields.size(); i++) {
//          f = (Field) fields.get(i);
//          dc = f.getAnnotation(DC);
//          type = dc.type();
//          
//          // we only use id and non-auto-generated attribute to create object
//          if (dc.id() || !dc.auto()) {
//            if (!type.isDomainType()) {
//              // read the sql value 
//              v = sqlToJava(dc, rs, i+1);
//              
//              // for special attributes:
//              // - Image-type attributes
//              if (type.isImage() && v != null) { 
//                // other types
//                v = bytesToImage((byte[])v);
//              }
//            } else {
//                // domain type attribute
//                // query the object whose id is the value of this field
//                // and use that for the object
//                domainType = f.getType();
//                refDcs = schema.getDsm().getIDAttributeConstraints(domainType);
//
//                refValues = new Object[refDcs.length];
//                v = sqlToJava(refDcs[0], rs, i+1);
//                if (v != null) {  // FK value is specified, look it up
//                  refValues[0] = v;
//                  if (refDcs.length > 1) {
//                    // if it is a compound key then we must
//                    // read the subsequent values in this record to complete
//                    // the id
//                    int j = 1;
//                    for (i = i + 1; i < i + refDcs.length; i++) {
//                      Field f1 = (Field) fields.get(i);
//                      DomainConstraint dc1 = f1.getAnnotation(DC);
//                      refValues[j] = sqlToJava(refDcs[j], rs, i); //rs.getObject(i);
//                      j++;
//                    }
//                  }
//                  // use the id to look up the object directly
//                  v = schema.getDsm().lookUpObjectByID(domainType, refValues);
//  
//                  if (v == null) { // referenced object not found
//                    // throw new NotFoundException(
//                    // NotFoundException.Code.OBJECT_NOT_FOUND,
//                    // "Referenced object required but not found for {1} (type {0})",
//                    // domainType, Arrays.toString(refValues));
//                    // log and skip this record
//                    System.err
//                        .println(
//                            String.format("Referenced object required but not found: %s.%s -> %s.%s[%s])", 
//                                cname, f.getName(), domainType, refDcs[0].name(), refValues[0]+""));
//                    if (debug)
//                      System.exit(1);
//                    
//                    continue REC;
//                  }
//                }
//              } // end domain type
//          
//            // v may be null
//            values.add(v);
//          } // end if
//        } // end for(fields) loop
//
//        try {
//          o = schema.getDsm().newInstance(c, values.toArray());
//          objects.add(o);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//
//        if (num > 0) {
//          recCount++;
//          if (recCount >= num)
//            break;
//        }
//      } // end while rs
//    } catch (SQLException ex) {
//      throw new DBException(DBException.Code.FAIL_RESULT_SET, ex,
//          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    } finally {
//      try {
//        rs.close();
//        rs.getStatement().close();
//      } catch (SQLException e) {
//        //
//      }
//    }
//
//    if (objects.isEmpty())
//      return null;
//    else
//      return objects;
//  }

  /**
   * Reads the database records of the domain class <code>c</code> from the
   * database.
   * 
   * <p>
   * We use the following basic algorithm:
   * 
   * <pre>
   *  if c has a domain super-class then 
   *    create a join SQL statement recursively with the super-class(es) 
   *      to get all the fields
   *  else if c has sub-class(es) then
   *    read only the records that are not in the sub-classes
   * </pre>
   * 
   * 
   * <b>About SQL in the first case:</b> <br>
   * -----------------------------<br>
   * - a joined query between the sub-class and each super domain class on the
   * id attribute(s) <br>
   * - the select clause includes all attributes of the sub-class and non-id
   * attributes of the super-class <br>
   * <p>
   * EXAMPLE 1: ElectiveModule is a sub-class of Module
   * 
   * <pre>
   * select t2.*,t1.deptname 
   * from electivemodule t1, module t2 
   * where
   *  t1.code=t2.code;
   * </pre>
   * 
   * <p>
   * EXAMPLE 2: instructor is a subclass of staff is a subclass of person
   * 
   * <pre>
   * select t1.title,t2.deptname,t2.joindate,t3.* 
   * from instructor t1, staff t2, person t3 
   * where
   *  t1.id = t2.id and 
   *  t2.id = t3.id;
   * </pre>
   * 
   * <b>About SQL in the second case</b>:<br>
   * -----------------------------<br>
   * - is the SQL of the first case plus an additional condition to exclude the
   * ids of the records in the sub-class tables
   * <p>
   * EXAMPLE 1: staff is a sub-class of person and is a super-class of
   * instructor and administrator
   * 
   * <pre>
   * select t2.id, t2.name, t2.dob, t2.address,t1.joindate,t1.deptname  
   * from staff t1, person t2 
   * where
   *  t1.id = t2.id and 
   *  t1.id not in (select id from instructor UNION 
   *                select id from administrator);
   * </pre>
   * 
   * @effects returns the <code>ResultSet</code> of database records of the
   *          domain table of the domain class <code>c</code> that meet the
   *          <code>Expression</code>s <code>conditions</code>. Throws
   *          <code>DBException</code> if an error occurred.
   */
  private ResultSet readRecords(final Class c, Expression[] conditions)
      throws DataSourceException {
    final String cname = dom.getDsm().getDomainClassName(c);

    ResultSet rs = null;

    Stack<String> tables = new Stack();
    Stack<String> select = new Stack();
    Stack<Expression> exps = new Stack();
    if (conditions != null)
      Collections.addAll(exps, conditions);
    StringBuffer orderBy = new StringBuffer("order by ");

    Class sup = dom.getDsm().getSuperClass(c);
    java.util.Map<Field,DAttr> fields;
    java.util.Map<Field,DAttr> idFields;
    String cTable;
    if (sup != null) {
      // first case
      int index = 1;

      Class currentClass = c;
      String currentTable = "t" + (index++);
      cTable = currentTable;
      tables.push(cname + " " + currentTable);
      // add the non-id attributes of the current class to select
      fields = dom.getDsm().getSerialisableAttributes(c);
      DAttr dc;
      String n;
      int colIndex = 0;
      /* v5.0: for (Field f : fields) { // super class table
        dc = f.getAnnotation(DC);*/
      for (Entry<Field,DAttr> entry : fields.entrySet()) {
        Field f = entry.getKey();
        dc = entry.getValue();
        if (!dc.id()) { // non-id attributes
          if (dc.type().isDomainType()) {
            n = toDBColumnName(c, dc, false);
          } else
            n = dc.name();
          select.add(colIndex++, currentTable + "." + n);
        }
      }

      String supName;
      String supTable;
      do {
        supName = dom.getDsm().getDomainClassName(sup);
        supTable = "t" + (index++);
        tables.push(supName + " " + supTable);

        idFields = dom.getDsm().getIDAttributes(sup);

        // use the id attributes to add new join expressions
        // v5.0: for (Field f : idFields) { // current table
        for (Entry<Field,DAttr> idEntry : idFields.entrySet()) {
          Field f = idEntry.getKey();
          // add join expressions between the id attributes of the two tables
          exps.add(new Expression(currentTable + "." + f.getName(),
              Op.EQ, supTable + "." + f.getName(),
              Expression.Type.Metadata));
        } // end for

        // add the non-id attributes of the super class to the
        // select clause
        fields = dom.getDsm().getSerialisableAttributes(sup);
        colIndex = 0;
        /* v5.0: for (Field f : fields) { // super class table
          dc = f.getAnnotation(DC); */
        for (Entry<Field,DAttr> entry : fields.entrySet()) {
          Field f = entry.getKey();
          dc = entry.getValue();
          if (!dc.id()) { // non-id attributes
            if (dc.type().isDomainType()) {
              n = toDBColumnName(sup, dc, false);
            } else
              n = dc.name();
            select.add(colIndex++, supTable + "." + n);
          }
        } // end for

        // recursive: check the super-super class and so on...
        currentTable = supTable;
        currentClass = sup;
        sup = dom.getDsm().getSuperClass(sup);
      } while (sup != null);

      // add the id attributes of the top-level super class to select
      colIndex = 0;
      int find = 0;
      /*v5.0: for (Field f : idFields) {
        dc = f.getAnnotation(DC); */
      for (Entry<Field,DAttr> idEntry : idFields.entrySet()) {
        Field f = idEntry.getKey();
        dc = idEntry.getValue();
        if (dc.type().isDomainType()) {
          n = toDBColumnName(sup, dc, false);
        } else {
          n = dc.name();
        }
        select.add(colIndex++, supTable + "." + n);
        // order by these ids
        orderBy.append(supTable + "." + n);
        if (find < idFields.size() - 1)
          orderBy.append(",");
        find++;
      }
    } else {
      // no super-type
      cTable = cname;
      // just return all records of the table cname
      // example sql: select * from student
      tables.add(cTable);
      select.add("*");

      // order by the id fields
      idFields = dom.getDsm().getIDAttributes(c);
      int find = 0;
      /* v5.0: for (Field f : idFields) { // current table */
      for (Entry<Field,DAttr> idEntry : idFields.entrySet()) {
        Field f = idEntry.getKey();
        orderBy.append(f.getName());
        if (find < idFields.size() - 1)
          orderBy.append(",");
        find++;
      }
    }

    // ascending order (if not the default)
    orderBy.append(" ASC");

    Class[] subs = dom.getDsm().getSubClasses(c);
    if (subs != null) {
      // second case: add new conditions to exclude the sub-class table ids
      idFields = dom.getDsm().getIDAttributes(c);
      if (idFields.size() > 1)
        throw new NotImplementedException(
            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
            new Object[] {"compoundKey(" + c + ")"});

      String id = idFields.keySet().iterator().next().getName();
      StringBuffer nestedSQL = new StringBuffer();
      for (int i = 0; i < subs.length; i++) {
        nestedSQL.append("select " + id + " from "
            + dom.getDsm().getDomainClassName(subs[i]));
        if (i < subs.length - 1)
          nestedSQL.append(LF).append(" UNION ");
      }
      String idName = cTable + "." + id;
      exps.add(new Expression(idName, Op.NOIN, nestedSQL.toString(),
          Expression.Type.Nested));
    }

    // execute the SQL and return the result
    if (!exps.isEmpty()) {
      rs = 
          /*selectAndProject(select.toArray(new String[select.size()]), //
          tables.toArray(new String[tables.size()]), //
          exps.toArray(new Expression[exps.size()]), //
          null, //
          orderBy.toString());*/
          selectAndProject(select.toArray(new String[select.size()]), //
              tables.toArray(new String[tables.size()]), //
              exps.toArray(new Expression[exps.size()]), //
              null, //
              null, // group by 
              orderBy.toString());
    } else {
      rs = 
          /* selectAndProject(select.toArray(new String[select.size()]), //
          tables.toArray(new String[tables.size()]), //
          null, //
          null, //
          orderBy.toString()); */
          selectAndProject(select.toArray(new String[select.size()]), //
              tables.toArray(new String[tables.size()]), //
              null, //
              null, //
              null, // group by 
              orderBy.toString());
          
    }

    return rs;
  }

  /**
   * @effects retrieves from database and returns an <code>Object[]</code> array
   *          of the values of the non-id attributes of an object of class
   *          <code>c</code> whose id value is <code>idVal</code>.
   * 
   *          <p>
   *          For example, give the database record for the object
   * 
   *          <pre>
   * Student('S2012','Nguyen Van A','1/1/1970')
   * </pre>
   * 
   *          , in which the first attribute is the identifier attribute
   *          <code>Student.id</code>, this method will return the array
   * 
   *          <pre>
   * ['Nguyen Van A','1/1/1970']
   * </pre>
   */
  // public Object[] readNonIDAttributeValues(Class c, String idAttr, Object
  // idVal)
  // throws DBException {
  // Expression[] conditions = new Expression[1];
  //
  // conditions[0] = new Expression(idAttr, "=", idVal);
  //
  // List<DomainConstraint> constraints = schema.getDsm().getAttributeConstraints(c);
  //
  // String[] fieldNames = new String[constraints.size() - 1]; // less the id
  // // attribute
  // String name;
  // int fi = 0;
  // for (int i = 0; i < constraints.size(); i++) {
  // name = constraints.get(i).name();
  // if (!name.equals(idAttr)) {
  // fieldNames[fi++] = name;
  // }
  // }
  //
  // ResultSet rs = selectAndProject(fieldNames, c.getSimpleName(), conditions,
  // null);
  //
  // Object[] vals = null;
  //
  // if (rs != null) {
  // vals = new Object[fieldNames.length];
  // try {
  // while (rs.next()) {
  // for (int i = 0; i < fieldNames.length; i++) {
  // vals[i] = rs.getObject(fieldNames[i]);
  // }
  // }
  // } catch (SQLException e) {
  // throw new DBException("Failed to process result set ", e);
  // }
  // }
  //
  // return vals;
  // }

  // /**
  // * @effects returns an object of <code>c</code> whose id attribute named
  // * <code>attribute</code> has the value <code>val</code>
  // */
  // private Object getObjectByID(Class c, String idAttribute, Object val)
  // throws SQLException, NotFoundException, NotPossibleException {
  // Expression[] conditions = new Expression[1];
  //
  // conditions[0] = new Expression(idAttribute, "=", val);
  //
  // List<DomainConstraint> constraints = DomainManager
  // .getAttributeConstraints(c);
  //
  // String[] fieldNames = new String[constraints.size()];
  // for (int i = 0; i < constraints.size(); i++) {
  // fieldNames[i] = constraints.get(i).name();
  // }
  //
  // ResultSet rs = selectAndProject(fieldNames, c.getSimpleName(), conditions,
  // null);
  //
  // Object o = null;
  // Object v = null;
  // Type type;
  // Class refType;
  // String name;
  // if (rs.next()) {
  // List values = new ArrayList();
  // for (DomainConstraint dc : constraints) {
  // if (!dc.auto()) {
  // type = dc.type();
  // name = dc.name();
  // v = rs.getObject(name);
  // if (type.isDomainType()) {
  // // query the object whose id is the value of this field
  // // and use that for the object
  // v = getObjectByID(refType, dcFK.name(), v);
  // }
  // values.add(v);
  // }
  // }
  //
  // o = DomainManager.newInstance(c, values.toArray());
  // }
  //
  // if (o == null) {
  // throw new NotFoundException("DBToolKit: failed to find object with id "
  // + val);
  // }
  //
  // return o;
  // }

  // private void getObject(Object o, String[] attributes, Expression[]
  // conditions)
  // throws DBException {
  //
  // String name = null;
  // Object v = null;
  //
  // // create a new object instance
  // final Class c = o.getClass();
  // String cname = c.getSimpleName();
  //
  // // read object records from db
  // ResultSet rs = selectAndProject(attributes, cname, conditions, null);
  //
  // Field f = null;
  //
  // if (rs != null) {
  // try {
  // // assume one record returns
  // rs.next();
  // for (int i = 0; i < attributes.length; i++) {
  // name = f.getName();
  // DomainManager.setAttributeValue(o, name, rs.getObject(i + 1));
  // }
  // } catch (Exception ex) {
  // throw new DBException("DBToolKit: failed to get object ", ex);
  // } finally {
  // try {
  // rs.close();
  // rs.getStatement().close();
  // } catch (SQLException e) {
  // //
  // }
  // }
  // }
  // }

  /**
   * Populate some <code>attributes</code> of an object <code>o</code> with data
   * from the data store.
   * 
   * @param o
   *          the object whose attribute values are to be retrieved
   * @param selectAttributes
   *          the attributes whose data values are to be retrieved
   * @param attributes
   *          the names of the attributes that will be used to identify the
   *          object in the storage
   * @param attributeValues
   *          the values of the specified <code>attributes</code>
   */
  // public void getObject(Object o, String[] selectAttributes,
  // String[] attributes, Object[] attributeValues) throws DBException {
  // Expression[] conditions = null;
  //
  // if (attributes != null) {
  // conditions = new Expression[attributes.length];
  // for (int i = 0; i < attributes.length; i++) {
  // conditions[i] = new Expression(attributes[i], "=", attributeValues[i]);
  // }
  // }
  //
  // getObject(o, selectAttributes, conditions);
  // }

  /**
   * @effects returns <code>num</code> of objects of a domain class from the
   *          data source, the values of whose domain attributes
   *          <code>attributes</code> are <code>attributeVals</code>.
   *          <p>
   *          If <code>num=-1</code> then return ALL objects.
   * 
   *          <p>
   *          Internally, this method invokes the method
   *          {@link #readObjects(Class, Expression[], int)} to store objects.
   * 
   * @return
   */
//  public List readObjects(Class c, String[] attributes,
//      Object[] attributeValues, int num) throws DBException {
//    Expression[] conditions = null;
//
//    if (attributes != null) {
//      conditions = new Expression[attributes.length];
//      for (int i = 0; i < attributes.length; i++) {
//        conditions[i] = new Expression(attributes[i], Expression.Op.EQ,
//            attributeValues[i]);
//      }
//    }
//
//    return readObjects(c, conditions, num);
//  }

  /**
   * @effects returns the objects of a class from the data store. This method
   *          directly invokes the method
   *          {@link #readObjects(Class, String[], Object[], int)} with the last
   *          argument is -1.
   */
//  public List readObjects(Class c, String[] attributes, Object[] attributeValues)
//      throws DBException {
//    return readObjects(c, attributes, attributeValues, -1);
//  }

  /**
   * @effects returns all objects of class <code>c</code> stored in the
   *          database, or <code>null</code> if no objects exist; throws
   *          <code>DBException</code> if errors occurred in reading objects
   *          from the database.
   * 
   * @requires a table was created for class <code>c</code> using the {@see
   *           #createTable(Class)} method.
   */
  @Override
  public <T> List<T> readObjects(Class<T> c) throws DataSourceException {
    return (List<T>) readObjects(c, null, -1);
  }

  /**
   * @effects 
   *  invoke {@link #readObjects(Class, Expression[], int)} with <tt>(c, null, num)</tt>
   * @version 2.7.3
   */
  @Override
  public <T> List<T> readObjects(Class<T> c, int num) throws NotFoundException, DataSourceException {
    return (List<T>) readObjects(c, null, num);
  }


  /**
   * @requires 
   *  a table corresponding to class c has been created in the data source /\ 
   *  currId is a valid domain object id of c
   * 
   * @effects
   *  reads from the data source and returns the Oid of the domain object that  
   *  immediately precedes currId (in natural ordering); or null if no such Oid exists
   *   
   *  <p>throws DBException if fails to read from data source;
   *  NotPossibleException if id values are invalid
   */
  @Override
  public Oid readIdFirstBefore(Class c, DAttr idAttrib, Oid currId) 
      throws DataSourceException, NotPossibleException {
    /**
     * Pseudocode:
     * <pre>
     *  read from data source(c) the LARGEST record id that is lower than currId
     *  set id = the first of such id (if exists)
     *  if id = null
     *    throw NotFoundException
     *  else
     *    return readObject(c,id)
     * </pre>
     */
    Query q = new Query();
    q.add(new Expression(idAttrib.name(), Op.LT, currId.getIdValue(0)));      
    String aggregateFunc = "max";
    Collection<Oid> oids = readObjectIds(c, aggregateFunc, q);

    
    if (oids == null) // not found
//      throw new NotFoundException(NotFoundException.Code.RECORD_ID_NOT_FOUND, 
//          "Không tìm thấy mã dữ liệu {0}<{1}>", c.getSimpleName(), q);
      return null;
    else {
      // return the id
      Oid oid = oids.iterator().next();
      return oid;
    }
  }

  /**
   * @requires 
   *  a table corresponding to class c has been created in the data source /\ 
   *  currId is a valid domain object id of c
   * 
   * @effects
   *  reads from the data source and returns the Oid of domain object that 
   *  immediately proceeds currId (in natural ordering); or null if no such Oid exists
   *   
   *  <p>throws DBException if fails to read from data source;
   *  NotPossibleException if id values are invalid
   */
  @Override
  public Oid readIdFirstAfter(Class c, DAttr idAttrib, Oid currId) 
      throws DataSourceException, NotPossibleException {
    /**
     * Pseudocode:
     * <pre>
     *  read from data source(c) the SMALLEST record id that is greater than currId
     *  set id = such id (if exists)
     *  if id = null
     *    throw NotFoundException
     *  else
     *    return readObject(c,id)
     * </pre>
     */
    Query q = new Query();
    q.add(new Expression(idAttrib.name(), Op.GT, currId.getIdValue(0)));      
    String aggregateFunc = "min";
    Collection<Oid> oids = readObjectIds(c, aggregateFunc, q);
    
    if (oids == null) // not found
      return null;
//      throw new NotFoundException(NotFoundException.Code.RECORD_ID_NOT_FOUND, 
//          "Không tìm thấy mã dữ liệu {0}<{1}>", c.getSimpleName(), q);
    else {
      // return the oid
      Oid oid = oids.iterator().next();
      return oid;
    }
  }

  @Override
  public <T> T readObject(final Class<T> c, final Oid oid) throws NotPossibleException, NotFoundException, DataSourceException {
    return readObject(c, oid,
        // sourceCls
        null, 
        // sourceOid
        null,
        // sourceAttrib
        null, 
        // loadAssociateIfNotFound
        true);
  }
  
  @Override
  public <T> T reloadObject(Class<T> c, final Oid oid) throws NotPossibleException, NotFoundException, DataSourceException {
    return readObject(c, oid, 
        // sourceCls
        null, 
        // sourceOid
        null,
        // sourceAttrib
        null, 
        // loadAssociateIfNotFound
        false);
  }
  
  @Override
  public <T> T readAssociatedObject(final Class<T> c, final Oid oid, 
      Class fromAssocCls, 
      Oid fromAssocOid,
      DAttr fromLinkedAttrib) throws NotPossibleException, NotFoundException, DataSourceException {
    return readObject(c, oid, 
        fromAssocCls,
        fromAssocOid,
        fromLinkedAttrib, 
        true);
  }
  
  /**
   * This method is invoked by:
   * <ul>
   *  <li>{@link #readObject(Class, Oid)}
   *  <li>{@link #readAssociatedObject(Class, Oid, Class, DAttr)}
   * </ul>
   * 
   * @requires 
   *  sourceCls != null -> sourceAttrib != null
   *  
   * @throws NotFoundException if <tt>loadAssociateIfNotFound = false</tt> but an associated object is not found  
   * @throws NotPossibleException if failed to create object from the data source record
   * @throws DataSourceException if failed to read record from the data source
   * 
   * @version 
   * - 3.0: add parameter<br>
   * - 3.1: added sourceOid
   */
  private <T> T readObject(final Class<T> c, final Oid oid, 
      final Class sourceCls, 
      final Oid sourceOid,   // v3.1
      final DAttr sourceAttrib, 
      final boolean loadAssociateIfNotFound // v3.0
      ) throws NotPossibleException, 
      NotFoundException,  // v3.0 
      DataSourceException {
    Class<? extends T> baseCls = oid.getCls();
    
    if (debug)
       System.out.printf("%s.readObject(%s, %s, %s, %s)%n", this.getClass().getSimpleName(), 
           baseCls.getSimpleName(), oid, 
           (sourceCls != null) ? sourceCls.getSimpleName() : null, 
           (sourceAttrib != null) ? sourceAttrib.name() : null);

    // now if c has sub-types then determine the sub-type of the object
    // with the specified oid; otherwise use c
    Class[] subs = dom.getDsm().getSubClasses(c);
   
    if (subs != null) {
      // has sub-types
      for (Class subType : subs) {
        if(existRecord(subType, oid)) {
          // found -> break
          baseCls = subType;
          break;
        }
      }
    }

    // TODO: (defensive programming) if baseCls is abstract and the object was not found in any of its sub-types (above)
    // then we should throw a NotPossibleException here
    
    // read database records for the object whose id is specified by oid
    ResultSet rs = readRecord(baseCls, oid);

    if (rs == null)
      return null;

    // read the domain attributes of the class
    // we will use these attributes to parse the object values
    java.util.Map<Field,DAttr> fields = dom.getDsm().getSerialisableDomainAttributes(baseCls);

    //v5.0: int numColumns = fields.size();

    // the objects of c that will be read

    // get the id columns and check if one of them is auto-generated
    Field f = null;
    DAttr dc;
    DAttr[] refDcs;
    Object[] refValues;
    Type type;
    Class domainType;

    List values;
    T o = null;
    Object v = null;
    Oid roid;
    
    // v2.6.4.b
    DAttr linkedAttrib;
    java.util.Map<Object,DAttr> linkLater = new HashMap<Object,DAttr>();
    
    try {
      // this loop iterates only once through the record of the specified object
      REC: 
        //v2.7.4: replaced by first()
        // while (rs.next()) {
        if (rs.first()) {
        values = new ArrayList();

        /*
         *  make a pass through the domain attributes and read their values
         *  from the current record. 
         *  
         *  If reflexive=true and the value of the concerned FK attribute is not null and 
         *  the referred object has not been read then we put the current values into 
         *  the delayed queue to process later
         */
        Collection<Entry<Field,DAttr>> fieldEntries = fields.entrySet();
        int i = -1;
        FIELD: // v5.0: for (int i = 0; i < numColumns; i++) { 
          for (Entry<Field,DAttr> entry : fieldEntries) { 
          /*f = fields.get(i);
          dc = f.getAnnotation(DC);
          */
          i++;
          f = entry.getKey();
          dc = entry.getValue();
          type = dc.type();
          
          // we only use id and non-auto-generated attribute to create object
          //v2.7.3: if (dc.id() || !dc.auto()) {
            if (!type.isDomainType()) {
              // read the sql value 
              v = sqlToJava(baseCls, dc, rs, i+1);
            } else {
                // domain type attribute -> an associated object
                domainType = f.getType();
                
                // query the object whose id is the value of this field
                // and use that for the object
                refDcs = dom.getDsm().getIDDomainConstraints(domainType).toArray(new DAttr[0]);

                // FK values
                refValues = new Object[refDcs.length];
                v = sqlToJava(baseCls, refDcs[0], rs, i+1);
                
                if (v != null) {  // FK value is specified, look it up
                  refValues[0] = v;
                  
                  // v3.1: moved from above
                  if (domainType == sourceCls && dc.equals(sourceAttrib) &&  
                      sourceOid.equals(refDcs, refValues)  // added this check, i.e. same source object
                      ) {
                    // domainType is the associated class that causes this loading (e.g. 1:1 association)
                    // AND that the association between the two is via the same association 
                    // that sources the loading 
                    //     then skip (b/c otherwise it is a loop)
                    continue FIELD;
                  }
                  
                  if (refDcs.length > 1) {
                    // if it is a compound key then we must
                    // read the subsequent values in this record to complete
                    // the id
                    int j = 1;
                    for (i = i + 1; i < i + refDcs.length; i++) {
                      //Field f1 = (Field) fields.get(i);
                      //DomainConstraint dc1 = f1.getAnnotation(DC);
                      refValues[j] = sqlToJava(baseCls, refDcs[j], rs, i); //rs.getObject(i);
                      j++;
                    }
                  }
                  
                  // look up the object, load if not found
                  
                  // first look up the object id, load if not found
                  
                  /*v3.0: support loadAssociateIfNotFound option 
                  roid = dom.retrieveObjectId(domainType, refDcs, refValues); 
                  */
                  if (loadAssociateIfNotFound) {
                    roid = dom.retrieveObjectId(domainType, refDcs, refValues);
                  } else {
                    roid = dom.lookUpObjectId(domainType, refDcs, refValues);
                    if (roid == null) {
                      // should not happen -> error
                      throw new NotFoundException(NotFoundException.Code.OBJECT_ASSOCIATE_ID_NOT_FOUND, 
                          new Object[] {domainType.getName(), Arrays.toString(refDcs), Arrays.toString(refValues)});
                    }
                  }
                  
                  v = dom.lookUpObject(domainType, roid);
                  
                  if (debug)
                    System.out.printf("  associated type: %s <attribute: %s>%n  --> value: %s%n",domainType, f, v);
                  
                  if (v == null) {
                    // not found -> to load
                    // v3.0: added this check for the option
                    if (!loadAssociateIfNotFound) {
                      // not to load associate -> throws error
                      throw new NotFoundException(NotFoundException.Code.OBJECT_ASSOCIATE_NOT_FOUND, 
                          new Object[] {domainType.getName(), Arrays.toString(refDcs), Arrays.toString(refValues)});
                    }
                    
                    if (debug)
                      System.out.printf("  loading linked object %s<%s>%n",domainType, roid);
                    
                    // if domainType is a super-type then the id can belong to 
                    // one of the sub-types or the super-type, so we need to 
                    // try loading from each of them until found
                    // TODO: is there a faster way for handling this situation?
                    Class[] subTypes = dom.getDsm().getSubClasses(domainType);
                    if (subTypes != null) {
                      // has sub-types
                      for (Class subType : subTypes) {
                        try {
                          //v2.7.4: v = dom.loadObject(subType, roid, null);
                          v = dom.loadAssociatedObject(subType, roid, c, 
                              oid, // v3.1
                              dc);
                          
                          if (v != null) 
                            // found -> break
                            break;
                        } catch (NotFoundException ex) {
                          // ignore 
                        }
                      }
                      
                      if (v == null) {
                        // not found in any sub-types -> try the super-type
                        // v2.7.4: v = dom.loadObject(domainType, roid, null);
                        v = dom.loadAssociatedObject(domainType, roid, c,
                            oid, // v3.1
                            dc);
                      }
                    } else {  
                      // no subtypes
                      // v2.7.4: v = dom.loadObject(domainType, roid, null);
                      v = dom.loadAssociatedObject(domainType, roid, c, 
                          oid, // v3.1
                          dc);
                    }
                    
                    // v2.6.4.b: if domainType is determined by c in a 1:1 association via f
                    // then record v for update the association link later
                    linkedAttrib = dom.getDsm().getLinkedAttribute(c, dc);
                    if (linkedAttrib != null && 
                        dom.getDsm().isDeterminedByAssociate(domainType, linkedAttrib)) {
                      linkLater.put(v, linkedAttrib);
                    }
                    if (debug)
                      System.out.printf("  --> %s%n",v);
                  }                    
                }
              } // end domain type
          
            values.add(v);
          //} // end if
        } // end for(fields) loop

        /* 
         *  create object
         **/
        try {
          o = dom.getDsm().newInstance(baseCls, values.toArray());
          
          // v2.6.4.b: if there are linked objects to be updated, then update them
          if (!linkLater.isEmpty()) {
            Object lo;
            for (Entry<Object,DAttr> e : linkLater.entrySet()) {
              lo = e.getKey();
              linkedAttrib = e.getValue();
              dom.setAttributeValue(lo, linkedAttrib.name(), o);
            }
          }
          
        } catch (Exception e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,
              new Object[] {baseCls.getSimpleName(), values});
        }
      } // end while rs
    } catch (SQLException ex) {
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, ex,
          new Object[] {baseCls.getSimpleName()});
    } finally {
      try {
        rs.close();
        rs.getStatement().close();
      } catch (SQLException e) {
        //
      }
    }
    
    return o;
  }  
  
  /**
   * This method is similar to {@link #readRecords(Class, Expression[])} except that 
   * it reads one record matching a specified object id instead of several records.
   * 
   * @requires 
   *  c is the base class of <tt>oid</tt> 
   *  (i.e. either <tt>c</tt> has no sub-types OR <tt>oid</tt> does not refer to an object of any sub-type of <tt>c</tt>)
   *   
   * @effects returns the <tt>ResultSet</tt> containing a <b>single</b> database record of the
   *          domain table of the domain class <tt>c</tt> for the object id <tt>oid</tt>. 
   *  <br>Throws <tt>DBException</tt> if an error occurred.  
   * 
   * @pseudocode
   * We use the following basic algorithm:
   * 
   * <pre>
   *  execute a join SQL statement recursively from c upward with any super-types 
   *    (to get all the fields) to obtain the record
   * </pre>
   * 
   * <p><b>The SQL basically:</b> <br>
   * -----------------------------<br>
   * - is a joined query between the sub-class and each super domain class on the
   * id attribute(s) <br>
   * - contains a select clause which includes all attributes of the sub-class and non-id
   * attributes of the super-class <br>
   * <p>
   * EXAMPLE 1: ElectiveModule is a sup-class of Module
   * 
   * <pre>
   * select t2.*,t1.deptname 
   * from electivemodule t1, module t2 
   * where
   *  t1.code=t2.code;
   * </pre>
   * 
   * <p>
   * EXAMPLE 2: instructor is a subclass of staff is a subclass of person
   * 
   * <pre>
   * select t1.title,t2.deptname,t2.joindate,t3.* 
   * from instructor t1, staff t2, person t3 
   * where
   *  t1.id = t2.id and 
   *  t2.id = t3.id;
   * </pre>
   */
  private ResultSet readRecord(Class c, 
      final Oid oid)
      throws DataSourceException {
    //Class c = oid.getCls();
    
    ResultSet rs = null;

    Stack<String> from = new Stack();
    Stack<String> select = new Stack();
    Stack<Expression> where = new Stack();
    
    StringBuffer orderBy = new StringBuffer("order by ");

    // initialise a String[] array for the id attribute names of the Oid 
    String[] idAttributeNames = new String[oid.size()];
    
    final String cname = dom.getDsm().getDomainClassName(c);
    Class sup = dom.getDsm().getSuperClass(c);
    
    String cTable;
    if (sup != null) {
      java.util.Map<Field,DAttr> fields;
      java.util.Map<Field,DAttr> idFields;

      // has super-type
      int index = 1;

      Class currentClass = c;
      String currentTable = "t" + (index++);
      cTable = currentTable;
      from.push(cname + " " + currentTable);
      // add the non-id attributes of the current class to select
      fields = dom.getDsm().getSerialisableAttributes(c);
      DAttr dc;
      String n;
      int colIndex = 0;
      /* v5.0: for (Field f : fields) { // super class table
        dc = f.getAnnotation(DC); */
      for (Entry<Field,DAttr> entry : fields.entrySet()) {
        Field f = entry.getKey();
        dc = entry.getValue();
        if (!dc.id()) { // non-id attributes
          if (dc.type().isDomainType()) {
            n = toDBColumnName(c, dc, false);
          } else
            n = dc.name();
          select.add(colIndex++, currentTable + "." + n);
        }
      }

      // create table-specific column name(s) for the id attribute(s) 
      for (int i = 0; i < oid.size(); i++) {
        idAttributeNames[i] = currentTable + "." + oid.getIdAttributeName(i);
      }
       
      String supName;
      String supTable;
      // recursively loop upward to add attributes of the super-class(es)
      do {
        supName = dom.getDsm().getDomainClassName(sup);
        supTable = "t" + (index++);
        from.push(supName + " " + supTable);

        idFields = dom.getDsm().getIDAttributes(sup);

        // use the id attributes to add new join expressions
        //v5.0: for (Field f : idFields) { // current table
        for (Entry<Field,DAttr> idEntry : idFields.entrySet()) {
          Field f = idEntry.getKey();
          // add join expressions between the id attributes of the two tables
          where.add(
              new Expression(currentTable + "." + f.getName(),
              Op.EQ, supTable + "." + f.getName(),
              Expression.Type.Metadata)
              );
        } // end for

        // add the non-id attributes of the super class to the
        // select clause
        fields = dom.getDsm().getSerialisableAttributes(sup);
        colIndex = 0;
        /*v5.0: for (Field f : fields) { // super class table
          dc = f.getAnnotation(DC); */
        for (Entry<Field,DAttr> entry : fields.entrySet()) {
          Field f = entry.getKey();
          dc = entry.getValue();
          if (!dc.id()) { // non-id attributes
            if (dc.type().isDomainType()) {
              n = toDBColumnName(sup, dc, false);
            } else
              n = dc.name();
            select.add(colIndex++, supTable + "." + n);
          }
        } // end for

        // recursive: check the super-super class and so on...
        currentTable = supTable;
        currentClass = sup;
        sup = dom.getDsm().getSuperClass(sup);
      } while (sup != null);

      // add the id attributes of the top-level super class to select
      colIndex = 0;
      int find = 0;
      /* v5.0: for (Field f : idFields) {
        dc = f.getAnnotation(DC); */
      for (Entry<Field,DAttr> idEntry : idFields.entrySet()) {
        Field f = idEntry.getKey();
        dc = idEntry.getValue();
        if (dc.type().isDomainType()) {
          n = toDBColumnName(sup, dc, false);
        } else {
          n = dc.name();
        }
        select.add(colIndex++, supTable + "." + n);
        // order by these ids
        orderBy.append(supTable + "." + n);
        if (find < idFields.size() - 1)
          orderBy.append(",");
        find++;
      }
    } else {
      // no super-type, just look in the current table 
      java.util.Map<Field,DAttr> idFields;
      cTable = cname;

      // example sql: select * from student
      from.add(cTable);
      select.add("*");

      // order by the id fields
      idFields = dom.getDsm().getIDAttributes(c);
      int find = 0;
      // v5.0: for (Field f : idFields) { // current table
      for (Entry<Field,DAttr> idEntry : idFields.entrySet()) {
        Field f = idEntry.getKey();
        orderBy.append(f.getName());
        if (find < idFields.size() - 1)
          orderBy.append(",");
        find++;
      }
      
      // create normal column name(s) for the id attribute(s) (i.e. without the table symbol prefix)
      for (int i = 0; i < oid.size(); i++) {
        idAttributeNames[i] = oid.getIdAttributeName(i);
      }
    }

    // ascending order (if not the default)
    orderBy.append(" ASC");

    // update exps with an id expression(s) created for the id attributes in Oid  
    // (using the column name(s) of these attributes created above) 
    Expression exp;
    for (int ind = 0; ind < oid.size(); ind++) {
      exp = new Expression(idAttributeNames[ind],Op.EQ,oid.getIdValue(ind));
      where.add(exp);
    }
    
    // execute the SQL and return the result
    if (!where.isEmpty()) {
      rs = selectAndProject(select.toArray(new String[select.size()]), //
              from.toArray(new String[from.size()]), //
              where.toArray(new Expression[where.size()]), //
              null, //
              null, // group by 
              orderBy.toString());
    } else {
      rs = selectAndProject(select.toArray(new String[select.size()]), //
              from.toArray(new String[from.size()]), //
              null, //
              null, //
              null, // group by 
              orderBy.toString());
          
    }

    return rs;
  }
  
  /**
   * @effects 
   *  if exists a record in the table of <tt>c</tt> whose PK column value(s) 
   *  are equal to those specified in <tt>id</tt>
   *    return true
   *  else
   *    return false
   */
  private boolean existRecord(Class c, Oid id) {
    int numIds = id.size();
    String cTable = dom.getDsm().getDomainClassName(c);
    
    StringBuffer sqlB = new StringBuffer("select * from ");
    sqlB.append(cTable);
    sqlB.append(" where ");
    DAttr idAttrib;
    Object idVal;
    
    for (int i = 0; i < numIds; i++) {
      idAttrib = id.getIdAttribute(i);
      idVal = id.getIdValue(i);
      sqlB.append(idAttrib.name()).
        append("=").  // idVal is never null
        append(toSQLString(idAttrib.type(), idVal, true)); 
      if (i < numIds-1) {
        sqlB.append(" and ");
      }
    }
    
    String sql = sqlB.toString();
    
    if (debug)
      System.out.println("DBToolKit.existRecord: sql = " + sql);
    
    try {
      ResultSet rs = executeQuery(sql);
      
      if (rs.next()) {
        // exists
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      // something wrong, ignore
      return false;
    }
  }
  
  /**
   * @effects 
   *  if exists a record in the table of <tt>c</tt> whose PK column <tt>idAttrib</tt> has value 
   *  equal to <tt>id</tt>
   *    return true
   *  else
   *    return false
   *    
   *  @version 3.2
   */  
  private boolean existRecord(Class c, DAttr idAttrib,
      Object idVal) {
    String cTable = dom.getDsm().getDomainClassName(c);
    
    StringBuffer sqlB = new StringBuffer("select * from ");
    sqlB.append(cTable);
    sqlB.append(" where ");
    
//    for (int i = 0; i < numIds; i++) {
//      idAttrib = id.getIdAttribute(i);
//      idVal = id.getIdValue(i);
    sqlB.append(idAttrib.name()).
      append("=").  // idVal is never null
      append(toSQLString(idAttrib.type(), idVal, true)); 
//      if (i < numIds-1) {
//        sqlB.append(" and ");
//      }
//    }
    
    String sql = sqlB.toString();
    
    if (debug)
      System.out.println("DBToolKit.existRecord: sql = " + sql);
    
    try {
      ResultSet rs = executeQuery(sql);
      
      if (rs.next()) {
        // exists
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      // something wrong, ignore
      return false;
    }
  }
  
  /**
   * @requires 
   *   c is a domain class  /\ 
   *   linkedObjId is a valid object id /\ 
   *   assoc and targetAssoc are two ends of an association from c to linked object's class
   *   
   * @effects 
   *  If exist object ids of <tt>c</tt> that are of the objects linked to the domain object 
   *  identified by <tt>linkedObjId</tt> via the specified association <tt>assoc</tt>
   *    return them as Collection
   *  else
   *    return null 
   *  
   *  <br>throws NotPossibleException if id values are invalid or 
   *  DBException if fails to read ids from the data source.
   */
  @Override
  public Collection<Oid> readLinkedObjectIds(Class c, Tuple2<DAttr,DAssoc> assoc, 
      Object linkedObj
      , Expression...exps // v3.0
      ) 
  throws NotPossibleException, DataSourceException {
    Query query = null;
    
    if (assoc != null) {
      DAttr fkAttrib = assoc.getFirst();
      /*v3.2: use ObjectExpression to ensure correct translation to SQL query
      Class targetClass = assoc.getSecond().associate().type();
      DomainConstraint[] idAttribs = dom.getDsm().getIDAttributeConstraints(targetClass);
      DomainConstraint pkAttrib = idAttribs[0];
      String fkColName = getFKColName(fkAttrib, pkAttrib);
      
      query = new Query();
      query.add(new Expression(fkColName, Op.EQ, linkedObjId.getIdValue(0)));
      */
      query = new Query();
      query.add(new ObjectExpression(c, fkAttrib, Op.EQ, linkedObj));
      
      /* v3.2: moved to outside if
      // v3.0
      if (exps != null)
        for (Expression exp : exps)
          query.add(exp);
      */
    }
    
    // v3.2: (see above)
    if (exps != null) {
      if (query == null)
        query = new Query();

      for (Expression exp : exps)
        query.add(exp);
    }

    return readObjectIds(c, query);
  }

  /**
   * @requires 
   *   c is a domain class  /\ 
   *   linkedObjId is a valid object id /\ 
   *   assoc and targetAssoc are two ends of an association from c to linked object's class
   *   
   * @effects 
   *  If exist object ids of <tt>c</tt> that are of the objects satisfying <tt>query</tt> and being 
   *  linked to the domain object identified by <tt>linkedObjId</tt> via the specified association <tt>assoc</tt>
   *    return them as Collection
   *  else
   *    return null 
   *  
   *  <br>throws NotPossibleException if id values are invalid or 
   *  DBException if fails to read ids from the data source.
   * @version 3.1
   */
  @Override
  public Collection<Oid> readLinkedObjectIds(Class c, Tuple2<DAttr,DAssoc> assoc, 
      Object linkedObj
      , Query query // v3.0
      ) 
  throws NotPossibleException, DataSourceException {
    if (assoc != null) {
      DAttr fkAttrib = assoc.getFirst();
      /*v3.2: use ObjectExpression to ensure correct translation to SQL query
      Class targetClass = assoc.getSecond().associate().type();
      DomainConstraint[] idAttribs = dom.getDsm().getIDAttributeConstraints(targetClass);
      DomainConstraint pkAttrib = idAttribs[0];
      String fkColName = getFKColName(fkAttrib, pkAttrib);

      query = new Query();
      query.add(new Expression(fkColName, Op.EQ, linkedObjId.getIdValue(0)));
      */
      query = new Query();
      query.add(new ObjectExpression(c, fkAttrib, Op.EQ, linkedObj));
      
      if (query == null)  // v3.2: added this check
        query = new Query();
      
      query.add(new ObjectExpression(c, fkAttrib, Op.EQ, linkedObj));
    }
    
    return readObjectIds(c, query);
  }
  
  // v3.1
//  /**
//   * @requires 
//   *   c is a domain class  /\ 
//   *   (query != null -> query is a valid Query over c) /\ 
//   *   (aggregateFunc != null -> aggregateFunc is a valid aggregate function over the attributes
//   *          of c)
//   *   
//   * @effects
//   * <pre> 
//   *  if query is not null 
//   *    translate <tt>query</tt> into a source query with <tt>aggregateFunc</tt> (if specified)
//   *    execute this query to find all the object ids of c from the data source
//   *    that satisfy it
//   *  else 
//   *    read all the object ids of c
//   *    
//   *  For a type-hierarchy, the object ids are created with the precise base class, 
//   *  i.e. the class that defines the objects bearing the ids.
//   *  
//   *  If exist object ids matching the query 
//   *    return them as Collection
//   *  else
//   *    return null 
//   *  
//   *  throws NotPossibleException if id values are invalid or 
//   *  DBException if fails to read ids from the data source.
//   *  </pre>   
//   *  
//   *  @example
//   *    <pre>
//   *    Example 1 (no query):
//   *    =====================
//   *    c = Student.class;
//   *    aggregateFunc = null;
//   *    query = null;
//   *    -> get all the object ids of Student.class
//   *    
//   *    Example 2 (with query):
//   *    ========================
//   *    c = Student.class;
//   *    aggregateFunc = null;
//   *    query = "name = Le";
//   *    -> get all the object ids of Student.class whose name is equal to "Le"
//   *    
//   *    Example 3 (with aggregate):
//   *    ===========================
//   *    c = Student.class;
//   *    aggregateFunc = "min";
//   *    query = "name = Le";
//   *    -> get the min object id of Student.class whose name is equal to "Le"
//   *    </pre>
//   *    @deprecated 
//   *      v2.6.4b: to be removed
//   */
//  public Collection<Oid> readObjectIdsOLD(final Class c, String aggregateFunc, final Query query) 
//  throws NotPossibleException, DataSourceException {
//    // the result
//    Collection<Oid> result = new ArrayList<Oid>();
//    //Query myQuery = query; 
//    
//    /*
//     * Translate query over c into an sql over the table of c
//     * example:
//     *  c = Student.class 
//     *  query = name="Le"
//     *  
//     *  -> sql = SELECT id from student where name='Le'
//     */
//    StringBuffer sqlSb = new StringBuffer();
//    Collection<String> Select = new ArrayList<String>();
//    Collection<String> From = new ArrayList<String>();
//    Collection<String> Where = null; 
//    StringBuffer orderBy = new StringBuffer(" order by ");
//    
//    // table var (if any) that refers to the table containing the attrib
//    String tableVar = null; 
//    final String cTable = dom.getDsm().getDomainClassName(c);
//    
//    // update From, Where from query
//    if (query != null) {
//      Where = new ArrayList();
//      Iterator<Expression> exps = query.terms();
//      Expression exp;
//      while (exps.hasNext()) {
//        exp = exps.next();
//        if (exp instanceof ObjectExpression) {
//          // if exp.attrib belongs to a super-class then must create a join
//          String tempVar = updateQueryOLD(From, Where, (ObjectExpression) exp);
//          if (tableVar == null) //  only need to update tableVar once
//            tableVar = tempVar;
//        } else {
//          // sub-query expression
//          Where.add(toSQLExpression(exp,true));
//        }
//      }
//    }
//
//    // determine if we need to read ids of the sub-types
//    Class[] subTypes = dom.getDsm().getSubClasses(c);
//    if (subTypes != null) {
//      if (Where == null) Where = new ArrayList();
//      
//      // has sub-types, read object ids of the sub-types first
//      // each is a recursive call
//      // in addition, create a sub-query clause of query to exclude the ids of the sub-types
//      // (for use below)
//      Collection<Oid> subIds;
//      
//      List<Field> idFields = dom.getDsm().getIDAttributes(c);
//      if (idFields.size() > 1)
//        throw new NotImplementedException(
//            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//            "Tính năng hiện không được hỗ trợ: {0}", "compoundKey(" + c + ")");
//
//      String cid = idFields.get(0).getName();
//      StringBuffer nestedSQL = new StringBuffer();
//      Class t;
//      Query subQuery;
//      int numSubTypes = subTypes.length;
//      for (int i = 0; i < numSubTypes; i++) {
//        t = subTypes[i];
//        // <<recursive>> read the object ids of this sub-type
//        // rewrite query over t (without valid checking)
//        subQuery = (query != null) ? rewriteQuery(query, t) : null;
//        
//        //TODO: better to check whether the query is valid for executing over t
//        // e.g. query may refer to attribute that only exists in the super-type
//        try {
//          subIds = readObjectIds(t, subQuery);
//          
//          if (subIds != null)
//            result.addAll(subIds);
//        } catch (DataSourceException e) {
//          // probably query not suitable for this subtype -> ignore 
//        }
//
//        
//        // update the nested query to ignore this sub-type
//        nestedSQL.append("select " + cid + " from "
//            + dom.getDsm().getDomainClassName(subTypes[i]));
//        if (i < numSubTypes - 1)
//          nestedSQL.append(LF).append(" UNION ");
//      }
//      
//      // add the sub-query created above to query
//      // using tableVar (if specified)
//      String idColName = ((tableVar != null) ? tableVar : cTable) + "." + cid; //schema.getDsm().getDomainClassName(c) + "." + cid;
//      Where.add(idColName + " not in (" + nestedSQL.toString()+")");
//    } // end sub-types 
//    
//    if (tableVar == null) {
//      // no table var was generated from the update of Where, add cTable to it
//      From.add(cTable);
//    }
//    
//    /*
//     * create query
//     */
//    
//    // select the id attribute of c
//    sqlSb.append("select ");
//    DomainConstraint dc;
//    final DomainConstraint[] idAttribs = dom.getDsm().getIDAttributeConstraints(c);
//
//    // ASSUME: one id attribute (see above) (change this to use the loop (below) if 
//    // this restriction is removed)
//    dc =  idAttribs[0];
//
//    String colName = (tableVar == null) ? dc.name() : tableVar + "." + dc.name();
//    
//    if (aggregateFunc != null) {
//      // ASSUME: aggregateFunc does not need groupBy
//      sqlSb.append(aggregateFunc).
//        append("(").
//        append(colName).//dc.name()).
//        append(")");
//      
//      // TODO: no need for order by here really, but keeps it for consistency 
//      orderBy.append(aggregateFunc).
//      append("(").
//      append(colName).//dc.name()).
//      append(")");
//    } else {
//      sqlSb.append(colName);//dc.name()).dc.name());
//      orderBy.append(colName);//dc.name()).dc.name());
//    }
//    
//    // from 
//    sqlSb.append(" from ");
//    int ind = 0;
//    int size = From.size();
//    for (String f : From) {
//      sqlSb.append(f);
//      if (ind < size-1)
//        sqlSb.append(",");
//      ind++;
//    }
//    
//    if (Where != null) {
//      // add Where to Sql
//      sqlSb.append(" where ");
//      size = Where.size();
//      ind = 0;
//      for (String w : Where) {
//        sqlSb.append(w);
//        if (ind < size-1) {
//          sqlSb.append(" and ");
//        }
//        ind++;
//      }
//    }
//    // order by (ascending) 
//    orderBy.append(" ASC ");
//    sqlSb.append(orderBy);
//    
//    String sql = sqlSb.toString();
//    
//    if (debug)
//      System.out.println("DBToolKit.readObjectIds: " + sql);
//
//    ResultSet rs = null;
//    
//    try {
//      rs = executeQuery(sql);
//    } catch (Exception e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY, e,
//          new Object[]{sql} );
//    }  
//    
//    // process result set
//    Oid oid;
//    Object idVal;
//    try {
//      while (rs.next()) {
//        oid = null;
//        for (int i = 1; i <= idAttribs.length; i++) {
//          dc = idAttribs[i-1];
//          idVal = sqlToJava(c, dc, rs, i);
//  
//          if (idVal != null) {
//            if (!(idVal instanceof Comparable))
//              throw new NotPossibleException(NotPossibleException.Code.INVALID_OBJECT_ID_TYPE, 
//                  "Mã đối tượng không hợp lệ {0}<{1}>:{2} (cần kiều Comparable)",c.getSimpleName(),"-",idVal);
//  
//            if (oid == null) oid = new Oid(c);
//            
//            oid.addIdValue(dc, (Comparable) idVal);
//          }
//        }
//        
//        if (oid != null)
//          result.add(oid);
//      }
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
//          new Object[] {c.getSimpleName()});
//    }
//  
//    return (result.isEmpty()) ? null : result;
//
//  }
  

  
//  /**
//   * @requires 
//   *   c is a domain class  /\ 
//   *   (query != null -> query is a valid Query over c) /\ 
//   *   (aggregateFunc != null -> aggregateFunc is a valid aggregate function over the attributes
//   *          of c)
//   *   
//   * @effects
//   * <pre> 
//   *  if query is not null 
//   *    translate <tt>query</tt> into a source query with <tt>aggregateFunc</tt> (if specified)
//   *    execute this query to find all the object ids of c from the data source
//   *    that satisfy it
//   *  else 
//   *    read all the object ids of c
//   *    
//   *  For a type-hierarchy, the object ids are created with the precise base class, 
//   *  i.e. the class that defines the objects bearing the ids.
//   *  
//   *  If exist object ids matching the query 
//   *    return them as Collection
//   *  else
//   *    return null 
//   *  
//   *  throws NotPossibleException if id values are invalid or 
//   *  DBException if fails to read ids from the data source.
//   *  </pre>   
//   *  
//   *  @example
//   *    <pre>
//   *    Example 1 (no query):
//   *    =====================
//   *    c = Student.class;
//   *    aggregateFunc = null;
//   *    query = null;
//   *    -> get all the object ids of Student.class
//   *    
//   *    Example 2 (with query):
//   *    ========================
//   *    c = Student.class;
//   *    aggregateFunc = null;
//   *    query = "name = Le";
//   *    -> get all the object ids of Student.class whose name is equal to "Le"
//   *    
//   *    Example 3 (with aggregate):
//   *    ===========================
//   *    c = Student.class;
//   *    aggregateFunc = "min";
//   *    query = "name = Le";
//   *    -> get the min object id of Student.class whose name is equal to "Le"
//   *    </pre>
//   */
//  @Override
//  public Collection<Oid> readObjectIds(final Class c, String aggregateFunc, final Query query) 
//  throws NotPossibleException, DataSourceException {
//// v3.3: call anothe method
//    Class orderByClass = null;
//    return readObjectIds(c, aggregateFunc, query, orderByClass);
////    /*v2.7: use a sorted Set for result if there are sub-types
////    // the result
////    Collection<Oid> result = new ArrayList<Oid>();*/
////    Collection<Oid> result;
////    Class[] subTypes = dom.getDsm().getSubClasses(c);
////    if (subTypes != null) {
////      // use sorted set
////      result = new TreeSet<Oid>();
////    } else {
////      result = new ArrayList<Oid>();
////    }
////      
////      
////    /*
////     * Translate query over c into an sql over the table of c
////     * example:
////     *  c = Student.class 
////     *  query = name="Le"
////     *  
////     *  -> sql = SELECT id from student where name='Le'
////     */
////    StringBuffer sqlSb = new StringBuffer();
////    Collection<String> Select = new ArrayList<String>();
////    java.util.Map<Class,TableSpec> From = new HashMap<Class,TableSpec>();
////    Collection<JoinSpec> Joins = new ArrayList<JoinSpec>();
////    Collection<String> Where = null; 
////    StringBuffer orderBy = new StringBuffer(" order by ");
////    
////    // table var (if any) that refers to the table containing the attrib
////    //String tableVar = null;
////    final String cTable = dom.getDsm().getDomainClassName(c);
////    TableSpec cTableSpec = new TableSpec(c, null, cTable);
////    
////    // update From, Where from query
////    if (query != null) {
////      Where = new ArrayList();
////      Iterator<Expression> exps = query.terms();
////      Expression exp;
////      
////      int tableIndex = 0; // the index used to create table vars
////      int[] tableInd = {tableIndex};// use array to pass table index
////      
////      while (exps.hasNext()) {
////        exp = exps.next();
////        if (exp instanceof ObjectExpression) {
////          updateQuery(From, Where, cTableSpec, tableInd, Joins, (ObjectExpression) exp);
////        } else {
////          // sub-query expression
////          Where.add(toSQLExpression(exp,true));
////        }
////      }
////    }
////
////    // determine if we need to read ids of the sub-types
////    
////    if (subTypes != null) {
////      if (Where == null) Where = new ArrayList();
////      
////      // has sub-types, read object ids of the sub-types first
////      // each is a recursive call
////      // in addition, create a sub-query clause of query to exclude the ids of the sub-types
////      // (for use below)
////      Collection<Oid> subIds;
////      
////      List<Field> idFields = dom.getDsm().getIDAttributes(c);
////      if (idFields.size() > 1)
////        throw new NotImplementedException(
////            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
////            "Tính năng hiện không được hỗ trợ: {0}", "compoundKey(" + c + ")");
////
////      String cid = idFields.get(0).getName();
////      StringBuffer nestedSQL = new StringBuffer();
////      Class t;
////      Query subQuery;
////      int numSubTypes = subTypes.length;
////      for (int i = 0; i < numSubTypes; i++) {
////        t = subTypes[i];
////        // <<recursive>> read the object ids of this sub-type
////        // rewrite query over t (without valid checking)
////        subQuery = (query != null) ? rewriteQuery(query, t) : null;
////        
////        //TODO: better to check whether the query is valid for executing over t
////        // e.g. query may refer to attribute that only exists in the super-type
////        try {
////          /*v3.2: FIXED bug: not passing aggregateFunc in as argument
////          subIds = readObjectIds(t, subQuery);
////          */
////          subIds = readObjectIds(t, aggregateFunc, subQuery);
////          
////          if (subIds != null)
////            result.addAll(subIds);
////        } catch (DataSourceException e) {
////          // probably query not suitable for this subtype -> ignore
////          if (debug)
////            throw e;
////        }
////
////        
////        // update the nested query to ignore this sub-type
////        nestedSQL.append("select " + cid + " from "
////            + dom.getDsm().getDomainClassName(t));
////        if (i < numSubTypes - 1)
////          nestedSQL.append(LF).append(" UNION ");
////      }
////      
////      // add the sub-query created above to query
////      // using tableVar (if specified)
////      String idColName = ((cTableSpec.var != null) ? cTableSpec.var : cTable) + "." + cid;
////          //((tableVar != null) ? tableVar : cTable) + "." + cid; 
////      Where.add(idColName + " not in (" + nestedSQL.toString()+")");
////    } // end sub-types 
////    
////    if (cTableSpec.var == null) {//(tableVar == null) {
////      // no table var was generated from the update of Where, only use cTable as table name 
////      From.put(c, new TableSpec(c, null, cTable));  // cTable
////    }
////    
////    if (debug) System.out.printf("Query class table spec: %s -> %s %n", c.getSimpleName(), cTableSpec.toString());
////    
////    /*
////     * create query
////     */
////    // select the id attribute of c
////    sqlSb.append("select ");
////    DAttr dc;
////    final DAttr[] idAttribs = dom.getDsm().getIDAttributeConstraints(c);
////
////    // ASSUME: one id attribute (see above) (change this to use the loop (below) if 
////    // this restriction is removed)
////    dc =  idAttribs[0];
////
////    String colName = (cTableSpec.var == null) ? dc.name() : cTableSpec.var + "." + dc.name(); 
////        //(tableVar == null) ? dc.name() : tableVar + "." + dc.name();
////    
////    if (aggregateFunc != null) {
////      // ASSUME: aggregateFunc does not need groupBy
////      sqlSb.append(aggregateFunc).
////        append("(").
////        append(colName).//dc.name()).
////        append(")");
////      
////      // TODO: no need for order by here really, but keeps it for consistency 
////      orderBy.append(aggregateFunc).
////      append("(").
////      append(colName).//dc.name()).
////      append(")");
////    } else {
////      sqlSb.append(colName);//dc.name()).dc.name());
////      orderBy.append(colName);//dc.name()).dc.name());
////    }
////    
////    // from 
////    sqlSb.append(" from ");
////    int ind = 0;
////    int size = From.size();
////    //for (String f : From) {
////    for(TableSpec s : From.values()) {
////      sqlSb.append(s.toString());
////      if (ind < size-1)
////        sqlSb.append(",");
////      ind++;
////    }
////    
////    if (Where != null
////          && !Where.isEmpty() // v3.1: added this check
////        ) {
////      // add Where to Sql
////      sqlSb.append(" where ");
////      size = Where.size();
////      ind = 0;
////      for (String w : Where) {
////        sqlSb.append(w);
////        if (ind < size-1) {
////          sqlSb.append(" and ");
////        }
////        ind++;
////      }
////    }
////    // order by (ascending) 
////    orderBy.append(" ASC ");
////    sqlSb.append(orderBy);
////    
////    String sql = sqlSb.toString();
////    
////    if (debug)
////      System.out.println("DBToolKit.readObjectIds: " + sql);
////
////    ResultSet rs = null;
////    
////    try {
////      rs = executeQuery(sql);
////    } catch (Exception e) {
////      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY, e,
////          new Object[]{sql} );
////    }  
////    
////    // process result set
////    Oid oid;
////    Object idVal;
////    try {
////      while (rs.next()) {
////        oid = null;
////        for (int i = 1; i <= idAttribs.length; i++) {
////          dc = idAttribs[i-1];
////          idVal = sqlToJava(c, dc, rs, i);
////  
////          if (idVal != null) {
////            if (!(idVal instanceof Comparable))
////              throw new NotPossibleException(NotPossibleException.Code.INVALID_OBJECT_ID_TYPE, 
////                  new Object[] {c.getSimpleName(),"-",idVal});
////  
////            if (oid == null) oid = new Oid(c);
////            
////            oid.addIdValue(dc, (Comparable) idVal);
////          }
////        }
////        
////        if (oid != null)
////          result.add(oid);
////      }
////    } catch (SQLException e) {
////      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
////          new Object[] {c.getSimpleName()});
////    }
////  
////    return (result.isEmpty()) ? null : result;
//  }
  
  @Override
  public Collection<Oid> readObjectIds(Class c, String aggregateFunc,
      Query query, final boolean withSubTypes, Class... orderByClass) throws NotPossibleException,
      DataSourceException {
    /*v2.7: use a sorted Set for result if there are sub-types
    // the result
    Collection<Oid> result = new ArrayList<Oid>();*/
    Collection<Oid> result;
    Class[] subTypes = null; // v3.3 = dom.getDsm().getSubClasses(c);
    if (withSubTypes) {
      subTypes = dom.getDsm().getSubClasses(c);
      if (subTypes != null) {        
        // use sorted set
        result = new TreeSet<>();
      } else {
        result = new ArrayList<>();
      }
    } else {
      result = new ArrayList<>();
    }
      
    /*
     * Translate query over c into an sql over the table of c
     * example:
     *  c = Student.class 
     *  query = name="Le"
     *  
     *  -> sql = SELECT id from student where name='Le'
     */
    StringBuffer sqlSb = new StringBuffer();
    Collection<String> Select = new ArrayList<String>();
    /* From: conversion structure for the conversion of the domain classes involved in an object query 
     *  into the SQL tables. This conversion structure will be used to define the FROM clause of the equivalent SQL query.    
     *  
     *  <p>More specifically, this map contains both standard mappings from domain classes of the object query to SQL table specs and 
     *   auxiliary mappings that are used in special object join expressions.
     */
    DualMap<Class,TableSpec> From = new DualMap<>(); //v3.3: HashMap<Class,TableSpec>();
    Collection<JoinSpec> Joins = new ArrayList<JoinSpec>();
    Collection<String> Where = null; 
    
    // table var (if any) that refers to the table containing the attrib
    //String tableVar = null;
    final String cTable = dom.getDsm().getDomainClassName(c);
    TableSpec cTableSpec = new TableSpec(c, null, cTable);
    
    // update From, Where from query
    if (query != null) {
      Where = new ArrayList();
      /* v3.3: use shared method 
      Iterator<Expression> exps = query.terms();
      Expression exp;
      
      int tableIndex = 0; // the index used to create table vars
      int[] tableInd = {tableIndex};// use array to pass table index
      
      while (exps.hasNext()) {
        exp = exps.next();
        if (exp instanceof ObjectExpression) {
          updateQuery(From, Where, cTableSpec, tableInd, Joins, (ObjectExpression) exp);
        } //v3.3: support AttributeExpression
        else if (exp instanceof AttributeExpression) {
          updateQueryWithAttributeExpression(From, Where, cTableSpec, tableInd, Joins, (AttributeExpression) exp);
        }
        else {
          // sub-query expression
          Where.add(toSQLExpression(exp,true));
        }
      }
      */
      translateQuery(query, From, Where, cTableSpec, Joins);      
    }

    // determine if we need to read ids of the sub-types
    if (withSubTypes && // v3.3: support this check 
        subTypes != null) {
      // has sub-types, read object ids of the sub-types first
      // each is a recursive call
      // in addition, create a sub-query clause of query to exclude the ids of the sub-types
      // (for use below)
      
      if (Where == null) Where = new ArrayList();
      
      Collection<Oid> subIds;
      
      java.util.Map<Field,DAttr> idFields = dom.getDsm().getIDAttributes(c);
      if (idFields.size() > 1)
        throw new NotImplementedException(
            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
            new Object[] {"compoundKey(" + c + ")"});

      String cid = idFields.keySet().iterator().next().getName(); //idFields.get(0).getName();
      StringBuffer nestedSQL = new StringBuffer();
      Class t;
      Query subQuery;
      int numSubTypes = subTypes.length;
      for (int i = 0; i < numSubTypes; i++) {
        t = subTypes[i];
        // <<recursive>> read the object ids of this sub-type
        // rewrite query over t (without valid checking)
        // v3.3: add c as argument
        //subQuery = (query != null) ? rewriteQuery(query, t) : null;
        subQuery = (query != null) ? rewriteQuery(query, c, t) : null;
        
        //TODO: better to check whether the query is valid for executing over t
        // e.g. query may refer to attribute that only exists in the super-type
        try {
          /*v3.2: FIXED bug: not passing aggregateFunc in as argument
          subIds = readObjectIds(t, subQuery);
          */
          subIds = readObjectIds(t, aggregateFunc, subQuery);
          
          if (subIds != null)
            result.addAll(subIds);
        } catch (DataSourceException e) {
          // probably query not suitable for this subtype -> ignore
          if (debug)
            throw e;
        }

        
        // update the nested query to ignore this sub-type
        nestedSQL.append("select " + cid + " from "
            + dom.getDsm().getDomainClassName(t));
        if (i < numSubTypes - 1)
          nestedSQL.append(LF).append(" UNION ");
      }
      
      // add the sub-query created above to query
      // using tableVar (if specified)
      String idColName = ((cTableSpec.var != null) ? cTableSpec.var : cTable) + "." + cid;
          //((tableVar != null) ? tableVar : cTable) + "." + cid; 
      Where.add(idColName + " not in (" + nestedSQL.toString()+")");
    } // end sub-types 
    
    if (cTableSpec.var == null) {//(tableVar == null) {
      // no table var was generated from the update of Where, only use cTable as table name 
      From.put(c, new TableSpec(c, null, cTable));  // cTable
    }
    
    if (debug) System.out.printf("Query class table spec: %s -> %s %n", c.getSimpleName(), cTableSpec.toString());
    
    /*
     * create query
     */
    StringBuffer orderBy = new StringBuffer(" order by ");

    // from:
    StringBuffer FromSb = new StringBuffer(" from ");
    int ind = 0;
    int size = From.size();
    for(TableSpec s : From.values()) {
      /* v3.3: need to process orderByClass separately to preserve the order of the order-by classes
      // if s is for orderByClass then extract its id column to use in order by
      if (orderByInd < orderBySize) {
        for (Class orderByCls : orderByClass) {
          if (s.getCls().equals(orderByCls)) { // found an orderByClass
            orderBy.append(getIdColumnFor(s));
            if (orderByInd < orderBySize-1)
              orderBy.append(", ");
            
            orderByInd++;
            if (foundOrderByClass == false) foundOrderByClass = true;
            break;
          }
        }
      }
      */
      FromSb.append(s.toString());
      if (ind < size-1) FromSb.append(",");
      
      ind++;
    }

    // order by:
    int orderByInd = 0;
    int orderBySize = (orderByClass != null && orderByClass.length > 0) ? orderByClass.length : 0;
    boolean foundOrderByClass = false;
    /* v3.3: need to process orderByClass separately to preserve the order of the order-by classes */
    // if s is for orderByClass then extract its id column to use in order by
    if (orderBySize > 0) {
      for (Class orderByCls : orderByClass) {
        for (TableSpec s : From.values()) {
          if (s.getCls().equals(orderByCls)) { // found an orderByClass
            orderBy.append(getIdColumnFor(s));
            if (orderByInd < orderBySize - 1)
              orderBy.append(", ");

            orderByInd++;
            if (foundOrderByClass == false) foundOrderByClass = true;
            break;
          }
        }
      }
    }

    if (orderBySize > 0 && !foundOrderByClass) {
      // error: one of the orderByClasses is not found among the classes in the from clause
      throw new DataSourceException(DataSourceException.Code.INVALID_ORDER_BY_CLASS, new Object[] {Arrays.toString(orderByClass)});
    }
    
    // select: (the id attribute of c)
    sqlSb.append("select ");
    DAttr dc;
    final List<DAttr> idAttribs = dom.getDsm().getIDDomainConstraints(c);

    // ASSUME: one id attribute (see above) (change this to use the loop (below) if 
    // this restriction is removed)
    dc =  idAttribs.get(0);

    String colName = (cTableSpec.var == null) ? dc.name() : cTableSpec.var + "." + dc.name(); 
    
    // v3.3: support orderByClass
    if (aggregateFunc != null) {
      // ASSUME: aggregateFunc does not need groupBy
      sqlSb.append(aggregateFunc).
        append("(").
        append(colName).//dc.name()).
        append(")");
      
      // no need for order by here really, but keeps it for consistency
      if (orderBySize == 0) {
        orderBy.append(aggregateFunc).
                append("(").append(colName).append(")");
      }
    } else {
      sqlSb.append(colName);
      if (orderBySize == 0)
        orderBy.append(colName);
    }
    
    // from 
    sqlSb.append(FromSb);
    
    if (Where != null
          && !Where.isEmpty() // v3.1: added this check
        ) {
      // add Where to Sql
      sqlSb.append(" where ");
      size = Where.size();
      ind = 0;
      for (String w : Where) {
        sqlSb.append(w);
        if (ind < size-1) {
          sqlSb.append(" and ");
        }
        ind++;
      }
    }
    // order by (ascending) 
    orderBy.append(" ASC ");  // TODO: could use separate sorting orders for different order-by classes
    sqlSb.append(orderBy);
    
    String sql = sqlSb.toString();
    
    if (debug)
      System.out.println("DBToolKit.readObjectIds: " + sql);

    ResultSet rs = null;
    
    try {
      rs = executeQuery(sql);
    } catch (Exception e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY, e,
          new Object[]{sql} );
    }  
    
    // process result set
    Oid oid;
    Object idVal;
    try {
      while (rs.next()) {
        oid = null;
        for (int i = 1; i <= idAttribs.size(); i++) {
          dc = idAttribs.get(i-1);
          idVal = sqlToJava(c, dc, rs, i);
  
          if (idVal != null) {
            if (!(idVal instanceof Comparable))
              throw new NotPossibleException(NotPossibleException.Code.INVALID_OBJECT_ID_TYPE, 
                  new Object[] {c.getSimpleName(),"-",idVal});
  
            if (oid == null) oid = new Oid(c);
            
            oid.addIdValue(dc, (Comparable) idVal);
          }
        }
        
        if (oid != null)
          result.add(oid);
      }
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
          new Object[] {c.getSimpleName()});
    }
  
    return (result.isEmpty()) ? null : result;
  }
  
  @Override
  public boolean existObject(Class c, Query q) throws DataSourceException {
    String sql = genSelectIdQuery(c, q);
    
    // execute and check that we can obtain some record
    ResultSet rs = null;
    
    try {
      rs = executeQuery(sql);
    } catch (Exception e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY, e,
          new Object[]{sql} );
    }  
    
    // process result set
    Object idVal;
    try {
      if (rs.next()) {
        // has some value
        return true;
      } else {
        return false;
      }
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
          new Object[] {c.getSimpleName()});
    }  
  }
  
  /**
   * @requires
   *  id-column(s) are owned by the domain class specified in <tt>tableSpec</tt> (not by an ancestor type)
   *  
   * @effects 
   *  return the SQL's friendly id-column of the <b>first</b> id-attribute of the domain class represented by <tt>tableSpec</tt>
   *  
   *  <p>throws DataSourceException if <tt>tableSpec</tt> does not contain require information about the underly 
   *  data store; NotPossibleException if pre-condition is violated
   *  
   * @version 3.3
   */
  private Object getIdColumnFor(TableSpec tableSpec) throws DataSourceException, NotPossibleException {
    
    Class c = tableSpec.getCls();
    
    if (tableSpec.getVar() == null)
      throw new DataSourceException(DataSourceException.Code.INVALID_DATA_SOURCE_SPEC, new Object[] {c, tableSpec});
    
    DAttr dc;
    final List<DAttr> idAttribs = dom.getDsm().getIDDomainConstraints(c);

    // ASSUME: one id attribute (see above) (change this to use the loop (below) if 
    // this restriction is removed)
    dc =  idAttribs.get(0);

    if (debug) {
      // check to make sure that dc is owned by c
      Class owner = dom.getDsm().getDeclaringClass(c, dc);
      if (owner != c) {
        throw new NotPossibleException(NotPossibleException.Code.INVALID_CLASS_NOT_THE_OWNER_OF_ID_ATTRIBUTE, new Object[] {c, dc.name()});
      }
    }
    
    String idKeyColumn = tableSpec.getVar() + "." + dc.name();
    
    return idKeyColumn;
  }

  /**
   * @requires 
   *  all id-column(s) of <tt>c</tt> are owned by <tt>c</tt> (not by any ancestor types)
   *  
   * @effects 
   *  return the SQL's friendly id-column(s) of <b>all</b> id-attribute(s) of the domain class <tt>c</tt>
   *  
   *  <p>throws NotPossibleException if pre-condition is violated
   *  
   * @version 3.3
   */
  private String[] getIdColumnsFor(Class c, String cTableVar) throws NotPossibleException {
    final List<DAttr> idAttribs = dom.getDsm().getIDDomainConstraints(c);

    String[] idCols = new String[idAttribs.size()];
    
    Class owner;
    int i = 0;
    for (DAttr dc : idAttribs) {
      if (debug) {
        // check to make sure that dc is owned by c
        owner = dom.getDsm().getDeclaringClass(c, dc);
        if (owner != c) {
          throw new NotPossibleException(NotPossibleException.Code.INVALID_CLASS_NOT_THE_OWNER_OF_ID_ATTRIBUTE, new Object[] {c, dc.name()});
        }
      }
      
      idCols[i] = cTableVar + "." + dc.name();
      i++;
    }
    
    return idCols;
  }
  
  /**
   * @effects 
   *  return a copy of <tt>sourceQuery</tt> that contains the same terms that are defined 
   *  over the domain class <tt>c</tt> instead of being over <tt>oldCls</tt>
   *  
   * @version 
   * - 3.3: added oldCls
   */
  private Query rewriteQuery(Query sourceQuery, Class oldCls, Class c) {
    Query newQuery = new Query();
    Iterator<Expression> terms = sourceQuery.terms();
    Expression exp, newExp;
    ObjectExpression oexp;
    ObjectJoinExpression oje;
    AttributeExpression axp;
    while (terms.hasNext()) {
      exp = terms.next();
      /*v3.3: support use of oldCls
      if (exp instanceof ObjectJoinExpression) {
        newExp = ObjectJoinExpression.createInstance(c, (ObjectJoinExpression) exp);
      } else if (exp instanceof ObjectExpression) {
        // copy as an object expression over c
        //oexp = (ObjectExpression) exp;
        newExp = ObjectExpression.createInstance(c, (ObjectExpression) exp);//new ObjectExpression(c, oexp.getDomainAttribute(), exp.getOperator(), exp.getVal());
      } else if (exp instanceof AttributeExpression) { // v3.3: added support 
        newExp = AttributeExpression.createInstance(oldCls, c, (AttributeExpression) exp);
      } else {
        // copy the same
        newExp = Expression.createInstance(exp); //new Expression(exp.getVar(), exp.getOperator(), exp.getVal());
      }
      */
      if (exp instanceof ObjectJoinExpression) {
        oje = (ObjectJoinExpression) exp;
        if (oje.getDomainClass().equals(oldCls))  // rewrite
          newExp = ObjectJoinExpression.createInstance(c, oje);
        else  // keep the same
          newExp = exp;
      } else if (exp instanceof ObjectExpression) {
        // copy as an object expression over c
        oexp = (ObjectExpression) exp;
        if (oexp.getDomainClass().equals(oldCls))  // rewrite
          newExp = ObjectExpression.createInstance(c, oexp);
        else  // keep the same
          newExp = exp;
      } else if (exp instanceof AttributeExpression) { // v3.3: added support 
        axp = (AttributeExpression) exp;
        if (axp.isAppliedTo(oldCls)) // rewrite
          newExp = AttributeExpression.createInstance(oldCls, c, axp);
        else // keep the same
          newExp = exp;
      } else {
        // copy the same
        newExp = Expression.createInstance(exp); //new Expression(exp.getVar(), exp.getOperator(), exp.getVal());
      }
      
      newQuery.add(newExp);
    }
    
    return newQuery;
  }
  

  /**
   * @requires 
   *  <tt>query</tt> is a Select object query defined over <tt>c</tt> /\ 
   *  <tt>query.getDomainAttributeCount() > 0</tt> has source domain attributes
   *  
   * @effects 
   *  translate <tt>query</tt> to the equivalent SQL SELECT query and return the result 
   *  
   * @version 3.3
   */
  private String translateSelectQuery(Class c, FlexiQuery query) {
    StringBuffer sqlSb = new StringBuffer();
    Collection<String> Select = new ArrayList<String>();
    // v3.3: java.util.Map<Class,TableSpec> From = new HashMap<Class,TableSpec>();
    DualMap<Class,TableSpec> From = new DualMap<>();
    Collection<JoinSpec> Joins = new ArrayList<>();
    Collection<String> Where = null; 
    
    // table var (if any) that refers to the table containing the attrib
    //String tableVar = null;
    final String cTable = dom.getDsm().getDomainClassName(c);
    TableSpec cTableSpec = new TableSpec(c, null, cTable);
    
    // update From, Where from query
    Where = new ArrayList();
    
    // do translation
    translateQuery(query, From, Where, cTableSpec, Joins);
    
    
    if (cTableSpec.var == null) {//(tableVar == null) {
      // no table var was generated from the update of Where, only use cTable as table name 
      From.put(c, new TableSpec(c, null, cTable));  // cTable
    }
    
    if (debug) System.out.printf("Query class table spec: %s -> %s %n", c.getSimpleName(), cTableSpec.toString());
    
    /*
     * create query
     */
    //StringBuffer orderBy = new StringBuffer(" order by ");

    // select: (the id attribute of c)
    sqlSb.append("select ");
    int numAttribs = query.getDomainAttributeCount();
    DAttr dc;
    String colName;
    for (int i = 0; i < numAttribs; i++) {
      dc = query.getDomainAttribute(i);
      colName = (cTableSpec.var == null) ? dc.name() : cTableSpec.var + "." + dc.name();
      sqlSb.append(colName);
      if (i < numAttribs-1) sqlSb.append(",");
    }
    
    //orderBy.append(colName);
    
    // from 
    StringBuffer FromSb = new StringBuffer(" from ");
    int ind = 0;
    int size = From.size();
    for(TableSpec s : From.values()) {
      FromSb.append(s.toString());
      if (ind < size-1)
        FromSb.append(",");
      ind++;
    }
    sqlSb.append(FromSb);
    
    if (Where != null && !Where.isEmpty()) {
      // add Where to Sql
      sqlSb.append(" where ");
      size = Where.size();
      ind = 0;
      for (String w : Where) {
        sqlSb.append(w);
        if (ind < size-1) {
          sqlSb.append(" and ");
        }
        ind++;
      }
    }
    
    // order by (ascending) 
    //orderBy.append(" ASC ");
    //sqlSb.append(orderBy);
    
    String sql = sqlSb.toString();
    
    return sql;
  }
  
  /**
   * Translate an object query to an equivalent (partial) SQL query that contains only details for the 
   * From and Where clauses as well as all the Joins.    
   * 
   * @modifies query, tableSpec, From, Where, Joins
   *  
   * @effects 
   *  translate <tt>query</tt> into suitable SQL query structure represented by <tt>From, Where, Joins</tt> 
   *  
   *  <p>Part of this translation is to group expressions of <tt>query</tt> such that each group is mapped to 
   *  a table var of a domain class. The same domain class may be given different table vars, one for each group. 
   *  
   *  <p>Set <tt>tableSpec.var</tt> to a table variable that is used to denote the table corresponding the 
   *  domain class of <tt>query</tt>
   *  
   * @version 3.3
   */
  private void translateQuery (
      final Query query, 
      final DualMap<Class, TableSpec> From,
      final Collection<String> Where, 
      final TableSpec qClsTableSpec, 
      final Collection<JoinSpec> Joins) {
    
    // todo(): group query expressions
    // change From to Map<Group,TableSpec>
    
    Iterator<Expression> exps = query.terms();
    Expression exp;
    
    int tableIndex = 0; // the index used to create table vars
    int[] tableInd = {tableIndex};// use array to pass table index
    
    while (exps.hasNext()) {
      exp = exps.next();
      
      if (exp instanceof ObjectJoinExpression) {
        updateQueryWithJoinExpression(From, Where, qClsTableSpec, tableInd, Joins, (ObjectJoinExpression) exp);
      } else if (exp instanceof ObjectExpression) {
        updateQueryWithObjectExpression(From, Where, qClsTableSpec, tableInd, Joins, 
            null, // useTableSpec 
            (ObjectExpression) exp);
      } else if (exp instanceof AttributeExpression) {
        updateQueryWithAttributeExpression(From, Where, qClsTableSpec, tableInd, Joins, (AttributeExpression) exp);
      } 
      // support other expression types here (if needed)
      else {
        // sub-query expression
        Where.add(toSQLExpression(exp,true));
      }
    }
  }

// v3.3: merged into translateQuery  
//  /**
//   * @modifies tableSpec, From, Where, Joins
//   *  
//   * @effects 
//   *  converts <tt>exp</tt> into a suitable SQL expression and add it to Where; 
//   *  if <tt>exp</tt> is <tt>ObjectExpression</tt> and <tt>exp.attrib</tt> belongs to a super-class then also add an SQL join from 
//   *  <tt>exp.domainClass</tt> to the super-class;
//   *  
//   *  <p>Update <tt>From</tt> with suitable SQL table variables.
//   *  <p>Set <tt>tableSpec.var</tt> to a table variable that is used to denote the table corresponding the 
//   *  domain class <tt>exp.domainClass</tt>
//   */
//  private void updateQuery (
//      final java.util.Map<Class, TableSpec> From,
//      final Collection<String> Where, 
//      final TableSpec tableSpec, 
//      final int[] tableInd,
//      final Collection<JoinSpec> Joins,
//      final ObjectExpression exp) {
//    if (exp instanceof ObjectJoinExpression)
//      updateQueryWithJoinExpression(From, Where, tableSpec, tableInd, Joins, (ObjectJoinExpression) exp);
//    else
//      updateQueryWithObjectExpression(From, Where, tableSpec, tableInd, Joins, exp);
//  }

  /**
   * @modifies tableSpec, From, Where, Joins
   *  
   * @effects 
   *  converts <tt>jexp</tt> into a suitable SQL expression and add it to Where; 
   *  if <tt>jexp.attrib</tt> belongs to a super-class then also add an SQL join from 
   *  <tt>jexp.domainClass</tt> to the super-class;
   *  
   *  <p>Update <tt>From</tt> with suitable SQL table variables.
   *  <p>If <tt>tableSpec.cls = exp.domainClass</tt> and <tt>tableSpec.var</tt>  has not been set then 
   *    set it to a suitable table var
   *  @version 
   *  - 3.0: improved to support {@link ObjectAttributeExpression} <br>
   *  - 3.1: improved to update tableSpec using From when needed  <br>
   *  - 3.3: improved to support {@link ObjectAttributeExpression} and {@link ObjectJoinOnAttributeExpression} 
   */
  private void updateQueryWithJoinExpression(
      final DualMap<Class,TableSpec> From, 
      final Collection<String> Where, 
      final TableSpec tableSpec, 
      final int[] tableInd,
      final Collection<JoinSpec> Joins,
      final ObjectJoinExpression jexp) {
// v3.3: improved
//     /*
//      *  let e1 = e.val
//      *  process e by creating an SQL join between e and e1 
//      *  if e1 is another join expression (i.e. a join chain)
//      *    invoke a recursive call on e1
//      *  else
//      *   invoke updateQueryWithObjectExpression on e1 
//      */
//    final Class c = jexp.getDomainClass();
//    final DAttr attrib = jexp.getDomainAttribute();
//    ObjectExpression targetExp = jexp.getTargetExpression();
//
//    // the class that is joined with c
//    final Class jc = targetExp.getDomainClass(); //jexp.getTargetExpression().getDomainClass();
//    
//    // update c.attrib into the query structure
//    TableSpec attribTableSpec = updateQueryWithAttribute(From, Where, tableSpec, tableInd, Joins, c, attrib);
//
//    // the column name of attrib
//    Class a = attribTableSpec.cls;    // a = c \/ a = super(c)
//    String tA = attribTableSpec.var;  // the table var of a
//    String colName = getColName(a, attrib);
//    //String colVar = tA + "." + colName;
//
//    // get table var for c
//    int tIndex = tableInd[0];
//    final String cTable = dom.getDsm().getDomainClassName(c);
//    String tcTable;
//    // if c is not in From, add it; else retrieve its var
//    TableSpec cTableSpec = From.get(c);
//    if (cTableSpec == null) {
//      tcTable = "t"+(tIndex++);
//      cTableSpec = new TableSpec(c, tcTable, cTable);
//      From.put(c, cTableSpec);
//    } else {
//      tcTable = cTableSpec.var;
//    }
//    
//    /*
//     * Now, add Sql join expression between c and tc to Where
//     */
//    // get table var for jc
//    String jcTable = dom.getDsm().getDomainClassName(jc);
//    String tjcTable;
//    
//    //todo(); // v3.3: support the case where jc = c but uses a different table var 
//    TableSpec jcTableSpec = From.get(jc);
//    if (jcTableSpec == null) {
//      tjcTable = "t"+(tIndex++);
//      jcTableSpec = new TableSpec(jc, tjcTable, jcTable);
//      From.put(jc, jcTableSpec);
//    } else {
//      tjcTable = jcTableSpec.var;
//    }
//
//    // v3.3: instead of only using id-attribute, use jexp.targetExpression.attribute (if available)
//    //DAttr jattrib = dom.getDsm().getIDDomainConstraints(jc).get(0);
//    DAttr jattrib;
//    if (targetExp instanceof ObjectAttributeExpression && ((ObjectAttributeExpression)targetExp).isUsingAttributeForJoin()) {
//      jattrib = targetExp.getDomainAttribute();
//    } else {
//      jattrib = dom.getDsm().getIDDomainConstraints(jc).get(0);
//    }
//    
//    String jcolName = getColName(jc,jattrib);
//    JoinSpec jspec = new JoinSpec(c, attrib, jc, jattrib);
//    Joins.add(jspec);
//    
//    // the join expression
//    Where.add(
//        // v2.7.2: join(tcTable, colName, tjcTable, jcolName)
//        join(((a!=c) ? tA : tcTable), colName, tjcTable, jcolName)
//        );
//    
//    // update data structures before continue
//    tableInd[0] = tIndex;
//    
//    /*v3.1: improved to look up for table var in FROM if c is neq tableSpec.cls
//     * this enables support for more flexible join definition in which the order of the join classes
//     * does not affect how their table vars are generated 
//     * 
//      // if the expression's class is the same as tableSpec and 
//      // the table var in the spec has not be set then set it
//      if (c == tableSpec.cls && tableSpec.var == null) {
//        tableSpec.var = tcTable;
//      }
//     *
//     */
//    // if the expression's class is the same as tableSpec and 
//    // the table var in the spec has not be set then set it
//    if (tableSpec.var == null) {
//      if (c == tableSpec.cls) { 
//        tableSpec.var = tcTable;
//      } else {
//        // added this case: look up in FROM
//        for (TableSpec tspec : From.values()) {
//          if (tableSpec.cls == tspec.cls) {
//            tableSpec.var = tspec.var; 
//            break;
//          }
//        }
//      }
//    }
//
//    // determine what to do with the value expression
//    if (targetExp instanceof ObjectJoinExpression) {
//      // recursive call on valExp
//      updateQueryWithJoinExpression(From, Where, jcTableSpec, tableInd, Joins, (ObjectJoinExpression)targetExp); 
//    } 
//    else if (targetExp instanceof ObjectAttributeExpression) { // v3.0: added this case
//      // a simple object expression exists for joining (above), nothing more to do with this valExp
//    }
//    else {
//      // process as a normal object expression
//      updateQueryWithObjectExpression(From, Where, jcTableSpec, tableInd, Joins, targetExp);
//    }

    final Class c = jexp.getDomainClass();
    final DAttr attrib = jexp.getDomainAttribute();
    
    ObjectExpression targetExp = jexp.getTargetExpression();
    
    // update c.attrib into the query structure
    TableSpec attribTableSpec = updateQueryWithAttribute(From, Where, tableSpec, tableInd, Joins, c, attrib);

    // the column name of attrib
    Class a = attribTableSpec.cls;    // a = c \/ a = super(c)
    String tA = attribTableSpec.var;  // the table var of a
    String colName = getColName(a, attrib);

    // get table var for c
    int tIndex = tableInd[0];
    final String cTable = dom.getDsm().getDomainClassName(c);
    String tcTable;
    // if c is not in From, add it; else retrieve its var
    TableSpec cTableSpec = From.get(c);
    if (cTableSpec == null) {
      tcTable = "t"+(tIndex++);
      cTableSpec = new TableSpec(c, tcTable, cTable);
      From.put(c, cTableSpec);
    } else {
      tcTable = cTableSpec.var;
    }
    
    tableInd[0] = tIndex;
    
    /*
     * Now, add Sql join expression between c and tc to Where
     */
    // v3.3: use shared method 
//    String jcTable = dom.getDsm().getDomainClassName(jc);
//    String tjcTable;
//    TableSpec jcTableSpec = From.get(jc);
//    if (jcTableSpec == null) {
//      tjcTable = "t"+(tIndex++);
//      jcTableSpec = new TableSpec(jc, tjcTable, jcTable);
//      From.put(jc, jcTableSpec);
//    } else {
//      tjcTable = jcTableSpec.var;
//    }
    // get table var for jc
    TableSpec jcTableSpec = getTargetJoinClassTableSpec(jexp, From, tableInd);
    String tjcTable = jcTableSpec.getVar();

    // v3.3: use shared method 
    DAttr jattrib = getJoinAttribute(jexp);
    //DAttr jattrib = jexp.getAttrib2();
//    if (targetExp instanceof ObjectAttributeExpression && ((ObjectAttributeExpression)targetExp).isUsingAttributeForJoin()) {
//      jattrib = targetExp.getDomainAttribute();
//    } else {
//      jattrib = dom.getDsm().getIDDomainConstraints(jc).get(0);
//    }
    
    // the target joined class (jc)
    Class jc = targetExp.getDomainClass(); 
    
    String jcolName = getColName(jc,jattrib);
    JoinSpec jspec = new JoinSpec(c, attrib, jc, jattrib);
    Joins.add(jspec);
    
    // the join expression
    Where.add(
        join(((a!=c) ? tA : tcTable), colName, tjcTable, jcolName)
        );
    
    // if the expression's class is the same as tableSpec and 
    // the table var in the spec has not be set then set it
    if (tableSpec.var == null) {
      if (c == tableSpec.cls) { 
        tableSpec.var = tcTable;
      } else {
        // added this case: look up in FROM
        for (TableSpec tspec : From.values()) {
          if (tableSpec.cls == tspec.cls) {
            tableSpec.var = tspec.var; 
            break;
          }
        }
      }
    }

    // determine what to do next with the target expression
    if (targetExp instanceof ObjectJoinExpression) {
      // recursive call on valExp
      updateQueryWithJoinExpression(From, Where, tableSpec, // v3.3: jcTableSpec, 
          tableInd, Joins, (ObjectJoinExpression)targetExp); 
    } 
    else if (targetExp instanceof ObjectAttributeExpression) { // v3.0: added this case
      // a simple object expression exists for joining (above), nothing more to do with this valExp
    }
    else {  // ObjectExpression
      // process as a normal object expression
      updateQueryWithObjectExpression(From, Where, tableSpec, // v3.3: jcTableSpec, 
          tableInd, Joins, jcTableSpec, targetExp);
    }
  }

//  /**
//   * @effects 
//   *  converts <tt>exp</tt> into a suitable SQL expression and add it to Where; 
//   *  if <tt>exp</tt> is <tt>ObjectExpression</tt> and <tt>exp.attrib</tt> belongs to a super-class then also add an SQL join from 
//   *  <tt>exp.domainClass</tt> to the super-class;
//   *  
//   *  <p>Update <tt>From</tt> with suitable SQL table variables.
//   *  <p>If <tt>tableSpec.cls = exp.domainClass</tt> and <tt>tableSpec.var</tt>  has not been set then 
//   *    set it to a suitable table var  
//   */
//  private void updateQueryWithJoinExpression(
//      final java.util.Map<Class,TableSpec> From, 
//      final Collection<String> Where, 
//      final TableSpec tableSpec, 
//      final int[] tableInd,
//      final Collection<JoinSpec> Joins,
//      final ObjectExpression nextExp) {
//     
//     /*
//      *  let e1 = e.val
//      *  process e by creating an SQL join between e and e1 
//      *  if e1 is another join expression (i.e. a join chain)
//      *    invoke a recursive call on e1
//      *  else
//      *   invoke updateQueryWithObjectExpression on e1 
//      */
//    final Class c = nextExp.getDomainClass();
//    final DomainConstraint attrib = nextExp.getDomainAttribute();
//    /*
//    ObjectExpression valExp = jexp.getTargetExpression();
//
//    // the class that is joined with c
//    final Class jc = jexp.getTargetExpression().getDomainClass();
//    */
//    ObjectExpression valExp; ObjectJoinExpression jexp;
//    Class jc;
//    if (nextExp instanceof ObjectJoinExpression) {
//      jexp = (ObjectJoinExpression) nextExp;
//      valExp = jexp.getTargetExpression();
//      jc = jexp.getTargetExpression().getDomainClass();
//    } else {  // a simple expression (without value expression)
//      valExp = null;
//      jc = nextExp.getDomainClass();
//    }
//    
//    // update c.attrib into the query structure
//    TableSpec attribTableSpec = updateQueryWithAttribute(From, Where, tableSpec, tableInd, Joins, c, attrib);
//
//    // the column name of attrib
//    Class a = attribTableSpec.cls;    // a = c \/ a = super(c)
//    String tA = attribTableSpec.var;  // the table var of a
//    String colName = getColName(a, attrib);
//    //String colVar = tA + "." + colName;
//
//    // get table var for c
//    int tIndex = tableInd[0];
//    final String cTable = dom.getDsm().getDomainClassName(c);
//    String tcTable;
//    // if c is not in From, add it; else retrieve its var
//    TableSpec cTableSpec = From.get(c);
//    if (cTableSpec == null) {
//      tcTable = "t"+(tIndex++);
//      cTableSpec = new TableSpec(c, tcTable, cTable);
//      From.put(c, cTableSpec);
//    } else {
//      tcTable = cTableSpec.var;
//    }
//    
//    /*
//     * Now, add Sql join expression between c and tc to Where
//     */
//    // get table var for jc
//    String jcTable = dom.getDsm().getDomainClassName(jc);
//    String tjcTable;
//    TableSpec jcTableSpec = From.get(jc);
//    if (jcTableSpec == null) {
//      tjcTable = "t"+(tIndex++);
//      jcTableSpec = new TableSpec(jc, tjcTable, jcTable);
//      From.put(jc, jcTableSpec);
//    } else {
//      tjcTable = jcTableSpec.var;
//    }
//
//    DomainConstraint jattrib = dom.getDsm().getIDDomainConstraints(jc).get(0);
//    String jcolName = getColName(jc,jattrib);
//    JoinSpec jspec = new JoinSpec(c, attrib, jc, jattrib);
//    Joins.add(jspec);
//    
//    // the join expression
//    Where.add(
//        // v2.7.2: join(tcTable, colName, tjcTable, jcolName)
//        join(((a!=c) ? tA : tcTable), colName, tjcTable, jcolName)
//        );
//    
//    // update data structures before continue
//    tableInd[0] = tIndex;
//    
//    // if the expression's class is the same as tableSpec and 
//    // the table var in the spec has not be set then set it
//    if (c == tableSpec.cls && tableSpec.var == null) {
//      tableSpec.var = tcTable;
//    }
//
//    // determine what to do with the value expression
//    if (valExp instanceof ObjectJoinExpression) {
//      // recursive call on valExp
//      updateQueryWithJoinExpression(From, Where, jcTableSpec, tableInd, Joins, (ObjectJoinExpression)valExp); 
//    } 
//    else if (valExp instanceof ObjectAttributeExpression) { // v3.0: added this case
//      
//    }
//    else {
//      // process as a normal object expression
//      updateQueryWithObjectExpression(From, Where, jcTableSpec, tableInd, Joins, valExp);
//    }
//  }
  
  /**
   * @requires 
   * tableInd.length=1
   * 
   * @modifies  From, Where, tableSpec, tableInd, Joins 
   * 
   * @effects 
   *  return a <tt>TableSpec</tt> of the exact table that contains the column corresponding to 
   *  the domain attribute <tt>c.attrib</tt> (the table may be mapped to a super-class of <tt>c</tt>)
   *  
   *  <p>If <tt>attrib</tt> is declared in an ancestor class of <tt>c</tt> then add to <tt>Joins, Where</tt> the id-join sequence
   *  from <tt>c</tt> to that class and add all the tables of the classes in this sequence to <tt>From</tt>
   *  
   *  <p>Update <tt>tableInd[0]</tt> if it is changed to accommodate new tables. 
   */
  private TableSpec updateQueryWithAttribute(
      final DualMap<Class,TableSpec> From, 
      final Collection<String> Where, 
      final TableSpec tableSpec, 
      final int[] tableInd,
      final Collection<JoinSpec> Joins,
      final Class c, 
      final DAttr attrib) {    
    Class sup = dom.getDsm().getSuperClass(c);
    int tIndex = tableInd[0];
    final String cTable = dom.getDsm().getDomainClassName(c);
    String tcTable;
    
    // add c to From
    TableSpec cTableSpec = From.get(c);
    if (cTableSpec == null) {
      tcTable = "t"+(tIndex++);
      cTableSpec = new TableSpec(c, tcTable, cTable);
      From.put(c, cTableSpec);//cTable + " " + tcTable);
    } else {
      tcTable = cTableSpec.var;
    }

    final Class a = dom.getDsm().getDeclaringClass(c, attrib);
    String tA;
    Collection<DAttr> idAttribs;
    String supTable, tsupTable;
    Class currClass;
    String currTable = cTable;
    String tcurrTable = tcTable;
    JoinSpec jspec;
    TableSpec supSpec;
    
    if (a != c) {
     /* attrib is inherited from an ancestor domain class a,  
      *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
      *    let s = SQL_Join(c,x,...,a) (x may = a)
      *    add s to WHERE
      *    add x,...,a to FROM
      */
      currClass = c;
      do {
        supTable = dom.getDsm().getDomainClassName(sup);
        // add super class table to FROM (if not already added)
        supSpec = From.get(sup);
        if (supSpec == null) {
          tsupTable = "t" + (tIndex++);
          supSpec = new TableSpec(sup, tsupTable, supTable);
          From.put(sup,supSpec);
        } else {
          tsupTable = supSpec.var;
        }

        // use the id attributes to add new join expressions
        idAttribs = dom.getDsm().getIDDomainConstraints(sup);
        for (DAttr f : idAttribs) { // current table
          // (if not already added) add join expressions between the id attributes of the two tables
          jspec = new JoinSpec(currClass, f, sup, f);
          if (!Joins.contains(jspec)) {
            Where.add(
                join(currClass, tcurrTable, sup, tsupTable, f)
                );
            Joins.add(jspec);
          } 
        } // end for

        // add super class table to FROM (if not already added)
        //From.add(supTable + " " + tsupTable);
       // tables.add(supTable);
        
        // recursive: check the super-super class and so on...
        currTable = supTable;
        currClass = sup;
        tcurrTable = tsupTable;
        sup = dom.getDsm().getSuperClass(sup);
      } while (sup != null);
      
      // attribute table is the last super class name
      tA = tcurrTable;
    } else {
      // attrib is in c
      tA = tcTable;
    } // end if 

    // update data structures before return
    tableInd[0] = tIndex;
    
    /*v3.1: improved to look up for table var in FROM if c is neq tableSpec.cls
     * this enables support for more flexible join definition in which the order of the join classes
     * does not affect how their table vars are generated 
     * 
    // if the expression's class is the same as tableSpec and 
    // the table var in the spec has not be set then set it
    if (c == tableSpec.cls && tableSpec.var == null) {
      tableSpec.var = tcTable;
    }
     *
     */
    // if the expression's class is the same as tableSpec and 
    // the table var in the spec has not be set then set it
    if (tableSpec.var == null) {
      if (c == tableSpec.cls) { 
        tableSpec.var = tcTable;
      } else {
        // added this case: look up in FROM
        for (TableSpec tspec : From.values()) {
          if (tableSpec.cls == tspec.cls) {
            tableSpec.var = tspec.var; 
            break;
          }
        }
      }
    }
    
    return new TableSpec(a, tA, null);
  }
  
  /**
   * @modifies  From, Where, tableSpec, tableInd, Joins 
   * 
   * @requires 
   * <tt>useAuxTableSpec != null => </tt> <tt>exp.attrib</tt> must be owned by <tt>exp.domainClass</tt> (i.e. not owned by one 
   *  of its ancestor classes)
   * 
   * @effects 
   *  converts <tt>exp</tt> into a suitable SQL expression and add it to Where. 
   *  
   *  <br>If <tt>useAuxTableSpec != null</tt> then use it for the domain class of <tt>exp</tt> 
   *   
   *  <br>If <tt>exp.attrib</tt> belongs to a super-class then also add an SQL join from 
   *  <tt>exp.domainClass</tt> to the super-class;
   *  
   *  <p>Update <tt>From</tt> with suitable SQL table variables.
   *  <p>If <tt>tableSpec.cls = exp.domainClass</tt> and <tt>tableSpec.var</tt>  has not been set then 
   *    set it to a suitable table var
   *    
   *  <p>Throws IllegalArgumentException if pre-condition is not met
   *  
   *  @version 
   *  - 3.3: added useAuxTableSpec
   */
  private void updateQueryWithObjectExpression(
      final DualMap<Class,TableSpec> From, 
      final Collection<String> Where, 
      final TableSpec tableSpec, 
      final int[] tableInd,
      final Collection<JoinSpec> Joins,
      TableSpec useAuxTableSpec, final ObjectExpression exp) throws IllegalArgumentException {
    final Class c = exp.getDomainClass();
    final DAttr attrib = exp.getDomainAttribute();

    /* v3.3: support useAuxTableSpec
    TableSpec attribTableSpec = updateQueryWithAttribute(From, Where, tableSpec, tableInd, Joins, c, attrib);
    */
    TableSpec attribTableSpec;
    if (useAuxTableSpec != null) {  // use it (due to the pre-condition)
      // validate pre-condition to be sure
      if (!c.equals(dom.getDsm().getDeclaringClass(c, attrib))) {
        // invalid
        throw new IllegalArgumentException(getClass().getSimpleName()+".updateQueryWithObjectExpression: pre-condition is not met due to attribute("+attrib.name()+") is not owned by " + c.getSimpleName());
      }
      
      attribTableSpec = useAuxTableSpec;
    } else {
      attribTableSpec = updateQueryWithAttribute(From, Where, tableSpec, tableInd, Joins, c, attrib);
    }
    
    Class a = attribTableSpec.cls;
    String tA = attribTableSpec.var;
    
    String colName = getColName(a, attrib);
    String colVar = tA + "." + colName;
    
    /*
     *   Add Sql expression to Where
     */
    Where.add(toSQLExpression(exp, colVar, true));
    
    /* v3.3: support useAuxTableSpec
    // add c to From
    int tIndex = tableInd[0];
    TableSpec cTableSpec = From.get(c);
    String cTable = dom.getDsm().getDomainClassName(c);
    String tcTable;
    if (cTableSpec == null) {
      tcTable = "t"+(tIndex++);
      cTableSpec = new TableSpec(c, tcTable, cTable); 
      From.put(c,cTableSpec);
    } else {
      tcTable = cTableSpec.var;
    }
    
    // update data structure before return
    tableInd[0] = tIndex;
    */
    String tcTable;
    if (useAuxTableSpec == null) {
      // add c to From
      int tIndex = tableInd[0];
      TableSpec cTableSpec = From.get(c);
      String cTable = dom.getDsm().getDomainClassName(c);
      if (cTableSpec == null) {
        tcTable = "t"+(tIndex++);
        cTableSpec = new TableSpec(c, tcTable, cTable); 
        From.put(c,cTableSpec);
      } else {
        tcTable = cTableSpec.var;
      }
      
      // update data structure before return
      tableInd[0] = tIndex;
    } else {
      // use useAuxTableSpec
      tcTable = useAuxTableSpec.getVar();
    }
    
    /*v3.1: improved to look up for table var in FROM if c is neq tableSpec.cls
     * this enables support for more flexible join definition in which the order of the join classes
     * does not affect how their table vars are generated 
     * 
    // if the expression's class is the same as tableSpec and 
    // the table var in the spec has not be set then set it
    if (c == tableSpec.cls && tableSpec.var == null) {
      tableSpec.var = tcTable;
    }
     *
     */
    // if the expression's class is the same as tableSpec and 
    // the table var in the spec has not be set then set it
    if (tableSpec.var == null) {
      if (c == tableSpec.cls) { 
        tableSpec.var = tcTable;
      } else {
        // added this case: look up in FROM
        for (TableSpec tspec : From.values()) {
          if (tableSpec.cls == tspec.cls) {
            tableSpec.var = tspec.var; 
            break;
          }
        }
      }
    }
  }
  
  /**
   * @modifies  From, Where, tableSpec, tableInd, Joins 
   * 
   * @effects 
   *  converts <tt>exp</tt> into a suitable SQL expression and add it to Where; 
   *  any attributes in <tt>exp</tt> that belong to a super-class will result in suitable SQL join expressions 
   *  being added for the association between the corresponding domain classes in <tt>exp.domainClass</tt> and the super-class;
   *  
   *  <p>Update <tt>From</tt> with suitable SQL table variables.
   * @version 3.3
   */
  private void updateQueryWithAttributeExpression(
      final DualMap<Class,TableSpec> From, 
      final Collection<String> Where, 
      final TableSpec tableSpec, 
      final int[] tableInd,
      final Collection<JoinSpec> Joins,
      final AttributeExpression exp) {
    final Class c1 = exp.getClass1();
    final Class c2 = exp.getClass2();
    final DAttr attrib1 = exp.getAttrib1();
    final DAttr attrib2 = exp.getAttrib2();

    TableSpec attribTableSpec1 = updateQueryWithAttribute(From, Where, tableSpec, tableInd, Joins, c1, attrib1);
    TableSpec attribTableSpec2 = updateQueryWithAttribute(From, Where, tableSpec, tableInd, Joins, c2, attrib2);

    // table column var of attrib1
    Class a1 = attribTableSpec1.getCls();
    String tA1 = attribTableSpec1.getVar();
    String colName1 = getColName(a1, attrib1);
    String colVar1 = tA1 + "." + colName1;

    // table column var of attrib2
    Class a2 = attribTableSpec2.getCls();
    String tA2 = attribTableSpec2.getVar();
    String colName2 = getColName(a2, attrib2);
    String colVar2 = tA2 + "." + colName2;

    /*
     *   Add Sql expression between attrib1 and attrib2 to Where
     */
    Where.add(toSQLExpression(exp, colVar1, colVar2, true));
    
    // add c1, c2 to From
    int tIndex = tableInd[0];
    TableSpec cTableSpec1 = From.get(c1);
    String cTable1 = dom.getDsm().getDomainClassName(c1);
    String tcTable1;
    if (cTableSpec1 == null) {
      tcTable1 = "t"+(tIndex++);
      cTableSpec1 = new TableSpec(c1, tcTable1, cTable1); 
      From.put(c1,cTableSpec1);
    } else {
      tcTable1 = cTableSpec1.var;
    }
    
    TableSpec cTableSpec2 = From.get(c2);
    String cTable2 = dom.getDsm().getDomainClassName(c2);
    String tcTable2;
    if (cTableSpec2 == null) {
      tcTable2 = "t"+(tIndex++);
      cTableSpec2 = new TableSpec(c2, tcTable2, cTable2); 
      From.put(c2,cTableSpec2);
    } else {
      tcTable2 = cTableSpec2.var;
    }
    
    // update data structure before return
    tableInd[0] = tIndex;
    
    /*v3.1: improved to look up for table var in FROM if c is neq tableSpec.cls
     * this enables support for more flexible join definition in which the order of the join classes
     * does not affect how their table vars are generated 
     */
    // if the expression's class is the same as tableSpec and 
    // the table var in the spec has not be set then set it
    if (tableSpec.getVar() == null) {
      if (c1 == tableSpec.getCls()) { 
        tableSpec.setVar(tcTable1);
      } else if (c2 == tableSpec.getCls()) { 
        tableSpec.setVar(tcTable2);
      } else {
        // added this case: look up in FROM
        for (TableSpec tspec : From.values()) {
          if (tableSpec.getCls() == tspec.getCls()) {
            tableSpec.setVar(tspec.getVar()); 
            break;
          }
        }
      }
    }
  }
  
  
  /**
   * @effects 
   *  return a suitable attribute in one of the component domain classes in <tt>jexp</tt> that will be used 
   *  as the join attribute.  
   *  
   * @version 3.3
   */
  private DAttr getJoinAttribute(ObjectJoinExpression jexp) {
    ObjectExpression targetExp = jexp.getTargetExpression();
    
    DAttr jattrib;
    if (jexp instanceof ObjectJoinOnAttributeExpression) {
      // use 2nd attribute of jexp
      jattrib = ((ObjectJoinOnAttributeExpression) jexp).getAttrib2();
    } else if (targetExp instanceof ObjectAttributeExpression
        && ((ObjectAttributeExpression) targetExp).isUsingAttributeForJoin()) {
      // use attribute of targetExp
      jattrib = targetExp.getDomainAttribute();
    } else {
      // use key attribute of targetExp.domainClass
      Class jc = targetExp.getDomainClass();
      jattrib = dom.getDsm().getIDDomainConstraints(jc).get(0);
    }
    
    return jattrib;
  }

  /**
   * @modifies From, tableInd
   * 
   * @effects 
   *  create or retrieve from <tt>From</tt> the {@link TableSpec} for the target join class in <tt>jexp</tt> 
   *  and return it.
   *  
   *  <p>In the former case, the created {@link TableSpec} is added to <tt>From.auxMap</tt>
   *  
   *  <p><tt>tableInd</tt> is updated with the most current table index value.
   *    
   * @version 3.3
   */
  private TableSpec getTargetJoinClassTableSpec(final ObjectJoinExpression jexp,
      DualMap<Class, TableSpec> From, int[] tableInd) {
    Class c = jexp.getDomainClass();
    Class jc = jexp.getTargetExpression().getDomainClass();
    
    String jcTable = dom.getDsm().getDomainClassName(jc);
    String tjcTable;
    int tIndex = tableInd[0];
    
    TableSpec jcTableSpec;
    
    if (jc.equals(c)) {
      // jc eq c: use a different table spec (regardless if whether c already has one)
      // and put it to From's auxiliary map
      tjcTable = "t"+(tIndex++);
      jcTableSpec = new TableSpec(jc, tjcTable, jcTable);
      From.putAux(jc, jcTableSpec);
    } else { // jc neq c
      jcTableSpec = From.get(jc);
      if (jcTableSpec == null) {
        tjcTable = "t"+(tIndex++);
        jcTableSpec = new TableSpec(jc, tjcTable, jcTable);
        From.put(jc, jcTableSpec);
      } 
  //    else {
  //      tjcTable = jcTableSpec.var;
  //    }
    }
    
    tableInd[0] = tIndex;
    
    return jcTableSpec;
  }

  // represents an SQL table spec <var,name>
  private static class TableSpec {
    Class cls;
    String var;
    String name;
    
    TableSpec(Class cls, String var, String name) {
      this.cls = cls;
      this.var = var;
      this.name = name;
    }
    
    public void setVar(String var) {
      this.var = var;
    }

    @Override
    public String toString() {
      if (var != null)
        return name + " " + var;
      else
        return name;
    }
        
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      TableSpec other = (TableSpec) obj;

      if (cls == null) {
        if (other.cls != null)
          return false;
      } else if (cls != other.cls)
        return false;
      return true;
    }

    /**
     * @effects 
     *  return cls
     * @version 3.2
     */
    public Class getCls() {
      return cls;
    }

    /**
     * @effects 
     *  return var
     * @version 3.2
     */
    public String getVar() {
      return var;
    }

    /**
     * @effects 
     *  return name
     * @version 3.2
     */
    public String getName() {
      return name;
    }        
  } // end TableSpec
  
  // represents an SQL table join
  private static class JoinSpec {
    JoinSpec(Class c1, DAttr a1, Class c2, DAttr a2) {
      this.c1 = c1;
      this.c2 = c2;
      this.a1 = a1;
      this.a2 = a2;
    }
    Class c1, c2;
    DAttr a1, a2;
    
    @Override
    public String toString() {
      return c1.getSimpleName() + "." + a1.name() + "="+
             c2.getSimpleName() + "." + a2.name();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      
      JoinSpec other = (JoinSpec) obj;

      if (a1 == null) {
        if (other.a1 != null)
          return false;
      } else if (!a1.equals(other.a1))
        return false;
      if (a2 == null) {
        if (other.a2 != null)
          return false;
      } else if (!a2.equals(other.a2))
        return false;
      if (c1 == null) {
        if (other.c1 != null)
          return false;
      } else if (!c1.equals(other.c1))
        return false;
      if (c2 == null) {
        if (other.c2 != null)
          return false;
      } else if (!c2.equals(other.c2))
        return false;
      return true;
    }
  } // end JoinSpec
  
// v3.1  
//  /**
//   * @effects 
//   *  converts <tt>exp</tt> into a suitable SQL expression and add it to Where; 
//   *  if <tt>exp</tt> is <tt>ObjectExpression</tt> and <tt>exp.attrib</tt> belongs to a super-class then also add an SQL join from 
//   *  <tt>exp.domainClass</tt> to the super-class;
//   *  
//   *  <p>Update <tt>From</tt> with suitable SQL table variables.
//   *  
//   *   <p>Return the table var of the table of <tt>exp.domainClass</tt>
//   * 
//   * @deprecated
//   *  v2.6.4.b
//   */
//  private String updateQueryOLD(Collection<String> From, Collection<String> Where, ObjectExpression exp) {
//    final Class c = exp.getDomainClass();
//    final DomainConstraint attrib = exp.getDomainAttribute();
//    //Object val = exp.getVal();
//    
//    Class sup = dom.getDsm().getSuperClass(c);
//    int tIndex = 0;
//    final String cTable = dom.getDsm().getDomainClassName(c);
//    final String tcTable = "t"+(tIndex++);
//    
//    final Class a = dom.getDsm().getDeclaringClass(c, attrib);
//    String tA;
//    Collection<DomainConstraint> idAttribs;
//    String supTable, tsupTable;
//    Class currClass;
//    String currTable = cTable;
//    String tcurrTable = tcTable;
//
//    // add c to From
//    From.add(cTable + " " + tcTable);
//
//    String colName;
//    String sqlVar;
//    
//    if (a != c) {
//     /* attrib is inherited from an ancestor domain class a,  
//      *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
//      *    let s = SQL_Join(c,x,...,a) (x may = a)
//      *    add s to WHERE
//      *    add x,...,a to FROM
//      */
//      currClass = c;
//      do {
//        supTable = dom.getDsm().getDomainClassName(sup);
//        tsupTable = "t" + (tIndex++);
//
//        // use the id attributes to add new join expressions
//        idAttribs = dom.getDsm().getIDDomainConstraints(sup);
//        for (DomainConstraint f : idAttribs) { // current table
//          // add join expressions between the id attributes of the two tables
//          Where.add(
//              join(currClass, tcurrTable, sup, tsupTable, f)
//              );
//          
//          //joinTablePairs.add(currTable+"-"+supTable);
//        } // end for
//
//        // add super class table to FROM
//        From.add(supTable + " " + tsupTable);
//       // tables.add(supTable);
//        
//        // recursive: check the super-super class and so on...
//        currTable = supTable;
//        currClass = sup;
//        tcurrTable = tsupTable;
//        sup = dom.getDsm().getSuperClass(sup);
//      } while (sup != null);
//      
//      // attribute table is the last super class name
//      tA = tcurrTable;
//    } else {
//      // attrib is in c
//      tA = tcTable;
//    } // end if 
//    
//    colName = getColName(a, attrib);
//    sqlVar = tA + "." + colName;
//    //sqlVal = toSQLString(attrib.type(), val);
//    //Where.add(sqlVar + "=" + sqlVal);
//    /*
//     *   Add Sql expression to Where
//     */
//    Where.add(toSQLExpression(exp, sqlVar,true));
//    
//    // return the table var
//    return tcTable;
//  }
  
//  /**
//   * @requires 
//   *   c is a domain class  /\ 
//   *   (query != null -> query is a valid Query over c)
//   *   
//   * @effects
//   * <pre> 
//   *  if query is not null 
//   *    translate <tt>query</tt> into a source query and 
//   *    execute this query to find all the object ids of c from the data source
//   *    that satisfy it
//   *  else 
//   *    read all the object ids of c
//   *    
//   *  For a type-hierarchy, the object ids are created with the precise base class, 
//   *  i.e. the class that defines the objects bearing the ids.
//   *  
//   *  If exist object ids matching the query 
//   *    return them as Collection
//   *  else
//   *    return null 
//   *  
//   *  throws NotPossibleException if id values are invalid or 
//   *  DBException if fails to read ids from the data source.
//   *  </pre>   
//   */
//  public Collection<Oid> readObjectIds(final Class c, final Query query) 
//  throws NotPossibleException, DataSourceException {
//    return readObjectIds(c, null, query);
//  }
  

  /**
   * @effects 
   *  read from data source and return a Collection of values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt>, 
   *  or return <tt>null</tt> if no domain objects of <tt>c</tt> exist
   * @version 
   * - 3.1: support domain-typed attribute
   */
  @Override
  public Collection readAttributeValues(Class c, DAttr attrib) {
    String sql = genSelect(c, null, attrib, false); //genSelect(c, attrib, false);
    
    if (debug)
      System.out.println("DBToolKit.readAttributeValues: " + sql);

    try {
      ResultSet rs = executeQuery(sql);
      Object attribVal;
      Collection result = new ArrayList();
      
      /*v3.1: support domain-typed attribute*/
      DAttr actualAttrib;
      if (attrib.type().isDomainType()) {
        // retrieve the id-attribute of the referenced type and use it to extract value (below)
        DSMBasic dsm = getDom().getDsm();
        Class domainType = dsm.getDomainClassFor(c, attrib);
        List<DAttr> refIdAttribs = dom.getDsm().getIDDomainConstraints(domainType);
        actualAttrib = refIdAttribs.get(0);
      } else {
        actualAttrib = attrib;
      }
      
      // collect the values from rs
      while (rs.next()) {
        /*v3.1: use actual attrib
        attribVal = sqlToJava(c, attrib, rs, 1);
        */
        attribVal = sqlToJava(c, actualAttrib, rs, 1);
        result.add(attribVal);
      }
      
      return result.isEmpty() ? null : result;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  @Override
  public Collection readAttributeValues(Class c, DAttr attrib, FlexiQuery query) {
    // simply execute query and return the result as Collection
    String sql = translateSelectQuery(c, query);
    
    if (debug)
      System.out.println(getClass().getSimpleName()+".readAttributeValues: " + sql);
    
    try {
      ResultSet rs = executeQuery(sql);
      Object attribVal;
      Collection result = new ArrayList();
      
      DAttr actualAttrib;
      if (attrib.type().isDomainType()) {
        // retrieve the id-attribute of the referenced type and use it to extract value (below)
        DSMBasic dsm = getDom().getDsm();
        Class domainType = dsm.getDomainClassFor(c, attrib);
        List<DAttr> refIdAttribs = dom.getDsm().getIDDomainConstraints(domainType);
        actualAttrib = refIdAttribs.get(0);
      } else {
        actualAttrib = attrib;
      }
      
      // collect the values from rs
      while (rs.next()) {
        attribVal = sqlToJava(c, actualAttrib, rs, 1);
        result.add(attribVal);
      }
      
      return result.isEmpty() ? null : result;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Collection readAttributeValues(Class c, DAttr attrib, final boolean orderByKey)  throws NotPossibleException {
    String sql = genSelect(c, null, attrib, false, orderByKey); 
    
    if (debug)
      System.out.println("DBToolKit.readAttributeValues: " + sql);

    try {
      ResultSet rs = executeQuery(sql);
      Object attribVal;
      Collection result = new ArrayList();
      
      /*v3.1: support domain-typed attribute*/
      DAttr actualAttrib;
      if (attrib.type().isDomainType()) {
        // retrieve the id-attribute of the referenced type and use it to extract value (below)
        DSMBasic dsm = getDom().getDsm();
        Class domainType = dsm.getDomainClassFor(c, attrib);
        List<DAttr> refIdAttribs = dom.getDsm().getIDDomainConstraints(domainType);
        actualAttrib = refIdAttribs.get(0);
      } else {
        actualAttrib = attrib;
      }
      
      // collect the values from rs
      while (rs.next()) {
        /*v3.1: use actual attrib
        attribVal = sqlToJava(c, attrib, rs, 1);
        */
        attribVal = sqlToJava(c, actualAttrib, rs, 1);
        result.add(attribVal);
      }
      
      return result.isEmpty() ? null : result;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  @Override
  public java.util.Map<DAttr, Collection> readAttributeValueTuples(Class c,
      DAttr[] attributes, final boolean orderByKey) throws NotPossibleException {
    String sql = genSelect(c, null, attributes, false, orderByKey); 
    
    if (debug)
      System.out.println("DBToolKit.readAttributeValueTuples: " + sql);

    java.util.Map<DAttr,Collection> valMap = new LinkedHashMap();
    
    try {
      ResultSet rs = executeQuery(sql);
      Object attribVal;
      //Collection result = new ArrayList();
      DAttr attrib;
      Collection attribVals;
      while (rs.next()) {
        for (int i = 0; i < attributes.length; i++) {
          attrib = attributes[i];
          attribVals = valMap.get(attrib);
          if (attribVals == null) {
            attribVals = new ArrayList();
            valMap.put(attrib, attribVals);
          }
          
          attribVal = sqlToJava(c, attrib, rs, i+1);
          attribVals.add(attribVal);
        }
      }
      
      return valMap.isEmpty() ? null : valMap;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * @effects 
   *  read from data source and return a Map<Oid,Object> of 
   *  the Oids and the values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt> 
   *  (in the same order as retrieved from the data source), 
   *  or return <tt>null</tt> if no such objects exist
   */
  @Override
  public java.util.Map<Oid,Object> readAttributeValuesWithOids(Class c, DAttr attrib) {
    String sql = genSelect(c, null, attrib, true); //genSelect(c, attrib, true);
    
    if (debug)
      System.out.println("DBToolKit.readAttributeValuesWithOids: " + sql);

    try {
      ResultSet rs = executeQuery(sql);
      Object attribVal, idVal;
      Oid oid;
      Collection<DAttr> idAttribs = dom.getDsm().getIDDomainConstraints(c);
      
      java.util.Map<Oid,Object> result = new LinkedHashMap<Oid,Object>(); // to preserve record order
      int colIndex = 1;
      while (rs.next()) {
        // generate Oid first
        oid = new Oid(c);
        for (DAttr idAttrib : idAttribs) {
          idVal = sqlToJava(c, idAttrib, rs, colIndex);
          //TODO: check Comparable of idVal
          oid.addIdValue(idAttrib, (Comparable) idVal);
          
          colIndex++;
        }
        
        // then get the value of attrib
        attribVal = sqlToJava(c, attrib, rs, colIndex);
        
        // reset 
        colIndex = 1;
        
        result.put(oid, attribVal);
      }
      
      return result.isEmpty() ? null : result;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  @Override
  public java.util.Map<Object, List> readAttributeValueTuplesWithOids(Class c,
      DAttr[] attributes) {
    String sql = genSelect(c, null, attributes, true);
    
    if (debug)
      System.out.println("DBToolKit.readAttributeValueTuplesWithOids: " + sql);

    Collection<DAttr> idAttribs = dom.getDsm().getIDDomainConstraints(c);
    
    try {
      ResultSet rs = executeQuery(sql);

      java.util.Map<Object,List> valMap = new LinkedHashMap();
      Object attribVal, idVal;
      DAttr attrib;
      List attribVals;
      Oid oid = null;
      
      // the map entry for Oids appears first under a String-typed key
      // the map entries for other attribute values appear after under the DomainConstraint-typed keys 
      List idVals = new ArrayList();
      valMap.put("Oid", idVals);
      
      int colIndex;
      while (rs.next()) {
        colIndex = 1;
        
        // generate Oid first
        oid = new Oid(c);
        idVals.add(oid);
        for (DAttr idAttrib : idAttribs) {
          idVal = sqlToJava(c, idAttrib, rs, colIndex);
          //TODO: check Comparable of idVal
          oid.addIdValue(idAttrib, (Comparable) idVal);
          
          colIndex++;
        }
        
        // then the values of other attributes
        for (int i = 0; i < attributes.length; i++) {
          attrib = attributes[i];
          attribVals = valMap.get(attrib);
          if (attribVals == null) {
            attribVals = new ArrayList();
            valMap.put(attrib, attribVals);
          }
          
          attribVal = sqlToJava(c, attrib, rs, colIndex+i);
          attribVals.add(attribVal);
        }
      }
      
      return valMap.isEmpty() ? null : valMap;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * 
   * @effects 
   *  read from the data source the value of the attribute <tt>attrib</tt> 
   *  of the domain object identified by <tt>oid</tt> of the domain class <tt>c</tt>
   *  
   *  <p>Return <tt>null</tt> if the object with the specified id is not found OR the actual 
   *  attribute value is <tt>null</tt>
   *   
   * @example
   *  <pre>
   *  c = Student
   *  oid = Student(S2014)
   *  attrib = Student:sclass (value = SClass(id=2)) 
   *  
   *  -> result := SClass(id=2) 
   *  </pre>
   */
  @Override
  public Object readAttributeValue(Class c, Oid oid, DAttr attrib) {
    String sql = genSelect(c, oid, attrib, false);
    
    if (debug)
      System.out.println("DBToolKit.readAttributeValue: " + sql);

    try {
      ResultSet rs = executeQuery(sql);
      Object attribVal;
      Type type;
      if (rs.next()) {
        // get the attribute type: which is either one of the primitive types
        // or a domain type. The latter requires getting the type of the 
        // id attribute of the domain type.
        type = attrib.type();
        if (type.isDomainType()) {
          // get the type of the domain type's id attribute
          Class domainType = dom.getDsm().getDomainClassFor(c, attrib.name());
          DAttr refIdAttrib = dom.getDsm().getIDDomainConstraints(domainType).get(0);
          
          attribVal = sqlToJava(domainType, refIdAttrib, rs, 1);
        } else {
          attribVal = sqlToJava(c, attrib, rs, 1);          
        }
        
        return attribVal;
      } else {
        // no value
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  
  @Override
  public java.util.Map<DAttr, Object> readAttributeValues(Class c, Oid oid,
      Collection<DAttr> attribs) {
    String sql = genSelect(c, oid, attribs.toArray(new DAttr[attribs.size()]), false);
    
    if (debug)
      System.out.println("RelationalOSMBasic.readAttributeValues: " + sql);

    java.util.Map<DAttr,Object> result = null;
    
    try {
      ResultSet rs = executeQuery(sql);
      Object attribVal;
      Type type;
      if (rs.next()) {
        if (result == null) result = new HashMap<>();

        for (DAttr attrib : attribs) {
          // get the attribute type: which is either one of the primitive types
          // or a domain type. The latter requires getting the type of the 
          // id attribute of the domain type.
          type = attrib.type();
          if (type.isDomainType()) {
            // get the type of the domain type's id attribute
            Class domainType = dom.getDsm().getDomainClassFor(c, attrib.name());
            DAttr refIdAttrib = dom.getDsm().getIDDomainConstraints(domainType).get(0);
            
            attribVal = sqlToJava(domainType, refIdAttrib, rs, 1);
          } else {
            attribVal = sqlToJava(c, attrib, rs, 1);          
          }
          
          result.put(attrib, attribVal);          
        }
      } else {
        // no value
        result = null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      result = null;
    }
    
    return result;
  }

  /**
   * @effects
   *  count and return the number of records of the table mapped to domain class <tt>c</tt> 
   *  or return -1 if no records are ound 
   *  <p>Throws DBException if fails to do so
   */
  @Override
  public int readObjectCount(Class c) throws DataSourceException {
    // sql: select count(*) from t, where t = table(c)
    String cTable = dom.getDsm().getDomainClassName(c);
    String sql = "Select count(*) from " + cTable;
    
    if (debug)
      System.out.println("DBToolKit.readObjectCount: " + sql);

    int count;
    try {
      ResultSet rs = executeQuery(sql);
      Collection result = new ArrayList();
      if (rs.next()) {
        count = rs.getInt(1);
      } else {
        // no records
        count = -1;
      }
      
      return count;
    } catch (Exception e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, 
          e, new Object[] {c.getSimpleName()});
    }
  }
  
  /**
   * @requires 
   *  cls != null /\ assoc != null /\  
   *  attrib is a valid attribute of cls /\
   *  linkedObj is a valid domain object  /\ linkedObjOid is the Oid of linkedObj
   *  
   * @effects
   *  load and return from the data source the number of objects  
   *  of the domain class <tt>cls</tt> that are linked to a given domain object 
   *  <tt>linkedObj</tt> via the attribute <tt>attrib</tt> (of <tt>cls</tt>).
   *  
   *  <p>Throws DBException if fails to retrieve the information from the data source
   * 
   *  @example
   *  <pre>
   *  c = Enrolment
   *  attrib = Enrolment.student
   *  linkedObj = Student<id=S2014>
   *  
   *  sql = Select count(*) from Enrolment Where student_id='S2014'
   *  </pre>   
   */
  @Override
  public int readAssociationLinkCount(Class c, DAttr attrib, 
      Object linkedObj, Oid linkedObjOid) throws DataSourceException {
    /*v3.2: support the case that attrib is declared in a c's super- or ancestor class 
     * - to create additional join(s)  
    String table = dom.getDsm().getDomainClassName(c);
    
    // the fk col of table that refers to linkedObj
    String fkName = getColName(c, attrib);
    
    // the fk col value
    Object val = linkedObjOid.getIdValue(0);
    DomainConstraint refPkAttrib = linkedObjOid.getIdAttribute(0);
    
    String sqlVal = toSQLString(refPkAttrib.type(), val, true);
    
    String sql = "Select count(*) from "+table+" where "+fkName+ 
        ((sqlVal != null) ? " = " + sqlVal : " is Null"); // v2.7.2
     */

    String[] Select = {"count(*)"};       // add count(*) to select

    // v3.3: java.util.Map<Class,TableSpec> From = new HashMap();
    DualMap<Class,TableSpec> From = new DualMap<>();
    Collection<JoinSpec> Joins = new ArrayList();
    Collection<String> Where = new ArrayList();
    final String cTable = dom.getDsm().getDomainClassName(c);
    TableSpec cTableSpec = new TableSpec(c, null, cTable);
    
    // update From, Where for attrib (taking into account inheritance)
    int tableIndex = 0; // the index used to create table vars
    int[] tableInd = {tableIndex};// use array to pass table index
    TableSpec aTableSpec = updateQueryWithAttribute(From, Where, cTableSpec, tableInd, Joins, c, attrib);
    Class aCls = aTableSpec.getCls();
    String aVar = aTableSpec.getVar();
    String aTableName = aTableSpec.getName();

    // add count condition to Where
    // the fk col of table that refers to linkedObj
    String fkName = getColName(aCls, attrib);
    // the fk col value
    Object val = linkedObjOid.getIdValue(0);
    DAttr refPkAttrib = linkedObjOid.getIdAttribute(0);
    String sqlVal = toSQLString(refPkAttrib.type(), val, true);
    Where.add(fkName+ ((sqlVal != null) ? " = " + sqlVal : " is Null"));

    // generate the query
    int ind = 0;
    int size = From.size();
    String[] FromArr = new String[size];
    for(TableSpec s : From.values()) {
      FromArr[ind] = s.toString();
      ind++;
    }
    
    String sql = genSelect(Select, 
        FromArr, 
        Where.toArray(new String[Where.size()]), null, null, null);
    
    if (debug)
      System.out.println("DBToolKit.readAssociationLinkCount: " + sql);
    
    ResultSet rs = executeQuery(sql);
    try {
      rs.next();
      int result = rs.getInt(1);
      return result;
    } catch (SQLException e) {
      // something wrong
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
          new Object[] {c.getSimpleName()});
    }
  }
  
  /**
   * @requires 
   *  a table corresponding to class c has been created in the data source /\ 
   *  attrib is a valid attribute of c 
   * 
   * @effects
   *  reads from the data source and returns a Tuple2 containing lowest and highest values of 
   *  the domain attributes <tt>atrib</tt> among the objects of c
   *   
   *  <p>throws DBException if fails to read from data source;
   *  NotFoundException if no value range is found
   */
  @Override
  public Tuple2<Object,Object> readValueRange(Class c, DAttr attrib) throws DataSourceException, NotFoundException {
    ResultSet rs = readValueRangeFromSource(c, attrib, null);
    
    try {
      if (rs.next()) {  // single-row
        Object minVal = sqlToJava(c, attrib, rs, 1);
        Object maxVal = sqlToJava(c, attrib, rs, 2);
        
        if (minVal == null && maxVal == null) {
          throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
              "Không tìm thấy giá trị mã dữ liệu nào của {0}.{1}", c.getSimpleName(), attrib.name());          
        }
        
        return new Tuple2<Object,Object>(minVal, maxVal);
      } else {
        // empty result
        throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
            "Không tìm thấy giá trị mã dữ liệu nào của {0}.{1}", c.getSimpleName(), attrib.name());
      }
    } catch (SQLException e) {
      // something wrong
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
          new Object[] {c.getSimpleName()});
    }
  }
      
  @Override
  public Tuple2<Tuple2<Class, Object>, Tuple2<Class, Object>> readIdValueRange(
      Class cls, DAttr idAttrib) throws DataSourceException, NotFoundException {
    ResultSet rs = readValueRangeFromSource(cls, idAttrib, null);
    
    try {
      if (rs.next()) {  // single-row
        Object minVal = sqlToJava(cls, idAttrib, rs, 1);
        Object maxVal = sqlToJava(cls, idAttrib, rs, 2);
        
        if (minVal == null && maxVal == null) {
          throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
              "Không tìm thấy giá trị mã dữ liệu nào của {0}.{1}", cls.getSimpleName(), idAttrib.name());          
        }
        
        // if cls has sub-types then find the sub-type that owns each value
        // if no sub-types own a value then cls is the one that owns the value
        DSMBasic dsm = getDom().getDsm();
        Class[] subTypes = dsm.getSubClasses(cls);
        Class minCls = null, maxCls = null; 
        if (subTypes != null) {
          // has sub-types
          for (Class sub: subTypes) {
            // ASSUME: subtypes inherit the id-attribute from super-type
            if (existRecord(sub, idAttrib, minVal)) {
              // sub owns minVal
              minCls = sub;
            }
            
            if (existRecord(sub, idAttrib, maxVal)) {
              // sub owns maxVal
              maxCls = sub;
            }
            
            if (minCls != null && maxCls != null) {
              // done
              break;
            }
          }
        }
        
        if (minCls == null) minCls = cls;
        if (maxCls == null) maxCls = cls;
        
        Tuple2<Tuple2<Class,Object>, Tuple2<Class,Object>> idValRange = 
            new Tuple2(new Tuple2(minCls, minVal), new Tuple2(maxCls, maxVal));
        
        return idValRange;
      } else {
        // empty result
        throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
            "Không tìm thấy giá trị mã dữ liệu nào của {0}.{1}", cls.getSimpleName(), idAttrib.name());
      }
    } catch (SQLException e) {
      // something wrong
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
          new Object[] {cls.getSimpleName()});
    }    
  }
  
  /**
   * @requires 
   *  a table corresponding to class c has been created in the data source /\ 
   *  attrib is a valid attribute of c /\
   *  elements of derivedAttributes are valid attributes of c
   * 
   * @effects
   *  reads from the data source and returns a <tt>Map</tt> containing lowest and highest values of 
   *  the domain attribute <tt>attrib</tt> among the objects of c, group by the attributes 
   *  specified by <tt>derivedAttributes</tt>
   *   
   *  <p>throws DBException if fails to read from data source;
   *  NotFoundException if no value range is found;
   *  IllegalArgumentException if no derived attributes were specified
   */
  @Override
  public java.util.Map<Tuple, Tuple2<Object,Object>> readValueRange(Class c, DAttr attrib, 
      DAttr[] derivedAttributes) 
      throws DataSourceException, NotFoundException {
    
    if (derivedAttributes == null)
      throw new IllegalArgumentException("DBToolKit.readValueRange: no derived attributes specified");
    
    ResultSet rs = readValueRangeFromSource(c, attrib, derivedAttributes);
    
    java.util.Map result = new LinkedHashMap<Tuple, Tuple2<Object,Object>>();
    
    try {
      Serializable[] derivedVals = new Serializable[derivedAttributes.length];
      Object minVal, maxVal;
      Tuple t; 
      Tuple2 mx;
      int i;
      while (rs.next()) {  // process all rows
        // first extract derived attribute values
        for (i = 0; i < derivedAttributes.length; i++) {
          // TODO: check Serializable
          derivedVals[i] = (Serializable) sqlToJava(c, derivedAttributes[i], rs, i+1);
        }
        
        i = i+1;
        
        // then extract min, max values
        minVal = sqlToJava(c, attrib, rs, i);
        maxVal = sqlToJava(c, attrib, rs, i+1);
        
        // prepare min-max tuple
        if (minVal == null && maxVal == null) {
          // no value range for this
          mx = null;
        } else {
          mx = new Tuple2<Object,Object>(minVal, maxVal);
        }
        
        // add to result
        result.put(Tuple.newInstance(derivedVals), mx);
      } 
    } catch (SQLException e) {
      // something wrong
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
          new Object[] {c.getSimpleName()});
    }
    
    if (result.isEmpty()) {
      // empty result
      throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
          "Không tìm thấy giá trị mã dữ liệu nào của {0}.{1}", c.getSimpleName(), attrib.name());
    }
    
    return result;
  }
  
  /**
   * @effects read the (min,max) value range of attribute <tt>c.attrib</tt> from
   *          all the data records of <tt>c</tt>, group by
   *          <tt>derivingAttributes</tt> (if any)
   * 
   *          <p>
   *          Throws DBException if fails to load data from data source.
   * 
   *          <p>
   *          Note: <tt>c.attrib</tt> may actually be defined in a super- or
   *          ancestor class of c and is thus inherited in <tt>c</tt>.
   *          Similarly, each of the deriving attribute in
   *          <tt>derivingAttributes</tt> may also be defined in some super- or
   *          ancestor classes of c.
   * 
   * @pseudocode The basic idea is to extract only the range of values of the
   *             data records of <tt>c</tt>, which may be a sub-set of those of
   *             the super- and ancestor classes of <tt>c</tt>
   * 
   *  <pre>
   *  Let SELECT, FROM, WHERE, GROUP BY be Sets of Strings 
   *  
   *  Add c to FROM
   *  let tA be a String
   * 
   *  if attrib is inherited from an ancestor domain class a,  
   *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
   *    let s = SQL_Join(c,x,...,a) (x may = a)
   *    add s to WHERE
   *    add x,...,a to FROM
   *    tA = a
   *  else
   *    tA = c
   *  
   *  if derivingAttributes != null
   *    for each attribute d in derivingAttributes
   *      let tD be a String
   *      if d is inherited from an ancestor domain class b (b != c)
   *        tD = b
   *        if b != a AND b has not been processed
   *          let t = SQL_Join(c,y,...,b) (y may = b)
   *          merge t into WHERE
   *          merge y,...,b into FROM
   *      else
   *        tD = c
   *      add tD.d to SELECT
   *      add tD.d to GROUP BY
   * 
   *  Add min(tA.attrib) and max(tA.attrib) to SELECT
   *   
   *  Let sql = SQL(SELECT, FROM, WHERE, GROUP BY)
   *  execute sql 
   *  return result as RecordSet
   * </pre>
   * 
   * @example <p>
   *          <b>EXAMPLE 1</b>:
   * 
   *          <pre>
   *  c=Student, 
   *  attrib=Student.id, derivingAttributes=null
   *   
   *  sql = SELECT min(t0.id), max(t0.id)  
   *        FROM Student t0
   * </pre>
   * 
   *          <p>
   *          <b>EXAMPLE 2</b>:
   * 
   *          <pre>
   *  c=ElectiveModule, 
   *  attrib=Module.code (Module=super(ElectiveModule)), 
   *  derivingAttributes={Module.semester}
   * 
   *  sql = SELECT min(t1.code), max(t1.code), t1.semester 
   *        FROM ElectiveModule t1, Module t2
   *        WHERE t1.id=t2.id
   *        GROUP BY semester
   * </pre>
   * 
   *          <p>
   *          <b>EXAMPLE 3</b>:
   * 
   *          <pre>
   *  c=Instructor, 
   *  attrib=Instructor.id, 
   *  derivingAttributes={Person.name,Person.dob} (Person=super(Staff), Staff=super(Instructor))
   * 
   *  sql = SELECT min(t1.id), max(t1.id), t3.name, t3.dob 
   *        FROM Instructor t1, Staff t2, Person t3
   *        WHERE t1.id=t2.id AND t2.id=t3.id
   *        GROUP BY t3.name, t3.dob
   * </pre>
   * 
   *          <p>
   *          <b>EXAMPLE 4</b>:
   * 
   *          <pre>
   *  c=Instructor, 
   *  attrib=Person.id, 
   *  derivingAttributes={Person.name,Person.dob} (Person=super(Staff), Staff=super(Instructor))
   * 
   *  sql = SELECT min(t3.id), max(t3.id), t3.name, t3.dob 
   *        FROM Instructor t1, Staff t2, Person t3
   *        WHERE t1.id=t2.id AND t2.id=t3.id
   *        GROUP BY t3.name, t3.dob
   * </pre>
   * 
   *          <p>
   *          <b>EXAMPLE 5</b>:
   * 
   *          <pre>
   *  c=Instructor, 
   *  attrib=Staff.code, 
   *  derivingAttributes={Person.name,Person.dob} (Person=super(Staff), Staff=super(Instructor))
   * 
   *  sql = SELECT min(t2.code), max(t2.code), t3.name, t3.dob 
   *        FROM Instructor t1, Staff t2, Person t3
   *        WHERE t1.id=t2.id AND t2.id=t3.id
   *        GROUP BY t3.name, t3.dob
   * </pre>
   */
  private ResultSet readValueRangeFromSource(Class c, DAttr attrib, 
      DAttr[] derivingAttributes) throws DataSourceException {
    Stack<String> Select = new Stack();
    Stack<String> From = new Stack();
    Stack<String> Where = new Stack();
    Stack<String> GroupBy = new Stack();
    
    Collection<String> tables = new ArrayList();
    Collection<String> joinTablePairs = new ArrayList();
    
    /*  
     *  Add c to FROM
     */
    int tIndex = 0;
    final String cTable = dom.getDsm().getDomainClassName(c);
    final String tcTable = "t"+(tIndex++);
    String tA;
    From.add(cTable + " " + tcTable);
    tables.add(cTable);
    
    final Class a = dom.getDsm().getDeclaringClass(c, attrib);
    
    Collection<DAttr> idFields;
    String supTable, tsupTable;
    Class sup = dom.getDsm().getSuperClass(c);
    Class currClass;
    String currTable = cTable;
    String tcurrTable = tcTable;
    String colName;
    
    if (a != c) {
     /* attrib is inherited from an ancestor domain class a,  
      *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
      *    let s = SQL_Join(c,x,...,a) (x may = a)
      *    add s to WHERE
      *    add x,...,a to FROM
      */
      currClass = c;
      do {
        supTable = dom.getDsm().getDomainClassName(sup);
        tsupTable = "t" + (tIndex++);
        
        // use the id attributes to add new join expressions
        idFields = dom.getDsm().getIDDomainConstraints(sup);
        for (DAttr f : idFields) { // current table
          // add join expressions between the id attributes of the two tables
          Where.add(join(currClass, tcurrTable, sup, tsupTable, f)
//              new Expression(tcurrTable + "." + f.name(),
//              Expression.Op.EQ, tsupTable + "." + f.name(),
//              Expression.Type.Metadata)
              );
          
          joinTablePairs.add(currTable+"-"+supTable);
        } // end for

        // add super class table to FROM
        From.add(supTable + " " + tsupTable);
        tables.add(supTable);
        
        // recursive: check the super-super class and so on...
        currTable = supTable;
        tcurrTable = tsupTable;
        currClass = sup;
        sup = dom.getDsm().getSuperClass(sup);
      } while (sup != null);
      
      // attribute table is the last super class name
      tA = tcurrTable;
    } else {
      // attrib is in c
      tA = tcTable;
    } // end if 
    
    // process deriving attributes...
    if (derivingAttributes != null) {
      Class b;
      java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();
      String tD;
      for (DAttr d : derivingAttributes) {
        /*      add d to SELECT
         *      add d to GROUP BY
         */
        b = dom.getDsm().getDeclaringClass(c, d);
        if (b != c) {
          /*d is inherited from an ancestor domain class b (b != c)
          *        if b != a AND b has not been processed
          *          let t = SQL_Join(c,y,...,b) (y may = b)
          *          merge t into WHERE
          *          merge y,...,b into FROM
          */
          if (b != a && !processed.containsKey(b)) {
            sup = dom.getDsm().getSuperClass(c);
            //tcurrTable = cTable;
            currTable = cTable;
            tcurrTable = tcTable;
            currClass = c;
            do {
              supTable = dom.getDsm().getDomainClassName(sup);
              tsupTable = "t" + (tIndex++);

              // merge Join into WHERE
              if (!joinTablePairs.contains(currTable+"-"+supTable)) {
                // use the id attributes to add new join expressions
                idFields = dom.getDsm().getIDDomainConstraints(sup);
                for (DAttr f : idFields) { // current table
                  // add join expressions between the id attributes of the two tables
                  Where.add(join(currClass, tcurrTable, sup, tsupTable, f)
//                      new Expression(tcurrTable + "." + f.name(),
//                      Expression.Op.EQ, tsupTable + "." + f.name(),
//                      Expression.Type.Metadata)
                      );
                  
                  joinTablePairs.add(currTable+"-"+supTable);
                } // end for
              }
              
              // merge table into FROM
              if (!tables.contains(supTable)) {
                From.add(supTable + " " + tsupTable);
                tables.add(supTable);
              }
              
              // recursive: go up to the super-super class and so on...
              currTable = supTable;
              tcurrTable = tsupTable;
              currClass = sup;
              sup = dom.getDsm().getSuperClass(sup);
            } while (sup != null);
            
            tD = tcurrTable;
            processed.put(b, tcurrTable);
          } else if (b == a) {
            tD = tA;
          } else {  // b already processed
            tD = processed.get(b);
          }  // end if
        } else {
          tD = tcTable;
        } // end if
        
        //Select.add(tD + "."+d.name());
        colName = getColName(b,d);
        colName = tD + "."+ colName;
        Select.add(colName);
        /*v3.1: FIXED use colName in GroupBy
        GroupBy.add(tD + "."+d.name());
        */
        GroupBy.add(colName);
      } // end for
    } // end if
    
    /*  Add min(attrib) and max(attrib) to SELECT
     */
//    Select.add("min("+tA+"."+attrib.name()+")");
//    Select.add("max("+tA+"."+attrib.name()+")");
    colName = getColName(a,attrib);
    Select.add("min("+tA+"."+colName+")");
    Select.add("max("+tA+"."+colName+")");
    
    String sql = genSelect(
        Select.toArray(new String[Select.size()]),  // selectCols
        From.toArray(new String[From.size()]),      // tables
        (!Where.isEmpty()) ? Where.toArray(new String[Where.size()]) : null, // AND
        null, // OR
        (!GroupBy.isEmpty()) ? GroupBy.toArray(new String[GroupBy.size()]) : null, // group by
        null  // order by
        );
    
    if (debug)
      System.out.println("DBToolKit.readValueRangeFromSource: " + sql);

    try {
      ResultSet rs = executeQuery(sql);
      return rs;
    } catch (Exception e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e, 
          new Object[] {c.getSimpleName()});
    }
    
//    ResultSet rs =
//        selectAndProject(
//            Select.toArray(new String[Select.size()]),
//            From.toArray(new String[From.size()]),
//            (!Where.isEmpty()) ? Where.toArray(new String[Where.size()]) : null, // AND
//            null, // OR
//            (!GroupBy.isEmpty()) ? GroupBy.toArray(new String[GroupBy.size()]) : null, // group by
//            null  // order by
//            );
    
//    if (rs == null)
//      throw new DBException(DBException.Code.FAIL_RESULT_SET,
//          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
    
//    return rs;   
  }

    
//  /**
//   * @effects 
//   *  read the max value of the attribute of the domain class <tt>c</tt> 
//   *  whose domain constraint is <tt>dc</tt> and whose group by attributes
//   *  are <tt>groupBy</tt> 
//   *  from the database table of <tt>c</tt>
//   *  and return this value.
//   *  
//   *  <p>Throws DBException if fails to read the database.
//   *  
//   * @requires 
//   *  <tt>attribute.type</tt> is sortable.
//   * @deprecated use {@link #readValueRange(Class, DomainConstraint)} instead
//   */
//  public Object readMaxValue(Class c, DomainConstraint dc, String[] groupBy) throws DBException {
//    // the class name used as the table name
//    final String cname = schema.getDsm().getDomainClassName(c);
//
//    String attribute = dc.name();
//    String maxCol = "max("+attribute+")"; 
//        
//    String[] selectCols;
//    if (groupBy != null) {
//      // use group by attributes and maxCol
//      selectCols = new String[groupBy.length+1];
//      System.arraycopy(groupBy, 0, selectCols, 0, groupBy.length);
//      selectCols[selectCols.length-1] = maxCol;
//    } else {
//      // use maxCol only
//      selectCols = new String[] { maxCol };
//    }
//    
//    ResultSet rs =
//        selectAndProject(
//            selectCols,
//            new String[] {cname},
//            null, null,
//            groupBy,
//            null  // order by
//            );
//    if (rs == null)
//      return null;
//    
//    try {
//      // there could be several rows, depending on whether groupBy was specified
//      if (groupBy != null) {
//        // (possibly) multiple rows
//        List<List> maxVals = new LinkedList<List>();
//        List valRow;
//        DomainConstraint dc1;
//        while (rs.next()) {
//          valRow = new LinkedList();
//          int numSelect = selectCols.length;
//          for (int ind = 0; ind < numSelect; ind++) {
//            dc1 = schema.getDsm().getDomainConstraint(c, 
//                (ind < numSelect-1) ? selectCols[ind] : attribute);
//            valRow.add(sqlToJava(dc1, rs, ind+1));
//          }
//          maxVals.add(valRow);
//        }
//        
//        return (maxVals.isEmpty()) ? null : maxVals;
//      } else {
//        // single row
//        if (rs.next()) {
//          Object maxVal = sqlToJava(dc, rs, 1);
//          return maxVal;
//        } else {
//          // empty result
//          return null;
//        }
//      }
//    } catch (SQLException e) {
//      // something wrong
//      throw new DBException(DBException.Code.FAIL_RESULT_SET, e,
//          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    }
//  }
  
  // private void populateData(Class cls) throws DBException {
  // if (debug)
  // System.out.println("Populating data for " + cls.getSimpleName() + "...");
  //
  // final int numObjs = 4;
  //
  // Object o = null;
  // String sql = null;
  // try {
  // for (int i = 0; i < numObjs; i++) {
  // o = DataManager.getAutoObject(cls);
  // sql = genInsert(o);
  // executeUpdate(sql);
  // }
  // } catch (NotPossibleException e) {
  // throw new DBException("Failed to populate data", e);
  // }
  // }

  /**
   * @effects returns <code>true</code> if the database schema with name
   *          <code>dbSchema</code> exists in the database, else returns
   *          <code>false</code>
   * @version 
   *  - 3.0: improved to use a source-specific SQL query
   */
  @Override
  public boolean existsSchema(String dbSchema) {
    /*v3.0:
    String sql =
        "select s.schemaname from sys.sysschemas s "
        + "where s.schemaname='%s'";

    sql = String.format(sql, dbSchema.toUpperCase());
    */
    String sql = getQuerySchemaExist(dbSchema);


    if (debug)
      System.out.println(sql);

    ResultSet rs = null;
    try {
      rs = executeQuery(sql);

      if (rs != null && rs.next()) {
        return true;
      }
    } catch (Exception e) {
      // something wrong, but dont care
    } finally {
      try {
        if (rs != null)
          rs.close();
      } catch (Exception e) {
      }
    }
    return false;
  }

  /**
   * @effects creates the database schema named <code>name</code>
   */
  @Override
  public void createSchema(String name) throws DataSourceException {
    // create
    String sql = "create schema %s";
    sql = String.format(sql, name);

    if (conn != null) {
      executeUpdate(sql);
    }
  }

  /**
   * @effects 
   *  Create the table for each domain class in <tt>domainClasses</tt>
   * 
   *  <p>Throws NotPossibleException if failed to create a table; 
   *  NotFoundException if a <tt>domainClass</tt> is not a registered domain class or 
   *  required id domain attributes of the class(es) referenced by this class are not found.  
   */
  private void createTable(Class[] domainClasses) throws DataSourceException, NotPossibleException, NotFoundException {
    if (debug)
      System.out.println("Creating tables...");

    if (conn != null) {
      String sql = null;
      for (int i = 0; i < domainClasses.length; i++) {
        createClassStore(domainClasses[i]);
      }
    }
  }

  /**
   * @effects returns a <code>String[]</code> array, the elements of which are
   *          the names of the table in the database schema used by the database schema named <tt>schemaName</t>, or <code>null</code> if no tables are found
   *          (i.e. the database is empty)
   */
  private String[] getTableNames(String schemaName) {
    List<String> names = new ArrayList();

    // query all the application schemas for tables to remove
    
    String sql = "select tablename from sys.systables t, sys.sysschemas s "
        + "where t.schemaid=s.schemaid and s.schemaname='" + schemaName + "'";

    ResultSet rs = null;
    try {
      rs = executeQuery(sql);

      if (rs != null) {
        String tname;
        while (rs.next()) {
          tname = rs.getString(1);
          names.add(tname);
        }
      }
    } catch (Exception e) {
      // something wrong, but dont care
    } finally {
      try {
        if (rs != null)
          rs.close();
      } catch (Exception e) {
      }
    }

    return (!names.isEmpty()) ? names.toArray(new String[names.size()]) : null;
  }

  /**
   * @effects 
   *  if exists data source constraints (e.g. FKs) of <tt>c</tt>
   *    return a List of their names (in the definition order)
   *  else
   *    return null
   * @version 
   *  2.6.4.b: read FK constraints only.
   */
  @Override
  public List<String> readDataSourceConstraint(Class c) {
    if (conn != null) {
      try {
        DatabaseMetaData dbMeta = conn.getMetaData();
        // Note: names must be in upper-case
        String tableName = c.getSimpleName().toUpperCase();
        String schemaName = dom.getDsm().getDomainSchema(c);

        //v2.7.3: no need
        // if (schemaName != null) schemaName = schemaName.toUpperCase();
        
        String catalog = conn.getCatalog();
        
        // TODO: find more constraints if needed
        // here we are interested in just the FK constraints
        ResultSet res = dbMeta.getImportedKeys(catalog, schemaName, tableName);
        
        List<String> fkNames  = new ArrayList();
        String fkName;
        while (res.next()) {
          fkName = (String) res.getString("FK_NAME");
          fkNames.add(fkName);
        }
        
        if (fkNames.isEmpty()) 
          return null;
        else
          return fkNames;
      } catch (SQLException e) {
        if (debug)
          e.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
  }
  
  /**
   * Drop database tables
   * 
   * @param domainClasses
   *          a <code>Class[]</code> array containing the classes that were used
   *          to create the tables.
   */
  public void dropTable(Class[] domainClasses) throws DataSourceException {
    if (debug)
      System.out.println("Droping tables...");

    if (conn != null) {
      String sql = null;
      for (Class c : domainClasses) {
        dropClassStore(c);
      }
    }
  }

  /**
   * Drop a given database table that corresponds to a class.
   * 
   * @param domainClass
   *          a <code>Class</code> object representing the table to be dropped
   */
  @Override
  public void dropClassStore(Class domainClass) throws DataSourceException {
    dropTable(dom.getDsm().getDomainClassName(domainClass));
  }

  /**
   * @effects drops all tables in the database schema used by this; throws a
   *          <code>DBException</code> if an error occured
   * @version 
   * - 5.4: updated to add schema prefix to each table name
   */
  @Override
  public void deleteDomainSchema(String schemaName) throws DataSourceException {
    String[] tables = getTableNames(schemaName);

    // the tables may have dependencies, thus we need to loop until all tables
    // have been deleted...
    if (tables != null) {
      List<String> tableNames = new ArrayList();
      // 5.4: Collections.addAll(tableNames, tables);
      for (String table : tables) {
        tableNames.add(schemaName + "." + table);
      }
      String table;
      if (isDebug())
        System.out.println("To drop " + tableNames.size() + " tables");

      while (tableNames.size() > 0) {
        table = tableNames.remove(0);
        try {
          if (isDebug())
            System.out.println("dropping table " + table);

          dropTable(table);
          if (isDebug())
            System.out.println("...ok");
        } catch (DataSourceException e) {
          if (isDebug()) {
            System.out.println("...failed (to retry)");
            e.printStackTrace();
          }
          // perhaps caused by dependency, move table to the end of the list
          // to try again later
          tableNames.add(table);
        }
      }
    } else {
      if (isDebug())
        System.out.println("No tables found in schema " + schemaName);
    }
  }

  private void dropTable(String tableName) throws DataSourceException {
    if (conn != null) {
      /*v3.2: use sub-type method 
      String sql = null;
      sql = "drop table " + tableName;
      */
      String sql = getQueryDropTable(tableName);
      
      if (isDebug())
        System.out.println(sql);

      executeUpdate(sql);
    }
  }

  /**
   * Delete the records of a given database table that corresponds to a class.
   * 
   * @param domainClass
   *          a <code>Class</code> object representing the table to be cleared
   */
  @Override
  public void deleteObjects(Class domainClass) throws DataSourceException {
    if (conn != null) {
      String sql = null;
      sql = "delete from " + dom.getDsm().getDomainClassName(domainClass);
      if (debug)
        System.out.println(sql);
      executeUpdate(sql);
    }
  }

  /**
   * @requires 
   *  the table of both <tt>c</tt> and <tt>subType</tt> are created in the data source  
   *  <br> /\ <tt>subType</tt>'s table does not contain an FK constraint to <tt>c</tt>'s table for its PK that 
   *  has "CASCADE ON DELETE" condition 
   * @effects 
   *  insert a new record in <tt>subType</tt>'s table that references <tt>o</tt>, 
   *  without actually creating a new record in <tt>c</tt>'s table.
   *  <br>Throws DataSourceException if failed to update data source.
   *  
   */
  /* (non-Javadoc)
   * @see domainapp.basics.core.dodm.osm.OSM#transformObjectToASubtype(java.lang.Class, java.lang.Object, java.lang.Class)
   */
  @Override
  public void transformObjectToASubtype(Class c, Object o, Class subType) throws DataSourceException {
    String sql = null;

    List<Field> updateAttributes = new ArrayList();
    
    try {
      sql = genParameterisedInsert(subType, o, updateAttributes);
      if (debug)
        System.out.println(sql);
    } catch (NotFoundException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_TRANSFORM_OBJECT, e,
          new Object[] {c.getSimpleName(), o, subType.getSimpleName()});
    }

    executeParameterisedUpdate(sql, c, updateAttributes, o);
  }

  /**
   * @requires 
   *  the table of both <tt>c</tt> and <tt>subType</tt> are created in the data source  
   *  <br> /\ <tt>c</tt>'s table does not contain an FK constraint to <tt>supType</tt>'s table for its PK that 
   *  has "CASCADE ON DELETE" condition
   *  
   * @effects 
   *   remove the corresponding record in <tt>c</tt>'s table but without touching the <tt>supType</tt> record
   *  <br>Throws DataSourceException if failed to update data source.
   */
  @Override
  public void transformObjectToSupertype(Class c, Object o, Class supType) throws DataSourceException {
    String sql = null;

    try {
      sql = genDelete(c, o);
      if (debug)
        System.out.println(sql);
    } catch (NotFoundException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_TRANSFORM_OBJECT, e,
          new Object[]{ c.getSimpleName(), o, supType.getSimpleName()});
    }

    executeUpdate(sql);
  }

  /**
   * @effects 
   *  remove the data source constraint of the table associated to the domain class 
   *  <tt>c</tt>, whose name is <tt>name</tt>
   *  
   *  <p>Throws DBException if failed to remove the constraint.
   */
  @Override
  public void dropDataSourceConstraint(Class c, String name) throws DataSourceException {
    if (conn != null) {
      String tableName = dom.getDsm().getDomainClassName(c);
      String sql = "alter table %s drop constraint %s";
      sql = String.format(sql, tableName, name);
      if (debug)
        System.out.println("DBToolKit.dropDataSourceConstraint: "+sql);
      
      executeUpdate(sql);
    }    
  }
  
  // public static void dropTable(String dropSQL) {
  // if (conn != null) {
  // try {
  // Statement stmt = conn.createStatement();
  // try {
  // stmt.executeUpdate(dropSQL);
  // } catch (SQLException sqle) {
  // // ignore
  // }
  // commit(stmt);
  // stmt.close();
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  // }

  /**
   * Print out the contents of the tables that store the objects of the domain
   * classes specified in the argument <code>domainClasses</code>.
   * 
   * @param domainClasses
   *          the domain classes whose data objects are to be printed out
   */
  public void print(Class[] domainClasses) {
    System.out.println("Printing data...");

    if (conn != null) {
      try {
        for (int i = 0; i < domainClasses.length; i++) {
          print(domainClasses[i]);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 
   * @effects 
   *  if the underlying data source schema named <tt>dbSchema</tt> exists
   *    list contents of all data stores in it
   *  else
   *    do nothing
   * @version 5.4
   */
  @Override
  public void printDataSourceSchema(String dsSchema) {
    String sql = getQueryRelationNames(dsSchema);
    final String tableSql = "select * from %s.%s";
    ResultSet rs = null;
    try {
      rs = executeQuery(sql);
      
      if (rs.isBeforeFirst()) {
        while (rs.next()) {
          String tname = rs.getString(1);
          System.out.println("\nTABLE: " + tname);
          String sqlTable = String.format(tableSql, dsSchema, tname);
          ResultSet trs = executeQuery(sqlTable);
          try {
            if (trs.isBeforeFirst()) {
              printResultSet(trs);
            } else {
              System.out.println("(empty)");
            }
          } catch (SQLException e) {
             e.printStackTrace();
            // something wrong, but dont care
          }
        }
      } else {
        System.out.println("(empty)");
      }
    } catch (Exception e) {
      e.printStackTrace();
      // something wrong, but dont care
    } finally {
      try {
        if (rs != null)
          rs.close();
      } catch (Exception e) {
      }
    }
  }
  
  
  @Override
  public void setDebugOn(boolean tf) {
    if (tf && !debug) {
      // keep old debug value
      oldDebug = false; // = debug
      debug = true;
    } else if (!tf && debug != oldDebug) {
      // reset
      debug = oldDebug;
    }
  }

  @Override
  public void print(Class domainClass) throws DataSourceException {
    //System.out.println("Printing data...");

    if (conn != null) {
      String name = dom.getDsm().getDomainClassName(domainClass);
      System.out.println("\nTABLE: " + name);
      ResultSet rs = //selectAndProject(new String[] { "*" },
            //new String[] { name }, null, null, null);
          selectAndProject(new String[] { "*" },
              new String[] { name }, null, null, null, null);

      try {
        if (rs.isBeforeFirst()) {
          printResultSet(rs);
        } else {
          System.out.println("(empty)");
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private void printResultSet(ResultSet rs) throws DataSourceException {
    // the order of the rows in a cursor
    // are implementation dependent unless you use the SQL ORDER _statement
    try {
      ResultSetMetaData metaData = rs.getMetaData();
      int colmax = metaData.getColumnCount();

      StringBuffer colFormat = new StringBuffer();
      StringBuffer rowFormat = new StringBuffer();

      String[] colNames = new String[colmax];
      final String space = "  ";

      int colSize;
      // get the column name and its format
      for (int i = 0; i < colmax; i++) {
        colNames[i] = metaData.getColumnName(i + 1);
        // colSize = colNames[i].length();//metaData.getColumnDisplaySize(i+1);
        // colFormat.append("%").append(colSize).append("s").append(space);
        colFormat.append("%").append("s");
        if (i < colmax - 1)
          colFormat.append(",").append(space);
      }

      // print the column names
      colFormat.append("%n"); // next line
      System.out.format(colFormat.toString(), colNames);

      int i;
      String ft;
      Object[] objs = null;
      int rowi = 0;
      for (; rs.next();) {
        objs = new Object[colmax];
        for (i = 0; i < colmax; ++i) {
          objs[i] = rs.getObject(i + 1); // SQL column index starts at 1
          // prepare row format
          if (rowi == 0) {
            ft = getDisplayFormatType(objs[i]);
            // colSize = metaData.getColumnDisplaySize(i+1);
            // rowFormat.append("%").append(colSize).append("s").append(space);
            rowFormat.append("%").append("s");
            if (i < colmax - 1)
              rowFormat.append(",").append(space);
          }
        }

        if (rowi == 0) {
          rowFormat.append("%n");
        } // next line

        // print row
        System.out.format(rowFormat.toString(), objs);
        rowi++;
      }
    } catch (SQLException e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e, new Object[] {""});
    } finally {
      try {
        rs.close();
        rs.getStatement().close();
      } catch (SQLException e) {
        //
      }
    }
  }

  private static String getDisplayFormatType(Object o) {
    if (o instanceof Boolean) {
      return "b";
    } else if (o instanceof Integer || o instanceof Long) {
      return "d";
    } else if (o instanceof Float || o instanceof Double) {
      return "f";
    } else {
      // the rest is string
      return "s";
    }
  }

  /**
   * Drop and create the database tables from the Java classes that represent
   * the domain entities.<br>
   * 
   * @param domainClasses
   *          the domain classes that represent the entities whose data are to
   *          be initialised
   * @throws Exception
   */
  public void initTables(Class[] domainClasses) throws DataSourceException {
    dropTable(domainClasses);
    createTable(domainClasses);
  }

  /**
   * Create a new relational table from a domain class
   * 
   * @param domainClass
   *          a domain class
   * @effects Create a new relational table whose columns are defined from
   *          the serialisable attributes of <code>domainClass</code>, else
   *          create a new relational table from all the attributes of
   *          <code>domainClass</code>.
   *        
   *        <p>Throws NotPossibleException if failed to create the table; 
   *  NotFoundException if <tt>domainClass</tt> is not a registered domain class or 
   *  required id domain attributes of the class(es) referenced by <tt>domainClass</tt> are not found.
   */
  @Override
  public void createClassStore(Class domainClass) throws DataSourceException, NotPossibleException, NotFoundException {
    if (conn != null) {
      String sql = null;
      try {
        sql = genCreate(domainClass);
        if (debug)
          System.out.println("\n" +sql);
      } catch (NotPossibleException e) {
        throw new DataSourceException(DataSourceException.Code.FAIL_TO_CREATE_CLASS_STORE, e,
            new Object[] {domainClass.getSimpleName()});
      }

      executeUpdate(sql);
    }
  }

  /**
   * Create a new relational table from a domain class but leaving the constraints (e.g. FKs) till later.
   *
   * @modifies  
   *  tableConstraints
   * @effects Create a new relational table whose columns are defined from
   *          the serialisable attributes of <code>domainClass</code>, else
   *          create a new relational table from all the attributes of
   *          <code>domainClass</code>.
   *          
   *          <p>All the table constraints are added to <tt>tableConstraints</tt>. 
   *        
   *        <p>Throws NotPossibleException if failed to create the table; 
   *  NotFoundException if <tt>domainClass</tt> is not a registered domain class or 
   *  required id domain attributes of the class(es) referenced by <tt>domainClass</tt> are not found.
   */
  @Override
  public void createClassStoreWithoutConstraints(final Class domainClass, 
      final java.util.Map<String,List<String>> tableConstraints) throws DataSourceException, NotPossibleException, NotFoundException {
    if (conn != null) {
      String sql = null;
      try {
        sql = genCreate(domainClass, tableConstraints);
        if (debug)
          System.out.println("\n" +sql);
      } catch (NotPossibleException e) {
        throw new DataSourceException(DataSourceException.Code.FAIL_TO_CREATE_CLASS_STORE, e,
            new Object[] {domainClass.getSimpleName()});
      }

      executeUpdate(sql);
    }
  }
  
  /**
   * @requires <pre>
   *  for each entry e in tableConstraints
   *    table(e.key) /\ e.getValue contains constraint statements on table(e.key)</pre>
   * @effects <pre> 
   *  for each entry e in tableConstraints
   *    let tableName = e.key
   *    let tableCons = e.getValue
   *    alter table(tableName) adding the constraints in tableCons
   *  
   *  Throws DBException if failed to add a constraint.
   *  </pre>
   */
  @Override
  public void createConstraints(java.util.Map<String,List<String>> tableConstraints) throws DataSourceException {
    String tableName;
    List<String> tableCons;
    String sqlTemp = "alter table %s add %s";
    String sql;
    for (Entry<String,List<String>> e : tableConstraints.entrySet()) {
      tableName = e.getKey();
      tableCons = e.getValue();
      
      for (String cons : tableCons) {
        sql = String.format(sqlTemp, tableName, cons);
        
        if (debug) System.out.println("DBToolKit.createConstraints: " + sql);
        
        executeUpdate(sql);
      }
    }
  }
  
  // /**
  // * Create the database tables from the Java classes that represent the
  // domain
  // * entities.<br>
  // *
  // * This method also populates some of these tables with some test data. The
  // * tables whose data will be populated are specified in the
  // * <code>dataClasses</code> parameter.<br>
  // *
  // * The generated test data come from the Test Data Definitions and Test Data
  // * objects that are defined in the header of the {@link DataManager}
  // class.<br>
  // *
  // * @param domainClasses
  // * the domain classes that represent the entities whose data are to
  // * be initialised
  // * @param dataClasses
  // * a sub-set of the <code>domainClasses</code> whose test data are to
  // * be populated
  // * @throws Exception
  // */
  // public void initData(Class[] domainClasses, Class[] dataClasses)
  // throws Exception {
  // initTables(domainClasses);
  //
  // // create test data for data classes
  // if (dataClasses != null) {
  // for (int i = 0; i < dataClasses.length; i++) {
  // populateData(dataClasses[i]);
  // }
  // }
  // }

  // //////// SQL FUNCTIONS ///////////////////////////
  /**
   * @deprecated use {@link #selectAndProject(String[], String[], Expression[], Expression[], String[], String)}
   *  instead
   */
  private ResultSet selectAndProject(String[] selectCols, String[] tables,
      Expression[] ANDs, Expression[] ORs, String orderBy) throws DataSourceException {
    return selectAndProject(selectCols, tables, ANDs, ORs, null, orderBy);
  }
  
  /**
   * @effects 
   *  generate a SELECT SQL query whose select columns are <tt>selectCols</tt>, 
   *  from <tt>tables</tt>, with conditions specified by <tt>ANDs, ORs</tt>, and 
   *  with GROUP BY <tt>groupBy</tt> and ORDER BY <tt>orderBy</tt>
   *  
   *  <p>execute the query and return a <tt>ResultSet</tt> if succeeded; 
   *  return <tt>null</tt> if an error occured.
   *  
   * @example if the desired SELECT query is <pre>
   *    select semester, max(code) from module group by semester order by semester desc
   *  </pre> then 
   *  <tt>selectCols = {"semester", "max(code)"},
   *      tables = {"module"},
   *      ANDs = null,
   *      ORs = null,
   *      groupBy = {"semester"},
   *      orderBy = "order by semester desc" 
   *   </tt>
   */
  private ResultSet selectAndProject(String[] selectCols, String[] tables,
      Expression[] ANDs, Expression[] ORs, String[] groupBy, String orderBy) throws DataSourceException {

    String sql = genSelect(selectCols, tables,
        ANDs, ORs, groupBy, orderBy);
    
    if (debug)
      System.out.println("DBToolKit.selectAndProject: " + sql);

    /*v2.7.4: throws exception
    try {
      ResultSet rs = executeQuery(sql);
      return rs;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    */
    ResultSet rs = executeQuery(sql);
    return rs;
  }

  /**
   * Update the values of some <code>attributes</code> of an object into the
   * database.
   * 
   * @param o
   *          an object whose attribute values are to be updated
   * @param attributes
   *          the names of the attributes to be updated
   * @param idAttributes
   *          the names of the id attributes of the object
   * @return
   */
  // public boolean putObject(Object o, String[] attributes, String[]
  // idAttributes) {
  // Expression[] updates = new Expression[attributes.length];
  // Expression[] conditions = new Expression[idAttributes.length];
  //
  // String attribute = null;
  // // prepare the update expressions
  // for (int i = 0; i < attributes.length; i++) {
  // attribute = attributes[i];
  // updates[i] = new Expression(attribute, "=",
  // DomainManager.getAttributeValue(o, attribute));
  // }
  //
  // // prepare the WHERE expressions using the IDs
  // for (int i = 0; i < idAttributes.length; i++) {
  // attribute = idAttributes[i];
  // conditions[i] = new Expression(attribute, "=",
  // DomainManager.getAttributeValue(o, attribute));
  // }
  //
  // return update(o.getClass(), updates, conditions);
  // }

  /**
   * Update data in a table
   * 
   * @param table
   *          the name of the table
   * @param sets
   *          an array of expressions for setting the table values
   * @param ANDs
   *          an array of conditions for finding the rows to set the values
   * @return
   */
  private boolean update(Class c, Expression[] sets, Expression[] ANDs) {
    String table = dom.getDsm().getDomainClassName(c);

    StringBuffer sb = new StringBuffer("update ");

    sb.append(table).append(" set ");

    for (int i = 0; i < sets.length; i++) {
      sb.append(sets[i]);
      if (i < sets.length - 1) {
        sb.append(",");
      }
    }

    if (ANDs != null) {
      sb.append(" where ");
      for (int i = 0; i < ANDs.length; i++) {
        sb.append(ANDs[i]);
        if (i < ANDs.length - 1) {
          sb.append(" and ");
        }
      }
    }

    try {
      executeUpdate(sb.toString());
      return true;
    } catch (DataSourceException e) {
      return false;
    }
  }
  
  /**
   * @effects 
   *  generate a SELECT statement over the column mapped to the attribute <tt>attrib</tt>, whose row
   *  is identified by <tt>oid</tt> (if specified),  
   *  of the table that is mapped to the domain class <tt>c</tt>. 
   *  <p>If <tt>withPK = true</tt> then also include the PK column(s) in the statement.
   *  <p>If <tt>attrib</tt>'s type is comparable, add ASC sorting to the statement
   *  
   *  <p>Thus, if oid is not specified (i.e. equal <tt>null</tt>) then this generates an SQL that results in 
   *  all row values of the specified attribute be returned. Otherwise, the generated SQL results in 
   *  a single row value of the attribute, that is specified by the <tt>oid</tt>, to be returned. 
   *  
   * @pseudocode
   * (similar to (and see examples in) {@link #readValueRangeFromSource(Class, DAttr, DAttr[])})
   * 
   * <pre>
   *  let SELECT, FROM, WHERE, ORDER BY be sets of strings
   *  let tA be a string
   *  
   *  Add c to FROM
   *  
   *  if attrib is inherited from an ancestor domain class a,  
   *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
   *    let s = SQL_Join(c,x,...,a) (x may = a)
   *    add s to WHERE
   *    add x,...,a to FROM
   *    tA = a
   *  else
   *    tA = c
   *  
   *  if oid != null
   *    let oid = Oid(c,(id,val))
   *    let tid be a String
   *    if id is inherited from an ancestor domain class e (e != c)
   *      tid = e
   *      if e != a AND e has not been processed
   *        let j = SQL_Join(c,z,...,e) (z may = e)
   *        merge j into WHERE
   *        merge z,...,e into FROM
   *    else
   *      tid = c
   *    add "tid.id=val" to WHERE
   *          
   *  if withPK = true
   *    let ids = id-attributes of c
   *    for each d in ids 
   *      let tD be a String
   *      if d is inherited from an ancestor domain class b (b != c)
   *        tD = b
   *        if b != a AND b has not been processed
   *          let t = SQL_Join(c,y,...,b) (y may = b)
   *          merge t into WHERE
   *          merge y,...,b into FROM
   *      else
   *        tD = c
   *      add tD.d to SELECT
   * 
   *  Add tA.attrib to SELECT
   *  
   *  if attrib.type is comparable
   *    add tA.attrib to ORDER BY
   *    
   *  Let sql = SQL(SELECT, FROM, WHERE, ORDER BY)
   *  return SQL
   * </pre>      
   */
  private String genSelect(Class c, Oid oid, DAttr attrib, boolean withPK) {
// v3.3: call more generic method
    boolean orderByKey = false;
    return genSelect(c, oid, attrib, withPK, orderByKey);
//    Stack<String> Select = new Stack();
//    Stack<String> From = new Stack();
//    Stack<String> Where = new Stack();
//    String OrderBy = null;
//    
//    Collection<String> tables = new ArrayList();
//    Collection<String> joinTablePairs = new ArrayList();
//    
//    /*  
//     *  Add c to FROM
//     */
//    int tIndex = 0;
//    final String cTable = dom.getDsm().getDomainClassName(c);
//    final String tcTable = "t"+(tIndex++);
//    String tA;
//    From.add(cTable + " " + tcTable);
//    tables.add(cTable);
//    
//    final Class a = dom.getDsm().getDeclaringClass(c, attrib);
//    
//    Collection<DAttr> idAttribs;
//    String supTable, tsupTable;
//    Class sup = dom.getDsm().getSuperClass(c);
//    Class currClass;
//    String currTable = cTable;
//    String tcurrTable = tcTable;
//    String colName;
//    
//    if (a != c) {
//     /* attrib is inherited from an ancestor domain class a,  
//      *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
//      *    let s = SQL_Join(c,x,...,a) (x may = a)
//      *    add s to WHERE
//      *    add x,...,a to FROM
//      */
//      currClass = c;
//      do {
//        supTable = dom.getDsm().getDomainClassName(sup);
//        tsupTable = "t" + (tIndex++);
//
//        // use the id attributes to add new join expressions
//        idAttribs = dom.getDsm().getIDDomainConstraints(sup);
//        for (DAttr f : idAttribs) { // current table
//          // add join expressions between the id attributes of the two tables
//          Where.add(
//              join(currClass, tcurrTable, sup, tsupTable, f)
//              );
//          
//          joinTablePairs.add(currTable+"-"+supTable);
//        } // end for
//
//        // add super class table to FROM
//        From.add(supTable + " " + tsupTable);
//        tables.add(supTable);
//        
//        // recursive: check the super-super class and so on...
//        currTable = supTable;
//        currClass = sup;
//        tcurrTable = tsupTable;
//        sup = dom.getDsm().getSuperClass(sup);
//      } while (sup != null);
//      
//      // attribute table is the last super class name
//      tA = tcurrTable;
//    } else {
//      // attrib is in c
//      tA = tcTable;
//    } // end if 
//    
//    if (oid != null) {
//      /* add oid to WHERE
//       *  if oid != null
//       *    let oid = Oid(c,(id,val))
//       *    let tid be a String
//       *    if id is inherited from an ancestor domain class e (e != c)
//       *      if e != a 
//       *        tid = e
//       *        let j = SQL_Join(c,z,...,e) (z may = e)
//       *        merge j into WHERE
//       *        merge z,...,e into FROM
//       *      else 
//       *        tid = tA
//       *    else
//       *      tid = c
//       *    add "tid.id=val" to WHERE 
//       */
//      String tid;
//      DAttr id = oid.getIdAttribute(0);
//      Object val = oid.getIdValue(0);
//      Class e = dom.getDsm().getDeclaringClass(c, id);
//      if (e != c && e != a) {
//        sup = dom.getDsm().getSuperClass(c);
//        currTable = cTable;
//        tcurrTable = tcTable;
//        currClass = c;
//        do {
//          supTable = dom.getDsm().getDomainClassName(sup);
//          tsupTable = "t" + (tIndex++);
//
//          // merge Join into WHERE
//          if (!joinTablePairs.contains(currTable+"-"+supTable)) {
//            // use the id attributes to add new join expressions
//            idAttribs = dom.getDsm().getIDDomainConstraints(sup);
//            for (DAttr f : idAttribs) { // current table
//              // add join expressions between the id attributes of the two tables
//              Where.add(
//                  join(currClass, tcurrTable, sup, tsupTable, f)
//                  );
//              
//              joinTablePairs.add(currTable+"-"+supTable);
//            } // end for
//          }
//          
//          // merge table into FROM
//          if (!tables.contains(supTable)) {
//            From.add(supTable + " " + tsupTable);
//            tables.add(supTable);
//          }
//          
//          // recursive: go up to the super-super class and so on...
//          currTable = supTable;
//          currClass = sup;
//          tcurrTable = tsupTable;
//          sup = dom.getDsm().getSuperClass(sup);
//        } while (sup != null);
//        
//        tid = tcurrTable;
//      } else if (e == a) {
//        tid = tA;
//      } else {
//        // e = c
//        tid = tcTable;
//      }
//      
//      // add oid to WHERE
//      colName = getColName(e, id);
//      colName = tid + "." + colName;
//      Where.add(colName + "=" + toSQLString(id.type(), val, true) // val is never null
//          );
//    } // end oid
//    
//    // process id attributes...
//    List<String> idAttribNames = null;  // v2.7
//    if (withPK) {
//      // v2.7
//      idAttribNames = new ArrayList<String>();
//      
//      Class b;
//      java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();
//      String tD;
//      idAttribs = dom.getDsm().getIDDomainConstraints(c);
//
//      for (DAttr d : idAttribs) {
//        b = dom.getDsm().getDeclaringClass(c, d);
//        if (b != c) {
//          /*d is inherited from an ancestor domain class b (b != c)
//           *        if b != a AND b has not been processed
//           *          let t = SQL_Join(c,y,...,b) (y may = b)
//           *          merge t into WHERE
//           *          merge y,...,b into FROM
//           */
//          if (b != a && !processed.containsKey(b)) {
//            sup = dom.getDsm().getSuperClass(c);
//            currTable = cTable;
//            tcurrTable = tcTable;
//            currClass = c;
//            do {
//              supTable = dom.getDsm().getDomainClassName(sup);
//              tsupTable = "t" + (tIndex++);
//
//              // merge Join into WHERE
//              if (!joinTablePairs.contains(currTable+"-"+supTable)) {
//                // use the id attributes to add new join expressions
//                idAttribs = dom.getDsm().getIDDomainConstraints(sup);
//                for (DAttr f : idAttribs) { // current table
//                  // add join expressions between the id attributes of the two tables
//                  Where.add(
//                      join(currClass, tcurrTable, sup, tsupTable, f)
//                      );
//                  
//                  joinTablePairs.add(currTable+"-"+supTable);
//                } // end for
//              }
//              
//              // merge table into FROM
//              if (!tables.contains(supTable)) {
//                From.add(supTable + " " + tsupTable);
//                tables.add(supTable);
//              }
//              
//              // recursive: go up to the super-super class and so on...
//              currTable = supTable;
//              currClass = sup;
//              tcurrTable = tsupTable;
//              sup = dom.getDsm().getSuperClass(sup);
//            } while (sup != null);
//            
//            tD = tcurrTable;
//            processed.put(b, tcurrTable);
//          } else if (b == a) {
//            tD = tA;
//          } else {  // b already processed
//            tD = processed.get(b);
//          }  // end if
//        } else {
//          tD = tcTable;
//        } // end if
//        
//        //Select.add(tD + "."+d.name());
//        colName = getColName(b,d);
//        colName = tD + "." + colName;
//        Select.add(colName);
//        
//        // v2.7
//        idAttribNames.add(colName);
//      } // end for
//    } // end if
//    
//    /*  Add attrib to SELECT
//     */
//    //String colName = tA+"."+attrib.name();
//    colName = getColName(a, attrib);
//    colName = tA + "." + colName;
//    Select.add(colName);
//    
//    boolean comparable = attrib.type().isComparable();
//    /**
//     * v2.7: if attribute is comparable, order by it; otherwise order by the oid if it is specified
//     */
//    
//    if (comparable) {
//      // add order by if attribute's type is comparable
//      OrderBy = "order by " + colName + " ASC";
//    } 
//    // v2.7
//    else if (idAttribNames != null) {
//      OrderBy = "order by ";
//      for (int i = 0; i < idAttribNames.size(); i++) {
//        OrderBy += idAttribNames.get(i);
//        if (i < idAttribNames.size()-1) OrderBy += ", ";
//      }
//      OrderBy += " ASC";
//    }
//    
//    // generate SQL statement
//    String sql = genSelect(
//        Select.toArray(new String[Select.size()]), 
//        From.toArray(new String[From.size()]),
//        (!Where.isEmpty() ? Where.toArray(new String[Where.size()]): null),
//        null,
//        null,
//        OrderBy);
//    
//    return sql;
  }
  
  /**
   * A more generic version of {@link #genSelect(Class, Oid, DAttr, boolean)} that supports order-by key
   *
   * @requires
   *  if <tt>orderByKey = true</tt> then <b>all</b> key attributes of <tt>c</tt> must by owned by <tt>c</tt> 
   *  
   * @effects 
   *  generate a SELECT statement over the column mapped to the attribute <tt>attrib</tt>, whose row
   *  is identified by <tt>oid</tt> (if specified),  
   *  of the table that is mapped to the domain class <tt>c</tt>. 
   *  <p>If <tt>withPK = true</tt> then also include the PK column(s) in the statement.
   *  <p>If <tt>orderByKey = true</tt> then add order-by by the key attribute of <tt>c</tt>,  
   *      else add order-by by <tt>attrib</tt>
   *  
   *  <p>Thus, if oid is not specified (i.e. equal <tt>null</tt>) then this generates an SQL that results in 
   *  all row values of the specified attribute be returned. Otherwise, the generated SQL results in 
   *  a single row value of the attribute, that is specified by the <tt>oid</tt>, to be returned. 
   *
   * <p>Throws NotPossibleException if pre-condition is violated
   * 
   * @version 3.3
   */
  private String genSelect(Class c, Oid oid, DAttr attrib, final boolean withPK, final boolean orderByKey) throws NotPossibleException {
    Stack<String> Select = new Stack();
    Stack<String> From = new Stack();
    Stack<String> Where = new Stack();
    StringBuffer OrderBy = null;
    
    Collection<String> tables = new ArrayList();
    Collection<String> joinTablePairs = new ArrayList();
    
    /*  
     *  Add c to FROM
     */
    int tIndex = 0;
    final String cTable = dom.getDsm().getDomainClassName(c);
    final String tcTable = "t"+(tIndex++);
    String tA;
    From.add(cTable + " " + tcTable);
    tables.add(cTable);
    
    final Class a = dom.getDsm().getDeclaringClass(c, attrib);
    
    Collection<DAttr> idAttribs;
    String supTable, tsupTable;
    Class sup = dom.getDsm().getSuperClass(c);
    Class currClass;
    String currTable = cTable;
    String tcurrTable = tcTable;
    String colName;
    
    if (a != c) {
     /* attrib is inherited from an ancestor domain class a,  
      *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
      *    let s = SQL_Join(c,x,...,a) (x may = a)
      *    add s to WHERE
      *    add x,...,a to FROM
      */
      currClass = c;
      do {
        supTable = dom.getDsm().getDomainClassName(sup);
        tsupTable = "t" + (tIndex++);
  
        // use the id attributes to add new join expressions
        idAttribs = dom.getDsm().getIDDomainConstraints(sup);
        for (DAttr f : idAttribs) { // current table
          // add join expressions between the id attributes of the two tables
          Where.add(
              join(currClass, tcurrTable, sup, tsupTable, f)
              );
          
          joinTablePairs.add(currTable+"-"+supTable);
        } // end for
  
        // add super class table to FROM
        From.add(supTable + " " + tsupTable);
        tables.add(supTable);
        
        // recursive: check the super-super class and so on...
        currTable = supTable;
        currClass = sup;
        tcurrTable = tsupTable;
        sup = dom.getDsm().getSuperClass(sup);
      } while (sup != null);
      
      // attribute table is the last super class name
      tA = tcurrTable;
    } else {
      // attrib is in c
      tA = tcTable;
    } // end if 
    
    if (oid != null) {
      /* add oid to WHERE
       *  if oid != null
       *    let oid = Oid(c,(id,val))
       *    let tid be a String
       *    if id is inherited from an ancestor domain class e (e != c)
       *      if e != a 
       *        tid = e
       *        let j = SQL_Join(c,z,...,e) (z may = e)
       *        merge j into WHERE
       *        merge z,...,e into FROM
       *      else 
       *        tid = tA
       *    else
       *      tid = c
       *    add "tid.id=val" to WHERE 
       */
      String tid;
      DAttr id = oid.getIdAttribute(0);
      Object val = oid.getIdValue(0);
      Class e = dom.getDsm().getDeclaringClass(c, id);
      if (e != c && e != a) {
        sup = dom.getDsm().getSuperClass(c);
        currTable = cTable;
        tcurrTable = tcTable;
        currClass = c;
        do {
          supTable = dom.getDsm().getDomainClassName(sup);
          tsupTable = "t" + (tIndex++);
  
          // merge Join into WHERE
          if (!joinTablePairs.contains(currTable+"-"+supTable)) {
            // use the id attributes to add new join expressions
            idAttribs = dom.getDsm().getIDDomainConstraints(sup);
            for (DAttr f : idAttribs) { // current table
              // add join expressions between the id attributes of the two tables
              Where.add(
                  join(currClass, tcurrTable, sup, tsupTable, f)
                  );
              
              joinTablePairs.add(currTable+"-"+supTable);
            } // end for
          }
          
          // merge table into FROM
          if (!tables.contains(supTable)) {
            From.add(supTable + " " + tsupTable);
            tables.add(supTable);
          }
          
          // recursive: go up to the super-super class and so on...
          currTable = supTable;
          currClass = sup;
          tcurrTable = tsupTable;
          sup = dom.getDsm().getSuperClass(sup);
        } while (sup != null);
        
        tid = tcurrTable;
      } else if (e == a) {
        tid = tA;
      } else {
        // e = c
        tid = tcTable;
      }
      
      // add oid to WHERE
      colName = getColName(e, id);
      colName = tid + "." + colName;
      Where.add(colName + "=" + toSQLString(id.type(), val, true) // val is never null
          );
    } // end oid
    
    // process id attributes...
    List<String> idAttribNames = null;  // v2.7
    if (withPK) {
      // v2.7
      idAttribNames = new ArrayList<String>();
      
      Class b;
      java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();
      String tD;
      idAttribs = dom.getDsm().getIDDomainConstraints(c);
  
      for (DAttr d : idAttribs) {
        b = dom.getDsm().getDeclaringClass(c, d);
        if (b != c) {
          /*d is inherited from an ancestor domain class b (b != c)
           *        if b != a AND b has not been processed
           *          let t = SQL_Join(c,y,...,b) (y may = b)
           *          merge t into WHERE
           *          merge y,...,b into FROM
           */
          if (b != a && !processed.containsKey(b)) {
            sup = dom.getDsm().getSuperClass(c);
            currTable = cTable;
            tcurrTable = tcTable;
            currClass = c;
            do {
              supTable = dom.getDsm().getDomainClassName(sup);
              tsupTable = "t" + (tIndex++);
  
              // merge Join into WHERE
              if (!joinTablePairs.contains(currTable+"-"+supTable)) {
                // use the id attributes to add new join expressions
                idAttribs = dom.getDsm().getIDDomainConstraints(sup);
                for (DAttr f : idAttribs) { // current table
                  // add join expressions between the id attributes of the two tables
                  Where.add(
                      join(currClass, tcurrTable, sup, tsupTable, f)
                      );
                  
                  joinTablePairs.add(currTable+"-"+supTable);
                } // end for
              }
              
              // merge table into FROM
              if (!tables.contains(supTable)) {
                From.add(supTable + " " + tsupTable);
                tables.add(supTable);
              }
              
              // recursive: go up to the super-super class and so on...
              currTable = supTable;
              currClass = sup;
              tcurrTable = tsupTable;
              sup = dom.getDsm().getSuperClass(sup);
            } while (sup != null);
            
            tD = tcurrTable;
            processed.put(b, tcurrTable);
          } else if (b == a) {
            tD = tA;
          } else {  // b already processed
            tD = processed.get(b);
          }  // end if
        } else {
          tD = tcTable;
        } // end if
        
        //Select.add(tD + "."+d.name());
        colName = getColName(b,d);
        colName = tD + "." + colName;
        Select.add(colName);
        
        // v2.7
        idAttribNames.add(colName);
      } // end for
    } // end if
    
    /*  Add attrib to SELECT
     */
    //String colName = tA+"."+attrib.name();
    colName = getColName(a, attrib);
    colName = tA + "." + colName;
    Select.add(colName);
    
    OrderBy = new StringBuffer("order by ");
    if (orderByKey) {
      // order by c's key attribute(s)
      if (idAttribNames != null) {
        // use the id attributes that have been identified above
        for (int i = 0; i < idAttribNames.size(); i++) {
          OrderBy.append(idAttribNames.get(i));
          if (i < idAttribNames.size()-1) 
            OrderBy.append(", "); 
        }
      } else {
        // id attributes have not been identified
        String[] idAttributeNames = getIdColumnsFor(c, tcTable);
        for (int i = 0; i < idAttributeNames.length; i++) {
          OrderBy.append(idAttributeNames[i]);
          if (i < idAttributeNames.length-1) 
            OrderBy.append(", "); 
        }
      }
      
      OrderBy.append(" ASC");
    } else {
      // order by attrib, if it is comparable, or by c's key attribute(s) if it is not comparable
      /**
       * v2.7: if attribute is comparable, order by it; otherwise order by the oid if it is specified
       */
      boolean comparable = attrib.type().isComparable();
      if (comparable) {
        // add order by if attribute's type is comparable
        //OrderBy = "order by " + colName + " ASC";
        OrderBy.append(colName).append(" ASC");
      } 
      // v2.7
      else if (idAttribNames != null) {
        //OrderBy = "order by ";
        for (int i = 0; i < idAttribNames.size(); i++) {
          //OrderBy += idAttribNames.get(i);
          OrderBy.append(idAttribNames.get(i));
          if (i < idAttribNames.size()-1) 
            OrderBy.append(", "); //OrderBy += ", ";
        }
        OrderBy.append(" ASC"); //OrderBy += " ASC";
      }
    }
    
    // generate SQL statement
    String sql = genSelect(
        Select.toArray(new String[Select.size()]), 
        From.toArray(new String[From.size()]),
        (!Where.isEmpty() ? Where.toArray(new String[Where.size()]): null),
        null,
        null,
        OrderBy.toString());
    
    return sql;    
  }
  
  /**
   * This is a more general version of {@link #genSelect(Class, Oid, DAttr, boolean)} in that 
   * it supports multiple attributes.
   * 
   * @requires 
   *  attribs != null /\ attribs.length > 1 /\ c != null
   * 
   * @version 3.1
   */
  private String genSelect(Class c, Oid oid, DAttr[] attribs, boolean withPK) {
    // v3.3: use more generic method
    boolean orderByKey = false;
    return genSelect(c, oid, attribs, withPK, orderByKey);
    
//    if (attribs.length == 1)
//      return genSelect(c, oid, attribs[0], withPK);
//    
//    final DSMBasic dsm = dom.getDsm();
//    
//    Stack<String> Select = new Stack();
//    Stack<String> From = new Stack();
//    Stack<String> Where = new Stack();
//    StringBuffer OrderBy = new StringBuffer("order by ");
//    
//    Collection<String> tables = new ArrayList();
//    Collection<String> joinTablePairs = new ArrayList();
//    
//    /*  
//     *  Add c to FROM
//     */
//    int tIndex = 0;
//    final String cTable = dsm.getDomainClassName(c);
//    final String tcTable = "t"+(tIndex++);
//    From.add(cTable + " " + tcTable);
//    tables.add(cTable);
//    
//    int[] tIndexArr = {tIndex};
//    
//    // loop through the attributes and update them into the query data structure
//    boolean firstAttrib = true;
//    for (DAttr attrib : attribs) {
//      if (firstAttrib) {
//        // first attrib: generate with all extra features (i.e. oid and withPK if specified)
//        genSelect(c, oid, attrib, withPK, tables, joinTablePairs, tIndexArr, tcTable, 
//          Select, From, Where, OrderBy);
//        firstAttrib = false;
//      } else {
//        // sub-sequent attributes: generate without extra features
//        genSelect(c, null, attrib, false, tables, joinTablePairs, tIndexArr, tcTable, 
//            Select, From, Where, OrderBy);
//      }
//    }
//    
//    // finalise OrderBy
//    if (OrderBy.charAt(OrderBy.length()-1) == ',') 
//      OrderBy.deleteCharAt(OrderBy.length()-1);
//      
//    OrderBy.append(" ASC");
//    
//    // generate SQL statement
//    String sql = genSelect(
//        Select.toArray(new String[Select.size()]), 
//        From.toArray(new String[From.size()]),
//        (!Where.isEmpty() ? Where.toArray(new String[Where.size()]): null),
//        // ORs
//        null,
//        // Group by
//        null,
//        OrderBy.toString());
//    
//    return sql;
  }
  
  /**
   * This is a more general version of {@link #genSelect(Class, Oid, DAttr[], boolean)} in that 
   * it supports order-by-key
   * 
   * @requires 
   *  attribs != null /\ attribs.length > 1 /\ c != null /\ 
   *  if <tt>orderByKey = true</tt> then <b>all</b> id-attributes of <tt>c</tt> must be owned by <tt>c</tt>
   * 
   * @version 3.3
   */
  private String genSelect(Class c, Oid oid, DAttr[] attribs, boolean withPK, final boolean orderByKey) throws NotPossibleException {
    if (attribs.length == 1)
      return genSelect(c, oid, attribs[0], withPK, orderByKey);
    
    final DSMBasic dsm = dom.getDsm();
    
    Stack<String> Select = new Stack();
    Stack<String> From = new Stack();
    Stack<String> Where = new Stack();
    StringBuffer OrderBy = new StringBuffer("order by ");
    
    Collection<String> tables = new ArrayList();
    Collection<String> joinTablePairs = new ArrayList();
    
    /*  
     *  Add c to FROM
     */
    int tIndex = 0;
    final String cTable = dsm.getDomainClassName(c);
    final String tcTable = "t"+(tIndex++);
    From.add(cTable + " " + tcTable);
    tables.add(cTable);
    
    int[] tIndexArr = {tIndex};
    
    // loop through the attributes and update them into the query data structure
    boolean firstAttrib = true;
    for (DAttr attrib : attribs) {
      if (firstAttrib) {
        // first attrib: generate with all extra features (i.e. oid and withPK if specified)
        genSelect(c, oid, attrib, withPK, orderByKey, tables, joinTablePairs, tIndexArr, tcTable, 
          Select, From, Where, OrderBy);
        firstAttrib = false;
      } else {
        // sub-sequent attributes: generate without extra features
        genSelect(c, null, attrib, false, orderByKey, tables, joinTablePairs, tIndexArr, tcTable, 
            Select, From, Where, 
            (orderByKey ? null : OrderBy) // update OrderBy just once (for first attribute) if orderByKey is true  
            );
      }
    }
    
    // finalise OrderBy
    if (OrderBy.charAt(OrderBy.length()-1) == ',') 
      OrderBy.deleteCharAt(OrderBy.length()-1);
      
    OrderBy.append(" ASC");
    
    // generate SQL statement
    String sql = genSelect(
        Select.toArray(new String[Select.size()]), 
        From.toArray(new String[From.size()]),
        (!Where.isEmpty() ? Where.toArray(new String[Where.size()]): null),
        // ORs
        null,
        // Group by
        null,
        OrderBy.toString());
    
    return sql;
  }
  
  /**
   * A base method for {@link #genSelect(Class, Oid, DAttr[], boolean, boolean)}
   * 
   * @modifies tables, joinTablePairs, tIndexArr, Select, From, Where, OrderBy
   * @effects 
   *  updates tables, joinTablePairs, tIndexArr, Select, From, Where, OrderBy to include <tt>c.attrib</tt> for 
   *  the object identified by <tt>oid</tt> (if specified)
   *  
   * @version
   * - 3.3: added orderByKey
   */
  private void genSelect(Class c, Oid oid, DAttr attrib, final boolean withPK, final boolean orderByKey,  
      Collection<String> tables, Collection<String> joinTablePairs, int[] tIndexArr, 
      final String tcTable, 
      Stack<String> Select, Stack<String> From, Stack<String> Where, StringBuffer OrderBy) {
    /*  
     *  Add c to FROM
     */
    int tIndex = tIndexArr[0];
    final DSMBasic dsm = dom.getDsm();
    final String cTable = dsm.getDomainClassName(c);
    
    String tA;
    final Class a = dsm.getDeclaringClass(c, attrib);
    
    Collection<DAttr> idAttribs;
    String supTable, tsupTable;
    Class sup = dsm.getSuperClass(c);
    Class currClass;
    String currTable = cTable;
    String tcurrTable = tcTable;
    String colName;
    
    if (a != c) {
     /* attrib is inherited from an ancestor domain class a,  
      *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
      *    let s = SQL_Join(c,x,...,a) (x may = a)
      *    add s to WHERE
      *    add x,...,a to FROM
      */
      currClass = c;
      do {
        supTable = dsm.getDomainClassName(sup);
        tsupTable = "t" + (tIndex++);

        // add super class table to FROM (if not already)
        if (!joinTablePairs.contains(currTable+"-"+supTable)) {
          // use the id attributes to add new join expressions
          idAttribs = dsm.getIDDomainConstraints(sup);
          for (DAttr f : idAttribs) { // current table
            // add join expressions between the id attributes of the two tables
            Where.add(
                join(currClass, tcurrTable, sup, tsupTable, f)
                );
            
            joinTablePairs.add(currTable+"-"+supTable);
          } // end for
        }

        if (!tables.contains(supTable)) {
          From.add(supTable + " " + tsupTable);
          tables.add(supTable);
        }

        // recursive: check the super-super class and so on...
        currTable = supTable;
        currClass = sup;
        tcurrTable = tsupTable;
        sup = dsm.getSuperClass(sup);
      } while (sup != null);
      
      // attribute table is the last super class name
      tA = tcurrTable;
    } else {
      // attrib is in c
      tA = tcTable;
    } // end if 
    
    if (oid != null) {
      /* add oid to WHERE
       *  if oid != null
       *    let oid = Oid(c,(id,val))
       *    let tid be a String
       *    if id is inherited from an ancestor domain class e (e != c)
       *      if e != a 
       *        tid = e
       *        let j = SQL_Join(c,z,...,e) (z may = e)
       *        merge j into WHERE
       *        merge z,...,e into FROM
       *      else 
       *        tid = tA
       *    else
       *      tid = c
       *    add "tid.id=val" to WHERE 
       */
      String tid;
      DAttr id = oid.getIdAttribute(0);
      Object val = oid.getIdValue(0);
      Class e = dsm.getDeclaringClass(c, id);
      if (e != c && e != a) {
        sup = dsm.getSuperClass(c);
        currTable = cTable;
        tcurrTable = tcTable;
        currClass = c;
        do {
          supTable = dsm.getDomainClassName(sup);
          tsupTable = "t" + (tIndex++);

          // merge Join into WHERE
          if (!joinTablePairs.contains(currTable+"-"+supTable)) {
            // use the id attributes to add new join expressions
            idAttribs = dsm.getIDDomainConstraints(sup);
            for (DAttr f : idAttribs) { // current table
              // add join expressions between the id attributes of the two tables
              Where.add(
                  join(currClass, tcurrTable, sup, tsupTable, f)
                  );
              
              joinTablePairs.add(currTable+"-"+supTable);
            } // end for
          }
          
          // merge table into FROM
          if (!tables.contains(supTable)) {
            From.add(supTable + " " + tsupTable);
            tables.add(supTable);
          }
          
          // recursive: go up to the super-super class and so on...
          currTable = supTable;
          currClass = sup;
          tcurrTable = tsupTable;
          sup = dsm.getSuperClass(sup);
        } while (sup != null);
        
        tid = tcurrTable;
      } else if (e == a) {
        tid = tA;
      } else {
        // e = c
        tid = tcTable;
      }
      
      // add oid to WHERE
      colName = getColName(e, id);
      colName = tid + "." + colName;
      Where.add(colName + "=" + toSQLString(id.type(), val, true) // val is never null
          );
    } // end oid
    
    // process id attributes...
    List<String> idAttribNames = null;  // v2.7
    if (withPK) {
      // v2.7
      idAttribNames = new ArrayList<String>();
      
      Class b;
      java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();
      String tD;
      idAttribs = dsm.getIDDomainConstraints(c);

      for (DAttr d : idAttribs) {
        b = dsm.getDeclaringClass(c, d);
        if (b != c) {
          /*d is inherited from an ancestor domain class b (b != c)
           *        if b != a AND b has not been processed
           *          let t = SQL_Join(c,y,...,b) (y may = b)
           *          merge t into WHERE
           *          merge y,...,b into FROM
           */
          if (b != a && !processed.containsKey(b)) {
            sup = dsm.getSuperClass(c);
            currTable = cTable;
            tcurrTable = tcTable;
            currClass = c;
            do {
              supTable = dsm.getDomainClassName(sup);
              tsupTable = "t" + (tIndex++);

              // merge Join into WHERE
              if (!joinTablePairs.contains(currTable+"-"+supTable)) {
                // use the id attributes to add new join expressions
                idAttribs = dsm.getIDDomainConstraints(sup);
                for (DAttr f : idAttribs) { // current table
                  // add join expressions between the id attributes of the two tables
                  Where.add(
                      join(currClass, tcurrTable, sup, tsupTable, f)
                      );
                  
                  joinTablePairs.add(currTable+"-"+supTable);
                } // end for
              }
              
              // merge table into FROM
              if (!tables.contains(supTable)) {
                From.add(supTable + " " + tsupTable);
                tables.add(supTable);
              }
              
              // recursive: go up to the super-super class and so on...
              currTable = supTable;
              currClass = sup;
              tcurrTable = tsupTable;
              sup = dsm.getSuperClass(sup);
            } while (sup != null);
            
            tD = tcurrTable;
            processed.put(b, tcurrTable);
          } else if (b == a) {
            tD = tA;
          } else {  // b already processed
            tD = processed.get(b);
          }  // end if
        } else {
          tD = tcTable;
        } // end if
        
        //Select.add(tD + "."+d.name());
        colName = getColName(b,d);
        colName = tD + "." + colName;
        Select.add(colName);
        
        // v2.7
        idAttribNames.add(colName);
      } // end for
    } // end if withPK
    
    /*  Add attrib to SELECT
     */
    colName = getColName(a, attrib);
    colName = tA + "." + colName;
    Select.add(colName);

    /* OrderBy (if specified) */
    if (OrderBy != null) {
      if (orderByKey) {
        // order by c's id attributes (if not already)
        if (idAttribNames != null) {
          // use the id attributes that have been identified above
          for (int i = 0; i < idAttribNames.size(); i++) {
            OrderBy.append(idAttribNames.get(i));
            if (i < idAttribNames.size()-1) OrderBy.append(","); 
          }
        } else {
          // id attributes have not been identified
          String[] idAttributeNames = getIdColumnsFor(c, tcTable);
          for (int i = 0; i < idAttributeNames.length; i++) {
            OrderBy.append(idAttributeNames[i]);
            if (i < idAttributeNames.length-1) OrderBy.append(","); 
          }
        }
      } else {
        // order by c.attrib if this is comparable, or by c's id attributes if it is not comparable  
        /**
         * v2.7: if attribute is comparable, order by it; otherwise order by the oid if it is specified
         */
        boolean comparable = attrib.type().isComparable();
        if (comparable) {
          // add order by if attribute's type is comparable
          OrderBy.append(colName).append(",");
        } 
        // v2.7
        else if (idAttribNames != null) {
          for (int i = 0; i < idAttribNames.size(); i++) {
            OrderBy.append(idAttribNames.get(i));
            if (i < idAttribNames.size()-1) OrderBy.append(",");
          }
        }      
      }
    }

    // update table index
    tIndexArr[0] = tIndex;
  }
  
  /**
   * @effects 
   *  generate a SELECT statement over the column mapped to the attribute <tt>attrib</tt> 
   *  of the table that is mapped to the domain class <tt>c</tt>. 
   *  <p>If <tt>withPK = true</tt> then also include the PK column(s) in the statement.
   *  <p>If <tt>attrib</tt>'s type is comparable, add ASC sorting to the statement
   * @pseudocode
   * (similar to (and see examples in) {@link #readValueRangeFromSource(Class, DAttr, DAttr[])})
   * 
   * <pre>
   *  let SELECT, FROM, WHERE, ORDER BY be sets of strings
   *  let tA be a string
   *  
   *  Add c to FROM
   *  
   *  if attrib is inherited from an ancestor domain class a,  
   *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
   *    let s = SQL_Join(c,x,...,a) (x may = a)
   *    add s to WHERE
   *    add x,...,a to FROM
   *    tA = a
   *  else
   *    tA = c
   *  
   *  if withPK = true
   *    let ids = id-attributes of c
   *    for each d in ids 
   *      let tD be a String
   *      if d is inherited from an ancestor domain class b (b != c)
   *        tD = b
   *        if b != a AND b has not been processed
   *          let t = SQL_Join(c,y,...,b) (y may = b)
   *          merge t into WHERE
   *          merge y,...,b into FROM
   *      else
   *        tD = c
   *      add tD.d to SELECT
   * 
   *  Add tA.attrib to SELECT
   *  
   *  if attrib.type is comparable
   *    add tA.attrib to ORDER BY
   *    
   *  Let sql = SQL(SELECT, FROM, WHERE, ORDER BY)
   *  return SQL
   * </pre>     
   * 
   * @deprecated (to be removed) use {@link #genSelect(Class, Oid, DAttr, boolean)} instead
   * 
   */
  private String genSelect(Class c, DAttr attrib, boolean withPK) {
    return genSelect(c, null, attrib, withPK);
//    
//    Stack<String> Select = new Stack();
//    Stack<String> From = new Stack();
//    Stack<String> Where = new Stack();
//    String OrderBy = null;
//    
//    Collection<String> tables = new ArrayList();
//    Collection<String> joinTablePairs = new ArrayList();
//    
//    /*  
//     *  Add c to FROM
//     */
//    int tIndex = 0;
//    final String cTable = schema.getDsm().getDomainClassName(c);
//    final String tcTable = "t"+(tIndex++);
//    String tA;
//    From.add(cTable + " " + tcTable);
//    tables.add(cTable);
//    
//    final Class a = schema.getDsm().getDeclaringClass(c, attrib);
//    
//    Collection<DomainConstraint> idAttribs;
//    String supTable, tsupTable;
//    Class sup = schema.getDsm().getSuperClass(c);
//    Class currClass;
//    String currTable = cTable;
//    String tcurrTable = tcTable;
//    String colName;
//    
//    if (a != c) {
//     /* attrib is inherited from an ancestor domain class a,  
//      *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
//      *    let s = SQL_Join(c,x,...,a) (x may = a)
//      *    add s to WHERE
//      *    add x,...,a to FROM
//      */
//      currClass = c;
//      do {
//        supTable = schema.getDsm().getDomainClassName(sup);
//        tsupTable = "t" + (tIndex++);
//
//        // use the id attributes to add new join expressions
//        idAttribs = schema.getDsm().getIDDomainConstraints(sup);
//        for (DomainConstraint f : idAttribs) { // current table
//          // add join expressions between the id attributes of the two tables
//          Where.add(
//              join(currClass, tcurrTable, sup, tsupTable, f)
////              new Expression(tcurrTable + "." + f.name(),
////              Expression.Op.EQ, tsupTable + "." + f.name(),
////              Expression.Type.Metadata)
//              );
//          
//          joinTablePairs.add(currTable+"-"+supTable);
//        } // end for
//
//        // add super class table to FROM
//        From.add(supTable + " " + tsupTable);
//        tables.add(supTable);
//        
//        // recursive: check the super-super class and so on...
//        currTable = supTable;
//        currClass = sup;
//        tcurrTable = tsupTable;
//        sup = schema.getDsm().getSuperClass(sup);
//      } while (sup != null);
//      
//      // attribute table is the last super class name
//      tA = tcurrTable;
//    } else {
//      // attrib is in c
//      tA = tcTable;
//    } // end if 
//    
//    // process id attributes...
//    if (withPK) {
//      Class b;
//      java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();
//      String tD;
//      idAttribs = schema.getDsm().getIDDomainConstraints(c);
//
//      for (DomainConstraint d : idAttribs) {
//        b = schema.getDsm().getDeclaringClass(c, d);
//        if (b != c) {
//          /*d is inherited from an ancestor domain class b (b != c)
//           *        if b != a AND b has not been processed
//           *          let t = SQL_Join(c,y,...,b) (y may = b)
//           *          merge t into WHERE
//           *          merge y,...,b into FROM
//           */
//          if (b != a && !processed.containsKey(b)) {
//            sup = schema.getDsm().getSuperClass(c);
//            currTable = cTable;
//            tcurrTable = tcTable;
//            currClass = c;
//            do {
//              supTable = schema.getDsm().getDomainClassName(sup);
//              tsupTable = "t" + (tIndex++);
//
//              // merge Join into WHERE
//              if (!joinTablePairs.contains(currTable+"-"+supTable)) {
//                // use the id attributes to add new join expressions
//                idAttribs = schema.getDsm().getIDDomainConstraints(sup);
//                for (DomainConstraint f : idAttribs) { // current table
//                  // add join expressions between the id attributes of the two tables
//                  Where.add(
//                      join(currClass, tcurrTable, sup, tsupTable, f)
////                      new Expression(tcurrTable + "." + f.name(),
////                      Expression.Op.EQ, tsupTable + "." + f.name(),
////                      Expression.Type.Metadata)
//                      );
//                  
//                  joinTablePairs.add(currTable+"-"+supTable);
//                } // end for
//              }
//              
//              // merge table into FROM
//              if (!tables.contains(supTable)) {
//                From.add(supTable + " " + tsupTable);
//                tables.add(supTable);
//              }
//              
//              // recursive: go up to the super-super class and so on...
//              currTable = supTable;
//              currClass = sup;
//              tcurrTable = tsupTable;
//              sup = schema.getDsm().getSuperClass(sup);
//            } while (sup != null);
//            
//            tD = tcurrTable;
//            processed.put(b, tcurrTable);
//          } else if (b == a) {
//            tD = tA;
//          } else {  // b already processed
//            tD = processed.get(b);
//          }  // end if
//        } else {
//          tD = tcTable;
//        } // end if
//        
//        //Select.add(tD + "."+d.name());
//        colName = getColName(b,d);
//        colName = tD + "." + colName;
//        Select.add(colName);
//      } // end for
//    } // end if
//    
//    /*  Add attrib to SELECT
//     */
//    //String colName = tA+"."+attrib.name();
//    colName = getColName(a, attrib);
//    colName = tA + "." + colName;
//    Select.add(colName);
//    
//    boolean comparable = attrib.type().isComparable();
//    if (comparable) {
//      // add order by if attribute's type is comparable
//      OrderBy = "order by " + colName + " ASC";
//    }
//    
//    // generate SQL statement
//    String sql = genSelect(
//        Select.toArray(new String[Select.size()]), 
//        From.toArray(new String[From.size()]),
//        (!Where.isEmpty() ? Where.toArray(new String[Where.size()]): null),
//        null,
//        null,
//        OrderBy);
//    
//    return sql;
  }
  
//  private void genJoinHierarchy(Class c, 
//      Collection<String> Select, 
//      Collection<Expression> Where, 
//      Collection<String> From, 
//      Collection<String> joinTablePairs,
//      Collection<String> tables) {
//    int tIndex = 0;
//    final String cTable = schema.getDsm().getDomainClassName(c);
//    final String tcTable = "t"+(tIndex++);
//    
//    Collection<DomainConstraint> idAttribs;
//    String supTable, tsupTable;
//    Class sup = schema.getDsm().getSuperClass(c);
//    String currTable = cTable;
//    String tcurrTable = tcTable;
//    
//    do {
//      supTable = schema.getDsm().getDomainClassName(sup);
//      tsupTable = "t" + (tIndex++);
//
//      // use the id attributes to add new join expressions
//      idAttribs = schema.getDsm().getIDDomainConstraints(sup);
//      for (DomainConstraint f : idAttribs) { // current table
//        // add join expressions between the id attributes of the two tables
//        Where.add(new Expression(tcurrTable + "." + f.name(),
//            Expression.Op.EQ, tsupTable + "." + f.name(),
//            Expression.Type.Metadata));
//        
//        joinTablePairs.add(currTable+"-"+supTable);
//      } // end for
//
//      // add super class table to FROM
//      From.add(supTable + " " + tsupTable);
//      tables.add(supTable);
//      
//      // recursive: check the super-super class and so on...
//      currTable = supTable;
//      tcurrTable = tsupTable;
//      sup = schema.getDsm().getSuperClass(sup);
//    } while (sup != null);
//  }
  
  /**
   * @effects 
   *  generate and return a SELECT SQL query whose select columns are <tt>selectCols</tt>, 
   *  from <tt>tables</tt>, with conditions specified by <tt>ANDs, ORs</tt>, and 
   *  with GROUP BY <tt>groupBy</tt> and ORDER BY <tt>orderBy</tt>.
   *  
   * @example if the desired SELECT query is <pre>
   *    select semester, max(code) from module group by semester order by semester desc
   *  </pre> then 
   *  <tt>selectCols = {"semester", "max(code)"},
   *      tables = {"module"},
   *      ANDs = null,
   *      ORs = null,
   *      groupBy = {"semester"},
   *      orderBy = "order by semester desc" 
   *   </tt>
   */
  private String genSelect(String[] selectCols, String[] tables,
      Expression[] ANDs, Expression[] ORs, String[] groupBy, String orderBy) {
    StringBuffer sb = new StringBuffer("select ");

    for (int i = 0; i < selectCols.length; i++) {
      sb.append(selectCols[i]);
      if (i < selectCols.length - 1) {
        sb.append(",");
      }
    }

    sb.append(" from ");
    for (int i = 0; i < tables.length; i++) {
      String table = tables[i];
      sb.append(table);
      if (i < tables.length - 1)
        sb.append(",");
    }

    if (!((ANDs == null) && (ORs == null))) {
      sb.append(" where ");
      if (ANDs != null) {
        for (int i = 0; i < ANDs.length; i++) {
          sb.append(ANDs[i]);
          if (i < ANDs.length - 1) {
            sb.append(" and ");
          }
        }
      }
      if (ORs != null) {
        for (int i = 0; i < ORs.length; i++) {
          sb.append(ORs[i]);
          if (i < ORs.length - 1) {
            sb.append(" or ");
          }
        }
      }
    }

    if (groupBy != null) {
      sb.append(" group by ");
      int numGb = groupBy.length;
      for (int i = 0; i < numGb; i++) {
        String gbCol = groupBy[i];
        sb.append(gbCol);
        if (i < numGb-1)
          sb.append(",");
      }
    }
    
    if (orderBy != null) {
      sb.append(" ").append(orderBy);
    }

    String sql = sb.toString();
    
    return sql;
  }
  
  /**
   * This method is identical to {@link #genSelect(String[], String[], Expression[], Expression[], String[], String)}, 
   * except for the use of String[] array for ANDs and ORs expressions. 
   * 
   * @effects 
   *  generate and return a SELECT SQL query whose select columns are <tt>selectCols</tt>, 
   *  from <tt>tables</tt>, with conditions specified by <tt>ANDs, ORs</tt>, and 
   *  with GROUP BY <tt>groupBy</tt> and ORDER BY <tt>orderBy</tt>.
   *  
   * @example if the desired SELECT query is <pre>
   *    select semester, max(code) from module group by semester order by semester desc
   *  </pre> then 
   *  <tt>selectCols = {"semester", "max(code)"},
   *      tables = {"module"},
   *      ANDs = null,
   *      ORs = null,
   *      groupBy = {"semester"},
   *      orderBy = "order by semester desc" 
   *   </tt>
   */
  private String genSelect(String[] selectCols, String[] tables,
      String[] ANDs, String[] ORs, String[] groupBy, String orderBy) {
    StringBuffer sb = new StringBuffer("select ");

    for (int i = 0; i < selectCols.length; i++) {
      sb.append(selectCols[i]);
      if (i < selectCols.length - 1) {
        sb.append(",");
      }
    }

    sb.append(" from ");
    for (int i = 0; i < tables.length; i++) {
      String table = tables[i];
      sb.append(table);
      if (i < tables.length - 1)
        sb.append(",");
    }

    if (!((ANDs == null) && (ORs == null))) {
      sb.append(" where ");
      if (ANDs != null) {
        for (int i = 0; i < ANDs.length; i++) {
          sb.append(ANDs[i]);
          if (i < ANDs.length - 1) {
            sb.append(" and ");
          }
        }
      }
      if (ORs != null) {
        for (int i = 0; i < ORs.length; i++) {
          sb.append(ORs[i]);
          if (i < ORs.length - 1) {
            sb.append(" or ");
          }
        }
      }
    }

    if (groupBy != null) {
      sb.append(" group by ");
      int numGb = groupBy.length;
      for (int i = 0; i < numGb; i++) {
        String gbCol = groupBy[i];
        sb.append(gbCol);
        if (i < numGb-1)
          sb.append(",");
      }
    }
    
    if (orderBy != null) {
      sb.append(" ").append(orderBy);
    }

    String sql = sb.toString();
    
    return sql;
  }
  
  /**
   * @requires 
   *  query != null
   *  
   * @effects 
   *  Generate and return a SELECT SQL statement for the id-attribute(s) of <tt>c</tt> whose WHERE clause
   *  contains the SQL expressions translated from those of <tt>q</tt>
   *   
   * @version 3.3
   */
  private String genSelectIdQuery(Class c, Query query) {
    /*
     * Translate query over c into an sql over the table of c
     * example:
     *  c = Student.class 
     *  query = name="Le"
     *  
     *  -> sql = SELECT id from student where name='Le'
     */
    StringBuffer sqlSb = new StringBuffer();
    Collection<String> Select = new ArrayList<String>();
    // v3.3: java.util.Map<Class,TableSpec> From = new HashMap<Class,TableSpec>();
    DualMap<Class,TableSpec> From = new DualMap<>();
    Collection<JoinSpec> Joins = new ArrayList<>();
    Collection<String> Where = null; 
    
    // table var (if any) that refers to the table containing the attrib
    //String tableVar = null;
    final String cTable = dom.getDsm().getDomainClassName(c);
    TableSpec cTableSpec = new TableSpec(c, null, cTable);
    
    // update From, Where from query
    Where = new ArrayList();
    
    /* v3.3: use shared method 
    Iterator<Expression> exps = query.terms();
    Expression exp;
    
    int tableIndex = 0; // the index used to create table vars
    int[] tableInd = {tableIndex};// use array to pass table index
    
    while (exps.hasNext()) {
      exp = exps.next();
      if (exp instanceof ObjectExpression) {
        updateQuery(From, Where, cTableSpec, tableInd, Joins, (ObjectExpression) exp);
      } //v3.3: support AttributeExpression
      else if (exp instanceof AttributeExpression) {
        updateQueryWithAttributeExpression(From, Where, cTableSpec, tableInd, Joins, (AttributeExpression) exp);
      } else {
        // sub-query expression
        Where.add(toSQLExpression(exp,true));
      }
    }
    */
    translateQuery(query, From, Where, cTableSpec, Joins);

    if (cTableSpec.var == null) {//(tableVar == null) {
      // no table var was generated from the update of Where, only use cTable as table name 
      From.put(c, new TableSpec(c, null, cTable));  // cTable
    }
    
    if (debug) System.out.printf("Query class table spec: %s -> %s %n", c.getSimpleName(), cTableSpec.toString());
    
    /*
     * create query
     */
    //StringBuffer orderBy = new StringBuffer(" order by ");

    // select: (the id attribute of c)
    sqlSb.append("select ");
    DAttr dc;
    final List<DAttr> idAttribs = dom.getDsm().getIDDomainConstraints(c);

    // ASSUME: one id attribute (see above) (change this to use the loop (below) if 
    // this restriction is removed)
    dc =  idAttribs.get(0);

    String colName = (cTableSpec.var == null) ? dc.name() : cTableSpec.var + "." + dc.name(); 
    
    sqlSb.append(colName);
    //orderBy.append(colName);
    
    // from 
    StringBuffer FromSb = new StringBuffer(" from ");
    int ind = 0;
    int size = From.size();
    for(TableSpec s : From.values()) {
      FromSb.append(s.toString());
      if (ind < size-1)
        FromSb.append(",");
      ind++;
    }
    sqlSb.append(FromSb);
    
    if (Where != null && !Where.isEmpty()) {
      // add Where to Sql
      sqlSb.append(" where ");
      size = Where.size();
      ind = 0;
      for (String w : Where) {
        sqlSb.append(w);
        if (ind < size-1) {
          sqlSb.append(" and ");
        }
        ind++;
      }
    }
    
    // order by (ascending) 
    //orderBy.append(" ASC ");
    //sqlSb.append(orderBy);
    
    String sql = sqlSb.toString();
    
    if (debug)
      System.out.println("DBToolKit.genIdSelect: " + sql);

    return sql;
  }

  /**
   * @effects 
   *  Generate a CREATE statement for class <code>c</code>.
   *  
   *  <p>Throws NotFoundException if <tt>c</tt> is not a registered domain class or 
   *  required id domain attributes of the class(es) referenced by <tt>c</tt> are not found.
   */
  private String genCreate(final Class c) throws NotPossibleException,
      NotFoundException {
    // v2.6.4.b:
    return genCreate(c, null);
//    // get the declared fields of this class
//    List fields = schema.getDsm().getRelationalAttributes(c);
//
//    if (fields == null)
//      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
//          "Không tìm thấy lớp: {0}", c);
//
//    final String typeName = schema.getDsm().getDomainClassName(c);
//    final Class sup = c.getSuperclass();
//
//    DomainConstraint dc = null;
//    Stack<String> pkeys = new Stack();
//    int fkIndex = 0;
//    Type type;
//    boolean inheritedID = false;
//    String idName = null; // keep track of the id field
//    Class refType = null;
//
//    StringBuffer sb = new StringBuffer("create table ");
//    StringBuffer fks = new StringBuffer();
//
//    // table name is same as class name
//    sb.append(typeName).append("(").append(LF);
//
//    for (int i = 0; i < fields.size(); i++) {
//      Field f = (Field) fields.get(i);
//      String name = f.getName().toLowerCase(); // to lower case
//      
//      // field type is either the native type or
//      // the type specified in the DomainConstraint annotation of the field
//      dc = f.getAnnotation(DC);
//      type = dc.type();
//      inheritedID = false;
//      idName = null;
//
//      if (dc.id()) {
//        // id field, add to stack
//        // could be an inherited id (from the super class)
//        if (f.getDeclaringClass() != c) {
//          inheritedID = true;
//        }
//        idName = name;
//      }
//
//      
//      //TODO: can we ever have a multi-column FK?
//      //DomainConstraint[] dcRefs = null;
//      DomainConstraint dcRef = null;
//      
//      if (!type.isDomainType()) {
//        // non-domain type
//        if (!inheritedID) {
//          // just add column
//          sb.append(name).append(" ").append(javaToDBType(type, dc.length()))
//              .append(",").append(LF);
//        } else {
//          // update FK constraints if this is an inherited id
//          refType = sup;// f.getDeclaringClass();
//          dcRef = dc;
//          //dcRefs = new DomainConstraint[] { dc };
//        }
//      } else {
//        // domain type
//        // get the referenced type and the referenced pk name to use as
//        // the table name for this field
//        
//        //TODO: support id field, whose type is set to another domain class
//        // e.g. CoinQty.coin field has the type Coin
//        if (idName != null) { 
//            // not yet support the above at the moment  
//          throw new NotImplementedException(
//              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//              "Kiểu mã trong là một lớp domain: {0}.{1} {2}", c, f.getType(), idName);            
//        }
//        
//        if (!inheritedID) {
//          refType = f.getType();
//          DomainConstraint[] dcRefs = schema.getDsm().getIDAttributeConstraints(refType);
//          if (dcRefs == null) {
//            throw new NotFoundException(
//                NotFoundException.Code.ID_CONSTRAINT_NOT_FOUND,
//                "Không tìm thấy ràng buộc dạng mã: {0}.{1}: {2}", c.getSimpleName(), name, refType);
//          }
//          
//          if (dcRefs.length>1) {
//            throw new NotImplementedException(
//                NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//                "Mã ngoại gồm nhiều trường (multi-column FK): {0}", refType);            
//          }
//          dcRef = dcRefs[0];
//        } else { // inherited ids also result in FK constraints
//          refType = sup; // f.getDeclaringClass();
//          //dcRefs = new DomainConstraint[] { dc };
//          dcRef = dc;
//        }
//      } 
//
//      // if this field is an FK, then creates its name differently
//      // and also updates fks
//      if (dcRef != null) {
//        // use tablename_col naming convention for FKs
//        // update the fk constraint at the same time...
//        String refTypeName = schema.getDsm().getDomainClassName(refType);
//        //for (DomainConstraint dcRef : dcRefs) {
//          String refTypePK = dcRef.name();
//          // fk column def: e.g. student_id varchar(20),
//          String fkColName;
//          if (!inheritedID)
//            fkColName = name + "_" + refTypePK;
//          else
//            fkColName = refTypePK; // refTypeName.toLowerCase() + "_" +
//                                   // refTypePK;
//
//          sb.append(fkColName).append(" ")
//              .append(javaToDBType(dcRef.type(), dcRef.length())).append(",")
//              .append(LF);
//                    
//          /** only generate FK constraints if the referenced type is not an Enum */
//          if (!refType.isEnum()) {
//            // fk constraint, e.g.: constraint regionstylefk_1 foreign key
//            // (regionid)
//            // references region(id) on delete cascade on update restrict,
//            // v2.6.4.a: added support for dependsOn
//            boolean dependsOn = (inheritedID || schema.getDsm().isDependentOn(c, dc, refType));
//            
//            String fkName = typeName + "fk" + (fkIndex + 1);
//            fks.append("constraint ").append(fkName).append(" foreign key(")
//            .append(fkColName).append(")").append(" references ")
//            .append(refTypeName).append("(").append(refTypePK).append(")")
//            // v2.6.4.a: only add "on delete cascade" if fkColName depends on refTypePK
//            //.append(" on delete cascade")
//            .append((dependsOn) ? " on delete cascade" : "")
//            .append(" on update restrict").append(",")
//            .append(LF);
//
//            fkIndex++;
//          }
//        //}
//      }
//      
//      // if id field then store it 
//      if (idName != null) {
//        pkeys.push(idName);
//      }
//    } // end field loop
//
//
//    // add PK constraint, e.g.: constraint regionstylepk primary
//    // key(regionid,styleid),
//    if (!pkeys.isEmpty()) {
//      String pkName = typeName + "pk"; // table+pk
//      sb.append("constraint ").append(pkName).append(" primary key(");
//      for (String pk : pkeys)
//        sb.append(pk).append(",");
//      sb.delete(sb.length() - 1, sb.length()); // the trailing comma
//      sb.append(")").append(",").append(LF);
//    }
//
//    // add FK constraints
//    if (fks.length() > 0) {
//      // add a trailing comma
//      sb.append(fks);
//    }
//
//    // remove the trailing comma+lf
//    sb.delete(sb.length() - 2, sb.length());
//
//    // close table def
//    sb.append(")");
//
//    return sb.toString();
  }

  /**
   * @modifies 
   *  tableConstraints
   * @effects 
   *  Generate a CREATE statement for class <code>c</code> with the PK constraint(s) but without other table constraints. 
   *  These constraints (e.g. FKs) are added separately to <tt>tableConstraints</tt>.
   *  
   *  <p>Throws NotFoundException if <tt>c</tt> is not a registered domain class or 
   *  required id domain attributes of the class(es) referenced by <tt>c</tt> are not found.
   */
  private String genCreate(final Class c, java.util.Map<String,List<String>> tableConstraints) throws NotPossibleException,
      NotFoundException {
    // get the declared fields of this class
    java.util.Map<Field,DAttr> fields = dom.getDsm().getSerialisableAttributes(c);

    if (fields == null)
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTES_NOT_FOUND,
          new Object[] {c});

    final String tableName = dom.getDsm().getDomainClassName(c);
    // v3.0: use a name prefix similar to tableName but with the dot (.) being replaced by '_'
    final String prefixName = tableName.replaceAll("\\.", "_");
    
    final Class sup = c.getSuperclass();

    DAttr dc = null;
    int fkIndex = 0;
    Type type;
    boolean inheritedID = false;
    String idName = null; // keep track of the id field
    Class refType = null;
    DAttr dcRef;
    
    StringBuffer sb = new StringBuffer("create table ");
    Stack<String> pkeys = new Stack();
    //StringBuffer fks = new StringBuffer();
    StringBuffer fk;
    List<String> fks = new ArrayList();
    
    // table name is same as class name
    sb.append(tableName).append("(").append(LF);
    
    /*v5.0: for (int i = 0; i < fields.size(); i++) {
      Field f = (Field) fields.get(i); */
    int i = -1;
    for (Entry<Field,DAttr> entry : fields.entrySet()) {
      i++;
      Field f = entry.getKey();
      dc = entry.getValue();
      String name = f.getName().toLowerCase(); // to lower case
      
      // field type is either the native type or
      // the type specified in the DomainConstraint annotation of the field
      //dc = f.getAnnotation(DC);
      type = dc.type();
      inheritedID = false;
      idName = null;

      if (dc.id()) {
        // id field, add to stack
        // could be an inherited id (from the super class)
        if (f.getDeclaringClass() != c) {
          inheritedID = true;
        }
        idName = name;
      }

      
      //TODO: can we ever have a multi-column FK?
      //DomainConstraint[] dcRefs = null;
      //DomainConstraint dcRef = null;
      dcRef = null;
      
      if (!type.isDomainType()) {
        // non-domain type
        if (!inheritedID) {
          // just add column
          sb.append(name).append(" ").append(javaToDBType(type, dc.length()))
              .append(",").append(LF);
        } else {
          // update FK constraints if this is an inherited id
          refType = sup;// f.getDeclaringClass();
          dcRef = dc;
          //dcRefs = new DomainConstraint[] { dc };
        }
      } else {
        // domain type
        // get the referenced type and the referenced pk name to use as
        // the table name for this field
        
        //TODO: support id field, whose type is set to another domain class
        // e.g. CoinQty.coin field has the type Coin
        if (idName != null) { 
            // not yet support the above at the moment  
          throw new NotImplementedException(
              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
              new Object[] {String.format("Kiểu mã là một lớp domain: %s.%s %s", 
                  c, f.getType(), idName)});            
        }
        
        if (!inheritedID) {
          refType = f.getType();
          
          List<DAttr> dcRefs = dom.getDsm().getIDDomainConstraints(refType);
          if (dcRefs == null) {
            throw new NotFoundException(
                NotFoundException.Code.ID_CONSTRAINT_NOT_FOUND,
                new Object[] {c.getSimpleName(), name, refType});
          }
          
          if (dcRefs.size()>1) {
            throw new NotImplementedException(
                NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
                new Object[] {String.format(
                    "Mã ngoại gồm nhiều trường (multi-column FK): %s", refType)});            
          }
          dcRef = dcRefs.get(0);

          /*v2.6.4b: additional check: skip if the association between c and refType is 1:1 and 
           * refType's end is the determinant of the association
           */
//          if (schema.getDsm().isDeterminedByAssociate(f) == false) {
//            DomainConstraint[] dcRefs = schema.getDsm().getIDAttributeConstraints(refType);
//            if (dcRefs == null) {
//              throw new NotFoundException(
//                  NotFoundException.Code.ID_CONSTRAINT_NOT_FOUND,
//                  "Không tìm thấy ràng buộc dạng mã: {0}.{1}: {2}", c.getSimpleName(), name, refType);
//            }
//            
//            if (dcRefs.length>1) {
//              throw new NotImplementedException(
//                  NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//                  "Mã ngoại gồm nhiều trường (multi-column FK): {0}", refType);            
//            }
//            dcRef = dcRefs[0];
//          }
        } else { // inherited ids also result in FK constraints
          refType = sup; // f.getDeclaringClass();
          //dcRefs = new DomainConstraint[] { dc };
          dcRef = dc;
        }
      } 

      // if this field is an FK, then creates its name differently
      // and also updates fks
      if (dcRef != null) {
        // use tablename_col naming convention for FKs
        // update the fk constraint at the same time...
        String refTypeName = dom.getDsm().getDomainClassName(refType);
        //for (DomainConstraint dcRef : dcRefs) {
          String refTypePK = dcRef.name();
          // fk column def: e.g. student_id varchar(20),
          String fkColName;
          if (!inheritedID)
            fkColName = name + "_" + refTypePK;
          else
            fkColName = refTypePK; // refTypeName.toLowerCase() + "_" +
                                   // refTypePK;

          sb.append(fkColName).append(" ")
              .append(javaToDBType(dcRef.type(), dcRef.length())).append(",")
              .append(LF);
                    
          /** only generate FK constraints if the referenced type is not an Enum */
          if (!refType.isEnum()) {
            // fk constraint, e.g.: constraint regionstylefk_1 foreign key
            // (regionid)
            // references region(id) on delete cascade on update restrict,
            // v2.6.4.a: added support for dependsOn
            boolean dependsOn = (inheritedID || dom.getDsm().isDependentOn(c, dc, refType));
            
            String fkConsName = prefixName + "fk" + (fkIndex + 1);// v3.0: tableName + "fk" + (fkIndex + 1);
            /* v2.6.4.b: add to tableConstraints
            fks.append("constraint ").append(fkName).append(" foreign key(")
            .append(fkColName).append(")").append(" references ")
            .append(refTypeName).append("(").append(refTypePK).append(")")
            // v2.6.4.a: only add "on delete cascade" if fkColName depends on refTypePK
            //.append(" on delete cascade")
            .append((dependsOn) ? " on delete cascade" : "")
            .append(" on update restrict").append(",")
            .append(LF);
            */
            fk = new StringBuffer();
            fk.append("constraint ").append(fkConsName).append(" foreign key(")
            .append(fkColName).append(")").append(" references ")
            .append(refTypeName).append("(").append(refTypePK).append(")")
            .append((dependsOn) ? " on delete cascade" : "")
            .append(" on update restrict");
            
            fks.add(fk.toString());
            
            fkIndex++;
          }
        //}
      }
      
      // if id field then store it 
      if (idName != null) {
        pkeys.push(idName);
      }
    } // end field loop


    // add PK constraint, e.g.: constraint regionstylepk primary
    // key(regionid,styleid),
    if (!pkeys.isEmpty()) {
      String pkConsName = prefixName + "pk";// v3.0: tableName + "pk"; 
      sb.append("constraint ").append(pkConsName).append(" primary key(");
      for (String pk : pkeys)
        sb.append(pk).append(",");
      sb.delete(sb.length() - 1, sb.length()); // the trailing comma
      sb.append(")").append(",").append(LF);
    }

    // add FK constraints
    /*v2.6.4.b: moved to tableconstraints
    if (fks.length() > 0) {
      // add a trailing comma
      sb.append(fks);
    }
    */
    if (!fks.isEmpty()) {
      if (tableConstraints != null)
        tableConstraints.put(tableName, fks);
      else
        for (String _fk : fks) sb.append(_fk).append(",")
        .append(LF); 
    }

    // remove the trailing comma+lf
    sb.delete(sb.length() - 2, sb.length());

    // close table def
    sb.append(")");

    return sb.toString();
  }
  
  /**
   * @effects 
   *  return the string representation of <tt>SqlType</tt> equivalent of <tt>javaType (length)</tt>
   *  
   *  <p>throws NotFoundException if <tt>javaType</tt> is not supported
   */
  private String javaToDBType(Type javaType, int length) 
      //v3.0: throws NotImplementedException
  throws NotFoundException 
  {
    if (length <= 0)
      length = DEFAULT_LENGTH;

    /*v3.0: move to method
    SqlType sqlType = SqlType.getMapping(javaType);
    */
    DataSourceType sqlType = getDataSourceTypeFor(getDataSourceTypeClass(), javaType);
    
//    if (sqlType == null) {
//      // not supported
//      throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
//          "Không hỗ trợ kiểu dữ liệu: {0}", javaType);
//    } else {
    return sqlType.toString(length);
//    }
  }

  /**
   * Generate an SQL insert statement for a domain object.
   * 
   * @requires <code>o</code> is assignment compatible to <code>c</code> and the
   *           database table of <code>c</code> was created by the SQL statement
   *           generated by the method {@link #genCreate(Class)} using
   *           <code>c</code> as an argument, 
   *           <tt>updateAttributes != null</tt>
   * @modifies <tt>updateAttributes</tt>
   * @effects 
   *  if <tt>c</tt> has domain attributes 
   *    return the SQL <b>prepared</b> INSERT statement for <tt>o</tt>
   *    add to <tt>updateAttributes</tt> the attributes whose values are to be set 
   *  else 
   *    throw <tt>NotFoundException</tt>
   * 
   * @see {@link #genCreate(Class)}
   * */
  private String genParameterisedInsert(Class c, Object o, List<Field> updateAttributes) throws NotFoundException {
    // get the relational attributes of this class
    DSMBasic dsm = dom.getDsm();
    java.util.Map<Field,DAttr> fields = dsm.getSerialisableAttributes(c);

    if (fields == null)
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
          new Object[] { c });

    StringBuffer sb = new StringBuffer("insert into ");
    sb.append(dsm.getDomainClassName(c)).append("(");

    StringBuffer valueSb = new StringBuffer(" values(");
    
    // append values (object values)
    Field f;
    Object v;
    DAttr dc;
//    Type type;  // v2.6.4.b
//    boolean isDeterminedByAssociate;  // v2.6.4.b
    int index=0;
    
    /*v5.0: for (int i = 0; i < fields.size(); i++) {
      f = fields.get(i);
      dc = f.getAnnotation(DC);*/
    int i = -1;
    for (Entry<Field,DAttr> entry : fields.entrySet()) {
      i++;
      f = entry.getKey();
      dc = entry.getValue();
      v = dsm.getAttributeValue(f, o);
      
      if (v != null) {
        // the comma
        if (index > 0) {
          sb.append(",");
          valueSb.append(",");
        }
        
        // column name
        sb.append(
            //v3.0: getColumName(c, f)
            // v3.1: RelationalOSMToolkit.getColumName(dsm, c, f)
            RelationalOSMToolkit.getColumName(this, c, f, null)
            );
        
        // the value
        valueSb.append("?");
        
        // add this to the update attributes 
        updateAttributes.add(f);
        
        index++;
      }
      /*v2.6.4.b: skip if this is determined by associate in a 1:1 association
      type = dc.type();
      isDeterminedByAssociate = (type.isDomainType()) ? 
          schema.getDsm().isDeterminedByAssociate(f) : false;
      if (!isDeterminedByAssociate) {
        v = schema.getDsm().getAttributeValue(f, o);
        
        if (v != null) {
          // the comma
          if (index > 0) {
            sb.append(",");
            valueSb.append(",");
          }
          
          // column name
          sb.append(getColumName(c, f));
          
          // the value
          valueSb.append("?");
          
          // add this to the update attributes 
          updateAttributes.add(f);
          
          index++;
        }
      }
      */
    }

    sb.append(")").append(valueSb).append(")");

    return sb.toString();
  }

  /**
   * @requires <code>c</code> is assignment compatible to <code>o</code>, 
   *          <tt>updateAttributes != null</tt>
   * @modifies updateAttributes
   * @effects 
   *  if <code>c</code> has non-id domain attributes 
   *    return the SQL UPDATE query for <tt>o</tt>
   *    add to <tt>updateAttributes</tt> the <tt>Field</tt> objects representing the attributes whose values
   *    are parameterised in this SQL query 
   *  else if <code>c</code> does not have domain attributes 
   *    throw<code>NotFoundException</code>
   *  else if <code>c</code> does not have non-id domain attributes 
   *    return <code>null</code>.
   */
  private String genParameterisedUpdate(Class c, Object o, List<Field> updateAttributes) throws NotFoundException {
    // get the declared fields of this class
    java.util.Map<Field,DAttr> fields = dom.getDsm().getSerialisableAttributes(c);

    if (fields == null)
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
          new Object[] {c});

    StringBuffer sb = null;
    StringBuffer wsb = null;
    sb = new StringBuffer("update ");
    sb.append(dom.getDsm().getDomainClassName(c)).append(" set ");

    wsb = new StringBuffer(" WHERE ");

    Type type;
    Class domainType;
    String name;
    String[] refNames = null;
    String[] refVals = null;
    Object v = null;
    Object sqlV = null;
    // true if c has an non-ID attribute(s), false if otherwise
    boolean hasNoneID = false;
    boolean inheritedID = false;

    // v2.6.4.b: support 1:1 association 
//    boolean isDeterminedByAssociate; 
    
    // append values (object values)
    /* v5.0: for (int i = 0; i < fields.size(); i++) {
      Field f = (Field) fields.get(i); */
    int i = -1;
    for (Entry<Field,DAttr> entry : fields.entrySet()) {
      i++;
      Field f = entry.getKey();
      //DAttr dc = f.getAnnotation(DC);
      DAttr dc = entry.getValue();
      name = dc.name();
      type = dc.type();
      inheritedID = false;

      List<DAttr> dcFKs = null;
//      isDeterminedByAssociate = type.isDomainType() ? 
//          schema.getDsm().isDeterminedByAssociate(f) : false;  // v2.6.4.b
      
      // get the column name(s)
      if (dc.id()) {
        if ((f.getDeclaringClass() != c)) {
          inheritedID = true;
          refNames = new String[] { name };
        }        
      } 
      
      if (type.isDomainType()) {
        // ref names: depend on whether the attribute is an inherited id or
        // not
        // if it is, then it is prefix with the super-class name; else it
        // is prefixed with the ref table name
        domainType = f.getType();
        dcFKs = dom.getDsm().getIDDomainConstraints(domainType);

        if (!inheritedID) {
          refNames = new String[dcFKs.size()];
          int j=0;
          for (DAttr dcFK : dcFKs) {
              refNames[j] = name + "_" + dcFK.name();
          }
        }
        /* v2.6.4.b: skip if this is determined by associate in a 1:1 association 
        if (!isDeterminedByAssociate) {
          // ref names: depend on whether the attribute is an inherited id or
          // not
          // if it is, then it is prefix with the super-class name; else it
          // is prefixed with the ref table name
          domainType = f.getType();
          dcFKs = schema
              .getIDAttributeConstraints(domainType);

          if (!inheritedID) {
            refNames = new String[dcFKs.length];
            int j=0;
            for (DomainConstraint dcFK : dcFKs) {
                refNames[j] = name + "_" + dcFK.name();
            }
          }
        }        */
      }
      
      // get the id value(s) to use in the WHERE part
      if (dc.id()) {
        v = dom.getDsm().getAttributeValue(f, o);

        // field type is either the native type or
        // the type specified in the DomainConstraint annotation of the field
        if (type.isPrimitive()) {
          sqlV = toSQLString(type, v, false);

          if (inheritedID) {
            refVals = new String[1];
            refVals[0] = (String) sqlV;
          }
        } else if (type.isDomainType()) {
          // ref values: are the values of the id-attributes of the ref
          // attributes
          refVals = new String[dcFKs.size()];
          int j = 0;
          for (DAttr dcFK : dcFKs) {
            sqlV = (v != null) ? dom.getDsm().getAttributeValue(v, dcFK.name())
                : null;
            refVals[j] = toSQLString(dcFK.type(), sqlV, false);
            j++;
          }
        } else {
          throw new NotImplementedException(
              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
              new Object[] { type.name()});
        }
      } // end if id attribute

      if (!dc.id()) { // non-id attributes
        if (!hasNoneID)
          hasNoneID = true;
        // SET part
        // extract non-id attributes
        if (type.isDomainType()) {
          //int j = 0;
          for (String refName : refNames) {
            sb.append(refName).append("=").append("?"
                //refVals[j++]
                    ).append(",");
          }
          /* v2.6.4.b: skip if this is determined by associate in a 1:1 association
          if (!isDeterminedByAssociate) {
            for (String refName : refNames) {
              sb.append(refName).append("=").append("?"
                  //refVals[j++]
                      ).append(",");
            }
          }          */
        } else {
          sb.append(name).append("=").append("?"
              //sqlV
              ).append(",");
        }
        
        // add this attribute to the list
        updateAttributes.add(f);
        /*v2.6.4.b: skip if this is determined by associate in a 1:1 association 
        if (!isDeterminedByAssociate) {
          // add this attribute to the list
          updateAttributes.add(f);
        }*/        
      } else { // WHERE part: id attributes
        if (type.isDomainType() || inheritedID) {
          int j = 0;
          for (String refName : refNames) {
            wsb.append(refName).append("=").append(refVals[j++])
                .append(" and ");
          }
        } else {
          wsb.append(name).append("=").append(sqlV).append(" and ");
        }
      }
    } // end for loop

    // if the class does not have non-id attributes then there is no need to
    // update, return null. Otherwise, return the update SQL
    if (!hasNoneID)
      return null;
    else {
      // remove extra trailing seperators
      sb.delete(sb.length() - 1, sb.length());
      wsb.delete(wsb.length() - 5, wsb.length()); // " and "

      // append WHERE part
      sb.append(wsb);

      return sb.toString();
    }
  }

  /**
   * @effects 
   *  return the name of the table column that is mapped to the attribute named <tt>attribName</tt> 
   *  of the domain class <tt>c</tt> 
   */
  public String getColName(Class c, DAttr attrib) {
    /*v3.1: redirect
    DSMBasic dsm = dom.getDsm();
    Field f = dsm.getDomainAttribute(c, attrib.name());
    //v3.0: return getColumName(c, f);
    return RelationalOSMToolkit.getColumName(dsm, c, f);
    */
    return getColName(c, attrib, null);
  }

  /**
   * @effects 
   *  return the name of the table column that is mapped to the attribute named <tt>attribName</tt>
   *  of the domain class <tt>c</tt> 
   *  and if <tt>func</tt> is specified then that name is updated to include the corresponding 
   *  SQL function 
   *  
   *  @version 3.1
   */
  public String getColName(Class c, DAttr attrib, Function func) {
    DSMBasic dsm = dom.getDsm();
    Field f = dsm.getDomainAttribute(c, attrib.name());
    return RelationalOSMToolkit.getColumName(this, c, f, func);
  }
  
  /**
   * @requires 
   *  fkAttrib describes the foreign-key column /\
   *  refPkAttrib describes the corresponding primary-key column /\ 
   *  table(refPkAttrib) is not a super-type of table(fkAttrib)  
   *  
   * @effects 
   *  return the standard name for the foreign-key column fkAttrib
   *  which references the primary key column refPkAttrib, 
   *  and where the two tables involved are not super-type/sub-type.
   */
  private String getFKColName(DAttr fkAttrib, DAttr refPkAttrib) {
    return (fkAttrib.name().toLowerCase() + "_" + refPkAttrib.name());
  }
  
  // v3.0: moved to toolkit
//  /**
//   * @effects
//   *  return the name of the table column that is mapped to the domain field <tt>f</tt>
//   *  of the domain class <tt>c</tt> (<tt>f</tt> may be declared in the super-class of <tt>c</tt>)
//   * @see #genCreate(Class)
//   */
//  private String getColumName(Class c, Field f) {
//    DomainConstraint dc = f.getAnnotation(DC);
//    Type type = dc.type();
//    
//    String name = f.getName().toLowerCase();
//    // default: colName is same as field name (to lowercase)
//    String colName = name;
//
//    Class refType, sup;
//    boolean inheritedID;
//    DomainConstraint dcRef=null;
//    
//    sup = c.getSuperclass();
//    inheritedID = false;
//
//    if (dc.id() && (f.getDeclaringClass() != c)) {
//      inheritedID = true;
//    }
//    
//    // some exceptions
//    if (!type.isDomainType()) {
//      if (inheritedID) {
//        // this is an inherited id
//        refType = sup;// f.getDeclaringClass();
//        dcRef = dc;
//      }
//    } else {
//      // domain type
//      // get the referenced type and the referenced pk name to use as
//      // the table name for this field
//      if (!inheritedID) {
//        refType = f.getType();
//        DomainConstraint[] dcRefs = dom.getDsm().getIDAttributeConstraints(refType);
//        if (dcRefs == null)
//          throw new ApplicationRuntimeException(null, "No id attributes found for {0}", refType);
//        
//        dcRef = dcRefs[0];
//      } else { // inherited ids also result in FK constraints
//        refType = sup; 
//        dcRef = dc;
//      }
//    } 
//
//    // if this field is an FK, then creates its name differently
//    if (dcRef != null) {
//      // use tablename_col naming convention for FKs
//      String refTypePK = dcRef.name();
//      // fk column def: e.g. student_id
//      if (!inheritedID)
//        colName = name + "_" + refTypePK;
//      else
//        colName = refTypePK;
//    }
//    
//    return colName;
//  }
  
// v3.1: NOT USED  
//  /**
//   * Generate an SQL insert statement for a domain object.
//   * 
//   * @effects If <code>c</code> has domain attributes then returns the SQL
//   *          INSERT statement for <code>o</code>, else throws
//   *          <code>NotFoundException</code>.
//   * 
//   * @requires <code>o</code> is assignment compatible to <code>c</code> and the
//   *           database table of <code>c</code> was created by the SQL statement
//   *           generated by the method {@link #genCreate(Class)} using
//   *           <code>c</code> as an argument.
//   * @see {@link #genCreate(Class)}
//   * @deprecated use {@link #genParameterisedInsert(Class, Object, List)} instead.
//   * */
//  private String genInsert(Class c, Object o) throws NotFoundException {
//    // get the relational attributes of this class
//    // Class c = o.getClass();
//    List fields = dom.getDsm().getSerialisableAttributes(c);
//
//    if (fields == null)
//      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
//          "Không tìm thấy lớp: {0}", c);
//
//    StringBuffer sb = new StringBuffer("insert into  ");
//
//    sb.append(dom.getDsm().getDomainClassName(c));
//
//    sb.append(" values(");
//    Type type;
//    Class domainType;
//    // append values (object values)
//    for (int i = 0; i < fields.size(); i++) {
//      Field f = (Field) fields.get(i);
//      String name = f.getName();
//
//      // field type is either the native type or
//      // the type specified in the DomainConstraint annotation of the field
//      DomainConstraint dc = f.getAnnotation(DC);
//      type = dc.type();
//      // Class type = f.getType();
//      Object v = dom.getDsm().getAttributeValue(f, o);
//
//      //if (type.isString() || type.isPrimitive()) {
//      if (type.isPrimitive()) {
//        sb.append(toSQLString(type, v, false));
//      } else if (type.isDomainType()) {
//        domainType = f.getType();
//        DomainConstraint[] dcFKs = dom.getDsm().getIDAttributeConstraints(domainType);
//        for (DomainConstraint dcFK : dcFKs) {
//          if (v != null)
//            v = dom.getDsm().getAttributeValue(v, dcFK.name());
//          sb.append(toSQLString(dcFK.type(), v, false));
//        }
//      } else {
//        throw new NotImplementedException(
//            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//            "Không hỗ trợ kiểu dữ liệu: {0}", type.name());
//      }
//
//      if (i < fields.size() - 1) {
//        sb.append(",");
//      }
//    }
//
//    sb.append(")");
//
//    return sb.toString();
//  }

// v3.1: NOT USED  
//  /**
//   * @effects If <code>c</code> has non-id domain attributes then returns the
//   *          SQL UPDATE statement for <code>o</code>, else if <code>c</code>
//   *          does not have domain attributes throws
//   *          <code>NotFoundException</code>, else if <code>c</code> does not
//   *          have non-id domain attributes return <code>null</code>.
//   * @requires <code>c</code> is assignment compatible to <code>o</code>
//   * 
//   */
//  private String genUpdate(Class c, Object o) throws NotFoundException {
//    // get the declared fields of this class
//    // Class c = o.getClass();
//    List fields = dom.getDsm().getSerialisableAttributes(c);
//
//    if (fields == null)
//      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
//          "Không tìm thấy lớp: {0}", c);
//
//    StringBuffer sb = null;
//    StringBuffer wsb = null;
//    sb = new StringBuffer("update ");
//    sb.append(dom.getDsm().getDomainClassName(c)).append(" set ");
//
//    wsb = new StringBuffer(" WHERE ");
//
//    Type type;
//    Class domainType;
//    String name;
//    String[] refNames = null;
//    String[] refVals = null;
//    Object v = null;
//    Object sqlV = null;
//    // true if c has an non-ID attribute(s), false if otherwise
//    boolean hasNoneID = false;
//    boolean inheritedID = false;
//
//    // append values (object values)
//    for (int i = 0; i < fields.size(); i++) {
//      Field f = (Field) fields.get(i);
//
//      DomainConstraint dc = f.getAnnotation(DC);
//      name = dc.name();
//      type = dc.type();
//      inheritedID = false;
//
//      // could be an inherited id (from the super class)
//      if (dc.id() && (f.getDeclaringClass() != c)) {
//        inheritedID = true;
//        refNames = new String[] { name };
//      }
//
//      // prepare the attribute value
//      v = dom.getDsm().getAttributeValue(f, o);
//
//      // field type is either the native type or
//      // the type specified in the DomainConstraint annotation of the field
//      //if (type.isString() || type.isPrimitive()) {
//      if (type.isPrimitive()) {
//        sqlV = toSQLString(type, v, false);
//
//        if (inheritedID) {
//          refVals = new String[1];
//          refVals[0] = (String) sqlV;
//        }
//      } else if (type.isDomainType()) {
//        // ref values: are the values of the id-attributes of the ref attributes
//        // ref names: depend on whether the attribute is an inherited id or not
//        // if it is, then it is prefix with the super-class name; else it
//        // is prefixed with the ref table name
//        domainType = f.getType();
//        DomainConstraint[] dcFKs = dom.getDsm().getIDAttributeConstraints(domainType);
//
//        if (!inheritedID) {
//          refNames = new String[dcFKs.length];
//        }
//
//        refVals = new String[dcFKs.length];
//        int j = 0;
//        for (DomainConstraint dcFK : dcFKs) {
//          if (!inheritedID)
//            refNames[j] = name + "_" + dcFK.name();
//          sqlV = (v != null) ? dom.getDsm().getAttributeValue(v, dcFK.name()) : null;
//          refVals[j] = toSQLString(dcFK.type(), sqlV, false);
//          j++;
//        }
//      } else {
//        throw new NotImplementedException(
//            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//            "Tính năng hiện không được hỗ trợ: {0}", type.name());
//      }
//
//      if (!dc.id()) { // non-id attributes
//        if (!hasNoneID)
//          hasNoneID = true;
//        // SET part
//        // extract non-id attributes
//        if (type.isDomainType()) {
//          int j = 0;
//          for (String refName : refNames) {
//            sb.append(refName).append("=").append(refVals[j++]).append(",");
//          }
//        } else {
//          sb.append(name).append("=").append(sqlV).append(",");
//        }
//      } else { // WHERE part: id attributes
//        if (type.isDomainType() || inheritedID) {
//          int j = 0;
//          for (String refName : refNames) {
//            wsb.append(refName).append("=").append(refVals[j++])
//                .append(" and ");
//          }
//        } else {
//          wsb.append(name).append("=").append(sqlV).append(" and ");
//        }
//      }
//    } // end for loop
//
//    // if the class does not have non-id attributes then there is no need to
//    // update, return null. Otherwise, return the update SQL
//    if (!hasNoneID)
//      return null;
//    else {
//      // remove extra trailing seperators
//      sb.delete(sb.length() - 1, sb.length());
//      wsb.delete(wsb.length() - 5, wsb.length()); // " and "
//
//      // append WHERE part
//      sb.append(wsb);
//
//      return sb.toString();
//    }
//  }

  /**
   * @effects 
   *  generate an SQL UPDATE statement for objects of <tt>c</tt> that satisfies the <tt>searchExp</tt> and 
   *  that uses <tt>updateExp</tt> as the update expressions.
   *   
   * @requires a table for <code>c</code> has been created in the data source /\
   * <tt>updateQuery</tt> must not contain valid expressions
   * 
   * @example
   * <pre>
   *  c := Student
   *  searchExp := Student.name ~= "An"
   *  updateExp := Student.sclass = SClass<1c11>
   *  -> SQL: 
   *  update Student 
   *    set sclass_name = "1c11" 
   *    where Student.name like '%An%' 
   * </pre>
   * @pseudocode
   *  let UPDATE := "update c"
   *  
   *  let SET be a string
   *  let WHERE be a set of strings
   *  
   *  let updateExp = (c,A,'=',v)
   *  if A is inherited from an ancestor domain class a,
   *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
   *    let j = SQL_Join(c,x,...,a) (x may = a)
   *    add j to WHERE
   *    SET := "a.A = v"
   *  else 
   *    SET := "A = v"
   *    
   * let searchExp = (c,B,op,v)
   *  if B is inherited from an ancestor domain class b
   *    if b != a AND b has not been processed
   *     let k = SQL_Join(c,y,...,b) (y may = b)
   *     merge k into WHERE
   *    add op(b.B,v) to WHERE
   *  else 
   *    add op(B,v) to WHERE  
   */
  private String genUpdate(Class c, Query<ObjectExpression> searchQuery, Query<ObjectExpression> updateQuery) throws NotFoundException {
    // get the declared fields of this class
    Collection<String> Set = new ArrayList(); 
    Collection<String> Where = new ArrayList(); 
    Collection<String> joinTablePairs = new ArrayList();
    
    final String cTable = dom.getDsm().getDomainClassName(c);
    String tA;
    
    // to record the classes that have been processed
    java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();

    Collection<DAttr> idAttribs;
    String supTable, tsupTable;
    Class sup, currClass;
    String currTable = cTable;
    String colName;
    
    // generate SET
    Iterator<ObjectExpression> updateExps = updateQuery.terms(); 
    ObjectExpression updateExp;
    while (updateExps.hasNext()) {
      updateExp = updateExps.next();
      DAttr A = updateExp.getDomainAttribute();
      Class a = dom.getDsm().getDeclaringClass(c, A);
      
      currTable = cTable;
      currClass = c;
      if (a != c && !processed.containsKey(a)) {
        /*  if A is inherited from an ancestor domain class a,
        *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
        *    if a has not been processed
        *     let j = SQL_Join(c,x,...,a) (x may = a)
        *     add j to WHERE
        *    SET := "a.A = v"
        *  else 
        *    SET := "A = v"
        */
        sup = dom.getDsm().getSuperClass(c);
        do {
          supTable = dom.getDsm().getDomainClassName(sup);
  
          // use the id attributes to add new join expressions
          idAttribs = dom.getDsm().getIDDomainConstraints(sup);
          for (DAttr f : idAttribs) { // current table
            // add join expressions between the id attributes of the two tables
            Where.add(join(currClass, currTable, sup, supTable, f));//currTable + "." + f.name() + "=" + supTable + "." + f.name());
            
            joinTablePairs.add(currTable+"-"+supTable);
          } // end for
  
          // recursive: check the super-super class and so on...
          currTable = supTable;
          currClass = sup;
          sup = dom.getDsm().getSuperClass(sup);
        } while (sup != null);
        
        // attribute table is the last super class name
        tA = currTable;
        processed.put(a, tA);
      } else if (a == c) {
        // same as c
        tA = cTable;
      } else {  // a already processed
        tA = processed.get(a);
      } // end if 
      
      // Add attrib to SET
      colName = getColName(a,A);
      /*v3.1: SET clause NEEDS NOT have table prefix (assumes column belongs to a table in 
       the UPDATE clause). Thus if tA is different from cTable then instead must use tA in  
       the UPDATE clause:
       
      colName = tA+"."+colName;
       */
      Set.add(toSQLExpression(updateExp, colName,false));
    }
    
    // update Where with searchQuery
    if (searchQuery != null) {
      ObjectExpression searchExp;
      Iterator<ObjectExpression> searchExps = searchQuery.terms();
      while (searchExps.hasNext()) {
        searchExp = searchExps.next();
         /*  
          * let searchExp = (c,B,op,v)
          *  if B is inherited from an ancestor domain class b
          *    if b != c AND b has not been processed
          *     let k = SQL_Join(c,y,...,b) (y may = b)
          *     merge k into WHERE
          *    add op(b.B,v) to WHERE
          *  else 
          *    add op(B,v) to WHERE    
          */
        Class b;
        String tB;
        idAttribs = dom.getDsm().getIDDomainConstraints(c);
        
        DAttr B = searchExp.getDomainAttribute();
        b = dom.getDsm().getDeclaringClass(c, B);
        if (b != c && !processed.containsKey(b)) {
          sup = dom.getDsm().getSuperClass(c);
          currTable = cTable;
          currClass = c;
          do {
            supTable = dom.getDsm().getDomainClassName(sup);

            // merge Join into WHERE
            if (!joinTablePairs.contains(currTable+"-"+supTable)) {
              // use the id attributes to add new join expressions
              idAttribs = dom.getDsm().getIDDomainConstraints(sup);
              for (DAttr f : idAttribs) { // current table
                // add join expressions between the id attributes of the two tables
                Where.add(join(currClass, currTable, sup, supTable, f));
                
                joinTablePairs.add(currTable+"-"+supTable);
              } // end for
            }
            
            // recursive: go up to the super-super class and so on...
            currTable = supTable;
            currClass = sup;
            sup = dom.getDsm().getSuperClass(sup);
          } while (sup != null);
          
          tB = currTable;
          processed.put(b, currTable);
        } else if (b == c) {
          tB = cTable;
        } else {  // b already processed
          tB = processed.get(b);
        }  // end if
        colName = getColName(b, B);
        colName = tB + "."+colName;
        Where.add(toSQLExpression(searchExp, colName,true));
      } // end while
    } // end if
    
    
    // generate SQL
    StringBuffer sqlb = new StringBuffer();
    
    sqlb.append("update ").append(cTable);
    
    sqlb.append(" set ");
    int i = 0;
    for (String set : Set) {
      sqlb.append(set);
      if (i < Set.size()-1) sqlb.append(", ");
      i++;
    }
    
    if (!Where.isEmpty()) {
      sqlb.append(" where ");
      i = 0;
      for (String w : Where) {
        sqlb.append(w);
        if (i < Where.size()-1) sqlb.append(" and ");
        i++;
      }
    }
    
    return sqlb.toString();
  }
  
  /**
   * @effects if the domain class <code>c</code> has domain attributes then
   *          returns the SQL DELETE statement for domain object <code>o</code>
   *          from the database table of <code>c</code>, else throws
   *          <code>NotFoundException</code>
   */
  private String genDelete(Class c, Object o) throws NotFoundException {
    // get the declared fields of this class
    // Class c = o.getClass();
    java.util.Map<Field,DAttr> fields = dom.getDsm().getSerialisableAttributes(c);

    if (fields == null)
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
          new Object[] { c } );

    StringBuffer sb = null;
    sb = new StringBuffer("delete from ");
    sb.append(dom.getDsm().getDomainClassName(c)).append(" WHERE ");

    Type type;
    Class domainType;
    String name;
    String[] refNames = null;
    Object v = null;
    Object sqlV = null;

    // append values (object values)
    /* v5.0: for (int i = 0; i < fields.size(); i++) {
      Field f = (Field) fields.get(i); */
    int i = -1;
    for (Entry<Field,DAttr> entry : fields.entrySet()) {
      i++;
      Field f = entry.getKey();
      //DAttr dc = f.getAnnotation(DC);
      DAttr dc = entry.getValue();
      if (dc.id()) { // only interested in the id attributes
        name = dc.name();
        type = dc.type();
        // prepare the attribute value
        v = dom.getDsm().getAttributeValue(f, o);
        // field type is either the native type or
        // the type specified in the DomainConstraint annotation of the field
        //if (type.isString() || type.isPrimitive()) {
        if (type.isPrimitive()) {
          sb.append(name).append("=").append(toSQLString(type, v, false)) // v is never null
              .append(" and ");
        } else if (type.isDomainType()) {
          domainType = f.getType();
          List<DAttr> dcFKs = dom.getDsm().getIDDomainConstraints(domainType);
          refNames = new String[dcFKs.size()];
          int j = 0;
          for (DAttr dcFK : dcFKs) {
            refNames[j] = dcFK.name();
            sqlV = dom.getDsm().getAttributeValue(v, dcFK.name());
            sb.append(name + "_" + dcFK.name()).append("=")
                .append(toSQLString(dcFK.type(), sqlV,false)).append(" and ");
          }
        } else {
          throw new NotImplementedException(
              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
              new Object[] { type.name()});
        }
      }
    } // end for loop

    // remove extra trailing seperators
    sb.delete(sb.length() - 5, sb.length()); // " and "

    return sb.toString();
  }

  
  /**
   * @effects 
   *  generate an SQL DELETE statement for objects of <tt>c</tt> that satisfies the expressions in 
   *  <tt>searchQuery</tt>
   *   
   * @requires a table for <code>c</code> has been created in the data source
   * 
   * @example
   * <pre>
   *  c := Student
   *  searchExp := Student.name ~= "An"
   *  -> SQL: 
   *  delete from Student 
   *    where Student.name like '%An%' 
   * </pre>
   * @pseudocode
   * <pre>
   *  let DELETE := "delete from c"
   *  
   *  let WHERE be a set of strings
   *  
   *  for each searchExp in searchQuery.terms
   *    let searchExp = (c,B,op,v)
   *    if B is inherited from an ancestor domain class b
   *      if b has not been processed
   *       let k = SQL_Join(c,y,...,b) (y may = b)
   *       merge k into WHERE
   *      add op(b.B,v) to WHERE
   *    else 
   *      add op(B,v) to WHERE
   *      </pre>  
   */
  private String genDelete(Class c, Query<ObjectExpression> searchQuery) throws NotFoundException {
    Collection<String> Where = new ArrayList(); 
    Collection<String> joinTablePairs = new ArrayList();
    
    final String cTable = dom.getDsm().getDomainClassName(c);
    String tA;
    
    // to record the classes that have been processed
    java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();

    Collection<DAttr> idAttribs;
    String supTable, tsupTable;
    Class sup, currClass;
    String currTable = cTable;
    String colName;
    
    // update Where with searchQuery
    if (searchQuery != null) {
      ObjectExpression searchExp;
      Iterator<ObjectExpression> searchExps = searchQuery.terms();
      while (searchExps.hasNext()) {
        searchExp = searchExps.next();
         /*  
          * let searchExp = (c,B,op,v)
          *  if B is inherited from an ancestor domain class b
          *    if b != c AND b has not been processed
          *     let k = SQL_Join(c,y,...,b) (y may = b)
          *     merge k into WHERE
          *    add op(b.B,v) to WHERE
          *  else 
          *    add op(B,v) to WHERE    
          */
        Class b;
        String tB;
        idAttribs = dom.getDsm().getIDDomainConstraints(c);
        
        DAttr B = searchExp.getDomainAttribute();
        b = dom.getDsm().getDeclaringClass(c, B);
        if (b != c && !processed.containsKey(b)) {
          sup = dom.getDsm().getSuperClass(c);
          currTable = cTable;
          currClass = c;
          do {
            supTable = dom.getDsm().getDomainClassName(sup);

            // merge Join into WHERE
            if (!joinTablePairs.contains(currTable+"-"+supTable)) {
              // use the id attributes to add new join expressions
              idAttribs = dom.getDsm().getIDDomainConstraints(sup);
              for (DAttr f : idAttribs) { // current table
                // add join expressions between the id attributes of the two tables
                Where.add(join(currClass, currTable, sup, supTable, f));
                
                joinTablePairs.add(currTable+"-"+supTable);
              } // end for
            }
            
            // recursive: go up to the super-super class and so on...
            currTable = supTable;
            currClass = sup;
            sup = dom.getDsm().getSuperClass(sup);
          } while (sup != null);
          
          tB = currTable;
          processed.put(b, tB);
        } else if (b == c) {
          tB = cTable;
        } else {  // b already processed
          tB = processed.get(b);
        }  // end if
        colName = getColName(b, B);
        colName = tB + "."+colName;
        Where.add(toSQLExpression(searchExp, colName,true));
      } // end while
    } // end if
    
    
    // generate SQL
    StringBuffer sqlb = new StringBuffer();
    
    sqlb.append("delete from ").append(cTable);
    
    if (!Where.isEmpty()) {
      sqlb.append(" where ");
      int i = 0;
      for (String w : Where) {
        sqlb.append(w);
        if (i < Where.size()-1) sqlb.append(" and ");
        i++;
      }
    }
    
    return sqlb.toString();  
  }
  
  /**
   * @effects 
   *  return an SQL Inner Join between tables <tt>t1</tt> (of the class <tt>c1</tt>) and 
   *  table <tt>t2</tt> (of the class <tt>c2</tt>) on the join attribute <tt>joinAttrib</tt>.
   */
  private String join(Class c1, String t1, Class c2, String t2, DAttr joinAttrib) {
    String col1, col2;
    
    col1 = getColName(c1,joinAttrib);
    col2 = getColName(c2,joinAttrib);
    
    return join(t1, col1, t2, col2); 
    //t1 + "." + col1 + "=" + t2 + "." + col2;
  }

  /**
   * @effects 
   *  return an SQL Inner Join between tables <tt>t1</tt> (of the class <tt>c1</tt>) and 
   *  table <tt>t2</tt> (of the class <tt>c2</tt>) on the join attribute <tt>joinAttrib</tt>.
   */
  private String join(String t1, String col1, String t2, String col2) {
    return t1 + "." + col1 + "=" + t2 + "." + col2;
  }

  /**
   * @effects invokes {@link #toDBColumnName(Class, String, boolean)}, with the
   *          second argument being set to
   *          <code>attributeConstraint.name()</code>.
   */
  public String toDBColumnName(Class c, DAttr attributeConstraint,
      boolean withTablePrefix) throws NotFoundException {
    return toDBColumnName(c, attributeConstraint.name(), withTablePrefix);
  }

  /**
   * @effects returns the precise table column name corresponds to the domain
   *          attribute <code>attribute </code> of the domain class
   *          <code>c</code>.
   * 
   *          <p>
   *          If <code>attribute</code> is a non-domain-type then it is the same
   *          as <code>attribute.name</code>, else it is of the form
   *          <code>type.name_attribute.name</code> where <code>type</code> is
   *          the type of the attribute.
   * 
   *          <p>
   *          If <code>withTablePrefix=true</code> then the attribute name is
   *          prefixed with the table prefix, such as <code>Student.id</code>.
   * 
   *          <p>
   *          Throws <code>NotFoundException</code> if the required identifier
   *          domain constraints of the associated domain type are not found.
   */
  public String toDBColumnName(Class c, String attribute,
      boolean withTablePrefix) throws NotFoundException {
    String colName;
    Field f = dom.getDsm().getDomainAttribute(c, attribute);
    DAttr dc = f.getAnnotation(DC);

    if (dc.type().isDomainType()) {
      Class domainType = f.getType();
      List<DAttr> idcs = dom.getDsm().getIDDomainConstraints(domainType);
      if (idcs == null) {
        throw new NotFoundException(
            NotFoundException.Code.CONSTRAINT_NOT_FOUND,
            "Không tìm thấy ràng buộc dữ liệu: {0}", domainType);
      }

      if (idcs.size() > 1) {
        throw new NotImplementedException(
            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
            "Tính năng hiện không được hỗ trợ: {0}", "compoundKey("
                + domainType + ")");
      }

      // a combination of the type-name and the name of its id attribute
      colName = dc.name() + "_" + idcs.get(0).name();
    } else {
      colName = dc.name();
    }

    if (withTablePrefix) {
      String tableName = dom.getDsm().getDomainClassName(c);
      colName = tableName + "." + colName;
    }

    return colName;
  }

  /**
   * @requires 
   *  v != null -> type != null
   *  
   * @effects
   *  return the SQL string equivalent of value <tt>v</tt> whose data type is <tt>type</tt>
   *  
   */
  /*v2.7.2: added isSelectQuery to correctly return value for two types of SQL statements*/
  private String toSQLString(Type type, Object v, boolean isSelectQuery) {
    if (v == null) {
      if (isSelectQuery)
        return null;  // remain as null
      else
        return "DEFAULT"; // update query: use DEFAULT
    } else {
      if (type.isString() || type.isBoolean()) {
        return ("'" + v + "'");
      } else { // if (type.isPrimitive()) {
        return (v + "");
      }
    }
  }
  
  /**
   * @effects 
   *  convert object expression's operator to <tt>SqlOp</tt>
   */
  private SqlOp toSQLOperator(Op op) {
    SqlOp[] sqlOps = SqlOp.values();
    for (SqlOp sqlOp : sqlOps) {
      if (sqlOp.getMapping() == op) {
        return sqlOp;
      } 
    }
    
    // something wrong, no mapping defined
    throw new InternalError("DBToolKit.toSQLOperator: no SQL conversion defined for " + op);
  }

//v3.0  
//  /**
//   * @effects 
//   *  if <tt>func</tt> is supported by SQL 
//   *    return the equivalent SQL function
//   *  else
//   *    throws NotImplementedException
//   */
//  private SqlFunction toSQLFunction(Function func) {
//    SqlFunction[] sqlFuncs = SqlFunction.values();
//    
//    for (SqlFunction sqlf : sqlFuncs) {
//      if (sqlf.getMapping() == func) {
//        return sqlf;
//      }
//    }
//    
//    
//    // something wrong, no mapping defined
//    throw new InternalError("DBToolKit.toSQLFunction: no SQL conversion defined for " + func);
//  }
  
  /**
   * @effects 
   *  converts exp to an equivalent SQL expression and return it
   *  
   * @version 
   * - 3.1: updated <br>
   * - 3.3: improved to support {@link AttributeExpression} 
   */
  public String toSQLExpression(Expression exp, boolean isSelectQuery) throws NotFoundException {
    /* v3.1: moved code from the other method 
    return toSQLExpression(exp, null, isSelectQuery);
    */
    
    /*Special conversions for special-types of expressions*/
    if (exp instanceof ObjectExpression) {
      return toSQLExpression((ObjectExpression) exp, 
          //variable,
          null,
          isSelectQuery);
    } else if (exp instanceof IdExpression) {
      return toSQLExpression((IdExpression) exp, 
          //variable, 
          null,
          isSelectQuery);
    } // v3.3 
    else if (exp instanceof AttributeExpression) {
      return toSQLExpression((AttributeExpression) exp, 
          // colVar1
          null, 
          // colVar2
          null, 
          isSelectQuery);
    }

    //TODO: v2.6.4b: to be removed ???
    
    // conversion for conventional expressions (not of any of the special types above)
    //String var = (variable != null) ? variable : exp.getVar();
    String var = exp.getVar();
    Object val = exp.getVal();
    Expression.Type type = exp.getType();
    Op op = exp.getOperator();
    
    Object v;
    if (val == null) {
      //TODO: This only works for Insert/Update; not for Select !!!!??????
      v = "DEFAULT";
    } else 
      v = val;
    
    //TODO: revise this: check and process attribute functor first because
    // that will be used in the var
    if (type.equals(Expression.Type.Data)) {
      // convert values
      if (val instanceof Number) {
        // numeric expression
        if (op == Op.MATCH)
          return var + "=" + v;
        else
          return var + op.getName() + v;
      } else if (op == Op.MATCH) {
          // approximate matching
          return var + " like '%" + v + "%'";
      } 
      // TODO: add special translations for other cases here
      else {
        return var + op.getName() + " '" + v + "'";
      }
    } else if (type.equals(Expression.Type.Nested)) {
      // add a pair of brackets
      // IMPORTANT: extra spaces around the operator 
      return var + " " + op.getName() + " (" + v + ")";
    } else { // other types
      // keep the same
      return var + op.getName() + v;
    }  
  }

// v3.1:  
//  /**
//   * @effects 
//   *  if variable != null
//   *    converts exp to an equivalent SQL expression using variable as the alternative variable name (where appropriate)
//   *  else 
//   *    converts exp to an equivalent SQL expression exp.var as the variable name (where appropriate)
//   */
//  private String toSQLExpression(Expression exp, String variable, boolean isSelectQuery) throws NotFoundException {
//
//    /*Special conversions for special-types of expressions*/
//    if (exp instanceof ObjectExpression) {
//      return toSQLExpression((ObjectExpression) exp, variable, isSelectQuery);
//    } else if (exp instanceof IdExpression) {
//      return toSQLExpression((IdExpression) exp, variable, isSelectQuery);
//    }
//
//    //TODO: v2.6.4b: to be removed
//    
//    // conversion for conventional expressions (not of any of the special types above)
//    String var = (variable != null) ? variable : exp.getVar();
//    Object val = exp.getVal();
//    Expression.Type type = exp.getType();
//    Expression.Op op = exp.getOperator();
//    
//    Object v;
//    if (val == null) {
//      //TODO: This only works for Insert/Update; not for Select !!!!??????
//      v = "DEFAULT";
//    } else 
//      v = val;
//    
//    //TODO: revise this: check and process attribute functor first because
//    // that will be used in the var
//    if (type.equals(Expression.Type.Data)) {
//      // convert values
//      if (val instanceof Number) {
//        // numeric expression
//        if (op == Expression.Op.MATCH)
//          return var + "=" + v;
//        else
//          return var + op.getName() + v;
//      } else if (op == Expression.Op.MATCH) {
//          // approximate matching
//          return var + " like '%" + v + "%'";
//      } 
//      // TODO: add special translations for other cases here
//      else {
//        return var + op.getName() + " '" + v + "'";
//      }
//    } else if (type.equals(Expression.Type.Nested)) {
//      // add a pair of brackets
//      // IMPORTANT: extra spaces around the operator 
//      return var + " " + op.getName() + " (" + v + ")";
//    } else { // other types
//      // keep the same
//      return var + op.getName() + v;
//    }  
//  }
  
  /**
   * @requires 
   *  variable != null -> variable is a fully-qualified table column variable (format: t.a) of oexp.attribute
   *  
   * @effects 
   *  Convert <tt>oexp</tt> into equivalent SQL expression and return it 
   *   
   * @version 
   * - 3.1: FIXED use variable for the case that expression's attribute has a Domain type <br>
   * - 3.2: improved to support the use of nested query for domain-typed attribute
   */
  private String toSQLExpression(ObjectExpression oexp, String variable, boolean isSelectQuery) throws NotFoundException {
    String var = (variable != null) ? variable : oexp.getVar();
    Object val = oexp.getVal();
    //Expression.Type type = exp.getType();
    Op op = oexp.getOperator();
    
    SqlOp sqlOp = toSQLOperator(op);
    
    // v3.1: ObjectExpression oexp = (ObjectExpression) exp;
    AttribFunctor attribFunctor = oexp.getAttributeFunctor();
    DAttr attrib = oexp.getDomainAttribute();
    
    /*two cases: domain-type val and expression with attribute functor */
    if (attribFunctor != null) {
      // attribute functor is specified (attribute's type must not be a domain type)
      // convert the specified function into an equivalent SQL function over the attribute
      Function attribFunc = attribFunctor.function();
      DataSourceFunction sqlFunc = 
          //v3.0: toSQLFunction(attribFunctor.function());
          getDataSourceFunctionFor(this.getDataSourceFunctionClass(), attribFunc);
      
      return sqlFunc.toString(var) +
          sqlOp.toString(this, // v3.0 
              attrib.type(), val, isSelectQuery);
    } else if (attrib.type().isDomainType()
        /*v3.2: added this check to support nested query */
        && !(val instanceof Query)
        ) {
      // domain-type (referenced) val -> get the domain id of val to use
      
      Object refIdVal = null; 
      DAttr refIdAttrib = null;
      Class cls = null; Oid refId;
      /* v3.1: FIXED added a check to use variable if it is specified (because this variable is already determined 
       * from oexp.domainClass and oexp.attribute)
      var = getColName(oexp.getDomainClass(), attrib);
      */
      if (variable == null)
        var = getColName(oexp.getDomainClass(), attrib);

      if (val != null) {
        if (val instanceof Object[]) {
          // value array
          Object[] valArray = (Object[]) val;
          Object[] refIdValArray = new Object[valArray.length];
          int i = 0;
          for (Object o : valArray) {
            if (cls == null) cls = o.getClass();
            refId = dom.lookUpObjectId(cls, o);
            if (refId == null) {
              // should not happen
              throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NOT_FOUND, 
                  new Object[] {cls.getSimpleName(), o});
              
            }
            
            if (refId.size() > 1) {
              throw new NotImplementedException(NotImplementedException.Code.OBJECT_ID_NOT_SUPPORTED, 
                  new Object[] {refId});
            }
            
            if (refIdAttrib == null) refIdAttrib = refId.getIdAttribute(0);
            refIdValArray[i] = refId.getIdValue(0);
            i++;
          }
          refIdVal = refIdValArray;
        } else {
          // single value
          cls = val.getClass();
          // val is a (ref) domain object
          refId = dom.lookUpObjectId(cls, val);
          if (refId == null) {
            // should not happen
            throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NOT_FOUND, 
                new Object[] {cls.getSimpleName(), val});
            
          }
          
          if (refId.size() > 1) {
            throw new NotImplementedException(NotImplementedException.Code.OBJECT_ID_NOT_SUPPORTED, 
                new Object[] {refId});
          }
          
          refIdAttrib = refId.getIdAttribute(0);
          refIdVal = refId.getIdValue(0);
          /* v3.3: this does not work b/c we donot know which attribute of cls to use for val
          DSMBasic dsm = dom.getDsm();
          cls = dsm.getDomainClassFor(oexp.getDomainClass(), attrib);
          //cls = val.getClass();
          if (cls.isInstance(val)) { //v3.3: added this check to support the case that val is already the refIdVal
            // val is a (ref) domain object
            refId = dom.lookUpObjectId(cls, val);
            if (refId == null) {
              // should not happen
              throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NOT_FOUND, 
                  new Object[] {cls.getSimpleName(), val});
              
            }
            
            if (refId.size() > 1) {
              throw new NotImplementedException(NotImplementedException.Code.OBJECT_ID_NOT_SUPPORTED, 
                  new Object[] {refId});
            }
            
            refIdAttrib = refId.getIdAttribute(0);
            refIdVal = refId.getIdValue(0);
          } else {  
            // val not a ref domain object; assumed to be refIdVal
            refIdAttrib = dsm.getIDDomainConstraint(cls);
            refIdVal = val;
          }
          */
        }
      }
      
      return var + 
          sqlOp.toString(this, // v3.0 
              (refIdAttrib != null) ? refIdAttrib.type() : null,refIdVal, isSelectQuery);
    } 
    // add support for other special cases here
    else {
      // process as a normal expression
//      throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
//          "Không hỗ trợ tính năng {0}", "Object expression: " + oexp);
      return var + 
          /*v2.7.2: moved op's toString to SqlOp class
          sqlOp.toString(toSQLString(attrib.type(), val, isSelectQuery)); */
          sqlOp.toString(this, // v3.0 
              attrib.type(), val, isSelectQuery);
    }
  }
  
  /**
   * @effects 
   *  Convert <tt>exp</tt> into equivalent SQL expression and return it 
   */
  private String toSQLExpression(IdExpression exp, String variable, boolean isSelectQuery) throws NotFoundException {
    String var; //= (variable != null) ? variable : exp.getVar();
    //Object val = exp.getVal();
    //Expression.Type type = exp.getType();
    Op op = exp.getOperator();
    SqlOp sqlOp = toSQLOperator(op);

    // use the id value directly (without the need to lookup)
    IdExpression iexp = (IdExpression) exp;
    Oid refId = iexp.getVal();
    if (refId.size() > 1) {
      throw new NotImplementedException(NotImplementedException.Code.OBJECT_ID_NOT_SUPPORTED, 
          "Không hỗ trợ mã đối tượng {0}", refId);
    }
    
    DAttr refIdAttrib = refId.getIdAttribute(0);
    Object refIdVal = refId.getIdValue(0);
  
    //TODO: it seems that variable is not used here?
    var = getColName(iexp.getDomainClass(), iexp.getDomainAttribute());
    return var + 
        /*v2.7.2: moved op's toString to SqlOp class
          sqlOp.toString(toSQLString(refIdAttrib.type(),refIdVal,isSelectQuery)); */
        sqlOp.toString(this, // v3.0 
            refIdAttrib.type(), refIdVal, isSelectQuery);
  }
  
  /**
   * @requires 
   *  <tt>colVar1, colVar2</tt> are table column variables of the two attributes in <tt>exp</tt>
   *  
   * @effects <pre>
   *  if colVar1 == null
   *    colVar1 = exp.c1 + "." + exp.attrib1.name()
   *  
   *   if (colVar2 == null
   *    colVar2 = exp.c2 + "." + exp.attrib2.name()
   *    
   *  Use colVar1, colVar2 to convert <tt>exp</tt>  into equivalent SQL expression and return it
   *  
   *  </pre>
   * @version 3.3
   */
  private String toSQLExpression(AttributeExpression exp, String colVar1, String colVar2, boolean isSelectQuery) throws NotFoundException {
    Op op = exp.getOperator();
    SqlOp sqlOp = toSQLOperator(op);

    String var1, var2;
    if (colVar1 == null) {
      Class class1 = exp.getClass1();
      DAttr attrib1 = exp.getAttrib1();
      var1 = getColName(class1, attrib1);      
    } else {
      var1 = colVar1;
    }
    
    if (colVar2 == null) {
      Class class2 = exp.getClass2();
      DAttr attrib2 = exp.getAttrib2();
      var2 = getColName(class2, attrib2);
    } else {
      var2 = colVar2;
    }
    
    return sqlOp.toString(this, var1, var2, isSelectQuery);
  }


// v3.0  
//  /**
//   * @requires 
//   *  exp is a query term whose value's type is binary (e.g. image)
//   * @effects 
//   *  converts the operator of exp to the equivalent SQL operator
//   */
//  //TODO: revise this method, it is currently only does the basic job  
//  private String getSQLOpForBinaryType(Expression exp) throws NotFoundException {
//    return "=";
//  }
  
  /**
   * determine DDL scripts to alter the table structure of c
   * @effects
   * <pre>
   *  get mapping of f
   *  if mapping not exists then create new column
   *  else get changes of mapping as opposed to dc
   *    generate sql for these changes & execute against the db
   * </pre>
   */
  @Override
  public void updateDataSourceSchema(Class c, DAttr dc,
      int fieldIndex, String oldFieldName, 
      java.util.Map<DAttr, Object> changedMappingAttribVals)
      throws DataSourceException {
    List<String> sqls = new ArrayList<>();

    DSMBasic dsm = dom.getDsm();
    String tableName = dsm.getDomainClassName(c);
    String template = "ALTER TABLE " + tableName + " \n %s";
    String sql = null;

    if (changedMappingAttribVals == null) {
      // add new column
      sqls.addAll(genSQLAddColumn(tableName, dc));

    } else {
      // update mapping changes
      DAttr attrib;
      Object val = null;
      String fieldName = dc.name();

      for (Entry<DAttr, Object> e : changedMappingAttribVals.entrySet()) {
        attrib = e.getKey();
        val = e.getValue();

        switch (attrib.name()) {
        case "fieldName":
          // get mapping for old fieldName
//          Mapping mapping = (Mapping) dom.lookUpObjectByID(Mapping.class,
//              tableName + "_" + fieldIndex);

          sqls.add(String.format("RENAME COLUMN %s.%s TO %s", tableName,
              //mapping.getFieldName(),
              oldFieldName,
              fieldName));
          break;
        case "serialisable":
          if ((Boolean) val) {
            sqls.addAll(genSQLAddColumn(tableName, dc));
          } else {
            // TODO: what if pkey, fkey
            sql = "DROP COLUMN " + fieldName + " CASCADE";
          }
          break;
        case "id":
          if ((Boolean) val) {
            sql = "ADD PRIMARY KEY (" + fieldName + ")";
          } else {
            sql = "DROP PRIMARY KEY";
          }

          break;
        case "type":
        case "maxLength":
          sql = String.format("ALTER COLUMN %s SET DATA TYPE %s", fieldName,
              getDataSourceType(dc) // type with length
              );
          break;
        case "autoIncrement":
          if ((Boolean) val) {
            sql = String.format("GENERATED ALWAYS AS IDENTITY (start with 1, increment by 1)", fieldName,
                getDataSourceType(dc));
          } else {
            sql = String.format("MODIFY %s %s", fieldName,
                getDataSourceType(dc));
          }
          break;

        case "isUnique":
          if ((Boolean) val) {
            sql = String.format("ADD CONSTRAINT unique_%s UNIQUE (%s)",
                fieldName, fieldName);
          } else {
            sql = String.format("DROP UNIQUE unique_%s", fieldName);
          }

          break;
        case "isOptional":
          sql = String.format("ALTER COLUMN %s %s", fieldName,
              (dc.optional()) ? "NULL" : "NOT NULL");
          break;
        case "defaultValue":
          if (val.equals(CommonConstants.NullString)) {
            sql = String.format("ALTER COLUMN %s DROP DEFAULT", fieldName);
          } else {
            sql = String.format("ALTER COLUMN %s SET DEFAULT '%s'\n",
                fieldName, val);
          }
          break;
        }

        if (sql != null) {
          sql = String.format(template, sql);
          sqls.add(sql);
        }
      }
    }

    // execute queries update
    if (conn != null) {
      for (int i = 0; i < sqls.size(); i++) {
        String tmp = sqls.get(i);

        if (debug)
          System.out.println("\n" + tmp);

        try {
          executeUpdate(tmp);
        } catch (Exception e) {
          System.err.println(e.getMessage());

          System.out.println("Warning: all table data rows are deleted!");
          // drop data rows
          executeUpdate("DELETE FROM " + tableName);
          // dom.retrieveObjectsWithAssociations(c);
          // dom.deleteObjects(c, true);
          
          // try to run again
          executeUpdate(tmp);
        }
      }
    }
  }

  /**
   * v 2.7.3 congnv returns sql statement for add column as specified by
   * <tt>dc</tt>
   * 
   * @param dc
   */
  public List<String> genSQLAddColumn(String tableName, DAttr dc) {
    List<String> sqls = new ArrayList<>();

    if (!dc.optional()) {
      if (!dc.defaultValue().equals(CommonConstants.NullString)) {
        sqls.add(String.format("ALTER TABLE %s ADD COLUMN %s %s NOT NULL %s DEFAULT '%s'",
            tableName, dc.name(), getDataSourceType(dc), 
            (dc.unique()) ? "UNIQUE": "", dc.defaultValue()));
      } else {
        // add null column
        sqls.add(String.format("ALTER TABLE %s ADD COLUMN %s %s %s",
            tableName, dc.name(), getDataSourceType(dc), 
            (dc.unique()) ? "UNIQUE": ""));
        
        // set not null
        sqls.add(String.format("ALTER TABLE %s ALTER COLUMN %s NOT NULL", tableName, dc.name()));
      }
    } else {
      sqls.add(String.format("ALTER TABLE %s ADD COLUMN %s %s %s %s",
          tableName, dc.name(), getDataSourceType(dc), 
          dc.unique() ? "UNIQUE": "", 
          !dc.defaultValue().equals(CommonConstants.NullString)? "DEFAULT '"+dc.defaultValue()+"'":""));
    }

    return sqls;
  }

  @Override
  public String getDataSourceType(DAttr dc) {
    return javaToDBType(dc.type(), dc.length());
  }

//v3.2  
//  /**
//   * @overview
//   *  A sub-class of <tt>HashMap</tt> used for caching
//   * @author dmle
//   */
//  private static class Cache extends HashMap {
//    public Cache() {
//      super();
//    }
//    
//    public void putCacheEntry(Object key,Object...vals) {
//      super.put(key,vals);
//    }
//
//    public Object[] getCacheEntry(Object key) {
//      return (Object[]) super.get(key);
//    }
//  } // end Cache
}
