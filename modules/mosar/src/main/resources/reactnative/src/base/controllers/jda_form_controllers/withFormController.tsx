import React, {ComponentType, useCallback, useEffect} from 'react';
import {FormProvider, useForm} from 'react-hook-form';
import {IJDAModuleConfig} from '../jda_module_controller/withModuleController';
import {JDAControlledFormInputComponent} from './withFormInputController';
import {JDAControlledFormMultiInputComponent} from './withFormMultiInputController';

export enum JDAFormMode {
  CREATE,
  EDIT,
  READ_ONLY,
}
export interface IJDAFormRef<T> {
  setMode: (mode: JDAFormMode) => void;
  setFormValue: (value: T) => void;
}

export interface IJDAFormAPI {
  submit: () => void;
  cancel?: () => void;
  formInputs: React.ReactNode[];
}

export type IJDAFormConfig<T> = Record<
  keyof T,
  | JDAControlledFormInputComponent<T, any>
  | JDAControlledFormMultiInputComponent<T, any, any>
>;

export interface IJDAFormControlerProps<T> extends IJDAFormAPI {
  onSubmit: (value: T) => void;
  onCancel?: () => void;
  mode: JDAFormMode;
  initValue?: T;
}

export function withJDAFormControler<T, P extends IJDAFormControlerProps<T>>(
  Component: ComponentType<P>,
  formConfig: IJDAFormConfig<T>,
  moduleConfig: IJDAModuleConfig<T>,
) {
  return (props: Omit<P, keyof IJDAFormAPI>) => {
    const form = useForm<T>();
    useEffect(() => {
      if (props.initValue) {
        form.reset(props.initValue as any);
      }
    }, [form, props.initValue]);

    const handleSubmit = useCallback(
      (data: T) => {
        console.log(data);
        props.onSubmit(data);
      },
      [props],
    );

    const checkDisabled = useCallback(
      (key: keyof T) => {
        if (
          props.mode === JDAFormMode.READ_ONLY ||
          String(key) === String(moduleConfig.primaryKey)
        ) {
          return true;
        } else {
          return false;
        }
      },
      [props.mode],
    );

    const formInputs = Object.keys(formConfig).map(key => {
      const InputView: JDAControlledFormInputComponent<T, any> =
        formConfig[key as keyof T];
      return (
        <InputView
          name={key}
          label={moduleConfig.fieldLabel[key as keyof T]}
          disabled={checkDisabled(key as keyof T)}
        />
      );
    });
    return (
      <FormProvider {...form}>
        <Component
          {...(props as P)}
          formInputs={formInputs}
          submit={form.handleSubmit(d => handleSubmit(d as T))}
          cancel={props.onCancel}
        />
      </FormProvider>
    );
  };
}

//Export componentType
class TypeUltil<T, P extends IJDAFormControlerProps<T>> {
  //TODO if you change parammeter of withJDAListController function, you must change parameters of controlled function below

  controlled = (
    Component: ComponentType<P>,
    formConfig: Record<keyof T, JDAControlledFormInputComponent<T, any>>,
    moduleConfig: IJDAModuleConfig<T>,
  ) => withJDAFormControler<T, P>(Component, formConfig, moduleConfig);
}

export type JDAControlledFormComponent<
  T,
  P extends IJDAFormControlerProps<T>,
> = ReturnType<TypeUltil<T, P>['controlled']>;
