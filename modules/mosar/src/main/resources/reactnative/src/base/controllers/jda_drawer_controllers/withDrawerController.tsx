import {
  createDrawerNavigator,
  DrawerContentComponentProps,
} from '@react-navigation/drawer';
import React, {ComponentType, createContext, useState} from 'react';

export interface IJDADrawerControllerProps {
  initialRoute: string;
  routes: {
    name: string;
    // eslint-disable-next-line no-undef
    component: JSX.Element;
  }[];
  drawerProps: DrawerContentComponentProps;
}
const Drawer = createDrawerNavigator();
export const DrawerContext = createContext<{
  setDrawerHeader: (show: boolean) => void;
}>({
  setDrawerHeader: (_show: boolean) => {},
});

export function withDrawerController<T extends IJDADrawerControllerProps>(
  Component: ComponentType<T>,
) {
  return (props: Omit<T, 'drawerProps'>) => {
    const [showHeader, setShowHeader] = useState(true);
    return (
      <DrawerContext.Provider value={{setDrawerHeader: v => setShowHeader(v)}}>
        <Drawer.Navigator
          initialRouteName={props.initialRoute}
          screenOptions={{
            headerShown: showHeader,
          }}
          drawerContent={drawer_props => (
            <Component {...(props as T)} drawerProps={drawer_props} />
          )}>
          {props.routes.map((route, index) => (
            <Drawer.Screen key={route.name + index} name={route.name}>
              {_props => route.component}
            </Drawer.Screen>
          ))}
        </Drawer.Navigator>
      </DrawerContext.Provider>
    );
  };
}
