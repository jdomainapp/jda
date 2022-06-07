import * as React from 'react';
import JDADrawer from './base/views/jda_drawer/JDADrawer';
import {@loop{1}[[
  @slot{{moduleName}}Module,]]loop{1}@
} from './modules/Modules';

export default class MainScreen extends React.Component {
  public render() {
    return (
      <JDADrawer
        initialRoute={'@slot{{initialRoute}}'}
        routes={[@loop{2}[[
          {
            component: <@slot{{moduleName}}Module />,
            name: '@slot{{moduleName}}',
          },]]loop{2}@
        ]}
      />
    );
  }
}
