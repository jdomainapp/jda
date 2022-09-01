import React, {
  ComponentType,
  forwardRef,
  useCallback,
  useEffect,
  useImperativeHandle,
  useState,
} from 'react';
import {FormProvider, useForm} from 'react-hook-form';
import {Modules} from '../../../data_types/enums/Modules';
import {IJDAModuleConfig} from '../jda_module_controller/withModuleController';
import {
  IJDAFormInputControllerProps,
  IJDAInputOptions,
  JDAControlledFormInputComponent,
} from './jda_form_input_controller/withFormInputController';
import {
  IJDAFormMultiInputControllerProps,
  JDAControlledFormMultiInputComponent,
} from './jda_form_input_controller/withFormMultiInputController';
import {
  IJDAModuleInputControllerProps,
  JDAControlledModuleInputComponent,
} from './jda_form_input_controller/withModuleInputController';
import {IJDAModuleMultiInputControllerProps} from './jda_form_input_controller/withModuleMultiInputController';

export enum JDAFormMode {
  CREATE,
  EDIT,
  READ_ONLY,
}
export interface IJDAFormRef<T> {
  setMode: (mode: JDAFormMode) => void;
  setLoading: (v: boolean) => void;
  setFormValue: (value?: T) => void;
}

export interface IJDAFormAPI {
  submit: () => void;
  cancel?: () => void;
  loading: boolean;
  formInputs: React.ReactNode[];
}

type InputComponent<T> =
  | JDAControlledFormInputComponent<T, any>
  | JDAControlledModuleInputComponent<T, any>
  | JDAControlledFormMultiInputComponent<T, any>;

type InputComponentProps<T> =
  | IJDAFormInputControllerProps<T>
  | IJDAModuleInputControllerProps<T>
  | IJDAModuleMultiInputControllerProps<T>
  | IJDAFormMultiInputControllerProps<T>;

export type IJDAFormConfig<T> = Partial<
  Record<
    keyof T,
    {
      component: InputComponent<T>;
      options?: IJDAInputOptions;
      props?: Partial<InputComponentProps<T[keyof T]>>;
    }
  >
>;

export interface IJDAFormControlerProps<T> extends IJDAFormAPI {
  onSubmit: (value: T) => void;
  onCancel?: () => void;
  mode: JDAFormMode;
  hiddenFields?: (keyof T)[];
  hideModuleInputs?: Modules[];
  initValue?: T;
}
export function withJDAFormControler<
  T,
  P extends IJDAFormControlerProps<T>,
  SubT = T,
>(
  Component: ComponentType<P>,
  formConfig: IJDAFormConfig<T>,
  moduleConfig: IJDAModuleConfig<T, SubT>,
) {
  return forwardRef<IJDAFormRef<T>, Omit<P, keyof IJDAFormAPI>>(
    (props, ref) => {
      const form = useForm<T>({
        reValidateMode: 'onChange',
        mode: 'onSubmit',
      });
      const [mode, setMode] = useState<JDAFormMode>(props.mode);
      const [loading, setLoading] = useState(false);
      // console.log('Hide module input', props.hideModuleInputs);

      useEffect(() => {
        if (props.initValue) form.reset(props.initValue as any);
      }, [form, props.initValue]);

      const setFormValue = useCallback(
        (value?: T) => {
          if (value) {
            form.reset(value as any);
          }
        },
        [form],
      );

      useImperativeHandle(ref, () => ({
        setFormValue,
        setLoading,
        setMode,
      }));

      const handleSubmit = useCallback(
        (data: T) => {
          props.onSubmit(data);
        },
        [props],
      );

      const checkDisabled = useCallback(
        (key: keyof T) => {
          if (
            mode === JDAFormMode.READ_ONLY ||
            String(key) === String(moduleConfig.primaryKey)
          ) {
            return true;
          } else {
            return false;
          }
        },
        [mode],
      );

      const formInputs = Object.keys(formConfig)
        .map((key) => {
          const config = formConfig[key as keyof T];
          if (props.hiddenFields?.includes(key as keyof T)) return null;
          // console.log(`config option for ${key}: `, config?.options);
          if (
            config?.options?.module &&
            props.hideModuleInputs?.includes(config.options.module)
          )
            return null;
          const InputView: JDAControlledFormInputComponent<T, any> =
            config?.component as any;
          return config?.options?.hideInMode?.includes(mode) ? undefined : (
            <InputView
              name={key}
              disabled={checkDisabled(key as keyof T)}
              {...config?.options}
              {...config?.props}
              label={moduleConfig.fieldLabel[key as keyof T]}
            />
          );
        })
        .filter((e) => e);
      return (
        <FormProvider {...form}>
          <Component
            {...(props as P)}
            loading={loading}
            mode={mode}
            formInputs={formInputs}
            submit={form.handleSubmit((d) => handleSubmit(d as T))}
            cancel={props.onCancel}
          />
        </FormProvider>
      );
    },
  );
}

//Export componentType
class TypeUltil<T, P extends IJDAFormControlerProps<T>> {
  //TODO if you change parammeter of withJDAListController function, you must change parameters of controlled function below
  controlled = (
    Component: ComponentType<P>,
    formConfig: IJDAFormConfig<T>,
    moduleConfig: IJDAModuleConfig<T>,
  ) => withJDAFormControler<T, P>(Component, formConfig, moduleConfig);
}

export type JDAControlledFormComponent<
  T,
  P extends IJDAFormControlerProps<T>,
> = ReturnType<TypeUltil<T, P>['controlled']>;
