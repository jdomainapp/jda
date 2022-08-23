import {
  IJDAFormConfig,
  JDAFormMode,
} from '../../base/controllers/jda_form_controllers/withFormController';
import {StudentClass} from '../../data_types/StudentClass';
import {Modules} from '../../data_types/enums/Modules';

import {FormNumberInput, FormStringInput} from '../FormInputs';

import {FormMultiStudentInput} from '../student/Input';

export const StudentClassFormConfig: IJDAFormConfig<StudentClass> = {
  id: {
    component: FormNumberInput,
    options: {disabled: true, rules: {maxLength: 6}},
  },
  name: {
    component: FormStringInput,
    options: {rules: {required: true, maxLength: 20}},
  },
  students: {
    component: FormMultiStudentInput,
    options: {module: Modules.Student},
    props: {associateField: 'studentClass'},
  },
};
