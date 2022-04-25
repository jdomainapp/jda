import {Input} from '@ui-kitten/components';
import * as React from 'react';
import {IJDAInput} from '.';

export interface IJDANumberInputProps extends IJDAInput<number> {}

export function JDANumberInput(props: IJDANumberInputProps) {
  return (
    <Input
      disabled={props.disabled}
      value={props.value ? String(props.value) : undefined}
      label={props.label}
      onChangeText={t => {
        if (props.onChange) {
          props.onChange(parseInt(t, 10));
        }
      }}
      keyboardType="numeric"
    />
  );
}
