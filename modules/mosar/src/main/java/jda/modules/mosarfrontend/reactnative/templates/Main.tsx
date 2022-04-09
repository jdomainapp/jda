import * as React from 'react';
import JDADrawer from './base/views/jda_drawer/JDADrawer';
import {
  @loop_1[[@slot{{moduleName}},
  ]]loop_1@
} from './modules/Modules';

export default class MainScreen extends React.Component {
  public render() {
    return (
      <JDADrawer
        initialRoute={'@slot{{initialRoute}}'}
        routes={[
          @loop_2[[{
            component: <@slot{{moduleComponent}} />,
            name: '@slot{{moduleName}}',
          },
          ]]loop_2@
        ]}
      />
    );
  }
}
