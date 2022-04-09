import React, {
  ComponentType,
  forwardRef,
  useImperativeHandle,
  useState,
} from 'react';
import {IJDAModuleConfig} from '../../jda_module_controller/withModuleController';
import {JDAListContext} from '../contexts/ListContext';
import {
  IJDAListItemActionsProps,
  useItemActions,
} from '../hooks/useItemsActions';
import {
  JDAListCheckControl,
  useListCheckControl,
} from '../hooks/useListCheckControl';
import {IJDAItemControl, useItemsControl} from '../hooks/useListItemsControl';
import {
  IJDAPageControl,
  IJDAPaging,
  usePageControl,
} from '../hooks/useListPageControl';
import {JDAControlledListItemComponent} from './withListItemController';

interface IJDAListAPI<T> {
  // eslint-disable-next-line no-undef
  itemComponents: JSX.Element[];
  checkedItems: T[];
  paging: IJDAPaging;
  loading: boolean;
}

export interface IJDAListControllerProps<T>
  extends IJDAListAPI<T>,
    IJDAListItemActionsProps<T> {
  onRefresh: () => void;
  onAddItem: () => void;
  onChangePage: (page: number) => void;
  onChangePageSize: (pageSize: number) => void;
}

export interface IJDAListRef<T> {
  itemsControl: IJDAItemControl<T>;
  pageControl: IJDAPageControl;
  checkControl: JDAListCheckControl;
  setLoading: (state: boolean) => void;
}

export function withJDAListController<
  T,
  // ItemProps extends IJDAListItemControllerProps<T>,
  ListProps extends IJDAListControllerProps<T>,
>(
  Component: ComponentType<ListProps>,
  ItemComponent: JDAControlledListItemComponent<T, any>,
  moduleConfig: IJDAModuleConfig<T>,
) {
  return forwardRef<IJDAListRef<T>, Omit<ListProps, keyof IJDAListAPI<T>>>(
    (props, ref) => {
      const [loading, setLoading] = useState(false);
      const {paging, pageControl} = usePageControl();
      const {items, itemsControl} = useItemsControl<T>(moduleConfig.primaryKey);
      const {checkedItems, checkControl} = useListCheckControl<T>(
        moduleConfig.primaryKey,
        items,
      );
      const itemActionsHandler = useItemActions(
        items,
        {...props},
        moduleConfig.primaryKey,
      );

      // export api via ref for control from ParrentComponent
      useImperativeHandle(ref, () => ({
        itemsControl,
        pageControl,
        checkControl,
        setLoading,
      }));
      return (
        <JDAListContext.Provider value={itemActionsHandler}>
          <Component
            {...(props as ListProps)}
            loading={loading}
            itemComponents={items.map((item, index) => (
              <ItemComponent {...({} as any)} item={item} itemIndex={index} />
            ))}
            paging={paging}
            checkedItems={checkedItems}
          />
        </JDAListContext.Provider>
      );
    },
  );
}

//Export componentType
class TypeUltil<
  T,
  // ItemProps extends IJDAListItemControllerProps<T>,
  ListProps extends IJDAListControllerProps<T>,
> {
  //TODO if you change parammeter of withJDAListController function, you must change parameters of controlled function below
  controlled = (
    Component: ComponentType<ListProps>,
    ItemComponent: JDAControlledListItemComponent<T, any>,
    moduleConfig: IJDAModuleConfig<T>,
  ) =>
    withJDAListController<T, ListProps>(Component, ItemComponent, moduleConfig);
}

export type JDAControlledListComponent<
  T,
  // ItemProps extends IJDAListItemControllerProps<T>,
  ListProps extends IJDAListControllerProps<T>,
> = ReturnType<TypeUltil<T, ListProps>['controlled']>;
