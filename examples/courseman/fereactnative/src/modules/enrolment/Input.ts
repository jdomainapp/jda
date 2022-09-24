import {createModuleInput} from '../../base/creators/createInputComponents';
import {EnrolmentModuleConfig} from './ModuleConfig';
export const {
  Input: EnrolmentInput,
  FormInput: FormEnrolmentInput,
  FormMultiInput: FormMultiEnrolmentInput,
} = createModuleInput(EnrolmentModuleConfig);
