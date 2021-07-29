package jda.modules.dodm.osm;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A {@link DefaultOSMFile} that works with comma-separated files (CSV).
 *  
 * @author dmle
 *
 */
public class CsvOSM extends DefaultOSMFile {

  private static final boolean debug = Toolkit.getDebug(CsvOSM.class);
  /* v3.1: replaced by input stream to support reading from jar file
  private File file;
  */
  private String filePath;
  private InputStream fileIns;
  
  public CsvOSM(OsmConfig config, DOMBasic dom)
      throws DataSourceException {
    super(config, dom);
  }
  
  @Override
  public void connect() throws DataSourceException {
    OsmConfig config = getConfig();
    
    /*v3.1: support jar-file format
    file = new File(config.getDataSourceName());
    */
    filePath = config.getDataSourceName();
    
    if (ToolkitIO.isFileUrl(filePath)) {
      // file URL in a jar file: validate later
      // TODO: should we validate the URL here?
      // for now: because it comes from a validate resource look up method that is based on 
      // a class, we assumes that it is correct
    } else {
      // local file system
      try {
        fileIns = new FileInputStream(filePath);
      } catch (Exception e) {
        throw new DataSourceException(DataSourceException.Code.FAIL_TO_CONNECT, e, 
            new Object[] {filePath, e.getMessage()});
      }
    }
  }

  @Override
  public <T> List<T> readObjects(Class<T> c, int num) throws NotFoundException, DataSourceException {
    if (debug)
      System.out.printf("%s: Importing objects%n", CsvOSM.class.getSimpleName());

    //TODO: store encoding in configuration
    final String charSetName = "UTF-8";

    /*v3.1: support two cases
    Collection<String> lines = readTextFile();
    */
    Collection<String> lines;
    try {
      if (fileIns != null) {
        // local file system
        lines = ToolkitIO.readTextFileWithEncoding(fileIns, charSetName);
      } else {
        // file URL in a jar file
        lines = ToolkitIO.readJarTextFileEntry(filePath, charSetName);
      }
    } catch (Exception e) {
      throw new DataSourceException(DataSourceException.Code.FAIL_TO_READ_DATA_SOURCE, e, 
          new Object[] {filePath, e.getMessage()});
    }
    
    // the objects that will be read
    List<T> objects = null;
 
    if (lines != null) {
      DOMBasic dom = getDom();
      DSMBasic schema = dom.getDsm();
      
      objects = new ArrayList<>();
      T o;
      
      final Class<DAttr> DC = schema.DC;
      final String cname = schema.getDomainClassName(c);

      boolean header=true;
      DAttr[] attribs = null;
      //DomainConstraint attrib;
      //Class attribDataType;
      String[] vals;
      
      // read the domain attributes of the class
      // we will use these attributes to parse the object values
      Map<Field,DAttr> fields = schema.getSerialisableDomainAttributes(c);

      // determine if c has a reflexive association (reflexive relationship)
      // if so then we must take care of the order in which we read objects from database
      // (see below)
      /* v3.2: use reflexive constraints
      boolean reflexive = schema.isReflexive(c, fields);
      */
      List<DAttr> reflexDcs = schema.getReflexiveDomainConstraints(c, fields, true);
      boolean reflexive = false;
      if (reflexDcs != null)
        reflexive = true;
      
      // get the id columns and check if one of them is auto-generated
      Field f = null;
      DAttr dc;
      DAttr[] refDcs;
      Object[] refValues;
      Type type;
      Class domainType;
      //int recCount = 0;

      List values;
      //Object o = null;
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
      
      int recCount = 0;
      DAttr attrib, linkedAttrib;
      java.util.Map<Object,DAttr> linkLater = new HashMap<Object,DAttr>();
      
      REC: for (String ps : lines) {
        vals = ps.split(",");
        if (header) {// header line
          header=false;  
          attribs = new DAttr[vals.length];
          for (int i = 0; i < attribs.length; i++) {
            attribs[i] = schema.getDomainConstraint(c, vals[i]);
          }
        } else {  // object record line
//          try {
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
          /*v3.0: use attributes
          for (int i = 0; i < fields.size(); i++) {
            f = (Field) fields.get(i);
            */
          for (int i = 0; i < attribs.length; i++) {
            attrib = attribs[i];
            f = schema.getDomainAttribute(c, attrib);
            dc = attrib; //v5.0: f.getAnnotation(DC);
            type = dc.type();
            //attribDataType = f.getType();

            v = vals[i]; //sqlToJava(dc, rs, i+1);
            
            if (v.equals(CommonConstants.NullValue)) {
              v = null;
            }
            
            // we only use id and non-autogenerated attribute to create object
            //if (dc.id()) { //v2.7.3: bug -> !dc.auto()) {
              if (!type.isDomainType()) {
                // read the sql value 
                v = dom.validateDomainValue(
                    c,  // v2.7.4   
                    dc, v);
              } else {
                  // domain type attribute
                  // query the object whose id is the value of this field
                  // and use that for the object
                  domainType = f.getType();
                  refDcs = schema.getIDDomainConstraints(domainType).toArray(new DAttr[0]);

                  refValues = new Object[refDcs.length];
                  v = dom.validateDomainRefValue(dc, refDcs[0], v);
                  if (v != null) {  
                    // ref value is specified, look it up
                    refValues[0] = v;
                    if (refDcs.length > 1) {
                      // if it is a compound key then we must
                      // read the subsequent values in this record to complete
                      // the id
                      int j = 1;
                      for (i = i + 1; i < i + refDcs.length; i++) {
                        /* v5.0: Field f1 = fields.get(i);
                        DAttr dc1 = f1.getAnnotation(DC);
                        */
                        refValues[j] = dom.validateDomainRefValue(dc, refDcs[j], vals[i]); //sqlToJava(refDcs[j], rs, i); 
                        j++;
                      }
                    }
                    
                    /**
                     * if reflexive=true and this field is the FK attribute
                     *  look up for object in objects
                     * else
                     *  look up for object in schema
                     */
                    if (reflexive && // v3.2: domainType == c
                          reflexDcs.contains(dc)
                        ) {
                      v = dom.lookUpObjectByID(objects, refValues);
                      if (v == null) { // referenced object not yet processed
                        //  record this value as normal to be processed later
                        // reset value to keep, also set delayed to true
                        if (!delayed) delayed = true;
                        v = refValues;
                      }        
                    } else {
                      // use the id to look up the object directly
//                      v = dom.lookUpObjectByID(domainType, refValues); 
                      v = retrieveAssociatedObject(domainType, refDcs, refValues);
                      if (v == null) { // referenced object not found
                        // TODO: skip this record?
                        throw new NotFoundException(NotFoundException.Code.REFERENCE_OBJECT_NOT_FOUND, 
                            new Object[] {domainType.getSimpleName(), refDcs[0].name(), refValues[0]+"", cname, values, f.getName()});
                      }
                      
                      // v2.6.4.b: if domainType is determined by c in a 1:1 association via f
                      // then record v for update the association link later
                      linkedAttrib = dom.getDsm().getLinkedAttribute(c, dc);
                      if (linkedAttrib != null && 
                          schema.isDeterminedByAssociate(domainType, linkedAttrib)) {
                        linkLater.put(v, linkedAttrib);
                      }
                    }
                  } else {
                    // ref value is not specified; if it is required then data integrity
                    // is violated, print error
                    if (!dc.optional()) {
                      // TODO: skip this record?
                      throw new NotFoundException(NotFoundException.Code.REFERENCE_OBJECT_NOT_FOUND, 
                            new Object[] {domainType.getSimpleName(), refDcs[0].name(), refValues[0]+"", cname, values, f.getName()});

                    }
                  }
                } // end domain type
              
                values.add(v);
              //} // end if
            } // end for(attribs) loop

            /* 
             * if the current record is not a member of the delayed queue (i.e delayed = false) 
             *  create object
             * else 
             *  add values to delayed queue  
             **/
            if (!delayed) {
              try {
                o = schema.newInstance(c, values.toArray());
                objects.add(o);
                
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
                e.printStackTrace();
              }
      
              if (num > 0) {
                recCount++;
                if (recCount >= num)
                  break;
              }
            } else {  // process this record later
              delayedStack.push(values);
            }
          }
        } // end REC
      
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
            /*v3.0
            for (int i = 0; i < fields.size(); i++) {
              f = fields.get(i);
              */
            for (int i = 0; i < attribs.length; i++) {
              attrib = attribs[i];
              f = schema.getDomainAttribute(c, attrib);
              dc = attrib; //v5.0: f.getAnnotation(DC);
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
                    throw new NotFoundException(NotFoundException.Code.REFERENCE_REFLEXIVE_OBJECT_NOT_FOUND, 
                        new Object[] {domainType.getSimpleName(), refValues[0]+"", cname, f.getName()});
                  }
                  // put v back into vals
                  values.set(i, v);
                }
              } // end domainType attribute
            } // end field pass
            
            // now create this object
            try {
              o = schema.newInstance(c, values.toArray());
              objects.add(o);
              
              if (!linkLater.isEmpty()) {
                Object lo;
                for (Entry<Object,DAttr> e : linkLater.entrySet()) {
                  lo = e.getKey();
                  linkedAttrib = e.getValue();
                  dom.setAttributeValue(lo, linkedAttrib.name(), o);
                }
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
  
            if (num > 0) {
              recCount++;
              if (recCount >= num)
                break STACK;
            }        
          } // end STACK
        } // end case: delayStack
      } else {
        throw new NotPossibleException(NotPossibleException.Code.DATA_SOURCE_IS_EMPTY);
      }
    
      return objects;
  }

  /**
   * @effects 
   *  retrieve and return domain object of <tt>c</tt>, that values of whose id attributes (<tt>ids</tt>)
   *  are <tt>idVals</tt> 
   */
  @SuppressWarnings("unchecked")
  private Object retrieveAssociatedObject(
      Class c,
      DAttr[] ids, Object[] idVals) throws NotFoundException, DataSourceException {
    // first look up the object id, load if not found
    
    /* v3.2: FIX BUG: when c is an enum type (its objects have already been registered then 
         this code does not work b/c datasource read will not work
        - to use lookUpObjectById first
        
    Oid roid = dom.retrieveObjectId(c, ids, idVals); 
    Object v = dom.lookUpObject(c, roid);
    */
    
    Object v = dom.lookUpObjectByID(c, idVals); 
    if (v == null) { 
      // referenced object not yet read from data source -> try to load it
      Oid roid = dom.retrieveObjectId(c, ids, idVals);

      // v is not an object of c (try the sub-types of c...)
      if (debug)
        System.out.printf("  loading linked object %s<%s>%n",c, roid);
      
      // if domainType is a super-type then the id can belong to 
      // one of the sub-types or the super-type, so we need to 
      // try loading from each of them until found
      // TODO: is there a faster way for handling this situation?
      Class[] subTypes = dom.getDsm().getSubClasses(c);
      if (subTypes != null) {
        // has sub-types
        for (Class subType : subTypes) {
          try {
            v = dom.loadObject(subType, roid, null);
            if (v != null) 
              // found -> break
              break;
          } catch (NotFoundException ex) {
            // ignore 
          }
        }
        
        if (v == null) {
          // not found in any sub-types -> try the super-type
          v = dom.loadObject(c, roid, null);
        }
      } else {  
        // no subtypes
        v = dom.loadObject(c, roid, null);
      }
      
      if (debug)
        System.out.printf("  --> %s%n",v);
    } else {
      if (debug)
        System.out.printf("  associated type: %s%n  --> value: %s%n",c, v);
    }
    

    
    return v;
  }

//  private Object convertTextValue(DomainConstraint attrib, Class attribDataType, String val) 
//      throws ParseException, NotFoundException, DataSourceException {
//    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
//    DOM dom = getDom();
//
//    Object result = null;
//    DomainConstraint.Type attribDomainType = attrib.type();
//    //String attribName = attrib.name();
//    
//    if (attribDomainType.isDate()) {
//      //TODO: support date conversion (see JFormattedDataField)
//      result = dateFormat.parse(val);
//    } else if (attribDomainType.isDomainType()) {
//      // look up the referenced obj
//      result = dom.lookUpObjectByID(attribDataType, val);
//      
//      if (result == null)
//        throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, attribDataType.getSimpleName(), "");
//    } else {
//      result = val;
//    }
//    
//    return result;
//  }

// v3.1: replaced by ToolkitIO.readTextFile  
//  /**
//   * @effects 
//   *  if file named <tt>fileName</tt> contains non-empty text and if succeeds in 
//   *  reading the file
//   *    return a Collection of the non-empty lines
//   *  else
//   *    return null
//   */
//  public Collection<String> readTextFile() {
//    String line;
//    Collection<String> lines = new ArrayList<>();
//    try {
//      //v3.1: use fileIns
//      // InputStream ins = new FileInputStream(file);
//      
//      BufferedReader reader = new BufferedReader(new InputStreamReader(fileIns));
//      
//      while ((line = reader.readLine()) != null) {
//        if (line.length() > 0)  // ignore empty lines
//          lines.add(line);
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    
//    return (lines.isEmpty()) ? null : lines;
//  }
}
