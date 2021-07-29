package jda.modules.dodm.osm.javadb;

import java.sql.Types;

import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.osm.relational.DataSourceType;

/**
 * @overview 
 *  Represents the {@link DataSourceType} for Java DB.
 *   
 * @author dmle
 */
public enum JavaDbSqlType implements DataSourceType {
  VarChar("varchar(%s)", Type.String, Types.VARCHAR), //
  
  VarCharMasked("varchar(%s)", Type.StringMasked, Types.VARCHAR), //

  VarCharBool("varchar(%s)", Type.Boolean, Types.VARCHAR), //

  Char("char(%s)", Type.Char, Types.CHAR), //

  SmallInt("smallint", Type.Short, Types.SMALLINT), //
  
  Int("Int", Type.Integer, Types.INTEGER), //
  
  Long("BigInt", Type.Long, Types.BIGINT), //

  BigInt("BigInt", Type.BigInteger, Types.BIGINT), //

  Real("Real", Type.Float, Types.REAL), //
  
  Double("Double", Type.Double, Types.DOUBLE), //
  
  Date("Date", Type.Date, Types.DATE), //
  
  Byte("varchar(%s) for bit data", Type.Byte, Types.VARBINARY), //
  
  ByteArraySmall("varchar(%s) for bit data", Type.ByteArraySmall, Types.VARBINARY), //

  ByteArrayLarge("blob(%s)", Type.ByteArrayLarge, Types.BLOB), //

  ByteArrayImage("blob(%s)", Type.Image, Types.BLOB), //
  
  /**
  * files
  * @version 3.3 
  */
  FileType("blob(%s)", Type.File, Types.BLOB),
  ;
  
  // the Java type
  private Type javaType;
  // SQL type name
  private String name;

  // the standard SQL Types value
  private int intValue;
  
  private JavaDbSqlType(String n, Type mappedToJavaType, int intValue) {
    javaType = mappedToJavaType;
    name = n;
    this.intValue = intValue;
  }
  
  @Override
  public Type getMapping() {
    return javaType;
  }

  // v3.0: not used
//  /**
//   * @effects 
//   *  return the <tt>SqlType</tt> that is equivalent to <tt>javaType</tt>
//   */
//  public static JavaDbSqlType getMapping(Type javaType) {
//    JavaDbSqlType[] types = JavaDbSqlType.values();
//    for (JavaDbSqlType type : types) {
//      if (type.getMapping() == javaType) {
//        return type;
//      }
//    }
//    
//    // no match
//    return null;
//  }
  
  @Override
  public String toString(Object...args) {
    return String.format(name, args);
  }

  @Override
  public int getIntValue() {
    return intValue;
  }

  // NOT USED????
  public String getName() { return name; }

  /* (non-Javadoc)
   * @see domainapp.basics.core.dodm.osm.relational.DataSourceType#isSizableFor(int)
   */
  @Override
  public boolean isSizableFor(int size) {
    //TODO: implement this when needed
    return true;
  }
}
