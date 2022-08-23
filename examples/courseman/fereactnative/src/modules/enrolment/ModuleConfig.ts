import {IJDAModuleConfig} from '../../base/controllers/jda_module_controller/withModuleController';
import {Modules} from '../../data_types/enums/Modules';
import {Enrolment} from '../../data_types/Enrolment';

export const EnrolmentModuleConfig: IJDAModuleConfig<Enrolment> = {
  primaryKey: 'id',
  route: Modules.Enrolment,
  apiResource: 'enrolments',
  moduleName: 'Enrolments',
  fieldLabel: {
    id: 'Id',
    student: 'Student',
    courseModule: 'Course Module',
    internalMark: 'Internal Mark',
    examMark: 'Exam Mark',
    finalGrade: 'Final Grade',
    finalMark: 'Finalmark',
  },
  quickRender: (enrolment) => (enrolment ? ` ${enrolment.id} |` : ''),
  apiConfig: {
    toPOST: (enrolment) => {
      return {
        ...enrolment,
        studentId: enrolment.student.id,
        courseModuleId: enrolment.courseModule.id,
      };
    },
  },
};
