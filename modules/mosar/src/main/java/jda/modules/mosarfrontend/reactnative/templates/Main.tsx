import * as React from 'react';
import JDADrawer from './base/views/jda_drawer/JDADrawer';
import {@loop{1}[[
  @slot{{moduleName}},]]loop{1}@
} from './modules/Modules';

export default class MainScreen extends React.Component {
  public render() {
    return (
      <JDADrawer
        initialRoute={'@slot{{initialRoute}}'}
        routes={[@loop{2}[[
          {
            component: <@slot{{moduleComponent}} />,
            name: '@slot{{moduleName}}',
          },]]loop{2}@
        ]}
      />
    );
  }
}
