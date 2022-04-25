import {Divider, Icon, List, ListItem} from '@ui-kitten/components';
import * as React from 'react';
import RBSheet from 'react-native-raw-bottom-sheet';
import {IJDAInput} from '.';
import {JDAButtonInput} from './JDAButtonInput';

export interface IJDASelectInputProps<T> extends IJDAInput<T> {
  label?: string;
  values: T[];
  valueRender: (v: T) => string;
}

export function JDASelectInput<T>(props: IJDASelectInputProps<T>) {
  const ref = React.useRef<RBSheet>();
  return (
    <>
      <JDAButtonInput
        disabled={props.disabled}
        onPress={() => ref.current?.open()}
        label={props.label}
        value={props.valueRender(props.value || props.values[0])}
      />
      <RBSheet
        ref={ref as any}
        // height={300}
        openDuration={250}>
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
              accessoryLeft={p => <Icon {...p} name="droplet-outline" />}
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
