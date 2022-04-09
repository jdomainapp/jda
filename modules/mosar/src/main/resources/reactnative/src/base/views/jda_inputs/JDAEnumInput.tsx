import * as React from 'react';
import {IJDAInput} from '.';
import {enum2Array} from '../../utils/enum2Array';
import {JDASelectInput} from './JDASelectInput';

export interface IEnumInputProps<T> extends IJDAInput<T> {
  enumObject: any;
}

export function JDAEnumInput<T>(props: IEnumInputProps<T>) {
  return (
    <JDASelectInput<T>
      values={enum2Array(props.enumObject)}
      onChange={props.onChange}
      value={props.value}
      label={props.label}
      disabled={props.disabled}
      valueRender={v => String(v)}
    />
  );
}
