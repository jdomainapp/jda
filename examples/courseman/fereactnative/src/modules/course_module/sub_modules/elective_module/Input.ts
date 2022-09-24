import {createModuleInput} from '../../../../base/creators/createInputComponents';
import {ElectiveModuleModuleConfig} from './ModuleConfig';

export const {
  Input: ElectiveModuleInput,
  FormInput: FormElectiveModuleInput,
  FormMultiInput: FormMultiElectiveModuleInput,
} = createModuleInput(ElectiveModuleModuleConfig);
