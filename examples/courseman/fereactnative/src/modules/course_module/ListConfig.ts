import {IJDAListConfig} from '../../base/creators/createListComponents';
import {CourseModule} from '../../data_types/CourseModule';
export const CourseModuleListConfig: IJDAListConfig<CourseModule> = {
  listItemProps: {
    icon: 'person-outline',
    title: (courseModule) =>
      ` ${courseModule.id} | ${courseModule.code} | ${courseModule.name} | ${courseModule.semester} | ${courseModule.credits} |`,
  },
  listProps: {},
};
