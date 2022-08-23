import {Student} from '../../data_types/Student';
import {StudentListConfig} from './ListConfig';
import {StudentModuleConfig} from './ModuleConfig';

import {createModuleComponents} from '../../base/creators/createModuleComponents';
import {StudentFormConfig} from './FormConfig';
export const {
  Module: StudentModule,
  List: StudentList,
  ListItem: StudentListItem,
  Form: StudentForm,
} = createModuleComponents<Student>(
  StudentModuleConfig,
  StudentListConfig,
  StudentFormConfig,
);
