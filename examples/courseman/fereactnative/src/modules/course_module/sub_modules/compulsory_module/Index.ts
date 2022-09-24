import {createModuleComponents} from '../../../../base/creators/createModuleComponents';
import {CompulsoryModule} from '../../../../data_types/CompulsoryModule';
import {CompulsoryModuleFormConfig} from './FormConfig';
import {CompulsoryModuleListConfig} from './ListConfig';
import {CompulsoryModuleModuleConfig} from './ModuleConfig';

export const {
  Module: CompulsoryModuleModule,
  List: CompulsoryModuleList,
  ListItem: CompulsoryModuleListItem,
  Form: CompulsoryModuleForm,
} = createModuleComponents<CompulsoryModule>(
  CompulsoryModuleModuleConfig,
  CompulsoryModuleListConfig,
  CompulsoryModuleFormConfig,
);
