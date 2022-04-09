import * as React from 'react';
import {
  ArrayPath,
  Controller,
  useFieldArray,
  UseFieldArrayReturn,
  useFormContext,
} from 'react-hook-form';
import {IJDAInput} from '../../views/jda_inputs';
interface IJDAFormMultiInputAPI<T> extends UseFieldArrayReturn<T> {
  formItems: React.ReactNode[];
}
export interface IJDAFormMultiInputControllerProps<T>
  extends IJDAFormMultiInputAPI<T> {
  name: ArrayPath<T>;
  label: string;
  disabled?: boolean;
}

export function withJDAFormMultiInputController<
  T,
  Props extends IJDAFormMultiInputControllerProps<T>,
  SingleInputProps extends IJDAInput<T>,
>(
  Component: React.ComponentType<Props>,
  SingleInputComponent: React.ComponentType<SingleInputProps>,
) {
  return (props: Omit<Props, keyof IJDAFormMultiInputAPI<T>>) => {
    const {control} = useFormContext<T>();
    const multiInputControl = useFieldArray<T>({control, name: props.name});
    const formItems = multiInputControl.fields.map((field, index) => (
      <Controller
        key={field.id}
        control={control}
        name={`${props.name}.${index}` as any}
        render={({field: itemInput}) => (
          <SingleInputComponent
            {...({} as any)}
            disabled={props.disabled}
            value={itemInput.value}
            onChange={itemInput.onChange}
          />
        )}
      />
    ));

    return (
      <Component
        {...(props as Props)}
        {...multiInputControl}
        formItems={formItems}
      />
    );
  };
}

//Export componentType
class TypeUltil<
  T,
  Props extends IJDAFormMultiInputControllerProps<T>,
  SingleInputProps extends IJDAInput<T>,
> {
  //TODO if you change parammeter of withJDAListController function, you must change parameters of controlled function below

  controlled = (
    Component: React.ComponentType<Props>,
    SingleInputComponent: React.ComponentType<SingleInputProps>,
  ) =>
    withJDAFormMultiInputController<T, Props, SingleInputProps>(
      Component,
      SingleInputComponent,
    );
}

export type JDAControlledFormMultiInputComponent<
  T,
  Props extends IJDAFormMultiInputControllerProps<T>,
  SingleInputProps extends IJDAInput<T>,
> = ReturnType<TypeUltil<T, Props, SingleInputProps>['controlled']>;
