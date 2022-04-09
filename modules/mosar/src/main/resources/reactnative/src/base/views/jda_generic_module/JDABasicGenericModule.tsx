import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import * as React from 'react';
import {JDAControlledModuleComponent} from '../../controllers/jda_module_controller/withModuleController';
export interface IBasicGenericModuleProps {
  modules: {
    Component: JDAControlledModuleComponent<any, any, any, any, any>;
    name: string;
    icon?: (props: {
      focused: boolean;
      color: string;
      size: number;
    }) => React.ReactNode;
  }[];
}

const Tab = createBottomTabNavigator();

export function JDABasicGenericModule(props: IBasicGenericModuleProps) {
  return (
    <Tab.Navigator>
      {props.modules.map((m, i) => (
        <Tab.Screen
          key={i}
          component={m.Component}
          name={m.name}
          options={{
            headerShown: false,
            tabBarIcon: m.icon,
          }}
        />
      ))}
    </Tab.Navigator>
  );
}
