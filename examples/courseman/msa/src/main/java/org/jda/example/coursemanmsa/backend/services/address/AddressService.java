package org.jda.example.coursemanmsa.backend.services.address;

import jda.modules.mosar.backend.base.services.SimpleDomServiceAdapter;

import javax.annotation.Generated;

import org.jda.example.coursemanmsa.modules.address.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.PagingModel;
import jda.modules.mosar.backend.base.models.Page;
import java.util.Collection;
import java.util.function.BiConsumer;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import jda.modules.mosar.utils.InheritanceUtils;

@Generated(value = "jda.modules.mosar.software.backend.generators.SourceCodeServiceTypeGenerator")
@Service(value = "org.jda.example.coursemanmdsa.backend.services.address.AddressService")
public class AddressService extends SimpleDomServiceAdapter<org.jda.example.coursemanmsa.modules.address.model.Address> {

    public Address createEntity(Address arg0) {
        return super.createEntity(arg0);
    }

    public Page getEntityListByPage(PagingModel arg0) {
        return super.getEntityListByPage(arg0);
    }

    public Address getEntityById(Identifier arg0) {
        return super.getEntityById(arg0);
    }

    public Address updateEntity(Identifier arg0, Address arg1) {
        return super.updateEntity(arg0, arg1);
    }

    public void deleteEntityById(Identifier arg0) {
        super.deleteEntityById(arg0);
    }

    public void setOnCascadeUpdate(BiConsumer arg0) {
        super.setOnCascadeUpdate(arg0);
    }

    public Collection getAllEntities() {
        return super.getAllEntities();
    }

    @Autowired()
    public AddressService(jda.mosa.software.impl.SoftwareImpl arg0) {
        super(arg0);
        this.setType(Address.class);
    }
}
