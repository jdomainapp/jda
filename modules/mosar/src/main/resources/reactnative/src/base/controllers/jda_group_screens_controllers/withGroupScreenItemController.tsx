import {useNavigation} from '@react-navigation/native';
import React, {ComponentType} from 'react';

export interface IJDAGroupScreenItemControllerProps {
  goTo: (subScreenName: string) => void;
}

export function withGroupScreenItemController<P>(Component: ComponentType<P>) {
  return (props: Omit<P, keyof IJDAGroupScreenItemControllerProps>) => {
    const navigator = useNavigation();
    return (
      <Component
        {...(props as P)}
        goTo={(subScreenName: string) =>
          navigator.navigate(subScreenName as never, {} as never)
        }
      />
    );
  };
}
