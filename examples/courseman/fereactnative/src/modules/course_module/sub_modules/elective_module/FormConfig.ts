import {IJDAFormConfig} from '../../../../base/controllers/jda_form_controllers/withFormController';
import {ElectiveModule} from '../../../../data_types/ElectiveModule';
import {CourseModuleFormConfig} from '../../FormConfig';

import {FormStringInput} from '../../../FormInputs';

export const ElectiveModuleFormConfig: IJDAFormConfig<ElectiveModule> = {
  ...CourseModuleFormConfig,
  deptName: {
    component: FormStringInput,
    options: {rules: {required: true, maxLength: 50}},
  },
};
