import {
  IJDAFormConfig,
  JDAFormMode,
} from '../../base/controllers/jda_form_controllers/withFormController';
import {@slot{{ModuleName}}} from '../../data_types/@slot{{ModuleName}}';
@if{haveLinkedModule}((import {Modules} from '../../data_types/enums/Modules';))if{haveLinkedModule}@

@if{BasicFormInputGen}((import {@loop{importInputs}[[
  Form@slot{{FieldType}}Input,]]loop{importInputs}@
} from '../FormInputs';))if{BasicFormInputGen}@
@if{ModuleFormInputGen}((@loop{importDomainInput}[[
import { Form@slot{{InputType}}Input } from "../@slot{{linked_domain}}/Input";]]loop{importDomainInput}@
))if{ModuleFormInputGen}@
export const @slot{{ModuleName}}FormConfig: IJDAFormConfig<@slot{{ModuleName}}> = {@loop{formConfig}[[
  @slot{{fieldName}}: {
    component: @slot{{formType}},
    @slot{{options}}@slot{{props}}
  },]]loop{formConfig}@
};

