/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import {NavigationContainer} from '@react-navigation/native';
import React from 'react';
import Main from './src/Main';
import * as eva from '@eva-design/eva';
import {
  ApplicationProvider as UIKittenProvider,
  IconRegistry,
} from '@ui-kitten/components';
import {EvaIconsPack} from '@ui-kitten/eva-icons';
import {DefaultTheme, Provider as PaperProvider} from 'react-native-paper';
import {navigationRef} from './src/base/RootNavigator';

import {LogBox} from 'react-native';

LogBox.ignoreLogs([
  "[react-native-gesture-handler] Seems like you're using an old API with gesture components, check out new Gestures system!",
]);

const App = () => {
  return (
    <NavigationContainer ref={navigationRef}>
      <IconRegistry icons={EvaIconsPack} />
      <PaperProvider theme={{...DefaultTheme}}>
        <UIKittenProvider {...eva} theme={eva.light}>
          <Main />
        </UIKittenProvider>
      </PaperProvider>
    </NavigationContainer>
  );
};

export default App;
