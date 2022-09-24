import {
  Button,
  Text,
  ThemedComponentProps,
  withStyles,
} from '@ui-kitten/components';
import * as React from 'react';
import {ReactElement} from 'react';
import {StyleSheet, View} from 'react-native';
import {IJDAInput} from '../../../controllers/jda_form_controllers/jda_form_input_controller';

export interface IJDAButtonInputProps
  extends IJDAInput<string>,
    ThemedComponentProps {
  onPress?: () => void;
  accessoryRight?: ReactElement;
}

export function MyButtonInput(props: IJDAButtonInputProps) {
  return (
    <>
      {props.label && (
        <Text style={props.eva?.style ? props.eva.style.label : {}}>
          {props.label}
        </Text>
      )}
      <View style={styles.row}>
        <View style={styles.expanded}>
          <Button
            size={'medium'}
            status={props.error ? 'danger' : 'basic'}
            style={props.eva?.style ? props.eva.style.buttonLikeInput : {}}
            appearance="outline"
            onPress={props.onPress}
          >
            {props.value}
          </Button>
        </View>
        {props.accessoryRight}
      </View>
      {props.error && (
        <Text category={'c2'} status="danger">
          {props.error}
        </Text>
      )}
    </>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  expanded: {
    flex: 1,
  },
});

export const JDAButtonInput = withStyles(MyButtonInput, (theme) => ({
  buttonLikeInput: {
    justifyContent: 'flex-start',
    fontWeight: 'normal',
    margin: 0,
  },
  label: {
    fontSize: 12,
    color: theme['color-basic-600'],
    fontWeight: 'bold',
  },
}));
