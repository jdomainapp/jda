package jda.modules.dodm.osm.javadb;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.osm.relational.DataSourceType;
import jda.modules.dodm.osm.relational.RelationalOSMBasic;
import jda.modules.dodm.osm.relational.sql.function.DataSourceFunction;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;

/**
 * @overview
 *  An implementation of {@link RelationalOSMBasic} for <tt>Java DB</tt> (a.k.a Apache Derby) RDBMS.
 *   
 * @author dmle
 */
public class JavaDbOSMBasic extends RelationalOSMBasic {
  
  private static final String QUERY_DROP_TABLE = "drop table %s";
  
  private static final String QUERY_RELATION_NAMES = 
      "select tablename from sys.systables t, sys.sysschemas s "
        + "where t.schemaid=s.schemaid and s.schemaname='%s'";
  
  private static final String QUERY_SCHEMA_EXIST = 
      "select s.schemaname from sys.sysschemas s "
        + "where s.schemaname='%s'";

  public JavaDbOSMBasic(OsmConfig config, DOMBasic dom)
      throws DataSourceException {
    super(config, dom);
  }

////  private static RelationalOSM instance;
//  //private String dbFilePath;
//
//  private Connection conn;
//  
//  //TODO: uncomment this for caching
//  //private Cache cache;
//
////  public static final String DB_SCHEMA = "APP";
//  //private static final String DBNAME = "myapp";
//  private static final String DBMS = "derby";
//  public static final String SQL_CREATE_TABLES = "create_tables"; // should
//                                                                  // append .sql
//  public static final String SQL_POPULATE_TABLES = "populate_tables"; // should
//                                                                      // append
//                                                                      // .sql
//  public static final String SQL_QUERY_TABLES = "queries"; // append .sql
//
//  // constants fields
//  private final static char LF = System.getProperty("line.separator").charAt(0);
//  private final static int DEFAULT_LENGTH = 100;
//  private static final Class<DomainConstraint> DC = DomainConstraint.class;
//
//  private static final boolean debug = Toolkit.getDebug(JavaDbOSMBasic.class);//Boolean.parseBoolean(System.getProperty(
//      //"debug", "false"));
//
////  private RelationalOSM(DomainSchema domainSchema, String dbFilePath)
////      throws DBException {
////    schema = domainSchema;
////    this.dbName = dbFilePath;
////    connect(dbFilePath);
////    
////    //TODO: uncomment this for caching
////    //cache = new Cache();
////  }
//
//  public JavaDbOSMBasic(OsmConfig config, DOMBasic dom)
//      throws DataSourceException {
//    super(config, dom);
//    //this.dbFilePath = config.getDataSourceName();
//    connect();
//    
//    //TODO: uncomment this for caching
//    //cache = new Cache();
//  }
//
////  /**
////   * @effects returns <code>this</code> (singleton) instance
////   */
////  public static RelationalOSM getInstance(DomainSchema schema) throws DBException {
////    return getInstance(schema, DBNAME);
////  }
//
////  public static RelationalOSM getInstance(DomainSchema domainSchema, String dbFilePath)
////      throws DBException {
////    if (instance == null) {
////      instance = new RelationalOSM(domainSchema, dbFilePath);
////    }
////
////    return instance;
////  }
//
////  /**
////   * @effects
////   *  return the data source name of this
////   */
////  public String getDataSourceName() {
////    return dbFilePath;
////  }
//  
//  /**
//   * @effects Returns a new <code>Connection</code> object to database
//   *          <code>dbFilePath</code> on disk. Database <code>dbFilePath</code> is
//   *          created if not yet exists.
//   * @modifies <code>this</code>
//   */
//  @Override
//  public void connect() throws DataSourceException {
//    Properties connectionProps = new Properties();
//    connectionProps.setProperty("create", "true");
//    
//    // set up properties, e.g. user name/password
//    OsmConfig config = getConfig();
//    
//    String connectionURL = config.getProtocolURL();
//    
//    try {
//      conn = DriverManager.getConnection(
//          //"jdbc:" + DBMS + ":" + dbFilePath
//          // + ";create=true", connectionProps
//          connectionURL, connectionProps
//          );
//      if (debug)
//        System.out.println("Connected to database " + connectionURL);
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_CONNECT, e,
//          "Failed to connect to database {0}: {1}", connectionURL, e.getMessage());
//    }
//  }
//
//  /**
//   * @effects 
//   *  if connection to data source is valid
//   *    return true
//   *  else
//   *    return false
//   */
//  public boolean isConnected() {
//    return (conn != null);
//  }
//  
//  /**
//   * @effects closes the active connection
//   */
//  public void disconnect() {
//    try {
//      conn.close();
//    } catch (SQLException e) {
//      // ignore
//    }
//  }
//
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
//
//  /**
//   * @effects Executes each statement in <code>this.SQL_CREATE_TABLES</code> to
//   *          create tables in the database connected to by
//   *          <code>this.conn</code>, throwing <code>SQLException</code> if an
//   *          error occured.
//   */
//  public void createSchemaFromFile(String sqlFile) throws DataSourceException {
//    // run the create_tables.sql file
//    executeStatementsFromFile(sqlFile, null);
//  }
//
//  /**
//   * @effects Executes each statement in <code>this.SQL_POPULATE_TABLES</code>
//   *          to insert data into each table in the database connected to by
//   *          <code>this.conn</code>, throwing <code>SQLException</code> if an
//   *          error occured.
//   */
//  public void createObjectsFromFile(String sqlFile) throws DataSourceException {
//    executeStatementsFromFile(sqlFile, null);
//  }
//
//  /**
//   * @effects Executes each statement in <code>this.SQL_QUERY_TABLES</code> to
//   *          query data from each table in the database connected to by
//   *          <code>this.conn</code>, throwing <code>SQLException</code> if an
//   *          error occured.
//   */
//  public void queryTables(String sqlFile) throws DataSourceException {
//    Map<String, String> resultMap = new Map<String, String>();
//    executeStatementsFromFile(sqlFile, resultMap);
//    for (Entry<String, String> e : resultMap.entrySet()) {
//      System.out.println("Query: \n" + e.getKey());
//      System.out.println("Result:");
//      System.out.println(e.getValue());
//    }
//  }
//
//  /**
//   * @effects invokes <code>executeStatementsFromFile(filePath,null)</code>.
//   */
//  public void executeStatementsFromFile(String filePath) throws DataSourceException {
//    executeStatementsFromFile(filePath, null);
//  }
//
//  /**
//   * @effects Executes each statement in <code>filePath</code> to over the
//   *          database connected to by <code>this.conn</code>, throwing
//   *          <code>SQLException</code> if an error occured.
//   * @modifies if <code>resultMap != null</code> and there are result sets then
//   *           adds
//   * 
//   *           <pre>
//   * <sql,ResultSet>
//   * </pre>
//   * 
//   *           entries to <code>resultMap</code>
//   * 
//   */
//  private void executeStatementsFromFile(String filePath,
//      Map<String, String> resultMap) throws DataSourceException {
//
//    if (debug)
//      System.out.println("------ Executing " + filePath + " ------");
//
//    // URL fileIn = DBToolKit.class.getResource(fname);
//    String sql = null;
//
//    Statement s = null;
//    try {
//
//      BufferedReader in = new BufferedReader(new FileReader(new File(filePath)));
//      s = conn.createStatement();
//      StringBuffer sb = new StringBuffer();
//      ResultSet rs = null;
//      while ((sql = in.readLine()) != null) {
//        sql = sql.trim();
//        sb.append(sql);
//
//        if (sql.endsWith(";")) {
//          sql = sb.toString();
//          if (debug)
//            System.out.println("-> Statement: \n" + sql);
//          try {
//            s.execute(sql.substring(0, sql.length() - 1));
//            // if result map is specified...
//            if (resultMap != null) {
//              rs = s.getResultSet();
//              if (rs != null) {
//                resultMap.put(sql, resultSetToString(rs));
//              }
//            }
//          } catch (SQLException e) {
//            System.err.println(e);
//          }
//          sb = new StringBuffer();
//        } else {
//          sb.append("\n");
//        }
//      }
//    } catch (SQLException e) {
//      // e.printStackTrace();
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_STATEMENT,
//          "Failed to execute statement", e);
//    } catch (FileNotFoundException e) {
//      // should not happen
//    } catch (IOException e) {
//      // should not happen
//      System.err.println("Faild to read file " + filePath + ": " + e);
//    } finally {
//      try {
//        if (s != null)
//          s.close();
//      } catch (SQLException e) {
//        //
//      }
//    }
//  }
//
//  /**
//   * @effects returns <code>true</code> if a table with name
//   *          <code>tableName</code> exists in the database schema named
//   *          <code>dbSchema</code> , else returns <code>false</code>
//   */
//  public boolean exists(String dbSchema, String tableName) {
//    String sql = "select tablename from sys.systables t, sys.sysschemas s "
//        + "where t.schemaid=s.schemaid and s.schemaname='%s'";
//
//    sql = String.format(sql, dbSchema);
//
//    ResultSet rs = null;
//    try {
//      rs = executeQuery(sql);
//
//      if (rs != null) {
//        String tname;
//        while (rs.next()) {
//          tname = rs.getString(1);
//          if (tname.equalsIgnoreCase(tableName)) {
//            return true;
//          }
//        }
//      }
//    } catch (Exception e) {
//      // something wrong, but dont care
//    } finally {
//      try {
//        if (rs != null)
//          rs.close();
//      } catch (Exception e) {
//      }
//    }
//    return false;
//  }
//
//  /**
//   * @effects returns <code>List<Map></code> object, each entry of which is a
//   *          <code>Map<String,Object></code>, which represents a record in the
//   *          result of executing <code>sql</code>. A map entry maps a column
//   *          name to its value.
//   * @deprecated use the method {@link #queryAsMap(String)} instead
//   */
//  public List<Map<String, Object>> query(String sql) throws DataSourceException {
//    Statement s = null;
//    ResultSet rs = null;
//
//    try {
//      s = conn.createStatement();
//      rs = s.executeQuery(sql);
//
//      List<Map<String, Object>> recs = new ArrayList<Map<String, Object>>();
//      int cols = rs.getMetaData().getColumnCount();
//      Object v;
//      Map<String, Object> rec;
//      while (rs.next()) {
//        rec = new Map<String, Object>();
//        for (int i = 1; i <= cols; i++) {
//          v = rs.getObject(i);
//          rec.put(rs.getMetaData().getColumnName(i).toLowerCase(), v);
//        }
//        recs.add(rec);
//      }
//
//      s.close();
//      if (recs.isEmpty())
//        return null;
//      else
//        return recs;
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY,
//          "Failed to execute query ", e.getMessage());
//    }
//  }
//
//  /**
//   * @effects returns a <code>MapList</code> object, the keys of which are the
//   *          SQL output column names and the values of which are
//   *          <code>List</code>s of values of the columns
//   */
//  public MapList queryAsMap(String sql) throws DataSourceException {
//    Statement s = null;
//    ResultSet rs = null;
//
//    try {
//      s = conn.createStatement();
//      rs = s.executeQuery(sql);
//
//      MapList recs = new MapList();
//      int cols = rs.getMetaData().getColumnCount();
//      Object v;
//      int rowIndex = 0;
//      while (rs.next()) {
//        // rec = new Map<String, Object>();
//        for (int i = 1; i <= cols; i++) {
//          v = rs.getObject(i);
//          // rec.put(rs.getMetaData().getColumnName(i).toLowerCase(), v);
//          recs.put(rs.getMetaData().getColumnName(i).toLowerCase(), v, rowIndex);
//        }
//        rowIndex++;
//        // recs.add(rec);
//      }
//
//      s.close();
//      if (recs.isEmpty())
//        return null;
//      else
//        return recs;
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY,
//          "Failed to execute query ", e.getMessage());
//    }
//  }
//
//  /**
//   * @effects Executes <code>sql</code> over the database connected to by
//   *          <code>this.conn</code>. If successful then returns a <code>ResultSet</code> object, 
//   *          otherwise throws
//   *          <code>SQLException</code>.
//   *          
//   *          <p>This method never returns <tt>null</tt>. Caller should invoke <tt>rs.next()</tt> to check 
//   *          if the result set is empty.
//   */
//  private ResultSet executeQuery(String sql) throws DataSourceException {
//    try {
//      // v2.7.4: add statement options so that we can invoke first(), etc. on the ResultSet
//      //Statement s = conn.createStatement();
//      Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
//      
//      ResultSet rs = s.executeQuery(sql);
//      // String rss = resultSetToString(rs);
//      // -- dont close statement here --> this s.close();
//      return rs;
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_QUERY, e,
//          "Failed to execute query: {0}", sql);
//    }
//  }
//
//  private void executeUpdate(String sql) throws DataSourceException {
//    try {
//      Statement s = conn.createStatement();
//      s.executeUpdate(sql);
//      s.close();
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_UPDATE, e,
//          "Lỗi thực thi truy vấn {0}", sql);
//    }
//  }
//
//  /**
//   * @effects 
//   *  execute the parameterised SQL query <tt>sql</tt> for the domain object <tt>o</tt>
//   *  of the domain class <tt>c</tt>, with the values of the attributes contained 
//   *  in <tt>updateAttributes</tt>.
//   *  
//   *  <p>Throws DBException if failed to execute the SQL statement. 
//   */
//  private void executeParameterisedUpdate(String sql, Class c, List<Field> updateAttributes, Object o) 
//      throws DataSourceException {
//    
//    PreparedStatement s;
//    DomainConstraint dc;
//    Type type;
//    int index=1;
//    Object d = null;
//    Field f = null;
//    try {
//      s = conn.prepareStatement(sql);
//
//      for (int i = 0; i < updateAttributes.size();i++) {
//        f = updateAttributes.get(i);
//        
//        dc = f.getAnnotation(DC);
//        
//        // field type is either the native type or
//        // the type specified in the DomainConstraint annotation of the field
//        type = dc.type();
//        d = dom.getDsm().getAttributeValue(f, o);
//        
//        //if (d != null) {          
//        javaToSQL(s, index, type, f.getType(), d);
//        index++;
//        //}
//      }
//      
//      s.execute();
//      
//      s.close();
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_UPDATE, e,
//          "Lỗi thực thi truy vấn {0} (object: {1}, column ({2}:{3}), value({4}))", 
//          sql, o, index,((f!=null) ? f.getName() : null), d);
//    }
//  }
//  
////  /**
////   * @effects 
////   *  execute the parameterised SQL query <tt>sql</tt> for the domain object <tt>o</tt>
////   *  of the domain class <tt>c</tt>. 
////   *  
////   *  <p>Throws NotFoundException if <tt>c</tt> is not a domain class, DBException if failed to execute
////   *  the SQL statement. 
////   */
////  private void executeParameterisedInsert(String sql, Class c, 
////      Object o) throws NotFoundException, 
////    DBException {
////    List fields = null;
////    Type type;
////    DomainConstraint dc;
////    int index=1;
////    try {
////      PreparedStatement s = conn.prepareStatement(sql);
////      
////      fields = schema.getDsm().getRelationalAttributes(c);
////
////      if (fields == null)
////        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
////            "Không tìm thấy lớp: {0}", c);
////
////      for (int i = 0; i < fields.size(); i++) {
////        Field f = (Field) fields.get(i);
////        dc = f.getAnnotation(DC);
////        
////        // field type is either the native type or
////        // the type specified in the DomainConstraint annotation of the field
////        type = dc.type();
////        Object d = schema.getDsm().getAttributeValue(f, o);
////        
////        if (d != null) {          
////          javaToSQL(s, index, type, f.getType(), d);
////          index++;
////        }
////      }
////      
////      s.execute();
////      
////      s.close();
////    } catch (SQLException e) {
////      throw new DBException(DBException.Code.FAIL_TO_UPDATE, e,
////          "Lỗi thực thi truy vấn {0} (object: {1})", sql, o);
////    }
////  }
//
//  /**
//   * @effects 
//   *  update the prepared SQL statement <tt>s</tt> with the data <tt>d</tt> for the column <tt>colIndex</tt>. 
//   *  Throws <tt>SQLException</tt> if fails to update the statement or <tt>NotPossibleException </tt>
//   *  if could not convert <tt>d</tt> to the required SQL value.
//   */
//  private void javaToSQL(PreparedStatement s, int colIndex, Type type, Class typeClass, Object d) 
//  throws SQLException, NotPossibleException {
//    if (type.isString() || type.isChar()) {
//      if (d != null)
//        s.setString(colIndex, d+"");
//      else 
//        s.setString(colIndex, null);
//    } else if (type.isInteger()) {
//      if (d==null)
//        s.setNull(colIndex, Types.INTEGER);
//      else {
//        s.setInt(colIndex, (Integer)d);
//      }
//    } else if (type.isLong()) {
//      if (d==null)
//        s.setNull(colIndex, Types.INTEGER);
//      else
//        s.setLong(colIndex, (Long)d);
//    } else if (type.isFloat()) {
//      if (d==null)
//        s.setNull(colIndex, Types.FLOAT);
//      else
//        s.setFloat(colIndex, (Float)d);
//    } else if (type.isDouble()) {
//      if (d==null)
//        s.setNull(colIndex, Types.FLOAT);
//      else
//        s.setDouble(colIndex, (Double)d);
//    } 
//    // v2.7.2 
//    else if (type.isDecimal()) {
//      if (d == null)
//        s.setNull(colIndex, Types.DECIMAL);
//      else
//        s.setBigDecimal(colIndex, Toolkit.toDecimal(type, d));
//    }
//    else if (type.isBoolean()) {
//      // use varchar for boolean type (see javaToDB)
//      if (d != null)
//        s.setString(colIndex, d+"");
//      else 
//        s.setNull(colIndex,
//            /*v2.7.3: changed to VARCHAR b/c boolean is stored as such 
//            Types.BOOLEAN
//            */
//            Types.VARCHAR 
//            );
//    } 
//    else if (type.isDate()) {
//      /**v2.5.4: support date type*/
//      if (d==null)
//        s.setNull(colIndex, Types.DATE);
//      else {
//        // convert to SQL Date type if necessary
//        Date date;
//        if (d instanceof java.util.Date)
//          date = javaDateToSQL((java.util.Date) d);
//        else
//          date = (Date) d;
//        s.setDate(colIndex, date);
//      }
//    } else if (type.isSerializable()) {
//      s.setObject(colIndex, d);
//    } else if (type.isImage()) {
//      // get image bytes
//      if (d != null) {
//        byte[] bytes = imageToBytes((ImageIcon) d, "jpg");
//        s.setBytes(colIndex, bytes);
//      } else {
//        s.setNull(colIndex, 
//            /*v3.0: use method
//            SqlType.getMapping(type).getIntValue()
//            */
//            getSqlTypeFor(getSqlTypeClass(), type).getIntValue()
//            );
//            
//      }
//        
//    } else if (type.isDomainType()) {
//      DomainConstraint[] dcFKs = dom.getDsm().getIDAttributeConstraints(typeClass);
//      for (DomainConstraint dcFK : dcFKs) {
//        if (d != null)
//          d = dom.getDsm().getAttributeValue(d, dcFK.name());
//        javaToSQL(s, colIndex, dcFK.type(), null, d);
//      }
//    } else {
//      throw new NotImplementedException(
//          NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//          "Không hỗ trợ kiểu dữ liệu: {0}", type.name());
//    }
//  }
//  
//  /**
//   * This is the reverse of {@link #sqlDateToJava(java.util.Date)}.
//   * 
//   * @requires
//   *  d != null
//   * @effects 
//   *  return a <tt>java.sql.Date</tt> object equivalence of the <tt>java.util.Date</tt> object <tt>d</tt>
//   */
//  private java.sql.Date javaDateToSQL(java.util.Date d) {
//    return new java.sql.Date(d.getTime());
//  }
//  
//  /**
//   * This is the reverse of {@link #javaDateToSQL(java.util.Date)}.
//   * 
//   * @requires
//   *  d != null
//   * @effects 
//   *  return a <tt>java.util.Date</tt> object equivalence of the <tt>java.sql.Date</tt> object <tt>d</tt>
//   */
//  private java.util.Date sqlDateToJava(java.sql.Date d) {
//    return new java.util.Date(d.getTime());
//  }
//  
//  /**
//   * The reverse of method {@link #javaToSQL(PreparedStatement, int, Type, Class, Object)}. 
//   * 
//   * @requires <tt>dc != null /\ rs != null /\ rs</tt> is currently pointing to a valid current row /\ 
//   *  colIndex >= 1 and is a valid column index
//   *    
//   * @effects 
//   *  the SQL value at the column <tt>colIndex</tt> of the current row of the 
//   *  result set </tt>rs</tt> and convert it to the corresponding Java value, 
//   *  using the domain constraint <tt>dc</tt>. 
//   *  If succeeds
//   *    return the value
//   *  else if <tt>dc.type()</tt> is not supported
//   *    throws NotImplementedException
//   *  else 
//   *    throws NotPossibleException
//   */
//  private Object sqlToJava(Class cls, // v2.7.4: added this parameter 
//      DomainConstraint dc, ResultSet rs, final int colIndex) 
//      throws NotImplementedException, NotPossibleException {
//    Object val = null;
//
//    Type type = dc.type();
//
//    try {
//      
//      /** 
//       * if data type is non-BLOB 
//       *    get the column value first 
//       *    if it is not null 
//       *      get the value again using the correct getter for the specified data type
//       * else
//       *  get the value as blob and convert if needed
//       */
//      // v2.6.4.a: changed to isByteArray to support all BLOB-type
//      if (!type.isByteArray()) { //(!type.isImage()) {
//        val = rs.getObject(colIndex);
//        // convert if val is not null (i.e. SQL null)
//        if (val != null) {
//          if (type.isString()) {
//            /* v2.7.3: support char 
//            val = rs.getString(colIndex);
//            */
//            String valStr = rs.getString(colIndex);
//            if (type.isChar()) {
//              val = (valStr.length() > 0) ? 
//                  valStr.charAt(0) :  // get the char 
//                  '\u0000'              // no char = null char
//                    ;
//            } else {
//              val = valStr;
//            }
//          }
//          else if (type.isInteger()) {
//            val = rs.getInt(colIndex);
//          } else if (type.isLong()) {
//            val = rs.getLong(colIndex);
//          } else if (type.isFloat()) {
//            val = rs.getFloat(colIndex);
//          } else if (type.isDouble()) {
//            val = rs.getDouble(colIndex);
//          }  
//          // v2.7.2 
//          else if (type.isDecimal()) {
//            val = rs.getBigDecimal(colIndex);
//            // convert
//            val = Toolkit.fromDecimal(type, (BigDecimal) val);
//          }
//          else if (type.isBoolean()) {
//            // Note: rs.getBoolean() does not work correctly
//            val = Boolean.parseBoolean(val.toString());
//          } else if (type.isDate()) {
//            // convert to java Date
//            val = rs.getDate(colIndex);
//            val = sqlDateToJava((java.sql.Date) val);
//          }
//          // add other data type here
//          else {
//            throw new NotImplementedException(
//                NotImplementedException.Code.DATA_TYPE_NOT_SUPPORTED,
//                "Không hỗ trợ kiểu dữ liệu {0}", type);
//          }
//        }
//      } else {
//        // blob-type
//        // get blob and convert to bytes
//        /*v2.7.4: use binary stream
//        Blob blob = rs.getBlob(colIndex);
//        if (blob != null) {
//          long blobSize = blob.length();
//          val = blob.getBytes(1, (int) blobSize);
//          // v2.6.4.a
//          if (type.isImage()) {
//            // image type -> convert to image
//            val = bytesToImage((byte[])val);
//          }
//        }
//        */
//        /*v2.7.4: this code is slightly better than the above for the image-typed case in the support for client/server config
//         * FIXME: BUT for client/server configuration it still does not successfully read images for all cases 
//         * For some cases (e.g. Configuration.appLogo) this works; for others (e.g. Person.idPhoto)
//         * it returns a null input stream (ins). 
//         * The remaining blob-typed cases (else branch) are thus also not guaranteed to work correctly.
//         */
//        if (type.isImage()) { // image-type data -> read into ImageIcon 
//          InputStream ins = rs.getBinaryStream(colIndex);
//          if (ins != null)
//            val = inputStreamToImage(ins);
//        } else {  // other binary data types
//          Blob blob = rs.getBlob(colIndex);
//          if (blob != null) {
//            long blobSize = blob.length();
//            val = blob.getBytes(1, (int) blobSize);
//          }
//        }
//        /*v2.7.4: this mimics what was done in the insert() method BUT throws exception 
//         * for client/server driver  
//        val = rs.getBytes(colIndex);
//        // v2.6.4.a
//        if (val != null && type.isImage()) {
//          // image type -> convert to image
//          val = bytesToImage((byte[])val);
//        }
//        */
//      }
//    } catch (SQLException e) {
//      throw new NotPossibleException(
//          NotPossibleException.Code.FAIL_TO_READ_TABLE_COLUMN_VALUE, e, new Object[] {cls.getSimpleName(), dc.name(), colIndex, type});
//    }
//    
//    // if we get here then ok
//    return val;
//  }
//  
//  /**
//   * @effects return array <tt>byte[]</tt> of the content of the <tt>Image img</tt>
//   */
//  private byte[] imageToBytes(ImageIcon img, String imgType) throws NotPossibleException {
//    ByteArrayOutputStream bout = new ByteArrayOutputStream();
//
//    try {
//      int width = img.getIconWidth();
//      int height = img.getIconHeight();
//      BufferedImage bimg = new BufferedImage(
//          width,
//          height, 
//          BufferedImage.TYPE_INT_RGB);
//      Graphics2D g = bimg.createGraphics();
//      g.drawImage(img.getImage(), 0, 0, null);
//      ImageIO.write(bimg, imgType, bout);
//      
//      return bout.toByteArray();
//    } catch (Exception e) {
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
//          e, "Không thể thực thi phương thức: {0}({1}", "imageToBytes", img);
//    }
//  }
//  
//  /**
//   * @requires 
//   *  <tt>ins</tt> represents bytes of an ImageIcon
//   *  
//   * @effects return an <tt>ImageIcon</tt> from the bytes read from <tt>ins</tt>
//   * @version 2.7.4
//   */
//  private ImageIcon inputStreamToImage(InputStream ins) throws NotPossibleException {
//    try {
//      BufferedImage bimg = ImageIO.read(ins);
//
//      if (bimg != null) {
//        ImageIcon img = new ImageIcon(bimg);
//      
//        return img;
//      } else {
//        return null;
//      }
//    } catch (Exception e) {
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
//          e, new Object[] {"inputStreamToImage", "", ""});
//    }
//  }
//  
//  /**
//   * @effects return an <tt>Image</tt> object whose content is <tt>byteArray</tt>
//   */
//  private ImageIcon bytesToImage(byte[] byteArray) {
//    
//    ImageIcon icon = new ImageIcon(byteArray);
//    return icon; //icon.getImage();
//  }
//  
//  /**
//   * @effects Returns each row in result set <code>rs</code> concatenated.
//   */
//  private String resultSetToString(ResultSet rs) throws DataSourceException {
//    try {
//      int cols = rs.getMetaData().getColumnCount();
//
//      Object v;
//      StringBuffer sb = new StringBuffer();
//      while (rs.next()) {
//        for (int i = 1; i <= cols; i++) {
//          v = rs.getObject(i);
//          sb.append(v.toString()).append(" ");
//        }
//        sb.append(LF);
//      }
//
//      return sb.toString();
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET,
//          "Failed to process result set ", e.getMessage());
//    }
//  }
//
//  /**
//   * @effects Closes <code>this.conn</code>
//   */
//  public void close() {
//    try {
//      this.conn.close();
//    } catch (SQLException e) {
//      // ignore
//    }
//  }
//
//  // public boolean exists(Class c) {
//  // String sql = "Select ";
//  // }
//
//  // ////////// NEW METHODS /////////////
//  /**
//   * Store the data of a domain object into the database table created for its
//   * class.<br>
//   * 
//   * @effects inserts a database record for a domain <code>obj</code> into the
//   *          database table of the domain class <code>c</code>.
//   */
//  public void putObject(Class c, Object obj) throws DataSourceException {
//    String sql = null;
//
//    List<Field> updateAttributes;
//    
//    try {
//      // TODO: fix the comments below for caching
////      Class actualClass = obj.getClass();
////      Object[] cached = cache.getCacheEntry(actualClass);
////      
////      if (cached != null) {
////        sql = (String) cached[0];
////        updateAttributes = (List<Field>) cached[1];
////        if (debug)
////          System.out.println("sql in cache: \n\t" + sql);
////      } else {
//        updateAttributes = new ArrayList();
//        sql = genParameterisedInsert(c, obj, updateAttributes);
//        if (debug)
//          System.out.println(sql);
//        // cache 
////        cache.putCacheEntry(actualClass,sql,updateAttributes);
////      }
//    } catch (NotFoundException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_INSERT, e,
//          "Lỗi thêm dữ liệu: {0}, {1}", c.getSimpleName(), obj);
//    }
//
//    executeParameterisedUpdate(sql, c, updateAttributes, obj);
//  }
//
//  /**
//   * @effects updates the database record of the domain object <code>obj</code>
//   *          in the database table of the domain class <code>c</code>, or
//   *          throws <code>DBException</code> if an error occured.
//   */
//  public void updateObject(Object obj, Class c) throws DataSourceException {
//    String sql = null;
//
//    List<Field> updateAttributes = new ArrayList();
//    try {
//      //sql = genUpdate(c, obj);
//      sql = genParameterisedUpdate(c, obj, updateAttributes);
//
//      //System.out.println("update attributes: " + updateAttributes);
//      
//      if (debug)
//        System.out.println(sql);
//    } catch (NotFoundException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_UPDATE, e,
//          "Lỗi cập nhật dữ liệu {0}", obj);
//    }
//
//    if (sql != null)
//      //executeUpdate(sql);
//      executeParameterisedUpdate(sql,c,updateAttributes,obj);
//    else {
//      // TODO: log
//    }
//  }
//
//  /**
//   * @effects 
//   *  update database records of <tt>c</tt> that satisfies <tt>searchExp</tt>
//   *  using the expressions in <tt>updateQuery</tt>
//   *   
//   * @requires the database table of <code>c</code> has been created
//   */
//  public void updateObjects(Class c, Query<ObjectExpression> searchQuery,
//      Query<ObjectExpression> updateQuery) throws DataSourceException {
//    String sql = null;
//
//    try {
//      sql = genUpdate(c, searchQuery, updateQuery);
//
//      if (debug)
//        System.out.println("DBToolKit.updateObjects: " + sql);
//    } catch (NotFoundException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_UPDATE, e,
//          "Lỗi cập nhật dữ liệu {0}", c.getSimpleName());
//    }
//
//    executeUpdate(sql);
//  }
//  
//  /**
//   * @effects delete database record of the domain object <code>obj</code> from
//   *          the database table of the domain class <code>c</code> or throws
//   *          <code>DBException</code> if failed to do so.
//   * @requires the database table of <code>c</code> contains a record for
//   *           <code>obj</code>
//   */
//  public void deleteObject(Class c, Object obj) throws DataSourceException {
//    String sql = null;
//
//    try {
//      sql = genDelete(c, obj);
//      if (debug)
//        System.out.println(sql);
//    } catch (NotFoundException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_DELETE, e,
//          "Lỗi xóa dữ liệu: {0}, {1}", c.getSimpleName(), obj);
//    }
//
//    executeUpdate(sql);
//  }
//  
//  /**
//   * @effects 
//   *  delete database records of <tt>c</tt> that satisfies <tt>searchQuery</tt> 
//   *  
//   * @requires the database table of <code>c</code> has been created
//   */
//  public void deleteObjects(Class c, Query<ObjectExpression> searchExp) throws DataSourceException {
//    String sql = null;
//
//    try {
//      sql = genDelete(c, searchExp);
//      if (debug)
//        System.out.println("DBToolKit.deleteObjects: " + sql);
//    } catch (NotFoundException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_DELETE, e,
//          "Lỗi xóa dữ liệu: {0}, {1}", c.getSimpleName(), searchExp);
//    }
//
//    executeUpdate(sql);
//  }
//
//  /**
//   * @effects returns <code>num</code> objects of a class <code>c</code> from
//   *          the database, or <code>null</code> if no objects were found;
//   *          
//   *          <br>throws <code>DataSourceException</code> if errors occurred in reading
//   *          objects from the database; NotPossibleException if fail to create object
//   *          
//   * @requires the objects that are referenced by the retrieved objects of
//   *           <code>c</code> must have already been read from the database.
//   */
//  private List readObjects(final Class c, Expression[] conditions, int num)
//      throws NotPossibleException, DataSourceException {
//    final String cname = dom.getDsm().getDomainClassName(c);
//
//    // read database records for c's objects
//    ResultSet rs = readRecords(c, conditions);
//
//    if (rs == null)
//      return null;
//
//    // read the domain attributes of the class
//    // we will use these attributes to parse the object values
//    List<Field> fields = dom.getDsm().getSerialisableDomainAttributes(c);
//
//    // determine if c has a reflexive association (reflexive relationship)
//    // if so then we must take care of the order in which we read objects from database
//    // (see below)
//    boolean reflexive = dom.getDsm().isReflexive(c, fields);
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
//    // an object stack to keep those that will be processed 
//    // later in the case reflexive=true
//    // stack is used because we want to process entries that are added
//    // later first
//    Stack<List> delayedStack = null;
//    if (reflexive)
//      delayedStack = new Stack();
//    
//    // use a flag to flag a record as member of the delayedQueue
//    boolean delayed;
//    
//    try {
//      // make a pass through the database records in the record set
//      REC: while (rs.next()) {
//        values = new ArrayList();
//
//        // reset delayed to false
//        delayed = false;
//        
//        /*
//         *  make a pass through the domain attributes and read their values
//         *  from the current record. 
//         *  
//         *  If reflexive=true and the value of the concerned FK attribute is not null and 
//         *  the referred object has not been read then we put the current values into 
//         *  the delayed queue to process later
//         */
//        for (int i = 0; i < fields.size(); i++) {
//          f = (Field) fields.get(i);
//          dc = f.getAnnotation(DC);
//          type = dc.type();
//          
//          // we only use id and non-auto-generated attribute to create object
//          // v2.7.3: if (dc.id() || !dc.auto()) {
//            if (!type.isDomainType()) {
//              // read the sql value 
//              v = sqlToJava(c, dc, rs, i+1);
//            } else {
//                // domain type attribute
//                // query the object whose id is the value of this field
//                // and use that for the object
//                domainType = f.getType();
//                refDcs = dom.getDsm().getIDAttributeConstraints(domainType);
//
//                refValues = new Object[refDcs.length];
//                v = sqlToJava(c, refDcs[0], rs, i+1);
//                if (v != null) {  
//                  // ref value is specified, look it up
//                  refValues[0] = v;
//                  if (refDcs.length > 1) {
//                    // if it is a compound key then we must
//                    // read the subsequent values in this record to complete
//                    // the id
//                    int j = 1;
//                    for (i = i + 1; i < i + refDcs.length; i++) {
//                      Field f1 = (Field) fields.get(i);
//                      DomainConstraint dc1 = f1.getAnnotation(DC);
//                      refValues[j] = sqlToJava(c, refDcs[j], rs, i); //rs.getObject(i);
//                      j++;
//                    }
//                  }
//                  
//                  /**
//                   * if reflexive=true and this field is the FK attribute
//                   *  look up for object in objects
//                   * else
//                   *  look up for object in schema
//                   */
//                  if (reflexive && domainType == c) {
//                    v = dom.lookUpObjectByID(objects, refValues);
//                    if (v == null) { // referenced object not yet processed
//                      //  record this value as normal to be processed later
//                      // reset value to keep, also set delayed to true
//                      if (!delayed) delayed = true;
//                      v = refValues;
//                    }        
//                  } else {
//                    // use the id to look up the object directly
//                    v = dom.lookUpObjectByID(domainType, refValues); 
//                    if (v == null) { // referenced object not found
//                      // log and skip this record
//                      System.err
//                          .println(
//                              String.format("Referenced object required but *not found*: %s(%s).%s -> %s.%s[%s])", 
//                                  cname, values, f.getName(), domainType.getSimpleName(), refDcs[0].name(), refValues[0]+""));
//                      if (debug)
//                        System.exit(1);
//  
//                      // skip this record
//                      continue REC;
//                    }                    
//                  }
//                } else {
//                  // v2.6.4.a:
//                  // ref value is not specified; if it is required then data integrity
//                  // is violated, print error
//                  if (!dc.optional()) {
//                    // log
//                    if (debug) {
//                      System.err
//                      .println(
//                          String.format("Referenced object required but *not specified*: %s(%s).%s -> %s.%s[%s])", 
//                              cname, values, f.getName(), domainType.getSimpleName(), refDcs[0].name(), refValues[0]+""));
//
//                      //System.exit(1);
//                    }
//                  }
//                }
//              } // end domain type
//          
//            values.add(v);
//            
//            //debug
////            if (cname.equals("CustomerOrder")) {
////              System.out.printf("      v=%s%n",v);
////            }
//          //} // end if
//        } // end for(fields) loop
//
//        /* 
//         * if the current record is not a member of the delayed queue (i.e delayed = false) 
//         *  create object
//         * else 
//         *  add values to delayed queue  
//         **/
//        if (!delayed) {
//          try {
//            o = dom.getDsm().newInstance(c, values.toArray());
//            objects.add(o);
//          } catch (Exception e) {
//            //e.printStackTrace();
//            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,
//                new Object[] {c.getSimpleName(), values});
//          }
//  
//          if (num > 0) {
//            recCount++;
//            if (recCount >= num)
//              break;
//          }
//        } else {
//          delayedStack.push(values);
//        }
//      } // end while rs
//    } catch (SQLException ex) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, ex,
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
//    /*
//     * if reflexive=true and delayed queue is not empty 
//     *  process values in the delay queue.
//     *  This should work since all the referred-to objects have now been loaded
//     */
//    if (reflexive && !delayedStack.isEmpty()) {
//      STACK: while (!delayedStack.isEmpty()) {
//        values = delayedStack.pop();
//        /* make a pass through the domain attributes (similar to the inner loop of the main loop above)
//        /* except that this time we only need to process the value 
//         * that corresponds to the domain-type attribute. In particular, 
//         * we will look up the reference object.
//         */
//        for (int i = 0; i < fields.size(); i++) {
//          f = fields.get(i);
//          dc = f.getAnnotation(DC);
//          type = dc.type();
//          if (type.isDomainType()) {
//            domainType = f.getType();
//            if (domainType == c) {
//              // the FK attribute that causes reflexive association
//              refValues = (Object[]) values.get(i);
//              // look up the object in objects 
//              v = dom.lookUpObjectByID(objects, refValues);
//              if (v == null) {
//                // should not happen
//                System.err
//                    .println(String
//                        .format(
//                            "Referenced *reflexive* object required but not found: %s.%s -> %s[%s])",
//                            cname, f.getName(), domainType, refValues[0] + ""));
//                if (debug)
//                  System.exit(1);
//
//                // skip this record
//                continue STACK;
//              }
//              // put v back into vals
//              values.set(i, v);
//            }
//          } // end domainType attribute
//        } // end field pass
//        
//        // now create this object
//        try {
//          o = dom.getDsm().newInstance(c, values.toArray());
//          objects.add(o);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//
//        if (num > 0) {
//          recCount++;
//          if (recCount >= num)
//            break STACK;
//        }        
//      } // end values loop
//    } // end case: delayStack
//    
//    if (objects.isEmpty())
//      return null;
//    else
//      return objects;
//  }  
//
//  // version 2.5.5
////  /**
////   * @effects returns <code>num</code> objects of a class <code>c</code> from
////   *          the database, or <code>null</code> if no objects were found;
////   *          throws <code>DBException</code> if errors occurred in reading
////   *          objects from the database.
////   * @requires the objects that are referenced by the retrieved objects of
////   *           <code>c</code> must have already been read from the database.
////   */
////  private List readObjects(final Class c, Expression[] conditions, int num)
////      throws DBException {
////    final String cname = schema.getDsm().getDomainClassName(c);
////
////    // read database records for c's objects
////    ResultSet rs = readRecords(c, conditions);
////
////    if (rs == null)
////      return null;
////
////    // read the domain attributes of the class
////    // we will use these attributes to parse the object values
////    List<Field> fields = schema.getDsm().getSerialisableDomainAttributes(c);
////
////    // the objects that will be read
////    List objects = new ArrayList();
////
////    // get the id columns and check if one of them is auto-generated
////    Field f = null;
////    DomainConstraint dc;
////    DomainConstraint[] refDcs;
////    Object[] refValues;
////    Type type;
////    Class domainType;
////    int recCount = 0;
////
////    List values;
////    Object o = null;
////    Object v = null;
////
////    try {
////      REC: while (rs.next()) {
////        values = new ArrayList();
////
////        // System.out.println(fields);
////        for (int i = 0; i < fields.size(); i++) {
////          f = (Field) fields.get(i);
////          dc = f.getAnnotation(DC);
////          type = dc.type();
////          
////          // we only use id and non-auto-generated attribute to create object
////          if (dc.id() || !dc.auto()) {
////            if (!type.isDomainType()) {
////              // read the sql value 
////              v = sqlToJava(dc, rs, i+1);
////              
////              // for special attributes:
////              // - Image-type attributes
////              if (type.isImage() && v != null) { 
////                // other types
////                v = bytesToImage((byte[])v);
////              }
////            } else {
////                // domain type attribute
////                // query the object whose id is the value of this field
////                // and use that for the object
////                domainType = f.getType();
////                refDcs = schema.getDsm().getIDAttributeConstraints(domainType);
////
////                refValues = new Object[refDcs.length];
////                v = sqlToJava(refDcs[0], rs, i+1);
////                if (v != null) {  // FK value is specified, look it up
////                  refValues[0] = v;
////                  if (refDcs.length > 1) {
////                    // if it is a compound key then we must
////                    // read the subsequent values in this record to complete
////                    // the id
////                    int j = 1;
////                    for (i = i + 1; i < i + refDcs.length; i++) {
////                      Field f1 = (Field) fields.get(i);
////                      DomainConstraint dc1 = f1.getAnnotation(DC);
////                      refValues[j] = sqlToJava(refDcs[j], rs, i); //rs.getObject(i);
////                      j++;
////                    }
////                  }
////                  // use the id to look up the object directly
////                  v = schema.getDsm().lookUpObjectByID(domainType, refValues);
////  
////                  if (v == null) { // referenced object not found
////                    // throw new NotFoundException(
////                    // NotFoundException.Code.OBJECT_NOT_FOUND,
////                    // "Referenced object required but not found for {1} (type {0})",
////                    // domainType, Arrays.toString(refValues));
////                    // log and skip this record
////                    System.err
////                        .println(
////                            String.format("Referenced object required but not found: %s.%s -> %s.%s[%s])", 
////                                cname, f.getName(), domainType, refDcs[0].name(), refValues[0]+""));
////                    if (debug)
////                      System.exit(1);
////                    
////                    continue REC;
////                  }
////                }
////              } // end domain type
////          
////            // v may be null
////            values.add(v);
////          } // end if
////        } // end for(fields) loop
////
////        try {
////          o = schema.getDsm().newInstance(c, values.toArray());
////          objects.add(o);
////        } catch (Exception e) {
////          e.printStackTrace();
////        }
////
////        if (num > 0) {
////          recCount++;
////          if (recCount >= num)
////            break;
////        }
////      } // end while rs
////    } catch (SQLException ex) {
////      throw new DBException(DBException.Code.FAIL_RESULT_SET, ex,
////          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
////    } finally {
////      try {
////        rs.close();
////        rs.getStatement().close();
////      } catch (SQLException e) {
////        //
////      }
////    }
////
////    if (objects.isEmpty())
////      return null;
////    else
////      return objects;
////  }
//
//  /**
//   * Reads the database records of the domain class <code>c</code> from the
//   * database.
//   * 
//   * <p>
//   * We use the following basic algorithm:
//   * 
//   * <pre>
//   *  if c has a domain super-class then 
//   *    create a join SQL statement recursively with the super-class(es) 
//   *      to get all the fields
//   *  else if c has sub-class(es) then
//   *    read only the records that are not in the sub-classes
//   * </pre>
//   * 
//   * 
//   * <b>About SQL in the first case:</b> <br>
//   * -----------------------------<br>
//   * - a joined query between the sub-class and each super domain class on the
//   * id attribute(s) <br>
//   * - the select clause includes all attributes of the sub-class and non-id
//   * attributes of the super-class <br>
//   * <p>
//   * EXAMPLE 1: ElectiveModule is a sub-class of Module
//   * 
//   * <pre>
//   * select t2.*,t1.deptname 
//   * from electivemodule t1, module t2 
//   * where
//   *  t1.code=t2.code;
//   * </pre>
//   * 
//   * <p>
//   * EXAMPLE 2: instructor is a subclass of staff is a subclass of person
//   * 
//   * <pre>
//   * select t1.title,t2.deptname,t2.joindate,t3.* 
//   * from instructor t1, staff t2, person t3 
//   * where
//   *  t1.id = t2.id and 
//   *  t2.id = t3.id;
//   * </pre>
//   * 
//   * <b>About SQL in the second case</b>:<br>
//   * -----------------------------<br>
//   * - is the SQL of the first case plus an additional condition to exclude the
//   * ids of the records in the sub-class tables
//   * <p>
//   * EXAMPLE 1: staff is a sub-class of person and is a super-class of
//   * instructor and administrator
//   * 
//   * <pre>
//   * select t2.id, t2.name, t2.dob, t2.address,t1.joindate,t1.deptname  
//   * from staff t1, person t2 
//   * where
//   *  t1.id = t2.id and 
//   *  t1.id not in (select id from instructor UNION 
//   *                select id from administrator);
//   * </pre>
//   * 
//   * @effects returns the <code>ResultSet</code> of database records of the
//   *          domain table of the domain class <code>c</code> that meet the
//   *          <code>Expression</code>s <code>conditions</code>. Throws
//   *          <code>DBException</code> if an error occurred.
//   */
//  private ResultSet readRecords(final Class c, Expression[] conditions)
//      throws DataSourceException {
//    final String cname = dom.getDsm().getDomainClassName(c);
//
//    ResultSet rs = null;
//
//    Stack<String> tables = new Stack();
//    Stack<String> select = new Stack();
//    Stack<Expression> exps = new Stack();
//    if (conditions != null)
//      Collections.addAll(exps, conditions);
//    StringBuffer orderBy = new StringBuffer("order by ");
//
//    Class sup = dom.getDsm().getSuperClass(c);
//    List<Field> fields;
//    List<Field> idFields;
//    String cTable;
//    if (sup != null) {
//      // first case
//      int index = 1;
//
//      Class currentClass = c;
//      String currentTable = "t" + (index++);
//      cTable = currentTable;
//      tables.push(cname + " " + currentTable);
//      // add the non-id attributes of the current class to select
//      fields = dom.getDsm().getSerialisableAttributes(c);
//      DomainConstraint dc;
//      String n;
//      int colIndex = 0;
//      for (Field f : fields) { // super class table
//        dc = f.getAnnotation(DC);
//        if (!dc.id()) { // non-id attributes
//          if (dc.type().isDomainType()) {
//            n = toDBColumnName(c, dc, false);
//          } else
//            n = dc.name();
//          select.add(colIndex++, currentTable + "." + n);
//        }
//      }
//
//      String supName;
//      String supTable;
//      do {
//        supName = dom.getDsm().getDomainClassName(sup);
//        supTable = "t" + (index++);
//        tables.push(supName + " " + supTable);
//
//        idFields = dom.getDsm().getIDAttributes(sup);
//
//        // use the id attributes to add new join expressions
//        for (Field f : idFields) { // current table
//          // add join expressions between the id attributes of the two tables
//          exps.add(new Expression(currentTable + "." + f.getName(),
//              Expression.Op.EQ, supTable + "." + f.getName(),
//              Expression.Type.Metadata));
//        } // end for
//
//        // add the non-id attributes of the super class to the
//        // select clause
//        fields = dom.getDsm().getSerialisableAttributes(sup);
//        colIndex = 0;
//        for (Field f : fields) { // super class table
//          dc = f.getAnnotation(DC);
//          if (!dc.id()) { // non-id attributes
//            if (dc.type().isDomainType()) {
//              n = toDBColumnName(sup, dc, false);
//            } else
//              n = dc.name();
//            select.add(colIndex++, supTable + "." + n);
//          }
//        } // end for
//
//        // recursive: check the super-super class and so on...
//        currentTable = supTable;
//        currentClass = sup;
//        sup = dom.getDsm().getSuperClass(sup);
//      } while (sup != null);
//
//      // add the id attributes of the top-level super class to select
//      colIndex = 0;
//      int find = 0;
//      for (Field f : idFields) {
//        dc = f.getAnnotation(DC);
//        if (dc.type().isDomainType()) {
//          n = toDBColumnName(sup, dc, false);
//        } else {
//          n = dc.name();
//        }
//        select.add(colIndex++, supTable + "." + n);
//        // order by these ids
//        orderBy.append(supTable + "." + n);
//        if (find < idFields.size() - 1)
//          orderBy.append(",");
//        find++;
//      }
//    } else {
//      // no super-type
//      cTable = cname;
//      // just return all records of the table cname
//      // example sql: select * from student
//      tables.add(cTable);
//      select.add("*");
//
//      // order by the id fields
//      idFields = dom.getDsm().getIDAttributes(c);
//      int find = 0;
//      for (Field f : idFields) { // current table
//        orderBy.append(f.getName());
//        if (find < idFields.size() - 1)
//          orderBy.append(",");
//        find++;
//      }
//    }
//
//    // ascending order (if not the default)
//    orderBy.append(" ASC");
//
//    Class[] subs = dom.getDsm().getSubClasses(c);
//    if (subs != null) {
//      // second case: add new conditions to exclude the sub-class table ids
//      idFields = dom.getDsm().getIDAttributes(c);
//      if (idFields.size() > 1)
//        throw new NotImplementedException(
//            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//            "Tính năng hiện không được hỗ trợ: {0}", "compoundKey(" + c + ")");
//
//      String id = idFields.get(0).getName();
//      StringBuffer nestedSQL = new StringBuffer();
//      for (int i = 0; i < subs.length; i++) {
//        nestedSQL.append("select " + id + " from "
//            + dom.getDsm().getDomainClassName(subs[i]));
//        if (i < subs.length - 1)
//          nestedSQL.append(LF).append(" UNION ");
//      }
//      String idName = cTable + "." + id;
//      exps.add(new Expression(idName, Expression.Op.NOIN, nestedSQL.toString(),
//          Expression.Type.Nested));
//    }
//
//    // execute the SQL and return the result
//    if (!exps.isEmpty()) {
//      rs = 
//          /*selectAndProject(select.toArray(new String[select.size()]), //
//          tables.toArray(new String[tables.size()]), //
//          exps.toArray(new Expression[exps.size()]), //
//          null, //
//          orderBy.toString());*/
//          selectAndProject(select.toArray(new String[select.size()]), //
//              tables.toArray(new String[tables.size()]), //
//              exps.toArray(new Expression[exps.size()]), //
//              null, //
//              null, // group by 
//              orderBy.toString());
//    } else {
//      rs = 
//          /* selectAndProject(select.toArray(new String[select.size()]), //
//          tables.toArray(new String[tables.size()]), //
//          null, //
//          null, //
//          orderBy.toString()); */
//          selectAndProject(select.toArray(new String[select.size()]), //
//              tables.toArray(new String[tables.size()]), //
//              null, //
//              null, //
//              null, // group by 
//              orderBy.toString());
//          
//    }
//
//    return rs;
//  }
//
//  /**
//   * @effects retrieves from database and returns an <code>Object[]</code> array
//   *          of the values of the non-id attributes of an object of class
//   *          <code>c</code> whose id value is <code>idVal</code>.
//   * 
//   *          <p>
//   *          For example, give the database record for the object
//   * 
//   *          <pre>
//   * Student('S2012','Nguyen Van A','1/1/1970')
//   * </pre>
//   * 
//   *          , in which the first attribute is the identifier attribute
//   *          <code>Student.id</code>, this method will return the array
//   * 
//   *          <pre>
//   * ['Nguyen Van A','1/1/1970']
//   * </pre>
//   */
//  // public Object[] readNonIDAttributeValues(Class c, String idAttr, Object
//  // idVal)
//  // throws DBException {
//  // Expression[] conditions = new Expression[1];
//  //
//  // conditions[0] = new Expression(idAttr, "=", idVal);
//  //
//  // List<DomainConstraint> constraints = schema.getDsm().getAttributeConstraints(c);
//  //
//  // String[] fieldNames = new String[constraints.size() - 1]; // less the id
//  // // attribute
//  // String name;
//  // int fi = 0;
//  // for (int i = 0; i < constraints.size(); i++) {
//  // name = constraints.get(i).name();
//  // if (!name.equals(idAttr)) {
//  // fieldNames[fi++] = name;
//  // }
//  // }
//  //
//  // ResultSet rs = selectAndProject(fieldNames, c.getSimpleName(), conditions,
//  // null);
//  //
//  // Object[] vals = null;
//  //
//  // if (rs != null) {
//  // vals = new Object[fieldNames.length];
//  // try {
//  // while (rs.next()) {
//  // for (int i = 0; i < fieldNames.length; i++) {
//  // vals[i] = rs.getObject(fieldNames[i]);
//  // }
//  // }
//  // } catch (SQLException e) {
//  // throw new DBException("Failed to process result set ", e);
//  // }
//  // }
//  //
//  // return vals;
//  // }
//
//  // /**
//  // * @effects returns an object of <code>c</code> whose id attribute named
//  // * <code>attribute</code> has the value <code>val</code>
//  // */
//  // private Object getObjectByID(Class c, String idAttribute, Object val)
//  // throws SQLException, NotFoundException, NotPossibleException {
//  // Expression[] conditions = new Expression[1];
//  //
//  // conditions[0] = new Expression(idAttribute, "=", val);
//  //
//  // List<DomainConstraint> constraints = DomainManager
//  // .getAttributeConstraints(c);
//  //
//  // String[] fieldNames = new String[constraints.size()];
//  // for (int i = 0; i < constraints.size(); i++) {
//  // fieldNames[i] = constraints.get(i).name();
//  // }
//  //
//  // ResultSet rs = selectAndProject(fieldNames, c.getSimpleName(), conditions,
//  // null);
//  //
//  // Object o = null;
//  // Object v = null;
//  // Type type;
//  // Class refType;
//  // String name;
//  // if (rs.next()) {
//  // List values = new ArrayList();
//  // for (DomainConstraint dc : constraints) {
//  // if (!dc.auto()) {
//  // type = dc.type();
//  // name = dc.name();
//  // v = rs.getObject(name);
//  // if (type.isDomainType()) {
//  // // query the object whose id is the value of this field
//  // // and use that for the object
//  // v = getObjectByID(refType, dcFK.name(), v);
//  // }
//  // values.add(v);
//  // }
//  // }
//  //
//  // o = DomainManager.newInstance(c, values.toArray());
//  // }
//  //
//  // if (o == null) {
//  // throw new NotFoundException("DBToolKit: failed to find object with id "
//  // + val);
//  // }
//  //
//  // return o;
//  // }
//
//  // private void getObject(Object o, String[] attributes, Expression[]
//  // conditions)
//  // throws DBException {
//  //
//  // String name = null;
//  // Object v = null;
//  //
//  // // create a new object instance
//  // final Class c = o.getClass();
//  // String cname = c.getSimpleName();
//  //
//  // // read object records from db
//  // ResultSet rs = selectAndProject(attributes, cname, conditions, null);
//  //
//  // Field f = null;
//  //
//  // if (rs != null) {
//  // try {
//  // // assume one record returns
//  // rs.next();
//  // for (int i = 0; i < attributes.length; i++) {
//  // name = f.getName();
//  // DomainManager.setAttributeValue(o, name, rs.getObject(i + 1));
//  // }
//  // } catch (Exception ex) {
//  // throw new DBException("DBToolKit: failed to get object ", ex);
//  // } finally {
//  // try {
//  // rs.close();
//  // rs.getStatement().close();
//  // } catch (SQLException e) {
//  // //
//  // }
//  // }
//  // }
//  // }
//
//  /**
//   * Populate some <code>attributes</code> of an object <code>o</code> with data
//   * from the data store.
//   * 
//   * @param o
//   *          the object whose attribute values are to be retrieved
//   * @param selectAttributes
//   *          the attributes whose data values are to be retrieved
//   * @param attributes
//   *          the names of the attributes that will be used to identify the
//   *          object in the storage
//   * @param attributeValues
//   *          the values of the specified <code>attributes</code>
//   */
//  // public void getObject(Object o, String[] selectAttributes,
//  // String[] attributes, Object[] attributeValues) throws DBException {
//  // Expression[] conditions = null;
//  //
//  // if (attributes != null) {
//  // conditions = new Expression[attributes.length];
//  // for (int i = 0; i < attributes.length; i++) {
//  // conditions[i] = new Expression(attributes[i], "=", attributeValues[i]);
//  // }
//  // }
//  //
//  // getObject(o, selectAttributes, conditions);
//  // }
//
//  /**
//   * @effects returns <code>num</code> of objects of a domain class from the
//   *          data source, the values of whose domain attributes
//   *          <code>attributes</code> are <code>attributeVals</code>.
//   *          <p>
//   *          If <code>num=-1</code> then return ALL objects.
//   * 
//   *          <p>
//   *          Internally, this method invokes the method
//   *          {@link #readObjects(Class, Expression[], int)} to store objects.
//   * 
//   * @return
//   */
////  public List readObjects(Class c, String[] attributes,
////      Object[] attributeValues, int num) throws DBException {
////    Expression[] conditions = null;
////
////    if (attributes != null) {
////      conditions = new Expression[attributes.length];
////      for (int i = 0; i < attributes.length; i++) {
////        conditions[i] = new Expression(attributes[i], Expression.Op.EQ,
////            attributeValues[i]);
////      }
////    }
////
////    return readObjects(c, conditions, num);
////  }
//
//  /**
//   * @effects returns the objects of a class from the data store. This method
//   *          directly invokes the method
//   *          {@link #readObjects(Class, String[], Object[], int)} with the last
//   *          argument is -1.
//   */
////  public List readObjects(Class c, String[] attributes, Object[] attributeValues)
////      throws DBException {
////    return readObjects(c, attributes, attributeValues, -1);
////  }
//
//  /**
//   * @effects returns all objects of class <code>c</code> stored in the
//   *          database, or <code>null</code> if no objects exist; throws
//   *          <code>DBException</code> if errors occurred in reading objects
//   *          from the database.
//   * 
//   * @requires a table was created for class <code>c</code> using the {@see
//   *           #createTable(Class)} method.
//   */
//  @Override
//  public <T> List<T> readObjects(Class<T> c) throws DataSourceException {
//    return (List<T>) readObjects(c, null, -1);
//  }
//
//  /**
//   * @effects 
//   *  invoke {@link #readObjects(Class, Expression[], int)} with <tt>(c, null, num)</tt>
//   * @version 2.7.3
//   */
//  @Override
//  public <T> List<T> readObjects(Class<T> c, int num) throws NotFoundException, DataSourceException {
//    return (List<T>) readObjects(c, null, num);
//  }
//
//
//  /**
//   * @requires 
//   *  a table corresponding to class c has been created in the data source /\ 
//   *  currId is a valid domain object id of c
//   * 
//   * @effects
//   *  reads from the data source and returns the Oid of the domain object that  
//   *  immediately precedes currId (in natural ordering); or null if no such Oid exists
//   *   
//   *  <p>throws DBException if fails to read from data source;
//   *  NotPossibleException if id values are invalid
//   */
//  public Oid readIdFirstBefore(Class c, DomainConstraint idAttrib, Oid currId) 
//      throws DataSourceException, NotPossibleException {
//    /**
//     * Pseudocode:
//     * <pre>
//     *  read from data source(c) the LARGEST record id that is lower than currId
//     *  set id = the first of such id (if exists)
//     *  if id = null
//     *    throw NotFoundException
//     *  else
//     *    return readObject(c,id)
//     * </pre>
//     */
//    Query q = new Query();
//    q.add(new Expression(idAttrib.name(), Op.LT, currId.getIdValue(0)));      
//    String aggregateFunc = "max";
//    Collection<Oid> oids = readObjectIds(c, aggregateFunc, q);
//
//    
//    if (oids == null) // not found
////      throw new NotFoundException(NotFoundException.Code.RECORD_ID_NOT_FOUND, 
////          "Không tìm thấy mã dữ liệu {0}<{1}>", c.getSimpleName(), q);
//      return null;
//    else {
//      // return the id
//      Oid oid = oids.iterator().next();
//      return oid;
//    }
//  }
//
//  /**
//   * @requires 
//   *  a table corresponding to class c has been created in the data source /\ 
//   *  currId is a valid domain object id of c
//   * 
//   * @effects
//   *  reads from the data source and returns the Oid of domain object that 
//   *  immediately proceeds currId (in natural ordering); or null if no such Oid exists
//   *   
//   *  <p>throws DBException if fails to read from data source;
//   *  NotPossibleException if id values are invalid
//   */
//  public Oid readIdFirstAfter(Class c, DomainConstraint idAttrib, Oid currId) 
//      throws DataSourceException, NotPossibleException {
//    /**
//     * Pseudocode:
//     * <pre>
//     *  read from data source(c) the SMALLEST record id that is greater than currId
//     *  set id = such id (if exists)
//     *  if id = null
//     *    throw NotFoundException
//     *  else
//     *    return readObject(c,id)
//     * </pre>
//     */
//    Query q = new Query();
//    q.add(new Expression(idAttrib.name(), Op.GT, currId.getIdValue(0)));      
//    String aggregateFunc = "min";
//    Collection<Oid> oids = readObjectIds(c, aggregateFunc, q);
//    
//    if (oids == null) // not found
//      return null;
////      throw new NotFoundException(NotFoundException.Code.RECORD_ID_NOT_FOUND, 
////          "Không tìm thấy mã dữ liệu {0}<{1}>", c.getSimpleName(), q);
//    else {
//      // return the oid
//      Oid oid = oids.iterator().next();
//      return oid;
//    }
//  }
//
//  @Override
//  public <T> T readObject(final Class<T> c, final Oid oid) throws NotPossibleException, NotFoundException, DataSourceException {
//    return readObject(c, oid, null, null, true);
//  }
//  
//  @Override
//  public <T> T reloadObject(Class<T> c, final Oid oid) throws NotPossibleException, NotFoundException, DataSourceException {
//    return readObject(c, oid, null, null, false);
//  }
//  
//  @Override
//  public <T> T readAssociatedObject(final Class<T> c, final Oid oid, 
//      Class fromAssocCls, DomainConstraint fromLinkedAttrib) throws NotPossibleException, NotFoundException, DataSourceException {
//    return readObject(c, oid, fromAssocCls, fromLinkedAttrib, true);
//  }
//  
//  /**
//   * This method is invoked by:
//   * <ul>
//   *  <li>{@link #readObject(Class, Oid)}
//   *  <li>{@link #readAssociatedObject(Class, Oid, Class, DomainConstraint)}
//   * </ul>
//   * 
//   * @requires 
//   *  sourceCls != null -> sourceAttrib != null
//   *  
//   * @throws NotFoundException if <tt>loadAssociateIfNotFound = false</tt> but an associated object is not found  
//   * @throws NotPossibleException if failed to create object from the data source record
//   * @throws DataSourceException if failed to read record from the data source
//   * 
//   * @version 3.9
//   *  add parameter
//   */
//  private <T> T readObject(final Class<T> c, final Oid oid, 
//      Class sourceCls, DomainConstraint sourceAttrib, 
//      boolean loadAssociateIfNotFound // v3.0
//      ) throws NotPossibleException, 
//      NotFoundException,  // v3.0 
//      DataSourceException {
//    Class<? extends T> baseCls = oid.getCls();
//    
//    if (debug)
//       System.out.printf("%s.readObject(%s, %s, %s, %s)%n", this.getClass().getSimpleName(), 
//           baseCls.getSimpleName(), oid, 
//           (sourceCls != null) ? sourceCls.getSimpleName() : null, 
//           (sourceAttrib != null) ? sourceAttrib.name() : null);
//
//    // now if c has sub-types then determine the sub-type of the object
//    // with the specified oid; otherwise use c
//    Class[] subs = dom.getDsm().getSubClasses(c);
//   
//    if (subs != null) {
//      // has sub-types
//      for (Class subType : subs) {
//        if(existRecord(subType, oid)) {
//          // found -> break
//          baseCls = subType;
//          break;
//        }
//      }
//    }
//
//    // TODO: (defensive programming) if baseCls is abstract and the object was not found in any of its sub-types (above)
//    // then we should throw a NotPossibleException here
//    
//    // read database records for the object whose id is specified by oid
//    ResultSet rs = readRecord(baseCls, oid);
//
//    if (rs == null)
//      return null;
//
//    // read the domain attributes of the class
//    // we will use these attributes to parse the object values
//    List<Field> fields = dom.getDsm().getSerialisableDomainAttributes(baseCls);
//
//    int numColumns = fields.size();
//
//    // the objects of c that will be read
//
//    // get the id columns and check if one of them is auto-generated
//    Field f = null;
//    DomainConstraint dc;
//    DomainConstraint[] refDcs;
//    Object[] refValues;
//    Type type;
//    Class domainType;
//
//    List values;
//    T o = null;
//    Object v = null;
//    Oid roid;
//    
//    // v2.6.4.b
//    DomainConstraint linkedAttrib;
//    java.util.Map<Object,DomainConstraint> linkLater = new HashMap<Object,DomainConstraint>();
//    
//    try {
//      // this loop iterates only once through the record of the specified object
//      REC: 
//        //v2.7.4: replaced by first()
//        // while (rs.next()) {
//        if (rs.first()) {
//        values = new ArrayList();
//
//        /*
//         *  make a pass through the domain attributes and read their values
//         *  from the current record. 
//         *  
//         *  If reflexive=true and the value of the concerned FK attribute is not null and 
//         *  the referred object has not been read then we put the current values into 
//         *  the delayed queue to process later
//         */
//        FIELD: for (int i = 0; i < numColumns; i++) { //fields.size(); i++) {
//          f = (Field) fields.get(i);
//          dc = f.getAnnotation(DC);
//          type = dc.type();
//          
//          // we only use id and non-auto-generated attribute to create object
//          //v2.7.3: if (dc.id() || !dc.auto()) {
//            if (!type.isDomainType()) {
//              // read the sql value 
//              v = sqlToJava(baseCls, dc, rs, i+1);
//            } else {
//                // domain type attribute -> an associated object
//                domainType = f.getType();
//                if (domainType == sourceCls && dc.equals(sourceAttrib)) {
//                  // domainType is the associated class that causes this loading (e.g. 1:1 association)
//                  // AND that the association between the two is via the same association 
//                  // that sources the loading 
//                  //     then skip (b/c otherwise it is a loop)
//                  continue FIELD;
//                }
//                
//                // query the object whose id is the value of this field
//                // and use that for the object
//                refDcs = dom.getDsm().getIDAttributeConstraints(domainType);
//
//                // FK values
//                refValues = new Object[refDcs.length];
//                v = sqlToJava(baseCls, refDcs[0], rs, i+1);
//                if (v != null) {  // FK value is specified, look it up
//                  refValues[0] = v;
//                  if (refDcs.length > 1) {
//                    // if it is a compound key then we must
//                    // read the subsequent values in this record to complete
//                    // the id
//                    int j = 1;
//                    for (i = i + 1; i < i + refDcs.length; i++) {
//                      //Field f1 = (Field) fields.get(i);
//                      //DomainConstraint dc1 = f1.getAnnotation(DC);
//                      refValues[j] = sqlToJava(baseCls, refDcs[j], rs, i); //rs.getObject(i);
//                      j++;
//                    }
//                  }
//                  
//                  // look up the object, load if not found
//                  
//                  // first look up the object id, load if not found
//                  
//                  /*v3.0: support loadAssociateIfNotFound option 
//                  roid = dom.retrieveObjectId(domainType, refDcs, refValues); 
//                  */
//                  if (loadAssociateIfNotFound) {
//                    roid = dom.retrieveObjectId(domainType, refDcs, refValues);
//                  } else {
//                    roid = dom.lookUpObjectId(domainType, refDcs, refValues);
//                    if (roid == null) {
//                      // should not happen -> error
//                      throw new NotFoundException(NotFoundException.Code.OBJECT_ASSOCIATE_ID_NOT_FOUND, 
//                          new Object[] {domainType.getName(), Arrays.toString(refDcs), Arrays.toString(refValues)});
//                    }
//                  }
//                  
//                  v = dom.lookUpObject(domainType, roid);
//                  
//                  if (debug)
//                    System.out.printf("  associated type: %s <attribute: %s>%n  --> value: %s%n",domainType, f, v);
//                  
//                  if (v == null) {
//                    // not found -> to load
//                    // v3.0: added this check for the option
//                    if (!loadAssociateIfNotFound) {
//                      // not to load associate -> throws error
//                      throw new NotFoundException(NotFoundException.Code.OBJECT_ASSOCIATE_NOT_FOUND, 
//                          new Object[] {domainType.getName(), Arrays.toString(refDcs), Arrays.toString(refValues)});
//                    }
//                    
//                    if (debug)
//                      System.out.printf("  loading linked object %s<%s>%n",domainType, roid);
//                    
//                    // if domainType is a super-type then the id can belong to 
//                    // one of the sub-types or the super-type, so we need to 
//                    // try loading from each of them until found
//                    // TODO: is there a faster way for handling this situation?
//                    Class[] subTypes = dom.getDsm().getSubClasses(domainType);
//                    if (subTypes != null) {
//                      // has sub-types
//                      for (Class subType : subTypes) {
//                        try {
//                          //v2.7.4: v = dom.loadObject(subType, roid, null);
//                          v = dom.loadAssociatedObject(subType, roid, c, dc);
//                          
//                          if (v != null) 
//                            // found -> break
//                            break;
//                        } catch (NotFoundException ex) {
//                          // ignore 
//                        }
//                      }
//                      
//                      if (v == null) {
//                        // not found in any sub-types -> try the super-type
//                        // v2.7.4: v = dom.loadObject(domainType, roid, null);
//                        v = dom.loadAssociatedObject(domainType, roid, c, dc);
//                      }
//                    } else {  
//                      // no subtypes
//                      // v2.7.4: v = dom.loadObject(domainType, roid, null);
//                      v = dom.loadAssociatedObject(domainType, roid, c, dc);
//                    }
//                    
//                    // v2.6.4.b: if domainType is determined by c in a 1:1 association via f
//                    // then record v for update the association link later
//                    linkedAttrib = dom.getDsm().getLinkedAttribute(c, dc);
//                    if (linkedAttrib != null && 
//                        dom.getDsm().isDeterminedByAssociate(domainType, linkedAttrib)) {
//                      linkLater.put(v, linkedAttrib);
//                    }
//                    if (debug)
//                      System.out.printf("  --> %s%n",v);
//                  }                    
//                }
//              } // end domain type
//          
//            values.add(v);
//          //} // end if
//        } // end for(fields) loop
//
//        /* 
//         *  create object
//         **/
//        try {
//          o = dom.getDsm().newInstance(baseCls, values.toArray());
//          
//          // v2.6.4.b: if there are linked objects to be updated, then update them
//          if (!linkLater.isEmpty()) {
//            Object lo;
//            for (Entry<Object,DomainConstraint> e : linkLater.entrySet()) {
//              lo = e.getKey();
//              linkedAttrib = e.getValue();
//              dom.setAttributeValue(lo, linkedAttrib.name(), o);
//            }
//          }
//          
//        } catch (Exception e) {
//          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,
//              new Object[] {baseCls.getSimpleName(), values});
//        }
//      } // end while rs
//    } catch (SQLException ex) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, ex,
//          "Lỗi xử lí kết quả dữ liệu {0}", baseCls.getSimpleName());
//    } finally {
//      try {
//        rs.close();
//        rs.getStatement().close();
//      } catch (SQLException e) {
//        //
//      }
//    }
//    
//    return o;
//  }  
//  
//  /**
//   * This method is similar to {@link #readRecords(Class, Expression[])} except that 
//   * it reads one record matching a specified object id instead of several records.
//   * 
//   * @requires 
//   *  c is the base class of <tt>oid</tt> 
//   *  (i.e. either <tt>c</tt> has no sub-types OR <tt>oid</tt> does not refer to an object of any sub-type of <tt>c</tt>)
//   *   
//   * @effects returns the <tt>ResultSet</tt> containing a <b>single</b> database record of the
//   *          domain table of the domain class <tt>c</tt> for the object id <tt>oid</tt>. 
//   *  <br>Throws <tt>DBException</tt> if an error occurred.  
//   * 
//   * @pseudocode
//   * We use the following basic algorithm:
//   * 
//   * <pre>
//   *  execute a join SQL statement recursively from c upward with any super-types 
//   *    (to get all the fields) to obtain the record
//   * </pre>
//   * 
//   * <p><b>The SQL basically:</b> <br>
//   * -----------------------------<br>
//   * - is a joined query between the sub-class and each super domain class on the
//   * id attribute(s) <br>
//   * - contains a select clause which includes all attributes of the sub-class and non-id
//   * attributes of the super-class <br>
//   * <p>
//   * EXAMPLE 1: ElectiveModule is a sup-class of Module
//   * 
//   * <pre>
//   * select t2.*,t1.deptname 
//   * from electivemodule t1, module t2 
//   * where
//   *  t1.code=t2.code;
//   * </pre>
//   * 
//   * <p>
//   * EXAMPLE 2: instructor is a subclass of staff is a subclass of person
//   * 
//   * <pre>
//   * select t1.title,t2.deptname,t2.joindate,t3.* 
//   * from instructor t1, staff t2, person t3 
//   * where
//   *  t1.id = t2.id and 
//   *  t2.id = t3.id;
//   * </pre>
//   */
//  private ResultSet readRecord(Class c, 
//      final Oid oid)
//      throws DataSourceException {
//    //Class c = oid.getCls();
//    
//    ResultSet rs = null;
//
//    Stack<String> from = new Stack();
//    Stack<String> select = new Stack();
//    Stack<Expression> where = new Stack();
//    
//    StringBuffer orderBy = new StringBuffer("order by ");
//
//    // initialise a String[] array for the id attribute names of the Oid 
//    String[] idAttributeNames = new String[oid.size()];
//    
//    final String cname = dom.getDsm().getDomainClassName(c);
//    Class sup = dom.getDsm().getSuperClass(c);
//    
//    String cTable;
//    if (sup != null) {
//      List<Field> fields;
//      List<Field> idFields;
//
//      // has super-type
//      int index = 1;
//
//      Class currentClass = c;
//      String currentTable = "t" + (index++);
//      cTable = currentTable;
//      from.push(cname + " " + currentTable);
//      // add the non-id attributes of the current class to select
//      fields = dom.getDsm().getSerialisableAttributes(c);
//      DomainConstraint dc;
//      String n;
//      int colIndex = 0;
//      for (Field f : fields) { // super class table
//        dc = f.getAnnotation(DC);
//        if (!dc.id()) { // non-id attributes
//          if (dc.type().isDomainType()) {
//            n = toDBColumnName(c, dc, false);
//          } else
//            n = dc.name();
//          select.add(colIndex++, currentTable + "." + n);
//        }
//      }
//
//      // create table-specific column name(s) for the id attribute(s) 
//      for (int i = 0; i < oid.size(); i++) {
//        idAttributeNames[i] = currentTable + "." + oid.getIdAttributeName(i);
//      }
//       
//      String supName;
//      String supTable;
//      // recursively loop upward to add attributes of the super-class(es)
//      do {
//        supName = dom.getDsm().getDomainClassName(sup);
//        supTable = "t" + (index++);
//        from.push(supName + " " + supTable);
//
//        idFields = dom.getDsm().getIDAttributes(sup);
//
//        // use the id attributes to add new join expressions
//        for (Field f : idFields) { // current table
//          // add join expressions between the id attributes of the two tables
//          where.add(
//              new Expression(currentTable + "." + f.getName(),
//              Expression.Op.EQ, supTable + "." + f.getName(),
//              Expression.Type.Metadata)
//              );
//        } // end for
//
//        // add the non-id attributes of the super class to the
//        // select clause
//        fields = dom.getDsm().getSerialisableAttributes(sup);
//        colIndex = 0;
//        for (Field f : fields) { // super class table
//          dc = f.getAnnotation(DC);
//          if (!dc.id()) { // non-id attributes
//            if (dc.type().isDomainType()) {
//              n = toDBColumnName(sup, dc, false);
//            } else
//              n = dc.name();
//            select.add(colIndex++, supTable + "." + n);
//          }
//        } // end for
//
//        // recursive: check the super-super class and so on...
//        currentTable = supTable;
//        currentClass = sup;
//        sup = dom.getDsm().getSuperClass(sup);
//      } while (sup != null);
//
//      // add the id attributes of the top-level super class to select
//      colIndex = 0;
//      int find = 0;
//      for (Field f : idFields) {
//        dc = f.getAnnotation(DC);
//        if (dc.type().isDomainType()) {
//          n = toDBColumnName(sup, dc, false);
//        } else {
//          n = dc.name();
//        }
//        select.add(colIndex++, supTable + "." + n);
//        // order by these ids
//        orderBy.append(supTable + "." + n);
//        if (find < idFields.size() - 1)
//          orderBy.append(",");
//        find++;
//      }
//    } else {
//      // no super-type, just look in the current table 
//      List<Field> idFields;
//      cTable = cname;
//
//      // example sql: select * from student
//      from.add(cTable);
//      select.add("*");
//
//      // order by the id fields
//      idFields = dom.getDsm().getIDAttributes(c);
//      int find = 0;
//      for (Field f : idFields) { // current table
//        orderBy.append(f.getName());
//        if (find < idFields.size() - 1)
//          orderBy.append(",");
//        find++;
//      }
//      
//      // create normal column name(s) for the id attribute(s) (i.e. without the table symbol prefix)
//      for (int i = 0; i < oid.size(); i++) {
//        idAttributeNames[i] = oid.getIdAttributeName(i);
//      }
//    }
//
//    // ascending order (if not the default)
//    orderBy.append(" ASC");
//
//    // update exps with an id expression(s) created for the id attributes in Oid  
//    // (using the column name(s) of these attributes created above) 
//    Expression exp;
//    for (int ind = 0; ind < oid.size(); ind++) {
//      exp = new Expression(idAttributeNames[ind],Op.EQ,oid.getIdValue(ind));
//      where.add(exp);
//    }
//    
//    // execute the SQL and return the result
//    if (!where.isEmpty()) {
//      rs = selectAndProject(select.toArray(new String[select.size()]), //
//              from.toArray(new String[from.size()]), //
//              where.toArray(new Expression[where.size()]), //
//              null, //
//              null, // group by 
//              orderBy.toString());
//    } else {
//      rs = selectAndProject(select.toArray(new String[select.size()]), //
//              from.toArray(new String[from.size()]), //
//              null, //
//              null, //
//              null, // group by 
//              orderBy.toString());
//          
//    }
//
//    return rs;
//  }
//  
//  /**
//   * @effects 
//   *  if exists a record in the table of <tt>c</tt> whose PK column value(s) 
//   *  are equal to those specified in <tt>id</tt>
//   *    return true
//   *  else
//   *    return false
//   */
//  public boolean existRecord(Class c, Oid id) {
//    int numIds = id.size();
//    String cTable = dom.getDsm().getDomainClassName(c);
//    
//    StringBuffer sqlB = new StringBuffer("select * from ");
//    sqlB.append(cTable);
//    sqlB.append(" where ");
//    DomainConstraint idAttrib;
//    Object idVal;
//    
//    for (int i = 0; i < numIds; i++) {
//      idAttrib = id.getIdAttribute(i);
//      idVal = id.getIdValue(i);
//      sqlB.append(idAttrib.name()).
//        append("=").  // idVal is never null
//        append(toSQLString(idAttrib.type(), idVal, true)); 
//      if (i < numIds-1) {
//        sqlB.append(" and ");
//      }
//    }
//    
//    String sql = sqlB.toString();
//    
//    if (debug)
//      System.out.println("DBToolKit.existRecord: sql = " + sql);
//    
//    try {
//      ResultSet rs = executeQuery(sql);
//      
//      if (rs.next()) {
//        // exists
//        return true;
//      } else {
//        return false;
//      }
//    } catch (Exception e) {
//      // something wrong, ignore
//      return false;
//    }
//  }
//  
//  /**
//   * @requires 
//   *   c is a domain class  /\ 
//   *   linkedObjId is a valid object id /\ 
//   *   assoc and targetAssoc are two ends of an association from c to linked object's class
//   *   
//   * @effects 
//   *  If exist object ids of <tt>c</tt> that are of the objects linked to the domain object 
//   *  identified by <tt>linkedObjId</tt> via the specified association <tt>assoc</tt>
//   *    return them as Collection
//   *  else
//   *    return null 
//   *  
//   *  <br>throws NotPossibleException if id values are invalid or 
//   *  DBException if fails to read ids from the data source.
//   */
//  public Collection<Oid> readObjectIds(Class c, Tuple2<DomainConstraint,Association> assoc, 
//      Oid linkedObjId) 
//  throws NotPossibleException, DataSourceException {
//    Query query = null;
//    
//    if (assoc != null) {
//      DomainConstraint fkAttrib = assoc.getFirst();
//      Class targetClass = assoc.getSecond().associate().type();
//      DomainConstraint[] idAttribs = dom.getDsm().getIDAttributeConstraints(targetClass);
//      DomainConstraint pkAttrib = idAttribs[0];
//      String fkColName = getFKColName(fkAttrib, pkAttrib);
//      
//      query = new Query();
//      query.add(new Expression(fkColName, Op.EQ, linkedObjId.getIdValue(0)));
//    }
//    
//    return readObjectIds(c, query);
//  }
//
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
////      if (myQuery == null)
////        myQuery = new Query();
////      
////      myQuery.add(new Expression(idName, Expression.Op.NOIN, nestedSQL.toString(),
////          Expression.Type.Nested));
//    } // end sub-types 
////    else {
////      // no sub-types, just read the object ids of c (below)
////    }
//    
////    for (int i = 0; i < idAttribs.length; i++) {
////      dc =  idAttribs[i];
////      // TODO: aggregateFunc does not need groupBy
////      if (aggregateFunc != null)
////        sb.append(aggregateFunc).
////          append("(").
////          append(dc.name()).
////          append(")");
////      else
////        sb.append(dc.name());
////      
////      if (i < idAttribs.length-1) 
////        sb.append(" ,");
////    }
//    
////    // from
////    sqlSb.append(" from ").append(schema.getDsm().getDomainClassName(c));
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
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_STATEMENT, e,
//          "Lỗi thực thi truy vấn {0}", sql);
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
//          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    }
//  
//    return (result.isEmpty()) ? null : result;
//
//    /* TESTING: to support binary-type query terms
//    orderBy.append(" ASC ");
//    
//    String sql = null;
//    ResultSet rs;
//    if (myQuery != null) {
//      // query
//      sqlSb.append(" where ");
//      Iterator<Expression> exps = myQuery.terms();
//      int size = myQuery.size();
//      Expression exp;
//      int colIndex = 0;
//      boolean hasBinaryTerm = false;
//      while (exps.hasNext()) {
//        exp = exps.next();
//        if (exp instanceof ObjectExpression && 
//            ((ObjectExpression) exp).getDomainAttribute().type().isByteArray()) {
//          // a query term that involves binary data -> uses wild cart
//          hasBinaryTerm = true;
//          sqlSb.append(exp.getVar()).append(getSQLOpForBinaryType(exp)).append("?");
//        } else {
//          // convert other query terms directly into SQL text 
//          sqlSb.append(toSQLExpression(exp));
//        }
//        if (colIndex < size-1) {
//          sqlSb.append(" and ");
//        }
//        colIndex++;
//      }
//      
//      // order by (ascending) 
//      sqlSb.append(orderBy);
//      
//      sql = sqlSb.toString();
//      if (debug)
//        System.out.println("DBToolKit.readObjectIds: " + sql);
//
//      PreparedStatement s;
//      DomainConstraint attrib = null;
//      colIndex = 1;
//      Object val = null;
//      
//      // create statement
//      try {
//        s = conn.prepareStatement(sql);
//      } catch (SQLException e) {
//        throw new DBException(DBException.Code.FAIL_TO_CREATE_QUERY, e,
//            "Lỗi tạo truy vấn: {0}", sql);
//      }      
//        
//      if (hasBinaryTerm) {
//        // update statement with values of the binary-type terms
//        Class typeClass;
//        try {
//          while (exps.hasNext()) {
//            exp = exps.next();
//            if (exp instanceof ObjectExpression && 
//                ((ObjectExpression) exp).getDomainAttribute().type().isByteArray()) {
//              attrib = ((ObjectExpression) exp).getDomainAttribute();
//              typeClass = schema.getDsm().getDomainAttribute(c, attrib).getType();
//              val = exp.getVal();
//              javaToSQL(s, colIndex, attrib.type(),typeClass, val);            
//            }
//            colIndex++;
//          }
//        } catch (SQLException e) {
//          throw new DBException(DBException.Code.FAIL_TO_UPDATE, e,
//              "Lỗi cập nhật truy vấn {0} (object: {1}, column ({2}:{3}), value({4}))", 
//              sql, null, colIndex,((attrib!=null) ? attrib.name() : null), val);
//        }  
//      }
//
//      // run statement
//      try {
//        s.execute();
//        
//        rs = s.getResultSet();
//      } catch (SQLException e) {
//        throw new DBException(DBException.Code.FAIL_TO_EXECUTE_STATEMENT, e,
//            "Lỗi thực thi truy vấn {0}", sql);
//      } 
//      //error: this causes an exception when processing the result set below
//      // finally {   
//        //try {s.close();} catch (SQLException e) {}
//      //}
//    } else {
//      // no query
//      // order by (ascending) 
//      sqlSb.append(orderBy);
//      
//      sql = sqlSb.toString();
//      if (debug)
//        System.out.println("DBToolKit.readObjectIds: " + sql);
//
//      try {
//        rs = executeQuery(sql);
//      } catch (Exception e) {
//        throw new DBException(DBException.Code.FAIL_TO_EXECUTE_STATEMENT, e,
//            "Lỗi thực thi truy vấn {0}", sql);
//      }      
//    }
//    
//    // process result set
//    if (rs != null) {
//      Oid oid;
//      Object idVal;
//      try {
//        while (rs.next()) {
//          oid = null;
//          for (int i = 1; i <= idAttribs.length; i++) {
//            dc = idAttribs[i-1];
//            idVal = sqlToJava(dc, rs, i);
//    
//            if (idVal != null) {
//              if (!(idVal instanceof Comparable))
//                throw new NotPossibleException(NotPossibleException.Code.INVALID_OBJECT_ID_TYPE, 
//                    "Mã đối tượng không hợp lệ {0}<{1}>:{2} (cần kiều Comparable)",c.getSimpleName(),"-",idVal);
//    
//              if (oid == null) oid = new Oid(c);
//              
//              oid.addIdValue(dc, (Comparable) idVal);
//            }
//          }
//          
//          if (oid != null)
//            result.add(oid);
//        }
//      } catch (SQLException e) {
//        throw new DBException(DBException.Code.FAIL_RESULT_SET, e,
//            "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//      }
//    }
//    
//    return (result.isEmpty()) ? null : result;
//    */
//  }
//  
//  // represents an SQL table spec <var,name>
//  private class TableSpec {
//    Class cls;
//    String var;
//    String name;
//    
//    TableSpec(Class cls, String var, String name) {
//      this.cls = cls;
//      this.var = var;
//      this.name = name;
//    }
//    
//    @Override
//    public String toString() {
//      if (var != null)
//        return name + " " + var;
//      else
//        return name;
//    }
//        
//    @Override
//    public boolean equals(Object obj) {
//      if (this == obj)
//        return true;
//      if (obj == null)
//        return false;
//      if (getClass() != obj.getClass())
//        return false;
//      TableSpec other = (TableSpec) obj;
//
//      if (cls == null) {
//        if (other.cls != null)
//          return false;
//      } else if (cls != other.cls)
//        return false;
//      return true;
//    }        
//  }
//  
//  // represents an SQL table join
//  private class JoinSpec {
//    JoinSpec(Class c1, DomainConstraint a1, Class c2, DomainConstraint a2) {
//      this.c1 = c1;
//      this.c2 = c2;
//      this.a1 = a1;
//      this.a2 = a2;
//    }
//    Class c1, c2;
//    DomainConstraint a1, a2;
//    
//    @Override
//    public String toString() {
//      return c1.getSimpleName() + "." + a1.name() + "="+
//             c2.getSimpleName() + "." + a2.name();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//      if (this == obj)
//        return true;
//      if (obj == null)
//        return false;
//      if (getClass() != obj.getClass())
//        return false;
//      
//      JoinSpec other = (JoinSpec) obj;
//
//      if (a1 == null) {
//        if (other.a1 != null)
//          return false;
//      } else if (!a1.equals(other.a1))
//        return false;
//      if (a2 == null) {
//        if (other.a2 != null)
//          return false;
//      } else if (!a2.equals(other.a2))
//        return false;
//      if (c1 == null) {
//        if (other.c1 != null)
//          return false;
//      } else if (!c1.equals(other.c1))
//        return false;
//      if (c2 == null) {
//        if (other.c2 != null)
//          return false;
//      } else if (!c2.equals(other.c2))
//        return false;
//      return true;
//    }
//  }
//  
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
//  public Collection<Oid> readObjectIds(final Class c, String aggregateFunc, final Query query) 
//  throws NotPossibleException, DataSourceException {
//    /*v2.7: use a sorted Set for result if there are sub-types
//    // the result
//    Collection<Oid> result = new ArrayList<Oid>();*/
//    Collection<Oid> result;
//    Class[] subTypes = dom.getDsm().getSubClasses(c);
//    if (subTypes != null) {
//      // use sorted set
//      result = new TreeSet<Oid>();
//    } else {
//      result = new ArrayList<Oid>();
//    }
//      
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
//    java.util.Map<Class,TableSpec> From = new HashMap<Class,TableSpec>();
//    Collection<JoinSpec> Joins = new ArrayList<JoinSpec>();
//    Collection<String> Where = null; 
//    StringBuffer orderBy = new StringBuffer(" order by ");
//    
//    // table var (if any) that refers to the table containing the attrib
//    //String tableVar = null;
//    final String cTable = dom.getDsm().getDomainClassName(c);
//    TableSpec cTableSpec = new TableSpec(c, null, cTable);
//    
//    // update From, Where from query
//    if (query != null) {
//      Where = new ArrayList();
//      Iterator<Expression> exps = query.terms();
//      Expression exp;
//      
//      int tableIndex = 0; // the index used to create table vars
//      int[] tableInd = {tableIndex};// use array to pass table index
//      
//      while (exps.hasNext()) {
//        exp = exps.next();
//        if (exp instanceof ObjectExpression) {
//          // if exp.attrib belongs to a super-class then must create a join
////          String tempVar = updateQuery(From, Where, (ObjectExpression) exp);
////          if (tableVar == null) //  only need to update tableVar once
////            tableVar = tempVar;
//          updateQuery(From, Where, cTableSpec, tableInd, Joins, (ObjectExpression) exp);
//        } else {
//          // sub-query expression
//          Where.add(toSQLExpression(exp,true));
//        }
//      }
//    }
//
//    // determine if we need to read ids of the sub-types
//    
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
//          if (debug)
//            throw e;
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
//      String idColName = ((cTableSpec.var != null) ? cTableSpec.var : cTable) + "." + cid;
//          //((tableVar != null) ? tableVar : cTable) + "." + cid; 
//      Where.add(idColName + " not in (" + nestedSQL.toString()+")");
//    } // end sub-types 
//    
//    if (cTableSpec.var == null) {//(tableVar == null) {
//      // no table var was generated from the update of Where, only use cTable as table name 
//      From.put(c, new TableSpec(c, null, cTable));  // cTable
//    }
//    
//    /*
//     * create query
//     */
//    // select the id attribute of c
//    sqlSb.append("select ");
//    DomainConstraint dc;
//    final DomainConstraint[] idAttribs = dom.getDsm().getIDAttributeConstraints(c);
//
//    // ASSUME: one id attribute (see above) (change this to use the loop (below) if 
//    // this restriction is removed)
//    dc =  idAttribs[0];
//
//    String colName = (cTableSpec.var == null) ? dc.name() : cTableSpec.var + "." + dc.name(); 
//        //(tableVar == null) ? dc.name() : tableVar + "." + dc.name();
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
//    //for (String f : From) {
//    for(TableSpec s : From.values()) {
//      sqlSb.append(s.toString());
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
//      throw new DataSourceException(DataSourceException.Code.FAIL_TO_EXECUTE_STATEMENT, e,
//          "Lỗi thực thi truy vấn {0}", sql);
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
//          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    }
//  
//    return (result.isEmpty()) ? null : result;
//  }
//  
//  /**
//   * @effects 
//   *  return a copy of <tt>sourceQuery</tt> that contains the same terms that are defined 
//   *  over the domain class <tt>c</tt>  
//   */
//  private Query rewriteQuery(Query sourceQuery, Class c) {
//    Query newQuery = new Query();
//    Iterator<Expression> terms = sourceQuery.terms();
//    Expression exp, newExp;
//    ObjectExpression oexp;
//    ObjectJoinExpression oje;
//    while (terms.hasNext()) {
//      exp = terms.next();
//      /*v2.7.2: support join expression */
//      if (exp instanceof ObjectJoinExpression) {
//        newExp = ObjectJoinExpression.createInstance(c, (ObjectJoinExpression) exp);
//      } else if (exp instanceof ObjectExpression) {
//        // copy as an object expression over c
//        //oexp = (ObjectExpression) exp;
//        newExp = ObjectExpression.createInstance(c, (ObjectExpression) exp);//new ObjectExpression(c, oexp.getDomainAttribute(), exp.getOperator(), exp.getVal());
//      } else
//        // copy the same
//        newExp = Expression.createInstance(exp); //new Expression(exp.getVar(), exp.getOperator(), exp.getVal());
//      
//      newQuery.add(newExp);
//    }
//    
//    return newQuery;
//  }
//  
//  /**
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
//  
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
//      final ObjectJoinExpression jexp) {
//     
//     /*
//      *  let e1 = e.val
//      *  process e by creating an SQL join between e and e1 
//      *  if e1 is another join expression (i.e. a join chain)
//      *    invoke a recursive call on e1
//      *  else
//      *   invoke updateQueryWithObjectExpression on e1 
//      */
//    final Class c = jexp.getDomainClass();
//    final DomainConstraint attrib = jexp.getDomainAttribute();
//    ObjectExpression valExp = jexp.getTargetExpression();
//
//    // the class that is joined with c
//    final Class jc = jexp.getTargetExpression().getDomainClass();
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
//    } else {
//      // process as a normal object expression
//      updateQueryWithObjectExpression(From, Where, jcTableSpec, tableInd, Joins, valExp);
//    }
//  }
//
//  /**
//   * @modifies  From, Where, tableSpec, tableInd, Joins 
//   * 
//   * @effects 
//   *  return a <tt>TableSpec</tt> of the exact table that contains the column corresponding to 
//   *  the domain attribute <tt>c.attrib</tt> (the table may be mapped to a super-class of <tt>c</tt>)
//   */
//  private TableSpec updateQueryWithAttribute(
//      final java.util.Map<Class,TableSpec> From, 
//      final Collection<String> Where, 
//      final TableSpec tableSpec, 
//      final int[] tableInd,
//      final Collection<JoinSpec> Joins,
//      final Class c, 
//      final DomainConstraint attrib) {    
//    Class sup = dom.getDsm().getSuperClass(c);
//    int tIndex = tableInd[0];
//    final String cTable = dom.getDsm().getDomainClassName(c);
//    String tcTable;
//    
//    // add c to From
//    TableSpec cTableSpec = From.get(c);
//    if (cTableSpec == null) {
//      tcTable = "t"+(tIndex++);
//      cTableSpec = new TableSpec(c, tcTable, cTable);
//      From.put(c, cTableSpec);//cTable + " " + tcTable);
//    } else {
//      tcTable = cTableSpec.var;
//    }
//
//    final Class a = dom.getDsm().getDeclaringClass(c, attrib);
//    String tA;
//    Collection<DomainConstraint> idAttribs;
//    String supTable, tsupTable;
//    Class currClass;
//    String currTable = cTable;
//    String tcurrTable = tcTable;
//    JoinSpec jspec;
//    TableSpec supSpec;
//    
//    String colName;
//    String colVar;
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
//        // add super class table to FROM (if not already added)
//        supSpec = From.get(sup);
//        if (supSpec == null) {
//          tsupTable = "t" + (tIndex++);
//          supSpec = new TableSpec(sup, tsupTable, supTable);
//          From.put(sup,supSpec);
//        } else {
//          tsupTable = supSpec.var;
//        }
//
//        // use the id attributes to add new join expressions
//        idAttribs = dom.getDsm().getIDDomainConstraints(sup);
//        for (DomainConstraint f : idAttribs) { // current table
//          // (if not already added) add join expressions between the id attributes of the two tables
//          jspec = new JoinSpec(currClass, f, sup, f);
//          if (!Joins.contains(jspec)) {
//            Where.add(
//                join(currClass, tcurrTable, sup, tsupTable, f)
//                );
//            Joins.add(jspec);
//          } 
//        } // end for
//
//        // add super class table to FROM (if not already added)
//        //From.add(supTable + " " + tsupTable);
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
//    // update data structures before return
//    tableInd[0] = tIndex;
//    
//    // if the expression's class is the same as tableSpec and 
//    // the table var in the spec has not be set then set it
//    if (c == tableSpec.cls && tableSpec.var == null) {
//      tableSpec.var = tcTable;
//    }
//
//    return new TableSpec(a, tA, null);
//  }
//  
//  /**
//   * @modifies  From, Where, tableSpec, tableInd, Joins 
//   * 
//   * @effects 
//   *  converts <tt>exp</tt> into a suitable SQL expression and add it to Where; 
//   *  if <tt>exp</tt> is <tt>ObjectExpression</tt> and <tt>exp.attrib</tt> belongs to a super-class then also add an SQL join from 
//   *  <tt>exp.domainClass</tt> to the super-class;
//   *  
//   *  <p>Update <tt>From</tt> with suitable SQL table variables.
//   *  <p>If <tt>tableSpec.cls = exp.domainClass</tt> and <tt>tableSpec.var</tt>  has not been set then 
//   *    set it to a suitable table var
//   */
//  private void updateQueryWithObjectExpression(
//      final java.util.Map<Class,TableSpec> From, 
//      final Collection<String> Where, 
//      final TableSpec tableSpec, 
//      final int[] tableInd,
//      final Collection<JoinSpec> Joins,
//      final ObjectExpression exp) {
//    final Class c = exp.getDomainClass();
//    final DomainConstraint attrib = exp.getDomainAttribute();
//
//    TableSpec attribTableSpec = updateQueryWithAttribute(From, Where, tableSpec, tableInd, Joins, c, attrib);
//    
//    Class a = attribTableSpec.cls;
//    String tA = attribTableSpec.var;
//    
//    String colName = getColName(a, attrib);
//    String colVar = tA + "." + colName;
//    
//    /*
//     *   Add Sql expression to Where
//     */
//    Where.add(toSQLExpression(exp, colVar,true));
//    
//    // add c to From
//    int tIndex = tableInd[0];
//    TableSpec cTableSpec = From.get(c);
//    String cTable = dom.getDsm().getDomainClassName(c);
//    String tcTable;
//    if (cTableSpec == null) {
//      tcTable = "t"+(tIndex++);
//      cTableSpec = new TableSpec(c, tcTable, cTable); 
//      From.put(c,cTableSpec);
//    } else {
//      tcTable = cTableSpec.var;
//    }
//    
//    // update data structure before return
//    tableInd[0] = tIndex;
//    
//    // if the expression's class is the same as tableSpec and 
//    // the table var in the spec has not be set then set it
//    if (c == tableSpec.cls && tableSpec.var == null) {
//      tableSpec.var = tcTable;
//    }
//  }
//  
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
//  
////  /**
////   * @requires 
////   *   c is a domain class  /\ 
////   *   (query != null -> query is a valid Query over c)
////   *   
////   * @effects
////   * <pre> 
////   *  if query is not null 
////   *    translate <tt>query</tt> into a source query and 
////   *    execute this query to find all the object ids of c from the data source
////   *    that satisfy it
////   *  else 
////   *    read all the object ids of c
////   *    
////   *  For a type-hierarchy, the object ids are created with the precise base class, 
////   *  i.e. the class that defines the objects bearing the ids.
////   *  
////   *  If exist object ids matching the query 
////   *    return them as Collection
////   *  else
////   *    return null 
////   *  
////   *  throws NotPossibleException if id values are invalid or 
////   *  DBException if fails to read ids from the data source.
////   *  </pre>   
////   */
////  public Collection<Oid> readObjectIds(final Class c, final Query query) 
////  throws NotPossibleException, DataSourceException {
////    return readObjectIds(c, null, query);
////  }
//  
//
//  /**
//   * @effects 
//   *  read from data source and return a Collection of values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt>, 
//   *  or return <tt>null</tt> if no domain objects of <tt>c</tt> exist
//   */
//  public Collection readAttributeValues(Class c, DomainConstraint attrib) {
//    String sql = genSelect(c, null, attrib, false); //genSelect(c, attrib, false);
//    
//    if (debug)
//      System.out.println("DBToolKit.readAttributeValues: " + sql);
//
//    try {
//      ResultSet rs = executeQuery(sql);
//      Object attribVal;
//      Collection result = new ArrayList();
//      while (rs.next()) {
//        attribVal = sqlToJava(c, attrib, rs, 1);
//        result.add(attribVal);
//      }
//      
//      return result.isEmpty() ? null : result;
//    } catch (Exception e) {
//      e.printStackTrace();
//      return null;
//    }
//  }
//  
//  /**
//   * @effects 
//   *  read from data source and return a Map<Oid,Object> of 
//   *  the Oids and the values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt> 
//   *  (in the same order as retrieved from the data source), 
//   *  or return <tt>null</tt> if no such objects exist
//   */
//  public java.util.Map<Oid,Object> readAttributeValuesWithOids(Class c, DomainConstraint attrib) {
//    String sql = genSelect(c, null, attrib, true); //genSelect(c, attrib, true);
//    
//    if (debug)
//      System.out.println("DBToolKit.readAttributeValuesWithOids: " + sql);
//
//    try {
//      ResultSet rs = executeQuery(sql);
//      Object attribVal, idVal;
//      Oid oid;
//      Collection<DomainConstraint> idAttribs = dom.getDsm().getIDDomainConstraints(c);
//      
//      java.util.Map<Oid,Object> result = new LinkedHashMap<Oid,Object>(); // to preserve record order
//      int colIndex = 1;
//      while (rs.next()) {
//        // generate Oid first
//        oid = new Oid(c);
//        for (DomainConstraint idAttrib : idAttribs) {
//          idVal = sqlToJava(c, idAttrib, rs, colIndex);
//          //TODO: check Comparable of idVal
//          oid.addIdValue(idAttrib, (Comparable) idVal);
//          
//          colIndex++;
//        }
//        
//        // then get the value of attrib
//        attribVal = sqlToJava(c, attrib, rs, colIndex);
//        
//        // reset 
//        colIndex = 1;
//        
//        result.put(oid, attribVal);
//      }
//      
//      return result.isEmpty() ? null : result;
//    } catch (Exception e) {
//      e.printStackTrace();
//      return null;
//    }
//  }
//  
//  /**
//   * 
//   * @effects 
//   *  read from the data source the value of the attribute <tt>attrib</tt> 
//   *  of the domain object identified by <tt>oid</tt> of the domain class <tt>c</tt>
//   *  
//   *  <p>Return <tt>null</tt> if the object with the specified id is not found OR the actual 
//   *  attribute value is <tt>null</tt>
//   *   
//   * @example
//   *  <pre>
//   *  c = Student
//   *  oid = Student(S2014)
//   *  attrib = Student:sclass (value = SClass(id=2)) 
//   *  
//   *  -> result := SClass(id=2) 
//   *  </pre>
//   */
//  public Object readAttributeValue(Class c, Oid oid, DomainConstraint attrib) {
//    String sql = genSelect(c, oid, attrib, false);
//    
//    if (debug)
//      System.out.println("DBToolKit.readAttributeValue: " + sql);
//
//    try {
//      ResultSet rs = executeQuery(sql);
//      Object attribVal;
//      Type type;
//      if (rs.next()) {
//        // get the attribute type: which is either one of the primitive types
//        // or a domain type. The latter requires getting the type of the 
//        // id attribute of the domain type.
//        type = attrib.type();
//        if (type.isDomainType()) {
//          // get the type of the domain type's id attribute
//          Class domainType = dom.getDsm().getDomainClassFor(c, attrib.name());
//          DomainConstraint refIdAttrib = dom.getDsm().getIDDomainConstraints(domainType).get(0);
//          
//          attribVal = sqlToJava(domainType, refIdAttrib, rs, 1);
//        } else {
//          attribVal = sqlToJava(c, attrib, rs, 1);          
//        }
//        
//        return attribVal;
//      } else {
//        // no value
//        return null;
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//      return null;
//    }
//  }
//  
//  /**
//   * @effects
//   *  count and return the number of records of the table mapped to domain class <tt>c</tt> 
//   *  or return -1 if no records are ound 
//   *  <p>Throws DBException if fails to do so
//   */
//  public int readObjectCount(Class c) throws DataSourceException {
//    // sql: select count(*) from t, where t = table(c)
//    String cTable = dom.getDsm().getDomainClassName(c);
//    String sql = "Select count(*) from " + cTable;
//    
//    if (debug)
//      System.out.println("DBToolKit.readObjectCount: " + sql);
//
//    int count;
//    try {
//      ResultSet rs = executeQuery(sql);
//      Collection result = new ArrayList();
//      if (rs.next()) {
//        count = rs.getInt(1);
//      } else {
//        // no records
//        count = -1;
//      }
//      
//      return count;
//    } catch (Exception e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, 
//          e, "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    }
//  }
//  
//  /**
//   * @requires 
//   *  cls != null /\ assoc != null /\  
//   *  attrib is a valid attribute of cls /\
//   *  linkedObj is a valid domain object  /\ linkedObjOid is the Oid of linkedObj
//   *  
//   * @effects
//   *  load and return from the data source the number of objects  
//   *  of the domain class <tt>cls</tt> that are linked to a given domain object 
//   *  <tt>linkedObj</tt> via the attribute <tt>attrib</tt> (of <tt>cls</tt>).
//   *  
//   *  <p>Throws DBException if fails to retrieve the information from the data source
//   * 
//   *  @example
//   *  <pre>
//   *  c = Enrolment
//   *  attrib = Enrolment.student
//   *  linkedObj = Student<id=S2014>
//   *  
//   *  sql = Select count(*) from Enrolment Where student_id='S2014'
//   *  </pre>   
//   */
//  public int readAssociationLinkCount(Class c, DomainConstraint attrib, 
//      Object linkedObj, Oid linkedObjOid) throws DataSourceException {
//    String table = dom.getDsm().getDomainClassName(c);
//    
//    // the fk col of table that refers to linkedObj
//    String fkName = getColName(c, attrib);
//    
//    // the fk col value
//    Object val = linkedObjOid.getIdValue(0);
//    DomainConstraint refPkAttrib = linkedObjOid.getIdAttribute(0);
//    
//    String sqlVal = toSQLString(refPkAttrib.type(), val, true);
//    
//    String sql = "Select count(*) from "+table+" where "+fkName+ 
//        ((sqlVal != null) ? " = " + sqlVal : " is Null"); // v2.7.2
//    
//    if (debug)
//      System.out.println("DBToolKit.readAssociationLinkCount: " + sql);
//    
//    ResultSet rs = executeQuery(sql);
//    try {
//      rs.next();
//      int result = rs.getInt(1);
//      return result;
//    } catch (SQLException e) {
//      // something wrong
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
//          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    }
//  }
//  
//  /**
//   * @requires 
//   *  a table corresponding to class c has been created in the data source /\ 
//   *  attrib is a valid attribute of c 
//   * 
//   * @effects
//   *  reads from the data source and returns a Tuple2 containing lowest and highest values of 
//   *  the domain attributes <tt>atrib</tt> among the objects of c
//   *   
//   *  <p>throws DBException if fails to read from data source;
//   *  NotFoundException if no value range is found
//   */
//  public Tuple2<Object,Object> readValueRange(Class c, DomainConstraint attrib) throws DataSourceException, NotFoundException {
//    ResultSet rs = readValueRangeFromSource(c, attrib, null);
//    
//    try {
//      if (rs.next()) {  // single-row
//        Object minVal = sqlToJava(c, attrib, rs, 1);
//        Object maxVal = sqlToJava(c, attrib, rs, 2);
//        
//        if (minVal == null && maxVal == null) {
//          throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
//              "Không tìm thấy giá trị mã dữ liệu nào của {0}.{1}", c.getSimpleName(), attrib.name());          
//        }
//        
//        return new Tuple2<Object,Object>(minVal, maxVal);
//      } else {
//        // empty result
//        throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
//            "Không tìm thấy giá trị mã dữ liệu nào của {0}.{1}", c.getSimpleName(), attrib.name());
//      }
//    } catch (SQLException e) {
//      // something wrong
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
//          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    }
//  }
//      
//  /**
//   * @requires 
//   *  a table corresponding to class c has been created in the data source /\ 
//   *  attrib is a valid attribute of c /\
//   *  elements of derivedAttributes are valid attributes of c
//   * 
//   * @effects
//   *  reads from the data source and returns a <tt>Map</tt> containing lowest and highest values of 
//   *  the domain attribute <tt>attrib</tt> among the objects of c, group by the attributes 
//   *  specified by <tt>derivedAttributes</tt>
//   *   
//   *  <p>throws DBException if fails to read from data source;
//   *  NotFoundException if no value range is found;
//   *  IllegalArgumentException if no derived attributes were specified
//   */
//  public java.util.Map<Tuple, Tuple2<Object,Object>> readValueRange(Class c, DomainConstraint attrib, 
//      DomainConstraint[] derivedAttributes) 
//      throws DataSourceException, NotFoundException {
//    
//    if (derivedAttributes == null)
//      throw new IllegalArgumentException("DBToolKit.readValueRange: no derived attributes specified");
//    
//    ResultSet rs = readValueRangeFromSource(c, attrib, derivedAttributes);
//    
//    java.util.Map result = new LinkedHashMap<Tuple, Tuple2<Object,Object>>();
//    
//    try {
//      Serializable[] derivedVals = new Serializable[derivedAttributes.length];
//      Object minVal, maxVal;
//      Tuple t; 
//      Tuple2 mx;
//      int i;
//      while (rs.next()) {  // process all rows
//        // first extract derived attribute values
//        for (i = 0; i < derivedAttributes.length; i++) {
//          // TODO: check Serializable
//          derivedVals[i] = (Serializable) sqlToJava(c, derivedAttributes[i], rs, i+1);
//        }
//        
//        i = i+1;
//        
//        // then extract min, max values
//        minVal = sqlToJava(c, attrib, rs, i);
//        maxVal = sqlToJava(c, attrib, rs, i+1);
//        
//        // prepare min-max tuple
//        if (minVal == null && maxVal == null) {
//          // no value range for this
//          mx = null;
//        } else {
//          mx = new Tuple2<Object,Object>(minVal, maxVal);
//        }
//        
//        // add to result
//        result.put(Tuple.newInstance(derivedVals), mx);
//      } 
//    } catch (SQLException e) {
//      // something wrong
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e,
//          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    }
//    
//    if (result.isEmpty()) {
//      // empty result
//      throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
//          "Không tìm thấy giá trị mã dữ liệu nào của {0}.{1}", c.getSimpleName(), attrib.name());
//    }
//    
//    return result;
//  }
//  
//  /**
//   * @effects read the (min,max) value range of attribute <tt>c.attrib</tt> from
//   *          all the data records of <tt>c</tt>, group by
//   *          <tt>derivingAttributes</tt> (if any)
//   * 
//   *          <p>
//   *          Throws DBException if fails to load data from data source.
//   * 
//   *          <p>
//   *          Note: <tt>c.attrib</tt> may actually be defined in a super- or
//   *          ancestor class of c and is thus inherited in <tt>c</tt>.
//   *          Similarly, each of the deriving attribute in
//   *          <tt>derivingAttributes</tt> may also be defined in some super- or
//   *          ancestor classes of c.
//   * 
//   * @pseudocode The basic idea is to extract only the range of values of the
//   *             data records of <tt>c</tt>, which may be a sub-set of those of
//   *             the super- and ancestor classes of <tt>c</tt>
//   * 
//   *  <pre>
//   *  Let SELECT, FROM, WHERE, GROUP BY be Sets of Strings 
//   *  
//   *  Add c to FROM
//   *  let tA be a String
//   * 
//   *  if attrib is inherited from an ancestor domain class a,  
//   *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
//   *    let s = SQL_Join(c,x,...,a) (x may = a)
//   *    add s to WHERE
//   *    add x,...,a to FROM
//   *    tA = a
//   *  else
//   *    tA = c
//   *  
//   *  if derivingAttributes != null
//   *    for each attribute d in derivingAttributes
//   *      let tD be a String
//   *      if d is inherited from an ancestor domain class b (b != c)
//   *        tD = b
//   *        if b != a AND b has not been processed
//   *          let t = SQL_Join(c,y,...,b) (y may = b)
//   *          merge t into WHERE
//   *          merge y,...,b into FROM
//   *      else
//   *        tD = c
//   *      add tD.d to SELECT
//   *      add tD.d to GROUP BY
//   * 
//   *  Add min(tA.attrib) and max(tA.attrib) to SELECT
//   *   
//   *  Let sql = SQL(SELECT, FROM, WHERE, GROUP BY)
//   *  execute sql 
//   *  return result as RecordSet
//   * </pre>
//   * 
//   * @example <p>
//   *          <b>EXAMPLE 1</b>:
//   * 
//   *          <pre>
//   *  c=Student, 
//   *  attrib=Student.id, derivingAttributes=null
//   *   
//   *  sql = SELECT min(t0.id), max(t0.id)  
//   *        FROM Student t0
//   * </pre>
//   * 
//   *          <p>
//   *          <b>EXAMPLE 2</b>:
//   * 
//   *          <pre>
//   *  c=ElectiveModule, 
//   *  attrib=Module.code (Module=super(ElectiveModule)), 
//   *  derivingAttributes={Module.semester}
//   * 
//   *  sql = SELECT min(t1.code), max(t1.code), t1.semester 
//   *        FROM ElectiveModule t1, Module t2
//   *        WHERE t1.id=t2.id
//   *        GROUP BY semester
//   * </pre>
//   * 
//   *          <p>
//   *          <b>EXAMPLE 3</b>:
//   * 
//   *          <pre>
//   *  c=Instructor, 
//   *  attrib=Instructor.id, 
//   *  derivingAttributes={Person.name,Person.dob} (Person=super(Staff), Staff=super(Instructor))
//   * 
//   *  sql = SELECT min(t1.id), max(t1.id), t3.name, t3.dob 
//   *        FROM Instructor t1, Staff t2, Person t3
//   *        WHERE t1.id=t2.id AND t2.id=t3.id
//   *        GROUP BY t3.name, t3.dob
//   * </pre>
//   * 
//   *          <p>
//   *          <b>EXAMPLE 4</b>:
//   * 
//   *          <pre>
//   *  c=Instructor, 
//   *  attrib=Person.id, 
//   *  derivingAttributes={Person.name,Person.dob} (Person=super(Staff), Staff=super(Instructor))
//   * 
//   *  sql = SELECT min(t3.id), max(t3.id), t3.name, t3.dob 
//   *        FROM Instructor t1, Staff t2, Person t3
//   *        WHERE t1.id=t2.id AND t2.id=t3.id
//   *        GROUP BY t3.name, t3.dob
//   * </pre>
//   * 
//   *          <p>
//   *          <b>EXAMPLE 5</b>:
//   * 
//   *          <pre>
//   *  c=Instructor, 
//   *  attrib=Staff.code, 
//   *  derivingAttributes={Person.name,Person.dob} (Person=super(Staff), Staff=super(Instructor))
//   * 
//   *  sql = SELECT min(t2.code), max(t2.code), t3.name, t3.dob 
//   *        FROM Instructor t1, Staff t2, Person t3
//   *        WHERE t1.id=t2.id AND t2.id=t3.id
//   *        GROUP BY t3.name, t3.dob
//   * </pre>
//   */
//  private ResultSet readValueRangeFromSource(Class c, DomainConstraint attrib, 
//      DomainConstraint[] derivingAttributes) throws DataSourceException {
//    Stack<String> Select = new Stack();
//    Stack<String> From = new Stack();
//    Stack<String> Where = new Stack();
//    Stack<String> GroupBy = new Stack();
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
//    Collection<DomainConstraint> idFields;
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
//        idFields = dom.getDsm().getIDDomainConstraints(sup);
//        for (DomainConstraint f : idFields) { // current table
//          // add join expressions between the id attributes of the two tables
//          Where.add(join(currClass, tcurrTable, sup, tsupTable, f)
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
//        tcurrTable = tsupTable;
//        currClass = sup;
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
//    // process deriving attributes...
//    if (derivingAttributes != null) {
//      Class b;
//      java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();
//      String tD;
//      for (DomainConstraint d : derivingAttributes) {
//        /*      add d to SELECT
//         *      add d to GROUP BY
//         */
//        b = dom.getDsm().getDeclaringClass(c, d);
//        if (b != c) {
//          /*d is inherited from an ancestor domain class b (b != c)
//          *        if b != a AND b has not been processed
//          *          let t = SQL_Join(c,y,...,b) (y may = b)
//          *          merge t into WHERE
//          *          merge y,...,b into FROM
//          */
//          if (b != a && !processed.containsKey(b)) {
//            sup = dom.getDsm().getSuperClass(c);
//            //tcurrTable = cTable;
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
//                idFields = dom.getDsm().getIDDomainConstraints(sup);
//                for (DomainConstraint f : idFields) { // current table
//                  // add join expressions between the id attributes of the two tables
//                  Where.add(join(currClass, tcurrTable, sup, tsupTable, f)
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
//              tcurrTable = tsupTable;
//              currClass = sup;
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
//        colName = tD + "."+ colName;
//        Select.add(colName);
//        GroupBy.add(tD + "."+d.name());
//      } // end for
//    } // end if
//    
//    /*  Add min(attrib) and max(attrib) to SELECT
//     */
////    Select.add("min("+tA+"."+attrib.name()+")");
////    Select.add("max("+tA+"."+attrib.name()+")");
//    colName = getColName(a,attrib);
//    Select.add("min("+tA+"."+colName+")");
//    Select.add("max("+tA+"."+colName+")");
//    
//    String sql = genSelect(
//        Select.toArray(new String[Select.size()]),  // selectCols
//        From.toArray(new String[From.size()]),      // tables
//        (!Where.isEmpty()) ? Where.toArray(new String[Where.size()]) : null, // AND
//        null, // OR
//        (!GroupBy.isEmpty()) ? GroupBy.toArray(new String[GroupBy.size()]) : null, // group by
//        null  // order by
//        );
//    
//    if (debug)
//      System.out.println("DBToolKit.readValueRangeFromSource: " + sql);
//
//    try {
//      ResultSet rs = executeQuery(sql);
//      return rs;
//    } catch (Exception e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET, e, 
//          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    }
//    
////    ResultSet rs =
////        selectAndProject(
////            Select.toArray(new String[Select.size()]),
////            From.toArray(new String[From.size()]),
////            (!Where.isEmpty()) ? Where.toArray(new String[Where.size()]) : null, // AND
////            null, // OR
////            (!GroupBy.isEmpty()) ? GroupBy.toArray(new String[GroupBy.size()]) : null, // group by
////            null  // order by
////            );
//    
////    if (rs == null)
////      throw new DBException(DBException.Code.FAIL_RESULT_SET,
////          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
//    
////    return rs;   
//  }
//
//    
////  /**
////   * @effects 
////   *  read the max value of the attribute of the domain class <tt>c</tt> 
////   *  whose domain constraint is <tt>dc</tt> and whose group by attributes
////   *  are <tt>groupBy</tt> 
////   *  from the database table of <tt>c</tt>
////   *  and return this value.
////   *  
////   *  <p>Throws DBException if fails to read the database.
////   *  
////   * @requires 
////   *  <tt>attribute.type</tt> is sortable.
////   * @deprecated use {@link #readValueRange(Class, DomainConstraint)} instead
////   */
////  public Object readMaxValue(Class c, DomainConstraint dc, String[] groupBy) throws DBException {
////    // the class name used as the table name
////    final String cname = schema.getDsm().getDomainClassName(c);
////
////    String attribute = dc.name();
////    String maxCol = "max("+attribute+")"; 
////        
////    String[] selectCols;
////    if (groupBy != null) {
////      // use group by attributes and maxCol
////      selectCols = new String[groupBy.length+1];
////      System.arraycopy(groupBy, 0, selectCols, 0, groupBy.length);
////      selectCols[selectCols.length-1] = maxCol;
////    } else {
////      // use maxCol only
////      selectCols = new String[] { maxCol };
////    }
////    
////    ResultSet rs =
////        selectAndProject(
////            selectCols,
////            new String[] {cname},
////            null, null,
////            groupBy,
////            null  // order by
////            );
////    if (rs == null)
////      return null;
////    
////    try {
////      // there could be several rows, depending on whether groupBy was specified
////      if (groupBy != null) {
////        // (possibly) multiple rows
////        List<List> maxVals = new LinkedList<List>();
////        List valRow;
////        DomainConstraint dc1;
////        while (rs.next()) {
////          valRow = new LinkedList();
////          int numSelect = selectCols.length;
////          for (int ind = 0; ind < numSelect; ind++) {
////            dc1 = schema.getDsm().getDomainConstraint(c, 
////                (ind < numSelect-1) ? selectCols[ind] : attribute);
////            valRow.add(sqlToJava(dc1, rs, ind+1));
////          }
////          maxVals.add(valRow);
////        }
////        
////        return (maxVals.isEmpty()) ? null : maxVals;
////      } else {
////        // single row
////        if (rs.next()) {
////          Object maxVal = sqlToJava(dc, rs, 1);
////          return maxVal;
////        } else {
////          // empty result
////          return null;
////        }
////      }
////    } catch (SQLException e) {
////      // something wrong
////      throw new DBException(DBException.Code.FAIL_RESULT_SET, e,
////          "Lỗi xử lí kết quả dữ liệu {0}", c.getSimpleName());
////    }
////  }
//  
//  // private void populateData(Class cls) throws DBException {
//  // if (debug)
//  // System.out.println("Populating data for " + cls.getSimpleName() + "...");
//  //
//  // final int numObjs = 4;
//  //
//  // Object o = null;
//  // String sql = null;
//  // try {
//  // for (int i = 0; i < numObjs; i++) {
//  // o = DataManager.getAutoObject(cls);
//  // sql = genInsert(o);
//  // executeUpdate(sql);
//  // }
//  // } catch (NotPossibleException e) {
//  // throw new DBException("Failed to populate data", e);
//  // }
//  // }
//
//  /**
//   * @effects returns <code>true</code> if the database schema with name
//   *          <code>dbSchema</code> exists in the database, else returns
//   *          <code>false</code>
//   */
//  public boolean existsSchema(String dbSchema) {
//    String sql = "select s.schemaname from sys.sysschemas s "
//        + "where s.schemaname='%s'";
//
//    sql = String.format(sql, dbSchema.toUpperCase());
//
//    if (debug)
//      System.out.println(sql);
//
//    ResultSet rs = null;
//    try {
//      rs = executeQuery(sql);
//
//      if (rs != null && rs.next()) {
//        return true;
//      }
//    } catch (Exception e) {
//      // something wrong, but dont care
//    } finally {
//      try {
//        if (rs != null)
//          rs.close();
//      } catch (Exception e) {
//      }
//    }
//    return false;
//  }
//
//  /**
//   * @effects creates the database schema named <code>name</code>
//   */
//  public void createSchema(String name) throws DataSourceException {
//    // create
//    String sql = "create schema %s";
//    sql = String.format(sql, name);
//
//    if (conn != null) {
//      executeUpdate(sql);
//    }
//  }
//
//  /**
//   * @effects 
//   *  Create the table for each domain class in <tt>domainClasses</tt>
//   * 
//   *  <p>Throws NotPossibleException if failed to create a table; 
//   *  NotFoundException if a <tt>domainClass</tt> is not a registered domain class or 
//   *  required id domain attributes of the class(es) referenced by this class are not found.  
//   */
//  private void createTable(Class[] domainClasses) throws DataSourceException, NotPossibleException, NotFoundException {
//    if (debug)
//      System.out.println("Creating tables...");
//
//    if (conn != null) {
//      String sql = null;
//      for (int i = 0; i < domainClasses.length; i++) {
//        createClassStore(domainClasses[i]);
//      }
//    }
//  }
//
//  /**
//   * @effects returns a <code>String[]</code> array, the elements of which are
//   *          the names of the table in the database schema used by the database schema named <tt>schemaName</t>, or <code>null</code> if no tables are found
//   *          (i.e. the database is empty)
//   */
//  private String[] getTableNames(String schemaName) {
//    List<String> names = new ArrayList();
//
//    // query all the application schemas for tables to remove
//    
//    String sql = "select tablename from sys.systables t, sys.sysschemas s "
//        + "where t.schemaid=s.schemaid and s.schemaname='" + schemaName + "'";
//
//    ResultSet rs = null;
//    try {
//      rs = executeQuery(sql);
//
//      if (rs != null) {
//        String tname;
//        while (rs.next()) {
//          tname = rs.getString(1);
//          names.add(tname);
//        }
//      }
//    } catch (Exception e) {
//      // something wrong, but dont care
//    } finally {
//      try {
//        if (rs != null)
//          rs.close();
//      } catch (Exception e) {
//      }
//    }
//
//    return (!names.isEmpty()) ? names.toArray(new String[names.size()]) : null;
//  }
//
//  /**
//   * @effects 
//   *  if exists data source constraints (e.g. FKs) of <tt>c</tt>
//   *    return a List of their names (in the definition order)
//   *  else
//   *    return null
//   * @version 
//   *  2.6.4.b: read FK constraints only.
//   */
//  public List<String> readDataSourceConstraint(Class c) {
//    if (conn != null) {
//      try {
//        DatabaseMetaData dbMeta = conn.getMetaData();
//        // Note: names must be in upper-case
//        String tableName = c.getSimpleName().toUpperCase();
//        String schemaName = dom.getDsm().getDomainSchema(c);
//
//        //v2.7.3: no need
//        // if (schemaName != null) schemaName = schemaName.toUpperCase();
//        
//        String catalog = conn.getCatalog();
//        
//        // TODO: find more constraints if needed
//        // here we are interested in just the FK constraints
//        ResultSet res = dbMeta.getImportedKeys(catalog, schemaName, tableName);
//        
//        List<String> fkNames  = new ArrayList();
//        String fkName;
//        while (res.next()) {
//          fkName = (String) res.getString("FK_NAME");
//          fkNames.add(fkName);
//        }
//        
//        if (fkNames.isEmpty()) 
//          return null;
//        else
//          return fkNames;
//      } catch (SQLException e) {
//        if (debug)
//          e.printStackTrace();
//        return null;
//      }
//    } else {
//      return null;
//    }
//  }
//  
//  /**
//   * Drop database tables
//   * 
//   * @param domainClasses
//   *          a <code>Class[]</code> array containing the classes that were used
//   *          to create the tables.
//   */
//  public void dropTable(Class[] domainClasses) throws DataSourceException {
//    if (debug)
//      System.out.println("Droping tables...");
//
//    if (conn != null) {
//      String sql = null;
//      for (Class c : domainClasses) {
//        dropClassStore(c);
//      }
//    }
//  }
//
//  /**
//   * Drop a given database table that corresponds to a class.
//   * 
//   * @param domainClass
//   *          a <code>Class</code> object representing the table to be dropped
//   */
//  public void dropClassStore(Class domainClass) throws DataSourceException {
//    dropTable(dom.getDsm().getDomainClassName(domainClass));
//  }
//
//  /**
//   * @effects drops all tables in the database schema used by this; throws a
//   *          <code>DBException</code> if an error occured
//   */
//  public void deleteDomainSchema(String schemaName) throws DataSourceException {
//    String[] tables = getTableNames(schemaName);
//
//    // the tables may have dependencies, thus we need to loop until all tables
//    // have been deleted...
//    if (tables != null) {
//      List<String> tableNames = new ArrayList();
//      Collections.addAll(tableNames, tables);
//      String table;
//      if (debug)
//        System.out.println("To drop " + tableNames.size() + " tables");
//
//      while (tableNames.size() > 0) {
//        table = tableNames.remove(0);
//        try {
//          if (debug)
//            System.out.println("dropping table " + table);
//
//          dropTable(table);
//          if (debug)
//            System.out.println("...ok");
//        } catch (DataSourceException e) {
//          if (debug)
//            System.out.println("...failed (to retry)");
//          // perhaps caused by dependency, move table to the end of the list
//          // to try again later
//          tableNames.add(table);
//        }
//      }
//    } else {
//      if (debug)
//        System.out.println("No tables found in schema " + schemaName);
//    }
//  }
//
//  private void dropTable(String tableName) throws DataSourceException {
//    if (conn != null) {
//      String sql = null;
//      sql = "drop table " + tableName;
//      if (debug)
//        System.out.println(sql);
//
//      executeUpdate(sql);
//    }
//  }
//
//  /**
//   * Delete the records of a given database table that corresponds to a class.
//   * 
//   * @param domainClass
//   *          a <code>Class</code> object representing the table to be cleared
//   */
//  public void deleteObjects(Class domainClass) throws DataSourceException {
//    if (conn != null) {
//      String sql = null;
//      sql = "delete from " + dom.getDsm().getDomainClassName(domainClass);
//      if (debug)
//        System.out.println(sql);
//      executeUpdate(sql);
//    }
//  }
//
//  /**
//   * @effects 
//   *  remove the data source constraint of the table associated to the domain class 
//   *  <tt>c</tt>, whose name is <tt>name</tt>
//   *  
//   *  <p>Throws DBException if failed to remove the constraint.
//   */
//  public void dropDataSourceConstraint(Class c, String name) throws DataSourceException {
//    if (conn != null) {
//      String tableName = dom.getDsm().getDomainClassName(c);
//      String sql = "alter table %s drop constraint %s";
//      sql = String.format(sql, tableName, name);
//      if (debug)
//        System.out.println("DBToolKit.dropDataSourceConstraint: "+sql);
//      
//      executeUpdate(sql);
//    }    
//  }
//  
//  // public static void dropTable(String dropSQL) {
//  // if (conn != null) {
//  // try {
//  // Statement stmt = conn.createStatement();
//  // try {
//  // stmt.executeUpdate(dropSQL);
//  // } catch (SQLException sqle) {
//  // // ignore
//  // }
//  // commit(stmt);
//  // stmt.close();
//  // } catch (Exception e) {
//  // e.printStackTrace();
//  // }
//  // }
//  // }
//
//  /**
//   * Print out the contents of the tables that store the objects of the domain
//   * classes specified in the argument <code>domainClasses</code>.
//   * 
//   * @param domainClasses
//   *          the domain classes whose data objects are to be printed out
//   */
//  public void print(Class[] domainClasses) {
//    System.out.println("Printing data...");
//
//    if (conn != null) {
//      try {
//        for (int i = 0; i < domainClasses.length; i++) {
//          print(domainClasses[i]);
//        }
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//  }
//
//  public void print(Class domainClass) throws DataSourceException {
//    System.out.println("Printing data...");
//
//    if (conn != null) {
//      String name = dom.getDsm().getDomainClassName(domainClass);
//      System.out.println("TABLE: " + name);
//      ResultSet rs = //selectAndProject(new String[] { "*" },
//            //new String[] { name }, null, null, null);
//          selectAndProject(new String[] { "*" },
//              new String[] { name }, null, null, null, null);
//
//      if (rs != null)
//        printResultSet(rs);
//    }
//  }
//
//  private void printResultSet(ResultSet rs) throws DataSourceException {
//    // the order of the rows in a cursor
//    // are implementation dependent unless you use the SQL ORDER _statement
//    try {
//      ResultSetMetaData metaData = rs.getMetaData();
//      int colmax = metaData.getColumnCount();
//
//      StringBuffer colFormat = new StringBuffer();
//      StringBuffer rowFormat = new StringBuffer();
//
//      String[] colNames = new String[colmax];
//      final String space = "  ";
//
//      int colSize;
//      // get the column name and its format
//      for (int i = 0; i < colmax; i++) {
//        colNames[i] = metaData.getColumnName(i + 1);
//        // colSize = colNames[i].length();//metaData.getColumnDisplaySize(i+1);
//        // colFormat.append("%").append(colSize).append("s").append(space);
//        colFormat.append("%").append("s");
//        if (i < colmax - 1)
//          colFormat.append(",").append(space);
//      }
//
//      // print the column names
//      colFormat.append("%n"); // next line
//      System.out.format(colFormat.toString(), colNames);
//
//      int i;
//      String ft;
//      Object[] objs = null;
//      int rowi = 0;
//      for (; rs.next();) {
//        objs = new Object[colmax];
//        for (i = 0; i < colmax; ++i) {
//          objs[i] = rs.getObject(i + 1); // SQL column index starts at 1
//          // prepare row format
//          if (rowi == 0) {
//            ft = getDisplayFormatType(objs[i]);
//            // colSize = metaData.getColumnDisplaySize(i+1);
//            // rowFormat.append("%").append(colSize).append("s").append(space);
//            rowFormat.append("%").append("s");
//            if (i < colmax - 1)
//              rowFormat.append(",").append(space);
//          }
//        }
//
//        if (rowi == 0) {
//          rowFormat.append("%n");
//        } // next line
//
//        // print row
//        System.out.format(rowFormat.toString(), objs);
//        rowi++;
//      }
//    } catch (SQLException e) {
//      throw new DataSourceException(DataSourceException.Code.FAIL_RESULT_SET,
//          "Failed to process result set ", e.getMessage());
//    } finally {
//      try {
//        rs.close();
//        rs.getStatement().close();
//      } catch (SQLException e) {
//        //
//      }
//    }
//  }
//
//  private static String getDisplayFormatType(Object o) {
//    if (o instanceof Boolean) {
//      return "b";
//    } else if (o instanceof Integer || o instanceof Long) {
//      return "d";
//    } else if (o instanceof Float || o instanceof Double) {
//      return "f";
//    } else {
//      // the rest is string
//      return "s";
//    }
//  }
//
//  /**
//   * Drop and create the database tables from the Java classes that represent
//   * the domain entities.<br>
//   * 
//   * @param domainClasses
//   *          the domain classes that represent the entities whose data are to
//   *          be initialised
//   * @throws Exception
//   */
//  public void initTables(Class[] domainClasses) throws DataSourceException {
//    dropTable(domainClasses);
//    createTable(domainClasses);
//  }
//
//  /**
//   * Create a new relational table from a domain class
//   * 
//   * @param domainClass
//   *          a domain class
//   * @effects Create a new relational table whose columns are defined from
//   *          the serialisable attributes of <code>domainClass</code>, else
//   *          create a new relational table from all the attributes of
//   *          <code>domainClass</code>.
//   *        
//   *        <p>Throws NotPossibleException if failed to create the table; 
//   *  NotFoundException if <tt>domainClass</tt> is not a registered domain class or 
//   *  required id domain attributes of the class(es) referenced by <tt>domainClass</tt> are not found.
//   */
//  public void createClassStore(Class domainClass) throws DataSourceException, NotPossibleException, NotFoundException {
//    if (conn != null) {
//      String sql = null;
//      try {
//        sql = genCreate(domainClass);
//        if (debug)
//          System.out.println("\n" +sql);
//      } catch (NotPossibleException e) {
//        throw new DataSourceException(DataSourceException.Code.FAIL_TO_CREATE, e,
//            "Failed to create table");
//      }
//
//      executeUpdate(sql);
//    }
//  }
//
//  /**
//   * Create a new relational table from a domain class but leaving the constraints (e.g. FKs) till later.
//   *
//   * @modifies  
//   *  tableConstraints
//   * @effects Create a new relational table whose columns are defined from
//   *          the serialisable attributes of <code>domainClass</code>, else
//   *          create a new relational table from all the attributes of
//   *          <code>domainClass</code>.
//   *          
//   *          <p>All the table constraints are added to <tt>tableConstraints</tt>. 
//   *        
//   *        <p>Throws NotPossibleException if failed to create the table; 
//   *  NotFoundException if <tt>domainClass</tt> is not a registered domain class or 
//   *  required id domain attributes of the class(es) referenced by <tt>domainClass</tt> are not found.
//   */
//  public void createClassStoreWithoutConstraints(final Class domainClass, 
//      final java.util.Map<String,List<String>> tableConstraints) throws DataSourceException, NotPossibleException, NotFoundException {
//    if (conn != null) {
//      String sql = null;
//      try {
//        sql = genCreate(domainClass, tableConstraints);
//        if (debug)
//          System.out.println("\n" +sql);
//      } catch (NotPossibleException e) {
//        throw new DataSourceException(DataSourceException.Code.FAIL_TO_CREATE, e,
//            "Failed to create table");
//      }
//
//      executeUpdate(sql);
//    }
//  }
//  
//  /**
//   * @requires <pre>
//   *  for each entry e in tableConstraints
//   *    table(e.key) /\ e.getValue contains constraint statements on table(e.key)</pre>
//   * @effects <pre> 
//   *  for each entry e in tableConstraints
//   *    let tableName = e.key
//   *    let tableCons = e.getValue
//   *    alter table(tableName) adding the constraints in tableCons
//   *  
//   *  Throws DBException if failed to add a constraint.
//   *  </pre>
//   */
//  public void createConstraints(java.util.Map<String,List<String>> tableConstraints) throws DataSourceException {
//    String tableName;
//    List<String> tableCons;
//    String sqlTemp = "alter table %s add %s";
//    String sql;
//    for (Entry<String,List<String>> e : tableConstraints.entrySet()) {
//      tableName = e.getKey();
//      tableCons = e.getValue();
//      
//      for (String cons : tableCons) {
//        sql = String.format(sqlTemp, tableName, cons);
//        
//        if (debug) System.out.println("DBToolKit.createConstraints: " + sql);
//        
//        executeUpdate(sql);
//      }
//    }
//  }
//  
//  // /**
//  // * Create the database tables from the Java classes that represent the
//  // domain
//  // * entities.<br>
//  // *
//  // * This method also populates some of these tables with some test data. The
//  // * tables whose data will be populated are specified in the
//  // * <code>dataClasses</code> parameter.<br>
//  // *
//  // * The generated test data come from the Test Data Definitions and Test Data
//  // * objects that are defined in the header of the {@link DataManager}
//  // class.<br>
//  // *
//  // * @param domainClasses
//  // * the domain classes that represent the entities whose data are to
//  // * be initialised
//  // * @param dataClasses
//  // * a sub-set of the <code>domainClasses</code> whose test data are to
//  // * be populated
//  // * @throws Exception
//  // */
//  // public void initData(Class[] domainClasses, Class[] dataClasses)
//  // throws Exception {
//  // initTables(domainClasses);
//  //
//  // // create test data for data classes
//  // if (dataClasses != null) {
//  // for (int i = 0; i < dataClasses.length; i++) {
//  // populateData(dataClasses[i]);
//  // }
//  // }
//  // }
//
//  // //////// SQL FUNCTIONS ///////////////////////////
//  /**
//   * @deprecated use {@link #selectAndProject(String[], String[], Expression[], Expression[], String[], String)}
//   *  instead
//   */
//  private ResultSet selectAndProject(String[] selectCols, String[] tables,
//      Expression[] ANDs, Expression[] ORs, String orderBy) throws DataSourceException {
//    return selectAndProject(selectCols, tables, ANDs, ORs, null, orderBy);
//  }
//  
//  /**
//   * @effects 
//   *  generate a SELECT SQL query whose select columns are <tt>selectCols</tt>, 
//   *  from <tt>tables</tt>, with conditions specified by <tt>ANDs, ORs</tt>, and 
//   *  with GROUP BY <tt>groupBy</tt> and ORDER BY <tt>orderBy</tt>
//   *  
//   *  <p>execute the query and return a <tt>ResultSet</tt> if succeeded; 
//   *  return <tt>null</tt> if an error occured.
//   *  
//   * @example if the desired SELECT query is <pre>
//   *    select semester, max(code) from module group by semester order by semester desc
//   *  </pre> then 
//   *  <tt>selectCols = {"semester", "max(code)"},
//   *      tables = {"module"},
//   *      ANDs = null,
//   *      ORs = null,
//   *      groupBy = {"semester"},
//   *      orderBy = "order by semester desc" 
//   *   </tt>
//   */
//  private ResultSet selectAndProject(String[] selectCols, String[] tables,
//      Expression[] ANDs, Expression[] ORs, String[] groupBy, String orderBy) throws DataSourceException {
//
//    String sql = genSelect(selectCols, tables,
//        ANDs, ORs, groupBy, orderBy);
//    
//    if (debug)
//      System.out.println("DBToolKit.selectAndProject: " + sql);
//
//    /*v2.7.4: throws exception
//    try {
//      ResultSet rs = executeQuery(sql);
//      return rs;
//    } catch (Exception e) {
//      e.printStackTrace();
//      return null;
//    }
//    */
//    ResultSet rs = executeQuery(sql);
//    return rs;
//  }
//
//  /**
//   * Update the values of some <code>attributes</code> of an object into the
//   * database.
//   * 
//   * @param o
//   *          an object whose attribute values are to be updated
//   * @param attributes
//   *          the names of the attributes to be updated
//   * @param idAttributes
//   *          the names of the id attributes of the object
//   * @return
//   */
//  // public boolean putObject(Object o, String[] attributes, String[]
//  // idAttributes) {
//  // Expression[] updates = new Expression[attributes.length];
//  // Expression[] conditions = new Expression[idAttributes.length];
//  //
//  // String attribute = null;
//  // // prepare the update expressions
//  // for (int i = 0; i < attributes.length; i++) {
//  // attribute = attributes[i];
//  // updates[i] = new Expression(attribute, "=",
//  // DomainManager.getAttributeValue(o, attribute));
//  // }
//  //
//  // // prepare the WHERE expressions using the IDs
//  // for (int i = 0; i < idAttributes.length; i++) {
//  // attribute = idAttributes[i];
//  // conditions[i] = new Expression(attribute, "=",
//  // DomainManager.getAttributeValue(o, attribute));
//  // }
//  //
//  // return update(o.getClass(), updates, conditions);
//  // }
//
//  /**
//   * Update data in a table
//   * 
//   * @param table
//   *          the name of the table
//   * @param sets
//   *          an array of expressions for setting the table values
//   * @param ANDs
//   *          an array of conditions for finding the rows to set the values
//   * @return
//   */
//  private boolean update(Class c, Expression[] sets, Expression[] ANDs) {
//    String table = dom.getDsm().getDomainClassName(c);
//
//    StringBuffer sb = new StringBuffer("update ");
//
//    sb.append(table).append(" set ");
//
//    for (int i = 0; i < sets.length; i++) {
//      sb.append(sets[i]);
//      if (i < sets.length - 1) {
//        sb.append(",");
//      }
//    }
//
//    if (ANDs != null) {
//      sb.append(" where ");
//      for (int i = 0; i < ANDs.length; i++) {
//        sb.append(ANDs[i]);
//        if (i < ANDs.length - 1) {
//          sb.append(" and ");
//        }
//      }
//    }
//
//    try {
//      executeUpdate(sb.toString());
//      return true;
//    } catch (DataSourceException e) {
//      return false;
//    }
//  }
//  
//  /**
//   * @effects 
//   *  generate a SELECT statement over the column mapped to the attribute <tt>attrib</tt>, whose row
//   *  is identified by <tt>oid</tt> (if specified),  
//   *  of the table that is mapped to the domain class <tt>c</tt>. 
//   *  <p>If <tt>withPK = true</tt> then also include the PK column(s) in the statement.
//   *  <p>If <tt>attrib</tt>'s type is comparable, add ASC sorting to the statement
//   *  
//   *  <p>Thus, if oid is not specified (i.e. equal <tt>null</tt>) then this generates an SQL that results in 
//   *  all row values of the specified attribute be returned. Otherwise, the generated SQL results in 
//   *  a single row value of the attribute, that is specified by the <tt>oid</tt>, to be returned. 
//   *  
//   * @pseudocode
//   * (similar to (and see examples in) {@link #readValueRangeFromSource(Class, DomainConstraint, DomainConstraint[])})
//   * 
//   * <pre>
//   *  let SELECT, FROM, WHERE, ORDER BY be sets of strings
//   *  let tA be a string
//   *  
//   *  Add c to FROM
//   *  
//   *  if attrib is inherited from an ancestor domain class a,  
//   *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
//   *    let s = SQL_Join(c,x,...,a) (x may = a)
//   *    add s to WHERE
//   *    add x,...,a to FROM
//   *    tA = a
//   *  else
//   *    tA = c
//   *  
//   *  if oid != null
//   *    let oid = Oid(c,(id,val))
//   *    let tid be a String
//   *    if id is inherited from an ancestor domain class e (e != c)
//   *      tid = e
//   *      if e != a AND e has not been processed
//   *        let j = SQL_Join(c,z,...,e) (z may = e)
//   *        merge j into WHERE
//   *        merge z,...,e into FROM
//   *    else
//   *      tid = c
//   *    add "tid.id=val" to WHERE
//   *          
//   *  if withPK = true
//   *    let ids = id-attributes of c
//   *    for each d in ids 
//   *      let tD be a String
//   *      if d is inherited from an ancestor domain class b (b != c)
//   *        tD = b
//   *        if b != a AND b has not been processed
//   *          let t = SQL_Join(c,y,...,b) (y may = b)
//   *          merge t into WHERE
//   *          merge y,...,b into FROM
//   *      else
//   *        tD = c
//   *      add tD.d to SELECT
//   * 
//   *  Add tA.attrib to SELECT
//   *  
//   *  if attrib.type is comparable
//   *    add tA.attrib to ORDER BY
//   *    
//   *  Let sql = SQL(SELECT, FROM, WHERE, ORDER BY)
//   *  return SQL
//   * </pre>      
//   */
//  private String genSelect(Class c, Oid oid, DomainConstraint attrib, boolean withPK) {
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
//    Collection<DomainConstraint> idAttribs;
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
//        for (DomainConstraint f : idAttribs) { // current table
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
//      DomainConstraint id = oid.getIdAttribute(0);
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
//            for (DomainConstraint f : idAttribs) { // current table
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
//      for (DomainConstraint d : idAttribs) {
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
//                for (DomainConstraint f : idAttribs) { // current table
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
//  }
//  
//  /**
//   * @effects 
//   *  generate a SELECT statement over the column mapped to the attribute <tt>attrib</tt> 
//   *  of the table that is mapped to the domain class <tt>c</tt>. 
//   *  <p>If <tt>withPK = true</tt> then also include the PK column(s) in the statement.
//   *  <p>If <tt>attrib</tt>'s type is comparable, add ASC sorting to the statement
//   * @pseudocode
//   * (similar to (and see examples in) {@link #readValueRangeFromSource(Class, DomainConstraint, DomainConstraint[])})
//   * 
//   * <pre>
//   *  let SELECT, FROM, WHERE, ORDER BY be sets of strings
//   *  let tA be a string
//   *  
//   *  Add c to FROM
//   *  
//   *  if attrib is inherited from an ancestor domain class a,  
//   *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
//   *    let s = SQL_Join(c,x,...,a) (x may = a)
//   *    add s to WHERE
//   *    add x,...,a to FROM
//   *    tA = a
//   *  else
//   *    tA = c
//   *  
//   *  if withPK = true
//   *    let ids = id-attributes of c
//   *    for each d in ids 
//   *      let tD be a String
//   *      if d is inherited from an ancestor domain class b (b != c)
//   *        tD = b
//   *        if b != a AND b has not been processed
//   *          let t = SQL_Join(c,y,...,b) (y may = b)
//   *          merge t into WHERE
//   *          merge y,...,b into FROM
//   *      else
//   *        tD = c
//   *      add tD.d to SELECT
//   * 
//   *  Add tA.attrib to SELECT
//   *  
//   *  if attrib.type is comparable
//   *    add tA.attrib to ORDER BY
//   *    
//   *  Let sql = SQL(SELECT, FROM, WHERE, ORDER BY)
//   *  return SQL
//   * </pre>     
//   * 
//   * @deprecated (to be removed) use {@link #genSelect(Class, Oid, DomainConstraint, boolean)} instead
//   * 
//   */
//  private String genSelect(Class c, DomainConstraint attrib, boolean withPK) {
//    return genSelect(c, null, attrib, withPK);
////    
////    Stack<String> Select = new Stack();
////    Stack<String> From = new Stack();
////    Stack<String> Where = new Stack();
////    String OrderBy = null;
////    
////    Collection<String> tables = new ArrayList();
////    Collection<String> joinTablePairs = new ArrayList();
////    
////    /*  
////     *  Add c to FROM
////     */
////    int tIndex = 0;
////    final String cTable = schema.getDsm().getDomainClassName(c);
////    final String tcTable = "t"+(tIndex++);
////    String tA;
////    From.add(cTable + " " + tcTable);
////    tables.add(cTable);
////    
////    final Class a = schema.getDsm().getDeclaringClass(c, attrib);
////    
////    Collection<DomainConstraint> idAttribs;
////    String supTable, tsupTable;
////    Class sup = schema.getDsm().getSuperClass(c);
////    Class currClass;
////    String currTable = cTable;
////    String tcurrTable = tcTable;
////    String colName;
////    
////    if (a != c) {
////     /* attrib is inherited from an ancestor domain class a,  
////      *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
////      *    let s = SQL_Join(c,x,...,a) (x may = a)
////      *    add s to WHERE
////      *    add x,...,a to FROM
////      */
////      currClass = c;
////      do {
////        supTable = schema.getDsm().getDomainClassName(sup);
////        tsupTable = "t" + (tIndex++);
////
////        // use the id attributes to add new join expressions
////        idAttribs = schema.getDsm().getIDDomainConstraints(sup);
////        for (DomainConstraint f : idAttribs) { // current table
////          // add join expressions between the id attributes of the two tables
////          Where.add(
////              join(currClass, tcurrTable, sup, tsupTable, f)
//////              new Expression(tcurrTable + "." + f.name(),
//////              Expression.Op.EQ, tsupTable + "." + f.name(),
//////              Expression.Type.Metadata)
////              );
////          
////          joinTablePairs.add(currTable+"-"+supTable);
////        } // end for
////
////        // add super class table to FROM
////        From.add(supTable + " " + tsupTable);
////        tables.add(supTable);
////        
////        // recursive: check the super-super class and so on...
////        currTable = supTable;
////        currClass = sup;
////        tcurrTable = tsupTable;
////        sup = schema.getDsm().getSuperClass(sup);
////      } while (sup != null);
////      
////      // attribute table is the last super class name
////      tA = tcurrTable;
////    } else {
////      // attrib is in c
////      tA = tcTable;
////    } // end if 
////    
////    // process id attributes...
////    if (withPK) {
////      Class b;
////      java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();
////      String tD;
////      idAttribs = schema.getDsm().getIDDomainConstraints(c);
////
////      for (DomainConstraint d : idAttribs) {
////        b = schema.getDsm().getDeclaringClass(c, d);
////        if (b != c) {
////          /*d is inherited from an ancestor domain class b (b != c)
////           *        if b != a AND b has not been processed
////           *          let t = SQL_Join(c,y,...,b) (y may = b)
////           *          merge t into WHERE
////           *          merge y,...,b into FROM
////           */
////          if (b != a && !processed.containsKey(b)) {
////            sup = schema.getDsm().getSuperClass(c);
////            currTable = cTable;
////            tcurrTable = tcTable;
////            currClass = c;
////            do {
////              supTable = schema.getDsm().getDomainClassName(sup);
////              tsupTable = "t" + (tIndex++);
////
////              // merge Join into WHERE
////              if (!joinTablePairs.contains(currTable+"-"+supTable)) {
////                // use the id attributes to add new join expressions
////                idAttribs = schema.getDsm().getIDDomainConstraints(sup);
////                for (DomainConstraint f : idAttribs) { // current table
////                  // add join expressions between the id attributes of the two tables
////                  Where.add(
////                      join(currClass, tcurrTable, sup, tsupTable, f)
//////                      new Expression(tcurrTable + "." + f.name(),
//////                      Expression.Op.EQ, tsupTable + "." + f.name(),
//////                      Expression.Type.Metadata)
////                      );
////                  
////                  joinTablePairs.add(currTable+"-"+supTable);
////                } // end for
////              }
////              
////              // merge table into FROM
////              if (!tables.contains(supTable)) {
////                From.add(supTable + " " + tsupTable);
////                tables.add(supTable);
////              }
////              
////              // recursive: go up to the super-super class and so on...
////              currTable = supTable;
////              currClass = sup;
////              tcurrTable = tsupTable;
////              sup = schema.getDsm().getSuperClass(sup);
////            } while (sup != null);
////            
////            tD = tcurrTable;
////            processed.put(b, tcurrTable);
////          } else if (b == a) {
////            tD = tA;
////          } else {  // b already processed
////            tD = processed.get(b);
////          }  // end if
////        } else {
////          tD = tcTable;
////        } // end if
////        
////        //Select.add(tD + "."+d.name());
////        colName = getColName(b,d);
////        colName = tD + "." + colName;
////        Select.add(colName);
////      } // end for
////    } // end if
////    
////    /*  Add attrib to SELECT
////     */
////    //String colName = tA+"."+attrib.name();
////    colName = getColName(a, attrib);
////    colName = tA + "." + colName;
////    Select.add(colName);
////    
////    boolean comparable = attrib.type().isComparable();
////    if (comparable) {
////      // add order by if attribute's type is comparable
////      OrderBy = "order by " + colName + " ASC";
////    }
////    
////    // generate SQL statement
////    String sql = genSelect(
////        Select.toArray(new String[Select.size()]), 
////        From.toArray(new String[From.size()]),
////        (!Where.isEmpty() ? Where.toArray(new String[Where.size()]): null),
////        null,
////        null,
////        OrderBy);
////    
////    return sql;
//  }
//  
////  private void genJoinHierarchy(Class c, 
////      Collection<String> Select, 
////      Collection<Expression> Where, 
////      Collection<String> From, 
////      Collection<String> joinTablePairs,
////      Collection<String> tables) {
////    int tIndex = 0;
////    final String cTable = schema.getDsm().getDomainClassName(c);
////    final String tcTable = "t"+(tIndex++);
////    
////    Collection<DomainConstraint> idAttribs;
////    String supTable, tsupTable;
////    Class sup = schema.getDsm().getSuperClass(c);
////    String currTable = cTable;
////    String tcurrTable = tcTable;
////    
////    do {
////      supTable = schema.getDsm().getDomainClassName(sup);
////      tsupTable = "t" + (tIndex++);
////
////      // use the id attributes to add new join expressions
////      idAttribs = schema.getDsm().getIDDomainConstraints(sup);
////      for (DomainConstraint f : idAttribs) { // current table
////        // add join expressions between the id attributes of the two tables
////        Where.add(new Expression(tcurrTable + "." + f.name(),
////            Expression.Op.EQ, tsupTable + "." + f.name(),
////            Expression.Type.Metadata));
////        
////        joinTablePairs.add(currTable+"-"+supTable);
////      } // end for
////
////      // add super class table to FROM
////      From.add(supTable + " " + tsupTable);
////      tables.add(supTable);
////      
////      // recursive: check the super-super class and so on...
////      currTable = supTable;
////      tcurrTable = tsupTable;
////      sup = schema.getDsm().getSuperClass(sup);
////    } while (sup != null);
////  }
//  
//  /**
//   * @effects 
//   *  generate and return a SELECT SQL query whose select columns are <tt>selectCols</tt>, 
//   *  from <tt>tables</tt>, with conditions specified by <tt>ANDs, ORs</tt>, and 
//   *  with GROUP BY <tt>groupBy</tt> and ORDER BY <tt>orderBy</tt>.
//   *  
//   * @example if the desired SELECT query is <pre>
//   *    select semester, max(code) from module group by semester order by semester desc
//   *  </pre> then 
//   *  <tt>selectCols = {"semester", "max(code)"},
//   *      tables = {"module"},
//   *      ANDs = null,
//   *      ORs = null,
//   *      groupBy = {"semester"},
//   *      orderBy = "order by semester desc" 
//   *   </tt>
//   */
//  private String genSelect(String[] selectCols, String[] tables,
//      Expression[] ANDs, Expression[] ORs, String[] groupBy, String orderBy) {
//    StringBuffer sb = new StringBuffer("select ");
//
//    for (int i = 0; i < selectCols.length; i++) {
//      sb.append(selectCols[i]);
//      if (i < selectCols.length - 1) {
//        sb.append(",");
//      }
//    }
//
//    sb.append(" from ");
//    for (int i = 0; i < tables.length; i++) {
//      String table = tables[i];
//      sb.append(table);
//      if (i < tables.length - 1)
//        sb.append(",");
//    }
//
//    if (!((ANDs == null) && (ORs == null))) {
//      sb.append(" where ");
//      if (ANDs != null) {
//        for (int i = 0; i < ANDs.length; i++) {
//          sb.append(ANDs[i]);
//          if (i < ANDs.length - 1) {
//            sb.append(" and ");
//          }
//        }
//      }
//      if (ORs != null) {
//        for (int i = 0; i < ORs.length; i++) {
//          sb.append(ORs[i]);
//          if (i < ORs.length - 1) {
//            sb.append(" or ");
//          }
//        }
//      }
//    }
//
//    if (groupBy != null) {
//      sb.append(" group by ");
//      int numGb = groupBy.length;
//      for (int i = 0; i < numGb; i++) {
//        String gbCol = groupBy[i];
//        sb.append(gbCol);
//        if (i < numGb-1)
//          sb.append(",");
//      }
//    }
//    
//    if (orderBy != null) {
//      sb.append(" ").append(orderBy);
//    }
//
//    String sql = sb.toString();
//    
//    return sql;
//  }
//  
//  /**
//   * This method is identical to {@link #genSelect(String[], String[], Expression[], Expression[], String[], String)}, 
//   * except for the use of String[] array for ANDs and ORs expressions. 
//   * 
//   * @effects 
//   *  generate and return a SELECT SQL query whose select columns are <tt>selectCols</tt>, 
//   *  from <tt>tables</tt>, with conditions specified by <tt>ANDs, ORs</tt>, and 
//   *  with GROUP BY <tt>groupBy</tt> and ORDER BY <tt>orderBy</tt>.
//   *  
//   * @example if the desired SELECT query is <pre>
//   *    select semester, max(code) from module group by semester order by semester desc
//   *  </pre> then 
//   *  <tt>selectCols = {"semester", "max(code)"},
//   *      tables = {"module"},
//   *      ANDs = null,
//   *      ORs = null,
//   *      groupBy = {"semester"},
//   *      orderBy = "order by semester desc" 
//   *   </tt>
//   */
//  private String genSelect(String[] selectCols, String[] tables,
//      String[] ANDs, String[] ORs, String[] groupBy, String orderBy) {
//    StringBuffer sb = new StringBuffer("select ");
//
//    for (int i = 0; i < selectCols.length; i++) {
//      sb.append(selectCols[i]);
//      if (i < selectCols.length - 1) {
//        sb.append(",");
//      }
//    }
//
//    sb.append(" from ");
//    for (int i = 0; i < tables.length; i++) {
//      String table = tables[i];
//      sb.append(table);
//      if (i < tables.length - 1)
//        sb.append(",");
//    }
//
//    if (!((ANDs == null) && (ORs == null))) {
//      sb.append(" where ");
//      if (ANDs != null) {
//        for (int i = 0; i < ANDs.length; i++) {
//          sb.append(ANDs[i]);
//          if (i < ANDs.length - 1) {
//            sb.append(" and ");
//          }
//        }
//      }
//      if (ORs != null) {
//        for (int i = 0; i < ORs.length; i++) {
//          sb.append(ORs[i]);
//          if (i < ORs.length - 1) {
//            sb.append(" or ");
//          }
//        }
//      }
//    }
//
//    if (groupBy != null) {
//      sb.append(" group by ");
//      int numGb = groupBy.length;
//      for (int i = 0; i < numGb; i++) {
//        String gbCol = groupBy[i];
//        sb.append(gbCol);
//        if (i < numGb-1)
//          sb.append(",");
//      }
//    }
//    
//    if (orderBy != null) {
//      sb.append(" ").append(orderBy);
//    }
//
//    String sql = sb.toString();
//    
//    return sql;
//  }
//  
//  /**
//   * @effects 
//   *  Generate a CREATE statement for class <code>c</code>.
//   *  
//   *  <p>Throws NotFoundException if <tt>c</tt> is not a registered domain class or 
//   *  required id domain attributes of the class(es) referenced by <tt>c</tt> are not found.
//   */
//  private String genCreate(final Class c) throws NotPossibleException,
//      NotFoundException {
//    // v2.6.4.b:
//    return genCreate(c, null);
////    // get the declared fields of this class
////    List fields = schema.getDsm().getRelationalAttributes(c);
////
////    if (fields == null)
////      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
////          "Không tìm thấy lớp: {0}", c);
////
////    final String typeName = schema.getDsm().getDomainClassName(c);
////    final Class sup = c.getSuperclass();
////
////    DomainConstraint dc = null;
////    Stack<String> pkeys = new Stack();
////    int fkIndex = 0;
////    Type type;
////    boolean inheritedID = false;
////    String idName = null; // keep track of the id field
////    Class refType = null;
////
////    StringBuffer sb = new StringBuffer("create table ");
////    StringBuffer fks = new StringBuffer();
////
////    // table name is same as class name
////    sb.append(typeName).append("(").append(LF);
////
////    for (int i = 0; i < fields.size(); i++) {
////      Field f = (Field) fields.get(i);
////      String name = f.getName().toLowerCase(); // to lower case
////      
////      // field type is either the native type or
////      // the type specified in the DomainConstraint annotation of the field
////      dc = f.getAnnotation(DC);
////      type = dc.type();
////      inheritedID = false;
////      idName = null;
////
////      if (dc.id()) {
////        // id field, add to stack
////        // could be an inherited id (from the super class)
////        if (f.getDeclaringClass() != c) {
////          inheritedID = true;
////        }
////        idName = name;
////      }
////
////      
////      //TODO: can we ever have a multi-column FK?
////      //DomainConstraint[] dcRefs = null;
////      DomainConstraint dcRef = null;
////      
////      if (!type.isDomainType()) {
////        // non-domain type
////        if (!inheritedID) {
////          // just add column
////          sb.append(name).append(" ").append(javaToDBType(type, dc.length()))
////              .append(",").append(LF);
////        } else {
////          // update FK constraints if this is an inherited id
////          refType = sup;// f.getDeclaringClass();
////          dcRef = dc;
////          //dcRefs = new DomainConstraint[] { dc };
////        }
////      } else {
////        // domain type
////        // get the referenced type and the referenced pk name to use as
////        // the table name for this field
////        
////        //TODO: support id field, whose type is set to another domain class
////        // e.g. CoinQty.coin field has the type Coin
////        if (idName != null) { 
////            // not yet support the above at the moment  
////          throw new NotImplementedException(
////              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
////              "Kiểu mã trong là một lớp domain: {0}.{1} {2}", c, f.getType(), idName);            
////        }
////        
////        if (!inheritedID) {
////          refType = f.getType();
////          DomainConstraint[] dcRefs = schema.getDsm().getIDAttributeConstraints(refType);
////          if (dcRefs == null) {
////            throw new NotFoundException(
////                NotFoundException.Code.ID_CONSTRAINT_NOT_FOUND,
////                "Không tìm thấy ràng buộc dạng mã: {0}.{1}: {2}", c.getSimpleName(), name, refType);
////          }
////          
////          if (dcRefs.length>1) {
////            throw new NotImplementedException(
////                NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
////                "Mã ngoại gồm nhiều trường (multi-column FK): {0}", refType);            
////          }
////          dcRef = dcRefs[0];
////        } else { // inherited ids also result in FK constraints
////          refType = sup; // f.getDeclaringClass();
////          //dcRefs = new DomainConstraint[] { dc };
////          dcRef = dc;
////        }
////      } 
////
////      // if this field is an FK, then creates its name differently
////      // and also updates fks
////      if (dcRef != null) {
////        // use tablename_col naming convention for FKs
////        // update the fk constraint at the same time...
////        String refTypeName = schema.getDsm().getDomainClassName(refType);
////        //for (DomainConstraint dcRef : dcRefs) {
////          String refTypePK = dcRef.name();
////          // fk column def: e.g. student_id varchar(20),
////          String fkColName;
////          if (!inheritedID)
////            fkColName = name + "_" + refTypePK;
////          else
////            fkColName = refTypePK; // refTypeName.toLowerCase() + "_" +
////                                   // refTypePK;
////
////          sb.append(fkColName).append(" ")
////              .append(javaToDBType(dcRef.type(), dcRef.length())).append(",")
////              .append(LF);
////                    
////          /** only generate FK constraints if the referenced type is not an Enum */
////          if (!refType.isEnum()) {
////            // fk constraint, e.g.: constraint regionstylefk_1 foreign key
////            // (regionid)
////            // references region(id) on delete cascade on update restrict,
////            // v2.6.4.a: added support for dependsOn
////            boolean dependsOn = (inheritedID || schema.getDsm().isDependentOn(c, dc, refType));
////            
////            String fkName = typeName + "fk" + (fkIndex + 1);
////            fks.append("constraint ").append(fkName).append(" foreign key(")
////            .append(fkColName).append(")").append(" references ")
////            .append(refTypeName).append("(").append(refTypePK).append(")")
////            // v2.6.4.a: only add "on delete cascade" if fkColName depends on refTypePK
////            //.append(" on delete cascade")
////            .append((dependsOn) ? " on delete cascade" : "")
////            .append(" on update restrict").append(",")
////            .append(LF);
////
////            fkIndex++;
////          }
////        //}
////      }
////      
////      // if id field then store it 
////      if (idName != null) {
////        pkeys.push(idName);
////      }
////    } // end field loop
////
////
////    // add PK constraint, e.g.: constraint regionstylepk primary
////    // key(regionid,styleid),
////    if (!pkeys.isEmpty()) {
////      String pkName = typeName + "pk"; // table+pk
////      sb.append("constraint ").append(pkName).append(" primary key(");
////      for (String pk : pkeys)
////        sb.append(pk).append(",");
////      sb.delete(sb.length() - 1, sb.length()); // the trailing comma
////      sb.append(")").append(",").append(LF);
////    }
////
////    // add FK constraints
////    if (fks.length() > 0) {
////      // add a trailing comma
////      sb.append(fks);
////    }
////
////    // remove the trailing comma+lf
////    sb.delete(sb.length() - 2, sb.length());
////
////    // close table def
////    sb.append(")");
////
////    return sb.toString();
//  }
//
//  /**
//   * @modifies 
//   *  tableConstraints
//   * @effects 
//   *  Generate a CREATE statement for class <code>c</code> with the PK constraint(s) but without other table constraints. 
//   *  These constraints (e.g. FKs) are added separately to <tt>tableConstraints</tt>.
//   *  
//   *  <p>Throws NotFoundException if <tt>c</tt> is not a registered domain class or 
//   *  required id domain attributes of the class(es) referenced by <tt>c</tt> are not found.
//   */
//  private String genCreate(final Class c, java.util.Map<String,List<String>> tableConstraints) throws NotPossibleException,
//      NotFoundException {
//    // get the declared fields of this class
//    List fields = dom.getDsm().getSerialisableAttributes(c);
//
//    if (fields == null)
//      throw new NotFoundException(NotFoundException.Code.ATTRIBUTES_NOT_FOUND,
//          "Không tìm thấy thuộc tính dữ liệu nào của lớp: {0}", c);
//
//    final String tableName = dom.getDsm().getDomainClassName(c);
//    final Class sup = c.getSuperclass();
//
//    DomainConstraint dc = null;
//    int fkIndex = 0;
//    Type type;
//    boolean inheritedID = false;
//    String idName = null; // keep track of the id field
//    Class refType = null;
//    DomainConstraint dcRef;
//    
//    StringBuffer sb = new StringBuffer("create table ");
//    Stack<String> pkeys = new Stack();
//    //StringBuffer fks = new StringBuffer();
//    StringBuffer fk;
//    List<String> fks = new ArrayList();
//    
//    // table name is same as class name
//    sb.append(tableName).append("(").append(LF);
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
//      //DomainConstraint dcRef = null;
//      dcRef = null;
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
//              "Kiểu mã là một lớp domain: {0}.{1} {2}", c, f.getType(), idName);            
//        }
//        
//        if (!inheritedID) {
//          refType = f.getType();
//          
//          DomainConstraint[] dcRefs = dom.getDsm().getIDAttributeConstraints(refType);
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
//
//          /*v2.6.4b: additional check: skip if the association between c and refType is 1:1 and 
//           * refType's end is the determinant of the association
//           */
////          if (schema.getDsm().isDeterminedByAssociate(f) == false) {
////            DomainConstraint[] dcRefs = schema.getDsm().getIDAttributeConstraints(refType);
////            if (dcRefs == null) {
////              throw new NotFoundException(
////                  NotFoundException.Code.ID_CONSTRAINT_NOT_FOUND,
////                  "Không tìm thấy ràng buộc dạng mã: {0}.{1}: {2}", c.getSimpleName(), name, refType);
////            }
////            
////            if (dcRefs.length>1) {
////              throw new NotImplementedException(
////                  NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
////                  "Mã ngoại gồm nhiều trường (multi-column FK): {0}", refType);            
////            }
////            dcRef = dcRefs[0];
////          }
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
//        String refTypeName = dom.getDsm().getDomainClassName(refType);
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
//            boolean dependsOn = (inheritedID || dom.getDsm().isDependentOn(c, dc, refType));
//            
//            String fkName = tableName + "fk" + (fkIndex + 1);
//            /* v2.6.4.b: add to tableConstraints
//            fks.append("constraint ").append(fkName).append(" foreign key(")
//            .append(fkColName).append(")").append(" references ")
//            .append(refTypeName).append("(").append(refTypePK).append(")")
//            // v2.6.4.a: only add "on delete cascade" if fkColName depends on refTypePK
//            //.append(" on delete cascade")
//            .append((dependsOn) ? " on delete cascade" : "")
//            .append(" on update restrict").append(",")
//            .append(LF);
//            */
//            fk = new StringBuffer();
//            fk.append("constraint ").append(fkName).append(" foreign key(")
//            .append(fkColName).append(")").append(" references ")
//            .append(refTypeName).append("(").append(refTypePK).append(")")
//            .append((dependsOn) ? " on delete cascade" : "")
//            .append(" on update restrict");
//            
//            fks.add(fk.toString());
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
//      String pkName = tableName + "pk"; // table+pk
//      sb.append("constraint ").append(pkName).append(" primary key(");
//      for (String pk : pkeys)
//        sb.append(pk).append(",");
//      sb.delete(sb.length() - 1, sb.length()); // the trailing comma
//      sb.append(")").append(",").append(LF);
//    }
//
//    // add FK constraints
//    /*v2.6.4.b: moved to tableconstraints
//    if (fks.length() > 0) {
//      // add a trailing comma
//      sb.append(fks);
//    }
//    */
//    if (!fks.isEmpty()) {
//      if (tableConstraints != null)
//        tableConstraints.put(tableName, fks);
//      else
//        for (String _fk : fks) sb.append(_fk).append(",")
//        .append(LF); 
//    }
//
//    // remove the trailing comma+lf
//    sb.delete(sb.length() - 2, sb.length());
//
//    // close table def
//    sb.append(")");
//
//    return sb.toString();
//  }
//  
//  /**
//   * @effects 
//   *  return the string representation of <tt>SqlType</tt> equivalent of <tt>javaType (length)</tt>
//   *  
//   *  <p>throws NotImplementedException if <tt>javaType</tt> is not supported
//   */
//  private String javaToDBType(Type javaType, int length) throws NotImplementedException {
//    if (length <= 0)
//      length = DEFAULT_LENGTH;
//
//    /*v3.0: move to method
//    SqlType sqlType = SqlType.getMapping(javaType);
//    */
//    SqlType sqlType = getSqlTypeFor(getSqlTypeClass(), javaType);
//    
//    if (sqlType == null) {
//      // not supported
//      throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
//          "Không hỗ trợ kiểu dữ liệu: {0}", javaType);
//    } else {
//      return sqlType.toString(length);
//    }
//  }
//
//  @Override
//  protected Class<? extends SqlType> getSqlTypeClass() {
//    return JavaDbSqlType.class;
//  }
//
//  /**
//   * Generate an SQL insert statement for a domain object.
//   * 
//   * @requires <code>o</code> is assignment compatible to <code>c</code> and the
//   *           database table of <code>c</code> was created by the SQL statement
//   *           generated by the method {@link #genCreate(Class)} using
//   *           <code>c</code> as an argument, 
//   *           <tt>updateAttributes != null</tt>
//   * @modifies <tt>updateAttributes</tt>
//   * @effects 
//   *  if <tt>c</tt> has domain attributes 
//   *    return the SQL <b>prepared</b> INSERT statement for <tt>o</tt>
//   *    add to <tt>updateAttributes</tt> the attributes whose values are to be set 
//   *  else 
//   *    throw <tt>NotFoundException</tt>
//   * 
//   * @see {@link #genCreate(Class)}
//   * */
//  private String genParameterisedInsert(Class c, Object o, List<Field> updateAttributes) throws NotFoundException {
//    // get the relational attributes of this class
//    List<Field> fields = dom.getDsm().getSerialisableAttributes(c);
//
//    if (fields == null)
//      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
//          "Không tìm thấy lớp: {0}", c);
//
//    StringBuffer sb = new StringBuffer("insert into ");
//    sb.append(dom.getDsm().getDomainClassName(c)).append("(");
//
//    StringBuffer valueSb = new StringBuffer(" values(");
//    
//    // append values (object values)
//    Field f;
//    Object v;
//    DomainConstraint dc;
////    Type type;  // v2.6.4.b
////    boolean isDeterminedByAssociate;  // v2.6.4.b
//    int index=0;
//    
//    for (int i = 0; i < fields.size(); i++) {
//      f = fields.get(i);
//      dc = f.getAnnotation(DC);
//      v = dom.getDsm().getAttributeValue(f, o);
//      
//      if (v != null) {
//        // the comma
//        if (index > 0) {
//          sb.append(",");
//          valueSb.append(",");
//        }
//        
//        // column name
//        sb.append(getColumName(c, f));
//        
//        // the value
//        valueSb.append("?");
//        
//        // add this to the update attributes 
//        updateAttributes.add(f);
//        
//        index++;
//      }
//      /*v2.6.4.b: skip if this is determined by associate in a 1:1 association
//      type = dc.type();
//      isDeterminedByAssociate = (type.isDomainType()) ? 
//          schema.getDsm().isDeterminedByAssociate(f) : false;
//      if (!isDeterminedByAssociate) {
//        v = schema.getDsm().getAttributeValue(f, o);
//        
//        if (v != null) {
//          // the comma
//          if (index > 0) {
//            sb.append(",");
//            valueSb.append(",");
//          }
//          
//          // column name
//          sb.append(getColumName(c, f));
//          
//          // the value
//          valueSb.append("?");
//          
//          // add this to the update attributes 
//          updateAttributes.add(f);
//          
//          index++;
//        }
//      }
//      */
//    }
//
//    sb.append(")").append(valueSb).append(")");
//
//    return sb.toString();
//  }
//
//  /**
//   * @requires <code>c</code> is assignment compatible to <code>o</code>, 
//   *          <tt>updateAttributes != null</tt>
//   * @modifies updateAttributes
//   * @effects 
//   *  if <code>c</code> has non-id domain attributes 
//   *    return the SQL UPDATE query for <tt>o</tt>
//   *    add to <tt>updateAttributes</tt> the <tt>Field</tt> objects representing the attributes whose values
//   *    are parameterised in this SQL query 
//   *  else if <code>c</code> does not have domain attributes 
//   *    throw<code>NotFoundException</code>
//   *  else if <code>c</code> does not have non-id domain attributes 
//   *    return <code>null</code>.
//   */
//  private String genParameterisedUpdate(Class c, Object o, List<Field> updateAttributes) throws NotFoundException {
//    // get the declared fields of this class
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
//    // v2.6.4.b: support 1:1 association 
////    boolean isDeterminedByAssociate; 
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
//      DomainConstraint[] dcFKs = null;
////      isDeterminedByAssociate = type.isDomainType() ? 
////          schema.getDsm().isDeterminedByAssociate(f) : false;  // v2.6.4.b
//      
//      // get the column name(s)
//      if (dc.id()) {
//        if ((f.getDeclaringClass() != c)) {
//          inheritedID = true;
//          refNames = new String[] { name };
//        }        
//      } 
//      
//      if (type.isDomainType()) {
//        // ref names: depend on whether the attribute is an inherited id or
//        // not
//        // if it is, then it is prefix with the super-class name; else it
//        // is prefixed with the ref table name
//        domainType = f.getType();
//        dcFKs = dom.getDsm().getIDAttributeConstraints(domainType);
//
//        if (!inheritedID) {
//          refNames = new String[dcFKs.length];
//          int j=0;
//          for (DomainConstraint dcFK : dcFKs) {
//              refNames[j] = name + "_" + dcFK.name();
//          }
//        }
//        /* v2.6.4.b: skip if this is determined by associate in a 1:1 association 
//        if (!isDeterminedByAssociate) {
//          // ref names: depend on whether the attribute is an inherited id or
//          // not
//          // if it is, then it is prefix with the super-class name; else it
//          // is prefixed with the ref table name
//          domainType = f.getType();
//          dcFKs = schema
//              .getIDAttributeConstraints(domainType);
//
//          if (!inheritedID) {
//            refNames = new String[dcFKs.length];
//            int j=0;
//            for (DomainConstraint dcFK : dcFKs) {
//                refNames[j] = name + "_" + dcFK.name();
//            }
//          }
//        }        */
//      }
//      
//      // get the id value(s) to use in the WHERE part
//      if (dc.id()) {
//        v = dom.getDsm().getAttributeValue(f, o);
//
//        // field type is either the native type or
//        // the type specified in the DomainConstraint annotation of the field
//        if (type.isPrimitive()) {
//          sqlV = toSQLString(type, v, false);
//
//          if (inheritedID) {
//            refVals = new String[1];
//            refVals[0] = (String) sqlV;
//          }
//        } else if (type.isDomainType()) {
//          // ref values: are the values of the id-attributes of the ref
//          // attributes
//          refVals = new String[dcFKs.length];
//          int j = 0;
//          for (DomainConstraint dcFK : dcFKs) {
//            sqlV = (v != null) ? dom.getDsm().getAttributeValue(v, dcFK.name())
//                : null;
//            refVals[j] = toSQLString(dcFK.type(), sqlV, false);
//            j++;
//          }
//        } else {
//          throw new NotImplementedException(
//              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//              "Tính năng hiện không được hỗ trợ: {0}", type.name());
//        }
//      } // end if id attribute
//
//      if (!dc.id()) { // non-id attributes
//        if (!hasNoneID)
//          hasNoneID = true;
//        // SET part
//        // extract non-id attributes
//        if (type.isDomainType()) {
//          //int j = 0;
//          for (String refName : refNames) {
//            sb.append(refName).append("=").append("?"
//                //refVals[j++]
//                    ).append(",");
//          }
//          /* v2.6.4.b: skip if this is determined by associate in a 1:1 association
//          if (!isDeterminedByAssociate) {
//            for (String refName : refNames) {
//              sb.append(refName).append("=").append("?"
//                  //refVals[j++]
//                      ).append(",");
//            }
//          }          */
//        } else {
//          sb.append(name).append("=").append("?"
//              //sqlV
//              ).append(",");
//        }
//        
//        // add this attribute to the list
//        updateAttributes.add(f);
//        /*v2.6.4.b: skip if this is determined by associate in a 1:1 association 
//        if (!isDeterminedByAssociate) {
//          // add this attribute to the list
//          updateAttributes.add(f);
//        }*/        
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
//
//  /**
//   * @effects 
//   *  return the name of the table column that is mapped to the attribute named <tt>attribName</tt> 
//   *  of the domain class <tt>c</tt> 
//   */
//  private String getColName(Class c, DomainConstraint attrib) {
//    Field f = dom.getDsm().getDomainAttribute(c, attrib.name());
//    return getColumName(c, f);
//  }
//
//  /**
//   * @requires 
//   *  fkAttrib describes the foreign-key column /\
//   *  refPkAttrib describes the corresponding primary-key column /\ 
//   *  table(refPkAttrib) is not a super-type of table(fkAttrib)  
//   *  
//   * @effects 
//   *  return the standard name for the foreign-key column fkAttrib
//   *  which references the primary key column refPkAttrib, 
//   *  and where the two tables involved are not super-type/sub-type.
//   */
//  private String getFKColName(DomainConstraint fkAttrib, DomainConstraint refPkAttrib) {
//    return (fkAttrib.name().toLowerCase() + "_" + refPkAttrib.name());
//  }
//  
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
//  
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
//
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
//
//  /**
//   * @effects 
//   *  generate an SQL UPDATE statement for objects of <tt>c</tt> that satisfies the <tt>searchExp</tt> and 
//   *  that uses <tt>updateExp</tt> as the update expressions.
//   *   
//   * @requires a table for <code>c</code> has been created in the data source
//   * 
//   * @example
//   * <pre>
//   *  c := Student
//   *  searchExp := Student.name ~= "An"
//   *  updateExp := Student.sclass = SClass<1c11>
//   *  -> SQL: 
//   *  update Student 
//   *    set sclass_name = "1c11" 
//   *    where Student.name like '%An%' 
//   * </pre>
//   * @pseudocode
//   *  let UPDATE := "update c"
//   *  
//   *  let SET be a string
//   *  let WHERE be a set of strings
//   *  
//   *  let updateExp = (c,A,'=',v)
//   *  if A is inherited from an ancestor domain class a,
//   *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
//   *    let j = SQL_Join(c,x,...,a) (x may = a)
//   *    add j to WHERE
//   *    SET := "a.A = v"
//   *  else 
//   *    SET := "A = v"
//   *    
//   * let searchExp = (c,B,op,v)
//   *  if B is inherited from an ancestor domain class b
//   *    if b != a AND b has not been processed
//   *     let k = SQL_Join(c,y,...,b) (y may = b)
//   *     merge k into WHERE
//   *    add op(b.B,v) to WHERE
//   *  else 
//   *    add op(B,v) to WHERE  
//   */
//  private String genUpdate(Class c, Query<ObjectExpression> searchQuery, Query<ObjectExpression> updateQuery) throws NotFoundException {
//    // get the declared fields of this class
//    Collection<String> Set = new ArrayList(); 
//    Collection<String> Where = new ArrayList(); 
//    Collection<String> joinTablePairs = new ArrayList();
//    
//    final String cTable = dom.getDsm().getDomainClassName(c);
//    String tA;
//    
//    // to record the classes that have been processed
//    java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();
//
//    Collection<DomainConstraint> idAttribs;
//    String supTable, tsupTable;
//    Class sup, currClass;
//    String currTable = cTable;
//    String colName;
//    
//    // generate SET
//    Iterator<ObjectExpression> updateExps = updateQuery.terms(); 
//    ObjectExpression updateExp;
//    while (updateExps.hasNext()) {
//      updateExp = updateExps.next();
//      DomainConstraint A = updateExp.getDomainAttribute();
//      Class a = dom.getDsm().getDeclaringClass(c, A);
//      
//      currTable = cTable;
//      currClass = c;
//      if (a != c && !processed.containsKey(a)) {
//        /*  if A is inherited from an ancestor domain class a,
//        *  (i.e. a = super(...(c)...), where ... is the path in the generalisation hierarchy from c -> a) then
//        *    if a has not been processed
//        *     let j = SQL_Join(c,x,...,a) (x may = a)
//        *     add j to WHERE
//        *    SET := "a.A = v"
//        *  else 
//        *    SET := "A = v"
//        */
//        sup = dom.getDsm().getSuperClass(c);
//        do {
//          supTable = dom.getDsm().getDomainClassName(sup);
//  
//          // use the id attributes to add new join expressions
//          idAttribs = dom.getDsm().getIDDomainConstraints(sup);
//          for (DomainConstraint f : idAttribs) { // current table
//            // add join expressions between the id attributes of the two tables
//            Where.add(join(currClass, currTable, sup, supTable, f));//currTable + "." + f.name() + "=" + supTable + "." + f.name());
//            
//            joinTablePairs.add(currTable+"-"+supTable);
//          } // end for
//  
//          // recursive: check the super-super class and so on...
//          currTable = supTable;
//          currClass = sup;
//          sup = dom.getDsm().getSuperClass(sup);
//        } while (sup != null);
//        
//        // attribute table is the last super class name
//        tA = currTable;
//        processed.put(a, tA);
//      } else if (a == c) {
//        tA = cTable;
//      } else {  // a already processed
//        tA = processed.get(a);
//      } // end if 
//      
//      // Add attrib to SET
//      colName = getColName(a,A);
//      colName = tA+"."+colName;
//      Set.add(toSQLExpression(updateExp, colName,false));
//    }
//    
//    // update Where with searchQuery
//    if (searchQuery != null) {
//      ObjectExpression searchExp;
//      Iterator<ObjectExpression> searchExps = searchQuery.terms();
//      while (searchExps.hasNext()) {
//        searchExp = searchExps.next();
//         /*  
//          * let searchExp = (c,B,op,v)
//          *  if B is inherited from an ancestor domain class b
//          *    if b != c AND b has not been processed
//          *     let k = SQL_Join(c,y,...,b) (y may = b)
//          *     merge k into WHERE
//          *    add op(b.B,v) to WHERE
//          *  else 
//          *    add op(B,v) to WHERE    
//          */
//        Class b;
//        String tB;
//        idAttribs = dom.getDsm().getIDDomainConstraints(c);
//        
//        DomainConstraint B = searchExp.getDomainAttribute();
//        b = dom.getDsm().getDeclaringClass(c, B);
//        if (b != c && !processed.containsKey(b)) {
//          sup = dom.getDsm().getSuperClass(c);
//          currTable = cTable;
//          currClass = c;
//          do {
//            supTable = dom.getDsm().getDomainClassName(sup);
//
//            // merge Join into WHERE
//            if (!joinTablePairs.contains(currTable+"-"+supTable)) {
//              // use the id attributes to add new join expressions
//              idAttribs = dom.getDsm().getIDDomainConstraints(sup);
//              for (DomainConstraint f : idAttribs) { // current table
//                // add join expressions between the id attributes of the two tables
//                Where.add(join(currClass, currTable, sup, supTable, f));
//                
//                joinTablePairs.add(currTable+"-"+supTable);
//              } // end for
//            }
//            
//            // recursive: go up to the super-super class and so on...
//            currTable = supTable;
//            currClass = sup;
//            sup = dom.getDsm().getSuperClass(sup);
//          } while (sup != null);
//          
//          tB = currTable;
//          processed.put(b, currTable);
//        } else if (b == c) {
//          tB = cTable;
//        } else {  // b already processed
//          tB = processed.get(b);
//        }  // end if
//        colName = getColName(b, B);
//        colName = tB + "."+colName;
//        Where.add(toSQLExpression(searchExp, colName,true));
//      } // end while
//    } // end if
//    
//    
//    // generate SQL
//    StringBuffer sqlb = new StringBuffer();
//    
//    sqlb.append("update ").append(cTable);
//    
//    sqlb.append(" set ");
//    int i = 0;
//    for (String set : Set) {
//      sqlb.append(set);
//      if (i < Set.size()-1) sqlb.append(", ");
//      i++;
//    }
//    
//    if (!Where.isEmpty()) {
//      sqlb.append(" where ");
//      i = 0;
//      for (String w : Where) {
//        sqlb.append(w);
//        if (i < Where.size()-1) sqlb.append(" and ");
//        i++;
//      }
//    }
//    
//    return sqlb.toString();
//  }
//  
//  /**
//   * @effects if the domain class <code>c</code> has domain attributes then
//   *          returns the SQL DELETE statement for domain object <code>o</code>
//   *          from the database table of <code>c</code>, else throws
//   *          <code>NotFoundException</code>
//   */
//  private String genDelete(Class c, Object o) throws NotFoundException {
//    // get the declared fields of this class
//    // Class c = o.getClass();
//    List fields = dom.getDsm().getSerialisableAttributes(c);
//
//    if (fields == null)
//      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
//          "Không tìm thấy lớp: {0}", c);
//
//    StringBuffer sb = null;
//    sb = new StringBuffer("delete from ");
//    sb.append(dom.getDsm().getDomainClassName(c)).append(" WHERE ");
//
//    Type type;
//    Class domainType;
//    String name;
//    String[] refNames = null;
//    Object v = null;
//    Object sqlV = null;
//
//    // append values (object values)
//    for (int i = 0; i < fields.size(); i++) {
//      Field f = (Field) fields.get(i);
//
//      DomainConstraint dc = f.getAnnotation(DC);
//      if (dc.id()) { // only interested in the id attributes
//        name = dc.name();
//        type = dc.type();
//        // prepare the attribute value
//        v = dom.getDsm().getAttributeValue(f, o);
//        // field type is either the native type or
//        // the type specified in the DomainConstraint annotation of the field
//        //if (type.isString() || type.isPrimitive()) {
//        if (type.isPrimitive()) {
//          sb.append(name).append("=").append(toSQLString(type, v, false)) // v is never null
//              .append(" and ");
//        } else if (type.isDomainType()) {
//          domainType = f.getType();
//          DomainConstraint[] dcFKs = dom.getDsm().getIDAttributeConstraints(domainType);
//          refNames = new String[dcFKs.length];
//          int j = 0;
//          for (DomainConstraint dcFK : dcFKs) {
//            refNames[j] = dcFK.name();
//            sqlV = dom.getDsm().getAttributeValue(v, dcFK.name());
//            sb.append(name + "_" + dcFK.name()).append("=")
//                .append(toSQLString(dcFK.type(), sqlV,false)).append(" and ");
//          }
//        } else {
//          throw new NotImplementedException(
//              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//              "Tính năng hiện không được hỗ trợ: {0}", type.name());
//        }
//      }
//    } // end for loop
//
//    // remove extra trailing seperators
//    sb.delete(sb.length() - 5, sb.length()); // " and "
//
//    return sb.toString();
//  }
//
//  
//  /**
//   * @effects 
//   *  generate an SQL DELETE statement for objects of <tt>c</tt> that satisfies the expressions in 
//   *  <tt>searchQuery</tt>
//   *   
//   * @requires a table for <code>c</code> has been created in the data source
//   * 
//   * @example
//   * <pre>
//   *  c := Student
//   *  searchExp := Student.name ~= "An"
//   *  -> SQL: 
//   *  delete from Student 
//   *    where Student.name like '%An%' 
//   * </pre>
//   * @pseudocode
//   * <pre>
//   *  let DELETE := "delete from c"
//   *  
//   *  let WHERE be a set of strings
//   *  
//   *  for each searchExp in searchQuery.terms
//   *    let searchExp = (c,B,op,v)
//   *    if B is inherited from an ancestor domain class b
//   *      if b has not been processed
//   *       let k = SQL_Join(c,y,...,b) (y may = b)
//   *       merge k into WHERE
//   *      add op(b.B,v) to WHERE
//   *    else 
//   *      add op(B,v) to WHERE
//   *      </pre>  
//   */
//  private String genDelete(Class c, Query<ObjectExpression> searchQuery) throws NotFoundException {
//    Collection<String> Where = new ArrayList(); 
//    Collection<String> joinTablePairs = new ArrayList();
//    
//    final String cTable = dom.getDsm().getDomainClassName(c);
//    String tA;
//    
//    // to record the classes that have been processed
//    java.util.Map<Class,String> processed = new LinkedHashMap<Class,String>();
//
//    Collection<DomainConstraint> idAttribs;
//    String supTable, tsupTable;
//    Class sup, currClass;
//    String currTable = cTable;
//    String colName;
//    
//    // update Where with searchQuery
//    if (searchQuery != null) {
//      ObjectExpression searchExp;
//      Iterator<ObjectExpression> searchExps = searchQuery.terms();
//      while (searchExps.hasNext()) {
//        searchExp = searchExps.next();
//         /*  
//          * let searchExp = (c,B,op,v)
//          *  if B is inherited from an ancestor domain class b
//          *    if b != c AND b has not been processed
//          *     let k = SQL_Join(c,y,...,b) (y may = b)
//          *     merge k into WHERE
//          *    add op(b.B,v) to WHERE
//          *  else 
//          *    add op(B,v) to WHERE    
//          */
//        Class b;
//        String tB;
//        idAttribs = dom.getDsm().getIDDomainConstraints(c);
//        
//        DomainConstraint B = searchExp.getDomainAttribute();
//        b = dom.getDsm().getDeclaringClass(c, B);
//        if (b != c && !processed.containsKey(b)) {
//          sup = dom.getDsm().getSuperClass(c);
//          currTable = cTable;
//          currClass = c;
//          do {
//            supTable = dom.getDsm().getDomainClassName(sup);
//
//            // merge Join into WHERE
//            if (!joinTablePairs.contains(currTable+"-"+supTable)) {
//              // use the id attributes to add new join expressions
//              idAttribs = dom.getDsm().getIDDomainConstraints(sup);
//              for (DomainConstraint f : idAttribs) { // current table
//                // add join expressions between the id attributes of the two tables
//                Where.add(join(currClass, currTable, sup, supTable, f));
//                
//                joinTablePairs.add(currTable+"-"+supTable);
//              } // end for
//            }
//            
//            // recursive: go up to the super-super class and so on...
//            currTable = supTable;
//            currClass = sup;
//            sup = dom.getDsm().getSuperClass(sup);
//          } while (sup != null);
//          
//          tB = currTable;
//          processed.put(b, tB);
//        } else if (b == c) {
//          tB = cTable;
//        } else {  // b already processed
//          tB = processed.get(b);
//        }  // end if
//        colName = getColName(b, B);
//        colName = tB + "."+colName;
//        Where.add(toSQLExpression(searchExp, colName,true));
//      } // end while
//    } // end if
//    
//    
//    // generate SQL
//    StringBuffer sqlb = new StringBuffer();
//    
//    sqlb.append("delete from ").append(cTable);
//    
//    if (!Where.isEmpty()) {
//      sqlb.append(" where ");
//      int i = 0;
//      for (String w : Where) {
//        sqlb.append(w);
//        if (i < Where.size()-1) sqlb.append(" and ");
//        i++;
//      }
//    }
//    
//    return sqlb.toString();  
//  }
//  
//  /**
//   * @effects 
//   *  return an SQL Inner Join between tables <tt>t1</tt> (of the class <tt>c1</tt>) and 
//   *  table <tt>t2</tt> (of the class <tt>c2</tt>) on the join attribute <tt>joinAttrib</tt>.
//   */
//  private String join(Class c1, String t1, Class c2, String t2, DomainConstraint joinAttrib) {
//    String col1, col2;
//    
//    col1 = getColName(c1,joinAttrib);
//    col2 = getColName(c2,joinAttrib);
//    
//    return join(t1, col1, t2, col2); 
//    //t1 + "." + col1 + "=" + t2 + "." + col2;
//  }
//
//  /**
//   * @effects 
//   *  return an SQL Inner Join between tables <tt>t1</tt> (of the class <tt>c1</tt>) and 
//   *  table <tt>t2</tt> (of the class <tt>c2</tt>) on the join attribute <tt>joinAttrib</tt>.
//   */
//  private String join(String t1, String col1, String t2, String col2) {
//    return t1 + "." + col1 + "=" + t2 + "." + col2;
//  }
//
//  /**
//   * @effects invokes {@link #toDBColumnName(Class, String, boolean)}, with the
//   *          second argument being set to
//   *          <code>attributeConstraint.name()</code>.
//   */
//  public String toDBColumnName(Class c, DomainConstraint attributeConstraint,
//      boolean withTablePrefix) throws NotFoundException {
//    return toDBColumnName(c, attributeConstraint.name(), withTablePrefix);
//  }
//
//  /**
//   * @effects returns the precise table column name corresponds to the domain
//   *          attribute <code>attribute </code> of the domain class
//   *          <code>c</code>.
//   * 
//   *          <p>
//   *          If <code>attribute</code> is a non-domain-type then it is the same
//   *          as <code>attribute.name</code>, else it is of the form
//   *          <code>type.name_attribute.name</code> where <code>type</code> is
//   *          the type of the attribute.
//   * 
//   *          <p>
//   *          If <code>withTablePrefix=true</code> then the attribute name is
//   *          prefixed with the table prefix, such as <code>Student.id</code>.
//   * 
//   *          <p>
//   *          Throws <code>NotFoundException</code> if the required identifier
//   *          domain constraints of the associated domain type are not found.
//   */
//  public String toDBColumnName(Class c, String attribute,
//      boolean withTablePrefix) throws NotFoundException {
//    String colName;
//    Field f = dom.getDsm().getDomainAttribute(c, attribute);
//    DomainConstraint dc = f.getAnnotation(DC);
//
//    if (dc.type().isDomainType()) {
//      Class domainType = f.getType();
//      DomainConstraint[] idcs = dom.getDsm().getIDAttributeConstraints(domainType);
//      if (idcs == null) {
//        throw new NotFoundException(
//            NotFoundException.Code.CONSTRAINT_NOT_FOUND,
//            "Không tìm thấy ràng buộc dữ liệu: {0}", domainType);
//      }
//
//      if (idcs.length > 1) {
//        throw new NotImplementedException(
//            NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//            "Tính năng hiện không được hỗ trợ: {0}", "compoundKey("
//                + domainType + ")");
//      }
//
//      // a combination of the type-name and the name of its id attribute
//      colName = dc.name() + "_" + idcs[0].name();
//    } else {
//      colName = dc.name();
//    }
//
//    if (withTablePrefix) {
//      String tableName = dom.getDsm().getDomainClassName(c);
//      colName = tableName + "." + colName;
//    }
//
//    return colName;
//  }
//
//  /**
//   * @requires 
//   *  v != null -> type != null
//   *  
//   * @effects
//   *  return the SQL string equivalent of value <tt>v</tt> whose data type is <tt>type</tt>
//   *  
//   */
//  /*v2.7.2: added isSelectQuery to correctly return value for two types of SQL statements*/
//  private String toSQLString(Type type, Object v, boolean isSelectQuery) {
//    if (v == null) {
//      if (isSelectQuery)
//        return null;  // remain as null
//      else
//        return "DEFAULT"; // update query: use DEFAULT
//    } else {
//      if (type.isString() || type.isBoolean()) {
//        return ("'" + v + "'");
//      } else { // if (type.isPrimitive()) {
//        return (v + "");
//      }
//    }
//  }
//  
//  /**
//   * @effects 
//   *  convert object expression's operator to <tt>SqlOp</tt>
//   */
//  private SqlOp toSQLOperator(Expression.Op op) {
//    SqlOp[] sqlOps = SqlOp.values();
//    for (SqlOp sqlOp : sqlOps) {
//      if (sqlOp.getMapping() == op) {
//        return sqlOp;
//      } 
//    }
//    
//    // something wrong, no mapping defined
//    throw new InternalError("DBToolKit.toSQLOperator: no SQL conversion defined for " + op);
//  }
//  
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
//  
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
//  
//  private String toSQLExpression(ObjectExpression exp, String variable, boolean isSelectQuery) throws NotFoundException {
//    String var = (variable != null) ? variable : exp.getVar();
//    Object val = exp.getVal();
//    //Expression.Type type = exp.getType();
//    Expression.Op op = exp.getOperator();
//    
//    SqlOp sqlOp = toSQLOperator(op);
//    
//    ObjectExpression oexp = (ObjectExpression) exp;
//    AttribFunctor attribFunctor = oexp.getAttributeFunctor();
//    DomainConstraint attrib = oexp.getDomainAttribute();
//    
//    /*two cases: domain-type val and expression with attribute functor */
//    if (attribFunctor != null) {
//      // attribute functor is specified (attribute's type must not be a domain type)
//      // convert the specified function into an equivalent SQL function over the attribute
//      SqlFunction sqlFunc = toSQLFunction(attribFunctor.function());
//      return sqlFunc.toString(var) + 
//          /*v2.7.2: moved op's toString to SqlOp class
//          sqlOp.toString(toSQLString(attrib.type(), val, isSelectQuery));*/
//          sqlOp.toString(attrib.type(), val, isSelectQuery);
//    } else if (attrib.type().isDomainType()) {
//      // domain-type (referenced) val -> get the domain id of val to use 
//      Object refIdVal = null; 
//      DomainConstraint refIdAttrib = null;
//      if (val != null) {
//        Class cls = val.getClass();
//        Oid refId = dom.lookUpObjectId(cls, val);
//        if (refId == null) {
//          // should not happen
//          throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NOT_FOUND, 
//              "Không tìm thấy mã đối tượng {0}<{1}>", cls.getSimpleName(), val);
//          
//        }
//        
//        if (refId.size() > 1) {
//          throw new NotImplementedException(NotImplementedException.Code.OBJECT_ID_NOT_SUPPORTED, 
//              "Không hỗ trợ mã đối tượng {0}", refId);
//        }
//        
//        refIdAttrib = refId.getIdAttribute(0);
//        refIdVal = refId.getIdValue(0);
//      }
//      
//      var = getColName(oexp.getDomainClass(), attrib);
//      return var + 
//          /*v2.7.2: moved op's toString to SqlOp class
//          sqlOp.toString(
//          toSQLString((refIdAttrib != null) ? refIdAttrib.type() : null,refIdVal, isSelectQuery));*/
//          sqlOp.toString((refIdAttrib != null) ? refIdAttrib.type() : null,refIdVal, isSelectQuery);
//    } 
//    // add support for other special cases here
//    else {
//      // process as a normal expression
////      throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
////          "Không hỗ trợ tính năng {0}", "Object expression: " + oexp);
//      return var + 
//          /*v2.7.2: moved op's toString to SqlOp class
//          sqlOp.toString(toSQLString(attrib.type(), val, isSelectQuery)); */
//          sqlOp.toString(attrib.type(), val, isSelectQuery);
//    }
//  }
//  
//  private String toSQLExpression(IdExpression exp, String variable, boolean isSelectQuery) throws NotFoundException {
//    String var; //= (variable != null) ? variable : exp.getVar();
//    //Object val = exp.getVal();
//    //Expression.Type type = exp.getType();
//    Expression.Op op = exp.getOperator();
//    SqlOp sqlOp = toSQLOperator(op);
//
//    // use the id value directly (without the need to lookup)
//    IdExpression iexp = (IdExpression) exp;
//    Oid refId = iexp.getVal();
//    if (refId.size() > 1) {
//      throw new NotImplementedException(NotImplementedException.Code.OBJECT_ID_NOT_SUPPORTED, 
//          "Không hỗ trợ mã đối tượng {0}", refId);
//    }
//    
//    DomainConstraint refIdAttrib = refId.getIdAttribute(0);
//    Object refIdVal = refId.getIdValue(0);
//  
//    //TODO: it seems that variable is not used here?
//    var = getColName(iexp.getDomainClass(), iexp.getDomainAttribute());
//    return var + 
//        /*v2.7.2: moved op's toString to SqlOp class
//          sqlOp.toString(toSQLString(refIdAttrib.type(),refIdVal,isSelectQuery)); */
//        sqlOp.toString(refIdAttrib.type(), refIdVal, isSelectQuery);
//  }
//  
//  /**
//   * @effects 
//   *  if variable != null
//   *    converts exp to an equivalent SQL expression using variable as the alternative variable name (where appropriate)
//   *  else 
//   *    converts exp to an equivalent SQL expression exp.var as the variable name (where appropriate)
//   *  @deprecated 
//   *    v2.6.4b: to be removed
//   */
//  // TODO: revise this: divide in two: (1) convert op, (2) convert val (using toSQLString)
//  private String toSQLExpressionOLD(Expression exp, String variable) throws NotFoundException {
//    final boolean withNulls = false;
//    
//    String var = (variable != null) ? variable : exp.getVar();
//    Object val = exp.getVal();
//    Expression.Type type = exp.getType();
//    Expression.Op op = exp.getOperator();
//    
//    Object v;
//    if (val == null && !withNulls)
//      v = "";
//    else 
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
//      } else {
//        if (op == Expression.Op.MATCH) {
//          // approximate matching
//          return var + " like '%" + v + "%'";
//        } else if (
//            (exp instanceof ObjectExpression)) {
//          ObjectExpression oexp = (ObjectExpression) exp;
//          DomainConstraint attrib = oexp.getDomainAttribute();
//          
//          /*two cases: domain-type val and expression with attribute functor */
//          if (attrib.type().isDomainType()) {
//            // domain-type val -> get the domain id of val to use 
//            Object refIdVal = null; 
//            DomainConstraint refIdAttrib = null;
//            if (val != null) {
//              Class cls = val.getClass();
//              Oid refId = dom.lookUpObjectId(cls, val);
//              if (refId == null) {
//                // should not happen
//                throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NOT_FOUND, 
//                    "Không tìm thấy mã đối tượng {0}<{1}>", cls.getSimpleName(), val);
//                
//              }
//              
//              if (refId.size() > 1) {
//                throw new NotImplementedException(NotImplementedException.Code.OBJECT_ID_NOT_SUPPORTED, 
//                    "Không hỗ trợ mã đối tượng {0}", refId);
//              }
//              
//              refIdAttrib = refId.getIdAttribute(0);
//              refIdVal = refId.getIdValue(0);
//            }
//            
//            var = getColName(oexp.getDomainClass(), attrib);
//            return var + op.getName() + toSQLString(
//                (refIdAttrib != null) ? refIdAttrib.type() : null, 
//                    refIdVal, false);
//          } else {
//            // not supported
//            throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
//                "Không hỗ trợ tính năng {0}", "Object expression: " + oexp);
//          }
//        } else if (exp instanceof IdExpression) {
//          // use the id value directly (without the need to lookup)
//          IdExpression iexp = (IdExpression) exp;
//          Oid refId = iexp.getVal();
//          if (refId.size() > 1) {
//            throw new NotImplementedException(NotImplementedException.Code.OBJECT_ID_NOT_SUPPORTED, 
//                "Không hỗ trợ mã đối tượng {0}", refId);
//          }
//          
//          DomainConstraint refIdAttrib = refId.getIdAttribute(0);
//          Object refIdVal = refId.getIdValue(0);
//        
//          var = getColName(iexp.getDomainClass(), iexp.getDomainAttribute());
//          return var + op.getName() + toSQLString(refIdAttrib.type(),refIdVal,false);
//        }
//        // TODO: add special translations for other cases here
//        else {
//          return var + op.getName() + " '" + v + "'";
//        }
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
//  
//  /**
//   * @effects 
//   *  converts exp to an equivalent SQL expression
//   */
//  private String toSQLExpression(Expression exp, boolean isSelectQuery) throws NotFoundException {
//    return toSQLExpression(exp, null, isSelectQuery);
////    final boolean withNulls = false;
////    
////    String var = exp.getVar();
////    Object val = exp.getVal();
////    Expression.Type type = exp.getType();
////    Expression.Op op = exp.getOperator();
////    
////    Object v;
////    if (val == null && !withNulls)
////      v = "";
////    else 
////      v = val;
////    
////    if (type.equals(Expression.Type.Data)) {
////      // convert values
////      if (val instanceof Number) {
////        // numeric expression
////        if (op == Expression.Op.MATCH)
////          return var + "=" + v;
////        else
////          return var + op.getName() + v;
////      } else {
////        if (op == Expression.Op.MATCH) {
////          // approximate matching
////          return var + " like '%" + v + "%'";
////        } else if (
////            (exp instanceof ObjectExpression) && 
////            ((ObjectExpression) exp).getDomainAttribute().type().isDomainType()) {
////          // val is a domain object -> need to get the domain id to use 
////          ObjectExpression oexp = (ObjectExpression) exp;
////          
////          Class cls = val.getClass();
////          Oid refId = schema.getDsm().lookUpObjectId(cls, val);
////          if (refId == null) {
////            // should not happen
////            throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NOT_FOUND, 
////                "Không tìm thấy mã đối tượng {0}<{1}>", cls.getSimpleName(), val);
////            
////          }
////          
////          if (refId.size() > 1) {
////            throw new NotImplementedException(NotImplementedException.Code.OBJECT_ID_NOT_SUPPORTED, 
////                "Không hỗ trợ mã đối tượng {0}", refId);
////          }
////          
////          DomainConstraint refIdAttrib = refId.getIdAttribute(0);
////          Object refIdVal = refId.getIdValue(0);
////          //String varName = getColName(oexp.getDomainClass(), oexp.getDomainAttribute());
////          var = getColName(oexp.getDomainClass(), oexp.getDomainAttribute());
////          
////          if (refIdVal instanceof Number)
////            return var + op.getName() + refIdVal;
////          else
////            return var + op.getName() + " '"+refIdVal+"'";            
////        }
////        // TODO: add special translations for other cases here
////        else {
////          return var + op.getName() + " '" + v + "'";
////        }
////      }
////    } else if (type.equals(Expression.Type.Nested)) {
////      // add a pair of brackets
////      // IMPORTANT: extra spaces around the operator 
////      return var + " " + op.getName() + " (" + v + ")";
////    } else { // other types
////      // keep the same
////      return var + op.getName() + v;
////    }  
//  }
//  
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
//  
//  @Override
//  /**
//   * determine DDL scripts to alter the table structure of c
//   * @effects
//   * <pre>
//   *  get mapping of f
//   *  if mapping not exists then create new column
//   *  else get changes of mapping as opposed to dc
//   *    generate sql for these changes & execute against the db
//   * </pre>
//   */
//  public void updateDataSourceSchema(Class c, DomainConstraint dc,
//      int fieldIndex, String oldFieldName, 
//      java.util.Map<DomainConstraint, Object> changedMappingAttribVals)
//      throws DataSourceException {
//    List<String> sqls = new ArrayList<>();
//
//    DSMBasic dsm = dom.getDsm();
//    String tableName = dsm.getDomainClassName(c);
//    String template = "ALTER TABLE " + tableName + " \n %s";
//    String sql = null;
//
//    if (changedMappingAttribVals == null) {
//      // add new column
//      sqls.addAll(genSQLAddColumn(tableName, dc));
//
//    } else {
//      // update mapping changes
//      DomainConstraint attrib;
//      Object val = null;
//      String fieldName = dc.name();
//
//      for (Entry<DomainConstraint, Object> e : changedMappingAttribVals.entrySet()) {
//        attrib = e.getKey();
//        val = e.getValue();
//
//        switch (attrib.name()) {
//        case "fieldName":
//          // get mapping for old fieldName
////          Mapping mapping = (Mapping) dom.lookUpObjectByID(Mapping.class,
////              tableName + "_" + fieldIndex);
//
//          sqls.add(String.format("RENAME COLUMN %s.%s TO %s", tableName,
//              //mapping.getFieldName(),
//              oldFieldName,
//              fieldName));
//          break;
//        case "serialisable":
//          if ((Boolean) val) {
//            sqls.addAll(genSQLAddColumn(tableName, dc));
//          } else {
//            // TODO: what if pkey, fkey
//            sql = "DROP COLUMN " + fieldName + " CASCADE";
//          }
//          break;
//        case "id":
//          if ((Boolean) val) {
//            sql = "ADD PRIMARY KEY (" + fieldName + ")";
//          } else {
//            sql = "DROP PRIMARY KEY";
//          }
//
//          break;
//        case "type":
//        case "maxLength":
//          sql = String.format("ALTER COLUMN %s SET DATA TYPE %s", fieldName,
//              getDataSourceType(dc) // type with length
//              );
//          break;
//        case "autoIncrement":
//          if ((Boolean) val) {
//            sql = String.format("GENERATED ALWAYS AS IDENTITY (start with 1, increment by 1)", fieldName,
//                getDataSourceType(dc));
//          } else {
//            sql = String.format("MODIFY %s %s", fieldName,
//                getDataSourceType(dc));
//          }
//          break;
//
//        case "isUnique":
//          if ((Boolean) val) {
//            sql = String.format("ADD CONSTRAINT unique_%s UNIQUE (%s)",
//                fieldName, fieldName);
//          } else {
//            sql = String.format("DROP UNIQUE unique_%s", fieldName);
//          }
//
//          break;
//        case "isOptional":
//          sql = String.format("ALTER COLUMN %s %s", fieldName,
//              (dc.optional()) ? "NULL" : "NOT NULL");
//          break;
//        case "defaultValue":
//          if (val.equals(MetaConstants.NullString)) {
//            sql = String.format("ALTER COLUMN %s DROP DEFAULT", fieldName);
//          } else {
//            sql = String.format("ALTER COLUMN %s SET DEFAULT '%s'\n",
//                fieldName, val);
//          }
//          break;
//        }
//
//        if (sql != null) {
//          sql = String.format(template, sql);
//          sqls.add(sql);
//        }
//      }
//    }
//
//    // execute queries update
//    if (conn != null) {
//      for (int i = 0; i < sqls.size(); i++) {
//        String tmp = sqls.get(i);
//
//        if (debug)
//          System.out.println("\n" + tmp);
//
//        try {
//          executeUpdate(tmp);
//        } catch (Exception e) {
//          System.err.println(e.getMessage());
//
//          System.out.println("Warning: all table data rows are deleted!");
//          // drop data rows
//          executeUpdate("DELETE FROM " + tableName);
//          // dom.retrieveObjectsWithAssociations(c);
//          // dom.deleteObjects(c, true);
//          
//          // try to run again
//          executeUpdate(tmp);
//        }
//      }
//    }
//  }
//
//  /**
//   * v 2.7.3 congnv returns sql statement for add column as specified by
//   * <tt>dc</tt>
//   * 
//   * @param dc
//   */
//  public List<String> genSQLAddColumn(String tableName, DomainConstraint dc) {
//    List<String> sqls = new ArrayList<>();
//
//    if (!dc.optional()) {
//      if (!dc.defaultValue().equals(MetaConstants.NullString)) {
//        sqls.add(String.format("ALTER TABLE %s ADD COLUMN %s %s NOT NULL %s DEFAULT '%s'",
//            tableName, dc.name(), getDataSourceType(dc), 
//            (dc.unique()) ? "UNIQUE": "", dc.defaultValue()));
//      } else {
//        // add null column
//        sqls.add(String.format("ALTER TABLE %s ADD COLUMN %s %s %s",
//            tableName, dc.name(), getDataSourceType(dc), 
//            (dc.unique()) ? "UNIQUE": ""));
//        
//        // set not null
//        sqls.add(String.format("ALTER TABLE %s ALTER COLUMN %s NOT NULL", tableName, dc.name()));
//      }
//    } else {
//      sqls.add(String.format("ALTER TABLE %s ADD COLUMN %s %s %s %s",
//          tableName, dc.name(), getDataSourceType(dc), 
//          dc.unique() ? "UNIQUE": "", 
//          !dc.defaultValue().equals(MetaConstants.NullString)? "DEFAULT '"+dc.defaultValue()+"'":""));
//    }
//
//    return sqls;
//  }
//
//  @Override
//  public String getDataSourceType(DomainConstraint dc) {
//    return javaToDBType(dc.type(), dc.length());
//  }
//  
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

  @Override
  protected Class<? extends DataSourceType> getDataSourceTypeClass() {
    return JavaDbSqlType.class;
  }

  @Override
  protected Class<? extends DataSourceFunction> getDataSourceFunctionClass() {
    return JavaDbSqlFunction.class;
  }
  
  @Override
  protected String getQuerySchemaExist(String schemaName) {
    /*v3.2: use query constant: 
    String sql = "select s.schemaname from sys.sysschemas s "
        + "where s.schemaname='%s'";

    sql = String.format(sql, schemaName.toUpperCase());
    */
    String sql = String.format(QUERY_SCHEMA_EXIST, schemaName.toUpperCase());
    
    return sql;
  }

  @Override
  protected String getQueryRelationNames(String schemaName) {
    /* v3.2: use constant:
    String sql = "select tablename from sys.systables t, sys.sysschemas s "
        + "where t.schemaid=s.schemaid and s.schemaname='%s'";

    sql = String.format(sql, schemaName);
    */
    
    String sql = String.format(QUERY_RELATION_NAMES, schemaName);
    
    return sql;
  }

  @Override
  protected String getQueryDropTable(String tableName) {
    String sql = String.format(QUERY_DROP_TABLE, tableName);
    
    return sql;
  }
  
  @Override
  public String getDefaultSchema() {
    // uppercase
    return "APP";
  }

  @Override
  public String getDataSourceSchema(String objSchema) {
    // upper case
    return objSchema.toUpperCase();
  }
}
