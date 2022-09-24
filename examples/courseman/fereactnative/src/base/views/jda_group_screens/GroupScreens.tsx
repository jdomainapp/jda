import {createNativeStackNavigator} from '@react-navigation/native-stack';
import * as React from 'react';

export interface IGroupScreenProps {
  subScreens: {
    name: string;
    title?: string;
    component: React.ReactNode;
  }[];
}
const Stack = createNativeStackNavigator();
export function GroupScreen(props: IGroupScreenProps) {
  return (
    <Stack.Navigator initialRouteName="home">
      {props.subScreens.map((screen, index) => (
        <Stack.Screen
          key={index}
          name={screen.name}
          options={{title: screen.title}}
        >
          {() => screen.component}
        </Stack.Screen>
      ))}
    </Stack.Navigator>
  );
}
