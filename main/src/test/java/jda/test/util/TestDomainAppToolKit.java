package jda.test.util;

import java.util.Collection;

import jda.modules.report.controller.ReportController;
import jda.modules.report.model.Report;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMTesterWithBrowse;
import jda.test.dodm.DODMTesterWithBrowse.BrowsingStep;

public class TestDomainAppToolKit {
  private TestDomainAppToolKit() {}
  
  /**
   * @effects 
   *  if browseSeq != null
   *    display on the terminal console the result of <tt>report</tt> which was executed by controller <tt>reptCtl</tt>
   *    using the browsing steps in <tt>browseSeq</tt>
   *  else
   *    display the report result from first to last 
   *  
   */
  public static void displayReportResult(ReportController<Report> reptCtl, Report report, 
      BrowsingStep[] browseSeq) {
    
    System.out.printf("%ndisplayResult(%s,%s)%n", reptCtl, report);

    Collection<Oid> result = reptCtl.getFirstResult();
    
    System.out.printf("Report result: %n%s%n", result);
    
    // browse through the result
    Class c = reptCtl.getInputDomainClass(); //result.iterator().next().getCls();
    
    try {
      if (browseSeq == null) {
        DODMTesterWithBrowse.browseFirstToLast(c, result);
      } else {
        DODMTesterWithBrowse.browse(c, result, browseSeq, null);
      }
    } catch (Exception e) {
      System.err.println("Failed to browse report result: ");
      e.printStackTrace();
    }
  }
}
