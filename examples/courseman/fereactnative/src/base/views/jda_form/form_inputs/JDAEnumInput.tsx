import * as React from 'react';
import {IJDAInput} from '../../../controllers/jda_form_controllers/jda_form_input_controller';
import {enum2Array} from '../../../utils/enum2Array';
import {JDASelectInput} from './JDASelectInput';

export interface IEnumInputProps<T> extends IJDAInput<T> {
  enumObject: any;
}

export function JDAEnumInput<T>(props: IEnumInputProps<T>) {
  return (
    <JDASelectInput<T>
      {...(props as any)}
      values={enum2Array(props.enumObject)}
      onChange={props.onChange}
      valueRender={(v) => (v ? String(v) : '')}
    />
  );
}
