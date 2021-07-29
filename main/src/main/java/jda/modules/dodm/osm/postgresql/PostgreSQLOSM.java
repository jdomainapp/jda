package jda.modules.dodm.osm.postgresql;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.osm.relational.DataSourceType;
import jda.modules.dodm.osm.relational.RelationalOSMBasic;
import jda.modules.dodm.osm.relational.sql.function.DataSourceFunction;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;

public class PostgreSQLOSM extends RelationalOSMBasic {

  private static final String QUERY_SCHEMA_EXIST = 
      "select nspname from pg_namespace where nspname = '%s'";
  
  private static final String QUERY_RELATION_NAMES = 
      "select relname from pg_class where relnamespace = "
          + "(select oid from pg_namespace where nspname='%s')";
  
  private static final String QUERY_DROP_TABLE = "drop table %s cascade";

  public PostgreSQLOSM(OsmConfig config, DOMBasic dom)
      throws DataSourceException {
    super(config, dom);
  }

  @Override
  protected Class<? extends DataSourceType> getDataSourceTypeClass() {
    return PostgreSqlType.class;
  }

  @Override
  protected Class<? extends DataSourceFunction> getDataSourceFunctionClass() {
    return PostgreSqlFunction.class;
  }
  
  @Override
  protected String getQuerySchemaExist(String schemaName) {
    /*v3.2: use query constant
    String sql = "select nspname from pg_namespace where nspname = '%s'";
    
    sql = String.format(sql, schemaName);
    */
    String sql = String.format(QUERY_SCHEMA_EXIST, schemaName);
    
    return sql;
  }

  @Override
  protected String getQueryRelationNames(String schemaName) {
    /*v3.2: use query constant
    String sql = "select relname from pg_class where relnamespace = "
        + "(select oid from pg_namespace where nspname='%s')";

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
    return "public";
  }

  @Override
  public String getDataSourceSchema(String objSchema) {
    // same
    return objSchema;
  }

}
