import {IJDAModuleConfig} from '../../../../base/controllers/jda_module_controller/withModuleController';
import {Modules} from '../../../../data_types/enums/Modules';
import {ElectiveModule} from '../../../../data_types/ElectiveModule';
import {CourseModuleModuleConfig} from '../../ModuleConfig';

export const ElectiveModuleModuleConfig: IJDAModuleConfig<ElectiveModule> = {
  primaryKey: 'id',
  route: Modules.CourseModule,
  apiResource: 'course-modules',
  moduleName: 'Course Modules',
  fieldLabel: {
    ...CourseModuleModuleConfig.fieldLabel,
    deptName: 'Dept. Name',
  },
  quickRender: (electiveModule) =>
    electiveModule
      ? ` ${electiveModule.id} | ${electiveModule.code} | ${electiveModule.name} | ${electiveModule.semester} | ${electiveModule.credits} |`
      : '',
  apiConfig: {
    toPOST: (electiveModule) => {
      return {
        ...electiveModule,
      };
    },
  },
};
