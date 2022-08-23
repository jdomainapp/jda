import {Button, Icon, List, ListItem} from '@ui-kitten/components';
import * as React from 'react';
import {StyleSheet, View} from 'react-native';
import {Divider} from 'react-native-paper';
import RBSheet from 'react-native-raw-bottom-sheet';
import useDebounce from '../../../../common_hooks/useDebounce';
import {IJDAModuleInput} from '../../../../controllers/jda_form_controllers/jda_form_input_controller';
import {JDAStringInput} from '../JDAStringInput';

export interface IModuleSelectProps<T> extends IJDAModuleInput<T> {
  renderOption: (option?: T) => string;
}

export interface ModuleSelectRef {
  open: () => void;
  close: () => void;
}
export default function ModuleSelect<T>() {
  return React.forwardRef<ModuleSelectRef, IModuleSelectProps<T>>(
    (props, reff) => {
      React.useImperativeHandle(reff, () => ({
        open: () => ref.current?.open(),
        close: () => ref.current?.close(),
      }));
      const ref = React.useRef<RBSheet>();
      const [keyword, setKeyword] = React.useState<string | undefined>('');
      const searchValue = useDebounce<string | undefined>(keyword, 500);
      React.useEffect(() => {
        if (searchValue) props.onSearch?.(searchValue);
      }, [props, searchValue]);
      return (
        <RBSheet
          ref={ref as any}
          // height={300}
          openDuration={250}
        >
          <View style={styles.bottomSheetContainer}>
            <JDAStringInput
              value={keyword}
              onChange={setKeyword}
              InputProps={{
                accessoryLeft: (p) => <Icon {...p} name="search" />,
                placeholder: 'Search',
                style: {margin: 10, flex: 1},
              }}
            />
            {props.onCreate && (
              <Button
                style={styles.createBtn}
                accessoryLeft={(p) => <Icon {...p} name="plus" />}
                size="tiny"
                onPress={() => {
                  props.onCreate?.();
                }}
              >
                Create
              </Button>
            )}
          </View>
          <List
            data={props.options}
            indicatorStyle="black"
            ItemSeparatorComponent={(p) => <Divider {...p} />}
            renderItem={({item}) => (
              <ListItem
                onPress={() => {
                  props.onChange?.(item);
                  ref.current?.close();
                }}
                title={props.renderOption(item)}
              />
            )}
          />
        </RBSheet>
      );
    },
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
  bottomSheetContainer: {
    display: 'flex',
    flexDirection: 'row',
  },
  createBtn: {
    marginVertical: 10,
    marginRight: 10,
  },
  button: {
    margin: 0,
    padding: 0,
    width: 10,
  },
});
