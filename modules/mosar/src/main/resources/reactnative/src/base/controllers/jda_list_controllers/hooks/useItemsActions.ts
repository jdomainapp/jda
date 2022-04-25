import {useCallback} from 'react';

export interface IJDAListItemActionsProps<T> {
  onShowDetail?: (item: T) => void;
  onDeleteItems?: (items: T[keyof T][]) => Promise<void>;
  onEditItem?: (item: T) => Promise<void>;
}

export function useItemActions<T>(
  items: T[],
  handler: IJDAListItemActionsProps<T>,
  primaryKey: keyof T,
) {
  const onEdit = useCallback(
    (index: number) => {
      if (handler.onEditItem && items[index]) {
        handler.onEditItem(items[index]);
      }
    },
    [items, handler],
  );
  const onDelete = useCallback(
    (index: number) => {
      console.log('delete', index);

      if (handler.onDeleteItems && items[index]) {
        handler.onDeleteItems([items[index][primaryKey]]);
      }
    },
    [handler, items, primaryKey],
  );
  const onShowDetail = useCallback(
    (index: number) => {
      if (handler.onShowDetail && items[index]) {
        handler.onShowDetail(items[index]);
      }
    },
    [handler, items],
  );
  return {onEdit, onDelete, onShowDetail};
}
