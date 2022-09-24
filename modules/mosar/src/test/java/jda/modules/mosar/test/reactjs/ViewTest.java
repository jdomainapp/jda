package jda.modules.mosar.test.reactjs;

import org.courseman.modules.address.model.Address;
import org.courseman.modules.coursemodule.ModuleCourseModule;
import org.courseman.modules.coursemodule.model.CompulsoryModule;
import org.courseman.modules.coursemodule.model.CourseModule;
import org.courseman.modules.coursemodule.model.ElectiveModule;
import org.courseman.modules.enrolment.ModuleEnrolment;
import org.courseman.modules.student.ModuleStudent;
import org.courseman.modules.student.model.Gender;
import org.courseman.modules.student.model.Student;
import org.courseman.modules.studentclass.ModuleStudentClass;
import org.courseman.modules.studentclass.model.StudentClass;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.frontend.MCCUtils;
import jda.modules.mosar.utils.DomainTypeRegistry;

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

//        View formView = new FormViewWithTypeSelect(CourseModule.class);
//        System.out.println(formView.getAsString());
//        View listView = new ListView(viewDesc);
//        System.out.println(listView.getAsString());
    }
}
