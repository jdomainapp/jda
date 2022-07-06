import {IJDAModuleConfig} from '../../base/controllers/jda_module_controller/withModuleController';
import { Modules } from '../../data_types/enums/Modules';
import {@slot{{importDataType}}} from '../../data_types/@slot{{ModuleName}}';

export const @slot{{ModuleName}}ModuleConfig: IJDAModuleConfig<@slot{{importDataType}}> = {
  primaryKey: '@slot{{fieldID}}',
  route: Modules.@slot{{ModuleName}},
  apiResource: '@slot{{apiResource}}',
  moduleName: '@slot{{moduleTitle}}',
  fieldLabel: {@loop{fieldLabelConfig}[[
    @slot{{fieldName}}: '@slot{{fieldLabel}}',]]loop{fieldLabelConfig}@
  },
  quickRender: @slot{{moduleName}} => (@slot{{moduleName}} ? `@loop{quickRender}[[ \$\{@slot{{moduleAlias}}.@slot{{fieldName}}\} |]]loop{quickRender}@` : ''),
  apiConfig: {
    toPOST: @slot{{moduleName}} => {
      return {
        ...@slot{{moduleName}},@loop{toPOST}[[
        @slot{{linkedModule}}Id: @slot{{moduleName1}}.@slot{{linkedModule}}@slot{{linkedOptional}}.@slot{{linkedModuleIdField}},]]loop{toPOST}@
      };
    },
  },
};
