package com.hanu.courseman.backend.services.studentclass;

import jda.modules.restfstool.backend.base.services.SimpleDomServiceAdapter;
import com.hanu.courseman.modules.studentclass.model.StudentClass;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import jda.modules.restfstool.backend.base.models.Identifier;
import jda.modules.restfstool.backend.base.models.PagingModel;
import jda.modules.restfstool.backend.base.models.Page;
import java.util.Collection;
import java.util.function.BiConsumer;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import jda.modules.restfstool.backend.utils.InheritanceUtils;

@Generated(value = "jda.modules.restfstool.backend.generators.SourceCodeServiceTypeGenerator")
@Service(value = "com.hanu.courseman.backend.services.studentclass.StudentClassService")
public class StudentClassService extends SimpleDomServiceAdapter<com.hanu.courseman.modules.studentclass.model.StudentClass> {

    public StudentClass createEntity(StudentClass arg0) {
        return super.createEntity(arg0);
    }

    public Page getEntityListByPage(PagingModel arg0) {
        return super.getEntityListByPage(arg0);
    }

    public StudentClass getEntityById(Identifier arg0) {
        return super.getEntityById(arg0);
    }

    public StudentClass updateEntity(Identifier arg0, StudentClass arg1) {
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
    public StudentClassService(jda.mosa.software.impl.SoftwareImpl arg0) {
        super(arg0);
        this.setType(StudentClass.class);
    }
}
