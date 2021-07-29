package jda.modules.dodm.osm.postgresql;

import java.sql.Types;

import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.osm.relational.DataSourceType;

/**
 * @overview 
 *  Represents the {@link DataSourceType} for PostgreSQL Db.
 *   
 * @author dmle
 */
public enum PostgreSqlType implements DataSourceType {
  VarChar("varchar(%s)", Type.String, Types.VARCHAR), //
  
  VarCharMasked("varchar(%s)", Type.StringMasked, Types.VARCHAR), //

  VarCharBool("varchar(%s)", Type.Boolean, Types.VARCHAR), //

  Char("char(%s)", Type.Char, Types.CHAR), //

  SmallInt("smallint", Type.Short, Types.SMALLINT), //
  
  Int("Int", Type.Integer, Types.INTEGER), //
  
  Long("BigInt", Type.Long, Types.BIGINT), //

  BigInt("BigInt", Type.BigInteger, Types.BIGINT), //

  Real("Real", Type.Float, Types.REAL), //
  
  // diff
  Double("Double precision", Type.Double, Types.DOUBLE), //
  
  Date("Date", Type.Date, Types.DATE), //
  
  // diff
  Byte("bit(%s)", Type.Byte, Types.VARBINARY), //
  
  ByteArraySmall("bit varying(%s) for bit data", Type.ByteArraySmall, Types.VARBINARY), //

  //diff
  ByteArrayLarge("bytea", Type.ByteArrayLarge, 
      //Types.BLOB
      Types.BINARY
      ), //

  //diff
  ByteArrayImage("bytea", Type.Image,
      //Types.BLOB: causes an error when setting column value to null using setNull
      Types.BINARY
      ),

  /**
  * files
  * @version 3.2 
  */
  FileType("bytea", Type.File, Types.BINARY),

//  /**
//   * files whose max size = 1MB
//   * @version 3.2 
//   */
//  File1MB("bytea", Type.File, Types.BINARY, null, 10^6),
//  
//  /**
//   * files whose max size = 2MB
//   * @version 3.2 
//   */
//  File2MB("bytea", Type.File, Types.BINARY, null, 2*10^6),
//      
//  /**
//   * files whose max size = 10MB
//   * @version 3.2 
//   */
//  File10MB("bytea", Type.File, Types.BINARY, null, 10^10),
//  
//  /**
//   * files whose max size = 20MB
//   * @version 3.2 
//   */
//  FileGeq20MB("bytea", Type.File, Types.BINARY, 2*10^10, null),
  ;
  
  // the Java type
  private Type javaType;
  // SQL type name
  private String name;

  // the standard SQL Types value
  private int intValue;
  
  // v3.2:
  private Integer minSize;
  
  private Integer maxSize;
  
  private PostgreSqlType(String n, Type mappedToJavaType, int intValue) {
    this(n, mappedToJavaType, intValue, null, null);
  }
  
  // v3.2
  private PostgreSqlType(String n, Type mappedToJavaType, int intValue, Integer minSize, Integer maxSize) {
    javaType = mappedToJavaType;
    name = n;
    this.intValue = intValue;
    
    this.minSize = minSize;
    this.maxSize = maxSize;
  }
  
  @Override
  public Type getMapping() {
    return javaType;
  }

  @Override
  public String toString(Object...args) {
    return String.format(name, args);
  }

  @Override
  public int getIntValue() {
    return intValue;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.core.dodm.osm.relational.DataSourceType#isSizableFor(int)
   */
  @Override
  public boolean isSizableFor(int size) {
    boolean minCheck = (minSize == null || size >= minSize);
    boolean maxCheck = (maxSize == null || size <= maxSize);
    
    return minCheck && maxCheck;
  }
}
