package jda.modules.dodm;

import java.io.File;
import java.io.FileNotFoundException;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.CsvOSM;
import jda.modules.dodm.osm.OSM;
import jda.modules.dodm.osm.OSMFactory;
import jda.modules.javadbserver.model.JavaDbServer;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;

/**
 * @overview
 *  Extends {@link DODM} with extra functionality
 *  
 * @author dmle
 *
 */
public class DODM extends DODMBasic {

  private JavaDbServer dbServer;

  public DODM(Configuration config) throws NotPossibleException {
    super(config);
  }

  public DODM(Configuration config, JavaDbServer dbServer) {
    super(config);
    this.dbServer = dbServer;
  }

  public static DODM getInstance(Configuration config,
      JavaDbServer dbServer) throws NotPossibleException {
    
    // 20210317:
//    Class<? extends DODMBasic> dodmType = config.getDodmConfig().getDodmType();
//    if (dodmType == null)
//      dodmType = DODM.class;
    
    // 20210317:
     DODM instance = DODMBasic.getInstance(DODM.class, config);
//    DODMBasic instance = DODMBasic.getInstance(dodmType, config);
    
//    if (instance instanceof DODM) 
    ((DODM)instance).dbServer = dbServer;
    
    return instance;  
  }
  
  /**
   * @effects 
   *  return this.dom
   */
  public DOM getDom() {
    return (DOM) super.getDom();
  }

  /**
   * @effects 
   *  return this.dsm
   */
  public DSM getDsm() {
    return (DSM) super.getDsm();
  } 
  
  /**
   * @effects 
   *  import into this the objects of the specified <tt>domainCls</tt> which are recorded in a 
   *  <tt>csv</tt> file at <tt>filePath</tt>
   *  
   *  <p>throws FileNotFoundException if file is not found; 
   *  DataSourceException if failed to import objects into the underlying data source; 
   *  NotPossibleException if failed to perform other tasks
   *  
   * @version 3.0
   */
  public void importObjectsFromCsvFile(Class domainCls, String filePath) throws FileNotFoundException, NotPossibleException, DataSourceException {
    // make sure to load the object pool metadata of the domain class
    DOMBasic dom = getDom();
    
    if (!dom.isIdRangeInitialised(domainCls))
      // v2.8: schema.loadMetadata(cls);
      dom.retrieveMetadata(domainCls);
    
    // for now: support Csv only 
    // create url = class-folder + cls name + .csv
    
    //v3.1: added a check if filePath is a path from a jar file
    // if so then file exists check is not performed here but when filePath is processed
    if (!ToolkitIO.isFileUrl(filePath)) {
      File importFile = new File(filePath);
      if (!importFile.exists()) {
        throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, filePath);
      }
    }
    
    OsmConfig osmCfg = OSMFactory.getStandardOsmConfig("csv", filePath);

    //osmCfg.setDataSourcePath(importFilePath);
    
    // clone the configuration with the OsmConfig
    OSM osm = OSMFactory.getOsmInstance(CsvOSM.class, osmCfg, dom);
    
    // connect to the data source
    osm.connect();
    
    // import object records of the specified domain class
    dom.importObjects(osm, domainCls);    
  }
  
  @Override
  public void close() {
    if (dbServer != null)
      dbServer.stop();

    super.close();
  }
}
