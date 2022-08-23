import {IJDAModuleConfig} from '../../base/controllers/jda_module_controller/withModuleController';
import {Modules} from '../../data_types/enums/Modules';
import {StudentClass} from '../../data_types/StudentClass';

export const StudentClassModuleConfig: IJDAModuleConfig<StudentClass> = {
  primaryKey: 'id',
  route: Modules.StudentClass,
  apiResource: 'student-classes',
  moduleName: 'Student Classes',
  fieldLabel: {
    id: 'Id',
    name: 'Name',
    students: 'Students',
  },
  quickRender: (studentClass) =>
    studentClass ? ` ${studentClass.name} |` : '',
  apiConfig: {
    toPOST: (studentClass) => {
      return {
        ...studentClass,
      };
    },
  },
};
