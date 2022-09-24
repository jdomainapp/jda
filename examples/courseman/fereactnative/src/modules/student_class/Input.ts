import {createModuleInput} from '../../base/creators/createInputComponents';
import {StudentClassModuleConfig} from './ModuleConfig';
export const {
  Input: StudentClassInput,
  FormInput: FormStudentClassInput,
  FormMultiInput: FormMultiStudentClassInput,
} = createModuleInput(StudentClassModuleConfig);
