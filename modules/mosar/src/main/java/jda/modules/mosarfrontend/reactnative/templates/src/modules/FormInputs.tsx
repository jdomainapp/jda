import {
  createEnumInput,
  createFormDataInput,
} from '../base/creators/createInputComponents';
import {JDADateInput} from '../base/views/jda_inputs/JDADateInput';
import {JDANumberInput} from '../base/views/jda_inputs/JDANumberInput';
import {JDAStringInput} from '../base/views/jda_inputs/JDAStringInput';

@loop{importEnums}[[import {@slot{{enumName}}} from '../data_types/enums/@slot{{enumName}}';
]]loop{importEnums}@

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