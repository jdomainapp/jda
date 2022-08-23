import {Input} from '@ui-kitten/components';
import * as React from 'react';
import {IJDAInput} from '../../../controllers/jda_form_controllers/jda_form_input_controller';

export interface IJDANumberInputProps extends IJDAInput<number> {}

export function JDANumberInput(props: IJDANumberInputProps) {
  return (
    <Input
      value={props.value ? String(props.value) : undefined}
      label={props.label}
      status={props.error ? 'danger' : 'basic'}
      disabled={props.disabled}
      onChangeText={(t) => {
        props.onChange?.(props.disabled ? undefined : parseInt(t, 10));
      }}
      keyboardType="numeric"
      caption={props.error}
    />
  );
}
