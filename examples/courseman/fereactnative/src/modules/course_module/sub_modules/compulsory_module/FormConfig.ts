import {IJDAFormConfig} from '../../../../base/controllers/jda_form_controllers/withFormController';
import {CompulsoryModule} from '../../../../data_types/CompulsoryModule';
import {CourseModuleFormConfig} from '../../FormConfig';

export const CompulsoryModuleFormConfig: IJDAFormConfig<CompulsoryModule> = {
  ...CourseModuleFormConfig,
};
