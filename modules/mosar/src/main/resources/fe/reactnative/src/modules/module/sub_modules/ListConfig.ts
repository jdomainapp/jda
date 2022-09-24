import { IJDAListConfig } from "../../../../base/creators/createListComponents";
import { @slot{{SubModuleName}} } from "../../../../data_types/@slot{{SubModuleName}}";
export const @slot{{SubModuleName}}ListConfig: IJDAListConfig<@slot{{SubModuleName}}> = {
  listItemProps: {
    icon: 'person-outline',
    title: @slot{{subModuleName}} => ` ${@slot{{subModuleName}}.id} | ${@slot{{subModuleName}}.code} | ${@slot{{subModuleName}}.name} | ${@slot{{subModuleName}}.semester} | ${@slot{{subModuleName}}.credits} |`,
  },
  listProps: {},
};