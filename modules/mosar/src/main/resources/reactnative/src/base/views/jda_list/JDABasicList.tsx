import {Button, Divider, Icon, List} from '@ui-kitten/components';
import * as React from 'react';
import {Image, StyleSheet, View} from 'react-native';
// import {
//   DefaultListAction,
//   DefaultListItemAction,
//   IJDAActionMap,
// } from '../../controllers/jda_list_controllers/contexts/ListActionContext';
import {IJDAListControllerProps} from '../../controllers/jda_list_controllers/hocs/withListController';
export interface IJDABasicListProps<T> extends IJDAListControllerProps<T> {}

export default function JDABasicList<T>(props: IJDABasicListProps<T>) {
  return (
    <View>
      <List
        ListHeaderComponent={
          <Button
            size={'small'}
            accessoryLeft={<Icon name="plus" />}
            style={styles.fab}
            onPress={props.onAddItem}>
            Add
          </Button>
        }
        refreshing={props.loading}
        data={props.itemComponents}
        onRefresh={props.onRefresh}
        ListEmptyComponent={
          <Image
            style={styles.emptyImage}
            source={require('./nodata-found.png')}
          />
        }
        ItemSeparatorComponent={Divider}
        renderItem={({item}) => item}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  fab: {
    margin: 5,
    alignSelf: 'flex-end',
  },
  emptyImage: {
    width: 200,
    height: 200,
    alignSelf: 'center',
  },
});
