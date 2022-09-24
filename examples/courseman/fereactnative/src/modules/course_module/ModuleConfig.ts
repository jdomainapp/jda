import {IJDAModuleConfig} from '../../base/controllers/jda_module_controller/withModuleController';
import {Modules} from '../../data_types/enums/Modules';
import {CourseModule} from '../../data_types/CourseModule';

export const CourseModuleModuleConfig: IJDAModuleConfig<CourseModule> = {
  primaryKey: 'id',
  route: Modules.CourseModule,
  apiResource: 'course-modules',
  moduleName: 'Course Modules',
  fieldLabel: {
    id: 'Id',
    code: 'Code',
    name: 'Name',
    semester: 'Semester',
    credits: 'Credits',
  },
  quickRender: (courseModule) =>
    courseModule
      ? ` ${courseModule.id} | ${courseModule.code} | ${courseModule.name} | ${courseModule.semester} | ${courseModule.credits} |`
      : '',
  apiConfig: {
    toPOST: (courseModule) => {
      return {
        ...courseModule,
      };
    },
  },
};
