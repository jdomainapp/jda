import {
  IJDAFormConfig,
  JDAFormMode,
} from '../../base/controllers/jda_form_controllers/withFormController';
import {Enrolment} from '../../data_types/Enrolment';
import {Modules} from '../../data_types/enums/Modules';

import {FormNumberInput, FormStringInput} from '../FormInputs';

import {FormStudentInput} from '../student/Input';
import {FormCourseModuleInput} from '../course_module/Input';

export const EnrolmentFormConfig: IJDAFormConfig<Enrolment> = {
  id: {
    component: FormNumberInput,
    options: {disabled: true, rules: {maxLength: 5}},
  },
  student: {
    component: FormStudentInput,
    options: {module: Modules.Student, rules: {required: true, maxLength: 5}},
    props: {associateCollection: 'enrolments'},
  },
  courseModule: {
    component: FormCourseModuleInput,
    options: {
      module: Modules.CourseModule,
      rules: {required: true, maxLength: 5},
    },
  },
  internalMark: {
    component: FormNumberInput,
    options: {rules: {min: 0.0, maxLength: 4}},
  },
  examMark: {
    component: FormNumberInput,
    options: {rules: {min: 0.0, maxLength: 4}},
  },
  finalGrade: {
    component: FormStringInput,
    options: {disabled: true, rules: {maxLength: 1}},
  },
  finalMark: {
    component: FormNumberInput,
    options: {disabled: true},
  },
};
