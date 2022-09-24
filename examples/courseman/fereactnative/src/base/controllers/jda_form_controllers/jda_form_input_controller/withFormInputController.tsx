import React, {ComponentProps, ComponentType} from 'react';
import {
  Controller,
  ControllerFieldState,
  Path,
  useFormContext,
  UseFormStateReturn,
} from 'react-hook-form';
import {IJDAInput} from '.';
import {Modules} from '../../../../data_types/enums/Modules';
import {JDAFormMode} from '../withFormController';

export interface IJDAFormInputAPI<T> extends IJDAInput<T> {
  fieldState: ControllerFieldState;
  formState: UseFormStateReturn<T>;
}

export interface IJDAInputOptions
  extends Pick<ComponentProps<typeof Controller>, 'rules'> {
  hideInMode?: JDAFormMode[];
  module?: Modules; // only use for ObjectInput
  disabled?: boolean;
}

export interface IJDAFormInputControllerProps<T>
  extends IJDAFormInputAPI<T>,
    IJDAInputOptions {
  name: Path<T>;
  label: string;
}

export function withJDAFormInputController<
  T,
  P extends IJDAFormInputControllerProps<T>,
>(
  Component: ComponentType<P>,
  componentProps?: Omit<P, keyof IJDAFormInputControllerProps<T>>,
) {
  return (props: Omit<P, keyof IJDAFormInputAPI<T>>) => {
    const {control} = useFormContext<T>();
    // console.log('Rules:', props.rules);

    return (
      <Controller
        name={props.name}
        control={control}
        rules={props.rules}
        render={(item) => (
          <Component
            {...componentProps}
            {...(props as P)}
            {...item}
            value={item.field.value}
            onChange={(value: T) => item.field.onChange(value)}
          />
        )}
      />
    );
  };
}

//Export componentType
class TypeUltil<T, P extends IJDAFormInputControllerProps<T>> {
  //TODO if you change parammeter of withJDAListController function, you must change parameters of controlled function below
  controlled = (Component: ComponentType<P>) =>
    withJDAFormInputController<T, P>(Component);
}

export type JDAControlledFormInputComponent<
  T,
  P extends IJDAFormInputControllerProps<T>,
> = ReturnType<TypeUltil<T, P>['controlled']>;
