package org.coursemanmdsa.software.services.assessmenthub.modules.assessmenthub;


import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import org.coursemanmdsa.software.services.assessmenthub.modules.assessmenthub.model.AssessmentHub;

@ModuleDescriptor(
        name = "ModuleAssessmentHub",
        modelDesc = @ModelDesc(
                model = AssessmentHub.class
        )
)
public class ModuleAssessmentHub {
}
