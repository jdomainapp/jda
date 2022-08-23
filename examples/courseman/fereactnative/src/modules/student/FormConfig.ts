import {
  IJDAFormConfig,
  JDAFormMode,
} from '../../base/controllers/jda_form_controllers/withFormController';
import {Student} from '../../data_types/Student';
import {Modules} from '../../data_types/enums/Modules';

import {FormStringInput, FormGenderInput, FormDateInput} from '../FormInputs';

import {FormAddressInput} from '../address/Input';
import {FormStudentClassInput} from '../student_class/Input';
import {FormMultiEnrolmentInput} from '../enrolment/Input';

export const StudentFormConfig: IJDAFormConfig<Student> = {
  id: {
    component: FormStringInput,
    options: {disabled: true, rules: {maxLength: 6}},
  },
  name: {
    component: FormStringInput,
    options: {rules: {required: true, maxLength: 30}},
  },
  gender: {
    component: FormGenderInput,
    options: {rules: {required: true, maxLength: 10}},
  },
  dob: {
    component: FormDateInput,
    options: {rules: {required: true, maxLength: 15}},
  },
  address: {
    component: FormAddressInput,
    options: {module: Modules.Address, rules: {maxLength: 20}},
    props: {associateField: 'student'},
  },
  email: {
    component: FormStringInput,
    options: {rules: {required: true, maxLength: 30}},
  },
  studentClass: {
    component: FormStudentClassInput,
    options: {module: Modules.StudentClass, rules: {maxLength: 6}},
    props: {associateCollection: 'students'},
  },
  enrolments: {
    component: FormMultiEnrolmentInput,
    options: {module: Modules.Enrolment, rules: {required: true}},
    props: {associateField: 'student'},
  },
};
