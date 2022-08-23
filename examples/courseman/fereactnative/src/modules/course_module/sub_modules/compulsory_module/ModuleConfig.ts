import {IJDAModuleConfig} from '../../../../base/controllers/jda_module_controller/withModuleController';
import {Modules} from '../../../../data_types/enums/Modules';
import {CompulsoryModule} from '../../../../data_types/CompulsoryModule';
import {CourseModuleModuleConfig} from '../../ModuleConfig';

export const CompulsoryModuleModuleConfig: IJDAModuleConfig<CompulsoryModule> =
  {
    primaryKey: 'id',
    route: Modules.CourseModule,
    apiResource: 'course-modules',
    moduleName: 'Course Modules',
    fieldLabel: {
      ...CourseModuleModuleConfig.fieldLabel,
    },
    quickRender: (compulsoryModule) =>
      compulsoryModule
        ? ` ${compulsoryModule.id} | ${compulsoryModule.code} | ${compulsoryModule.name} | ${compulsoryModule.semester} | ${compulsoryModule.credits} |`
        : '',
    apiConfig: {
      toPOST: (compulsoryModule) => {
        return {
          ...compulsoryModule,
        };
      },
    },
  };
