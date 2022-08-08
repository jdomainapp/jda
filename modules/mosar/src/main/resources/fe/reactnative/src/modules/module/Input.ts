import { createModuleInput } from '../../base/creators/createInputComponents';
import {@slot{{ModuleName}}ModuleConfig} from './ModuleConfig'
export const {
  Input: @slot{{ModuleName}}Input,
  FormInput: Form@slot{{ModuleName}}Input,
  FormMultiInput: FormMulti@slot{{ModuleName}}Input,
} = createModuleInput(@slot{{ModuleName}}ModuleConfig);