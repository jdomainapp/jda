import {IJDAFormConfig} from '../../base/controllers/jda_form_controllers/withFormController';
import {IJDAModuleConfig} from '../../base/controllers/jda_module_controller/withModuleController';
import {IJDAListConfig} from '../../base/creators/createListComponents';
import {@slot{{importDataType}}} from '../../data_types/@slot{{ModuleName}}';
import {@loop{importInputs}[[
  Form@slot{{FieldType}}Input,]]loop{importInputs}@
} from '../FormInputs';

export const @slot{{ModuleName}}ModuleConfig: IJDAModuleConfig<@slot{{importDataType}}> = {
  primaryKey: '@slot{{fieldID}}',
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

export const @slot{{ModuleName}}FormConfig: IJDAFormConfig<@slot{{ModuleName}}> = {@loop{formConfig}[[
  @slot{{fieldName}}: @slot{{formType}},]]loop{formConfig}@
};

export const @slot{{ModuleName}}ListConfig: IJDAListConfig<@slot{{ModuleName}}> = {
  listItemProps: {
    icon: 'person-outline',
    title: @slot{{moduleName}} => `@loop{listTitle}[[ \$\{@slot{{moduleAlias}}.@slot{{fieldName}}\} |]]loop{listTitle}@`,
  },
  listProps: {},
};