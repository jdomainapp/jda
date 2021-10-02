package org.jda.example.courseman.modules.enrolmentmgmt.forked;

import java.util.Collection;

import org.jda.example.courseman.modules.authorisation.ModuleAuthorisation;
import org.jda.example.courseman.modules.authorisation.model.Authorisation;
import org.jda.example.courseman.modules.enrolmentmgmt.forked.model.control.FEnrolmentProcessing;
import org.jda.example.courseman.modules.payment.ModulePayment;
import org.jda.example.courseman.modules.payment.model.Payment;

import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;

@ModuleDescriptor(
    name="ModuleFEnrolmentProcessing",
    modelDesc=@ModelDesc(
        model=FEnrolmentProcessing.class
    ),
    viewDesc=@ViewDesc(
        formTitle="-",
        domainClassLabel="FEnrolmentProcessing",    
        imageIcon="enrolmentProc.jpg",
        view=View.class,
        viewType=RegionType.Data,
        layoutBuilderType=TabLayoutBuilder.class
        //topX=0.5,topY=0.0,//widthRatio=0.9f,heightRatio=0.9f,
        //parent=RegionName.Tools,
    )
    ,isPrimary=true,
    childModules={ModulePayment.class, ModuleAuthorisation.class}
    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleFEnrolmentProcessing {
  @AttributeDesc(label="Enrolment Processing")
  private String title;

  // payment 
  @AttributeDesc(label="Payment"
      ,type=DefaultPanel.class
  )
  private Collection<Payment> payments;
  
  // authorisation 
  @AttributeDesc(label="Authorisation"
      ,type=DefaultPanel.class
  )
  private Collection<Authorisation> authorisations;
  
}
