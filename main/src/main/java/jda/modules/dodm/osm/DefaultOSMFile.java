package jda.modules.dodm.osm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.osm.relational.DataSourceType;
import jda.modules.dodm.osm.relational.sql.function.DataSourceFunction;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.FlexiQuery;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A sub-type of {@link OSM} that is used for file-based data source. 
 *  
 * @author dmle
 */
public abstract class DefaultOSMFile extends OSM {

  public DefaultOSMFile(OsmConfig config, DOMBasic dom) {
    super(config, dom);
  }

  @Override
  protected Class<? extends DataSourceType> getDataSourceTypeClass() {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  protected Class<? extends DataSourceFunction> getDataSourceFunctionClass() {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }
  
  @Override
  public boolean isConnected() {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");

  }

  @Override
  public void disconnect() {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public void deleteObjects(Class domainClass) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public void createSchemaFromFile(String filePath) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public void createObjectsFromFile(String filePath) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public void deleteObjects(Class c, Query<ObjectExpression> searchQuery)
      throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public void updateObjects(Class you, Query<ObjectExpression> searchQuery,
      Query<ObjectExpression> updateQuery) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public List<String> readDataSourceConstraint(Class c) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }
  
  @Override
  public boolean exists(String schema, String storeName) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");

  }

  @Override
  public boolean existsSchema(String schema) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");

  }

  @Override
  public void createSchema(String name) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
    
  }

  @Override
  public void deleteDomainSchema(String schemaName) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
    
  }

  @Override
  public void createConstraints(Map<String, List<String>> dataSourceConstraints)
      throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
    
  }

  @Override
  public void dropDataSourceConstraint(Class c, String name)
      throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
    
  }

  @Override
  public void createClassStoreWithoutConstraints(Class domainClass,
      Map<String, List<String>> storeConstraints) throws DataSourceException,
      NotPossibleException, NotFoundException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
    
  }

  @Override
  public void createClassStore(Class domainClass) throws DataSourceException,
      NotPossibleException, NotFoundException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
    
  }

  @Override
  public void dropClassStore(Class domainClass) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
    
  }

  @Override
  public Map<Tuple, Tuple2<Object, Object>> readValueRange(Class c,
      DAttr attrib, DAttr[] derivedAttributes)
      throws DataSourceException, NotFoundException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");

  }

  @Override
  public Tuple2<Object, Object> readValueRange(Class c, DAttr attrib)
      throws DataSourceException, NotFoundException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public Tuple2<Tuple2<Class, Object>, Tuple2<Class, Object>> readIdValueRange(
      Class cls, DAttr idAttrib) throws DataSourceException, NotFoundException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");    
  }
      
  @Override
  public void putObject(Class c, Object o) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");

  }

  @Override
  public void updateObject(Object o, Class c) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");

  }

  @Override
  public <T> T readObject(Class<T> c, Oid oid) throws NotPossibleException,
      DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }


  @Override
  public <T> T readAssociatedObject(Class<T> c, Oid oid, Class fromAssocCls,
      Oid fromAssocOid, 
      DAttr fromLinkedAttrib) throws NotPossibleException,
      DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }
  
  @Override
  public void deleteObject(Class c, Object o) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");

  }

  @Override
  public void transformObjectToASubtype(Class c, Object o, Class subType) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public void transformObjectToSupertype(Class c, Object o, Class supType) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public Collection<Oid> readLinkedObjectIds(Class c,
      Tuple2<DAttr, DAssoc> assoc, Object linkedObj, Expression...exps)
      throws DataSourceException, NotPossibleException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");

  }

  @Override
  public Collection<Oid> readLinkedObjectIds(Class c,
      Tuple2<DAttr, DAssoc> assoc, Object linkedObj, Query query)
      throws NotPossibleException, DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }
  
  @Override
  public Collection<Oid> readObjectIds(Class c, String aggregateFunc,
      Query query, final boolean withSubTypes, Class...orderByClass) throws NotPossibleException,
      DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public boolean existObject(Class c, Query q) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public Map<Oid, Object> readAttributeValuesWithOids(Class c,
      DAttr attrib) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");

  }

  @Override
  public Collection readAttributeValues(Class c, DAttr attrib) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public Collection readAttributeValues(Class c, DAttr attrib, FlexiQuery query) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public Collection readAttributeValues(Class c, DAttr attrib, final boolean orderByKey) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }
  
  @Override
  public Map<DAttr, Collection> readAttributeValueTuples(Class c,
      DAttr[] attributes, final boolean orderByKey) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }
  
  @Override
  public Map<Object, List> readAttributeValueTuplesWithOids(Class c,
      DAttr[] attributes) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public Object readAttributeValue(Class c, Oid oid, DAttr attrib) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  
  @Override
  public Map<DAttr, Object> readAttributeValues(Class c, Oid oid,
      Collection<DAttr> attribs) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public int readObjectCount(Class c) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public Oid readIdFirstBefore(Class c, DAttr idAttrib, Oid currId)
      throws DataSourceException, NotPossibleException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public Oid readIdFirstAfter(Class c, DAttr idAttrib, Oid currId)
      throws DataSourceException, NotPossibleException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public int readAssociationLinkCount(Class cls, DAttr attrib,
      Object linkedObj, Oid linkedObjOid) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");
  }

  @Override
  public void print(Class c) throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");    
  }

  @Override
  public void printDataSourceSchema(String schemaName) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");    
  }

  
  @Override
  public void setDebugOn(boolean tf) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");  
  }

  @Override
  public void updateDataSourceSchema(Class c, DAttr dc,
      int fieldIndex, String oldFieldName, Map<DAttr, Object> changedAttribVals)
      throws DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), "");     
  }

  @Override
  public String getDataSourceType(DAttr dc) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), ""); 
  }
  
  @Override
  public <T> T reloadObject(Class<T> c, Oid oid) throws NotPossibleException,
      DataSourceException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), ""); 
  }

  @Override
  public String getDefaultSchema() {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), ""); 
  }

  @Override
  public String getDataSourceSchema(String objSchema) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DefaultOSMFile.class.getSimpleName(), ""); 
  }
  
  
}
