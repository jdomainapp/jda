import React, {ComponentType} from 'react';
import {useAPI} from './useAPI';

export interface IJDAAPIProps extends ReturnType<typeof useAPI> {}

/**
 *
 * @param routeName
 * @param Component
 * @returns
 */
export function withAPI<T, P extends IJDAAPIProps>(
  routeName: string,
  Component: ComponentType<P>,
) {
  return (props: Omit<P, keyof IJDAAPIProps>) => {
    const api = useAPI<T>(routeName);
    return <Component {...(props as P)} {...api} />;
  };
}
