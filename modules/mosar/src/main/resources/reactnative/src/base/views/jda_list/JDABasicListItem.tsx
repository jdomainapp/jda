import {Button, Icon, ListItem} from '@ui-kitten/components';
import * as React from 'react';
import {StyleSheet} from 'react-native';
import {IJDAListItemControllerProps} from '../../controllers/jda_list_controllers/hocs/withListItemController';

export interface IJDABasicListItemProps<T>
  extends IJDAListItemControllerProps<T> {
  icon: any;
  title: (item: T) => string;
  subTitle?: (item: T) => string;
}
export function JDABasicListItem<T>(props: IJDABasicListItemProps<T>) {
  return (
    <ListItem
      accessoryLeft={p => <Icon {...p} name={props.icon} />}
      onPress={props.onShowDetail}
      title={props.title(props.item)}
      description={props.subTitle ? props.subTitle(props.item) : undefined}
      accessoryRight={_p => (
        <>
          <Button
            style={styles.button}
            appearance={'ghost'}
            status="basic"
            accessoryRight={<Icon name="edit-2-outline" />}
            onPress={props.onEdit}
          />
          <Button
            style={styles.button}
            appearance={'ghost'}
            status="danger"
            accessoryRight={<Icon name="trash-2-outline" />}
            onPress={props.onDelete}
          />
        </>
      )}
    />
  );
}

const styles = StyleSheet.create({
  button: {
    margin: 0,
    padding: 0,
    width: 10,
  },
});
