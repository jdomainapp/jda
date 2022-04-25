import {
  IJDAFormConfig,
  withJDAFormControler,
} from '../controllers/jda_form_controllers/withFormController';
import {IJDAModuleConfig} from '../controllers/jda_module_controller/withModuleController';
import JDABasicForm, {IJDABasicFormProps} from '../views/jda_form/JDABasicForm';

export function createFormComponents<T>(
  moduleConfig: IJDAModuleConfig<T>,
  formConfig: IJDAFormConfig<T>,
) {
  type FormProps = IJDABasicFormProps<T>;
  const Form = withJDAFormControler<T, FormProps>(
    JDABasicForm,
    formConfig,
    moduleConfig,
  );
  return {Form};
}
