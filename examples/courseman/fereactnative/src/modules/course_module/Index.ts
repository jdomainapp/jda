import {CourseModule} from '../../data_types/CourseModule';
import {CourseModuleListConfig} from './ListConfig';
import {CourseModuleModuleConfig} from './ModuleConfig';

import {createTypedModuleComponents} from '../../base/creators/createTypedModuleComponents';
import {ITypedFormItem} from '../../base/controllers/jda_form_controllers/withTypedFormController';
import {CourseModuleType} from '../../data_types/enums/CourseModuleType';
import {CompulsoryModuleForm} from './sub_modules/compulsory_module/Index';
import {ElectiveModuleForm} from './sub_modules/elective_module/Index';

export const CourseModuleFormList: ITypedFormItem[] = [
  {
    type: CourseModuleType.compulsory,
    formComponent: CompulsoryModuleForm,
  },
  {
    type: CourseModuleType.elective,
    formComponent: ElectiveModuleForm,
  },
];

export const {
  Module: CourseModuleModule,
  List: CourseModuleList,
  ListItem: CourseModuleListItem,
  Form: CourseModuleForm,
} = createTypedModuleComponents<CourseModule>(
  CourseModuleModuleConfig,
  CourseModuleListConfig,
  CourseModuleFormList,
);
