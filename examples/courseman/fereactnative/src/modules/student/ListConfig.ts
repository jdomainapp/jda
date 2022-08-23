import {IJDAListConfig} from '../../base/creators/createListComponents';
import {Student} from '../../data_types/Student';
export const StudentListConfig: IJDAListConfig<Student> = {
  listItemProps: {
    icon: 'person-outline',
    title: (student) =>
      ` ${student.id} | ${student.name} | ${student.gender} | ${student.dob} | ${student.email} |`,
  },
  listProps: {},
};
