package jda.test.app.courseman.extended.units;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.extended.SClass;
import jda.test.model.extended.Student;


public class DeleteAnSClass extends CourseManExtendedTester {

  // delete SClass and update Student objects linked to it in memory
  @Test
  public void deleteSClassWithSomeStudents() throws Exception {
    //TestDBExtended me = (TestDBExtended) instance;
    System.out.println("deleteSClassWithSomeStudents()");
    
    DODMBasic schema = instance.getDODM();
    
    Class c = SClass.class;
    System.out.printf("Domain class: %s%n", c.getSimpleName());

    String name = "class #2";//"class #3";
    
    Tuple2<Oid,Object> t = loadSClass(name);
    
    if (t != null) {
      // delete SClass object
      SClass sclass = (SClass) t.getSecond();
      Oid oid = t.getFirst();

      System.out.printf("  SClass object: %s -> %s%n", oid, sclass);

      // load students whose class do NOT belong to the specified class
      // and update them to belong to the class
      Class c2 = Student.class;
      DAttr attrib = schema.getDsm().getDomainConstraint(c2, "sclass");
      Query q = new Query(
          new ObjectExpression(c2, attrib, Op.NOTEQ, sclass)
          );
      
      loadObjectsWithOid(c2, q);
      
      Collection<Tuple2<Oid,Object>> students = getRandomObjects(c2, 2);
      
      System.out.printf("  Loaded %d %s objects NOT belonging to the class %n", students.size(), c2.getSimpleName());

      // update students to belong to the class
      Student s;
      for (Tuple2<Oid,Object> ts : students) {
        s = (Student) ts.getSecond();
        System.out.printf("      Student object: %s -> %s%n          sclass: %s%n", ts.getFirst(), s, s.getSclass());
        
        s.setSclass(sclass);
        System.out.printf("         new sclass: %s%n", sclass);
        
        // save to db
        schema.getDom().updateObject(s, null);
        System.out.printf("         updated into data source: %s%n", s);
        
        sclass.addStudent(s);
      }
      
      System.out.printf("  Deleting %s %n", sclass);

      deleteObjectStrict(c, sclass, oid);
      
      System.out.printf("       -> deleted%n");
    } else {
      System.out.printf("  No objects found%n");
    }
  }  
  
  @Ignore
  @Test
  public void deleteSClassFromDB() throws Exception {
    System.out.println("deleteSClassFromDB()");
    
    CourseManExtendedTester me = (CourseManExtendedTester) instance;
    
    Class c = SClass.class;

    System.out.printf("Domain class: %s%n", c.getSimpleName());

    // load SClass object
    String name = "class #2";
    Tuple2<Oid,Object> t = loadSClass(name);
    
    if (t != null) {
      // delete SClass object
      Object o = t.getSecond();
      Oid oid = t.getFirst();

      System.out.printf("  Object: %s -> %s%n", oid, o);

      deleteObjectStrict(c, o, oid);
      
      System.out.printf("  Deleted%n");
    } else {
      System.out.printf("  No objects found%n");
    }
  }  

  
  // load an SClass matching the specified name
  private Tuple2<Oid,Object> loadSClass(String className) throws DataSourceException {
    CourseManExtendedTester me = (CourseManExtendedTester) instance;
    
    Class c = SClass.class;

    System.out.printf("Domain class: %s%n", c.getSimpleName());

    // load SClass object
    Query query = new Query(
        new Expression("name", Op.MATCH, className)
        );
    
    System.out.printf("Query: %s%n", query);
    
    Tuple2<Oid,Object> t = loadObjectWithOid(c, query);
    
    return t;
  }
  
  public void deleteObjectStrict(Class c, Object o, Oid oid) throws NotFoundException, NotPossibleException, DataSourceException {
    System.out.printf("deleteObjectStrict(%s, %s, %s)%n", c.getSimpleName(), o, oid);

    // update all associated objects first
    updateAssociatesOnDelete(c, o);
    
    // now delete 
    deleteObject(c, o, oid);  
  }
  
  /**
   * @effects 
   *  update all objects associated to deletedObj to reflect the fact that 
   *  deletedObj is removed from the system.
   */
  private void updateAssociatesOnDelete(Class c, Object t) throws DataSourceException, 
    NotFoundException, NotPossibleException {
    System.out.printf("updateAssociatesOnDelete(%s, %s)%n", c.getSimpleName(), t);

    DODMBasic schema = instance.getDODM();
    
    /*
     * let t = deletedObj
     * let Table(x) be the database table of the domain class x
     * card(a,n) be the cardinality constraint of a domain class a in an association n 
     * 
     * for each association n(c:a,d:b) of an attribute c:a that associates to another attribute d:b (d may eq c)
     *   let O(d) be a sub-set of Objects(d) s.t. for all o in O(d). n(t,o)
     *   for each o in O(d)
     *    if d depends-on c
     *      remove o [without updating Table(d)]
     *    else
     *      if card(c,n)=1
     *        set o.b = null [without updating Table(d)]
     *      else if card(c,n)=M
     *        remove t from o.b [without updating Table(d)]
     *    
     *    let S(d) be a sub-set of records in Table(d) s.t. for all r in S(d). n(t,r)
     *    for each r in S(d)
     *      if d depends-on c
     *        remove r
     *      else
     *        if card(c,n)=1
     *          set r.b = null  
     */
    DSMBasic dsm = schema.getDsm();
    Map<DAttr,DAssoc> associations = dsm.getAssociations(c);

    if (associations == null)
      return;
    
    DAttr a, b;
    Class d;
    Tuple2<DAttr,DAssoc> yourAssocTuple;
    DAssoc n, m;
    boolean youDependsOnMe;
    Object Od;

    for (Entry<DAttr,DAssoc> e: associations.entrySet()) {
      a = e.getKey();
      n = e.getValue();
      
      d = n.associate().type();
      // get the other end of the association 
      yourAssocTuple = dsm.getTargetAssociation(n); 
      b = yourAssocTuple.getFirst();
      m = yourAssocTuple.getSecond();
      // TODO: assume no Many-To-Many associations
      youDependsOnMe = dsm.isDependentOn(d, m); 

      // if associate is a collection-type then find in the object-pool 
      // all the objects that are not yet added in associate (because they have not 
      // be browsed to)
      if (a.type().isCollection()) {
        /* find all those matching myObj in the object pool of yourCls
         * Note: NOT necessary to look up for these in the data source because
         * they are not of interest to the caller of this method
         */
        Query q = new Query();
        q.add(new ObjectExpression(d, b, Op.EQ, t));
        Od = schema.getDom().getObjects(d, q);
      } else {
        Od = dsm.getAttributeValue(t, a.name());
      }

      if (Od != null) {
        // TODO: process Od
        updateAssociatesOnDelete(t, c, n.ascType(), n.endType(), 
        a, 
        Od, d, 
        b,
        youDependsOnMe);
      }
      
      // TODO: process Sd
      updateDataSourceOnDelete(t, c, n, d, b, youDependsOnMe);
    }
  }
  
  private void updateDataSourceOnDelete(Object t, Class me, DAssoc n,
      Class you, DAttr b, boolean youDependsOnMe) throws DataSourceException {
    System.out.printf(
        "updateDataSourceOnDelete(%s, %n %s, %n %s, %n %s, %n %s, %n %s)%n",
        t,
        me,
        n, 
        you.getSimpleName(),
        b.name(), 
        youDependsOnMe);
    
    DODMBasic schema = instance.getDODM();

    boolean cardOne = (n.ascType() == AssocType.One2One || 
        n.ascType() == AssocType.One2Many);
    
    //OSM dbt = schema.getDom().getOsm();
    DOMBasic dbt = schema.getDom();
    
    // the search expression: select all records in you that refers to me(t)
    ObjectExpression searchExp = new ObjectExpression(you, b, Op.EQ, t);
    Query<ObjectExpression> searchQuery = new Query<ObjectExpression>(searchExp);
    
    System.out.printf("searchQuery: %s%n", searchQuery);
    
    if (youDependsOnMe) {
      // remove all records in you that refers to t
      dbt.deleteObjects(you, searchQuery);
    } else {
      // update all records in you that refers to t by setting the values of the concerned FK col b to null
      ObjectExpression updateExp = new ObjectExpression(you, b, Op.EQ, null); 
      Query<ObjectExpression> updateQuery = new Query<ObjectExpression>(updateExp);

      System.out.printf("updateQuery: %s%n", updateQuery);
      
      dbt.updateObjects(you, searchQuery, updateQuery);
    }
  }

  private void updateAssociatesOnDelete(Object me, Class c, 
      AssocType assocType, AssocEndType myEndType, 
      DAttr myAttrib,   
      Object you, Class d, 
      DAttr yourAttrib, 
      boolean youDependsOnMe) throws DataSourceException {
    
    System.out.printf("updateAssociatesOnDelete(%s, %n %s, %n %s, %n %s, %n %s, %n %s, %n %b)%n", 
        me, 
        c.getSimpleName(), 
        assocType, 
        myEndType, 
        you,
        d.getSimpleName(),
        youDependsOnMe);

    if (!youDependsOnMe) {
      // associate does not depend on deletedObj
      // update associates depending on the association type
      if (assocType == AssocType.One2One) {
        // set linked attribute value of associate to null
        // without updating the object in the data source
        updateOneToOneAssociateOnDelete(you, yourAttrib, null, false);
      } else if (assocType == AssocType.One2Many) {
        if (myEndType == AssocEndType.One) {
          // one-to-many
          // set linked attribute of each element of associateObj to null
          // without updating the object in the data source
          Collection col = (Collection) you;
          for (Object obj : col) {
            updateOneToOneAssociateOnDelete(obj, yourAttrib, null, false);
          }
        } else {
          // many-to-one: remove deletedObj from linked attribute value of
          // associate
          // topDctl.updateManyAssociateOnDelete(associateObj, cls,
          // deletedObj);
          Oid associateOid = schema.getDom().lookUpObjectId(d, you);
          // without updating the data source
          updateManyAssociateOnDelete(you, associateOid, myAttrib, me, false);
        }
      }
    } else {
      // associate depends on deletedObj -> delete
      if (assocType == AssocType.One2Many) {
        // far-end is always one
        // delete all elements of associatedObj

        // to be sure...
        if (!(you instanceof List)) {
          throw new NotPossibleException(
              NotPossibleException.Code.INVALID_TARGET_ASSOCIATE_OBJECT_TYPE,
              "Kiểu dữ liệu liên quan {0} không đúng, cần kiểu {1}",
              you, "List");
        }

        List col = (List) you;
        // TODO: not thread-safe because col may be modified by invocation
        // rootDctl.updateManyAssociateOnDelete above
        // temporary solution: use a backward for loop to overcome this
        Object obj;
        Oid oid;
        for (int i = col.size() - 1; i >= 0; i--) {
          obj = col.get(i);
          // recursive call
          oid = schema.getDom().lookUpObjectId(obj.getClass(), obj);
          deleteObjectStrict(obj.getClass(), obj, oid);
        }
      } else {
        // one-to-one
        // delete associatedObj
        // recursive call
        Oid oid = schema.getDom().lookUpObjectId(you.getClass(), you);
        deleteObjectStrict(you.getClass(), you, oid);
      }
    }
  }
  
  private boolean updateManyAssociateOnDelete(Object myObj, 
      Oid objId,
      //Class associateClass, 
      DAttr myAttrib,
      Object val, boolean updateDS) 
  throws NotFoundException, NotPossibleException {
    DODMBasic schema = instance.getDODM();

    // v2.6: make sure that myObj is fully loaded
    //TODO: not use this 
    //schema.getDom().loadReferencedObjects(myObj);
    /*v2.7.2: use the new method
    boolean updated = schema.getDom().updateOneToManyAssociateOnDelete(myObj, associateClass, val);
    */
    boolean updated = schema.getDom().updateAssociateToRemoveLink(myObj, val, myAttrib);
    
    if (updateDS && updated) {
      // save changes
      try {
        schema.getDom().updateObject(myObj, null);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
            "Không thể thực thi phương thức {0}.{1}({2})", "DataController", "updateObject", myObj);
      }
    }
    
    return updated;
  }
  
  private boolean updateOneToOneAssociateOnDelete(Object myObj, 
      //v2.7.2:  Class associateClass,
      DAttr attrib, 
      Object val, boolean updateDS) 
  throws NotFoundException, NotPossibleException {
    //TODO: to use additional role information to avoid the case of multiple links
    // between the objects
    DODMBasic schema = instance.getDODM();

    boolean updated = schema.getDom().updateAssociateLink(myObj, attrib, val);
    
    if (updateDS && updated) {
      // save changes
      try {
        schema.getDom().updateObject(myObj, null);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
            "Không thể thực thi phương thức {0}.{1}({2})", "DataController", "updateAttributeValue", myObj);
      }
    }
    
    return updated;
  }
}
