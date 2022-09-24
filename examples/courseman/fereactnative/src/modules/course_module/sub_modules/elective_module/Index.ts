import {createModuleComponents} from '../../../../base/creators/createModuleComponents';
import {ElectiveModule} from '../../../../data_types/ElectiveModule';
import {ElectiveModuleFormConfig} from './FormConfig';
import {ElectiveModuleListConfig} from './ListConfig';
import {ElectiveModuleModuleConfig} from './ModuleConfig';

export const {
  Module: ElectiveModuleModule,
  List: ElectiveModuleList,
  ListItem: ElectiveModuleListItem,
  Form: ElectiveModuleForm,
} = createModuleComponents<ElectiveModule>(
  ElectiveModuleModuleConfig,
  ElectiveModuleListConfig,
  ElectiveModuleFormConfig,
);
