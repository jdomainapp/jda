import {Button} from '@ui-kitten/components';
import * as React from 'react';
import {useMemo, useState} from 'react';
import {StyleSheet, View} from 'react-native';
import DatePicker from 'react-native-date-picker';
import RBSheet from 'react-native-raw-bottom-sheet';
import {IJDAInput} from '../../../controllers/jda_form_controllers/jda_form_input_controller';
import {JDAButtonInput} from './JDAButtonInput';
export interface IJDADateInputProps extends IJDAInput<Date> {}

export function JDADateInput(props: IJDADateInputProps) {
  const date = useMemo(() => {
    if (!props.value) {
      return undefined;
    }
    const value = new Date(props.value);
    return isNaN(value.getTime()) ? undefined : value;
  }, [props.value]);
  const ref = React.useRef<RBSheet>();
  const [tempDate, setTempDate] = useState<Date>();
  return (
    <>
      <JDAButtonInput
        {...(props as any)}
        value={date?.toLocaleDateString()}
        onPress={() => {
          if (!props.disabled) ref.current?.open();
        }}
      />
      <RBSheet ref={ref as any} openDuration={200}>
        <View style={styles.container}>
          <DatePicker
            mode="date"
            date={tempDate || new Date()}
            onDateChange={setTempDate}
          />
        </View>
        <Button
          style={styles.button}
          onPress={() => {
            ref.current?.close();
            if (props.onChange) props.onChange(tempDate);
          }}
        >
          OK
        </Button>
      </RBSheet>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
  },
  button: {
    margin: 10,
  },
});
