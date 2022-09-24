import {StudentClass} from '../../data_types/StudentClass';
import {StudentClassListConfig} from './ListConfig';
import {StudentClassModuleConfig} from './ModuleConfig';

import {createModuleComponents} from '../../base/creators/createModuleComponents';
import {StudentClassFormConfig} from './FormConfig';
export const {
  Module: StudentClassModule,
  List: StudentClassList,
  ListItem: StudentClassListItem,
  Form: StudentClassForm,
} = createModuleComponents<StudentClass>(
  StudentClassModuleConfig,
  StudentClassListConfig,
  StudentClassFormConfig,
);
