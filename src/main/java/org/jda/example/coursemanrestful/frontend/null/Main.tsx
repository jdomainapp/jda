import * as React from 'react';
import JDADrawer from './base/views/jda_drawer/JDADrawer';
import {
  asdfasdf
} from './modules/Modules';

export default class MainScreen extends React.Component {
  public render() {
    return (
      <JDADrawer
        initialRoute={'HELLO'}
        routes={[
          @loop_slot[[{
                component: <@single_slot{{moduleComponent}} />,
                name: '@single_slot{{moduleName}}',
              },
          ]]
        ]}
      />
    );
  }
}
