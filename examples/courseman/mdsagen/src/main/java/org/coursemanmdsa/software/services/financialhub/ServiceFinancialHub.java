package org.coursemanmdsa.software.services.financialhub;

import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.tmsa.tasl.syntax.ServiceDesc;
import org.coursemanmdsa.software.services.assessmenthub.modules.assessmenthub.ModuleAssessmentHub;
import org.coursemanmdsa.software.services.financialhub.modules.assessment.ModuleAssessment;
import org.coursemanmdsa.software.services.financialhub.modules.financialhub.ModuleFinancialHub;
import org.coursemanmdsa.software.services.assessmenthub.ServiceAssessmentHub;

@ServiceDesc(
        name = "financialhub-service",
        description = "FinancialHub Service",
        port = 8091,
        serviceTree = @CTree(
                root = ModuleFinancialHub.class,
                edges = {
                        @CEdge(parent = ModuleFinancialHub.class, child = ModuleAssessment.class),
                        @CEdge(parent = ModuleAssessment.class, child = ServiceAssessmentHub.class)

                        // TODO: invoice
                }
        )
)
public class ServiceFinancialHub extends ModuleAssessmentHub {
}
