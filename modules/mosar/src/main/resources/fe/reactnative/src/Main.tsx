import * as React from 'react';
import JDARouter from './base/views/jda_router/JDARouter';
import { Modules } from './data_types/enums/Modules';
@loop{importModules}[[import { @slot{{ModuleName}}Module } from './modules/@slot{{moduleName}}/Index';
]]loop{importModules}@
export default class MainScreen extends React.Component {
  public render() {
    return (
      <JDARouter
        homeScreenOptions={{
          title: '@slot{{AppName}}'
        }}
        routeConfigs={[@loop{routeConfigs}[[
          {
            component: @slot{{ModuleName}}Module,
            name: Modules.@slot{{ModuleName}},
            title: '@slot{{ModuleTitle}}'
          },]]loop{routeConfigs}@
        ]}
      />
    );
  }
}
