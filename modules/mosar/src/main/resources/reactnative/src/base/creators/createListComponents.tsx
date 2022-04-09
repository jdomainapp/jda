import {
  IJDAListControllerProps,
  withJDAListController,
} from '../controllers/jda_list_controllers/hocs/withListController';
import {
  IJDAListItemControllerProps,
  withJDAListItemController,
} from '../controllers/jda_list_controllers/hocs/withListItemController';
import {IJDAModuleConfig} from '../controllers/jda_module_controller/withModuleController';
import JDABasicList, {IJDABasicListProps} from '../views/jda_list/JDABasicList';
import {
  IJDABasicListItemProps,
  JDABasicListItem,
} from '../views/jda_list/JDABasicListItem';

/**
 * Use only for JDABasicList and JDABasicListItem,
 * if you have custom object for specific Module, you should change IJDABasicListProps and IJDABasicListProps
 * to your custom List props and custom ListItem props
 */
export type IJDAListConfig<T> = {
  listProps: Omit<IJDABasicListProps<T>, keyof IJDAListControllerProps<T>>;
  listItemProps: Omit<
    IJDABasicListItemProps<T>,
    keyof IJDAListItemControllerProps<T>
  >;
};

export function createListComponents<T>(
  moduleConfig: IJDAModuleConfig<T>,
  listConfig: IJDAListConfig<T>,
) {
  type ListItemProps = IJDABasicListItemProps<T>;
  const ListItem = withJDAListItemController<T, ListItemProps>(
    JDABasicListItem,
    listConfig.listItemProps,
  );

  type ListProps = IJDABasicListProps<T>;
  const List = withJDAListController<T, ListProps>(
    JDABasicList,
    ListItem,
    moduleConfig,
  );
  return {List, ListItem};
}
