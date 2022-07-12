import {createModuleComponents} from '../base/creators/createModuleComponents';

@loop{importDomainTypes}[[import {@slot{{requiredInterface}}} from '../data_types/@slot{{module_name}}';
]]loop{importDomainTypes}@

@loop{importDomainConfig}[[import {
  @slot{{module_name}}FormConfig,
  @slot{{module_name}}ListConfig,
  @slot{{module_name}}ModuleConfig,
} from './@slot{{module_folder}}/ModuleConfig';
]]loop{importDomainConfig}@

@loop{3}[[
export const {
  Module: @slot{{module_name}}Module,
  List: @slot{{module_name}}List,
  ListItem: @slot{{module_name}}ListItem,
  Form: @slot{{module_name}}Form,
} = createModuleComponents<@slot{{requiredInterface}}>(
  @slot{{module_name}}ModuleConfig,
  @slot{{module_name}}ListConfig,
  @slot{{module_name}}FormConfig,
);
]]loop{3}@
