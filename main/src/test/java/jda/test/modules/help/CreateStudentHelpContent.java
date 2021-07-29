package jda.test.modules.help;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.help.lang.en.HelpContentEN;
import jda.modules.help.lang.vi.HelpContentVI;
import jda.modules.help.model.AppHelp;
import jda.modules.help.model.HelpContent;
import jda.modules.help.model.HelpItem;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.util.TestDomainAppToolKit;

public class CreateStudentHelpContent extends CourseManBasicTester {
  @Test
  public void doTest() throws Exception {
    
    // register configuration schema
    DSM dsm = (DSM) instance.getDsm();
    DOM dom = (DOM) instance.getDom();
    DODMBasic schema = instance.getDODM();
    
    CourseManSetUp su = new CourseManSetUp();
    instance.registerConfigurationSchema(su);
    
//    
//    int count = (oids != null) ? oids.size() : 0;
//    
//    System.out.printf("Number of modules: %d%n", count);
//    
//    if (oids != null) {
//      TestObjectBrowser.browseFirstToLast(c, oids);
//    }
    
    // register classes// register a test domain class (e.g. Student)
    Class[] domainClasses = { 
        HelpItem.class, 
        HelpContent.class,
        HelpContentVI.class,
        HelpContentEN.class,
        AppHelp.class
    };
    instance.addClasses(domainClasses);
    
    
    // load/read student application module
    Class c = Configuration.class;
    // v2.8 dom.loadMetadata(c);
    dom.retrieveMetadata(c);
    
    Oid cfgOid = dom.getLowestOid(c);
    Configuration cfg = (Configuration) dom.loadObject(c, cfgOid);
    
    Class<ApplicationModule> c1 = ApplicationModule.class;
    DAttr linkAttrib = dsm.getDomainConstraint(c1, "config");
    Query q = new Query(new ObjectExpression(
        c1, linkAttrib, Op.EQ, cfg
        ));
    Collection<Oid> oids = dom.retrieveObjectOids(c1, q);
    
    Iterator<Oid> it = oids.iterator();
    
    ApplicationModule main = dom.retrieveObject(c1, it.next());
    ApplicationModule city = dom.retrieveObject(c1, it.next());
    
    // create help content for student module
    HelpContent helpContentVI = new HelpContentVI(main, "Overview: CourseMan", "Title: Chương trình quản lý CourseMan", null, null);
//    HelpContent helpContentEN = new HelpContentEN(main, "Overview: CourseMan", "Title: course management application", null);
//    helpContentVI.addHelpItem(helpItem);

    RegionGui mainGuiRegion = main.getViewCfg();
    HelpItem helpItem = new HelpItem(mainGuiRegion, "Chương trình quản lý CourseMan", helpContentVI);
    
    helpContentVI.addHelpItem(helpItem);

    // store help content to db
    dom.addObject(helpContentVI);
    //dom.addObject(helpContentEN);
    dom.addObject(helpItem);
    
    // printDB for help content
    Class[] toPrint = { HelpContent.class, HelpContentVI.class, HelpItem.class };
    printDataDB(toPrint);
  }
}
