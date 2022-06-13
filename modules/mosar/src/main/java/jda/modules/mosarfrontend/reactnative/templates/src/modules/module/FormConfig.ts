import { IJDAFormConfig } from "../../base/controllers/jda_form_controllers/withFormController";
import {@slot{{ModuleName}}} from '../../data_types/@slot{{ModuleName}}';

@if{BasicFormInputGen}((import {@loop{importInputs}[[
  Form@slot{{FieldType}}Input,]]loop{importInputs}@
} from '../FormInputs';))if{BasicFormInputGen}@
@if{ModuleFormInputGen}((@loop{importDomainInput}[[
import { Form@slot{{DomainName}}Input } from "../@slot{{domainName}}/Input";]]loop{importDomainInput}@
))if{ModuleFormInputGen}@
export const @slot{{ModuleName}}FormConfig: IJDAFormConfig<@slot{{ModuleName}}> = {@loop{formConfig}[[
  @slot{{fieldName}}: @slot{{formType}},]]loop{formConfig}@
};