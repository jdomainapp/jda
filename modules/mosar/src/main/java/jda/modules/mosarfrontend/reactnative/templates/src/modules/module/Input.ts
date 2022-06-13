import { createModuleInput } from '../../base/creators/createInputComponents';
import {@slot{{moduleName}}ModuleConfig} from './ModuleConfig'
export const {
  Input: @slot{{moduleName}}Input,
  FormInput: Form@slot{{moduleName}}Input,
  FormMultiInput: FormMulti@slot{{moduleName}}Input,
} = createModuleInput(@slot{{moduleName}}ModuleConfig);