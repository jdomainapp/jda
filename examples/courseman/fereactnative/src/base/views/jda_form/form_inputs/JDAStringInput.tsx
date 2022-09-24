import {Input, InputProps} from '@ui-kitten/components';
import * as React from 'react';
import {IJDAInput} from '../../../controllers/jda_form_controllers/jda_form_input_controller';

export interface IJDAStringInputProps extends IJDAInput<string> {
  InputProps?: InputProps;
}

export function JDAStringInput(props: IJDAStringInputProps) {
  return (
    <Input
      {...props.InputProps}
      status={props.error ? 'danger' : 'basic'}
      caption={props.error}
      disabled={props.disabled}
      clearButtonMode="while-editing"
      value={props.value}
      label={props.label}
      onChangeText={(v) => {
        props.onChange?.(props.disabled ? undefined : v);
      }}
    />
  );
}
