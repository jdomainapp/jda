import {
  createEnumInput,
  createFormDataInput,
} from '../base/creators/createInputComponents';
import {JDADateInput} from '../base/views/jda_form/form_inputs/JDADateInput';
import {JDANumberInput} from '../base/views/jda_form/form_inputs/JDANumberInput';
import {JDAStringInput} from '../base/views/jda_form/form_inputs/JDAStringInput';

import {CourseModuleType} from '../data_types/enums/CourseModuleType';
import {Gender} from '../data_types/enums/Gender';

// Basic input components
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

// Enums input components

export const {
  Input: CourseModuleTypeInput,
  FormInput: FormCourseModuleTypeInput,
  FormMultiInput: FormMultiCourseModuleTypeInput,
} = createEnumInput(CourseModuleType);

export const {
  Input: GenderInput,
  FormInput: FormGenderInput,
  FormMultiInput: FormMultiGenderInput,
} = createEnumInput(Gender);
