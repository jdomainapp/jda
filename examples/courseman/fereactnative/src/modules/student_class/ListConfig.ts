import {IJDAListConfig} from '../../base/creators/createListComponents';
import {StudentClass} from '../../data_types/StudentClass';
export const StudentClassListConfig: IJDAListConfig<StudentClass> = {
  listItemProps: {
    icon: 'person-outline',
    title: (studentClass) => ` ${studentClass.id} | ${studentClass.name} |`,
  },
  listProps: {},
};
