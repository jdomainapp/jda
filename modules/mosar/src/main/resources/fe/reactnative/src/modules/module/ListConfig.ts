import { IJDAListConfig } from "../../base/creators/createListComponents";
import { @slot{{ModuleName}} } from "../../data_types/@slot{{ModuleName}}";
export const @slot{{ModuleName}}ListConfig: IJDAListConfig<@slot{{ModuleName}}> = {
  listItemProps: {
    icon: 'person-outline',
    title: @slot{{moduleName}} => `@loop{listTitle}[[ \$\{@slot{{moduleAlias}}.@slot{{fieldName}}\} |]]loop{listTitle}@`,
  },
  listProps: {},
};