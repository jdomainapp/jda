package org.coursemanmdsa.software.services.acadadmin.modules.acadadmin;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import org.coursemanmdsa.software.services.acadadmin.modules.acadadmin.model.AcadAdmin;

@ModuleDescriptor(
        name = "acadadmin",
        modelDesc = @ModelDesc(
                model = AcadAdmin.class
        )
)
public class ModuleAcadAdmin {

}
