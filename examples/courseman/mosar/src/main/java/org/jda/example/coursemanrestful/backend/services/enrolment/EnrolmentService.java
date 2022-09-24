package org.jda.example.coursemanrestful.backend.services.enrolment;

import jda.modules.mosar.backend.base.services.SimpleDomServiceAdapter;
import org.jda.example.coursemanrestful.modules.enrolment.model.Enrolment;
import javax.annotation.Generated;
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
@Service(value = "org.jda.example.coursemanrestful.backend.services.enrolment.EnrolmentService")
public class EnrolmentService extends SimpleDomServiceAdapter<org.jda.example.coursemanrestful.modules.enrolment.model.Enrolment> {

    public Enrolment createEntity(Enrolment arg0) {
        return super.createEntity(arg0);
    }

    public Page getEntityListByPage(PagingModel arg0) {
        return super.getEntityListByPage(arg0);
    }

    public Enrolment getEntityById(Identifier arg0) {
        return super.getEntityById(arg0);
    }

    public Enrolment updateEntity(Identifier arg0, Enrolment arg1) {
        return super.updateEntity(arg0, arg1);
    }

    public void deleteEntityById(Identifier arg0) {
        super.deleteEntityById(arg0);
    }

    public Collection getAllEntities() {
        return super.getAllEntities();
    }

    public void setOnCascadeUpdate(BiConsumer arg0) {
        super.setOnCascadeUpdate(arg0);
    }

    @Autowired()
    public EnrolmentService(jda.mosa.software.impl.SoftwareImpl arg0) {
        super(arg0);
        this.setType(Enrolment.class);
    }
}
