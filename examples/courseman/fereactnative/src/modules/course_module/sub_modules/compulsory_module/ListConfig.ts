import {IJDAListConfig} from '../../../../base/creators/createListComponents';
import {CompulsoryModule} from '../../../../data_types/CompulsoryModule';
export const CompulsoryModuleListConfig: IJDAListConfig<CompulsoryModule> = {
  listItemProps: {
    icon: 'person-outline',
    title: (compulsoryModule) =>
      ` ${compulsoryModule.id} | ${compulsoryModule.code} | ${compulsoryModule.name} | ${compulsoryModule.semester} | ${compulsoryModule.credits} |`,
  },
  listProps: {},
};
