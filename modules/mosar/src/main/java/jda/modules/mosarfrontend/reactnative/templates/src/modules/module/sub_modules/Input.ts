import { createModuleInput } from "../../../../base/creators/createInputComponents";
import { @slot{{SubModuleName}}ModuleConfig } from "./ModuleConfig";

export const {
  Input: @slot{{SubModuleName}}Input,
  FormInput: Form@slot{{SubModuleName}}Input,
  FormMultiInput: FormMulti@slot{{SubModuleName}}Input,
} = createModuleInput(@slot{{SubModuleName}}ModuleConfig);