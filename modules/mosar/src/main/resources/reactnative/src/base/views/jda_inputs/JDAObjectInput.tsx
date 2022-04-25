import {Icon, List, ListItem} from '@ui-kitten/components';
import * as React from 'react';
import {useEffect} from 'react';
import RBSheet from 'react-native-raw-bottom-sheet';
import {IJDAInput} from '.';
import useDebounce from '../../common_hooks/useDebounce';
import {useAPI} from '../../controllers/jda_apis/useAPI';
import {JDAButtonInput} from './JDAButtonInput';
import {JDAStringInput} from './JDAStringInput';

export interface IJDAObjectInputProps<T> extends IJDAInput<T> {
  apiResource: string;
  renderOption: (option?: T) => string;
}

export function JDAObjectInput<T>(props: IJDAObjectInputProps<T>) {
  const ref = React.useRef<RBSheet>();
  const api = useAPI<T>(props.apiResource);
  const [options, setOptions] = React.useState<T[]>([]);
  const [keyword, setKeyword] = React.useState<string | undefined>('');
  const searchValue = useDebounce<string | undefined>(keyword, 500);
  const search = React.useCallback(async () => {
    const address = await api.getByPage(0);
    if (address.success && address.payload.content) {
      setOptions(address.payload.content);
    }
  }, [api]);

  useEffect(() => {
    search();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchValue]);
  return (
    <>
      <JDAButtonInput
        disabled={props.disabled}
        onPress={() => ref.current?.open()}
        label={props.label}
        value={
          props.renderOption
            ? props.renderOption(props.value)
            : String(props.value)
        }
      />
      <RBSheet
        ref={ref as any}
        // height={300}
        openDuration={250}>
        <JDAStringInput
          value={keyword}
          onChange={setKeyword}
          InputProps={{
            accessoryLeft: p => <Icon {...p} name="search" />,
            placeholder: `Search for ${props.apiResource}`,
            style: {margin: 10},
          }}
        />
        <List
          data={options}
          renderItem={({item}) => (
            <ListItem
              onPress={() => {
                if (props.onChange) {
                  props.onChange(item);
                }
                ref.current?.close();
              }}
              title={props.renderOption(item)}
            />
          )}
        />
      </RBSheet>
    </>
  );
}
