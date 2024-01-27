package org.coursemanmdsa.software.services.financialhub.modules.financialhub;


import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import org.coursemanmdsa.software.services.financialhub.modules.financialhub.model.FinancialHub;

@ModuleDescriptor(
        name = "financialhub",
        modelDesc = @ModelDesc(
                model = FinancialHub.class
        )
)
public class ModuleFinancialHub {
}
