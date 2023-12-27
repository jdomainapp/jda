import React from 'react';
import {Col, Container, Row} from 'react-bootstrap';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import constants, {@loop{importModuleConfig}[[@slot{{moduleNames}}, ]]loop{importModuleConfig}@} from './common/Constants';
import Navigation from './common/Navigation';
@loop{importDomainModule}[[
import Module@slot{{ModuleName}} from './@slot{{moduleJnames}}']]loop{importDomainModule}@
import AccordionSearchableMenu from "./common/AccordionSearchableMenu";

export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.getAppName = this.getAppName.bind(this);
    this.getModules = this.getModules.bind(this);
  }

  getWelcomeMessage() {
    return "Welcome to @slot{{AppName}} App!";
  }

  getAppName() {
    return "@slot{{AppName}}";
  }

  getModules() {
    return [@loop{getModules}[[
        {"endpoint":"/@slot{{moduleJnames}}","name":"Manage @slot{{Module__names}}"},]]loop{getModules}@
    ];
  }

  render() {
    return (<>
        <Router>
          <Navigation appName={this.getAppName()}
                      modules={this.getModules()} />
          <br />
          <Container>
            <Switch>@loop{moduleRoutes}[[
              <Route path='/@slot{{moduleJnames}}'><Module@slot{{ModuleName}} title='Manage @slot{{Module__names}}' /></Route>]]loop{moduleRoutes}@
              <Route path='/'>
                <h3 className="text-center">{this.getWelcomeMessage()}</h3>
                <br />
                <h4 className="text-center">Select a module to continue.</h4>
              </Route>
            </Switch>
          </Container>
        </Router>
      </>
    );
  }
}
