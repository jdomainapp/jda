import {createModuleInput} from '../../base/creators/createInputComponents';
import {StudentModuleConfig} from './ModuleConfig';
export const {
  Input: StudentInput,
  FormInput: FormStudentInput,
  FormMultiInput: FormMultiStudentInput,
} = createModuleInput(StudentModuleConfig);
