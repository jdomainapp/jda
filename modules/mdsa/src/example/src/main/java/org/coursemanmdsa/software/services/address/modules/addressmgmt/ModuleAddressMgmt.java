package org.coursemanmdsa.software.services.address.modules.addressmgmt;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.model.ModelDesc;
import org.coursemanmdsa.software.services.address.modules.addressmgmt.model.AddressMgmt;

@ModuleDescriptor(
        name = "addressmgmt",
        modelDesc = @ModelDesc(
                model = AddressMgmt.class
// TODO:               eventSource = true, eventSink = true
        )
)
public class ModuleAddressMgmt {
}
