import {Button, Icon, Text, useTheme} from '@ui-kitten/components';
import * as React from 'react';
import {StyleSheet, View} from 'react-native';
import {IJDAFormMultiInputControllerProps} from '../../../controllers/jda_form_controllers/jda_form_input_controller/withFormMultiInputController';

export interface IJDAFormMultiInputProps<T>
  extends IJDAFormMultiInputControllerProps<T> {}

export function JDAFormMultiInput<T>(props: IJDAFormMultiInputProps<T>) {
  const theme = useTheme();
  return (
    <>
      {props.label && (
        <Text style={[styles.label, {color: theme['color-basic-600']}]}>
          {props.label}
        </Text>
      )}

      {props.formItems.map((item, index) => (
        <View style={styles.row} key={index}>
          <View style={styles.expanded}>{item}</View>
          {!props.disabled && (
            <Button
              style={styles.delete_btn}
              size={'medium'}
              appearance={'ghost'}
              status="danger"
              accessoryLeft={<Icon name="close-circle-outline" />}
              onPress={() => props.remove(index)}
            />
          )}
        </View>
      ))}
      {!props.disabled && (
        <Button
          size={'small'}
          onPress={() => props.append({} as any)}
          appearance="outline"
          status={'basic'}
        >
          {`+ Add ${props.label.toLowerCase()}`}
        </Button>
      )}
    </>
  );
}

const styles = StyleSheet.create({
  label: {
    fontSize: 12,
    fontWeight: 'bold',
  },
  list: {},
  row: {
    flex: 1,
    flexDirection: 'row',
    marginVertical: 2,
  },
  expanded: {
    flex: 1,
    flexGrow: 1,
  },
  delete_btn: {
    padding: 0,
    width: 30,
  },
});
