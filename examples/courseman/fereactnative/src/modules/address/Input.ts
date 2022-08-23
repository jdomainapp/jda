import {createModuleInput} from '../../base/creators/createInputComponents';
import {AddressModuleConfig} from './ModuleConfig';
export const {
  Input: AddressInput,
  FormInput: FormAddressInput,
  FormMultiInput: FormMultiAddressInput,
} = createModuleInput(AddressModuleConfig);
