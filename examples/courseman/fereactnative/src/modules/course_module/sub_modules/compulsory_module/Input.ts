import {createModuleInput} from '../../../../base/creators/createInputComponents';
import {CompulsoryModuleModuleConfig} from './ModuleConfig';

export const {
  Input: CompulsoryModuleInput,
  FormInput: FormCompulsoryModuleInput,
  FormMultiInput: FormMultiCompulsoryModuleInput,
} = createModuleInput(CompulsoryModuleModuleConfig);
