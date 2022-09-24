import {Button, Icon, Text, useTheme} from '@ui-kitten/components';
import * as React from 'react';
import {StyleSheet, View} from 'react-native';
import {IJDAModuleMultiInputControllerProps} from '../../../../controllers/jda_form_controllers/jda_form_input_controller/withModuleMultiInputController';
import {JDAButtonInput} from '../JDAButtonInput';
import ModuleSelect, {ModuleSelectRef} from './ModuleSelect';

export interface IJDAModuleMultiInputProps<T>
  extends IJDAModuleMultiInputControllerProps<T> {
  renderOption: (t?: T) => string;
}

export function JDAModuleMultiInput<T>(props: IJDAModuleMultiInputProps<T>) {
  const theme = useTheme();
  const ref = React.useRef<ModuleSelectRef>();
  const JDAModuleSelect = ModuleSelect<T>();
  return (
    <>
      {props.label && (
        <Text style={[styles.label, {color: theme['color-basic-600']}]}>
          {props.label}
        </Text>
      )}

      {props.values?.map((item, index) => {
        console.log('Item in Multiinput', item);

        return (
          <View style={styles.row} key={index}>
            <View style={styles.expanded}>
              <JDAButtonInput
                value={props.renderOption(item)}
                onPress={() => props.onShowDetail?.(item)}
              />
            </View>
            {!props.disabled && (
              <>
                <Button
                  style={styles.delete_btn}
                  size={'medium'}
                  appearance={'ghost'}
                  status={'basic'}
                  accessoryLeft={<Icon name="edit-2-outline" />}
                  onPress={() => props.onEdit?.(item)}
                />
                <Button
                  style={styles.delete_btn}
                  size={'medium'}
                  appearance={'ghost'}
                  status="danger"
                  accessoryLeft={<Icon name="close-circle-outline" />}
                  onPress={() => props.onRemove(item)}
                />
              </>
            )}
          </View>
        );
      })}

      {!props.disabled && (
        <Button
          size={'small'}
          onPress={() => ref.current?.open()}
          appearance="outline"
          status={'basic'}
        >
          {`+ Add ${props.label.toLowerCase()}`}
        </Button>
      )}
      <JDAModuleSelect
        ref={ref as any}
        options={props.options}
        renderOption={props.renderOption}
        onChange={(e) => (e ? props.onAppend(e) : {})}
        onCreate={props.onCreate}
        onSearch={props.onSearch}
      />
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
