package jda.software.ddd;

import java.util.Collection;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.mosa.software.impl.DomSoftware;

/**
 * @overview 
 *  Represents a micro software abstraction, which provides CRUD and other shared tasks
 *  over objects a single domain class.  
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1 
 */
public abstract class MicroDomSoftware<T> {
  private static Logger logger = (Logger) LoggerFactory.getLogger(MicroDomSoftware.class.getSimpleName());

  private Class<T> dcls;
  
  private DomSoftware sw;
  
  /**
   * @effects 
   *
   * @version 
   */
  public MicroDomSoftware(DomSoftware sw, Class<T> dcls) {
    this.sw = sw;
    this.dcls = dcls;
  }

  /**
   * @effects return sw
   */
  public DomSoftware getSw() {
    return sw;
  }

  public abstract MicroDomSoftware run() throws DataSourceException;

  public MicroDomSoftware initDom() throws DataSourceException {
    // register a domain model fragment concerning Student
    Class[] domFrag = {
        dcls
    };
    sw.addClasses(domFrag);
    
    return this;
  }
  
  public abstract MicroDomSoftware create() throws DataSourceException;
  
  public MicroDomSoftware update(Object id) throws NotFoundException, DataSourceException {
    T obj = sw.retrieveObjectById(dcls, id);
    if (obj != null) {
      logger.info("Updating object\n{}\n", obj);
      doUpdate(obj, id);
      logger.info("... after:\n{}\n", obj);
    }  
    
    return this;
  }
  
  /**
   * @effects 
   *  perform the actual update on <code>obj</code>
   */
  protected abstract void doUpdate(T obj, Object id)  throws DataSourceException;

  public MicroDomSoftware delete(Object id) throws NotFoundException, DataSourceException {
    T obj = sw.retrieveObjectById(dcls, id);
    if (obj != null) {
      logger.info("Deleting object\n{}\n", obj);
      sw.deleteObject(obj, dcls);
    }     
    
    return this;
  }
  
  public MicroDomSoftware loadAndDisplay() throws DataSourceException {
    //   get objects
    sw.loadAndPrintObjects(dcls);
    
    return this;
  }

  public MicroDomSoftware displayPool() throws DataSourceException {
    // check that a new object is in the object pool
    sw.printObjectPool(dcls);
    
    return this;    
  }

  public MicroDomSoftware displaySource() throws DataSourceException {
    // check that object is in the database by printing data in the database
    sw.printObjectDB(dcls);
    
    return this;    
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public MicroDomSoftware displayMaterialisedDomainModel() {
    String modelName = sw.getDomainModelName(dcls);
    if (modelName != null) {
      sw.printMaterialisedDomainModel(modelName);
    }
    
    return this;
  }

  /**
   * @effects 
   * 
   * @version 
   * @throws DataSourceException 
   * 
   */
  public MicroDomSoftware deleteMyDomainModel() throws DataSourceException {
    String modelName = sw.getDomainModelName(dcls);
    if (modelName != null) {
      sw.deleteDomainModel(modelName);
    }
    
    return this;
  }

  public MicroDomSoftware deleteDomainModel(Class... classes) throws DataSourceException {
    sw.deleteDomainModel(classes);
    
    return this;
  }
  
  /**
   * @effects 
   * 
   * @version 
   * @param sw 
   * 
   */
  public MicroDomSoftware deleteClass() throws DataSourceException {
    boolean isReg = sw.isRegistered(dcls);
    boolean isMat = sw.isMaterialised(dcls);
    logger.info("{}\n  isRegistered: {}\n  isMaterialised: {}\n", 
        dcls.getSimpleName(), isReg, isMat);
    if (isMat) {
      Class[] toDelete = {dcls};
      logger.info("...unregistering/deleting\n");
      sw.deleteDomainModel(toDelete);
      isReg = sw.isRegistered(dcls);
      isMat = sw.isMaterialised(dcls);
      logger.info("  isRegistered: %b\n  isMaterialised: %b\n", isReg, isMat);
    }    
    
    return this;
  }
  
  /**
   * @effects 
   *  retrieve and display objects matching the arguments.  
   */
  public Collection<T> querySimple(String attribName, Op op, String val) throws NotPossibleException, DataSourceException {
    
    Collection<T> objects = getSw().retrieveObjects(dcls, attribName, op, val);
    sw.printObjects(dcls, objects);
    return objects;
  }

  /**
   * @effects 
   *  delete data source store of {@link #dcls}
   */
  public void reset() throws DataSourceException {
    deleteDomainModel(dcls);
  }  
}
