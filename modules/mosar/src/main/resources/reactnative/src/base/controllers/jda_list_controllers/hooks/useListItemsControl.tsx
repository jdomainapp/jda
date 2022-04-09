import {useCallback, useState} from 'react';

export type IJDAItemControl<T> = {
  resetItems: (newItems: T[]) => void;
  addItems: (newItems: T[]) => void;
  deleteItems: (removedItems: T[keyof T][]) => void;
  updateItem: (updatedItem: T) => void;
  getItemByIndex: (index: number) => T;
};

export function useItemsControl<T>(itemPrimaryKey: keyof T): {
  items: T[];
  itemsControl: IJDAItemControl<T>;
} {
  const [items, setItems] = useState<T[]>([]);

  // api for parent component, will forward by ref
  const resetItems = useCallback((newItems: T[]) => {
    setItems(newItems);
  }, []);

  const getItemByIndex = useCallback(
    (index: number) => {
      return items[index];
    },
    [items],
  );

  const addItems = useCallback((newItems: T[]) => {
    setItems(old => {
      return [...old, ...newItems];
    });
  }, []);

  const deleteItems = useCallback(
    (removedItems: T[keyof T][]) => {
      setItems(old => {
        return [...old].filter(t => !removedItems.includes(t[itemPrimaryKey]));
      });
    },
    [itemPrimaryKey],
  );

  const updateItem = useCallback(
    (updatedItem: T) => {
      setItems(old => {
        const index = old.findIndex(
          t => t[itemPrimaryKey] === updatedItem[itemPrimaryKey],
        );
        if (index > -1) {
          const newItems = [...old];
          newItems[index] = updatedItem;
          return newItems;
        } else {
          return [...old];
        }
      });
    },
    [itemPrimaryKey],
  );

  return {
    items,
    itemsControl: {
      getItemByIndex,
      resetItems,
      addItems,
      deleteItems,
      updateItem,
    },
  };
}
