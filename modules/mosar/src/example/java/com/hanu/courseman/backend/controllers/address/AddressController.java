package com.hanu.courseman.backend.controllers.address;

import jda.modules.mosar.backend.base.controllers.DefaultRestfulController;
import com.hanu.courseman.modules.address.model.Address;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.PagingModel;
import jda.modules.mosar.backend.base.models.Page;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController()
@RequestMapping(value = "/addresses")
@Generated(value = "jda.modules.mosar.software.backend.generators.SourceCodeWebControllerGenerator")
public class AddressController extends DefaultRestfulController<com.hanu.courseman.modules.address.model.Address> {

    @org.springframework.web.bind.annotation.PostMapping()
    public Address createEntity(@org.springframework.web.bind.annotation.RequestBody() Address arg0) {
        return super.createEntity(arg0);
    }

    @org.springframework.web.bind.annotation.GetMapping()
    public Page getEntityListByPage(PagingModel arg0) {
        return super.getEntityListByPage(arg0);
    }

    @org.springframework.web.bind.annotation.GetMapping(value = "/{id}")
    public Address getEntityById(Identifier arg0) {
        return super.getEntityById(arg0);
    }

    @org.springframework.web.bind.annotation.PatchMapping(value = "/{id}")
    public Address updateEntity(Identifier arg0, @org.springframework.web.bind.annotation.RequestBody() Address arg1) {
        return super.updateEntity(arg0, arg1);
    }

    @org.springframework.web.bind.annotation.DeleteMapping(value = "/{id}")
    public void deleteEntityById(Identifier arg0) {
        super.deleteEntityById(arg0);
    }

    @Autowired()
    public AddressController(jda.modules.mosar.backend.base.websockets.WebSocketHandler arg0) {
        super(arg0);
    }
}
