import { IJDAModuleConfig } from '../../../../base/controllers/jda_module_controller/withModuleController';
import { Modules } from '../../../../data_types/enums/Modules';
import { @slot{{SubModuleName}} } from '../../../../data_types/@slot{{SubModuleName}}';
import { @slot{{ModuleName}}ModuleConfig } from '../../ModuleConfig';

export const @slot{{SubModuleName}}ModuleConfig: IJDAModuleConfig<@slot{{SubModuleName}}> = {
  primaryKey: 'id',
  route: Modules.@slot{{ModuleName}},
  apiResource: 'course-modules',
  moduleName: 'Course Modules',
  fieldLabel: {
    ...@slot{{ModuleName}}ModuleConfig.fieldLabel,@loop{fieldLabelConfig}[[
    @slot{{fieldName}}: '@slot{{fieldLabel}}',]]loop{fieldLabelConfig}@
  },
  quickRender: @slot{{subModuleName}} => (@slot{{subModuleName}} ? ` ${@slot{{subModuleName}}.id} | ${@slot{{subModuleName}}.code} | ${@slot{{subModuleName}}.name} | ${@slot{{subModuleName}}.semester} | ${@slot{{subModuleName}}.credits} |` : ''),
  apiConfig: {
    toPOST: @slot{{subModuleName}} => {
      return {
        ...@slot{{subModuleName}},
      };
    },
  },
};