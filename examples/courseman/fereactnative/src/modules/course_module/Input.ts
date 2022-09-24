import {createModuleInput} from '../../base/creators/createInputComponents';
import {CourseModuleModuleConfig} from './ModuleConfig';
export const {
  Input: CourseModuleInput,
  FormInput: FormCourseModuleInput,
  FormMultiInput: FormMultiCourseModuleInput,
} = createModuleInput(CourseModuleModuleConfig);
