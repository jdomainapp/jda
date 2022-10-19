package org.jda.example.coursemanrestful.backend.services.coursemodule;

import jda.modules.mosar.backend.base.services.InheritedDomServiceAdapter;
import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.PagingModel;
import java.util.Collection;
import jda.modules.mosar.backend.base.models.Page;
import java.util.function.BiConsumer;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import jda.modules.mosar.utils.InheritanceUtils;

@Generated(value = "jda.modules.mosar.software.backend.generators.SourceCodeServiceTypeGenerator")
@Service(value = "org.jda.example.coursemanrestful.backend.services.coursemodule.CourseModuleService")
public class CourseModuleService extends InheritedDomServiceAdapter<org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule> {

    public void deleteEntityById(Identifier arg0) {
        super.deleteEntityById(arg0);
    }

    public CourseModule getEntityById(Identifier arg0) {
        return super.getEntityById(arg0);
    }

    public CourseModule updateEntity(Identifier arg0, CourseModule arg1) {
        return super.updateEntity(arg0, arg1);
    }

    public void setOnCascadeUpdate(BiConsumer arg0) {
        super.setOnCascadeUpdate(arg0);
    }

    public Collection getAllEntities() {
        return super.getAllEntities();
    }

    public CourseModule createEntity(CourseModule arg0) {
        return super.createEntity(arg0);
    }

    public Page getEntityListByPage(PagingModel arg0) {
        return super.getEntityListByPage(arg0);
    }

    @Autowired()
    public CourseModuleService(jda.mosa.software.impl.SoftwareImpl arg0, @Qualifier(value = "org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule") java.util.Map arg1) {
        super(arg0, arg1);
        this.setType(CourseModule.class);
        this.setSubtypes(InheritanceUtils.getSubtypeMapFor(this.type));
    }
}
