import { IJDAFormConfig } from "../../../../base/controllers/jda_form_controllers/withFormController";
import { @slot{{SubModuleName}} } from "../../../../data_types/@slot{{SubModuleName}}";
import { @slot{{ModuleName}}FormConfig } from "../../FormConfig";
@if{haveLinkedModule}((import {Modules} from '../../../../data_types/enums/Modules';))if{haveLinkedModule}@

@if{BasicFormInputGen}((import {@loop{importInputs}[[
  Form@slot{{FieldType}}Input,]]loop{importInputs}@
} from '../../../FormInputs';))if{BasicFormInputGen}@
@if{ModuleFormInputGen}((@loop{importDomainInput}[[
import { Form@slot{{DomainName}}Input } from "../../@slot{{domainName}}/Input";]]loop{importDomainInput}@
))if{ModuleFormInputGen}@

export const @slot{{SubModuleName}}FormConfig: IJDAFormConfig<@slot{{SubModuleName}}> = {
  ...@slot{{ModuleName}}FormConfig,@loop{formConfig}[[
    @slot{{fieldName}}: {
      component: @slot{{formType}},
      @slot{{options}}@slot{{props}}
    },]]loop{formConfig}@
};