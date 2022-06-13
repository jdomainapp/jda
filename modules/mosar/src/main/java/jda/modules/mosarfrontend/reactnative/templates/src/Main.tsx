import * as React from 'react';
import JDADrawer from './base/views/jda_drawer/JDADrawer';
@loop{importModules}[[import { @slot{{ModuleName}}Module } from './modules/@slot{{moduleName}}/Index';
]]loop{importModules}@
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
