import {IJDAFormConfig} from '../controllers/jda_form_controllers/withFormController';import {  IJDAModuleConfig,  withModuleController,} from '../controllers/jda_module_controller/withModuleController';import {IJDABasicFormProps} from '../views/jda_form/JDABasicForm';import {IJDABasicListProps} from '../views/jda_list/JDABasicList';import {  IJDABasicModuleProps,  JDABasicModule,} from '../views/jda_module/JDABasicModule';import {createFormComponents} from './createFormComponents';import {createListComponents, IJDAListConfig} from './createListComponents';export function createModuleComponents<T, SubT = T>(  moduleConfig: IJDAModuleConfig<T, SubT>,  listConfig: IJDAListConfig<T>,  formConfig: IJDAFormConfig<T>,) {  const {Form} = createFormComponents<T, SubT>(moduleConfig, formConfig);  const {List, ListItem} = createListComponents<T, SubT>(    moduleConfig,    listConfig,  );  const Module = withModuleController<    T,    IJDABasicListProps<T>,    IJDABasicFormProps<T>,    IJDABasicModuleProps<T>,    SubT  >(JDABasicModule, List, Form, moduleConfig);  return {Module, Form, List, ListItem};}