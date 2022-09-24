import {IJDAListConfig} from '../../../../base/creators/createListComponents';
import {ElectiveModule} from '../../../../data_types/ElectiveModule';
export const ElectiveModuleListConfig: IJDAListConfig<ElectiveModule> = {
  listItemProps: {
    icon: 'person-outline',
    title: (electiveModule) =>
      ` ${electiveModule.id} | ${electiveModule.code} | ${electiveModule.name} | ${electiveModule.semester} | ${electiveModule.credits} |`,
  },
  listProps: {},
};
