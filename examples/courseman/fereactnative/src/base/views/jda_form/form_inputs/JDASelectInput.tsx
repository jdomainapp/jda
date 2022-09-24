import {Divider, Icon, List, ListItem} from '@ui-kitten/components';
import * as React from 'react';
import RBSheet from 'react-native-raw-bottom-sheet';
import {IJDAInput} from '../../../controllers/jda_form_controllers/jda_form_input_controller';
import {JDAButtonInput} from './JDAButtonInput';

export interface IJDASelectInputProps<T> extends IJDAInput<T> {
  label?: string;
  values: T[];
  valueRender: (v?: T) => string;
}

export function JDASelectInput<T>(props: IJDASelectInputProps<T>) {
  const ref = React.useRef<RBSheet>();
  return (
    <>
      <JDAButtonInput
        {...(props as any)}
        onPress={() => {
          if (!props.disabled) ref.current?.open();
        }}
        value={props.valueRender(props.value)}
      />
      <RBSheet
        ref={ref as any}
        // height={300}
        openDuration={200}
      >
        <List
          data={props.values}
          ItemSeparatorComponent={Divider}
          renderItem={({item}) => (
            <ListItem
              onPress={() => {
                if (props.onChange) {
                  props.onChange(item);
                }
                ref.current?.close();
              }}
              accessoryLeft={(p) => <Icon {...p} name="droplet-outline" />}
              title={props.valueRender(item)}
            />
          )}
        />
      </RBSheet>
      {/* </View> */}
    </>
  );
}

// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//     backgroundColor: '#F5FCFF',
//   },
// });
