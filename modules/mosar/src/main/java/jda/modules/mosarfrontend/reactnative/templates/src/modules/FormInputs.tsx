import {
  createEnumInput,
  createFormDataInput,
  createModuleInput,
} from '../base/creators/createInputComponents';
import {JDADateInput} from '../base/views/jda_inputs/JDADateInput';
import {JDANumberInput} from '../base/views/jda_inputs/JDANumberInput';
import {JDAStringInput} from '../base/views/jda_inputs/JDAStringInput';

@loop{importEnums}[[import {@slot{{enumName}}} from '../data_types/@slot{{enumName}}';
]]loop{importEnums}@

@loop{importConfigs}[[import {@slot{{module_name}}ModuleConfig} from './@slot{{module_folder}}/ModuleConfig';
]]loop{importConfigs}@

// Basic form input components
export const {
  FormInput: FormStringInput,
  FormMultiInput: FormMultiStringInput,
} = createFormDataInput(JDAStringInput);

export const {FormInput: FormDateInput, FormMultiInput: FormMultiDateInput} =
  createFormDataInput(JDADateInput);

export const {
  FormInput: FormNumberInput,
  FormMultiInput: FormMultiNumberInput,
} = createFormDataInput(JDANumberInput);


// Enum form input components
@loop{exportEnumInputs}[[export const {
  Input: @slot{{enumName}}Input,
  FormInput: Form@slot{{enumName}}Input,
  FormMultiInput: FormMulti@slot{{enumName}}Input,
} = createEnumInput(@slot{{enumName}});
]]loop{exportEnumInputs}@

// Module form input components
@loop{exportModuleInputs}[[export const {
  Input: @slot{{moduleName}}Input,
  FormInput: Form@slot{{moduleName}}Input,
  FormMultiInput: FormMulti@slot{{moduleName}}Input,
} = createModuleInput(@slot{{moduleName}}ModuleConfig);
]]loop{exportModuleInputs}@
