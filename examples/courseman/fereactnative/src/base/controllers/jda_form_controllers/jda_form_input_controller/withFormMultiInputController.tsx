import * as React from 'react';
import {
  ArrayPath,
  Controller,
  useFieldArray,
  UseFieldArrayReturn,
  useFormContext,
} from 'react-hook-form';
import {IJDAInput} from '.';
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
>(
  Component: React.ComponentType<Props>,
  SingleInputComponent: React.ComponentType<IJDAInput<T>>,
) {
  return (props: Omit<Props, keyof IJDAFormMultiInputAPI<T>>) => {
    const {control} = useFormContext<T>();
    const multiInputControl = useFieldArray<T>({control, name: props.name});
    const formItems = multiInputControl.fields.map((field, index) => (
      <Controller
        key={field.id + index}
        control={control}
        name={`${props.name}.${index}` as any}
        render={({field: itemInput}) => (
          <SingleInputComponent
            disabled={props.disabled}
            value={itemInput.value as any}
            {...(props as any)}
            label={undefined}
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

export type JDAControlledFormMultiInputComponent<
  T,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  Props extends IJDAFormMultiInputControllerProps<T>,
  // eslint-disable-next-line prettier/prettier, no-undef
> = (props: Omit<Props, keyof IJDAFormMultiInputAPI<T>>) => JSX.Element;
