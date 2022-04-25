import React, {ComponentType} from 'react';
import {JDAListContext} from '../contexts/ListContext';

export interface IJDAListItemAPI {
  onEdit: () => void;
  onDelete: () => void;
  onShowDetail: () => void;
}

export interface IJDAListItemControllerProps<T> extends IJDAListItemAPI {
  item: T;
  itemIndex: number;
}

export function withJDAListItemController<
  T,
  P extends IJDAListItemControllerProps<T>,
>(
  Component: ComponentType<P>,
  customProps: Omit<P, keyof IJDAListItemControllerProps<T>>,
) {
  return (props: Omit<P, keyof IJDAListItemAPI>) => {
    return (
      <JDAListContext.Consumer>
        {v => (
          <Component
            {...(props as P)}
            {...customProps}
            onDelete={() => v.onDelete(props.itemIndex)}
            onEdit={() => v.onEdit(props.itemIndex)}
            onShowDetail={() => v.onShowDetail(props.itemIndex)}
          />
        )}
      </JDAListContext.Consumer>
    );
  };
}

//Export componentType
class TypeUltil<T, P extends IJDAListItemControllerProps<T>> {
  //TODO if you change parammeter of withJDAListItemController function, you must change parameters of controlled function below
  controlled = (
    Component: ComponentType<P>,
    customProps: Omit<P, keyof IJDAListItemControllerProps<T>>,
  ) => withJDAListItemController<T, P>(Component, customProps);
}

export type JDAControlledListItemComponent<
  T,
  P extends IJDAListItemControllerProps<T>,
> = ReturnType<TypeUltil<T, P>['controlled']>;
