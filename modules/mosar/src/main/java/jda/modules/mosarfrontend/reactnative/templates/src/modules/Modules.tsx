import {createModuleComponents} from '../base/creators/createModuleComponents';

@loop{1}[[import {@slot{{module_name}}} from '../data_types/@slot{{module_name}}';
]]loop{1}@

@loop{2}[[import {
  @slot{{module_name}}FormConfig,
  @slot{{module_name}}ListConfig,
  @slot{{module_name}}ModuleConfig,
} from './@slot{{module_folder}}/config';
]]loop{2}@

@loop{3}[[
export const {
  Module: @slot{{module_name}}Module,
  List: @slot{{module_name}}List,
  ListItem: @slot{{module_name}}ListItem,
  Form: @slot{{module_name}}Form,
} = createModuleComponents<@slot{{module_name}}>(
  @slot{{module_name}}ModuleConfig,
  @slot{{module_name}}ListConfig,
  @slot{{module_name}}FormConfig,
);
]]loop{3}@
