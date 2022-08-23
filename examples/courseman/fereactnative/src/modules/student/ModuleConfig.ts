import {IJDAModuleConfig} from '../../base/controllers/jda_module_controller/withModuleController';
import {Modules} from '../../data_types/enums/Modules';
import {Student} from '../../data_types/Student';

export const StudentModuleConfig: IJDAModuleConfig<Student> = {
  primaryKey: 'id',
  route: Modules.Student,
  apiResource: 'students',
  moduleName: 'Students',
  fieldLabel: {
    id: 'Student ID',
    name: 'Full Name',
    gender: 'Gender',
    dob: 'Date of birth',
    address: 'Current Address',
    email: 'Email',
    studentClass: 'Student class',
    enrolments: 'Course Enrolments',
  },
  quickRender: (student) =>
    student
      ? ` ${student.id} | ${student.name} | ${student.gender} | ${student.dob} | ${student.email} |`
      : '',
  apiConfig: {
    toPOST: (student) => {
      return {
        ...student,
        addressId: student.address?.id,
        studentClassId: student.studentClass?.id,
      };
    },
  },
};
