import _ from 'lodash';
import * as React from 'react';
import {StyleSheet, View} from 'react-native';
import {JDAFormMode} from '../../controllers/jda_form_controllers/withFormController';
import {IJDAMultiFormControllerProps} from '../../controllers/jda_form_controllers/withMultiFormControler';
import {JDASelectInput} from '../jda_inputs/JDASelectInput';
export interface IJDABasicGenenricFormProps
  extends IJDAMultiFormControllerProps {}

export function JDABasicMultiForm(props: IJDABasicGenenricFormProps) {
  return (
    <View style={styles.container}>
      <View style={styles.select}>
        <JDASelectInput<string>
          disabled={props.mode === JDAFormMode.READ_ONLY}
          label="Type"
          values={props.formTypes}
          valueRender={v => _.upperFirst(v)}
          value={props.formType}
          onChange={props.onChangeFormType}
        />
      </View>
      {props.FormView}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginTop: 10,
  },
  select: {
    marginHorizontal: 10,
  },
});
