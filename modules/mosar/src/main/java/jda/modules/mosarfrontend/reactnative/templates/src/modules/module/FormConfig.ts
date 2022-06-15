import { IJDAFormConfig } from "../../base/controllers/jda_form_controllers/withFormController";
import {@slot{{ModuleName}}} from '../../data_types/@slot{{ModuleName}}';
import { @slot{{ModuleName}}Type } from "../../data_types/enums/@slot{{ModuleName}}Type";
@loop{importSubModuleConfig}[[
import { @slot{{SubModuleName}}Form } from "./sub_modules/@slot{{submoduleFolder}}/Index";]]loop{importSubModuleConfig}@

import { ElectiveModuleForm } from "./sub_modules/elective_module/Index";
@if{importTypedFormItem}((import { ITypedFormItem } from "../../base/controllers/jda_form_controllers/withTypedFormController";))if{importTypedFormItem}@

@if{BasicFormInputGen}((import {@loop{importInputs}[[
  Form@slot{{FieldType}}Input,]]loop{importInputs}@
} from '../FormInputs';))if{BasicFormInputGen}@
@if{ModuleFormInputGen}((@loop{importDomainInput}[[
import { Form@slot{{DomainName}}Input } from "../@slot{{domainName}}/Input";]]loop{importDomainInput}@
))if{ModuleFormInputGen}@
export const @slot{{ModuleName}}FormConfig: IJDAFormConfig<@slot{{ModuleName}}> = {@loop{formConfig}[[
  @slot{{fieldName}}: @slot{{formType}},]]loop{formConfig}@
};

@if{FormList}((
export const @slot{{ModuleName}}FormList: ITypedFormItem[] = [
  @loop{formTypeItem}[[{
    type: @slot{{EnumType}}Type.@slot{{type}},
    formComponent: @slot{{SubModuleName}}Form
  },]]loop{formTypeItem}@
]
))if{FormList}@