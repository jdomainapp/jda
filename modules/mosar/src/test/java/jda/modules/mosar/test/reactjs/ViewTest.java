package jda.modules.mosar.test.reactjs;

import java.util.stream.Collectors;

import com.hanu.courseman.modules.address.ModuleAddress;
import com.hanu.courseman.modules.address.model.Address;
import com.hanu.courseman.modules.coursemodule.ModuleCourseModule;
import com.hanu.courseman.modules.coursemodule.model.CompulsoryModule;
import com.hanu.courseman.modules.coursemodule.model.CourseModule;
import com.hanu.courseman.modules.coursemodule.model.ElectiveModule;
import com.hanu.courseman.modules.enrolment.ModuleEnrolment;
import com.hanu.courseman.modules.enrolment.model.Enrolment;
import com.hanu.courseman.modules.student.ModuleStudent;
import com.hanu.courseman.modules.student.model.Gender;
import com.hanu.courseman.modules.student.model.Student;
import com.hanu.courseman.modules.studentclass.ModuleStudentClass;
import com.hanu.courseman.modules.studentclass.model.StudentClass;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.frontend.MCCUtils;
import jda.modules.mosar.utils.DomainTypeRegistry;
import jda.modules.mosarfrontend.reactjs.model.common.MCCRegistry;
import jda.modules.mosarfrontend.reactjs.model.views.FormView;

public class ViewTest {
    public static void main(String[] args) {
        Class[] models = {
                Student.class,
                Gender.class,
                Address.class,
                StudentClass.class,
                CourseModule.class,
                ElectiveModule.class,
                CompulsoryModule.class
        };
        Class[] modules = {
                ModuleStudent.class,
                ModuleEnrolment.class,
                ModuleStudentClass.class,
                ModuleCourseModule.class
        };

        DomainTypeRegistry.getInstance().addDomainTypes(models);

        MCC viewDesc = MCCUtils.readMCC(Student.class, ModuleStudent.class);
        MCCRegistry.getInstance().add(viewDesc);
        MCCRegistry.getInstance().add(MCCUtils.readMCC(Enrolment.class, ModuleEnrolment.class));
        MCCRegistry.getInstance().add(MCCUtils.readMCC(Address.class, ModuleAddress.class));
        MCCRegistry.getInstance().add(MCCUtils.readMCC(StudentClass.class, ModuleStudentClass.class));
        MCCRegistry.getInstance().add(MCCUtils.readMCC(CourseModule.class, ModuleCourseModule.class));
        FormView formView = new FormView(viewDesc);
        System.out.println(formView.getAsString());
        System.out.println(formView.getSubViews().stream().map(view -> view.getAsString()).collect(Collectors.toList()));
//        View formView = new FormViewWithTypeSelect(CourseModule.class);
//        System.out.println(formView.getAsString());
//        View listView = new ListView(viewDesc);
//        System.out.println(listView.getAsString());
    }
}
