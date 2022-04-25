import {
  Button,
  Text,
  ThemedComponentProps,
  withStyles,
} from '@ui-kitten/components';
import * as React from 'react';
import {IJDAInput} from '.';

export interface IJDAButtonInputProps
  extends IJDAInput<string>,
    ThemedComponentProps {
  onPress?: () => void;
}

export function MyButtonInput(props: IJDAButtonInputProps) {
  return (
    <>
      {props.label && (
        <Text style={props.eva?.style ? props.eva.style.label : {}}>
          {props.label}
        </Text>
      )}
      <Button
        style={props.eva?.style ? props.eva.style.buttonLikeInput : {}}
        appearance="outline"
        status={'basic'}
        disabled={props.disabled}
        onPress={props.onPress}>
        {props.value}
      </Button>
    </>
  );
}

export const JDAButtonInput = withStyles(MyButtonInput, theme => ({
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
