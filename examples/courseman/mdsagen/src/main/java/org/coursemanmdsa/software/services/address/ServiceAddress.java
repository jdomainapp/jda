package org.coursemanmdsa.software.services.address;

import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.tmsa.tasl.syntax.ServiceDesc;
import org.coursemanmdsa.software.services.address.modules.address.ModuleAddress;
import org.coursemanmdsa.software.services.address.modules.addressmgmt.ModuleAddressMgmt;

@ServiceDesc(
        name = "address-service",
        description = "Address Service",
        port = 8082,
        serviceTree = @CTree(
                root = ModuleAddressMgmt.class,
                edges = {
                        @CEdge(parent = ModuleAddressMgmt.class, child = ModuleAddress.class)
                }
        )
)
public class ServiceAddress extends ModuleAddressMgmt {
}
