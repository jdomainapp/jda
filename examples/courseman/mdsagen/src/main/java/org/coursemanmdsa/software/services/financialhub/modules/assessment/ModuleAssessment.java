package org.coursemanmdsa.software.services.financialhub.modules.assessment;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import org.coursemanmdsa.software.services.financialhub.modules.assessment.model.Assessment;

@ModuleDescriptor(
        name = "ModuleAssessment",
        modelDesc = @ModelDesc(
                model = Assessment.class
        )
)
public class ModuleAssessment {

}
